package com.beyondheavven.waveify.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val WaveifyRed = Color(0xFFE50914)
val WaveifyRedDark = Color(0xFFB1060E)
val WaveifyRedLight = Color(0xFFFF5963)
val DarkBackground = Color(0xFF0B0B0D)

private val LightText = Color(0xFF111114)
private val LightTextSecondary = Color(0xFF5E5E67)
private val LightTextMuted = Color(0xFF96969F)
private val LightOutline = Color(0xFFE6E6EA)
private val LightSurfaceVariant = Color(0xFFF5F5F7)

private val DarkText = Color(0xFFF7F7F8)
private val DarkTextSecondary = Color(0xFFB4B4BC)
private val DarkTextMuted = Color(0xFF80808A)
private val DarkOutline = Color(0xFF2C2C31)
private val DarkSurfaceVariant = Color(0xFF1C1C20)

val LightWaveifyColorScheme = lightColorScheme(
    primary = WaveifyRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE7E8),
    onPrimaryContainer = Color(0xFF950810),
    secondary = LightTextSecondary,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightText,
    background = Color.White,
    onBackground = LightText,
    surface = Color.White,
    onSurface = LightText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFD2D2D8),
    outlineVariant = LightOutline,
    error = Color(0xFFC5221E),
    onError = Color.White,
    errorContainer = Color(0xFFFFE7E8),
    onErrorContainer = Color(0xFF950810),
    scrim = Color(0xFF000000)
)

val DarkWaveifyColorScheme = darkColorScheme(
    primary = WaveifyRedLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5F0B11),
    onPrimaryContainer = Color(0xFFFFDADD),
    secondary = DarkTextSecondary,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkText,
    background = DarkBackground,
    onBackground = DarkText,
    surface = Color(0xFF141417),
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF3A3A41),
    outlineVariant = DarkOutline,
    error = Color(0xFFFF8A80),
    onError = Color(0xFF3B0000),
    errorContainer = Color(0xFF5F0B11),
    onErrorContainer = Color(0xFFFFDADD),
    scrim = Color(0xFF000000)
)

data class WaveifyPalette(
    val isDark: Boolean,
    val success: Color,
    val successContainer: Color,
    val errorContainer: Color,
    val infoContainer: Color,
    val elevatedSurface: Color,
    val subtleBorder: Color,
    val muted: Color,
    val waveBackground: Color,
    val waveGrid: Color,
    val coverPlaceholder: Color,
    val spotify: Color,
    val soundcloud: Color
)

val LightWaveifyPalette = WaveifyPalette(
    isDark = false,
    success = Color(0xFF16803C),
    successContainer = Color(0xFFEAF7EE),
    errorContainer = Color(0xFFFFF0F0),
    infoContainer = Color(0xFFFFF4F4),
    elevatedSurface = Color(0xFFFFFFFF),
    subtleBorder = LightOutline,
    muted = LightTextMuted,
    waveBackground = Color(0xFFF7F7F9),
    waveGrid = Color(0xFFE9E9EE),
    coverPlaceholder = Color(0xFFEDEDF0),
    spotify = Color(0xFF16803C),
    soundcloud = Color(0xFFE26200)
)

val DarkWaveifyPalette = WaveifyPalette(
    isDark = true,
    success = Color(0xFF5DD18A),
    successContainer = Color(0xFF14291C),
    errorContainer = Color(0xFF2A1416),
    infoContainer = Color(0xFF251518),
    elevatedSurface = Color(0xFF1A1A1E),
    subtleBorder = DarkOutline,
    muted = DarkTextMuted,
    waveBackground = Color(0xFF111114),
    waveGrid = Color(0xFF24242A),
    coverPlaceholder = Color(0xFF24242A),
    spotify = Color(0xFF5DD18A),
    soundcloud = Color(0xFFFF8A4C)
)

val LocalWaveifyPalette = staticCompositionLocalOf { LightWaveifyPalette }

object WaveifyColors {
    val current: WaveifyPalette
        @Composable get() = LocalWaveifyPalette.current
}
