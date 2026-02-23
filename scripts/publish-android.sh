#!/usr/bin/env bash

ANDROID_DIR=../android
LOG_OUTPUT=./tmp/live-updates-provider-android.txt

# Get version from npm package.json in Android dir
LIVEUPDATESPROVIDER_VERSION=`grep '"version": ' $ANDROID_DIR/package.json | awk '{print $2}' | tr -d '",'`

# Get latest io.ionic:live-updates-provider XML version info
LIVEUPDATESPROVIDER_PUBLISHED_URL="https://repo1.maven.org/maven2/io/ionic/live-updates-provider/maven-metadata.xml"
LIVEUPDATESPROVIDER_PUBLISHED_DATA=$(curl -s $LIVEUPDATESPROVIDER_PUBLISHED_URL)
LIVEUPDATESPROVIDER_PUBLISHED_VERSION="$(perl -ne 'print and last if s/.*<latest>(.*)<\/latest>.*/\1/;' <<< $LIVEUPDATESPROVIDER_PUBLISHED_DATA)"

if [[ "$LIVEUPDATESPROVIDER_VERSION" == "$LIVEUPDATESPROVIDER_PUBLISHED_VERSION" ]]; then
    printf %"s\n\n" "Duplicate: a published Live Updates exists for version $LIVEUPDATESPROVIDER_VERSION, skipping..."
else
    # Make log dir if doesnt exist
    mkdir -p ./tmp

    # Export ENV variable used by Gradle for Versioning
    export LIVEUPDATESPROVIDER_VERSION
    export LIVEUPDATESPROVIDER_PUBLISH=true

    printf %"s\n\n" "Attempting to publish new Live Updates Provider for version $LIVEUPDATESPROVIDER_VERSION"
    $ANDROID_DIR/gradlew -p $ANDROID_DIR clean build publishReleasePublicationToSonatypeRepository closeAndReleaseSonatypeStagingRepository --max-workers 1 -Pandroid.useAndroidX=true -Pandroid.enableJetifier=true > $LOG_OUTPUT 2>&1

    echo $RESULT

    if grep --quiet "BUILD SUCCESSFUL" $LOG_OUTPUT; then
        printf %"s\n" "Success: Ionic Live Updates Provider published to MavenCentral."
    else
        printf %"s\n" "Error publishing, check $LOG_OUTPUT for more info! Manually review may be necessary in the Sonatype Repository Manager https://s01.oss.sonatype.org/"
        cat $LOG_OUTPUT
        exit 1
    fi

fi
