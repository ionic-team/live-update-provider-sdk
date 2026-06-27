# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] — Unreleased

This is a breaking release that finalizes the public API before `1.0.0`. Consumers
must update type names and adjust to the optional sync result.

### Changed (breaking)

- Renamed the satellite contract types to drop the redundant `LiveUpdate` infix
  (the anchor `LiveUpdateProvider` is unchanged):
  - `LiveUpdateProviderManager` → `ProviderManager`
  - `LiveUpdateProviderSyncResult` → `ProviderSyncResult`
  - `LiveUpdateProviderSyncCallback` → `ProviderSyncCallback` (Android)
  - `LiveUpdateProviderError` → `ProviderError`
  - `LiveUpdateProviderRegistry` → `ProviderRegistry`
- `sync` now returns an optional result; `nil` / `null` means "nothing to report":
  - iOS: `func sync() async throws -> (any ProviderSyncResult)?`
  - Android: `ProviderSyncCallback.onSuccess(result: ProviderSyncResult?)`
- Android: the `sync` callback parameter is now non-null
  (`fun sync(callback: ProviderSyncCallback)`).
- `MetadataSyncResult` is now a single concrete type (Swift `struct` / Kotlin
  `data class`); the separate `DefaultMetadataSyncResult` is removed. Construct it
  directly: `MetadataSyncResult(metadata: …)`.
- `MetadataSyncResult.metadata` is now non-optional (`[String: Any]` / `Map<String, Any>`);
  it may be empty but is never absent.
- Android: the Federated Capacitor contracts moved to a subpackage —
  `LiveUpdateProvider` and `ProviderRegistry` are now in
  `io.ionic.liveupdateprovider.federatedcapacitor`.
- iOS sources reorganized — shared contracts at the target root, Federated Capacitor
  types under `FederatedCapacitor/`. No import change — the module name
  (`LiveUpdateProvider`) is unchanged.
- Unified the `ProviderNotRegistered` error message wording across platforms.

### Added

- Android optional Kotlin coroutines support, in the `io.ionic.liveupdateprovider.coroutines`
  subpackage of the main artifact:
  - `suspend fun ProviderManager.sync(): ProviderSyncResult?` for consumers.
  - `abstract class CoroutineProviderManager` for providers that prefer to implement
    `suspend fun performSync(): ProviderSyncResult?`.
  - This adds `kotlinx-coroutines-core` as an `api` dependency of
    `io.ionic:liveupdateprovider`; the callback contract is unchanged and Java callers
    need no coroutines code.

### Notes

- `MetadataSyncResult` is the metadata-bridge result for Federated Capacitor. A
  provider with nothing to report returns `nil` / `null`; a Portals provider with
  custom native data returns its own `ProviderSyncResult`-conforming type.

## [0.1.0] — 2026-03

- Initial release of the iOS and Android live update provider contracts.
