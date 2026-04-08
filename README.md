# Live Updates Provider SDK

Provider-facing SDK contracts for integrating custom Live Updates services with Ionic Portals and Federated Capacitor.

## Why This Exists

Ionic Appflow is being sunset, which impacts customers using Ionic Live Updates to deliver updated web assets to mobile apps.

Standard Capacitor apps can already integrate alternative Live Updates services without Ionic infrastructure changes. However, Portals and Federated Capacitor historically depended on Ionic/Appflow-specific behavior.

This SDK introduces a provider abstraction so Portals and Federated Capacitor can depend on a stable provider contract rather than provider-specific logic.

## What You Implement

All providers implement manager sync behavior that fetches, stores, and activates web assets.

Provider responsibilities:
- Keep `latestAppDirectory` accurate at all times
- Ensure `latestAppDirectory` is correct when a manager is created
- Update `latestAppDirectory` before sync returns when new assets are downloaded
- Clean up unused disk assets

## Integration Targets

### Portals

To support Portals, implement manager contracts:
- iOS: `LiveUpdateManaging`
- Android: `LiveUpdateProviderManager`

### Federated Capacitor

To support Federated Capacitor, implement provider + manager contracts:
- iOS: `LiveUpdateProviding` + `LiveUpdateManaging`
- Android: `LiveUpdateProvider` + `LiveUpdateProviderManager`

Additional Federated Capacitor requirements:
- Package your provider as a Capacitor plugin
- Register your provider in `LiveUpdateProviderRegistry` on plugin load
- If you want sync metadata returned to JS, return `FederatedCapacitorSyncResult`

## Platform Packages

### iOS

- Source: `ios/Sources/LiveUpdateProvider`
- Package manager support:
  - Swift Package Manager (`Package.swift`)
  - CocoaPods (`LiveUpdateProvider.podspec`)

### Android

- Source: `android/live-update-provider`
- Maven coordinates: `io.ionic:liveupdateprovider:<version>`
- Android-specific setup and publishing details: [android/README.md](android/README.md)

## Repository Layout

- `ios/`: iOS SDK source and tests
- `android/`: Android SDK source, tests, and publishing config
- `scripts/`: helper scripts (including Android publish helper)
- `ionic-live-updates-architecture-customer.md`: architecture guidance for teams building a service

## Service Architecture Guidance

As Appflow approaches sunset, teams that plan to build and operate their own Live Updates backend can use the architecture guidance document for implementation planning.

The document covers architecture, data requirements, security considerations, and operational responsibilities.

- Live Updates Service Architecture Overview: `ionic-live-updates-architecture-customer.md`

## Versioning

Pre-1.0 releases may include API refinements as implementation feedback is incorporated.

## License

See [License](License).
