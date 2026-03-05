package io.ionic.liveupdateprovider

import java.io.File

interface SyncCallback {

    fun onComplete(result: SyncResult)

    fun onError(error: LiveUpdateError.SyncFailed)
}


interface LiveUpdateManager {
    fun sync(callback: SyncCallback? = null)

    val latestAppDirectory: File?
}
