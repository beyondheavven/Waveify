package com.beyondheavven.waveify.ui.cover

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf

object AsciiSettings {

    private const val PREFS_NAME = "waveify_ascii_prefs"

    private const val KEY_IS_ENABLED = "is_ascii_enabled"

    private const val KEY_COLOR_MODE = "ascii_color_mode"

    private var prefs: SharedPreferences? = null

    private val _isEnabled = mutableStateOf(true)

    private val _currentMode = mutableStateOf(AsciiColorMode.COLORED)

    var isEnabled: Boolean
        get() = _isEnabled.value
        set(value) {
            _isEnabled.value = value
            prefs?.edit()?.putBoolean(KEY_IS_ENABLED, value)?.apply()
        }

    var currentMode: AsciiColorMode
        get() = _currentMode.value
        set(value) {
            _currentMode.value = value
            prefs?.edit()?.putString(KEY_COLOR_MODE, value.name)?.apply()
        }

    fun init(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sp

        _isEnabled.value = sp.getBoolean(KEY_IS_ENABLED, true)

        val savedMode = sp.getString(KEY_COLOR_MODE, AsciiColorMode.COLORED.name)
        _currentMode.value = runCatching {
            AsciiColorMode.valueOf(savedMode ?: AsciiColorMode.COLORED.name)
        }.getOrDefault(AsciiColorMode.COLORED)
    }
}