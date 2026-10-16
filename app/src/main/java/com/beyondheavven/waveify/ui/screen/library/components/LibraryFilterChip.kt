package com.beyondheavven.waveify.ui.screen.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.ui.screen.library.LibraryFilter
import com.beyondheavven.waveify.ui.theme.DarkSurfaceElevated
import com.beyondheavven.waveify.ui.theme.DividerColor
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.TextSecondary
import com.beyondheavven.waveify.ui.theme.WaveifyRed

@Composable
fun LibraryFilterChip(
    selectedFilter: LibraryFilter,
    onFilterSelected: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = "Your likes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
            FilterChip(
                selected = selectedFilter == LibraryFilter.ALL,
                onClick = {onFilterSelected(LibraryFilter.ALL)},
                label = {Text("All")},
                colors = chipColors(),
                border = borderColors(selectedFilter == LibraryFilter.ALL)
            )

            FilterChip(
                selected = selectedFilter == LibraryFilter.SPOTIFY,
                onClick = {onFilterSelected(LibraryFilter.SPOTIFY)},
                label = {Text("Spotify")},
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                MaterialTheme.colorScheme.background,
                                CircleShape
                            )
                    )
                },
                colors = chipColors(),
                border = borderColors(selectedFilter == LibraryFilter.SPOTIFY)
            )

            FilterChip(
                selected = selectedFilter == LibraryFilter.SOUNDCLOUD,
                onClick = {onFilterSelected(LibraryFilter.SOUNDCLOUD)},
                label = {Text("SoundCloud")},
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                MaterialTheme.colorScheme.background,
                                CircleShape
                            )
                    )
                },
                colors = chipColors(),

                border = borderColors(selectedFilter == LibraryFilter.SOUNDCLOUD)
            )
        }
    }
}

@Composable
fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = WaveifyRed,
    selectedLabelColor = TextPrimary,
    containerColor = DarkSurfaceElevated,
    labelColor = TextSecondary
)

@Composable
fun borderColors(isSelected: Boolean ) = FilterChipDefaults.filterChipBorder(
    enabled = true,
    selected = isSelected,
    selectedBorderColor = WaveifyRed,
    borderColor = DividerColor
)