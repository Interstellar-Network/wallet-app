package gg.interstellar.wallet

// https://github.com/jinleili/wgpu-on-app/blob/master/Android/app/src/main/java/name/jinleili/wgpu/WGPUSurfaceView.kt

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.material.Colors
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb

open class WGPUSurfaceView(
    context: Context,
    val pinpad_rects: Array<Rect>,
    val message_rect: Array<Rect>,
    val colors: Colors,
) : SurfaceView(context),
    SurfaceHolder.Callback2 {
    private var rustBrige = RustWrapper()
    private var rustPtr: Long? = null
    val WS_URl = "ws://127.0.0.1:9944"

    init {
        holder.addCallback(this)
        println("fda init")
        // else invisible b/c behind the WGPUSurfaceView itself
        // TODO? what is the proper way to do this?
        // TODO? hasOverlappingRendering?
        this.setZOrderOnTop(false)
//        this.setZOrderMediaOverlay(false)

        // TODO move out of here!
        val pub_key = rustBrige.getMobilePublicKey()
        Log.i("interstellar", "pub_key : $pub_key")
        rustBrige.ExtrinsicRegisterMobile(WS_URl, pub_key)

        // TODO move to proper "Loading screen", in a thread
        // MUST wait in a loop until CircuitsPackage is valid(or ideally watch for events)
        rustBrige.ExtrinsicGarbleAndStripDisplayCircuitsPackage(WS_URl, "0.13 ETH to REPLACEME")
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

            // TODO pass to rust initPinpad
            val pinpad_rects = pinpad_rects
            val message_rect = message_rect

            // Flatten List<Rect> -> [left0, top0, right0, bottom0, left1, top2, right1, bottom1, ...]
            // order: match Rect.fromLTRB
            val pinpad_rects_flattened =
                pinpad_rects.fold(mutableListOf<Float>()) { acc: MutableList<Float>, rect: Rect ->
                    acc.add(rect.left)
                    acc.add(rect.top)
                    acc.add(rect.right)
                    acc.add(rect.bottom)
                    acc
                }

            val message_rects_flattened = mutableListOf<Float>(
                message_rect[0].left,
                message_rect[0].top,
                message_rect[0].right,
                message_rect[0].bottom,
            )

            rustPtr = rustBrige.initSurface(
                h.surface,
                message_rects_flattened.toFloatArray(),
                pinpad_rects_flattened.toFloatArray(),
                3, 4,
                // substring: IMPORTANT "toArgb" return ARGB(obviously) but on Rust sideColor::hex wants ordered as RGB(or RGBA)
                // message_text_color_hex: String,
                Integer.toHexString(colors.onBackground.toArgb()).substring(2),
                // circle_text_color_hex: String,
                Integer.toHexString(colors.onSurface.toArgb()).substring(2),
                // circle_color_hex: String,
                Integer.toHexString(colors.surface.toArgb()).substring(2),
                // background_color_hex: String
                Integer.toHexString(colors.background.toArgb()).substring(2),
            )

            setWillNotDraw(false)
        }
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        rustPtr?.let {
            rustBrige.render(it)
        }
        invalidate()
    }
}