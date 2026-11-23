package com.beyondheavven.waveify.server

import com.beyondheavven.waveify.server.util.userIdFromUrn
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SoundCloudUrnTest {

    @Test
    fun `parses numeric id from soundcloud urn`() {
        assertEquals(12345L, userIdFromUrn("soundcloud:users:12345"))
    }

    @Test
    fun `returns null for invalid urn`() {
        assertNull(userIdFromUrn("soundcloud:users:abc"))
        assertNull(userIdFromUrn(null))
    }
}
