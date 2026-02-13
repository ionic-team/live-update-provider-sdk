package io.ionic.liveupdatesprovider.provider

import android.content.Context
import io.ionic.liveupdatesprovider.provider.models.LiveUpdatesOptions
import io.ionic.liveupdatesprovider.provider.models.LiveUpdatesProviderConfig
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
class LiveUpdatesRegistryConcurrencyTests {

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
        val jobs = providers.map { provider ->
            async(Dispatchers.Default) {
                LiveUpdatesRegistry.register(provider)
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
            LiveUpdatesRegistry.register(TestProvider("provider-$index"))
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
                            LiveUpdatesRegistry.register(TestProvider(providerId))
                        } catch (_: IllegalArgumentException) {
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
                    LiveUpdatesRegistry.register(TestProviderImpl(providerId))
                    successCount.incrementAndGet()
                } catch (_: IllegalArgumentException) {
                    failureCount.incrementAndGet()
                }
            }
        }

        // Wait for all attempts
        jobs.joinAll()

        // Exactly one registration should succeed, others should fail
        assertEquals(1, successCount.get())
        assertEquals(attemptCount - 1, failureCount.get())

        // Provider should be registered
        assertTrue(LiveUpdatesRegistry.isRegistered(providerId))
    }

    // Test provider implementation
    private class TestProvider(override var id: String) : LiveUpdatesProvider {
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
