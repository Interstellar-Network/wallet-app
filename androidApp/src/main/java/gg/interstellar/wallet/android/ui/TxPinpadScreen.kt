package gg.interstellar.wallet.android.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.Greeting
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.RustWrapper

@Preview
@Composable
fun TxPinpadScreen() {
    val greeting = Greeting().greeting()
    // TODO DO NOT hardcode IP, or at least make it depends on Emulator vs Device
    val tx_hash = RustWrapper().CallExtrinsic("ws://10.0.2.2:9944")
    Column {
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
private fun MessageTopScreen() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.33f)) {
        // TODO proper OpenGL render using lib_eval?
        Text("0.06 ETH to \"SATOSHI\"", textAlign = TextAlign.Center,
            fontSize = 50.sp,
            modifier = Modifier.fillMaxHeight().wrapContentHeight(Alignment.CenterVertically))
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

@OptIn(ExperimentalFoundationApi::class)
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
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().weight(0.25f)) {
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input top left",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
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

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().weight(0.25f)) {
            Icon(
                painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input middle left",
                tint = Color.Unspecified,
                modifier = Modifier.weight(0.33f)
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

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().weight(0.25f)) {
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

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().weight(0.25f),
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