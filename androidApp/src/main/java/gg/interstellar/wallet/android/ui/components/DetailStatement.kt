import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> StatementCard(
    //TODO card or box
    modifier: Modifier = Modifier,
    items: List<T>,
    rows: @Composable (T) -> Unit,
) {
    Box {
        Column(
            //LazyColumn( ///TEST Lazy Column
            modifier = modifier.padding(0.dp)
        ) {
            DoubleColumn(items, rows)
        }
    }
}

/** extract first row (use for send currencies init from list except in single currency
 * when doubleColumn are always false*/
@Composable
private fun <T> DoubleColumn(items: List<T>, rows: @Composable (T) -> Unit) {
    Row {
        Column {
            //items(items) { //TEST lazy column
            val list = items.filterIndexed { index, _ -> (index % 2 == 0) }
            list.forEach { item ->
                rows(item)
                Spacer(Modifier.height(30.dp))
            }
        }
        Spacer(Modifier.width(30.dp))
        Column {
            val list = items.filterIndexed { index, _ -> (index % 2 != 0) }
            list.forEach { item ->
                rows(item)
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}
