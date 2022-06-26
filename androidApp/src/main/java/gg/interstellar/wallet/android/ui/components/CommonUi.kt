package gg.interstellar.wallet.android.ui

import android.icu.text.DecimalFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.components.handlebuttonClick
import gg.interstellar.wallet.android.ui.theme.Modernista
import kotlin.math.abs


//val horizontalLine:HorizontalAlignmentLine = remember { HorizontalAlignmentLine(::min) }
//val horizontalLine:HorizontalAlignmentLine(merger = { old, new -> min(old, new) })

@Preview (showBackground = true )
@Composable
fun DisplayInterstellar() {

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
                    placeholderVerticalAlign  = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                // This Icon will fill maximum size, which is specified by the [Placeholder]
                // above. Notice the width and height in [Placeholder] are specified in TextUnit,
                // and are converted into pixel by text layout.

                Icon(
                    painterResource(R.drawable.ic_interstellar_black_logo),
                    contentDescription = "logo",
                    tint = if (MaterialTheme.colors.isLight) Color.Black
                    else Color.White,
                    modifier = Modifier
                        .padding(1.5.dp)
                )
            }
        )
    )
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.12f)
    ) {
        Text(
            intLogoText,
            textAlign = TextAlign.Center,
            fontFamily = Modernista, fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = if (MaterialTheme.colors.isLight) Color.Black
            else Color.White,

            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically)

                .shadow(elevation = 52.dp, clip = true),
            inlineContent = inlineContent //add logo in place holder
        )
    }
}

