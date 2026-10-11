package com.beyondheavven.waveify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.remote.client.SpotifyClient
import com.beyondheavven.waveify.ui.theme.DarkBackground
import com.beyondheavven.waveify.ui.theme.TrackSource
import com.beyondheavven.waveify.ui.theme.TrackUiModel
import kotlinx.coroutines.launch


enum class LibraryFilter {
    ALL,
    SPOTIFY,
    SOUNDCLOUD
}

sealed interface LibraryUiState {
    object NotAuthorized: LibraryUiState
    object Loading: LibraryUiState
    data class Error(val message: String) : LibraryUiState
    data class Success(val tracks: List<TrackUiModel>) : LibraryUiState
}


@Composable
fun LibraryScreen(
    padding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val isSpotifyLoggedIn = !SpotifyClient.accessToken.isNullOrEmpty()

    var selectedFilter by remember { mutableStateOf(LibraryFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var uiState by remember {
        mutableStateOf<LibraryUiState>(
            if (isSpotifyLoggedIn) LibraryUiState.Loading else LibraryUiState.NotAuthorized
        )
    }

    fun loadingLibraryTracks() {
        if (!isSpotifyLoggedIn) {
            uiState = LibraryUiState.NotAuthorized
            return
        }

        coroutineScope.launch {
            uiState = LibraryUiState.Loading
            try {
                val response = SpotifyClient.api.getSavedTracks(limit = 50)

                val spotifyTracks = response.items.map { item ->
                    val track = item.track
                    val totalSec = track.durationMs / 1000
                    TrackUiModel(
                        id = track.id,
                        title = track.name,
                        artist = track.artists.joinToString(", ") { it.name },
                        coverUrl = track.album?.images?.firstOrNull()?.url,
                        durationFormatted = String.format("%d:%02d", totalSec / 60, totalSec % 60),
                        source = TrackSource.SPOTIFY
                    )
                }
                uiState = LibraryUiState.Success(spotifyTracks)
            } catch (e: Exception) {
                uiState = LibraryUiState.Error(e.localizedMessage ?: "Failed to fetch library")
            }
        }
    }

    LaunchedEffect(
        isSpotifyLoggedIn
    ) {
        if (isSpotifyLoggedIn){
            loadingLibraryTracks()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(paddingValues = padding)
    ) {
    }
}