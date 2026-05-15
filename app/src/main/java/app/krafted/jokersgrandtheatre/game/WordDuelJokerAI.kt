package app.krafted.jokersgrandtheatre.game

import app.krafted.jokersgrandtheatre.data.DictionaryRepository
import kotlin.random.Random

class WordDuelJokerAI(private val dictionary: DictionaryRepository) {

    private var targetWord: String? = null

    fun pickLetter(
        grid: List<Char>,
        lockedLetters: Set<Int>,
        playerWord: String,
        jokerWord: String,
        round: Int
    ): Int? {
        val unlockedIndices = grid.indices.filter { it !in lockedLetters }
        if (unlockedIndices.isEmpty()) return null

        if (jokerWord.isEmpty()) resetState()

        return when (round) {
            1 -> pickRoundOne(grid, unlockedIndices, jokerWord)
            2 -> pickRoundTwo(grid, unlockedIndices, playerWord, jokerWord)
            else -> pickRoundThree(grid, unlockedIndices, playerWord, jokerWord)
        }
    }

    fun resetState() {
        targetWord = null
    }

    private fun pickRandom(unlockedIndices: List<Int>): Int? {
        if (unlockedIndices.isEmpty()) return null
        return unlockedIndices[Random.Default.nextInt(unlockedIndices.size)]
    }

    private fun getValidExtensions(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        jokerWord: String
    ): List<Int> {
        val availableOnly = unlockedIndices.map { grid[it].lowercaseChar() }
        val extensions = mutableListOf<Int>()
        for (index in unlockedIndices) {
            val candidate = (jokerWord + grid[index]).lowercase()
            if (dictionary.isValidWord(candidate)) {
                extensions.add(index)
            } else {
                val remainingLetters = availableOnly.toMutableList()
                remainingLetters.remove(grid[index].lowercaseChar())
                if (dictionary.findShortestWordWithPrefixFromLetters(candidate, remainingLetters, candidate.length + 1) != null) {
                    extensions.add(index)
                }
            }
        }
        return extensions
    }

    private fun pickRoundOne(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        jokerWord: String
    ): Int? {
        val extensions = getValidExtensions(grid, unlockedIndices, jokerWord)
        if (extensions.isNotEmpty()) {
            return extensions[Random.Default.nextInt(extensions.size)]
        }
        return pickRandom(unlockedIndices)
    }

    private fun pickRoundTwo(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        playerWord: String,
        jokerWord: String
    ): Int? {
        val extensions = getValidExtensions(grid, unlockedIndices, jokerWord)
        if (extensions.isEmpty()) return pickRandom(unlockedIndices)

        if (playerWord.isNotEmpty()) {
            val blockingExt = extensions.firstOrNull { index ->
                dictionary.isValidWord((playerWord + grid[index]).lowercase())
            }
            if (blockingExt != null) return blockingExt
        }

        return extensions[Random.Default.nextInt(extensions.size)]
    }

    private fun pickRoundThree(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        playerWord: String,
        jokerWord: String
    ): Int? {
        val availableLetters = unlockedIndices.map { grid[it] }

        if (targetWord != null) {
            if (jokerWord.length >= targetWord!!.length) {
                targetWord = null
            } else {
                val remainingNeeded = targetWord!!.drop(jokerWord.length)
                val remainingAvailable = availableLetters.toMutableList()
                for (c in remainingNeeded) {
                    val idx = remainingAvailable.indexOfFirst { it.lowercaseChar() == c.lowercaseChar() }
                    if (idx == -1) {
                        targetWord = null
                        break
                    }
                    remainingAvailable.removeAt(idx)
                }
            }
        }

        if (targetWord == null) {
            val jokerPrefix = jokerWord.lowercase()
            val availableOnly = availableLetters.map { it.lowercaseChar() }
            val preferredMin = maxOf(4, jokerWord.length + 1)
            val fallbackMin = maxOf(3, jokerWord.length + 1)
            val newTarget = (dictionary.findShortestWordWithPrefixFromLetters(jokerPrefix, availableOnly, preferredMin)
                ?: dictionary.findShortestWordWithPrefixFromLetters(jokerPrefix, availableOnly, fallbackMin))
            if (newTarget != null) {
                targetWord = newTarget
            }
        }

        if (targetWord != null) {
            val nextNeededChar = targetWord!![jokerWord.length].lowercaseChar()
            val match = unlockedIndices.firstOrNull { grid[it].lowercaseChar() == nextNeededChar }
            if (match != null) return match
        }

        val extensions = getValidExtensions(grid, unlockedIndices, jokerWord)
        if (extensions.isNotEmpty()) {
            return extensions[Random.Default.nextInt(extensions.size)]
        }

        return pickRandom(unlockedIndices)
    }
}