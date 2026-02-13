package io.ionic.liveupdatesintegration.provider.models

/**
 * Opaque provider configuration passed from hosts to providers.
 * Providers validate their own config requirements.
 */
data class LiveUpdatesProviderConfig(
    val values: Map<String, LiveUpdatesConfigValue>
)

/**
 * Sealed class representing config value types.
 */
sealed class LiveUpdatesConfigValue {
    data class StringValue(val value: String) : LiveUpdatesConfigValue()
    data class NumberValue(val value: Double) : LiveUpdatesConfigValue()
    data class BoolValue(val value: Boolean) : LiveUpdatesConfigValue()
    data class ObjectValue(val value: Map<String, LiveUpdatesConfigValue>) : LiveUpdatesConfigValue()
}
