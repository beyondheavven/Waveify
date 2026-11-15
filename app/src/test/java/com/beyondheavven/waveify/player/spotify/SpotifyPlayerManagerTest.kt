package com.beyondheavven.waveify.player.spotify

import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifyPlayerManagerTest {

    @Test
    fun `normalizeSpotifyUri prepends spotify track prefix when bare ID is provided`() {
        val bareId = "4cOdK2wGLETKBW3PvgPWqT"
        val normalized = SpotifyPlayerManager.normalizeSpotifyUri(bareId)
        assertEquals("spotify:track:4cOdK2wGLETKBW3PvgPWqT", normalized)
    }

    @Test
    fun `normalizeSpotifyUri preserves existing spotify URIs unchanged`() {
        val trackUri = "spotify:track:4cOdK2wGLETKBW3PvgPWqT"
        val albumUri = "spotify:album:1DFixLWuPkv3KT3TnV35m3"
        val playlistUri = "spotify:playlist:37i9dQZF1DXcBWIGoYBM5M"

        assertEquals(trackUri, SpotifyPlayerManager.normalizeSpotifyUri(trackUri))
        assertEquals(albumUri, SpotifyPlayerManager.normalizeSpotifyUri(albumUri))
        assertEquals(playlistUri, SpotifyPlayerManager.normalizeSpotifyUri(playlistUri))
    }
}
