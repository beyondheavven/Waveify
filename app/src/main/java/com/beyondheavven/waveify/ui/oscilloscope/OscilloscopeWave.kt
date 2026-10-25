package com.beyondheavven.waveify.ui.oscilloscope

import android.icu.number.IntegerWidth
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.ui.theme.WaveifyRed
import kotlin.math.sin

@Composable
fun OscilloscopeWave(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    waveColor: Color = WaveifyRed,
    strokeWidth: Dp = 1.5.dp,
    maxAmplitude: Float = 6f
) {

    val infiniteTransition = rememberInfiniteTransition(label = "waveTransition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val animatedAmplitude by animateFloatAsState(
        targetValue = if (isPlaying) maxAmplitude else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "amplitude"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(18.dp)
    ) {
        val width = size.width
        val centerY = size.height / 2f
        val path = Path()

        val step = 4f
        var x = 0f
        path.moveTo(0f, centerY)

        while (x <= width) {
            val normalizedX = x / width
            val sine1 = sin(normalizedX * 14f * Math.PI.toFloat() + phase)
            val sine2 = sin(normalizedX * 28f * Math.PI.toFloat() + phase * 2) * 0.35f

            val y = centerY + (sine1 + sine2) * animatedAmplitude
            path.lineTo(x, y)
            x +=step
        }

        drawPath(
            path = path,
            color = waveColor,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }


}