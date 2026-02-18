package io.ionic.liveupdatesprovider

import io.ionic.liveupdatesprovider.models.SyncResult
import java.io.File

/**
 * Manager interface for per-app Live Updates operations.
 * Each manager instance is responsible for one configured app.
 */
interface LiveUpdatesManager {
    /**
     * Perform sync operation (check for updates, download, activate).
     * @return SyncResult indicating success and whether active assets changed
     */
    suspend fun sync(): SyncResult

    /**
     * Get the active asset directory for this app.
     * @return File pointing to the active web assets directory, or null if no assets available
     */
    fun latestAppDirectory(): File?

}
