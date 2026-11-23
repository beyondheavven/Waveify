package com.beyondheavven.waveify.data.spotify.service

import com.beyondheavven.waveify.data.spotify.models.SpotifyTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SpotifyAuthService {
    @POST("/api/token")
    @FormUrlEncoded
    suspend fun exchangeCode(
        @Field("grant_type")
        grantType: String = "authorization_code",

        @Field("client_id")
        clientId: String,

        @Field("code")
        code: String,

        @Field("redirect_uri")
        redirectUri: String,

        @Field("code_verifier")
        codeVerifier: String
    ): SpotifyTokenResponse
}
