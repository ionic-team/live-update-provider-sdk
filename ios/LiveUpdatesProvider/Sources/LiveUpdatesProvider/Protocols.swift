import Foundation

public protocol LiveUpdatesProvider: Sendable {
    var id: String { get }
    
    func createManager(
        config: ProviderConfig
    ) throws -> any LiveUpdateManaging
}

public protocol LiveUpdateManaging: Sendable {
    func sync() async throws -> SyncResult
    func latestAppDirectory() -> URL?
}
