package io.ionic.liveupdateprovider

import android.util.Log
import java.util.concurrent.ConcurrentHashMap


object LiveUpdateProviderRegistry {
    private val providers: ConcurrentHashMap<String, LiveUpdateProvider> = ConcurrentHashMap()


    @JvmStatic
    fun register(provider: LiveUpdateProvider) {
        if (provider.id.isBlank()) {
            Log.w("LiveUpdateProviderRegistry", "Cannot register a provider with an empty ID")
            return
        }
        val previous = providers.putIfAbsent(provider.id, provider)
        if (previous != null) {
            Log.w("LiveUpdateProviderRegistry", "Provider with ID '${provider.id}' is already registered. Ignoring subsequent registration.")
            return
        }
    }

    @JvmStatic
    fun resolve(providerId: String): LiveUpdateProvider? {
        return providers[providerId]
    }

    @JvmStatic
    @Throws(LiveUpdateError.ProviderNotRegistered::class)
    fun require(providerId: String): LiveUpdateProvider {
        return resolve(providerId)
            ?: throw LiveUpdateError.ProviderNotRegistered(providerId)
    }

    internal fun clear() {
        providers.clear()
    }
}
