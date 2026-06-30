import Foundation

/// Manages live update assets for a configured app instance.
public protocol ProviderManager {
    /// The latest app directory prepared by the provider, if one is available.
    var latestAppDirectory: URL? { get }

    /// Synchronizes provider-managed web assets.
    ///
    /// - Returns: A provider-defined result, or `nil` when there is nothing to report.
    /// - Throws: `ProviderError.syncFailed` when synchronization cannot complete.
    func sync() async throws -> (any ProviderSyncResult)?
}
