package com.beyondheavven.waveify.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySavedTracksResponse
import com.beyondheavven.waveify.ui.theme.DarkBackground
import com.beyondheavven.waveify.ui.theme.DarkSurfaceElevated
import com.beyondheavven.waveify.ui.theme.DividerColor
import com.beyondheavven.waveify.ui.theme.TextMuted
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.TextSecondary
import com.beyondheavven.waveify.ui.theme.TrackSource
import com.beyondheavven.waveify.ui.theme.TrackUiModel
import com.beyondheavven.waveify.ui.theme.WaveifyRed
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
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val isSpotifyLoggedIn = !SpotifyClient.accessToken.isNullOrEmpty()
    val isSoundCloudConnected = !SoundCloudClient.connectedUsername.isNullOrEmpty()
    val hasAnySource = isSpotifyLoggedIn || isSoundCloudConnected

    var selectedFilter by remember { mutableStateOf(LibraryFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var uiState by remember {
        mutableStateOf<LibraryUiState>(
            if (hasAnySource) LibraryUiState.Loading else LibraryUiState.NotAuthorized
        )
    }

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
                    try {
                        val spotifyResponse = SpotifyClient.api.getSavedTracks(limit = 50)
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
                    } catch (e: Exception) {
                        println("Error while loading Spotify tracks: ${e.message}")
                    }
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
                        val scResponse = SoundCloudClient.api.getUserTracks(userId = scUser.id, limit = 50)

                        val scTracks = scResponse.collection.map { track ->
                            val totalSec = track.durationMs / 1000
                            TrackUiModel(
                                id = "sc_${track.id}",
                                title = track.title,
                                artist = track.user?.username ?: "SoundCloud Artist",
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(paddingValues = padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (uiState is LibraryUiState.Success){
                    IconButton(onClick = { isSearchActive = !isSearchActive}) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSearchActive) WaveifyRed else TextSecondary
                        )
                    }
                }

                AnimatedVisibility(visible = isSearchActive
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search in saved tracks...", color = TextMuted)},
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WaveifyRed,
                            unfocusedBorderColor = DividerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = WaveifyRed,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    FilterChip(
                        selected = selectedFilter == LibraryFilter.ALL,
                        onClick = {selectedFilter = LibraryFilter.ALL},
                        label = {Text("All")},
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WaveifyRed,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextSecondary
                        ),

                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == LibraryFilter.ALL,
                            selectedBorderColor = WaveifyRed,
                            borderColor = DividerColor
                        )
                    )

                    FilterChip(
                        selected = selectedFilter == LibraryFilter.SPOTIFY,
                        onClick = {selectedFilter = LibraryFilter.SPOTIFY},
                        label = {Text("Spotify")},
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(MaterialTheme.colorScheme.background,CircleShape)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WaveifyRed,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextSecondary
                        ),

                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == LibraryFilter.SPOTIFY,
                            selectedBorderColor = WaveifyRed,
                            borderColor = DividerColor
                        )
                    )

                    FilterChip(
                        selected = selectedFilter == LibraryFilter.SOUNDCLOUD,
                        onClick = {selectedFilter = LibraryFilter.SOUNDCLOUD},
                        label = {Text("Soundcloud")},
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(MaterialTheme.colorScheme.background,CircleShape)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WaveifyRed,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextSecondary
                        ),

                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == LibraryFilter.SOUNDCLOUD,
                            selectedBorderColor = WaveifyRed,
                            borderColor = DividerColor
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
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
                                text = "Connect Your Accounts",
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
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(
                                    finalTracks, key = { it.id }) { track ->
                                    TrackItem(
                                        track = track,
                                        onClick = {

                                        }
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