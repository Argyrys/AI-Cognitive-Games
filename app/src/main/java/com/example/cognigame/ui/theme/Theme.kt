package com.example.cognigame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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

private val HighContrastScheme = lightColorScheme(
    primary = Color(0xFF006B3F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF00A86B),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF3E6352),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5A7F6C),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF006B5A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF00A890),
    onTertiaryContainer = Color.White,
    background = Color(0xFFF5FBF5),
    onBackground = Color(0xFF000000),
    surface = Color.White,
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFD5EDDA),
    onSurfaceVariant = Color(0xFF000000),
)

fun scaledTypography(textScale: Float) = Typography.copy(
    headlineLarge = Typography.headlineLarge.copy(fontSize = Typography.headlineLarge.fontSize * textScale),
    headlineMedium = Typography.headlineMedium.copy(fontSize = Typography.headlineMedium.fontSize * textScale),
    titleLarge = Typography.titleLarge.copy(fontSize = Typography.titleLarge.fontSize * textScale),
    titleMedium = Typography.titleMedium.copy(fontSize = Typography.titleMedium.fontSize * textScale),
    bodyLarge = Typography.bodyLarge.copy(fontSize = Typography.bodyLarge.fontSize * textScale),
    bodyMedium = Typography.bodyMedium.copy(fontSize = Typography.bodyMedium.fontSize * textScale),
    labelLarge = Typography.labelLarge.copy(fontSize = Typography.labelLarge.fontSize * textScale)
)

@Composable
fun CogniGameTheme(
    textScale: Float = 1.0f,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (highContrast) HighContrastScheme else LightColorScheme
    val typography = scaledTypography(textScale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
