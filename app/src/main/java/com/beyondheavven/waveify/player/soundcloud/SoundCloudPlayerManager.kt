package com.beyondheavven.waveify.player.soundcloud

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SoundCloudPlayerManager {

    private const val TAG = "SoundCloudPlayer"

    private var exoPlayer: ExoPlayer? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    var isPlayingSc by mutableStateOf(false)
        private set

    var currentTrackTitle by mutableStateOf<String?>(null)
        private set

    var currentTrackId by mutableStateOf<Long?>(null)
        private set

    var isLoadingSc by mutableStateOf(false)
        private set

    fun play(track: TrackUiModel, context: Context) {
        val transcodingUrl = track.streamUrl
        if(transcodingUrl == null){
            Log.e(TAG, "No suitable audio stream found for tracks: ${track.title}")
            return
        }

        isLoadingSc = true
        currentTrackTitle = track.title

        scope.launch {
            try{
                val streamResponse = SoundCloudClient.api.getStreamUrl(transcodingUrl)
                val directMp3Url = streamResponse.httpUrl

                withContext(Dispatchers.Main){
                    playUrl(directMp3Url, context)
                }
            } catch (e: Exception){
                Log.e(TAG, "Failed to resolve stream url", e)
                withContext(Dispatchers.Main){
                    isLoadingSc = false

                }
            }
        }
    }

    private fun playUrl(url: String, context: Context){
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context.applicationContext).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        isPlayingSc = isPlaying
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        isLoadingSc = playbackState == Player.STATE_BUFFERING
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(TAG, "ExoPlayer error: ${error.message}", error)
                        isLoadingSc = false
                        isPlayingSc = false
                    }

                })
            }
        }

        val mediaItem = MediaItem.fromUri(url)
        exoPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }

    }

    fun pause(){
        exoPlayer?.pause()
    }

    fun resume(){
        exoPlayer?.play()
    }

    fun togglePlayPause(){
        if (isPlayingSc) pause() else resume()
    }

    fun release(){
        exoPlayer?.release()
        exoPlayer = null
        isPlayingSc = false
    }
}