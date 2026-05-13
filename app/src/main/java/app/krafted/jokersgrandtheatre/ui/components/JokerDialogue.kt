package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import kotlinx.coroutines.delay

@Composable
fun JokerDialogue(
    text: String,
    modifier: Modifier = Modifier,
    typingDelayMs: Long = 20L,
    textStyle: TextStyle = Typography().bodyLarge,
    color: Color = Color.White
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText = ""
        for (i in text.indices) {
            displayedText += text[i]
            delay(typingDelayMs)
        }
    }

    Text(
        text = displayedText,
        modifier = modifier,
        style = textStyle,
        color = color,
        fontStyle = FontStyle.Italic
    )
}