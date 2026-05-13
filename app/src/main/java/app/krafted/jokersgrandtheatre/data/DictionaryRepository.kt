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

    fun isValidWord(word: String): Boolean {
        return words.contains(word.lowercase())
    }

    val wordCount: Int
        get() = words.size
}
