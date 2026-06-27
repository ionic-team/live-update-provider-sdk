import Foundation

/// Errors used by the Live Update Provider SDK.
///
/// The registry emits `providerNotRegistered`. Providers use `invalidConfiguration`
/// when creating a manager and `syncFailed` during sync.
public enum ProviderError: Error {
    /// No provider is registered for the requested identifier.
    case providerNotRegistered(String)

    /// Required configuration is missing or invalid.
    case invalidConfiguration(String, underlyingError: Error?)

    /// Sync could not complete.
    case syncFailed(String, underlyingError: Error?)
}

extension ProviderError: LocalizedError {
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
