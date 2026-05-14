package app.krafted.jokersgrandtheatre.di

import android.content.Context
import app.krafted.jokersgrandtheatre.data.DialogueRepository
import app.krafted.jokersgrandtheatre.data.DictionaryRepository
import app.krafted.jokersgrandtheatre.game.GambleEngine
import app.krafted.jokersgrandtheatre.game.PatternEngine
import app.krafted.jokersgrandtheatre.game.WordDuelEngine
import app.krafted.jokersgrandtheatre.game.WordDuelJokerAI

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val dialogueRepository: DialogueRepository by lazy { DialogueRepository(appContext) }
    val dictionaryRepository: DictionaryRepository by lazy { DictionaryRepository(appContext) }

    fun createWordDuelEngine(): WordDuelEngine = WordDuelEngine(dictionaryRepository)
    fun createWordDuelJokerAI(): WordDuelJokerAI = WordDuelJokerAI(dictionaryRepository)
    fun createPatternEngine(): PatternEngine = PatternEngine()
    fun createGambleEngine(): GambleEngine = GambleEngine(dialogueRepository)
}
