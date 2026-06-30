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

Android publishing is manually triggered from GitHub Actions and publishes to Maven Central via the Central Portal, using the [`com.vanniktech.maven.publish`](https://vanniktech.github.io/gradle-maven-publish-plugin/) Gradle plugin.

1. Update `VERSION_NAME` in `android/gradle.properties`.
2. Update README installation examples if the public version changes.
3. Merge the release PR.
4. Run the Android publish workflow manually from the intended release ref.

The workflow runs `./gradlew :live-update-provider:publishToMavenCentral`, which builds the AAR with sources and Javadoc jars, signs them, and (automatic release is enabled) publishes to Maven Central. Re-running for an already-published version fails — bump `VERSION_NAME` first.

If `main` has moved after the release PR merge, select a branch or tag that points to the intended release commit before running the workflow.
