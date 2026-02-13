package io.ionic.liveupdatesprovider.provider

import android.content.Context
import io.ionic.liveupdatesprovider.provider.models.LiveUpdatesOptions
import io.ionic.liveupdatesprovider.provider.models.LiveUpdatesProviderConfig
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LiveUpdatesRegistryTests {
    @Mock
    private lateinit var mockProvider: LiveUpdatesProvider

    @Before
    fun setup() {
        // Clear registry before each test
        LiveUpdatesRegistry.clear()
    }

    @Test
    fun `register provider successfully`() {
        LiveUpdatesRegistry.register("test-provider", mockProvider)
        assertTrue(LiveUpdatesRegistry.isRegistered("test-provider"))
    }

    @Test
    fun `resolve registered provider returns provider`() {
        LiveUpdatesRegistry.register("test-provider", mockProvider)
        val resolved = LiveUpdatesRegistry.resolve("test-provider")
        assertEquals(mockProvider, resolved)
    }

    @Test
    fun `resolve unregistered provider returns null`() {
        val resolved = LiveUpdatesRegistry.resolve("nonexistent")
        assertNull(resolved)
    }

    @Test
    fun `require registered provider returns provider`() {
        LiveUpdatesRegistry.register("test-provider", mockProvider)
        val required = LiveUpdatesRegistry.require("test-provider")
        assertEquals(mockProvider, required)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `require unregistered provider throws exception`() {
        LiveUpdatesRegistry.require("nonexistent")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register duplicate provider ID throws exception`() {
        LiveUpdatesRegistry.register("test-provider", mockProvider)
        LiveUpdatesRegistry.register("test-provider", mockProvider)
    }

    @Test
    fun `isRegistered returns false for unregistered provider`() {
        assertFalse(LiveUpdatesRegistry.isRegistered("nonexistent"))
    }

    @Test
    fun `register multiple providers with different IDs succeeds`() {
        val mockProvider2 = object : LiveUpdatesProvider {
            override fun createManager(
                context: Context,
                config: LiveUpdatesProviderConfig,
                options: LiveUpdatesOptions
            ): LiveUpdatesManager {
                throw NotImplementedError()
            }
        }

        LiveUpdatesRegistry.register("provider1", mockProvider)
        LiveUpdatesRegistry.register("provider2", mockProvider2)

        assertTrue(LiveUpdatesRegistry.isRegistered("provider1"))
        assertTrue(LiveUpdatesRegistry.isRegistered("provider2"))
        assertEquals(mockProvider, LiveUpdatesRegistry.resolve("provider1"))
        assertEquals(mockProvider2, LiveUpdatesRegistry.resolve("provider2"))
    }

    @Test
    fun `clear removes all registered providers`() {
        LiveUpdatesRegistry.register("test-provider", mockProvider)
        assertTrue(LiveUpdatesRegistry.isRegistered("test-provider"))

        LiveUpdatesRegistry.clear()

        assertFalse(LiveUpdatesRegistry.isRegistered("test-provider"))
    }
}
