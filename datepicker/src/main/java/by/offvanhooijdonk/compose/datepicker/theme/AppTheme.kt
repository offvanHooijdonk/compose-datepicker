package by.offvanhooijdonk.compose.datepicker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun PreviewAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) darkPalette else lightPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

private val lightPalette = lightColors(
    primary = Indigo500,
    primaryVariant = Indigo500,
    secondary = DeepOrangeA200,
    surface = BackLight,
    onSurface = TextColor,
    onSecondary = Color.White,
)
private val darkPalette = darkColors(
    primary = Indigo500,
    primaryVariant = Indigo500,
    secondary = DeepOrangeA200,
    onSecondary = Color.White,
)