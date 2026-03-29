import Foundation

/// Marker protocol for provider-defined sync results.
public protocol SyncResult {}

/// Manages live updates for a configured app instance.
public protocol LiveUpdateManaging: Sendable {
    /// Latest resolved app directory, if available.
    var latestAppDirectory: URL? { get }

    /// Performs a sync operation.
    ///
    /// - Returns: A provider-defined `SyncResult`.
    /// - Throws: An error if sync fails.
    func sync() async throws -> any SyncResult
}

/// Creates live update managers from provider-specific configuration.
public protocol LiveUpdateProviding: Sendable {
    /// Provider identifier used for registration and lookup.
    var id: String { get }
    
    /// Creates a manager for this provider.
    ///
    /// - Parameter config: Provider-specific configuration values.
    /// - Returns: A configured manager.
    /// - Throws: An error if creation fails.
    func createManager(
        config: [String: any Sendable]?
    ) throws -> any LiveUpdateManaging
}

/// Result shape used by providers integrating with Federated Capacitor.
public protocol FederatedCapacitorSyncResult: SyncResult {
    /// Indicates whether sync produced new app content.
    var didUpdate: Bool { get }

    /// Optional provider metadata from the sync operation.
    var metadata: [String: any Sendable]? { get }
}
