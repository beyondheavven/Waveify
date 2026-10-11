package com.beyondheavven.waveify.data.remote.soundcloud.client

import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.data.remote.soundcloud.service.SoundCloudApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SoundCloudClient {

    private const val BASE_URL = BuildConfig.SOUNDCLOUD_BASE_URL
    var clientId: String = BuildConfig.SOUNDCLOUD_CLIENT_ID

    var connectedUsername: String? = null

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        .addInterceptor{ chain ->
            val original = chain.request()
            val originalUrl = original.url

            val urlWithClientId = originalUrl.newBuilder()
                .apply {
                    if (clientId.isNotBlank()){
                        addQueryParameter("client_id", clientId)
                    }
                }
                .build()

            val request = original.newBuilder().url(urlWithClientId).build()
            chain.proceed(request)
        }
        .build()

    val api: SoundCloudApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SoundCloudApiService::class.java)
}