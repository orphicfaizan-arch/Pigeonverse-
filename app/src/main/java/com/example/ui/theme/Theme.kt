package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PigeonDarkColorScheme = darkColorScheme(
    primary = PigeonGold,
    onPrimary = Color(0xFF1C1300),
    primaryContainer = PigeonGoldDark,
    onPrimaryContainer = PigeonGoldLight,
    secondary = PigeonEmerald,
    onSecondary = Color(0xFF002116),
    secondaryContainer = PigeonEmeraldMuted,
    onSecondaryContainer = Color(0xFF7BFFE0),
    tertiary = PigeonViolet,
    onTertiary = Color(0xFF20005E),
    background = PigeonObsidian,
    onBackground = PigeonTextPrimary,
    surface = PigeonSurfaceDark,
    onSurface = PigeonTextPrimary,
    surfaceVariant = PigeonSurfaceCard,
    onSurfaceVariant = PigeonTextSecondary,
    outline = PigeonBorder,
    outlineVariant = Color(0xFF1F2B3B),
    error = PigeonError,
    onError = Color.White
)

@Composable
fun PigeonverseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PigeonDarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias for template references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PigeonverseTheme(content = content)
}
