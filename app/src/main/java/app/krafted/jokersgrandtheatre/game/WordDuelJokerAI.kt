package app.krafted.jokersgrandtheatre.game

import app.krafted.jokersgrandtheatre.data.DictionaryRepository
import kotlin.random.Random

class WordDuelJokerAI(private val dictionary: DictionaryRepository) {

    private val followUpLetters = listOf('E', 'A', 'S', 'R', 'T', 'N', 'I', 'O', 'L')

    fun pickLetter(
        grid: List<Char>,
        lockedLetters: Set<Int>,
        playerWord: String,
        jokerWord: String,
        round: Int
    ): Int? {
        val unlockedIndices = grid.indices.filter { it !in lockedLetters }
        if (unlockedIndices.isEmpty()) return null

        return when (round) {
            1 -> pickRandom(unlockedIndices)
            2 -> pickRoundTwo(grid, unlockedIndices, playerWord, jokerWord)
            else -> pickRoundThree(grid, unlockedIndices, playerWord, jokerWord)
        }
    }

    private fun pickRandom(unlockedIndices: List<Int>): Int? {
        if (unlockedIndices.isEmpty()) return null
        return unlockedIndices[Random.Default.nextInt(unlockedIndices.size)]
    }

    private fun pickRoundTwo(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        playerWord: String,
        jokerWord: String
    ): Int? {
        val extendFirst = Random.Default.nextBoolean()
        val extend = { tryExtend(grid, unlockedIndices, jokerWord) }
        val block = { tryBlock(grid, unlockedIndices, playerWord) }

        val primary = if (extendFirst) extend() else block()
        if (primary != null) return primary
        val secondary = if (extendFirst) block() else extend()
        if (secondary != null) return secondary
        return pickRandom(unlockedIndices)
    }

    private fun pickRoundThree(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        playerWord: String,
        jokerWord: String
    ): Int? {
        val criticalBlock = pickCriticalBlock(grid, unlockedIndices, playerWord)
        if (criticalBlock != null) return criticalBlock

        val extend = tryExtend(grid, unlockedIndices, jokerWord)
        if (extend != null) return extend

        return pickRandom(unlockedIndices)
    }

    private fun tryExtend(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        jokerWord: String
    ): Int? {
        for (index in unlockedIndices) {
            val candidate = jokerWord + grid[index]
            if (dictionary.isValidWord(candidate.lowercase())) return index
            if (hasPrefix(candidate)) return index
        }
        return null
    }

    private fun tryBlock(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        playerWord: String
    ): Int? {
        if (playerWord.isNotEmpty()) {
            val direct = unlockedIndices.firstOrNull {
                dictionary.isValidWord((playerWord + grid[it]).lowercase())
            }
            if (direct != null) return direct
        }

        val counts = mutableMapOf<Char, Int>()
        for (index in unlockedIndices) {
            val ch = grid[index]
            counts[ch] = (counts[ch] ?: 0) + 1
        }
        val mostCommon = counts.maxByOrNull { it.value }?.key ?: return null
        return unlockedIndices.firstOrNull { grid[it] == mostCommon }
    }

    private fun pickCriticalBlock(
        grid: List<Char>,
        unlockedIndices: List<Int>,
        playerWord: String
    ): Int? {
        if (playerWord.isEmpty()) return null

        val counts = mutableMapOf<Char, Int>()
        for (index in unlockedIndices) {
            val ch = grid[index]
            counts[ch] = (counts[ch] ?: 0) + 1
        }

        val criticalLetters = mutableSetOf<Char>()
        val seen = mutableSetOf<Char>()
        for (index in unlockedIndices) {
            val ch = grid[index]
            if (ch in seen) continue
            seen.add(ch)
            val candidate = playerWord + ch
            if (dictionary.isValidWord(candidate.lowercase()) || hasPrefix(candidate)) {
                criticalLetters.add(ch)
            }
        }

        if (criticalLetters.isEmpty()) return null

        val bestLetter = criticalLetters.maxByOrNull { counts[it] ?: 0 } ?: return null
        return unlockedIndices.firstOrNull { grid[it] == bestLetter }
    }

    private fun hasPrefix(prefix: String): Boolean {
        val lower = prefix.lowercase()
        for (followUp in followUpLetters) {
            if (dictionary.isValidWord(lower + followUp.lowercaseChar())) return true
        }
        return false
    }
}
