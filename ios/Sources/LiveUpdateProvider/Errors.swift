import Foundation

public enum LiveUpdateError: Error, Sendable {
    case providerNotRegistered(String)
    case invalidConfiguration(String, underlyingError: Error?)
    case syncFailed(String, underlyingError: Error?)
}

extension LiveUpdateError: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .providerNotRegistered(let id):
            return "Live update provider '\(id)' is not registered."
        case .invalidConfiguration(let details, _):
            return "Invalid configuration: \(details)"
        case .syncFailed(let details, _):
            return "Sync failed: \(details)"
        }
    }
}
