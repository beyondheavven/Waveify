package com.beyondheavven.waveify.data.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.ui.cover.CoverSize
import com.beyondheavven.waveify.ui.cover.WaveifyCover
import com.beyondheavven.waveify.ui.theme.WaveifyColors

@Composable
fun TrackItem(
    track: TrackUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = WaveifyColors.current
    val sourceColor = if (track.source == TrackSource.SPOTIFY) palette.spotify else palette.soundcloud
    val sourceName = if (track.source == TrackSource.SPOTIFY) "Spotify" else "SoundCloud"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WaveifyCover(
            coverUrl = track.coverUrl,
            size = CoverSize.MEDIUM,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    color = sourceColor
                )
                Text(
                    text = " · ${track.artist}",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = track.durationFormatted,
            style = MaterialTheme.typography.bodySmall,
            color = palette.muted
        )
    }
}
