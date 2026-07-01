package io.ionic.liveupdateprovider

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Performs a [ProviderManager.sync] operation using Kotlin coroutines.
 *
 * Throws whatever error is passed to [ProviderSyncCallback.onFailure].
 *
 * @return A sync result, or `null` when no update is available.
 */
suspend fun ProviderManager.sync(): ProviderSyncResult? =
    suspendCancellableCoroutine { continuation ->
        sync(object : ProviderSyncCallback {
            override fun onSuccess(result: ProviderSyncResult?) {
                if (continuation.isActive) continuation.resume(result)
            }

            override fun onFailure(error: Throwable) {
                if (continuation.isActive) continuation.resumeWithException(error)
            }
        })
    }
