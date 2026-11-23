package com.beyondheavven.waveify.feature.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.feature.player.waveify.WaveifyPlayer
import com.beyondheavven.waveify.core.ui.cover.CoverSize
import com.beyondheavven.waveify.core.ui.cover.WaveifyCover
import com.beyondheavven.waveify.core.ui.oscilloscope.OscilloscopeWave
import com.beyondheavven.waveify.theme.WaveifyColors

@Composable
fun MiniPlayer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = WaveifyPlayer.currentTrack
    val palette = WaveifyColors.current

    AnimatedVisibility(
        visible = track != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        if (track != null) {
            val progress = if (WaveifyPlayer.durationMs > 0) {
                (WaveifyPlayer.currentPositionMs.toFloat() / WaveifyPlayer.durationMs).coerceIn(0f, 1f)
            } else {
                0f
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick() },
                shape = RoundedCornerShape(16.dp),
                color = palette.elevatedSurface,
                border = BorderStroke(1.dp, palette.subtleBorder),
                shadowElevation = if (palette.isDark) 0.dp else 4.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 2.dp)
                    ) {
                        WaveifyCover(
                            coverUrl = track.coverUrl,
                            size = CoverSize.SMALL,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(40.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track.artist,
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.muted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OscilloscopeWave(
                            isPlaying = WaveifyPlayer.isPlaying,
                            waveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                            strokeWidth = 1.2.dp,
                            maxAmplitude = 4f,
                            modifier = Modifier.width(28.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { WaveifyPlayer.togglePlayPause() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (WaveifyPlayer.isPlaying) {
                                    Icons.Default.Pause
                                } else {
                                    Icons.Default.PlayArrow
                                },
                                contentDescription = "Play/Pause",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = palette.waveGrid
                    )
                }
            }
        }
    }
}
