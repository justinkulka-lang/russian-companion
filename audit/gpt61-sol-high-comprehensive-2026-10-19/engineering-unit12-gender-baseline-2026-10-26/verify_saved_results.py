"""Read-only actual Unit12 baseline/capture verification; zero new tests."""
from pathlib import Path
from datetime import datetime, timezone
import hashlib
import json
import subprocess
import xml.etree.ElementTree as ET
import zipfile

D = Path(__file__).resolve().parent
R = Path(subprocess.check_output(['git', 'rev-parse', '--show-toplevel'], cwd=D, text=True).strip())
sha = lambda data: hashlib.sha256(data).hexdigest()
read = lambda name: json.loads((D / name).read_text())
blob = lambda source, name: subprocess.check_output(['git', 'show', source + ':' + name], cwd=R)
git = lambda *args: subprocess.check_output(['git', *args], cwd=R, text=True).strip()
manifest = read('SHA256.json')
assert set(manifest) == {p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name != 'SHA256.json'}
tracked = set(git('ls-files').splitlines())
for name, expected in manifest.items():
    assert sha((D / name).read_bytes()) == expected and (D / name).relative_to(R).as_posix() in tracked
r, d, identity, prep, con = [read(name) for name in [
    'REGRESSION_RESULT.json', 'DISCOVERY_RESULT.json', 'CAPTURE_IDENTITY.json', 'PREPARATION.json', 'CONSERVATION.json']]
S = identity['producerCommit']
assert r['sourceCommit'] == d['sourceCommit'] == S and git('rev-parse', S + '^') == prep['baselineParent']
assert r['testCounts'] == dict(reported=2, passed=1, failed=1, errors=0, skipped=0)
assert r['classes'] == 1 and r['exitCode'] == 1
assert d['testCounts'] == dict(reported=2, passed=0, failed=0, errors=0, skipped=2) and d['exitCode'] == 0
assert {t['method']: t['outcome'] for t in r['tests']} == {
    'printAndPastAnswersCannotReplaceFreshAssignedNounGenderAnswer': 'FAILED',
    'actualCurrentPartialTaskRoundtripsReopensAndCompletesItsOwnObligation': 'PASSED',
}
assert r['lintIssuesBySeverity'] == dict(Fatal=0, Error=0, Warning=20, Hint=1)
assert r['tasks']['verifyReleaseGateAttachment'] == '> Task :app:verifyReleaseGateAttachment'
assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
assert r['inputTrees'] == d['inputTrees'] == con['inputTrees']
for name, expected in prep['preexistingTestFiles'].items():
    assert sha(blob(S, name)) == expected
for name, expected in prep['unchangedTrees'].items():
    assert git('rev-parse', S + ':' + name) == expected
