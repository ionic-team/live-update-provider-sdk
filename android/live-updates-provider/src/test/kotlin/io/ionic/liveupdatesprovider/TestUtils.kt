package io.ionic.liveupdatesprovider

import android.content.Context
import io.ionic.liveupdatesprovider.models.ProviderConfig

/**
 * Test provider implementation for use in unit tests.
 * This simple provider doesn't create actual managers and is used only for testing registry operations.
 */
class TestProviderImpl(override var id: String) : LiveUpdatesProvider {
    override fun createManager(
        context: Context,
        config: ProviderConfig
    ): LiveUpdatesManager {
        throw NotImplementedError("Test provider does not create managers")
    }
}
