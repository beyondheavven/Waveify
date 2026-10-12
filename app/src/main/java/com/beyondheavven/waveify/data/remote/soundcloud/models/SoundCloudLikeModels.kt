package com.beyondheavven.waveify.data.remote.soundcloud.models

import com.google.gson.annotations.SerializedName


data class SoundCloudLikeModel(

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("track")
    val track: SoundCloudTrackModel?
)

data class SoundCloudLikesResponse(

    @SerializedName("collection")
    val collection: List<SoundCloudLikeModel>,

    @SerializedName("next_href")
    val nextHref: String?
)