package com.beyondheavven.waveify.ui.screen.home

import android.util.Log
import com.beyondheavven.waveify.data.model.HomeItemType
import com.beyondheavven.waveify.data.model.HomeItemUiModel
import com.beyondheavven.waveify.data.model.TrackSource
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudGenre
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HomeRepository{

    private const val TAG = "HomeRepository"

    suspend fun getSpotifyNewRelease(): List<HomeItemUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SpotifyClient.api.searchNewReleases()
            response.albums.items.map { album ->
                HomeItemUiModel(
                    id = album.id,
                    title = album.name,
                    subtitle = album.artists.joinToString(", ") { it.name }.ifEmpty { "Spotify" },
                    coverUrl = album.images.firstOrNull()?.url,
                    source = TrackSource.SPOTIFY,
                    type = HomeItemType.ALBUM,
                    spotifyUri = "spotify:album:${album.id}"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Spotify new releases error ${e.message}")
            emptyList()
        }
    }

    suspend fun getSpotifyFeaturedPlaylists(): List<HomeItemUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SpotifyClient.api.searchPlaylists()
            response.playlists?.items?.filterNotNull()?.map { playlist ->
                HomeItemUiModel(
                    id = playlist.id,
                    title = playlist.name,
                    subtitle = playlist.description?.takeIf { it.isNotBlank() } ?: "Featured Playlist",
                    coverUrl = playlist.images?.firstOrNull()?.url,
                    source = TrackSource.SPOTIFY,
                    type = HomeItemType.ALBUM,
                    spotifyUri = "spotify:playlist:${playlist.id}"
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Spotify featured playlists error ${e.message}")
            emptyList()
        }
    }

    suspend fun getSoundCloudTrending(limit: Int = 20): List<HomeItemUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SoundCloudClient.api.getTrendingCharts(
                kind = "trending",
                genre = SoundCloudGenre.ALL_MUSIC.queryValue,
                limit = limit
            )

            response.collection.mapNotNull { item ->
                val track = item.track ?: return@mapNotNull null
                HomeItemUiModel(
                    id = track.id.toString(),
                    title = track.title,
                    subtitle = track.user.username,
                    coverUrl = track.artworkUrl ?: track.user.avatarUrl,
                    source = TrackSource.SOUNDCLOUD,
                    type = HomeItemType.TRACK
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while loading SoundCloud trends: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getSoundCloudGenreChart(
        genre: SoundCloudGenre = SoundCloudGenre.HIP_HOP,
        limit: Int = 20
    ): List<HomeItemUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SoundCloudClient.api.getGenreCharts(
                kind = "top",
                genre = genre.queryValue,
                limit = limit
            )

            response.collection.mapNotNull { item ->
                val track = item.track ?: return@mapNotNull null
                HomeItemUiModel(
                    id = track.id.toString(),
                    title = track.title,
                    subtitle = track.user.username,
                    coverUrl = track.artworkUrl ?: track.user.avatarUrl,
                    source = TrackSource.SOUNDCLOUD,
                    type = HomeItemType.TRACK
                )
            }
        } catch (e: Exception){
            Log.e(TAG, "Error while loading SoundCloud Genre ${genre.displayName}", e)
            emptyList()
        }
    }
}