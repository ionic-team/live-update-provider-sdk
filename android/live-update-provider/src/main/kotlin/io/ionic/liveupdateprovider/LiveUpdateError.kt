package io.ionic.liveupdateprovider

sealed interface LiveUpdateError {
    class InvalidConfiguration(
        val details: String,
        cause: Throwable? = null
    ) : Exception("Invalid configuration: $details", cause), LiveUpdateError

    class SyncFailed(
        val details: String,
        cause: Throwable? = null
    ) : Exception("Sync failed: $details", cause), LiveUpdateError

    class ProviderNotRegistered(val providerId: String) :
        Exception("Provider with ID '$providerId' not found"), LiveUpdateError
}
