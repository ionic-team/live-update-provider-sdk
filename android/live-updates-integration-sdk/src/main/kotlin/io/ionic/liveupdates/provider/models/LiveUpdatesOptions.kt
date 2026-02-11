package io.ionic.liveupdates.provider.models

data class LiveUpdatesOptions(
    val autoUpdateMethod: AutoUpdateMethod
) {
    enum class AutoUpdateMethod {
        BACKGROUND,
        ALWAYS_LATEST,
        FORCE_UPDATE,
        NONE
    }
}
