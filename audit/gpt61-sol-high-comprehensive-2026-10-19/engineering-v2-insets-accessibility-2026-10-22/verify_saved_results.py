"""Read-only verification of immutable V2 UI regression evidence; no audit closure."""
import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import subprocess
import xml.etree.ElementTree as ET
import zipfile

p = argparse.ArgumentParser()
p.add_argument('--repository', type=Path, required=True)
repo = p.parse_args().repository.resolve()
folder = Path(__file__).resolve().parent
read = lambda path: json.loads(path.read_text())
sha = lambda raw: hashlib.sha256(raw).hexdigest()
git = lambda *args: subprocess.check_output(['git', *args], cwd=repo)

def check(value, message):
    if not value:
        raise ValueError(message)

manifest = read(folder / 'SHA256_CURRENT.json')
check(set(manifest) == {f.relative_to(folder).as_posix() for f in folder.rglob('*')
                      if f.is_file() and f.name != 'SHA256_CURRENT.json'}, 'payload manifest completeness')
for name, digest in manifest.items():
    check(sha((folder / name).read_bytes()) == digest, 'payload digest: ' + name)
identities = {}
for mode, count, passed, failed, code in [('BASELINE', 7, 0, 7, 1), ('FINAL', 11, 11, 0, 0)]:
    context = read(folder / mode / 'CONTEXT.json')
    result = read(folder / mode / 'RESULT.json')
    check(context['sourceCommit'] == result['sourceCommit'], 'source pin')
    for path, tree in context['inputTrees'].items():
        check(git('rev-parse', context['sourceCommit'] + ':' + path).decode().strip() == tree, 'input tree: ' + path)
    index = read(folder / ('RAW_' + mode + '_INDEX.json'))
    with zipfile.ZipFile(folder / ('RAW_' + mode + '.zip')) as archive:
        check(archive.testzip() is None, 'raw CRC')
        check(len(archive.namelist()) == len(set(archive.namelist())) == len(index), 'unique raw members')
        check(set(archive.namelist()) == set(index), 'raw member inventory')
        for name, value in index.items():
            rel = PurePosixPath(name)
            check(not rel.is_absolute() and '..' not in rel.parts, 'raw path safety')
            raw = archive.read(name)
            check(sha(raw) == value['sha256'] and len(raw) == value['bytes'], 'raw member identity')
        check(int(archive.read('RUN.exit')) == result['exitCode'] == code, 'actual exit')
        xml = {}
        for name in index:
            if not name.startswith('XML/') or not name.endswith('.xml'):
                continue
            for case in ET.fromstring(archive.read(name)).findall('testcase'):
                key = (case.get('classname'), case.get('name'))
                check(key not in xml, 'duplicate XML identity')
                xml[key] = ('FAILED' if case.find('failure') is not None else
                            'ERROR' if case.find('error') is not None else
                            'SKIPPED' if case.find('skipped') is not None else 'PASSED')
        rows = {(r['testClass'], r['method']): r['outcome'] for r in result['tests']}
        events = [json.loads(line) for line in archive.read('EVENTS.jsonl').decode().splitlines()]
        event_rows = [e for e in events if e['kind'] == 'RESULT']
        outcomes = {'SUCCESS': 'PASSED', 'FAILURE': 'FAILED', 'SKIPPED': 'SKIPPED'}
        event_map = {(e['className'], e['name']): outcomes[e['resultType']] for e in event_rows}
        check(xml == rows == event_map and len(rows) == len(event_rows) == count, 'exact XML/events/outcomes')
        check(result['testCounts'] == dict(reported=count, passed=passed, failed=failed, errors=0, skipped=0), 'scope counts')
        if mode == 'FINAL':
            issues = ET.fromstring(archive.read('LINT.xml')).findall('issue')
            check(result['lint'] == {s: sum(i.get('severity') == s for i in issues)
                                    for s in ['Fatal', 'Error', 'Warning', 'Hint']}, 'lint identity')
            check(result['lint']['Error'] == result['lint']['Fatal'] == 0, 'lint errors')
            log = archive.read('RUN.log').decode()
            check('BUILD SUCCESSFUL' in log, 'final build success')
            for task in ['lintDebug', 'verifyReleaseGateAttachment']:
                check(('> Task :app:' + task) in log.splitlines(), 'executed ' + task)
    identities[mode] = set(rows)
check(identities['BASELINE'] < identities['FINAL'], 'baseline seven retained plus controls')
baseline = read(folder / 'BASELINE/CONTEXT.json')['sourceCommit']
final = read(folder / 'FINAL/CONTEXT.json')['sourceCommit']
check(git('rev-parse', baseline + ':app/src/test') == git('rev-parse', final + ':app/src/test'), 'unchanged assertions')
conservation = read(folder / 'CONSERVATION.json')
for name, digest in conservation['original66ArchiveHashes'].items():
    check(sha((repo / name).read_bytes()) == digest, 'retained archive SHA')
    with zipfile.ZipFile(repo / name) as archive:
        check(archive.testzip() is None, 'retained archive CRC')
for path, unchanged in conservation['inputTreesUnchangedFromBaseline'].items():
    check(unchanged and git('rev-parse', baseline + ':' + path) == git('rev-parse', final + ':' + path), 'conserved input: ' + path)
print(json.dumps(dict(status='SAVED_HOST_EVIDENCE_VERIFIED', baselineMethods=7,
    baselineFailed=7, correctedMethods=11, correctedPassed=11, originalArchives=66,
    unchangedRegressionAssertions=True, currentInputTreesMatchTestedSource={
        path: git('rev-parse', 'HEAD:' + path) == git('rev-parse', final + ':' + path)
        for path in ['app/src/main', 'app/src/test', 'catalog', 'docs', 'review', 'release']},
    sourceBindingReconciliation='PENDING', completeSuite=False, findingsClosed=0,
    physicalAcceptance='OUTSTANDING_LAST'), indent=2))
