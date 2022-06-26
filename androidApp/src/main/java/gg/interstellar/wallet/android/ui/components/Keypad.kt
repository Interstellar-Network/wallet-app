package gg.interstellar.wallet.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.ui.theme.Modernista

//@Preview
@Composable
fun Keypad(
    modifier:Modifier=Modifier,
    onClickKP:(String) -> Unit
) {

    //val input = remember { mutableStateOf("test") }

    //val onClickKP = { text:String-> handlebuttonClick(text,input) }
    Column(
        modifier = modifier
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Row{//TO TEST
            //Text(text = input.value) -white
        }
        Row { Spacer(Modifier.height(5.dp)) }

        StandardPinpadRow("7", "8", "9", onClickKP)
        StandardPinpadRow("4", "5", "6", onClickKP)
        StandardPinpadRow("1", "2", "3", onClickKP)

        Row(
            horizontalArrangement = Arrangement.Center, modifier = modifier
                .fillMaxWidth()
                .weight(0.25f)
        ) {
            // In this case we must set the wight b/c we have different children contrary to the
            // other rows. TODO? is there a better way to do this?
            Spacer(Modifier.weight(0.20f))
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .weight(0.15f)
                    .wrapContentSize()
            ) {
                Surface(
                    modifier = modifier.padding(25.dp, 25.dp),
                    shape = RoundedCornerShape(25),
                    elevation = 28.dp,
                    color = if (MaterialTheme.colors.isLight) Color.Black
                    else Color.White,

                    ) {

                    IconButton(onClick = { onClickKP("CE") }) {
                        Icon(
                            Icons.Filled.Close,
                            modifier = Modifier,
                            contentDescription = "close icon",
                            tint = if (MaterialTheme.colors.isLight) Color.White
                            else Color.Black,
                        )
                    }
                }
            }
            SetPadCircle("0", onClickKP)
            //SetPadCircle( stringResource(R.string.one_point_redacted))
            SetPadCircle(".", onClickKP)
            Spacer(Modifier.weight(0.20f))

            Row { Spacer(Modifier.height(30.dp)) }
        }


    }

}

/**
 * Standard Pinpad row, with 3 circles
 */
@Composable
fun ColumnScope.StandardPinpadRow(
    string0: String, string1: String,string2: String,
    onClickKP: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.25f)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        SetPadCircle(string0,onClickKP)
        SetPadCircle(string1,onClickKP)
        SetPadCircle(string2,onClickKP)
    }
}

@Composable
fun SetPadCircle(
    string:String,
    onClickKP: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .shadow(elevation = 35.dp, shape = CircleShape, clip = false)

    ) {
        Surface(
            modifier = Modifier
                .sizeIn(60.dp, 60.dp, 70.dp, 70.dp)
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
                Button(onClick = { onClickKP(string) },
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor =
                        if (MaterialTheme.colors.isLight) Color.Black
                        else Color.White,
                    )
                ) {
                    Text(
                        text=string,
                        fontFamily = Modernista,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically),
                        color=if (MaterialTheme.colors.isLight) Color.White
                        else Color.Black,
                    )
                }
            }
        }
    }
}


fun handlebuttonClick(
    txt:String,
    inputTextView: MutableState<String>,
    firstTime: MutableState<Boolean>

) {
    if (firstTime.value && inputTextView.value.isNotEmpty() ) {
        inputTextView.value =""
        firstTime.value = false
    }
    when (txt) {
        "CE" -> if (inputTextView.value.isNotEmpty()) {
            inputTextView.value = ""
        }
        else -> inputTextView.value += txt
    }
}

