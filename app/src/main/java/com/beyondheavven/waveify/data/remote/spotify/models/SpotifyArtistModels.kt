package com.beyondheavven.waveify.data.remote.spotify.models

import com.google.gson.annotations.SerializedName


data class SpotifyArtistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)