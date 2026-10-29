#!/usr/bin/env python3
"""Read-only baseline integrity checks, not another test execution."""
from pathlib import Path
import hashlib
import json
import subprocess
import zipfile
import xml.etree.ElementTree as ET
from datetime import datetime,timezone

here = Path(__file__).resolve().parent
root = Path(subprocess.check_output(["git", "rev-parse", "--show-toplevel"], cwd=here, text=True).strip())
sha = lambda data: hashlib.sha256(data).hexdigest()
read = lambda name: json.loads((here / name).read_text())
tracked=set(subprocess.check_output(["git","ls-files"],cwd=root,text=True).splitlines())
for name, expected in read("SHA256.json").items():
    assert (here/name).relative_to(root).as_posix() in tracked,("Untracked evidence",name)
    assert sha((here / name).read_bytes()) == expected, name
runtime, discovery = read("REGRESSION_RESULT.json"), read("DISCOVERY_RESULT.json")
source = "38542882cd9ab2e075691eca19442ddfabd17b6b"
assert runtime["sourceCommit"] == discovery["sourceCommit"] == source
prep=read("PREPARATION.json")
for p,value in prep["preexistingTestFiles"].items():
    assert sha(subprocess.check_output(["git","show",source+":"+p],cwd=root))==value,p
for p,value in prep["unchangedTrees"].items():
    assert subprocess.check_output(["git","rev-parse",source+":"+p],cwd=root,text=True).strip()==value,p
