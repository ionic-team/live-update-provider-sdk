# Live Updates Provider SDK

## Getting Started

This SDK defines contracts. To use it, create your own provider package that implements the protocols/interfaces in the iOS and Android SDKs.

### Core implementation (all providers)

- Implement manager sync logic that fetches and activates new web assets.
- Keep `latestAppDirectory` accurate:
  - correct when a manager is created
  - updated before `sync` returns when new assets are applied
- Clean up unused disk assets.

### Portals support requirements

To support Portals, a provider must implement a manager interface.

- iOS: implement `LiveUpdateManaging`
- Android: implement `LiveUpdateProviderManager`

### Federated Capacitor support requirements

To support Federated Capacitor, a provider must:

- implement provider + manager contracts
  - iOS: `LiveUpdateProviding` + `LiveUpdateManaging`
  - Android: `LiveUpdateProvider` + `LiveUpdateProviderManager`
- ensure manager state is accurate on creation (`latestAppDirectory` points to the latest active bundle)
- package itself as a Capacitor plugin
- register its provider in `LiveUpdateProviderRegistry` on plugin load

If sync is used for Federated Capacitor and you want to return metadata to the JS layer, return `FederatedCapacitorSyncResult` with optional metadata.

## Additional Documentation

- [Android Implementation](android/README.md)
- iOS Implementation: see `ios/Sources/LiveUpdateProvider`

## Service Architecture

As Ionic Appflow is being sunset, teams may want to build and operate their own Live Updates service.
If you are planning that path, this document provides guidance on architecture, responsibilities, and integration expectations.

Live Updates Service Architecture Overview: [Ionic Live Updates Architecture](ionic-live-updates-architecture-customer.md)
