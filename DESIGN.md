# API Design — Live Update Provider SDK

**Status:** Accepted — implemented on `feat/api-0.2.0` (pending `0.2.0` release)
**Target release:** `0.2.0` (final breaking pre-1.0 change), stabilizing at `1.0.0`
**Scope:** iOS (Swift) + Android (Kotlin) public contract surface

This document is the agreed target design for modernizing the SDK's public API. It
consolidates decisions on naming, the sync-result contract, Android concurrency,
module organization, and the path to a stable `1.0.0`. It supersedes the current
naming and result-type shape; the conceptual model (Provider → Manager → sync →
result) is unchanged.

---

## 1. Goals and principles

1. **Contract layer only.** No networking, storage, retry, or backend behavior.
   Stability is the product (existing Architectural Tenet #1).
2. **Java-callable contract, Kotlin coroutine sugar included.** The `sync` contract is
   callback-based so it is implementable and callable from both Java and Kotlin.
   Optional Kotlin coroutine helpers ship alongside it (in the `coroutines`
   subpackage) and are never required — using the callback directly needs no
   coroutines knowledge.
3. **Cross-platform parity.** iOS and Android expose the same concepts with the
   same names and the same semantics, differing only where platform idiom requires.
4. **Minimal and hard to misuse.** No speculative generality; no API that only
   works correctly if the caller reads the docs.
5. **Two result audiences, served cleanly.** Native-to-native rich results
   (Portals) and bridge-safe metadata (Federated Capacitor) — see §3.

---

## 2. Naming

The package/module already namespaces everything (`LiveUpdateProvider` module on
iOS, `io.ionic.liveupdateprovider` package on Android), so the `LiveUpdate` infix
on every type is pure redundancy. The token that is **load-bearing** is `Provider`,
because these types coexist in consumer code with the legacy
`io.ionic.liveupdates.*` SDK (`LiveUpdateManager`, `SyncResult`, `SyncCallback`).

Decision: keep `LiveUpdateProvider` as the descriptive **anchor**, trim the
satellites to a `Provider*` prefix. Bare `Provider` is rejected — it collides with
`javax.inject.Provider` / `jakarta.inject.Provider` in the DI-heavy third-party
codebases that implement this SDK.

| Current | New |
| --- | --- |
| `LiveUpdateProvider` | `LiveUpdateProvider` *(unchanged — the anchor you implement)* |
| `LiveUpdateProviderManager` | `ProviderManager` |
| `LiveUpdateProviderSyncResult` | `ProviderSyncResult` *(marker — see §3)* |
| `MetadataSyncResult` | `MetadataSyncResult` *(now a concrete type — see §3)* |
| `DefaultMetadataSyncResult` | *(removed — folded into the concrete `MetadataSyncResult`)* |
| `LiveUpdateProviderSyncCallback` | `ProviderSyncCallback` *(Android only)* |
| `LiveUpdateProviderError` | `ProviderError` |
| `LiveUpdateProviderRegistry` | `ProviderRegistry` |

Member names (`id`, `createManager`, `latestAppDirectory`, `sync`, `metadata`) are
already good and do not change.

---

## 3. Sync result contract

The result serves two distinct audiences:

- **Portals (native → native):** a provider returns a result to native app code that
  calls `manager.sync()` directly. This needs open extensibility — providers should
  be able to return rich, custom, native objects.
- **Federated Capacitor (native → web):** the result data is bridged to JavaScript,
  so it must use bridge-safe values (strings, numbers, booleans, nulls, arrays,
  plain maps) carried in a `metadata` map.

### Two decisions

**1. `metadata` stays out of the base, on a separate `MetadataSyncResult` type.**
The type a provider returns signals the integration it targets — a Portals-only
provider works entirely with the metadata-free `ProviderSyncResult` marker and never
has to reason about a bridge concept it doesn't use; returning a `MetadataSyncResult`
self-documents "this result carries data for the web bridge." Putting `metadata` on
the base would force every Portals-only provider to implement a member they never use,
dragging a Federated-Capacitor concept into the Portals mental model. The cost it
would save — a single `as?` / `instanceof` check in the bridge — is trivial and
semantically honest, since not every result has metadata. (Collapsing to one flat
struct is also rejected: it would remove the Portals native-passthrough — a provider
could no longer return a rich custom conforming object.)

**2. "Nothing to report" is modeled as an optional result, not a shipped empty type.**
"Nothing to report" is *absence*, which Swift/Kotlin already model as `nil`. A shipped
empty `DefaultSyncResult` would be a Null-Object sentinel for what the language
expresses natively, so `sync` returns an **optional** result instead.

**3. `MetadataSyncResult` is a single concrete type, not a protocol + default.**
Each result shape serves exactly one audience: a Portals result is the provider's own
`ProviderSyncResult` type (no metadata), a Federated Capacitor result is
`MetadataSyncResult` (metadata only). Keeping `MetadataSyncResult` a *protocol* would
let one type serve both runtimes at once, but since there's a single return value,
that unified type necessarily carries the *other* audience's concern (a Portals caller
sees `metadata`; the bridge sees ignored native fields). That dual-runtime-single-type
case is hypothetical — providers already have per-runtime code paths (FedCap needs the
Capacitor-plugin + registry wrapper; Portals just needs the manager), so they return
the right shape per path. A concrete keeps each shape's concerns clean and drops a
type. If a real dual need ever appears, a metadata protocol can be added additively
later (the concrete conforms to it) — whereas removing the protocol would be breaking.

