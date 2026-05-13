package app.krafted.jokersgrandtheatre.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TheatreColorScheme = darkColorScheme(
    primary = TheatreGold,
    onPrimary = TheatreDark,
    secondary = TheatreCrimson,
    onSecondary = TheatreOnSurface,
    tertiary = TheatrePurple,
    onTertiary = TheatreOnSurface,
    background = TheatreDark,
    onBackground = TheatreOnSurface,
    surface = TheatreSurface,
    onSurface = TheatreOnSurface,
    surfaceVariant = Color(0xFF1F0F0F),
    onSurfaceVariant = TheatreOnSurfaceMuted,
    error = Color(0xFFCF6679),
    onError = TheatreDark,
)

@Composable
fun JokersGrandTheatreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TheatreColorScheme,
        typography = Typography,
        content = content
    )
}
