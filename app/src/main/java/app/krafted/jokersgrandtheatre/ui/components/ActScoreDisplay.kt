package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldHi
import app.krafted.jokersgrandtheatre.ui.theme.TheatreSilver

@Composable
fun ActScoreDisplay(
    actLabel: String,
    score: Int,
    roundsWon: Int,
    jokerRoundsWon: Int,
    durationMs: Int = 1000,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 1200),
        label = "ScoreCountUp"
    )

    OrnateFrame(
        modifier = modifier.fillMaxWidth(),
        padding = 20.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            CinzelLabel(
                text = actLabel,
                color = TheatreGold,
                fontSize = 11.sp,
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = TheatreGold.copy(alpha = 0.55f)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = animatedScore.toString(),
                style = TextStyle(
                    fontFamily = CinzelDecorativeFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 48.sp,
                    brush = Brush.verticalGradient(
                        listOf(TheatreGoldHi, TheatreGold, TheatreGoldDeep)
                    ),
                    letterSpacing = 2.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            val animatedRoundsWon by animateIntAsState(
                targetValue = roundsWon,
                animationSpec = tween(durationMillis = durationMs),
                label = "RoundsWonCountUp"
            )
            val animatedJokerRoundsWon by animateIntAsState(
                targetValue = jokerRoundsWon,
                animationSpec = tween(durationMillis = durationMs),
                label = "JokerRoundsWonCountUp"
            )

            Text(
                text = "ROUNDS  $animatedRoundsWon – $animatedJokerRoundsWon",
                style = TextStyle(
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = TheatreSilver,
                    letterSpacing = 2.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A0008)
@Composable
private fun ActScoreDisplayPreview() {
    ActScoreDisplay(
        actLabel = "ACT I — WORD DUEL",
        score = 2450,
        roundsWon = 3,
        jokerRoundsWon = 2,
        durationMs = 1000,
        modifier = Modifier.padding(24.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A0008)
@Composable
private fun ActScoreDisplayZeroPreview() {
    ActScoreDisplay(
        actLabel = "ACT II — THE PATTERN",
        score = 0,
        roundsWon = 0,
        jokerRoundsWon = 0,
        modifier = Modifier.padding(24.dp)
    )
}
