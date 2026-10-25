package com.beyondheavven.waveify.data.remote.spotify.service

import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySavedTracksResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySearchAlbumsResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySearchPlaylistsResponse
import retrofit2.http.GET
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
}