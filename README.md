# Live Update Provider

[![Swift Package Manager](https://img.shields.io/github/v/release/ionic-team/live-update-provider-sdk?label=SwiftPM)](https://github.com/ionic-team/live-update-provider-sdk/releases)
[![CocoaPods](https://img.shields.io/cocoapods/v/LiveUpdateProvider?label=CocoaPods)](https://cocoapods.org/pods/LiveUpdateProvider)
[![Maven Central](https://img.shields.io/maven-central/v/io.ionic/liveupdateprovider?label=Maven%20Central)](https://central.sonatype.com/artifact/io.ionic/liveupdateprovider)

Live Update Provider is the shared iOS and Android contract that lets [Ionic Portals](https://ionic.io/docs/portals/) and [Federated Capacitor](https://ionic.io/docs/portals/for-capacitor/overview) load web assets from any external live update service, without depending on one specific backend.

## Overview

This project is centered around one main component: `ProviderManager`. A `ProviderManager` performs a sync for a single configured app instance and exposes the latest prepared app directory via `latestAppDirectory` for the host runtime to load. A provider implements the synchronization work behind it, including fetching, verifying, storing, and activating web assets.

There are two integration paths. Ionic Portals constructs and uses a `ProviderManager` directly. Federated Capacitor resolves a registered provider by ID through `LiveUpdateProvider` and `ProviderRegistry`, which then creates a `ProviderManager`.

## Installation

### iOS

Swift Package Manager:

```swift
.package(
    url: "https://github.com/ionic-team/live-update-provider-sdk.git",
    from: "0.2.0"
)
```

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

## Usage

A provider implements `ProviderManager` and ships one of two ways, depending on the host: as a native iOS/Android library for Ionic Portals, or as a Capacitor plugin for Federated Capacitor.

### Implement a manager

#### iOS

```swift
import LiveUpdateProvider

final class ExampleManager: ProviderManager {
    private let appId: String
    private(set) var latestAppDirectory: URL?

    init(appId: String) {
        self.appId = appId
    }

    func sync() async throws -> (any ProviderSyncResult)? {
        do {
            latestAppDirectory = try prepareAssets()
            return nil
        } catch {
            throw ProviderError.syncFailed(message: "Unable to sync live update assets.", underlyingError: error)
        }
    }

    private func prepareAssets() throws -> URL {
        // Fetch, validate, store, and activate provider-managed assets.
        URL(fileURLWithPath: "/path/to/latest/app")
    }
}
```

#### Android

```kotlin
import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.ProviderSyncCallback
import java.io.File

class ExampleManager(private val appId: String) : ProviderManager {
    override var latestAppDirectory: File? = null
        private set

    override fun sync(callback: ProviderSyncCallback) {
        try {
            latestAppDirectory = prepareAssets()
            callback.onSuccess(null)
        } catch (error: Throwable) {
            callback.onFailure(
                ProviderError.SyncFailed("Unable to sync live update assets.", error)
            )
        }
    }

    private fun prepareAssets(): File {
        // Fetch, validate, store, and activate provider-managed assets.
        return File("/path/to/latest/app")
    }
}
```

Kotlin callers can use the suspending `ProviderManager.sync()` extension from the `io.ionic.liveupdateprovider.coroutines` package instead of the callback API.

### Ionic Portals

A Portals integration uses the manager directly — no provider type or registry is involved. In your native app, construct your `ProviderManager` and attach it to the Portal's configuration. Portals reads `latestAppDirectory` to locate the web assets and calls `sync` to refresh them.

### Federated Capacitor

Federated Capacitor resolves providers at runtime, so a provider supplies two things beyond the manager: a `LiveUpdateProvider` that creates managers from configuration, and a registration call so the runtime can find it by ID. Package this as a Capacitor plugin, registering with `ProviderRegistry` when the plugin loads. A web app installs the plugin and, for each app, selects a provider and passes its configuration.

See the [Federated Capacitor documentation](https://ionic.io/docs/portals/for-capacitor/live-updates) for more.

#### iOS

```swift
import LiveUpdateProvider

final class ExampleProvider: LiveUpdateProvider {
    let id = "example"

    func createManager(config: [String: Any]) throws -> any ProviderManager {
        guard let appId = config["appId"] as? String else {
            throw ProviderError.invalidConfiguration(message: "Missing appId.")
        }
        return ExampleManager(appId: appId)
    }
}

// In your Capacitor plugin's load():
try ProviderRegistry.shared.register(ExampleProvider())
```

#### Android

```kotlin
import android.content.Context
import io.ionic.liveupdateprovider.ProviderError
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.federatedcapacitor.LiveUpdateProvider
import io.ionic.liveupdateprovider.federatedcapacitor.ProviderRegistry

class ExampleProvider : LiveUpdateProvider {
    override val id = "example"

    override fun createManager(context: Context, config: Map<String, Any>): ProviderManager {
        val appId = config["appId"] as? String
            ?: throw ProviderError.InvalidConfiguration("Missing appId.")
        return ExampleManager(appId)
    }
}

// In your Capacitor plugin's load():
ProviderRegistry.register(ExampleProvider())
```

See the [Capacitor documentation](https://capacitorjs.com/docs/plugins/creating-plugins) for building and publishing a plugin.

## Data Flow

The host runtime integration differs by product, but both paths end with a `ProviderManager` that syncs assets and exposes `latestAppDirectory`.

### Ionic Portals

```mermaid
sequenceDiagram
    participant App as Host App
    participant Provider as Provider Code
    participant Manager as ProviderManager
    participant Portal as Portal Runtime

    App->>Provider: create/configure manager
    Provider-->>App: manager
    App->>Portal: attach manager
    Portal->>Manager: latestAppDirectory
    Portal->>Manager: sync()
    Manager-->>Portal: sync result
```

### Federated Capacitor

```mermaid
sequenceDiagram
    participant Plugin as Provider Plugin
    participant Registry as ProviderRegistry
    participant Runtime as Federated Capacitor Runtime
    participant Provider as LiveUpdateProvider
    participant Manager as ProviderManager

    Plugin->>Registry: register(provider)
    Runtime->>Registry: require(providerId)
    Registry-->>Runtime: provider
    Runtime->>Provider: createManager(config)
    Provider-->>Runtime: manager
    Runtime->>Manager: sync()
    Manager-->>Runtime: sync result
    Runtime->>Manager: latestAppDirectory
```

## Provider Responsibilities

- Keep `latestAppDirectory` pointed at the latest valid app directory. Do not point it at partial, invalid, or rolled-back assets.
- Own service-specific behavior such as authentication, artifact verification, cleanup, and rollback.

## Related Resources

- [Reference provider implementation](https://github.com/ionic-team/live-update-provider-mock)
- [Building a backend service](docs/live-update-service-architecture.md)

## License

Released under the MIT License. See [LICENSE](LICENSE).