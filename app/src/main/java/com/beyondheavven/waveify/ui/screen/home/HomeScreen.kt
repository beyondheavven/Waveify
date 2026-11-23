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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyondheavven.waveify.data.model.HomeItemUiModel
import com.beyondheavven.waveify.data.model.toTrackUiModel
import com.beyondheavven.waveify.player.waveify.WaveifyPlayer
import com.beyondheavven.waveify.ui.screen.home.card.ConnectServiceCard
import com.beyondheavven.waveify.ui.screen.home.card.FavouriteTracksCard
import com.beyondheavven.waveify.ui.screen.home.carousel.HomeCarouselSection
import com.beyondheavven.waveify.ui.theme.WaveifyColors

@Composable
fun HomeScreen(
    padding: PaddingValues,
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val palette = WaveifyColors.current

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    val play: (HomeItemUiModel, List<HomeItemUiModel>) -> Unit = { item, items ->
        WaveifyPlayer.play(
            item.toTrackUiModel(),
            items.map { it.toTrackUiModel() },
            context
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "waveify.",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Your music, one place.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.muted
        )

        Spacer(modifier = Modifier.height(28.dp))

        FavouriteTracksCard(
            isLoggedIn = uiState.isSpotifyConnected,
            onClick = onNavigateToLibrary,
            onPlayClick = onNavigateToLibrary
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            if (uiState.isSpotifyConnected) {
                HomeCarouselSection(
                    title = "On your repeat",
                    sourceName = "Spotify",
                    sourceColor = palette.spotify,
                    items = uiState.spotifyTopTracks,
                    onItemClick = { item -> play(item, uiState.spotifyTopTracks) }
                )
            } else {
                ConnectServiceCard(
                    serviceName = "Spotify",
                    serviceColor = palette.spotify,
                    description = "Connect to see your top tracks and playlists.",
                    onClick = onNavigateToProfile
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isSoundCloudConnected) {
                HomeCarouselSection(
                    title = "Trending",
                    sourceName = "SoundCloud",
                    sourceColor = palette.soundcloud,
                    items = uiState.soundCloudTrending,
                    onItemClick = { item -> play(item, uiState.soundCloudTrending) }
                )
            } else {
                ConnectServiceCard(
                    serviceName = "SoundCloud",
                    serviceColor = palette.soundcloud,
                    description = "Connect to explore trending tracks.",
                    onClick = onNavigateToProfile
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isSpotifyConnected && uiState.spotifyNewReleases.isNotEmpty()) {
                HomeCarouselSection(
                    title = "New releases",
                    sourceName = "Spotify",
                    sourceColor = palette.spotify,
                    items = uiState.spotifyNewReleases,
                    onItemClick = { item -> play(item, uiState.spotifyNewReleases) }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isSpotifyConnected && uiState.spotifyUserPlaylists.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Playlists & mixes",
                    sourceName = "Spotify",
                    sourceColor = palette.spotify,
                    items = uiState.spotifyUserPlaylists,
                    onItemClick = { item -> play(item, uiState.spotifyUserPlaylists) }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isSpotifyConnected && uiState.spotifyArtistsTracks.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Favourite artists",
                    sourceName = "Spotify",
                    sourceColor = palette.spotify,
                    items = uiState.spotifyArtistsTracks,
                    onItemClick = { item -> play(item, uiState.spotifyArtistsTracks) }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isSpotifyConnected && uiState.spotifyFeaturedPlaylists.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Featured playlists",
                    sourceName = "Spotify",
                    sourceColor = palette.spotify,
                    items = uiState.spotifyFeaturedPlaylists,
                    onItemClick = { item -> play(item, uiState.spotifyFeaturedPlaylists) }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isSoundCloudConnected && uiState.soundCloudHipHop.isNotEmpty()) {
                HomeCarouselSection(
                    title = "Hip-hop & rap",
                    sourceName = "SoundCloud",
                    sourceColor = palette.soundcloud,
                    items = uiState.soundCloudHipHop,
                    onItemClick = { item -> play(item, uiState.soundCloudHipHop) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
