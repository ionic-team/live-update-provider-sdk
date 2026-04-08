package io.ionic.liveupdateprovider

import android.content.Context

/**
 * Creates [LiveUpdateProviderManager] instances from provider-specific configuration.
 *
 * This contract is used for Federated Capacitor provider registration, lookup,
 * and manager creation.
 */
interface LiveUpdateProvider {
    /** Provider identifier used for registration and runtime lookup. */
    val id: String

    /**
     * Creates a manager for this provider.
     *
     * @param context Android context used by the provider (application context recommended).
     * @param config Provider-specific configuration required to build a manager.
     * @return A configured manager instance.
     * @throws LiveUpdateProviderError.InvalidConfiguration when configuration is invalid
     * or manager creation fails.
     */
    @Throws(LiveUpdateProviderError.InvalidConfiguration::class)
    fun createManager(
        context: Context,
        config: Map<String, Any>
    ): LiveUpdateProviderManager
}
