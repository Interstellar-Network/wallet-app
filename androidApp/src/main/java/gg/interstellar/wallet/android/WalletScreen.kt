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
    Portfolio(
        icon = Icons.Filled.AccountBalanceWallet
    ),
    /**
    Market(
        icon = Icons.Filled.PieChart,
    ),

    Addresses(
        icon = Icons.Filled.ImportContacts,
    ),
    */

    Send(
        icon = Icons.Default.Send,
    ),
    TxPinpad(
        icon = Icons.Filled.Lock,

    ),
    Profile(
        icon = Icons.Filled.AccountCircle,
    ), ;

    companion object {
        fun fromRoute(route: String?): WalletScreen =
            when (route?.substringBefore("/")) {
                Profile.name->Profile
                Portfolio.name->Portfolio
                Send.name -> Send
                TxPinpad.name -> TxPinpad
                //Market.name -> Market
                //Addresses.name -> Addresses
                null -> Portfolio
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}
