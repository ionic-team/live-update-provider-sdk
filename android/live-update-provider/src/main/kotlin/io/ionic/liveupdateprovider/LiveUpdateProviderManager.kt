package io.ionic.liveupdateprovider

import java.io.File

/**
 * Manages live updates for a configured app instance.
 */
interface LiveUpdateProviderManager {
    /**
     * Latest resolved app directory, if available.
     *
     * This should reflect the currently active web bundle path for the manager.
     */
    val latestAppDirectory: File?

    /**
     * Performs a sync operation.
     *
     * Providers should update [latestAppDirectory] before signaling success when
     * new assets are applied.
     *
     * @param callback Callback receiving either success or failure.
     */
    fun sync(callback: LiveUpdateProviderSyncCallback?)
}

/** Callback for sync completion or failure. */
interface LiveUpdateProviderSyncCallback {
    /** Called when sync completes successfully with a provider-defined result. */
    fun onSuccess(result: LiveUpdateProviderSyncResult)

    /** Called when sync fails before completion. */
    fun onFailure(error: LiveUpdateProviderError.SyncFailed)
}

/** Marker interface for provider-defined sync results. */
interface LiveUpdateProviderSyncResult

/**
 * Optional sync result extension for Federated Capacitor metadata bridging.
 */
data class FederatedCapacitorSyncResult(
    /** Provider metadata from the sync operation to bridge to the web layer. */
    val metadata: Map<String, Any>?
): LiveUpdateProviderSyncResult
