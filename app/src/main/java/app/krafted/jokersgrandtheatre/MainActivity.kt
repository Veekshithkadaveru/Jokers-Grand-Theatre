package app.krafted.jokersgrandtheatre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import app.krafted.jokersgrandtheatre.data.db.ActScoreEntity
import app.krafted.jokersgrandtheatre.di.AppContainer
import app.krafted.jokersgrandtheatre.model.Act
import app.krafted.jokersgrandtheatre.ui.ActIntroScreen
import app.krafted.jokersgrandtheatre.ui.GrandFinaleScreen
import app.krafted.jokersgrandtheatre.ui.IntermissionScreen
import app.krafted.jokersgrandtheatre.ui.LeaderboardScreen
import app.krafted.jokersgrandtheatre.ui.SplashScreen
import app.krafted.jokersgrandtheatre.ui.TheatreLobbyScreen
import app.krafted.jokersgrandtheatre.ui.actI.WordDuelScreen
import app.krafted.jokersgrandtheatre.ui.actII.PatternInputScreen
import app.krafted.jokersgrandtheatre.ui.actIII.GambleRevealScreen
import app.krafted.jokersgrandtheatre.ui.actIII.GambleScreen
import app.krafted.jokersgrandtheatre.ui.theme.JokersGrandTheatreTheme
import app.krafted.jokersgrandtheatre.ui.theme.TheatreGold
import app.krafted.jokersgrandtheatre.viewmodel.GambleViewModel
import app.krafted.jokersgrandtheatre.viewmodel.PatternViewModel
import app.krafted.jokersgrandtheatre.viewmodel.WordDuelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Routes {
    const val SPLASH = "splash"
    const val LOBBY = "lobby"
    const val LEADERBOARD = "leaderboard"
    const val ACT_INTRO = "actIntro/{actId}/{stakes}"
    const val WORD_DUEL = "wordDuel"
    const val PATTERN_DISPLAY = "patternDisplay"
    const val PATTERN_INPUT = "patternInput"
    const val GAMBLE = "gamble"
    const val GAMBLE_REVEAL = "gambleReveal"
    const val INTERMISSION = "intermission/{intermissionId}/{stakes}"
    const val FINALE = "finale/{priorStakes}/{actIIIScore}"

    fun actIntro(actId: String, stakes: Int) = "actIntro/$actId/$stakes"
    fun intermission(intermissionId: String, stakes: Int) = "intermission/$intermissionId/$stakes"
    fun finale(priorStakes: Int, actIIIScore: Int) = "finale/$priorStakes/$actIIIScore"
}

private object Graphs {
    const val ACT_I = "graph_actI"
    const val ACT_II = "graph_actII"
    const val ACT_III = "graph_actIII"
}

