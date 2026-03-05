package io.ionic.liveupdateprovider

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.atomic.AtomicInteger

@RunWith(MockitoJUnitRunner::class)
class RegistryConcurrencyTests {

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

    @Test
    fun `registry handles concurrent registrations safely`() = runBlocking {
        // Create 20 mock providers
        val providerCount = 20
        val providers = (0 until providerCount).map { index ->
            TestProviderImpl("provider-$index")
        }

        // Register providers concurrently using coroutines
        val jobs = providers.map { provider ->
            async(Dispatchers.Default) {
                LiveUpdateProviderRegistry.register(provider)
            }
        }

        // Wait for all registrations to complete
        jobs.awaitAll()

        // Verify all providers registered
        for (index in 0 until providerCount) {
            assertTrue(
                "Provider $index should be registered",
                LiveUpdateProviderRegistry.resolve("provider-$index") != null
            )
        }
    }

    @Test
    fun `registry handles concurrent resolutions safely`() = runBlocking {
        // Register 10 providers
        val providerCount = 10
        for (index in 0 until providerCount) {
            LiveUpdateProviderRegistry.register(TestProviderImpl("provider-$index"))
        }

        // Resolve providers concurrently
        val resolveCount = 100
        val successCount = AtomicInteger(0)

        val jobs = (0 until resolveCount).map {
            launch(Dispatchers.Default) {
                val providerIndex = it % providerCount
                val provider = LiveUpdateProviderRegistry.resolve("provider-$providerIndex")
                if (provider != null) {
                    successCount.incrementAndGet()
                }
            }
        }

        // Wait for all resolutions
        jobs.joinAll()

        // All resolutions should succeed
        assertEquals(resolveCount, successCount.get())
    }

    @Test
    fun `registry handles concurrent mixed operations safely`() = runBlocking {
        val providerCount = 10
        val operationCount = 50

        // Perform mixed operations concurrently
        val jobs = (0 until operationCount).map { index ->
            async(Dispatchers.Default) {
                when (index % 3) {
                    0 -> {
                        // Register
                        val providerId = "provider-${index % providerCount}"
                        try {
                            LiveUpdateProviderRegistry.register(TestProviderImpl(providerId))
                        } catch (_: IllegalArgumentException) {
                            // Duplicate registration expected in concurrent scenario
                        }
                    }
                    1 -> {
                        // Resolve
                        val providerId = "provider-${index % providerCount}"
                        LiveUpdateProviderRegistry.resolve(providerId)
                    }
                    else -> {
                        // Check registration
                        val providerId = "provider-${index % providerCount}"
                        LiveUpdateProviderRegistry.resolve(providerId) != null
                    }
                }
            }
        }

        // Wait for all operations
        jobs.awaitAll()

        // Verify registry is still consistent
        assertTrue(
            "At least some providers should be registered",
            (0 until providerCount).any { LiveUpdateProviderRegistry.resolve("provider-$it") != null }
        )
    }

    @Test
    fun `registry handles concurrent registrations with same ID safely`() = runBlocking {
        val providerId = "test-provider"
        val attemptCount = 20

        val firstProvider = TestProviderImpl(providerId)
        LiveUpdateProviderRegistry.register(firstProvider)
        // Attempt to register same provider ID concurrently
        val jobs = (0 until attemptCount).map {
            launch(Dispatchers.Default) {
                val testProvider = TestProviderImpl(providerId)
                LiveUpdateProviderRegistry.register(testProvider)
            }
        }

        // Wait for all attempts
        jobs.joinAll()

        // Provider should be registered
        assertTrue(LiveUpdateProviderRegistry.resolve(providerId) != null)
        assertEquals(firstProvider, LiveUpdateProviderRegistry.resolve(providerId))
    }
}
