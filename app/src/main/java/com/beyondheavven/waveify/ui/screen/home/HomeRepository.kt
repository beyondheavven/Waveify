package com.beyondheavven.waveify.ui.screen.home

import android.util.Log
import com.beyondheavven.waveify.data.model.HomeItemType
import com.beyondheavven.waveify.data.model.HomeItemUiModel
import com.beyondheavven.waveify.data.model.TrackSource
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.soundcloud.models.SoundCloudTrackModel
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HomeRepository{

    private const val TAG = "HomeRepository"

    private fun extractStreamUrl(track: SoundCloudTrackModel): String? {
        return track.media?.transcodings?.firstOrNull{
            it.format.protocol == "progressive"
        }?.url ?: track.media?.transcodings?.firstOrNull()?.url
    }

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
            val response = SoundCloudClient.api.getTrendingCharts(limit = limit)
            response.collection.mapNotNull { item ->
                val track = item.track ?: return@mapNotNull null
                HomeItemUiModel(
                    id = track.id.toString(),
                    title = track.title,
                    subtitle = track.user.username,
                    coverUrl = track.artworkUrl ?: track.user.avatarUrl,
                    source = TrackSource.SOUNDCLOUD,
                    type = HomeItemType.TRACK,
                    streamUrl = extractStreamUrl(track)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while loading SoundCloud trends: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getSoundCloudHipHop(limit: Int = 20): List<HomeItemUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SoundCloudClient.api.searchTracks(
                query = "Hip-Hop",
                limit = limit
            )
            response.collection.map { track ->
                HomeItemUiModel(
                    id = track.id.toString(),
                    title = track.title,
                    subtitle = track.user.username,
                    coverUrl = track.artworkUrl ?: track.user.avatarUrl,
                    source = TrackSource.SOUNDCLOUD,
                    type = HomeItemType.TRACK,
                    streamUrl = extractStreamUrl(track)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while loading SoundCloud Hip-Hop: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getSpotifyTopTracks(): List<HomeItemUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SpotifyClient.api.getTopTracks(timeRange = "short_term", limit = 20)
            response.items.map { track ->
                HomeItemUiModel(
                    id = track.id,
                    title = track.name,
                    subtitle = track.artists.joinToString(", ") { it.name },
                    coverUrl = track.album?.images?.firstOrNull()?.url,
                    source = TrackSource.SPOTIFY,
                    type = HomeItemType.TRACK,
                    spotifyUri = "spotify:track:${track.id}"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Spotify top tracks error ${e.message}")
            emptyList()
        }
    }

    suspend fun getSpotifyUserPlaylists(): List<HomeItemUiModel> = withContext(Dispatchers.IO) {
        try {
            val response = SpotifyClient.api.getUserPlaylists(limit = 20)
            response.items.map { playlist ->
                HomeItemUiModel(
                    id = playlist.id,
                    title = playlist.name,
                    subtitle = playlist.description?.takeIf { it.isNotBlank() } ?: "Spotify Playlist",
                    coverUrl = playlist.images?.firstOrNull()?.url,
                    source = TrackSource.SPOTIFY,
                    type = HomeItemType.PLAYLIST,
                    spotifyUri = "spotify:playlist:${playlist.id}"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Spotify user playlists error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getSpotifyFavoriteArtistsAlbums(): List<HomeItemUiModel> = withContext(Dispatchers.IO) {
        try {
            val topArtists = SpotifyClient.api.getTopArtists(timeRange = "medium_term", limit = 6)
            val albumList = mutableListOf<HomeItemUiModel>()
            topArtists.items.forEach { artist ->
                try {
                    val albumsResponse = SpotifyClient.api.getArtistAlbums(artist.id)
                    albumsResponse.items.forEach { album ->
                        albumList.add(
                            HomeItemUiModel(
                                id = album.id,
                                title = album.name,
                                subtitle = album.artists.joinToString(", ") { it.name }.ifEmpty { artist.name },
                                coverUrl = album.images.firstOrNull()?.url,
                                source = TrackSource.SPOTIFY,
                                type = HomeItemType.ALBUM,
                                spotifyUri = "spotify:album:${album.id}"
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching albums for ${artist.name}: ${e.message}")
                }
            }
            albumList.distinctBy { it.id }.shuffled()
        } catch (e: Exception) {
            Log.e(TAG, "Spotify favorite artists albums error: ${e.message}")
            emptyList()
        }
    }
}