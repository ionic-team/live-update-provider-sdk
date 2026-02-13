package io.ionic.liveupdatesintegration.provider

import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe registry for Live Updates provider registration and lookup.
 */
object LiveUpdatesRegistry {
    private val providers: ConcurrentHashMap<String, LiveUpdatesProvider> = ConcurrentHashMap()
    const val DEFAULT_PROVIDER_ID = "ionic"

    /**
     * Register a provider with the given ID.
     * @param providerId Unique provider identifier
     * @param provider Provider implementation
     * @throws IllegalArgumentException if provider ID is empty or already registered
     */
    fun register(providerId: String, provider: LiveUpdatesProvider) {
        if (providerId.isBlank()) {
            throw IllegalArgumentException("Provider ID cannot be empty or blank")
        }
        if (isRegistered(providerId)) {
            throw IllegalArgumentException("Provider with ID '$providerId' is already registered")
        }
        providers[providerId] = provider
    }

    /**
     * Resolve a provider by ID.
     * @param providerId Provider identifier
     * @return Provider implementation, or null if not found
     */
    fun resolve(providerId: String): LiveUpdatesProvider? {
        return providers[providerId];
    }

    /**
     * Require a provider by ID.
     * @param providerId Provider identifier
     * @return Provider implementation
     * @throws IllegalArgumentException if provider not found
     */
    fun require(providerId: String): LiveUpdatesProvider {
        return providers[providerId]
            ?: throw IllegalArgumentException("Provider with ID '$providerId' not found")
    }

    /**
     * Resolve a provider by ID, or return the default provider if providerId is null.
     * @param providerId Provider identifier, or null to use default
     * @return Provider implementation, or null if not found
     */
    fun resolveOrDefault(providerId: String?): LiveUpdatesProvider? {
        return if (providerId == null) {
            resolve(DEFAULT_PROVIDER_ID)
        } else {
            resolve(providerId) ?: resolve(DEFAULT_PROVIDER_ID)
        }
    }

    /**
     * Check if a provider is registered.
     * @param providerId Provider identifier
     * @return true if provider is registered
     */
    fun isRegistered(providerId: String): Boolean {
        return providers.containsKey(providerId)
    }

    /**
     * Clear all registered providers. For testing purposes only.
     * @internal
     */
    internal fun clear() {
        providers.clear()
    }
}
