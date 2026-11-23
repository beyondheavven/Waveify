package com.beyondheavven.waveify.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyondheavven.waveify.core.ui.component.TrackItem
import com.beyondheavven.waveify.feature.player.waveify.WaveifyPlayer
import com.beyondheavven.waveify.feature.library.LibraryEmptyState
import com.beyondheavven.waveify.feature.library.LibraryErrorContent
import com.beyondheavven.waveify.feature.library.LibraryUiState
import com.beyondheavven.waveify.feature.library.LibraryViewModel
import com.beyondheavven.waveify.feature.library.NotAuthorizedContent
import com.beyondheavven.waveify.feature.library.components.LibraryFilterChip
import com.beyondheavven.waveify.feature.library.components.LibrarySearchBar
import com.beyondheavven.waveify.data.repository.LibraryFilter
import com.beyondheavven.waveify.theme.WaveifyColors

@Composable
fun SearchScreen(
    padding: PaddingValues,
    onNavigateToProfile: () -> Unit = {},
    viewModel: LibraryViewModel = viewModel()
) {
    val context = LocalContext.current
    val palette = WaveifyColors.current

    LaunchedEffect(Unit) {
        viewModel.syncLibrary()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Search",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Find something to listen to",
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
                    NotAuthorizedContent(onGoToProfile = onNavigateToProfile)
                }

                is LibraryUiState.Error -> {
                    LibraryErrorContent(
                        message = state.message,
                        onRetry = { viewModel.syncLibrary() }
                    )
                }

                is LibraryUiState.Success -> {
                    val tracks = viewModel.filteredTracks
                    when {
                        viewModel.searchQuery.isBlank() -> {
                            LibraryEmptyState(message = "Type a track or artist name to search")
                        }

                        tracks.isEmpty() -> {
                            LibraryEmptyState(message = "No results for \"${viewModel.searchQuery}\"")
                        }

                        else -> {
                            LazyColumn(
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
                            }
                        }
                    }
                }
            }
        }
    }
}
