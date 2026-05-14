package app.krafted.jokersgrandtheatre.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "act_scores")
data class ActScoreEntity(
    @PrimaryKey val actName: String,
    val bestScore: Int,
    val achievedAt: Long = System.currentTimeMillis()
)
