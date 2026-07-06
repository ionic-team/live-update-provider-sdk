import Foundation

/// Manages a provider's live updates for a single app.
public protocol ProviderManager {
    /// The directory containing the latest app files on disk.
    var latestAppDirectory: URL? { get }

    /// Checks for updates and prepares the latest app files.
    ///
    /// Implementations should perform blocking work off the cooperative thread pool
    /// (e.g. via a detached `Task` or an appropriate executor) rather than blocking
    /// the calling task.
    ///
    /// - Returns: A sync result, or `nil` when no update is available.
    func sync() async throws -> (any ProviderSyncResult)?
}
