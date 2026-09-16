package com.example.cognigame.ui.games.wordrecall

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

enum class GamePhase { SHOWING, GUESSING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordRecallScreen(
    navController: NavController,
    viewModel: GameViewModel = viewModel(
        factory = CogniGameViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val wordSets = listOf(
        listOf("\uD83C\uDF4E Apple", "\uD83D\uDC36 Dog", "\u2600\uFE0F Sun", "\uD83C\uDFE0 House", "\uD83C\uDF55 Pizza"),
        listOf("\uD83C\uDF40 Leaf", "\uD83D\uDE97 Car", "\uD83C\uDF19 Moon", "\uD83D\uDCA8 Star", "\uD83C\uDF3F Grass"),
        listOf("\uD83C\uDFAF Target", "\uD83D\uDC8E Diamond", "\uD83C\uDFB5 Music", "\uD83C\uDFA8 Paint", "\u2615 Coffee"),
        listOf("\uD83D\uDC31 Cat", "\uD83C\uDF0A Wave", "\uD83D\uDD25 Fire", "\uD83C\uDF3E Corn", "\uD83D\uDC83 Dance"),
    )
    var currentSet by remember { mutableStateOf(wordSets.random()) }
    var phase by remember { mutableStateOf(GamePhase.SHOWING) }
    var timeLeft by remember { mutableIntStateOf(5) }
    var userGuesses by remember { mutableStateOf(listOf<String>()) }
    var currentInput by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(1) }
    var resultMessage by remember { mutableStateOf("") }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var totalRounds by remember { mutableIntStateOf(0) }

    LaunchedEffect(phase, timeLeft) {
        if (phase == GamePhase.SHOWING && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (phase == GamePhase.SHOWING && timeLeft == 0) {
            phase = GamePhase.GUESSING
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Recall", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GameOrange,
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Round $round", style = MaterialTheme.typography.titleLarge, color = GameOrange)
            Spacer(modifier = Modifier.height(8.dp))

            when (phase) {
                GamePhase.SHOWING -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GameOrange.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Memorize these words:", fontSize = 18.sp, color = GameOrange)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Time left: ${timeLeft}s", fontSize = 24.sp, color = GameRed)
                            Spacer(modifier = Modifier.height(16.dp))
                            for (word in currentSet) {
                                Text(word, fontSize = 24.sp, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }
                }

                GamePhase.GUESSING -> {
                    Text("Type the words you remember:", fontSize = 16.sp, color = Green40)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = currentInput,
                        onValueChange = { currentInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Type a word and press Add", fontSize = 15.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val trimmed = currentInput.trim()
                            if (trimmed.isNotBlank() && userGuesses.none { it.equals(trimmed, ignoreCase = true) }) {
                                userGuesses = userGuesses + trimmed
                                currentInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GameOrange),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Add Word", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (userGuesses.isNotEmpty()) {
                        Text("Your answers:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        for (answer in userGuesses) {
                            val isCorrect = currentSet.any { fullWord ->
                                val wordOnly = fullWord.replace(Regex("[^a-zA-Z]"), "")
                                wordOnly.equals(answer, ignoreCase = true)
                            }
                            Text(
                                text = if (isCorrect) "\u2705 $answer" else "\u274C $answer",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val uniqueGuesses = userGuesses.distinct()
                            var correctCount = 0
                            for (answer in uniqueGuesses) {
                                val found = currentSet.any { fullWord ->
                                    val wordOnly = fullWord.replace(Regex("[^a-zA-Z]"), "")
                                    wordOnly.equals(answer, ignoreCase = true)
                                }
                                if (found) correctCount++
                            }
                            score += correctCount
                            totalRounds++
                            resultMessage = "You remembered $correctCount out of ${currentSet.size} words!"
                            phase = GamePhase.RESULT
                            val duration = System.currentTimeMillis() - startTime
                            viewModel.saveGameSession(
                                GameSession(
                                    userId = "user_1",
                                    gameType = "word_recall",
                                    score = score,
                                    maxScore = totalRounds * currentSet.size,
                                    roundsPlayed = totalRounds,
                                    duration = duration
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Green40),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Check Answers", fontSize = 16.sp)
                    }
                }

                GamePhase.RESULT -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Green80),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Round $round Complete!", fontSize = 20.sp, color = Green40)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(resultMessage, fontSize = 16.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Total Score: $score", fontSize = 22.sp, color = GameOrange)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    round++
                                    currentSet = wordSets.random()
                                    timeLeft = 5
                                    userGuesses = emptyList()
                                    currentInput = ""
                                    resultMessage = ""
                                    phase = GamePhase.SHOWING
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GameOrange)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Next Round", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
