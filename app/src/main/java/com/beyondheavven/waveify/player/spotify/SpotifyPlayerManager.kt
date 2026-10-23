package com.beyondheavven.waveify.player.spotify

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyPlayRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object SpotifyPlayerManager {

    private const val TAG = "SpotifyPlayerManager"

    private val scope = CoroutineScope(Dispatchers.IO)

    var isConnected by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var currentTrackTitle by mutableStateOf<String?>(null)
        private set

    var currentTrackArtist by mutableStateOf<String?>(null)
        private set

    var currentTrackUri by mutableStateOf<String?>(null)
        private set

    fun play(trackId: String, context: Context? = null){
        val uri = if (trackId.startsWith("spotify:track:")){
            trackId
        } else {
            "spotify:track:$trackId"
        }
        currentTrackUri = uri

        scope.launch {
            try {
                val response = SpotifyClient.api.playTrack(
                    request = SpotifyPlayRequest(uris = listOf(uri))
                )

                if(response.isSuccessful) {
                    isPlaying = true
                    isConnected = true
                    Log.d(TAG, "Started streaming track: $uri")
                } else if (response.code() == 404) {
                    Log.w(TAG, "No active devices.")
                    handleNoActiveDevice(uri, context)
                } else {
                    Log.e(TAG, "Error launching HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while playTrack", e)
            }
        }
    }

    private suspend fun handleNoActiveDevice(uri: String, context: Context?) {
        try {
            val devicesResponse = SpotifyClient.api.getAvailableDevices()
            val availableDevices = devicesResponse.devices

            val targetDevice = availableDevices.firstOrNull() { it.isActive } ?: availableDevices.firstOrNull()

            if (targetDevice != null) {
                Log.d(TAG, "Found device: ${targetDevice.name} (${targetDevice.id})")
                val retryResponse = SpotifyClient.api.playTrack(
                    deviceId = targetDevice.id,
                    request = SpotifyPlayRequest(uris = listOf(uri))
                )

                if(retryResponse.isSuccessful) {
                    isPlaying = true
                    isConnected = true
                    Log.d(TAG, "Playing on ${targetDevice.name}")
                }
            } else {
                Log.w(TAG, "No available Spotify devices")
                if (context != null) {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage("com.spotify.music")
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while getting devices", e)
        }
    }

    fun pause(){
        scope.launch {
            try {
                val response = SpotifyClient.api.pausePlayback()
                if (response.isSuccessful) {
                    isPlaying = false
                    Log.d(TAG, "Paused")
                } else {
                    Log.e(TAG, "Error occurred ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error when calling pause ${e.message}")
            }
        }
    }

    fun resume(){
        scope.launch {
            try {
                val response = SpotifyClient.api.playTrack(request = SpotifyPlayRequest())
                if (response.isSuccessful) {
                    isPlaying = true
                    Log.d(TAG, "Resume")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error when calling resume", e)
            }
        }
    }

    fun togglePlayPause(){
        if (isPlaying){
            pause()
        } else {
            resume()
        }

    }

    fun disconnect(){
        isPlaying = false
        isConnected = false
    }
}