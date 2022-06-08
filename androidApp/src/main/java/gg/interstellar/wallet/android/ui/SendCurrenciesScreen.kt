package gg.interstellar.wallet.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme

@Preview (showBackground = true)
@Composable
fun SendCurrenciesScreen(onClickGo: () -> Unit = {},) {

    InterstellarWalletTheme(
        //darkTheme = true

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
                .sizeIn(280.dp, 150.dp, 280.dp, 150.dp)
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
        Box(
        modifier = Modifier
            .shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
            .sizeIn(180.dp, 100.dp, 180.dp, 100.dp)
            .clip(shape)
            //.padding(3.dp)
            .background(
                Brush.linearGradient(
                    0.4f to Color(0xFF627eea),
                    1f to Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(420f, 0f),
                )
            )
        ) {
                Text(
            "O.6 ETH",
                modifier = Modifier
                    .align(Alignment.Center),
                //fontSize = 10.dp,
                // TODO fix fontsize does not work, Why?

                color = if (MaterialTheme.colors.isLight) Color.White
                else Color.Black,

            )
        }
        /*
        Icon(
            painterResource(R.drawable.ic_check_fill0_wght400_grad0_opsz48),
             modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 0.0.dp),
                HorizontalArrangement = TopCenter,
            //.fillMaxSize(),
            contentDescription = "check icon",
            tint = if (MaterialTheme.colors.isLight) Color.White
            else Color.Black,
        )

         */
    }

    Row{ Spacer(Modifier.height(10.dp)) }// Blank row to adjust

}




@Composable
private fun DestinationMiddle(shape: Shape){
    Box(
        modifier = Modifier
            .shadow(elevation = 15.dp, shape = RectangleShape, clip = false)
            .sizeIn(180.dp, 100.dp, 180.dp, 100.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    0.4f to Color(0xFF627eea),
                    1.1f to Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(420f, 0f),
                )
            )
    ) {
        Text("John Doe", modifier = Modifier
            .align(Alignment.Center),
            //fontSize = 10.dp,

            color =if (MaterialTheme.colors.isLight) Color.White
            else Color.Black,

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
            //color = MaterialTheme.colors.surface,
            shape = CircleShape,
            elevation = 20.dp,
            //TODO fix difference of color surface in darkmode???
            //TODO does it come from elevation/shadow surface? yes
            color =  if (MaterialTheme.colors.isLight) Color.Black
            else Color.White,

        ) {
            Icon(
                Icons.Filled.Add,//TODO replace with Vector Icon
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 0.0.dp),

                contentDescription = "add icon",
                tint =if (MaterialTheme.colors.isLight) Color.White
                else Color.Black,
                //TODO with vector asset and painter we could get finer tuning
                //TODO and tint apply only on vector

            )
        }

        Spacer(Modifier.height(30.dp))
    }
    Box(
        modifier = Modifier
            .shadow(elevation = 25.dp, shape = RectangleShape, clip = false)
            .sizeIn(180.dp, 50.dp, 180.dp, 50.dp)
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

            color =if (MaterialTheme.colors.isLight) Color.White
            else Color.Black,



            // TODO fix fontsize does not work, Why?
        )
    }

    Row{ Spacer(Modifier.height(10.dp)) }// Blank row to adjust

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
            //.fillMaxSize()
            //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
            .sizeIn(80.dp, 80.dp, 80.dp, 80.dp)
            .aspectRatio(1f),
        //.padding(horizontal = 1.dp),
        //color = MaterialTheme.colors.surface,
        shape = CircleShape,
        //elevation = 20.dp,
        //TODO fix difference of color surface in darkmode???
        //TODO does it come from elevation/shadow surface? yes
        //color = Color(0x0080ff) ->0069d2 ???
    ) {
        IconButton(
            onClick = onClickGo,
        ) {
            Icon(
                painterResource(R.drawable.ic_check_fill0_wght400_grad0_opsz48),
                modifier = Modifier
                    //.padding(horizontal=0.dp, vertical = 0.0.dp),
                    .fillMaxSize(),
                contentDescription = "check icon",
                tint = if (MaterialTheme.colors.isLight) Color.White
                else Color.Black,

            )

        }
    }
    Row{ Spacer(Modifier.height(10.dp)) }// Blank row to adjust


}