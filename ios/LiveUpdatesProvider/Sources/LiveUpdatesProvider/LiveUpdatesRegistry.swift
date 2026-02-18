import Foundation

public final class LiveUpdatesRegistry: @unchecked Sendable {
    public static let shared = LiveUpdatesRegistry()
    
    private var providers = [String: any LiveUpdatesProvider]()
    
    private let lock = NSLock()
    
    private init() {}
    
    public func register(_ provider: any LiveUpdatesProvider) {
        lock.lock()
        defer { lock.unlock() }
        
        if providers[provider.id] != nil {
            assertionFailure("LiveUpdatesRegistry: Provider with ID '\(provider.id)' is already registered.")
            print("LiveUpdatesRegistry Warning: Provider with ID '\(provider.id)' is being overwritten.")
        }
        
        providers[provider.id] = provider
    }
    
    public func resolve(_ id: String) -> (any LiveUpdatesProvider)? {
        lock.lock()
        defer { lock.unlock() }
        return providers[id]
    }
    
    public func require(_ id: String) throws -> any LiveUpdatesProvider {
        guard let provider = resolve(id) else {
            throw LiveUpdatesError.providerNotRegistered(id)
        }
        return provider
    }
}
