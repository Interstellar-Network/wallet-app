package gg.interstellar.wallet.android.ui

//import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Canvas
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
// import com.google.android.material.elevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import gg.interstellar.wallet.Greeting
import gg.interstellar.wallet.android.R



@Preview
@Composable
fun TxPinpadScreen() {

    val greeting = Greeting().greeting()
    Column {



        DisplayLogo()
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
fun DisplayLogo() {

    //Row(modifier = Modifier
      //  .fillMaxWidth()
      //  .fillMaxHeight(0.33f)) {




}



@Composable
fun SetPadCircle() {

        Surface(
            modifier = Modifier
                    //Adjust Keypad topology with this values
                .sizeIn(90.dp, 140.dp, 90.dp, 140.dp)
                .aspectRatio(1f)
                .padding(5.dp),

            shape = CircleShape,
            elevation = 25.dp,

        ) { Surface(
            modifier = Modifier
                .fillMaxSize(),

            //TO DO  if Theme Dark or Light Theme -> Black or White
            color = Color.Black
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


@Composable
private fun MessageTopScreen() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.33f)) {
        // TODO proper OpenGL render using lib_eval?
        Text("0.06 ETH to \"SATOSHI\"", textAlign = TextAlign.Center,
            fontSize = 50.sp,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically))
    }
}

@Composable
private fun ConfirmMessageMiddleScreen() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Icon(painterResource(R.drawable.ic_confirm_transaction_no_text_black_mockup_component_shadow),
            contentDescription = "confirm transaction",
            tint = Color.Unspecified)
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
    Column {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.15f)
        )
        {

            SetPadCircle()
            SetPadCircle()
            SetPadCircle()


        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.15f)
        )
        {

            SetPadCircle()
            SetPadCircle()
            SetPadCircle()

        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.15f)
        )
        {
            SetPadCircle()
            SetPadCircle()
            SetPadCircle()
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.15f),
            verticalAlignment = Alignment.CenterVertically) {
            // In this case we must set the wight b/c we have different children contrary to the
            // other rows. TODO? is there a better way to do this?
            Spacer(Modifier.weight(0.33f))

            SetPadCircle()


            Icon(
                painterResource(R.drawable.ic_undo_black_mockup_component_shadow),
                contentDescription = "pinpad input cancel",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
        }
    }
}
















@Composable
private fun PinpadBottomScreenBis() {
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
    Column {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.25f)) {

                Icon (
                    painterResource(R.drawable.ic_circle_black_background_shadow),
                    contentDescription = "pinpad input top left",
                    tint = Color.Unspecified,
                    modifier = Modifier.weight(0.33f),
                   // elevation = "2dp"
                )

            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input top middle",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input top right",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.25f)) {


            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input middle left",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f),

            )
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input middle middle",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input middle right",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.25f)) {
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input bottom left",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input bottom middle",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input bottom right",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(0.25f),
            verticalAlignment = Alignment.CenterVertically) {
            // In this case we must set the wight b/c we have different children contrary to the
            // other rows. TODO? is there a better way to do this?
            Spacer(Modifier.weight(0.33f))
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input bottom bottom middle",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
            Icon(
                painterResource(R.drawable.ic_undo_black_mockup_component_shadow),
                contentDescription = "pinpad input cancel",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
            )
        }
    }
}















