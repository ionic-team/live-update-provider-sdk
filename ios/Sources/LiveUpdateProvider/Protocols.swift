import Foundation

// MARK: - Shared Contracts (Portals + Federated Capacitor)

/// Marker protocol for provider-defined sync results.
public protocol LiveUpdateProviderSyncResult {}

/// Manages live update synchronization for a configured app instance.
public protocol LiveUpdateProviderManager {
    /// Latest resolved app directory, if available.
    ///
    /// When sync prepares a new bundle for host runtime use, this value should
    /// be updated before `sync()` returns success.
    var latestAppDirectory: URL? { get }

    /// Performs a sync operation.
    ///
    /// - Returns: A provider-defined sync result.
    /// - Throws: An error when sync cannot complete.
    func sync() async throws -> any LiveUpdateProviderSyncResult
}

// MARK: - Federated Capacitor Contracts

/// Creates live update managers from provider-specific configuration.
public protocol LiveUpdateProvider {
    /// Provider identifier used for registration and runtime lookup.
    var id: String { get }

    /// Creates a manager for this provider.
    ///
    /// - Parameter config: Provider-specific configuration values.
    /// - Returns: A configured manager.
    /// - Throws: An error when configuration is invalid or manager creation fails.
    func createManager(config: [String: Any]) throws -> any LiveUpdateProviderManager
}

/// Sync result that includes provider metadata.
///
/// Federated Capacitor can expose this metadata to JavaScript after a provider sync.
/// Metadata intended for JavaScript exposure should use bridge-safe values.
public protocol MetadataSyncResult: LiveUpdateProviderSyncResult {
    /// Provider metadata returned with the sync result.
    var metadata: [String: Any]? { get }
}

/// Default `MetadataSyncResult` implementation.
public struct DefaultMetadataSyncResult: MetadataSyncResult {
    /// Provider metadata returned with the sync result.
    public let metadata: [String: Any]?

    /// Creates a metadata sync result.
    ///
    /// - Parameter metadata: Optional bridge-safe metadata for host integrations.
    public init(metadata: [String: Any]? = nil) {
        self.metadata = metadata
    }
}
