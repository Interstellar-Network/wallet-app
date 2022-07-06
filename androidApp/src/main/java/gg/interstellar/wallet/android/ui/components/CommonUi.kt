package gg.interstellar.wallet.android.ui.components

import android.icu.text.DecimalFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.R
import gg.interstellar.wallet.android.ui.theme.Modernista
import kotlin.math.abs


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
                    contentDescription = "Interstellar logo",
                    tint = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .padding(1.5.dp)
                )
            }
        )
    )
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
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

@Composable
fun ScreenTopButton( onClickGo: ()->Unit,
                    tittle:String
) {
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
                    Button(onClick =onClickGo,
                        modifier = Modifier
                            .align(Alignment.Center),
                        colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                        ),
                        border = BorderStroke(0.dp,Color.Transparent),
                        elevation = ButtonDefaults.elevation(0.dp,15.dp,0.dp)
                    ){
                        Text(
                            tittle,
                            color= MaterialTheme.colors.onSurface,
                            fontSize = 35.sp,
                        )
                    }
                }
            }
        }
}

//TODO make it cleaner
@Composable
fun ScreenTopBox(
        modifier:Modifier,
        tittle:String
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
    modifier:Modifier,
    tittle:String,
    number:Float
) {
    Box(
        modifier
            .shadow(elevation = 0.dp, shape = CircleShape, clip = false)
    ) {
                Box  (modifier = Modifier.padding(0.dp,10.dp))
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
                                tittle,
                                modifier = Modifier
                                    .align(Alignment.Center),
                                fontSize = 35.sp,
                            )
                        }
                    }
                CircleLabelWithIconIn(
                    modifier = Modifier
                        //.align(Alignment.TopCenter),
                        .offset(115.dp,13.dp),//TODO use alignment line
                    "" + formatChange(number)+ "% ",
                    if (number>0)  Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    if (number>0)  Color(0xff12c785) else Color(0xffe93943)
                )

            }
        }
}



/**
 * A row representing the basic information of an Currency for Overview Market
 * and sendCurrencies screens
 */

@Composable
fun CurrencyRow(
    modifier: Modifier = Modifier,
    name: String,
    coin: String,
    pubKey: String,
    amount: Float,
    amountFiat: Float,
    change:Float,
    usd:Float,
    changeOn:Boolean,
    largeRow: Boolean, // doubleColumn
    inputTextView: MutableState<String>,
    currencyInFiat: MutableState<String>,
    useInput: Boolean,
    inputDone:MutableState<Boolean>,
    single: Boolean,
    fiat: Boolean,
    color: Color
) {
    BaseRow(
        modifier = modifier,
        color = color,
        title = name,
        symbol  = coin,
        subtitle = "address:$pubKey",
        amount = amount,
        amountFiat = amountFiat,
        change = change,
        usd =usd,
        changeOn=changeOn,
        largeRow  = largeRow,
        single =single,
        inputTextView = inputTextView,
        currencyInFiat =currencyInFiat,
        useInput = useInput,
        inputDone = inputDone,
        fiat = fiat,
    )
}

/**
 * A row representing the basic information of a currency
 */

