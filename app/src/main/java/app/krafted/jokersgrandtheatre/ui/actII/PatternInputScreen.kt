package app.krafted.jokersgrandtheatre.ui.actII

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.game.TapResult
import app.krafted.jokersgrandtheatre.ui.components.JokerDialogue
import app.krafted.jokersgrandtheatre.ui.components.JokerPortrait
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimson
import app.krafted.jokersgrandtheatre.ui.theme.TheatreDark
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreMidnightBlue
import app.krafted.jokersgrandtheatre.ui.theme.TheatreOnSurface
import app.krafted.jokersgrandtheatre.ui.theme.TheatreOnSurfaceMuted
import app.krafted.jokersgrandtheatre.ui.theme.TheatreSilver
import app.krafted.jokersgrandtheatre.viewmodel.PatternPhase
import app.krafted.jokersgrandtheatre.viewmodel.PatternState
import app.krafted.jokersgrandtheatre.viewmodel.PatternViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SYMBOL_COUNT = 7
private const val GRID_COLS = 2
private const val GRID_ROWS = 4

@Composable
fun PatternInputScreen(
    viewModel: PatternViewModel,
    onActComplete: (playerActScore: Int, playerRoundsWon: Int, jokerRoundsWon: Int) -> Unit = { _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TheatreDark)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            PatternTopBar(state)
            Spacer(Modifier.height(12.dp))
            PatternJokerSection(state)
            Spacer(Modifier.height(10.dp))
            PatternScoreRow(state)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = TheatreSilver.copy(alpha = 0.25f)
            )
            val showGrid = state.phase == PatternPhase.PLAYER_INPUT ||
                (state.phase != PatternPhase.DISPLAY && state.lastTapResult == TapResult.SEQUENCE_COMPLETE)
            if (showGrid) {
                InputProgressLabel(state)
                Spacer(Modifier.height(12.dp))
                SymbolGrid(
                    state = state,
                    onTap = viewModel::onSymbolTap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                ProgressStrip(state)
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ActiveSymbolDisplay(activeSymbolIndex = state.activeSymbolIndex)
                }
            }
        }

        val cascadeMs = SYMBOL_COUNT * 120L + 300L
        var overlayDelayDone by remember(state.round, state.lastTapResult) {
            mutableStateOf(state.lastTapResult != TapResult.SEQUENCE_COMPLETE)
        }
        LaunchedEffect(state.round, state.lastTapResult) {
            if (state.lastTapResult == TapResult.SEQUENCE_COMPLETE) {
                delay(cascadeMs)
                overlayDelayDone = true
            }
        }

        if (state.phase == PatternPhase.ROUND_END && overlayDelayDone) {
            PatternRoundEndOverlay(
                state = state,
                onContinue = {
                    viewModel.clearLastTapResult()
                    viewModel.continueToNextRound()
                }
            )
        }

        if (state.phase == PatternPhase.ACT_END && overlayDelayDone) {
            PatternActEndOverlay(
                state = state,
                onContinue = {
                    viewModel.acknowledgeActEnd()
                    onActComplete(
                        state.actScore,
                        state.playerRoundsWon,
                        state.jokerRoundsWon
                    )
                }
            )
        }
    }
}

