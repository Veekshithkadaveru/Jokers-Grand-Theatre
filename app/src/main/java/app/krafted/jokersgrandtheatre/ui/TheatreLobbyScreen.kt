package app.krafted.jokersgrandtheatre.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.model.Act
import app.krafted.jokersgrandtheatre.ui.components.CinzelLabel
import app.krafted.jokersgrandtheatre.ui.components.CountUp
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.GoldButton
import app.krafted.jokersgrandtheatre.ui.components.OrnateFrame
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.components.TheatricalTitle
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCream
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class ActEntry(
    val act: Act,
    val numeral: String,
    val mechanic: String,
    val moodName: String,
    val desc: String,
    val accent: Color,
    val bestScore: Int
)

private val LobbyBackground = Color(0xB3080203)

@Composable
fun TheatreLobbyScreen(
    bestActIScore: Int,
    bestActIIScore: Int,
    bestActIIIScore: Int,
    onPlayAct: (Act) -> Unit,
    onPlayAll: () -> Unit,
    onLeaderboard: () -> Unit
) {
    val acts = listOf(
        ActEntry(
            act = Act.ACT_I,
            numeral = "I",
            mechanic = "Word Duel",
            moodName = "Theatrical",
            desc = "Build longer words than the Joker on a 5×5 grid.",
            accent = TheatreGold,
            bestScore = bestActIScore
        ),
        ActEntry(
            act = Act.ACT_II,
            numeral = "II",
            mechanic = "Pattern Mirror",
            moodName = "Sinister",
            desc = "Watch a sequence, then tap it back in reverse.",
            accent = Color(0xFFC0C0C0),
            bestScore = bestActIIScore
        ),
        ActEntry(
            act = Act.ACT_III,
            numeral = "III",
            mechanic = "Final Gamble",
            moodName = "Unhinged",
            desc = "Three masks. One crown. The Joker misdirects.",
            accent = Color(0xFFFF7A18),
            bestScore = bestActIIIScore
        )
    )
    val grandTotal = bestActIScore + bestActIIScore + bestActIIIScore

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(R.drawable.jok019_back_1, tint = LobbyBackground)
        EmberParticles(density = 20, opacity = 0.4f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 24.dp)
        ) {
            LobbyHeader()
            Spacer(Modifier.height(18.dp))
            GrandTotalCard(total = grandTotal, onLeaderboard = onLeaderboard)
            Spacer(Modifier.height(16.dp))
            acts.forEachIndexed { index, entry ->
                if (index > 0) Spacer(Modifier.height(12.dp))
                ActCard(entry = entry, index = index, onClick = { onPlayAct(entry.act) })
            }
            Spacer(Modifier.height(18.dp))
            GoldButton(onClick = onPlayAll, fill = false, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Play All Three Acts",
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.5.sp,
                    color = TheatreCrimsonDeep
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = "\"The curtain rises for those who dare. The Joker awaits.\"",
                fontFamily = PlayfairFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = Color(0x73FFE7A8),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LobbyHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "· TONIGHT'S PROGRAMME ·",
            fontFamily = PlayfairFamily,
            fontStyle = FontStyle.Italic,
            fontSize = 10.sp,
            letterSpacing = 4.sp,
            color = Color(0xB3E8D29A)
        )
        Spacer(Modifier.height(2.dp))
        TheatricalTitle(text = "The Lobby", size = 28.sp)
    }
}

@Composable
private fun GrandTotalCard(total: Int, onLeaderboard: () -> Unit) {
    OrnateFrame(modifier = Modifier.fillMaxWidth(), padding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFF3A6),
                                TheatreGold,
                                Color(0xFFA86B07)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👑", fontSize = 26.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                CinzelLabel(
                    text = "BEST GRAND TOTAL",
                    color = Color(0xB3FFE7A8),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
                CountUp(
                    value = total,
                    style = TextStyle(
                        fontFamily = CinzelDecorativeFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        color = TheatreGold,
                        shadow = Shadow(TheatreCrimsonDeep, Offset(0f, 2f), 10f)
                    )
                )
                Text(
                    text = "across three acts",
                    fontFamily = PlayfairFamily,
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    color = Color(0x8CFFE7A8)
                )
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, TheatreGold, RoundedCornerShape(6.dp))
                    .clickable(onClick = onLeaderboard)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RECORDS ›",
                    fontFamily = CinzelFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    color = TheatreGold
                )
            }
        }
    }
}

@Composable
private fun ActCard(entry: ActEntry, index: Int, onClick: () -> Unit) {
    val entrance = remember(entry.act) { Animatable(0f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(entry.act) {
        scope.launch {
            delay(index * 80L)
            entrance.animateTo(1f, tween(durationMillis = 420))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(entrance.value)
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val dy = ((1f - entrance.value) * 12.dp.toPx()).toInt()
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, dy)
                }
            }
            .clickable(onClick = onClick)
    ) {
        OrnateFrame(
            modifier = Modifier.fillMaxWidth(),
            accent = entry.accent,
            padding = 0.dp
        ) {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF4A0008), Color(0xFF2A0306))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.numeral,
                        fontFamily = CinzelDecorativeFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp,
                        color = entry.accent,
                        style = TextStyle(shadow = Shadow(Color.Black, Offset(0f, 2f), 14f))
                    )
                    Text(
                        text = "ACT",
                        fontFamily = CinzelFamily,
                        fontSize = 7.sp,
                        letterSpacing = 1.5.sp,
                        color = Color(0x73FFFFFF),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(2.dp)
                            .fillMaxSize()
                            .background(entry.accent)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = entry.mechanic,
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp,
                        color = TheatreCream
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = entry.moodName,
                        fontFamily = CinzelFamily,
                        fontSize = 9.sp,
                        letterSpacing = 2.sp,
                        color = entry.accent
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = entry.desc,
                        fontFamily = PlayfairFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color(0xBFFFE7A8)
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "BEST",
                            fontFamily = CinzelFamily,
                            fontSize = 9.sp,
                            letterSpacing = 1.2.sp,
                            color = Color(0x80FFE7A8)
                        )
                        Text(
                            text = entry.bestScore.toString()
                                .reversed().chunked(3).joinToString(",").reversed(),
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = entry.accent
                        )
                        Spacer(Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(entry.accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "›",
                                fontFamily = CinzelFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = TheatreCrimsonDeep
                            )
                        }
                    }
                }
            }
        }
    }
}
