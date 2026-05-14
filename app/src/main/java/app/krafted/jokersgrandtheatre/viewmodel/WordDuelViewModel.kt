package app.krafted.jokersgrandtheatre.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.krafted.jokersgrandtheatre.data.DialogueRepository
import app.krafted.jokersgrandtheatre.game.WordDuelEngine
import app.krafted.jokersgrandtheatre.game.WordDuelJokerAI
import app.krafted.jokersgrandtheatre.model.JokerExpression
import app.krafted.jokersgrandtheatre.model.WordResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Turn { PLAYER, JOKER }

enum class Phase { PLAYING, ROUND_END, ACT_END }

data class WordDuelState(
    val grid: List<Char> = emptyList(),
    val lockedLetters: Set<Int> = emptySet(),
    val playerSelection: List<Int> = emptyList(),
    val playerWord: String = "",
    val jokerWord: String = "",
    val jokerLastPick: Int? = null,
    val currentTurn: Turn = Turn.PLAYER,
    val isJokerThinking: Boolean = false,
    val round: Int = 1,
    val playerRoundScore: Int = 0,
    val jokerRoundScore: Int = 0,
    val playerRoundsWon: Int = 0,
    val jokerRoundsWon: Int = 0,
    val playerActScore: Int = 0,
    val jokerActScore: Int = 0,
    val phase: Phase = Phase.PLAYING,
    val jokerExpression: JokerExpression = JokerExpression.NEUTRAL,
    val jokerLine: String = "",
    val lastWordResult: WordResult? = null,
    val lastSubmittedWord: String = "",
    val lastSubmitter: Turn? = null
)

