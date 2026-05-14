package app.krafted.jokersgrandtheatre.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.krafted.jokersgrandtheatre.data.DialogueRepository
import app.krafted.jokersgrandtheatre.game.GambleEngine
import app.krafted.jokersgrandtheatre.model.JokerExpression
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class GamblePhase { CHOOSING, REVEALING, ROUND_END, ACT_END }

data class GambleState(
    val round: Int = 1,
    val crownPosition: Int = 0,
    val selectedMask: Int? = null,
    val revealedMasks: Set<Int> = emptySet(),
    val misdirectionLine: String = "",
    val isMisdirectionTruth: Boolean = false,
    val hintedPosition: Int = 0,
    val stakes: Int = 0,
    val roundScore: Int = 0,
    val actScore: Int = 0,
    val playerWins: Int = 0,
    val jokerWins: Int = 0,
    val phase: GamblePhase = GamblePhase.CHOOSING,
    val jokerExpression: JokerExpression = JokerExpression.UNHINGED,
    val jokerLine: String = ""
)

class GambleViewModel(
    private val engine: GambleEngine,
    private val dialogue: DialogueRepository,
    private val baseStakes: Int
) : ViewModel() {

    private val _state = MutableStateFlow(GambleState())
    val state: StateFlow<GambleState> = _state.asStateFlow()

    private var revealJob: Job? = null

    init {
        startRound(isVeryFirst = true)
    }

    fun selectMask(maskIndex: Int) {
        val s = _state.value
        if (s.phase != GamblePhase.CHOOSING) return
        if (maskIndex in s.revealedMasks) return
        _state.update {
            it.copy(
                selectedMask = maskIndex,
                phase = GamblePhase.REVEALING,
                jokerLine = dialogue.line("actIII", "beforeReveal"),
                jokerExpression = JokerExpression.SINISTER
            )
        }
        revealJob?.cancel()
        revealJob = viewModelScope.launch {
            delay(900L)
            revealMask()
        }
    }

    fun revealMask() {
        val s = _state.value
        if (s.phase != GamblePhase.REVEALING) return
        val maskIndex = s.selectedMask ?: return

        val crownFound = maskIndex == s.crownPosition
        val newRoundScore = if (crownFound) (s.stakes * 1.5).toInt() else (s.stakes * 0.5).toInt()
        val newPlayerWins = s.playerWins + if (crownFound) 1 else 0
        val newJokerWins = s.jokerWins + if (crownFound) 0 else 1
        val newActScore = s.actScore + newRoundScore

        _state.update {
            it.copy(
                revealedMasks = it.revealedMasks + maskIndex,
                roundScore = newRoundScore,
                actScore = newActScore,
                playerWins = newPlayerWins,
                jokerWins = newJokerWins,
                phase = GamblePhase.ROUND_END,
                jokerExpression = if (crownFound) JokerExpression.IMPRESSED else JokerExpression.GLEEFUL,
                jokerLine = dialogue.line("actIII", if (crownFound) "crownFound" else "crownMissed")
            )
        }

        if (newPlayerWins >= 2 || newJokerWins >= 2 || s.round >= 3) {
            finalizeAct()
        }
    }

    fun nextRound() {
        if (_state.value.phase != GamblePhase.ROUND_END) return
        _state.update { it.copy(round = it.round + 1) }
        startRound(isVeryFirst = false)
    }

    fun acknowledgeActEnd() {
    }

    private fun startRound(isVeryFirst: Boolean) {
        val crown = engine.placeCrown()
        val mis = engine.generateMisdirection(crown)
        val openingEvent = if (isVeryFirst) "entrance" else "roundStart"
        _state.update {
            it.copy(
                crownPosition = crown,
                selectedMask = null,
                revealedMasks = emptySet(),
                misdirectionLine = mis.line,
                isMisdirectionTruth = mis.isTruth,
                hintedPosition = mis.hintedPosition,
                stakes = baseStakes,
                roundScore = 0,
                phase = GamblePhase.CHOOSING,
                jokerExpression = JokerExpression.UNHINGED,
                jokerLine = dialogue.line("actIII", openingEvent)
            )
        }
    }

    private fun finalizeAct() {
        val s = _state.value
        val playerWonAct = s.playerWins > s.jokerWins
        val bonus = if (playerWonAct) {
            when {
                s.jokerWins == 0 -> 500
                s.jokerWins == 1 -> 250
                else -> 0
            }
        } else 0
        _state.update {
            it.copy(
                actScore = it.actScore + bonus,
                phase = GamblePhase.ACT_END,
                jokerExpression = if (playerWonAct) JokerExpression.IMPRESSED else JokerExpression.TRIUMPHANT,
                jokerLine = dialogue.line("actIII", if (playerWonAct) "playerWinsAct" else "jokerWinsAct")
            )
        }
    }

    companion object {
        fun factory(
            engine: GambleEngine,
            dialogue: DialogueRepository,
            baseStakes: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GambleViewModel(engine, dialogue, baseStakes) as T
            }
        }
    }
}
