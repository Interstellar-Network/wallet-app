package gg.interstellar.wallet.android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Screen metadata for Rally.
 */
enum class WalletScreen(
    val icon: ImageVector,
) {
    SwapCurrencies(
        icon = Icons.Default.Send,
    ),
    TxPinpad(
        icon = Icons.Filled.Lock,
    ),;

    companion object {
        fun fromRoute(route: String?): WalletScreen =
            when (route?.substringBefore("/")) {
                SwapCurrencies.name -> SwapCurrencies
                TxPinpad.name -> TxPinpad
                // TODO start screen(=landing page) on null
                null -> SwapCurrencies
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}
