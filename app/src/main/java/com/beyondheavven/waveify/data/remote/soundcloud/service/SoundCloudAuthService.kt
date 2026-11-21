package com.beyondheavven.waveify.data.remote.soundcloud.service

import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudTokenResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class SoundCloudTokenRequest(
    @SerializedName("code") val code: String,
    @SerializedName("codeVerifier") val codeVerifier: String,
    @SerializedName("redirectUri") val redirectUri: String
)

data class SoundCloudRefreshRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class SoundCloudDisconnectRequest(
    @SerializedName("accessToken") val accessToken: String
)

interface SoundCloudAuthService {

    @POST("api/auth/soundcloud/token")
    suspend fun exchangeCode(@Body request: SoundCloudTokenRequest): SoundCloudTokenResponse

    @POST("api/auth/soundcloud/refresh")
    suspend fun refresh(@Body request: SoundCloudRefreshRequest): SoundCloudTokenResponse

    @POST("api/auth/soundcloud/disconnect")
    suspend fun disconnect(@Body request: SoundCloudDisconnectRequest): Response<Unit>
}
