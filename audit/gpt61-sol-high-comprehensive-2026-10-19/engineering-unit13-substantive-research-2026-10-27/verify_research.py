"""Reproduce pinned research; --current also checks conservation of this checkpoint."""
from pathlib import Path
import hashlib
import json
import subprocess
import sys
import zipfile

D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())
BASE = '5971ed56cb4e17b15e4430202d206ec8df59337a'
git = lambda *args: subprocess.check_output(['git', *args], cwd=R)
sha = lambda data: hashlib.sha256(data).hexdigest()
assert sys.argv[1:] in [[], ['--current']]
manifest = json.loads((D / 'SHA256.json').read_text())
assert set(manifest) == {p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name != 'SHA256.json'}
tracked = set(git('ls-files').decode().splitlines())
for name, expected in manifest.items():
    path = D / name
    assert sha(path.read_bytes()) == expected and path.relative_to(R).as_posix() in tracked, name
research = json.loads(subprocess.check_output(['python3', str(D / 'extract_research.py'), '--check'], cwd=R))
fields = json.loads(subprocess.check_output(['python3', str(D / 'extract_source_bindings.py'), '--check'], cwd=R))
assert research['verified'] and fields['verified']
assert research['chains'] == 25 and research['inventoriedCheckRecords'] == 72
assert fields['presentItems'] == 4 and fields['pastGenderFields'] == 3 and fields['aspectSourceForms'] == 4
inputs = json.loads((D / 'INPUTS.json').read_text())
assert inputs['sourceRootTree'] == git('rev-parse', BASE + '^{tree}').decode().strip()
introduced = git('log', '--diff-filter=A', '--format=%H', '--', str(D.relative_to(R) / 'extract_research.py')).decode().splitlines()
evidence_commit = introduced[-1] if introduced else None
for name, tree in inputs['inputTrees'].items():
    assert tree == git('rev-parse', BASE + ':' + name).decode().strip()
    if evidence_commit:
        assert tree == git('rev-parse', evidence_commit + ':' + name).decode().strip()
    if sys.argv[1:] == ['--current']:
        assert tree == git('rev-parse', 'HEAD:' + name).decode().strip()
        assert not git('diff', 'HEAD', '--', name).decode().strip()
for name, expected in json.loads((D / 'PROTECTED_EVIDENCE_HASHES.json').read_text()).items():
    assert sha(git('show', BASE + ':' + name)) == expected, name
    if sys.argv[1:] == ['--current']:
        assert sha((R / name).read_bytes()) == expected, name
with zipfile.ZipFile(D / 'CHECKER_PRECHECK_1.zip') as z:
    assert z.testzip() is None
    precheck = json.loads((D / 'CHECKER_PRECHECK_1.json').read_text())
    assert set(z.namelist()) == set(precheck['archiveIndex'])
    for name, row in precheck['archiveIndex'].items():
        assert len(z.read(name)) == row['bytes'] and sha(z.read(name)) == row['sha256']
    assert precheck['newJUnitMethodsExecuted'] == precheck['newGradleTasks'] == 0
    assert b"['letterId'] == record['shippedText']" in z.read('original_checker.py')
    assert "['display'] == record['shippedText']" in (D / 'extract_source_bindings.py').read_text()
s = json.loads((D / 'SUMMARY.json').read_text())
assert not s['activated'] and s['registeredPolicyObjectsPreserved'] == 39 and s['schemasPreserved'] == list(range(1, 15))
assert s['proposedFreshSchema'] == 15 and s['assignedSlots'] == 3
assert [s[k] for k in ['selectedPresentCandidates', 'selectedAspectCandidates', 'selectedPastGenderCandidates']] == [4, 2, 2]
assert s['archivesContainingUnit13'] == 18 and s['archivesContainingUnit14'] == 3
assert s['historicalStandaloneJsonDocuments'] == 24 and s['standaloneJsonLearnersDuplicateArchiveMembers']
assert s['newRuntimeMethodsExecuted'] == 0 and s['freshActualCurrentUnit13Capture'] == 'NOT_YET_PRODUCED'
checkpoint = json.loads((D / 'VERIFICATION_CHECKPOINT.json').read_text())
prior = checkpoint['latestSelectedHostEvidence']
c = json.loads(git('show', BASE + ':' + prior['contextPath']))
r = json.loads(git('show', BASE + ':' + prior['resultPath']))
for key in ['context', 'result']:
    assert sha(git('show', BASE + ':' + prior[key + 'Path'])) == prior[key + 'Sha256']
assert c['sourceCommit'] == r['sourceCommit'] == prior['sourceCommit'] == '44cb3e669d287d2f9848113f1722ab1224af9c37'
assert c['command'] == prior['command'] and c['inputTrees'] == prior['inputTrees'] == inputs['inputTrees']
assert r['testCounts'] == prior['testCounts'] == dict(reported=1458, passed=1457, failed=0, errors=0, skipped=1)
assert r['classes'] == prior['classes'] == 253 and r['exitCode'] == prior['exitCode'] == 0
assert r['lintIssuesBySeverity'] == prior['lint'] and r['tasks'] == prior['tasks']
assert prior['tasks']['verifyReleaseGateAttachment'] == '> Task :app:verifyReleaseGateAttachment'
assert checkpoint['newRuntimeMethodsExecuted'] == 0 and not checkpoint['overlappingScopesSummed']
pointers = {'AGENTS.md', 'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md', 'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'}
if not evidence_commit or sys.argv[1:] == ['--current']:
    for line in git('diff', '--name-status', BASE).decode().splitlines():
        status, name = line.split('\t')
        assert (status == 'A' and name.startswith(D.relative_to(R).as_posix() + '/')) or (status == 'M' and name in pointers), line
    for name in pointers:
        if (R / name).read_bytes() != git('show', BASE + ':' + name):
            assert (R / name).read_bytes().endswith(git('show', BASE + ':' + name)), name
print(json.dumps(dict(verified=True, sourceCommit=BASE, currentCheckpointConservation=sys.argv[1:] == ['--current'],
    chains=25, inventoriedCheckRecords=72, archivesShaCrc=73, archivesContainingUnit13=18,
    archivesContainingUnit14=3, standaloneDocuments=24, standaloneDocumentsDuplicateArchiveMembers=True,
    currentPolicy='v2/schema6', proposal='v3/schema15_NOT_ACTIVATED', selectedCandidates=8, assignedSlots=3,
    newJUnitMethodsExecuted=0, newGradleTasks=0, noNewProductionBindingGeneration=True,
    priorBroad=dict(reported=1458,passed=1457,failed=0,errors=0,skipped=1,classes=253,exitCode=0),
    productionTestsAnd155RecordsUnchanged=True, all48Findings='OPEN', audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST')))
