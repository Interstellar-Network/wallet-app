package gg.interstellar.wallet.android.ui.send


import StatementCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import gg.interstellar.wallet.android.data.Address
import gg.interstellar.wallet.android.data.Currency
import gg.interstellar.wallet.android.data.UserData
import gg.interstellar.wallet.android.ui.components.*

//@Preview
//@OptIn(ExperimentalComposeUiApi::class)
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
    val currencyInFiat = remember { mutableStateOf("+") } // to display in address
    val currencyOn = remember { mutableStateOf(false)}// match inputtedCurrency state
    val destinationOn = remember { mutableStateOf(false)}
    val noInput = remember { mutableStateOf("notUsed") }// to keep it generic

    val currencyChoice = remember { mutableStateOf(true)} //display or not
    val addressChoice =  remember { mutableStateOf(false)}
    val inputDone = remember { mutableStateOf(false)}
    //val softwareKeyboardController = LocalSoftwareKeyboardController.current
    //val keypadOn= remember { mutableStateOf(false)} //custom keypad not used

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        if (!currencyChoice.value && !addressChoice.value && inputDone.value)
            Modifier else Modifier //.verticalScroll(rememberScrollState())
        // deactivate scrolling
    ) {
        Spacer(Modifier.height(30.dp))
        DisplayInterstellar()
        Spacer(Modifier.height(10.dp))

        ScreenTopBox(modifier=Modifier,"Send")

        //TODO solve this - does not work
        //if (currencyOn.value)  softwareKeyboardController?.show()//does not work???

        Spacer(Modifier.height(20.dp))
            SingleCurrencyStatement(
                modifier = Modifier.padding(150.dp,150.dp),
                currency,
                inputtedCurrency,
                currencyInFiat,
                currencyOn, // useInput boolean
                inputDone   // Mutable Boolean to be changed
            ) {
                currencyChoice.value = true
                addressChoice.value = false
            }

            SingleAddressStatement(
                modifier = Modifier, //.align(Alignment.BottomCenter),
                           // .sizeIn(200.dp,249.dp,220.dp, 500.dp),
                address = address,
                currencyInFiat,
            ) {
                addressChoice.value = true
                currencyChoice.value = false

            }
            CircleButtonDest(
                modifier = Modifier
                    //.align(Alignment.CenterHorizontally),
                    .offset(0.dp, (-110).dp), //TODO better way
                    2.dp, onClickDest = {})

            if ( currencyChoice.value && !inputDone.value) CurrenciesStatement(
                currencies = currencies,noInput,noInput,inputDone,
                onCurrencyClick = {
                        name-> currencyName = name
                    currencyOn.value = true
                    currencyChoice.value = false
                    addressChoice.value = true
                }
            )

            if (addressChoice.value && !destinationOn.value)
                AddressesStatement(
                    addresses = addresses,noInput,
                    onAddressClick = {
                            name-> addressName = name
                        destinationOn.value = true
                        addressChoice.value = false
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
private fun CurrenciesStatement(
    currencies: List<Currency>,
    inputTextView: MutableState<String>,
    currencyInFiat:MutableState<String>,
    inputDone: MutableState<Boolean>,
    onCurrencyClick: (String) -> Unit = {},
) {
    StatementCard(
        modifier = Modifier.semantics { contentDescription = "Currency Card" },
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
            pubKey = currency.pubKey,
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
private fun SingleCurrencyStatement(
    modifier:Modifier=Modifier,
    currency: Currency,
    inputTextView: MutableState<String>,
    currencyInFiat: MutableState<String>,
    currencyOn:MutableState<Boolean>,
    inputDone: MutableState<Boolean>, // just to add safely currency symbol
    onClickRow:()->Unit
) {
    StatementCard(
        modifier =modifier,
        items = listOf(currency),
        doubleColumn = false,
        single = true,
    ) { row ->
        CurrencyRow(
            modifier = Modifier        //specific to this screen
               // .padding(PADDING_FOR_BASE_SIMPLE_ROW, 0.dp) // padding to be close to address box
                .clickable {
                    onClickRow()
                },
            name = row.name,
            coin = row.coin,
            pubKey = row.pubKey,
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
private fun AddressesStatement(
    addresses: List<Address>,
    currencyInFiat: MutableState<String>,
    onAddressClick: (String) -> Unit = {},
) {
    StatementCard(
        modifier = Modifier.semantics { contentDescription = "Addresses Statement" },
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
            pubKey = address.pubKey,
            largeRow = false, // appearance of row rounded box or circle
            single = false,
            currencyInFiat = currencyInFiat,
            useInput = true,

        )
    }
}

/**
 * Detail screen for a single address
 */
@Composable
private fun SingleAddressStatement(
    modifier: Modifier= Modifier,
    address: Address,
    currencyInFiat: MutableState<String>,
    onClickRow:()->Unit,
) {
    StatementCard(
        modifier =modifier,
        items = listOf(address),
        doubleColumn = false,
        single = true,
    ) { row ->
        AddressRow(
            modifier = Modifier.clickable {
                onClickRow()
            },
            name = row.name,
            pubKey= row.pubKey,
            largeRow = true,
            single = true,
            currencyInFiat = currencyInFiat,
            useInput = true,
            color = row.color
        )
    }
}

@Composable
private fun TransactionFee() {

    Spacer(modifier = Modifier.height(15.dp))
    Box{
            Box(
                modifier = Modifier
                    .padding(18.dp)
                    .shadow(elevation = 25.dp, shape = RectangleShape, clip = false)
                    .sizeIn(220.dp, 43.dp, 220.dp, 43.dp)
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
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .align(Alignment.Center),
                )
            }
            CircleImage(
                modifier = Modifier.align(Alignment.CenterEnd),
                "select", 35.dp, 35.dp  //select = interstellar icon with border
            )

        }
    Spacer(modifier = Modifier.height(30.dp))
}


@Composable
private fun CheckButton(onClickGo: () -> Unit) {
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