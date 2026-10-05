package com.beyondheavven.waveify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.ui.theme.TrackSource
import com.beyondheavven.waveify.ui.theme.TrackUiModel

@Composable
fun HomeScreen(padding: PaddingValues) {
    val dummyTracks = remember {
        listOf(
            TrackUiModel(
                id = "1",
                title = "Starboy",
                artist = "The Weeknd, Daft Punk",
                coverUrl = "https://i.scdn.co/image/ab67616d0000b2734718e2b124f79258be7bc452",
                durationFormatted = "3:50",
                source = TrackSource.SPOTIFY
            ),
            TrackUiModel(
                id = "2",
                title = "Midnight City",
                artist = "M83",
                coverUrl = "https://i.scdn.co/image/ab67616d0000b2734f40d6e27b8ed3e9ef2b8df1",
                durationFormatted = "4:03",
                source = TrackSource.SOUNDCLOUD
            ),
            TrackUiModel(
                id = "3",
                title = "Numb",
                artist = "Linkin Park",
                coverUrl = "https://i.scdn.co/image/ab67616d0000b273b5bf0b3e6c0c28373b9e4ec3",
                durationFormatted = "3:07",
                source = TrackSource.SPOTIFY
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(bottom = 16.dp)
    ){
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Waveify",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text("Spotify") },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF1DB954), CircleShape)
                            )
                        }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("SoundCloud") },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFFF5500), CircleShape)
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Твои треки",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(dummyTracks, key = { it.id }) { track ->
            TrackItem(
                track = track,
                onClick = {
                    println("Выбран трек: ${track.title}")
                }
            )
        }
    }
}