@Composable
private fun InputProgressLabel(state: PatternState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "YOUR TURN",
            color = TheatreSilver,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 2.sp
        )
        Text(
            text = "${state.inputProgress} / ${state.sequence.size}",
            color = TheatreGold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SymbolGrid(
    state: PatternState,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(state.lastTapResult, state.mistakesMade) {
        if (state.lastTapResult == TapResult.WRONG) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.1f),
                initialVelocity = 1800f
            )
        }
    }

    val redFlash = remember { Animatable(0f) }
    LaunchedEffect(state.lastTapResult, state.mistakesMade) {
        if (state.lastTapResult == TapResult.WRONG) {
            redFlash.snapTo(1f)
            redFlash.animateTo(0f, tween(durationMillis = 450))
        }
    }

    val enabled = state.phase == PatternPhase.PLAYER_INPUT
    val cascadeActive = state.lastTapResult == TapResult.SEQUENCE_COMPLETE
    val cascadeProgress = remember { List(SYMBOL_COUNT) { Animatable(0f) } }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.lastTapResult, state.round) {
        if (cascadeActive) {
            cascadeProgress.forEachIndexed { idx, anim ->
                scope.launch {
                    delay(idx * 120L)
                    anim.animateTo(1f, tween(durationMillis = 280))
                }
            }
        } else {
            cascadeProgress.forEach { it.snapTo(0f) }
        }
    }

    Box(
        modifier = modifier
            .offset { IntOffset(shakeOffset.value.dp.roundToPx(), 0) }
            .background(TheatreCrimson.copy(alpha = redFlash.value * 0.25f))
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until GRID_ROWS) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until GRID_COLS) {
                        val index = row * GRID_COLS + col
                        if (index < SYMBOL_COUNT) {
                            SymbolCell(
                                index = index,
                                state = state,
                                cascade = cascadeProgress[index].value,
                                enabled = enabled,
                                onTap = { onTap(index) },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SymbolCell(
    index: Int,
    state: PatternState,
    cascade: Float,
    enabled: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLastTapped = state.playerSequence.lastOrNull() == index &&
        (state.lastTapResult == TapResult.CORRECT || state.lastTapResult == TapResult.SEQUENCE_COMPLETE)

    val pulseTarget = if (isLastTapped) 1.15f else 1.0f
    val scale by animateFloatAsState(
        targetValue = pulseTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 600f
        ),
        label = "SymbolCellScale-$index"
    )

    val greenPulse = remember { Animatable(0f) }
    LaunchedEffect(state.lastTapResult, state.inputProgress) {
        if (isLastTapped && state.lastTapResult == TapResult.CORRECT) {
            greenPulse.snapTo(1f)
            greenPulse.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = 600f
                )
            )
        } else if (!isLastTapped) {
            greenPulse.snapTo(0f)
        }
    }

    val baseBackground = TheatreMidnightBlue
    val tappedTint = Color(0xFF2EA043)
    val baseColor = lerp(baseBackground, tappedTint, greenPulse.value)
    val background = lerp(baseColor, TheatreGold, cascade)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .border(
                width = 1.dp,
                color = TheatreSilver.copy(alpha = 0.35f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = enabled, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = symbolDrawable(index)),
            contentDescription = "Symbol ${index + 1}",
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        )
    }
}

@Composable
private fun PatternRoundEndOverlay(state: PatternState, onContinue: () -> Unit) {
    val playerWon = state.lastTapResult == TapResult.SEQUENCE_COMPLETE
    val headline = if (playerWon) "YOU WIN THE ROUND" else "JOKER WINS THE ROUND"
    val headlineColor = if (playerWon) TheatreGold else TheatreCrimson
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ROUND ${state.round}",
                color = TheatreOnSurfaceMuted,
                fontSize = 14.sp,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = headline,
                color = headlineColor,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Round score  ${state.roundScore}",
                color = TheatreOnSurface,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Mistakes  ${state.mistakesMade}",
                color = TheatreOnSurfaceMuted,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Total  ${state.actScore}",
                color = TheatreOnSurfaceMuted,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Rounds  You ${state.playerRoundsWon}  —  ${state.jokerRoundsWon} Joker",
                color = TheatreOnSurfaceMuted,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(24.dp))
            JokerPortrait(
                expression = state.jokerExpression,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(12.dp))
            JokerDialogue(
                text = state.jokerLine,
                color = TheatreOnSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TheatreMidnightBlue,
                    contentColor = TheatreGold
                )
            ) {
                Text("NEXT ROUND", fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            }
        }
    }
}

@Composable
private fun PatternActEndOverlay(state: PatternState, onContinue: () -> Unit) {
    val playerWonAct = state.playerRoundsWon > state.jokerRoundsWon
    val tied = state.playerRoundsWon == state.jokerRoundsWon
    val headline = when {
        tied -> "A DRAW"
        playerWonAct -> "YOU WIN THE ACT"
        else -> "JOKER WINS THE ACT"
    }
    val headlineColor = if (playerWonAct) TheatreGold else TheatreCrimson

    var fired by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ACT II COMPLETE",
                color = TheatreOnSurfaceMuted,
                fontSize = 14.sp,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = headline,
                color = headlineColor,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Rounds  You ${state.playerRoundsWon}  —  ${state.jokerRoundsWon} Joker",
                color = TheatreOnSurface,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Act score  ${state.actScore}",
                color = TheatreOnSurface,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(24.dp))
            JokerPortrait(
                expression = state.jokerExpression,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(12.dp))
            JokerDialogue(
                text = state.jokerLine,
                color = TheatreOnSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    if (!fired) {
                        fired = true
                        onContinue()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TheatreMidnightBlue,
                    contentColor = TheatreGold
                )
            ) {
                Text("CONTINUE", fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            }
        }
    }
}
