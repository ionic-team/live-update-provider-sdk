import Foundation

// MARK: - Shared Contracts (Portals + Federated Capacitor)

/// Marker protocol for provider-defined sync results.
public protocol SyncResult {}

/// Manages live updates for a configured app instance.
public protocol LiveUpdateManaging {
    /// Latest resolved app directory, if available.
    /// This should reflect the currently active web bundle path.
    var latestAppDirectory: URL? { get }

    /// Performs a sync operation.
    ///
    /// - Returns: A provider-defined sync result.
    /// - Throws: An error when sync cannot complete.
    func sync() async throws -> any SyncResult
}

// MARK: - Federated Capacitor Contracts

/// Creates live update managers from provider-specific configuration.
public protocol LiveUpdateProviding {
    /// Provider identifier used for registration and runtime lookup.
    var id: String { get }

    /// Creates a manager for this provider.
    ///
    /// - Parameter config: Provider-specific configuration values.
    /// - Returns: A configured manager.
    /// - Throws: An error when configuration is invalid or manager creation fails.
    func createManager(config: [String: Any]) throws -> any LiveUpdateManaging
}

/// Optional sync result extension for Federated Capacitor metadata bridging.
public protocol FederatedCapacitorSyncResult: SyncResult {
    /// Provider metadata from the sync operation to bridge to the web layer.
    var metadata: [String: Any]? { get }
}

/// Default `FederatedCapacitorSyncResult` implementation.
public struct DefaultFederatedCapacitorSyncResult: FederatedCapacitorSyncResult {
    /// Optional provider metadata for the web layer.
    public let metadata: [String: Any]?

    /// Creates a default Federated Capacitor sync result.
    public init(metadata: [String: Any]? = nil) {
        self.metadata = metadata
    }
}
