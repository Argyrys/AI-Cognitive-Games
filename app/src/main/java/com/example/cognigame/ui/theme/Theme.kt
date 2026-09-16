package com.example.cognigame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green80,
    onPrimaryContainer = Color(0xFF002204),
    secondary = GreenGrey40,
    onSecondary = Color.White,
    secondaryContainer = GreenGrey80,
    onSecondaryContainer = Color(0xFF002204),
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Teal80,
    onTertiaryContainer = Color(0xFF002020),
    background = LightBackground,
    onBackground = Color(0xFF1A1C18),
    surface = Color.White,
    onSurface = Color(0xFF1A1C18),
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFF424940),
)

@Composable
fun CogniGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
