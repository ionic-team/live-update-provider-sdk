package io.ionic.liveupdateprovider

import java.io.File

/** Manages a provider's live updates for a single app. */
interface ProviderManager {
    /** The directory containing the latest app files on disk. */
    val latestAppDirectory: File?

    /** Checks for updates and prepares the latest app files. */
    fun sync(callback: ProviderSyncCallback)
}

/** Receives the result of a provider sync operation. */
interface ProviderSyncCallback {
    /** Called when the sync operation completes successfully. */
    fun onSuccess(result: ProviderSyncResult?)

    /** Called when the sync operation fails. */
    fun onFailure(error: Throwable)
}
