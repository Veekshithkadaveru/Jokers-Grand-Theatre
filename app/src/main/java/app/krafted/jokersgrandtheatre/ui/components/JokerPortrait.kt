package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import app.krafted.jokersgrandtheatre.model.JokerExpression
import kotlinx.coroutines.delay

@Composable
fun JokerPortrait(
    expression: JokerExpression,
    modifier: Modifier = Modifier
) {
    var scaleTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(expression) {
        scaleTrigger = true
        delay(100)
        scaleTrigger = false
    }

    val scale by animateFloatAsState(
        targetValue = if (scaleTrigger) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "JokerPortraitScale"
    )

    Box(modifier = modifier.scale(scale)) {
        Image(
            painter = painterResource(id = expression.drawableRes),
            contentDescription = "Joker Expression: ${expression.name}"
        )
    }
}