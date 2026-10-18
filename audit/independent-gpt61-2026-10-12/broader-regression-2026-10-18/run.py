"""Retain discovery and real regression as separate, immutable evidence scopes."""
from pathlib import Path
from datetime import datetime, timezone
import hashlib
import json
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[3]
HERE = Path(__file__).resolve().parent
MODE = sys.argv[1]
assert MODE in ("DISCOVERY", "INITIAL", "RETRY", "FOCUSED_HISTORY", "FOCUSED_COMPLETION")
OUT = HERE / MODE
OUT.mkdir(exist_ok=False)

def git(*args):
    return subprocess.check_output(["git", *args], cwd=ROOT, text=True).strip()

def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def save(name, data):
    (OUT / name).write_text(json.dumps(data, indent=2) + "\n")

trees = {p: git("rev-parse", "HEAD:" + p) for p in
         ("app/src/main", "app/src/test", "catalog", "docs", "review")}
assert not git("diff", "HEAD", "--", *trees), "Commit input changes before executing"
args = ["-I", "/workspace/setup/cloud-validation.gradle", "-I",
        "/workspace/setup/parallel-tests.gradle"]
if MODE in ("RETRY", "FOCUSED_HISTORY", "FOCUSED_COMPLETION"):
    args += ["-I", str(HERE / "broader-tests.gradle"),
             "-Daudit.events.path=" + str(OUT / "EVENTS.jsonl")]
args += ["testDebugUnitTest", "--rerun"]
selectors = []
if MODE == "FOCUSED_HISTORY":
    selectors = ["*Unit14ImpersonalRemediationTest", "*Unit15ImperativeRemediationTest",
                 "*HistoricalTeachingFixtureScopeTest",
                 "*Unit14CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup",
                 "*Unit15CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup"]
    for selector in selectors:
        args += ["--tests", selector]
if MODE == "FOCUSED_COMPLETION":
    selectors = ["*CompletionEvidenceCorrectionTest"]
    args += ["--tests", selectors[0]]
if MODE == "DISCOVERY":
    args += ["--test-dry-run"]
else:
    args += ["compileDebugSources", "lintDebug", "verifyReleaseGateAttachment", "--continue"]
args += ["--max-workers=4", "--console=plain"]
started = datetime.now(timezone.utc)
context = {
    "sourceCommit": git("rev-parse", "HEAD"),
    "startedUtc": started.isoformat(), "inputTrees": trees,
    "mode": MODE, "syntheticDryRun": MODE == "DISCOVERY",
    "command": ["./gradlew", *args],
    "activation": "source /workspace/setup/android-env.sh",
    "runnerSha256": digest(Path(__file__)),
    "workerPolicy": "4 forks / 3GiB heaps / recycle after8classes" if MODE in ("RETRY", "FOCUSED_HISTORY", "FOCUSED_COMPLETION")
                    else "4 forks / 1GiB heaps / unbounded worker lifetime",
    "initScripts": {p: digest(Path(p)) for p in args if p.endswith(".gradle")},
    "selectors": selectors, "failFast": False,
    "exclusions": {"VisualReviewCaptureTest": 2, "Units35ChunkCaptureTest": 2,
                   "Units610ChunkCaptureTest": 10, "Units1115GapCaptureTest": 2},
    "scope": "debug host unit-test task, explicit named focused selectors" if selectors else
             "debug host unit-test task, all discovered methods except four screenshot exporters; no instrumented/device/exporter execution",
}
save("CONTEXT.json", context)
with (OUT / "RUN.log").open("w") as log:
    result = subprocess.run(["bash", "-c",
        'source /workspace/setup/android-env.sh; exec ./gradlew "$@"',
        "regression", *args], cwd=ROOT, stdout=log, stderr=subprocess.STDOUT)
(OUT / "RUN.exit").write_text(str(result.returncode) + "\n")
xml_dir = OUT / "XML"
xml_dir.mkdir()
tests, suites = [], []
for path in sorted((ROOT / "app/build/test-results/testDebugUnitTest").glob("TEST-*.xml")):
    suite = ET.parse(path).getroot()
    timestamp = datetime.fromisoformat(suite.attrib["timestamp"].replace("Z", "+00:00"))
    assert timestamp >= started, (path, timestamp, started)
    shutil.copy2(path, xml_dir / path.name)
    suites.append(suite.attrib)
    for case in suite.findall("testcase"):
        failure, error, skip = (case.find(tag) for tag in ("failure", "error", "skipped"))
        tests.append({"class": case.attrib["classname"], "name": case.attrib["name"],
            "seconds": case.attrib["time"],
            "outcome": "FAILED" if failure is not None else "ERROR" if error is not None
                       else "SKIPPED" if skip is not None else "PASSED",
            "message": next((v.attrib.get("message") for v in (failure, error, skip)
                             if v is not None), None)})
