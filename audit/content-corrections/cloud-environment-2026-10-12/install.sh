#!/usr/bin/env bash
set -euo pipefail
mkdir -p /workspace/setup /workspace/android-sdk/cmdline-tools
cat > /workspace/setup/android-env.sh <<'ANDROID_ENV'
export JAVA_HOME=/workspace/jdk21
export ANDROID_HOME=/workspace/android-sdk
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export GRADLE_USER_HOME=/workspace/.gradle
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/19.0/bin:$ANDROID_HOME/platform-tools:$PATH"
proxy_java_options=$(python3 - <<'PY'
import os, urllib.parse
p=urllib.parse.urlparse(os.environ.get('HTTPS_PROXY',''))
if p.hostname:
 print(f'-Dhttps.proxyHost={p.hostname} -Dhttps.proxyPort={p.port or 80} -Dhttp.proxyHost={p.hostname} -Dhttp.proxyPort={p.port or 80} -Dhttp.nonProxyHosts=localhost|127.*')
PY
)
export JAVA_TOOL_OPTIONS="$proxy_java_options"
unset proxy_java_options
export ANDROID_USER_HOME=/workspace/.android
export ANDROID_PREFS_ROOT=/workspace
mkdir -p "$ANDROID_USER_HOME"
export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts"
export PYTHONDONTWRITEBYTECODE=1
ANDROID_ENV
if [ ! -x /workspace/jdk21/bin/javac ]; then
  jdk_archive=$(mktemp /tmp/jdk21.XXXXXX.tar.gz)
  jdk_checksum=$(mktemp /tmp/jdk21.XXXXXX.sha256)
  curl -fsSL https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz.sha256 -o "$jdk_checksum"
  curl -fsSL https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz -o "$jdk_archive"
  printf '%s  %s\n' "$(cat "$jdk_checksum")" "$jdk_archive" | sha256sum -c -
  mkdir -p /workspace/jdk21
  tar -xzf "$jdk_archive" --strip-components=1 -C /workspace/jdk21
  rm -f "$jdk_archive" "$jdk_checksum"
fi
source /workspace/setup/android-env.sh
if [ ! -x "$ANDROID_HOME/cmdline-tools/19.0/bin/sdkmanager" ]; then
  sdk_zip=$(mktemp /tmp/android-cli.XXXXXX.zip)
  sdk_extract=$(mktemp -d /tmp/android-cli.XXXXXX)
  curl -fsSL https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip -o "$sdk_zip"
  printf '5fdcc763663eefb86a5b8879697aa6088b041e70  %s\n' "$sdk_zip" | sha1sum -c -
  unzip -q "$sdk_zip" -d "$sdk_extract"
  mv "$sdk_extract/cmdline-tools" "$ANDROID_HOME/cmdline-tools/19.0"
  rm -f "$sdk_zip"
  rmdir "$sdk_extract"
fi
# Avoid a yes-process SIGPIPE obscuring the SDK manager exit status.
set +o pipefail
yes | sdkmanager --sdk_root="$ANDROID_HOME" --licenses > /workspace/setup/sdk-licenses.log
license_status=${PIPESTATUS[1]}
set -o pipefail
[ "$license_status" -eq 0 ]
sdkmanager --sdk_root="$ANDROID_HOME" --install 'platforms;android-37.0' 'build-tools;36.0.0' platform-tools
mkdir -p /workspace/java-user-home
cat > /workspace/setup/cloud-validation.gradle <<'GRADLE_INIT'
allprojects {
    tasks.withType(Test).configureEach {
        systemProperty 'user.home', '/workspace/java-user-home'
        systemProperty 'maven.repo.local', '/workspace/.m2/repository' 
        // These screenshot exporters overwrite retained evidence and write to /opt/cursor.
        exclude '**/VisualReviewCaptureTest*', '**/Units35ChunkCaptureTest*', '**/Units610ChunkCaptureTest*', '**/Units1115GapCaptureTest*'
        systemProperty 'robolectric.dependency.repo.url', 'https://repo.maven.apache.org/maven2'
        ['https.proxyHost','https.proxyPort','http.proxyHost','http.proxyPort','http.nonProxyHosts','javax.net.ssl.trustStore'].each { name ->
            def value = System.getProperty(name)
            if (value != null) systemProperty name, value
        }
    }
}
GRADLE_INIT
cd /workspace/russian-companion
./gradlew -I /workspace/setup/cloud-validation.gradle compileDebugSources lintDebug verifyReleaseGateAttachment testDebugUnitTest --tests '*Unit27PastInstructionTest' --tests '*Unit26PostalInstructionTest' --tests '*RequiredPathProgressTest' --tests '*LearnerReloadRecoveryTest' --tests '*HostActivityRecreationTest' --tests '*Aud57CorrectionTest' --tests '*ContentAuditCheckpointRepairTest' --tests '*ContentAuditCorrectionTest' --max-workers=4 --console=plain
