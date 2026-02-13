package io.ionic.liveupdatesintegration.provider.models

import java.io.File

data class LiveUpdatesSyncResult(
    val activeAssetsChanged: Boolean,
    val activeAssetDirectory: File?
)
