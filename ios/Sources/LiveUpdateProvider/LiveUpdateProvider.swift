/// Creates managers for a live update provider.
public protocol LiveUpdateProvider {
    /// Creates a manager for a single app.
    ///
    /// - Parameter configuration: Provider-specific configuration values.
    /// - Throws: `ProviderError.invalidConfiguration` when the configuration is invalid.
    /// - Returns: A manager that can sync and resolve the app's latest files.
    func createManager(configuration: [String: Any]) throws -> any ProviderManager
}
