package com.beyondheavven.waveify.ui.screen.profile.spotify

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.ui.component.NotificationController
import com.beyondheavven.waveify.ui.component.NotificationState
import com.beyondheavven.waveify.ui.theme.WaveifyRedDark
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.launch

@Composable
fun SpotifyLoginButton(
    onLoginSuccess: () -> Unit,
    notificationController: NotificationController,
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = AuthorizationClient.getResponse(result.resultCode, result.data)
        when(response.type){
            AuthorizationResponse.Type.CODE -> {
                val code = response.code
                coroutineScope.launch {
                    handleCodeReceived(code, notificationController, onLoginSuccess)
                }
            }
            AuthorizationResponse.Type.TOKEN -> {
                val token = response.accessToken
                SpotifyClient.accessToken = token
                notificationController.show(
                    message = "Successfully connected to Spotify",
                    NotificationState.SUCCESS
                )
            }
            AuthorizationResponse.Type.ERROR -> {
                notificationController.show(
                    message = "Error occurred while logging to Spotify",
                    NotificationState.ERROR
                )
            }
            else -> {
                notificationController.show(
                    message = "Unknown error occurred",
                    NotificationState.ERROR
                )
            }
        }

    }

    Button(
        onClick = {
            if (activity != null){
                val builder = AuthorizationRequest.Builder(
                    BuildConfig.SPOTIFY_CLIENT_ID,
                    AuthorizationResponse.Type.CODE,
                    BuildConfig.SPOTIFY_REDIRECT_URI
                )
                builder.setScopes(arrayOf("user-library-read", "playlist-read-private"))
                val request = builder.build()
                val intent = AuthorizationClient.createLoginActivityIntent(activity, request)
                authLauncher.launch(intent)
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = WaveifyRedDark),
        modifier = modifier.fillMaxWidth()
    ) {
        Text("Connect profile", color = Color.White)
    }

}

private suspend fun handleCodeReceived(
    code: String,
    notificationController : NotificationController,
    onLoginSuccess: () -> Unit
) {
    val success = SpotifyClient.exchangeCodeForToken(code)
    if (success) {
        notificationController.show(
            message = "Successfully connected to Spotify",
            NotificationState.SUCCESS
        )
        onLoginSuccess()
    } else {
        notificationController.show(
            message = "Failed to obtain token",
            NotificationState.ERROR
        )
    }
}
