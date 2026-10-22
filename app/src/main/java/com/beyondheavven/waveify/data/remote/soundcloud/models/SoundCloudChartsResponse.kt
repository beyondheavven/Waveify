package com.beyondheavven.waveify.data.remote.soundcloud.models

data class SoundCloudChartsResponse(
    val collection: List<SoundCloudChartItem>
)

data class SoundCloudChartItem(

    val track: SoundCloudTrackModel,

    val score: Double? = null
)