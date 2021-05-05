package by.offvanhooijdonk.compose.datepicker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun PreviewAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = Indigo500,
            primaryVariant = Indigo500,
            secondary = DeepOrangeA200,
            surface = BackLight,
            onSurface = TextColor,
            onSecondary = Color.White,
        ),
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}