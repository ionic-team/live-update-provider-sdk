import Foundation

/// Thread-safe registry of `LiveUpdateProviding` implementations.
public final class LiveUpdateProviderRegistry {
    public static let shared = LiveUpdateProviderRegistry()

    private var providers: [String: any LiveUpdateProviding] = [:]
    private let lock = NSLock()

    private init() {}

    /// Registers a provider by `id`.
    ///
    /// - Throws: `LiveUpdateProviderError.invalidConfiguration` when the provider ID is empty
    ///   or when another provider is already registered with the same ID.
    public func register(_ provider: any LiveUpdateProviding) throws {
        guard !provider.id.isEmpty else {
            throw LiveUpdateProviderError.invalidConfiguration(
                "Cannot register a provider with an empty ID.",
                underlyingError: nil
            )
        }

        lock.lock()
        defer { lock.unlock() }

        guard providers[provider.id] == nil else {
            throw LiveUpdateProviderError.invalidConfiguration(
                "Provider with ID '\(provider.id)' is already registered.",
                underlyingError: nil
            )
        }

        providers[provider.id] = provider
    }

    /// Returns the provider registered for `id`, or `nil` when missing.
    public func resolve(_ id: String) -> (any LiveUpdateProviding)? {
        lock.lock()
        defer { lock.unlock() }
        return providers[id]
    }

    /// Returns the provider registered for `id`.
    ///
    /// - Throws: `LiveUpdateProviderError.providerNotRegistered` when missing.
    public func require(_ id: String) throws -> any LiveUpdateProviding {
        guard let provider = resolve(id) else {
            throw LiveUpdateProviderError.providerNotRegistered(id)
        }
        return provider
    }
}
