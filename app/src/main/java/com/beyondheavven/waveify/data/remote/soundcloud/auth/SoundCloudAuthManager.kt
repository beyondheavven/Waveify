package com.beyondheavven.waveify.data.remote.soundcloud.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.data.remote.oauth.PkceUtils
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.soundcloud.service.SoundCloudDisconnectRequest
import com.beyondheavven.waveify.data.remote.soundcloud.service.SoundCloudTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object SoundCloudAuthManager {

    private const val TAG = "SoundCloudAuth"
    private const val PREFS_NAME = "soundcloud_auth_pending"
    private const val KEY_STATE = "state"
    private const val KEY_VERIFIER = "code_verifier"
    private const val KEY_CREATED_AT = "created_at"
    private const val AUTH_TIMEOUT_MS = 10 * 60 * 1000L

    private val scope = CoroutineScope(Dispatchers.IO)

    sealed interface AuthEvent {
        data object Success : AuthEvent
        data object Disconnected : AuthEvent
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

        val authUri = Uri.parse(BuildConfig.SOUNDCLOUD_AUTHORIZE_URL).buildUpon()
            .appendQueryParameter("client_id", BuildConfig.SOUNDCLOUD_CLIENT_ID)
            .appendQueryParameter("redirect_uri", BuildConfig.SOUNDCLOUD_REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("state", state)
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("display", "popup")
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

    fun logout() {
        val accessToken = SoundCloudClient.accessToken
        if (accessToken.isNullOrEmpty()) {
            SoundCloudClient.clear()
            scope.launch {
                _authEvents.emit(AuthEvent.Disconnected)
            }
            return
        }

        scope.launch {
            runCatching {
                SoundCloudClient.authApi.disconnect(SoundCloudDisconnectRequest(accessToken))
            }
            SoundCloudClient.clear()
            _authEvents.emit(AuthEvent.Disconnected)
        }
    }

    private fun exchangeCodeForToken(code: String, codeVerifier: String, onComplete: () -> Unit) {
        scope.launch {
            try {
                val response = SoundCloudClient.authApi.exchangeCode(
                    SoundCloudTokenRequest(
                        code = code,
                        codeVerifier = codeVerifier,
                        redirectUri = BuildConfig.SOUNDCLOUD_REDIRECT_URI
                    )
                )
                SoundCloudClient.applyToken(response)
                Log.d(TAG, "Authorization is successful! Access token obtained")
                _authEvents.emit(AuthEvent.Success)
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred ${e.message}")
                _authEvents.emit(AuthEvent.Error(e.message ?: "Authentication error"))
            } finally {
                onComplete()
            }
        }
    }

    fun notifyError(error: String) {
        scope.launch {
            _authEvents.emit(AuthEvent.Error("SoundCloud error: $error"))
        }
    }
}
