package com.beyondheavven.waveify.data.remote.soundcloud.models

import com.google.gson.annotations.SerializedName


data class SoundCloudTrackModel(

    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("duration")
    val durationMs: Long,

    @SerializedName("artwork_url")
    val artworkUrl: String?,

    @SerializedName("user")
    val user: SoundCloudUserModel,

    @SerializedName("media")
    val media: SoundCloudMediaModel? = null
)

data class SoundCloudMediaModel(

    @SerializedName("transcodings")
    val transcodings: List<SoundCloudTranscodingModel> = emptyList()
)

data class SoundCloudTranscodingModel(

    @SerializedName("url")
    val url: String,

    @SerializedName("format")
    val format: SoundCloudFormatModel

)

data class SoundCloudFormatModel(

    @SerializedName("protocol")
    val protocol: String,

    @SerializedName("mime_type")
    val mimeType: String
)

data class SoundCloudStreamResponse(

    @SerializedName("url")
    val httpUrl: String
)