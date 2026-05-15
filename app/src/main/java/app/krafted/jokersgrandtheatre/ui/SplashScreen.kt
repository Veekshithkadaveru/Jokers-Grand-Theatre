package app.krafted.jokersgrandtheatre.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.SpotlightBackground
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldHi
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    var phase by remember { mutableStateOf(SplashPhase.CURTAIN_CLOSED) }

    val curtainProgress = remember { Animatable(0f) }

    val titleAlpha = remember { Animatable(0f) }

    val portraitScale = remember { Animatable(0.3f) }
    val portraitAlpha = remember { Animatable(0f) }

    val subtitleAlpha = remember { Animatable(0f) }

    val tapAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(400)

        phase = SplashPhase.CURTAIN_RISING
        curtainProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )

        phase = SplashPhase.CONTENT_REVEAL
        delay(100)

        portraitAlpha.animateTo(1f, tween(400))
        portraitScale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        titleAlpha.animateTo(1f, tween(600))
        delay(200)

        subtitleAlpha.animateTo(1f, tween(500))
        delay(200)

        tapAlpha.animateTo(1f, tween(400))

        delay(1000)
        onFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splashGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050102))
    ) {
        Image(
            painter = painterResource(id = R.drawable.jok019_back_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC050102))
        )

        SpotlightBackground(intensity = 0.75f)

        if (phase != SplashPhase.CURTAIN_CLOSED) {
            EmberParticles(density = 15, opacity = 0.35f)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .scale(portraitScale.value)
                    .alpha(portraitAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(148.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    TheatreGold.copy(alpha = glowAlpha * 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF5A0A0A), Color(0xFF1A0306))
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jok019_sym_1),
                        contentDescription = "The Joker",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "JOKER'S\nGRAND THEATRE",
                modifier = Modifier.alpha(titleAlpha.value),
                style = TextStyle(
                    fontFamily = CinzelDecorativeFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    brush = Brush.verticalGradient(
                        listOf(TheatreGoldHi, TheatreGold, TheatreGoldDeep)
                    ),
                    letterSpacing = 3.sp,
                    shadow = Shadow(
                        TheatreCrimsonDeep,
                        Offset(0f, 4f),
                        blurRadius = 16f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "\"Three acts. One night. The Joker awaits.\"",
                modifier = Modifier.alpha(subtitleAlpha.value),
                style = TextStyle(
                    fontFamily = PlayfairFamily,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color(0x99FFE7A8),
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            Text(
                text = "· ENTERING THE THEATRE ·",
                modifier = Modifier.alpha(tapAlpha.value * glowAlpha),
                style = TextStyle(
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    letterSpacing = 4.sp,
                    color = TheatreGold.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
        }

        CurtainRiseOverlay(progress = curtainProgress.value)
    }
}

@Composable
private fun CurtainRiseOverlay(progress: Float) {
    if (progress >= 1f) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val curtainHeight = h * (1f - progress)

        if (curtainHeight > 0f) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2A0306),
                        Color(0xFF6A0A0A),
                        Color(0xFF4A0008),
                        Color(0xFF3A0306)
                    ),
                    startX = 0f,
                    endX = w * 0.52f
                ),
                topLeft = Offset(0f, 0f),
                size = Size(w * 0.52f, curtainHeight)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF3A6),
                        Color(0xFFFFC933),
                        Color(0xFFA86B07)
                    )
                ),
                topLeft = Offset(w * 0.52f - 6f, 0f),
                size = Size(6f, curtainHeight)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF3A0306),
                        Color(0xFF4A0008),
                        Color(0xFF6A0A0A),
                        Color(0xFF2A0306)
                    ),
                    startX = w * 0.48f,
                    endX = w
                ),
                topLeft = Offset(w * 0.48f, 0f),
                size = Size(w * 0.52f, curtainHeight)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF3A6),
                        Color(0xFFFFC933),
                        Color(0xFFA86B07)
                    )
                ),
                topLeft = Offset(w * 0.48f, 0f),
                size = Size(6f, curtainHeight)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFA86B07),
                        Color(0xFFFFC933),
                        Color(0xFFFFF3A6),
                        Color(0xFFFFC933),
                        Color(0xFFA86B07)
                    )
                ),
                topLeft = Offset(0f, curtainHeight - 4f),
                size = Size(w, 4f)
            )

            val numTassels = 14
            val tasselWidth = 5f
            val tasselHeight = 16f
            repeat(numTassels) { i ->
                val tx = (w / numTassels) * i + (w / numTassels / 2f) - tasselWidth / 2f
                drawRect(
                    color = Color(0xFFFFC933),
                    topLeft = Offset(tx, curtainHeight),
                    size = Size(tasselWidth, tasselHeight)
                )
            }

            drawRect(
                color = Color(0x33000000),
                topLeft = Offset(w * 0.49f, 0f),
                size = Size(w * 0.02f, curtainHeight)
            )

            val foldCount = 8
            repeat(foldCount) { i ->
                val foldX = (w * 0.52f / foldCount) * i
                drawRect(
                    color = Color(0x1A000000),
                    topLeft = Offset(foldX, 0f),
                    size = Size(2f, curtainHeight)
                )
                drawRect(
                    color = Color(0x1A000000),
                    topLeft = Offset(w - foldX - 2f, 0f),
                    size = Size(2f, curtainHeight)
                )
            }
        }
    }
}

private enum class SplashPhase {
    CURTAIN_CLOSED,
    CURTAIN_RISING,
    CONTENT_REVEAL
}
