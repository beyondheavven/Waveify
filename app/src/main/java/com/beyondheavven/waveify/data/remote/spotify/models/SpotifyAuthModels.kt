package com.beyondheavven.waveify.data.remote.spotify.models

import com.google.gson.annotations.SerializedName


data class SpotifyTokenResponse(

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("expires_in")
    val expiresIn: Int,

    @SerializedName("refresh_token")
    val refreshToken: String? = null
)