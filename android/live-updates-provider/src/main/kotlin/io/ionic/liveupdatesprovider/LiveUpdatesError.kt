package io.ionic.liveupdatesprovider

sealed interface LiveUpdatesError {


    class ProviderNotRegistered(val providerId: String) :
        Exception("Provider with ID '$providerId' not found"), LiveUpdatesError


    class InvalidConfiguration(val details: String, cause: Throwable? = null) :
        Exception("Invalid configuration: $details", cause), LiveUpdatesError

    class SyncFailed(val details: String, cause: Throwable? = null) :
        Exception("Sync failed: $details", cause), LiveUpdatesError
}