assert sha(blob(S, prep['newTestFile'])) == prep['newTestSha256']
with zipfile.ZipFile(D / 'RAW_BASELINE.zip') as archive:
    assert archive.testzip() is None
    index = read('RAW_BASELINE_INDEX.json')
    assert set(archive.namelist()) == {row['path'] for row in index}
    for row in index:
        data = archive.read(row['path'])
        assert len(data) == row['bytes'] and sha(data) == row['sha256']
    for mode in ['DISCOVERY', 'REGRESSION']:
        context = json.loads(archive.read(mode + '/CONTEXT.json'))
        result = json.loads(archive.read(mode + '/RESULT.json'))
        assert context['sourceCommit'] == result['sourceCommit'] == S
        assert context['sourceRootTree'] == git('rev-parse', S + '^{tree}')
        assert context['inputTrees'] == result['inputTrees'] == con['inputTrees']
        for name, expected in context['inputTrees'].items():
            assert git('rev-parse', S + ':' + name) == expected
        assert context['exclusions'] == prep['exclusions']
        assert context['selectors'] == ['dev.russiancompanion.v2.Unit12GenderReviewBaselineTest']
        assert sha(archive.read('run.py')) == context['runnerSha256']
        for name, expected in context['initScriptSha256'].items():
            assert sha(archive.read('INIT/' + Path(name).name)) == expected
        done = json.loads(archive.read(mode + '_COMPLETION.json'))
        assert done['runnerExit'] == result['exitCode'] == int(done['gradleExit']) == int(archive.read(mode + '/RUN.exit'))
        suites = [name for name in archive.namelist() if name.startswith(mode + '/XML/') and name.endswith('.xml')]
        assert len(suites) == 1
        suite = ET.fromstring(archive.read(suites[0]))
        stamp = datetime.fromisoformat(suite.get('timestamp').replace('Z', '+00:00'))
        if stamp.tzinfo is None:
            stamp = stamp.replace(tzinfo=timezone.utc)
        assert stamp.timestamp() >= datetime.fromisoformat(context['startedUtc']).timestamp() - 1
        rows = {}
        for test in suite.findall('testcase'):
            key = (test.get('classname'), test.get('name'))
            assert key not in rows
            rows[key] = 'FAILED' if test.find('failure') is not None else 'ERROR' if test.find('error') is not None else 'SKIPPED' if test.find('skipped') is not None else 'PASSED'
        events = [json.loads(line) for line in archive.read(mode + '/EVENTS.jsonl').splitlines()]
        events = [e for e in events if e['kind'] == 'RESULT']
        assert len(events) == len(rows) == 2
        assert rows == {(t['testClass'], t['method']): t['outcome'] for t in result['tests']}
        assert {(e['className'], e['name']): e['resultType'] for e in events} == {
            key: {'PASSED': 'SUCCESS', 'FAILED': 'FAILURE', 'ERROR': 'FAILURE', 'SKIPPED': 'SKIPPED'}[value] for key, value in rows.items()}
        for name, expected in json.loads(archive.read(mode + '/SHA256.json')).items():
            if name == 'CAPTURE/PARTIAL_UNIT12_V1.zip':
                data = (D / 'BASELINE_CURRENT_UNIT12_V1.zip').read_bytes()
            elif name == 'CAPTURE/IDENTITY.json':
                data = (D / 'CAPTURE_IDENTITY.json').read_bytes()
            else:
                data = archive.read(mode + '/' + name)
            assert sha(data) == expected
    xml = ET.fromstring(archive.read('REGRESSION/XML/TEST-dev.russiancompanion.v2.Unit12GenderReviewBaselineTest.xml'))
    assert 'actualAdvanceWithoutNounGender=true' in xml.findtext('system-out', '')
    failures = xml.findall('testcase/failure')
    assert len(failures) == 1 and 'proposed Unit12 obligation must refuse actual advancement' in failures[0].get('message')
    original_document = archive.read('REGRESSION/CAPTURE/PARTIAL_UNIT12_V1.json')
    completed = json.loads(archive.read('REGRESSION/CAPTURE/COMPLETED_UNIT12_V1.json'))
    assert sha(original_document) == identity['documentSHA256']
    monitor = json.loads(archive.read('DURABLE_MONITOR_STATUS.json'))
    assert monitor['completionExists'] and not monitor['newNativeReports']
    assert '-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare' in archive.read('INIT/resources.gradle').decode()
capture = D / 'BASELINE_CURRENT_UNIT12_V1.zip'
assert sha(capture.read_bytes()) == identity['zipSHA256'] and capture.stat().st_size == identity['zipBytes']
with zipfile.ZipFile(capture) as archive:
    assert archive.testzip() is None and sorted(archive.namelist()) == ['learner.json', 'manifest.json']
    raw = archive.read('learner.json')
    doc, meta = json.loads(raw), json.loads(archive.read('manifest.json'))
    assert doc == json.loads(original_document)
    assert meta['formatVersion'] == doc['schemaVersion'] == identity['documentSchemaVersion'] == 3
    assert meta['applicationId'] == 'dev.russiancompanion' and meta['recordingCount'] == identity['recordingCount'] == 0
    assert meta['files'] == [dict(path='learner.json', sha256=sha(raw), bytes=len(raw))]
