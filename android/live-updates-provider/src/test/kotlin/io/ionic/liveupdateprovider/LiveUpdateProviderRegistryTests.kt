package io.ionic.liveupdateprovider


import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LiveUpdateProviderRegistryTests {
    private lateinit var mockProvider: TestProviderImpl

    @Before
    fun setup() {
        // Clear registry before each test
        LiveUpdateProviderRegistry.clear()
        mockProvider = TestProviderImpl("test-provider")
    }

    @Test
    fun `register provider successfully`() {
        LiveUpdateProviderRegistry.register(mockProvider)
        assertTrue(LiveUpdateProviderRegistry.resolve("test-provider") != null)
    }

    @Test
    fun `resolve registered provider returns provider`() {
        LiveUpdateProviderRegistry.register(mockProvider)
        val resolved = LiveUpdateProviderRegistry.resolve("test-provider")
        assertEquals(mockProvider, resolved)
    }

    @Test
    fun `resolve unregistered provider returns null`() {
        val resolved = LiveUpdateProviderRegistry.resolve("nonexistent")
        assertNull(resolved)
    }

    @Test
    fun `require registered provider returns provider`() {
        LiveUpdateProviderRegistry.register(mockProvider)
        val required = LiveUpdateProviderRegistry.require("test-provider")
        assertEquals(mockProvider, required)
    }

    @Test(expected = LiveUpdateError.ProviderNotRegistered::class)
    fun `require unregistered provider throws exception`() {
        LiveUpdateProviderRegistry.require("nonexistent")
    }

    @Test
    fun `register duplicate provider ID throws exception`() {
        LiveUpdateProviderRegistry.register(mockProvider)
        LiveUpdateProviderRegistry.register(mockProvider)
        val mockProvider2 = TestProviderImpl("test-provider")
        LiveUpdateProviderRegistry.register(mockProvider2)
        assertEquals(mockProvider, LiveUpdateProviderRegistry.resolve("test-provider"))
    }

    @Test
    fun `resolve returns null for unregistered provider`() {
        assertNull(LiveUpdateProviderRegistry.resolve("nonexistent"))
    }

    @Test
    fun `register multiple providers with different IDs succeeds`() {
        val mockProvider1 = TestProviderImpl("provider1")
        val mockProvider2 = TestProviderImpl("provider2")

        LiveUpdateProviderRegistry.register(mockProvider1)
        LiveUpdateProviderRegistry.register(mockProvider2)

        assertTrue(LiveUpdateProviderRegistry.resolve("provider1") != null)
        assertTrue(LiveUpdateProviderRegistry.resolve("provider2") != null)
        assertEquals(mockProvider1, LiveUpdateProviderRegistry.resolve("provider1"))
        assertEquals(mockProvider2, LiveUpdateProviderRegistry.resolve("provider2"))
    }

    @Test
    fun `clear removes all registered providers`() {
        LiveUpdateProviderRegistry.register(mockProvider)
        assertTrue(LiveUpdateProviderRegistry.resolve("test-provider") != null)

        LiveUpdateProviderRegistry.clear()

        assertNull(LiveUpdateProviderRegistry.resolve("test-provider"))
    }
}
