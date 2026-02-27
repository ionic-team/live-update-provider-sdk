package io.ionic.liveupdatesprovider.models


data class ProviderConfig(
    val data: Map<String, Any>
) {
    inline fun <reified T> value(key: String): T? {
        return data[key] as? T
    }

    fun <T> value(key: String, type: Class<T>): T? = type.cast(data[key])
}
