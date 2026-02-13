package io.ionic.liveupdatesintegration.provider

import android.content.Context
import io.ionic.liveupdatesintegration.provider.models.LiveUpdatesOptions
import io.ionic.liveupdatesintegration.provider.models.LiveUpdatesProviderConfig

/**
 * Provider interface for Live Updates implementations.
 * Providers are responsible for creating manager instances for configured apps.
 */
interface LiveUpdatesProvider {
    /**
     * Create a manager instance for the specified app configuration.
     * @param context Android context
     * @param config Provider-specific configuration
     * @param options Live Updates options (e.g., autoUpdateMethod)
     * @return LiveUpdatesManager instance
     * @throws IllegalArgumentException if config is invalid
     */
    fun createManager(
        context: Context,
        config: LiveUpdatesProviderConfig,
        options: LiveUpdatesOptions
    ): LiveUpdatesManager
}


