# Live Update Service Architecture Guide

This guide is optional background for teams building or adapting a live update backend service. The Live Update Provider SDK does not define a required backend API, manifest format, storage layout, rollout model, or update protocol.

Use this document as a starting point when you do not already have a service architecture. Existing live update services can use different APIs and still implement the provider contracts in this SDK.

## Scope

A live update service commonly coordinates over-the-air updates for web assets such as HTML, CSS, JavaScript, images, and fonts. Native code, native plugins, app permissions, and binary dependencies still require an app store release.

The provider implementation is responsible for translating its service-specific API into the SDK contracts used by Ionic Portals or Federated Capacitor.

## Typical Architecture

```text
Mobile app
  |
  | check for update
  v
Provider API service
  |
  | signed or authorized artifact URL
  v
CDN / object storage
  |
  | download artifact
  v
Provider implementation on device
  |
  | validate, extract, activate
  v
latestAppDirectory
```

Common service components include:

- API service for update checks, channel resolution, and compatibility decisions.
- Database for apps, channels, builds, artifact metadata, and rollout state.
- Object storage for update artifacts.
- CDN for artifact delivery.
- Authentication and authorization for protected apps or tenants.
- Observability for update checks, downloads, failures, and rollback events.

## Typical Update Flow

### Check for update

The provider implementation sends service-specific device and app context, such as platform, binary version, current update ID, channel, tenant, or app ID.

The service usually responds with one of these outcomes:

- No update is available.
- An update is available and compatible.
- An update exists but is not compatible with the current binary or platform.
- The request is unauthorized or invalid.

### Download artifact

The service may return a direct artifact URL, an authenticated endpoint, or a short-lived signed URL. The provider implementation should treat the artifact URL and any associated credentials as sensitive.

### Prepare assets

The provider implementation downloads, validates, extracts, and stages the update before changing `latestAppDirectory`. A sync success should not point the host runtime at partially prepared assets.

### Activate and recover

Activation and rollback policy belong to the provider and host integration. A robust provider preserves enough state to recover from failed downloads, failed validation, extraction errors, and failed activation.

## Example API Shape

This example is illustrative only. It is not required by the SDK.

```http
POST /apps/{app_id}/channels/check-device
```

```json
{
  "device_id": "unique-device-identifier",
  "channel_name": "production",
  "platform": "ios",
  "platform_version": "17.0",
  "binary_version": "1.2.3",
  "current_update_id": "current-version-id"
}
```

```json
{
  "available": true,
  "compatible": true,
  "update_id": "new-version-id",
  "artifact_url": "https://cdn.example.com/apps/app-id/updates/new-version-id.zip",
  "integrity": "sha256-example"
}
```

## Data Model Considerations

A service built from scratch often tracks:

- Applications: app identity, organization ownership, and settings.
- Channels: deployment lanes such as development, staging, and production.
- Builds or updates: source commit, binary compatibility, artifact references, and rollout state.
- Artifacts: storage location, size, checksum, signature, and format.
- Devices or installations: current update, channel assignment, and eligibility.

These entities are examples, not SDK requirements.

## Security Considerations

Provider services and provider implementations should consider:

- HTTPS for API and artifact traffic.
- Tenant, organization, app, and channel authorization.
- Short-lived signed URLs or equivalent protected artifact access.
- Artifact signatures or checksums before activation.
- Archive traversal protection during extraction.
- Compatibility checks for platform, binary version, and artifact format.
- Careful handling of tokens, signed URLs, credentials, and internal service details.

Do not expose secrets through `MetadataSyncResult`, SDK error messages, logs, or JavaScript-visible metadata.

## Reliability Considerations

A production provider should account for:

- No-update paths.
- Interrupted downloads.
- Partial or corrupt artifacts.
- Insufficient disk space.
- Atomic staging and activation.
- Rollback or recovery from failed activation.
- Cleanup of unused assets.
- Retry and backoff behavior.

`latestAppDirectory` should only be updated after the provider has prepared assets that the host runtime can load.

## Rollout and Operations

Common rollout practices include:

- Channels for staged releases.
- Compatibility constraints tied to binary versions.
- Canary or percentage rollouts.
- Fast rollback to a known-good update.
- Metrics for check frequency, download success, sync success, sync failure, latency, and rollback.
- Audit history for channel assignments and artifact publication.

## Build Pipeline Considerations

A typical build pipeline may:

1. Build web assets.
2. Generate artifact metadata and integrity hashes.
3. Optionally sign artifacts.
4. Upload artifacts to object storage.
5. Register the update with the service.
6. Assign the update to one or more channels.

Providers can implement different pipelines as long as the on-device provider can prepare a valid latest app directory for the host runtime.
