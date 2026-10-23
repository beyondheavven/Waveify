package com.beyondheavven.waveify.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyondheavven.waveify.player.waveify.WaveifyPlayer
import com.beyondheavven.waveify.ui.screen.home.card.ConnectServiceCard
import com.beyondheavven.waveify.ui.screen.home.corousel.HomeCarouselSection

@Composable
fun HomeScreen(
    padding: PaddingValues,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

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
            if (uiState.isSpotifyConnected) {
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
            } else {
                ConnectServiceCard(
                    serviceName = "Spotify",
                    serviceColor = Color(0xFF1DB954),
                    description = "Please enter in Spotify account.",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isSoundCloudConnected) {
                HomeCarouselSection(
                    title = "SoundCloud recommendations",
                    sourceName = "SoundCloud",
                    sourceColor = Color(0xFFFF5500),
                    tracks = uiState.soundCloudTrending,
                    onTrackClick = { selectedTrack ->
                        WaveifyPlayer.play(
                            track = selectedTrack,
                            playlist = uiState.soundCloudTrending,
                            context = context
                        )
                    }
                )
            } else {
                ConnectServiceCard(
                    serviceName = "SoundCloud",
                    serviceColor = Color(0xFFFF5500),
                    description = "Please enter SoundCloud account.",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}