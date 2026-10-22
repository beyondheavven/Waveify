package com.beyondheavven.waveify.data.remote.spotify.models

import com.google.gson.annotations.SerializedName


data class SpotifyRecommendationsResponse(
    val tracks: List<SpotifyTrackDto>
)

data class SpotifyNewReleasesResponse(
    val albums: SpotifyAlbumsContainer
)

data class SpotifyAlbumsContainer(
    val items: List<SpotifyAlbumDto>
)

data class SpotifyAlbumItemDto(
    val id: String,
    val name: String,
    val artists: List<SpotifyArtistDto>,
    val images: List<SpotifyImageDto>,

    @SerializedName("total_tracks")
    val totalTracks: Int
)