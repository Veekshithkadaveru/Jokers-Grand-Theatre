package app.krafted.jokersgrandtheatre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.krafted.jokersgrandtheatre.ui.theme.JokersGrandTheatreTheme

object Routes {
    const val SPLASH = "splash"
    const val LOBBY = "lobby"
    const val LEADERBOARD = "leaderboard"
    const val ACT_INTRO = "actIntro/{actId}"
    const val WORD_DUEL = "wordDuel"
    const val WORD_DUEL_RESULT = "wordDuelResult"
    const val PATTERN_DISPLAY = "patternDisplay"
    const val PATTERN_INPUT = "patternInput"
    const val PATTERN_RESULT = "patternResult"
    const val GAMBLE = "gamble"
    const val GAMBLE_REVEAL = "gambleReveal"
    const val GAMBLE_RESULT = "gambleResult"
    const val INTERMISSION = "intermission/{intermissionId}"
    const val FINALE = "finale"

    fun actIntro(actId: String) = "actIntro/$actId"
    fun intermission(intermissionId: String) = "intermission/$intermissionId"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JokersGrandTheatreTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TheatreNavHost()
                }
            }
        }
    }
}

@Composable
fun TheatreNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            androidx.compose.material3.Text("Splash")
        }
        composable(Routes.LOBBY) {
            androidx.compose.material3.Text("Lobby")
        }
        composable(Routes.LEADERBOARD) {
            androidx.compose.material3.Text("Leaderboard")
        }
        composable(Routes.ACT_INTRO) {
            androidx.compose.material3.Text("Act Intro")
        }
        composable(Routes.WORD_DUEL) {
            androidx.compose.material3.Text("Word Duel")
        }
        composable(Routes.WORD_DUEL_RESULT) {
            androidx.compose.material3.Text("Word Duel Result")
        }
        composable(Routes.PATTERN_DISPLAY) {
            androidx.compose.material3.Text("Pattern Display")
        }
        composable(Routes.PATTERN_INPUT) {
            androidx.compose.material3.Text("Pattern Input")
        }
        composable(Routes.PATTERN_RESULT) {
            androidx.compose.material3.Text("Pattern Result")
        }
        composable(Routes.GAMBLE) {
            androidx.compose.material3.Text("Gamble")
        }
        composable(Routes.GAMBLE_REVEAL) {
            androidx.compose.material3.Text("Gamble Reveal")
        }
        composable(Routes.GAMBLE_RESULT) {
            androidx.compose.material3.Text("Gamble Result")
        }
        composable(Routes.INTERMISSION) {
            androidx.compose.material3.Text("Intermission")
        }
        composable(Routes.FINALE) {
            androidx.compose.material3.Text("Finale")
        }
    }
}
