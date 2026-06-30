package io.ionic.liveupdateprovider

/** Errors reported by provider registration, configuration, and sync operations. */
sealed class ProviderError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    /** Required configuration is missing or invalid. */
    class InvalidConfiguration(
        val details: String,
        cause: Throwable? = null
    ) : ProviderError("Invalid configuration: $details", cause)

    /** Sync could not complete. */
    class SyncFailed(
        val details: String,
        cause: Throwable? = null
    ) : ProviderError("Sync failed: $details", cause)

    /** No provider is registered for the requested identifier. */
    class ProviderNotRegistered(val providerId: String) :
        ProviderError("Live update provider '$providerId' is not registered.")
}
