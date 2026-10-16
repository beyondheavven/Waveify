package com.beyondheavven.waveify.ui.screen.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beyondheavven.waveify.data.model.TrackSource
import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.ui.screen.library.repository.LibraryRepository
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: LibraryRepository = LibraryRepository()
) : ViewModel() {

    var uiState by mutableStateOf<LibraryUiState>(LibraryUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf(LibraryFilter.ALL)
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    var paginationError by mutableStateOf<String?>(null)
        private set

    init {
        loadInitialTracks()
    }

    val filteredTracks: List<TrackUiModel>
        get() {
            val successState = uiState as? LibraryUiState.Success ?: return emptyList()

            val sourceFilter = when(selectedFilter){
                LibraryFilter.ALL -> successState.tracks
                LibraryFilter.SPOTIFY -> successState.tracks.filter { it.source == TrackSource.SPOTIFY }
                LibraryFilter.SOUNDCLOUD -> successState.tracks.filter { it.source == TrackSource.SOUNDCLOUD }
            }

            if (searchQuery.isBlank()){
                return sourceFilter
            }

            return sourceFilter.filter { track ->
                track.title.contains(searchQuery, ignoreCase = true) ||
                        track.artist.contains(searchQuery, ignoreCase = true)
            }
        }

    fun loadInitialTracks(force: Boolean = false){
        if (!repository.hasAnySource){
            uiState = LibraryUiState.NotAuthorized
            return
        }

        if (!force && uiState is LibraryUiState.Success){
            return
        }

        viewModelScope.launch {
            uiState = LibraryUiState.Loading
            repository.getInitialTracks()
                .onSuccess { tracks ->
                    uiState = LibraryUiState.Success(tracks)
                }
                .onFailure { error ->
                    uiState = LibraryUiState.Error(error.localizedMessage ?: "Failed to fetch library")
                }
        }
    }

    fun loadMoreTracks(){
        if (isLoadingMore || !repository.hasMoreTracks) return

        val currentSuccess = uiState as? LibraryUiState.Success ?: return

        viewModelScope.launch {
            isLoadingMore = true
            repository.loadMoreTracks()
                .onSuccess { newTracks ->
                    if (newTracks.isNotEmpty()){
                        uiState = LibraryUiState.Success(tracks = (currentSuccess.tracks + newTracks).distinctBy { it.id })
                    }
                }
                .onFailure { error ->
                    paginationError = error.localizedMessage ?: "Failed to load more tracks"
                }

            isLoadingMore = false
        }
    }

    fun onSearchQueryChange(newQuery: String){
        searchQuery = newQuery
    }

    fun onFilterSelected(filter: LibraryFilter){
        selectedFilter = filter
    }

    fun clearPaginationError(){
        paginationError = null
    }




}