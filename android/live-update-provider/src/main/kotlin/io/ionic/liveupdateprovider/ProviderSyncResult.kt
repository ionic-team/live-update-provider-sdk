package io.ionic.liveupdateprovider

/** Marker interface for provider-defined sync results. */
interface ProviderSyncResult

/**
 * A sync result carrying metadata for the Federated Capacitor bridge.
 *
 * @param metadata bridge-safe metadata returned with the sync result
 */
data class MetadataSyncResult(
    val metadata: Map<String, Any>
) : ProviderSyncResult
