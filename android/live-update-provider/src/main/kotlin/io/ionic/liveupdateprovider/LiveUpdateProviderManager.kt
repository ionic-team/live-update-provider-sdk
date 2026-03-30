package io.ionic.liveupdateprovider

import java.io.File

interface LiveUpdateProviderManager {
    var latestAppDirectory: File?
    fun sync(callback: ProviderSyncCallback?)
}

interface ProviderSyncCallback {
    fun onSuccess(result: ProviderSyncResult)
    fun onFailure(error: LiveUpdateError.SyncFailed)
}

interface ProviderSyncResult

data class FederatedCapacitorSyncResult(
    val didUpdate: Boolean,
    val metadata: Map<String, Any>
): ProviderSyncResult
