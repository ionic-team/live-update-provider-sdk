import Foundation

/// Errors reported by provider registration, configuration, and sync operations.
public enum ProviderError: Error {
    /// No provider is registered for the requested identifier.
    case providerNotRegistered(id: String)

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
        case .invalidConfiguration(let message, _):
            return "Invalid configuration: \(message)"
        case .syncFailed(let message, _):
            return "Sync failed: \(message)"
        }
    }
}

extension ProviderError: CustomNSError {
    public static var errorDomain: String { "io.ionic.LiveUpdateProvider" }

    public var errorCode: Int {
        switch self {
        case .providerNotRegistered: return 1
        case .invalidConfiguration: return 2
        case .syncFailed: return 3
        }
    }

    public var errorUserInfo: [String: Any] {
        switch self {
        case .invalidConfiguration(_, let underlyingError),
             .syncFailed(_, let underlyingError):
            guard let underlyingError else { return [:] }
            return [NSUnderlyingErrorKey: underlyingError]
        case .providerNotRegistered:
            return [:]
        }
    }
}
