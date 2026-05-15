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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import app.krafted.jokersgrandtheatre.game.TapResult
import app.krafted.jokersgrandtheatre.ui.components.CinzelLabel
import app.krafted.jokersgrandtheatre.ui.components.DialogueBox
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.GoldButton
import app.krafted.jokersgrandtheatre.ui.components.OrnateFrame
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.viewmodel.PatternPhase
import app.krafted.jokersgrandtheatre.viewmodel.PatternState
import app.krafted.jokersgrandtheatre.viewmodel.PatternViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SYMBOL_COUNT = 7
private const val GRID_COLS = 4
private const val GRID_ROWS = 2

private val ActAccent = Color(0xFFC0C0C0)
private val ActBg = Color(0xC5020C08)

@Composable
fun PatternInputScreen(
    viewModel: PatternViewModel,
    onActComplete: (playerActScore: Int, playerRoundsWon: Int, jokerRoundsWon: Int) -> Unit = { _, _, _ -> },
    onBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showQuitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = state.phase != PatternPhase.ACT_END) { showQuitDialog = true }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            containerColor = Color(0xFF2A0306),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            title = {
                Text("Abandon the Stage?", fontFamily = CinzelDecorativeFamily, fontWeight = FontWeight.Black, fontSize = 18.sp, color = ActAccent)
            },
            text = {
                Text("Your progress in this act will be lost.", fontFamily = PlayfairFamily, fontStyle = FontStyle.Italic, fontSize = 13.sp, color = Color(0xBFFFE7A8))
            },
            confirmButton = {
                TextButton(onClick = { showQuitDialog = false; onBack() }) {
                    Text("QUIT", fontFamily = CinzelFamily, fontWeight = FontWeight.Bold, color = Color(0xFFFF5A3A), fontSize = 13.sp, letterSpacing = 2.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text("STAY", fontFamily = CinzelFamily, fontWeight = FontWeight.Bold, color = ActAccent, fontSize = 13.sp, letterSpacing = 2.sp)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(
            app.krafted.jokersgrandtheatre.R.drawable.jok019_back_3,
            tint = ActBg
        )
        EmberParticles(density = 10, opacity = 0.3f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            PatternTopBar(state)
            Spacer(Modifier.height(8.dp))
            PatternJokerStrip(state)
            Spacer(Modifier.height(10.dp))

            val showGrid = state.phase == PatternPhase.PLAYER_INPUT ||
                (state.phase != PatternPhase.DISPLAY && state.lastTapResult == TapResult.SEQUENCE_COMPLETE)

            if (showGrid) {
                InputProgressLabel(state)
                Spacer(Modifier.height(12.dp))
                SymbolGrid(
                    state = state,
                    onTap = viewModel::onSymbolTap,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            } else {
                ProgressStrip(state)
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
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
            PatternRoundResultOverlay(
                state = state,
                onContinue = {
                    viewModel.clearLastTapResult()
                    viewModel.continueToNextRound()
                }
            )
        }

        if (state.phase == PatternPhase.ACT_END && overlayDelayDone) {
            PatternActCompleteOverlay(
                state = state,
                onContinue = {
                    viewModel.acknowledgeActEnd()
                    onActComplete(state.actScore, state.playerRoundsWon, state.jokerRoundsWon)
                }
            )
        }
    }
}

@Composable
private fun InputProgressLabel(state: PatternState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x8C000000), RoundedCornerShape(8.dp))
            .border(1.dp, ActAccent.copy(alpha = 0.33f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CinzelLabel("REVERSE TAPS", color = ActAccent, fontSize = 10.sp, letterSpacing = 2.sp)
            CinzelLabel("${state.inputProgress} / ${state.sequence.size}", color = Color(0xA6FFE7A8), fontSize = 10.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (i in 0 until state.sequence.size) {
                val filled = i >= state.sequence.size - state.inputProgress
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (filled) Color(0xFF7CD34E) else Color(0x1FFFFFFF))
                )
            }
        }
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
            shakeOffset.animateTo(0f, spring(dampingRatio = 0.1f), initialVelocity = 1800f)
        }
    }

    val redFlash = remember { Animatable(0f) }
    LaunchedEffect(state.lastTapResult, state.mistakesMade) {
        if (state.lastTapResult == TapResult.WRONG) {
            redFlash.snapTo(0.3f)
            redFlash.animateTo(0f, tween(450))
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
                    anim.animateTo(1f, tween(280))
                }
            }
        } else {
            cascadeProgress.forEach { it.snapTo(0f) }
        }
    }

    Box(
        modifier = modifier
            .offset { IntOffset(shakeOffset.value.dp.roundToPx(), 0) }
            .background(Color.Red.copy(alpha = redFlash.value))
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until GRID_ROWS) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until GRID_COLS) {
                        val index = row * GRID_COLS + col
                        if (index < SYMBOL_COUNT) {
                            SymbolCell(
                                index = index,
                                state = state,
                                cascadeGold = cascadeProgress[index].value,
                                enabled = enabled,
                                onTap = { onTap(index) },
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                        } else {

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(2.dp, ActAccent.copy(alpha = 0.27f), RoundedCornerShape(12.dp))
                                    .background(Color(0x73000000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "REVERSE",
                                        color = ActAccent,
                                        fontFamily = CinzelFamily,
                                        fontSize = 8.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${state.inputProgress}/${state.sequence.size}",
                                        color = ActAccent,
                                        fontFamily = CinzelDecorativeFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        "${state.sequence.size - state.inputProgress} left",
                                        color = ActAccent,
                                        fontFamily = CinzelFamily,
                                        fontSize = 8.sp
                                    )
                                }
                            }
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
    cascadeGold: Float,
    enabled: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLastCorrect = state.playerSequence.lastOrNull() == index &&
        state.lastTapResult == TapResult.CORRECT
    val isLastWrong = state.playerSequence.lastOrNull() == index &&
        state.lastTapResult == TapResult.WRONG
    val isCascade = cascadeGold > 0f

    val scale by animateFloatAsState(
        targetValue = if (isLastCorrect) 1.15f else 1.0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, 600f),
        label = "SymbolScale$index"
    )

    val borderColor = when {
        isCascade -> TheatreGold.copy(alpha = cascadeGold)
        isLastCorrect -> Color(0xFF7CD34E)
        isLastWrong -> Color(0xFFFF5A3A)
        else -> ActAccent.copy(alpha = 0.33f)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x0FFFFFFF), Color(0x73000000)),
                    radius = 200f
                )
            )
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = symbolDrawable(index)),
            contentDescription = "Symbol ${index + 1}",
            modifier = Modifier.fillMaxSize().padding(14.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun PatternRoundResultOverlay(state: PatternState, onContinue: () -> Unit) {
    val playerWon = state.lastTapResult == TapResult.SEQUENCE_COMPLETE
    val tone = if (playerWon) Color(0xFF7CD34E) else Color(0xFFFF5A3A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(bottom = 36.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF2A0306), Color(0xFF4A0008))),
                    RoundedCornerShape(14.dp)
                )
                .border(2.dp, tone, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CinzelLabel(
                    text = "· ROUND ${state.round} RESULT ·",
                    color = Color(0x8CFFE7A8),
                    fontSize = 9.sp,
                    letterSpacing = 3.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (playerWon) "You Win the Round" else "The Joker Wins",
                    fontFamily = CinzelDecorativeFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = tone,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (playerWon) "Sequence mirrored perfectly." else "The mirror broke.",
                    fontFamily = PlayfairFamily,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = Color(0xBFFFE7A8),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                DialogueBox(
                    expression = state.jokerExpression,
                    accent = ActAccent,
                    text = state.jokerLine,
                    portraitSize = 56.dp
                )
                Spacer(Modifier.height(12.dp))
                GoldButton(onClick = onContinue) {
                    Text(
                        text = "CONTINUE",
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 2.5.sp,
                        color = TheatreCrimsonDeep
                    )
                }
            }
        }
    }
}

