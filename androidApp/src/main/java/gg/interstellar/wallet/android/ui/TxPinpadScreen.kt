package gg.interstellar.wallet.android.ui

//import androidx.compose.foundation.ExperimentalFoundationApi
// import com.google.android.material.elevation

import android.R.attr.fontFamily
import android.graphics.drawable.Icon
import android.media.Image
import android.text.style.TextAppearanceSpan
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.Greeting
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme
import gg.interstellar.wallet.android.ui.theme.MagentaCustom
import gg.interstellar.wallet.android.ui.theme.Modernista
import androidx.compose.material.Icon as MaterialIcon
/*
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
*/
@Preview (showBackground = true)
@Composable
fun TxPinpadScreen() {
    InterstellarWalletTheme(
        //darkTheme = true

    ) {
        //val greeting = Greeting().greeting()
        Column {
            //TestGradient()
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
}



@Composable
fun TestGradient(){
    Box(
    modifier = Modifier
        .sizeIn(200.dp, 50.dp, 300.dp, 80.dp)
        .background(
            Brush.linearGradient(
                0.3f to MaterialTheme.colors.primary,
                1.0f to MaterialTheme.colors.secondary,
                start = Offset(420f, 90f),
                end = Offset(70f, 0f)
            )
        )
    ) {}

}

@Composable
private fun MessageTopScreen() {
    Row( horizontalArrangement = Arrangement.Center,   modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.25f)) {
        // TODO proper OpenGL render using lib_eval?


        //TEST Modernista
        Text(
            text = " 0.5 ETH \n TO\n SATOSHI",
            textAlign = TextAlign.Center,
            fontFamily = Modernista, fontWeight = FontWeight.Normal,
            //style = MaterialTheme.typography.body1,
            fontSize = 45.sp,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically),
            //inlineContent = inlineContent

        )
    }
    Row{ Spacer(Modifier.height(10.dp)) }// Blank row before confirm to adjust

}


@Composable
private fun ConfirmMessageMiddleScreen() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth())
    {

        Box(
            modifier = Modifier
                .shadow(elevation = 50.dp, shape = CircleShape, clip = false)

        ) {
            Surface(

                modifier = Modifier
                    //.fillMaxSize()
                    //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
                    .sizeIn(200.dp, 50.dp, 300.dp, 80.dp)
                    //.aspectRatio(1f)
                    .padding(15.dp),


                //contentColor = Colors.White,
                shape = CircleShape,
                //elevation = 30.dp,
                //color = MaterialTheme.colors.secondary,
                //color = MaterialTheme.colors.surface,


                ) {
                Box(
                    modifier = Modifier
                        //TODO optimize gradient
                        .background(
                            Brush.linearGradient(
                                0.3f to MaterialTheme.colors.secondary,
                                1f to MaterialTheme.colors.primary,
                                start = Offset(0f, 0f),
                                end = Offset(280f, 280f)
                            )
                        )
                        // test shadow for box/strange behavior/same as circle pad/but working
                        // combination of both shadow and elevation to get expected shadow???
                        //.shadow(elevation = 30.dp, shape = RectangleShape, clip = true)
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
                                .padding(horizontal = 6.dp)
                                .padding(vertical = 6.dp),

                            color = MaterialTheme.colors.secondaryVariant,
                            // TO DO change theme with right color value
                            //contentColor = Color.White,
                            shape = CircleShape,
                            //elevation = 20.dp,
                        ) {
                            Text(
                                "Confirm Transaction",
                                textAlign = TextAlign.Center,
                                //fontFamily = Modernista, fontWeight = FontWeight.Normal,
                                //style = MaterialTheme.typography.body1,
                                fontSize = 16.sp,
                                //color = if (MaterialTheme.colors.isLight) Color.White
                                //else Color.Black,
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
                            " ... ",
                            textAlign = TextAlign.Center,
                            //fontFamily = Modernista, fontWeight = FontWeight.Normal,
                            //style = MaterialTheme.typography.body1,
                            fontSize = 15.sp,
                            //color = if (MaterialTheme.colors.isLight) Color.White
                            //else Color.Black,
                            modifier = Modifier

                            //TODO find right alignement use icon?
                        )

                        Spacer(Modifier.width(7.dp))
                        Surface(
                            modifier = Modifier
                                //.fillMaxSize()
                                //.requiredWidthIn(min = 344.4.dp).requiredHeightIn(99.84.dp)
                                .sizeIn(30.dp, 30.dp, 40.dp, 40.dp)
                                .aspectRatio(1f),
                            //.padding(horizontal = 1.dp),
                            color = MaterialTheme.colors.secondaryVariant,
                            shape = CircleShape,
                            //elevation = 20.dp,
                            //TODO fix difference of color surface in darkmode???
                            //TODO does it come from elevation/shadow surface? yes
                            //color = Color(0x0080ff) ->0069d2 ???
                        ) {

                            MaterialIcon(
                                Icons.Filled.Check,
                                modifier = Modifier
                                    .padding(3.dp),
                                contentDescription = "check icon",
                            )
                        }
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
Column()
    {
            Row { Spacer(Modifier.height(20.dp)) }
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                SetPadCircle()
                SetPadCircle()
                SetPadCircle()
            }
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                //.padding(horizontal = 10.dp, vertical=10.dp)
                //verticalAlignment = Alignment.CenterVertically
            ) {
                // In this case we must set the wight b/c we have different children contrary to the
                // other rows. TODO? is there a better way to do this?
                Spacer(Modifier.weight(0.400f))

                SetPadCircle()

                BoxWithConstraints(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        //.shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
                        .weight(0.20f)
                        .wrapContentSize()
                        //.requiredSize(30.dp,30.dp)
                        //.sizeIn(30.dp, 30.dp, 30.dp, 30.dp),

                        .padding(horizontal = 0.dp, vertical = 0.dp),
                    //propagateMinConstraints = true,
                ) {
                    Surface(
                        modifier = Modifier.padding(25.dp, 25.dp),
                        shape = RoundedCornerShape(25),
                        elevation = 28.dp,
                        color = if (MaterialTheme.colors.isLight) Color.Black
                        else Color.White,
                    ) {
                        MaterialIcon(
                            Icons.Filled.Check,
                            modifier = Modifier
                                .padding(horizontal = 0.dp, vertical = 0.0.dp),
                            contentDescription = "Check icon",
                        )
                        /*
                        Icon(
                            painterResource(R.drawable.ic_close_fill0_wght400_grad0_opsz48),
                            contentDescription = "reset/close",
                            tint = if (MaterialTheme.colors.isLight) Color.White
                            else Color.Black,
                            modifier = Modifier

                            //.padding(horizontal=0.dp, vertical = 10.dp),
                        )*/
                    }

                }

                Spacer(Modifier.weight(0.20f))
            }
            Row { Spacer(Modifier.height(20.dp)) }


        }
}

@Composable
fun SetPadCircle() {

    Box(
            modifier = Modifier
                .shadow(elevation = 35.dp, shape = CircleShape, clip = false)

        ) {

            Surface(
                modifier = Modifier
                    //Adjust Keypad topology with this values
                    //.fillMaxSize()
                    //TO DO check device resolution to increase Circle size
                    //TO DO or use prefered size or something similar
                    .sizeIn(80.dp, 80.dp, 90.dp, 90.dp)
                    .aspectRatio(1f)
                    .padding(9.dp),
                //.padding(10.dp, 10.dp),

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
































