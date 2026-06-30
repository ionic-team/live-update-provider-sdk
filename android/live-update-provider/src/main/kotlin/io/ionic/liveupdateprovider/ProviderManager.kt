package io.ionic.liveupdateprovider

import java.io.File

/** Manages live update assets for a configured app instance. */
interface ProviderManager {
    /** The latest app directory prepared by the provider, if one is available. */
    val latestAppDirectory: File?

    /**
     * Synchronizes provider-managed web assets.
     *
     * Calls [ProviderSyncCallback.onSuccess] or [ProviderSyncCallback.onFailure].
     */
    fun sync(callback: ProviderSyncCallback)
}

/** Receives the outcome of [ProviderManager.sync]. */
interface ProviderSyncCallback {
    /** Called when synchronization succeeds, with `null` when there is no result to report. */
    fun onSuccess(result: ProviderSyncResult?)

    /** Called when synchronization fails. */
    fun onFailure(error: ProviderError.SyncFailed)
}
