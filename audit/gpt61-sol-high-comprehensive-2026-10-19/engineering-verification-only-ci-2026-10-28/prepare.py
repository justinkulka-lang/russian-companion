from pathlib import Path
import json,hashlib,subprocess,shutil
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-verification-only-ci-2026-10-28';W=Path('/workspace/artifacts/verification-only-ci-2026-10-28');assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R);D.mkdir();W.mkdir()
sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n');parent=subprocess.check_output(['git','rev-parse','HEAD'],cwd=R).decode().strip();targets=['.github/workflows/build.yml','gradle/wrapper/gradle-wrapper.properties','README.md'];before={n:(R/n).read_bytes()for n in targets}
s=before[targets[0]].decode();s=s.replace('name: Build','name: Engineering verification',1).replace('  assemble:\n','  verify:\n',1)
s=s.replace('''          packages: platform-tools''','''          packages: platform-tools
          accept-android-sdk-licenses: 'true'
          log-accepted-android-sdk-licenses: 'false' ''').replace("'false' \n","'false'\n")
s=s.replace('          yes | sdkmanager --licenses >/dev/null || true\n','')
old='''      - name: Assemble debug, lint, gate attachment, and unit tests
        run: ./gradlew assembleDebug lintDebug verifyReleaseGateAttachment testDebugUnitTest --stacktrace'''
new='''      - name: Compile, lint, verify gate attachment, and run host tests
        run: |
          cat > verification-evidence/exporter-exclusions.gradle <<'GRADLE'
          allprojects {
              tasks.withType(Test).configureEach {
                  // These four exporters overwrite retained screenshot evidence (16 methods).
                  exclude '**/VisualReviewCaptureTest*', '**/Units35ChunkCaptureTest*', '**/Units610ChunkCaptureTest*', '**/Units1115GapCaptureTest*'
              }
          }
          GRADLE
          ./gradlew -I verification-evidence/exporter-exclusions.gradle compileDebugSources lintDebug verifyReleaseGateAttachment testDebugUnitTest --continue --stacktrace'''
assert s.count(old)==1;s=s.replace(old,new);s=s[:s.index('      - name: Record acceptance-candidate APK identity')]
assert 'assembleDebug'not in s and 'app-debug.apk'not in s and '|| true'not in s
(R/targets[0]).write_text(s)
checksum='bbaeb2fef8710818cf0e261201dab964c572f92b942812df0c3620d62a529a01';observation=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-codex-takeover-2026-10-28/ci-preflight/OFFICIAL_GRADLE_9_6_0_SHA256_RESPONSE.txt';assert observation.read_text().strip()==checksum
p=R/targets[1];text=p.read_text();assert 'distributionSha256Sum'not in text;p.write_text(text.replace('networkTimeout=60000','distributionSha256Sum='+checksum+'\nnetworkTimeout=60000'))
p=R/'README.md';s=p.read_text();old='ANDROID_HOME=$HOME/android-sdk ./gradlew testDebugUnitTest assembleDebug --offline';assert old in s;s=s.replace('Build:\n','Engineering verification (APK creation is deferred until after the final independent audit):\n').replace(old,'ANDROID_HOME=$HOME/android-sdk ./gradlew compileDebugSources lintDebug verifyReleaseGateAttachment --offline');s=s.replace('`assembleRelease` stays fail-closed', 'CI also runs host tests with the four screenshot exporters explicitly excluded (16 methods): `VisualReviewCaptureTest`, `Units35ChunkCaptureTest`, `Units610ChunkCaptureTest`, and `Units1115GapCaptureTest`. Those exporters overwrite retained evidence and are outside environment verification. CI archives source and test/lint reports; it does not assemble or sign an APK. Host verification does not replace the final independent comprehensive audit or subsequent physical-device acceptance.\n\n`assembleRelease` stays fail-closed');p.write_text(s)
for n,data in before.items():p=W/'ORIGINAL'/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)
shutil.copyfile('/workspace/artifacts/setup-android-pinned-action-yml.txt',D/'PINNED_SETUP_ANDROID_ACTION.yml.txt');shutil.copyfile(observation,D/observation.name);shutil.copyfile(__file__,D/'prepare.py')
records={n:{'beforeSha256':sha(data),'afterSha256':sha((R/n).read_bytes())}for n,data in before.items()};save(D/'CHANGES.json',{'parentCommit':parent,'files':records,'gradleExpectedDistributionSha256':checksum,'expectedChecksumSource':'https://services.gradle.org/distributions/gradle-9.6.0-bin.zip.sha256','installedCachedDistributionArchiveIndependentlyHashed':False,'all284TestFilesUnchanged':True,'nativeDirectiveNotAdopted':True})
(D/'STATUS.md').write_text('''# Verification-only CI prepared

The ordinary push/PR workflow compiles debug sources, runs lint, verifies release-gate attachment, and executes host unit tests with exactly four screenshot exporter classes/16methods excluded. It retains source and test/lint reports but no longer assembles, signs, identifies or uploads a device-candidate APK. Release task attachment remains fail-closed and unchanged. No global workflow disable, grading/source-policy or test changes. The pinned SDK setup action explicitly accepts licenses; remove the redundant masked license command. Its exact pinned action metadata is retained. The Gradle 9.6.0 wrapper now pins the previously observed official expected distribution SHA256; this does not claim an independent hash of an already-installed cached distribution archive.

Pending local task-graph/configuration verification, not a CI pass. Existing GitHub Actions runs were blocked before runner assignment by an Actions budget; that external blocker remains. Source bindings pending because workflow/wrapper inputs are included in the official repository source digest. All48findingsOPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/approval. Provisional host SQLite JIT directive is not adopted by CI and is not a test exclusion.
''')
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in D.rglob('*')if p.is_file()and p.name!='SHA256.json'})
subprocess.run(['git','add',*targets,str(D)],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True);subprocess.run(['git','commit','-m','Keep engineering CI verification-only and pin Gradle distribution checksum'],cwd=R,check=True)
