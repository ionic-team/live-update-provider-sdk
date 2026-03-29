import Foundation

public enum LiveUpdateError: Error, Sendable {
    case providerNotRegistered(String)
}

extension LiveUpdateError: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .providerNotRegistered(let id):
            return "Live update provider '\(id)' is not registered."
        }
    }
}
