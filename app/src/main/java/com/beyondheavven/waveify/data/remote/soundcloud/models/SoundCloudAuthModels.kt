package com.beyondheavven.waveify.data.remote.soundcloud.models

import com.google.gson.annotations.SerializedName

data class SoundCloudTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String? = null,

    @SerializedName("expires_in")
    val expiresIn: Long? = null,

    @SerializedName("scope")
    val scope: String? = null,

    @SerializedName("token_type")
    val tokenType: String? = null,

    @SerializedName("user")
    val user: SoundCloudProfileModel? = null
)

data class SoundCloudProfileModel(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("urn")
    val urn: String? = null,

    @SerializedName("username")
    val username: String,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("permalink_url")
    val permalinkUrl: String? = null
)
