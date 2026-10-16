package com.beyondheavven.waveify.player.spotify

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.beyondheavven.waveify.BuildConfig
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState


object SpotifyPlayerManager {

    private const val TAG = "SpotifyPlayerManager"

    private var spotifyAppRemote: SpotifyAppRemote? = null

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

    fun connect(context: Context, onConnected: (()-> Unit)? = null) {
        if (isConnected && spotifyAppRemote != null){
            onConnected?.invoke()
            return
        }

        val connectionParams = ConnectionParams.Builder(
            BuildConfig.SPOTIFY_CLIENT_ID)
            .setRedirectUri(BuildConfig.SPOTIFY_REDIRECT_URI)
            .showAuthView(false)
            .build()

        SpotifyAppRemote.connect(
            context.applicationContext,
            connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    isConnected = true
                    Log.d(TAG, "Connected to Spotify App Remote successfully!")
                    subscribeToPlayerState()
                    onConnected?.invoke()
                }

                override fun onFailure(throwable: Throwable) {
                    isConnected = false
                    spotifyAppRemote = null
                    Log.e(TAG, "Failed to connect to Spotify App Remote: ${throwable.message}", throwable)
                }
            }

        )

    }

    fun play(trackId: String, context: Context){
        val uri = if (trackId.startsWith("spotify:track:")){
            trackId
        } else {
            "spotify:track:$trackId"
        }

        if (isConnected && spotifyAppRemote != null) {
            spotifyAppRemote?.playerApi?.play(uri)
        } else {
            connect(context) {
                spotifyAppRemote?.playerApi?.play(uri)
            }
        }

    }

    fun pause(){
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resume(){
        spotifyAppRemote?.playerApi?.resume()
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
            spotifyAppRemote = null
            isConnected = false
            isPlaying = false
        }
    }

    private fun subscribeToPlayerState() {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState: PlayerState ->
            isPlaying = !playerState.isPaused
            val track = playerState.track
            if (track != null) {
                currentTrackTitle = track.name
                currentTrackArtist = track.artist.name
                currentTrackUri = track.uri
            }
        }

    }
}