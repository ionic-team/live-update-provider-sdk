package io.ionic.liveupdatesprovider

import android.content.Context
import io.ionic.liveupdatesprovider.models.ProviderConfig

/**
 * Provider interface for Live Updates implementations.
 * Providers are responsible for creating manager instances for configured apps.
 */
interface LiveUpdatesProvider {
    var id: String

    /**
     * Create a manager instance for the specified app configuration.
     * @param context Android context
     * @param config Provider-specific configuration
     * @return LiveUpdatesManager instance
     * @throws IllegalArgumentException if config is invalid
     */
    fun createManager(
        context: Context,
        config: ProviderConfig
    ): LiveUpdatesManager
}
