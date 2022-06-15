package gg.interstellar.wallet

// https://github.com/jinleili/wgpu-on-app/blob/master/Android/app/src/main/java/name/jinleili/wgpu/WGPUSurfaceView.kt

import android.content.Context
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.view.SurfaceHolder
import android.view.SurfaceView

open class WGPUSurfaceView(context: Context, val is_message: Boolean) : SurfaceView(context),
    SurfaceHolder.Callback2 {
    private var rustBrige = RustWrapper()
    private var rustPtr: Long? = null

    init {
        holder.addCallback(this)
        println("fda init")
        // else invisible b/c behind the WGPUSurfaceView itself
        // TODO? what is the proper way to do this?
        // TODO? hasOverlappingRendering?
        this.setZOrderOnTop(false)
//        this.setZOrderMediaOverlay(false)
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // TODO?("rustBrige.resize(rustPtr,format,width,height)")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        rustPtr?.let {
            rustBrige.cleanup(it)
            rustPtr = null
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        holder.let { h ->
            // TODO? NOTE: TRANSLUCENT crash on Emulator? same with TRANSPARENT
//            holder.setFormat(PixelFormat.TRANSLUCENT) // crash EMU + DEVICE
//            holder.setFormat(PixelFormat.TRANSPARENT) // crash EMU + DEVICE
//            holder.setFormat(PixelFormat.RGBA_8888) // crash EMU + DEVICE

            rustPtr = if (is_message) {
                rustBrige.initSurfaceMessage(h.surface)
            } else {
                rustBrige.initSurfacePinpad(h.surface)
            }
            setWillNotDraw(false)
        }
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        rustPtr?.let {
            rustBrige.update(it)
            rustBrige.render(it)
        }
        invalidate()
    }
}

class WGPUSurfaceViewMessage(context: Context) : WGPUSurfaceView(context, true) {

}

class WGPUSurfaceViewPinpad(
    context: Context,
    var callbackGetPositions: () -> Any
) : WGPUSurfaceView(context, false) {
    override fun surfaceCreated(holder: SurfaceHolder) {
        // TODO pass to rust initPinpad
        val positions = callbackGetPositions()

        super.surfaceCreated(holder)
    }
}