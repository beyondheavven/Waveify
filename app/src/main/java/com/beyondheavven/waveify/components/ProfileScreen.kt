package com.beyondheavven.waveify.components

import android.widget.Space
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import com.beyondheavven.waveify.ui.theme.WaveifyRed
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(padding: PaddingValues) {
    val coroutineScope = rememberCoroutineScope()

    var isSpotifyLoggedIn by remember { mutableStateOf(!SpotifyClient.accessToken.isNullOrEmpty()) }

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
            text = "Профиль",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                    if (isSpotifyLoggedIn) Color(0xFF1DB954) else Color.Gray,
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
                        text = if (isSpotifyLoggedIn) "Подключен" else "Не подключен",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSpotifyLoggedIn) Color(0xFF1DB954) else MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isSpotifyLoggedIn) {
                    OutlinedButton(
                        onClick = {
                            SpotifyClient.accessToken = null
                            isSpotifyLoggedIn = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Отключить аккаунт", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    SpotifyLoginButton(
                        onLoginSuccess = {
                            isSpotifyLoggedIn = true
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
                                .background(if (isSoundCloudConnected) Color(0xFFFF5500) else Color.Gray, CircleShape)
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
                        color = if (isSoundCloudConnected) Color(0xFFFF5500) else MaterialTheme.colorScheme.outline
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
                        Text("Turn off profile", color = MaterialTheme.colorScheme.error)
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5500)),
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
    }

}