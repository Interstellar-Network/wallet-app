package gg.interstellar.wallet

// https://github.com/jinleili/wgpu-on-app/blob/master/Android/app/src/main/java/name/jinleili/wgpu/WGPUSurfaceView.kt

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class WGPUSurfaceView : SurfaceView, SurfaceHolder.Callback2 {
    private var rustBrige = RustWrapper()
    private var rustPtr: Long = Long.MAX_VALUE

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    init {
        holder.addCallback(this)
        println("fda init")
        // else invisible b/c behind the WGPUSurfaceView itself
        // TODO what is the proper way to do this?
//        setZOrderOnTop(true)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
////        libEGL  : eglCreateWindowSurface: native_window_api_connect (win=0xc166b808) failed (0xffffffed) (already connected to another API?)
////        libEGL  : eglCreateWindowSurface:679 error 3003 (EGL_BAD_ALLOC)
//        holder.let { h ->
//            rustPtr = rustBrige.initSurface(h.surface)
//            setWillNotDraw(false)
//        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (rustPtr != Long.MAX_VALUE) {
            // TODO
//            rustBrige.drop(rustPtr)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        holder.let { h ->
            rustPtr = rustBrige.initSurface(h.surface)
            setWillNotDraw(false)
        }
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
    }

    // API Level 26+
//    override fun surfaceRedrawNeededAsync(holder: SurfaceHolder, drawingFinished: Runnable) {
//        super.surfaceRedrawNeededAsync(holder, drawingFinished)
//    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (rustPtr != Long.MAX_VALUE) {
            rustBrige.update(rustPtr)
            rustBrige.render(rustPtr)
        }
        invalidate()
    }

}