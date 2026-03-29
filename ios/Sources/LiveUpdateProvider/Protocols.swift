import Foundation

public protocol SyncResult {}

public protocol LiveUpdateManaging: Sendable {
    /// - Note: Implementors managing this property on a class should mark it
    ///   `nonisolated(unsafe)` to satisfy `Sendable` conformance.
    var latestAppDirectory: URL? { get }
    func sync() async throws -> any SyncResult
}

/// Factory protocol for creating live update managers from provider-specific configuration.
public protocol LiveUpdateProviding: Sendable {
    /// Stable identifier used to register and resolve this provider.
    var id: String { get }
    
    /// Creates a manager instance for this provider.
    ///
    /// - Parameter config: Provider-specific configuration values.
    /// - Returns: A configured manager that can perform sync operations.
    /// - Throws: An error if configuration is invalid or manager creation fails.
    func createManager(
        config: [String: any Sendable]?
    ) throws -> any LiveUpdateManaging
}

public protocol FederatedCapacitorSyncResult: SyncResult {
    var didUpdate: Bool { get }
    var metadata: [String: any Sendable]? { get }
}
