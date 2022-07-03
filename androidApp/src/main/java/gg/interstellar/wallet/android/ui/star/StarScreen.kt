package gg.interstellar.wallet.android.ui.star


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.data.Currency
import gg.interstellar.wallet.android.data.StarButtonBox
import gg.interstellar.wallet.android.data.UserData
import gg.interstellar.wallet.android.data.UserData.getBoxName
import gg.interstellar.wallet.android.ui.CircleImage
import gg.interstellar.wallet.android.ui.DisplayInterstellar
import gg.interstellar.wallet.android.ui.RoundedLabel
import gg.interstellar.wallet.android.ui.theme.MagentaCustom
import gg.interstellar.wallet.android.ui.theme.PurpleCustom
import kotlinx.coroutines.processNextEventInCurrentThread


@Composable
fun StarScreen(
            onSendClick:()->Unit,
            onMarketClick:()->Unit,
            onPortfolioClick:()->Unit,
) {
    Column(
         horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        DisplayInterstellar()
        Spacer(Modifier.height(20.dp))

        Box {
            CircleImage(string = "nash", width = 160.dp, height = 160.dp)
            RoundedLabel(
                modifier = Modifier
                    .align(Alignment.BottomCenter), label = "NASH"
            )
        }
        Spacer(Modifier.height(30.dp))

        Column {
            val menu = UserData.menu
            BoxArrangement(modifier=Modifier,menu,//TODO make it cleaner with (String)->Unit
                                    onSendClick,
                                    onMarketClick,
                                    onPortfolioClick)
            Spacer(modifier =Modifier.height(20.dp))
        }

    }
}

@Composable
private fun  BoxArrangement(
    modifier:Modifier,
    buttons: List<StarButtonBox>,
    onSendClick:()->Unit,
    onMarketClick:()->Unit,
    onPortfolioClick:()->Unit,
) {
    LazyColumn(modifier = modifier) {
        //item {Spacer(Modifier.height(16.dp)) }
        items( buttons.chunked(2),) { rowItems ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(90.dp, 5.dp),
            ) {
                for (button in rowItems) {
                    GenericBoxButton(
                        modifier = modifier.weight(button.weight),
                        name = button.name,
                        colorStart = button.colorStart,
                        colorEnd = button.colorEnd,
                        onClickButton = ManageOnClick(
                            button.name,
                            onSendClick,
                            onMarketClick,
                            onPortfolioClick
                        )
                    )
                    Spacer(modifier =Modifier.width(10.dp))
                    if(button.weight == 1f) break
                }
            }
        }
    }
}

private fun ManageOnClick(
    name:String,
    onSendClick:()->Unit,
    onMarketClick:()->Unit,
    onPortfolioClick:()->Unit,
): () -> Unit {
    when (name) {
        "Send" -> return onSendClick
        "Market" -> return onMarketClick
        "Portfolio" -> return onPortfolioClick
        else -> return {}
    }
}


@Composable
private fun GenericBoxButton(
    modifier: Modifier = Modifier,
    name: String,
    colorStart: Color,
    colorEnd: Color,
    onClickButton: () -> Unit
) {
    val gradient = Brush.linearGradient(
        0.2f to colorStart,
        1f to colorEnd,
        start = Offset(0f, 00f),
        end = Offset(310f, 310f),
    )
    //Box {
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
                modifier = modifier
                    .sizeIn(80.dp, 80.dp, 80.dp, 80.dp),
                //.padding(5.dp),
                shape = RoundedCornerShape(10.dp),
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
   // }
}
@Composable
fun button(onClick: (String) -> Unit, modifier: Modifier, elevation: ButtonElevation, colors: ButtonColors, contentPadding: PaddingValues, content: RowScope.() -> Unit) {



}

