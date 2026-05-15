package app.krafted.jokersgrandtheatre.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

class DictionaryRepository(private val context: android.content.Context) {

    private val words: HashSet<String> by lazy {
        context.assets.open("dictionary.json").use { stream ->
            InputStreamReader(stream).use { reader ->
                val array = Gson().fromJson(reader, Array<String>::class.java)
                HashSet(array.toList())
            }
        }
    }

    private val sortedWords: List<String> by lazy { words.sorted() }

    fun isValidWord(word: String): Boolean {
        return words.contains(word.lowercase())
    }

    val wordCount: Int
        get() = words.size

    fun hasWordsWithPrefix(prefix: String): Boolean {
        val lowerPrefix = prefix.lowercase()
        var lo = 0
        var hi = sortedWords.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (sortedWords[mid] < lowerPrefix) lo = mid + 1 else hi = mid
        }
        return lo < sortedWords.size && sortedWords[lo].startsWith(lowerPrefix)
    }

    fun findShortestWordFromLetters(letters: List<Char>, minLength: Int = 3): String? {
        val lowerLetters = letters.map { it.lowercaseChar() }
        val freq = lowerLetters.groupingBy { it }.eachCount()
        return words
            .filter { word ->
                if (word.length < minLength) return@filter false
                val needed = mutableMapOf<Char, Int>()
                for (ch in word) needed[ch] = (needed[ch] ?: 0) + 1
                needed.all { (ch, count) -> (freq[ch] ?: 0) >= count }
            }
            .minByOrNull { it.length }
    }

    fun findShortestWordWithPrefixFromLetters(prefix: String, availableLetters: List<Char>, minLength: Int = 3): String? {
        val lowerPrefix = prefix.lowercase()
        val allLetters = (lowerPrefix.toList() + availableLetters).map { it.lowercaseChar() }
        
        val freq = IntArray(26)
        for (ch in allLetters) {
            if (ch in 'a'..'z') {
                freq[ch - 'a']++
            }
        }

        var lo = 0
        var hi = sortedWords.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (sortedWords[mid] < lowerPrefix) lo = mid + 1 else hi = mid
        }

        var shortest: String? = null
        while (lo < sortedWords.size && sortedWords[lo].startsWith(lowerPrefix)) {
            val word = sortedWords[lo]
            lo++
            if (word.length < minLength) continue
            
            var canMake = true
            val tempFreq = IntArray(26)
            for (i in 0 until word.length) {
                val ch = word[i]
                if (ch in 'a'..'z') {
                    val idx = ch - 'a'
                    tempFreq[idx]++
                    if (tempFreq[idx] > freq[idx]) {
                        canMake = false
                        break
                    }
                } else {
                    canMake = false
                    break
                }
            }
            
            if (canMake) {
                if (shortest == null || word.length < shortest.length) {
                    shortest = word
                }
            }
        }
        return shortest
    }

    fun findContinuationLetter(prefix: String, availableLetters: List<Char>): Char? {
        val lowerPrefix = prefix.lowercase()
        val lowerAvailable = availableLetters.map { it.lowercaseChar() }
        var lo = 0
        var hi = sortedWords.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (sortedWords[mid] < lowerPrefix) lo = mid + 1 else hi = mid
        }
        while (lo < sortedWords.size && sortedWords[lo].startsWith(lowerPrefix)) {
            val word = sortedWords[lo]
            if (word.length > lowerPrefix.length) {
                val next = word[lowerPrefix.length]
                if (next in lowerAvailable) return next
            }
            lo++
        }
        return null
    }
}
