package com.example.cognigame.ui.memorybook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.cognigame.data.local.MemoryBookManager
import com.example.cognigame.ui.theme.*

data class FamilyMember(
    val name: String,
    val relationship: String,
    val icon: ImageVector,
    val color: Color
)

private fun String.toColor(): Color = when (this) {
    "GameRed" -> GameRed
    "GameBlue" -> GameBlue
    "GamePurple" -> GamePurple
    "GameOrange" -> GameOrange
    "GameYellow" -> GameYellow
    "Green40" -> Green40
    "Teal40" -> Teal40
    else -> Green40
}

private fun Color.toColorName(): String = when (this) {
    GameRed -> "GameRed"
    GameBlue -> "GameBlue"
    GamePurple -> "GamePurple"
    GameOrange -> "GameOrange"
    GameYellow -> "GameYellow"
    Green40 -> "Green40"
    Teal40 -> "Teal40"
    else -> "Green40"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryBookScreen(navController: NavController) {
    val context = LocalContext.current
    val memoryBookManager = remember { MemoryBookManager(context) }

    val defaultMembers = listOf(
        FamilyMember("Amma", "Mother", Icons.Default.Female, GameRed),
        FamilyMember("Appa", "Father", Icons.Default.Male, GameBlue),
        FamilyMember("Grandma", "Grandmother", Icons.Default.Woman, GamePurple),
        FamilyMember("Grandpa", "Grandfather", Icons.Default.Man, Green40),
        FamilyMember("Rahul", "Brother", Icons.Default.Boy, GameOrange),
        FamilyMember("Priya", "Sister", Icons.Default.Girl, GameYellow),
        FamilyMember("Dr. Sharma", "Doctor", Icons.Default.LocalHospital, GameBlue),
        FamilyMember("Neighbor", "Friend", Icons.Default.People, Teal40),
    )

    var members by remember { mutableStateOf(defaultMembers) }

    LaunchedEffect(Unit) {
        val saved = memoryBookManager.getMembers()
        if (saved.isNotEmpty()) {
            members = saved.map { (name, relationship, colorName) ->
                FamilyMember(name, relationship, Icons.Default.Person, colorName.toColor())
            }
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newRelation by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memory Book", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(28.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, "Add Person", modifier = Modifier.size(28.dp), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GameRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(bottom = 4.dp)) {
                    Text(
                        "People You Know",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GameRed
                    )
                    Text(
                        "Tap a photo to see their name and relationship",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(members) { member ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = member.color.copy(alpha = 0.15f)
                        ) {
                            Icon(
                                member.icon,
                                contentDescription = member.name,
                                modifier = Modifier.padding(14.dp),
                                tint = member.color
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            member.name,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            member.relationship,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Person") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newRelation,
                        onValueChange = { newRelation = it },
                        label = { Text("Relationship") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank() && newRelation.isNotBlank()) {
                        val newMember = FamilyMember(newName, newRelation, Icons.Default.Person, Green40)
                        members = members + newMember
                        memoryBookManager.saveMembers(
                            members.map { Triple(it.name, it.relationship, it.color.toColorName()) }
                        )
                        newName = ""
                        newRelation = ""
                        showDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
