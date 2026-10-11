package com.beyondheavven.waveify.data.remote.soundcloud.service

import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudTracksResponse
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudUserModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SoundCloudApiService {

    @GET("/resolve")
    suspend fun resolveUser(
        @Query("url")
        profileUrl: String
    ): SoundCloudUserModel


    @GET("users/{userId}/tracks")
    suspend fun getUserTracks(
        @Path("userId")
        userId: Long,

        @Path("limit")
        limit: Int = 50
    ): SoundCloudTracksResponse
}