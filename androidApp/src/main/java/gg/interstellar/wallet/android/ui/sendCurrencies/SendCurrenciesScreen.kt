package gg.interstellar.wallet.android.ui.sendCurrencies

import StatementCard
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import gg.interstellar.wallet.android.data.Address
import gg.interstellar.wallet.android.data.Currency
import gg.interstellar.wallet.android.data.UserData
import gg.interstellar.wallet.android.ui.AddressRow
import gg.interstellar.wallet.android.ui.CurrencyRow
import gg.interstellar.wallet.android.ui.DisplayInterstellar
import gg.interstellar.wallet.android.ui.ScreenTopBox
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme


import androidx.compose.material.Icon as MaterialIcon

//@Preview
@Composable
fun SendCurrenciesBody(
    currencies: List<Currency>,
    addresses: List<Address>,
    onClickGo: () -> Unit = {},
    //onCurrencyClick: (String) -> Unit = {},
    onCClick: (String) -> Unit = {},

) {
    var currencyName by remember { mutableStateOf("Bitcoin") }
    var currency =  UserData.getCurrency("Bitcoin")
    //val currencies = (UserData.currencies)
    //val currency = UserData.getCurrency("Bitcoin")
    //val addresses = UserData.addresses
    var address = UserData.getAddress("alice")




    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(20.dp))
        DisplayInterstellar()
        Spacer(Modifier.height(20.dp))
        ScreenTopBox("Send")

        var string = currency.name
        var test: (String) -> Unit = {}
        Row {
            Text(
                text = string,
                style = MaterialTheme.typography.h2
            )
        }
        currency =  UserData.getCurrency(currencyName)

        SingleCurrencyStatement(currency)
        SingleAddressStatement(address)

        CurrenciesStatement(
            currencies = currencies,
            onCurrencyClick ={name-> currencyName = name}
        )
        //FromToCurrenciesToDestinationMiddle(onClickGo)

        GoButtonBottom(onClickGo)
    }
}

/** Detail statement for currencies
*/
@Composable
fun CurrenciesStatement(
    //modifier: Modifier,
    currencies: List<Currency>,
    onCurrencyClick: (String) -> Unit = {},
) {
    StatementCard(
        //modifier = Modifier.semantics { contentDescription = "Currency Card" },
        items = currencies,
        doubleColumn = true,
    ) // appearance double column or one row
    { currency ->
        CurrencyRow(
            modifier = Modifier.clickable {
                 onCurrencyClick(currency.name)
                },
            name = currency.name,
            coin = currency.coin,
            pubkey = currency.pubkey,
            amount = currency.balance,
            amountFiat = currency.balanceFiat,
            change = currency.change,
            largeRow = false, // appearance of row rounded box or circle
            fiat = true,
            color = currency.color
        )
    }
}

/**
 * Detail statement for a single currency.
 */
@Composable
fun SingleCurrencyStatement(currency: Currency) {
    StatementCard(
        items = listOf(currency),
        doubleColumn = false,
    ) { row ->
        CurrencyRow(
            name = row.name,
            coin = row.coin,
            pubkey = row.pubkey,
            amount = row.balance,
            amountFiat = row.balanceFiat,
            change = row.change,
            largeRow = true,
            fiat = false,
            color = row.color
        )
    }
}

@Composable
fun AddressesStatement(
    addresses: List<Address>,
    onAccountClick: (String) -> Unit = {},
) {
    StatementCard(
        //modifier = Modifier.semantics { contentDescription = "Addresses Screen" },
        items = addresses,
        doubleColumn = true,
    ) // appearance double column or one row
    { address ->
        AddressRow(
            modifier = Modifier.clickable {
                onAccountClick(address.name)
            },
            name = address.name,
            color = address.color,
            pubkey = address.pubkey,
            largeRow = false, // appearance of row rounded box or circle
        )
    }
}

/**
 * Detail screen for a single address
 */
@Composable
fun SingleAddressStatement(address: Address) {
    StatementCard(
        items = listOf(address),
        doubleColumn = false,
    ) { row ->
        AddressRow(
            name = row.name,
            pubkey = row.pubkey,
            largeRow = true,
            color = row.color
        )
    }
}




@Composable
private fun FromToCurrenciesToDestinationMiddle(onClickGo: () -> Unit) {
    FromToCurrencies(RoundedCornerShape(20.dp))
    Destination(RoundedCornerShape(20.dp))
    TransactionFee(RoundedCornerShape(20.dp))

    CircleButton25(Icons.Filled.ArrowDropDown, 4.dp, "drop down", 4.dp, -230.dp, onClickGo)
    CircleButtonCurrencies("ETH", 110.dp, -300.dp)

    CircleButtonCurrencies("ETH", 110.dp, -230.dp,)


    CircleButtonCurrencies("interstellar_black_icon_white_border", 112.dp, -114.dp,)

    //DisplayCircleLabel('label'"120 USD", 4.dp,-245.dp, 60.dp,25.dp )

    CircleButton25(Icons.Filled.Add, 0.dp, "add", 4.dp, -230.dp, onClickGo)
}

