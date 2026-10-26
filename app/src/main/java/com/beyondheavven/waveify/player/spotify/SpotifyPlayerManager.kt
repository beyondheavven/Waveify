package com.beyondheavven.waveify.player.spotify

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.beyondheavven.waveify.BuildConfig
import com.beyondheavven.waveify.player.waveify.WaveifyPlayer
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

object SpotifyPlayerManager {

    private const val TAG = "SpotifyPlayerManager"

    private var spotifyAppRemote: SpotifyAppRemote? = null

    var isConnected by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set


    fun connect(context: Context, onConnected: (() -> Unit)? = null) {
        if (spotifyAppRemote?.isConnected == true) {
            onConnected?.invoke()
            return
        }

        val connectionParams = ConnectionParams.Builder(BuildConfig.SPOTIFY_CLIENT_ID)
            .setRedirectUri(BuildConfig.SPOTIFY_REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener{
            override fun onConnected(appRemote: SpotifyAppRemote?) {
                spotifyAppRemote = appRemote
                isConnected = true
                Log.d(TAG, "SpotifyAppRemote loaded successfully")

                appRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
                    isPlaying = !playerState.isPaused
                    playerState.track?.duration?.let { dur ->
                        if (dur > 0) WaveifyPlayer.updateDuration(dur)
                    }
                    if (playerState.playbackPosition >= 0){
                        WaveifyPlayer.updatePosition(playerState.playbackPosition)
                    }
                }

                onConnected?.invoke()
            }

            override fun onFailure(throwable: Throwable) {
                isConnected = true
                Log.e(TAG, "Error occurred in SpotifyAppRemote ${throwable.message}", throwable)
            }
        })
    }

    fun play(uriOrId: String, context: Context? = null){
        val uri = when {
            uriOrId.startsWith("spotify:") -> uriOrId
            else -> "spotify:track:$uriOrId"
        }

        if (spotifyAppRemote?.isConnected == true) {
            Log.d(TAG, "Playing thru App Remote SDK: $uri")
            spotifyAppRemote?.playerApi?.play(uri)
            isPlaying = true
        } else if (context != null) {
            connect(context) {
                spotifyAppRemote?.playerApi?.play(uri)
                isPlaying = true
            }
        }
    }

    fun pause(){
        spotifyAppRemote?.playerApi?.pause()
        isPlaying = false
    }

    fun resume(){
        spotifyAppRemote?.playerApi?.resume()
        isPlaying = true
    }

    fun seekTo(positionMs: Long) {
        spotifyAppRemote?.playerApi?.seekTo(positionMs)
    }

    fun togglePlayPause(){
        if (isPlaying){
            pause()
        } else {
            resume()
        }
    }

    fun disconnect(){
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
        isPlaying = false
        isConnected = false
    }
}