import Foundation


/// An error thrown by a live update provider.
public enum ProviderError: Error {
    /// The provider received an invalid configuration.
    case invalidConfiguration(message: String, underlyingError: Error?)
}

public extension ProviderError {
    static func invalidConfiguration(message: String) -> ProviderError {
        .invalidConfiguration(message: message, underlyingError: nil)
    }
}

extension ProviderError: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .invalidConfiguration(let message, _):
            return "Invalid configuration: \(message)"
        }
    }
}
