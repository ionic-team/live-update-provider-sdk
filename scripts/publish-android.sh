#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
ANDROID_DIR="$REPO_ROOT/android"
LOG_DIR="$SCRIPT_DIR/tmp"
LOG_OUTPUT="$LOG_DIR/live-update-provider-sdk-android.txt"
LIVE_UPDATE_PROVIDER_SDK_VERSION="$(node -e "console.log(require(process.argv[1]).version)" "$ANDROID_DIR/package.json")"

LIVE_UPDATE_PROVIDER_PUBLISHED_URL="https://repo1.maven.org/maven2/io/ionic/liveupdateprovider/maven-metadata.xml"
LIVE_UPDATE_PROVIDER_ALREADY_PUBLISHED=false

if LIVE_UPDATE_PROVIDER_PUBLISHED_DATA="$(curl --fail --silent --show-error --location "$LIVE_UPDATE_PROVIDER_PUBLISHED_URL")"; then
    if grep -Fq "<version>$LIVE_UPDATE_PROVIDER_SDK_VERSION</version>" <<< "$LIVE_UPDATE_PROVIDER_PUBLISHED_DATA"; then
        LIVE_UPDATE_PROVIDER_ALREADY_PUBLISHED=true
    fi
else
    printf "%s\n" "Unable to read published Maven metadata. Continuing with publish attempt for version $LIVE_UPDATE_PROVIDER_SDK_VERSION."
fi

if [[ "$LIVE_UPDATE_PROVIDER_ALREADY_PUBLISHED" == "true" ]]; then
    printf "%s\n\n" "Duplicate: a published Live Update Provider SDK exists for version $LIVE_UPDATE_PROVIDER_SDK_VERSION, skipping."
else
    mkdir -p "$LOG_DIR"

    export LIVE_UPDATE_PROVIDER_SDK_VERSION
    export LIVE_UPDATE_PROVIDER_PUBLISH=true

    printf "%s\n" "Attempting to build and publish Live Update Provider SDK version $LIVE_UPDATE_PROVIDER_SDK_VERSION"

    if "$ANDROID_DIR/gradlew" -p "$ANDROID_DIR" clean build publishReleasePublicationToSonatypeRepository closeAndReleaseSonatypeStagingRepository --max-workers 1 > "$LOG_OUTPUT" 2>&1; then
        printf "%s\n" "Success: Live Update Provider SDK published to MavenCentral."
    else
        printf "%s\n" "Error publishing, check $LOG_OUTPUT for more info. Manually review and release from the Sonatype Repository Manager may be necessary: https://s01.oss.sonatype.org/"
        cat "$LOG_OUTPUT"
        exit 1
    fi
fi
