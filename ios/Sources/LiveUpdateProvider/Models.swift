import Foundation

// public struct FederatedCapacitorSyncResult: SyncResult, Sendable {
//     public var didUpdate: Bool
//     public var metadata: [String: any Sendable]?
// }

public enum LiveUpdateError: Error, Sendable {
    case providerNotRegistered(String)
    case invalidConfiguration(String, underlyingError: (any Error)? = nil)
    case syncFailed(String, underlyingError: (any Error)? = nil)
}

extension LiveUpdateError: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .providerNotRegistered(let id):
            return "Live update provider '\(id)' is not registered."
        case .invalidConfiguration(let details, _):
            return "Invalid live update configuration: \(details)."
        case .syncFailed(let details, _):
            return "Live update sync failed: \(details)."
        }
    }
}
