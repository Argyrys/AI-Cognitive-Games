package com.example.cognigame.ui.games.emojipuzzle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cognigame.data.model.GameSession
import com.example.cognigame.ui.theme.*
import com.example.cognigame.ui.viewmodel.CogniGameViewModelFactory
import com.example.cognigame.ui.viewmodel.GameViewModel

data class EmojiPuzzle(
    val emoji: String,
    val description: String,
    val options: List<String>,
    val correctIndex: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPuzzleScreen(
    navController: NavController,
    viewModel: GameViewModel = viewModel(
        factory = CogniGameViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val puzzles = listOf(
        EmojiPuzzle("\uD83C\uDF19", "Moon", listOf("Sun", "Moon", "Star", "Cloud"), 1),
        EmojiPuzzle("\uD83C\uDF0A", "Ocean Wave", listOf("Mountain", "Ocean Wave", "River", "Rain"), 1),
        EmojiPuzzle("\uD83C\uDF3B", "Corn", listOf("Wheat", "Corn", "Rice", "Flower"), 1),
        EmojiPuzzle("\uD83D\uDC83", "Dance", listOf("Sleep", "Run", "Dance", "Eat"), 2),
        EmojiPuzzle("\u2615", "Coffee", listOf("Tea", "Juice", "Coffee", "Milk"), 2),
        EmojiPuzzle("\uD83C\uDFA8", "Painting", listOf("Photo", "Painting", "Drawing", "Mirror"), 1),
        EmojiPuzzle("\uD83D\uDD25", "Fire", listOf("Water", "Fire", "Wind", "Earth"), 1),
        EmojiPuzzle("\uD83C\uDF3E", "Corn", listOf("Tomato", "Corn", "Potato", "Carrot"), 1),
        EmojiPuzzle("\uD83D\uDE97", "Car", listOf("Bus", "Car", "Bike", "Train"), 1),
        EmojiPuzzle("\uD83C\uDFB5", "Music", listOf("Dance", "Song", "Music", "Movie"), 2),
    )

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val currentPuzzle = if (currentIndex < puzzles.size) puzzles[currentIndex] else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emoji Puzzle", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GamePurple,
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: $score", style = MaterialTheme.typography.titleLarge, color = GamePurple)
                Text("Question: ${currentIndex + 1}/${puzzles.size}", style = MaterialTheme.typography.titleLarge, color = GameOrange)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!gameOver && currentPuzzle != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("What does this emoji mean?", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = currentPuzzle.emoji, fontSize = 72.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        currentPuzzle.options.forEachIndexed { index, option ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        if (!showResult) {
                                            selectedAnswer = index
                                            showResult = true
                                            if (index == currentPuzzle.correctIndex) {
                                                score += 10
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        showResult && index == currentPuzzle.correctIndex -> Green80
                                        showResult && index == selectedAnswer -> GameRed.copy(alpha = 0.1f)
                                        else -> Color.White
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    color = when {
                                        showResult && index == currentPuzzle.correctIndex -> Green40
                                        showResult && index == selectedAnswer -> GameRed
                                        else -> Color.Black
                                    }
                                )
                            }
                        }
                    }
                }

                if (showResult) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            currentIndex++
                            selectedAnswer = -1
                            showResult = false
                            if (currentIndex >= puzzles.size) {
                                gameOver = true
                                val duration = System.currentTimeMillis() - startTime
                                viewModel.saveGameSession(
                                    GameSession(
                                        userId = "user_1",
                                        gameType = "emoji_puzzle",
                                        score = score,
                                        maxScore = puzzles.size * 10,
                                        roundsPlayed = puzzles.size,
                                        duration = duration
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GamePurple)
                    ) {
                        Text("Next", fontSize = 18.sp)
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Green80),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Puzzle Complete!", fontSize = 24.sp, color = Green40)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Score: $score / ${puzzles.size * 10}", fontSize = 20.sp)
                        val percentage = (score * 100) / (puzzles.size * 10)
                        Text(
                            when {
                                percentage >= 80 -> "Excellent memory!"
                                percentage >= 50 -> "Good job! Keep practicing!"
                                else -> "Try again to improve!"
                            },
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                currentIndex = 0
                                score = 0
                                selectedAnswer = -1
                                showResult = false
                                gameOver = false
                                startTime = System.currentTimeMillis()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamePurple)
                        ) {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Play Again", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
