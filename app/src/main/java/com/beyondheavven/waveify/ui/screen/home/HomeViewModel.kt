package com.beyondheavven.waveify.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,

    val isSpotifyConnected: Boolean = false,

    val isSoundCloudConnected: Boolean = false,

    val spotifyRecommendations: List<TrackUiModel> = emptyList(),

    val soundCloudTrending: List<TrackUiModel> = emptyList(),

    val error: String? = null
)


class HomeViewModel: ViewModel() {

    var uiState by mutableStateOf(value = HomeUiState())
        private set

    init {
        loadFeed()
    }

    fun loadFeed() {
        val hasSpotify = !SpotifyClient.accessToken.isNullOrEmpty()
        val hasSoundCloud = !SoundCloudClient.connectedUsername.isNullOrEmpty()

        uiState = uiState.copy(
            isSpotifyConnected = hasSpotify,
            isSoundCloudConnected = hasSoundCloud
        )

        if (!hasSpotify && !hasSoundCloud) {
            uiState = uiState.copy(isLoading = false)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val spotifyDeferred = if (hasSpotify) {
                    async { HomeRepository.getSpotifyRecommendations() }
                } else null

                val soundCloudDeferred = if (hasSoundCloud) {
                    async { HomeRepository.getSoundCloudRecommendations() }
                } else null

                val spotifyTracks = spotifyDeferred?.await() ?: emptyList()
                val soundCloudTracks = soundCloudDeferred?.await() ?: emptyList()

                uiState = HomeUiState(
                    isLoading = true,
                    spotifyRecommendations = spotifyTracks,
                    soundCloudTrending = soundCloudTracks
                )
            } catch (e: Exception) {
                uiState = HomeUiState(isLoading = false, error = e.localizedMessage)
            }
        }
    }


}