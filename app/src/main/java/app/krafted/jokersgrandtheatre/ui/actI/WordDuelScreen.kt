package app.krafted.jokersgrandtheatre.ui.actI

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.model.WordResult
import app.krafted.jokersgrandtheatre.ui.components.JokerDialogue
import app.krafted.jokersgrandtheatre.ui.components.JokerPortrait
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimson
import app.krafted.jokersgrandtheatre.ui.theme.TheatreDark
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreOnSurface
import app.krafted.jokersgrandtheatre.ui.theme.TheatreOnSurfaceMuted
import app.krafted.jokersgrandtheatre.ui.theme.TheatrePurple
import app.krafted.jokersgrandtheatre.ui.theme.TheatreSurface
import app.krafted.jokersgrandtheatre.viewmodel.Phase
import app.krafted.jokersgrandtheatre.viewmodel.Turn
import app.krafted.jokersgrandtheatre.viewmodel.WordDuelState
import app.krafted.jokersgrandtheatre.viewmodel.WordDuelViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WordDuelScreen(
    viewModel: WordDuelViewModel,
    onActComplete: (playerActScore: Int, jokerActScore: Int, playerRoundsWon: Int, jokerRoundsWon: Int) -> Unit = { _, _, _, _ -> }
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
            TopBar(state)
            Spacer(Modifier.height(12.dp))
            JokerSection(state)
            Spacer(Modifier.height(10.dp))
            ScoreRow(state)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = TheatreGold.copy(alpha = 0.25f)
            )
            WordDisplay(state)
            Spacer(Modifier.height(14.dp))
            LetterGrid(
                state = state,
                onLetterTap = viewModel::onLetterTap,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
            TurnIndicator(state)
            Spacer(Modifier.height(10.dp))
            ActionRow(
                state = state,
                onUndo = viewModel::onUndoLastTap,
                onPass = viewModel::onPass
            )
        }

        if (state.phase == Phase.ROUND_END) {
            RoundEndOverlay(state, onContinue = viewModel::continueToNextRound)
        }

        if (state.phase == Phase.ACT_END) {
            ActEndOverlay(
                state = state,
                onContinue = {
                    onActComplete(
                        state.playerActScore,
                        state.jokerActScore,
                        state.playerRoundsWon,
                        state.jokerRoundsWon
                    )
                }
            )
        }
    }
}

