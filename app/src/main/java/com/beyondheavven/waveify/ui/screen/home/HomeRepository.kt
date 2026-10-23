package com.beyondheavven.waveify.ui.screen.home

import android.util.Log
import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.data.model.toTrackUiModel
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HomeRepository{

    suspend fun getSpotifyRecommendations(): List<TrackUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SpotifyClient.api.getRecommendations()
            response.tracks.map { it.toTrackUiModel() }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Spotify error : ${e.message}", e)
            emptyList()
        }

    }

    suspend fun getSoundCloudRecommendations(): List<TrackUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SoundCloudClient.api.getTrendingCharts()
            response.collection.map { it.track.toTrackUiModel() }
        } catch (e: Exception) {
            Log.e("HomeRepository", "SoundCloud charts error : ${e.message}", e)
            emptyList()
        }
    }

}