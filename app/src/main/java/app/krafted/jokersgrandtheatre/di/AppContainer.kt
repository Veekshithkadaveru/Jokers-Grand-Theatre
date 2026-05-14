package app.krafted.jokersgrandtheatre.di

import android.content.Context
import app.krafted.jokersgrandtheatre.data.DialogueRepository
import app.krafted.jokersgrandtheatre.data.DictionaryRepository
import app.krafted.jokersgrandtheatre.data.db.AppDatabase
import app.krafted.jokersgrandtheatre.data.db.TheatreDao
import app.krafted.jokersgrandtheatre.game.GambleEngine
import app.krafted.jokersgrandtheatre.game.PatternEngine
import app.krafted.jokersgrandtheatre.game.WordDuelEngine
import app.krafted.jokersgrandtheatre.game.WordDuelJokerAI

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val dialogueRepository: DialogueRepository by lazy { DialogueRepository(appContext) }
    val dictionaryRepository: DictionaryRepository by lazy { DictionaryRepository(appContext) }
    val database: AppDatabase by lazy { AppDatabase.getInstance(appContext) }
    val theatreDao: TheatreDao by lazy { database.theatreDao() }

    fun createWordDuelEngine(): WordDuelEngine = WordDuelEngine(dictionaryRepository)
    fun createWordDuelJokerAI(): WordDuelJokerAI = WordDuelJokerAI(dictionaryRepository)
    fun createPatternEngine(): PatternEngine = PatternEngine()
    fun createGambleEngine(): GambleEngine = GambleEngine(dialogueRepository)
}