### Type hierarchy

```text
ProviderSyncResult       (marker — sync's return type; Portals extension point)
└── MetadataSyncResult   (concrete; carries bridge-safe metadata — FedCap)
```

| Case | Provider returns |
| --- | --- |
| Portals — nothing to report | `nil` |
| Portals — typed native data | provider's own `ProviderSyncResult` type |
| FedCap — metadata for the bridge | `MetadataSyncResult(metadata: …)` |

### iOS

```swift
/// Marker for provider-defined sync results. Providers may return a custom
/// conforming type to expose richer native data to direct (Portals) callers.
public protocol ProviderSyncResult {}

/// Built-in result carrying bridge-safe metadata for host integrations
/// (e.g. Federated Capacitor → JavaScript). Use only strings, numbers, booleans,
/// nulls, arrays, and plain dictionaries.
public struct MetadataSyncResult: ProviderSyncResult {
    public let metadata: [String: Any]
    public init(metadata: [String: Any]) {
        self.metadata = metadata
    }
}
```

### Android

```kotlin
/** Marker for provider-defined sync results. */
interface ProviderSyncResult

/** Built-in result carrying bridge-safe metadata for host integrations. */
data class MetadataSyncResult(
    val metadata: Map<String, Any>
) : ProviderSyncResult
```

### Consumer impact

- **Portals** providers return `nil` (nothing to report) or a custom conforming type,
  and never touch metadata.
- **Federated Capacitor** keeps its `result as? MetadataSyncResult` /
  `instanceof MetadataSyncResult` check (see [FedCapPlugin.java:333]) — retained and
  correct — and now also handles a `nil` result as "no metadata." Once cast, `metadata`
  is non-null (may be empty), so the inner `metadata != null` check is dropped.

---

## 4. Manager and sync

### iOS — already modern

`async throws`, returning an **optional** result existential (`nil` = nothing to
report). No change beyond the rename.

```swift
public protocol ProviderManager {
    var latestAppDirectory: URL? { get }
    func sync() async throws -> (any ProviderSyncResult)?
}

public protocol LiveUpdateProvider {
    var id: String { get }
    func createManager(config: [String: Any]) throws -> any ProviderManager
}
```

### Android — callback primitive + coroutines adapter

The core contract stays callback-based because a real Java consumer
(`FedCapPlugin.java`) exists and `suspend` is hostile to Java callers; the callback
also maps cleanly onto Capacitor's async `PluginCall`. The callback parameter becomes
**non-null** (the previous `?` only enabled silently dropping the result); the result
delivered to `onSuccess` is **nullable** (`null` = nothing to report).

