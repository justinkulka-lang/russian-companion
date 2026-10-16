#!/usr/bin/env python3
"""Replay the actual predecessor production tree; always restore current source.

Run with no other Gradle process or source editor active. This changes one tracked
production file temporarily, verifies the entire main tree equals the predecessor,
and restores its exact original bytes in finally. It does not edit learner rows.
"""
from pathlib import Path
from datetime import datetime, timezone
import hashlib
import json
import subprocess

ROOT = Path(__file__).resolve().parents[3]
OUT = Path(__file__).resolve().parent / "predecessor-schema1"
PREDECESSOR = "74eb8b655ebd0af388c4fba05a986d04e6ec02a8"
CURRENT = "c6adbec66cde2d22b7f73289b6328a94a1d1a11d"
REL = "app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt"
SOURCE = ROOT / REL
EXPORTER = ROOT / "app/src/test/java/dev/russiancompanion/v2/PolicyHistoryPredecessorExportTest.kt"
COMMAND = ("source /workspace/setup/android-env.sh\n"
           "./gradlew -I /workspace/setup/cloud-validation.gradle "
           "-I /workspace/setup/parallel-tests.gradle testDebugUnitTest "
           "--tests '*PolicyHistoryPredecessorExportTest' --max-workers=4 --console=plain")


def git(*args):
    return subprocess.check_output(["git", *args], cwd=ROOT).decode().strip()


def sha(data):
    return hashlib.sha256(data).hexdigest()


assert not git("diff", "--name-only", "--", "app/src/main"), "Preserve existing edits"
assert git("rev-parse", "HEAD:app/src/main") == git("rev-parse", CURRENT + ":app/src/main")
assert not EXPORTER.exists(), "Preserve existing exporter"
assert git("diff", "--name-only", PREDECESSOR, CURRENT, "--", "app/src/main") == REL
original = SOURCE.read_bytes()
predecessor = subprocess.check_output(["git", "show", PREDECESSOR + ":" + REL], cwd=ROOT)
exporter = (OUT.parent / "BASELINE_EXPORT_SCHEMA1.kt.txt").read_bytes()
OUT.mkdir(exist_ok=True)
assert not (OUT / "RUN.txt").exists(), "Do not overwrite historical run evidence"
context = {
    "checkoutHead": git("rev-parse", "HEAD"), "productionCommit": PREDECESSOR,
    "productionState": "Exact predecessor app/src/main tree overlaid temporarily; current tests plus archived exporter. Not a full historical repository checkout or clean test commit.",
    "runStartedUtc": datetime.now(timezone.utc).isoformat(),
    "command": COMMAND, "sourceSha256": sha(predecessor),
    "restoreSourceSha256": sha(original), "exporterSha256": sha(exporter),
    "productionTrees": {p: git("rev-parse", PREDECESSOR + ":" + p)
                        for p in ["app/src/main", "app/schemas", "catalog"]},
    "completeSuite": False,
}
try:
    SOURCE.write_bytes(predecessor)
    EXPORTER.write_bytes(exporter)
    assert not git("diff", PREDECESSOR, "--name-only", "--", "app/src/main")
    with (OUT / "RUN.txt").open("wb") as log:
        result = subprocess.run(["bash", "-c", COMMAND], cwd=ROOT, stdout=log, stderr=subprocess.STDOUT)
    context["runnerExitCode"] = result.returncode
    (OUT / "EXIT.txt").write_text(str(result.returncode) + "\n")
finally:
    SOURCE.write_bytes(original)
    EXPORTER.unlink(missing_ok=True)
    context["currentSourceRestored"] = SOURCE.read_bytes() == original
    context["currentMainDiffEmpty"] = not git("diff", "--name-only", "--", "app/src/main")
    (OUT / "CAPTURE_CONTEXT.json").write_text(json.dumps(context, indent=2) + "\n")
assert context["currentSourceRestored"] and context["currentMainDiffEmpty"]
raise SystemExit(context["runnerExitCode"])
