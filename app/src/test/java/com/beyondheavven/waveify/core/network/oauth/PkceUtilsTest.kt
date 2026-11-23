package com.beyondheavven.waveify.core.network.oauth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest
import java.util.Base64

class PkceUtilsTest {

    private val urlSafePattern = Regex("[A-Za-z0-9_-]{43}")

    @Test
    fun `verifier is url safe and unique`() {
        val verifier = PkceUtils.generateVerifier()

        assertTrue(verifier.matches(urlSafePattern))
        assertNotEquals(verifier, PkceUtils.generateVerifier())
    }

    @Test
    fun `state is url safe and unique`() {
        val state = PkceUtils.generateState()

        assertTrue(state.matches(urlSafePattern))
        assertNotEquals(state, PkceUtils.generateState())
    }

    @Test
    fun `challenge is base64url encoded sha256 of verifier`() {
        val verifier = PkceUtils.generateVerifier()
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(verifier.toByteArray(Charsets.US_ASCII))
        val expected = Base64.getUrlEncoder().withoutPadding().encodeToString(digest)

        assertEquals(expected, PkceUtils.generateChallenge(verifier))
    }
}
