package com.beyondheavven.waveify.ui.oscilloscope

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.model.TrackSource
import com.beyondheavven.waveify.ui.theme.WaveifyRed
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WaveifyOscilloscope(
    source: TrackSource?,
    isPlaying: Boolean,
    realWaveData: ByteArray?,
    modifier: Modifier = Modifier,
    beamColor: Color = WaveifyRed,
    gridColor: Color = Color(0xFF16222F),
    backgroundColor: Color = Color(0xFF0A0F14)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "synthTransition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val beatKick by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 480
                1f at 0
                2.4f at 80
                1.3f at 180
                1f at 480
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "beatKick"
    )
    val activeScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
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
            val isSoundCloud = source == TrackSource.SOUNDCLOUD && realWaveData != null && realWaveData.isNotEmpty()
            if (isSoundCloud) {
                val sliceWidth = width / realWaveData!!.size.toFloat()
                var x = 0f
                val gain = 1.8f * activeScale
                for (i in realWaveData.indices) {
                    val rawSample = (realWaveData[i].toInt() and 0xFF) - 128
                    val sample = rawSample * gain
                    val y = (centerY + sample).coerceIn(4f, height - 4f)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    x += sliceWidth
                }
            } else {
                val points = 80
                val step = width / points
                var x = 0f
                path.moveTo(0f, centerY)
                for (i in 0..points) {
                    val normX = i.toFloat() / points
                    val noise = if (isPlaying) (Random.nextFloat() - 0.5f) * 4f else 0f
                    val wave = sin(normX * 16f * Math.PI.toFloat() + phase) * 12f * beatKick * activeScale
                    val y = (centerY + wave + noise).coerceIn(4f, height - 4f)
                    path.lineTo(x, y)
                    x += step
                }
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