@Composable
private fun PatternActCompleteOverlay(state: PatternState, onContinue: () -> Unit) {
    val playerWonAct = state.playerRoundsWon > state.jokerRoundsWon
    val tone = if (playerWonAct) Color(0xFF7CD34E) else Color(0xFFFF5A3A)
    var fired by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CinzelLabel(
                text = "· ACT II · CURTAIN ·",
                color = Color(0x8CFFE7A8),
                fontSize = 10.sp,
                letterSpacing = 4.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (playerWonAct) "Act Claimed" else "Act Lost",
                fontFamily = CinzelDecorativeFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = tone,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            OrnateFrame(accent = ActAccent, padding = 16.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CinzelLabel(
                        text = "YOUR SCORE FOR ACT II",
                        color = Color(0x8CFFE7A8),
                        fontSize = 10.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = state.actScore.toString(),
                        fontFamily = CinzelDecorativeFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp,
                        color = ActAccent,
                        style = TextStyle(shadow = Shadow(Color.Black, Offset.Zero, 18f))
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            GoldButton(
                onClick = {
                    if (!fired) { fired = true; onContinue() }
                }
            ) {
                Text(
                    text = "CONTINUE",
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.5.sp,
                    color = TheatreCrimsonDeep
                )
            }
        }
    }
}
