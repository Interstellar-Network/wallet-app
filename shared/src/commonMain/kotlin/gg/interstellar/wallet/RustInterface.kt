package gg.interstellar.wallet

/**
 * Kotlin binding("expect") corresponding to the Rust code in shared/rust/
 * There will be on implementation("actual") for Android and one for iOs
 */
interface RustInterface {
    // WARNING: this CAN NOT be "external" if part of the commonMain?
    // e: Compilation failed: external function RustWrapper.CallExtrinsic must have @TypedIntrinsic, @SymbolName, @GCUnsafeCall or @ObjCMethod annotation
    fun CallExtrinsic(url: String) : String?

    fun <Surface> initSurface(surface: Surface, is_message: Boolean): Long
    fun render(rustObj: Long)
    fun update(rustObj: Long)
    fun cleanup(rustObj: Long)
}
