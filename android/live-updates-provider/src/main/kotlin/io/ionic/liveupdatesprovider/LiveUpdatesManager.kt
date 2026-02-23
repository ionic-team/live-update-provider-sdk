package io.ionic.liveupdatesprovider

import io.ionic.liveupdatesprovider.models.SyncResult
import java.io.File

interface SyncCallback {

    fun onComplete(result: SyncResult)

    fun onError(error: LiveUpdatesError.SyncFailed)
}


interface LiveUpdatesManager {

    fun sync(callback: SyncCallback)

    fun latestAppDirectory(): File?

}
