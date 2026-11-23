package com.beyondheavven.waveify.core.ui.oscilloscope

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.core.model.TrackSource
import com.beyondheavven.waveify.theme.WaveifyColors
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun WaveifyOscilloscope(
    source: TrackSource?,
    isPlaying: Boolean,
    realWaveData: ByteArray?,
    modifier: Modifier = Modifier,
    beamColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = WaveifyColors.current.waveGrid,
    backgroundColor: Color = WaveifyColors.current.waveBackground
) {
    val realLevel = remember(realWaveData) {
        if (realWaveData == null || realWaveData.isEmpty()) {
            0f
        } else {
            var sum = 0L
            for (byte in realWaveData) {
                val sample = (byte.toInt() and 0xFF) - 128
                sum += sample * sample
            }
            (sqrt(sum / realWaveData.size.toFloat()) / 64f).coerceIn(0f, 1f)
        }
    }
    val hasRealLevel = source == TrackSource.SOUNDCLOUD && realLevel > 0.01f
    val infiniteTransition = rememberInfiniteTransition(label = "oscilloscopePulse")
    val beatPulse = infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 500
                0.75f at 0
                1.35f at 45
                0.95f at 150
                0.75f at 500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "beatPulse"
    )
    val waveLevel = animateFloatAsState(
        targetValue = if (hasRealLevel && isPlaying) realLevel else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 260f
        ),
        label = "waveLevel"
    )
    val activeScale = animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "activeScale"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val cols = 8
            val rows = 4
            for (i in 1 until cols) {
                val x = width * (i.toFloat() / cols)
                drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
            }
            for (i in 1 until rows) {
                val y = height * (i.toFloat() / rows)
                drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
            }
            drawLine(gridColor.copy(alpha = 0.8f), Offset(0f, centerY), Offset(width, centerY), strokeWidth = 1.5f)
            val path = Path()
            val points = 72
            val step = width / points
            val pulse = if (hasRealLevel) {
                0.6f + waveLevel.value * 1.1f
            } else {
                0.75f + beatPulse.value * 0.45f
            }
            val amplitude = 13f * pulse * activeScale.value
            var x = 0f
            path.moveTo(0f, centerY)
            for (i in 0..points) {
                val normX = i.toFloat() / points
                val envelope = sin(normX * Math.PI.toFloat())
                val harmonic =
                    sin(normX * 12f * Math.PI.toFloat()) * 0.62f +
                        sin(normX * 26f * Math.PI.toFloat()) * 0.24f +
                        sin(normX * 5f * Math.PI.toFloat()) * 0.14f
                val y = (centerY + harmonic * envelope * amplitude).coerceIn(4f, height - 4f)
                path.lineTo(x, y)
                x += step
            }
            drawPath(
                path = path,
                color = beamColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}