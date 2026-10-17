package com.beyondheavven.waveify.player.waveify

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.beyondheavven.waveify.data.model.TrackSource
import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.player.soundcloud.SoundCloudPlayerManager
import com.beyondheavven.waveify.player.spotify.SpotifyPlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

object WaveifyPlayer {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var progressJob: Job? = null

    var currentTrack by mutableStateOf<TrackUiModel?>(null)
        private set

    private var currentPlaylist: List<TrackUiModel> = emptyList()

    var isPlaying by mutableStateOf(false)
        private set

    var currentPositionMs by mutableLongStateOf(0L)
        private set

    var durationMs by mutableLongStateOf(1L)
        private set

    fun play(track: TrackUiModel, playlist: List<TrackUiModel>, context: Context) {
        currentTrack = track
        currentPlaylist = playlist
        currentPositionMs = 0L

        when (track.source) {
            TrackSource.SPOTIFY -> {
                SoundCloudPlayerManager.pause()
                SpotifyPlayerManager.play(track.id, context)
            }

            TrackSource.SOUNDCLOUD -> {
                SpotifyPlayerManager.pause()
                SoundCloudPlayerManager.play(track, context)
            }
        }

        isPlaying = true
        startProgressTicker()
    }

    fun pause() {
        when (currentTrack?.source) {
            TrackSource.SPOTIFY -> SpotifyPlayerManager.pause()
            TrackSource.SOUNDCLOUD -> SoundCloudPlayerManager.pause()
            null -> Unit
        }
        isPlaying = false
    }

    fun resume() {
        when (currentTrack?.source) {
            TrackSource.SPOTIFY -> SpotifyPlayerManager.resume()
            TrackSource.SOUNDCLOUD -> SoundCloudPlayerManager.resume()
            null -> Unit
        }
        isPlaying = true
    }

    fun next(context: Context) {
        if (currentPlaylist.isEmpty()) return
        val currentIndex = currentPlaylist.indexOfFirst { it.id == currentTrack?.id }
        if (currentIndex != -1 && currentIndex < currentPlaylist.size - 1) {
            play(currentPlaylist[currentIndex + 1], currentPlaylist, context)
        }
    }

    fun seekTo(positionMs: Long) {
        currentPositionMs = positionMs
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                if (isPlaying) {
                    currentPositionMs += 1000L
                }
                delay(1000L.milliseconds)
            }
        }
    }

}