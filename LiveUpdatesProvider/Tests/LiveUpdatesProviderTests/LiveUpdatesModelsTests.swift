import XCTest
@testable import LiveUpdatesProvider

final class LiveUpdatesModelsTests: XCTestCase {
    
    func testProviderConfigTypeCasting() {
        let dictionary: [String: Sendable] = [
            "appId": "12345",
            "timeout": 30,
            "isEnabled": true
        ]
        let config = LiveUpdatesProviderConfig(dictionary: dictionary)
        
        XCTAssertEqual(config.value(for: "appId"), "12345")
        XCTAssertEqual(config.value(for: "timeout"), 30)
        XCTAssertEqual(config.value(for: "isEnabled"), true)
        XCTAssertNil(config.value(for: "missingKey") as String?)
        XCTAssertNil(config.value(for: "appId") as Int?)
    }
    
    func testSyncResultInitialization() {
        let url = URL(string: "file:///app/www")
        let result = LiveUpdatesSyncResult(didUpdate: true, latestAppDirectory: url)
        
        XCTAssertTrue(result.didUpdate)
        XCTAssertEqual(result.latestAppDirectory, url)
    }
}
