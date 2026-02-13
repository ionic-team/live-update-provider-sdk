package io.ionic.liveupdatesprovider.provider

import io.ionic.liveupdatesprovider.provider.models.LiveUpdatesSyncResult
import java.io.File

/**
 * Manager interface for per-app Live Updates operations.
 * Each manager instance is responsible for one configured app.
 */
interface LiveUpdatesManager {
    /**
     * Perform sync operation (check for updates, download, activate).
     * @return LiveUpdatesSyncResult indicating success and whether active assets changed
     */
    suspend fun sync(): LiveUpdatesSyncResult

    /**
     * Get the active asset directory for this app.
     * @return File pointing to the active web assets directory, or null if no assets available
     */
    fun currentAssetDirectory(): File?

    /**
     * Cancel any running sync operation for this app.
     */
    fun cancelSync()
}
