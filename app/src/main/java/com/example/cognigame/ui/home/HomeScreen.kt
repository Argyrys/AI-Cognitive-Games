package com.example.cognigame.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cognigame.ui.navigation.Routes
import com.example.cognigame.ui.theme.*

data class GameTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val games = listOf(
        GameTile("Memory Match", "Match pairs", Icons.Default.GridView, GameBlue, Routes.MEMORY_MATCH),
        GameTile("Word Recall", "Remember words", Icons.Default.TextFields, GameOrange, Routes.WORD_RECALL),
        GameTile("Pattern Game", "Find sequence", Icons.Default.Pattern, GamePurple, Routes.PATTERN_GAME),
        GameTile("Daily Schedule", "Your routine", Icons.Default.Today, Green40, Routes.DAILY_SCHEDULE),
        GameTile("Memory Book", "Family & friends", Icons.Default.PhotoLibrary, GameRed, Routes.MEMORY_BOOK),
        GameTile("My Progress", "View scores", Icons.Default.BarChart, Teal40, Routes.REPORTS),
        GameTile("My Profile", "Account info", Icons.Default.Person, Teal40, Routes.PROFILE),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("CogniGame", fontSize = 24.sp)
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green40,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(bottom = 4.dp)) {
                    Text(
                        text = "Hello! What would you like to do today?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Green40
                    )
                    Text(
                        text = "Tap a game to start",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            items(games) { game ->
                GameCard(game = game) {
                    navController.navigate(game.route)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(game: GameTile, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(14.dp),
                color = game.color.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = game.icon,
                    contentDescription = game.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(11.dp),
                    tint = game.color
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = game.subtitle,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
