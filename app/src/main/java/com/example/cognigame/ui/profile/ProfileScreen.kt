package com.example.cognigame.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cognigame.data.model.UserProfile
import com.example.cognigame.ui.theme.*
import com.example.cognigame.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            age = if (it.age > 0) it.age.toString() else ""
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            isEditing = false
            viewModel.resetSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontSize = 22.sp) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                color = Teal40.copy(alpha = 0.15f)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.padding(24.dp),
                    tint = Teal40
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (profile != null && !isEditing) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth()
                    ) {
                        ProfileField("Name", profile?.name ?: "Not set")
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileField("Age", if (profile?.age != null && profile!!.age > 0) "${profile!!.age} years" else "Not set")
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileField("Member since", formatTimestamp(profile?.createdAt ?: 0L))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal40)
                ) {
                    Text("Edit Profile", fontSize = 16.sp)
                }
            } else {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth()
                    ) {
                        Text(
                            "Tell us about yourself",
                            fontSize = 18.sp,
                            color = Teal40,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Your Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = age,
                            onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
                            label = { Text("Your Age") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val ageInt = age.toIntOrNull() ?: 0
                        viewModel.saveProfile(
                            UserProfile(
                                id = "user_1",
                                name = name.trim(),
                                age = ageInt
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal40),
                    enabled = name.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Profile", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}

fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown"
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
