package io.ionic.liveupdateprovider

/**
 * A provider's sync result.
 *
 * Implement on a custom type to return native data to a direct (Portals) caller.
 */
interface ProviderSyncResult

/**
 * A sync result carrying metadata for the Federated Capacitor bridge.
 *
 * @param metadata bridge-safe values to expose to the web layer: strings, numbers,
 * booleans, nulls, lists, and maps.
 */
data class MetadataSyncResult(
    val metadata: Map<String, Any>
) : ProviderSyncResult
