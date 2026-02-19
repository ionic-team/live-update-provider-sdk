import XCTest
@testable import LiveUpdatesProvider

// MARK: - Mocks
final class MockProvider: LiveUpdatesProvider {
    let id: String
    init(id: String) { self.id = id }
    func createManager(config: ProviderConfig) throws -> any LiveUpdatesManaging {
        return MockManager()
    }
}

struct MockManager: LiveUpdatesManaging {
    func sync() async throws -> SyncResult {
        return SyncResult(didUpdate: true, latestAppDirectory: nil)
    }
    func latestAppDirectory() -> URL? { return nil }
}

// MARK: - Tests
final class LiveUpdatesRegistryTests: XCTestCase {
    
    func testRegistryResolveAndRequire() throws {
        let registry = LiveUpdatesRegistry.shared
        let providerId = "test-provider-\(UUID().uuidString)"
        let provider = MockProvider(id: providerId)
        
        registry.register(provider)
        
        XCTAssertNotNil(registry.resolve(providerId))
        XCTAssertNil(registry.resolve("missing"))
        XCTAssertNoThrow(try registry.require(providerId))
        XCTAssertThrowsError(try registry.require("missing"))
    }
    
    func testRegistryConcurrency() async {
        let registry = LiveUpdatesRegistry.shared
        
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
