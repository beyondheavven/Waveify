package com.beyondheavven.waveify.ui.cover

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.beyondheavven.waveify.ui.theme.StatusSuccess
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.WaveifyRed

enum class AsciiColorMode(
    val title: String,
    val indicatorColor: Color? = null,
    val indicatorBrush: Brush? = null
) {
    COLORED(
        title = "Color",
        indicatorBrush = Brush.sweepGradient(
            listOf(
                Color(0xFFFF3366),
                Color(0xFFFF9900),
                Color(0xFF22C55E),
                Color(0xFF00B4D8),
                Color(0xFFFF3366)
            )
        )
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