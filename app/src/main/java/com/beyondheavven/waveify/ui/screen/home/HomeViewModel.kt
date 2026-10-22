package com.beyondheavven.waveify.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyondheavven.waveify.data.model.TrackUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,

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
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val spotifyDeferred = async { HomeRepository.getSpotifyRecommendations() }
                val soundCloudDeferred = async { HomeRepository.getSoundCloudRecommendations() }

                val spotifyTracks = spotifyDeferred.await()
                val soundCloudTracks = soundCloudDeferred.await()

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