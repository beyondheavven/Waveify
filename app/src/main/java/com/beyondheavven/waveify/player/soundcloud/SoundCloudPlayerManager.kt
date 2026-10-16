package com.beyondheavven.waveify.player.soundcloud

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudTrackModel
import com.beyondheavven.waveify.player.soundcloud.SoundCloudPlayerManager.isPlayingSc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SoundCloudPlayerManager {

    private const val TAG = "SoundCloudPlayer"

    private var mediaPlayer: MediaPlayer? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    var isPlayingSc by mutableStateOf(false)
        private set

    var currentTrackTitle by mutableStateOf<String?>(null)
        private set

    var currentTrackId by mutableStateOf<Long?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun play(track: SoundCloudTrackModel) {
        val transcoding = track.media?.transcodings?.firstOrNull{
            it.format.protocol == "progressive"
        } ?: track.media?.transcodings?.firstOrNull()


        if(transcoding == null){
            Log.e(TAG, "No suitable audio stream found for tracks: ${track.title}")
            return
        }

        isLoading = true
        currentTrackTitle = track.title
        currentTrackId = track.id

        scope.launch {
            try{
                val streamResponse = SoundCloudClient.api.getStreamUrl(transcoding.url)
                val directMp3Url = streamResponse.httpUrl

                withContext(Dispatchers.Main){
                    playUrl(directMp3Url)
                }
            } catch (e: Exception){
                Log.e(TAG, "Failed to resolve stream url", e)
                isLoading = false
            }
        }
    }

    private fun playUrl(url: String){
        release()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepareAsync()

            setOnPreparedListener { mp ->
                isLoading = false
                mp.start()
                isPlayingSc = true
            }

            setOnCompletionListener {
                isPlayingSc = false
            }

            setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                isLoading = false
                isPlayingSc = false
                true
            }
        }

    }

    fun pause(){
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlayingSc = false
            }
        }
    }

    fun resume(){
        mediaPlayer?.let {
            if (!it.isPlaying){
                it.start()
                isPlayingSc = true
            }
        }
    }

    fun togglePlayPause(){
        if (isPlayingSc) pause() else resume()
    }

    fun release(){
        mediaPlayer?.release()
        mediaPlayer = null
        isPlayingSc = false
    }
}