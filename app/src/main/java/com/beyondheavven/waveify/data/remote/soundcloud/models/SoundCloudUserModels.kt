package com.beyondheavven.waveify.data.remote.soundcloud.models

import com.google.gson.annotations.SerializedName


data class SoundCloudUserModel(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("avatar_url")
    val avatarUrl: String,

    @SerializedName("permalink_url")
    val permalinkUrl: String
)