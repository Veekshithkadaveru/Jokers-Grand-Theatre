package app.krafted.jokersgrandtheatre.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.krafted.jokersgrandtheatre.model.Act
import app.krafted.jokersgrandtheatre.model.JokerExpression
import app.krafted.jokersgrandtheatre.model.JokerMood
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TheatreSessionState(
    val actIScore: Int = 0,
    val actIIScore: Int = 0,
    val actIIIScore: Int = 0,
    val actIRoundsWon: Int = 0,
    val actIIRoundsWon: Int = 0,
    val actIIIRoundsWon: Int = 0,
    val jokerActIRoundsWon: Int = 0,
    val jokerActIIRoundsWon: Int = 0,
    val jokerActIIIRoundsWon: Int = 0,
    val currentAct: Act = Act.LOBBY,
    val jokerMood: JokerMood = JokerMood.THEATRICAL,
    val jokerExpression: JokerExpression = JokerExpression.NEUTRAL,
    val isSessionComplete: Boolean = false,
    val playAllMode: Boolean = false
) {
    val totalScore: Int get() = actIScore + actIIScore + actIIIScore
}

class TheatreViewModel : ViewModel() {

    private val _state = MutableStateFlow(TheatreSessionState())
    val state: StateFlow<TheatreSessionState> = _state.asStateFlow()

    fun startPlayAll() {
        _state.update {
            TheatreSessionState(
                currentAct = Act.ACT_I,
                jokerMood = JokerMood.THEATRICAL,
                playAllMode = true,
                isSessionComplete = false
            )
        }
    }

    fun startSingleAct(act: Act) {
        _state.update {
            TheatreSessionState(
                currentAct = act,
                jokerMood = moodForAct(act),
                playAllMode = false,
                isSessionComplete = false
            )
        }
    }

    fun recordActResult(
        act: Act,
        playerScore: Int,
        jokerScore: Int,
        playerRoundsWon: Int,
        jokerRoundsWon: Int
    ) {
        _state.update {
            when (act) {
                Act.ACT_I -> it.copy(
                    actIScore = playerScore,
                    actIRoundsWon = playerRoundsWon,
                    jokerActIRoundsWon = jokerRoundsWon
                )

                Act.ACT_II -> it.copy(
                    actIIScore = playerScore,
                    actIIRoundsWon = playerRoundsWon,
                    jokerActIIRoundsWon = jokerRoundsWon
                )

                Act.ACT_III -> it.copy(
                    actIIIScore = playerScore,
                    actIIIRoundsWon = playerRoundsWon,
                    jokerActIIIRoundsWon = jokerRoundsWon
                )

                else -> it
            }
        }
    }

    fun advanceAfterAct() {
        val current = _state.value.currentAct
        if (_state.value.playAllMode) {
            val next = when (current) {
                Act.ACT_I -> Act.INTERMISSION_I
                Act.INTERMISSION_I -> Act.ACT_II
                Act.ACT_II -> Act.INTERMISSION_II
                Act.INTERMISSION_II -> Act.ACT_III
                Act.ACT_III -> Act.FINALE
                else -> current
            }
            _state.update {
                it.copy(
                    currentAct = next,
                    jokerMood = moodForAct(next),
                    isSessionComplete = next == Act.FINALE
                )
            }
        } else {
            _state.update {
                it.copy(
                    currentAct = Act.FINALE,
                    isSessionComplete = true
                )
            }
        }
    }

    fun returnToLobby() {
        _state.update {
            it.copy(
                currentAct = Act.LOBBY,
                isSessionComplete = false
            )
        }
    }

    private fun moodForAct(act: Act): JokerMood = when (act) {
        Act.LOBBY, Act.ACT_I, Act.INTERMISSION_I -> JokerMood.THEATRICAL
        Act.ACT_II, Act.INTERMISSION_II -> JokerMood.SINISTER
        Act.ACT_III, Act.FINALE -> JokerMood.UNHINGED
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TheatreViewModel() as T
            }
        }
    }
}
