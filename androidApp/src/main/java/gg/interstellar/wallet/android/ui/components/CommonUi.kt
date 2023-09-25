package gg.interstellar.wallet.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.theme.Modernista
import kotlin.math.abs


@Composable
private fun DisplayInterstellar() {

    val myId = "inlineContent"
    val intLogoText = buildAnnotatedString {
        append("I N T E R S T E L L ")
        // Append a placeholder string "[icon]" and attach an annotation "inlineContent" on it.
        appendInlineContent(myId, "[icon]")
        append(" R")
    }
    val inlineContent = mapOf(
        Pair(
            // This tells the [CoreText] to replace the placeholder string "[icon]" by
            // the composable given in the [InlineTextContent] object.
            myId,
            InlineTextContent(
                // Placeholder tells text layout the expected size and vertical alignment of
                // children composable.
                Placeholder(
                    width = 16.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                // This Icon will fill maximum size, which is specified by the [Placeholder]
                // above. Notice the width and height in [Placeholder] are specified in TextUnit,
                // and are converted into pixel by text layout.
                Icon(
                    painterResource(R.drawable.ic_interstellar_black_logo),
                    contentDescription = "Interstellar logo",
                    tint = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .padding(1.5.dp)
                )
            }
        )
    )
    Row(
        horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.05f)
    ) {
        Text(
            intLogoText,
            textAlign = TextAlign.Center,
            fontFamily = Modernista, fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically)
                .shadow(elevation = 0.dp, clip = true),
            inlineContent = inlineContent //add logo in place holder
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayInterstellarPreview() {
    DisplayInterstellar()
}

@Composable
fun HeaderWithBrand() {
    Spacer(Modifier.height(30.dp))
    DisplayInterstellar()
    Spacer(Modifier.height(10.dp))
}

//TODO make it cleaner
@Composable
fun ScreenTopBox(
    modifier: Modifier,
    tittle: String
) {
    Box(
        modifier.shadow(elevation = 0.dp, shape = CircleShape, clip = false)
    ) {
        Surface(
            modifier = Modifier
                .sizeIn(310.dp, 140.dp, 320.dp, 140.dp)
                .padding(25.dp),
            shape = CircleShape,
            elevation = 20.dp,
        ) {
            Box(
                modifier
                    .background(
                        Brush.linearGradient(
                            0.3f to MaterialTheme.colors.secondary,
                            1f to MaterialTheme.colors.primary,
                            start = Offset(0f, 0f),
                            end = Offset(310f, 310f)
                        )
                    )
            ) {
                Text(
                    tittle,
                    modifier = Modifier
                        .align(Alignment.Center),
                    fontSize = 35.sp,
                )
            }
        }
    }
}

@Composable
fun ScreenTopBoxWithCircleLabel(
    modifier: Modifier,
    title: String,
    number: Float
) {
    Box(
        modifier
            .shadow(elevation = 0.dp, shape = CircleShape, clip = false)
    ) {
        Box(modifier = Modifier.padding(0.dp, 10.dp))
        {
            Surface(
                modifier = Modifier
                    .sizeIn(310.dp, 140.dp, 310.dp, 140.dp)
                    .padding(25.dp),
                shape = CircleShape,
                elevation = 20.dp,
            ) {
                Box(
                    modifier
                        .background(
                            Brush.linearGradient(
                                0.3f to MaterialTheme.colors.secondary,
                                1f to MaterialTheme.colors.primary,
                                start = Offset(0f, 0f),
                                end = Offset(310f, 310f)
                            )
                        )
                ) {
                    Text(
                        title,
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontSize = 35.sp,
                    )
                }
            }
            SmallCoinChangeLabel(
                //TODO use alignment line
                modifier = Modifier
                    //.align(Alignment.TopCenter),
                    .offset(115.dp, 13.dp),
                number,
            )

        }
    }
}


/**
 * A row representing the basic information of a currency
 */

// TO manage spacing between two singles row in SendScreen
val HEIGHT_REF = 100.dp
val PADDING_V = 5.dp
val PADDING_H = 18.dp
val PADDING_CIRCLE = PADDING_V

@Composable
fun SmallCircleRow(
    modifier: Modifier = Modifier,
    drawableId: Int,
    label: String,
    change: Float?,
) {
    Row(
        modifier = modifier
            .height(HEIGHT_REF - 3.5.dp)
        // TODO? re-add?
//            .clearAndSetSemantics {
//                contentDescription =
//                    "$title add other info"
//            }
        ,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Box {
            Box(
                modifier =
                modifier.padding(PADDING_CIRCLE)
                //.padding(PADDING_FOR_SIMPLE_ROW), // enlarge the box to put image,labels on border
            ) {// use box in a box to display text label on border
                CircleImage(
                    modifier = Modifier,
                    drawableId = drawableId, 90.dp, 90.dp
                )

                //Row{Spacer(modifier =Modifier.height(100.dp))} TEST to enlarge
            }

            change?.let { change ->
                SmallCoinChangeLabel(
                    modifier = Modifier.align(Alignment.TopCenter),
                    change,
                )
            }

            RoundedLabel(
                modifier = Modifier.align(Alignment.BottomCenter),
                label
            )

        }
    }
    Spacer(Modifier.height(0.dp))
}

/**
 *
 * @param validatedFloatAmount: when NOT null it be an editable TextInput
 */
@Composable
fun LargeTextOnlyRow(
    color: Color,
    drawableId: Int? = null,
    text: String,
    bottomLabel: String? = null,
    change: Float? = null,
    validatedFloatAmount: MutableState<Float?>?,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = if (onClick != null) (Modifier.clickable(onClick = onClick)) else Modifier,
//        modifier = modifier?.
//            .height(HEIGHT_REF - 3.5.dp)
        // TODO? re-add?
//            .clearAndSetSemantics {
//                contentDescription =
//                    "$title add other info"
//            }
//        ,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Box {
            Box(
//                modifier = modifier.padding(PADDING_H, PADDING_V)
                //.padding(PADDING_FOR_SIMPLE_ROW), // enlarge the box to put image,labels on border
            ) {// use box in a box to display text label on border
                LargeTextWidget(
                    //modifier,
                    placeholder = text,
                    color,
                    validatedFloatAmount = validatedFloatAmount,
                )

                //Row{Spacer(modifier =Modifier.height(100.dp))} TEST to enlarge
            }

            drawableId?.let { drawableId ->
                CircleImage(
                    modifier = Modifier.align(Alignment.CenterEnd), drawableId = drawableId,
                    width = 35.dp, height = 35.dp
                )
            }

            bottomLabel?.let {
                RoundedLabel(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    label = bottomLabel
                )
            }

            change?.let {
                SmallCoinChangeLabel(modifier = Modifier.align(Alignment.TopCenter), number = it)
            }
        }
    }
    Spacer(Modifier.height(15.dp))
}

