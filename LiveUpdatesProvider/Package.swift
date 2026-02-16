// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "LiveUpdatesProvider",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "LiveUpdatesProvider",
            targets: ["LiveUpdatesProvider"]
        ),
    ],
    targets: [
        .target(
            name: "LiveUpdatesProvider"
        ),
        .testTarget(
            name: "LiveUpdatesProviderTests",
            dependencies: ["LiveUpdatesProvider"]
        ),
    ]
)
