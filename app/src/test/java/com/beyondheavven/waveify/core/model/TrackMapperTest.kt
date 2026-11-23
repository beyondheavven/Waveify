package com.beyondheavven.waveify.core.model

import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudFormatModel
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudMediaModel
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudTrackModel
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudTranscodingModel
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudUserModel
import com.beyondheavven.waveify.data.spotify.models.SpotifyAlbumDto
import com.beyondheavven.waveify.data.spotify.models.SpotifyArtistDto
import com.beyondheavven.waveify.data.spotify.models.SpotifyImageDto
import com.beyondheavven.waveify.data.spotify.models.SpotifyTrackDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrackMapperTest {

    @Test
    fun `formatDuration formats zero and sub-minute durations correctly`() {
        assertEquals("0:00", formatDuration(0L))
        assertEquals("0:05", formatDuration(5_000L))
        assertEquals("0:45", formatDuration(45_000L))
    }

    @Test
    fun `formatDuration formats minutes and seconds correctly`() {
        assertEquals("1:00", formatDuration(60_000L))
        assertEquals("1:05", formatDuration(65_000L))
        assertEquals("3:34", formatDuration(214_500L))
        assertEquals("10:15", formatDuration(615_000L))
    }

    @Test
    fun `SpotifyTrackDto toTrackUiModel maps fields correctly`() {
        val dto = SpotifyTrackDto(
            id = "sp_123",
            name = "Cyberpunk City",
            durationMs = 195_000L,
            artists = listOf(
                SpotifyArtistDto(id = "a1", name = "Synthwave Boy"),
                SpotifyArtistDto(id = "a2", name = "Neon Girl")
            ),
            album = SpotifyAlbumDto(
                name = "Night Drive",
                images = listOf(
                    SpotifyImageDto(url = "https://image.url/large.jpg", height = 640, width = 640),
                    SpotifyImageDto(url = "https://image.url/small.jpg", height = 300, width = 300)
                )
            )
        )

        val uiModel = dto.toTrackUiModel()

        assertEquals("sp_123", uiModel.id)
        assertEquals("Cyberpunk City", uiModel.title)
        assertEquals("Synthwave Boy, Neon Girl", uiModel.artist)
        assertEquals("https://image.url/large.jpg", uiModel.coverUrl)
        assertEquals("3:15", uiModel.durationFormatted)
        assertEquals(195_000L, uiModel.durationMs)
        assertEquals(TrackSource.SPOTIFY, uiModel.source)
    }

    @Test
    fun `SpotifyTrackDto with null album images maps coverUrl to null`() {
        val dto = SpotifyTrackDto(
            id = "sp_no_img",
            name = "No Image Track",
            durationMs = 60_000L,
            artists = listOf(SpotifyArtistDto(id = "a1", name = "Solo")),
            album = SpotifyAlbumDto(name = "Empty Album", images = emptyList())
        )

        val uiModel = dto.toTrackUiModel()
        assertNull(uiModel.coverUrl)
    }

    @Test
    fun `SoundCloudTrackModel toTrackUiModel maps fields and prefixes id`() {
        val scTrack = SoundCloudTrackModel(
            id = 987654L,
            title = "Chill Lofi Beat",
            durationMs = 125_000L,
            artworkUrl = "https://i1.sndcdn.com/artworks-12345-large.jpg",
            user = SoundCloudUserModel(
                id = 111L,
                username = "LofiProducer",
                avatarUrl = "https://avatar.url/avatar.jpg",
                permalinkUrl = "https://soundcloud.com/lofiproducer"
            ),
            media = SoundCloudMediaModel(
                transcodings = listOf(
                    SoundCloudTranscodingModel(
                        url = "https://api-v2.soundcloud.com/media/hls",
                        format = SoundCloudFormatModel(protocol = "hls", mimeType = "audio/mpeg")
                    ),
                    SoundCloudTranscodingModel(
                        url = "https://api-v2.soundcloud.com/media/progressive",
                        format = SoundCloudFormatModel(protocol = "progressive", mimeType = "audio/mpeg")
                    )
                )
            )
        )

        val uiModel = scTrack.toTrackUiModel()

        assertEquals("sc_987654", uiModel.id)
        assertEquals("Chill Lofi Beat", uiModel.title)
        assertEquals("LofiProducer", uiModel.artist)
        // Checks that -large is upgraded to -t500x500 for high resolution cover
        assertEquals("https://i1.sndcdn.com/artworks-12345-t500x500.jpg", uiModel.coverUrl)
        assertEquals("2:05", uiModel.durationFormatted)
        assertEquals(125_000L, uiModel.durationMs)
        assertEquals(TrackSource.SOUNDCLOUD, uiModel.source)
        // Prioritizes progressive stream over hls
        assertEquals("https://api-v2.soundcloud.com/media/progressive", uiModel.streamUrl)
    }

    @Test
    fun `HomeItemUiModel toTrackUiModel preserves soundcloud and spotify IDs`() {
        val scHomeItem = HomeItemUiModel(
            id = "555",
            title = "SC Item",
            subtitle = "Artist SC",
            coverUrl = "https://cover.url",
            source = TrackSource.SOUNDCLOUD,
            type = HomeItemType.TRACK,
            streamUrl = "https://stream.url"
        )
        val scResult = scHomeItem.toTrackUiModel()
        assertEquals("sc_555", scResult.id)
        assertEquals("SC Item", scResult.title)
        assertEquals(TrackSource.SOUNDCLOUD, scResult.source)
        assertEquals("https://stream.url", scResult.streamUrl)

        val spHomeItem = HomeItemUiModel(
            id = "777",
            title = "SP Item",
            subtitle = "Artist SP",
            coverUrl = "https://cover.url",
            source = TrackSource.SPOTIFY,
            type = HomeItemType.TRACK,
            spotifyUri = "spotify:track:777"
        )
        val spResult = spHomeItem.toTrackUiModel()
        assertEquals("spotify:track:777", spResult.id)
        assertEquals(TrackSource.SPOTIFY, spResult.source)
    }
}
