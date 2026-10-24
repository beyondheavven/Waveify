package com.beyondheavven.waveify.data.remote.soundcloud.models

import com.google.gson.annotations.SerializedName


data class SoundCloudChartsResponse(

    @SerializedName("collection")
    val collection: List<SoundCloudChartItem>,

    @SerializedName("total_results")
    val totalResults: Int? = null,

    @SerializedName("next_href")
    val nextRef: String? = null
)

data class SoundCloudChartItem(

    @SerializedName("track")
    val track: SoundCloudTrackModel?,

    @SerializedName("score")
    var score: Double? = null
)