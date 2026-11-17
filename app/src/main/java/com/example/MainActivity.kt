package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DragDropGameScreen
import com.example.ui.screens.InteractiveStoryScreen
import com.example.ui.screens.MainHubScreen
import com.example.ui.screens.MemoryGameScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReviewAppNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewAppNavigation(
    modifier: Modifier = Modifier,
    quizViewModel: QuizViewModel = viewModel()
) {
    val navController = rememberNavController()
    val isProfileCompleted by quizViewModel.isProfileCompleted.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "hub",
        modifier = modifier
    ) {
        composable("welcome") {
            WelcomeScreen(
                viewModel = quizViewModel,
                onStartClick = {
                    if (!isProfileCompleted) {
                        navController.navigate("profile") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        navController.navigate("hub") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                },
                onNavigateToSection = { section ->
                    when (section) {
                        "hub", "quiz" -> navController.navigate("hub")
                        "drag_drop" -> navController.navigate("drag_drop")
                        "memory_game" -> navController.navigate("memory_game")
                        "interactive_story" -> navController.navigate("interactive_story")
                        "dashboard" -> navController.navigate("dashboard")
                        "rewards" -> navController.navigate("rewards")
                        "profile" -> navController.navigate("profile")
                        else -> navController.navigate("hub")
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                viewModel = quizViewModel,
                onNavigateToHub = {
                    if (!navController.popBackStack("hub", inclusive = false)) {
                        navController.navigate("hub") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("hub") {
            MainHubScreen(
                viewModel = quizViewModel,
                onNavigateToQuiz = { subject ->
                    quizViewModel.startQuiz(subject)
                    navController.navigate("quiz")
                },
                onNavigateToDailyChallenge = {
                    quizViewModel.startDailyChallenge()
                    navController.navigate("quiz")
                },
                onNavigateToDashboard = {
                    navController.navigate("dashboard")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToRewards = {
                    navController.navigate("rewards")
                },
                onNavigateToDragDrop = {
                    navController.navigate("drag_drop")
                },
                onNavigateToMemoryGame = {
                    navController.navigate("memory_game")
                },
                onNavigateToStory = {
                    navController.navigate("interactive_story")
                }
            )
        }

        composable("rewards") {
            RewardsScreen(
                viewModel = quizViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("drag_drop") {
            DragDropGameScreen(
                viewModel = quizViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("memory_game") {
            MemoryGameScreen(
                viewModel = quizViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("interactive_story") {
            InteractiveStoryScreen(
                viewModel = quizViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("quiz") {
            QuizScreen(
                viewModel = quizViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "quiz/{subject}",
            arguments = listOf(
                navArgument("subject") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectName = backStackEntry.arguments?.getString("subject") ?: ""
            if (subjectName.isNotBlank()) {
                quizViewModel.startQuiz(subjectName)
            }
            QuizScreen(
                viewModel = quizViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = quizViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
