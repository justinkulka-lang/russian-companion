"""Run an explicit scoped regression without overwriting evidence."""
from pathlib import Path
from datetime import datetime, timezone
import hashlib
import json
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET

SELECTION_CONFIG = Path(sys.argv[2]).resolve() if len(sys.argv) > 2 else None
HERE = SELECTION_CONFIG.parent if SELECTION_CONFIG else Path(__file__).resolve().parent
ROOT = HERE.parents[2]
LABEL = sys.argv[1] if len(sys.argv) > 1 else "INITIAL"
assert LABEL.isidentifier() and LABEL.isupper()
OUT = HERE / LABEL
OUT.mkdir(exist_ok=False)

def git(*args):
    return subprocess.check_output(["git", *args], cwd=ROOT, text=True).strip()

def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def save(name, value):
    (OUT / name).write_text(json.dumps(value, indent=2) + "\n")

names = {
    "RuntimeCorrectionTest": ["formerGuidedPracticeGapsNowHaveRealItems", "remediationReachesGuidedPracticeThenReturnsToTheFailedCheck"],
    "Unit4NominativeRoleTest": ["twoDistinctIndependentSubjectsAndFullTeachingAreRequired", "correctGlossesStayOptionalAndCannotReplaceCaseRoleChecks",
        "oldTranslationCompletionHistoryIsPreservedButStaleForTheNewConstruct", "wrongCaseRoleShowsTargetedFeedbackAndReturnsToEveryExample"],
    "OptionalGlossRemediationTest": ["optionalMeaningExplanationReturnsToItsSelectionWithoutGrantingCaseRoleCredit"],
}
if SELECTION_CONFIG:
    names = json.loads(SELECTION_CONFIG.read_text())["selectedMethods"]
expected = {("dev.russiancompanion.v2." + cls, name) for cls, methods in names.items() for name in methods}
selectors = ["*" + cls + "." + name for cls, methods in names.items() for name in methods]
trees = {p: git("rev-parse", "HEAD:" + p) for p in ("app/src/main", "app/src/test", "catalog", "docs", "review")}
assert not git("diff", "HEAD", "--", *trees), "Commit inputs before execution"
scripts = [Path("/workspace/setup/cloud-validation.gradle"), Path("/workspace/setup/parallel-tests.gradle"),
    HERE.parent / "broader-regression-2026-10-18/broader-tests.gradle", HERE / "test-candidates.gradle"]
args = [part for path in scripts for part in ("-I", str(path))]
args += ["-Daudit.events.path=" + str(OUT / "EVENTS.jsonl"), "testDebugUnitTest", "--rerun"]
args += [part for selector in selectors for part in ("--tests", selector)]
args += ["compileDebugSources", "lintDebug", "verifyReleaseGateAttachment", "--continue", "--max-workers=4", "--console=plain"]
started = datetime.now(timezone.utc)
context = {"sourceCommit": git("rev-parse", "HEAD"), "startedUtc": started.isoformat(), "inputTrees": trees,
    "command": ["./gradlew", *args], "activation": "source /workspace/setup/android-env.sh",
    "runnerSha256": sha(Path(__file__)), "initScriptSha256": {str(p): sha(p) for p in scripts},
    "selectionConfig": {"path": str(SELECTION_CONFIG), "sha256": sha(SELECTION_CONFIG)} if SELECTION_CONFIG else None,
    "selectors": selectors, "candidateClasses": list(names), "workerPolicy": "4 forks / 3GiB / forkEvery8",
    "excludedScreenshotExporters": {"VisualReviewCaptureTest": 2, "Units35ChunkCaptureTest": 2, "Units610ChunkCaptureTest": 10, "Units1115GapCaptureTest": 2}}
save("CONTEXT.json", context)
with (OUT / "RUN.log").open("w") as log:
    result = subprocess.run(["bash", "-c", 'source /workspace/setup/android-env.sh; exec ./gradlew "$@"', "verification", *args],
        cwd=ROOT, stdout=log, stderr=subprocess.STDOUT)
