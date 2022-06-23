import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme

@Preview
@Composable
private fun Keypad() {

    InterstellarWalletTheme {
        // We MUST set "weight" on each children, that weight each row will have the same height
        Column()
        {
            Row { Spacer(Modifier.height(20.dp)) }

            StandardPinpadRow("7", "8", "9")
            StandardPinpadRow("4", "5", "6")
            StandardPinpadRow("1", "2", "3")

            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
            ) {
                // In this case we must set the wight b/c we have different children contrary to the
                // other rows. TODO? is there a better way to do this?
                Spacer(Modifier.weight(0.400f))

                SetPadCircle("0")

                BoxWithConstraints(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(0.20f)
                        .wrapContentSize()
                ) {
                    Surface(
                        modifier = Modifier.padding(25.dp, 25.dp),
                        shape = RoundedCornerShape(25),
                        elevation = 28.dp,
                        color = if (MaterialTheme.colors.isLight) Color.Black
                        else Color.White,

                        ) {
                        Icon(
                            Icons.Filled.Close,
                            modifier = Modifier,
                            contentDescription = "close icon",
                            tint = if (MaterialTheme.colors.isLight) Color.White
                            else Color.Black,
                        )
                    }

                }

                Spacer(Modifier.weight(0.20f))
            }

            Row { Spacer(Modifier.height(20.dp)) }
        }
    }
}

/**
 * Standard Pinpad row, with 3 circles
 */
@Composable
private fun ColumnScope.StandardPinpadRow(string0: String,string1: String,string2: String) {
    Row(
        horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.25f)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        SetPadCircle(string0)
        SetPadCircle(string1)
        SetPadCircle(string2)
    }
}

@Composable
fun SetPadCircle(string:String) {
    Box(
        modifier = Modifier
            .shadow(elevation = 35.dp, shape = CircleShape, clip = false)

    ) {
        Surface(
            modifier = Modifier
                .sizeIn(80.dp, 80.dp, 90.dp, 90.dp)
                .aspectRatio(1f)
                .padding(9.dp),
            shape = CircleShape,
            elevation = 15.dp,
            color = if (MaterialTheme.colors.isLight) Color.Black
            else Color.White,
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                shape = CircleShape,
                color = if (MaterialTheme.colors.isLight) Color.Black
                else Color.White,
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Text(
                        text=string,
                        fontSize = 45.sp,
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically),
                    )
                }
            }
        }
    }
}
