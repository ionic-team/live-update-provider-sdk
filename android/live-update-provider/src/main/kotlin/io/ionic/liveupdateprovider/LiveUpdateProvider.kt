package io.ionic.liveupdateprovider

import android.content.Context

/** Creates managers for a live update provider. */
interface LiveUpdateProvider {
    /**
     * Creates a manager for a single app.
     *
     * @param context Android context for manager creation.
     * @param configuration Provider-specific configuration values.
     * @throws ProviderError.InvalidConfiguration when [configuration] is invalid.
     * @return A manager that can sync and resolve the app's latest files.
     */
    @Throws(ProviderError.InvalidConfiguration::class)
    fun createManager(
        context: Context,
        configuration: Map<String, Any>
    ): ProviderManager
}