(OUT / "RUN.exit").write_text(str(result.returncode) + "\n")
log = (OUT / "RUN.log").read_text()
tasks = {}
for name in ("testDebugUnitTest", "compileDebugSources", "lintDebug", "verifyReleaseGateAttachment"):
    prefix = "> Task :app:" + name
    tasks[name] = next((s for s in log.splitlines() if s == prefix or s.startswith(prefix + " ")), "NOT_REACHED")
tests, suites, stale = [], [], []
xml_dir = OUT / "XML"
xml_dir.mkdir()
for path in sorted((ROOT / "app/build/test-results/testDebugUnitTest").glob("TEST-*.xml")):
    suite = ET.parse(path).getroot()
    if datetime.fromisoformat(suite.attrib["timestamp"].replace("Z", "+00:00")) < started:
        stale.append(path.name)
        continue
    shutil.copy2(path, xml_dir / path.name)
    suites.append(suite.attrib)
    for case in suite.findall("testcase"):
        failure, error, skip = (case.find(tag) for tag in ("failure", "error", "skipped"))
        tests.append({"class": case.attrib["classname"], "name": case.attrib["name"], "seconds": case.attrib["time"],
            "outcome": "FAILED" if failure is not None else "ERROR" if error is not None else "SKIPPED" if skip is not None else "PASSED"})
assert not git("diff", "HEAD", "--", *trees)
assert all(git("rev-parse", "HEAD:" + p) == t for p, t in trees.items())
actual = {(t["class"], t["name"]) for t in tests}
events = [json.loads(line) for line in (OUT / "EVENTS.jsonl").read_text().splitlines()] if (OUT / "EVENTS.jsonl").exists() else []
finished = [e for e in events if e["kind"] == "RESULT"]
event_names = sorted((e["className"], e["name"]) for e in finished)
xml_names = sorted((t["class"], t["name"]) for t in tests)
scope_complete = actual == expected and len(tests) == len(expected) and event_names == xml_names and not stale
counts = {"reported": len(tests), **{k: sum(t["outcome"] == v for t in tests) for k, v in
    (("passed", "PASSED"), ("failed", "FAILED"), ("errors", "ERROR"), ("skipped", "SKIPPED"))}} if tests else None
lint = None
if tasks["lintDebug"] != "NOT_REACHED":
    path = ROOT / "app/build/reports/lint-results-debug.xml"
    shutil.copy2(path, OUT / "LINT.xml")
    issues = ET.parse(path).getroot().findall("issue")
    lint = {severity: sum(e.attrib.get("severity") == severity for e in issues) for severity in ("Fatal", "Error", "Warning", "Information", "Ignore")}
manifest = {"sourceContext": context, "finishedUtc": datetime.now(timezone.utc).isoformat(), "exitCode": result.returncode,
    "testCounts": counts, "classes": len(suites), "tests": tests, "suites": suites, "taskStatuses": tasks, "lint": lint,
    "scopeComplete": scope_complete, "missingSelectedMethods": sorted(expected - actual), "unexpectedMethods": sorted(actual - expected),
    "staleXmlNotCounted": stale, "eventResultCount": len(finished), "eventsMatchFreshXml": event_names == xml_names,
    "completeSuite": False, "separateScopesDoNotSum": True,
    "apk": {"assembly": "NOT_RUN", "configuredIdentity": "dev.russiancompanion / 2.0.0-dev / code5", "sha256": "NOT_RECORDED", "signature": "NOT_RECORDED"},
    "formalAcceptance": False, "releaseApproval": False, "physicalAcceptance": "OUTSTANDING_LAST_AFTER_FINAL_INDEPENDENT_AUDIT"}
save("MANIFEST.json", manifest)
save("SHA256.json", {str(p.relative_to(OUT)): sha(p) for p in sorted(OUT.rglob("*")) if p.is_file() and p.name != "SHA256.json"})
print(json.dumps({k: manifest[k] for k in ("exitCode", "testCounts", "classes", "scopeComplete", "taskStatuses", "lint")}))
sys.exit(result.returncode or (0 if scope_complete else 1))
