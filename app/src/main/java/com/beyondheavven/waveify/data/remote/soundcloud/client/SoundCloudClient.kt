package com.beyondheavven.waveify.data.remote.soundcloud.client

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

    var clientId: String = BuildConfig.SOUNDCLOUD_CLIENT_ID

    var accessToken: String? = null
    var refreshToken: String? = null
    var currentUser: SoundCloudProfileModel? = null

    val isAuthorized: Boolean
        get() = !accessToken.isNullOrEmpty()

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

    fun applyToken(response: SoundCloudTokenResponse) {
        accessToken = response.accessToken
        refreshToken = response.refreshToken
        currentUser = response.user
    }

    fun clear() {
        accessToken = null
        refreshToken = null
        currentUser = null
    }
}
