"""Collect already executed diagnostics. This script does not execute any tests."""
from pathlib import Path
import collections
import hashlib
import json
import subprocess
import xml.etree.ElementTree as ET

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[2]
BASELINE = "11d70e5e4512ff93758ebf246f2a4773f83ca0e0"
FIRST_RUN = "5d11f1509243a4bba2991cfbff0c22888cbe2654"
TESTED = "c4b789a2628fb60eb81acf098e61c333dbeb9810"


def sha(path):
    return hashlib.sha256(Path(path).read_bytes()).hexdigest()


def git(*args):
    return subprocess.check_output(["git", *args], cwd=ROOT, text=True).strip()


def tree(commit, path):
    result = subprocess.run(["git", "rev-parse", f"{commit}:{path}"], cwd=ROOT,
                            text=True, capture_output=True)
    return result.stdout.strip() if result.returncode == 0 else None


def counts(suite):
    return {key: int(suite.get(key)) for key in ("tests", "failures", "errors", "skipped")}


first = ET.parse(HERE / "BASELINE_TEST_RESULTS.xml").getroot()
last = ET.parse(HERE / "FINAL_BASELINE_TEST_RESULTS.xml").getroot()
assert counts(last) == dict(tests=6, failures=0, errors=0, skipped=0)
lint = collections.Counter(issue.get("severity") for issue in
                           ET.parse(HERE / "FINAL_BASELINE_LINT.xml").getroot().findall("issue"))
assert lint["Error"] == 0
log = (HERE / "FINAL_BASELINE_RUN.txt").read_text()
assert "BUILD SUCCESSFUL" in log
assert "> Task :app:testDebugUnitTest\n" in log
assert "> Task :app:verifyReleaseGateAttachment\n" in log
protected = []
for path in ("app/src/main", "app/src/test", "app/src/androidTest", "catalog", "review", "release", "docs"):
    before, after = tree(BASELINE, path), tree(TESTED, path)
    assert before == after, path
    assert tree("HEAD", path) == after, path
    protected.append(dict(path=path, gitTree=after, unchanged=True,
                          status="ABSENT_BOTH_COMMITS" if after is None else "PRESERVED"))
inventory = json.loads((HERE / "BASELINE_INVENTORY.json").read_text())
for record in inventory["inspectedFiles"]:
    assert sha(ROOT / record["path"]) == record["sha256"], record["path"]
receipt_path = HERE.parent / "RESULTS_RECEIPT.json"
receipt = json.loads(receipt_path.read_text())
for record in receipt["files"]:
    assert sha(receipt_path.parent / record["path"]) == record["sha256"]
changed = git("diff", "--name-only", BASELINE, TESTED).splitlines()
assert all(path.startswith("audit/independent-gpt61-2026-10-12/ia003-cumulative-review/") for path in changed)

