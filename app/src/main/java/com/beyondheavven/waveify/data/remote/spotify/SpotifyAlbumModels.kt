package com.beyondheavven.waveify.data.remote.spotify

import com.google.gson.annotations.SerializedName

data class SpotifyAlbumDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("images")
    val images: List<SpotifyImageDto>
)

data class SpotifyImageDto(
    @SerializedName("url")
    val url: String,

    @SerializedName("height")
    val height: Int?,

    @SerializedName("width")
    val width: Int?
)