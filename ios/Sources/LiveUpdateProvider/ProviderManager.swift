import Foundation

/// Manages a provider's live updates for a single app.
public protocol ProviderManager {
    /// The directory containing the latest app files on disk.
    var latestAppDirectory: URL? { get }

    /// Checks for updates and prepares the latest app files.
    ///
    /// - Returns: A sync result, or `nil` when no update is available.
    func sync() async throws -> (any ProviderSyncResult)?
}