```kotlin
// Core artifact: io.ionic:liveupdateprovider  (zero dependencies)
interface ProviderManager {
    val latestAppDirectory: File?
    fun sync(callback: ProviderSyncCallback)
}

interface ProviderSyncCallback {
    fun onSuccess(result: ProviderSyncResult?)
    fun onFailure(error: ProviderError.SyncFailed)
}
```

Providers must invoke exactly one terminal callback (`onSuccess` or `onFailure`) per
`sync`. The SDK does not require a specific callback thread.

### Android coroutines layer — same artifact (`io.ionic.liveupdateprovider.coroutines`)

Kotlin ergonomics ship in the main artifact, in the `coroutines` subpackage. This
adds `kotlinx-coroutines-core` as an `api` dependency of `io.ionic:liveupdateprovider`,
so every consumer — including the Java `FedCapPlugin` — inherits it transitively.

Decision: a separate `-coroutines` artifact would keep the core zero-dependency (the
textbook `-ktx` pattern), but for this SDK that split isn't worth a second published
Maven coordinate to exclude `kotlinx-coroutines` — the single most common Android
dependency, which Portals and most FedCap host apps already pull in. Java consumers
inherit the jar at runtime but reference no coroutine code. The contract stays
callback-based, so Java interop is unaffected.

```kotlin
// Consumer ergonomics — parity with iOS's `async sync()`.
suspend fun ProviderManager.sync(): ProviderSyncResult? =
    suspendCancellableCoroutine { cont ->
        sync(object : ProviderSyncCallback {
            override fun onSuccess(result: ProviderSyncResult?) {
                if (cont.isActive) cont.resume(result)
            }
            override fun onFailure(error: ProviderError.SyncFailed) {
                if (cont.isActive) cont.resumeWithException(error)
            }
        })
    }

// Implementer ergonomics — write suspend, satisfy the callback contract for free.
abstract class CoroutineProviderManager(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : ProviderManager {
    abstract suspend fun performSync(): ProviderSyncResult?

    final override fun sync(callback: ProviderSyncCallback) {
        scope.launch {
            try {
                callback.onSuccess(performSync())
            } catch (e: CancellationException) {
                throw e
            } catch (e: ProviderError.SyncFailed) {
                callback.onFailure(e)
            } catch (e: Throwable) {
                callback.onFailure(ProviderError.SyncFailed(e.message ?: "Sync failed", e))
            }
        }
    }
}
```

Note: the callback contract has no cancellation signal, so cancelling the coroutine
cannot stop in-flight provider work — the `isActive` guard makes a late callback a
no-op. Propagating real cancellation would be a separate, additive contract change.

---

## 5. Errors

Keep the platform-idiomatic shapes (iOS enum, Android sealed-interface-of-exceptions).
Rename to `ProviderError`. Unify the cross-platform message wording (e.g.
`ProviderNotRegistered` reads "Live update provider '<id>' is not registered." on
both platforms).

```swift
public enum ProviderError: Error {
    case providerNotRegistered(String)
    case invalidConfiguration(String, underlyingError: Error?)
    case syncFailed(String, underlyingError: Error?)
}
```

```kotlin
sealed interface ProviderError {
    class InvalidConfiguration(val details: String, cause: Throwable? = null) :
        Exception("Invalid configuration: $details", cause), ProviderError
    class SyncFailed(val details: String, cause: Throwable? = null) :
        Exception("Sync failed: $details", cause), ProviderError
    class ProviderNotRegistered(val providerId: String) :
        Exception("Live update provider '$providerId' is not registered."), ProviderError
}
```

Polish: surface `underlyingError` in the iOS `errorDescription` (or document why it
is intentionally omitted) so iOS reaches parity with Android's native `cause` chain.

---

## 6. Registry (Federated Capacitor only)

No behavioral change beyond the rename. iOS singleton (`ProviderRegistry.shared`,
`NSLock`), Android `object` (`ConcurrentHashMap`, `@JvmStatic`). Rejects blank and
duplicate IDs; `require` throws `ProviderNotRegistered`.

Optional testability improvement (decide separately): an internal/test-only `reset()`
so tests stop relying on UUID-unique IDs to avoid cross-test contamination of global
state.

---

## 7. Module and file organization

