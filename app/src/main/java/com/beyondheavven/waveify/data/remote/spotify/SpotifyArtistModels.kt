package com.beyondheavven.waveify.data.remote.spotify

import com.google.gson.annotations.SerializedName


data class SpotifyArtistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)