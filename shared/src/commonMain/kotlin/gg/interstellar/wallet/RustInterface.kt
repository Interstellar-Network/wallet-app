package gg.interstellar.wallet

/**
 * Kotlin binding("expect") corresponding to the Rust code in shared/rust/
 * There will be on implementation("actual") for Android and one for iOs
 */
interface RustInterface {
    // WARNING: this CAN NOT be "external" if part of the commonMain?
    // e: Compilation failed: external function RustWrapper.CallExtrinsic must have @TypedIntrinsic, @SymbolName, @GCUnsafeCall or @ObjCMethod annotation
    fun CallExtrinsic(url: String): String?

    fun <Surface> initSurface(
        surface: Surface,
        messageRects: FloatArray,
        pinpadRects: FloatArray,
        pinpad_nb_cols: Int,
        pinpad_nb_rows: Int,
        message_text_color_hex: String,
        circle_text_color_hex: String,
        circle_color_hex: String,
        background_color_hex: String,
    ): Long

    fun render(rustObj: Long)
    fun update(rustObj: Long)
    fun cleanup(rustObj: Long)
}
