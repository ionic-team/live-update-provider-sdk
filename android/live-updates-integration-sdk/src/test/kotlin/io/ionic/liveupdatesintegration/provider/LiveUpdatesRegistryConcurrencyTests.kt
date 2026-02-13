package io.ionic.liveupdatesintegration.provider

import android.content.Context
import io.ionic.liveupdatesintegration.provider.models.LiveUpdatesOptions
import io.ionic.liveupdatesintegration.provider.models.LiveUpdatesProviderConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.atomic.AtomicInteger

@RunWith(MockitoJUnitRunner::class)
class LiveUpdatesRegistryConcurrencyTests {

    @Mock
    private lateinit var mockContext: Context

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

    @Test
    fun `registry handles concurrent registrations safely`() = runBlocking {
        // Create 20 mock providers
        val providerCount = 20
        val providers = (0 until providerCount).map { index ->
            TestProvider("provider-$index")
        }

        // Register providers concurrently using coroutines
        val jobs = providers.mapIndexed { index, provider ->
            async(Dispatchers.Default) {
                LiveUpdatesRegistry.register("provider-$index", provider)
            }
        }

        // Wait for all registrations to complete
        jobs.awaitAll()

        // Verify all providers registered
        for (index in 0 until providerCount) {
            assertTrue(
                "Provider $index should be registered",
                LiveUpdatesRegistry.isRegistered("provider-$index")
            )
        }
    }

    @Test
    fun `registry handles concurrent resolutions safely`() = runBlocking {
        // Register 10 providers
        val providerCount = 10
        for (index in 0 until providerCount) {
            LiveUpdatesRegistry.register("provider-$index", TestProvider("provider-$index"))
        }

        // Resolve providers concurrently
        val resolveCount = 100
        val successCount = AtomicInteger(0)

        val jobs = (0 until resolveCount).map {
            launch(Dispatchers.Default) {
                val providerIndex = it % providerCount
                val provider = LiveUpdatesRegistry.resolve("provider-$providerIndex")
                if (provider != null) {
                    successCount.incrementAndGet()
                }
            }
        }

        // Wait for all resolutions
        jobs.forEach { it.join() }

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
                            LiveUpdatesRegistry.register(providerId, TestProvider(providerId))
                        } catch (e: IllegalArgumentException) {
                            // Duplicate registration expected in concurrent scenario
                        }
                    }
                    1 -> {
                        // Resolve
                        val providerId = "provider-${index % providerCount}"
                        LiveUpdatesRegistry.resolve(providerId)
                    }
                    else -> {
                        // Check registration
                        val providerId = "provider-${index % providerCount}"
                        LiveUpdatesRegistry.isRegistered(providerId)
                    }
                }
            }
        }

        // Wait for all operations
        jobs.awaitAll()

        // Verify registry is still consistent
        assertTrue(
            "At least some providers should be registered",
            (0 until providerCount).any { LiveUpdatesRegistry.isRegistered("provider-$it") }
        )
    }

    @Test
    fun `registry handles concurrent registrations with same ID safely`() = runBlocking {
        val providerId = "test-provider"
        val attemptCount = 20
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // Attempt to register same provider ID concurrently
        val jobs = (0 until attemptCount).map {
            launch(Dispatchers.Default) {
                try {
                    LiveUpdatesRegistry.register(providerId, TestProvider(providerId))
                    successCount.incrementAndGet()
                } catch (e: IllegalArgumentException) {
                    failureCount.incrementAndGet()
                }
            }
        }

        // Wait for all attempts
        jobs.forEach { it.join() }

        // Exactly one registration should succeed, others should fail
        assertEquals(1, successCount.get())
        assertEquals(attemptCount - 1, failureCount.get())

        // Provider should be registered
        assertTrue(LiveUpdatesRegistry.isRegistered(providerId))
    }

    @Test
    fun `registry handles concurrent resolveOrDefault safely`() = runBlocking {
        // Register test providers and also ensure ionic provider is registered
        LiveUpdatesRegistry.register("ionic", TestProvider("ionic"))
        LiveUpdatesRegistry.register("provider-1", TestProvider("provider-1"))
        LiveUpdatesRegistry.register("provider-2", TestProvider("provider-2"))

        val resolveCount = 99 // Use 99 to avoid modulo issues
        val jobs = (0 until resolveCount).map { index ->
            async(Dispatchers.Default) {
                when (index % 3) {
                    0 -> LiveUpdatesRegistry.resolveOrDefault(null) // Default (ionic)
                    1 -> LiveUpdatesRegistry.resolveOrDefault("provider-1")
                    else -> LiveUpdatesRegistry.resolveOrDefault("provider-2")
                }
            }
        }

        // Collect all results
        val results = jobs.awaitAll()

        // All resolutions should succeed (no nulls expected)
        val nonNullResults = results.filterNotNull()
        assertTrue(
            "All ${resolveCount} resolutions should succeed, got ${nonNullResults.size}",
            nonNullResults.size == resolveCount
        )
    }

    // Test provider implementation
    private class TestProvider(private val id: String) : LiveUpdatesProvider {
        override fun createManager(
            context: Context,
            config: LiveUpdatesProviderConfig,
            options: LiveUpdatesOptions
        ): LiveUpdatesManager {
            // Not used in these tests
            throw NotImplementedError("Test provider does not create managers")
        }
    }
}
