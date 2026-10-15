# Reconstruct the verified generation helper

These are exact source copies of the external helper used for REVIEW_REFRESH.txt and READINESS_REFRESH.txt. They call the repository's canonical renderers;they do not grant curriculum acceptance. The historical /workspace/setup/u27-digests.json name is retained exactly. No private reference is read.

On a fresh environment,after the saved Android install/start instructions have recreated /workspace/setup/android-env.sh and the validation init file,run from /workspace/russian-companion:

```bash
source /workspace/setup/android-env.sh
mkdir -p /workspace/setup/checkpoint-tools
cp audit/independent-gpt61-2026-10-12/ia003-unit20-gate/GENERATION_SUPPORT/ReviewCheckpointTools.java /workspace/setup/checkpoint-tools/ReviewCheckpointTools.java
cp audit/independent-gpt61-2026-10-12/ia003-unit20-gate/GENERATION_SUPPORT/checkpoint-tools.gradle /workspace/setup/checkpoint-tools.gradle
javac -d /workspace/setup/checkpoint-tools /workspace/setup/checkpoint-tools/ReviewCheckpointTools.java
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/checkpoint-tools.gradle -Dcheckpoint.mode=reviews renderCheckpointArtifacts --max-workers=4 --console=plain
```

Update only the declared current digest/ancestry bindings and candidate source digest after reviewing the generated changes,then run the same Gradle command with -Dcheckpoint.mode=readiness. Preserve historical values/evidence. Test verification commands/init contents and hashes are in FINAL_VERIFICATION_MANIFEST.json. Generation writes tracked review/readiness files;do not run concurrently with Gradle tests or while preserving a tested checkout for a receipt. Do not assemble/sign an APK or change freeze/approval status. The helper's Java source compiles independently of app classes;the Gradle test classpath supplies them at runtime.
