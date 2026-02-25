import Foundation

public struct ProviderConfig: Sendable {
    private let data: [String: Sendable]
    
    public init(dictionary: [String: Sendable]) {
        self.data = dictionary
    }
    
    public func value<T>(for key: String) -> T? {
        data[key] as? T
    }
}

public struct SyncResult: Sendable, Equatable {
    public let didUpdate: Bool
    public let latestAppDirectory: URL?
    
    
    public init(
        didUpdate: Bool,
        latestAppDirectory: URL?
    ) {
        self.didUpdate = didUpdate
        self.latestAppDirectory = latestAppDirectory
    }
}

public enum LiveUpdatesError: Error, Sendable {
    case providerNotRegistered(String)
    case invalidConfiguration(String, underlyingError: (any Error)? = nil)
    case syncFailed(message: String, underlyingError: (any Error)? = nil)
}
