package gg.interstellar.wallet.android.ui.portfolio

import StatementCard
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.data.Currency
import gg.interstellar.wallet.android.ui.components.*

/**
 * The Portfolio screen.
 */
@Composable
fun PortfolioBody(
    currencies: List<Currency>,
    inputTextView: MutableState<String>,
    currencyInFiat: MutableState<String>,
    inputDone: MutableState<Boolean>,
    onCurrencyClick: (String) -> Unit = {},
) {
    Column(
       horizontalAlignment = Alignment.CenterHorizontally,
       modifier = Modifier.verticalScroll(rememberScrollState())
       
    ) {
        Spacer(Modifier.height(30.dp))
        DisplayInterstellar()

        Spacer(Modifier.height(0.dp))

        ScreenTopBoxWithCircleLabel(
            modifier = Modifier.padding(0.dp, 0.dp),
            amountTotal(currencies),
            4.35f
        )
        Spacer(Modifier.height(19.dp))
        Surface (
            modifier = Modifier
                .height(150.dp),
            shape = RectangleShape,
            color = Color.Transparent, //MaterialTheme.colors.onSurface ,
            //elevation = 2.dp
        ) {
            Box(
                //modifier =Modifier.fillMaxHeight()
            ) {
                Box {
                    Image(
                        painter = painterResource(R.drawable.ic_graph_mockup),
                        contentDescription = "graph",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                DrawDashLine(Modifier
                    .align(Alignment.Center)
                    .padding(65.dp,0.dp),
                    MaterialTheme.colors.surface,)
            }
        }
        Spacer(Modifier.height(54.dp))

        CurrenciesStatement(
            currencies, inputTextView, currencyInFiat, inputDone,
            onCurrencyClick = onCurrencyClick
        )
    }
}

@Composable
private fun DrawDashLine(modifier:Modifier,
color:Color
) {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(44f,18f),0f)

    Canvas(modifier = modifier.fillMaxWidth().height(3.dp)) {
        drawLine(
            color = color,
            strokeWidth = 3f,
            start = Offset(74f, 74f),
            end = Offset(size.width, 74f),
            pathEffect = pathEffect
        )
    }
}



private fun  amountTotal(
          currencies: List<Currency>,
): String {
    return "$"+ formatAmount(currencies.map { currency -> currency.balanceFiat }.sum())
}



@Composable
private fun CurrenciesStatement(
        currencies: List<Currency>,
        inputTextView: MutableState<String>,
        currencyInFiat:MutableState<String>,
        inputDone: MutableState<Boolean>,
        onCurrencyClick: (String) -> Unit = {},
){
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
            single = false,
            fiat = true,
            color = row.color
        )
    }
}




