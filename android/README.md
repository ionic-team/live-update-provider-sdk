# Live Updates Provider API - Android

Android implementation of the Live Updates Provider API, which defines an abstraction layer for live update implementations.

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

Providers register themselves with the `LiveUpdateProviderRegistry`:

```kotlin
// Register a provider implementation
LiveUpdateProviderRegistry.register(myProviderInstance)

// Resolve and create a manager
val provider = LiveUpdateProviderRegistry.require("provider-id") // LiveUpdateProviderRegistry.resolve("provider-id")
val config = mapOf("appId" to "my-app", "channel" to "your-channel")
val manager = provider.createManager(context, config)

// Sync and check for updates (with callback)
manager.sync(object : SyncCallback {
    override fun onComplete(result: SyncResult) {
        if (result.didUpdate) {
            val assetsDir = manager.latestAppDirectory
        }
    }

    override fun onError(error: LiveUpdateError.SyncFailed) {
        // Handle error
    }
})
```

## Publishing

### Local Development

Publish to your local Maven repository for testing:

```bash
./gradlew publishToMavenLocal
```

### Maven Central

Production releases are automated via CI. The version is managed in `android/package.json`.

1. Update the version in `android/package.json`
2. Create a GitHub release
3. The `publish-android.yml` workflow automatically runs `scripts/publish-android.sh`
4. The script publishes to Maven Central if the version is not already published

To manually publish, run the script from the repository root:

```bash
./scripts/publish-android.sh
```

## License

Copyright © 2026 Ionic
