package io.ionic.liveupdatesprovider.provider.models

import java.io.File

data class LiveUpdatesSyncResult(
    val activeAssetsChanged: Boolean,
    val activeAssetDirectory: File?
)
