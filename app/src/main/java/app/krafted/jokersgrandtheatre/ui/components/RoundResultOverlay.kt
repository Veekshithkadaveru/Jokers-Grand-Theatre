package app.krafted.jokersgrandtheatre.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.model.JokerExpression
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCream
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldHi

@Composable
fun RoundResultOverlay(
    isPlayerWin: Boolean,
    roundNumber: Int,
    jokerExpression: JokerExpression,
    dialogueLine: String,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC0A0002)),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                animationSpec = spring(
                    stiffness = 500f,
                    dampingRatio = 0.7f
                ),
                initialOffsetY = { it }
            )
        ) {
            OrnateFrame(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                padding = 24.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isPlayerWin)
                                    Brush.verticalGradient(
                                        listOf(TheatreGoldHi, TheatreGold, TheatreGoldDeep)
                                    )
                                else
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFFC92A1A),
                                            TheatreCrimsonDeep,
                                            Color(0xFF1A0002)
                                        )
                                    )
                            )
                            .padding(horizontal = 28.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isPlayerWin) "VICTORY" else "DEFEATED",
                            style = TextStyle(
                                fontFamily = CinzelFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                letterSpacing = 4.sp,
                                color = if (isPlayerWin) TheatreCrimsonDeep else TheatreGold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "ROUND $roundNumber",
                        style = TextStyle(
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            letterSpacing = 3.sp,
                            color = TheatreGold.copy(alpha = 0.75f)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    JokerPortrait(
                        expression = jokerExpression,
                        size = 72.dp,
                        accent = if (isPlayerWin) TheatreGold else Color(0xFFC92A1A),
                        glowing = true
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "“$dialogueLine”",
                        style = TextStyle(
                            fontFamily = PlayfairFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            color = TheatreCream,
                            lineHeight = 22.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(28.dp))

                    GoldButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "CONTINUE",
                            style = TextStyle(
                                fontFamily = CinzelFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 3.sp,
                                color = TheatreCrimsonDeep
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A0008)
@Composable
private fun RoundResultOverlayWinPreview() {
    RoundResultOverlay(
        isPlayerWin = true,
        roundNumber = 2,
        jokerExpression = JokerExpression.GLEEFUL,
        dialogueLine = "Bravo! You've out-worded the house this round.",
        onDismiss = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A0008)
@Composable
private fun RoundResultOverlayLossPreview() {
    RoundResultOverlay(
        isPlayerWin = false,
        roundNumber = 3,
        jokerExpression = JokerExpression.SINISTER,
        dialogueLine = "The house always wins in the end, my friend.",
        onDismiss = {}
    )
}
