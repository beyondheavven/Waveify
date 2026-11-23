package com.beyondheavven.waveify.feature.profile.soundcloud

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.soundcloud.auth.SoundCloudAuthManager
import com.beyondheavven.waveify.data.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.core.ui.notification.NotificationController
import com.beyondheavven.waveify.core.ui.notification.NotificationState
import kotlinx.coroutines.launch

@Composable
fun SoundCloudLoginButton(
    onLoginSuccess: () -> Unit,
    notificationController: NotificationController,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SoundCloudAuthManager.authEvents.collect { event ->
            when (event) {
                is SoundCloudAuthManager.AuthEvent.Success -> {
                    if (!SoundCloudClient.isAuthorized) return@collect
                    notificationController.show(
                        message = "SoundCloud connected as @${SoundCloudClient.nickname.orEmpty()}",
                        NotificationState.SUCCESS
                    )
                    onLoginSuccess()
                }

                is SoundCloudAuthManager.AuthEvent.Disconnected -> {
                    if (SoundCloudClient.isAuthorized) return@collect
                }

                is SoundCloudAuthManager.AuthEvent.Error -> {
                    notificationController.show(
                        message = event.message,
                        NotificationState.ERROR
                    )
                }
            }
        }
    }

    Button(
        onClick = {
            error = null
            showDialog = true
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Connect with nickname",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) showDialog = false },
            title = { Text(text = "Connect SoundCloud") },
            text = {
                Column {
                    Text(
                        text = "Enter your SoundCloud nickname. The app will get a public client ID and load your profile — no paid developer app needed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = {
                            nickname = it
                            error = null
                        },
                        label = { Text("Nickname") },
                        placeholder = { Text("your-name") },
                        singleLine = true,
                        isError = error != null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            error = null
                            val result = SoundCloudAuthManager.connectWithNickname(nickname)
                            isLoading = false
                            result.onSuccess {
                                showDialog = false
                                nickname = ""
                            }.onFailure { throwable ->
                                error = throwable.message ?: "Could not connect SoundCloud"
                            }
                        }
                    },
                    enabled = !isLoading && nickname.isNotBlank()
                ) {
                    Text(if (isLoading) "Connecting..." else "Connect")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
