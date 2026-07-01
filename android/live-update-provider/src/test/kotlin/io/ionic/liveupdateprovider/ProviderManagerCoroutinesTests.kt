package io.ionic.liveupdateprovider

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
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
    fun `suspend sync rethrows the error passed to onFailure`() = runTest {
        val failure = RuntimeException("boom")
        val manager = CallbackManager { callback -> callback.onFailure(failure) }

        val thrown = try {
            manager.sync()
            null
        } catch (error: Throwable) {
            error
        }

        assertEquals(failure.message, thrown?.message)
    }

    private class CallbackManager(
        private val onSync: (ProviderSyncCallback) -> Unit
    ) : ProviderManager {
        override val latestAppDirectory: File? = null
        override fun sync(callback: ProviderSyncCallback) = onSync(callback)
    }
}
