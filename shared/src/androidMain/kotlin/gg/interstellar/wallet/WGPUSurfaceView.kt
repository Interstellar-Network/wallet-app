package gg.interstellar.wallet

// https://github.com/jinleili/wgpu-on-app/blob/master/Android/app/src/main/java/name/jinleili/wgpu/WGPUSurfaceView.kt

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.compose.material.Colors
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import java.lang.RuntimeException

open class WGPUSurfaceView(
    context: Context,
    val pinpad_rects: Array<Rect>,
    val message_rect: Array<Rect>,
    val colors: Colors,
    val callbackTxDone: () -> Unit,
) : SurfaceView(context),
    SurfaceHolder.Callback2 {
    private var rustBridge = RustWrapper()
    private var rustPtr: Long? = null
    val WS_URL = "ws://127.0.0.1:9944"
    val IPFS_ADDR = "/ip4/127.0.0.1/tcp/5001"
    private var circuitsPackagePtr: Long? = null
    private var packagePtr: Long? = null

    private var message_nb_digts = 0
    private var inputDigits: ArrayList<Byte> = arrayListOf()

    init {
        holder.addCallback(this)
        println("fda init")
        // else invisible b/c behind the WGPUSurfaceView itself
        // TODO? what is the proper way to do this?
        // TODO? hasOverlappingRendering?
        this.setZOrderOnTop(false)
//        this.setZOrderMediaOverlay(false)

        ////////////////////////////////////////////////////////////////////////////////////////////
        // TODO properly wrap in "loading screen", in a thread
        Toast.makeText(context, "Processing...", Toast.LENGTH_SHORT).show()

        // TODO avoid registering if already registered
        // - use shared_prefs to store status
        // - add fn in substrate_client: is_registered()
        val pub_key = rustBridge.getMobilePublicKey()
        Log.i("interstellar", "pub_key : $pub_key")
        rustBridge.ExtrinsicRegisterMobile(WS_URL, pub_key)
        Toast.makeText(context, "Registered", Toast.LENGTH_SHORT).show()

        rustBridge.ExtrinsicGarbleAndStripDisplayCircuitsPackage(WS_URL, "0.13 ETH to REPLACEME")

        // wait in a loop until CircuitsPackage is valid
        // TODO SHOULD use Events
        var retryCount = 0
        do {
            try {
                circuitsPackagePtr = rustBridge.GetCircuits(
                    WS_URL,
                    IPFS_ADDR,
                )
                message_nb_digts = rustBridge.GetMessageNbDigitsFromPtr(circuitsPackagePtr!!)
                Log.i(
                    "interstellar",
                    "circuitsPackagePtr : ${circuitsPackagePtr}, message_nb_digts : $message_nb_digts"
                )
                packagePtr = rustBridge.GetTxIdPtrFromPtr(circuitsPackagePtr!!)
            } catch (e: RuntimeException) {
                // ONLY retry when specifically NoCircuitAvailableException; thrown by "Java_gg_interstellar_wallet_RustWrapper_GetCircuits"
                if(e.message.equals("NoCircuitAvailableException")){
                    Log.i("interstellar", "NoCircuitAvailableException: will retry in 1s...")
                    Thread.sleep(1000)
                    retryCount++
                } else {
                    throw e
                }
            }
        } while (circuitsPackagePtr == null && retryCount < 10)

        if(retryCount >= 10){
            Log.e("interstellar", "Still no circuit available after 10s; exiting!")
            Toast.makeText(context, "No circuits available after 10s; exiting!", Toast.LENGTH_SHORT).show()

            // TODO? add error-specific callback?
            callbackTxDone()
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    fun onClickPinpadDigit(idx: Int) {
        assert(idx >=0 && idx <= 11,
            { "onClickPinpadDigit: should only have [0-11] digit on a pinpad!" })

        Log.i("interstellar", "onClickPinpadDigit $idx")
        if (idx == 11) {
            // "clear" button: reset
            inputDigits.clear()
        } else if (idx == 9) {
            // "go" button, but for now it is empty
            // DO NOTHING
        } else {
            // standard digit: add it to the list
            inputDigits.add(idx.toByte())
        }

        // if we have enough inputs, try to validate
        if(inputDigits.size >= message_nb_digts) {
            Toast.makeText(context, "Validating transaction...", Toast.LENGTH_SHORT).show()
            rustBridge.ExtrinsicCheckInput(WS_URL, packagePtr!!, inputDigits.toByteArray())
            Toast.makeText(context, "Transaction done!", Toast.LENGTH_SHORT).show()

            // not valid after ExtrinsicCheckInput, so reset it
            packagePtr = null

            callbackTxDone()
        }
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // TODO?("rustBrige.resize(rustPtr,format,width,height)")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        rustPtr?.let {
            rustBridge.cleanup(it)
            rustPtr = null
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        holder.let { h ->
            // TODO? NOTE: TRANSLUCENT crash on Emulator? same with TRANSPARENT
//            holder.setFormat(PixelFormat.TRANSLUCENT) // crash EMU + DEVICE
//            holder.setFormat(PixelFormat.TRANSPARENT) // crash EMU + DEVICE
//            holder.setFormat(PixelFormat.RGBA_8888) // crash EMU + DEVICE

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

            rustPtr = rustBridge.initSurface(
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
                circuitsPackagePtr!!,
            )

            // not valid anymore after "initSurface" so we "reset" it
            circuitsPackagePtr = null

            setWillNotDraw(false)
        }
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        rustPtr?.let {
            rustBridge.render(it)
        }
        invalidate()
    }
}