package com.beyondheavven.waveify.data.remote.spotify.client

import android.util.Base64
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
    private const val CLIENT_SECRET = BuildConfig.SPOTIFY_SECRET
    private const val CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID

    var accessToken: String? = null
    var refreshToken: String? = null

    val isAuthorized: Boolean
        get() = !accessToken.isNullOrEmpty()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
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

    private val authOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val authApi: SpotifyAuthService = Retrofit.Builder()
        .baseUrl(ACCOUNTS_URL)
        .client(authOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyAuthService::class.java)

    suspend fun exchangeCodeForToken(code: String): Boolean {
        return try {
            val credentials = "${CLIENT_ID}:${CLIENT_SECRET}"
            val basicAuth = "Basic " + Base64.encodeToString(
                credentials.toByteArray(),
                Base64.NO_WRAP
            )
            val response = authApi.exchangeCode(
                basicAuth = basicAuth,
                code = code,
                redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI
            )
            accessToken = response.accessToken
            refreshToken = response.refreshToken
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}