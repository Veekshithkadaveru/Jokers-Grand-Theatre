package app.krafted.jokersgrandtheatre.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersgrandtheatre.R
import app.krafted.jokersgrandtheatre.data.db.ActScoreEntity
import app.krafted.jokersgrandtheatre.ui.components.CinzelLabel
import app.krafted.jokersgrandtheatre.ui.components.EmberParticles
import app.krafted.jokersgrandtheatre.ui.components.GoldButton
import app.krafted.jokersgrandtheatre.ui.components.OrnateFrame
import app.krafted.jokersgrandtheatre.ui.components.StageBackground
import app.krafted.jokersgrandtheatre.ui.components.TheatricalTitle
import app.krafted.jokersgrandtheatre.ui.theme.CinzelDecorativeFamily
import app.krafted.jokersgrandtheatre.ui.theme.CinzelFamily
import app.krafted.jokersgrandtheatre.ui.theme.PlayfairFamily
import app.krafted.jokersgrandtheatre.ui.theme.TheatreCrimsonDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldDeep
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGoldHi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class ActRecord(
    val actName: String,
    val displayName: String,
    val accent: Color,
    val entity: ActScoreEntity?
)

@Composable
fun LeaderboardScreen(
    bestScores: List<ActScoreEntity>,
    onBack: () -> Unit
) {
    val scoreMap = bestScores.associateBy { it.actName }

    val records = listOf(
        ActRecord("actI", "Act I · Word Duel", TheatreGold, scoreMap["actI"]),
        ActRecord("actII", "Act II · Pattern Mirror", Color(0xFFC0C0C0), scoreMap["actII"]),
        ActRecord("actIII", "Act III · Final Gamble", Color(0xFFFF7A18), scoreMap["actIII"])
    )

    Box(modifier = Modifier.fillMaxSize()) {
        StageBackground(R.drawable.jok019_back_1, tint = Color(0xB3080203))
        EmberParticles(density = 14, opacity = 0.35f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "· HALL OF RECORDS ·",
                fontFamily = PlayfairFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 10.sp,
                letterSpacing = 4.sp,
                color = Color(0xB3E8D29A)
            )
            Spacer(Modifier.height(4.dp))
            TheatricalTitle(text = "Records", size = 30.sp)

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = TheatreGold.copy(alpha = 0.5f)
            )

            Spacer(Modifier.height(20.dp))

            records.forEachIndexed { index, record ->
                if (index > 0) Spacer(Modifier.height(14.dp))
                ActRecordRow(record = record)
            }

            Spacer(Modifier.height(28.dp))

            GoldButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "BACK TO LOBBY",
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = TheatreCrimsonDeep
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ActRecordRow(record: ActRecord) {
    OrnateFrame(
        modifier = Modifier.fillMaxWidth(),
        accent = record.accent,
        padding = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CinzelLabel(
                text = record.displayName.uppercase(),
                color = record.accent,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))

            if (record.entity != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = record.entity.playerName.uppercase(),
                            fontFamily = PlayfairFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp,
                            color = Color(0xB3E8D29A)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = record.entity.bestScore
                                    .toString()
                                    .reversed()
                                    .chunked(3)
                                    .joinToString(",")
                                    .reversed(),
                                style = TextStyle(
                                    fontFamily = CinzelDecorativeFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 40.sp,
                                    brush = Brush.verticalGradient(
                                        listOf(TheatreGoldHi, TheatreGold, TheatreGoldDeep)
                                    ),
                                    letterSpacing = 1.sp,
                                    textAlign = TextAlign.Start
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            CinzelLabel(
                                text = "pts",
                                color = TheatreGold.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        CinzelLabel(
                            text = "ACHIEVED",
                            color = Color(0x80FFE7A8),
                            fontSize = 8.sp,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        CinzelLabel(
                            text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                                .format(Date(record.entity.achievedAt)),
                            color = Color(0xBFFFE7A8),
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = "No record yet",
                    fontFamily = PlayfairFamily,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = Color(0x59FFE7A8)
                )
            }
        }
    }
}
