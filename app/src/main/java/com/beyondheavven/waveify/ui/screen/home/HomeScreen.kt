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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.beyondheavven.waveify.player.soundcloud.SoundCloudPlayerManager
import com.beyondheavven.waveify.player.spotify.SpotifyPlayerManager
import com.beyondheavven.waveify.player.waveify.WaveifyPlayer
import com.beyondheavven.waveify.ui.screen.home.card.ConnectServiceCard
import com.beyondheavven.waveify.ui.screen.home.card.FavouriteTracksCard
import com.beyondheavven.waveify.ui.screen.home.carousel.HomeCarouselSection
import com.beyondheavven.waveify.ui.theme.StatusSuccess
import com.beyondheavven.waveify.ui.theme.WaveifyRed

@Composable
fun HomeScreen(
    padding: PaddingValues,
    onNavigateToLibrary: () -> Unit = {},
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
            .verticalScroll(scrollState)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Waveify",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FavouriteTracksCard(
            isLoggedIn = uiState.isSpotifyConnected,
            onClick = onNavigateToLibrary,
            onPlayClick = onNavigateToLibrary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = WaveifyRed)
            }
        } else {
            if (uiState.isSpotifyConnected) {
                HomeCarouselSection(
                    title = "Spotify Recommendations",
                    sourceName = "Spotify",
                    sourceColor = StatusSuccess,
                    items = uiState.spotifyNewReleases,
                    onItemClick = { item ->
                        SpotifyPlayerManager.play(item.spotifyUri ?: item.id, context)
                    }
                )
            } else {
                ConnectServiceCard(
                    serviceName = "Spotify",
                    serviceColor = StatusSuccess,
                    description = "Connect Spotify to discover new releases and playlists.",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isSoundCloudConnected) {
                HomeCarouselSection(
                    title = "SoundCloud trends now",
                    sourceName = "SoundCloud",
                    sourceColor = Color(0xFFFF5500),
                    items = uiState.soundCloudTrending,
                    onItemClick = { item ->
                        SpotifyPlayerManager.play(item.id)
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

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isSpotifyConnected && uiState.spotifyFeaturedPlaylists.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Featured Playlists",
                    sourceName = "Spotify",
                    sourceColor = StatusSuccess,
                    items = uiState.spotifyFeaturedPlaylists,
                    onItemClick = { item ->
                        SpotifyPlayerManager.play(item.spotifyUri ?: item.id, context)
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                if (uiState.isSoundCloudConnected && uiState.soundCloudHipHop.isNotEmpty()) {
                    HomeCarouselSection(
                        title = "Hip-hop & Rap",
                        sourceName = "SoundCloud",
                        sourceColor = Color(0xFFFF5500),
                        items = uiState.soundCloudHipHop,
                        onItemClick = {
                        }
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}