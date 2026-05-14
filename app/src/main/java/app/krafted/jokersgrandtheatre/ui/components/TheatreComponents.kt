package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldHi
import app.krafted.jokersgrandtheatre.ui.theme.TheatreInk
import app.krafted.jokersgrandtheatre.ui.theme.TheatreInkDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreVelvet
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun StageBackground(
    backgroundRes: Int,
    tint: Color = Color(0xB3050102),
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tint)
        )
        SpotlightVignette()
    }
}

@Composable
fun SpotlightVignette(intensity: Float = 0.7f) {
    val infinite = rememberInfiniteTransition(label = "spotlight")
    val alpha by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spotlightAlpha"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = intensity * alpha)
                ),
                center = Offset(size.width * 0.5f, size.height * 0.35f),
                radius = size.width * 0.85f
            )
        )
    }
}

private data class Ember(
    val startX: Float,
    val size: Float,
    val duration: Int,
    val delay: Int,
    val drift: Float,
    val color: Color
)

@Composable
fun EmberParticles(density: Int = 18, opacity: Float = 0.5f) {
    val embers = remember(density) {
        List(density) {
            Ember(
                startX = Random.nextFloat(),
                size = 2f + Random.nextFloat() * 4f,
                duration = (7000 + Random.nextFloat() * 8000).toInt(),
                delay = -(Random.nextFloat() * 12000).toInt(),
                drift = (-24f + Random.nextFloat() * 48f),
                color = if (Random.nextBoolean()) Color(0xFFFFAE3A) else Color(0xFFFFD773)
            )
        }
    }
    val infinite = rememberInfiniteTransition(label = "embers")
    val progress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing)
        ),
        label = "emberProgress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        embers.forEach { ember ->
            val t = ((progress + (ember.delay / 12000f + 1f)) % 1f)
            val x = ember.startX * size.width + ember.drift * t
            val y = size.height * (1f - t)
            val emberAlpha = when {
                t < 0.1f -> t / 0.1f
                t > 0.9f -> (1f - t) / 0.1f
                else -> 1f
            } * opacity
            drawCircle(
                color = ember.color.copy(alpha = emberAlpha),
                radius = ember.size,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun OrnateFrame(
    modifier: Modifier = Modifier,
    accent: Color = TheatreGold,
    padding: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A0306), Color(0xFF4A0008), Color(0xFF2A0306))
                ),
                RoundedCornerShape(10.dp)
            )
            .border(2.dp, accent, RoundedCornerShape(10.dp))
            .padding(padding)
    ) {
        content()

        listOf(
            Alignment.TopStart, Alignment.TopEnd,
            Alignment.BottomStart, Alignment.BottomEnd
        ).forEach { alignment ->
            val dx = if (alignment == Alignment.TopStart || alignment == Alignment.BottomStart) (-5).dp else 5.dp
            val dy = if (alignment == Alignment.TopStart || alignment == Alignment.TopEnd) (-5).dp else 5.dp
            Box(
                modifier = Modifier
                    .align(alignment)
                    .offset(dx, dy)
                    .width(10.dp)
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
        }
    }
}

@Composable
fun GoldButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fill: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .then(if (fill) Modifier.fillMaxSize() else Modifier)
            .height(54.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (enabled) Brush.verticalGradient(
                    listOf(TheatreGoldHi, TheatreGold, TheatreGoldDeep)
                ) else Brush.verticalGradient(
                    listOf(
                        TheatreGoldHi.copy(alpha = 0.4f),
                        TheatreGold.copy(alpha = 0.4f),
                        TheatreGoldDeep.copy(alpha = 0.4f)
                    )
                )
            )
            .border(2.dp, TheatreCrimsonDeep, RoundedCornerShape(6.dp))
            .then(
                if (enabled) Modifier.background(Color.Transparent)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                contentColor = TheatreCrimsonDeep,
                disabledContentColor = TheatreCrimsonDeep.copy(alpha = 0.5f)
            ),
            elevation = null
        ) {
            content()
        }
    }
}

@Composable
fun TheatricalTitle(
    text: String,
    size: TextUnit = 32.sp,
    accent: Color = TheatreGold,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = CinzelDecorativeFamily,
            fontWeight = FontWeight.Black,
            fontSize = size,
            brush = Brush.verticalGradient(
                listOf(TheatreGoldHi, accent, TheatreGoldDeep)
            ),
            letterSpacing = if (size.value > 28f) 3.sp else 2.sp
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun CinzelLabel(
    text: String,
    color: Color = TheatreGold,
    fontSize: TextUnit = 10.sp,
    letterSpacing: TextUnit = 2.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontFamily = CinzelFamily,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        modifier = modifier
    )
}

@Composable
fun CountUp(
    value: Int,
    durationMs: Int = 900,
    style: TextStyle = TextStyle.Default,
    modifier: Modifier = Modifier
) {
    val animated by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = durationMs),
        label = "CountUp"
    )
    Text(
        text = animated.toString().reversed().chunked(3).joinToString(",").reversed(),
        style = style,
        modifier = modifier
    )
}

@Composable
fun SparkleBurst(
    color: Color = TheatreGold,
    count: Int = 12,
    size: Float = 300f,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "sparkle")
    val scale by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleScale"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        repeat(count) { i ->
            val angle = (i * (360f / count)) * (Math.PI / 180f).toFloat()
            val len = size * scale
            val alpha = (1f - scale) * 0.8f
            drawLine(
                color = color.copy(alpha = alpha),
                start = Offset(cx, cy),
                end = Offset(cx + cos(angle) * len, cy + sin(angle) * len),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
fun Curtain(
    isOpen: Boolean,
    accent: Color = TheatreGold,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val panelWidth = w * 0.52f * if (isOpen) 0f else 1f

        if (panelWidth > 0f) {

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF2A0306), Color(0xFF6A0A0A), Color(0xFF3A0306)),
                    startX = 0f, endX = panelWidth
                ),
                size = androidx.compose.ui.geometry.Size(panelWidth, h)
            )

            drawRect(
                color = accent,
                topLeft = Offset(panelWidth - 6f, 0f),
                size = androidx.compose.ui.geometry.Size(6f, h)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF3A0306), Color(0xFF6A0A0A), Color(0xFF2A0306)),
                    startX = w - panelWidth, endX = w
                ),
                topLeft = Offset(w - panelWidth, 0f),
                size = androidx.compose.ui.geometry.Size(panelWidth, h)
            )

            drawRect(
                color = accent,
                topLeft = Offset(w - panelWidth, 0f),
                size = androidx.compose.ui.geometry.Size(6f, h)
            )
        }

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF7A1208), Color(0xFF3A0306)),
                startY = 0f, endY = 48.dp.toPx()
            ),
            size = androidx.compose.ui.geometry.Size(w, 48.dp.toPx())
        )
        drawRect(
            color = accent,
            topLeft = Offset(0f, 48.dp.toPx() - 3.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(w, 3.dp.toPx())
        )
    }
}
