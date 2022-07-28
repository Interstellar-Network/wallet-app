package gg.interstellar.wallet.android.ui

import android.util.Log
import gg.interstellar.wallet.android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import gg.interstellar.wallet.RustWrapper
import gg.interstellar.wallet.WGPUSurfaceView
import gg.interstellar.wallet.android.ui.components.DisplayInterstellar
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme
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
    // Will store the computed WGPUSurfaceView's coordinates
    // updated by AndroidView onGloballyPositioned
    // It is needed to compute the BBox for pinpadRectsRelativeToWGPUSurfaceView/messageRectRelativeToWGPUSurfaceView relative
    // to their parent WGPUSurfaceView
    // MUST use a ref that is "passed around" the whole TxPinpadScreen
    // using eg "LayoutCoordinates? = null" means that even if AndroidView.onGloballyPositioned is indeed called
    // BEFORE SetPadCircle onGloballyPositioned, we get a NPE there...
    // TODO we could probably remove it and look up using parent.parent... recursively until we find the WGPUSurfaceView
    // (note that WGPUSurfaceView and Spacer/SetPadCircle are siblings!)
    val wgpu_sv_pinpad_coordinates: Array<LayoutCoordinates?> = arrayOf(null)

    // Array to store the computed coordinates of each "pinpad circle"
    // updated by SetPadCircle onGloballyPositioned
    // TODO? or a State? https://developer.android.com/jetpack/compose/state
    //var sizeTopBar by remember { mutableStateOf(IntSize.Zero) }
    //var positionInRootTopBar by remember { mutableStateOf(Offset.Zero) }
    val pinpadRectsRelativeToWGPUSurfaceView: Array<Rect> = Array(12) { Rect.Zero }
    // Same idea but for the message
    // NOTE: contrary to pinpad we only have ONE Rect, not an array
    // Array b/c same issue than "wgpu_sv_pinpad_coordinates"
    val messageRectRelativeToWGPUSurfaceView: Array<Rect> = Array(1) { Rect.Zero }

    InterstellarWalletTheme {
        Column {
            DisplayInterstellar()

            // we need to store a ref to colors b/c AndroidView below is not a Composable
            val colors = MaterialTheme.colors

            Box {
                // We have a "native screen" in the background which consists of the "message screen" at the top
                // and the "pinpad screen" at the bottom
                //
                // Only load the Rust wrapper if NOT in Preview
                // else: java.lang.UnsatisfiedLinkError: no shared_substrate_client in java.library.path
                if (!LocalInspectionMode.current) {
                    AndroidView(
                        factory = { ctx ->
                            WGPUSurfaceView(
                                context = ctx,
                                pinpadRectsRelativeToWGPUSurfaceView,
                                messageRectRelativeToWGPUSurfaceView,
                                colors
                            )
                        },
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            wgpu_sv_pinpad_coordinates[0] = coordinates
                        }
                    )
                }

                // And on top of the Rust "native screen" we draw the UI(ie the slider for the confirmation)
                Column {
                    // TODO add onGloballyPositioned, and same as PinpadBottomScreen
                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight(0.25f)
                            // IMPORTANT: without "fillMaxWidth" in onGloballyPositioned: localBoundingBoxOf will return Rect.Zero
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // TODO? positionInWindow,boundsInParent,boundsInWindow,boundsInRoot,size?
                                // NOTE: use wgpu_sv_pinpad_coordinates?. to make sure the Preview does not get an NPE
                                messageRectRelativeToWGPUSurfaceView[0] =
                                    wgpu_sv_pinpad_coordinates[0]?.localBoundingBoxOf(coordinates)!!
                            })

                    ConfirmMessageMiddleScreen()

                    PinpadBottomScreen(
                        pinpadRectsRelativeToWGPUSurfaceView,
                        wgpu_sv_pinpad_coordinates
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmMessageMiddleScreen() {
    Row(
        horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
    )
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
                            stringResource(R.string.three_point_redacted),
                            textAlign = TextAlign.Center,
                            fontSize = 8.sp,
                            color =  if (MaterialTheme.colors.isLight) Color.White
                            else Color.Black,
                            modifier = Modifier
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
private fun PinpadBottomScreen(
    arrayRectFinalCoords: Array<Rect>,
    wgpu_sv_pinpad_coordinates: Array<LayoutCoordinates?>
) {
    // We MUST set "weight" on each children, that weight each row will have the same height
    Box {
        // Same level as the Column, so it will be drawn ON TOP of it
        // TODO? NOTE: order matters! also TODO? setZOrderOnTop?

        Column()
        {
            Spacer(Modifier.weight(0.5f))

            StandardPinpadRow(
                Modifier.weight(4f),
                0,
                1,
                2,
                arrayRectFinalCoords,
                wgpu_sv_pinpad_coordinates
            )
            Spacer(Modifier.weight(1f))
            StandardPinpadRow(
                Modifier.weight(4f),
                3,
                4,
                5,
                arrayRectFinalCoords,
                wgpu_sv_pinpad_coordinates
            )
            Spacer(Modifier.weight(1f))
            StandardPinpadRow(
                Modifier.weight(4f),
                6,
                7,
                8,
                arrayRectFinalCoords,
                wgpu_sv_pinpad_coordinates
            )
            Spacer(Modifier.weight(1f))
            StandardPinpadRow(
                Modifier.weight(4f),
                9,
                10,
                11,
                arrayRectFinalCoords,
                wgpu_sv_pinpad_coordinates
            )

            Spacer(Modifier.weight(2f))
        }
    }
}

/**
 * Standard Pinpad row, with 3 circles
 */
@Composable
private fun ColumnScope.StandardPinpadRow(
    modifier: Modifier,
    id_left: Int,
    id_middle: Int,
    id_right: Int,
    arrayRectFinalCoords: Array<Rect>,
    wgpu_sv_pinpad_coordinates: Array<LayoutCoordinates?>
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        Spacer(Modifier.weight(4f))

        SetPadCircle(
            Modifier.weight(4f),
            id_left,
            arrayRectFinalCoords,
            wgpu_sv_pinpad_coordinates
        )
        Spacer(Modifier.weight(1f))
        SetPadCircle(
            Modifier.weight(4f),
            id_middle,
            arrayRectFinalCoords,
            wgpu_sv_pinpad_coordinates
        )
        Spacer(Modifier.weight(1f))
        SetPadCircle(
            Modifier.weight(4f),
            id_right,
            arrayRectFinalCoords,
            wgpu_sv_pinpad_coordinates
        )

        Spacer(Modifier.weight(4f))
    }
}

/**
 * @param forceDraw: usually in PROD we DO NOT draw the Circle in Java/Kotlin; all is done in Rust(OpenGL/Vulkan)
 *  But it can be useful to forceDraw the UI circles for layout purposes.
 *  NOTE: the POSITIONS of the circles is calculated by Compose, and then passed to Rust.
 *  If false(=default): it will draw them in PREVIEW mode, but not in the app.
 */
@Composable
fun SetPadCircle(
    modifier: Modifier,
    id: Int,
    arrayRectFinalCoords: Array<Rect>,
    wgpu_sv_pinpad_coordinates: Array<LayoutCoordinates?>
) {
    fun isClearButton(): Boolean {
        return id == 11
    }

    // IMPORTANT make the component invisible(still above the Rust OpenGL)
    // (but still draw in Preview mode for easier dev/debug)
    // TODO ideally it would be better to have the button GONE when not in preview, to avoid compositing
    // but for now it does not seem to be possible
    // Tried to replace the Surface with a Space when not in preview, but onGloballyPositioned returned different values
    val alpha = if (LocalInspectionMode.current) {
        1.0f
    } else {
        // when NOT in preview: we SHOULD draw CANCEL button
        // [they are NOT drawn by the Rust side]
        // TODO GO button? id != 9
        if (!isClearButton()) {
            0.0f
        } else {
            1.0f
        }
    }

    // Avoid nesting, etc; keep it simple
    Surface(
        modifier = modifier
            .aspectRatio(1f)  // needed else "squished"(ie ellipses not circles)
            .alpha(alpha)
            .onGloballyPositioned { coordinates ->
                // TODO? positionInWindow,boundsInParent,boundsInWindow,boundsInRoot,size?
                arrayRectFinalCoords[id] =
                    wgpu_sv_pinpad_coordinates[0]!!.localBoundingBoxOf(coordinates)
            }
            .scale(
                if (!isClearButton()) {
                    1.0f
                } else {
                    0.33f
                }
            ),
        // TODO?: .shadow(elevation = 35.dp, /* shape = CircleShape */) are NOT supported on Rust side, so no point in having it enabled
        shape = if (!isClearButton()) {CircleShape} else {RoundedCornerShape(20)},
        elevation = 35.dp,
        color = MaterialTheme.colors.surface,
    ) {
        if (isClearButton()) {
            MaterialIcon(
                Icons.Filled.Clear,
                contentDescription = "clear",
            )
        }

        // TODO ok button if id == 11? But not really needed b/c we know how many inputs we want?
    }
}






























