package io.ionic.liveupdateprovider

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class LiveUpdateProviderRegistryTests {
    @Test
    fun `require throws ProviderNotRegistered for unknown id`() {
        val unknownId = uniqueProviderId()

        try {
            LiveUpdateProviderRegistry.require(unknownId)
            throw AssertionError("Expected ProviderNotRegistered")
        } catch (error: LiveUpdateProviderError.ProviderNotRegistered) {
            assertEquals(unknownId, error.providerId)
        }
    }

    @Test
    fun `register and require returns the same provider`() {
        val id = uniqueProviderId()
        val provider = TestProvider(id)

        LiveUpdateProviderRegistry.register(provider)

        assertSame(provider, LiveUpdateProviderRegistry.resolve(id))
        assertSame(provider, LiveUpdateProviderRegistry.require(id))
    }

    @Test
    fun `register throws for empty and blank ids`() {
        val emptyIdProvider = TestProvider("")
        val blankIdProvider = TestProvider("   ")

        try {
            LiveUpdateProviderRegistry.register(emptyIdProvider)
            throw AssertionError("Expected InvalidConfiguration for empty ID")
        } catch (error: LiveUpdateProviderError.InvalidConfiguration) {
            assertTrue(error.details.contains("empty ID"))
        }

        try {
            LiveUpdateProviderRegistry.register(blankIdProvider)
            throw AssertionError("Expected InvalidConfiguration for blank ID")
        } catch (error: LiveUpdateProviderError.InvalidConfiguration) {
            assertTrue(error.details.contains("empty ID"))
        }
    }

    @Test
    fun `duplicate registration throws invalid configuration`() {
        val id = uniqueProviderId()
        val first = TestProvider(id)
        val second = TestProvider(id)

        LiveUpdateProviderRegistry.register(first)
        try {
            LiveUpdateProviderRegistry.register(second)
            throw AssertionError("Expected InvalidConfiguration for duplicate ID")
        } catch (error: LiveUpdateProviderError.InvalidConfiguration) {
            assertTrue(error.details.contains("already registered"))
        }
    }

    @Test
    fun `concurrent duplicate registration keeps first and throws for others`() {
        val id = uniqueProviderId()
        val first = TestProvider(id)
        LiveUpdateProviderRegistry.register(first)
        val invalidConfigurationCount = AtomicInteger(0)

        runConcurrent(times = 24) {
            try {
                LiveUpdateProviderRegistry.register(TestProvider(id))
            } catch (error: LiveUpdateProviderError.InvalidConfiguration) {
                invalidConfigurationCount.incrementAndGet()
            }
        }

        assertSame(first, LiveUpdateProviderRegistry.resolve(id))
        assertEquals(24, invalidConfigurationCount.get())
    }

    @Test
    fun `concurrent unique registrations are resolvable`() {
        val runId = UUID.randomUUID().toString()
        val providersById = ConcurrentHashMap<String, TestProvider>()

        runConcurrent(times = 60) { index ->
            val id = "provider-$runId-$index"
            val provider = TestProvider(id)
            providersById[id] = provider
            LiveUpdateProviderRegistry.register(provider)
        }

        providersById.forEach { (id, provider) ->
            assertSame(provider, LiveUpdateProviderRegistry.resolve(id))
        }
    }

    private fun uniqueProviderId(): String = "provider-${UUID.randomUUID()}"

    private fun runConcurrent(times: Int, action: (Int) -> Unit) {
        val pool = Executors.newFixedThreadPool(8)
        val done = CountDownLatch(times)

        repeat(times) { index ->
            pool.execute {
                try {
                    action(index)
                } finally {
                    done.countDown()
                }
            }
        }

        val finished = done.await(10, TimeUnit.SECONDS)
        pool.shutdownNow()
        if (!finished) {
            throw AssertionError("Timed out waiting for concurrent operations")
        }
    }

    private class TestProvider(override val id: String) : LiveUpdateProvider {
        override fun createManager(
            context: Context,
            config: Map<String, Any>
        ): LiveUpdateProviderManager {
            throw NotImplementedError("Not needed for registry tests")
        }
    }
}
