package com.beyondheavven.waveify.ui.cover

import androidx.compose.ui.graphics.Color
import com.beyondheavven.waveify.ui.theme.StatusSuccess
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.WaveifyRed
import com.beyondheavven.waveify.ui.theme.WaveifyRedDark

enum class AsciiColorMode(
    val title: String,
    val indicatorColor: Color
) {
    COLORED(
        title = "Color",
        indicatorColor = WaveifyRedDark
    ),
    WAVEIFY_RED(
        title = "Red",
        indicatorColor = WaveifyRed
    ),
    GREEN_MATRIX(
        title = "Matrix",
        indicatorColor = StatusSuccess
    ),
    MONOCHROME(
        title = "Mono",
        indicatorColor = TextPrimary
    )
}