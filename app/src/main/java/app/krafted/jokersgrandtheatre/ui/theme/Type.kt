package app.krafted.jokersgrandtheatre.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R

val CinzelFamily = FontFamily(
    Font(R.font.cinzel, FontWeight.Normal),
    Font(R.font.cinzel, FontWeight.Bold),
    Font(R.font.cinzel, FontWeight.Black),
)

val CinzelDecorativeFamily = FontFamily(
    Font(R.font.cinzel_decorative, FontWeight.Normal),
    Font(R.font.cinzel_decorative_black, FontWeight.Black),
)

val PlayfairFamily = FontFamily(
    Font(R.font.playfair_display, FontWeight.Normal),
    Font(R.font.playfair_display, FontWeight.Bold),
    Font(R.font.playfair_display_italic, FontWeight.Normal, FontStyle.Italic),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PlayfairFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 2.sp
    )
)
