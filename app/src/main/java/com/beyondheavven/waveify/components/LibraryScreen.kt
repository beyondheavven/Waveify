package com.beyondheavven.waveify.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.beyondheavven.waveify.data.remote.client.SpotifyClient
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
    onNavigateToProfile: () -> Unit,
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

    fun loadingLibraryTracks(){
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
                        artist = track.artists.joinToString(", ") { it.name},
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



}