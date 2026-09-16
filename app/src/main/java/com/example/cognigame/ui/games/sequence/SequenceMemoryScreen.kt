package com.example.cognigame.ui.games.sequence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequenceMemoryScreen(
    navController: NavController,
    viewModel: GameViewModel = viewModel(
        factory = CogniGameViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var userSequence by remember { mutableStateOf(listOf<Int>()) }
    var showingSequence by remember { mutableStateOf(false) }
    var highlightedNumber by remember { mutableIntStateOf(-1) }
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var canTap by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    fun generateNewRound() {
        sequence = sequence + (1..9).random()
        userSequence = emptyList()
        message = "Watch the sequence!"
        showingSequence = true
        canTap = false
    }

    LaunchedEffect(round) {
        if (round == 1) generateNewRound()
    }

    LaunchedEffect(showingSequence) {
        if (showingSequence && sequence.isNotEmpty()) {
            delay(500)
            for (num in sequence) {
                highlightedNumber = num
                delay(700)
                highlightedNumber = -1
                delay(300)
            }
            showingSequence = false
            canTap = true
            message = "Your turn! Tap the numbers in order"
        }
    }

    fun checkAnswer() {
        if (userSequence == sequence) {
            score += round * 15
            message = "Correct! +${round * 15} points"
            round++
            canTap = false
            generateNewRound()
        } else {
            gameOver = true
            message = "Wrong sequence! Game over"
            val duration = System.currentTimeMillis() - startTime
            viewModel.saveGameSession(
                GameSession(
                    userId = "user_1",
                    gameType = "sequence_memory",
                    score = score,
                    maxScore = 100,
                    roundsPlayed = round - 1,
                    duration = duration
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sequence Memory", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GameRed,
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
                Text("Round: $round", style = MaterialTheme.typography.titleLarge, color = GameRed)
                Text("Score: $score", style = MaterialTheme.typography.titleLarge, color = GameOrange)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(message, fontSize = 16.sp, color = GameRed, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))

                    val numberColors = listOf(
                        GameRed, GameBlue, GameOrange,
                        GamePurple, Green40, GameYellow,
                        Teal40, GameRed, GameBlue
                    )

                    for (row in 0 until 3) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.padding(bottom = 14.dp)
                        ) {
                            for (col in 0 until 3) {
                                val number = row * 3 + col + 1
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (highlightedNumber == number)
                                                numberColors[number - 1]
                                            else numberColors[number - 1].copy(alpha = 0.3f)
                                        )
                                        .border(
                                            width = if (userSequence.contains(number)) 4.dp else 2.dp,
                                            color = if (userSequence.contains(number)) numberColors[number - 1] else numberColors[number - 1].copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            if (canTap && !gameOver) {
                                                userSequence = userSequence + number
                                                if (userSequence.size == sequence.size) {
                                                    canTap = false
                                                    checkAnswer()
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$number",
                                        fontSize = 24.sp,
                                        color = if (highlightedNumber == number) Color.White else numberColors[number - 1],
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (userSequence.isNotEmpty()) {
                        Text("Your sequence: ${userSequence.size}/${sequence.size}", fontSize = 14.sp)
                    }
                }
            }

            if (gameOver) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = GameRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Game Over!", fontSize = 22.sp, color = GameRed)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Final Score: $score", fontSize = 18.sp)
                        Text("Rounds completed: ${round - 1}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                sequence = emptyList()
                                userSequence = emptyList()
                                round = 1
                                score = 0
                                gameOver = false
                                message = ""
                                startTime = System.currentTimeMillis()
                                generateNewRound()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GameRed)
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
