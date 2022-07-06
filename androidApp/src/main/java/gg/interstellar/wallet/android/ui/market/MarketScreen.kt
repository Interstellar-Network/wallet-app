package gg.interstellar.wallet.android.ui.market

import gg.interstellar.wallet.android.R


import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

import gg.interstellar.wallet.android.data.Currency

import gg.interstellar.wallet.android.ui.components.CurrencyRow
import gg.interstellar.wallet.android.ui.components.StatementBody


/**
 * The market screen.
 */
//@Preview
@Composable
fun CurrenciesBody(
    currencies: List<Currency>,
    inputTextView: MutableState<String>,
    currencyInFiat:MutableState<String>,
    inputDone: MutableState<Boolean>,
    onCurrencyClick: (String) -> Unit = {},
) {
    StatementBody(
        modifier = Modifier.semantics { contentDescription = "Market Screen" },
        items = currencies,
        colors = { currency -> currency.color },
        amounts = { currency-> currency.balance },
        amountsFiat= {currency-> currency.balanceFiat },
        amountsTotal = currencies.map { currency -> currency.balanceFiat }.sum(),
        circleLabel = stringResource(R.string.total),
        screenLabel = "Market",
        doubleColumn = false,
        single = false,
        fiat = true
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
            usd =currency.usd,
            changeOn = true,
            largeRow = true, // appearance of row rounded box or circle
            inputTextView = inputTextView,
            currencyInFiat= currencyInFiat,
            useInput = false,
            inputDone=inputDone,
            single = false,
            fiat = true,
            color = currency.color
        )
    }
}

/**
 * Detail screen for a single currency.
 */
@Composable
fun SingleCurrencyBody(
    currency: Currency,
    inputTextView: MutableState<String>,
    currencyInFiat:MutableState<String>,
    inputDone: MutableState<Boolean>,
){
    StatementBody(
        items = listOf(currency),
        colors = { currency.color },
        amounts = { currency.balance },
        amountsFiat ={ currency.balanceFiat },
        amountsTotal = currency.balance,
        circleLabel = currency.coin,
        screenLabel = currency.name,
        doubleColumn = false,
        single = true, // bad trick to use display first statement on single body
        fiat = false
    ) { row ->
        CurrencyRow(
            name = row.name,
            coin = row.coin,
            pubKey = row.pubKey,
            amount = row.balance,
            amountFiat = row.balanceFiat,
            change = row.change,
            usd = row.usd,
            changeOn = true,
            largeRow = true,
            inputTextView = inputTextView,
            currencyInFiat =  currencyInFiat,
            useInput = false,
            inputDone = inputDone,
            single = true,
            fiat = true,
            color = row.color
        )
    }
}