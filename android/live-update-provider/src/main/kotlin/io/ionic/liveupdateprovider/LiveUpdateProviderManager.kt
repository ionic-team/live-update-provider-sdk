package io.ionic.liveupdateprovider

import java.io.File

/**
 * Manages live update sync for a configured app instance.
 */
interface LiveUpdateProviderManager {
    /**
     * Latest resolved app directory, if available.
     *
     * When sync prepares a new bundle for host runtime use, this value should
     * be updated before signaling success.
     */
    val latestAppDirectory: File?

    /**
     * Providers should call exactly one terminal callback per invocation:
     * [LiveUpdateProviderSyncCallback.onSuccess] or
     * [LiveUpdateProviderSyncCallback.onFailure]. The SDK does not require a
     * specific callback thread.
     *
     * @param callback callback receiving either success or failure
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
 * Sync result that includes provider metadata.
 *
 * Federated Capacitor can expose this metadata to JavaScript after a provider sync.
 * Metadata intended for JavaScript exposure should use bridge-safe values.
 */
interface MetadataSyncResult : LiveUpdateProviderSyncResult {
    /** Provider metadata returned with the sync result. */
    val metadata: Map<String, Any>?
}

/**
 * Default [MetadataSyncResult] implementation.
 *
 * @param metadata optional bridge-safe metadata for host integrations
 */
data class DefaultMetadataSyncResult(
    override val metadata: Map<String, Any>? = null
) : MetadataSyncResult
