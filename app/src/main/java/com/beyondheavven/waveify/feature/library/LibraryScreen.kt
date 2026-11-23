package com.beyondheavven.waveify.feature.library

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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyondheavven.waveify.core.ui.component.TrackItem
import com.beyondheavven.waveify.feature.player.waveify.WaveifyPlayer
import com.beyondheavven.waveify.feature.library.components.LibraryFilterChip
import com.beyondheavven.waveify.feature.library.components.LibrarySearchBar
import com.beyondheavven.waveify.data.repository.LibraryFilter
import com.beyondheavven.waveify.core.ui.notification.NotificationBanner
import com.beyondheavven.waveify.core.ui.notification.NotificationState
import com.beyondheavven.waveify.core.ui.notification.rememberNotificationController
import com.beyondheavven.waveify.theme.WaveifyColors

@Composable
fun LibraryScreen(
    padding: PaddingValues,
    viewModel: LibraryViewModel = viewModel(),
    onNavigationToProfile: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val notificationController = rememberNotificationController()
    val context = LocalContext.current
    val palette = WaveifyColors.current

    LaunchedEffect(Unit) {
        viewModel.syncLibrary()
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisibleItem >= totalItems - 5
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMoreTracks()
        }
    }

    LaunchedEffect(viewModel.paginationError) {
        viewModel.paginationError?.let { errorMsg ->
            notificationController.show(message = errorMsg, type = NotificationState.ERROR)
            viewModel.clearPaginationError()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Everything you've saved",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.muted
                )

                Spacer(modifier = Modifier.height(24.dp))

                LibrarySearchBar(
                    query = viewModel.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                LibraryFilterChip(
                    selectedFilter = viewModel.selectedFilter,
                    onFilterSelected = { viewModel.onFilterSelected(it) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = viewModel.uiState) {
                    is LibraryUiState.Loading -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }

                    is LibraryUiState.NotAuthorized -> {
                        NotAuthorizedContent(onGoToProfile = onNavigationToProfile)
                    }

                    is LibraryUiState.Error -> {
                        LibraryErrorContent(
                            message = state.message,
                            onRetry = { viewModel.syncLibrary() }
                        )
                    }

                    is LibraryUiState.Success -> {
                        val tracks = viewModel.filteredTracks
                        if (tracks.isEmpty()) {
                            LibraryEmptyState(
                                message = if (viewModel.searchQuery.isNotBlank()) {
                                    "No results for \"${viewModel.searchQuery}\""
                                } else {
                                    "No saved tracks yet"
                                }
                            )
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(tracks, key = { it.id }) { track ->
                                    Column {
                                        TrackItem(
                                            track = track,
                                            onClick = { WaveifyPlayer.play(track, tracks, context) }
                                        )
                                        HorizontalDivider(
                                            modifier = Modifier.padding(start = 80.dp),
                                            thickness = 1.dp,
                                            color = palette.subtleBorder
                                        )
                                    }
                                }

                                if (viewModel.isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(22.dp),
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

        NotificationBanner(
            controller = notificationController,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = padding.calculateTopPadding() + 4.dp)
        )
    }
}

@Composable
fun NotAuthorizedContent(onGoToProfile: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Connect your accounts",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Sign in to Spotify or SoundCloud to see your music here.",
            style = MaterialTheme.typography.bodyMedium,
            color = WaveifyColors.current.muted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onGoToProfile,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Go to profile")
        }
    }
}

@Composable
fun LibraryErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Couldn't load your library",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = WaveifyColors.current.muted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            onClick = onRetry,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Retry")
        }
    }
}

@Composable
fun LibraryEmptyState(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = WaveifyColors.current.muted,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(24.dp)
    )
}
