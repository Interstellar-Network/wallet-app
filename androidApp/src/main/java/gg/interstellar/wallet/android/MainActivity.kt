package gg.interstellar.wallet.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme
import gg.interstellar.wallet.Greeting

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterstellarWalletTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    InterstellarTxScreen()
                }
            }
        }
    }
}

@Composable
fun InterstellarTxScreen() {
    val greeting = Greeting().greeting()
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
fun MessageTopScreen() {
    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.33f)) {
        // TODO proper OpenGL render using lib_eval?
        Image(bitmap = ImageBitmap(300, 200), contentDescription = "message screen")
    }
}

@Composable
fun ConfirmMessageMiddleScreen() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Icon(painterResource(R.drawable.ic_confirm_transaction_black_mockup_component_shadow),
            contentDescription = "confirm transaction",
            tint = Color.Unspecified)
    }
}

@Composable
fun PinpadBottomScreen() {
    Column {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input top left",
                tint = Color.Unspecified)
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input top middle",
                tint = Color.Unspecified)
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input top right",
                tint = Color.Unspecified)
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input middle left",
                tint = Color.Unspecified)
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input middle middle",
                tint = Color.Unspecified)
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input middle right",
                tint = Color.Unspecified)
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input bottom left",
                tint = Color.Unspecified)
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input bottom middle",
                tint = Color.Unspecified)
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input bottom right",
                tint = Color.Unspecified)
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.fillMaxSize(0.33f))
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input bottom bottom middle",
                tint = Color.Unspecified)
            Icon(painterResource(R.drawable.ic_circle_black_background_shadow),
                contentDescription = "pinpad input cancel",
                tint = Color.Unspecified)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    InterstellarWalletTheme {
        InterstellarTxScreen()
    }
}