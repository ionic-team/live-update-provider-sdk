package io.ionic.liveupdates.provider

import io.ionic.liveupdates.provider.models.LiveUpdatesConfigValue
import io.ionic.liveupdates.provider.models.LiveUpdatesProviderConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveUpdatesProviderConfigTests {

    @Test
    fun `create config with empty values`() {
        val config = LiveUpdatesProviderConfig(emptyMap())

        assertNotNull(config)
        assertTrue(config.values.isEmpty())
    }

    @Test
    fun `create config with string value`() {
        val config = LiveUpdatesProviderConfig(
            mapOf("appId" to LiveUpdatesConfigValue.StringValue("test-app"))
        )

        assertEquals(1, config.values.size)
        val appId = config.values["appId"] as LiveUpdatesConfigValue.StringValue
        assertEquals("test-app", appId.value)
    }

    @Test
    fun `create config with number value`() {
        val config = LiveUpdatesProviderConfig(
            mapOf("timeout" to LiveUpdatesConfigValue.NumberValue(30.0))
        )

        assertEquals(1, config.values.size)
        val timeout = config.values["timeout"] as LiveUpdatesConfigValue.NumberValue
        assertEquals(30.0, timeout.value, 0.001)
    }

    @Test
    fun `create config with bool value`() {
        val config = LiveUpdatesProviderConfig(
            mapOf("enabled" to LiveUpdatesConfigValue.BoolValue(true))
        )

        assertEquals(1, config.values.size)
        val enabled = config.values["enabled"] as LiveUpdatesConfigValue.BoolValue
        assertTrue(enabled.value)
    }

    @Test
    fun `create config with nested object value`() {
        val nestedConfig = mapOf(
            "host" to LiveUpdatesConfigValue.StringValue("example.com"),
            "port" to LiveUpdatesConfigValue.NumberValue(8080.0)
        )
        val config = LiveUpdatesProviderConfig(
            mapOf("server" to LiveUpdatesConfigValue.ObjectValue(nestedConfig))
        )

        assertEquals(1, config.values.size)
        val server = config.values["server"] as LiveUpdatesConfigValue.ObjectValue
        assertEquals(2, server.value.size)

        val host = server.value["host"] as LiveUpdatesConfigValue.StringValue
        assertEquals("example.com", host.value)

        val port = server.value["port"] as LiveUpdatesConfigValue.NumberValue
        assertEquals(8080.0, port.value, 0.001)
    }

    @Test
    fun `create config with mixed value types`() {
        val config = LiveUpdatesProviderConfig(
            mapOf(
                "appId" to LiveUpdatesConfigValue.StringValue("test-app"),
                "timeout" to LiveUpdatesConfigValue.NumberValue(60.0),
                "syncOnAdd" to LiveUpdatesConfigValue.BoolValue(false)
            )
        )

        assertEquals(3, config.values.size)
        assertTrue(config.values.containsKey("appId"))
        assertTrue(config.values.containsKey("timeout"))
        assertTrue(config.values.containsKey("syncOnAdd"))
    }

    @Test
    fun `config data class equals works correctly`() {
        val config1 = LiveUpdatesProviderConfig(
            mapOf("appId" to LiveUpdatesConfigValue.StringValue("test-app"))
        )
        val config2 = LiveUpdatesProviderConfig(
            mapOf("appId" to LiveUpdatesConfigValue.StringValue("test-app"))
        )

        assertEquals(config1, config2)
    }

    @Test
    fun `config data class hashCode works correctly`() {
        val config1 = LiveUpdatesProviderConfig(
            mapOf("appId" to LiveUpdatesConfigValue.StringValue("test-app"))
        )
        val config2 = LiveUpdatesProviderConfig(
            mapOf("appId" to LiveUpdatesConfigValue.StringValue("test-app"))
        )

        assertEquals(config1.hashCode(), config2.hashCode())
    }
}
