package com.beyondheavven.waveify.ui.screen.library

import com.beyondheavven.waveify.data.model.TrackUiModel

sealed interface LibraryUiState {
    object NotAuthorized: LibraryUiState
    object Loading: LibraryUiState
    data class Error(val message: String) : LibraryUiState
    data class Success(val tracks: List<TrackUiModel>) : LibraryUiState
}
