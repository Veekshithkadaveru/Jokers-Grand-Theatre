package app.krafted.jokersgrandtheatre.ui.actI

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.model.WordResult
import app.krafted.jokersgrandtheatre.ui.components.CinzelLabel
import app.krafted.jokersgrandtheatre.ui.components.DialogueBox
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.GoldButton
import app.krafted.jokersgrandtheatre.ui.components.JokerPortrait
import app.krafted.jokersgrandtheatre.ui.components.OrnateFrame
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonHi
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldHi
import app.krafted.jokersgrandtheatre.ui.theme.TheatreInk
import app.krafted.jokersgrandtheatre.viewmodel.Phase
import app.krafted.jokersgrandtheatre.viewmodel.Turn
import app.krafted.jokersgrandtheatre.viewmodel.WordDuelState
import app.krafted.jokersgrandtheatre.viewmodel.WordDuelViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ActAccent = TheatreGold
private val ActDeep = Color(0xFF7A0F12)
private val ActBackground = Color(0xC5080203)

@Composable
fun WordDuelScreen(
    viewModel: WordDuelViewModel,
    onActComplete: (playerActScore: Int, jokerActScore: Int, playerRoundsWon: Int, jokerRoundsWon: Int) -> Unit = { _, _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.phase) {
        if (state.phase == Phase.ACT_END) {
            delay(1800L)
            onActComplete(
                state.playerActScore,
                state.jokerActScore,
                state.playerRoundsWon,
                state.jokerRoundsWon
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(R.drawable.jok019_back_2, tint = ActBackground)
        EmberParticles(density = 14, opacity = 0.35f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            ActTopBar(state)
            Spacer(Modifier.height(8.dp))
            JokerStrip(state)
            Spacer(Modifier.height(10.dp))
            WordDisplayRow(state)
            Spacer(Modifier.height(10.dp))
            LetterGrid(
                state = state,
                onLetterTap = viewModel::onLetterTap,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(Modifier.height(10.dp))
            ActionRow(
                state = state,
                onUndo = viewModel::onUndoLastTap,
                onPass = viewModel::onPass
            )
        }

        if (state.phase == Phase.ROUND_END) {
            RoundResultOverlay(state, onContinue = viewModel::continueToNextRound)
        }

        if (state.phase == Phase.ACT_END) {
            ActCompleteOverlay(state)
        }
    }
}

@Composable
private fun ActTopBar(state: WordDuelState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x8C000000), RoundedCornerShape(8.dp))
            .border(1.dp, ActAccent.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ACT I",
            fontFamily = CinzelDecorativeFamily,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = ActAccent,
            style = TextStyle(
                shadow = Shadow(Color.Black, Offset(0f, 1f), 0f)
            )
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "· R${state.round}/3",
            color = Color(0xA6FFE7A8),
            fontFamily = CinzelFamily,
            fontSize = 9.sp,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 0 until 3) {
                val playerWin = i < state.playerRoundsWon
                val jokerWin = i >= state.playerRoundsWon && i < state.playerRoundsWon + state.jokerRoundsWon
                val isCurrent = i == state.playerRoundsWon + state.jokerRoundsWon
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                playerWin -> Color(0xFF7CD34E)
                                jokerWin -> Color(0xFFC92A1A)
                                isCurrent -> ActAccent
                                else -> Color(0x1FFFFFFF)
                            }
                        )
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = state.playerActScore.toString(),
            fontFamily = CinzelDecorativeFamily,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = ActAccent,
            style = TextStyle(shadow = Shadow(ActAccent.copy(alpha = 0.4f), Offset.Zero, 6f))
        )
    }
}

