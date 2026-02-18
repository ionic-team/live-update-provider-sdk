package io.ionic.liveupdatesprovider.models

import java.io.File

data class SyncResult(
    val didUpdate: Boolean,
    val latestAppDirectory: File?
)
