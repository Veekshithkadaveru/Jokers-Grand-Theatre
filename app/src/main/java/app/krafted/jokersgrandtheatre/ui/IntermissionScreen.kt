package app.krafted.jokersgrandtheatre.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.data.DialogueRepository
import app.krafted.jokersgrandtheatre.model.Act
import app.krafted.jokersgrandtheatre.ui.components.DialogueBox
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.GoldButton
import app.krafted.jokersgrandtheatre.ui.components.JokerPortrait
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.components.TheatricalTitle
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep

private data class IntermissionVisuals(
    val intKey: String,
    val fromMood: String,
    val toMood: String,
    val fromAccent: Color,
    val target: ActVisuals,
    val buttonLabel: String
)

private fun intermissionVisuals(intermission: Act): IntermissionVisuals = when (intermission) {
    Act.INTERMISSION_II -> IntermissionVisuals(
        intKey = "intermissionII",
        fromMood = "Sinister",
        toMood = "Unhinged",
        fromAccent = Color(0xFF9BE37A),
        target = actVisuals(Act.ACT_III),
        buttonLabel = "CONTINUE · ACT III"
    )

    else -> IntermissionVisuals(
        intKey = "intermissionI",
        fromMood = "Theatrical",
        toMood = "Sinister",
        fromAccent = app.krafted.jokersgrandtheatre.ui.theme.TheatreGold,
        target = actVisuals(Act.ACT_II),
        buttonLabel = "CONTINUE · ACT II"
    )
}

@Composable
fun IntermissionScreen(
    intermission: Act,
    dialogue: DialogueRepository,
    onContinue: () -> Unit
) {
    val visuals = remember(intermission) { intermissionVisuals(intermission) }
    val moodShiftLine = remember(intermission) { dialogue.line(visuals.intKey, "moodShift") }

    var doneTyping by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(visuals.target.backgroundRes, tint = Color(0xC7080203))
        EmberParticles(density = 14, opacity = 0.35f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(22.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "· INTERMISSION ·",
                fontFamily = CinzelFamily,
                fontSize = 10.sp,
                letterSpacing = 4.sp,
                color = Color(0x99FFE7A8),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            TheatricalTitle(
                text = "The Mood Shifts",
                size = 28.sp,
                accent = visuals.target.accent
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = visuals.fromMood,
                    fontFamily = CinzelFamily,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = visuals.fromAccent
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "→",
                    fontFamily = CinzelFamily,
                    fontSize = 11.sp,
                    color = Color(0x66FFE7A8)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = visuals.toMood,
                    fontFamily = CinzelFamily,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = visuals.target.accent
                )
            }

            Spacer(Modifier.height(16.dp))
            JokerPortrait(
                expression = visuals.target.expression,
                size = 140.dp,
                accent = visuals.target.accent,
                glowing = true
            )
            Spacer(Modifier.height(16.dp))

            DialogueBox(
                expression = visuals.target.expression,
                accent = visuals.target.accent,
                text = moodShiftLine,
                portraitSize = 56.dp,
                onDone = { doneTyping = true }
            )

            Spacer(Modifier.height(14.dp))
            GoldButton(
                onClick = onContinue,
                enabled = doneTyping,
                modifier = Modifier.alpha(if (doneTyping) 1f else 0.4f)
            ) {
                Text(
                    text = visuals.buttonLabel,
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.5.sp,
                    color = TheatreCrimsonDeep,
                    style = TextStyle(
                        shadow = Shadow(
                            Color.Black.copy(alpha = 0.3f),
                            Offset(0f, 1f),
                            0f
                        )
                    )
                )
            }
        }
    }
}
