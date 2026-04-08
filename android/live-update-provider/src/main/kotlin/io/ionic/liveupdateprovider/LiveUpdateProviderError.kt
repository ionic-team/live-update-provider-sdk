package io.ionic.liveupdateprovider

/**
 * Errors used by the Live Update Provider SDK.
 *
 * [ProviderNotRegistered] is emitted by the SDK runtime.
 * [InvalidConfiguration] and [SyncFailed] are intended for provider implementations.
 */
sealed interface LiveUpdateProviderError {
    /**
     * Provider configuration is missing required values or contains invalid values.
     *
     * Recommended for provider implementations during manager creation.
     */
    class InvalidConfiguration(
        val details: String,
        cause: Throwable? = null
    ) : Exception("Invalid configuration: $details", cause), LiveUpdateProviderError

    /**
     * A sync operation failed before completion.
     *
     * Recommended for provider implementations during sync operations.
     */
    class SyncFailed(
        val details: String,
        cause: Throwable? = null
    ) : Exception("Sync failed: $details", cause), LiveUpdateProviderError

    /** No provider is registered for the requested provider identifier. */
    class ProviderNotRegistered(val providerId: String) :
        Exception("Provider with ID '$providerId' not found"), LiveUpdateProviderError
}
