package com.beyondheavven.waveify.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.beyondheavven.waveify.data.model.HomeItemUiModel
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,

    val isSpotifyConnected: Boolean = false,

    val isSoundCloudConnected: Boolean = false,

    val spotifyNewReleases: List<HomeItemUiModel> = emptyList(),

    val spotifyFeaturedPlaylists: List<HomeItemUiModel> = emptyList(),

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
    }


}