package io.ionic.liveupdateprovider.coroutines

import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.ProviderSyncCallback
import io.ionic.liveupdateprovider.ProviderSyncResult
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
