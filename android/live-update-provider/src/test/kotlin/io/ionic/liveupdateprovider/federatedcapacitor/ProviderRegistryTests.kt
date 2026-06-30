package io.ionic.liveupdateprovider.federatedcapacitor

import android.content.Context
import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager
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

class ProviderRegistryTests {
    @Test
    fun `require throws ProviderNotRegistered for unknown id`() {
        val unknownId = uniqueProviderId()

        try {
            ProviderRegistry.require(unknownId)
            throw AssertionError("Expected ProviderNotRegistered")
        } catch (error: ProviderError.ProviderNotRegistered) {
            assertEquals(unknownId, error.providerId)
        }
    }

    @Test
    fun `register and require returns the same provider`() {
        val id = uniqueProviderId()
        val provider = TestProvider(id)

        ProviderRegistry.register(provider)

        assertSame(provider, ProviderRegistry.resolve(id))
        assertSame(provider, ProviderRegistry.require(id))
    }

    @Test
    fun `register throws for empty and blank ids`() {
        val emptyIdProvider = TestProvider("")
        val blankIdProvider = TestProvider("   ")

        try {
            ProviderRegistry.register(emptyIdProvider)
            throw AssertionError("Expected RegistrationFailed for empty ID")
        } catch (error: ProviderError.RegistrationFailed) {
            assertTrue(error.details.contains("empty ID"))
        }

        try {
            ProviderRegistry.register(blankIdProvider)
            throw AssertionError("Expected RegistrationFailed for blank ID")
        } catch (error: ProviderError.RegistrationFailed) {
            assertTrue(error.details.contains("empty ID"))
        }
    }

    @Test
    fun `duplicate registration throws RegistrationFailed`() {
        val id = uniqueProviderId()
        val first = TestProvider(id)
        val second = TestProvider(id)

        ProviderRegistry.register(first)
        try {
            ProviderRegistry.register(second)
            throw AssertionError("Expected RegistrationFailed for duplicate ID")
        } catch (error: ProviderError.RegistrationFailed) {
            assertTrue(error.details.contains("already registered"))
        }
    }

    @Test
    fun `concurrent duplicate registration keeps first and throws for others`() {
        val id = uniqueProviderId()
        val first = TestProvider(id)
        ProviderRegistry.register(first)
        val registrationFailedCount = AtomicInteger(0)

        runConcurrent(times = 24) {
            try {
                ProviderRegistry.register(TestProvider(id))
            } catch (error: ProviderError.RegistrationFailed) {
                registrationFailedCount.incrementAndGet()
            }
        }

        assertSame(first, ProviderRegistry.resolve(id))
        assertEquals(24, registrationFailedCount.get())
    }

    @Test
    fun `concurrent unique registrations are resolvable`() {
        val runId = UUID.randomUUID().toString()
        val providersById = ConcurrentHashMap<String, TestProvider>()

        runConcurrent(times = 60) { index ->
            val id = "provider-$runId-$index"
            val provider = TestProvider(id)
            providersById[id] = provider
            ProviderRegistry.register(provider)
        }

        providersById.forEach { (id, provider) ->
            assertSame(provider, ProviderRegistry.resolve(id))
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
        ): ProviderManager {
            throw NotImplementedError("Not needed for registry tests")
        }
    }
}