assert not git("diff", "HEAD", "--", *trees), "Inputs changed during run"
assert all(git("rev-parse", "HEAD:" + p) == t for p, t in trees.items()), "Committed inputs changed during run"
log_text = (OUT / "RUN.log").read_text()
tasks = {}
for name in ("testDebugUnitTest", "compileDebugSources", "lintDebug", "verifyReleaseGateAttachment"):
    prefix = "> Task :app:" + name
    tasks[name] = next((s for s in log_text.splitlines()
        if s == prefix or s.startswith(prefix + " ")), "NOT_REACHED")
lint = None
if tasks["lintDebug"] != "NOT_REACHED":
    path = ROOT / "app/build/reports/lint-results-debug.xml"
    shutil.copy2(path, OUT / "LINT.xml")
    issues = ET.parse(path).getroot().findall("issue")
    lint = {s: sum(i.attrib.get("severity") == s for i in issues)
            for s in ("Fatal", "Error", "Warning", "Information", "Ignore")}
counts = {"reported": len(tests), **{k: sum(t["outcome"] == v for t in tests)
    for k, v in (("passed", "PASSED"), ("failed", "FAILED"), ("errors", "ERROR"), ("skipped", "SKIPPED"))}}
manifest = {"sourceContext": context, "exitCode": result.returncode,
    "finishedUtc": datetime.now(timezone.utc).isoformat(), "testCounts": counts,
    "classes": len(suites), "tests": tests, "suites": suites,
    "taskStatuses": tasks, "lint": lint, "completeSuite": False,
    "dryRunPassesAreNotExecution": True, "separateScopesDoNotSum": True,
    "apk": {"assembly": "NOT_RUN", "configuredIdentity": "dev.russiancompanion / 2.0.0-dev / code5",
            "sha256": "NOT_RECORDED", "signature": "NOT_RECORDED"},
    "releaseApproval": False, "physicalAcceptance": "OUTSTANDING_LAST_AFTER_FINAL_INDEPENDENT_AUDIT"}
if MODE != "DISCOVERY":
    discovery = json.loads((HERE / "DISCOVERY/MANIFEST.json").read_text())
    a = {(t["class"], t["name"]) for t in discovery["tests"]}
    b = {(t["class"], t["name"]) for t in tests}
    manifest["discoveryComparison"] = {"sameInputTrees": discovery["sourceContext"]["inputTrees"] == trees,
        "sameSelection": not selectors,
        "baselineDiscoveryOnlyWhenInputsOrSelectionDiffer": True,
        "discoveredNotReported": sorted(a - b), "reportedNotDiscovered": sorted(b - a)}
if MODE in ("RETRY", "FOCUSED_HISTORY", "FOCUSED_COMPLETION"):
    events = [json.loads(line) for line in (OUT / "EVENTS.jsonl").read_text().splitlines()]
    results = [e for e in events if e["kind"] == "RESULT"]
    event_names = [(e["className"], e["name"]) for e in results]
    xml_names = [(e["class"], e["name"]) for e in tests]
    manifest["eventComparison"] = {
        "startEvents": sum(e["kind"] == "START" for e in events),
        "resultEvents": len(results), "sameNamesAsXml": sorted(event_names) == sorted(xml_names),
        "eventOutcomes": {v: sum(e["resultType"] == v for e in results)
                          for v in ("SUCCESS", "FAILURE", "SKIPPED")},
    }
save("MANIFEST.json", manifest)
save("SHA256.json", {str(p.relative_to(OUT)): digest(p) for p in sorted(OUT.rglob("*"))
                      if p.is_file() and p.name != "SHA256.json"})
print(json.dumps({"mode": MODE, "exitCode": result.returncode, "counts": counts,
                  "classes": len(suites), "tasks": tasks, "lint": lint}))
sys.exit(result.returncode)