private object NavArgs {
    const val STAKES = "stakes"
    const val ACT_ID = "actId"
    const val INTERMISSION_ID = "intermissionId"
    const val PRIOR_STAKES = "priorStakes"
    const val ACT_III_SCORE = "actIIIScore"
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
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.LOBBY) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOBBY) {
            var scores by remember { mutableStateOf<List<ActScoreEntity>?>(null) }
            LaunchedEffect(Unit) {
                scores = withContext(Dispatchers.IO) { container.theatreDao.getAllBestScores() }
            }
            if (scores == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TheatreGold)
                }
            } else {
                val scoreMap = scores!!.associateBy { it.actName }
                TheatreLobbyScreen(
                    bestActIScore = scoreMap["actI"]?.bestScore ?: 0,
                    bestActIIScore = scoreMap["actII"]?.bestScore ?: 0,
                    bestActIIIScore = scoreMap["actIII"]?.bestScore ?: 0,
                    onPlayAct = { act ->
                        navController.navigate(Routes.actIntro(actIdOf(act), 0))
                    },
                    onPlayAll = {
                        navController.navigate(Routes.actIntro("I", 0))
                    },
                    onLeaderboard = { navController.navigate(Routes.LEADERBOARD) }
                )
            }
        }

        composable(Routes.ACT_INTRO) { entry ->
            val act = actFromId(entry.arguments?.getString(NavArgs.ACT_ID))
            val stakes = stakesArgOf(entry)
            ActIntroScreen(
                act = act,
                dialogue = container.dialogueRepository,
                onBegin = {
                    navController.navigate(actGraphRoute(act, stakes)) {
                        popUpTo(Routes.ACT_INTRO) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.INTERMISSION) { entry ->
            val intermission =
                intermissionFromId(entry.arguments?.getString(NavArgs.INTERMISSION_ID))
            val stakes = stakesArgOf(entry)
            IntermissionScreen(
                intermission = intermission,
                dialogue = container.dialogueRepository,
                onContinue = {
                    navController.navigate(
                        actGraphRoute(
                            actAfterIntermission(intermission),
                            stakes
                        )
                    ) {
                        popUpTo(Routes.INTERMISSION) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LEADERBOARD) {
            var scores by remember { mutableStateOf<List<ActScoreEntity>?>(null) }
            LaunchedEffect(Unit) {
                scores = withContext(Dispatchers.IO) { container.theatreDao.getAllBestScores() }
            }
            if (scores == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TheatreGold)
                }
            } else {
                LeaderboardScreen(
                    bestScores = scores!!,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.FINALE) { entry ->
            val priorStakes = entry.arguments?.getString(NavArgs.PRIOR_STAKES)?.toIntOrNull() ?: 0
            val actIIIScore = entry.arguments?.getString(NavArgs.ACT_III_SCORE)?.toIntOrNull() ?: 0
            GrandFinaleScreen(
                priorStakes = priorStakes,
                actIIIScore = actIIIScore,
                onReturnToLobby = {
                    navController.navigate(Routes.LOBBY) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        actINavGraph(navController, container)
        actIINavGraph(navController, container)
        actIIINavGraph(navController, container)
    }
}

private fun NavGraphBuilder.actINavGraph(
    navController: NavHostController,
    container: AppContainer
) {
    navigation(startDestination = Routes.WORD_DUEL, route = Graphs.ACT_I) {
        composable(Routes.WORD_DUEL) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Graphs.ACT_I)
            }
            val viewModel: WordDuelViewModel = viewModel(
                viewModelStoreOwner = graphEntry,
                factory = WordDuelViewModel.factory(
                    engine = container.createWordDuelEngine(),
                    ai = container.createWordDuelJokerAI(),
                    dialogue = container.dialogueRepository
                )
            )

            var showNameDialog by rememberSaveable { mutableStateOf(false) }
            var pendingScore by rememberSaveable { mutableStateOf(0) }
            val scope = androidx.compose.runtime.rememberCoroutineScope()

            WordDuelScreen(
                viewModel = viewModel,
                onActComplete = { playerActScore, _, _, _ ->
                    pendingScore = playerActScore
                    showNameDialog = true
                },
                onBack = {
                    navController.navigate(Routes.LOBBY) { popUpTo(0) { inclusive = true } }
                }
            )

            if (showNameDialog) {
                app.krafted.jokersgrandtheatre.ui.components.NameEntryDialog(
                    score = pendingScore,
                    onSave = { name ->
                        scope.launch {
                            try {
                                container.theatreDao.saveBestScore("actI", pendingScore, name)
                            } catch (_: Exception) { }
                            showNameDialog = false
                            navController.navigate(Routes.intermission("I", pendingScore)) {
                                popUpTo(Graphs.ACT_I) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun NavGraphBuilder.actIINavGraph(
    navController: NavHostController,
    container: AppContainer
) {
    val graphRoute = "${Graphs.ACT_II}/{${NavArgs.STAKES}}"
    navigation(startDestination = Routes.PATTERN_DISPLAY, route = graphRoute) {
        composable(Routes.PATTERN_DISPLAY) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(graphRoute)
            }
            val carriedStakes = stakesArgOf(graphEntry)
            val viewModel: PatternViewModel = viewModel(
                viewModelStoreOwner = graphEntry,
                factory = PatternViewModel.factory(
                    engine = container.createPatternEngine(),
                    dialogue = container.dialogueRepository
                )
            )

            var showNameDialog by rememberSaveable { mutableStateOf(false) }
            var pendingScore by rememberSaveable { mutableStateOf(0) }
            val scope = androidx.compose.runtime.rememberCoroutineScope()

            PatternInputScreen(
                viewModel = viewModel,
                onActComplete = { patternActScore, _, _ ->
                    pendingScore = patternActScore
                    showNameDialog = true
                },
                onBack = {
                    navController.navigate(Routes.LOBBY) { popUpTo(0) { inclusive = true } }
                }
            )

            if (showNameDialog) {
                app.krafted.jokersgrandtheatre.ui.components.NameEntryDialog(
                    score = pendingScore,
                    onSave = { name ->
                        scope.launch {
                            try {
                                container.theatreDao.saveBestScore("actII", pendingScore, name)
                            } catch (_: Exception) { }
                            showNameDialog = false
                            val stakes = carriedStakes + pendingScore
                            navController.navigate(Routes.intermission("II", stakes)) {
                                popUpTo(graphRoute) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun NavGraphBuilder.actIIINavGraph(
    navController: NavHostController,
    container: AppContainer
) {
    val graphRoute = "${Graphs.ACT_III}/{${NavArgs.STAKES}}"
    navigation(startDestination = Routes.GAMBLE, route = graphRoute) {
        composable(Routes.GAMBLE) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(graphRoute)
            }
            val priorStakes = stakesArgOf(graphEntry)
            val viewModel = gambleViewModel(navController, container, graphRoute, backStackEntry)

            var showNameDialog by rememberSaveable { mutableStateOf(false) }
            var pendingScore by rememberSaveable { mutableStateOf(0) }
            val scope = androidx.compose.runtime.rememberCoroutineScope()

            GambleScreen(
                viewModel = viewModel,
                onReveal = { navController.navigate(Routes.GAMBLE_REVEAL) },
                onActComplete = { gambleScore, _, _ ->
                    pendingScore = gambleScore
                    showNameDialog = true
                },
                onBack = {
                    navController.navigate(Routes.LOBBY) { popUpTo(0) { inclusive = true } }
                }
            )

            if (showNameDialog) {
                app.krafted.jokersgrandtheatre.ui.components.NameEntryDialog(
                    score = pendingScore,
                    onSave = { name ->
                        scope.launch {
                            try {
                                container.theatreDao.saveBestScore("actIII", pendingScore, name)
                            } catch (_: Exception) { }
                            showNameDialog = false
                            navController.navigate(Routes.finale(priorStakes, pendingScore))
                        }
                    }
                )
            }
        }
        composable(Routes.GAMBLE_REVEAL) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(graphRoute)
            }
            val priorStakes = stakesArgOf(graphEntry)
            val viewModel = gambleViewModel(navController, container, graphRoute, backStackEntry)

            var showNameDialog by rememberSaveable { mutableStateOf(false) }
            var pendingScore by rememberSaveable { mutableStateOf(0) }
            val scope = androidx.compose.runtime.rememberCoroutineScope()

            GambleRevealScreen(
                viewModel = viewModel,
                onContinue = { navController.popBackStack() },
                onActComplete = { gambleScore, _, _ ->
                    pendingScore = gambleScore
                    showNameDialog = true
                },
                onBack = {
                    navController.navigate(Routes.LOBBY) { popUpTo(0) { inclusive = true } }
                }
            )

            if (showNameDialog) {
                app.krafted.jokersgrandtheatre.ui.components.NameEntryDialog(
                    score = pendingScore,
                    onSave = { name ->
                        scope.launch {
                            try {
                                container.theatreDao.saveBestScore("actIII", pendingScore, name)
                            } catch (_: Exception) { }
                            showNameDialog = false
                            navController.navigate(Routes.finale(priorStakes, pendingScore)) {
                                popUpTo(graphRoute) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun gambleViewModel(
    navController: NavHostController,
    container: AppContainer,
    graphRoute: String,
    backStackEntry: NavBackStackEntry
): GambleViewModel {
    val graphEntry = remember(backStackEntry) {
        navController.getBackStackEntry(graphRoute)
    }
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
    entry.arguments?.getString(NavArgs.STAKES)?.toIntOrNull() ?: 0

private fun actIdOf(act: Act): String = when (act) {
    Act.ACT_II -> "II"
    Act.ACT_III -> "III"
    else -> "I"
}

private fun actFromId(id: String?): Act = when (id) {
    "II" -> Act.ACT_II
    "III" -> Act.ACT_III
    else -> Act.ACT_I
}

private fun intermissionFromId(id: String?): Act = when (id) {
    "II" -> Act.INTERMISSION_II
    else -> Act.INTERMISSION_I
}

private fun actAfterIntermission(intermission: Act): Act = when (intermission) {
    Act.INTERMISSION_II -> Act.ACT_III
    else -> Act.ACT_II
}

private fun actGraphRoute(act: Act, stakes: Int): String = when (act) {
    Act.ACT_II -> actIIRoute(stakes)
    Act.ACT_III -> actIIIRoute(stakes)
    else -> Graphs.ACT_I
}

private fun actIIRoute(stakes: Int) = "${Graphs.ACT_II}/$stakes"
private fun actIIIRoute(stakes: Int) = "${Graphs.ACT_III}/$stakes"
