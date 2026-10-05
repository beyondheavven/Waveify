package com.beyondheavven.waveify.ui.theme

data class TrackUiModel(
    val id: String,
    val title: String,
    var artist: String,
    val coverUrl: String?,
    val durationFormatted: String,
    val source: TrackSource
)