// TO manage spacing between two singles row in SendScreen
val HEIGHT_REF = 100.dp
val PADDING_REF = 5.dp
val PADDING_ADJUST = 18.dp
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
    usd:Float,
    changeOn: Boolean,
    largeRow: Boolean,
    single:Boolean,
    inputTextView: MutableState<String>,
    currencyInFiat: MutableState<String>,
    useInput:Boolean,
    inputDone:MutableState<Boolean>,
    fiat: Boolean
) {
    val dollarSign = if (fiat) "$" else ""
    val formattedAmount = formatAmount(amount)
    val formattedAmountFiat = formatAmount(amount*usd)
    val formattedChange = formatChange(change)

    if (
        useInput &&
        inputTextView.value != "_" &&
        inputTextView.value.isNotEmpty() &&
        inputTextView.value.isNotBlank() &&
        symbol != "select" &&
        !inputDone.value
    ) {// otherwise it crash
        currencyInFiat.value = formatAmount(inputTextView.value.toFloat() * usd)
    }
    Row(
        modifier = modifier
            .height(
                if (largeRow && single)
                    HEIGHT_REF - PADDING_ADJUST + PADDING_REF else HEIGHT_REF
            )
            //.padding(PADDING_ROW)

            .clearAndSetSemantics {
                contentDescription = // TODO update
                    "$title  ending in ${subtitle.takeLast(4)}, current balance $dollarSign$formattedAmount"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = modifier
        ){
            Box( modifier =
                if (largeRow && single)
                    modifier.padding(PADDING_ADJUST, 0.dp)
                else if (largeRow) modifier.padding(PADDING_ADJUST)
                else modifier.padding(PADDING_REF)
                // enlarge the box to put image,labels on border
                // no padding here for single row
                // padding modifier is injected in row creation of SingleStatement

            ) {// should use box in a box to display text label on border
                if (largeRow) {
                    LargeRow(
                        //modifier,
                        if (symbol == "select") "Tap Currency..."
                            else formattedAmount,
                        if (symbol == "select") "" else symbol,
                        color,
                        inputTextView,
                        useInput,
                        inputDone
                    )
                }
                else // Circle Row in columns
                   Box { CircleImage(modifier =Modifier,symbol,90.dp,90.dp) }
            }
            if (largeRow)
                if (title!="select") CircleImage(
                    modifier =Modifier.align(Alignment.CenterEnd),
                    symbol,
                    35.dp,35.dp
                )
            if (fiat) RoundedLabel(
                modifier =Modifier.align(Alignment.BottomCenter),
                if (useInput) " " +dollarSign + currencyInFiat.value +" "
                else " $dollarSign$formattedAmountFiat ",

            )
            if (changeOn) CircleLabelWithIconIn(
                modifier = Modifier.align(Alignment.TopCenter),
                "$formattedChange% ",
                if (change>0)  Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                if (change>0)  Color(0xff12c785) else Color(0xffe93943)
            )
        }
    }
    Spacer(Modifier.height(if (single) 0.dp else if (largeRow) 15.dp else 25.dp))
}

@Composable
fun AddressRow(
    modifier: Modifier = Modifier,
    name: String,
    pubKey: String,
    largeRow: Boolean,
    single: Boolean,
    currencyInFiat: MutableState<String>,
    useInput: Boolean,
    color:Color
) {
    SimpleBaseRow(
        modifier = modifier,
        title = name,
        largeRow  = largeRow,
        single = single,
        currencyInFiat = currencyInFiat,
        useInput = useInput,
        color = color
    )
}

@Composable
private fun SimpleBaseRow(
    modifier: Modifier = Modifier,
    color: Color,
    title: String,
    largeRow: Boolean,
    single: Boolean,
    currencyInFiat: MutableState<String>,
    useInput: Boolean
) {
    val noInput = remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .height(HEIGHT_REF - 3.5.dp)
            .clearAndSetSemantics {
                contentDescription =
                    "$title add other info"
            },
        verticalAlignment = Alignment.CenterVertically

    ) {
        Box {
            Box(
                modifier =
                if (largeRow && single)
                    modifier.padding(PADDING_ADJUST, PADDING_REF,)
                else modifier.padding(PADDING_REF)
                    //.padding(PADDING_FOR_SIMPLE_ROW), // enlarge the box to put image,labels on border
            ) {// use box in a box to display text label on border
                if (largeRow) {
                    LargeRow(
                        //modifier,
                        if (title=="select") "Tap Contact...."
                        else title.uppercase(),
                        "", //no symbol
                        color,
                        currencyInFiat, // no textview to update
                        false,
                        noInput,
                    )
                } else {// Row of Circles in two columns
                    CircleImage(modifier = Modifier,//.align(Alignment.Center),
                        title, 90.dp, 90.dp)
                }

                //Row{Spacer(modifier =Modifier.height(100.dp))} TEST to enlarge
            }
            if (largeRow) {
                if (title!="select") CircleImage(
                    modifier = Modifier.align(Alignment.CenterEnd), title,
                    width = 35.dp, height = 35.dp
                )
                if (useInput) RoundedLabel(modifier= Modifier.align(Alignment.BottomCenter),
                    label = " "+ currencyInFiat.value +" USD ")

            } else {
                RoundedLabel(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    " "+title.uppercase()+ " "
                )
            }
        }
    }
    Spacer(Modifier.height(if (single) 0.dp else if (largeRow) 15.dp else 25.dp))
}

