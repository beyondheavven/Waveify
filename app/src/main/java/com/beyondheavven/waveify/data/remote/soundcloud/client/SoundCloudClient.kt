package com.beyondheavven.waveify.data.remote.soundcloud.client

import android.content.Context
import android.content.SharedPreferences
import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudProfileModel
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudTokenResponse
import com.beyondheavven.waveify.data.remote.soundcloud.service.SoundCloudApiService
import com.beyondheavven.waveify.data.remote.soundcloud.service.SoundCloudAuthService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SoundCloudClient {

    private const val BASE_URL = BuildConfig.SOUNDCLOUD_BASE_URL
    private const val AUTH_BASE_URL = BuildConfig.SOUNDCLOUD_AUTH_BASE_URL
    private const val PREFS_NAME = "soundcloud_profile"
    private const val KEY_CLIENT_ID = "client_id"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_AVATAR_URL = "avatar_url"
    private const val KEY_PERMALINK_URL = "permalink_url"
    private const val KEY_URN = "urn"

    private var prefs: SharedPreferences? = null

    var clientId: String = BuildConfig.SOUNDCLOUD_CLIENT_ID
        set(value) {
            field = value
            prefs?.edit()?.putString(KEY_CLIENT_ID, value)?.apply()
        }

    var nickname: String? = null
        private set

    var accessToken: String? = null
    var refreshToken: String? = null
    var currentUser: SoundCloudProfileModel? = null

    val isAuthorized: Boolean
        get() = hasNicknameSession || !accessToken.isNullOrEmpty()

    private val hasNicknameSession: Boolean
        get() = clientId.isNotBlank() && currentUser != null && !nickname.isNullOrBlank()

    val connectedUsername: String?
        get() = currentUser?.username

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        })
        .addInterceptor { chain ->
            val original = chain.request()
            val urlWithClientId = original.url.newBuilder()
                .apply {
                    if (clientId.isNotBlank()) {
                        addQueryParameter("client_id", clientId)
                    }
                }
                .build()

            val requestBuilder = original.newBuilder().url(urlWithClientId)
            accessToken?.let { token ->
                requestBuilder.addHeader("Authorization", "OAuth $token")
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    val api: SoundCloudApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SoundCloudApiService::class.java)

    private val authOkHttpClient = OkHttpClient.Builder().build()

    val authApi: SoundCloudAuthService = Retrofit.Builder()
        .baseUrl(AUTH_BASE_URL)
        .client(authOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SoundCloudAuthService::class.java)

    fun init(context: Context) {
        val sharedPreferences = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sharedPreferences

        clientId = sharedPreferences.getString(KEY_CLIENT_ID, BuildConfig.SOUNDCLOUD_CLIENT_ID).orEmpty()
        nickname = sharedPreferences.getString(KEY_NICKNAME, null)

        val userId = sharedPreferences.getLong(KEY_USER_ID, 0L)
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        if (userId != 0L && !username.isNullOrBlank()) {
            currentUser = SoundCloudProfileModel(
                id = userId,
                urn = sharedPreferences.getString(KEY_URN, null),
                username = username,
                avatarUrl = sharedPreferences.getString(KEY_AVATAR_URL, null),
                permalinkUrl = sharedPreferences.getString(KEY_PERMALINK_URL, null)
            )
        }
    }

    fun applyToken(response: SoundCloudTokenResponse) {
        accessToken = response.accessToken
        refreshToken = response.refreshToken
        currentUser = response.user
    }

    fun saveNicknameSession(nickname: String, user: SoundCloudProfileModel) {
        this.nickname = nickname
        currentUser = user
        prefs?.edit()?.let { editor ->
            editor.putString(KEY_NICKNAME, nickname)
            editor.putLong(KEY_USER_ID, user.id ?: 0L)
            editor.putString(KEY_USERNAME, user.username)
            editor.putString(KEY_AVATAR_URL, user.avatarUrl)
            editor.putString(KEY_PERMALINK_URL, user.permalinkUrl)
            editor.putString(KEY_URN, user.urn)
            editor.apply()
        }
    }

    fun clear() {
        accessToken = null
        refreshToken = null
        currentUser = null
        nickname = null
        prefs?.edit()?.let { editor ->
            editor.remove(KEY_NICKNAME)
            editor.remove(KEY_USER_ID)
            editor.remove(KEY_USERNAME)
            editor.remove(KEY_AVATAR_URL)
            editor.remove(KEY_PERMALINK_URL)
            editor.remove(KEY_URN)
            editor.apply()
        }
    }
}
