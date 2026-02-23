package io.ionic.liveupdatesprovider

import android.content.Context
import io.ionic.liveupdatesprovider.models.ProviderConfig

interface LiveUpdatesProvider {
    val id: String


    @Throws(LiveUpdatesError.InvalidConfiguration::class)
    fun createManager(
        context: Context,
        config: ProviderConfig
    ): LiveUpdatesManager
}
