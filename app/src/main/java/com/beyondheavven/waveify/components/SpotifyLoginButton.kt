package com.beyondheavven.waveify.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import com.beyondheavven.waveify.BuildConfig
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

@Composable
fun SpotifyLoginButton(
    onLoginSuccess: () -> Unit
){
    val context = LocalContext.current
    val activity = context as? Activity

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = AuthorizationClient.getResponse(result.resultCode, result.data)

        when(response.type){
            AuthorizationResponse.Type.TOKEN -> {
                val token = response.accessToken
                SpotifyClient.accessToken = token
                onLoginSuccess()
            }
            AuthorizationResponse.Type.ERROR -> {
                println("Ошибка авторизации: ${response.error}")
            }
            else -> {}
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
        }
    ) {
        Text("Войти через спотифай")
    }

}