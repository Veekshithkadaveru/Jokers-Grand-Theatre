package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold

@Composable
fun CurtainTransition(
    isOpen: Boolean,
    modifier: Modifier = Modifier,
    accent: Color = TheatreGold,
    animationDurationMs: Int = 1100,
    onAnimationFinished: () -> Unit = {}
) {
    val progress = remember { Animatable(if (isOpen) 1f else 0f) }

    LaunchedEffect(isOpen) {
        progress.animateTo(
            targetValue = if (isOpen) 1f else 0f,
            animationSpec = tween(durationMillis = animationDurationMs)
        )
        onAnimationFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val panelWidth = (w * 0.52f) * (1f - progress.value)

        if (panelWidth > 0f) {

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF2A0306), Color(0xFF6A0A0A), Color(0xFF3A0306)),
                    startX = 0f, endX = panelWidth
                ),
                size = Size(panelWidth, h)
            )
            drawRect(
                color = accent,
                topLeft = Offset(panelWidth - 6.dp.toPx(), 0f),
                size = Size(6.dp.toPx(), h)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF3A0306), Color(0xFF6A0A0A), Color(0xFF2A0306)),
                    startX = w - panelWidth, endX = w
                ),
                topLeft = Offset(w - panelWidth, 0f),
                size = Size(panelWidth, h)
            )
            drawRect(
                color = accent,
                topLeft = Offset(w - panelWidth, 0f),
                size = Size(6.dp.toPx(), h)
            )
        }

        val valanceH = 48.dp.toPx()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF7A1208), Color(0xFF3A0306)),
                startY = 0f, endY = valanceH
            ),
            size = Size(w, valanceH)
        )

        drawRect(
            color = accent,
            topLeft = Offset(0f, valanceH - 3.dp.toPx()),
            size = Size(w, 3.dp.toPx())
        )

        val numTassels = 12
        val tassel = 4.dp.toPx()
        val tassel_h = 14.dp.toPx()
        repeat(numTassels) { i ->
            val tx = (w / numTassels) * i + (w / numTassels / 2f) - tassel / 2f
            drawRect(
                color = accent,
                topLeft = Offset(tx, valanceH),
                size = Size(tassel, tassel_h)
            )
        }
    }
}
