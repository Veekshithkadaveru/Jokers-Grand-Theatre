package app.krafted.jokersgrandtheatre.ui.actIII

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.ui.components.CinzelLabel
import app.krafted.jokersgrandtheatre.ui.components.DialogueBox
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.GoldButton
import app.krafted.jokersgrandtheatre.ui.components.OrnateFrame
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.viewmodel.GamblePhase
import app.krafted.jokersgrandtheatre.viewmodel.GambleState
import app.krafted.jokersgrandtheatre.viewmodel.GambleViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GambleRevealScreen(
    viewModel: GambleViewModel,
    onContinue: () -> Unit = {},
    onActComplete: (playerActScore: Int, playerWins: Int, jokerWins: Int) -> Unit = { _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val selected = state.selectedMask ?: 0
    val crownFound = selected == state.crownPosition

    val flip = remember { Animatable(0f) }
    val flash = remember { Animatable(0f) }
    val desaturate = remember { Animatable(0f) }
    val burst = remember { Animatable(0f) }
    val sigh = remember { Animatable(0f) }
    var revealComplete by remember { mutableStateOf(false) }

    LaunchedEffect(state.round, state.selectedMask) {
        if (state.selectedMask == null) return@LaunchedEffect
        flip.snapTo(0f); flash.snapTo(0f)
        desaturate.snapTo(0f); burst.snapTo(0f); sigh.snapTo(0f)
        revealComplete = false

        flip.animateTo(180f, tween(900, easing = LinearEasing))
        if (crownFound) {
            launch { burst.animateTo(1f, tween(1000, easing = FastOutSlowInEasing)) }
            repeat(3) {
                flash.animateTo(1f, tween(180))
                flash.animateTo(0f, tween(220))
            }
        } else {
            launch { sigh.animateTo(1f, tween(1200, easing = FastOutSlowInEasing)) }
            desaturate.animateTo(1f, tween(600))
        }
        delay(150L)
        revealComplete = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(R.drawable.jok019_back_4, tint = Color(0xC53A0A02))
        EmberParticles(density = 20, opacity = 0.45f)

        if (crownFound && burst.value > 0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCrownBurst(burst.value, ActAccent)
            }
        }

        if (flash.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ActAccent.copy(alpha = flash.value * 0.35f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            GambleTopBar(state)
            Spacer(Modifier.height(8.dp))
            GambleJokerStrip(state)
            Spacer(Modifier.height(10.dp))

            CinzelLabel(
                text = if (!revealComplete) "REVEALING..." else if (crownFound) "👑 CROWN FOUND" else "EMPTY",
                color = if (revealComplete && crownFound) ActAccent else Color(0xBFFFE7A8),
                fontSize = 13.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 3) {
                    if (i == selected) {
                        FlippingMask(
                            flipDegrees = flip.value,
                            crownFound = crownFound,
                            desaturate = desaturate.value,
                            sigh = sigh.value,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.72f)
                        )
                    } else {
                        StaticMask(
                            dimmed = revealComplete,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.72f)
                        )
                    }
                }
            }

            if (revealComplete) {
                Spacer(Modifier.height(12.dp))
                RoundResultPanel(
                    state = state,
                    crownFound = crownFound,
                    onContinue = {
                        if (state.phase == GamblePhase.ACT_END) {
                            viewModel.acknowledgeActEnd()
                            onActComplete(state.actScore, state.playerWins, state.jokerWins)
                        } else {
                            viewModel.nextRound()
                            onContinue()
                        }
                    }
                )
            } else {
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun FlippingMask(
    flipDegrees: Float,
    crownFound: Boolean,
    desaturate: Float,
    sigh: Float,
    modifier: Modifier = Modifier
) {
    val showFront = flipDegrees > 90f
    val bgBrush = if (showFront && crownFound)
        Brush.verticalGradient(listOf(Color(0xFF3A2800), Color(0xFF6B4200)))
    else
        Brush.verticalGradient(listOf(Color(0xFF2A0306), Color(0xFF5A0A0A)))
    val borderCol = if (showFront && crownFound) ActAccent else ActAccent.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = flipDegrees
                cameraDistance = 12f * density
                translationY = sigh * 14.dp.toPx()
            }
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .border(3.dp, borderCol, RoundedCornerShape(14.dp))
            .padding(if (showFront) 8.dp else 0.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!showFront) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, ActAccent, CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0x1AFFFFFF), Color.Transparent))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "?",
                    fontFamily = CinzelDecorativeFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp,
                    color = ActAccent,
                    style = TextStyle(shadow = Shadow(ActAccent.copy(0.5f), Offset.Zero, 8f))
                )
            }
        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
                contentAlignment = Alignment.Center
            ) {
                if (crownFound) {
                    Image(
                        painter = painterResource(id = R.drawable.jok019_sym_7),
                        contentDescription = "Crown",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.jok019_sym_4),
                        contentDescription = "Empty",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .alpha(1f - desaturate * 0.4f),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply { setToSaturation(1f - desaturate) }
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StaticMask(dimmed: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .alpha(if (dimmed) 0.4f else 0.85f)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF2A0306), Color(0xFF5A0A0A))))
            .border(
                3.dp,
                ActAccent.copy(alpha = if (dimmed) 0.33f else 0.7f),
                RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, ActAccent.copy(alpha = if (dimmed) 0.33f else 0.7f), CircleShape)
                .background(Brush.radialGradient(listOf(Color(0x1AFFFFFF), Color.Transparent))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?",
                fontFamily = CinzelDecorativeFamily,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                color = ActAccent.copy(alpha = if (dimmed) 0.33f else 0.7f)
            )
        }
    }
}

