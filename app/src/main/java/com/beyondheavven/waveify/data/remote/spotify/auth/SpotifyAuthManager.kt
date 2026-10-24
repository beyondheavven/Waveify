package com.beyondheavven.waveify.data.remote.spotify.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object SpotifyAuthManager {

    private const val TAG = "SpotifyAuth"

    var isAuthorized: Boolean = false
        private set

    private val scope = CoroutineScope(Dispatchers.IO)

    sealed interface AuthEvent {
        data object Success: AuthEvent
        data class Error(val message: String): AuthEvent
    }

    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvents = _authEvents.asSharedFlow()

    fun startLogin(context: Context) {
        val scopes = listOf(
            "streaming",
            "user-read-email",
            "user-read-private",
            "user-modify-playback-state",
            "user-read-playback-state",
            "user-library-read"
        ).joinToString(" ")

        val authUri = Uri.parse("https://accounts.spotify.com/authorize").buildUpon()
            .appendQueryParameter("client_id", BuildConfig.SPOTIFY_CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", BuildConfig.SPOTIFY_REDIRECT_URI)
            .appendQueryParameter("scope", scopes)
            .appendQueryParameter("show_dialog", "true")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, authUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun exchangeCodeForToken(code: String, onComplete: () -> Unit) {
        scope.launch {
            try {
                val success = SpotifyClient.exchangeCodeForToken(code)
                if (success) {
                    Log.d(TAG, "Authorization is successful! Access token obtained")
                    isAuthorized = true
                    _authEvents.emit(AuthEvent.Success)
                } else {
                    Log.e(TAG, "Failed to exchange code for token.")
                    _authEvents.emit(AuthEvent.Error("Failed to obtain token"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred $e")
                _authEvents.emit(AuthEvent.Error(e.message ?: "Authentication error"))
            } finally {
                onComplete()
            }
        }
    }

    fun notifyError(error: String) {
        scope.launch {
            _authEvents.emit(AuthEvent.Error("Spotify error: $error"))
        }
    }
}