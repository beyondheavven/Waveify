package com.beyondheavven.waveify.ui.screen.home.card

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.ui.theme.DarkSurfaceElevated
import com.beyondheavven.waveify.ui.theme.DividerColor
import com.beyondheavven.waveify.ui.theme.TextMuted
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.WaveifyRed
import com.beyondheavven.waveify.ui.theme.WaveifyRedDark


@Composable
fun FavouriteTracksCard(
    trackCount: Int = 0,
    isLoggedIn: Boolean = false,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
){

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick()},
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DividerColor),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
            Row(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WaveifyRed.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favourite tracks",
                        tint = WaveifyRed,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Favourite tracks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isLoggedIn) {
                            if (trackCount > 0) "$trackCount tracks" else "Spotify Library"
                        } else {
                            "Connect to view"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Listen all",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

}