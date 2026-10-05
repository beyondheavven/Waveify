package com.beyondheavven.waveify.spotify

import okhttp3.Interceptor
import okhttp3.Response

class SpotifyAuthInterceptor(
    private val tokenProvider: () -> String?
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenProvider()

        val newRequest = if (!token.isNullOrBlank()){
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }

}