/**
 * row variant: (almost) full width; no label, no widget etc
 */
@Composable
private fun LargeTextWidget(
    placeholder: String, color: Color,
    validatedFloatAmount: MutableState<Float?>?,
) {
    // TODO? re-add?
//    if ((inputTextView.value =="_") && (string != "select") &&(useInput))
//        inputTextView.value = string
    Box(
        modifier = Modifier
            .shadow(elevation = 20.dp, shape = CircleShape, clip = false)
            .sizeIn(220.dp, 200.dp, 220.dp, 200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    0.4f to color,
                    1f to Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(450f, 450f),
                )
            )
    ) {
        if (validatedFloatAmount != null) {
            // with android keypad
            InputTextNumber(
                placeholder = "",
                validatedFloatAmount = validatedFloatAmount,
            )
        } else {
            Text(
                placeholder,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colors.onSurface,
                style = typography.h5
            )
        }
    }
}

/**
 * OutlinedTextField with keyboard input for a FLOAT
 */
@Composable
fun InputTextNumber(
    placeholder: String,
    validatedFloatAmount: MutableState<Float?>,
) {
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight()
    ) {
        OutlinedTextField(
            value = if (validatedFloatAmount.value != null) {
                validatedFloatAmount.value.toString()
            } else {
                ""
            },
            singleLine = true,
            textStyle = typography.h5.copy(textAlign = TextAlign.Center),
            modifier = Modifier,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.onPrimary,
                disabledTextColor = MaterialTheme.colors.onSurface,
                backgroundColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colors.onPrimary,
                trailingIconColor = MaterialTheme.colors.onPrimary,
            ),
            placeholder = { Text(placeholder) },
            // TODO only allow correctly formatted Floats!
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            onValueChange = { textInput ->
                val parsedFloat = textInput.toFloatOrNull()
                if (parsedFloat != null) {
                    validatedFloatAmount.value = parsedFloat
                }
            },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
        )
    }
}

