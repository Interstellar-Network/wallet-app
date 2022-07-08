package gg.interstellar.wallet.android.ui.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import gg.interstellar.wallet.android.data.StarButtonBox
import gg.interstellar.wallet.android.data.UserData
import gg.interstellar.wallet.android.ui.components.CircleImage
import gg.interstellar.wallet.android.ui.components.DisplayInterstellar
import gg.interstellar.wallet.android.ui.components.RoundedLabel


@Composable
fun ProfileScreen(
            onSendClick:()->Unit,
            onMarketClick:()->Unit,
            onPortfolioClick:()->Unit,
) {
    Column(
         horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))
        DisplayInterstellar()
        Spacer(Modifier.height(35.dp))

        Box {
            CircleImage(string = "nash", width = 180.dp, height = 180.dp)
            RoundedLabel(
                modifier = Modifier
                    .align(Alignment.BottomCenter), label = " NASH "
            )
        }
        Spacer(Modifier.height(38.dp))

        Column {
            val menu = UserData.menu
            BoxArrangement(modifier=Modifier,menu,//TODO make it cleaner with (String)->Unit
                                    onSendClick,
                                    onMarketClick,
                                    onPortfolioClick)
            Spacer(modifier =Modifier.height(10.dp))
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
                modifier = Modifier.padding(84.dp, 5.dp),
            ) {
                Spacer(modifier =Modifier.width(10.dp))
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
                    .sizeIn(130.dp, 80.dp, 130.dp, 80.dp),
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
                        fontSize = 14.sp,
                    )
                }
            }
        }
   // }
}
@Composable
fun button(onClick: (String) -> Unit, modifier: Modifier, elevation: ButtonElevation, colors: ButtonColors, contentPadding: PaddingValues, content: RowScope.() -> Unit) {



}

