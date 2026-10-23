package com.beyondheavven.waveify.data.remote.spotify.service

import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyDevicesResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyPlayRequest
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyRecommendationsResponse
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifySavedTracksResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
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

    @GET("v1/recommendations")
    suspend fun getRecommendations(
        @Query("seed_genres")
        seedGenres: String = "pop,rock,electronic,hip-hop",

        @Query("limit")
        limit: Int = 20
    ): SpotifyRecommendationsResponse

    @PUT("v1/me/player/play")
    suspend fun playTrack(
        @Query("device_id")
        deviceId: String? = null,

        @Body
        request: SpotifyPlayRequest
    ): Response<Unit>

    @PUT("v1/me/player/pause")
    suspend fun pausePlayback(
        @Query("device_id")
        deviceId: String? = null,
    ): Response<Unit>

    @GET("v1/me/player/devices")
    suspend fun getAvailableDevices(): SpotifyDevicesResponse
}