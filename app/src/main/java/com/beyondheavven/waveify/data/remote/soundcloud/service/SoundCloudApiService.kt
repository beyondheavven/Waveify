package com.beyondheavven.waveify.data.remote.soundcloud.service

import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudChartsResponse
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudLikesResponse
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudStreamResponse
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

    @GET
    suspend fun getStreamUrl(

        @Url
        url: String
    ): SoundCloudStreamResponse

    @GET("charts")
    suspend fun getTrendingCharts(

        @Query("kind")
        kind: String = "trending",

        @Query("genre")
        genre: String = "soundcloud:genres:all-music",

        @Query("limit")
        limit: Int = 20
    ): SoundCloudChartsResponse

    @GET("charts")
    suspend fun getGenreCharts(

        @Query("kind")
        kind: String = "top",

        @Query("genre")
        genre: String,

        @Query("limit")
        limit: Int = 20
    ): SoundCloudChartsResponse
}