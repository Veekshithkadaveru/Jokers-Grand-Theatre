package app.krafted.jokersgrandtheatre.game

import kotlin.random.Random

enum class TapResult {
    CORRECT,
    WRONG,
    SEQUENCE_COMPLETE
}

class PatternEngine {

    companion object {
        const val SYMBOL_COUNT = 7
        const val ROUND_1_LENGTH = 4
        const val ROUND_2_LENGTH = 5
        const val ROUND_3_LENGTH = 6
    }

    fun generateSequence(round: Int): List<Int> {
        val length = when (round) {
            1 -> ROUND_1_LENGTH
            2 -> ROUND_2_LENGTH
            else -> ROUND_3_LENGTH
        }
        return List(length) { Random.Default.nextInt(SYMBOL_COUNT) }
    }

    fun validateTap(sequence: List<Int>, playerInputSize: Int, tappedSymbol: Int): TapResult {
        val expected = sequence.reversed()[playerInputSize]
        if (tappedSymbol != expected) return TapResult.WRONG
        if (playerInputSize == sequence.size - 1) return TapResult.SEQUENCE_COMPLETE
        return TapResult.CORRECT
    }
}
