package io.ionic.liveupdateprovider

import java.io.File

/** Syncs live update assets for a configured app instance. */
interface ProviderManager {
    /**
     * The latest app directory the provider has prepared, if any.
     *
     * Update this before signaling success when a new bundle is ready.
     */
    val latestAppDirectory: File?

    /**
     * Syncs provider-managed assets.
     *
     * Call exactly one terminal callback per invocation: [ProviderSyncCallback.onSuccess]
     * or [ProviderSyncCallback.onFailure]. The SDK does not require a specific thread.
     */
    fun sync(callback: ProviderSyncCallback)
}

/** Receives the outcome of [ProviderManager.sync]. */
interface ProviderSyncCallback {
    /** Called on success, with a provider-defined result or `null` when there is nothing to report. */
    fun onSuccess(result: ProviderSyncResult?)

    /** Called when sync fails. */
    fun onFailure(error: ProviderError.SyncFailed)
}
