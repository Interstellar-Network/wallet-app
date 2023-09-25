package gg.interstellar.wallet.android.ui.portfolio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.data.Currency
import gg.interstellar.wallet.android.data.UserData
import gg.interstellar.wallet.android.ui.components.HeaderWithBrand
import gg.interstellar.wallet.android.ui.components.LargeTextOnlyRow
import gg.interstellar.wallet.android.ui.components.ScreenTopBox
import gg.interstellar.wallet.android.ui.components.ScreenTopBoxWithCircleLabel
import gg.interstellar.wallet.android.ui.send.CurrenciesStatement
import java.util.Locale

/**
 * The Portfolio screen.
 */
@Composable
fun PortfolioBody(
    currencies: List<Currency>,
    onCurrencyClick: (Currency) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())

    ) {
        HeaderWithBrand()

        ScreenTopBoxWithCircleLabel(
            modifier = Modifier.padding(0.dp, 0.dp),
            title = "$${amountTotal(currencies)}",
            4.35f
        )
        Spacer(Modifier.height(19.dp))
        Surface(
            modifier = Modifier
                .height(150.dp),
            shape = RectangleShape,
            color = Color.Transparent, //MaterialTheme.colors.onSurface ,
            //elevation = 2.dp
        ) {
            Box {
                Box {
                    Image(
                        painter = painterResource(R.drawable.ic_graph_mockup),
                        contentDescription = "graph",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                DrawDashLine(
                    Modifier
                        .align(Alignment.Center)
                        .padding(50.dp, 0.dp),
                    MaterialTheme.colors.surface,
                )
            }
        }
        Spacer(Modifier.height(54.dp))

        CurrenciesStatement(
            currencies, onCurrencyClick = onCurrencyClick, modifier = Modifier,
        )
    }
}

@Composable
private fun DrawDashLine(
    modifier: Modifier,
    color: Color
) {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(44f, 18f), 0f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
    ) {
        drawLine(
            color = color,
            strokeWidth = 3f,
            start = Offset(74f, 74f),
            end = Offset(size.width, 74f),
            pathEffect = pathEffect
        )
    }
}


private fun amountTotal(
    currencies: List<Currency>,
): Float {
    return currencies.map { currency -> currency.balance * currency.usd }.sum()
}

@Composable
fun SingleCurrencyBody(
    currency: Currency,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        HeaderWithBrand()

        ScreenTopBox(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            currency.name.capitalize(Locale.getDefault())
        )

        Spacer(Modifier.height(30.dp))

        // TODO make this param an option
        val validatedInputAmount = remember { mutableStateOf<Float?>(null) }
        ShowCurrencyWidget(currency, validatedInputAmount, true, " $${currency.balance * currency.usd} ", null)
    }
}

/**
 * If "amount" is given it will be used, else it defaults to using "currency.balance"
 */
@Composable
fun ShowCurrencyWidget(
    currency: Currency, validatedFloatAmount: MutableState<Float?>,
    showChange: Boolean,
    bottomLabel: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val balance = validatedFloatAmount.value ?: currency.balance

    Box(modifier = Modifier.height(100.dp)) {
        LargeTextOnlyRow(
            color = currency.color,
            drawableId = currency.drawableId,
            text = "$balance",
            bottomLabel = bottomLabel,
            change = if (showChange) currency.change else null,
            validatedFloatAmount = validatedFloatAmount,
            onClick = onClick,
        )
    }
}

@Preview
@Composable
fun SingleCurrencyBodyPreview() {
    SingleCurrencyBody(UserData.currencies[1])
}