@Composable
private fun RoundResultPanel(
    state: GambleState,
    crownFound: Boolean,
    onContinue: () -> Unit
) {
    val actEnding = state.phase == GamblePhase.ACT_END
    val playerWonAct = state.playerWins > state.jokerWins
    val title = when {
        actEnding && playerWonAct -> "YOU WIN THE ACT"
        actEnding && !playerWonAct -> "JOKER WINS THE ACT"
        actEnding -> "A DRAW"
        crownFound -> "Crown Found!"
        else -> "Empty Mask"
    }
    val tone = when {
        (actEnding && playerWonAct) || (!actEnding && crownFound) -> Color(0xFF7CD34E)
        else -> Color(0xFFFF5A3A)
    }
    var fired by remember { mutableStateOf(false) }

    OrnateFrame(accent = ActAccent) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CinzelLabel(
                "· RESULT ·",
                color = Color(0x8CFFE7A8),
                fontSize = 9.sp,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                fontFamily = CinzelDecorativeFamily,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = tone,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Round score  ${formatStakes(state.roundScore)}",
                color = tone,
                fontFamily = CinzelFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Total  ${formatStakes(state.actScore)}",
                color = Color(0xBFFFE7A8),
                fontFamily = CinzelFamily,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Rounds  You ${state.playerWins}  —  ${state.jokerWins} Joker",
                color = Color(0xBFFFE7A8),
                fontFamily = CinzelFamily,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(12.dp))
            DialogueBox(
                expression = state.jokerExpression,
                accent = ActAccent,
                text = state.jokerLine,
                portraitSize = 48.dp
            )
            Spacer(Modifier.height(12.dp))
            GoldButton(
                onClick = {
                    if (!fired) {
                        fired = true; onContinue()
                    }
                }
            ) {
                Text(
                    text = if (actEnding) "FINISH ACT" else "NEXT ROUND",
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 2.5.sp,
                    color = TheatreCrimsonDeep
                )
            }
        }
    }
}

private fun DrawScope.drawCrownBurst(progress: Float, color: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxRadius = size.width * 1.2f
    repeat(20) { i ->
        val angle = (i * (2.0 * PI / 20)).toFloat()
        val alpha = (1f - progress) * 0.7f
        val len = maxRadius * progress
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(cx, cy),
            end = Offset(cx + cos(angle) * len, cy + sin(angle) * len),
            strokeWidth = 2.5f
        )
    }
}
