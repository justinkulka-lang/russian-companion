"""Collect actual Unit12 baseline/capture once; never rerun or change app/tests."""
from pathlib import Path
import hashlib
import json
import re
import shutil
import subprocess
import xml.etree.ElementTree as ET
import zipfile

R = Path('/workspace/russian-companion')
W = Path(__file__).parent
B = R / 'audit/gpt61-sol-high-comprehensive-2026-10-19'
D = B / 'engineering-unit12-gender-baseline-2026-10-26'
PRE = B / 'engineering-unit11-source-reconciliation-2026-10-26'
sha = lambda data: hashlib.sha256(data).hexdigest()
git = lambda *args: subprocess.check_output(['git', *args], cwd=R, text=True).strip()


def save(name, value):
    (D / name).write_text(json.dumps(value, indent=2) + '\n')


r = json.loads((W / 'REGRESSION/RESULT.json').read_text())
c = json.loads((W / 'REGRESSION/CONTEXT.json').read_text())
done = json.loads((W / 'REGRESSION_COMPLETION.json').read_text())
S = c['sourceCommit']
assert not git('status', '--porcelain')
assert git('rev-parse', 'HEAD') == S == r['sourceCommit'] == done['sourceCommit']
assert done['resultExists'] and done['manifestExists']
assert done['runnerExit'] == r['exitCode'] == int(done['gradleExit'])
assert r['testCounts']['reported'] == 2 and r['classes'] == 1
prep = json.loads((W / 'PREPARATION.json').read_text())
assert git('rev-parse', S + '^') == prep['baselineParent']
assert sha((R / prep['newTestFile']).read_bytes()) == prep['newTestSha256']
for name, expected in prep['preexistingTestFiles'].items():
    assert sha((R / name).read_bytes()) == expected
for name, expected in prep['unchangedTrees'].items():
    assert git('rev-parse', S + ':' + name) == expected
assert not (D / 'RAW_BASELINE.zip').exists()
files = {}
for mode in ['DISCOVERY', 'REGRESSION']:
    for path in (W / mode).rglob('*'):
        if path.is_file() and path.relative_to(W / mode).as_posix() not in [
            'CAPTURE/PARTIAL_UNIT12_V1.zip', 'CAPTURE/IDENTITY.json',
        ]:
            files[path.relative_to(W).as_posix()] = path
    for name in ['CONTEXT.json', 'RESULT.json', 'RESULTS.csv', 'RUN.exit', 'SHA256.json']:
        shutil.copy2(W / mode / name, D / (mode + '_' + name))
    for suffix in ['COMPLETION.json', 'LAUNCHER.json', 'RUNNER_PROCESS.json',
                   'LAUNCHER.exit', 'RUNNER.log', 'LAUNCHER.log']:
        files[mode + '_' + suffix] = W / (mode + '_' + suffix)
for name in ['run.py', 'resources.gradle', 'supervise.py', 'durable_monitor.py', 'PREPARATION.json']:
    files[name] = W / name
for name in ['DURABLE_MONITOR.log', 'DURABLE_MONITOR_STATUS.json']:
    files[name] = W / name
for name in c['initScriptSha256']:
    files['INIT/' + Path(name).name] = Path(name)
index = []
with zipfile.ZipFile(D / 'RAW_BASELINE.zip', 'x', zipfile.ZIP_DEFLATED) as archive:
    for name, path in sorted(files.items()):
        assert not name.lower().endswith('.pdf')
        archive.write(path, name)
        index.append(dict(path=name, bytes=path.stat().st_size, sha256=sha(path.read_bytes())))
save('RAW_BASELINE_INDEX.json', index)
# Preserve actual raw failure evidence before requiring a positive capture.
cap = W / 'REGRESSION/CAPTURE'
assert (cap / 'PARTIAL_UNIT12_V1.zip').exists() and (cap / 'IDENTITY.json').exists(), 'Capture absent; inspect preserved actual positive-method outcome.'
shutil.copy2(cap / 'PARTIAL_UNIT12_V1.zip', D / 'BASELINE_CURRENT_UNIT12_V1.zip')
shutil.copy2(cap / 'IDENTITY.json', D / 'CAPTURE_IDENTITY.json')
identity = json.loads((D / 'CAPTURE_IDENTITY.json').read_text())
assert identity['producerCommit'] == S
with zipfile.ZipFile(D / 'ORIGINAL_BASELINE_TEST.zip', 'x', zipfile.ZIP_DEFLATED) as archive:
    archive.write(R / prep['newTestFile'], prep['newTestFile'])
prior = json.loads((PRE / 'CONSERVATION.json').read_text())
assert len(prior['archives']) == 72
for name, expected in prior['archives'].items():
    assert sha((R / name).read_bytes()) == expected
    with zipfile.ZipFile(R / name) as archive:
        assert archive.testzip() is None
records = json.loads((PRE / 'APPLIED_RECORDS.json').read_text())['currentRecordHashes']
assert len(records) == 155
for name, expected in records.items():
    assert sha((R / name).read_bytes()) == expected
save('CURRENT_RECORD_HASHES.json', records)
save('CONSERVATION.json', dict(
    sourceCommit=S, inputTrees=c['inputTrees'], productionUnchangedFrom=prep['baselineParent'],
    priorArchives=prior['archives'], priorArchiveCount=72, newUnit12V1Capture=identity,
    preexistingTestFilesUnchanged=len(prep['preexistingTestFiles']), currentGeneratedRecordsUnchanged=155,
    canonicalSourceDigest='61bbaafebb57184f2702bf321f09ecaa74ec3e2603bd8bc8faf91f4e2e4c1b19',
    sourceDigestBasis='Production/source inputs unchanged from prior official Kotlin calculation; no new approximate calculation or generators.',
    apkIdentity=None, apkSignature=None, settingsChanged=False, all48Findings='OPEN',
    audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST',
))
xml = ET.parse(W / 'REGRESSION/XML/TEST-dev.russiancompanion.v2.Unit12GenderReviewBaselineTest.xml').getroot()
observation = re.search(r'actualAdvanceWithoutNounGender=(true|false)', xml.findtext('system-out', ''))
save('BASELINE_OBSERVATION.json', dict(
    sourceCommit=S, testCounts=r['testCounts'], tests=r['tests'],
    actualAdvanceWithoutAssignedNounGenderAnswer=None if not observation else observation.group(1) == 'true',
    currentDeclaredGateBypassed=False, ordinaryKnowledgeBypassed=False, proposedPolicyActivated=False,
    policyId=identity['policyId'], schema=identity['schema'], lint=r['lintIssuesBySeverity'],
    tasks=r['tasks'], completeSuite=False,
))
print(json.dumps(dict(collected=True, source=S, counts=r['testCounts'], identity=identity)))
