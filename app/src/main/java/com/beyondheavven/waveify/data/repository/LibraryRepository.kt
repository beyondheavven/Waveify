package com.beyondheavven.waveify.data.repository

import com.beyondheavven.waveify.core.model.TrackUiModel
import com.beyondheavven.waveify.core.model.toTrackUiModel
import com.beyondheavven.waveify.data.soundcloud.client.SoundCloudClient
import com.beyondheavven.waveify.data.spotify.client.SpotifyClient
import com.beyondheavven.waveify.data.repository.LibraryFilter

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
        get() = soundCloudClient.isAuthorized

    val hasAnySource: Boolean
        get() = isSpotifyLoggedIn || isSoundCloudConnected

    val hasMoreSpotifyTracks: Boolean
        get() = nextSpotifyUrl != null

    val hasMoreSoundCloudTracks: Boolean
        get() = nextSoundCloudUrl != null


    suspend fun getInitialSpotifyTracks(): Result<List<TrackUiModel>> = runCatching {
        nextSpotifyUrl = null

        if (!isSpotifyLoggedIn) return@runCatching emptyList()
        val spotifyResponse = spotifyClient.api.getSavedTracks(limit = 50)
        nextSpotifyUrl = spotifyResponse.nextHref
        spotifyResponse.items.map { it.track.toTrackUiModel() }
    }

    suspend fun loadMoreSpotifyTracks(): Result<List<TrackUiModel>> = runCatching {
        val url = nextSpotifyUrl ?: return@runCatching emptyList()
        val spotifyResponse = spotifyClient.api.getNextSavedTracks(url)
        nextSpotifyUrl = spotifyResponse.nextHref
        spotifyResponse.items.map { it.track.toTrackUiModel() }
    }

    suspend fun getInitialSoundCloudTracks(): Result<List<TrackUiModel>> = runCatching {
        nextSoundCloudUrl = null
        if (!isSoundCloudConnected) return@runCatching emptyList()

        val userId = soundCloudClient.currentUser?.id
            ?: return@runCatching emptyList()
        cachedSoundcloudUserId = userId

        val scResponse = soundCloudClient.api.getUserLikes(userId = userId, limit = 50)
        nextSoundCloudUrl = scResponse.nextHref
        scResponse.collection.mapNotNull { it.track?.toTrackUiModel() }
    }

    suspend fun loadMoreSoundCloudTracks(): Result<List<TrackUiModel>> = runCatching {
        val url = nextSoundCloudUrl ?: return@runCatching emptyList()
        val scResponse = soundCloudClient.api.getNextLikesPage(url)
        nextSoundCloudUrl = scResponse.nextHref
        scResponse.collection.mapNotNull { it.track?.toTrackUiModel() }
    }

    fun hasMoreTracks(filter: LibraryFilter): Boolean {
        return when(filter) {
            LibraryFilter.ALL -> nextSpotifyUrl != null || nextSoundCloudUrl != null
            LibraryFilter.SPOTIFY -> nextSpotifyUrl != null
            LibraryFilter.SOUNDCLOUD -> nextSoundCloudUrl != null
        }
    }

    fun clearSoundCloudCache() {
        cachedSoundcloudUserId = null
        nextSoundCloudUrl = null
    }
}