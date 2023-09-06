package gg.interstellar.wallet.android.ui.send


import StatementCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.data.Address
import gg.interstellar.wallet.android.data.Currency
import gg.interstellar.wallet.android.data.UserData
import gg.interstellar.wallet.android.ui.components.CircleButtonDest
import gg.interstellar.wallet.android.ui.components.CircleIcon
import gg.interstellar.wallet.android.ui.components.CircleImage
import gg.interstellar.wallet.android.ui.components.HeaderWithBrand
import gg.interstellar.wallet.android.ui.components.LargeTextOnlyRow
import gg.interstellar.wallet.android.ui.components.ScreenTopBox
import gg.interstellar.wallet.android.ui.components.SmallCircleRow
import gg.interstellar.wallet.android.ui.portfolio.ShowCurrencyWidget

/**
 * The logic is:
 * - by default we show no currencies nor addresses lists
 * - when the user click "Select currency" or "Select address" we show the corresponding list
 * - after the user has picked an entry from this list we show the other one
 * - when BOTH have been selected we show the "Go" button AND the transaction fees
 *   AND we remove both the currencies and addresses list
 *
 * NOTE: most of the parameters (except "currencies" and "addresses") are parameters
 * only to allow various preview states to be tested.
 */
@Composable
fun SendCurrenciesBody(
    currencies: List<Currency>,
    addresses: List<Address>,
    selectedCurrency: MutableState<Currency?> = remember { mutableStateOf(null) },
    selectedContact: MutableState<Address?> = remember { mutableStateOf(null) },
    validatedInputAmount: MutableState<Float?> = remember { mutableStateOf(null) },
    isReady: MutableState<Boolean> = remember { mutableStateOf(false) },
    onClickGo: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
        // TODO? deactivate scrolling
//        if (!currencyChoice.value && !addressChoice.value && inputDone.value)
//            Modifier else Modifier.verticalScroll(rememberScrollState())
    ) {
        HeaderWithBrand()

        ScreenTopBox(modifier = Modifier, "Send")

        Spacer(Modifier.height(20.dp))

        // We arbitrarily decide to show the currencies list by default
        val shouldShowCurrencies = remember { mutableStateOf((!isReady.value) && true) }
        val shouldShowAddresses = remember { mutableStateOf((!isReady.value) && false) }

        // The goal is to have the button basically on top of PlaceholderSelectCurrency/PlaceholderSingleAddressStatement
        // ie we DO NOT want a space b/w these
        PlaceholderSelectCurrency(
            currency = selectedCurrency,
            validatedFloatAmount = validatedInputAmount,
            onClickRow = {
                isReady.value = false
                shouldShowCurrencies.value = true
                shouldShowAddresses.value = false
            },
            // TODO?
//                onDone = { textInput ->
//                    val parsedFloat = textInput.toFloatOrNull()
//                    if (parsedFloat != null) {
//                        isReady.value = textInput.isNotBlank()
//                        inputAmount.value = parsedFloat
//                    }
//                }
        )

        Spacer(Modifier.height(5.dp))

        // in order it is b/w the two PlaceholderSingleAddressStatement / PlaceholderSelectCurrency
        // - requiredHeightIn: that makes the "measures" ignored; so the other elements are drawn like this one is not here
        // - zIndex: ensures it will be drawn above "PlaceholderSelectAddress" which is drawn after
        CircleButtonDest(
            2.dp,
            Modifier.requiredHeightIn(0.dp, 0.dp).zIndex(1.0f)
        )

        PlaceholderSelectAddress(
            modifier = Modifier /* .offset(y = 95.dp) */,
            address = selectedContact,
            currency = selectedCurrency,
            amount = validatedInputAmount
        ) {
            isReady.value = false
            shouldShowCurrencies.value = false
            shouldShowAddresses.value = true
        }

        Spacer(Modifier.height(20.dp))

        ShowCurrenciesOrAddresses(
            modifier = Modifier,
            currencies = currencies,
            addresses = addresses,
            shouldShowCurrencies = shouldShowCurrencies,
            shouldShowAddresses = shouldShowAddresses,
            onCurrencyClick = { currency ->
                selectedCurrency.value = currency

                if (selectedContact.value != null) {
                    isReady.value = true
                }

                shouldShowCurrencies.value = false
                shouldShowAddresses.value = !isReady.value
            },
            onAddressClick = { address ->
                selectedContact.value = address

                if (selectedCurrency.value != null) {
                    isReady.value = true
                }

                shouldShowCurrencies.value = !isReady.value
                shouldShowAddresses.value = false
            }
        )

        if ((selectedCurrency.value != null) && (selectedContact.value != null) && (validatedInputAmount.value != null)) {
            CircleIcon(
                imageVector = Icons.Filled.Add, border = 0.dp, string = "add",
                size_height = 25.dp, size_width = 25.dp
            )
            TransactionFee(drawableId = R.drawable.ic_interstellar_black_icon_white_border)
            CheckButton(onClickGo)
        }
    }
}

