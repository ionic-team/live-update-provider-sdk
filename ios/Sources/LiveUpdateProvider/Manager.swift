import Foundation

/// Syncs live update assets for a configured app instance.
public protocol ProviderManager {
    /// The latest app directory the provider has prepared, if any.
    ///
    /// Update this before `sync()` returns when a new bundle is ready.
    var latestAppDirectory: URL? { get }

    /// Syncs provider-managed assets.
    ///
    /// - Returns: A provider-defined result, or `nil` when there is nothing to report.
    /// - Throws: An error when sync cannot complete.
    func sync() async throws -> (any ProviderSyncResult)?
}
