package com.beyondheavven.waveify.ui.screen.profile.spotify

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.beyondheavven.waveify.data.remote.spotify.auth.SpotifyAuthManager
import com.beyondheavven.waveify.ui.screen.notification.NotificationController
import com.beyondheavven.waveify.ui.screen.notification.NotificationState
import com.beyondheavven.waveify.ui.theme.WaveifyRedDark
@Composable
fun SpotifyLoginButton(
    onLoginSuccess: () -> Unit,
    notificationController: NotificationController,
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        SpotifyAuthManager.authEvents.collect { event ->
            when (event) {
                is SpotifyAuthManager.AuthEvent.Success -> {
                    notificationController.show(
                        message = "Successfully connected to Spotify",
                        NotificationState.SUCCESS
                    )
                    onLoginSuccess()
                }
                is SpotifyAuthManager.AuthEvent.Error -> {
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
            SpotifyAuthManager.startLogin(context)
        },
        colors = ButtonDefaults.buttonColors(containerColor = WaveifyRedDark),
        modifier = modifier.fillMaxWidth()
    ) {
        Text("Connect profile", color = Color.White)
    }

}
