import Foundation

public protocol LiveUpdateProviding: Sendable {
    var id: String { get }
    
    func createManager(
        config: [String: Any]?
    ) throws -> any LiveUpdateManaging
}

public protocol LiveUpdateManaging {
    var latestAppDirectory: URL? { get }
    
    func sync() async throws -> any SyncResult
}

public protocol SyncResult: Sendable {
    var didUpdate: Bool { get }
}
