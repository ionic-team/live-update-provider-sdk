import XCTest
@testable import LiveUpdateProvider

// MARK: - Mocks
private struct MockSyncResult: FederatedCapacitorSyncResult {
    let didUpdate: Bool
    let metadata: [String: any Sendable]?
}

private struct MockManager: LiveUpdateManaging {
    let latestAppDirectory: URL? = nil
    
    func sync() async throws -> any SyncResult {
        MockSyncResult(didUpdate: true, metadata: nil)
    }
}

private struct MockProvider: LiveUpdateProviding {
    let id: String
    
    func createManager(
        config: [String: any Sendable]?
    ) throws -> any LiveUpdateManaging {
        MockManager()
    }
}

// MARK: - Tests
final class LiveUpdateProviderRegistryTests: XCTestCase {
    func testRegistryResolveAndRequire() async throws {
        let registry = LiveUpdateProviderRegistry.shared
        let providerId = "test-provider-\(UUID().uuidString)"
        let provider = MockProvider(id: providerId)
        
        await registry.register(provider)
        
        let resolvedProvider = await registry.resolve(providerId)
        let missingProvider = await registry.resolve("missing")
        
        XCTAssertNotNil(resolvedProvider)
        XCTAssertNil(missingProvider)
        
        do {
            _ = try await registry.require(providerId)
        } catch {
            XCTFail("Expected require(\(providerId)) not to throw, got: \(error)")
        }
        
        do {
            _ = try await registry.require("missing")
            XCTFail("Expected require(\"missing\") to throw")
        } catch {
            // expected
        }
    }
    
    func testRegistryConcurrency() async {
        let registry = LiveUpdateProviderRegistry.shared
        let runId = UUID().uuidString
        
        await withTaskGroup(of: Void.self) { group in
            for i in 0..<100 {
                group.addTask {
                    let id = "concurrent-provider-\(runId)-\(i)"
                    await registry.register(MockProvider(id: id))
                    _ = await registry.resolve(id)
                }
            }
        }
        
        let provider50 = await registry.resolve("concurrent-provider-\(runId)-50")
        XCTAssertNotNil(provider50)
    }
    
}
