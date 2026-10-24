package com.beyondheavven.waveify.data.remote.soundcloud.models

import com.google.gson.annotations.SerializedName

data class SoundCloudSearchTracksResponse(

    @SerializedName("collection")
    val collection: List<SoundCloudTrackModel> = emptyList(),

    @SerializedName("total_results")
    val totalResults: Int? = null,

    @SerializedName("next_href")
    val nextHref: String? = null
)