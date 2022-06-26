import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> StatementCard( //TODO card or box
    modifier: Modifier=Modifier,
    items: List<T>,
    doubleColumn: Boolean,
    single:Boolean,
    rows: @Composable (T) -> Unit,
) {

    Box(
    ) {
        Column(
            //LazyColumn( ///TEST Lazy Column
            modifier = Modifier.padding(
            )
        ) {
            if (doubleColumn) {
                DoubleColumn(items, rows)
            } else SingleColumn(items, rows, single)
        }
    }
}

/** extract first row (use for send currencies init from list except in single currency
 * when doubleColumm are always false*/
@Composable
private fun <T> DoubleColumn( items: List<T>,  rows: @Composable (T) -> Unit ){

    Row {
        Column {
            //items(items) { //TEST lazy column
             val list= items.filterIndexed { index, _ -> (index !=0) && (index%2==0) }
            list.forEach { item ->
                rows(item)  }
        }
        Spacer(Modifier.width(40.dp))
        Column {
            val list = items.filterIndexed { index, _ -> (index !=0) && (index%2!=0) }
            list.forEach { item ->
                rows(item) }
        }
    }
}

@Composable
private fun <T> SingleColumn( items: List<T>,  rows: @Composable (T) -> Unit,
                              single: Boolean ){//TODO check
    val list = if (single) items else items.filterIndexed { index, _ -> (index !=0)}
    list.forEach { item ->
        rows(item)
    }
}