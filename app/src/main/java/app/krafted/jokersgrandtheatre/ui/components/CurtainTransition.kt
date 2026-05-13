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
import androidx.compose.ui.graphics.Color

@Composable
fun CurtainTransition(
    isOpen: Boolean,
    modifier: Modifier = Modifier,
    animationDurationMs: Int = 800,
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
        val width = size.width
        val height = size.height

        val curtainColor = Color(0xFF8B0000)
        val trimColor = Color(0xFFFFD700)

        val curtainWidth = (width / 2) * (1f - progress.value)

        if (curtainWidth > 0) {
            drawRect(
                color = curtainColor,
                topLeft = Offset(0f, 0f),
                size = Size(curtainWidth, height)
            )
            drawRect(
                color = trimColor,
                topLeft = Offset(curtainWidth - 10f, 0f),
                size = Size(10f, height)
            )

            drawRect(
                color = curtainColor,
                topLeft = Offset(width - curtainWidth, 0f),
                size = Size(curtainWidth, height)
            )
            drawRect(
                color = trimColor,
                topLeft = Offset(width - curtainWidth, 0f),
                size = Size(10f, height)
            )
        }
    }
}