package io.ionic.liveupdatesprovider

import android.util.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe registry for Live Updates provider registration and lookup.
 */
object LiveUpdatesRegistry {
    private val providers: ConcurrentHashMap<String, LiveUpdatesProvider> = ConcurrentHashMap()

    /**
     * Register a provider with the given ID.
     * @param provider Provider implementation
     */
    fun register(provider: LiveUpdatesProvider) {
        val providerId = provider.id
        val msg = when {
            providerId.isBlank() -> "Provider ID cannot be empty"
            isRegistered(providerId) -> "Provider with ID '$providerId' is already registered"
            else -> null
        }
        if (msg != null) {
            Log.w("LiveUpdatesRegistry", "$msg, ignoring registration")
            return
        }
        providers[providerId] = provider
    }

    /**
     * Resolve a provider by ID.
     * @param providerId Provider identifier
     * @return Provider implementation, or null if not found
     */
    fun resolve(providerId: String): LiveUpdatesProvider? {
        return providers[providerId]
    }

    /**
     * Require a provider by ID.
     * @param providerId Provider identifier
     * @return Provider implementation
     * @throws IllegalArgumentException if provider not found
     */
    fun require(providerId: String): LiveUpdatesProvider {
        return resolve(providerId)
            ?: throw IllegalArgumentException("Provider with ID '$providerId' not found")
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
