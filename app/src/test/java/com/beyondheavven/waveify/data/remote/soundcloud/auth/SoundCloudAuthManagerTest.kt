package com.beyondheavven.waveify.data.remote.soundcloud.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SoundCloudAuthManagerTest {

    @Test
    fun `extract client id from hydration payload`() {
        val html = """
            <script>
            window.__sc_hydration = [{"hydratable":"apiClient","data":{"id":"AbCdEf123456GhIjKlMnOpQrStUvWx"}},{"hydratable":"systemPlaylist","data":{"id":42}}];
            </script>
        """.trimIndent()

        assertEquals("AbCdEf123456GhIjKlMnOpQrStUvWx", SoundCloudAuthManager.extractClientId(html))
    }

    @Test
    fun `extract client id from quoted object notation`() {
        val html = """apiClient = { id: 'Short1234567890AbCd' };"""

        assertEquals("Short1234567890AbCd", SoundCloudAuthManager.extractClientId(html))
    }

    @Test
    fun `normalize nickname from profile url`() {
        assertEquals("my-name", SoundCloudAuthManager.normalizeNickname("@my-name"))
        assertEquals("my-name", SoundCloudAuthManager.normalizeNickname("https://soundcloud.com/my-name?x=1"))
        assertNull(SoundCloudAuthManager.normalizeNickname("bad name!"))
    }
}
