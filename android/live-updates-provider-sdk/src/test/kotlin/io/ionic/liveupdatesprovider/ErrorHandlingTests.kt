package io.ionic.liveupdatesprovider

import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ErrorHandlingTests {

    @Before
    fun setup() {
        // Clear registry before each test
        LiveUpdatesRegistry.clear()
    }

    @After
    fun teardown() {
        // Clean up after each test
        LiveUpdatesRegistry.clear()
    }

    // ===== Registry Error Handling =====

    @Test
    fun `resolve with unregistered provider ID returns null`() {
        val provider = LiveUpdatesRegistry.resolve("nonexistent-provider")
        assertNull(provider)
    }

    @Test
    fun `require with unregistered provider ID throws IllegalArgumentException`() {
        try {
            LiveUpdatesRegistry.require("nonexistent-provider")
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(
                "Exception message should mention provider not found",
                e.message?.contains("not found") == true
            )
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register with empty provider ID throws IllegalArgumentException`() {
        val testProvider = TestProviderImpl("")
        LiveUpdatesRegistry.register(testProvider)
        LiveUpdatesRegistry.require(testProvider.id) // This will throw if registration succeeded
        throw AssertionError("Expected IllegalArgumentException")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register with blank provider ID throws IllegalArgumentException`() {
        val testProvider = TestProviderImpl("   ")

        LiveUpdatesRegistry.register(testProvider)
        LiveUpdatesRegistry.require(testProvider.id) // This will throw if registration succeeded
        throw AssertionError("Expected IllegalArgumentException")
    }

    @Test
    fun `resolve with empty provider ID returns null`() {
        val provider = LiveUpdatesRegistry.resolve("")
        assertNull(provider)
    }

    @Test
    fun `require with empty provider ID throws IllegalArgumentException`() {
        try {
            LiveUpdatesRegistry.require("")
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(
                "Exception message should mention provider not found",
                e.message?.contains("not found") == true
            )
        }
    }
}