@Composable
private fun FromToCurrencies(shape: Shape) {
    // Blank row to adjust
    Row { Spacer(Modifier.height(40.dp)) }
        Box(
            modifier = Modifier
                .shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
                .sizeIn(220.dp, 80.dp, 220.dp, 80.dp)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        0.4f to Color(0xFF627eea),
                        1f to Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(650f, 0f),
                    )
                )
        ) {
            Text(
                "O.6 ETH",
                modifier = Modifier
                    .align(Alignment.Center),
            )
        }

}




@Composable
private fun CircleButtonCurrencies(string: String, dpx: Dp, dpy: Dp
) {
    Surface(
        modifier = Modifier
            .sizeIn(25.dp, 25.dp, 25.dp, 25.dp)
            .aspectRatio(1f)
            .offset(x = dpx, y = dpy),
        shape = CircleShape,
    ) {
            val context = LocalContext.current
            val logoname = "ic_"+ string.lowercase()
            val drawableId = remember(logoname) {
                context.resources.getIdentifier(
                    logoname,
                    "drawable",
                    context.packageName
                )
            }
            Image(
                painterResource(id = drawableId),
                contentDescription = "..."
            )
        }
    }






@Composable
private fun CircleButtonCurrenciesbis(
    paintDrawable: Painter, string: String, dpx: Dp, dpy: Dp,
    onClickGo: () -> Unit
) {
    Surface(
        modifier = Modifier
            .sizeIn(25.dp, 25.dp, 25.dp, 25.dp)
            .aspectRatio(1f)
            .offset(x = dpx, y = dpy),
        shape = CircleShape,
    ) {
        IconButton(
            //TODO solve onClickGo issue
            onClick = onClickGo,
        ) {
            Image(paintDrawable, string)

        }
    }
}

@Composable
private fun Destination(shape: Shape) {
    // Blank row to adjust
    Row { Spacer(Modifier.height(10.dp)) }
    Box(
        modifier = Modifier
            .shadow(elevation = 15.dp, shape = RectangleShape, clip = false)
            .sizeIn(220.dp, 80.dp, 220.dp, 80.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    0.4f to Color(0xFF627eea),
                    1.1f to Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(650f, 0f),
                )
            )
    ) {
        Text(
            "John Doe",
            modifier = Modifier
                .align(Alignment.Center),
        )
    }
    Row { Spacer(Modifier.height(40.dp)) }
}
@Composable
private fun CircleButton25(
    imageVector: ImageVector, border: Dp, string: String, dpx: Dp, dpy: Dp,
    onClickGo: () -> Unit
)//TODO  size of button
{
    Surface(
        modifier = Modifier
            .sizeIn(25.dp, 25.dp, 25.dp, 25.dp)
            .aspectRatio(1f)
            .offset(x = dpx, y = dpy),

        color = MaterialTheme.colors.surface,
        shape = CircleShape,
        border = BorderStroke(
            border,
            if (MaterialTheme.colors.isLight) Color.White
            else Color.Black
        ),

        ) {
        IconButton(
            onClick = onClickGo,
        ) {
            androidx.compose.material.Icon(imageVector = imageVector, contentDescription = string)
        }
    }
}








@Composable
private fun TransactionFee(shape: Shape) {

    Row { Spacer(Modifier.height(40.dp)) }
    Box(
        modifier = Modifier
            .shadow(elevation = 25.dp, shape = RectangleShape, clip = false)
            .sizeIn(220.dp, 50.dp, 220.dp, 50.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    0.0f to Color.Black,
                    0.8f to Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(520f, 0f),
                )
            )
    ) {
        Text(
            "0.10 USD",
            modifier = Modifier
                .align(Alignment.Center),
        )
    }
}


@Composable
private fun GoButtonBottom(onClickGo: () -> Unit) {
    // Blank row to adjust
    //Row { Spacer(Modifier.height(20.dp)) }
    Surface(
        modifier = Modifier
            .sizeIn(60.dp, 60.dp, 60.dp, 60.dp)
            .aspectRatio(1f),
        color = MaterialTheme.colors.secondaryVariant,
        shape = CircleShape,
    ) {
        IconButton(
            onClick = onClickGo,
        ) {
            MaterialIcon(
                Icons.Filled.Check,
                "check icon", Modifier.size(35.dp)
            )
        }
    }
    // Blank row to adjust
    Row { Spacer(Modifier.height(80.dp)) }//TODO fix issue
}