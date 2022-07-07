package gg.interstellar.wallet.android.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import gg.interstellar.wallet.android.R


val Modernista = FontFamily(
        Font(R.font.modernista_regular)
        )


// Set of Material typography styles to start with
/**val Typography = Typography(
    body1 = TextStyle(
        fontFamily = Modernista,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )*/

//From Rally
val Typography = Typography(
    defaultFontFamily = Modernista,
    h1 = TextStyle(
        fontWeight = FontWeight.W100,
        fontSize = 96.sp,
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 44.sp,
        //fontFamily = EczarFontFamily,
        letterSpacing = 1.5.sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 34.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 20.sp,
        //fontFamily = EczarFontFamily,
        letterSpacing = 0.sp// 3 before
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 3.sp
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.1.em
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.em //0.1 before
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.em
    ),
    button = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.em
    ),
    caption = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 10.sp
    )
)


// body1 = TextStyle(
// fontFamily = FontFamily.Default,
// fontWeight = FontWeight.Normal,
// fontSize = 16.sp
// )
//
// /* Other default text styles to override
// button = TextStyle(
// fontFamily = FontFamily.Default,
// fontWeight = FontWeight.W500,
// fontSize = 14.sp
// ),
// caption = TextStyle(
// fontFamily = FontFamily.Default,
// fontWeight = FontWeight.Normal,
// fontSize = 12.sp
// )
//