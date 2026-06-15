# Releasing

This document is for maintainers publishing the Live Update Provider SDK.

## iOS

iOS publishing is tag-driven because `LiveUpdateProvider.podspec` uses the package version as the source tag.

1. Update `LiveUpdateProvider.podspec`.
2. Update README installation examples if the public version changes.
3. Merge the release PR.
4. Push a plain version tag that exactly matches the podspec version.
5. The iOS publish workflow validates the tag against the podspec version, runs iOS checks, and publishes to CocoaPods.

## Android

Android publishing is manually triggered from GitHub Actions. The publish script reads the version from `android/package.json`.

1. Update `android/package.json`.
2. Update README installation examples if the public version changes.
3. Merge the release PR.
4. Run the Android publish workflow manually from the intended release ref.

If `main` has moved after the release PR merge, select a branch or tag that points to the intended release commit before running the workflow.
