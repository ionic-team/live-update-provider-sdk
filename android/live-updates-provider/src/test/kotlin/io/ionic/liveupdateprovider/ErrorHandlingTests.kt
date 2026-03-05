package io.ionic.liveupdateprovider

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
        LiveUpdateProviderRegistry.clear()
    }

    @After
    fun teardown() {
        // Clean up after each test
        LiveUpdateProviderRegistry.clear()
    }

    // ===== Registry Error Handling =====

    @Test
    fun `resolve with unregistered provider ID returns null`() {
        val provider = LiveUpdateProviderRegistry.resolve("nonexistent-provider")
        assertNull(provider)
    }

    @Test
    fun `require with unregistered provider ID throws ProviderNotRegistered`() {
        try {
            LiveUpdateProviderRegistry.require("nonexistent-provider")
            throw AssertionError("Expected LiveUpdateError.ProviderNotRegistered")
        } catch (e: LiveUpdateError.ProviderNotRegistered) {
            assertTrue(
                "Exception message should mention provider not found",
                e.message?.contains("not found") == true
            )
        }
    }

    @Test(expected = LiveUpdateError.ProviderNotRegistered::class)
    fun `register with empty provider ID does not register and require throws`() {
        val testProvider = TestProviderImpl("")
        LiveUpdateProviderRegistry.register(testProvider)
        LiveUpdateProviderRegistry.require(testProvider.id) // This will throw if registration succeeded
        throw AssertionError("Expected LiveUpdateError.ProviderNotRegistered")
    }

    @Test(expected = LiveUpdateError.ProviderNotRegistered::class)
    fun `register with blank provider ID does not register and require throws`() {
        val testProvider = TestProviderImpl("   ")

        LiveUpdateProviderRegistry.register(testProvider)
        LiveUpdateProviderRegistry.require(testProvider.id) // This will throw if registration succeeded
        throw AssertionError("Expected LiveUpdateError.ProviderNotRegistered")
    }

    @Test
    fun `resolve with empty provider ID returns null`() {
        val provider = LiveUpdateProviderRegistry.resolve("")
        assertNull(provider)
    }

    @Test
    fun `require with empty provider ID throws ProviderNotRegistered`() {
        try {
            LiveUpdateProviderRegistry.require("")
            throw AssertionError("Expected LiveUpdateError.ProviderNotRegistered")
        } catch (e: LiveUpdateError.ProviderNotRegistered) {
            assertTrue(
                "Exception message should mention provider not found",
                e.message?.contains("not found") == true
            )
        }
    }
}
