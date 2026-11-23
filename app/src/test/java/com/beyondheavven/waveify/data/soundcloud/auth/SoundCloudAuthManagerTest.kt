package com.beyondheavven.waveify.data.soundcloud.auth

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
    fun `extract client id when another id appears first in hydration`() {
        val html = """
            <script>
            window.__sc_hydration = [{"hydratable":"trackingBrowserTabId","data":{"id":"Unrelated1234567890"}},{"hydratable":"apiClient","data":{"id":"TargetClient1234567890","isExpiring":true}}];</script>
        """.trimIndent()

        assertEquals("TargetClient1234567890", SoundCloudAuthManager.extractClientId(html))
    }

    @Test
    fun `extract client id from quoted object notation`() {
        val html = """apiClient = { id: 'Short1234567890AbCd' };"""

        assertEquals("Short1234567890AbCd", SoundCloudAuthManager.extractClientId(html))
    }

    @Test
    fun `return null when mobile page has no hydration`() {
        val html = """<html><script>window.__sc_version="1795431512"</script><body>Mobile</body></html>"""

        assertNull(SoundCloudAuthManager.extractClientId(html))
    }

    @Test
    fun `normalize nickname from profile url`() {
        assertEquals("my-name", SoundCloudAuthManager.normalizeNickname("@my-name"))
        assertEquals("my-name", SoundCloudAuthManager.normalizeNickname("https://soundcloud.com/my-name?x=1"))
        assertNull(SoundCloudAuthManager.normalizeNickname("bad name!"))
    }
}
