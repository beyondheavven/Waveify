package com.beyondheavven.waveify.core.ui.cover

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AsciiColorModeTest {

    @Test
    fun `AsciiColorMode contains expected themes`() {
        val modes = AsciiColorMode.values().map { it.name }

        assertTrue(modes.contains("COLORED"))
        assertTrue(modes.contains("MONOCHROME"))
        assertTrue(modes.contains("WAVEIFY_RED"))
        assertTrue(modes.contains("GREEN_MATRIX"))
    }

    @Test
    fun `AsciiColorMode valueOf works for valid names and fallback can be recovered`() {
        assertEquals(AsciiColorMode.COLORED, AsciiColorMode.valueOf("COLORED"))
        assertEquals(AsciiColorMode.WAVEIFY_RED, AsciiColorMode.valueOf("WAVEIFY_RED"))

        val fallback = runCatching {
            AsciiColorMode.valueOf("UNKNOWN_MODE")
        }.getOrDefault(AsciiColorMode.COLORED)

        assertEquals(AsciiColorMode.COLORED, fallback)
    }
}
