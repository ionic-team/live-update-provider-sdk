import Foundation

/// Actor-backed registry of `LiveUpdateProviding` implementations.
public actor LiveUpdateProviderRegistry {
    public static let shared = LiveUpdateProviderRegistry()
    
    private var providers = [String: any LiveUpdateProviding]()
    
    private init() {}
    
    /// Registers a provider by `id`.
    public func register(_ provider: any LiveUpdateProviding) {
        guard !provider.id.isEmpty else {
            assertionFailure("Cannot register a provider with an empty ID.")
            return
        }

        if providers[provider.id] != nil {
            assertionFailure("Provider with ID '\(provider.id)' is already registered.")
            return
        }

        providers[provider.id] = provider
    }
    
    /// Returns the provider registered for `id`, or `nil` when missing.
    public func resolve(_ id: String) -> (any LiveUpdateProviding)? {
        return providers[id]
    }
    
    /// Returns the provider registered for `id`.
    ///
    /// - Throws: `LiveUpdateError.providerNotRegistered` when missing.
    public func require(_ id: String) throws -> any LiveUpdateProviding {
        guard let provider = resolve(id) else {
            throw LiveUpdateError.providerNotRegistered(id)
        }
        return provider
    }
}
