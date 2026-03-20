// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "LiveUpdateProvider",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "LiveUpdateProvider",
            targets: ["LiveUpdateProvider"]
        ),
    ],
    targets: [
        .target(
            name: "LiveUpdateProvider",
            path: "ios/LiveUpdateProvider/Sources/LiveUpdateProvider"
        ),
        .testTarget(
            name: "LiveUpdateProviderTests",
            dependencies: ["LiveUpdateProvider"],
            path: "ios/LiveUpdateProvider/Tests/LiveUpdateProviderTests"
        ),
    ]
)
