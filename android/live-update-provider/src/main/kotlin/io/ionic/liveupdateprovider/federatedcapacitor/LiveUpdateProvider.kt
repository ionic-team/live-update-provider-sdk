package io.ionic.liveupdateprovider.federatedcapacitor

import android.content.Context
import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager

/** Creates managers for providers registered with Federated Capacitor. */
interface LiveUpdateProvider {
    /** Identifier used for provider registration and lookup. */
    val id: String

    /**
     * Creates a manager from provider-specific configuration.
     *
     * @param context Android context for manager creation.
     * @param config provider-specific configuration.
     * @throws ProviderError.InvalidConfiguration when [config] is invalid.
     */
    @Throws(ProviderError.InvalidConfiguration::class)
    fun createManager(
        context: Context,
        config: Map<String, Any>
    ): ProviderManager
}
