package gg.interstellar.wallet.android


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import gg.interstellar.wallet.android.data.Address


/**
 * Screen metadata for Rally.
 */
enum class WalletScreen(
    val icon: ImageVector,
) {
    /**
    Accounts(// For test purpose
        icon = Icons.Filled.Send,
    ),
    */
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
                SendCurrencies.name -> SendCurrencies
                TxPinpad.name -> TxPinpad
                Market.name -> Market
                Addresses.name -> Addresses
                // TODO start screen(=landing page) on null
                null -> SendCurrencies
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}
