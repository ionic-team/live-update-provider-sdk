import Foundation

public actor LiveUpdateProviderRegistry {
    public static let shared = LiveUpdateProviderRegistry()
    
    private var providers = [String: any LiveUpdateProviding]()
    
    private init() {}
    
    public func register(_ provider: any LiveUpdateProviding) {
        guard !provider.id.isEmpty else {
            let message = "LiveUpdateRegistry: Cannot register a provider with an empty ID."
            print(message)
            assertionFailure(message)
            return
        }
        
        if providers[provider.id] != nil {
            let message = "LiveUpdateRegistry: Provider with ID '\(provider.id)' is already registered. Ignoring subsequent registration."
            print(message)
            assertionFailure(message)
            return
        }
        
        providers[provider.id] = provider
    }
    
    public func resolve(_ id: String) -> (any LiveUpdateProviding)? {
        return providers[id]
    }
    
    public func require(_ id: String) throws -> any LiveUpdateProviding {
        guard let provider = resolve(id) else {
            throw LiveUpdateError.providerNotRegistered(id)
        }
        return provider
    }
}
