package io.ionic.liveupdateprovider.federatedcapacitor

import android.content.Context
import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager

/**
 * Creates managers from provider-specific configuration, for Federated Capacitor
 * provider registration and lookup.
 */
interface LiveUpdateProvider {
    /** Identifier used for registration and runtime lookup. */
    val id: String

    /**
     * Creates a manager from provider-specific configuration.
     *
     * @param context application context recommended.
     * @throws ProviderError.InvalidConfiguration when [config] is invalid.
     */
    @Throws(ProviderError.InvalidConfiguration::class)
    fun createManager(
        context: Context,
        config: Map<String, Any>
    ): ProviderManager
}
