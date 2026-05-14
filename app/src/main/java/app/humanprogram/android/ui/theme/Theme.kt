package app.humanprogram.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF3E4C36),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF675E45),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF7B4E57),
    background = Color(0xFFFFFDF8),
    onBackground = Color(0xFF211F1A),
    surface = Color(0xFFF8F1E7),
    onSurface = Color(0xFF211F1A),
    surfaceVariant = Color(0xFFE8DED0),
    onSurfaceVariant = Color(0xFF51483B)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFC6D7B8),
    onPrimary = Color(0xFF26351F),
    secondary = Color(0xFFD1C4A4),
    onSecondary = Color(0xFF372F1D),
    tertiary = Color(0xFFE8B7C0),
    background = Color(0xFF171511),
    onBackground = Color(0xFFEDE6DA),
    surface = Color(0xFF242019),
    onSurface = Color(0xFFEDE6DA),
    surfaceVariant = Color(0xFF4B453A),
    onSurfaceVariant = Color(0xFFD4C8B8)
)

@Composable
fun HumanProgramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
