"""Read-only Unit12 broad-input conservation; no generators or JUnit executions."""
from pathlib import Path
import hashlib
import json
import subprocess
import zipfile

D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())
git = lambda *args: subprocess.check_output(['git', *args], cwd=R, text=True).strip()
sha = lambda p: hashlib.sha256(p.read_bytes()).hexdigest()
p = json.loads((D / 'BROAD_PREPARATION.json').read_text())
for path, tree in p['inputTrees'].items():
    assert git('rev-parse', 'HEAD:' + path) == tree
    assert not git('diff', 'HEAD', '--', path)
assert p['inputTrees']['app/src/test'] == git('rev-parse', '6959ab29b09cfef9918859b2d40991dd7042fae8:app/src/test')
assert p['inputTrees']['app/src/main'] == git('rev-parse', 'ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913:app/src/main')
for source in p['requiredAncestorCorrections']:
    subprocess.run(['git', 'merge-base', '--is-ancestor', source, 'HEAD'], cwd=R, check=True)
assert set(git('ls-files', 'app/src/test').splitlines()) == set(p['allPreexistingTests'])
for path, digest in p['allPreexistingTests'].items():
    assert sha(R / path) == digest, path
for path, digest in p['records'].items():
    assert sha(R / path) == digest, path
for path, digest in p['archives'].items():
    assert sha(R / path) == digest, path
    with zipfile.ZipFile(R / path) as z:
        assert z.testzip() is None, path
comparison = json.loads(subprocess.check_output(['python3', str(D / 'verify_followup_comparisons.py')], cwd=R, text=True))
assert comparison['verified'] and comparison['allOriginalAssertionsAndPinsPreserved']
inv = json.loads((D / 'BROAD_INVENTORY.json').read_text())
prior_path = R / inv['priorBroadResultPath']
assert sha(prior_path) == inv['priorBroadResultSha256']
prior = json.loads(prior_path.read_text())
old = {(x['testClass'], x['method']) for x in prior['tests']}
expected = {(x['testClass'], x['method']) for x in inv['tests']}
added = {(x['testClass'], x['method']) for x in inv['expectedAdded']}
assert len(old) == 1441 and len(added) == 17 and not old & added
assert expected == old | added and inv['expectedRemoved'] == []
assert len(expected) == inv['expectedReported'] == 1458
assert len({x[0] for x in expected}) == inv['expectedClasses'] == 253
print(json.dumps(dict(verified=True, parent=p['parent'], inputTrees=p['inputTrees'],
    all271TestFilesUnchanged=True, allOriginalAssertionsAndPinsPreserved=True,
    reviewedTestTreeSource='6959ab29b09cfef9918859b2d40991dd7042fae8',
    requiredAncestorCorrections=p['requiredAncestorCorrections'], recordsUnchanged=155,
    archivesShaCrcVerified=73, all1441PriorBroadIdentitiesIncluded=True,
    expectedAdded=17, expectedRemoved=0, expectedMethods=1458, expectedClasses=253,
    testsExecutedByVerifier=0, completeSuite=False, all48Findings='OPEN',
    audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST')))
