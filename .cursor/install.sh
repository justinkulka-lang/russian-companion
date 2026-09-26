#!/usr/bin/env bash
# Idempotent Cloud Agent bootstrap for the russian-companion Android project.
# Installs the Android SDK packages the build needs and warms the Gradle caches
# so a fresh agent can run ./gradlew assembleDebug / testDebugUnitTest offline-fast.
set -euo pipefail

ANDROID_SDK_DIR="${ANDROID_HOME:-$HOME/android-sdk}"
CMDLINE_TOOLS_VERSION="11076708"
PLATFORM="platforms;android-37.0"
BUILD_TOOLS="build-tools;36.0.0"

export ANDROID_HOME="$ANDROID_SDK_DIR"
export ANDROID_SDK_ROOT="$ANDROID_SDK_DIR"

install_cmdline_tools() {
  local sdkmanager="$ANDROID_SDK_DIR/cmdline-tools/latest/bin/sdkmanager"
  if [ -x "$sdkmanager" ]; then
    echo "Android command-line tools already present."
    return
  fi
  echo "Installing Android command-line tools..."
  mkdir -p "$ANDROID_SDK_DIR/cmdline-tools"
  local tmp
  tmp="$(mktemp -d)"
  curl -fsSL -o "$tmp/clt.zip" \
    "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"
  unzip -q "$tmp/clt.zip" -d "$tmp"
  rm -rf "$ANDROID_SDK_DIR/cmdline-tools/latest"
  mv "$tmp/cmdline-tools" "$ANDROID_SDK_DIR/cmdline-tools/latest"
  rm -rf "$tmp"
}

install_cmdline_tools

SDKMANAGER="$ANDROID_SDK_DIR/cmdline-tools/latest/bin/sdkmanager"

echo "Accepting SDK licenses..."
yes | "$SDKMANAGER" --sdk_root="$ANDROID_SDK_DIR" --licenses >/dev/null 2>&1 || true

echo "Installing SDK packages (idempotent)..."
"$SDKMANAGER" --sdk_root="$ANDROID_SDK_DIR" \
  "platform-tools" "$PLATFORM" "$BUILD_TOOLS"

# Point Gradle at the SDK without relying on env var propagation across shells.
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
echo "sdk.dir=$ANDROID_SDK_DIR" > "$REPO_ROOT/local.properties"

echo "Warming Gradle caches (Gradle distribution, main + test dependencies)..."
cd "$REPO_ROOT"
./gradlew --no-daemon assembleDebug compileDebugUnitTestKotlin --stacktrace

echo "Cloud Agent environment ready."
