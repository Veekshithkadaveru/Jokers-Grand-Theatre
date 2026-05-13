package app.krafted.jokersgrandtheatre.game

import app.krafted.jokersgrandtheatre.data.DictionaryRepository
import app.krafted.jokersgrandtheatre.model.WordResult
import kotlin.math.roundToInt
import kotlin.random.Random

class WordDuelEngine(private val dictionary: DictionaryRepository) {

    companion object {
        const val GRID_SIZE = 25
        const val MIN_WORD_LENGTH = 3

        val COMMON_LETTERS: List<Pair<Char, Int>> = listOf(
            'E' to 12,
            'A' to 9,
            'R' to 9,
            'I' to 9,
            'O' to 8,
            'T' to 9,
            'N' to 7,
            'S' to 7,
            'L' to 5,
            'C' to 4,
            'U' to 4,
            'D' to 4,
            'P' to 3,
            'M' to 3,
            'H' to 3,
            'G' to 2,
            'B' to 2,
            'F' to 2,
            'Y' to 2,
            'W' to 2,
            'K' to 1,
            'V' to 1
        )

        val RARE_LETTERS: List<Pair<Char, Int>> = listOf(
            'X' to 1,
            'J' to 1,
            'Q' to 1,
            'Z' to 1
        )
    }

    fun generateGrid(round: Int): List<Char> {
        val rareProbability = when (round) {
            1 -> 0.05
            2 -> 0.12
            else -> 0.20
        }
        return List(GRID_SIZE) {
            val pool = if (Random.Default.nextDouble() < rareProbability) RARE_LETTERS else COMMON_LETTERS
            weightedPick(pool)
        }
    }

    fun validateWord(word: String): WordResult {
        if (word.length < MIN_WORD_LENGTH) return WordResult.TOO_SHORT
        if (!dictionary.isValidWord(word)) return WordResult.NOT_A_WORD
        return WordResult.VALID
    }

    fun scoreWord(word: String, round: Int): Int {
        return (word.length * 50 * (1.0 + round * 0.2)).roundToInt()
    }

    fun buildWord(grid: List<Char>, selectedIndices: List<Int>): String {
        return buildString {
            for (i in selectedIndices) append(grid[i])
        }
    }

    private fun weightedPick(pool: List<Pair<Char, Int>>): Char {
        val total = pool.sumOf { it.second }
        var roll = Random.Default.nextInt(total)
        for ((letter, weight) in pool) {
            roll -= weight
            if (roll < 0) return letter
        }
        return pool.last().first
    }
}
