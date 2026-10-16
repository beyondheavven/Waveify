package com.beyondheavven.waveify.ui.screen.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyondheavven.waveify.data.model.TrackSource
import com.beyondheavven.waveify.player.spotify.SpotifyPlayerManager
import com.beyondheavven.waveify.ui.theme.DarkBackground
import com.beyondheavven.waveify.ui.theme.TextMuted
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.TextSecondary
import com.beyondheavven.waveify.ui.component.NotificationBanner
import com.beyondheavven.waveify.ui.component.NotificationState
import com.beyondheavven.waveify.ui.component.TrackItem
import com.beyondheavven.waveify.ui.component.rememberNotificationController
import com.beyondheavven.waveify.ui.screen.library.components.LibraryFilterChip
import com.beyondheavven.waveify.ui.screen.library.components.LibrarySearchBar
import com.beyondheavven.waveify.ui.theme.WaveifyRed


@Composable
fun LibraryScreen(
    padding: PaddingValues,
    viewModel: LibraryViewModel = viewModel(),
    onNavigationToProfile: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val notificationController = rememberNotificationController()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadInitialTracks()
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
            viewModel.loadMoreTracks()
        }
    }

    LaunchedEffect(
        viewModel.paginationError
    ) {
        viewModel.paginationError?.let { errorMsg ->
            notificationController.show(
                message = errorMsg,
                type = NotificationState.ERROR
            )
            viewModel.clearPaginationError()
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
                    query = viewModel.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(newQuery = it)}
                )

                Spacer(modifier = Modifier.height(10.dp))

                LibraryFilterChip(
                    selectedFilter = viewModel.selectedFilter,
                    onFilterSelected = { viewModel.onFilterSelected(filter = it)}
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    when( val state = viewModel.uiState){
                        is LibraryUiState.Loading -> {
                            CircularProgressIndicator(color = WaveifyRed)
                        }
                        is LibraryUiState.NotAuthorized -> {
                            NotAuthorizedContent(onGoToProfile = onNavigationToProfile)
                        }
                        is LibraryUiState.Error -> {
                            ErrorContent(
                                message = state.message,
                                onRetry = { viewModel.loadInitialTracks() }
                            )
                        }
                        is LibraryUiState.Success -> {
                            val tracks = viewModel.filteredTracks

                            if (tracks.isEmpty()){
                                Text(
                                    text = if (viewModel.searchQuery.isNotBlank()){
                                        "Could not found anything for \"${viewModel.searchQuery}\""
                                    } else {
                                        "No tracks found"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(24.dp)
                                )
                            } else {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 50.dp, top = 8.dp)
                                ) {
                                    items(
                                        tracks, key = { it.id }) { track ->
                                        TrackItem(
                                            track = track,
                                            onClick = {
                                                when (track.source) {
                                                    TrackSource.SPOTIFY -> {
                                                        SpotifyPlayerManager.play(track.id, context)
                                                    }
                                                    TrackSource.SOUNDCLOUD -> {
                                                        //TODO
                                                    }
                                                }
                                            }
                                        )
                                    }

                                    if(viewModel.isLoadingMore){
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

@Composable
private fun NotAuthorizedContent(onGoToProfile: () -> Unit) {
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
            onClick = onGoToProfile,
            colors = ButtonDefaults.buttonColors(containerColor = WaveifyRed),
            shape = RoundedCornerShape(12.dp)
        ){
            Text("Go to profile", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Could not load library. Error: $message",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = onRetry,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveifyRed),
            border = BorderStroke(1.dp, WaveifyRed)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", Modifier.size(18.dp))

            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}