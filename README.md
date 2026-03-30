# Live Updates Provider SDK

This repository contains the Live Updates Provider API for Android, which defines an abstraction layer for live update implementations.

## Project Structure

```
live-update-provider-sdk/
├── android/                  # Android implementation
│   ├── live-update-provider/
│   └── README.md
└── scripts/                  # Publishing scripts
```

## Documentation

- [Android Implementation](android/README.md)

## Overview

The Live Updates Provider API defines a standard interface for live update implementations across platforms:

- **LiveUpdatesProvider**: Creates manager instances for configured apps
- **LiveUpdatesManager**: Handles sync operations for a single app
- **LiveUpdatesRegistry**: Thread-safe registry for provider registration and lookup
- **ProviderConfig**: Opaque configuration passed to providers
- **SyncResult**: Result of sync operations

Each platform implementation provides a default Ionic provider that integrates with Ionic Appflow.

## License

Copyright © 2026 Ionic
