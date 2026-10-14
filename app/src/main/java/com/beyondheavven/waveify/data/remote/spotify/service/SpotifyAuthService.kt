package com.beyondheavven.waveify.data.remote.spotify.service

import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface SpotifyAuthService {
    @POST("/api/token")
    @FormUrlEncoded
    suspend fun exchangeCode(

        @Header("Authorization")
        basicAuth: String,

        @Field("grant_type")
        grantType: String = "authorization_code",

        @Field("code")
        code: String,

        @Field("redirect_uri")
        redirectUri: String,

    ): SpotifyTokenResponse
}