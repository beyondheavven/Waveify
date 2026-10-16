package com.beyondheavven.waveify.ui.screen.library.repository
import android.annotation.SuppressLint
import com.beyondheavven.waveify.data.model.TrackSource
import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudTrackModel
import com.beyondheavven.waveify.data.remote.spotify.models.SpotifyTrackDto


@SuppressLint("DefaultLocale")
fun formatDuration(durationMs: Long): String{
    val totalSec = durationMs / 1000
    return String.format("%d:%02d", totalSec / 60, totalSec % 60)
}

fun SpotifyTrackDto.toTrackUiModel(): TrackUiModel {
    return TrackUiModel(
        id = id,
        title = name,
        artist = artists.joinToString(", ") { it.name },
        coverUrl = album?.images?.firstOrNull()?.url,
        durationFormatted = formatDuration(durationMs),
        source = TrackSource.SPOTIFY
    )
}

fun SoundCloudTrackModel.toTrackUiModel(): TrackUiModel {
    val progressiveUrl = media?.transcodings?.firstOrNull(){
        it.format.protocol == "progressive"
    }?.url ?: media?.transcodings?.firstOrNull()?.url

    return TrackUiModel(
        id = "sc_${id}",
        title = title,
        artist = user.username,
        coverUrl = artworkUrl?.replace("-large", "-t500x500"),
        durationFormatted = formatDuration(durationMs),
        source = TrackSource.SOUNDCLOUD,
        streamUrl = progressiveUrl
    )
}