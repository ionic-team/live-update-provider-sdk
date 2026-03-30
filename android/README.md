# Live Updates Provider API - Android

Android implementation of the Live Updates Provider API, which defines an abstraction layer for live update implementations.

## Installation

Add the dependency to your `build.gradle` or `build.gradle.kts`:

**Gradle (Groovy)**
```groovy
dependencies {
    implementation 'io.ionic:liveupdateprovider:0.1.0-alpha.1'
}
```

**Gradle (Kotlin DSL)**
```kotlin
dependencies {
    implementation("io.ionic:liveupdateprovider:0.1.0-alpha.1")
}
```

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

- Android SDK 24+ (minSdk)
- Kotlin 2.1.0
- Java 17
- Gradle 8.13+

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
export LIVE_UPDATE_PROVIDER_SDK_VERSION=0.1.0-alpha.1
./gradlew publishToMavenLocal
```

### Maven Central

Production releases are automated via CI using `.github/workflows/publish-android.yml`.

**Prerequisites:**
Configure the following GitHub secrets in your repository:
- `ANDROID_CENTRAL_USERNAME` - Maven Central username
- `ANDROID_CENTRAL_PASSWORD` - Maven Central password
- `ANDROID_SONATYPE_STAGING_PROFILE_ID` - Sonatype staging profile ID
- `ANDROID_SIGNING_KEY_ID` - GPG signing key ID
- `ANDROID_SIGNING_PASSWORD` - GPG signing password
- `ANDROID_SIGNING_KEY` - GPG signing key (base64 encoded)

**Release Process:**
1. Update the version in `android/package.json`
2. Commit and merge changes to the main branch
3. Create a GitHub release with tag matching the version (e.g., `v0.1.0-alpha.1`)
4. The GitHub Actions workflow will:
   - Build the Android library
   - Upload the AAR artifact to the GitHub release
   - Publish the library to Maven Central

The version is read from `android/package.json` and the publish script checks if the version already exists on Maven Central before publishing.

## License

Copyright © 2026 Ionic
