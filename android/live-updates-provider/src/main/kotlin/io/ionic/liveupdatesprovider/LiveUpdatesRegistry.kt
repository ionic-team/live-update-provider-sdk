package io.ionic.liveupdatesprovider

import android.util.Log
import java.util.concurrent.ConcurrentHashMap


object LiveUpdatesRegistry {
    private val providers: ConcurrentHashMap<String, LiveUpdatesProvider> = ConcurrentHashMap()


    @JvmStatic
    fun register(provider: LiveUpdatesProvider) {
        if (provider.id.isBlank()) {
            Log.w("LiveUpdatesRegistry", "Cannot register a provider with an empty ID")
            return
        }
        val previous = providers.putIfAbsent(provider.id, provider)
        if (previous != null) {
            Log.w("LiveUpdatesRegistry", "Provider with ID '${provider.id}' is already registered. Ignoring subsequent registration.")
            return
        }
    }

    @JvmStatic
    fun resolve(providerId: String): LiveUpdatesProvider? {
        return providers[providerId]
    }

    @JvmStatic
    @Throws(LiveUpdatesError.ProviderNotRegistered::class)
    fun require(providerId: String): LiveUpdatesProvider {
        return resolve(providerId)
            ?: throw LiveUpdatesError.ProviderNotRegistered(providerId)
    }

    internal fun clear() {
        providers.clear()
    }
}
