import Foundation

/// Thread-safe registry for Federated Capacitor providers.
public final class ProviderRegistry {
    public static let shared = ProviderRegistry()

    private var providers: [String: any LiveUpdateProvider] = [:]
    private let lock = NSLock()

    private init() {}

    /// Registers a provider by ID.
    ///
    /// - Throws: `ProviderError.registrationFailed` when the provider ID is blank
    ///   or already registered.
    public func register(_ provider: any LiveUpdateProvider) throws {
        guard !provider.id.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw ProviderError.registrationFailed(
                message: "Cannot register a provider with an empty ID."
            )
        }

        lock.lock()
        defer { lock.unlock() }

        guard providers[provider.id] == nil else {
            throw ProviderError.registrationFailed(
                message: "Provider with ID '\(provider.id)' is already registered."
            )
        }

        providers[provider.id] = provider
    }

    /// Returns the provider registered for `id`, or `nil` when none exists.
    public func resolve(_ id: String) -> (any LiveUpdateProvider)? {
        lock.lock()
        defer { lock.unlock() }
        return providers[id]
    }

    /// Returns the provider registered for `id`.
    ///
    /// - Throws: `ProviderError.providerNotRegistered` when no provider is registered for `id`.
    public func require(_ id: String) throws -> any LiveUpdateProvider {
        guard let provider = resolve(id) else {
            throw ProviderError.providerNotRegistered(id: id)
        }
        return provider
    }
}
