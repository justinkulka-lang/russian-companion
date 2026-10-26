"""Read-only research reproduction/conservation; zero Android or JUnit executions."""
from pathlib import Path
import hashlib
import json
import subprocess

D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())
BASE = '05407a70d1d70b196ef21b25cff51357c072c087'


def git(*args):
    return subprocess.check_output(['git', *args], cwd=R).decode().strip()


def sha(data):
    return hashlib.sha256(data).hexdigest()


manifest = json.loads((D / 'SHA256.json').read_text())
assert set(manifest) == {p.relative_to(D).as_posix() for p in D.rglob('*')
                         if p.is_file() and p.name != 'SHA256.json'}
tracked = set(git('ls-files').splitlines())
for name, expected in manifest.items():
    path = D / name
    assert sha(path.read_bytes()) == expected and path.relative_to(R).as_posix() in tracked, name
reproduced = json.loads(subprocess.check_output(['python3', str(D / 'extract_research.py'), '--check'], cwd=R))
assert reproduced['verified'] and reproduced['runtimeMethodsExecuted'] == 0
bindings = json.loads(subprocess.check_output(['python3', str(D / 'extract_source_bindings.py'), '--check'], cwd=R))
assert bindings['verified'] and bindings['genderBindings'] == 5 and bindings['pastGenderFields'] == 3
for name, expected in json.loads((D / 'PROTECTED_EVIDENCE_HASHES.json').read_text()).items():
    assert sha((R / name).read_bytes()) == expected, name
inputs = json.loads((D / 'INPUTS.json').read_text())
assert inputs['sourceRootTree'] == git('rev-parse', BASE + '^{tree}')
introduced = git('log', '--diff-filter=A', '--format=%H', '--',
                 str(D.relative_to(R) / 'extract_research.py')).splitlines()
evidence_commit = introduced[-1] if introduced else git('rev-parse', 'HEAD')
for name, tree in inputs['inputTrees'].items():
    assert tree == git('rev-parse', BASE + ':' + name) == git('rev-parse', evidence_commit + ':' + name)
    subprocess.check_call(['git', 'diff', '--quiet', evidence_commit, '--', name], cwd=R)
if not introduced:
    pointers = {'AGENTS.md', 'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md',
                'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'}
    for line in git('diff', '--name-status', BASE).splitlines():
        status, name = line.split('\t')
        assert (status == 'A' and name.startswith(D.relative_to(R).as_posix() + '/')) or (
            status == 'M' and name in pointers), line
        if name in pointers:
            old = subprocess.check_output(['git', 'show', BASE + ':' + name], cwd=R)
            assert (R / name).read_bytes().endswith(old), name
s = json.loads((D / 'SUMMARY.json').read_text())
assert not s['activated'] and s['registeredPolicyObjectsPreserved'] == 38
assert s['objectiveChains'] == 21 and s['inventoriedCheckRecords'] == 63
assert s['historicalArchivesChecked'] == 72 and s['archivesContainingUnit12'] == 24
assert s['assignedSlots'] == 2 and s['proposedFreshSchema'] == 14
assert s['schemasPreserved'] == list(range(1, 14))
assert s['newRuntimeMethodsExecuted'] == 0 and s['freshActualCurrentUnit12Capture'] == 'NOT_YET_PRODUCED'
checkpoint = json.loads((D / 'VERIFICATION_CHECKPOINT.json').read_text())
prior = checkpoint['latestSelectedHostEvidence']
for key in ['context', 'result']:
    assert sha((R / prior[key + 'Path']).read_bytes()) == prior[key + 'Sha256']
context = json.loads((R / prior['contextPath']).read_text())
result = json.loads((R / prior['resultPath']).read_text())
assert prior['sourceCommit'] == context['sourceCommit'] == result['sourceCommit']
assert prior['command'] == context['command']
assert result['testCounts'] == {k: prior[k] for k in ['reported', 'passed', 'failed', 'errors', 'skipped']}
assert result['classes'] == prior['classes'] and result['exitCode'] == prior['exitCode']
assert result['lintIssuesBySeverity'] == prior['lint']
assert result['tasks']['verifyReleaseGateAttachment'] == '> Task :app:verifyReleaseGateAttachment'
print(json.dumps(dict(
    verified=True, sourceCommit=BASE, chains=21, inventoriedCheckRecords=63,
    archivesShaCrc=72, archivesContainingUnit12=24, proposal='NOT_ACTIVATED',
    newJUnitMethodsExecuted=0, productionTestsAnd155RecordsUnchanged=True,
    all48Findings='OPEN', audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST',
)))
