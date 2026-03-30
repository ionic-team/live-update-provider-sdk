package io.ionic.liveupdateprovider

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LiveUpdateProviderRegistryTests {
    @Test
    fun `require throws ProviderNotRegistered for unknown id`() {
        val unknownId = uniqueProviderId()

        try {
            LiveUpdateProviderRegistry.require(unknownId)
            throw AssertionError("Expected ProviderNotRegistered")
        } catch (error: LiveUpdateError.ProviderNotRegistered) {
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
    fun `register ignores empty and blank ids`() {
        val emptyIdProvider = TestProvider("")
        val blankIdProvider = TestProvider("   ")

        LiveUpdateProviderRegistry.register(emptyIdProvider)
        LiveUpdateProviderRegistry.register(blankIdProvider)

        assertNull(LiveUpdateProviderRegistry.resolve(""))
        assertNull(LiveUpdateProviderRegistry.resolve("   "))
    }

    @Test
    fun `duplicate registration keeps the first provider`() {
        val id = uniqueProviderId()
        val first = TestProvider(id)
        val second = TestProvider(id)

        LiveUpdateProviderRegistry.register(first)
        LiveUpdateProviderRegistry.register(second)

        assertSame(first, LiveUpdateProviderRegistry.resolve(id))
    }

    @Test
    fun `concurrent duplicate registration keeps the first provider`() {
        val id = uniqueProviderId()
        val first = TestProvider(id)
        LiveUpdateProviderRegistry.register(first)

        runConcurrent(times = 24) {
            LiveUpdateProviderRegistry.register(TestProvider(id))
        }

        assertSame(first, LiveUpdateProviderRegistry.resolve(id))
    }

    @Test
    fun `concurrent mixed register and resolve does not crash`() {
        val runId = UUID.randomUUID().toString()
        val ids = (0 until 10).map { "provider-$runId-$it" }

        runConcurrent(times = 60) { index ->
            val id = ids[index % ids.size]
            when (index % 2) {
                0 -> LiveUpdateProviderRegistry.register(TestProvider(id))
                else -> LiveUpdateProviderRegistry.resolve(id)
            }
        }

        val registeredCount = ids.count { LiveUpdateProviderRegistry.resolve(it) != null }
        assertTrue("Expected at least one provider to be registered", registeredCount > 0)
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
            config: Map<String, Any>?
        ): LiveUpdateManager {
            throw NotImplementedError("Not needed for registry tests")
        }
    }
}
