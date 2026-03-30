package io.ionic.liveupdateprovider

import android.content.Context

interface LiveUpdateProvider {
    val id: String

    @Throws(LiveUpdateError.InvalidConfiguration::class)
    fun createManager(
        context: Context,
        config: Map<String, Any>?
    ): LiveUpdateProviderManager
}