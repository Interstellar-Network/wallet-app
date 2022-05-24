package gg.interstellar.wallet.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import gg.interstellar.wallet.WGPUSurfaceView
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme
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
@Preview(showBackground = true)
@Composable
fun TxPinpadScreen() {
    InterstellarWalletTheme {
        Column {
            DisplayInterstellar()
            MessageTopScreen()

            ConfirmMessageMiddleScreen()

            PinpadBottomScreen()
        }
    }
}

@Composable
private fun MessageTopScreen() {
    Row(
        horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f)
    ) {
        AndroidView(
            factory = { ctx ->
                WGPUSurfaceView(context = ctx)
            }
        )
    }
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
                    .sizeIn(200.dp, 50.dp, 300.dp, 80.dp)
                    .padding(15.dp),
                shape = CircleShape,
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
                )
                {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier
                                .sizeIn(200.dp, 99.dp, 300.dp, 90.dp)
                                .padding(horizontal = 6.dp)
                                .padding(vertical = 6.dp),

                            color = MaterialTheme.colors.secondaryVariant,
                            shape = CircleShape,
                        ) {
                            Text(
                                "Confirm Transaction",
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .wrapContentHeight(Alignment.CenterVertically)
                            )
                        }
                        Text(
                            " ... ",
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            modifier = Modifier

                            //TODO find right alignement use icon?
                        )

                        Spacer(Modifier.width(7.dp))
                        Surface(
                            modifier = Modifier
                                .sizeIn(30.dp, 30.dp, 40.dp, 40.dp)
                                .aspectRatio(1f),
                            color = MaterialTheme.colors.secondaryVariant,
                            shape = CircleShape,
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
    // We MUST set "weight" on each children, that weight each row will have the same height
    Column()
    {
        Row { Spacer(Modifier.height(20.dp)) }

        StandardPinpadRow()
        StandardPinpadRow()
        StandardPinpadRow()

        Row(
            horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
        ) {
            // In this case we must set the wight b/c we have different children contrary to the
            // other rows. TODO? is there a better way to do this?
            Spacer(Modifier.weight(0.400f))

            SetPadCircle()

            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(0.20f)
                    .wrapContentSize()
                    .padding(horizontal = 0.dp, vertical = 0.dp),
            ) {
                Surface(
                    modifier = Modifier.padding(25.dp, 25.dp),
                    shape = RoundedCornerShape(25),
                    elevation = 28.dp,
                    color = if (MaterialTheme.colors.isLight) Color.Black
                    else Color.White,
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
                    }

                }

            }
            Spacer(Modifier.weight(0.20f))
        }

        Row { Spacer(Modifier.height(20.dp)) }
    }
}

/**
 * Standard Pinpad row, with 3 circles
 */
@Composable
private fun ColumnScope.StandardPinpadRow() {
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
}

@Composable
fun SetPadCircle() {
    Box(
        modifier = Modifier
            .shadow(elevation = 35.dp, shape = CircleShape, clip = false)

    ) {
        Surface(
            modifier = Modifier
                .sizeIn(80.dp, 80.dp, 90.dp, 90.dp)
                .aspectRatio(1f)
                .padding(9.dp),
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