@Composable
fun LargeRow(
        string:String, symbol:String,color: Color,
        inputTextView: MutableState<String>,
        useInput:Boolean,
        inputDone:MutableState<Boolean>
 ) {
    if ((inputTextView.value =="_") && (string != "select") &&(useInput))
        inputTextView.value = string
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
        if (useInput) { // trigger usage of inputTextView for keypad
            /**Text(
                inputTextView.value + " " + symbol,
                modifier = Modifier.align(Alignment.Center),
                color = if (MaterialTheme.colors.isLight) Color.White
                else Color.Black
            )*/
            // with android keypad
            InputTextNumber( modifier = Modifier.align(Alignment.Center)
                ,inputTextView = inputTextView, inputDone =inputDone,string = symbol )

        } else
            Text(
                "$string $symbol",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colors.onSurface,
                style = typography.h5)
    }
}

@Composable
fun InputTextNumber(
        modifier:Modifier,
        inputTextView: MutableState<String>,
        inputDone:MutableState<Boolean>,
        string:String,

) {
    val change: (String)->Unit= {it->inputTextView.value = it}
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = inputTextView.value,
        singleLine = true,
        enabled = !inputDone.value,
        modifier = modifier,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
        fontSize = 26.sp, ), //TODO use style = typography.h5

        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor= MaterialTheme.colors.onPrimary,
            disabledTextColor = MaterialTheme.colors.onSurface,
            backgroundColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.onPrimary,
            trailingIconColor = MaterialTheme.colors.onPrimary,

        ),
        //label= { Text("amount in $string") },
        placeholder ={ Text(text = "amount in $string")},

        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        onValueChange = change,

        keyboardActions = KeyboardActions(onDone = {
            inputTextView.value += " $string"
            inputDone.value = true
            focusManager.clearFocus()
        }),

    )

}



/**
 * A vertical colored line that will be used in a [BaseRow] to differentiate pubKey.
 */
@Composable
private fun PubKeyIndicator(color: Color, modifier: Modifier = Modifier) {
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
     val logoName = "ic_" + string.lowercase() // crash depend on those val position

     Surface(
         modifier
             .sizeIn(width, height, width, height)
             .aspectRatio(1f),
         shape = CircleShape,
         elevation = 20.dp
     ) {
         val drawableId = remember(logoName) {
             context.resources.getIdentifier(
                 logoName,
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
    label: String) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
    ) {
        Box(
            modifier = Modifier
            .shadow(elevation = 20.dp, shape = RectangleShape, clip = false)
        ) {
            Text(label, Modifier
                .align(Alignment.Center),
                style = typography.h6)
        }
    }
}

//TODO reuse previous function with different type management for string
@Composable
fun CircleLabelWithIconIn(
    modifier: Modifier=Modifier,
    label:String, imageVector: ImageVector,color: Color) {

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
            TextWithIcon(label,imageVector)
        }
    }
}

@Composable
fun TextWithIcon(string:String,imageVector: ImageVector) {

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
        style = typography.body2,
        inlineContent = inlineContent,

    )

}

@Composable
fun CircleButtonDest( /// For sendCurrencies screen
    modifier:Modifier=Modifier,
    border:Dp,
    onClickDest: () -> Unit
)
{
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
        IconButton(
            onClick = onClickDest,
        ) {
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "arrow down",

                tint= MaterialTheme.colors.onSurface
            )
        }
    }
}


@Composable
fun CircleIcon(
    modifier:Modifier=Modifier,
    imageVector: ImageVector, border: Dp, string: String,
    size_height: Dp, size_width: Dp) {

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

fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}
fun formatChange(change: Float): String {
    return AmountDecimalFormat.format(abs(change))
}
/** for future pubKey usage*/
@Composable
fun StellarDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.background, thickness = 1.dp, modifier = modifier)
}


//private val AccountDecimalFormat = DecimalFormat("####")
private val AmountDecimalFormat = DecimalFormat("#,###.##")

/**
 * Used with currencies  to create the animated circle.
 */
fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}
