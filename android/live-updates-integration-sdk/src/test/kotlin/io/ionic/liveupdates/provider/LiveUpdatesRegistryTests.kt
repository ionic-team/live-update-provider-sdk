package io.ionic.liveupdates.provider

import android.content.Context
import io.ionic.liveupdates.provider.models.LiveUpdatesOptions
import io.ionic.liveupdates.provider.models.LiveUpdatesProviderConfig
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

    @Mock 
    private lateinit var secondMockProvider: LiveUpdatesProvider

    @Before
    fun setup() {
        // Clear registry before each test
        LiveUpdatesRegistry.clear()
        // register mockProvider as the default "ionic" provider for tests that rely on it
        LiveUpdatesRegistry.register(LiveUpdatesRegistry.DEFAULT_PROVIDER_ID, mockProvider)
    }

    @Test
    fun `register provider successfully`() {
        LiveUpdatesRegistry.register("test-provider", secondMockProvider)
        assertTrue(LiveUpdatesRegistry.isRegistered("test-provider"))
    }

    @Test
    fun `resolve registered provider returns provider`() {
        LiveUpdatesRegistry.register("test-provider", secondMockProvider)
        val resolved = LiveUpdatesRegistry.resolve("test-provider")
        assertEquals(secondMockProvider, resolved)
    }

    @Test
    fun `resolve unregistered provider returns null`() {
        val resolved = LiveUpdatesRegistry.resolve("nonexistent")
        assertNull(resolved)
    }

    @Test
    fun `require registered provider returns provider`() {
        LiveUpdatesRegistry.register("test-provider", secondMockProvider)
        val required = LiveUpdatesRegistry.require("test-provider")
        assertEquals(secondMockProvider, required)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `require unregistered provider throws exception`() {
        LiveUpdatesRegistry.require("nonexistent")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register duplicate provider ID throws exception`() {
        LiveUpdatesRegistry.register("test-provider", secondMockProvider)
        LiveUpdatesRegistry.register("test-provider", secondMockProvider)
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
        LiveUpdatesRegistry.register("test-provider", secondMockProvider)
        assertTrue(LiveUpdatesRegistry.isRegistered("test-provider"))

        LiveUpdatesRegistry.clear()

        assertFalse(LiveUpdatesRegistry.isRegistered("test-provider"))
    }

    @Test
    fun `resolveOrDefault with null providerId returns ionic provider if registered`() {
        // Register the default "ionic" provider
        val provider = LiveUpdatesRegistry.resolveOrDefault(null)
        assertEquals(mockProvider, provider)
    }


    @Test
    fun `resolveOrDefault with explicit providerId returns specified provider`() {
        LiveUpdatesRegistry.register("custom", mockProvider)
        val provider = LiveUpdatesRegistry.resolveOrDefault("custom")
        assertEquals(mockProvider, provider)
    }

    @Test
    fun `resolveOrDefault with unregistered providerId returns null`() {
        val provider = LiveUpdatesRegistry.resolveOrDefault("nonexistent")
        assertEquals(provider, mockProvider)
    }
}
