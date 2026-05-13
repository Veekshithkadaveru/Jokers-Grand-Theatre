package app.krafted.jokersgrandtheatre.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.random.Random

class DialogueRepository(private val context: Context) {

    private val dialogue: Map<String, Map<String, List<String>>> by lazy {
        context.assets.open("joker_dialogue.json").use { stream ->
            InputStreamReader(stream).use { reader ->
                val type = object : TypeToken<Map<String, Map<String, List<String>>>>() {}.type
                Gson().fromJson(reader, type)
            }
        }
    }

    fun line(act: String, event: String): String {
        val list = dialogue[act]?.get(event)
        if (list.isNullOrEmpty()) return ""
        return list[Random.Default.nextInt(list.size)]
    }

    fun lines(act: String, event: String): List<String> {
        return dialogue[act]?.get(event) ?: emptyList()
    }
}
