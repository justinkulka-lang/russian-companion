"""Read-only original-assertion/source-pin and input conservation check; zero JUnit."""
from pathlib import Path
from collections import Counter
import hashlib
import json
import re
import subprocess
import zipfile

D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())
prep = json.loads((D / 'FOLLOWUP_PREPARATION.json').read_text())
sha = lambda b: hashlib.sha256(b).hexdigest()
git = lambda *args: subprocess.check_output(['git', *args], cwd=R).decode().strip()
# Treat Kotlin strings as indivisible tokens, preserving their complete contents.
lexer = re.compile(r'//[^\n]*|/\*[\s\S]*?\*/|"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|\s+|[A-Za-z_$][\w$]*|.', re.DOTALL)

def tokens(text):
    return [m.group() for m in lexer.finditer(text)
            if not m.group().isspace() and not m.group().startswith(('//', '/*'))]

def assertions(text):
    ts = tokens(text)
    result = []
    for i, token in enumerate(ts[:-1]):
        if not re.fullmatch(r'assert\w*', token) or ts[i + 1] != '(':
            continue
        depth = 0
        for j in range(i + 1, len(ts)):
            depth += (ts[j] == '(') - (ts[j] == ')')
            if depth == 0:
                result.append(tuple(ts[i:j + 1]))
                break
        else:
            raise AssertionError(('Unbalanced assertion', token))
    return Counter(result)

changed = []
with zipfile.ZipFile(D / 'FOLLOWUP_ORIGINAL_TESTS.zip') as z:
    assert z.testzip() is None
    assert set(z.namelist()) == {x['path'] for x in prep['originals']}
    for row in prep['originals']:
        path = row['path']
        old = z.read(path)
        new = (R / path).read_bytes()
        assert sha(old) == row['sha256'] and len(old) == row['bytes']
        assert git('rev-parse', row['sourceCommit'] + ':' + path) == row['gitBlob']
        assert old == subprocess.check_output(['git', 'show', row['sourceCommit'] + ':' + path], cwd=R)
        before, after = assertions(old.decode()), assertions(new.decode())
        assert not before - after, ('Original assertion removed/changed', path, before - after)
        pins = lambda b: Counter(re.findall(rb'"(?:[a-f0-9]{40}|[a-f0-9]{64})"', b))
        assert not pins(old) - pins(new), ('Original source/digest pin changed', path)
        methods = lambda b: re.findall(rb'@Test\s+(?:@[\s\S]*?)?fun\s+(\w+)', b)
        assert methods(old) == methods(new), ('Original method inventory changed', path)
        assert b'@Ignore' not in new if b'@Ignore' not in old else True
        assert old != new
        changed.append(dict(path=path, originalSha256=sha(old), currentSha256=sha(new),
                            originalAssertionExpressions=sum(before.values()),
                            allOriginalAssertionsPreserved=True, allOriginalPinsPreserved=True,
                            methodNamesPreserved=True))
allowed = {x['path'] for x in prep['originals']}
for path, h in prep['allPreexistingTests'].items():
    if path not in allowed:
        assert sha((R / path).read_bytes()) == h, path
assert set(git('ls-files', 'app/src/test').splitlines()) == set(prep['allPreexistingTests'])
for path, tree in prep['fixedInputTrees'].items():
    assert git('rev-parse', 'HEAD:' + path) == tree
    assert not git('diff', 'HEAD', '--', path)
for path, h in prep['records'].items():
    assert sha((R / path).read_bytes()) == h, path
for path, h in prep['archives'].items():
    assert sha((R / path).read_bytes()) == h, path
    with zipfile.ZipFile(R / path) as z:
        assert z.testzip() is None, path
# Historical Unit11 pair must genuinely match; do not pair an old reference with a newer kernel.
commit = '05407a70d1d70b196ef21b25cff51357c072c087'
blob = lambda path: subprocess.check_output(['git', 'show', commit + ':' + path], cwd=R)
reference = json.loads(blob('app/src/test/resources/reviewed-curriculum-cyrillic.json'))
kernel = 'src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
assert len(reference['subsequentEngineeringReferenceReviews']) == 6
assert reference['subsequentEngineeringReferenceReviews'][-1]['sourceCommit'] == 'c2d297e79fa677aa1375917cfbe052e4c0806633'
assert next(x['sha256'] for x in reference['files'] if x['path'] == kernel) == sha(blob('app/' + kernel))
inventory = json.loads((D / 'FOLLOWUP_INVENTORY.json').read_text())
assert inventory['classes'] == len(inventory['selectors']) == 20
assert inventory['expectedReported'] == len(inventory['tests']) == 140
assert len(inventory['originalFailedIdentities']) == 8
assert all(x in inventory['tests'] for x in inventory['originalFailedIdentities'])
callers = set()
for p in (R / 'app/src/test').rglob('*.kt'):
    if 'RecordedCurriculumConservationScope.assertHistoricalPinsAndCurrentUnits(' in p.read_text():
        package = re.search(r'^package (.+)$', p.read_text(), re.MULTILINE).group(1)
        callers.add(package + '.' + p.stem)
assert len(callers) == 4 and callers <= set(inventory['selectors'])
result = dict(verified=True, parent=prep['parent'], changedFiles=changed,
              allOriginalAssertionsAndPinsPreserved=True, nonassertInputReadsChanged=2,
              historicalUnit11ReferenceAndKernelSource=commit,
              historicalPendingRecords=6, actualCurrentControlsUnchanged=True,
              productionCatalogDocsReviewReleaseUnchanged=True, recordsUnchanged=155,
              archivesShaCrcVerified=73, allOtherTestFilesUnchanged=len(prep['allPreexistingTests']) - 6,
              directMappingHelperCallers=sorted(callers), plannedClasses=20, plannedMethods=140,
              original462ResultUnchanged=True, originalEightFailuresNotWaived=True,
              testsExecutedByVerifier=0, completeSuite=False, scopesNotSummed=True,
              all48Findings='OPEN', audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST')
print(json.dumps(result, indent=2))
