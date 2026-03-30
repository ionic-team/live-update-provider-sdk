package io.ionic.liveupdateprovider

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LiveUpdateErrorTests {
    @Test
    fun `provider not registered includes provider id`() {
        val error = LiveUpdateError.ProviderNotRegistered("test-provider")

        assertEquals("test-provider", error.providerId)
        assertEquals("Provider with ID 'test-provider' not found", error.message)
    }

    @Test
    fun `invalid configuration carries details and cause`() {
        val cause = IllegalArgumentException("bad config")
        val error = LiveUpdateError.InvalidConfiguration("missing appId", cause)

        assertEquals("missing appId", error.details)
        assertSame(cause, error.cause)
        assertEquals("Invalid configuration: missing appId", error.message)
    }

    @Test
    fun `sync failed carries details and cause`() {
        val cause = RuntimeException("network down")
        val error = LiveUpdateError.SyncFailed("request failed", cause)

        assertEquals("request failed", error.details)
        assertSame(cause, error.cause)
        assertEquals("Sync failed: request failed", error.message)
    }
}
