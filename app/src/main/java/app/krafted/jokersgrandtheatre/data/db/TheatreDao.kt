package app.krafted.jokersgrandtheatre.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TheatreDao {
    @Query("SELECT * FROM act_scores WHERE actName = :actName LIMIT 1")
    suspend fun getBestScore(actName: String): ActScoreEntity?

    @Query("SELECT * FROM act_scores")
    suspend fun getAllBestScores(): List<ActScoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertScore(entity: ActScoreEntity)

    suspend fun saveBestScore(actName: String, newScore: Int) {
        val current = getBestScore(actName)
        if (current == null || newScore > current.bestScore) {
            upsertScore(ActScoreEntity(actName = actName, bestScore = newScore))
        }
    }
}
