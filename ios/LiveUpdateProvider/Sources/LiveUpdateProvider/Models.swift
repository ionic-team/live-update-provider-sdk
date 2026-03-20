import Foundation
//
//public struct SyncResult: Sendable {
//    public let latestAppDirectory: URL?
//    public let didUpdate: Bool
//    public let metadata: [String: Any]?
//    
//    public init(
//        latestAppDirectory: URL?,
//        didUpdate: Bool,
//        metadata: [String: Any?]? = nil
//    ) {
//        self.latestAppDirectory = latestAppDirectory
//        self.didUpdate = didUpdate
//        self.metadata = metadata
//    }
//}



public enum LiveUpdateError: Error, Sendable {
    case providerNotRegistered(String)
    case invalidConfiguration(String, underlyingError: (any Error)? = nil)
    case syncFailed(String, underlyingError: (any Error)? = nil)
}