@Preview(device = "id:pixel_7_pro", showSystemUi = true)
@Composable
fun SendCurrenciesBodyPreviewPixel7Pro() {
    SendCurrenciesBody(currencies = UserData.currencies, addresses = UserData.addresses) {}
}

@Preview(device = "id:pixel_6a", showSystemUi = true)
@Composable
fun SendCurrenciesBodyPreviewPixel6a() {
    SendCurrenciesBody(currencies = UserData.currencies, addresses = UserData.addresses) {}
}

@Preview(device = "id:pixel_4_xl", showSystemUi = true)
@Composable
fun SendCurrenciesBodyPreviewPixel4Xl() {
    SendCurrenciesBody(currencies = UserData.currencies, addresses = UserData.addresses) {}
}

@Preview(device = "id:pixel_7_pro", showSystemUi = true)
@Composable
fun SendCurrenciesBodyPreviewPixel7ProReady() {
    val selectedCurrency = remember { mutableStateOf<Currency?>(UserData.currencies[0]) }
    val selectedContact = remember { mutableStateOf<Address?>(UserData.addresses[1]) }
    val validatedInputAmount = remember { mutableStateOf<Float?>(42.0f) }
    val isReady = remember { mutableStateOf(true) }
    SendCurrenciesBody(
        currencies = UserData.currencies,
        addresses = UserData.addresses,
        selectedCurrency = selectedCurrency,
        selectedContact = selectedContact,
        validatedInputAmount = validatedInputAmount,
        isReady = isReady
    ) {}
}

@Preview(device = "id:pixel_6a", showSystemUi = true)
@Composable
fun SendCurrenciesBodyPreviewPixel6aReady() {
    val selectedCurrency = remember { mutableStateOf<Currency?>(UserData.currencies[0]) }
    val selectedContact = remember { mutableStateOf<Address?>(UserData.addresses[1]) }
    val validatedInputAmount = remember { mutableStateOf<Float?>(42.0f) }
    val isReady = remember { mutableStateOf(true) }
    SendCurrenciesBody(
        currencies = UserData.currencies,
        addresses = UserData.addresses,
        selectedCurrency = selectedCurrency,
        selectedContact = selectedContact,
        validatedInputAmount = validatedInputAmount,
        isReady = isReady
    ) {}
}

@Preview(device = "id:pixel_4_xl", showSystemUi = true)
@Composable
fun SendCurrenciesBodyPreviewPixel4XlReady() {
    val selectedCurrency = remember { mutableStateOf<Currency?>(UserData.currencies[0]) }
    val selectedContact = remember { mutableStateOf<Address?>(UserData.addresses[1]) }
    val validatedInputAmount = remember { mutableStateOf<Float?>(42.0f) }
    val isReady = remember { mutableStateOf(true) }
    SendCurrenciesBody(
        currencies = UserData.currencies,
        addresses = UserData.addresses,
        selectedCurrency = selectedCurrency,
        selectedContact = selectedContact,
        validatedInputAmount = validatedInputAmount,
        isReady = isReady
    ) {}
}

