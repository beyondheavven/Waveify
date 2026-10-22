package com.beyondheavven.waveify.ui.screen.profile

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import com.beyondheavven.waveify.ui.ascii.AsciiColorMode
import com.beyondheavven.waveify.ui.ascii.AsciiSettings
import com.beyondheavven.waveify.ui.screen.profile.spotify.SpotifyLoginButton
import com.beyondheavven.waveify.ui.screen.notification.rememberNotificationController
import com.beyondheavven.waveify.ui.theme.DarkSurfaceElevated
import com.beyondheavven.waveify.ui.theme.DividerColor
import com.beyondheavven.waveify.ui.theme.TextMuted
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.WaveifyRed
import com.beyondheavven.waveify.ui.theme.WaveifyRedDark
import com.beyondheavven.waveify.ui.theme.WaveifyRedLight
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(padding: PaddingValues) {
    val coroutineScope = rememberCoroutineScope()

    val notificationController = rememberNotificationController()

    var isSpotifyConnected by remember { mutableStateOf(!SpotifyClient.accessToken.isNullOrEmpty()) }

    var isSoundCloudConnected by remember { mutableStateOf(!SoundCloudClient.connectedUsername.isNullOrEmpty()) }

    var soundCloudInput by remember { mutableStateOf(SoundCloudClient.connectedUsername ?: "") }

    var isSoundCloudLoading by remember { mutableStateOf(false)}

    var soundCloudError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp,
                if (isSpotifyConnected) WaveifyRed.copy(alpha = 0.4f) else DividerColor
            ),
            colors = CardDefaults.cardColors(
                containerColor = DarkSurfaceElevated
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    if (isSpotifyConnected) WaveifyRedDark else TextMuted,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Spotify",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (isSpotifyConnected) "Connected" else "Not connected",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSpotifyConnected) WaveifyRedLight else TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isSpotifyConnected) {
                    OutlinedButton(
                        onClick = {
                            SpotifyClient.accessToken = null
                            isSpotifyConnected = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Disconnect profile", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    SpotifyLoginButton(
                        onLoginSuccess = {
                            isSpotifyConnected = true
                        },
                        notificationController = notificationController
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp,
                if (isSoundCloudConnected) WaveifyRed.copy(alpha = 0.4f) else DividerColor
            ),
            colors = CardDefaults.cardColors(
                containerColor = DarkSurfaceElevated
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(if (isSoundCloudConnected) WaveifyRedDark else TextMuted, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SoundCloud",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (isSoundCloudConnected) "Connected" else "Not connected",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSoundCloudConnected) WaveifyRedLight else TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isSoundCloudConnected) {
                    Text(
                        text = "Profile: @${SoundCloudClient.connectedUsername}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            SoundCloudClient.connectedUsername = null
                            isSoundCloudConnected = false
                            soundCloudInput = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Disconnect profile", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    OutlinedTextField(
                        value = soundCloudInput,
                        onValueChange = {
                            soundCloudInput = it
                            soundCloudError = null
                        },
                        placeholder = { Text("Nickname or profile url")},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = soundCloudError != null,
                        supportingText = {
                            soundCloudError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (soundCloudInput.isNotBlank()){
                                coroutineScope.launch {
                                    isSoundCloudLoading = true
                                    soundCloudError = null
                                    try{
                                        val url = if (soundCloudInput.startsWith("http")){
                                            soundCloudInput
                                        } else {
                                            "https://soundcloud.com/$soundCloudInput"
                                        }

                                        val user = SoundCloudClient.api.resolveUser(url)

                                        SoundCloudClient.connectedUsername = user.username
                                        isSoundCloudConnected = true
                                    } catch (e: Exception){
                                        soundCloudError = "Profile not found"
                                    } finally {
                                        isSoundCloudLoading = false
                                    }
                                }
                            }
                        },
                        enabled = !isSoundCloudLoading && soundCloudInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = WaveifyRedDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSoundCloudLoading){
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Connect profile", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp,
                if (AsciiSettings.isEnabled) WaveifyRed.copy(alpha = 0.4f) else DividerColor
            ),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                if (AsciiSettings.isEnabled) WaveifyRed else TextMuted,
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    Column{
                        Text(
                            text = "Cover style",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (AsciiSettings.isEnabled) "ASCII: ${AsciiSettings.currentMode.name}" else "Original",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isOriginalSelected = !AsciiSettings.isEnabled
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isOriginalSelected) WaveifyRed.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isOriginalSelected) WaveifyRed else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                AsciiSettings.isEnabled = false
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Original",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isOriginalSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isOriginalSelected) TextPrimary else TextMuted
                        )
                    }

                    AsciiColorMode.entries.forEach { mode ->
                        val isSelected = AsciiSettings.isEnabled && AsciiSettings.currentMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) WaveifyRed.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) WaveifyRed else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable{
                                    AsciiSettings.isEnabled = true
                                    AsciiSettings.currentMode = mode
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(mode.indicatorColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) { }
    }
}