package com.example.cognigame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CogniGameTheme {
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
                    }
                }
            }
        }
    }
}
