package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


@Composable
fun SpotlightBackground(
    modifier: Modifier = Modifier,
    intensity: Float = 0.7f,
    centreX: Float = 0.5f,
    centreY: Float = 0.35f,
    radiusFraction: Float = 0.85f,
    pulseEnabled: Boolean = true,
    pulseDurationMs: Int = 4000
) {
    val pulseAlpha = if (pulseEnabled) {
        val infinite = rememberInfiniteTransition(label = "spotlightBg")
        val alpha by infinite.animateFloat(
            initialValue = 0.55f,
            targetValue = 0.75f,
            animationSpec = infiniteRepeatable(
                animation = tween(pulseDurationMs, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "spotlightBgAlpha"
        )
        alpha
    } else {
        0.65f
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width * centreX
        val cy = size.height * centreY
        val radius = size.width * radiusFraction

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = intensity * pulseAlpha * 0.4f),
                    Color.Black.copy(alpha = intensity * pulseAlpha * 0.7f),
                    Color.Black.copy(alpha = intensity * pulseAlpha)
                ),
                center = Offset(cx, cy),
                radius = radius
            )
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x0DFFC933),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = radius * 0.5f
            )
        )
    }
}
