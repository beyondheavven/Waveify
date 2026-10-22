package com.beyondheavven.waveify.ui.screen.home.corousel

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.ui.screen.home.card.HomeTrackCard
import com.beyondheavven.waveify.ui.theme.TextPrimary
import org.w3c.dom.Text

@Composable
fun HomeCarouselSection(
    title: String,
    sourceName: String,
    sourceColor: Color,
    tracks: List<TrackUiModel>,
    onTrackClick: (track: TrackUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tracks.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(sourceColor, CircleShape)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(sourceColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = sourceColor
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(tracks, key = { it.id }) {track ->
                HomeTrackCard(
                    track = track,
                    onClick = { onTrackClick(track)}
                )
            }
        }
    }
}