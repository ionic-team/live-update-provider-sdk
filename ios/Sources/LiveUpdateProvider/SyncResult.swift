/// Marker protocol for provider-defined sync results.
public protocol ProviderSyncResult {}

/// A sync result carrying metadata for Federated Capacitor.
public struct MetadataSyncResult: ProviderSyncResult {
    /// Bridge-safe metadata returned with the sync result.
    public let metadata: [String: Any]

    public init(metadata: [String: Any]) {
        self.metadata = metadata
    }
}
