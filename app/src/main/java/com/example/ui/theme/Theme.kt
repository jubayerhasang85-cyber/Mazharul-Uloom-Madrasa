package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MadrasaGreenPrimary,
    onPrimary = MadrasaTextWhite,
    primaryContainer = MadrasaGreenContainer,
    onPrimaryContainer = MadrasaGreenPrimary,
    secondary = MadrasaGreenSecondary,
    onSecondary = MadrasaTextWhite,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = MadrasaGold,
    onTertiary = MadrasaTextWhite,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF92400E),
    background = MadrasaBackground,
    onBackground = MadrasaTextDark,
    surface = MadrasaSurface,
    onSurface = MadrasaTextDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = MadrasaTextMuted,
    outline = MadrasaCardBorder,
    error = MadrasaError,
    onError = MadrasaTextWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = MadrasaGreenLight,
    onPrimary = MadrasaGreenDarkContainer,
    primaryContainer = MadrasaGreenPrimary,
    onPrimaryContainer = MadrasaTextWhite,
    secondary = MadrasaGreenLight,
    onSecondary = Color(0xFF022C22),
    tertiary = MadrasaGoldLight,
    onTertiary = Color(0xFF451A03),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

@Composable
fun MazharulUloomTheme(
    darkTheme: Boolean = false, // Keep default crisp green-and-white theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