@Composable
private fun JokerStrip(state: WordDuelState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x8C000000), RoundedCornerShape(8.dp))
            .border(1.dp, ActAccent.copy(alpha = 0.44f), RoundedCornerShape(8.dp))
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JokerPortrait(expression = state.jokerExpression, size = 42.dp, accent = ActAccent)
        Spacer(Modifier.width(8.dp))
        Text(
            text = state.jokerLine,
            color = Color(0xD9FFE7A8),
            fontFamily = PlayfairFamily,
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WordDisplayRow(state: WordDuelState) {
    val pendingLetters = state.playerSelection.map { state.grid[it] }
    val playerTiles = state.playerWord.toList() + pendingLetters
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        WordStrip(
            label = "JOKER",
            accent = Color(0xFFFF5A3A),
            tiles = state.jokerWord.toList(),
            thinking = state.isJokerThinking
        )
        WordStrip(
            label = "YOU",
            accent = ActAccent,
            tiles = playerTiles,
            valid = playerTiles.size >= 3 && state.lastWordResult == WordResult.VALID,
            invalid = playerTiles.size >= 3 && state.lastWordResult != WordResult.VALID && state.lastWordResult != null
        )
    }
}

@Composable
private fun WordStrip(
    label: String,
    accent: Color,
    tiles: List<Char>,
    thinking: Boolean = false,
    valid: Boolean = false,
    invalid: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x8C000000), RoundedCornerShape(8.dp))
            .border(1.dp, accent.copy(alpha = 0.66f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = accent,
            fontFamily = CinzelFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 2.sp,
            modifier = Modifier.width(44.dp)
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (tiles.isEmpty()) {
                Text(
                    text = if (thinking) "thinking…" else "—",
                    color = Color(0x59FFE7A8),
                    fontFamily = PlayfairFamily,
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp
                )
            } else {
                tiles.forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(20.dp, 22.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFFE78A), Color(0xFFFFB53A), Color(0xFFA86B07))
                                ),
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = c.toString(),
                            color = Color(0xFF3A0306),
                            fontFamily = CinzelDecorativeFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
        Text(
            text = buildString {
                if (tiles.isNotEmpty()) append("×${tiles.size}")
                if (valid) append(" ✓")
                if (invalid) append(" ✗")
            },
            color = when {
                valid -> Color(0xFF7CD34E)
                invalid -> Color(0xFFFF5A3A)
                else -> Color(0x73FFE7A8)
            },
            fontFamily = CinzelFamily,
            fontSize = 10.sp,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun LetterGrid(
    state: WordDuelState,
    onLetterTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cellAlphas = remember(state.round, state.grid) {
        List(state.grid.size) { Animatable(0f) }
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(state.round, state.grid) {
        cellAlphas.forEachIndexed { idx, anim ->
            scope.launch {
                delay(idx * 25L)
                anim.animateTo(1f, tween(durationMillis = 280))
            }
        }
    }

    val jokerGlow = remember { Animatable(0f) }
    LaunchedEffect(state.jokerLastPick) {
        if (state.jokerLastPick != null) {
            jokerGlow.snapTo(0f)
            jokerGlow.animateTo(1f, tween(200))
            jokerGlow.animateTo(0f, tween(400))
        }
    }

    Box(
        modifier = modifier
            .background(Color(0x73000000), RoundedCornerShape(12.dp))
            .border(2.dp, ActAccent.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (row in 0 until 5) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (col in 0 until 5) {
                        val idx = row * 5 + col
                        if (idx < state.grid.size) {
                            LetterCell(
                                letter = state.grid[idx],
                                isLocked = idx in state.lockedLetters,
                                isSelected = idx in state.playerSelection,
                                isJokerLastPick = state.jokerLastPick == idx,
                                jokerGlowAlpha = jokerGlow.value,
                                enabled = state.currentTurn == Turn.PLAYER &&
                                    !state.isJokerThinking &&
                                    state.phase == Phase.PLAYING,
                                entranceAlpha = cellAlphas.getOrNull(idx)?.value ?: 1f,
                                onTap = { onLetterTap(idx) },
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LetterCell(
    letter: Char,
    isLocked: Boolean,
    isSelected: Boolean,
    isJokerLastPick: Boolean,
    jokerGlowAlpha: Float,
    enabled: Boolean,
    entranceAlpha: Float,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.15f
            isLocked -> 0.96f
            else -> 1.0f
        },
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "LetterScale"
    )

    val cellBg = if (isLocked)
        Brush.verticalGradient(listOf(Color(0xB30F0505), Color(0xB30F0505)))
    else
        Brush.verticalGradient(listOf(TheatreGoldHi, TheatreGold, TheatreGoldDeep))

    Box(
        modifier = modifier
            .alpha(entranceAlpha)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(cellBg)
            .border(
                width = if (isLocked) 1.dp else 2.dp,
                color = if (isLocked) Color(0xFF3A0306) else TheatreGoldDeep,
                shape = RoundedCornerShape(8.dp)
            )
            .then(
                if (isJokerLastPick && jokerGlowAlpha > 0f)
                    Modifier.border(2.dp, Color(0xFFFF5A3A).copy(alpha = jokerGlowAlpha), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable(enabled = enabled && !isLocked, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            color = if (isLocked) Color(0x40FFE7A8) else Color(0xFF3A0306),
            fontFamily = CinzelDecorativeFamily,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            style = TextStyle(
                shadow = if (!isLocked) Shadow(Color.White.copy(alpha = 0.5f), Offset(0f, 2f), 0f) else null
            )
        )
    }
}

@Composable
private fun ActionRow(state: WordDuelState, onUndo: () -> Unit, onPass: () -> Unit) {
    val canAct = state.currentTurn == Turn.PLAYER && state.phase == Phase.PLAYING
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val undoEnabled = canAct && state.playerSelection.isNotEmpty()
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x8C000000))
                .border(2.dp, ActAccent.copy(alpha = if (undoEnabled) 0.55f else 0.22f), RoundedCornerShape(8.dp))
                .clickable(enabled = undoEnabled, onClick = onUndo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "← UNDO",
                color = ActAccent.copy(alpha = if (undoEnabled) 1f else 0.4f),
                fontFamily = CinzelFamily,
                fontSize = 12.sp,
                letterSpacing = 1.5.sp
            )
        }

        Box(
            modifier = Modifier
                .weight(1.4f)
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFC92A1A), Color(0xFF7A0F12)))
                )
                .border(2.dp, ActAccent, RoundedCornerShape(8.dp))
                .clickable(enabled = canAct, onClick = onPass),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (!canAct) "PASSED" else "SUBMIT WORD",
                color = Color.White.copy(alpha = if (canAct) 1f else 0.5f),
                fontFamily = CinzelFamily,
                fontSize = 13.sp,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
private fun RoundResultOverlay(state: WordDuelState, onContinue: () -> Unit) {
    val playerWon = state.playerRoundScore > state.jokerRoundScore
    val tie = state.playerRoundScore == state.jokerRoundScore
    val title = when {
        tie -> "A Stalemate"
        playerWon -> "You Win the Round"
        else -> "The Joker Wins"
    }
    val tone = when {
        tie -> Color(0xFFCBB37C)
        playerWon -> Color(0xFF7CD34E)
        else -> Color(0xFFFF5A3A)
    }

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
                    text = title,
                    fontFamily = CinzelDecorativeFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = tone,
                    textAlign = TextAlign.Center,
                    style = TextStyle(shadow = Shadow(Color.Black, Offset.Zero, 14f))
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when {
                        tie -> "Honour preserved."
                        playerWon -> "A point to the challenger."
                        else -> "A point to the master."
                    },
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
private fun ActCompleteOverlay(state: WordDuelState) {
    val playerWonAct = state.playerRoundsWon > state.jokerRoundsWon
    val tone = if (playerWonAct) Color(0xFF7CD34E) else Color(0xFFFF5A3A)

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
                text = "· ACT I · CURTAIN ·",
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
            OrnateFrame(padding = 16.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CinzelLabel(
                        text = "YOUR SCORE FOR ACT I",
                        color = Color(0x8CFFE7A8),
                        fontSize = 10.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = state.playerActScore.toString(),
                        fontFamily = CinzelDecorativeFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp,
                        color = ActAccent,
                        style = TextStyle(shadow = Shadow(Color.Black, Offset.Zero, 18f))
                    )
                }
            }
        }
    }
}
