import Foundation

/// Errors reported by provider registration, configuration, and sync operations.
public enum ProviderError: Error {
    /// No provider is registered for the requested identifier.
    case providerNotRegistered(id: String)

    /// Provider registration failed.
    case registrationFailed(message: String)

    /// Required configuration is missing or invalid.
    case invalidConfiguration(message: String, underlyingError: Error?)

    /// Sync could not complete.
    case syncFailed(message: String, underlyingError: Error?)
}

public extension ProviderError {
    static func invalidConfiguration(message: String) -> ProviderError {
        .invalidConfiguration(message: message, underlyingError: nil)
    }

    static func syncFailed(message: String) -> ProviderError {
        .syncFailed(message: message, underlyingError: nil)
    }
}

extension ProviderError: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .providerNotRegistered(let id):
            return "Live update provider '\(id)' is not registered."
        case .registrationFailed(let message):
            return "Registration failed: \(message)"
        case .invalidConfiguration(let message, _):
            return "Invalid configuration: \(message)"
        case .syncFailed(let message, _):
            return "Sync failed: \(message)"
        }
    }
}
