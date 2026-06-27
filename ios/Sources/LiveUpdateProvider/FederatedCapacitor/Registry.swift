import Foundation

/// Thread-safe registry of `LiveUpdateProvider` implementations.
public final class ProviderRegistry {
    public static let shared = ProviderRegistry()

    private var providers: [String: any LiveUpdateProvider] = [:]
    private let lock = NSLock()

    private init() {}

    /// Registers a provider by `id`.
    ///
    /// - Throws: `ProviderError.invalidConfiguration` when the provider ID is blank
    ///   or when another provider is already registered with the same ID.
    public func register(_ provider: any LiveUpdateProvider) throws {
        guard !provider.id.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw ProviderError.invalidConfiguration(
                "Cannot register a provider with an empty ID.",
                underlyingError: nil
            )
        }

        lock.lock()
        defer { lock.unlock() }

        guard providers[provider.id] == nil else {
            throw ProviderError.invalidConfiguration(
                "Provider with ID '\(provider.id)' is already registered.",
                underlyingError: nil
            )
        }

        providers[provider.id] = provider
    }

    /// Returns the provider registered for `id`, or `nil` when missing.
    public func resolve(_ id: String) -> (any LiveUpdateProvider)? {
        lock.lock()
        defer { lock.unlock() }
        return providers[id]
    }

    /// Returns the provider registered for `id`.
    ///
    /// - Throws: `ProviderError.providerNotRegistered` when missing.
    public func require(_ id: String) throws -> any LiveUpdateProvider {
        guard let provider = resolve(id) else {
            throw ProviderError.providerNotRegistered(id)
        }
        return provider
    }
}
