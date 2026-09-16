package com.example.cognigame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cognigame.ui.navigation.Routes
import com.example.cognigame.ui.theme.CogniGameTheme
import com.example.cognigame.ui.home.HomeScreen
import com.example.cognigame.ui.games.memorymatch.MemoryMatchScreen
import com.example.cognigame.ui.games.wordrecall.WordRecallScreen
import com.example.cognigame.ui.games.pattern.PatternGameScreen
import com.example.cognigame.ui.dailyschedule.DailyScheduleScreen
import com.example.cognigame.ui.memorybook.MemoryBookScreen
import com.example.cognigame.ui.settings.SettingsScreen
import com.example.cognigame.ui.reports.ReportsScreen
import com.example.cognigame.ui.profile.ProfileScreen
import com.example.cognigame.ui.games.sequence.SequenceMemoryScreen
import com.example.cognigame.ui.games.quickmath.QuickMathScreen
import com.example.cognigame.ui.games.emojipuzzle.EmojiPuzzleScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as CogniGameApp
        val settingsManager = app.settingsManager

        setContent {
            val textScale by settingsManager.textScale.collectAsState()
            val highContrast by settingsManager.highContrast.collectAsState()

            CogniGameTheme(
                textScale = textScale,
                highContrast = highContrast
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Routes.HOME) {
                        composable(Routes.HOME) {
                            HomeScreen(navController = navController)
                        }
                        composable(Routes.MEMORY_MATCH) {
                            MemoryMatchScreen(navController = navController)
                        }
                        composable(Routes.WORD_RECALL) {
                            WordRecallScreen(navController = navController)
                        }
                        composable(Routes.PATTERN_GAME) {
                            PatternGameScreen(navController = navController)
                        }
                        composable(Routes.DAILY_SCHEDULE) {
                            DailyScheduleScreen(navController = navController)
                        }
                        composable(Routes.MEMORY_BOOK) {
                            MemoryBookScreen(navController = navController)
                        }
                        composable(Routes.SETTINGS) {
                            SettingsScreen(navController = navController)
                        }
                        composable(Routes.REPORTS) {
                            ReportsScreen(navController = navController)
                        }
                        composable(Routes.PROFILE) {
                            ProfileScreen(navController = navController)
                        }
                        composable(Routes.SEQUENCE_MEMORY) {
                            SequenceMemoryScreen(navController = navController)
                        }
                        composable(Routes.QUICK_MATH) {
                            QuickMathScreen(navController = navController)
                        }
                        composable(Routes.EMOJI_PUZZLE) {
                            EmojiPuzzleScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
