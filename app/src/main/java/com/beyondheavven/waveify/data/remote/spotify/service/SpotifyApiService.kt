package com.beyondheavven.waveify.data.remote.spotify.service

import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySavedTracksResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySearchTracksResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SpotifyApiService {

    @GET("v1/search")
    suspend fun searchTracks(
        @Query("q")
        query: String,

        @Query("type")
        type: String = "track",

        @Query("limit")
        limit: Int = 20
    ): SpotifySearchTracksResponse

    @GET("v1/me/tracks")
    suspend fun getSavedTracks(
        @Query("limit")
        limit: Int = 20,

        @Query("offset")
        offset: Int = 0
    ): SpotifySavedTracksResponse

}