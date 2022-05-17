package gg.interstellar.wallet

/**
 * Kotlin binding("expect") corresponding to the Rust code in shared/rust/
 * There will be on implementation("actual") for Android and one for iOs
 */
class RustWrapper {
    external fun CallExtrinsic(x: String) : String?
}