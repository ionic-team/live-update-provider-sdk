import Foundation

public protocol LiveUpdatesProvider: Sendable {
    var id: String { get }
    
    func createManager(
        config: LiveUpdatesProviderConfig
    ) throws -> any LiveUpdatesManaging
}

public protocol LiveUpdatesManaging: Sendable {
    func sync() async throws -> LiveUpdatesSyncResult
    func latestAppDirectory() -> URL?
}
