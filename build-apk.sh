#!/usr/bin/env bash
set -e
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" >/dev/null
gradle assembleDebug --no-daemon
printf '\nAPK جاهز: %s\n' "$PWD/app/build/outputs/apk/debug/app-debug.apk"