@Composable
fun ScreenTopBox(tittle:String) {
    Box(
        modifier = Modifier
            .shadow(elevation = 50.dp, shape = CircleShape, clip = false)
    ) {
        Surface(
            modifier = Modifier
                .sizeIn(280.dp, 120.dp, 280.dp, 120.dp)
                .padding(25.dp),
            shape = CircleShape,
            elevation = 50.dp,
        ) {
            Box(
                modifier = Modifier
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

/**
 * A row representing the basic information of an Currency for Market and sendCurrencies screens
 */
@Composable
fun CurrencyRow(
    modifier: Modifier = Modifier,
    name: String,
    coin: String,
    pubkey: String,
    amount: Float,
    amountFiat: Float,
    change:Float,
    largeRow: Boolean,
    inputTextView: MutableState<String>,
    useInput: Boolean,
    single: Boolean,
    fiat: Boolean,
    color: Color
) {
    BaseRow(
        modifier = modifier,
        color = color,
        title = name,
        symbol  = coin,
        subtitle = "address:"+ pubkey,
        amount = amount,
        amountFiat = amountFiat,
        change = change,
        largeRow  = largeRow,
        inputTextView = inputTextView,
        useInput = useInput,
        fiat = true,
    )
}

/**
 * A row representing the basic information of a currency
 */

@Composable
private fun BaseRow(
    modifier: Modifier = Modifier,
    color: Color,
    title: String,
    symbol: String,
    subtitle: String,
    amount: Float,
    amountFiat: Float,
    change: Float,
    largeRow: Boolean,
    inputTextView: MutableState<String>,
    useInput:Boolean,
    fiat: Boolean
) {
    val dollarSign = if (fiat) "$ " else ""
    val formattedAmount = formatAmount(amount)
    val formattedAmountFiat = formatAmount(amountFiat)
    val formattedChange = formatChange(change)
    Row(
        modifier = modifier
            .height(98.dp)
            .clearAndSetSemantics {
                contentDescription =
                    "$title account ending in ${subtitle.takeLast(4)}, current balance $dollarSign$formattedAmount"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = modifier
        ){
            Box(
                modifier = modifier
                    .padding(10.dp), // enlarge the box to put image,labels on border

            ) {// should use box in a box to display text label on border
                if (largeRow) {
                    LargeRow(
                        if (symbol == "select") " Select a currency"
                            else formattedAmount,
                        if (symbol == "select") "" else symbol,
                        color,
                        inputTextView,
                        useInput//
                    )
                }
                else // Circle Row in columns
                   Box { CircleImage(modifier =Modifier,symbol,90.dp,90.dp)}
            }
            if (largeRow)
                CircleImage(
                    modifier.align(Alignment.CenterEnd),
                    symbol,
                    25.dp,25.dp
                )
            RoundedLabel(
                modifier.align(Alignment.BottomCenter),
                dollarSign+formattedAmountFiat,
                70.dp,25.dp
            )
            CircleLabelwithIconIn(
                modifier.align(Alignment.TopCenter),
                formattedChange+"%",
                if (change>0)  Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                if (change>0)  Color(0xffe93943) else Color(0xff12c785),
                70.dp,25.dp
            )
            //}
        }
    }
    Spacer(Modifier.height(if (largeRow) 0.dp else 15.dp))
}

@Composable
fun AddressRow(
    modifier: Modifier = Modifier,
    name: String,
    pubkey: String,
    largeRow: Boolean,
    inputTextView: MutableState<String>,
    color:Color
) {
    BaseAddressRow(
        modifier = modifier,
        title = name,
        largeRow  = largeRow,
        inputTextView = inputTextView,
        useInput = false,
        color = color
    )
}

@Composable
private fun BaseAddressRow(
    modifier: Modifier = Modifier,
    color: Color,
    title: String,
    largeRow: Boolean,
    inputTextView: MutableState<String>,
    useInput: Boolean
) {
    Row(
        modifier = modifier
            .height(98.dp)

            .clearAndSetSemantics {
                contentDescription =
                    "$title add other info"
            },
        verticalAlignment = Alignment.CenterVertically

    ) {
        Box {
            Box(
                modifier = modifier
                    .padding(10.dp), // enlarge the box to put image,labels on border
            ) {// use box in a box to display text label on border
                if (largeRow) {
                    LargeRow(
                        if (title=="select") "Select a destination"
                        else title.uppercase(),
                        "", //no symbol
                        color,
                        inputTextView, // no textview to update
                        false
                    )
                    //CircleImage(modifier=Modifier,title, 180.dp, 20.dp, 25.dp, 25.dp)

                } else {// Row of Circles in two columns
                    CircleImage(modifier = Modifier,//.align(Alignment.Center),
                        title, 90.dp, 90.dp)
                }
            }
            if (largeRow) {
                CircleImage(
                    modifier = modifier.align(Alignment.CenterEnd),
                    title, width = 25.dp, height = 25.dp
                )
            } else {
                RoundedLabel(
                    modifier = modifier.align(Alignment.BottomCenter),
                    title.uppercase(),
                    70.dp, 25.dp
                ) // size does not change
            }
        }
        //Spacer(Modifier.height(if (largeRow) 25.dp else 150.dp))
    }
}

@Composable
fun BoxScope.LargeRow(
                        string:String, symbol:String,color: Color,
                        inputTextView: MutableState<String>,
                        useInput:Boolean
 ) {
    if (inputTextView.value =="_"&& string != "select") inputTextView.value = string
    Box(
        modifier = Modifier
            .shadow(elevation = 10.dp, shape = RectangleShape, clip = false)
            .sizeIn(220.dp, 80.dp, 220.dp, 80.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    0.4f to color,
                    1f to Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(650f, 0f),
                )
            )
    ) {
        if (useInput) { // trigger usage of inputTextView for keyppad
            Text(
                inputTextView.value + " " + symbol,
                modifier = Modifier.align(Alignment.Center)
            )
        } else Text(
            string + " " + symbol,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}



/**
 * A vertical colored line that is used in a [BaseRow] to differentiate accounts.
 */
@Composable
private fun AccountIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(4.dp, 36.dp)
            .background(color = color)
    )
}
@Composable
fun CircleImage(
    modifier:Modifier=Modifier,
    string: String,
    width:Dp,height:Dp)
 {
     val context = LocalContext.current
     val logoname = "ic_" + string.lowercase() // crash depend on those val position

     Surface(
         modifier
             .sizeIn(width, height, width, height)
             .aspectRatio(1f),
         shape = CircleShape,
     ) {
         val drawableId = remember(logoname) {
             context.resources.getIdentifier(
                 logoname,
                 "drawable",
                 context.packageName
             )
         }
         Image(
             painterResource(id = drawableId),
             contentDescription = "..."
         )
     }
}

@Composable
fun RoundedLabel(
    modifier: Modifier=Modifier,
    label: String,
    size_width: Dp, size_height:Dp ) {
    Surface(
        modifier = modifier
            .sizeIn(size_width, size_height, size_width, size_height),
        shape = CircleShape,
    ) {
        Box(
            modifier = Modifier
            .shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
        ) {
            Text(label, Modifier
                .align(Alignment.Center),
                style = typography.caption)
        }
    }
}

//TODO reuse previous function with different type management for string
@Composable
fun CircleLabelwithIconIn(
    modifier: Modifier=Modifier,
    label:String, imageVector: ImageVector,color: Color,
    size_width: Dp, size_height:Dp ) {

    Surface(
        modifier = modifier
            .sizeIn(size_width, size_height, size_width, size_height),
        shape = CircleShape,
        color = color,
        contentColor = Color.White
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
        ) {
            TextwithIcon(label,imageVector)
        }
    }
}

@Composable
fun TextwithIcon(string:String,imageVector: ImageVector) {

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
                    width = 25.sp,
                    height = 25.sp,
                    placeholderVerticalAlign  = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                // This Icon will fill maximum size, which is specified by the [Placeholder]
                // above. Notice the width and height in [Placeholder] are specified in TextUnit,
                // and are converted into pixel by text layout.

                Icon(imageVector, string)
            }
        )
    )
    Text(text,
        textAlign = TextAlign.Center,
        inlineContent = inlineContent,
        //fontsize = 10.dp,
        //Color = Color.White,
    )

}

@Composable
fun CircleIcon(
    imageVector: ImageVector, border: Dp, string: String,
    dpx: Dp, dpy: Dp,size_height: Dp, size_width: Dp) {

    Surface(
        modifier = Modifier
            .sizeIn(size_width, size_height, size_width, size_height)
            .aspectRatio(1f)
            .offset(x = dpx, y = dpy),

        color = MaterialTheme.colors.surface,
        shape = CircleShape,
        border = BorderStroke(
            border,
            if (MaterialTheme.colors.isLight) Color.White
            else Color.Black
        )

        ) {
        Icon(imageVector = imageVector, contentDescription = string)
    }
}


@Composable
fun StellarDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.background, thickness = 1.dp, modifier = modifier)
}

fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}
fun formatChange(change: Float): String {
    return AmountDecimalFormat.format(abs(change))
}


private val AccountDecimalFormat = DecimalFormat("####")
private val AmountDecimalFormat = DecimalFormat("#,###.##")

/**
 * Used with currencies  to create the animated circle.
 */
fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {

    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}
