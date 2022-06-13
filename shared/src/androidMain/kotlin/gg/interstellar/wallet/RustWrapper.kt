package gg.interstellar.wallet

import android.view.Surface

class RustWrapper: RustInterface {
    init {
        // MUST match the lib [package] name in shared/rust/Cargo.toml
        System.loadLibrary("shared_substrate_client")
        System.loadLibrary("renderer")
    }

    external override fun CallExtrinsic(url: String) : String?

    // TODO? split initSurfaceMessage + initSurfacePinpad?
    // would the rendeder work with 2 windows?
    // cf https://github.com/gfx-rs/wgpu/blob/master/wgpu/examples/hello-windows/main.rs
    external override fun <Surface> initSurface(surface: Surface): Long
    external override fun render(rustObj: Long)
    external override fun update(rustObj: Long)
// TODO?
//    external fun drop(rustObj: Long)

}