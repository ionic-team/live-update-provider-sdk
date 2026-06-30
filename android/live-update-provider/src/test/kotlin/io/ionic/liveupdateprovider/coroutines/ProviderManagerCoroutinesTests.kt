package io.ionic.liveupdateprovider.coroutines

import io.ionic.liveupdateprovider.MetadataSyncResult
import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.ProviderSyncCallback
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File

class ProviderManagerCoroutinesTests {
    @Test
    fun `suspend sync resumes with success result`() = runTest {
        val expected = MetadataSyncResult(mapOf("version" to "1.0.0"))
        val manager = CallbackManager { callback -> callback.onSuccess(expected) }

        val result = manager.sync()

        Assert.assertSame(expected, result)
    }

    @Test
    fun `suspend sync resumes with null when nothing to report`() = runTest {
        val manager = CallbackManager { callback -> callback.onSuccess(null) }

        Assert.assertNull(manager.sync())
    }

    @Test
    fun `suspend sync throws on failure`() = runTest {
        val failure = ProviderError.SyncFailed("boom")
        val manager = CallbackManager { callback -> callback.onFailure(failure) }

        try {
            manager.sync()
            throw AssertionError("Expected SyncFailed")
        } catch (error: ProviderError.SyncFailed) {
            Assert.assertSame(failure, error)
        }
    }

    private class CallbackManager(
        private val onSync: (ProviderSyncCallback) -> Unit
    ) : ProviderManager {
        override val latestAppDirectory: File? = null
        override fun sync(callback: ProviderSyncCallback) = onSync(callback)
    }
}