package app.krafted.jokersgrandtheatre.ui.actIII

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.ui.components.CinzelLabel
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.JokerPortrait
import app.krafted.jokersgrandtheatre.ui.components.OrnateFrame
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.viewmodel.GamblePhase
import app.krafted.jokersgrandtheatre.viewmodel.GambleState
import app.krafted.jokersgrandtheatre.viewmodel.GambleViewModel

internal val ActAccent = Color(0xFFFF7A18)
private val ActBg = Color(0xC53A0A02)

@Composable
fun GambleScreen(
    viewModel: GambleViewModel,
    onReveal: () -> Unit = {},
    onActComplete: (playerActScore: Int, playerWins: Int, jokerWins: Int) -> Unit = { _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.phase) {
        when (state.phase) {
            GamblePhase.REVEALING -> onReveal()
            GamblePhase.ACT_END -> onActComplete(state.actScore, state.playerWins, state.jokerWins)
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(R.drawable.jok019_back_4, tint = ActBg)
        EmberParticles(density = 20, opacity = 0.45f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            GambleTopBar(state)
            Spacer(Modifier.height(8.dp))
            GambleJokerStrip(state)
            Spacer(Modifier.height(12.dp))
            MisdirectionCard(state.misdirectionLine)
            Spacer(Modifier.height(12.dp))
            StakesDisplay(state.stakes)
            Spacer(Modifier.height(20.dp))
            CinzelLabel(
                text = "CHOOSE A MASK",
                color = Color(0xBFFFE7A8),
                fontSize = 12.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                fontWeight = FontWeight.Bold
            )
            MaskRow(
                enabled = state.phase == GamblePhase.CHOOSING,
                selected = state.selectedMask,
                onSelect = viewModel::selectMask,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
    }
}

@Composable
internal fun GambleTopBar(state: GambleState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x8C000000), RoundedCornerShape(8.dp))
            .border(1.dp, ActAccent.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ACT III",
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
                                i < state.playerWins -> Color(0xFF7CD34E)
                                i >= state.playerWins && i < state.playerWins + state.jokerWins -> Color(0xFFC92A1A)
                                i == state.playerWins + state.jokerWins -> ActAccent
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
internal fun GambleJokerStrip(state: GambleState) {
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
private fun MisdirectionCard(line: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F0F00))
            .border(1.dp, ActAccent.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "\"$line\"",
            color = Color(0xF2FFE7A8),
            fontFamily = PlayfairFamily,
            fontStyle = FontStyle.Italic,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun StakesDisplay(stakes: Int) {
    OrnateFrame(accent = ActAccent) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                CinzelLabel("STAKES", color = Color(0x99FFE7A8), fontSize = 9.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${formatStakes(stakes)} pts",
                    fontFamily = CinzelDecorativeFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = ActAccent
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text("×1.5", color = Color(0xFF7CD34E), fontFamily = CinzelFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.width(6.dp))
                Text("or", color = Color(0x73FFE7A8), fontFamily = PlayfairFamily, fontStyle = FontStyle.Italic, fontSize = 11.sp)
                Spacer(Modifier.width(6.dp))
                Text("×0.5", color = Color(0xFFFF5A3A), fontFamily = CinzelFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun MaskRow(
    enabled: Boolean,
    selected: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 3) {
            MaskCard(
                index = i,
                enabled = enabled,
                isSelected = selected == i,
                onClick = { onSelect(i) },
                modifier = Modifier.weight(1f).aspectRatio(0.72f)
            )
        }
    }
}

@Composable
private fun MaskCard(
    index: Int,
    enabled: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFFFFF3A6) else ActAccent
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF2A0306), Color(0xFF5A0A0A)))
            )
            .border(3.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, ActAccent, CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0x1AFFFFFF), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "?",
                    fontFamily = CinzelDecorativeFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp,
                    color = ActAccent,
                    style = TextStyle(shadow = Shadow(ActAccent.copy(alpha = 0.5f), Offset.Zero, 8f))
                )
            }
            Spacer(Modifier.height(12.dp))
            CinzelLabel(
                text = positionLabel(index),
                color = ActAccent,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

internal fun positionLabel(index: Int): String = when (index) {
    0 -> "LEFT"
    1 -> "CENTRE"
    else -> "RIGHT"
}

internal fun maskDrawable(index: Int): Int = R.drawable.jok019_sym_4

internal fun crownDrawable(): Int = R.drawable.jok019_sym_7

internal fun formatStakes(value: Int): String {
    val s = value.toString()
    return s.reversed().chunked(3).joinToString(",").reversed()
}
