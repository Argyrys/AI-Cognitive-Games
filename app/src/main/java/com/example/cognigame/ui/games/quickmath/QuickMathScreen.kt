package com.example.cognigame.ui.games.quickmath

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
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
import kotlinx.coroutines.delay

data class MathProblem(
    val num1: Int,
    val num2: Int,
    val operator: String,
    val answer: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMathScreen(
    navController: NavController,
    viewModel: GameViewModel = viewModel(
        factory = CogniGameViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    var currentProblem by remember { mutableStateOf(generateProblem()) }
    var userAnswer by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(1) }
    var totalRounds by remember { mutableIntStateOf(10) }
    var timeLeft by remember { mutableIntStateOf(10) }
    var gameOver by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var correctCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(timeLeft, gameOver) {
        if (!gameOver && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (timeLeft == 0 && !gameOver) {
            gameOver = true
            message = "Time's up!"
            viewModel.saveGameSession(
                GameSession(
                    userId = "user_1",
                    gameType = "quick_math",
                    score = score,
                    maxScore = totalRounds * 10,
                    roundsPlayed = round - 1,
                    duration = System.currentTimeMillis() - startTime
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Math", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GameYellow,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
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
                Text("Score: $score", style = MaterialTheme.typography.titleLarge, color = GameYellow)
                Text("Time: ${timeLeft}s", style = MaterialTheme.typography.titleLarge, color = GameRed)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = timeLeft / 10f,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (timeLeft > 5) Green40 else GameRed,
                trackColor = Green40.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!gameOver) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Solve:", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "${currentProblem.num1} ${currentProblem.operator} ${currentProblem.num2} = ?",
                            fontSize = 36.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = userAnswer,
                            onValueChange = { userAnswer = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Your answer", fontSize = 18.sp) },
                            textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, textAlign = TextAlign.Center),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val answer = userAnswer.toIntOrNull()
                                if (answer == currentProblem.answer) {
                                    score += 10
                                    correctCount++
                                    message = "Correct!"
                                } else {
                                    message = "Wrong! Answer was ${currentProblem.answer}"
                                }
                                userAnswer = ""
                                round++
                                if (round > totalRounds) {
                                    gameOver = true
                                    viewModel.saveGameSession(
                                        GameSession(
                                            userId = "user_1",
                                            gameType = "quick_math",
                                            score = score,
                                            maxScore = totalRounds * 10,
                                            roundsPlayed = totalRounds,
                                            duration = System.currentTimeMillis() - startTime
                                        )
                                    )
                                } else {
                                    currentProblem = generateProblem()
                                    timeLeft = 10
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GameYellow)
                        ) {
                            Text("Submit", fontSize = 18.sp)
                        }
                    }
                }

                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (message.startsWith("Correct")) Green80 else GameRed.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = if (message.startsWith("Correct")) Green40 else GameRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Problem $round of $totalRounds", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Text("Game Over!", fontSize = 24.sp, color = Green40)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Score: $score / ${totalRounds * 10}", fontSize = 20.sp)
                        Text("Correct: $correctCount / $totalRounds", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                score = 0
                                round = 1
                                correctCount = 0
                                timeLeft = 10
                                gameOver = false
                                message = ""
                                startTime = System.currentTimeMillis()
                                currentProblem = generateProblem()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GameYellow)
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

private fun generateProblem(): MathProblem {
    val operators = listOf("+", "-", "\u00D7")
    val operator = operators.random()
    val num1: Int
    val num2: Int
    val answer: Int

    when (operator) {
        "+" -> {
            num1 = (1..50).random()
            num2 = (1..50).random()
            answer = num1 + num2
        }
        "-" -> {
            num1 = (10..50).random()
            num2 = (1..num1).random()
            answer = num1 - num2
        }
        else -> {
            num1 = (1..12).random()
            num2 = (1..12).random()
            answer = num1 * num2
        }
    }

    return MathProblem(num1, num2, operator, answer)
}
