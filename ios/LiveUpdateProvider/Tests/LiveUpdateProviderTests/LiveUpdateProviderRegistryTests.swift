import XCTest
@testable import LiveUpdateProvider

// MARK: - Mocks
final class MockProvider: LiveUpdateProvider {
    let id: String
    init(id: String) { self.id = id }
    func createManager(config: ProviderConfig) throws -> any LiveUpdateManaging {
        return MockManager()
    }
}

struct MockManager: LiveUpdateManaging {
    func sync() async throws -> SyncResult {
        return SyncResult(didUpdate: true, latestAppDirectory: nil)
    }
    func latestAppDirectory() -> URL? { return nil }
}

// MARK: - Tests
final class LiveUpdateRegistryTests: XCTestCase {
    
    func testRegistryResolveAndRequire() throws {
        let registry = LiveUpdateProviderRegistry.shared
        let providerId = "test-provider-\(UUID().uuidString)"
        let provider = MockProvider(id: providerId)
        
        registry.register(provider)
        
        XCTAssertNotNil(registry.resolve(providerId))
        XCTAssertNil(registry.resolve("missing"))
        XCTAssertNoThrow(try registry.require(providerId))
        XCTAssertThrowsError(try registry.require("missing"))
    }
    
    func testRegistryConcurrency() async {
        let registry = LiveUpdateProviderRegistry.shared
        
        await withTaskGroup(of: Void.self) { group in
            for i in 0..<100 {
                group.addTask {
                    let id = "concurrent-provider-\(i)"
                    registry.register(MockProvider(id: id))
                    _ = registry.resolve(id)
                }
            }
        }
        
        XCTAssertNotNil(registry.resolve("concurrent-provider-50"))
    }
}
