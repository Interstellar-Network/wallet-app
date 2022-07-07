package gg.interstellar.wallet.android.ui.components

import StatementCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Generic component used by the  screens to show a chart and a list of items.
 */
@Composable
fun <T> StatementBody(
    modifier: Modifier = Modifier,
    items: List<T>,
    colors: (T) -> Color,
    amounts: (T) -> Float,
    amountsFiat: (T) -> Float,
    amountsTotal: Float,
    circleLabel: String,
    screenLabel: String,
    doubleColumn: Boolean,
    single: Boolean,
    fiat: Boolean,
    rows: @Composable (T) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,

        modifier = modifier.verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(20.dp))
        DisplayInterstellar()
        Spacer(Modifier.height(20.dp))
        ScreenTopBox(modifier=Modifier,screenLabel)
        //Spacer(Modifier.height(10.dp))
        //TopCircle(items,colors,amounts,amountsFiat,amountsTotal,circleLabel,fiat)

        Spacer(Modifier.height(10.dp))
        StatementCard(modifier,items,doubleColumn,single,rows)
    }
}

@Composable
fun <T> TopCircle( items: List<T>,colors: (T) -> Color, amounts: (T) -> Float,
                   amountsFiat: (T) -> Float,
                   amountsTotal: Float, circleLabel: String,fiat:Boolean) {

    Box(Modifier.padding(16.dp)) {
        // Extract proportion for amouuntsFiat not amounts in currency
        val accountsProportion = items.extractProportions { amountsFiat(it)}
        val circleColors = items.map { colors(it) }
        AnimatedCircle(
            accountsProportion,
            circleColors,
            Modifier
                .height(130.dp) // 10.dp circle is not shown
                .align(Alignment.Center)
                .fillMaxWidth()
        )
        Column(modifier = Modifier.align(Alignment.Center)) {

            val color = if (MaterialTheme.colors.isLight) Color.Black else Color.White
            val add = if (fiat) "$" else ""
            Text(
                text = circleLabel,
                style = MaterialTheme.typography.body1,
                color = color,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = if ( amountsTotal != 0f  ) add + formatAmount(amountsTotal)
                else "",
                style = MaterialTheme.typography.h2,
                color = color,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

}



