package com.beyondheavven.waveify.feature.library

import com.beyondheavven.waveify.core.model.TrackSource
import com.beyondheavven.waveify.core.model.TrackUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LibraryTracksTest {

    private fun createTrack(id: String, source: TrackSource): TrackUiModel {
        return TrackUiModel(
            id = id,
            title = "Title $id",
            artist = "Artist $id",
            coverUrl = null,
            durationFormatted = "3:00",
            durationMs = 180_000L,
            source = source
        )
    }

    @Test
    fun `interleaveTracks merges lists in alternating order when lengths are equal`() {
        val spList = listOf(createTrack("sp_1", TrackSource.SPOTIFY), createTrack("sp_2", TrackSource.SPOTIFY))
        val scList = listOf(createTrack("sc_1", TrackSource.SOUNDCLOUD), createTrack("sc_2", TrackSource.SOUNDCLOUD))

        val result = LibraryViewModel.interleaveTracks(spList, scList)

        assertEquals(4, result.size)
        assertEquals("sp_1", result[0].id)
        assertEquals("sc_1", result[1].id)
        assertEquals("sp_2", result[2].id)
        assertEquals("sc_2", result[3].id)
    }

    @Test
    fun `interleaveTracks appends remaining tracks when first list is longer`() {
        val spList = listOf(
            createTrack("sp_1", TrackSource.SPOTIFY),
            createTrack("sp_2", TrackSource.SPOTIFY),
            createTrack("sp_3", TrackSource.SPOTIFY)
        )
        val scList = listOf(createTrack("sc_1", TrackSource.SOUNDCLOUD))

        val result = LibraryViewModel.interleaveTracks(spList, scList)

        assertEquals(4, result.size)
        assertEquals(listOf("sp_1", "sc_1", "sp_2", "sp_3"), result.map { it.id })
    }

    @Test
    fun `interleaveTracks appends remaining tracks when second list is longer`() {
        val spList = listOf(createTrack("sp_1", TrackSource.SPOTIFY))
        val scList = listOf(
            createTrack("sc_1", TrackSource.SOUNDCLOUD),
            createTrack("sc_2", TrackSource.SOUNDCLOUD),
            createTrack("sc_3", TrackSource.SOUNDCLOUD)
        )

        val result = LibraryViewModel.interleaveTracks(spList, scList)

        assertEquals(4, result.size)
        assertEquals(listOf("sp_1", "sc_1", "sc_2", "sc_3"), result.map { it.id })
    }

    @Test
    fun `interleaveTracks handles empty lists gracefully`() {
        val list = listOf(createTrack("sp_1", TrackSource.SPOTIFY))

        val emptyA = LibraryViewModel.interleaveTracks(emptyList(), list)
        assertEquals(1, emptyA.size)
        assertEquals("sp_1", emptyA[0].id)

        val emptyB = LibraryViewModel.interleaveTracks(list, emptyList())
        assertEquals(1, emptyB.size)
        assertEquals("sp_1", emptyB[0].id)

        val bothEmpty = LibraryViewModel.interleaveTracks(emptyList(), emptyList())
        assertTrue(bothEmpty.isEmpty())
    }
}
