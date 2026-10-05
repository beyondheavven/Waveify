package com.beyondheavven.waveify.data.remote.spotify

import com.google.gson.annotations.SerializedName


data class SpotifyTrack(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("artists")
    val artists: List<SpotifyArtistDto>,

    @SerializedName("album")
    val album: SpotifyAlbum
)

data class SpotifyArtistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)

data class SpotifyAlbum(
    @SerializedName("name")
    val name: String
)