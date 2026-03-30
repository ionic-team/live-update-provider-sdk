# Live Updates Provider API - Android

Android implementation of the Live Updates Provider API, which defines an abstraction layer for live update implementations.

## Building

```bash
# Build the library
./gradlew :live-update-provider:build

# Run tests
./gradlew :live-update-provider:test

# Generate AAR
./gradlew :live-update-provider:assembleRelease
```

The output AAR will be located at:
```
android/live-update-provider/build/outputs/aar/live-update-provider-release.aar
```

## Requirements

- Android SDK 21+ (minSdk)
- Kotlin 2.1.0
- Java 17
- Gradle 8.14.3

## Dependencies

The provider API module depends on:
- AndroidX Core KTX

## Usage

Providers register themselves with the `LiveUpdateProviderRegistry`:

```kotlin
// Register a provider implementation
LiveUpdateProviderRegistry.register(myProviderInstance)

// Resolve and create a manager
val provider = LiveUpdateProviderRegistry.require("provider-id") // LiveUpdateProviderRegistry.resolve("provider-id")
val config = mapOf("appId" to "my-app", "channel" to "your-channel")
val manager = provider.createManager(context, config)

// Sync (with callback)
manager.sync(object : ProviderSyncCallback {
    override fun onSuccess(result: ProviderSyncResult) {
        // Check if update was applied
        if (result is FederatedCapacitorSyncResult && result.didUpdate) {
            val assetsDir = manager.latestAppDirectory
        }
    }

    override fun onFailure(error: LiveUpdateError.SyncFailed) {
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

Production releases are automated via CI using `.github/workflows/publish-android.yml`.

1. Create a GitHub release tag (for example, `v0.1.0`)
2. The workflow resolves the release version from the tag
3. The workflow publishes the `:live-update-provider` artifact to Maven Central

## License

Copyright © 2026 Ionic