class WordDuelViewModel(
    private val engine: WordDuelEngine,
    private val ai: WordDuelJokerAI,
    private val dialogue: DialogueRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WordDuelState())
    val state: StateFlow<WordDuelState> = _state.asStateFlow()

    init {
        startRound()
    }

    fun onLetterTap(index: Int) {
        val s = _state.value
        if (s.currentTurn != Turn.PLAYER) return
        if (s.phase != Phase.PLAYING) return
        if (index in s.lockedLetters) return
        if (index in s.playerSelection) return
        _state.update { it.copy(playerSelection = it.playerSelection + index) }
    }

    fun onUndoLastTap() {
        val s = _state.value
        if (s.currentTurn != Turn.PLAYER) return
        if (s.phase != Phase.PLAYING) return
        if (s.playerSelection.isEmpty()) return
        _state.update { it.copy(playerSelection = it.playerSelection.dropLast(1)) }
    }

    fun onPass() {
        val s = _state.value
        if (s.currentTurn != Turn.PLAYER) return
        if (s.phase != Phase.PLAYING) return

        if (s.playerSelection.isEmpty()) {
            _state.update { it.copy(currentTurn = Turn.JOKER) }
            runJokerTurn()
            return
        }

        val submitted = engine.buildWord(s.grid, s.playerSelection)
        val newPlayerWord = s.playerWord + submitted
        val result = engine.validateWord(newPlayerWord)

        when (result) {
            WordResult.VALID -> {
                val oldScore = if (s.playerWord.isEmpty()) 0 else engine.scoreWord(s.playerWord, s.round)
                val newScore = engine.scoreWord(newPlayerWord, s.round)
                val delta = newScore - oldScore
                val isGood = newPlayerWord.length >= 5
                val newLocked = s.lockedLetters + s.playerSelection.toSet()
                _state.update {
                    it.copy(
                        lockedLetters = newLocked,
                        playerWord = newPlayerWord,
                        playerSelection = emptyList(),
                        playerRoundScore = it.playerRoundScore + delta,
                        jokerExpression = if (isGood) JokerExpression.IMPRESSED else JokerExpression.AMUSED,
                        jokerLine = dialogue.line("actI", if (isGood) "playerGoodWord" else "playerBadWord"),
                        lastWordResult = WordResult.VALID,
                        lastSubmittedWord = submitted,
                        lastSubmitter = Turn.PLAYER,
                        currentTurn = Turn.JOKER
                    )
                }
            }
            WordResult.TOO_SHORT, WordResult.NOT_A_WORD -> {
                _state.update {
                    it.copy(
                        playerSelection = emptyList(),
                        jokerExpression = JokerExpression.AMUSED,
                        jokerLine = dialogue.line("actI", "playerBadWord"),
                        lastWordResult = result,
                        lastSubmittedWord = submitted,
                        lastSubmitter = Turn.PLAYER,
                        currentTurn = Turn.JOKER
                    )
                }
            }
        }

        runJokerTurn()
    }

    fun continueToNextRound() {
        val s = _state.value
        if (s.phase != Phase.ROUND_END) return
        _state.update {
            it.copy(
                round = it.round + 1,
                phase = Phase.PLAYING
            )
        }
        startRound()
    }

    fun acknowledgeActEnd() {

    }

    private fun startRound() {
        val s = _state.value
        val isVeryFirst = s.round == 1 && s.playerActScore == 0 && s.jokerActScore == 0
        val newGrid = engine.generateGrid(s.round)
        val openingEvent = if (isVeryFirst) "entrance" else "roundStart"
        _state.update {
            it.copy(
                grid = newGrid,
                lockedLetters = emptySet(),
                playerSelection = emptyList(),
                playerWord = "",
                jokerWord = "",
                jokerLastPick = null,
                playerRoundScore = 0,
                jokerRoundScore = 0,
                currentTurn = Turn.PLAYER,
                isJokerThinking = false,
                phase = Phase.PLAYING,
                jokerExpression = JokerExpression.NEUTRAL,
                jokerLine = dialogue.line("actI", openingEvent),
                lastWordResult = null,
                lastSubmittedWord = "",
                lastSubmitter = null
            )
        }
    }

    private fun runJokerTurn() {
        viewModelScope.launch {
            _state.update { it.copy(isJokerThinking = true) }
            delay(1500L)

            val s = _state.value
            val picked = ai.pickLetter(s.grid, s.lockedLetters, s.playerWord, s.jokerWord, s.round)

            if (picked == null) {
                _state.update { it.copy(isJokerThinking = false) }
                endRound()
                return@launch
            }

            val pickedChar = s.grid[picked]
            val newJokerWord = s.jokerWord + pickedChar
            val newLocked = s.lockedLetters + picked

            val jokerResult = engine.validateWord(newJokerWord)
            val scoreDelta = if (jokerResult == WordResult.VALID) {
                val oldScore = if (s.jokerWord.isEmpty()) 0 else {
                    if (engine.validateWord(s.jokerWord) == WordResult.VALID) engine.scoreWord(s.jokerWord, s.round) else 0
                }
                engine.scoreWord(newJokerWord, s.round) - oldScore
            } else 0

            _state.update {
                it.copy(
                    lockedLetters = newLocked,
                    jokerWord = newJokerWord,
                    jokerLastPick = picked,
                    jokerRoundScore = it.jokerRoundScore + scoreDelta,
                    jokerExpression = if (jokerResult == WordResult.VALID) JokerExpression.GLEEFUL else JokerExpression.NEUTRAL,
                    jokerLine = dialogue.line("actI", "jokerPicksLetter"),
                    isJokerThinking = false
                )
            }

            if (_state.value.lockedLetters.size >= WordDuelEngine.GRID_SIZE - 3) {
                endRound()
            } else {
                _state.update { it.copy(currentTurn = Turn.PLAYER) }
            }
        }
    }

    private fun endRound() {
        val s = _state.value
        val playerWon = s.playerRoundScore > s.jokerRoundScore
        val jokerWon = s.jokerRoundScore > s.playerRoundScore

        val newPlayerActScore = s.playerActScore + s.playerRoundScore
        val newJokerActScore = s.jokerActScore + s.jokerRoundScore
        val newPlayerRoundsWon = s.playerRoundsWon + if (playerWon) 1 else 0
        val newJokerRoundsWon = s.jokerRoundsWon + if (jokerWon) 1 else 0

        val actDone = newPlayerRoundsWon >= 2 || newJokerRoundsWon >= 2 || s.round >= 3

        val roundWinnerIsPlayer = playerWon
        val roundExpression = if (roundWinnerIsPlayer) JokerExpression.IMPRESSED else JokerExpression.TRIUMPHANT
        val roundLine = dialogue.line("actI", if (roundWinnerIsPlayer) "playerWinsRound" else "jokerWinsRound")

        _state.update {
            it.copy(
                playerActScore = newPlayerActScore,
                jokerActScore = newJokerActScore,
                playerRoundsWon = newPlayerRoundsWon,
                jokerRoundsWon = newJokerRoundsWon,
                phase = Phase.ROUND_END,
                jokerExpression = roundExpression,
                jokerLine = roundLine,
                isJokerThinking = false
            )
        }

        if (actDone) {
            val actPlayerWins = when {
                newPlayerRoundsWon > newJokerRoundsWon -> true
                newJokerRoundsWon > newPlayerRoundsWon -> false
                else -> newPlayerActScore > newJokerActScore
            }
            _state.update {
                it.copy(
                    phase = Phase.ACT_END,
                    jokerExpression = if (actPlayerWins) JokerExpression.IMPRESSED else JokerExpression.TRIUMPHANT,
                    jokerLine = dialogue.line("actI", if (actPlayerWins) "playerWinsAct" else "jokerWinsAct")
                )
            }
        }
    }

    companion object {
        fun factory(
            engine: WordDuelEngine,
            ai: WordDuelJokerAI,
            dialogue: DialogueRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WordDuelViewModel(engine, ai, dialogue) as T
            }
        }
    }
}
