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
import com.beyondheavven.waveify.data.model.toTrackUiModel
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

        Spacer(modifier = Modifier.height(24.dp))

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
                    title = "On Your Repeat",
                    sourceName = "Spotify",
                    sourceColor = StatusSuccess,
                    items = uiState.spotifyTopTracks,
                    onItemClick = { item ->
                        val track = item.toTrackUiModel()
                        val playlist = uiState.spotifyTopTracks.map { it.toTrackUiModel() }
                        WaveifyPlayer.play(track, playlist, context)
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

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isSoundCloudConnected) {
                HomeCarouselSection(
                    title = "Trending",
                    sourceName = "SoundCloud",
                    sourceColor = Color(0xFFFF5500),
                    items = uiState.soundCloudTrending,
                    onItemClick = { item ->
                        val track = item.toTrackUiModel()
                        val playlist = uiState.soundCloudTrending.map { it.toTrackUiModel() }
                        WaveifyPlayer.play(track, playlist, context)
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

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isSpotifyConnected && uiState.spotifyNewReleases.isNotEmpty()) {
                HomeCarouselSection(
                    title = "New Releases",
                    sourceName = "Spotify",
                    sourceColor = StatusSuccess,
                    items = uiState.spotifyNewReleases,
                    onItemClick = { item ->
                        val track = item.toTrackUiModel()
                        val playlist = uiState.soundCloudHipHop.map { it.toTrackUiModel() }
                        WaveifyPlayer.play(track, playlist, context)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isSpotifyConnected && uiState.spotifyUserPlaylists.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Playlists & Mixes",
                    sourceName = "Spotify",
                    sourceColor = StatusSuccess,
                    items = uiState.spotifyUserPlaylists,
                    onItemClick = { item ->
                        val track = item.toTrackUiModel()
                        val playlist = uiState.spotifyUserPlaylists.map { it.toTrackUiModel() }
                        WaveifyPlayer.play(track, playlist, context)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isSpotifyConnected && uiState.spotifyArtistsTracks.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Favourite Artists",
                    sourceName = "Spotify",
                    sourceColor = StatusSuccess,
                    items = uiState.spotifyArtistsTracks,
                    onItemClick = { item ->
                        val track = item.toTrackUiModel()
                        val playlist = uiState.spotifyArtistsTracks.map { it.toTrackUiModel() }
                        WaveifyPlayer.play(track, playlist, context)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isSpotifyConnected && uiState.spotifyFeaturedPlaylists.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Featured Playlists",
                    sourceName = "Spotify",
                    sourceColor = StatusSuccess,
                    items = uiState.spotifyFeaturedPlaylists,
                    onItemClick = { item ->
                        val track = item.toTrackUiModel()
                        val playlist = uiState.spotifyFeaturedPlaylists.map { it.toTrackUiModel() }
                        WaveifyPlayer.play(track, playlist, context)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isSoundCloudConnected && uiState.soundCloudHipHop.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Hip-hop & Rap",
                    sourceName = "SoundCloud",
                    sourceColor = Color(0xFFFF5500),
                    items = uiState.soundCloudHipHop,
                    onItemClick = { item ->
                        val track = item.toTrackUiModel()
                        val playlist = uiState.soundCloudHipHop.map { it.toTrackUiModel() }
                        WaveifyPlayer.play(track, playlist, context)
                    }
                )
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}