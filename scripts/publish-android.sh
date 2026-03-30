#!/usr/bin/env bash

ANDROID_DIR=../android
LOG_OUTPUT=./tmp/live-update-provider-sdk-android.txt
LIVE_UPDATE_PROVIDER_SDK_VERSION=`grep '"version": ' $ANDROID_DIR/package.json | awk '{print $2}' | tr -d '",'`

# Get latest io.ionic:liveupdateprovider XML version info
LIVE_UPDATE_PROVIDER_PUBLISHED_URL="https://repo1.maven.org/maven2/io/ionic/liveupdateprovider/maven-metadata.xml"
LIVE_UPDATE_PROVIDER_PUBLISHED_DATA=$(curl -s $LIVE_UPDATE_PROVIDER_PUBLISHED_URL)
LIVE_UPDATE_PROVIDER_PUBLISHED_VERSION="$(perl -ne 'print and last if s/.*<latest>(.*)<\/latest>.*/\1/;' <<< $LIVE_UPDATE_PROVIDER_PUBLISHED_DATA)"

if [[ "$LIVE_UPDATE_PROVIDER_SDK_VERSION" == "$LIVE_UPDATE_PROVIDER_PUBLISHED_VERSION" ]]; then
    printf %"s\n\n" "Duplicate: a published Live Update Provider SDK exists for version $LIVE_UPDATE_PROVIDER_SDK_VERSION, skipping..."
else
    # Make log dir if doesnt exist
    mkdir -p ./tmp

    # Export ENV variable used by Gradle for Versioning
    export LIVE_UPDATE_PROVIDER_SDK_VERSION
    export LIVE_UPDATE_PROVIDER_PUBLISH=true

    printf %"s\n" "Attempting to build and publish Live Update Provider SDK version $LIVE_UPDATE_PROVIDER_SDK_VERSION"
    $ANDROID_DIR/gradlew -p $ANDROID_DIR clean build publishReleasePublicationToSonatypeRepository closeAndReleaseSonatypeStagingRepository --max-workers 1 -Pandroid.useAndroidX=true -Pandroid.enableJetifier=true > $LOG_OUTPUT 2>&1

    echo $RESULT

    if grep --quiet "BUILD SUCCESSFUL" $LOG_OUTPUT; then
        printf %"s\n" "Success: Live Update Provider SDK published to MavenCentral."
    else
        printf %"s\n" "Error publishing, check $LOG_OUTPUT for more info! Manually review and release from the Sonatype Repository Manager may be necessary https://s01.oss.sonatype.org/"
        cat $LOG_OUTPUT
        exit 1
    fi

fi
