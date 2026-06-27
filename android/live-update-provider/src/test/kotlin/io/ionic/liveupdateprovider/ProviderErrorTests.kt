package io.ionic.liveupdateprovider

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ProviderErrorTests {
    @Test
    fun `provider not registered includes provider id`() {
        val error = ProviderError.ProviderNotRegistered("test-provider")

        assertEquals("test-provider", error.providerId)
    }

    @Test
    fun `invalid configuration carries details and cause`() {
        val cause = IllegalArgumentException("bad config")
        val error = ProviderError.InvalidConfiguration("missing appId", cause)

        assertEquals("missing appId", error.details)
        assertSame(cause, error.cause)
    }

    @Test
    fun `sync failed carries details and cause`() {
        val cause = RuntimeException("network down")
        val error = ProviderError.SyncFailed("request failed", cause)

        assertEquals("request failed", error.details)
        assertSame(cause, error.cause)
    }
}
