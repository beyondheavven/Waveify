package com.beyondheavven.waveify.data.remote.spotify.service

import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyAlbumsContainer
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyArtistTopTracksResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySavedTracksResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySearchAlbumsResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySearchPlaylistsResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyTopArtistsResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyTopTracksResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyUserPlaylistsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface SpotifyApiService {
    @GET("v1/me/tracks")
    suspend fun getSavedTracks(
        @Query("limit")
        limit: Int = 50,

        @Query("offset")
        offset: Int = 0
    ): SpotifySavedTracksResponse

    @GET
    suspend fun getNextSavedTracks(
        @Url
        url: String
    ): SpotifySavedTracksResponse

    @GET("v1/search")
    suspend fun searchNewReleases(
        @Query("q")
        query: String = "tag:new",
        @Query("type")
        type: String = "album",
        @Query("limit")
        limit: Int = 10
    ): SpotifySearchAlbumsResponse

    @GET("v1/search")
    suspend fun searchPlaylists(
        @Query("q")
        query: String = "Top Hits",
        @Query("type")
        type: String = "playlist",
        @Query("limit")
        limit: Int = 10
    ): SpotifySearchPlaylistsResponse

    @GET("v1/me/top/tracks")
    suspend fun getTopTracks(
        @Query("time_range") timeRange: String = "short_term",
        @Query("limit") limit: Int = 20
    ): SpotifyTopTracksResponse

    @GET("v1/me/playlists")
    suspend fun getUserPlaylists(
        @Query("limit") limit: Int = 20
    ): SpotifyUserPlaylistsResponse

    @GET("v1/me/top/artists")
    suspend fun getTopArtists(
        @Query("time_range") timeRange: String = "medium_term",
        @Query("limit") limit: Int = 5
    ): SpotifyTopArtistsResponse

    @GET("v1/artists/{id}/albums")
    suspend fun getArtistAlbums(
        @Path("id") artistId: String,
        @Query("include_groups") includeGroups: String = "album,single",
        @Query("limit") limit: Int = 3
    ): SpotifyAlbumsContainer
}