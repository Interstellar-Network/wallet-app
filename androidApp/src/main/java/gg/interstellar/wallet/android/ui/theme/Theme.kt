package gg.interstellar.wallet.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = PurpleCustom,
    secondary = MagentaCustom,
    secondaryVariant = BlueCustom,
    onPrimary = Color.DarkGray,
    surface = Color.White,
    onSurface = Color.Black,
    onSecondary = Color.Black,
    //background = DarkGrey,
    background = Color.Black,
    onBackground = Color.White
)

private val LightColorPalette = lightColors(

    primary = PurpleCustom,
    secondary = MagentaCustom,
    secondaryVariant = BlueCustom,
    onPrimary = Color.DarkGray,
    surface = Color.Black,
    onSurface = Color.White,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black
    /*Other default colors to override

    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
*/
)

@Composable
fun InterstellarWalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}