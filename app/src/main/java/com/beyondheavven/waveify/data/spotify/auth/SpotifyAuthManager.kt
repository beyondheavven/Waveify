package com.beyondheavven.waveify.data.spotify.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.core.network.oauth.AuthErrorParser
import com.beyondheavven.waveify.core.network.oauth.PkceUtils
import com.beyondheavven.waveify.data.spotify.client.SpotifyClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object SpotifyAuthManager {

    private const val TAG = "SpotifyAuth"
    private const val PREFS_NAME = "spotify_auth_pending"
    private const val KEY_STATE = "state"
    private const val KEY_VERIFIER = "code_verifier"
    private const val KEY_CREATED_AT = "created_at"
    private const val AUTH_TIMEOUT_MS = 10 * 60 * 1000L

    var isAuthorized: Boolean = false
        private set

    private val scope = CoroutineScope(Dispatchers.IO)

    sealed interface AuthEvent {
        data object Success : AuthEvent
        data class Error(val message: String) : AuthEvent
    }

    private val _authEvents = MutableSharedFlow<AuthEvent>(replay = 1)
    val authEvents = _authEvents.asSharedFlow()

    private fun pendingStorage(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun startLogin(context: Context) {
        val state = PkceUtils.generateState()
        val codeVerifier = PkceUtils.generateVerifier()
        val codeChallenge = PkceUtils.generateChallenge(codeVerifier)

        pendingStorage(context).edit()
            .putString(KEY_STATE, state)
            .putString(KEY_VERIFIER, codeVerifier)
            .putLong(KEY_CREATED_AT, System.currentTimeMillis())
            .apply()

        val scopes = listOf(
            "streaming",
            "user-read-email",
            "user-read-private",
            "user-modify-playback-state",
            "user-read-playback-state",
            "user-library-read",
            "user-top-read",
            "playlist-read-private",
            "playlist-read-collaborative"
        ).joinToString(" ")

        val authUri = Uri.parse("https://accounts.spotify.com/authorize").buildUpon()
            .appendQueryParameter("client_id", BuildConfig.SPOTIFY_CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", BuildConfig.SPOTIFY_REDIRECT_URI)
            .appendQueryParameter("scope", scopes)
            .appendQueryParameter("state", state)
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("show_dialog", "true")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, authUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun handleCallback(
        context: Context,
        code: String?,
        state: String?,
        error: String?,
        errorDescription: String?,
        onComplete: () -> Unit
    ) {
        if (error != null) {
            notifyError(errorDescription ?: error)
            onComplete()
            return
        }

        if (code == null || state == null) {
            notifyError("Invalid authorization response")
            onComplete()
            return
        }

        val storage = pendingStorage(context)
        val expectedState = storage.getString(KEY_STATE, null)
        val codeVerifier = storage.getString(KEY_VERIFIER, null)
        val createdAt = storage.getLong(KEY_CREATED_AT, 0L)

        if (expectedState == null || codeVerifier == null) {
            notifyError("Authorization request not found")
            onComplete()
            return
        }

        if (state != expectedState) {
            notifyError("Invalid authorization state")
            onComplete()
            return
        }

        if (System.currentTimeMillis() - createdAt > AUTH_TIMEOUT_MS) {
            storage.edit().clear().apply()
            notifyError("Authorization request expired")
            onComplete()
            return
        }

        storage.edit().clear().apply()
        exchangeCodeForToken(code, codeVerifier, onComplete)
    }

    private fun exchangeCodeForToken(code: String, codeVerifier: String, onComplete: () -> Unit) {
        scope.launch {
            try {
                SpotifyClient.exchangeCodeForToken(code, codeVerifier)
                Log.d(TAG, "Authorization is successful! Access token obtained")
                isAuthorized = true
                _authEvents.emit(AuthEvent.Success)
            } catch (e: Exception) {
                val message = AuthErrorParser.message(e)
                Log.e(TAG, "Failed to exchange code for token: $message")
                _authEvents.emit(AuthEvent.Error(message))
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
