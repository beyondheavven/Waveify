package com.beyondheavven.waveify.data.soundcloud.models

import com.google.gson.annotations.SerializedName

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
