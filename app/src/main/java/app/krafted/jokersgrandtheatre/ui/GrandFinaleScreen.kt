package app.krafted.jokersgrandtheatre.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.ui.components.CinzelLabel
import app.krafted.jokersgrandtheatre.ui.components.CountUp
import app.krafted.jokersgrandtheatre.ui.components.Curtain
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.GoldButton
import app.krafted.jokersgrandtheatre.ui.components.OrnateFrame
import app.krafted.jokersgrandtheatre.ui.components.SparkleBurst
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.components.TheatricalTitle
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldHi
import kotlinx.coroutines.delay

@Composable
fun GrandFinaleScreen(
    priorStakes: Int,
    actIIIScore: Int,
    onReturnToLobby: () -> Unit
) {
    val total = priorStakes + actIIIScore

    var curtainOpen by remember { mutableStateOf(false) }
    var showCard1 by remember { mutableStateOf(false) }
    var showCard2 by remember { mutableStateOf(false) }
    var showTotal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(800L)
        curtainOpen = true
        delay(400L)
        showCard1 = true
        delay(400L)
        showCard2 = true
        delay(400L)
        showTotal = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(R.drawable.jok019_back_5)
        EmberParticles(density = 25)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TheatricalTitle(text = "THE GRAND FINALE", size = 30.sp)

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = TheatreGold.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = showCard1,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
            ) {
                ScoreCard(label = "ACTS I & II", score = priorStakes, durationMs = 800)
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showCard2,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
            ) {
                ScoreCard(label = "ACT III — FINAL GAMBLE", score = actIIIScore, durationMs = 800)
            }

            Spacer(Modifier.height(20.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = TheatreGold.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = showTotal,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 2 }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    SparkleBurst(color = TheatreGold, count = 16, size = 260f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CinzelLabel(
                            text = "TOTAL SCORE",
                            color = TheatreGoldHi,
                            fontSize = 11.sp,
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        CountUp(
                            value = total,
                            durationMs = 1200,
                            style = TextStyle(
                                fontFamily = CinzelDecorativeFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 60.sp,
                                brush = Brush.verticalGradient(
                                    listOf(TheatreGoldHi, TheatreGold, TheatreGoldDeep)
                                ),
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        CinzelLabel(
                            text = "POINTS",
                            color = TheatreGold.copy(alpha = 0.7f),
                            fontSize = 9.sp,
                            letterSpacing = 3.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            GoldButton(onClick = onReturnToLobby, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "RETURN TO LOBBY",
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.5.sp,
                    color = TheatreCrimsonDeep
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        Curtain(isOpen = curtainOpen)
    }
}

@Composable
private fun ScoreCard(label: String, score: Int, durationMs: Int) {
    OrnateFrame(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CinzelLabel(
                text = label,
                color = TheatreGold.copy(alpha = 0.8f),
                fontSize = 10.sp,
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            CountUp(
                value = score,
                durationMs = durationMs,
                style = TextStyle(
                    fontFamily = CinzelDecorativeFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp,
                    brush = Brush.verticalGradient(
                        listOf(TheatreGoldHi, TheatreGold, TheatreGoldDeep)
                    ),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
