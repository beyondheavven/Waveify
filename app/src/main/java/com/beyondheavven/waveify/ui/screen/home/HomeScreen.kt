package com.beyondheavven.waveify.ui.screen.home

import android.widget.Space
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import com.beyondheavven.waveify.player.waveify.WaveifyPlayer
import com.beyondheavven.waveify.ui.cover.WaveifyCover
import com.beyondheavven.waveify.ui.screen.home.card.FavouriteTracksCard
import com.beyondheavven.waveify.ui.screen.home.corousel.HomeCarouselSection

@Composable
fun HomeScreen(
    padding: PaddingValues,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Waveify",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {

            }
        } else {
            HomeCarouselSection(
                title = "Spotify Recommendations",
                sourceName = "Spotify",
                sourceColor = Color(0xFF1DB954),
                tracks = uiState.spotifyRecommendations,
                onTrackClick = { selectedTracks ->
                    WaveifyPlayer.play(
                        track = selectedTracks,
                        playlist = uiState.spotifyRecommendations,
                        context = context
                    )
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            HomeCarouselSection(
                title = "SoundCloud Recommendations",
                sourceName = "SoundCloud",
                sourceColor = Color(0xFFFF5500),
                tracks = uiState.soundCloudTrending,
                onTrackClick = { selectedTracks ->
                    WaveifyPlayer.play(
                        track = selectedTracks,
                        playlist = uiState.soundCloudTrending,
                        context = context
                    )
                }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}