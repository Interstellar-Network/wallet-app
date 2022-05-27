package gg.interstellar.wallet.android.ui

//import androidx.compose.foundation.ExperimentalFoundationApi
// import com.google.android.material.elevation

import android.R.attr.fontFamily
import android.text.style.TextAppearanceSpan
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.Greeting
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.theme.Modernista


@Preview (showBackground = true)
@Composable
fun TxPinpadScreen() {

    val greeting = Greeting().greeting()
    Column {

        DisplayInterstellar()
        MessageTopScreen()

        ConfirmMessageMiddleScreen()
//        Image(
//            painter = painterResource(R.drawable.profile_picture),
//            contentDescription = "Contact profile picture",
//            modifier = Modifier
//                // Set image size to 40 dp
//                .size(40.dp)
//                // Clip image to be shaped as a circle
//                .clip(CircleShape)
//        )

        PinpadBottomScreen()
    }
}
@Composable
fun DisplayInterstellar() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.08f)
    ){
        Text("INTERSTELLAR",
            textAlign = TextAlign.Center,
            fontFamily = Modernista, fontWeight = FontWeight.Normal,
            //style = MaterialTheme.typography.body1,
            fontSize = 11.sp,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically)
                //.shadow(elevation = 2.dp)
        )
    }

}



@Composable
private fun MessageTopScreen() {
    Row( horizontalArrangement = Arrangement.Center,   modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.33f)) {
        // TODO proper OpenGL render using lib_eval?


        //TEST Modernista
        Text("0.06 ETH\n TO\n \"SATOSHI\"",
            textAlign = TextAlign.Center,
            fontFamily = Modernista, fontWeight = FontWeight.Normal,
            //style = MaterialTheme.typography.body1,
            fontSize = 50.sp,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically))
    }
}


@Composable
private fun ConfirmMessageMiddleScreen() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth())
    {
        Surface(

            modifier = Modifier
                //.fillMaxSize()
                //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
                .sizeIn(200.dp, 50.dp, 300.dp, 80.dp)
                //.aspectRatio(1f)
               .padding(15.dp),


            //contentColor = Colors.White,
            shape = CircleShape,
            elevation = 12.dp,
            color = MaterialTheme.colors.primary,

            //TO DO change to gradient
           //color = Brush.horizontalGradient(
            //colors = listOf(
                   //MaterialTheme.colors.primary,
                   //MaterialTheme.colors.secondary
                //)
            //)

            ) { Row(
                modifier = Modifier
                    .fillMaxSize(),
                    //.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        //.fillMaxSize()
                        .sizeIn(200.dp, 99.dp, 300.dp, 90.dp)
                        //.shadow( 2.dp,RectangleShape,true)
                        .padding(horizontal = 9.dp)
                        .padding(vertical = 6.dp),

                    color =  MaterialTheme.colors.secondary,
                    // TO DO change theme with right color value
                    contentColor =  Color.White,
                    shape = CircleShape,
                    elevation = 12.dp,
                    ) { Text(
                        "Confirm Transaction",
                        textAlign = TextAlign.Center,
                        fontFamily = Modernista, fontWeight = FontWeight.Normal,
                        //style = MaterialTheme.typography.body1,
                        fontSize = 18.sp,
                        //color = Color.White,
                        modifier = Modifier
                            //.fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically)
                            //.wrapContentWidth(Alignement.Start)
                            //.padding(vertical = 5.dp)
                            //.padding(3.dp)
                            //.shadow(elevation = 0.3.dp)
                    )
                   }
                    Spacer(Modifier.width(27.dp))
                    Surface(modifier = Modifier
                            //.fillMaxSize()
                            //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
                            .sizeIn(30.dp, 30.dp, 40.dp, 40.dp)
                            .aspectRatio(1f),
                            //.padding(horizontal = 1.dp),
                        color = MaterialTheme.colors.secondary,
                        shape = CircleShape,
                        elevation = 12.dp,

                        //color = Color(0x080ff)
                    ) {    // TO ADD ICON}
                    }
            }
        }
    }
}





@Composable
private fun PinpadBottomScreen() {
//    val numbers = (0..12).toList()

        // TODO TOREMOVE sort of works, but this is a scrollable layout; which is not what we want
//    LazyVerticalGrid(
//        cells = GridCells.Fixed(3)
//    ) {
//        items(numbers.size) {
//            // standard: draw a "circle" for everything except bottom left and bottom right
//            if(it < 9 || it == 10){
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    // TOREMOVE
////                    Text(text = "Number")
////                    Text(text = "  $it",)
//                    Icon(
//                        painterResource(R.drawable.ic_circle_black_background_shadow),
//                        // TODO correct contentDescription eg "top left"
//                        contentDescription = "pinpad input",
//                        tint = Color.Unspecified
//                    )
//                }
//            }
//            // bottom right: draw "undo"
//            else if(it == 11) {
//                Icon(
//                    painterResource(R.drawable.ic_undo_black_mockup_component_shadow),
//                    contentDescription = "pinpad input cancel",
//                    tint = Color.Unspecified
//                )
//            }
//        }
//    }

        // We MUST set "weight" on each children, that weight each row will have the same height
        Column (
        )
            {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.330f)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // In this case we must set the wight b/c we have different children contrary to the
                // other rows. TODO? is there a better way to do this?
                Spacer(Modifier.weight(0.33f))
                SetPadCircle()
                //TO DO
                Icon(
                    painterResource(R.drawable.ic_undo_black_mockup_component_shadow),
                    contentDescription = "pinpad input cancel",
                    tint = Color.Unspecified,
                    modifier = Modifier.weight(0.33f)
                )
            }
            Row {
                Spacer(Modifier.height(100.dp)) }
        }
}



@Composable
fun SetPadCircle() {

    Surface(
        modifier = Modifier
            //Adjust Keypad topology with this values
            .sizeIn(80.dp, 100.dp, 80.dp, 100.dp)
            .aspectRatio(1f)
            .padding(4.dp),

        shape = CircleShape,
        elevation = 12.dp,

        ) { Surface(
        modifier = Modifier
            .fillMaxSize(),

        color = if (MaterialTheme.colors.isLight)  Color.Black
        else Color.White,



    ) {} }
}

/*
             {
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    //.alpha(10f)
                    .drawBehind {
                        drawCircle(
                            color = Color.DarkGray,
                            //center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                            radius = size.minDimension / 3
                        )
                    }
                    //.shadow(
                    //elevation = 10.dp,
                    //shape = CircleShape,
                    //clip = true
                ) {

                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    drawCircle(
                        color = Color.Black,
                        center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                        radius = size.minDimension / 4
                    )
                }

            }
        }
}

 */
