/**
 * Show either `CurrenciesStatement` or `AddressesStatement`, or nothing
 * The behavior depends on/when the user clicks "Select a contact" or "Select a currency"
 */
@Composable
fun ShowCurrenciesOrAddresses(
    modifier: Modifier,
    currencies: List<Currency>,
    addresses: List<Address>,
    shouldShowCurrencies: MutableState<Boolean>,
    shouldShowAddresses: MutableState<Boolean>,
    onCurrencyClick: (Currency) -> Unit = {},
    onAddressClick: (Address) -> Unit = {},
) {
    if (shouldShowCurrencies.value) {
        CurrenciesStatement(
            currencies = currencies,
            onCurrencyClick = onCurrencyClick,
            modifier = modifier
        )
    }

    if (shouldShowAddresses.value) {
        AddressesStatement(
            addresses = addresses,
            onAddressClick = onAddressClick,
            modifier = modifier
        )
    }
}

@Preview
@Composable
fun ShowCurrenciesOrAddressesPreviewDefault() {
    val shouldShowCurrencies = remember { mutableStateOf(true) }
    val shouldShowAddresses = remember { mutableStateOf(false) }
    ShowCurrenciesOrAddresses(
        currencies = UserData.currencies,
        addresses = UserData.addresses,
        shouldShowCurrencies = shouldShowCurrencies,
        shouldShowAddresses = shouldShowAddresses,
        modifier = Modifier,
    )
}

/** Detail statement for currencies
 */
@Composable
fun CurrenciesStatement(
    currencies: List<Currency>,
    onCurrencyClick: (Currency) -> Unit = {},
    modifier: Modifier
) {
    StatementCard(
        modifier = modifier.semantics { contentDescription = "Currency Card" },
        items = currencies,
    ) // appearance double column or one row
    { currency ->
        SmallCircleRow(
            modifier = modifier.clickable {
                onCurrencyClick(currency)
            },
            drawableId = currency.drawableId,
            label = " $${(currency.balance * currency.usd)} ",
            change = currency.change,
        )
    }
}

@Preview
@Composable
fun CurrenciesStatementPreview() {
    CurrenciesStatement(modifier = Modifier, currencies = UserData.currencies, onCurrencyClick = {})
}

/**
 * Wrapper for `SingleCurrencyStatement`
 * - ShowCurrencyWidget is displayed once a currency is chosen
 * - "Select a currency" basic text is displayed until then
 */
@Composable
private fun PlaceholderSelectCurrency(
    currency: MutableState<Currency?>,
    validatedFloatAmount: MutableState<Float?>,
    onClickRow: () -> Unit,
) {
    Box(modifier = Modifier.height(90.dp)) {
        if (currency.value == null) {
            LargeTextOnlyRow(
                color = UserData.currencies[0].color,
                drawableId = null,
                text = "Select a currency",
                bottomLabel = null,
                validatedFloatAmount = null,
                onClick = onClickRow,
            )
        } else {
            // [demo] We auto-fill with the Currency's balance for convenience
            if (validatedFloatAmount.value == null) {
                validatedFloatAmount.value = currency.value!!.balance
            }
            ShowCurrencyWidget(
                currency = currency.value!!, validatedFloatAmount = validatedFloatAmount,
                showChange = false,
                onClick = onClickRow,
            )
        }
    }
}

@Preview
@Composable
fun PlaceholderSelectCurrencyPreviewDefault() {
    val currency = remember { mutableStateOf<Currency?>(null) }
    val validatedInputAmount = remember { mutableStateOf<Float?>(null) }
    PlaceholderSelectCurrency(
        currency = currency,
        validatedFloatAmount = validatedInputAmount,
        onClickRow = {})
}

@Preview
@Composable
fun SelectedCurrencyPreviewWithSelected() {
    val currency = remember { mutableStateOf<Currency?>(UserData.currencies[0]) }
    val validatedInputAmount = remember { mutableStateOf<Float?>(null) }
    PlaceholderSelectCurrency(
        currency = currency,
        validatedFloatAmount = validatedInputAmount,
        onClickRow = {})
}

