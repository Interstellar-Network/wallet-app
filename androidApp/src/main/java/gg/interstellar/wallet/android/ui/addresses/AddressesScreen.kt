import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import gg.interstellar.wallet.android.data.Address
import gg.interstellar.wallet.android.ui.AddressRow
import gg.interstellar.wallet.android.ui.components.StatementBody

@Composable
fun AddressesBody(
    addresses: List<Address>,
    inputTextView: MutableState<String>,
    onAddressClick: (String) -> Unit = {},
) {
    StatementBody(
        modifier = Modifier.semantics { contentDescription = "Addresses Screen" },
        items = addresses,
        colors = { address-> address.color},
        amounts = { address-> address.no_amount},
        amountsFiat= {address-> address.no_amount },
        amountsTotal = 0f,
        circleLabel = "",
        screenLabel = "Addresses",
        doubleColumn = false,
        single = false,
    ) // appearance double column or one row
    { address ->
        AddressRow(
            modifier = Modifier.clickable {
                onAddressClick(address.name)
            },
            name = address.name,
            color = address.color,
            pubkey = address.pubkey,
            largeRow = true, // appearance of row rounded box or circle
            inputTextView = inputTextView,

        )
    }
}

/**
 * Detail screen for a single address
 */
@Composable
fun SingleAddressBody(address: Address, inputTextView: MutableState<String>) {
    StatementBody(
        items = listOf(address),
        colors = { address.color},
        amounts = {address.no_amount},
        amountsFiat = {address.no_amount},
        amountsTotal = 0f,
        circleLabel = address.name,
        screenLabel = address.name,
        doubleColumn = false,
        single = true,
        //useInput = false,
       // fiat = false
    ) { row ->
        AddressRow(
            name = row.name,
            pubkey = row.pubkey,
            largeRow = true,
            inputTextView = inputTextView,
            color = row.color
        )
    }
}