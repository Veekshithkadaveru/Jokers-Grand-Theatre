package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.model.JokerExpression
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCream
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import kotlinx.coroutines.delay

@Composable
fun JokerDialogue(
    text: String,
    modifier: Modifier = Modifier,
    typingDelayMs: Long = 20L,
    textStyle: TextStyle = TextStyle(
        fontFamily = PlayfairFamily,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        color = TheatreCream,
        lineHeight = 20.sp
    ),
    color: Color = TheatreCream,
    onDone: (() -> Unit)? = null
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText = ""
        for (i in text.indices) {
            displayedText += text[i]
            delay(typingDelayMs)
        }
        onDone?.invoke()
    }

    Text(
        text = "\"$displayedText\"",
        modifier = modifier,
        style = textStyle,
        color = color
    )
}

@Composable
fun DialogueBox(
    expression: JokerExpression,
    accent: Color,
    text: String,
    modifier: Modifier = Modifier,
    portraitSize: Dp = 56.dp,
    onDone: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xD9140306), Color(0xD928060A))
                ),
                RoundedCornerShape(12.dp)
            )
            .border(2.dp, accent, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        JokerPortrait(
            expression = expression,
            size = portraitSize,
            accent = accent
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f).padding(top = 4.dp)) {
            Text(
                text = "THE JOKER",
                color = accent,
                fontFamily = CinzelFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))
            JokerDialogue(
                text = text,
                onDone = onDone
            )
        }
    }
}
