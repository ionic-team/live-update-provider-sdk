package io.ionic.liveupdates.provider.models

import java.io.File

data class LiveUpdatesSyncResult(
    val activeAssetsChanged: Boolean,
    val activeAssetDirectory: File?
)
