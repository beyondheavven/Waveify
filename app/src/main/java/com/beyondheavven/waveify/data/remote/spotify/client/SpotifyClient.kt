package com.beyondheavven.waveify.data.remote.spotify.client

import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.data.remote.spotify.service.SpotifyApiService
import com.beyondheavven.waveify.data.remote.spotify.service.SpotifyAuthService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpotifyClient {

    private const val BASE_URL = BuildConfig.SPOTIFY_BASE_URL
    private const val ACCOUNTS_URL = BuildConfig.ACCOUNTS_BASE_URL
    private const val CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID

    var accessToken: String? = null
    var refreshToken: String? = null

    val isAuthorized: Boolean
        get() = !accessToken.isNullOrEmpty()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        })
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            accessToken?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    val api: SpotifyApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyApiService::class.java)

    private val authOkHttpClient = OkHttpClient.Builder().build()

    val authApi: SpotifyAuthService = Retrofit.Builder()
        .baseUrl(ACCOUNTS_URL)
        .client(authOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyAuthService::class.java)

    suspend fun exchangeCodeForToken(code: String, codeVerifier: String) {
        val response = authApi.exchangeCode(
            clientId = CLIENT_ID,
            code = code,
            redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI,
            codeVerifier = codeVerifier
        )
        accessToken = response.accessToken
        refreshToken = response.refreshToken
    }
}
