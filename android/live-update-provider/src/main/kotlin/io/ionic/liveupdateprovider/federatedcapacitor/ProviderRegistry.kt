package io.ionic.liveupdateprovider.federatedcapacitor

import io.ionic.liveupdateprovider.ProviderError
import java.util.concurrent.ConcurrentHashMap

/** Thread-safe registry of [LiveUpdateProvider] implementations. */
object ProviderRegistry {
    private val providers: ConcurrentHashMap<String, LiveUpdateProvider> = ConcurrentHashMap()

    /**
     * Registers a provider by [LiveUpdateProvider.id].
     *
     * @throws ProviderError.InvalidConfiguration when the provider ID is empty
     * or when another provider is already registered with the same ID.
     */
    @JvmStatic
    @Throws(ProviderError.InvalidConfiguration::class)
    fun register(provider: LiveUpdateProvider) {
        if (provider.id.isBlank()) {
            throw ProviderError.InvalidConfiguration(
                "Cannot register a provider with an empty ID."
            )
        }

        val previous = providers.putIfAbsent(provider.id, provider)
        if (previous != null) {
            throw ProviderError.InvalidConfiguration(
                "Provider with ID '${provider.id}' is already registered."
            )
        }
    }

    /** Returns the provider registered for [id], or null when missing. */
    @JvmStatic
    fun resolve(id: String): LiveUpdateProvider? = providers[id]

    /**
     * Returns the provider registered for [id].
     *
     * @throws ProviderError.ProviderNotRegistered when missing.
     */
    @JvmStatic
    @Throws(ProviderError.ProviderNotRegistered::class)
    fun require(id: String): LiveUpdateProvider {
        return resolve(id) ?: throw ProviderError.ProviderNotRegistered(id)
    }
}
