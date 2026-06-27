package io.ionic.liveupdateprovider.coroutines

import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.ProviderSyncCallback
import io.ionic.liveupdateprovider.ProviderSyncResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Suspending form of [ProviderManager.sync] — mirrors iOS's `async` sync for Kotlin callers.
 *
 * @return a provider-defined result, or `null` when there is nothing to report
 * @throws ProviderError.SyncFailed when sync fails
 */
suspend fun ProviderManager.sync(): ProviderSyncResult? =
    suspendCancellableCoroutine { continuation ->
        sync(object : ProviderSyncCallback {
            override fun onSuccess(result: ProviderSyncResult?) {
                if (continuation.isActive) continuation.resume(result)
            }

            override fun onFailure(error: ProviderError.SyncFailed) {
                if (continuation.isActive) continuation.resumeWithException(error)
            }
        })
    }

/**
 * Base [ProviderManager] for providers that implement sync as a coroutine.
 *
 * Implement [performSync]; the callback contract is satisfied automatically. Cancelling
 * [scope] does not stop in-flight work — the callback contract has no cancel signal.
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
            } catch (error: ProviderError.SyncFailed) {
                callback.onFailure(error)
            } catch (error: Throwable) {
                callback.onFailure(
                    ProviderError.SyncFailed(error.message ?: "Sync failed", error)
                )
            }
        }
    }
}
