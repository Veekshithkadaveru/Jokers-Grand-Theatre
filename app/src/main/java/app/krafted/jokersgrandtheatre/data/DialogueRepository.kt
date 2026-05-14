package app.krafted.jokersgrandtheatre.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.random.Random

class DialogueRepository(private val context: Context) {

    private val root: Map<String, Map<String, Any>> by lazy {
        context.assets.open("joker_dialogue.json").use { stream ->
            InputStreamReader(stream).use { reader ->
                val type = object : TypeToken<Map<String, Map<String, Any>>>() {}.type
                Gson().fromJson(reader, type)
            }
        }
    }

    private val dialogue: Map<String, Map<String, List<String>>> by lazy {
        root.mapValues { (_, events) ->
            events.mapNotNull { (event, value) ->
                @Suppress("UNCHECKED_CAST")
                if (value is List<*>) event to (value as List<String>) else null
            }.toMap()
        }
    }

    private val misdirection: Map<String, Map<String, List<String>>> by lazy {
        @Suppress("UNCHECKED_CAST")
        val node = root["actIII"]?.get("misdirection") as? Map<String, Map<String, List<String>>>
        node ?: emptyMap()
    }

    fun line(act: String, event: String): String {
        val list = dialogue[act]?.get(event)
        if (list.isNullOrEmpty()) return ""
        return list[Random.Default.nextInt(list.size)]
    }

    fun lines(act: String, event: String): List<String> {
        return dialogue[act]?.get(event) ?: emptyList()
    }

    fun misdirectionLine(position: Int, truth: Boolean): String {
        val bucket = if (truth) "truth" else "lie"
        val list = misdirection["mask$position"]?.get(bucket)
        if (list.isNullOrEmpty()) return ""
        return list[Random.Default.nextInt(list.size)]
    }
}
