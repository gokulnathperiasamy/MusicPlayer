#!/bin/bash

# Shell Script to generate builds in Mac.
# Before running the script do chmod +x build.sh
# Generates Release builds with Release Key configured in build.gradle file.
# APKs will be available under ../app/build/outputs/apk

# Run as ./build.sh from Terminal in Mac (If error, check JDK is set to Java1.8)

chmod +x gradlew

./gradlew clean
./gradlew build
./gradlew uploadProdConfigReleaseToHockeyApp