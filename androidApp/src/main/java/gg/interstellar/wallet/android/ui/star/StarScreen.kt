package gg.interstellar.wallet.android.ui.star


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.ui.CircleImage
import gg.interstellar.wallet.android.ui.DisplayInterstellar
import gg.interstellar.wallet.android.ui.RoundedLabel


@Composable
fun StarScreen(
            onSendClick:()->Unit,
            onMarketClick:()->Unit,
            onPortfolioClick:()->Unit,
            onNullClick:()->Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        DisplayInterstellar()
        Spacer(Modifier.height(20.dp))

        Box {
            CircleImage(string = "nash", width = 160.dp, height = 160.dp)
            RoundedLabel(modifier = Modifier
                .align(Alignment.BottomCenter)
                , label = "NASH")
        }

        Spacer(Modifier.height(30.dp))

        Column {

            Row {

                SendBox(onSendClick)
                ReceiveBox(onNullClick)
            }
            Row {
                PortfolioBox(onPortfolioClick)
            }
            Row {
                MarketBox(onMarketClick)
                NFTsBox(onNullClick)
            }
            Row {
                SwapBox(onNullClick)
                BuyBox(onNullClick)
            }
        }
    }
}
@Composable
private fun GenericRow(modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth(),

        
    ) {


    }

}



@Composable
private fun GenericBoxButton(
    modifier:Modifier =Modifier,
    name:String,
    colorStart:Color,
    colorEnd:Color,
    onClickButton:()->Unit
) {
    val gradient = Brush.linearGradient(
        0.4f to colorStart,
        1f to colorEnd,
        start = Offset(0f, 0f),
        end = Offset(150f, 0f),
    )
    Box {
        Button(
            onClick = onClickButton,
            modifier,
            elevation = ButtonDefaults.elevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 20.dp,
                hoveredElevation = 30.dp,
                focusedElevation = 50.dp
            ),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
            contentPadding = PaddingValues(),

            ) {
            Surface(
                modifier =modifier
                    .sizeIn(80.dp,80.dp, 120.dp,120.dp),
                    //.padding(5.dp),
                shape =  RoundedCornerShape(10.dp),
            ) {

            Box(
                modifier = Modifier
                    .background(gradient)
                    .then(modifier),
                contentAlignment = Alignment.Center,

            ) {
                    Text(
                        name,
                        color = MaterialTheme.colors.onSurface,
                        modifier = modifier
                            .align(Alignment.Center),
                        fontSize = 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun SendBox(
    onClickButton:()->Unit) {
    Box {
        GenericBoxButton(
            modifier = Modifier,
            "Send",
            colorStart = MaterialTheme.colors.secondary,
            colorEnd = MaterialTheme.colors.primary,
            onClickButton
        )
    }
}
@Composable
private fun ReceiveBox(
    onClickButton:()->Unit) {
    Box {
        GenericBoxButton(
            modifier = Modifier,
            "Receive",
            colorStart = MaterialTheme.colors.secondary,
            colorEnd = MaterialTheme.colors.primary,
            onClickButton
        )
    }
}

@Composable
private fun PortfolioBox(
    onClickButton:()->Unit) {
    Box {
        GenericBoxButton(
            modifier = Modifier,
            "Portfolio",
            colorStart = MaterialTheme.colors.secondary,
            colorEnd = MaterialTheme.colors.primary,
            onClickButton
        )
    }
}

@Composable
private fun MarketBox(
    onClickButton:()->Unit) {
    Box {
        GenericBoxButton(
            modifier = Modifier,
            "Market",
            colorStart = MaterialTheme.colors.secondary,
            colorEnd = MaterialTheme.colors.primary,
            onClickButton
        )
    }
}

@Composable
private fun NFTsBox(
    onClickButton:()->Unit) {
    Box {
        GenericBoxButton(
            modifier = Modifier,
            "NFTs",
            colorStart = MaterialTheme.colors.secondary,
            colorEnd = MaterialTheme.colors.primary,
            onClickButton
        )
    }
}

@Composable
private fun SwapBox(
    onClickButton:()->Unit) {
    Box {
        GenericBoxButton(
            modifier = Modifier,
            "Swap",
            colorStart = MaterialTheme.colors.secondary,
            colorEnd = MaterialTheme.colors.primary,
            onClickButton
        )
    }
}

@Composable
private fun BuyBox(
    onClickButton:()->Unit) {
    Box {
        GenericBoxButton(
            modifier = Modifier,
            "Buy",
            colorStart = MaterialTheme.colors.secondary,
            colorEnd = MaterialTheme.colors.primary,
            onClickButton
        )
    }
}

