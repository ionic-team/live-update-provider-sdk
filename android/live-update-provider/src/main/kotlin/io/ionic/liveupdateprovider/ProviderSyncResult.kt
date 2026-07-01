package io.ionic.liveupdateprovider

/** A provider-specific result from a sync operation. */
interface ProviderSyncResult

/**
 * A sync result containing provider metadata.
 *
 * @param metadata Metadata containing JSON-serializable values.
 */
data class MetadataSyncResult(
    val metadata: Map<String, Any>
) : ProviderSyncResult
