package com.beyondheavven.waveify.core.network.oauth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

internal object PkceUtils {

    private const val RANDOM_BYTES = 32

    fun generateVerifier(): String = randomUrlSafe()

    fun generateState(): String = randomUrlSafe()

    fun generateChallenge(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(verifier.toByteArray(Charsets.US_ASCII))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    private fun randomUrlSafe(): String {
        val bytes = ByteArray(RANDOM_BYTES)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
