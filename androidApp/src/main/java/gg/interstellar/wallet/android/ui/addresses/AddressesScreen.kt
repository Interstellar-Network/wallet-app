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
    currencyInFiat: MutableState<String>,
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
        doubleColumn = true, // appearance double column or one column
        single = false,
        fiat = true
    )
    { address ->
        AddressRow(
            modifier = Modifier.clickable {
                onAddressClick(address.name)
            },
            name = address.name,
            color = address.color,
            pubkey = address.pubkey,
            largeRow = false, // appearance of row rounded box or circle box
            currencyInFiat = currencyInFiat,
            useInput = false,

        )
    }
}

/**
 * Detail screen for a single address
 */
@Composable
fun SingleAddressBody(address: Address,
                      currencyInFiat: MutableState<String>,
) {
    StatementBody(
        items = listOf(address),
        colors = { address.color},
        amounts = {address.no_amount},
        amountsFiat = {address.no_amount},
        amountsTotal = 0f,
        circleLabel = address.name,
        screenLabel = address.name,
        doubleColumn = false,
        single = true, // to handle one row in singleBody or statement
       fiat = false
    ) { row ->
        AddressRow(
            name = row.name,
            pubkey = row.pubkey,
            largeRow = true,
            currencyInFiat =currencyInFiat,
            useInput = false,
            color = row.color
        )
    }
}