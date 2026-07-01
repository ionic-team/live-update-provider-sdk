package io.ionic.liveupdateprovider

/** An error thrown by a live update provider. */
sealed class ProviderError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    /** The provider received an invalid configuration. */
    class InvalidConfiguration(
        val details: String,
        cause: Throwable? = null
    ) : ProviderError("Invalid configuration: $details", cause)
}
