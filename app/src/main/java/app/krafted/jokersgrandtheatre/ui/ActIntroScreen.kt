package app.krafted.jokersgrandtheatre.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.data.DialogueRepository
import app.krafted.jokersgrandtheatre.model.Act
import app.krafted.jokersgrandtheatre.model.JokerExpression
import app.krafted.jokersgrandtheatre.ui.components.CurtainTransition
import app.krafted.jokersgrandtheatre.ui.components.DialogueBox
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.GoldButton
import app.krafted.jokersgrandtheatre.ui.components.JokerPortrait
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.components.TheatricalTitle
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import kotlinx.coroutines.delay

internal data class ActVisuals(
    val actKey: String,
    val backgroundRes: Int,
    val accent: Color,
    val expression: JokerExpression,
    val moodName: String,
    val tagline: String,
    val numeral: String
)

internal fun actVisuals(act: Act): ActVisuals = when (act) {
    Act.ACT_II -> ActVisuals(
        actKey = "actII",
        backgroundRes = R.drawable.jok019_back_3,
        accent = Color(0xFF9BE37A),
        expression = JokerExpression.SINISTER,
        moodName = "Sinister",
        tagline = "The Mirror of Memory",
        numeral = "II"
    )

    Act.ACT_III -> ActVisuals(
        actKey = "actIII",
        backgroundRes = R.drawable.jok019_back_4,
        accent = Color(0xFFFF7A18),
        expression = JokerExpression.UNHINGED,
        moodName = "Unhinged",
        tagline = "Three Masks, One Crown",
        numeral = "III"
    )

    else -> ActVisuals(
        actKey = "actI",
        backgroundRes = R.drawable.jok019_back_2,
        accent = app.krafted.jokersgrandtheatre.ui.theme.TheatreGold,
        expression = JokerExpression.NEUTRAL,
        moodName = "Theatrical",
        tagline = "A Duel of Words",
        numeral = "I"
    )
}

@Composable
fun ActIntroScreen(
    act: Act,
    dialogue: DialogueRepository,
    onBegin: () -> Unit
) {
    val visuals = remember(act) { actVisuals(act) }
    val entranceLine = remember(act) { dialogue.line(visuals.actKey, "entrance") }

    var curtainOpen by remember { mutableStateOf(false) }
    var doneTyping by remember { mutableStateOf(false) }

    LaunchedEffect(act) {
        delay(600L)
        curtainOpen = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(visuals.backgroundRes, tint = Color(0xA6080203))
        EmberParticles(density = 20, opacity = 0.45f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(22.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "· ACT ${visuals.numeral} OF III ·",
                fontFamily = PlayfairFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 11.sp,
                letterSpacing = 4.sp,
                color = Color(0xA6FFE7A8),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            TheatricalTitle(
                text = visuals.tagline,
                size = 36.sp,
                accent = visuals.accent
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "MOOD · ${visuals.moodName}",
                fontFamily = CinzelFamily,
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = visuals.accent
            )

            Spacer(Modifier.height(16.dp))
            JokerPortrait(
                expression = visuals.expression,
                size = 160.dp,
                accent = visuals.accent,
                glowing = true
            )
            Spacer(Modifier.height(16.dp))

            DialogueBox(
                expression = visuals.expression,
                accent = visuals.accent,
                text = entranceLine,
                portraitSize = 64.dp,
                onDone = { doneTyping = true }
            )

            Spacer(Modifier.height(16.dp))
            GoldButton(
                onClick = onBegin,
                enabled = doneTyping,
                modifier = Modifier.alpha(if (doneTyping) 1f else 0.4f)
            ) {
                Text(
                    text = "RAISE THE CURTAIN",
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

        CurtainTransition(
            isOpen = curtainOpen,
            accent = visuals.accent
        )
    }
}
