package com.beyondheavven.waveify.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyondheavven.waveify.data.model.HomeItemUiModel
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.spotify.auth.SpotifyAuthManager
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,

    val isSpotifyConnected: Boolean = false,

    val isSoundCloudConnected: Boolean = false,

    val spotifyNewReleases: List<HomeItemUiModel> = emptyList(),

    val spotifyFeaturedPlaylists: List<HomeItemUiModel> = emptyList(),

    val soundCloudTrending: List<HomeItemUiModel> = emptyList(),

    val soundCloudHipHop: List<HomeItemUiModel> = emptyList(),

    val error: String? = null
)


class HomeViewModel: ViewModel() {

    var uiState by mutableStateOf(value = HomeUiState())
        private set

    init {
        loadFeed()

        viewModelScope.launch {
            SpotifyAuthManager.authEvents.collect { event ->
                if (event is SpotifyAuthManager.AuthEvent.Success) {
                    loadFeed()
                }
            }
        }
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
                val newReleasesDef = if (hasSpotify) async {
                    HomeRepository.getSpotifyNewRelease()
                } else {
                    null
                }

                val featuredPlaylistsDef = if (hasSpotify) async {
                    HomeRepository.getSpotifyFeaturedPlaylists()
                } else {
                    null
                }

                val scTrendingDef = if (hasSoundCloud) async {
                    HomeRepository.getSoundCloudTrending()
                } else {
                    null
                }

                val scHipHopDef = if (hasSoundCloud) async {
                    HomeRepository.getSoundCloudGenreChart()
                } else {
                    null
                }

                val newReleases = newReleasesDef?.await() ?: emptyList()
                val featuredPlaylists = featuredPlaylistsDef?.await() ?: emptyList()
                val scTrending = scTrendingDef?.await() ?: emptyList()
                val scHipHop = scHipHopDef?.await() ?: emptyList()

                uiState = uiState.copy(
                    isLoading = false,
                    isSpotifyConnected = hasSpotify,
                    isSoundCloudConnected = hasSoundCloud,
                    spotifyNewReleases = newReleases,
                    spotifyFeaturedPlaylists = featuredPlaylists,
                    soundCloudTrending = scTrending,
                    soundCloudHipHop = scHipHop
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
            }
        }
    }


}