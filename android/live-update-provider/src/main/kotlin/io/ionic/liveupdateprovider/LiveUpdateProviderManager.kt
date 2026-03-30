package io.ionic.liveupdateprovider

import java.io.File

interface LiveUpdateProviderManager {
    var latestAppDirectory: File?
    fun sync(callback: ProviderSyncCallback?)
}

interface ProviderSyncCallback {
    fun onSuccess(result: ProviderSyncResult)
    fun onFailure(error: ProviderSyncError)
}

interface ProviderSyncResult

data class FederatedCapacitorSyncResult(
    val metadata: Map<String, Any>
): ProviderSyncResult

data class ProviderSyncError(
    val message: String
)
