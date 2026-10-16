package com.beyondheavven.waveify.ui.screen.library.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.ui.theme.DarkSurfaceElevated
import com.beyondheavven.waveify.ui.theme.DividerColor
import com.beyondheavven.waveify.ui.theme.TextMuted
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.TextSecondary
import com.beyondheavven.waveify.ui.theme.WaveifyRed

@Composable
fun LibrarySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search in saved tracks...", color = TextMuted)},
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
        },
        trailingIcon = {
            if (query.isNotEmpty()){
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WaveifyRed,
            unfocusedBorderColor = DividerColor,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = WaveifyRed,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurfaceElevated
        ),
        modifier = modifier.fillMaxWidth()
    )
}