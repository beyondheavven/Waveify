package com.beyondheavven.waveify.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.soundcloud.auth.SoundCloudAuthManager
import com.beyondheavven.waveify.data.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.spotify.client.SpotifyClient
import com.beyondheavven.waveify.core.ui.notification.NotificationBanner
import com.beyondheavven.waveify.core.ui.notification.rememberNotificationController
import com.beyondheavven.waveify.feature.profile.soundcloud.SoundCloudLoginButton
import com.beyondheavven.waveify.feature.profile.spotify.SpotifyLoginButton
import com.beyondheavven.waveify.theme.WaveifyColors

@Composable
fun ProfileScreen(
    padding: PaddingValues,
    onNavigateToSettings: () -> Unit = {}
) {
    val notificationController = rememberNotificationController()
    val palette = WaveifyColors.current

    var isSpotifyConnected by remember { mutableStateOf(!SpotifyClient.accessToken.isNullOrEmpty()) }
    var isSoundCloudConnected by remember { mutableStateOf(SoundCloudClient.isAuthorized) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Connect your services",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.muted
            )

            Spacer(modifier = Modifier.height(32.dp))

            ConnectionCard(
                name = "Spotify",
                connected = isSpotifyConnected,
                detail = if (isSpotifyConnected) "Your library is synced" else "Not connected",
                topCorner = true,
                bottomCorner = false,
                content = {
                    if (isSpotifyConnected) {
                        DisconnectButton {
                            SpotifyClient.accessToken = null
                            SpotifyClient.refreshToken = null
                            isSpotifyConnected = false
                        }
                    } else {
                        SpotifyLoginButton(
                            onLoginSuccess = { isSpotifyConnected = true },
                            notificationController = notificationController
                        )
                    }
                }
            )

            ConnectionCard(
                name = "SoundCloud",
                connected = isSoundCloudConnected,
                detail = if (isSoundCloudConnected) {
                    "@${SoundCloudClient.currentUser?.username.orEmpty()}"
                } else {
                    "Not connected"
                },
                topCorner = false,
                bottomCorner = true,
                content = {
                    if (isSoundCloudConnected) {
                        DisconnectButton {
                            SoundCloudAuthManager.logout()
                            isSoundCloudConnected = false
                        }
                    } else {
                        SoundCloudLoginButton(
                            onLoginSuccess = { isSoundCloudConnected = true },
                            notificationController = notificationController
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSettings() },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, palette.subtleBorder),
                colors = CardDefaults.cardColors(containerColor = palette.elevatedSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = palette.muted
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
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
private fun ConnectionCard(
    name: String,
    connected: Boolean,
    detail: String,
    topCorner: Boolean,
    bottomCorner: Boolean,
    content: @Composable () -> Unit
) {
    val palette = WaveifyColors.current
    val shape = RoundedCornerShape(
        topStart = if (topCorner) 18.dp else 2.dp,
        topEnd = if (topCorner) 18.dp else 2.dp,
        bottomStart = if (bottomCorner) 18.dp else 2.dp,
        bottomEnd = if (bottomCorner) 18.dp else 2.dp
    )
    Card(
        shape = shape,
        border = BorderStroke(1.dp, palette.subtleBorder),
        colors = CardDefaults.cardColors(containerColor = palette.elevatedSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (connected) MaterialTheme.colorScheme.primary else palette.muted
                        )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (connected) palette.successContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (connected) "Connected" else "Not connected",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (connected) palette.success else palette.muted
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = palette.muted,
                modifier = Modifier.padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun DisconnectButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Disconnect")
    }
}
