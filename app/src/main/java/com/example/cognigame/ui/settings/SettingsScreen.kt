package com.example.cognigame.ui.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cognigame.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var textScale by remember { mutableStateOf(1.0f) }
    var highContrast by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    val languages = listOf("English", "Assamese", "Bengali", "Manipuri", "Hindi", "Nagamese")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontSize = 22.sp) },
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
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Accessibility & Language",
                style = MaterialTheme.typography.headlineMedium,
                color = Teal40,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Language", fontSize = 18.sp, color = Teal40)
                    Spacer(modifier = Modifier.height(8.dp))
                    for (lang in languages) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang,
                                onClick = { selectedLanguage = lang },
                                colors = RadioButtonDefaults.colors(selectedColor = Teal40)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, fontSize = 16.sp)
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Text Size", fontSize = 18.sp, color = Teal40)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("A", fontSize = 14.sp)
                        Slider(
                            value = textScale,
                            onValueChange = { textScale = it },
                            valueRange = 0.8f..1.5f,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(thumbColor = Teal40, activeTrackColor = Teal40)
                        )
                        Text("A", fontSize = 24.sp)
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggle(
                        icon = Icons.Default.Contrast,
                        title = "High Contrast",
                        subtitle = "Makes text and buttons easier to see",
                        checked = highContrast,
                        onCheckedChange = { highContrast = it },
                        color = Teal40
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    SettingToggle(
                        icon = Icons.Default.VolumeUp,
                        title = "Sound Effects",
                        subtitle = "Play sounds during games",
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it },
                        color = Green40
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    SettingToggle(
                        icon = Icons.Default.Vibration,
                        title = "Vibration",
                        subtitle = "Vibrate on actions",
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it },
                        color = GameBlue
                    )
                }
            }
        }
    }
}

@Composable
fun SettingToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = color)
        )
    }
}
