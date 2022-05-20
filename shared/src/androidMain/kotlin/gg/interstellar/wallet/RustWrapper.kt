package gg.interstellar.wallet

class RustWrapper: RustInterface {
    external override fun CallExtrinsic(url: String) : String?
}