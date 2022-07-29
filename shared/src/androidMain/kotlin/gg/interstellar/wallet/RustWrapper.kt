package gg.interstellar.wallet

import android.view.Surface

class RustWrapper : RustInterface {
    init {
        // MUST match the lib [package] name in shared/rust/Cargo.toml
        System.loadLibrary("renderer")
        // cf "COPY libc++_shared.so" in shared/build.gradle.kts
        // System.loadLibrary("c++_shared.so")
        // NOT needed, but it MUST be in jniLibs/!!!
    }

    external override fun ExtrinsicGarbleAndStripDisplayCircuitsPackage(ws_url: String, tx_message: String): String?

    // TODO? split initSurfaceMessage + initSurfacePinpad?
    // would the rendeder work with 2 windows?
    // cf https://github.com/gfx-rs/wgpu/blob/master/wgpu/examples/hello-windows/main.rs
    external override fun <Surface> initSurface(
        surface: Surface,
        messageRects: FloatArray,
        pinpadRects: FloatArray,
        pinpad_nb_cols: Int,
        pinpad_nb_rows: Int,
        message_text_color_hex: String,
        circle_text_color_hex: String,
        circle_color_hex: String,
        background_color_hex: String
    ): Long

    external override fun render(rustObj: Long)
    external override fun cleanup(rustObj: Long)
}