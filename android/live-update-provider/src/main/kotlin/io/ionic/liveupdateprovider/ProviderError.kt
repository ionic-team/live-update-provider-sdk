package io.ionic.liveupdateprovider

/**
 * Errors used by the Live Update Provider SDK.
 *
 * The registry emits [ProviderNotRegistered]. Providers use [InvalidConfiguration]
 * when creating a manager and [SyncFailed] during sync.
 */
sealed interface ProviderError {
    /** Required configuration is missing or invalid. */
    class InvalidConfiguration(
        val details: String,
        cause: Throwable? = null
    ) : Exception("Invalid configuration: $details", cause), ProviderError

    /** Sync could not complete. */
    class SyncFailed(
        val details: String,
        cause: Throwable? = null
    ) : Exception("Sync failed: $details", cause), ProviderError

    /** No provider is registered for the requested identifier. */
    class ProviderNotRegistered(val providerId: String) :
        Exception("Live update provider '$providerId' is not registered."), ProviderError
}
