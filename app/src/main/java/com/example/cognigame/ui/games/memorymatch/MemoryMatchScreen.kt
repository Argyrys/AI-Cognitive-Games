package com.example.cognigame.ui.games.memorymatch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cognigame.data.model.GameSession
import com.example.cognigame.ui.theme.*
import com.example.cognigame.ui.viewmodel.GameViewModel
import kotlin.random.Random

data class CardItem(
    val id: Int,
    val emoji: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMatchScreen(
    navController: NavController,
    viewModel: GameViewModel = viewModel()
) {
    val emojis = listOf("\uD83C\uDF4E", "\uD83C\uDF4A", "\uD83C\uDF49", "\uD83C\uDF48", "\uD83D\uDC8E", "\u2B50", "\uD83C\uDF3B", "\uD83C\uDF3A")
    var cards by remember { mutableStateOf(generateCards(emojis)) }
    var flippedCards by remember { mutableStateOf(listOf<Int>()) }
    var moves by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(flippedCards) {
        if (flippedCards.size == 2) {
            kotlinx.coroutines.delay(800)
            val first = cards.find { it.id == flippedCards[0] }
            val second = cards.find { it.id == flippedCards[1] }
            if (first != null && second != null && first.emoji == second.emoji) {
                cards = cards.map {
                    if (it.id == first.id || it.id == second.id) it.copy(isMatched = true)
                    else it
                }
                matchedPairs++
                message = "Great job!"
            } else {
                cards = cards.map {
                    if (it.id == first?.id || it.id == second?.id) it.copy(isFlipped = false)
                    else it
                }
            }
            moves++
            flippedCards = emptyList()
        }
    }

    LaunchedEffect(matchedPairs) {
        if (matchedPairs == emojis.size && matchedPairs > 0) {
            gameOver = true
            message = "You won! All pairs matched!"
            val duration = System.currentTimeMillis() - startTime
            val score = maxOf(0, 100 - (moves * 2))
            viewModel.saveGameSession(
                GameSession(
                    userId = "user_1",
                    gameType = "memory_match",
                    score = score,
                    maxScore = 100,
                    roundsPlayed = 1,
                    duration = duration
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memory Match", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GameBlue,
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
                Text("Moves: $moves", style = MaterialTheme.typography.titleMedium, color = GameBlue)
                Text("Matched: $matchedPairs/${emojis.size}", style = MaterialTheme.typography.titleMedium, color = Green40)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (message.isNotEmpty() && !gameOver) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Green80),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = Green40
                    )
                }
            }

            val columns = 4
            val rows = (cards.size + columns - 1) / columns
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until columns) {
                        val index = row * columns + col
                        if (index < cards.size) {
                            val card = cards[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.8f)
                                    .clickable {
                                        if (!card.isFlipped && !card.isMatched && flippedCards.size < 2 && !gameOver) {
                                            cards = cards.map {
                                                if (it.id == card.id) it.copy(isFlipped = true) else it
                                            }
                                            flippedCards = flippedCards + card.id
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            card.isMatched -> Green80
                                            card.isFlipped -> Color.White
                                            else -> CardBack
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (card.isFlipped || card.isMatched) {
                                            Text(text = card.emoji, fontSize = 32.sp)
                                        } else {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = "?",
                                                tint = Color.White.copy(alpha = 0.5f),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (gameOver) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Green80),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Congratulations!", fontSize = 22.sp, color = Green40)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("You matched all pairs in $moves moves!", fontSize = 16.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                cards = generateCards(emojis)
                                flippedCards = emptyList()
                                moves = 0
                                matchedPairs = 0
                                gameOver = false
                                message = ""
                                startTime = System.currentTimeMillis()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GameBlue)
                        ) {
                            Text("Play Again", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun generateCards(emojis: List<String>): List<CardItem> {
    val pairs = (emojis + emojis).shuffled(Random)
    return pairs.mapIndexed { index, emoji ->
        CardItem(id = index, emoji = emoji)
    }
}
