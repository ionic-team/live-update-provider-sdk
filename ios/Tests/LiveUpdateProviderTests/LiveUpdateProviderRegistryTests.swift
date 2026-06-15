import XCTest
@testable import LiveUpdateProvider

private struct MockManager: LiveUpdateProviderManager {
    let latestAppDirectory: URL? = nil

    func sync() async throws -> any LiveUpdateProviderSyncResult {
        DefaultMetadataSyncResult(metadata: nil)
    }
}

private struct MockProvider: LiveUpdateProvider {
    let id: String

    func createManager(
        config: [String: Any]
    ) throws -> any LiveUpdateProviderManager {
        MockManager()
    }
}

final class LiveUpdateProviderRegistryTests: XCTestCase {
    func testResolveReturnsRegisteredProvider() throws {
        let registry = LiveUpdateProviderRegistry.shared
        let providerId = "test-provider-\(UUID().uuidString)"
        let provider = MockProvider(id: providerId)

        try registry.register(provider)

        let resolvedProvider = registry.resolve(providerId)

        XCTAssertEqual(resolvedProvider?.id, providerId)
    }

    func testResolveReturnsNilForUnknownProvider() {
        let providerId = "missing-provider-\(UUID().uuidString)"

        XCTAssertNil(LiveUpdateProviderRegistry.shared.resolve(providerId))
    }

    func testRequireReturnsRegisteredProvider() throws {
        let registry = LiveUpdateProviderRegistry.shared
        let providerId = "required-provider-\(UUID().uuidString)"

        try registry.register(MockProvider(id: providerId))

        let provider = try registry.require(providerId)

        XCTAssertEqual(provider.id, providerId)
    }

    func testRequireThrowsProviderNotRegisteredForUnknownProvider() {
        let providerId = "missing-provider-\(UUID().uuidString)"

        do {
            _ = try LiveUpdateProviderRegistry.shared.require(providerId)
            XCTFail("Expected require to throw for missing provider")
        } catch LiveUpdateProviderError.providerNotRegistered(let id) {
            XCTAssertEqual(id, providerId)
        } catch {
            XCTFail("Expected providerNotRegistered, got: \(error)")
        }
    }

    func testConcurrentUniqueRegistrationsAreResolvable() async throws {
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

        for i in 0..<100 {
            let id = "concurrent-provider-\(runId)-\(i)"
            XCTAssertEqual(registry.resolve(id)?.id, id)
        }
    }

    func testRegisterThrowsForBlankProviderId() {
        let registry = LiveUpdateProviderRegistry.shared
        let providers = [MockProvider(id: ""), MockProvider(id: "   ")]

        for provider in providers {
            do {
                try registry.register(provider)
                XCTFail("Expected register to throw for blank provider ID")
            } catch LiveUpdateProviderError.invalidConfiguration(let details, _) {
                XCTAssertTrue(details.contains("empty ID"))
            } catch {
                XCTFail("Expected invalidConfiguration, got: \(error)")
            }
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
