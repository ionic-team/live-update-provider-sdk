package io.ionic.liveupdatesprovider

import io.ionic.liveupdatesprovider.models.ProviderConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderConfigTests {

    @Test
    fun `create config with empty values`() {
        val config = ProviderConfig(emptyMap())

        assertNotNull(config)
        assertTrue(config.data.isEmpty())
    }

    @Test
    fun `create config with string value`() {
        val config = ProviderConfig(
            mapOf("appId" to "test-app")
        )

        assertEquals(1, config.data.size)
        val appId = config.data["appId"] as String
        assertEquals("test-app", appId)
    }

    @Test
    fun `create config with number value`() {
        val config = ProviderConfig(
            mapOf("timeout" to 30.0)
        )
        assertEquals(1, config.data.size)
        val timeout = config.data["timeout"] as Double
        assertEquals(30.0, timeout, 0.001)
    }

    @Test
    fun `create config with bool value`() {
        val config = ProviderConfig(
            mapOf("enabled" to true)
        )

        assertEquals(1, config.data.size)
        val enabled = config.data["enabled"] as Boolean
        assertTrue(enabled)
    }

    @Test
    fun `create config with nested object value`() {
        val nestedConfig = mapOf(
            "host" to "example.com",
            "port" to 8080
        )
        val config = ProviderConfig(
            mapOf("server" to nestedConfig)
        )

        assertEquals(1, config.data.size)
        val server = config.data["server"] as Map<*, *>
        assertEquals(2, server.size)

        assertEquals("example.com", server["host"])

        val port = server["port"] as Int
        assertEquals(8080, port)
    }

    @Test
    fun `create config with mixed value types`() {
        val config = ProviderConfig(
            mapOf(
                "appId" to "test-app",
                "timeout" to 60.0,
                "syncOnAdd" to false
            )
        )

        assertEquals(3, config.data.size)
         assertTrue(config.data.containsKey("appId"))
        assertTrue(config.data.containsKey("timeout"))
        assertTrue(config.data.containsKey("syncOnAdd"))
    }

}
