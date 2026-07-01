/// A provider-specific result from a sync operation.
public protocol ProviderSyncResult {}

/// A sync result containing provider metadata.
public struct MetadataSyncResult: ProviderSyncResult {
    /// Metadata containing JSON-serializable values.
    public let metadata: [String: Any]

    public init(metadata: [String: Any]) {
        self.metadata = metadata
    }
}
