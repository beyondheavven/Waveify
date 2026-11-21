package com.beyondheavven.waveify.ui.screen.profile.soundcloud

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.data.remote.soundcloud.auth.SoundCloudAuthManager
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.ui.screen.notification.NotificationController
import com.beyondheavven.waveify.ui.screen.notification.NotificationState
import com.beyondheavven.waveify.ui.theme.WaveifyRed

@Composable
fun SoundCloudLoginButton(
    onLoginSuccess: () -> Unit,
    notificationController: NotificationController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        SoundCloudAuthManager.authEvents.collect { event ->
            when (event) {
                is SoundCloudAuthManager.AuthEvent.Success -> {
                    if (!SoundCloudClient.isAuthorized) return@collect
                    notificationController.show(
                        message = "Successfully connected to SoundCloud",
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
            SoundCloudAuthManager.startLogin(context)
        },
        colors = ButtonDefaults.buttonColors(containerColor = WaveifyRed),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Connect profile",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}
