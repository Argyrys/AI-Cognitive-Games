package com.example.cognigame.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cognigame.ui.theme.*
import com.example.cognigame.ui.viewmodel.GameViewModel

data class DisplayScore(
    val game: String,
    val score: Int,
    val maxScore: Int,
    val icon: ImageVector,
    val color: Color,
    val trend: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: GameViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadUserSessions("user_1")
    }

    val cognitiveScores by viewModel.cognitiveScores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val scores = if (cognitiveScores.isEmpty()) {
        listOf(
            DisplayScore("Memory Match", 0, 100, Icons.Default.GridView, GameBlue, "Play to start"),
            DisplayScore("Word Recall", 0, 100, Icons.Default.TextFields, GameOrange, "Play to start"),
            DisplayScore("Pattern Game", 0, 100, Icons.Default.Pattern, GamePurple, "Play to start"),
            DisplayScore("Sequence Memory", 0, 100, Icons.Default.Looks, GameRed, "Play to start"),
            DisplayScore("Quick Math", 0, 100, Icons.Default.Calculate, GameYellow, "Play to start"),
            DisplayScore("Emoji Puzzle", 0, 100, Icons.Default.EmojiEmotions, Teal40, "Play to start")
        )
    } else {
        cognitiveScores.map { score ->
            DisplayScore(
                game = when (score.gameType) {
                    "memory_match" -> "Memory Match"
                    "word_recall" -> "Word Recall"
                    "pattern_game" -> "Pattern Game"
                    "sequence_memory" -> "Sequence Memory"
                    "quick_math" -> "Quick Math"
                    "emoji_puzzle" -> "Emoji Puzzle"
                    else -> score.gameType
                },
                score = score.score,
                maxScore = score.maxScore,
                icon = when (score.gameType) {
                    "memory_match" -> Icons.Default.GridView
                    "word_recall" -> Icons.Default.TextFields
                    "pattern_game" -> Icons.Default.Pattern
                    "sequence_memory" -> Icons.Default.Looks
                    "quick_math" -> Icons.Default.Calculate
                    "emoji_puzzle" -> Icons.Default.EmojiEmotions
                    else -> Icons.Default.Gamepad
                },
                color = when (score.gameType) {
                    "memory_match" -> GameBlue
                    "word_recall" -> GameOrange
                    "pattern_game" -> GamePurple
                    "sequence_memory" -> GameRed
                    "quick_math" -> GameYellow
                    "emoji_puzzle" -> Teal40
                    else -> Green40
                },
                trend = score.trend
            )
        }
    }

    val overallScore = if (scores.isEmpty()) 0 else scores.map { it.score }.average().toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Progress", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal40,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Teal40)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Overall Cognitive Score", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$overallScore%", fontSize = 44.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        when {
                            overallScore >= 80 -> "Excellent performance!"
                            overallScore >= 60 -> "Good progress, keep going!"
                            else -> "Keep practicing, you'll improve!"
                        },
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Text(
                "Game Scores",
                style = MaterialTheme.typography.titleLarge,
                color = Teal40,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            for (score in scores) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = score.color.copy(alpha = 0.15f)
                            ) {
                                Icon(
                                    score.icon,
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp),
                                    tint = score.color
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(score.game, fontSize = 15.sp, color = Color.Black)
                                Text(score.trend, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("${score.score}%", fontSize = 18.sp, color = score.color)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = (score.score.toFloat() / score.maxScore.toFloat()).coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = score.color,
                            trackColor = score.color.copy(alpha = 0.15f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GameYellow.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Lightbulb, null, tint = GameYellow, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Tip for Today", fontSize = 14.sp, color = GameYellow)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Try playing Memory Match 2-3 times today. Repetition helps strengthen memory!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
