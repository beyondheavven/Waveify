package com.beyondheavven.waveify.data.remote.soundcloud

import com.google.gson.annotations.SerializedName


data class SoundCloudUserModel(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("avatarUrl")
    val avatarUrl: String,

    @SerializedName("permalink_url")
    val permalinkUrl: String
)