assert sha(subprocess.check_output(["git","show",source+":app/src/test/java/dev/russiancompanion/v2/Unit7GenderReviewBaselineTest.kt"],cwd=root))==prep["newTestSha256"]
assert runtime["testCounts"] == dict(reported=2, passed=1, failed=1, errors=0, skipped=0)
assert runtime["exitCode"] == 1 and discovery["exitCode"] == 0
assert runtime['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment'
assert runtime['tasks']['lintDebug']=='> Task :app:lintDebug'
assert {(x['method'],x['outcome']) for x in runtime['tests']}=={
 ('printAndObjectAnswersCannotReplaceFreshAssignedGenderAnswer','FAILED'),
 ('actualCurrentPartialTaskRoundtripsReopensAndCompletesItsOwnObligation','PASSED')}
assert discovery["testCounts"] == dict(reported=2, passed=0, failed=0, errors=0, skipped=2)
assert runtime["lintIssuesBySeverity"] == dict(Fatal=0, Error=0, Warning=20, Hint=1)
assert runtime["discoveryComparison"]["sameInputTrees"]
assert runtime["discoveryComparison"]["exactSameMethodSet"]
assert runtime["inputTrees"] == discovery["inputTrees"] == read("CONSERVATION.json")["inputTrees"]
with zipfile.ZipFile(here / "RAW_BASELINE.zip") as raw:
    assert raw.testzip() is None
    for mode in ["DISCOVERY","REGRESSION"]:
        c=json.loads(raw.read(mode+"/CONTEXT.json"))
        start=datetime.fromisoformat(c["startedUtc"])
        result=json.loads(raw.read(mode+"/RESULT.json"))
        assert c["sourceCommit"]==result["sourceCommit"]==source
        assert c["inputTrees"]==result["inputTrees"]
        assert sha(raw.read("run.py"))==c["runnerSha256"]
        for path,expected in c["initScriptSha256"].items():assert sha(raw.read("INIT/"+Path(path).name))==expected
        assert c["exclusions"]==prep["exclusions"] and c["selectors"]==["dev.russiancompanion.v2.Unit7GenderReviewBaselineTest"]
        completion=json.loads(raw.read(mode+"_COMPLETION.json"))
        assert completion["runnerExit"]==result["exitCode"]==int(raw.read(mode+"/RUN.exit"))==int(completion["gradleExit"])
        suites=[n for n in raw.namelist() if n.startswith(mode+"/XML/") and n.endswith(".xml")]
        assert len(suites)==1
        suite=ET.fromstring(raw.read(suites[0]));stamp=datetime.fromisoformat(suite.get("timestamp").replace("Z","+00:00"))
        if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
        assert stamp.timestamp()>=start.timestamp()-1
        rows={}
        for case in suite.findall("testcase"):
            key=(case.get("classname"),case.get("name"));assert key not in rows
            rows[key]="FAILED" if case.find("failure") is not None else "ERROR" if case.find("error") is not None else "SKIPPED" if case.find("skipped") is not None else "PASSED"
        events=[json.loads(v) for v in raw.read(mode+"/EVENTS.jsonl").splitlines()];events=[v for v in events if v["kind"]=="RESULT"]
        actual={(v["className"],v["name"]):v["resultType"] for v in events}
        assert len(events)==len(actual)==len(rows)==2
        assert rows=={(v["testClass"],v["method"]):v["outcome"] for v in result["tests"]}
        assert actual=={key:{"PASSED":"SUCCESS","FAILED":"FAILURE","ERROR":"FAILURE","SKIPPED":"SKIPPED"}[value] for key,value in rows.items()}
    assert 'CompileCommand: exclude androidx/sqlite/driver/SupportSQLiteConnection.prepare bool exclude = true' in raw.read('REGRESSION/RUN.log').decode()
    assert '-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare' in raw.read('INIT/resources.gradle').decode()
    index = read("RAW_BASELINE_INDEX.json")
    assert sorted(raw.namelist()) == sorted(r["path"] for r in index)
    for record in index:
        data = raw.read(record["path"])
        assert sha(data) == record["sha256"] and len(data) == record["bytes"]
    xml = ET.fromstring(raw.read("REGRESSION/XML/TEST-dev.russiancompanion.v2.Unit7GenderReviewBaselineTest.xml"))
    assert "actualAdvanceWithoutGender=true" in xml.find("system-out").text
    failures = xml.findall("testcase/failure")
    assert len(failures) == 1 and "must refuse actual advancement" in failures[0].get("message")
    capture_document = raw.read("REGRESSION/CAPTURE/PARTIAL_UNIT7_V1.json")
    identity = read("CAPTURE_IDENTITY.json")
    assert sha(capture_document) == identity["documentSHA256"]
    for mode in ["DISCOVERY", "REGRESSION"]:
        original = json.loads(raw.read(mode + "/SHA256.json"))
        for path, expected in original.items():
            if path == "CAPTURE/PARTIAL_UNIT7_V1.zip":
                data = (here / "BASELINE_CURRENT_UNIT7_V1.zip").read_bytes()
            elif path == "CAPTURE/IDENTITY.json":
                data = (here / "CAPTURE_IDENTITY.json").read_bytes()
            else:
                data = raw.read(mode + "/" + path)
            assert sha(data) == expected, (mode, path)
archive = here / "BASELINE_CURRENT_UNIT7_V1.zip"
assert sha(archive.read_bytes()) == identity["zipSHA256"] and archive.stat().st_size == identity["zipBytes"]
assert identity["producerCommit"] == source
with zipfile.ZipFile(archive) as backup:
    assert backup.testzip() is None
    assert sorted(backup.namelist()) == ["learner.json", "manifest.json"]
    document = json.loads(backup.read("learner.json"))
assert document == json.loads(capture_document)
task = next(a for a in document["reviewAssignments"] if a["targetUnit"] == 7)
assert task["schema"] == 1 and task["policyId"] == identity["policyId"]=="unit7-prior-reading-inanimate-case-v1"
assert [s["objectiveId"] for s in task["slots"]]==["obj-u1-decoding","obj-u6-animacy"]
assert any(a["targetUnit"]==6 and a["schema"]==8 and a["policyId"]=="unit6-prior-present-subject-noun-behavior-v3" for a in document["reviewAssignments"])
assert task["digest"] == identity["assignmentDigest"] and len(task["slots"]) == 2
answers = [a for a in document["reviewAnswers"] if a["assignmentId"] == task["id"]]
assert len(answers) == 1 and answers[0]["slotIndex"] == 0 and answers[0]["acceptedOrder"] > task["boundary"]
assert not document["recordings"] and not any(c["unitNumber"] == 7 for c in document["unitCompletions"])
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
                  "originalArchivesConserved": 66, "additionalSchema1Capture": identity["zipSHA256"],
                  "newRuntimeTestsExecutedByVerifier": 0}))
