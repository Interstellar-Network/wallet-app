import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> StatementCard(
    items: List<T>,
    doubleColumn: Boolean,
    rows: @Composable (T) -> Unit,
) {

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
            // if SingleBody  100, 30 works well
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