package io.ionic.liveupdateprovider

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * A [ProviderManager] for Kotlin implementations that perform sync as a suspend function.
 *
 * Implement [performSync]; this class bridges it to the callback-based [ProviderManager.sync].
 */
abstract class CoroutineProviderManager(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : ProviderManager {
    /** Performs the sync; return `null` when there is nothing to report. */
    abstract suspend fun performSync(): ProviderSyncResult?

    final override fun sync(callback: ProviderSyncCallback) {
        scope.launch {
            try {
                callback.onSuccess(performSync())
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                callback.onFailure(error)
            }
        }
    }
}
