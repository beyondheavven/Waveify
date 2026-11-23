package com.beyondheavven.waveify.feature.library

import com.beyondheavven.waveify.core.model.TrackUiModel

sealed interface LibraryUiState {
    object NotAuthorized: LibraryUiState
    object Loading: LibraryUiState
    data class Error(val message: String) : LibraryUiState
    data class Success(val tracks: List<TrackUiModel>) : LibraryUiState
}
