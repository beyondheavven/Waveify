package com.beyondheavven.waveify.ui.screen.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private var spotifyTracks by mutableStateOf<List<TrackUiModel>>(emptyList())

    private var soundCloudTracks by mutableStateOf<List<TrackUiModel>>(emptyList())

    val filteredTracks: List<TrackUiModel>
        get() {
            val sourceFilter = when(selectedFilter){
                LibraryFilter.ALL -> interleaveTracks(spotifyTracks, soundCloudTracks)
                LibraryFilter.SPOTIFY -> spotifyTracks
                LibraryFilter.SOUNDCLOUD -> soundCloudTracks
            }

            if (searchQuery.isBlank()){
                return sourceFilter
            }

            return sourceFilter.filter { track ->
                track.title.contains(searchQuery, ignoreCase = true) ||
                        track.artist.contains(searchQuery, ignoreCase = true)
            }
        }

    fun syncLibrary() {
        if (!repository.hasAnySource) {
            uiState = LibraryUiState.NotAuthorized
            spotifyTracks = emptyList()
            soundCloudTracks = emptyList()
            return
        }

        if (uiState is LibraryUiState.NotAuthorized) {
            uiState = LibraryUiState.Loading
        }

        viewModelScope.launch {
            if (repository.isSpotifyLoggedIn && spotifyTracks.isEmpty()){
                repository.getInitialSpotifyTracks().onSuccess {
                    spotifyTracks = it
                }
            } else if (!repository.isSpotifyLoggedIn) {
                spotifyTracks = emptyList()
            }

            if (repository.isSoundCloudConnected && soundCloudTracks.isEmpty()) {
                repository.getInitialSpotifyTracks()
            }

            if (repository.isSoundCloudConnected && soundCloudTracks.isEmpty()) {
                repository.getInitialSoundCloudTracks().onSuccess {
                    soundCloudTracks = it
                }
            } else if (!repository.isSoundCloudConnected) {
                soundCloudTracks = emptyList()
            }

            uiState = LibraryUiState.Success(filteredTracks)
        }
    }

    fun loadMoreTracks(){
        if (isLoadingMore) return

        val canLoadMore = when(selectedFilter) {
            LibraryFilter.ALL -> repository.hasMoreSpotifyTracks || repository.hasMoreSoundCloudTracks
            LibraryFilter.SOUNDCLOUD -> repository.hasMoreSoundCloudTracks
            LibraryFilter.SPOTIFY -> repository.hasMoreSpotifyTracks
        }

        if (!canLoadMore) return

        viewModelScope.launch {
            isLoadingMore = true
            if ((selectedFilter == LibraryFilter.ALL || selectedFilter == LibraryFilter.SPOTIFY) && repository.hasMoreSpotifyTracks) {
                repository.loadMoreSpotifyTracks().onSuccess { newTracks ->
                    spotifyTracks = (spotifyTracks + newTracks).distinctBy { it.id }
                }.onFailure {
                    paginationError = it.localizedMessage
                }
            }

            if ((selectedFilter == LibraryFilter.ALL || selectedFilter == LibraryFilter.SOUNDCLOUD) && repository.hasMoreSoundCloudTracks) {
                repository.loadMoreSoundCloudTracks().onSuccess { newTracks ->
                    soundCloudTracks = (soundCloudTracks + newTracks).distinctBy { it.id }
                }.onFailure {
                    paginationError = it.localizedMessage
                }
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

    private fun interleaveTracks(listA: List<TrackUiModel>, listB: List<TrackUiModel>): List<TrackUiModel> {
        val result = mutableListOf<TrackUiModel>()
        val maxLen = maxOf(listA.size, listB.size)
        for (i in 0 until maxLen) {
            if (i < listA.size) result.add(listA[i])
            if (i < listB.size) result.add(listB[i
            ])
        }
        return result
    }
}