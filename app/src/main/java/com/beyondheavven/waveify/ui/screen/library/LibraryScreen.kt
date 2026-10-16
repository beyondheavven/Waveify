package com.beyondheavven.waveify.ui.screen.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import com.beyondheavven.waveify.data.model.TrackSource
import com.beyondheavven.waveify.ui.theme.DarkBackground
import com.beyondheavven.waveify.ui.theme.TextMuted
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.TextSecondary
import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.ui.component.NotificationBanner
import com.beyondheavven.waveify.ui.component.NotificationState
import com.beyondheavven.waveify.ui.component.TrackItem
import com.beyondheavven.waveify.ui.component.rememberNotificationController
import com.beyondheavven.waveify.ui.screen.library.components.LibraryFilterChip
import com.beyondheavven.waveify.ui.screen.library.components.LibrarySearchBar
import com.beyondheavven.waveify.ui.theme.WaveifyRed
import kotlinx.coroutines.launch


@Composable
fun LibraryScreen(
    padding: PaddingValues
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var isLoadingMore by remember { mutableStateOf(false) }
    val notificationController = rememberNotificationController()

    var selectedFilter by remember { mutableStateOf(LibraryFilter.ALL) }

    var searchQuery by remember { mutableStateOf("") }


    fun loadingLibraryTracks() {
        if (!hasAnySource) {
            uiState = LibraryUiState.NotAuthorized
            return
        }

        coroutineScope.launch {
            uiState = LibraryUiState.Loading
            try {
                val allTracks = mutableListOf<TrackUiModel>()
                if (isSpotifyLoggedIn) {
                    val spotifyResponse = SpotifyClient.api.getSavedTracks(limit = 50)
                    nextSpotifyUrl = spotifyResponse.nextHref

                    val spotifyTracks = spotifyResponse.items.map { item ->
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

                    allTracks.addAll(spotifyTracks)
                }

                if(isSoundCloudConnected) {
                    val username = SoundCloudClient.connectedUsername
                    if (!username.isNullOrEmpty()){
                        val profileUrl = if (username.startsWith("http")){
                            username
                        } else {
                            "https://soundcloud.com/$username"
                        }

                        val scUser = SoundCloudClient.api.resolveUser(profileUrl)
                        val scResponse = SoundCloudClient.api.getUserLikes(userId = scUser.id, limit = 50)
                        nextSoundCloudUrl = scResponse.nextHref

                        val scTracks = scResponse.collection
                            .mapNotNull { it.track }
                            .map { track ->
                                val totalSec = track.durationMs / 1000
                                TrackUiModel(
                                    id = "sc_${track.id}",
                                    title = track.title,
                                    artist = track.user.username,
                                    coverUrl = track.artworkUrl?.replace("-large", "-t500x500"),
                                    durationFormatted = String.format("%d:%02d", totalSec / 60, totalSec % 60),
                                    source = TrackSource.SOUNDCLOUD
                                )
                            }

                        allTracks.addAll(scTracks)
                    }
                }
                uiState = LibraryUiState.Success(allTracks)
            } catch (e: Exception) {
                uiState = LibraryUiState.Error(e.localizedMessage ?: "Failed to fetch library")
                notificationController.show(
                    message = e.localizedMessage ?: "Failed to fetch tracks",
                    type = NotificationState.ERROR
                )
            }
        }
    }

    fun loadMoreTracks(){
        if (isLoadingMore) return

        if (nextSpotifyUrl == null && nextSoundCloudUrl == null) return

        coroutineScope.launch {
            isLoadingMore = true
            try {

                val newTracks = mutableListOf<TrackUiModel>()

                nextSoundCloudUrl?.let { scUrl ->
                    val scResponse = SoundCloudClient.api.getNextLikesPage(scUrl)
                    nextSoundCloudUrl = scResponse.nextHref

                    val newScTracks = scResponse.collection
                        .mapNotNull { it.track }
                        .map { track ->
                            val totalSec = track.durationMs / 1000
                            TrackUiModel(
                                id = "sc_${track.id}",
                                title = track.title,
                                artist = track.user.username,
                                coverUrl = track.artworkUrl?.replace("-large", "-t500x500"),
                                durationFormatted = String.format("%d:%02d", totalSec / 60, totalSec % 60),
                                source = TrackSource.SOUNDCLOUD
                            )
                        }
                    newTracks.addAll(newScTracks)
                }

                nextSpotifyUrl?.let { spotifyUrl ->
                    val spotifyResponse = SpotifyClient.api.getNextSavedTracks(spotifyUrl)
                    nextSpotifyUrl = spotifyResponse.nextHref
                    val newSpotifyTracks = spotifyResponse.items.map { item ->
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
                    newTracks.addAll(newSpotifyTracks)
                }

                if (newTracks.isNotEmpty()){
                    val currentTracks = (uiState as? LibraryUiState.Success)?.tracks ?: emptyList()
                    uiState = LibraryUiState.Success((currentTracks + newTracks).distinctBy { it.id })
                }
            } catch (e: Exception) {
                notificationController.show(
                    message = "Error while fetching next tracks ${e.localizedMessage}",
                    type = NotificationState.ERROR
                )
            } finally {
                isLoadingMore = false
            }
        }
    }

    LaunchedEffect(
        hasAnySource
    ) {
        if (hasAnySource){
            loadingLibraryTracks()
        } else {
            uiState = LibraryUiState.NotAuthorized
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount

            totalItems > 0 && lastVisibleItem >= totalItems - 5
        }
    }

    LaunchedEffect(
        shouldLoadMore.value
    ) {
        if (shouldLoadMore.value){
            loadMoreTracks()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(paddingValues = padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ){
                LibrarySearchBar(
                    searchQuery,
                    onQueryChange = {searchQuery = it}
                )

                Spacer(modifier = Modifier.height(10.dp))

                LibraryFilterChip(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it}
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    when( val state = uiState){
                        is LibraryUiState.NotAuthorized -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Connect your account",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = WaveifyRed),
                                    shape = RoundedCornerShape(12.dp)
                                ){
                                    Text("Go to profile", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        is LibraryUiState.Loading -> {
                            CircularProgressIndicator(color = WaveifyRed)
                        }
                        is LibraryUiState.Error -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(text = "Could not load library",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedButton(
                                    onClick = { loadingLibraryTracks() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveifyRed),
                                    border = BorderStroke(1.dp, WaveifyRed)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", Modifier.size(18.dp))

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retry")
                                }
                            }
                        }
                        is LibraryUiState.Success -> {
                            val sourceFilter = when (selectedFilter) {
                                LibraryFilter.ALL -> state.tracks
                                LibraryFilter.SOUNDCLOUD -> state.tracks.filter { it.source ==
                                        TrackSource.SOUNDCLOUD
                                }
                                LibraryFilter.SPOTIFY -> state.tracks.filter { it.source ==
                                        TrackSource.SPOTIFY
                                }
                            }

                            val finalTracks = if (searchQuery.isBlank()) {
                                sourceFilter
                            } else {
                                sourceFilter.filter {
                                    it.title.contains(searchQuery, ignoreCase = true) ||
                                            it.artist.contains(searchQuery, ignoreCase = true)
                                }
                            }

                            if (finalTracks.isEmpty()){
                                Text(
                                    text = "Could not found anything for $searchQuery",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextMuted
                                )
                            } else {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 50.dp, top = 8.dp)
                                ) {
                                    items(
                                        finalTracks, key = { it.id }) { track ->
                                        TrackItem(
                                            track = track,
                                            onClick = {
                                            }
                                        )
                                    }

                                    if(isLoadingMore){
                                        item{
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ){
                                                CircularProgressIndicator(
                                                    color = WaveifyRed,
                                                    modifier = Modifier.size(25.dp),
                                                    strokeWidth = 2.dp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        NotificationBanner(
            controller = notificationController,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = padding.calculateTopPadding() + 5.dp)
        )
    }
 }