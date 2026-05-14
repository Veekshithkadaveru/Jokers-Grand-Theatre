package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.krafted.jokersgrandtheatre.model.JokerExpression
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import kotlinx.coroutines.delay

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height

@Composable
fun JokerPortrait(
    expression: JokerExpression,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    accent: Color = TheatreGold,
    glowing: Boolean = false
) {
    var scaleTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(expression) {
        scaleTrigger = true
        delay(80)
        scaleTrigger = false
    }

    val scale by animateFloatAsState(
        targetValue = if (scaleTrigger) 1.18f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "JokerPortraitScale"
    )

    val colorFilter = remember(expression) {
        when (expression) {
            JokerExpression.SINISTER -> ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToSaturation(0.65f)
                }
            )
            JokerExpression.UNHINGED -> ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToSaturation(1.4f)
                }
            )
            JokerExpression.TRIUMPHANT, JokerExpression.GLEEFUL -> ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToSaturation(1.3f)
                }
            )
            else -> null
        }
    }

    Box(
        modifier = modifier
            .width(size)
            .height(size * 1.25f)
            .scale(scale)
    ) {

        if (glowing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.4f), Color.Transparent)
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF5A0A0A), Color(0xFF1A0306))
                    )
                )
                .border(3.dp, accent, CircleShape)
        ) {
            Image(
                painter = painterResource(id = expression.drawableRes),
                contentDescription = "Joker",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentScale = ContentScale.Fit,
                colorFilter = colorFilter
            )
        }
    }
}
