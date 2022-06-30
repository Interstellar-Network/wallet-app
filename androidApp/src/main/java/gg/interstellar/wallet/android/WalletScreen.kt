package gg.interstellar.wallet.android


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector


/**
 * Screen metadata for Rally.
 */
enum class WalletScreen(
    val icon: ImageVector,
) {
    Star(
        icon = Icons.Filled.Star,
    ),

    Market(
        icon = Icons.Filled.PieChart,
    ),

    Addresses(
        icon = Icons.Filled.ImportContacts,
    ),

    SendCurrencies(
        icon = Icons.Default.Send,
    ),
    TxPinpad(
        icon = Icons.Filled.Lock,

    ),;

    companion object {
        fun fromRoute(route: String?): WalletScreen =
            when (route?.substringBefore("/")) {
                Star.name->Star
                SendCurrencies.name -> SendCurrencies
                TxPinpad.name -> TxPinpad
                Market.name -> Market
                Addresses.name -> Addresses
                // TODO start screen(=landing page) on null
                null -> Star
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}
