package app.krafted.jokersgrandtheatre.game

import app.krafted.jokersgrandtheatre.data.DialogueRepository
import kotlin.random.Random

data class Misdirection(val line: String, val isTruth: Boolean, val hintedPosition: Int)

class GambleEngine(private val dialogue: DialogueRepository) {

    companion object {
        const val MASK_COUNT = 3
        const val TRUTH_PROBABILITY = 0.4
    }

    fun placeCrown(): Int = Random.Default.nextInt(MASK_COUNT)

    fun generateMisdirection(crownPosition: Int): Misdirection {
        val isTruth = Random.Default.nextDouble() < TRUTH_PROBABILITY
        val hintedPosition = if (isTruth) {
            crownPosition
        } else {
            val candidates = (0 until MASK_COUNT).filter { it != crownPosition }
            candidates[Random.Default.nextInt(candidates.size)]
        }
        return Misdirection(
            line = dialogue.misdirectionLine(hintedPosition, truth = isTruth),
            isTruth = isTruth,
            hintedPosition = hintedPosition
        )
    }
}
