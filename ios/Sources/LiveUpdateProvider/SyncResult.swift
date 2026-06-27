import Foundation

/// A provider's sync result.
///
/// Conform a custom type to return native data to a direct (Portals) caller.
public protocol ProviderSyncResult {}

/// A sync result carrying metadata for the Federated Capacitor bridge.
public struct MetadataSyncResult: ProviderSyncResult {
    /// Bridge-safe metadata to expose to the web layer: strings, numbers, booleans,
    /// nulls, arrays, and dictionaries.
    public let metadata: [String: Any]

    public init(metadata: [String: Any]) {
        self.metadata = metadata
    }
}
