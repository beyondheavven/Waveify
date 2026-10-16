package com.beyondheavven.waveify.ui.screen.library.repository

import com.beyondheavven.waveify.data.model.TrackUiModel
import com.beyondheavven.waveify.data.remote.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.remote.spotify.client.SpotifyClient
import okhttp3.HttpUrl.Companion.toHttpUrl


class LibraryRepository(

    private val spotifyClient: SpotifyClient = SpotifyClient,

    private val soundCloudClient: SoundCloudClient = SoundCloudClient,
) {

    private var nextSpotifyUrl: String? = null

    private var nextSoundCloudUrl: String? = null

    private var cachedSoundcloudUserId: Long? = null

    val isSpotifyLoggedIn: Boolean
        get() = !spotifyClient.accessToken.isNullOrEmpty()

    val isSoundCloudConnected: Boolean
        get() = !soundCloudClient.connectedUsername.isNullOrEmpty()

    val hasAnySource = isSpotifyLoggedIn || isSoundCloudConnected

    val hasMoreTracks: Boolean
        get() = nextSpotifyUrl != null || nextSoundCloudUrl != null


    suspend fun getInitialTracks(): Result<List<TrackUiModel>> = runCatching {
        nextSoundCloudUrl = null
        nextSpotifyUrl = null
        val allTracks = mutableListOf<TrackUiModel>()

        if(isSpotifyLoggedIn){
            val spotifyResponse = spotifyClient.api.getSavedTracks(limit = 50)
            nextSpotifyUrl = spotifyResponse.nextHref
            allTracks += spotifyResponse.items.map { it.track.toTrackUiModel() }
        }

        if(isSoundCloudConnected){
            val username = soundCloudClient.connectedUsername.orEmpty()
            val userId = cachedSoundcloudUserId ?: resolveSoundCloudUserId(username)
            cachedSoundcloudUserId = userId

            if (userId != null) {
                val scResponse = soundCloudClient.api.getUserLikes(userId = userId, limit = 50)
                nextSoundCloudUrl = scResponse.nextHref
                allTracks += scResponse.collection.mapNotNull { it.track?.toTrackUiModel() }
            }
        }
        allTracks.distinctBy { it.id }
    }

    suspend fun loadMoreTracks(): Result<List<TrackUiModel>> = runCatching {
        val newTracks = mutableListOf<TrackUiModel>()

        nextSoundCloudUrl?.let { url ->
            val scResponse = soundCloudClient.api.getNextLikesPage(url)
            nextSoundCloudUrl = scResponse.nextHref
            newTracks += scResponse.collection.mapNotNull { it.track?.toTrackUiModel() }
        }

        nextSpotifyUrl?.let { url ->
            val spotifyResponse = spotifyClient.api.getNextSavedTracks(url)
            nextSpotifyUrl = spotifyResponse.nextHref
            newTracks += spotifyResponse.items.map { it.track.toTrackUiModel() }
        }
        newTracks.distinctBy { it.id }
    }

    fun clearSoundCloudCache() {
        cachedSoundcloudUserId = null
        nextSoundCloudUrl = null
    }

    private suspend fun resolveSoundCloudUserId(username: String): Long? {
        val profileUrl = if (username.startsWith("http")) username else "https://soundcloud.com/$username"
        return runCatching {
            soundCloudClient.api.resolveUser(profileUrl).id
        }.getOrNull()
    }


}