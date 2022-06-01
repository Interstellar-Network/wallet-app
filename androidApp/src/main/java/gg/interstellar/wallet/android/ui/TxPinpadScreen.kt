package gg.interstellar.wallet.android.ui

//import androidx.compose.foundation.ExperimentalFoundationApi
// import com.google.android.material.elevation

import android.R.attr.fontFamily
import android.graphics.drawable.Icon
import android.text.style.TextAppearanceSpan
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.Greeting
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.theme.Modernista



@Preview //(showBackground = true)
@Preview(name = "NEXUS_7", device = Devices.NEXUS_7)
@Preview(name = "NEXUS_7_2013", device = Devices.NEXUS_7_2013)
@Preview(name = "NEXUS_5", device = Devices.NEXUS_5)
@Preview(name = "NEXUS_6", device = Devices.NEXUS_6)
@Preview(name = "NEXUS_9", device = Devices.NEXUS_9)
@Preview(name = "NEXUS_10", device = Devices.NEXUS_10)
@Preview(name = "NEXUS_5X", device = Devices.NEXUS_5X)
@Preview(name = "NEXUS_6P", device = Devices.NEXUS_6P)
@Preview(name = "PIXEL_C", device = Devices.PIXEL_C)
@Preview(name = "PIXEL", device = Devices.PIXEL)
@Preview(name = "PIXEL_XL", device = Devices.PIXEL_XL)
@Preview(name = "PIXEL_2", device = Devices.PIXEL_2)
@Preview(name = "PIXEL_2_XL", device = Devices.PIXEL_2_XL)
@Preview(name = "PIXEL_3", device = Devices.PIXEL_3)
@Preview(name = "PIXEL_3_XL", device = Devices.PIXEL_3_XL)
@Preview(name = "PIXEL_3A", device = Devices.PIXEL_3A)
@Preview(name = "PIXEL_3A_XL", device = Devices.PIXEL_3A_XL)
@Preview(name = "PIXEL_4", device = Devices.PIXEL_4)
@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL)
@Preview(name = "AUTOMOTIVE_1024p", device = Devices.AUTOMOTIVE_1024p)

@Composable
fun TxPinpadScreen() {

    //val greeting = Greeting().greeting()
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
            elevation = 14.dp,
            color = MaterialTheme.colors.secondary,

            //TO DO change to gradient
           //color = Brush.horizontalGradient(
            //colors = listOf(
                   //MaterialTheme.colors.primary,
                   //MaterialTheme.colors.secondary
                //)
            //)

            ) {
            Box(
                modifier = Modifier
                    .background( Brush.linearGradient(
                        0.3f to Color(0xFF6633FF),
                        1f to Color( 0xFFFF33FF0),
                        //1.0f to Color.Blue,
                        start = Offset(117.18f, 207.61f),
                        end = Offset(-33.32f, 128.71f)
                        // From original vector svg
                            //startY="117.18",
                            //startX="207.61",
                            //endY="-33.32",
                            //endX="120.71",
                            //type="linear",
                            //offset="0.3",color="#FF6633FF",
                            //offset="1",color="#FFFF33FF"
                            )
                        )
                    // test shadow for box
                    //.shadow(elevation =15.dp, shape= RectangleShape, clip =false)
                    )
             {
            Row(
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

                    color = MaterialTheme.colors.surface,
                    // TO DO change theme with right color value
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = 16.dp,
                ) {
                    Text(
                        "Confirm Transaction",
                        textAlign = TextAlign.Center,
                        fontFamily = Modernista, fontWeight = FontWeight.Normal,
                        //style = MaterialTheme.typography.body1,
                        fontSize = 18.sp,
                        color = if (MaterialTheme.colors.isLight)  Color.White
                        else Color.Black,
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
                    Text(
                    "...  ",
                    textAlign = TextAlign.Center,
                    fontFamily = Modernista, fontWeight = FontWeight.Normal,
                    //style = MaterialTheme.typography.body1,
                    fontSize = 21.sp,
                    color = if (MaterialTheme.colors.isLight)  Color.White
                    else Color.Black,
                    modifier = Modifier
                        //.fillMaxHeight()
                        //.alignmentH()
                       //.wrapContentHeight(Alignment.CenterVertically)
                       //.padding(vertical =4.dp)
                       //.padding(horizontal = 0.dp)
                    //TO DO find right alignement use icon
                    )

                //Spacer(Modifier.width(27.dp))
                Surface(
                    modifier = Modifier
                        //.fillMaxSize()
                        //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
                        .sizeIn(30.dp, 30.dp, 40.dp, 40.dp)
                        .aspectRatio(1f),
                    //.padding(horizontal = 1.dp),
                    color = MaterialTheme.colors.surface,
                    shape = CircleShape,
                    elevation = 12.dp,

                    //color = Color(0x080ff)
                ) {    // TO ADD ICON}
                }
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
                .padding(horizontal = 10.dp, vertical=10.dp)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .weight(0.33f)
                .padding(horizontal = 10.dp, vertical=10.dp)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f)
                    .padding(horizontal = 10.dp, vertical=10.dp)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f)
                    .padding(horizontal = 10.dp, vertical=10.dp)
                //verticalAlignment = Alignment.CenterVertically
            ) {
                // In this case we must set the wight b/c we have different children contrary to the
                // other rows. TODO? is there a better way to do this?
                Spacer(Modifier.weight(0.33f))

                SetPadCircle()
                Icon(
                    painterResource(R.drawable.ic_undo_black_mockup_component_shadow),
                    contentDescription = "pinpad input cancel",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .weight(0.33f),

                )
            }
            Row {
                Spacer(Modifier.height(100.dp)) }
        }
}

@Composable
fun SetPadCircle()  {

    Box(
        modifier = Modifier
            .shadow(elevation = 20.dp, shape = CircleShape, clip = false)

    ) {

        Surface(
            modifier = Modifier
                //Adjust Keypad topology with this values
                //.fillMaxSize()
                    //TO DO check device resolution to increase Circle size
                    //TO DO or use prefered size or something similar
                .sizeIn(70.dp, 70.dp, 70.dp, 70.dp)
                .aspectRatio(1f)
                .padding(10.dp),
                //.padding(10.dp, 10.dp),

            shape = CircleShape,
            elevation = 28.dp,
            color = if (MaterialTheme.colors.isLight) Color.Black
            else Color.White,
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                shape = CircleShape,
                color = if (MaterialTheme.colors.isLight) Color.Black
                else Color.White,



                ) { }
        }
    }

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
































