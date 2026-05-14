package app.krafted.jokersgrandtheatre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import app.krafted.jokersgrandtheatre.di.AppContainer
import app.krafted.jokersgrandtheatre.ui.actI.WordDuelScreen
import app.krafted.jokersgrandtheatre.ui.actII.PatternInputScreen
import app.krafted.jokersgrandtheatre.ui.actIII.GambleRevealScreen
import app.krafted.jokersgrandtheatre.ui.actIII.GambleScreen
import app.krafted.jokersgrandtheatre.ui.theme.JokersGrandTheatreTheme
import app.krafted.jokersgrandtheatre.viewmodel.GambleViewModel
import app.krafted.jokersgrandtheatre.viewmodel.PatternViewModel
import app.krafted.jokersgrandtheatre.viewmodel.WordDuelViewModel

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

private object Graphs {
    const val ACT_I = "graph_actI"
    const val ACT_II = "graph_actII"
    const val ACT_III = "graph_actIII"
}

private object GambleArgs {
    const val STAKES = "stakes"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = AppContainer(this)
        setContent {
            JokersGrandTheatreTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TheatreNavHost(container)
                }
            }
        }
    }
}

@Composable
fun TheatreNavHost(container: AppContainer) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        placeholderDestination(Routes.SPLASH, "Splash") {
            navController.navigate(Graphs.ACT_I)
        }
        placeholderDestination(Routes.LOBBY, "Lobby") {
            navController.navigate(Graphs.ACT_I)
        }
        placeholderDestination(Routes.LEADERBOARD, "Leaderboard", onTap = null)
        placeholderDestination(Routes.ACT_INTRO, "Act Intro", onTap = null)
        placeholderDestination(Routes.WORD_DUEL_RESULT, "Word Duel Result", onTap = null)
        placeholderDestination(Routes.PATTERN_RESULT, "Pattern Result", onTap = null)
        placeholderDestination(Routes.GAMBLE_RESULT, "Gamble Result", onTap = null)
        placeholderDestination(Routes.INTERMISSION, "Intermission", onTap = null)
        placeholderDestination(Routes.FINALE, "Finale", onTap = null)

        actINavGraph(navController, container)
        actIINavGraph(navController, container)
        actIIINavGraph(navController, container)
    }
}

private fun NavGraphBuilder.placeholderDestination(
    route: String,
    label: String,
    onTap: (() -> Unit)?
) {
    composable(route) {
        Box(
            modifier = if (onTap != null) {
                Modifier.fillMaxSize().clickable(onClick = onTap)
            } else {
                Modifier.fillMaxSize()
            },
            contentAlignment = Alignment.Center
        ) {
            Text(if (onTap != null) "$label (tap to continue)" else label)
        }
    }
}

private fun NavGraphBuilder.actINavGraph(
    navController: NavHostController,
    container: AppContainer
) {
    navigation(startDestination = Routes.WORD_DUEL, route = Graphs.ACT_I) {
        composable(Routes.WORD_DUEL) {
            val viewModel: WordDuelViewModel = viewModel(
                viewModelStoreOwner = navController.getBackStackEntry(Graphs.ACT_I),
                factory = WordDuelViewModel.factory(
                    engine = container.createWordDuelEngine(),
                    ai = container.createWordDuelJokerAI(),
                    dialogue = container.dialogueRepository
                )
            )
            WordDuelScreen(
                viewModel = viewModel,
                onActComplete = { playerActScore, _, _, _ ->
                    navController.navigate(actIIRoute(playerActScore)) {
                        popUpTo(Graphs.ACT_I) { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.actIINavGraph(
    navController: NavHostController,
    container: AppContainer
) {
    val graphRoute = "${Graphs.ACT_II}/{${GambleArgs.STAKES}}"
    navigation(startDestination = Routes.PATTERN_DISPLAY, route = graphRoute) {
        composable(Routes.PATTERN_DISPLAY) {
            val graphEntry = navController.getBackStackEntry(graphRoute)
            val carriedStakes = stakesArgOf(graphEntry)
            val viewModel: PatternViewModel = viewModel(
                viewModelStoreOwner = graphEntry,
                factory = PatternViewModel.factory(
                    engine = container.createPatternEngine(),
                    dialogue = container.dialogueRepository
                )
            )
            PatternInputScreen(
                viewModel = viewModel,
                onActComplete = { patternActScore, _, _ ->
                    val stakes = carriedStakes + patternActScore
                    navController.navigate(actIIIRoute(stakes)) {
                        popUpTo(graphRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.actIIINavGraph(
    navController: NavHostController,
    container: AppContainer
) {
    val graphRoute = "${Graphs.ACT_III}/{${GambleArgs.STAKES}}"
    navigation(startDestination = Routes.GAMBLE, route = graphRoute) {
        composable(Routes.GAMBLE) {
            val viewModel = gambleViewModel(navController, container, graphRoute)
            GambleScreen(
                viewModel = viewModel,
                onReveal = { navController.navigate(Routes.GAMBLE_REVEAL) },
                onActComplete = { _, _, _ -> navController.navigate(Routes.FINALE) }
            )
        }
        composable(Routes.GAMBLE_REVEAL) {
            val viewModel = gambleViewModel(navController, container, graphRoute)
            GambleRevealScreen(
                viewModel = viewModel,
                onContinue = { navController.popBackStack() },
                onActComplete = { _, _, _ ->
                    navController.navigate(Routes.FINALE) {
                        popUpTo(graphRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun gambleViewModel(
    navController: NavHostController,
    container: AppContainer,
    graphRoute: String
): GambleViewModel {
    val graphEntry = navController.getBackStackEntry(graphRoute)
    return viewModel(
        viewModelStoreOwner = graphEntry,
        factory = GambleViewModel.factory(
            engine = container.createGambleEngine(),
            dialogue = container.dialogueRepository,
            baseStakes = stakesArgOf(graphEntry)
        )
    )
}

private fun stakesArgOf(entry: NavBackStackEntry): Int =
    entry.arguments?.getString(GambleArgs.STAKES)?.toIntOrNull() ?: 0

private fun actIIRoute(stakes: Int) = "${Graphs.ACT_II}/$stakes"
private fun actIIIRoute(stakes: Int) = "${Graphs.ACT_III}/$stakes"
