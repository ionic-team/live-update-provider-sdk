package io.ionic.liveupdateprovider.coroutines

import io.ionic.liveupdateprovider.MetadataSyncResult
import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.ProviderSyncCallback
import io.ionic.liveupdateprovider.ProviderSyncResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ProviderManagerCoroutinesTests {
    @Test
    fun `suspend sync resumes with success result`() = runTest {
        val expected = MetadataSyncResult(mapOf("version" to "1.0.0"))
        val manager = CallbackManager { callback -> callback.onSuccess(expected) }

        val result = manager.sync()

        assertSame(expected, result)
    }

    @Test
    fun `suspend sync resumes with null when nothing to report`() = runTest {
        val manager = CallbackManager { callback -> callback.onSuccess(null) }

        assertNull(manager.sync())
    }

    @Test
    fun `suspend sync throws on failure`() = runTest {
        val failure = ProviderError.SyncFailed("boom")
        val manager = CallbackManager { callback -> callback.onFailure(failure) }

        try {
            manager.sync()
            throw AssertionError("Expected SyncFailed")
        } catch (error: ProviderError.SyncFailed) {
            assertSame(failure, error)
        }
    }

    @Test
    fun `CoroutineProviderManager delivers performSync result via callback`() = runTest {
        val manager = object : CoroutineProviderManager() {
            override val latestAppDirectory: File? = null
            override suspend fun performSync(): ProviderSyncResult =
                MetadataSyncResult(mapOf("ok" to true))
        }

        val result = manager.sync()

        assertTrue(result is MetadataSyncResult)
        assertEquals(true, (result as MetadataSyncResult).metadata["ok"])
    }

    @Test
    fun `CoroutineProviderManager wraps thrown errors as SyncFailed`() = runTest {
        val manager = object : CoroutineProviderManager() {
            override val latestAppDirectory: File? = null
            override suspend fun performSync(): ProviderSyncResult =
                throw IllegalStateException("disk full")
        }

        try {
            manager.sync()
            throw AssertionError("Expected SyncFailed")
        } catch (error: ProviderError.SyncFailed) {
            assertEquals("disk full", error.cause?.message)
        }
    }

    private class CallbackManager(
        private val onSync: (ProviderSyncCallback) -> Unit
    ) : ProviderManager {
        override val latestAppDirectory: File? = null
        override fun sync(callback: ProviderSyncCallback) = onSync(callback)
    }
}