task, = [a for a in doc['reviewAssignments'] if a['targetUnit'] == 12]
assert task['policyId'] == identity['policyId'] == 'unit12-prior-reading-past-gender-v1'
assert task['schema'] == identity['schema'] == 1 and identity['targetUnit'] == 12
assert task['policyDigest'] == identity['policyDigest'] and task['digest'] == identity['assignmentDigest']
assert [s['objectiveId'] for s in task['slots']] == ['obj-u1-decoding', 'obj-u11-past-aspect']
assert [s['ownerUnit'] for s in task['slots']] == [1, 11]
assert task['slots'][1]['itemId'] == 'u11-i-past-aspect-1'
assert task['slots'][1]['unitDigest'] == identity['unit11OwnerDigest'] == '1d4c25c25e20b7fb6629995fcfb66e29f02554e276c5a7f5ec69fb5ac0a2ca5c'
answers = [a for a in doc['reviewAnswers'] if a['assignmentId'] == task['id']]
assert len(answers) == 1 and answers[0]['slotIndex'] == 0
assert answers[0]['acceptedOrder'] > task['boundary'] and answers[0]['assignmentDigest'] == task['digest']
assert not doc['recordings'] and sorted(c['unitNumber'] for c in doc['unitCompletions']) == list(range(1, 12))
for unit, schema, policy in [(8, 10, 'unit8-prior-present-nominative-agreement-v2'),
                             (9, 11, 'unit9-prior-noun-behavior-plural-gender-v3'),
                             (10, 12, 'unit10-prior-present-numeral-gender-v2'),
                             (11, 13, 'unit11-prior-noun-behavior-possession-absence-v2')]:
    assert any(a['targetUnit'] == unit and a['schema'] == schema and a['policyId'] == policy for a in doc['reviewAssignments'])
assert sorted(c['unitNumber'] for c in completed['unitCompletions']) == list(range(1, 13))
assert doc['profile']['currentUnitNumber'] == 12
assert completed['profile']['currentUnitNumber'] == 13
assert completed['profile']['currentSessionId'] == 'u13-s-orient'
assert completed['reviewAssignments'] == doc['reviewAssignments']
completion, = [c for c in completed['unitCompletions'] if c['unitNumber'] == 12]
assert completion['reviewAssignmentId'] == task['id'] and completion['reviewPolicyDigest'] == task['policyDigest']
assert completion['reviewCompletionOrder'] > task['boundary']
assert con['priorArchiveCount'] == len(con['priorArchives']) == 72
for name, expected in con['priorArchives'].items():
    assert sha((R / name).read_bytes()) == expected
    with zipfile.ZipFile(R / name) as archive:
        assert archive.testzip() is None
records = read('CURRENT_RECORD_HASHES.json')
assert len(records) == 155
for name, expected in records.items():
    assert sha(blob(S, name)) == expected
with zipfile.ZipFile(D / 'ORIGINAL_BASELINE_TEST.zip') as archive:
    assert archive.read(prep['newTestFile']) == blob(S, prep['newTestFile'])
assert sha(blob(S, 'app/src/main/assets/content/v2-curriculum.json')) == identity['rawCurriculumSHA256']
assert con['canonicalSourceDigest'] == '61bbaafebb57184f2702bf321f09ecaa74ec3e2603bd8bc8faf91f4e2e4c1b19'
print(json.dumps(dict(
    verifiedBaseline=S, reported=2, passed=1, intendedFailed=1, currentDeclaredGateBypassed=False,
    ordinaryKnowledgeBypassed=False, priorArchivesConserved=72, newGenuineUnit12V1Capture=identity['zipSHA256'],
    captureBytes=identity['zipBytes'], assignmentSchema=1, backupFormatVersion=3, recordings=0,
    actualCurrentUnit11V2Prerequisite=True, completeSnapshotRetained=True, newRuntimeMethodsExecutedByVerifier=0,
)))
