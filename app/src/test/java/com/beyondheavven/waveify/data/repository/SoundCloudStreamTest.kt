package com.beyondheavven.waveify.data.repository

import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudFormatModel
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudMediaModel
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudTrackModel
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudTranscodingModel
import com.beyondheavven.waveify.data.soundcloud.models.SoundCloudUserModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SoundCloudStreamTest {

    private val dummyUser = SoundCloudUserModel(
        id = 1,
        username = "TestUser",
        avatarUrl = "",
        permalinkUrl = ""
    )

    @Test
    fun `extractStreamUrl prioritizes progressive transcoding over others`() {
        val track = SoundCloudTrackModel(
            id = 123,
            title = "Test",
            durationMs = 60000,
            artworkUrl = null,
            user = dummyUser,
            media = SoundCloudMediaModel(
                transcodings = listOf(
                    SoundCloudTranscodingModel(
                        url = "https://stream.hls.url",
                        format = SoundCloudFormatModel(protocol = "hls", mimeType = "audio/mpeg")
                    ),
                    SoundCloudTranscodingModel(
                        url = "https://stream.progressive.url",
                        format = SoundCloudFormatModel(protocol = "progressive", mimeType = "audio/mpeg")
                    )
                )
            )
        )

        val streamUrl = HomeRepository.extractStreamUrl(track)
        assertEquals("https://stream.progressive.url", streamUrl)
    }

    @Test
    fun `extractStreamUrl falls back to first transcoding if progressive is absent`() {
        val track = SoundCloudTrackModel(
            id = 123,
            title = "Test",
            durationMs = 60000,
            artworkUrl = null,
            user = dummyUser,
            media = SoundCloudMediaModel(
                transcodings = listOf(
                    SoundCloudTranscodingModel(
                        url = "https://stream.fallback.url",
                        format = SoundCloudFormatModel(protocol = "hls", mimeType = "audio/ogg")
                    )
                )
            )
        )

        val streamUrl = HomeRepository.extractStreamUrl(track)
        assertEquals("https://stream.fallback.url", streamUrl)
    }

    @Test
    fun `extractStreamUrl returns null when media or transcodings are empty`() {
        val trackWithoutMedia = SoundCloudTrackModel(
            id = 123,
            title = "Test",
            durationMs = 60000,
            artworkUrl = null,
            user = dummyUser,
            media = null
        )
        assertNull(HomeRepository.extractStreamUrl(trackWithoutMedia))

        val trackEmptyTranscodings = SoundCloudTrackModel(
            id = 123,
            title = "Test",
            durationMs = 60000,
            artworkUrl = null,
            user = dummyUser,
            media = SoundCloudMediaModel(transcodings = emptyList())
        )
        assertNull(HomeRepository.extractStreamUrl(trackEmptyTranscodings))
    }
}
