package app.krafted.jokersgrandtheatre.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.krafted.jokersgrandtheatre.data.DialogueRepository
import app.krafted.jokersgrandtheatre.game.PatternEngine
import app.krafted.jokersgrandtheatre.game.TapResult
import app.krafted.jokersgrandtheatre.model.JokerExpression
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PatternPhase { DISPLAY, PLAYER_INPUT, ROUND_END, ACT_END }

data class PatternState(
    val sequence: List<Int> = emptyList(),
    val activeSymbolIndex: Int = -1,
    val inputProgress: Int = 0,
    val playerSequence: List<Int> = emptyList(),
    val mistakesMade: Int = 0,
    val phase: PatternPhase = PatternPhase.DISPLAY,
    val round: Int = 1,
    val roundScore: Int = 0,
    val actScore: Int = 0,
    val playerRoundsWon: Int = 0,
    val jokerRoundsWon: Int = 0,
    val jokerExpression: JokerExpression = JokerExpression.SINISTER,
    val jokerLine: String = "",
    val lastTapResult: TapResult? = null
)

class PatternViewModel(
    private val engine: PatternEngine,
    private val dialogue: DialogueRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PatternState())
    val state: StateFlow<PatternState> = _state.asStateFlow()

    private var displayJob: Job? = null

    init {
        startRound()
    }

    fun onSymbolTap(symbolIndex: Int) {
        val s = _state.value
        if (s.phase != PatternPhase.PLAYER_INPUT) return

        when (engine.validateTap(s.sequence, s.playerSequence.size, symbolIndex)) {
            TapResult.CORRECT -> {
                _state.update {
                    it.copy(
                        playerSequence = it.playerSequence + symbolIndex,
                        inputProgress = it.playerSequence.size + 1,
                        jokerExpression = JokerExpression.NEUTRAL,
                        jokerLine = dialogue.line("actII", "playerCorrectTap"),
                        lastTapResult = TapResult.CORRECT
                    )
                }
            }
            TapResult.SEQUENCE_COMPLETE -> {
                val newPlayerSequence = s.playerSequence + symbolIndex
                val roundScore = s.sequence.size * 100 - s.mistakesMade * 50 + s.round * 50
                val newPlayerRoundsWon = s.playerRoundsWon + 1
                val newActScore = s.actScore + roundScore
                val actDone = newPlayerRoundsWon >= 2 || s.jokerRoundsWon >= 2 || s.round >= 3
                _state.update {
                    it.copy(
                        playerSequence = newPlayerSequence,
                        inputProgress = newPlayerSequence.size,
                        roundScore = roundScore,
                        actScore = newActScore,
                        playerRoundsWon = newPlayerRoundsWon,
                        phase = PatternPhase.ROUND_END,
                        jokerExpression = JokerExpression.IMPRESSED,
                        jokerLine = dialogue.line("actII", "playerWinsRound"),
                        lastTapResult = TapResult.SEQUENCE_COMPLETE
                    )
                }
                if (actDone) finalizeAct(playerWonFinalRound = true)
            }
            TapResult.WRONG -> {
                val newMistakes = s.mistakesMade + 1
                val newJokerRoundsWon = s.jokerRoundsWon + 1
                val actDone = s.playerRoundsWon >= 2 || newJokerRoundsWon >= 2 || s.round >= 3
                _state.update {
                    it.copy(
                        mistakesMade = newMistakes,
                        jokerRoundsWon = newJokerRoundsWon,
                        phase = PatternPhase.ROUND_END,
                        jokerExpression = JokerExpression.GLEEFUL,
                        jokerLine = dialogue.line("actII", "playerWrongTap"),
                        lastTapResult = TapResult.WRONG
                    )
                }
                if (actDone) finalizeAct(playerWonFinalRound = false)
            }
        }
    }

    fun continueToNextRound() {
        val s = _state.value
        if (s.phase != PatternPhase.ROUND_END) return
        _state.update {
            it.copy(
                round = it.round + 1,
                phase = PatternPhase.DISPLAY
            )
        }
        startRound()
    }

    fun acknowledgeActEnd() {
    }

    fun clearLastTapResult() {
        _state.update { it.copy(lastTapResult = null) }
    }

    private fun startRound() {
        val s = _state.value
        val isVeryFirst = s.round == 1 && s.actScore == 0 && s.playerRoundsWon == 0 && s.jokerRoundsWon == 0
        val newSequence = engine.generateSequence(s.round)
        val openingEvent = if (isVeryFirst) "entrance" else "roundStart"
        _state.update {
            it.copy(
                sequence = newSequence,
                activeSymbolIndex = -1,
                inputProgress = 0,
                playerSequence = emptyList(),
                mistakesMade = 0,
                roundScore = 0,
                phase = PatternPhase.DISPLAY,
                jokerExpression = JokerExpression.SINISTER,
                jokerLine = dialogue.line("actII", openingEvent),
                lastTapResult = null
            )
        }
        runDisplayPhase(newSequence)
    }

    private fun runDisplayPhase(sequence: List<Int>) {
        displayJob?.cancel()
        displayJob = viewModelScope.launch {
            delay(600L)
            _state.update {
                it.copy(jokerLine = dialogue.line("actII", "displayingSequence"))
            }
            for ((i, symbol) in sequence.withIndex()) {
                _state.update { it.copy(activeSymbolIndex = symbol) }
                delay(700L)
                _state.update { it.copy(activeSymbolIndex = -1) }
                if (i != sequence.lastIndex) delay(300L)
            }
            _state.update { it.copy(phase = PatternPhase.PLAYER_INPUT) }
        }
    }

    private fun finalizeAct(playerWonFinalRound: Boolean) {
        val s = _state.value
        val actPlayerWins = when {
            s.playerRoundsWon > s.jokerRoundsWon -> true
            s.jokerRoundsWon > s.playerRoundsWon -> false
            else -> playerWonFinalRound
        }
        _state.update {
            it.copy(
                phase = PatternPhase.ACT_END,
                jokerExpression = if (actPlayerWins) JokerExpression.IMPRESSED else JokerExpression.TRIUMPHANT,
                jokerLine = dialogue.line("actII", if (actPlayerWins) "playerWinsAct" else "jokerWinsAct")
            )
        }
    }

    companion object {
        fun factory(
            engine: PatternEngine,
            dialogue: DialogueRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PatternViewModel(engine, dialogue) as T
            }
        }
    }
}
