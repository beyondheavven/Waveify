package com.beyondheavven.waveify.data.remote.soundcloud.service

import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudLikesResponse
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudTracksResponse
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudUserModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface SoundCloudApiService {

    @GET("/resolve")
    suspend fun resolveUser(
        @Query("url")
        profileUrl: String
    ): SoundCloudUserModel

    @GET("users/{userId}/likes")
    suspend fun getUserLikes(
        @Path("userId")
        userId: Long,

        @Query("limit")
        limit: Int = 50
    ): SoundCloudLikesResponse


    @GET
    suspend fun getNextLikesPage(
        @Url
        nextUrl: String
    ): SoundCloudLikesResponse
}