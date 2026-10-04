package com.beyondheavven.waveify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView

private val WaveifyColorScheme = darkColorScheme(
    primary = WaveifyRed,
    onPrimary = TextPrimary,
    primaryContainer = WaveifyRedDark,
    onPrimaryContainer = TextPrimary,
    secondary = WaveifyRedLight,
    onSecondary = TextPrimary,
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
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
        }
    }

    MaterialTheme(
        colorScheme = WaveifyColorScheme,
        typography = Typography,
        content = content
    )
}