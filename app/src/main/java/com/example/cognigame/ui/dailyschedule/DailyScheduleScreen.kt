package com.example.cognigame.ui.dailyschedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cognigame.CogniGameApp
import com.example.cognigame.ui.theme.*

data class ScheduleItem(
    val time: String,
    val activity: String,
    val icon: ImageVector,
    val color: Color,
    var completed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyScheduleScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as CogniGameApp
    val scheduleManager = remember { com.example.cognigame.data.local.ScheduleManager(context) }

    val defaultItems = listOf(
        ScheduleItem("07:00 AM", "Wake Up & Freshen Up", Icons.Default.WbSunny, GameYellow),
        ScheduleItem("07:30 AM", "Morning Exercise", Icons.Default.DirectionsWalk, Green40),
        ScheduleItem("08:00 AM", "Breakfast", Icons.Default.Restaurant, GameOrange),
        ScheduleItem("09:00 AM", "Memory Match Game", Icons.Default.GridView, GameBlue),
        ScheduleItem("10:00 AM", "Word Recall Game", Icons.Default.TextFields, GameOrange),
        ScheduleItem("11:00 AM", "Family Photo Time", Icons.Default.PhotoLibrary, GameRed),
        ScheduleItem("12:00 PM", "Lunch", Icons.Default.Restaurant, GameOrange),
        ScheduleItem("02:00 PM", "Rest / Nap", Icons.Default.Bed, GamePurple),
        ScheduleItem("03:30 PM", "Pattern Game", Icons.Default.Pattern, GamePurple),
        ScheduleItem("04:00 PM", "Evening Snack", Icons.Default.Cake, GameYellow),
        ScheduleItem("05:00 PM", "Walk / Light Activity", Icons.Default.DirectionsWalk, Green40),
        ScheduleItem("06:00 PM", "Listen to Music", Icons.Default.MusicNote, GamePurple),
        ScheduleItem("07:00 PM", "Dinner", Icons.Default.Restaurant, GameOrange),
        ScheduleItem("08:00 PM", "Family Time", Icons.Default.People, GameBlue),
        ScheduleItem("09:00 PM", "Bedtime", Icons.Default.Bed, GamePurple),
    )

    val completedMap = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        val saved = scheduleManager.getCompletedItems()
        saved.forEach { (time, completed) ->
            completedMap[time] = completed
        }
    }

    val items = defaultItems.map { it.copy(completed = completedMap[it.time] ?: false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Schedule", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green40,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    "Today's Routine",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Green40,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            itemsIndexed(items) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.completed) Green80 else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = item.color.copy(alpha = 0.15f)
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = null,
                                modifier = Modifier.padding(9.dp),
                                tint = item.color
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.time, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                item.activity,
                                fontSize = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = if (item.completed) Green40 else Color.Black
                            )
                        }
                        Checkbox(
                            checked = item.completed,
                            onCheckedChange = { checked ->
                                completedMap[item.time] = checked
                                scheduleManager.saveCompletedItems(
                                    defaultItems.map { it.time to (completedMap[it.time] ?: false) }
                                )
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Green40)
                        )
                    }
                }
            }
        }
    }
}
