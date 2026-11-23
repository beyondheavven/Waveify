package com.beyondheavven.waveify.ui.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.runtime.CompositionLocalProvider

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

object AppearanceSettings {

    private const val PREFS_NAME = "waveify_appearance_prefs"
    private const val KEY_THEME_MODE = "theme_mode"

    private var prefs: SharedPreferences? = null
    private val _themeMode = mutableStateOf(ThemeMode.LIGHT)

    val themeMode: ThemeMode get() = _themeMode.value

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs?.edit()?.putString(KEY_THEME_MODE, mode.name)?.apply()
    }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs?.getString(KEY_THEME_MODE, ThemeMode.LIGHT.name)
        _themeMode.value = runCatching {
            ThemeMode.valueOf(saved ?: ThemeMode.LIGHT.name)
        }.getOrDefault(ThemeMode.LIGHT)
    }
}

@Composable
fun WaveifyTheme(
    themeMode: ThemeMode = AppearanceSettings.themeMode,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkWaveifyColorScheme else LightWaveifyColorScheme
    val palette = if (darkTheme) DarkWaveifyPalette else LightWaveifyPalette

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalWaveifyPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
