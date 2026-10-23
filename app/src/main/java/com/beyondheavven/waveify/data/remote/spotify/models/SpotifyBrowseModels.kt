package com.beyondheavven.waveify.data.remote.spotify.models

import com.google.gson.annotations.SerializedName

data class SpotifyNewReleasesResponse(
    @SerializedName("albums")
    val albums: SpotifyAlbumsContainer
)

data class SpotifyAlbumsContainer(
    @SerializedName("items")
    val items: List<SpotifyAlbumRecommendations>
)

data class SpotifyAlbumRecommendations(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("album_type")
    val albumType: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("artists")
    val artists: List<SpotifyArtistDto>,

    @SerializedName("images")
    val images: List<SpotifyImageDto>

)

data class SpotifyFeaturedPlaylistsResponse(

    @SerializedName("message")
    val message: String?,

    @SerializedName("playlists")
    val playlists: SpotifyPlaylistsContainer
)

data class SpotifyPlaylistsContainer(
    @SerializedName("items")
    val items: List<SpotifyPlayListItemDto>
)

data class SpotifyPlayListItemDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("images")
    val images: List<SpotifyImageDto>
)

