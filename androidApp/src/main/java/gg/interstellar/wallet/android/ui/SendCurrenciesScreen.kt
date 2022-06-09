package gg.interstellar.wallet.android.ui

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme
import androidx.compose.material.Icon as MaterialIcon

@Preview //(showBackground = true)
@Composable
fun SendCurrenciesScreen(onClickGo: () -> Unit = {},) {

    InterstellarWalletTheme(
        darkTheme = true

    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            DisplayInterstellar()
            SendButtonTop(RoundedCornerShape(33))

            FromToCurrenciesMiddle(RoundedCornerShape(20.dp))
            DestinationMiddle( RoundedCornerShape(20.dp))

            TransactionFee(RoundedCornerShape(20.dp))

            GoButtonBottom(onClickGo)
        }
    }
}


@Composable
private fun SendButtonTop(shape: Shape) {
    Box(
        modifier = Modifier
            .shadow(elevation = 50.dp, shape = CircleShape, clip = false)
    ) {
        Surface(
            modifier = Modifier
                //.fillMaxSize()
                //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
                .sizeIn(280.dp, 120.dp, 280.dp, 120.dp)
                .padding(25.dp),
            shape = CircleShape,
            elevation = 50.dp,
        ) { Box(
                modifier = Modifier
                    //TODO optimize gradient
                    .background(
                        Brush.linearGradient(
                            0.3f to MaterialTheme.colors.secondary,
                            1f to MaterialTheme.colors.primary,
                            start = Offset(0f, 0f),
                            end = Offset(310f, 310f)
                        )
                    )
            ) {
                Text("Send", modifier = Modifier
                    .align(Alignment.Center),
                    fontSize = 35.sp,
                )
            }

        }

    }
}

@Composable
private fun FromToCurrenciesMiddle(shape: Shape){

     Row {
        Box() {

            Box(
                modifier = Modifier
                    .shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
                    .sizeIn(220.dp, 80.dp, 220.dp, 80.dp)
                    .clip(shape)
                    //.padding(3.dp)
                    .background(
                        Brush.linearGradient(
                            0.4f to Color(0xFF627eea),
                            1f to Color.White,
                            start = Offset(0f, 0f),
                            end = Offset(650f, 0f),
                        )
                    )
            ) {
                Text(
                    "O.6 ETH",
                    modifier = Modifier
                        .align(Alignment.Center),
                    //fontSize = 10.dp,
                    // TODO fix fontsize does not work, Why?
                )
            }
            CircleButton25(Icons.Filled.Add, "add", 205.dp,25.5.dp,)
            //onClickGo )
            //TODO why onClickGo does not work
        }
     }
    //Row{ Spacer(Modifier.height(10.dp)) }// Blank row to adjust
}

@Composable
private fun CircleButton25(imageVector: ImageVector, string: String, dpx: Dp, dpy :Dp,)
                           //onClickGo: () -> Unit)
{
    Surface(
        modifier = Modifier

            .sizeIn(25.dp, 25.dp, 25.dp, 25.dp)
            .aspectRatio(1f)
        //.padding(horizontal = 1.dp),
        .offset(x =dpx, y = dpy),

        color = MaterialTheme.colors.surface,
        shape = CircleShape,
        elevation = 20.dp,
    ) {
        //IconButton(//TODO solve onClickGo issue
            //onClick = onClickGo,

            //Modifier.offset(x =dpx, y = dpy)
        //) {
            MaterialIcon(imageVector = imageVector, contentDescription = string,


            )
        //}
    }
}

@Composable
private fun DestinationMiddle(shape: Shape){

    //Row() {
        Surface(
            modifier = Modifier
                //.fillMaxSize()
                //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
                .sizeIn(25.dp, 25.dp, 25.dp, 25.dp)
                .aspectRatio(1f),
            //.padding(horizontal = 1.dp),
            //color = MaterialTheme.colors.surface,
            shape = CircleShape,
            border = BorderStroke(2.dp,
                if (MaterialTheme.colors.isLight) Color.White
                else Color.Black),
            elevation = 20.dp,
        ) {

            MaterialIcon(
                Icons.Filled.ArrowDropDown,"arrow drop down",
                Modifier.border(2.dp,
                    if (MaterialTheme.colors.isLight) Color.White
                    else Color.Black))
        }


        Box(
            modifier = Modifier
                .shadow(elevation = 15.dp, shape = RectangleShape, clip = false)
                .sizeIn(220.dp, 80.dp, 220.dp, 80.dp)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        0.4f to Color(0xFF627eea),
                        1.1f to Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(650f, 0f),
                    )
                )
        ) {
            Text(
                "John Doe",
                modifier = Modifier
                    .align(Alignment.Center),
                //fontSize = 10.dp,

                //color =,

                // TODO fix fontsize does not work, Why?
            )
        }

        Row{ Spacer(Modifier.height(10.dp)) }// Blank row to adjust

}

@Composable
private fun TransactionFee(shape: Shape){

    Row() {
        Surface(
            modifier = Modifier
                //.fillMaxSize()
                //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
                .sizeIn(18.dp, 18.dp, 18.dp, 18.dp)
                .aspectRatio(1f),
            //.padding(horizontal = 1.dp),
            color = MaterialTheme.colors.surface,
            shape = CircleShape,
            elevation = 20.dp,

        ) {

            MaterialIcon(
                Icons.Filled.Add,
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 0.0.dp),
                contentDescription = "add icon",
            )
        }
        Spacer(Modifier.height(30.dp))
    }
    Box(
        modifier = Modifier
            .shadow(elevation = 25.dp, shape = RectangleShape, clip = false)
            .sizeIn(220.dp, 50.dp, 220.dp, 50.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    0.0f to Color.Black,
                    0.8f to Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(520f, 0f),
                )
            )
    ) {
        Text("0.10 USD", modifier = Modifier
            .align(Alignment.Center),
            //fontSize = 10.dp,
            // TODO fix fontsize does not work, Why?
        )
    }

    Row{ Spacer(Modifier.height(50.dp)) }// Blank row to adjust

}



@Composable
private fun FromToCurrenciesMiddlebis(){
    var fromTextState by remember { mutableStateOf("From") }
    var toTextState by remember { mutableStateOf("To") }
    BasicTextField(value = fromTextState, onValueChange = {
        fromTextState = it
    })
    BasicTextField(value = toTextState, onValueChange = {
        toTextState = it
    })

    Text("USD")
}

@Composable
private fun GoButtonBottom(onClickGo: () -> Unit) {
    Surface(
        modifier = Modifier
            .sizeIn(60.dp, 60.dp, 60.dp, 60.dp)
            .aspectRatio(1f),
        //.padding(horizontal = 1.dp),
        color = MaterialTheme.colors.secondaryVariant,
        shape = CircleShape,
        //elevation = 20.dp,
    ) {
        IconButton(
            onClick = onClickGo,
        ) {
            MaterialIcon(Icons.Filled.Check,
                "check icon",Modifier.size(35.dp))
        }
    }
    Row{ Spacer(Modifier.height(10.dp)) }// Blank row to adjust

}