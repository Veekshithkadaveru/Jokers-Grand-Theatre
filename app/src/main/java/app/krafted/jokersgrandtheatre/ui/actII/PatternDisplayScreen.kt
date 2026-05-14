package app.krafted.jokersgrandtheatre.ui.actII

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.ui.components.JokerDialogue
import app.krafted.jokersgrandtheatre.ui.components.JokerPortrait
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimson
import app.krafted.jokersgrandtheatre.ui.theme.TheatreDark
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreMidnightBlue
import app.krafted.jokersgrandtheatre.ui.theme.TheatreOnSurface
import app.krafted.jokersgrandtheatre.ui.theme.TheatreOnSurfaceMuted
import app.krafted.jokersgrandtheatre.ui.theme.TheatreSilver
import app.krafted.jokersgrandtheatre.viewmodel.PatternState
import app.krafted.jokersgrandtheatre.viewmodel.PatternViewModel

@Composable
fun PatternDisplayScreen(
    viewModel: PatternViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = modifier
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
}

@Composable
internal fun ActiveSymbolDisplay(activeSymbolIndex: Int) {
    val isActive = activeSymbolIndex in 0..6
    val scale = remember { Animatable(0.8f) }
    val glow = remember { Animatable(0f) }

    LaunchedEffect(activeSymbolIndex) {
        if (isActive) {
            scale.snapTo(0.8f)
            glow.snapTo(0f)
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            glow.animateTo(1f, tween(durationMillis = 200))
            glow.animateTo(0f, tween(durationMillis = 400))
        } else {
            scale.snapTo(0.8f)
            glow.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .alpha(glow.value * 0.6f)
                    .clip(CircleShape)
                    .background(TheatreGold.copy(alpha = 0.5f))
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
                    .clip(RoundedCornerShape(24.dp))
                    .background(TheatreMidnightBlue),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = symbolDrawable(activeSymbolIndex)),
                    contentDescription = "Symbol ${activeSymbolIndex + 1}",
                    modifier = Modifier
                        .size(140.dp)
                        .padding(8.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(TheatreMidnightBlue.copy(alpha = 0.4f))
            )
        }
    }
}

@Composable
internal fun ProgressStrip(state: PatternState) {
    val total = state.sequence.size.coerceAtLeast(1)
    var stepCounter by remember(state.round) { mutableIntStateOf(0) }
    LaunchedEffect(state.round) { stepCounter = 0 }
    LaunchedEffect(state.activeSymbolIndex, state.round) {
        if (state.activeSymbolIndex in 0..6 && stepCounter < total) {
            stepCounter += 1
        }
    }
    val currentStep = stepCounter.coerceIn(0, total)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "STEP $currentStep OF $total",
            color = TheatreSilver,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until total) {
                val active = i < currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (active) TheatreGold else TheatreSilver.copy(alpha = 0.25f))
                )
            }
        }
    }
}

@Composable
internal fun PatternTopBar(state: PatternState) {
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
internal fun PatternJokerSection(state: PatternState) {
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
internal fun PatternScoreRow(state: PatternState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "YOU ${state.actScore + state.roundScore} pts",
            color = TheatreGold,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "MISTAKES ${state.mistakesMade}",
            color = TheatreSilver.copy(alpha = 0.85f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

internal fun symbolDrawable(index: Int): Int = when (index) {
    0 -> R.drawable.jok019_sym_1
    1 -> R.drawable.jok019_sym_2
    2 -> R.drawable.jok019_sym_3
    3 -> R.drawable.jok019_sym_4
    4 -> R.drawable.jok019_sym_5
    5 -> R.drawable.jok019_sym_6
    else -> R.drawable.jok019_sym_7
}