@Composable
fun CircleImage(
    modifier: Modifier = Modifier,
    drawableId: Int,
    width: Dp, height: Dp
) {
    Surface(
        modifier
            .sizeIn(width, height, width, height)
            .aspectRatio(1f),
        shape = CircleShape,
        elevation = 20.dp
    ) {
        Image(
            painterResource(id = drawableId),
            contentDescription = "..."
        )
    }
}

@Composable
fun RoundedLabel(
    modifier: Modifier = Modifier,
    label: String
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
        ) {
            Text(
                label, Modifier
                    .align(Alignment.Center),
                style = typography.h6
            )
        }
    }
}

/**
 * Display a small label representing if the coin has gone up/down
 *
 * - if up: up arrow with green color
 * - else: down arrow with red color
 */
//TODO reuse previous function with different type management for string
@Composable
fun SmallCoinChangeLabel(
    modifier: Modifier = Modifier,
    number: Float,
) {
    val label = "${abs(number)}% "
    val imageVector = if (number > 0) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
    val color = if (number > 0) Color(0xff12c785) else Color(0xffe93943)

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color,
        contentColor = Color.White
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
        ) {
            TextWithIcon(label, imageVector)
        }
    }
}

@Composable
fun TextWithIcon(string: String, imageVector: ImageVector) {

    val myId = "inlineContent"
    val text = buildAnnotatedString {
        // Append a placeholder string "[icon]" and attach an annotation "inlineContent" on it.
        appendInlineContent(myId, "[icon]")
        append(string)
    }
    val inlineContent = mapOf(
        Pair(
            // This tells the [CoreText] to replace the placeholder string "[icon]" by
            // the composable given in the [InlineTextContent] object.
            myId,
            InlineTextContent(
                // Placeholder tells text layout the expected size and vertical alignment of
                // children composable.
                Placeholder(
                    width = 21.sp,
                    height = 21.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                // This Icon will fill maximum size, which is specified by the [Placeholder]
                // above. Notice the width and height in [Placeholder] are specified in TextUnit,
                // and are converted into pixel by text layout.

                Icon(imageVector, string)
            }
        )
    )
    Text(
        text,
        textAlign = TextAlign.Center,
        style = typography.body2,
        inlineContent = inlineContent,

        )

}

@Composable
fun CircleButtonDest(
    border: Dp,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier
            .sizeIn(20.dp, 20.dp, 30.dp, 30.dp)
            .aspectRatio(1f),
        //.offset(x = dpx, y = dpy),

        color = MaterialTheme.colors.surface,
        shape = CircleShape,
        border = BorderStroke(
            border,
            if (MaterialTheme.colors.isLight) Color.LightGray
            else MaterialTheme.colors.onSurface
        ),

        ) {
        Icon(
            Icons.Filled.ArrowDropDown, contentDescription = "arrow down",

            tint = MaterialTheme.colors.onSurface
        )
    }
}


@Composable
fun CircleIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector, border: Dp, string: String,
    size_height: Dp, size_width: Dp
) {

    Surface(
        modifier = Modifier
            .sizeIn(size_width, size_height, size_width, size_height)
            .aspectRatio(1f),
        //.offset(x = dpx, y = dpy),

        color = MaterialTheme.colors.surface,
        shape = CircleShape,
        border = BorderStroke(
            border,
            MaterialTheme.colors.onSurface
        )
    ) {
        Icon(imageVector = imageVector, contentDescription = string)
    }
}
