set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
# Install the archived temporary harness before running; remove it after archiving XML.
# Exit 1 is expected ONLY when both distinct-context assertions fail and all six
# existing instruction/credit methods pass. Inspect XML; do not mask arbitrary failures.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*Unit28TimingDiagnosticTest' --tests '*Unit28InstructionTest' --max-workers=4 --console=plain
