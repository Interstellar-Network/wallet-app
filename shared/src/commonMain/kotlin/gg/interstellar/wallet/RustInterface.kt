package gg.interstellar.wallet

/**
 * Kotlin binding("expect") corresponding to the Rust code in shared/rust/
 * There will be on implementation("actual") for Android and one for iOs
 */
interface RustInterface {
    /**
     *
     * @param ws_url address of the WS endpoint of the OCW; something like "ws://127.0.0.1:9944"
     */
    // WARNING: this CAN NOT be "external" if part of the commonMain?
    // e: Compilation failed: external function RustWrapper.CallExtrinsic must have @TypedIntrinsic, @SymbolName, @GCUnsafeCall or @ObjCMethod annotation
    fun ExtrinsicGarbleAndStripDisplayCircuitsPackage(ws_url: String, tx_message: String): String?
    fun ExtrinsicRegisterMobile(ws_url: String, pub_key: ByteArray): String?

    /********************************** RENDER-related ********************************************/
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
    fun cleanup(rustObj: Long)

    /********************************* REGISTRY-related *******************************************/
    fun getMobilePublicKey(): ByteArray
}
