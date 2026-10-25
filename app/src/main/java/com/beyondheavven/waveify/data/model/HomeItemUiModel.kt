package com.beyondheavven.waveify.data.model

enum class HomeItemType {
    TRACK,
    ALBUM,
    PLAYLIST
}

data class HomeItemUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val coverUrl: String?,
    val source: TrackSource,
    val type: HomeItemType,
    val spotifyUri: String? = null,
    val streamUrl: String? = null
)