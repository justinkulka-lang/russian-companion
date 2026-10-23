#!/usr/bin/env python3
"""Read-only integrity/outcome/conservation checks; does not execute host tests."""
from pathlib import Path
import hashlib
import json
import subprocess
import re
import zipfile
import xml.etree.ElementTree as ET

here = Path(__file__).resolve().parent
root = Path(subprocess.check_output(["git", "rev-parse", "--show-toplevel"], cwd=here, text=True).strip())
sha = lambda data: hashlib.sha256(data).hexdigest()
read = lambda name: json.loads((here / name).read_text())
blob = lambda commit, path: subprocess.check_output(["git", "show", commit + ":" + path], cwd=root)
for name, expected in read("SHA256.json").items():
    assert sha((here / name).read_bytes()) == expected, name
scopes = [("INITIAL", 21, "53d9c292e1f32f204bdff2b09b8f10d02b6b28a6", 5),
          ("OWNER_BOUNDARY", 5, "7d6bb0b3fe889920c1b31f4d6e2beac4270b703b", 1)]
with zipfile.ZipFile(here / "RAW_VERIFICATION.zip") as raw:
    assert raw.testzip() is None
    index = read("RAW_VERIFICATION_INDEX.json")
    assert sorted(raw.namelist()) == sorted(row["path"] for row in index)
    for row in index:
        data = raw.read(row["path"])
        assert len(data) == row["bytes"] and sha(data) == row["sha256"]
    for label, count, commit, classes in scopes:
        for mode in ["DISCOVERY", "REGRESSION"]:
            prefix = label + "/" + mode + "/"
            result = read(label + "_" + mode + "_RESULT.json")
            context = read(label + "_" + mode + "_CONTEXT.json")
            assert result == json.loads(raw.read(prefix + "RESULT.json"))
            assert context == json.loads(raw.read(prefix + "CONTEXT.json"))
            assert result["sourceCommit"] == context["sourceCommit"] == commit
            dry = mode == "DISCOVERY"
            assert result["exitCode"] == 0 and result["classes"] == classes
            assert result["testCounts"] == dict(reported=count, passed=0 if dry else count,
                                               failed=0, errors=0, skipped=count if dry else 0)
            assert context["exclusions"] == dict(VisualReviewCaptureTest=2, Units35ChunkCaptureTest=2,
                                                 Units610ChunkCaptureTest=10, Units1115GapCaptureTest=2)
            for path, expected in json.loads(raw.read(prefix + "SHA256.json")).items():
                assert sha(raw.read(prefix + path)) == expected
            for path, expected in context["initScriptSha256"].items():
                basename = Path(path).name
                data = (here / label / basename).read_bytes() if basename == "resources.gradle" else raw.read("INIT/" + basename)
                assert sha(data) == expected
            rows = []
            for path in raw.namelist():
                if path.startswith(prefix + "XML/") and path.endswith(".xml"):
                    suite = ET.fromstring(raw.read(path))
                    for case in suite.findall("testcase"):
                        assert case.find("failure") is None and case.find("error") is None
                        assert (case.find("skipped") is not None) == dry
                        rows.append((case.get("classname"), case.get("name")))
            assert len(rows) == count and len(set(rows)) == count
            events = [json.loads(line) for line in raw.read(prefix + "EVENTS.jsonl").decode().splitlines()]
            results = [row for row in events if row["kind"] == "RESULT"]
            assert sorted(rows) == sorted((row["className"], row["name"]) for row in results)
            assert all(row["resultType"] == ("SKIPPED" if dry else "SUCCESS") for row in results)
            if not dry:
                assert result["discoveryComparison"]["sameInputTrees"] and result["discoveryComparison"]["exactSameMethodSet"]
                assert result["lintIssuesBySeverity"] == dict(Fatal=0, Error=0, Warning=20, Hint=1)
                issues = ET.fromstring(raw.read(prefix + "LINT.xml")).findall("issue")
                assert {s: sum(i.get("severity") == s for i in issues) for s in ["Fatal", "Error", "Warning", "Hint"]} == result["lintIssuesBySeverity"]
                assert result["tasks"]["verifyReleaseGateAttachment"] == "> Task :app:verifyReleaseGateAttachment"
    xml = ET.fromstring(raw.read("INITIAL/REGRESSION/XML/TEST-dev.russiancompanion.v2.Unit6PresentReviewTest.xml"))
    assert "actualAdvanceWithoutPresent=false" in xml.find("system-out").text
conserved = read("CONSERVATION.json")
rawpath = "app/src/main/assets/content/v2-curriculum.json"
old = blob(conserved["priorCommit"], rawpath)
new = blob(conserved["implementationCommit"], rawpath)
oldid = b'"cumulativeReviewPolicyId":"unit6-prior-reading-subject-noun-behavior-v2"'
newid = b'"cumulativeReviewPolicyId":"unit6-prior-present-subject-noun-behavior-v3"'
assert old.count(oldid) == 1 and new == old.replace(oldid, newid)
assert sha(old) == conserved["rawCurriculumSHA256Before"] and sha(new) == conserved["rawCurriculumSHA256After"]
baseline = "app/src/test/java/dev/russiancompanion/v2/Unit6PresentReviewTest.kt"
assert sha(blob("ed6bc90227c0f89966b18ee7ecf35d09e8921a5c", baseline)) == conserved["baselineWholeTestFileSHA256"]
assert sha(blob(conserved["followupCommit"], baseline)) == conserved["baselineWholeTestFileSHA256"]
assert len(conserved["oldPolicyObjectsSHA256"]) == 32
policy_path = "app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt"
for commit in [conserved["priorCommit"], conserved["implementationCommit"]]:
    objects = {name: text for text, name in re.findall(r'^(internal object (\w+CumulativeReviewPolicy) \{.*?^\})',
               blob(commit, policy_path).decode(), re.M | re.S)}
    for name, expected in conserved["oldPolicyObjectsSHA256"].items():
        assert sha(objects[name].encode()) == expected
assert len(conserved["originalArchives"]) == 66
for row in conserved["originalArchives"]:
    path = root / row["path"]
    assert sha(path.read_bytes()) == row["sha256"]
    with zipfile.ZipFile(path) as archive:
        assert archive.testzip() is None
for path, expected in conserved["precedingEvidenceSHA256"].items():
    assert sha((root / path).read_bytes()) == expected, path
identity = read("SOURCE_IDENTITY.json")
assert identity["canonicalReleaseSourceDigest"] != identity["recordedCandidateSourceDigest"]
assert sha((here / "SourceIdentity.java").read_bytes()) == identity["helperSHA256"]
print(json.dumps({"initialReportedAndPassed": 21, "separateFollowupReportedAndPassed": 5,
                  "scopesNotSummed": True, "baselineFileUnchanged": True,
                  "originalArchivesConserved": 66, "bindings": "RECONCILIATION_PENDING",
                  "newRuntimeTestsExecutedByVerifier": 0}))
