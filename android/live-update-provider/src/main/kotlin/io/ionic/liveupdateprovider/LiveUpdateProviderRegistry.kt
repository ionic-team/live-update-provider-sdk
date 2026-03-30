package io.ionic.liveupdateprovider

import java.util.concurrent.ConcurrentHashMap


object LiveUpdateProviderRegistry {
    private val providers: ConcurrentHashMap<String, LiveUpdateProvider> = ConcurrentHashMap()


    @JvmStatic
    fun register(provider: LiveUpdateProvider) {
        if (provider.id.isBlank()) return
        providers.putIfAbsent(provider.id, provider)
    }

    @JvmStatic
    fun resolve(id: String): LiveUpdateProvider? = providers[id]

    @JvmStatic
    @Throws(LiveUpdateError.ProviderNotRegistered::class)
    fun require(id: String): LiveUpdateProvider {
        return resolve(id) ?: throw LiveUpdateError.ProviderNotRegistered(id)
    }
}
