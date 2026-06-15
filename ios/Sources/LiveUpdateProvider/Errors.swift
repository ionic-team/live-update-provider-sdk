import Foundation

/// Errors used by the Live Update Provider SDK.
///
/// `providerNotRegistered` is emitted by the registry. Providers should use
/// `invalidConfiguration` during manager creation and `syncFailed` during sync.
public enum LiveUpdateProviderError: Error {
    /// No provider is registered for the requested provider identifier.
    case providerNotRegistered(String)

    /// Provider configuration is missing required values or contains invalid values.
    case invalidConfiguration(String, underlyingError: Error?)

    /// A sync operation failed before completion.
    case syncFailed(String, underlyingError: Error?)
}

extension LiveUpdateProviderError: LocalizedError {
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
