package gg.interstellar.wallet.android.ui.sendCurrencies


import StatementCard
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import gg.interstellar.wallet.android.data.Address
import gg.interstellar.wallet.android.data.Currency
import gg.interstellar.wallet.android.data.UserData
import gg.interstellar.wallet.android.ui.*
import gg.interstellar.wallet.android.ui.components.Keypad
import gg.interstellar.wallet.android.ui.components.handleKeyButtonClick

//@Preview
@Composable
fun SendCurrenciesBody(
    currencies: List<Currency>,
    addresses: List<Address>,
    onClickGo: () -> Unit = {},
) {
    var currencyName by remember { mutableStateOf("select") }
    var addressName by remember { mutableStateOf("select") }
    val address = UserData.getAddress(addressName)
    val currency =  UserData.getCurrency(currencyName)

    val inputtedCurrency = remember { mutableStateOf("_") }//init state
    val currencyInFiat = remember { mutableStateOf("+") } // to display in addressrow
    val currencyOn = remember { mutableStateOf(false)}// match inputtedCurrency state
    val destinationOn = remember { mutableStateOf(false)}
    val noInput = remember { mutableStateOf("notUsed") }// to keep it generic
    val keypadOn= remember { mutableStateOf(false)} //custom keypad not used
    val currencyChoice = remember { mutableStateOf(false)}
    val addressChoice =  remember { mutableStateOf(false)}
    val inputDone = remember { mutableStateOf(false)}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier =
        /**if (keypadOn.value) Modifier else*{*/ Modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(20.dp))
        DisplayInterstellar()
        Spacer(Modifier.height(30.dp))
            ScreenTopButton (onClickGo,
                //modifier= Modifier.clickable (onClickGo) /// Too bad
                "Send"
            )

        SingleCurrencyStatement(
            modifier = Modifier,
            currency,
            inputtedCurrency,
            currencyInFiat,
            currencyOn, // useInput boolean
            inputDone   // Mutable Boolean to be changed
        ) {
            currencyChoice.value = true
        }

            Box {
            SingleAddressStatement(
                modifier = Modifier,
                address = address,
                currencyInFiat,
            ) {
                addressChoice.value = true
            }
            CircleButtonDest(
                modifier = Modifier
                    //.align(Alignment.TopCenter),
                    .offset(100.dp,-20.dp), //TODO better way
                6.dp, onClickDest = {})
        }

        Spacer(modifier = Modifier.height(30.dp))
        if ( currencyChoice.value && !inputDone.value) CurrenciesStatement(
            currencies = currencies,noInput,noInput,inputDone,
            onCurrencyClick = {
                    name-> currencyName = name
                    currencyOn.value = true
                    currencyChoice.value = false

            }
        )

        if (addressChoice.value)
            AddressesStatement(
            addresses = addresses,noInput,
            onAddressClick = {
                    name-> addressName = name
                    destinationOn.value = true
                    addressChoice.value = false
                    //currencyOn.value = false
            }
        )

        if (currencyOn.value && destinationOn.value) {
            CircleIcon(imageVector = Icons.Filled.Add , border = 0.dp, string ="add" ,
                size_height = 25.dp, size_width = 25.dp )
            TransactionFee()
            CheckButton(onClickGo)
        }

    }
}

/** Detail statement for currencies
*/
@Composable
fun CurrenciesStatement(
    currencies: List<Currency>,
    inputTextView: MutableState<String>,
    currencyInFiat:MutableState<String>,
    inputDone: MutableState<Boolean>,
    onCurrencyClick: (String) -> Unit = {},
) {
    StatementCard(
        //modifier = Modifier.semantics { contentDescription = "Currency Card" },
        items = currencies,
        doubleColumn = true,
        single = false,
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
            usd = currency.usd,
            changeOn = true,
            largeRow = false, // appearance of row rounded box or circle
            inputTextView = inputTextView,
            currencyInFiat = currencyInFiat,
            useInput = false,
            inputDone =inputDone,
            single = false,
            fiat = true,
            color = currency.color
        )
    }
}

/**
 * Detail statement for a single currency.
 */
@Composable
fun SingleCurrencyStatement(
    modifier:Modifier=Modifier,
    currency: Currency,
    inputTextView: MutableState<String>,
    currencyInFiat: MutableState<String>,
    currencyOn:MutableState<Boolean>,
    inputDone: MutableState<Boolean>, // just to add safely currency symbol
    onClickRow:()->Unit
) {
    StatementCard(
        items = listOf(currency),
        doubleColumn = false,
        single = true,
    ) { row ->
        CurrencyRow(
            modifier = Modifier.clickable {
                onClickRow()
            },
            name = row.name,
            coin = row.coin,
            pubkey = row.pubkey,
            amount = row.balance,
            amountFiat = row.balanceFiat,
            change = row.change,
            usd = row.usd,
            changeOn = false,
            largeRow = true,
            inputTextView = inputTextView,
            currencyInFiat =currencyInFiat,
            useInput = currencyOn.value, // enable activation of input when keypad is on
            inputDone =inputDone,
            single = true,
            fiat = false,
            color = row.color
        )
    }
}


@Composable
fun AddressesStatement(
    addresses: List<Address>,
    currencyInFiat: MutableState<String>,
    onAddressClick: (String) -> Unit = {},
) {
    StatementCard(
        //modifier = Modifier.semantics { contentDescription = "Addresses Screen" },
        items = addresses,
        doubleColumn = true,
        single = true,
    ) // appearance double column or one row
    { address ->
        AddressRow(
            modifier = Modifier.clickable {
                onAddressClick(address.name)
            },
            name = address.name,
            color = address.color,
            pubkey = address.pubkey,
            largeRow = false, // appearance of row rounded box or circle
            currencyInFiat = currencyInFiat,
            useInput = true,

        )
    }
}

/**
 * Detail screen for a single address
 */
@Composable
fun SingleAddressStatement(
    modifier: Modifier=Modifier,
    address: Address,
    currencyInFiat: MutableState<String>,
    onClickRow:()->Unit,
) {
    StatementCard(
        items = listOf(address),
        doubleColumn = false,
        single = true,
    ) { row ->
        AddressRow(
            modifier = Modifier.clickable {
                onClickRow()
            },
            name = row.name,
            pubkey = row.pubkey,
            largeRow = true,
            currencyInFiat = currencyInFiat,
            useInput = true,
            color = row.color
        )
    }
}


@Composable
private fun TransactionFee() {

    Row { Spacer(Modifier.height(40.dp)) }
    Box(
    )
    {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .shadow(elevation = 25.dp, shape = RectangleShape, clip = false)
                .sizeIn(220.dp, 50.dp, 220.dp, 50.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        0.0f to Color.DarkGray,
                        0.8f to Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(820f, 0f),
                    )
                )
        ) {
            Text(
                "0.10 USD",
                modifier = Modifier
                    .align(Alignment.Center),
            )
        }
        CircleImage(
            modifier =Modifier.align(Alignment.CenterEnd),
            "select",30.dp,30.dp  //select = interstellar icon with border
        )

    }
    Row { Spacer(Modifier.height(40.dp)) }
}


@Composable
private fun CheckButton(onClickGo: () -> Unit) {
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
            Icon(
                Icons.Filled.Check,
                "check icon", Modifier.size(35.dp)
            )
        }
    }
}