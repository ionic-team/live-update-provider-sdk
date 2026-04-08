import XCTest
@testable import LiveUpdateProvider

// MARK: - Mocks
private struct MockManager: LiveUpdateManaging {
    let latestAppDirectory: URL? = nil
    
    func sync() async throws -> any SyncResult {
        DefaultFederatedCapacitorSyncResult(metadata: nil)
    }
}

private struct MockProvider: LiveUpdateProviding {
    let id: String
    
    func createManager(
        config: [String: Any]
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
        
        try registry.register(provider)
        
        let resolvedProvider = registry.resolve(providerId)
        let missingProvider = registry.resolve("missing")
        
        XCTAssertNotNil(resolvedProvider)
        XCTAssertNil(missingProvider)
        
        do {
            _ = try registry.require(providerId)
        } catch {
            XCTFail("Expected require(\(providerId)) not to throw, got: \(error)")
        }
        
        do {
            _ = try registry.require("missing")
            XCTFail("Expected require(\"missing\") to throw")
        } catch {
            // expected
        }
    }
    
    func testRegistryConcurrency() async throws {
        let registry = LiveUpdateProviderRegistry.shared
        let runId = UUID().uuidString
        
        try await withThrowingTaskGroup(of: Void.self) { group in
            for i in 0..<100 {
                group.addTask {
                    let id = "concurrent-provider-\(runId)-\(i)"
                    try registry.register(MockProvider(id: id))
                    _ = registry.resolve(id)
                }
            }

            try await group.waitForAll()
        }
        
        let provider50 = registry.resolve("concurrent-provider-\(runId)-50")
        XCTAssertNotNil(provider50)
    }

    func testRegisterThrowsForEmptyProviderId() {
        let registry = LiveUpdateProviderRegistry.shared
        let provider = MockProvider(id: "")

        do {
            try registry.register(provider)
            XCTFail("Expected register to throw for empty provider ID")
        } catch LiveUpdateProviderError.invalidConfiguration(let details, _) {
            XCTAssertTrue(details.contains("empty ID"))
        } catch {
            XCTFail("Expected invalidConfiguration, got: \(error)")
        }
    }

    func testRegisterThrowsForDuplicateProviderId() throws {
        let registry = LiveUpdateProviderRegistry.shared
        let providerId = "duplicate-provider-\(UUID().uuidString)"

        try registry.register(MockProvider(id: providerId))

        do {
            try registry.register(MockProvider(id: providerId))
            XCTFail("Expected register to throw for duplicate provider ID")
        } catch LiveUpdateProviderError.invalidConfiguration(let details, _) {
            XCTAssertTrue(details.contains("already registered"))
        } catch {
            XCTFail("Expected invalidConfiguration, got: \(error)")
        }
    }
}
