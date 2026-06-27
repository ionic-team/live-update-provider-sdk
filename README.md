# Live Update Provider SDK

[![CI](https://img.shields.io/github/actions/workflow/status/ionic-team/live-update-provider-sdk/ci.yml?branch=main&label=CI)](https://github.com/ionic-team/live-update-provider-sdk/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.ionic/liveupdateprovider?label=Maven%20Central)](https://central.sonatype.com/artifact/io.ionic/liveupdateprovider)
[![CocoaPods](https://img.shields.io/cocoapods/v/LiveUpdateProvider?label=CocoaPods)](https://cocoapods.org/pods/LiveUpdateProvider)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Native iOS and Android contracts for integrating external live update services with [Ionic Portals](https://ionic.io/docs/portals/) and [Federated Capacitor](https://ionic.io/docs/portals/for-capacitor/overview).

## Overview

This SDK defines the iOS and Android interfaces that live update providers implement so certain Ionic products can load web assets from a provider-managed source. It is a contract layer only — it does not implement an update backend, downloader, or storage strategy.

The project was created as part of the transition away from [Ionic Live Updates](https://ionic.io/docs/appflow/deploy/intro) backed by [Ionic Appflow](https://ionic.io/docs/appflow). Standard Capacitor apps can integrate alternative live update services directly, but Ionic Portals and Federated Capacitor need a stable provider contract that does not depend on one backend service.

## Requirements

| Platform | Minimum | Toolchain |
| --- | --- | --- |
| iOS | 15.0 | Swift 5.9 |
| Android | API 24 | JDK 17 |

## Installation

### iOS

Swift Package Manager:

```swift
.package(
    url: "https://github.com/ionic-team/live-update-provider-sdk.git",
    from: "0.2.0"
)
```

Add the product to your target:

```swift
.product(name: "LiveUpdateProvider", package: "live-update-provider-sdk")
```

CocoaPods:

```ruby
pod 'LiveUpdateProvider', '~> 0.2.0'
```

### Android

Gradle:

```kotlin
dependencies {
    implementation("io.ionic:liveupdateprovider:0.2.0")
}
```

## Provider Model

The SDK has two provider roles:

- Provider: identifies a live update provider and creates configured managers.
- Manager: syncs one configured app instance and reports the latest app directory.

| Concept | iOS | Android |
| --- | --- | --- |
| Provider | `LiveUpdateProvider` | `LiveUpdateProvider` |
| Manager | `ProviderManager` | `ProviderManager` |
| Sync result marker | `ProviderSyncResult` | `ProviderSyncResult` |
| Metadata sync result | `MetadataSyncResult` | `MetadataSyncResult` |
| Registry | `ProviderRegistry.shared` | `ProviderRegistry` |

On Android, the Federated Capacitor types (`LiveUpdateProvider`, `ProviderRegistry`)
live in the `io.ionic.liveupdateprovider.federatedcapacitor` package; the shared contracts live
in `io.ionic.liveupdateprovider`.

`latestAppDirectory` is the handoff point between the provider and the host runtime. It should point to the latest app directory that the provider has prepared. It does not mean that a WebView is currently displaying that directory.

`MetadataSyncResult` is optional. Return it when a host integration, such as Federated Capacitor, should pass provider metadata from native sync to JavaScript. Metadata intended for JavaScript exposure should use bridge-safe values: strings, numbers, booleans, nulls, arrays, and plain dictionaries/maps.

## Build a Provider

### Ionic Portals

Portals integrations use the manager contract directly:

- Implement `ProviderManager`.
- Construct the manager from your app or provider integration code.
- Pass the configured manager to the Portal configuration.

Portals does not require `ProviderRegistry` registration.

### Federated Capacitor

Federated Capacitor integrations use provider lookup:

- Implement `LiveUpdateProvider`.
- Implement `ProviderManager`.
- Package the provider as a Capacitor plugin.
- Register the provider with `ProviderRegistry` during plugin initialization.

The Federated Capacitor runtime resolves the provider by ID, passes provider-specific configuration to `createManager`, and then calls the returned manager.

## Minimal Provider Shape

### iOS

```swift
import LiveUpdateProvider

final class ExampleProvider: LiveUpdateProvider {
    let id = "example"

    func createManager(config: [String: Any]) throws -> any ProviderManager {
        guard config["appId"] is String else {
            throw ProviderError.invalidConfiguration(
                "Missing appId.",
                underlyingError: nil
            )
        }

        return ExampleManager()
    }
}

final class ExampleManager: ProviderManager {
    private(set) var latestAppDirectory: URL?

    func sync() async throws -> (any ProviderSyncResult)? {
        do {
            latestAppDirectory = try prepareAssets()
            // Return `nil` when there is nothing to report, or a custom
            // `ProviderSyncResult`. Use `MetadataSyncResult` to pass
            // bridge-safe metadata to Federated Capacitor.
            return MetadataSyncResult(metadata: ["status": "updated"])
        } catch {
            throw ProviderError.syncFailed(
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
import io.ionic.liveupdateprovider.MetadataSyncResult
import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.ProviderSyncCallback
import io.ionic.liveupdateprovider.federatedcapacitor.LiveUpdateProvider
import java.io.File

class ExampleProvider : LiveUpdateProvider {
    override val id = "example"

    override fun createManager(
        context: Context,
        config: Map<String, Any>
    ): ProviderManager {
        if (config["appId"] !is String) {
            throw ProviderError.InvalidConfiguration("Missing appId.")
        }

        return ExampleManager()
    }
}

class ExampleManager : ProviderManager {
    override var latestAppDirectory: File? = null
        private set

    override fun sync(callback: ProviderSyncCallback) {
        try {
            latestAppDirectory = prepareAssets()
            // Pass `null` when there is nothing to report, or a custom
            // ProviderSyncResult. Use MetadataSyncResult to pass
            // bridge-safe metadata to Federated Capacitor.
            callback.onSuccess(MetadataSyncResult(mapOf("status" to "updated")))
        } catch (error: Throwable) {
            callback.onFailure(
                ProviderError.SyncFailed(
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

Kotlin providers can instead extend `CoroutineProviderManager` (from
`io.ionic.liveupdateprovider.coroutines`), implementing
`suspend fun performSync(): ProviderSyncResult?`; the callback contract is satisfied
automatically. Kotlin consumers can call `suspend fun ProviderManager.sync(): ProviderSyncResult?`
for an `async`-style API. Both are included in the main artifact.

For a working reference provider, see [`live-update-provider-mock`](https://github.com/ionic-team/live-update-provider-mock).

## Provider Contract

Key rules for provider implementations:

- Validate configuration before returning a manager; throw `invalidConfiguration` / `InvalidConfiguration` for bad config.
- Point `latestAppDirectory` at the latest valid bundle before reporting sync success — never at a partial or invalid one.
- Leave `latestAppDirectory` unchanged on failure or when no new bundle is prepared.
- Throw `syncFailed` / `SyncFailed` when sync cannot complete.
- Never expose secrets, tokens, signed URLs, or sensitive backend details through errors or metadata.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the complete contract, including activation, rollback, and cleanup policy.

## Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md): provider contract, platform architecture, registry behavior, and operational boundaries
- [DESIGN.md](DESIGN.md): API design rationale for the `0.2.0` contract
- [docs/live-update-service-architecture.md](docs/live-update-service-architecture.md): optional guidance for teams building a live update backend service
- [CHANGELOG.md](CHANGELOG.md): release history
- [RELEASING.md](RELEASING.md): maintainer release process

## License

Licensed under the MIT License. See [LICENSE](LICENSE).
