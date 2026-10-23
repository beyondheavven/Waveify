package com.beyondheavven.waveify.data.remote.spotify.models

import com.google.gson.annotations.SerializedName

data class SpotifyPlayRequest(

    @SerializedName("uris")
    val uris: List<String>? = null,

    @SerializedName("context_uri")
    val contextUri: String? = null,

    @SerializedName("position_ms")
    val positionMs: Long? = null
)

data class SpotifyDevicesResponse(

    @SerializedName("devices")
    val devices: List<SpotifyDevices>
)

data class SpotifyDevices(

    val id: String,

    @SerializedName("is_active")
    val isActive: Boolean,

    val name: String,

    val type: String
)