@Preview
@Composable
fun SelectedCurrencyPreviewWithSelected2() {
    val currency = remember { mutableStateOf<Currency?>(UserData.currencies[0]) }
    val validatedInputAmount = remember { mutableStateOf<Float?>(42.0f) }
    PlaceholderSelectCurrency(
        currency = currency,
        validatedFloatAmount = validatedInputAmount,
        onClickRow = {})
}


@Composable
private fun AddressesStatement(
    addresses: List<Address>,
    onAddressClick: (Address) -> Unit = {},
    modifier: Modifier
) {
    StatementCard(
        modifier = modifier.semantics { contentDescription = "Addresses Statement" },
        items = addresses,
    ) // appearance double column or one row
    { address ->
        SmallCircleRow(
            modifier = modifier.clickable {
                onAddressClick(address)
            },
            drawableId = address.drawableId,
            label = " " + address.name.uppercase() + " ",
            change = null,
        )
    }
}

@Preview
@Composable
fun AddressesStatementPreview() {
    AddressesStatement(addresses = UserData.addresses, onAddressClick = {}, modifier = Modifier)
}


/**
 * Like `SingleCurrencyStatement` but for the Contact(== Address)
 * - Contact name is displayed once a contact is chosen
 * - "Select a contact" basic text is displayed until then
 */
@Composable
private fun PlaceholderSelectAddress(
    modifier: Modifier,
    address: MutableState<Address?>,
    currency: MutableState<Currency?>,
    amount: MutableState<Float?>,
    onClickRow: () -> Unit,
) {
    Box(modifier = modifier.height(90.dp)) {
        // TODO DRY b/w "if address.value == null" and else
        if (address.value == null) {
            LargeTextOnlyRow(
                color = UserData.currencies[0].color,
                drawableId = null,
                text = "Select a contact",
                bottomLabel = null,
                validatedFloatAmount = null,
                onClick = onClickRow,
            )
        } else {
            // NOTE: the Contact CAN be selected BEFORE the Currency
            // so we need a few checks before displaying the bottom label
            // (ie the USD amount entered in the Currency text input)
            val bottomLabel = if ((amount.value != null) && (currency.value != null)) {
                " $${amount.value!! * currency.value!!.usd} "
            } else {
                null
            }
            LargeTextOnlyRow(
                color = address.value!!.color,
                drawableId = address.value!!.drawableId,
                text = address.value!!.name.uppercase(),
                bottomLabel = bottomLabel,
                validatedFloatAmount = null,
                onClick = onClickRow,
            )
        }
    }
}

@Preview
@Composable
fun PlaceholderSelectAddressPreviewDefault() {
    val selectedCurrency = remember { mutableStateOf<Currency?>(null) }
    val amount = remember { mutableStateOf<Float?>(null) }
    val address = remember { mutableStateOf<Address?>(null) }
    PlaceholderSelectAddress(
        modifier = Modifier.offset(y = 110.dp),
        address = address,
        onClickRow = {},
        currency = selectedCurrency,
        amount = amount
    )
}

@Preview
@Composable
fun PlaceholderSelectAddressPreviewWithSelectedContact() {
    val selectedCurrency = remember { mutableStateOf<Currency?>(UserData.currencies[1]) }
    val amount = remember { mutableStateOf<Float?>(42.0f) }
    val address = remember { mutableStateOf<Address?>(UserData.addresses[1]) }
    PlaceholderSelectAddress(
        modifier = Modifier.offset(y = 110.dp),
        address = address,
        onClickRow = {},
        currency = selectedCurrency,
        amount = amount
    )
}

@Composable
private fun TransactionFee(drawableId: Int) {

    Spacer(modifier = Modifier.height(15.dp))
    Box {
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
            drawableId, 35.dp, 35.dp  //select = interstellar icon with border
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
