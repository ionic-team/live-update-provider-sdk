import Foundation

/// Creates managers from provider-specific configuration, for Federated Capacitor
/// provider registration and lookup.
public protocol LiveUpdateProvider {
    /// Identifier used for registration and runtime lookup.
    var id: String { get }

    /// Creates a manager from provider-specific configuration.
    ///
    /// - Throws: `ProviderError.invalidConfiguration` when `config` is invalid.
    func createManager(config: [String: Any]) throws -> any ProviderManager
}
