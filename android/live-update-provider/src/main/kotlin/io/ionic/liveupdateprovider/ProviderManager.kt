package io.ionic.liveupdateprovider

import java.io.File

/** Manages a provider's live updates for a single app. */
interface ProviderManager {
    /** The directory containing the latest app files on disk. */
    val latestAppDirectory: File?

    /**
     * Checks for updates and prepares the latest app files.
     *
     * Implementations should perform blocking work via an appropriate dispatcher
     * (e.g. `withContext(Dispatchers.IO)`) rather than blocking the calling coroutine.
     *
     * @return A sync result, or `null` when no update is available.
     */
    suspend fun sync(): ProviderSyncResult?
}
