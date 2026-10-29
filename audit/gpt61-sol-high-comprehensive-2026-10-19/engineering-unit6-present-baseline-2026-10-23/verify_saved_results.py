#!/usr/bin/env python3
"""Read-only baseline integrity checks, not another test execution."""
from pathlib import Path
import hashlib
import json
import subprocess
import zipfile
import xml.etree.ElementTree as ET

here = Path(__file__).resolve().parent
root = Path(subprocess.check_output(["git", "rev-parse", "--show-toplevel"], cwd=here, text=True).strip())
sha = lambda data: hashlib.sha256(data).hexdigest()
read = lambda name: json.loads((here / name).read_text())
for name, expected in read("SHA256.json").items():
    assert sha((here / name).read_bytes()) == expected, name
runtime, discovery = read("REGRESSION_RESULT.json"), read("DISCOVERY_RESULT.json")
source = "ed6bc90227c0f89966b18ee7ecf35d09e8921a5c"
assert runtime["sourceCommit"] == discovery["sourceCommit"] == source
assert runtime["testCounts"] == dict(reported=2, passed=1, failed=1, errors=0, skipped=0)
assert runtime["exitCode"] == 1 and discovery["exitCode"] == 0
assert discovery["testCounts"] == dict(reported=2, passed=0, failed=0, errors=0, skipped=2)
assert runtime["lintIssuesBySeverity"] == dict(Fatal=0, Error=0, Warning=20, Hint=1)
assert runtime["discoveryComparison"]["sameInputTrees"]
assert runtime["discoveryComparison"]["exactSameMethodSet"]
assert runtime["inputTrees"] == discovery["inputTrees"] == read("CONSERVATION.json")["inputTrees"]
with zipfile.ZipFile(here / "RAW_BASELINE.zip") as raw:
    assert raw.testzip() is None
    index = read("RAW_BASELINE_INDEX.json")
    assert sorted(raw.namelist()) == sorted(r["path"] for r in index)
    for record in index:
        data = raw.read(record["path"])
        assert sha(data) == record["sha256"] and len(data) == record["bytes"]
    xml = ET.fromstring(raw.read("REGRESSION/XML/TEST-dev.russiancompanion.v2.Unit6PresentReviewTest.xml"))
    assert "actualAdvanceWithoutPresent=true" in xml.find("system-out").text
    failures = xml.findall("testcase/failure")
    assert len(failures) == 1 and "must refuse actual advancement" in failures[0].get("message")
    capture_document = raw.read("REGRESSION/CAPTURE/PARTIAL_UNIT6_V2.json")
    identity = read("CAPTURE_IDENTITY.json")
    assert sha(capture_document) == identity["documentSHA256"]
    for mode in ["DISCOVERY", "REGRESSION"]:
        original = json.loads(raw.read(mode + "/SHA256.json"))
        for path, expected in original.items():
            if path == "CAPTURE/PARTIAL_UNIT6_V2.zip":
                data = (here / "BASELINE_CURRENT_UNIT6_V2.zip").read_bytes()
            elif path == "CAPTURE/IDENTITY.json":
                data = (here / "CAPTURE_IDENTITY.json").read_bytes()
            else:
                data = raw.read(mode + "/" + path)
            assert sha(data) == expected, (mode, path)
archive = here / "BASELINE_CURRENT_UNIT6_V2.zip"
assert sha(archive.read_bytes()) == identity["zipSHA256"] and archive.stat().st_size == identity["zipBytes"]
assert identity["producerCommit"] == source
with zipfile.ZipFile(archive) as backup:
    assert backup.testzip() is None
    assert sorted(backup.namelist()) == ["learner.json", "manifest.json"]
    document = json.loads(backup.read("learner.json"))
assert document == json.loads(capture_document)
task = next(a for a in document["reviewAssignments"] if a["targetUnit"] == 6)
assert task["schema"] == 4 and task["policyId"] == identity["policyId"]
assert task["digest"] == identity["assignmentDigest"] and len(task["slots"]) == 3
answers = [a for a in document["reviewAnswers"] if a["assignmentId"] == task["id"]]
assert len(answers) == 1 and answers[0]["slotIndex"] == 0 and answers[0]["acceptedOrder"] > task["boundary"]
assert not document["recordings"] and not any(c["unitNumber"] == 6 for c in document["unitCompletions"])
conserved = read("CONSERVATION.json")
assert len(conserved["originalArchives"]) == 66
for row in conserved["originalArchives"]:
    path = root / row["path"]
    assert sha(path.read_bytes()) == row["sha256"]
    with zipfile.ZipFile(path) as original:
        assert original.testzip() is None
blob = subprocess.check_output(["git", "show", source + ":app/src/main/assets/content/v2-curriculum.json"], cwd=root)
assert sha(blob) == identity["rawCurriculumSHA256"] == conserved["rawCurriculumSHA256"]
print(json.dumps({"verifiedBaseline": source, "reported": 2, "passed": 1, "intendedFailed": 1,
                  "originalArchivesConserved": 66, "additionalSchema4Capture": identity["zipSHA256"],
                  "newRuntimeTestsExecutedByVerifier": 0}))
