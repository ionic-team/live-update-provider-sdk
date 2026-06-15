# Live Update Provider SDK

Contracts for integrating external live update services with [Ionic Portals](https://ionic.io/docs/portals/) and [Federated Capacitor](https://ionic.io/docs/portals/for-capacitor/overview).

## Overview

This SDK defines the iOS and Android interfaces that live update providers implement so certain Ionic products can load web assets from a provider-managed source.

The project was created as part of the transition away from [Ionic Live Updates](https://ionic.io/docs/appflow/deploy/intro) backed by [Ionic Appflow](https://ionic.io/docs/appflow). Standard Capacitor apps can integrate alternative live update services directly, but Ionic Portals and Federated Capacitor need a stable provider contract that does not depend on one backend service.

## Installation

### iOS

Swift Package Manager:

```swift
.package(
    url: "https://github.com/ionic-team/live-update-provider-sdk.git",
    from: "0.1.0"
)
```

Add the product to your target:

```swift
.product(name: "LiveUpdateProvider", package: "live-update-provider-sdk")
```

CocoaPods:

```ruby
pod 'LiveUpdateProvider', '~> 0.1.0'
```

### Android

Gradle:

```kotlin
dependencies {
    implementation("io.ionic:liveupdateprovider:0.1.0")
}
```

## Provider Model

The SDK has two provider roles:

- Provider: identifies a live update provider and creates configured managers.
- Manager: syncs one configured app instance and reports the latest app directory.

| Concept | iOS | Android |
| --- | --- | --- |
| Provider | `LiveUpdateProvider` | `LiveUpdateProvider` |
| Manager | `LiveUpdateProviderManager` | `LiveUpdateProviderManager` |
| Sync result | `LiveUpdateProviderSyncResult` | `LiveUpdateProviderSyncResult` |
| Metadata sync result | `MetadataSyncResult` | `MetadataSyncResult` |
| Default metadata result | `DefaultMetadataSyncResult` | `DefaultMetadataSyncResult` |
| Registry | `LiveUpdateProviderRegistry.shared` | `LiveUpdateProviderRegistry` |

`latestAppDirectory` is the handoff point between the provider and the host runtime. It should point to the latest app directory that the provider has prepared. It does not mean that a WebView is currently displaying that directory.

`MetadataSyncResult` is optional. Return it when a host integration, such as Federated Capacitor, should pass provider metadata from native sync to JavaScript. Metadata intended for JavaScript exposure should use bridge-safe values: strings, numbers, booleans, nulls, arrays, and plain dictionaries/maps.

## Build a Provider

### Ionic Portals

Portals integrations use the manager contract directly:

- Implement `LiveUpdateProviderManager`.
- Construct the manager from your app or provider integration code.
- Pass the configured manager to the Portal configuration.

Portals does not require `LiveUpdateProviderRegistry` registration.

### Federated Capacitor

Federated Capacitor integrations use provider lookup:

- Implement `LiveUpdateProvider`.
- Implement `LiveUpdateProviderManager`.
- Package the provider as a Capacitor plugin.
- Register the provider with `LiveUpdateProviderRegistry` during plugin initialization.

The Federated Capacitor runtime resolves the provider by ID, passes provider-specific configuration to `createManager`, and then calls the returned manager.

## Sync Contract Checklist

Provider implementations should:

- Validate provider-specific configuration before returning a manager.
- Keep `latestAppDirectory` pointed at the latest app directory when one exists.
- Update `latestAppDirectory` before reporting sync success when sync prepares a new bundle.
- Leave `latestAppDirectory` unchanged when sync fails or when no new bundle is prepared.
- Never point `latestAppDirectory` at a partially downloaded, partially extracted, or invalid bundle.
- If activation fails, leave `latestAppDirectory` unchanged or restore it to a known-good app directory.
- Return `invalidConfiguration` / `InvalidConfiguration` for bad configuration.
- Return `syncFailed` / `SyncFailed` for failed sync work.
- Avoid exposing secrets, tokens, signed URLs, native-only objects, or sensitive backend details through errors or metadata.
- Clean up unused disk assets according to the provider's retention policy.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the full provider contract and architecture boundaries.

## Minimal Provider Shape

### iOS

```swift
import LiveUpdateProvider

final class ExampleProvider: LiveUpdateProvider {
    let id = "example"

    func createManager(config: [String: Any]) throws -> any LiveUpdateProviderManager {
        guard config["appId"] is String else {
            throw LiveUpdateProviderError.invalidConfiguration(
                "Missing appId.",
                underlyingError: nil
            )
        }

        return ExampleManager()
    }
}

final class ExampleManager: LiveUpdateProviderManager {
    private(set) var latestAppDirectory: URL?

    func sync() async throws -> any LiveUpdateProviderSyncResult {
        do {
            latestAppDirectory = try prepareAssets()
            return DefaultMetadataSyncResult(metadata: ["status": "updated"])
        } catch {
            throw LiveUpdateProviderError.syncFailed(
                "Unable to sync live update assets.",
                underlyingError: error
            )
        }
    }

    private func prepareAssets() throws -> URL {
        // Fetch, validate, store, and prepare provider-managed assets.
        URL(fileURLWithPath: "/path/to/latest/app")
    }
}
```

### Android

```kotlin
import android.content.Context
import io.ionic.liveupdateprovider.DefaultMetadataSyncResult
import io.ionic.liveupdateprovider.LiveUpdateProvider
import io.ionic.liveupdateprovider.LiveUpdateProviderError
import io.ionic.liveupdateprovider.LiveUpdateProviderManager
import io.ionic.liveupdateprovider.LiveUpdateProviderSyncCallback
import java.io.File

class ExampleProvider : LiveUpdateProvider {
    override val id = "example"

    override fun createManager(
        context: Context,
        config: Map<String, Any>
    ): LiveUpdateProviderManager {
        if (config["appId"] !is String) {
            throw LiveUpdateProviderError.InvalidConfiguration("Missing appId.")
        }

        return ExampleManager()
    }
}

class ExampleManager : LiveUpdateProviderManager {
    override var latestAppDirectory: File? = null
        private set

    override fun sync(callback: LiveUpdateProviderSyncCallback?) {
        try {
            latestAppDirectory = prepareAssets()
            callback?.onSuccess(DefaultMetadataSyncResult(mapOf("status" to "updated")))
        } catch (error: Throwable) {
            callback?.onFailure(
                LiveUpdateProviderError.SyncFailed(
                    "Unable to sync live update assets.",
                    error
                )
            )
        }
    }

    private fun prepareAssets(): File {
        // Fetch, validate, store, and prepare provider-managed assets.
        return File("/path/to/latest/app")
    }
}
```

Android providers should call exactly one terminal callback per `sync` invocation: `onSuccess` or `onFailure`.

For a working reference provider, see [`live-update-provider-mock`](https://github.com/ionic-team/live-update-provider-mock).

## Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md): provider contract, platform architecture, registry behavior, and operational boundaries
- [docs/live-update-service-architecture.md](docs/live-update-service-architecture.md): optional guidance for teams building a live update backend service
- [RELEASING.md](RELEASING.md): maintainer release process

## License

See [License](License).
