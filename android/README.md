# Live Updates Provider API - Android

This module contains the Android implementation of the Live Updates Provider API, which defines an abstraction layer for live update implementations.

## Project Structure

```
android/
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Module configuration
├── gradle/                       # Gradle wrapper files
├── gradlew                       # Gradle wrapper script (Unix)
├── gradlew.bat # Gradle wrapper script (Windows)
└── live-updates-integrations-sdk/        # Android library module
    ├── build.gradle.kts          # Module build configuration
    ├── proguard-rules.pro        # ProGuard rules
    ├── consumer-rules.pro        # Consumer ProGuard rules
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml   # Android manifest
        │   └── kotlin/io/ionic/liveupdatesintegration/provider/
        │       ├── LiveUpdatesProvider.kt       # Provider interface
        │       ├── LiveUpdatesManager.kt        # Manager interface
        │       ├── LiveUpdatesRegistry.kt       # Provider registry
        │       └── models/
        │           ├── LiveUpdatesProviderConfig.kt # Configuration data classes
        │           ├── LiveUpdatesOptions.kt        # Options data classes
        │           └── LiveUpdatesSyncResult.kt     # Result data classes
        └── test/
            └── kotlin/io/ionic/liveupdatesintegration/provider/ # Unit tests
```

## Overview

The Live Updates Provider API defines a standard interface for live update implementations:

- **LiveUpdatesProvider**: Creates manager instances for configured apps
- **LiveUpdatesManager**: Handles sync operations for a single app
- **LiveUpdatesRegistry**: Thread-safe registry for provider registration and lookup
- **LiveUpdatesProviderConfig**: Opaque configuration passed to providers
- **LiveUpdatesOptions**: Auto-update method configuration
- **LiveUpdatesSyncResult**: Result of sync operations

## Building

```bash
# Build the library
./gradlew :live-updates-provider:build

# Run tests
./gradlew :live-updates-provider:test

# Generate AAR
./gradlew :live-updates-provider:assembleRelease
```

The output AAR will be located at:
```
android/live-updates-provider/build/outputs/aar/live-updates-provider-release.aar
```

## Requirements

- Android SDK 21+ (minSdk)
- Kotlin 1.9.25
- Java 17
- Gradle 8.9+

## Dependencies

The provider API module depends on:
- AndroidX Core KTX
- Kotlin Coroutines for Android

## Usage

Providers register themselves with the `LiveUpdatesRegistry`:

```kotlin
// Registration (typically done in provider's init block)
LiveUpdatesRegistry.register("my-provider", MyLiveUpdatesProvider)

// Lookup and create manager
val provider = LiveUpdatesRegistry.require("my-provider")
val config = LiveUpdatesProviderConfig(
    values = mapOf(
        "appId" to LiveUpdatesConfigValue.StringValue("your-app-id"),
        "channel" to LiveUpdatesConfigValue.StringValue("your-channel")
    )
)
val options = LiveUpdatesOptions(
    autoUpdateMethod = LiveUpdatesOptions.AutoUpdateMethod.BACKGROUND
)
val manager = provider.createManager(context, config, options)

// Perform sync
val result = manager.sync()
if (result.activeAssetsChanged) {
    val assetsDir = result.activeAssetDirectory
    // Use the updated assets
}
```

## License

Copyright © 2024 Ionic
