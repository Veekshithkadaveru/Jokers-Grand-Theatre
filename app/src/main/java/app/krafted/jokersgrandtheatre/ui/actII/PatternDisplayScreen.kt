package app.krafted.jokersgrandtheatre.ui.actII

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.ui.components.CinzelLabel
import app.krafted.jokersgrandtheatre.ui.components.JokerPortrait
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.viewmodel.PatternState

private val ActAccent = Color(0xFFC0C0C0)

@Composable
internal fun ActiveSymbolDisplay(activeSymbolIndex: Int) {
    val isActive = activeSymbolIndex in 0..6
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(activeSymbolIndex) {
        if (isActive) {
            scale.snapTo(0.3f)
            scale.animateTo(1.2f, tween(150))
            scale.animateTo(1.0f, tween(120))
        } else {
            scale.snapTo(0.8f)
        }
    }

    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
            )
            Image(
                painter = painterResource(id = symbolDrawable(activeSymbolIndex)),
                contentDescription = "Symbol ${activeSymbolIndex + 1}",
                modifier = Modifier
                    .size(170.dp)
                    .scale(scale.value),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, ActAccent.copy(alpha = 0.27f), RoundedCornerShape(20.dp))
                    .background(Color(0x1AFFFFFF))
            )
        }
    }
}

@Composable
internal fun ProgressStrip(state: PatternState) {
    val total = state.sequence.size.coerceAtLeast(1)
    var stepCounter by remember(state.round) { mutableIntStateOf(0) }
    LaunchedEffect(state.activeSymbolIndex, state.round) {
        if (state.activeSymbolIndex in 0..6 && stepCounter < total) {
            stepCounter = (stepCounter + 1).coerceAtMost(total)
        }
    }

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
            CinzelLabel("WATCHING", color = ActAccent, fontSize = 10.sp, letterSpacing = 2.sp)
            CinzelLabel("$stepCounter / $total", color = Color(0xA6FFE7A8), fontSize = 10.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (i in 0 until total) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (i < stepCounter) ActAccent else Color(0x1FFFFFFF))
                )
            }
        }
    }
}

@Composable
internal fun PatternTopBar(state: PatternState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x8C000000), RoundedCornerShape(8.dp))
            .border(1.dp, ActAccent.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ACT II",
            fontFamily = CinzelDecorativeFamily,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = ActAccent,
            style = TextStyle(shadow = Shadow(Color.Black, Offset(0f, 1f), 0f))
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
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                i < state.playerRoundsWon -> Color(0xFF7CD34E)
                                i >= state.playerRoundsWon && i < state.playerRoundsWon + state.jokerRoundsWon -> Color(0xFFC92A1A)
                                i == state.playerRoundsWon + state.jokerRoundsWon -> ActAccent
                                else -> Color(0x1FFFFFFF)
                            }
                        )
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = state.actScore.toString(),
            fontFamily = CinzelDecorativeFamily,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = ActAccent,
            style = TextStyle(shadow = Shadow(ActAccent.copy(alpha = 0.4f), Offset.Zero, 6f))
        )
    }
}

@Composable
internal fun PatternJokerStrip(state: PatternState) {
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

internal fun symbolDrawable(index: Int): Int = when (index) {
    0 -> R.drawable.jok019_sym_1
    1 -> R.drawable.jok019_sym_2
    2 -> R.drawable.jok019_sym_3
    3 -> R.drawable.jok019_sym_4
    4 -> R.drawable.jok019_sym_5
    5 -> R.drawable.jok019_sym_6
    else -> R.drawable.jok019_sym_7
}
