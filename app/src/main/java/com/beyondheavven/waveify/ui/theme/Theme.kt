package com.beyondheavven.waveify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView

private val WaveifyColorScheme = darkColorScheme(
    primary = WaveifyRed,
    onPrimary = Color.White,
    primaryContainer = WaveifyRedDark,
    onPrimaryContainer = Color.White,

    secondary = TextSecondary,
    onSecondary = DarkBackground,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = DividerColor,
)

@Composable
fun WaveifyTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = WaveifyColorScheme,
        typography = Typography,
        content = content
    )
}