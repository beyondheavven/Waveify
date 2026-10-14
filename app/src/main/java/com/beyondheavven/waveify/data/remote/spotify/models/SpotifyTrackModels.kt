package com.beyondheavven.waveify.data.remote.spotify.models

import com.google.gson.annotations.SerializedName


data class SpotifySearchTracksResponse(
    @SerializedName("tracks") val tracks: SpotifyTracksContainer
)

data class SpotifySavedTracksResponse(
    @SerializedName("items")
    val items: List<SpotifySavedTrackDto>,

    @SerializedName("next")
    val nextHref: String? = null
)

data class SpotifySavedTrackDto(
    @SerializedName("track")
    val track: SpotifyTrackDto
)

data class SpotifyTracksContainer(
    @SerializedName("items")
    val items: List<SpotifyTrackDto>
)

data class SpotifyTrackDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("duration_ms")
    val durationMs: Long,

    @SerializedName("artists")
    val  artists: List<SpotifyArtistDto>,

    @SerializedName("album")
    val album: SpotifyAlbumDto?
)