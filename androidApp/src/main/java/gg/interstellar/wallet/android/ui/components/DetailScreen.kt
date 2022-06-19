package gg.interstellar.wallet.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import gg.interstellar.wallet.android.ui.DisplayInterstellar
import gg.interstellar.wallet.android.ui.ScreenTopBox
import gg.interstellar.wallet.android.ui.extractProportions
import gg.interstellar.wallet.android.ui.formatAmount
import java.nio.channels.Selector

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
        ScreenTopBox(screenLabel)

        //TopCircle(items,colors,amounts,amountsFiat,amountsTotal,circleLabel,fiat)

        Spacer(Modifier.height(10.dp))
        BottomCard(items,rows,doubleColumn)
    }
}

@Composable
fun <T> TopCircle( items: List<T>,colors: (T) -> Color, amounts: (T) -> Float,
                   amountsFiat: (T) -> Float,
                   amountsTotal: Float, circleLabel: String,fiat:Boolean) {

    Box(Modifier.padding(16.dp)) {
        // Extract proportion for amouuntsFiat not amounts in currency
        val accountsProportion = items.extractProportions { amountsFiat(it)}
        //TODO//amountsFiat(it) solve issue
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
                //TODo manage circle label with coin name for single screen
                text = if ( amountsTotal != 0f  ) add + formatAmount(amountsTotal)
                else "",
                style = MaterialTheme.typography.h2,
                color = color,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

}




@Composable
fun <T> BottomCard( items: List<T>,  rows: @Composable (T) -> Unit,doubleColumn: Boolean ) {

    Card(
        backgroundColor = if (MaterialTheme.colors.isLight)
            Color.White else MaterialTheme.colors.onSurface
    ) {
        Column(
            //LazyColumn( ///TEST Lazy Column
            modifier = Modifier.padding(
                if (doubleColumn) 10.dp else 100.dp,
                30.dp
            )
            //TODO conditional padding to adapt to SingleBody
            // if SingleBody - 100.30 works well
        ) {
            if (doubleColumn) {
                DoubleColumn(items, rows)
            } else SingleColumn(items, rows)
        }
    }
}




@Composable
private fun <T> DoubleColumn( items: List<T>,  rows: @Composable (T) -> Unit ){
    Row {
        Column {
            //items(items) { //TEST lazy column
            val list= items.filterIndexed { index, s-> (index%2==0) }
            list.forEach { item ->
                rows(item)  }
        }
        Spacer(Modifier.width(40.dp))
        Column {
            val list = items.filterIndexed { index, s-> (index%2!=0) }
            list.forEach { item ->
                rows(item) }
        }
    }
}
@Composable
private fun <T> SingleColumn( items: List<T>,  rows: @Composable (T) -> Unit ){
    items.forEach { item ->
        rows(item)
    }
}