The audience split is two buckets, not three: **shared core** + **Federated
Capacitor extras** (`LiveUpdateProvider` + `ProviderRegistry`). Portals uses a strict
subset of the shared core and has no exclusive types. The boundary is expressed
through file/package layout + documentation — not separate artifacts (the error type
is a single type and the result hierarchy refines a shared protocol, so hard module
splits would fight the type system).

### iOS (one SPM module — reorg is free, imports unchanged)

Shared contracts sit at the target root (mirroring Android, where shared types live
in the root package and only Federated Capacitor is namespaced); Federated Capacitor
extras are grouped under `FederatedCapacitor/`.

```
ios/Sources/LiveUpdateProvider/
  Manager.swift          // ProviderManager
  SyncResult.swift       // ProviderSyncResult, MetadataSyncResult
  Errors.swift           // ProviderError
  FederatedCapacitor/
    Provider.swift       // LiveUpdateProvider
    Registry.swift       // ProviderRegistry
```

### Android (single artifact; subpackage move is a breaking import change — bundle into 0.2.0)

One published artifact (`io.ionic:liveupdateprovider`), depending on
`kotlinx-coroutines-core` (`api`). Organized by Kotlin subpackage:

```
android/live-update-provider/   # the one published artifact
  io.ionic.liveupdateprovider               # ProviderManager, ProviderSyncCallback,
                                            #   ProviderSyncResult, MetadataSyncResult,
                                            #   ProviderError
  io.ionic.liveupdateprovider.federatedcapacitor    # LiveUpdateProvider, ProviderRegistry
  io.ionic.liveupdateprovider.coroutines    # suspend sync(), CoroutineProviderManager
```

---

## 8. "Modern package" infrastructure

These enforce the contract discipline a 1.0 implies. Status noted per item.

- **Maven Central publishing via `com.vanniktech.maven.publish`** *(done)* — Central
  Portal, in-memory signing, sources + Javadoc jars; version in `gradle.properties`.
- **`CHANGELOG.md`** *(done)*. A SemVer policy section in `RELEASING.md` is still pending.
- **Kotlin `explicitApi()`** on the library module — forces explicit visibility and
  return types across the public API. *(pending)*
- **Binary/API compatibility checking:** Kotlin binary-compatibility-validator
  (checked-in `.api` files); `swift package diagnose-api-breaking-changes` on iOS in
  CI. *(pending — the most 1.0-defining item)*
- **Lint/format in CI:** SwiftFormat/SwiftLint + ktlint. *(pending)*
- **Version-consistency CI check** across `LiveUpdateProvider.podspec`,
  `android/gradle.properties` (`VERSION_NAME`), and README install snippets. *(pending)*
- **`Sendable` annotations on the iOS protocols** for Swift strict-concurrency
  readiness — defer unless targeting Swift 6 mode, as it constrains implementers. *(optional)*

---

## 9. Versioning and migration

1. Land everything here as one breaking **`0.2.0`** — the last cheap window for
   breaking changes before stabilizing.
2. Migrate first-party consumers (mechanical, ~4 files):
   - `ionic-portals-android` — `Portal.kt` (`setLiveUpdateProviderManager`).
   - `enterprise-capacitor-portals` — `FedCapPlugin.java` (type renames only; the
     `instanceof MetadataSyncResult` check is retained and correct).
   - `live-update-provider-mock` — adopt `CoroutineProviderManager` (drop the raw
     `Thread {}` + `Handler` in `LiveUpdateProviderMockManager.kt`).
3. Resolve the install-range inconsistency (SPM `from:` is liberal, CocoaPods
   `~> 0.2.0` and Gradle pin are conservative).
4. Cut **`1.0.0`** and adopt strict SemVer (breaking change ⇒ major).

This SDK versions independently of the Portals host SDKs; it does not inherit the
"minor-only" convention. As a contract layer, honest majors are a feature.

---

## 10. Open decisions

- **Registry test-reset** (§6): add internal `reset()` or leave UUID-based test
  isolation as-is.
- **`Sendable` / Swift 6 strict concurrency** (§8): adopt now or defer.

Resolved: coroutines ship in the main artifact (not a separate `-coroutines`
artifact) — see §4.
