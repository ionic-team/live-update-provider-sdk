import XCTest
@testable import LiveUpdateProvider

final class MetadataSyncResultTests: XCTestCase {
    func testDefaultMetadataSyncResultCarriesMetadata() throws {
        let result: any MetadataSyncResult = DefaultMetadataSyncResult(metadata: ["version": "1.0.0"])

        let version = try XCTUnwrap(result.metadata?["version"] as? String)
        XCTAssertEqual(version, "1.0.0")
    }

    func testDefaultMetadataSyncResultSupportsMissingMetadata() {
        let result: any MetadataSyncResult = DefaultMetadataSyncResult()

        XCTAssertNil(result.metadata)
    }
}
