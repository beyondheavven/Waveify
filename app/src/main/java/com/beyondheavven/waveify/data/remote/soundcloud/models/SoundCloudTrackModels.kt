package com.beyondheavven.waveify.data.remote.soundcloud.models

import com.google.gson.annotations.SerializedName


data class SoundCloudTrackModel(

    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("duration")
    val durationMs: String,

    @SerializedName("artwork_url")
    val artworkUrl: String?,

    @SerializedName("user")
    val user: SoundCloudUserModel
)


data class SoundCloudTracksResponse(
    @SerializedName("collection")
    val collection: List<SoundCloudTrackModel>,

    @SerializedName("next_href")
    val nextHref: String?
)