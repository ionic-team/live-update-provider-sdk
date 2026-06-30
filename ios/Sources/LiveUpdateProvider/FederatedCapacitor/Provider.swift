/// Creates managers for providers registered with Federated Capacitor.
public protocol LiveUpdateProvider {
    /// Identifier used for provider registration and lookup.
    var id: String { get }

    /// Creates a manager from provider-specific configuration.
    ///
    /// - Throws: `ProviderError.invalidConfiguration` when `config` is invalid.
    func createManager(config: [String: Any]) throws -> any ProviderManager
}
