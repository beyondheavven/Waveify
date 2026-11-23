package com.beyondheavven.waveify.data.soundcloud.auth

import android.content.Context
import android.util.Log
import com.beyondheavven.waveify.data.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudProfileModel
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

    fun logout() {
        scope.launch {
            SoundCloudClient.clear()
            _authEvents.emit(AuthEvent.Disconnected)
        }
    }
}
