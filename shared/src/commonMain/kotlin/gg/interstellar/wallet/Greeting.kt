package gg.interstellar.wallet

//import io.emeraldpay.polkaj.scale.ScaleCodecReader

class Greeting {
    fun greeting(): String {
        // TODO dep in commonMain
//        val msg: ByteArray = byteArrayOf()
//        val rdr = ScaleCodecReader(msg)
        return "Hello, ${Platform().platform}!"
    }
}