@Composable
private fun TopBar(state: WordDuelState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ROUND ${state.round} / 3",
            color = TheatreGold,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "YOU ${state.playerRoundsWon}",
                color = TheatreGold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = "  ·  ",
                color = TheatreOnSurfaceMuted,
                fontSize = 14.sp
            )
            Text(
                text = "${state.jokerRoundsWon} JOKER",
                color = TheatreCrimson,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun JokerSection(state: WordDuelState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JokerPortrait(
            expression = state.jokerExpression,
            modifier = Modifier.size(96.dp)
        )
        Spacer(Modifier.width(12.dp))
        JokerDialogue(
            text = state.jokerLine,
            color = TheatreOnSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ScoreRow(state: WordDuelState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "YOU ${state.playerActScore + state.playerRoundScore} pts",
            color = TheatreGold,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "JOKER ${state.jokerActScore + state.jokerRoundScore} pts",
            color = TheatreGold.copy(alpha = 0.7f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun WordDisplay(state: WordDuelState) {
    val pendingLetters = state.playerSelection.joinToString("") { state.grid[it].toString() }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "YOUR WORD",
            color = TheatreOnSurfaceMuted,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp
        )
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            val committed = state.playerWord.ifEmpty { if (pendingLetters.isEmpty()) "—" else "" }
            Text(
                text = committed,
                color = TheatreOnSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            if (pendingLetters.isNotEmpty()) {
                Text(
                    text = pendingLetters,
                    color = TheatrePurple.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
        }
        WordErrorMessage(state)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "JOKER'S WORD",
            color = TheatreOnSurfaceMuted,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = state.jokerWord.ifEmpty { "—" },
            color = TheatreCrimson,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }
}

@Composable
private fun WordErrorMessage(state: WordDuelState) {
    var visible by remember(state.lastWordResult, state.lastSubmittedWord, state.lastSubmitter) {
        mutableStateOf(
            state.lastSubmitter == Turn.PLAYER &&
                (state.lastWordResult == WordResult.TOO_SHORT || state.lastWordResult == WordResult.NOT_A_WORD)
        )
    }
    LaunchedEffect(state.lastWordResult, state.lastSubmittedWord, state.lastSubmitter) {
        if (visible) {
            delay(2000)
            visible = false
        }
    }
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        val label = when (state.lastWordResult) {
            WordResult.TOO_SHORT -> "TOO SHORT"
            WordResult.NOT_A_WORD -> "NOT A WORD"
            else -> ""
        }
        Text(
            text = label,
            color = TheatreCrimson,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
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
                delay(idx * 30L)
                anim.animateTo(1f, tween(durationMillis = 300))
            }
        }
    }

    val jokerGlow = remember { Animatable(0f) }
    LaunchedEffect(state.jokerLastPick) {
        if (state.jokerLastPick != null) {
            jokerGlow.snapTo(0f)
            jokerGlow.animateTo(1f, tween(durationMillis = 200))
            jokerGlow.animateTo(0f, tween(durationMillis = 400))
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (row in 0 until 5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (col in 0 until 5) {
                    val index = row * 5 + col
                    if (index < state.grid.size) {
                        LetterCell(
                            letter = state.grid[index],
                            isLocked = index in state.lockedLetters,
                            isSelected = index in state.playerSelection,
                            isJokerLastPick = state.jokerLastPick == index,
                            jokerGlowAlpha = jokerGlow.value,
                            enabled = state.currentTurn == Turn.PLAYER &&
                                !state.isJokerThinking &&
                                state.phase == Phase.PLAYING,
                            entranceAlpha = cellAlphas.getOrNull(index)?.value ?: 1f,
                            onTap = { onLetterTap(index) },
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
    val targetScale = when {
        isSelected -> 1.15f
        isLocked -> 0.9f
        else -> 1.0f
    }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "LetterCellScale"
    )

    val background = when {
        isSelected -> TheatrePurple
        isLocked -> TheatreSurface
        else -> TheatreSurface
    }
    val textColor = when {
        isSelected -> TheatreGold
        isLocked -> TheatreOnSurfaceMuted
        else -> TheatreGold
    }

    Box(
        modifier = modifier
            .alpha(entranceAlpha)
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .then(
                if (isJokerLastPick && isLocked && jokerGlowAlpha > 0f) {
                    Modifier.border(
                        width = 2.dp,
                        color = TheatreCrimson.copy(alpha = jokerGlowAlpha),
                        shape = RoundedCornerShape(10.dp)
                    )
                } else Modifier
            )
            .clickable(enabled = enabled && !isLocked, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }
}

@Composable
private fun TurnIndicator(state: WordDuelState) {
    val (label, color, italic) = when {
        state.isJokerThinking -> Triple("JOKER IS THINKING…", TheatreCrimson, true)
        state.currentTurn == Turn.PLAYER -> Triple("YOUR TURN", TheatreGold, false)
        else -> Triple("JOKER'S TURN", TheatreCrimson, false)
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.Bold,
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
            fontSize = 14.sp,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun ActionRow(
    state: WordDuelState,
    onUndo: () -> Unit,
    onPass: () -> Unit
) {
    val canAct = state.currentTurn == Turn.PLAYER && state.phase == Phase.PLAYING
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onUndo,
            enabled = canAct && state.playerSelection.isNotEmpty(),
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = TheatreSurface,
                contentColor = TheatreOnSurface,
                disabledContainerColor = TheatreSurface.copy(alpha = 0.5f),
                disabledContentColor = TheatreOnSurfaceMuted
            )
        ) {
            Text("UNDO", fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp)
        }
        Button(
            onClick = onPass,
            enabled = canAct,
            modifier = Modifier.weight(2f),
            colors = ButtonDefaults.buttonColors(
                containerColor = TheatrePurple,
                contentColor = TheatreGold,
                disabledContainerColor = TheatrePurple.copy(alpha = 0.4f),
                disabledContentColor = TheatreOnSurfaceMuted
            )
        ) {
            Text("PASS / SUBMIT", fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        }
    }
}

@Composable
private fun RoundEndOverlay(state: WordDuelState, onContinue: () -> Unit) {
    val headline = when {
        state.playerRoundScore > state.jokerRoundScore -> "YOU WIN THE ROUND"
        state.jokerRoundScore > state.playerRoundScore -> "JOKER WINS THE ROUND"
        else -> "A DRAW"
    }
    val headlineColor = if (state.playerRoundScore > state.jokerRoundScore) TheatreGold else TheatreCrimson
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
                text = "You ${state.playerRoundScore}  —  ${state.jokerRoundScore} Joker",
                color = TheatreOnSurface,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Total  You ${state.playerActScore}  —  ${state.jokerActScore} Joker",
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
                    containerColor = TheatrePurple,
                    contentColor = TheatreGold
                )
            ) {
                Text("NEXT ROUND", fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            }
        }
    }
}

@Composable
private fun ActEndOverlay(state: WordDuelState, onContinue: () -> Unit) {
    val playerWonAct = when {
        state.playerRoundsWon > state.jokerRoundsWon -> true
        state.jokerRoundsWon > state.playerRoundsWon -> false
        else -> state.playerActScore > state.jokerActScore
    }
    val headline = when {
        state.playerRoundsWon == state.jokerRoundsWon && state.playerActScore == state.jokerActScore -> "A DRAW"
        playerWonAct -> "YOU WIN THE ACT"
        else -> "JOKER WINS THE ACT"
    }
    val headlineColor = if (playerWonAct) TheatreGold else TheatreCrimson

    var fired by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1500)
        if (!fired) {
            fired = true
            onContinue()
        }
    }

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
                text = "ACT I COMPLETE",
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
                text = "Score  You ${state.playerActScore}  —  ${state.jokerActScore} Joker",
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
                    containerColor = TheatrePurple,
                    contentColor = TheatreGold
                )
            ) {
                Text("CONTINUE", fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            }
        }
    }
}