manifest = {
    "schemaVersion": 1,
    "status": "BASELINE_DIAGNOSTIC_REPRODUCED_IMPLEMENTATION_NOT_STARTED",
    "finding": "IA003", "requirements": ["MA2-027", "MA2-088"],
    "baselineCommit": BASELINE, "testedCommit": TESTED,
    "repository": "justinkulka-lang/russian-companion",
    "branch": "cursor/content-audit-corrections-964f", "draftPR": 64,
    "command": "bash audit/independent-gpt61-2026-10-12/ia003-cumulative-review/verify-baseline.sh",
    "expandedGradleCommand": "./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle -I /workspace/setup/ia003-diagnostics.gradle testDebugUnitTest --tests '*Ia003CumulativeReviewBaselineTest' compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain",
    "attempts": [
        {"name": "initial external source-set override", "commit": FIRST_RUN,
         "result": "NO_TESTS_FOUND", "executedTests": 0, "log": "INITIAL_NO_TESTS.txt",
         "diagnosis": "AGP9 built-in Kotlin did not compile the diagnostic .kt from the added java source directory."},
        {"name": "second external source-set override", "commit": FIRST_RUN,
         "result": "NO_TESTS_FOUND", "executedTests": 0, "log": "SECOND_NO_TESTS.txt",
         "diagnosis": "org.jetbrains.kotlin.android plugin callback did not apply to AGP9 built-in Kotlin; diagnostic source not compiled."},
        {"name": "corrected built-in Kotlin source-set baseline", "commit": FIRST_RUN,
         "result": "PASS_DEFECT_REPRODUCTIONS", "counts": counts(first),
         "timestamp": first.get("timestamp"), "xml": "BASELINE_TEST_RESULTS.xml", "log": "BASELINE_RUN.txt"},
        {"name": "committed replay-helper final baseline", "commit": TESTED,
         "result": "PASS_DEFECT_REPRODUCTIONS", "counts": counts(last),
         "timestamp": last.get("timestamp"), "xml": "FINAL_BASELINE_TEST_RESULTS.xml", "log": "FINAL_BASELINE_RUN.txt"},
    ],
    "finalScope": {"counts": counts(last), "classes": 1,
                   "selectors": ["*Ia003CumulativeReviewBaselineTest"],
                   "methods": [dict(name=test.get("name"), seconds=test.get("time")) for test in last.findall("testcase")],
                   "type": "Historical baseline defect reproductions; NOT correction acceptance or complete suite",
                   "overlappingRunsSummed": False},
    "lint": {"result": "PASS", "errors": lint["Error"], "warnings": lint["Warning"], "hints": lint["Hint"],
             "xml": "FINAL_BASELINE_LINT.xml",
             "currentFinalReport": "UP-TO-DATE; generated in first successful diagnostic run with same inputs",
             "finalTask": "lintDebug executes; main/Android-test/unit-test analysis and report UP-TO-DATE"},
    "compile": {"compileDebugSources": "UP-TO-DATE", "mainSourceChanged": False,
                "diagnosticTestCompilation": "Executed in first successful run; UP-TO-DATE in final run"},
    "releaseGateAttachment": {"result": "PASS", "task": "verifyReleaseGateAttachment", "finalTaskExecuted": True},
    "apk": {"assembled": False, "signed": False, "configuredApplicationId": "dev.russiancompanion",
            "configuredVersionName": "2.0.0-dev", "configuredVersionCode": 5,
            "currentArtifactIdentity": "NOT_RECORDED", "currentApkSha256": "NOT_RECORDED",
            "currentApkSignature": "NOT_RECORDED", "historicalCandidateArtifacts": "Preserved; not this run"},
    "excludedScreenshotExporters": [dict(name=name, methods=n) for name, n in
         [("VisualReviewCaptureTest", 2), ("Units35ChunkCaptureTest", 2), ("Units610ChunkCaptureTest", 10), ("Units1115GapCaptureTest", 2)]],
    "exclusionReason": "Overwrite tracked screenshot evidence and write /opt/cursor; excluded by external cloud-validation init. Diagnostic selector additionally limits scope to one class.",
    "historicalCountComparison": {
        "mainCommit": "ff539220645670fbf7a1abacaa6f71e828606b80", "mainSourceAnnotations": 579,
        "excludedMethods": 16, "reportedTests": 563, "passed": 562, "skipped": 1,
        "completeSuiteClaim": "NOT_PROVEN",
        "pr64BaselineCommit": "420f41ff6c2a4417cd829fdee0179b6a3b797eff", "pr64BaselineSourceAnnotations": 691,
        "sourceAnnotationDifference": 112, "handoffExecution": "UNVERIFIED",
        "currentScope": "Six explicit diagnostic methods at another commit; neither 562 nor 691 is this executed scope."},
    "preservation": {"protectedTrees": protected, "allOriginalNineDiagnosticHashes": "PASS",
        "rawAssetSha256": sha(ROOT / "app/src/main/assets/content/v2-curriculum.json"),
        "packDigest": "4d2d7078afd288289e300f082e641e637fda261d486fa9f1c6b91c014c5b3192",
        "repositorySourceDigest": "f575c241a0c7e2d103e6babb6ca224fb199dc9a3751c8220b3d09837d46b422f",
        "candidate": "Unchanged; unfrozen/sourceCommit null/current artifact NOT_RECORDED/public release NOT_APPROVED"},
    "environment": {"configurationChangeThisRun": False, "installScriptAndStartSkill": "Saved previously",
        "priorPublication": "USER_CONFIRMED", "laterNetworkOnlyDraftPublication": "NOT_INDEPENDENTLY_ESTABLISHED",
        "supportedPublicationControlHere": "UNAVAILABLE"},
    "acceptance": {"independentComprehensiveAudit": "OUTSTANDING; after engineering corrections",
        "physicalGalaxyS26UltraAirplaneModeAndReboot": "OUTSTANDING; LAST after final independent comprehensive audit",
        "mergeFreezeSigningReleaseApproval": "NOT_AUTHORIZED_OR_PERFORMED"},
    "next": "Add assignment/history persistence and migration plus a bounded Unit3 vertical slice; then integrate all explicit main-path policies and backup/restore, with correction acceptance before IA003 closure.",
    "evidenceFiles": [dict(path=path.name, sha256=sha(path), bytes=path.stat().st_size)
                      for path in sorted(HERE.iterdir()) if path.is_file() and path.name != "VERIFICATION_MANIFEST.json"],
    "externalInitFiles": [dict(path=str(path), sha256=sha(path)) for path in map(Path,
        ["/workspace/setup/cloud-validation.gradle", "/workspace/setup/parallel-tests.gradle", "/workspace/setup/ia003-diagnostics.gradle"])],
    "diagnosticSourceSha256": sha(HERE / "diagnostics/Ia003CumulativeReviewBaselineTest.kt"),
}
(HERE / "VERIFICATION_MANIFEST.json").write_text(json.dumps(manifest, indent=2) + "\n")
print("Six baseline diagnostics pass; correction NOT implemented. Lint:", dict(lint))
print("Tested commit:", TESTED)
