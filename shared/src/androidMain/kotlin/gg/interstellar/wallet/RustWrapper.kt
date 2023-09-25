package gg.interstellar.wallet

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.Surface
import java.security.KeyPairGenerator
import java.security.KeyStore

class RustWrapper : RustInterface {
    val KEY_ALIAS = "interstellar"

    init {
        // MUST match the lib [package] name in shared/rust/Cargo.toml
        System.loadLibrary("renderer")
        System.loadLibrary("substrate_client")
        // cf "COPY libc++_shared.so" in shared/build.gradle.kts
        // System.loadLibrary("c++_shared.so")
        // NOT needed, but it MUST be in jniLibs/!!!
    }

    external override fun ExtrinsicGarbleAndStripDisplayCircuitsPackage(
        ws_url: String,
        node_url: String,
        tx_message: String
    ): String?

    external override fun ExtrinsicRegisterMobile(
        ws_url: String,
        node_url: String,
        pub_key: ByteArray
    ): String?

    external override fun GetCircuits(
        ws_url: String,
        node_url: String,
        ipfs_addr: String,
    ): Long

    external override fun ExtrinsicCheckInput(
        ws_url: String,
        node_url: String,
        circuits_package_ptr: Long,
        inputs: ByteArray,
    )

    external override fun GetMessageNbDigitsFromPtr(ptr: Long): Int
    external override fun GetTxIdPtrFromPtr(circuits_package_ptr: Long): Long

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
        background_color_hex: String,
        circuits_package_ptr: Long,
    ): Long

    external override fun render(rustObj: Long)
    external override fun cleanup(rustObj: Long)

    override fun getMobilePublicKey(): ByteArray {
        // check if the alias already exists
        val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        var entry = ks.getEntry(KEY_ALIAS, null)
        if (entry != null) {
            val pub = (entry as KeyStore.PrivateKeyEntry).certificate.publicKey
            return pub.encoded
        }

        /*
         * Generate a new EC key pair entry in the Android Keystore by
         * using the KeyPairGenerator API. The private key can only be
         * used for signing or verification and only with SHA-256 or
         * SHA-512 as the message digest.
         */
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )
        val parameterSpec: KeyGenParameterSpec =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                ).run {
                    setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    build()
                }
            } else {
                TODO("VERSION.SDK_INT < M")
            }

        kpg.initialize(parameterSpec)

        val kp = kpg.generateKeyPair()

        return kp.public.encoded
    }
}