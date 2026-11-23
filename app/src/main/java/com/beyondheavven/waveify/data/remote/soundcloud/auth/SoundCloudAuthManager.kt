package com.beyondheavven.waveify.data.remote.soundcloud.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.data.remote.oauth.AuthErrorParser
import com.beyondheavven.waveify.data.remote.oauth.PkceUtils
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudProfileModel
import com.beyondheavven.waveify.data.remote.soundcloud.service.SoundCloudDisconnectRequest
import com.beyondheavven.waveify.data.remote.soundcloud.service.SoundCloudTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object SoundCloudAuthManager {

    private const val TAG = "SoundCloudAuth"
    private const val PREFS_NAME = "soundcloud_auth_pending"
    private const val KEY_STATE = "state"
    private const val KEY_VERIFIER = "code_verifier"
    private const val KEY_CREATED_AT = "created_at"
    private const val AUTH_TIMEOUT_MS = 10 * 60 * 1000L
    private const val DEFAULT_CLIENT_ID = "QhVEWrrJKvW4uSU5u5EoI0FTXYUGzy5k"
    private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"

    private val scope = CoroutineScope(Dispatchers.IO)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val nicknamePattern = Regex("^[A-Za-z0-9_-]{1,80}$")
    private val idPatterns = listOf(
        Regex(""""id"\s*:\s*"([A-Za-z0-9]{16,80})""""),
        Regex("""\bid\s*:\s*'([A-Za-z0-9]{16,80})'"""),
        Regex("""client_id["']?\s*[:=]\s*["']([A-Za-z0-9]{16,80})["']""")
    )

    sealed interface AuthEvent {
        data object Success : AuthEvent
        data object Disconnected : AuthEvent
        data class Error(val message: String) : AuthEvent
    }

    private val _authEvents = MutableSharedFlow<AuthEvent>(replay = 1)
    val authEvents = _authEvents.asSharedFlow()

    fun init(context: Context) {
        SoundCloudClient.init(context)
    }

    private fun pendingStorage(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun connectWithNickname(nickname: String): Result<SoundCloudProfileModel> = runCatching {
        val normalized = normalizeNickname(nickname)
            ?: throw IllegalArgumentException("Enter a valid SoundCloud nickname")

        val clientId = withContext(Dispatchers.IO) { resolveClientId(normalized) }
        SoundCloudClient.clientId = clientId

        val user = withContext(Dispatchers.IO) {
            SoundCloudClient.api.resolveUser("https://soundcloud.com/$normalized")
        }
        val profile = SoundCloudProfileModel(
            id = user.id,
            username = user.username,
            avatarUrl = user.avatarUrl,
            permalinkUrl = user.permalinkUrl
        )
        SoundCloudClient.saveNicknameSession(normalized, profile)
        Log.d(TAG, "Connected SoundCloud profile @$normalized")
        _authEvents.emit(AuthEvent.Success)
        profile
    }

    internal fun normalizeNickname(input: String): String? {
        val trimmed = input.trim().removePrefix("@")
        val candidate = if (trimmed.contains("soundcloud.com/")) {
            trimmed.substringAfterLast("soundcloud.com/")
                .substringBefore("?")
                .substringBefore("/")
                .trim('/')
        } else {
            trimmed
        }
        return candidate.takeIf { nicknamePattern.matches(it) }
    }

    internal fun extractClientId(html: String): String? {
        val markerIndex = html.indexOf("apiClient")
        val area = if (markerIndex >= 0) {
            html.substring(markerIndex, kotlin.math.min(markerIndex + 3000, html.length))
        } else {
            html
        }
        for (pattern in idPatterns) {
            val match = pattern.find(area)
            if (match != null) return match.groupValues[1]
        }
        return null
    }

    private suspend fun resolveClientId(nickname: String): String = withContext(Dispatchers.IO) {
        val profileHtml = fetchHtml("https://soundcloud.com/$nickname")
        extractClientId(profileHtml)
            ?: extractClientId(fetchHtml("https://soundcloud.com/"))
            ?: throw IllegalStateException("Could not get SoundCloud client id. Try again later.")
    }

    private fun fetchHtml(url: String): String {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", "en-US,en;q=0.9")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("SoundCloud is unavailable (HTTP ${response.code})")
            }
            return response.body?.string().orEmpty()
        }
    }

    fun startLogin(context: Context) {
        if (BuildConfig.SOUNDCLOUD_CLIENT_ID.isBlank() ||
            BuildConfig.SOUNDCLOUD_CLIENT_ID == DEFAULT_CLIENT_ID
        ) {
            notifyError("SoundCloud OAuth is not configured: set SOUNDCLOUD_CLIENT_ID and SOUNDCLOUD_CLIENT_SECRET for app and server")
            return
        }

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
        scope.launch {
            if (!accessToken.isNullOrEmpty()) {
                runCatching {
                    SoundCloudClient.authApi.disconnect(SoundCloudDisconnectRequest(accessToken))
                }
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
                val message = AuthErrorParser.message(e)
                Log.e(TAG, "Error occurred $message")
                _authEvents.emit(AuthEvent.Error(message))
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
