package io.ionic.liveupdateprovider

import android.content.Context

/**
 * Test provider implementation for use in unit tests.
 * This simple provider doesn't create actual managers and is used only for testing registry operations.
 */
class TestProviderImpl(override var id: String) : LiveUpdateProvider {
    override fun createManager(
        context: Context,
        config: Map<String, Any>?
    ): LiveUpdateManager {
        throw NotImplementedError("Test provider does not create managers")
    }
}
