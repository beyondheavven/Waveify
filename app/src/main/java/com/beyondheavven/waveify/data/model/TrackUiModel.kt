package com.beyondheavven.waveify.data.model


data class TrackUiModel(
    val id: String,
    val title: String,
    var artist: String,
    val coverUrl: String?,
    val durationFormatted: String,
    val durationMs: Long = 0L,
    val source: TrackSource,
    val streamUrl: String? = null
)