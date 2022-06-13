package gg.interstellar.wallet

// https://github.com/jinleili/wgpu-on-app/blob/master/Android/app/src/main/java/name/jinleili/wgpu/WGPUSurfaceView.kt

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

open class WGPUSurfaceView : SurfaceView, SurfaceHolder.Callback2 {
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
        // TODO? what is the proper way to do this?
        // TODO? hasOverlappingRendering?
        this.setZOrderOnTop(true)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
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

class WGPUSurfaceViewMessage(context: Context) : WGPUSurfaceView(context) {

}

class WGPUSurfaceViewPinpad(
    context: Context,
    var callbackGetPositions: () -> Any
): WGPUSurfaceView(context) {
    init {
        // Too soon! this is null!
        var parent = getParent()
        println(parent)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
//        getParent(): androidx.compose.ui.viewinterop.ViewFactoryHolder{4e0c4fc V.E...... ......ID 0,799-1080,1731}
//        getParent().getParent(): androidx.compose.ui.platform.AndroidViewsHandler{34fbd8d V.E...... ......ID 0,0-1080,1731}
//        getParent().getParent().getParent(): androidx.compose.ui.platform.AndroidComposeView{2f585e4 VFED..... ......ID 0,0-1080,1731}
//        getParent().getParent().getParent().getParent(): androidx.compose.ui.platform.ComposeView{c403e84 V.E...... ......ID 0,0-1080,1731}
//        getParent().getParent().getParent().getParent().getParent(): android.widget.FrameLayout{74ac6ee V.E...... ......ID 0,63-1080,1794 #1020002 android:id/content}
//        getParent().getParent().getParent().getParent().getParent().getParent(): android.widget.LinearLayout{75922a2 V.E...... ......ID 0,0-1080,1794}
//        getParent().getParent().getParent().getParent().getParent().getParent().getParent(): DecorView@7929c69[MainActivity]
//        getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent(): ViewRootImpl
        var parent = getParent()
        println(parent)

        // TODO pass to rust initPinpad
        val positions = callbackGetPositions()

        super.surfaceCreated(holder)
    }
}