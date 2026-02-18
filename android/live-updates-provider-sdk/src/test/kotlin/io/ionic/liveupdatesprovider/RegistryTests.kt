package io.ionic.liveupdatesprovider


import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegistryTests {
    private lateinit var mockProvider: TestProviderImpl

    @Before
    fun setup() {
        // Clear registry before each test
        LiveUpdatesRegistry.clear()
        mockProvider = TestProviderImpl("test-provider")
    }

    @Test
    fun `register provider successfully`() {
        LiveUpdatesRegistry.register(mockProvider)
        assertTrue(LiveUpdatesRegistry.isRegistered("test-provider"))
    }

    @Test
    fun `resolve registered provider returns provider`() {
        LiveUpdatesRegistry.register(mockProvider)
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
        LiveUpdatesRegistry.register(mockProvider)
        val required = LiveUpdatesRegistry.require("test-provider")
        assertEquals(mockProvider, required)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `require unregistered provider throws exception`() {
        LiveUpdatesRegistry.require("nonexistent")
    }

    @Test
    fun `register duplicate provider ID throws exception`() {
        LiveUpdatesRegistry.register(mockProvider)
        LiveUpdatesRegistry.register(mockProvider)
        val mockProvider2 = TestProviderImpl("test-provider")
        LiveUpdatesRegistry.register(mockProvider2)
        assertEquals(mockProvider, LiveUpdatesRegistry.resolve("test-provider"))
    }

    @Test
    fun `isRegistered returns false for unregistered provider`() {
        assertFalse(LiveUpdatesRegistry.isRegistered("nonexistent"))
    }

    @Test
    fun `register multiple providers with different IDs succeeds`() {
        val mockProvider1 = TestProviderImpl("provider1")
        val mockProvider2 = TestProviderImpl("provider2")

        LiveUpdatesRegistry.register(mockProvider1)
        LiveUpdatesRegistry.register(mockProvider2)

        assertTrue(LiveUpdatesRegistry.isRegistered("provider1"))
        assertTrue(LiveUpdatesRegistry.isRegistered("provider2"))
        assertEquals(mockProvider1, LiveUpdatesRegistry.resolve("provider1"))
        assertEquals(mockProvider2, LiveUpdatesRegistry.resolve("provider2"))
    }

    @Test
    fun `clear removes all registered providers`() {
        LiveUpdatesRegistry.register(mockProvider)
        assertTrue(LiveUpdatesRegistry.isRegistered("test-provider"))

        LiveUpdatesRegistry.clear()

        assertFalse(LiveUpdatesRegistry.isRegistered("test-provider"))
    }
}
