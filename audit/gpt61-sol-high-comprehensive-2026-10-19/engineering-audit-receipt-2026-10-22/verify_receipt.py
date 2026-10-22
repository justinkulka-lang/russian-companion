"""Read-only receipt checks. Does not run returned scripts or validate audit semantics."""
import argparse
import ast
import collections
import csv
import hashlib
import io
import json
from pathlib import Path, PurePosixPath
import re
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument('--repository', type=Path, required=True)
repo = parser.parse_args().repository.resolve()
base = 'audit/gpt61-sol-high-comprehensive-2026-10-19/'
relative = base + 'final-independent-recheck-results/2026-10-22-claude-code/'
folder = repo / relative
external = '2ab65c1db9fa13edbe1f02feb5b7c8a2376107c1'
delivery = 'ae1105fbc78f6b5fbb2dbdce9058b08c5a44792e'
target = '8606c4d74185f594d93941bef6858b2af28e0b75'

def git(*args):
    return subprocess.check_output(['git', *args], cwd=repo)

def require(condition, message):
    if not condition:
        raise ValueError(message)

require(git('rev-parse', external + '^').decode().strip() == delivery, 'parent')
changes = git('diff', '--name-status', delivery, external).decode().splitlines()
require(len(changes) == 61 and all(x.startswith('A\t' + relative) for x in changes), 'additions-only scope')
entries = git('ls-tree', '-r', external, '--', relative).decode().splitlines()
require(len(entries) == 61 and all(x.startswith('100644 blob ') for x in entries), 'regular file modes')
required_text = (repo / (base + 'final-independent-recheck-2026-10-22/REQUIRED_RETURNS.md')).read_text()
required = re.findall(r'^- `([^`]+)`', required_text, re.M) + [
    'CURRENT_RUNTIME_EVIDENCE_VALIDATION.json', 'INDEPENDENT_RECHECK_DISCREPANCIES.md',
    'CURRENT_INPUT_SUPPLEMENT.csv',
]
require(len(required) == 25 and set(required) == {p.name for p in folder.iterdir() if p.is_file()}, '25 roots')
manifest = {}
for line in (folder / 'SHA256SUMS.txt').read_text().splitlines():
    digest, name = line.split(maxsplit=1)
    name = name.removeprefix('*')
    path = PurePosixPath(name)
    require(re.fullmatch('[0-9a-f]{64}', digest) is not None, 'digest syntax')
    require(not path.is_absolute() and '..' not in path.parts and '\\' not in name and name not in manifest, 'safe unique manifest path')
    require(hashlib.sha256((folder / name).read_bytes()).hexdigest() == digest, 'payload hash: ' + name)
    manifest[name] = digest
files = {p.relative_to(folder).as_posix() for p in folder.rglob('*') if p.is_file()}
require(len(manifest) == 60 and files == set(manifest) | {'SHA256SUMS.txt'}, 'complete manifest')
tables = {}
for name in sorted(files):
    path = folder / name
    require(not path.is_symlink(), 'symlink')
    raw = path.read_bytes()
    require(raw == git('show', external + ':' + relative + name), 'external byte identity: ' + name)
    require(path.suffix.lower() != '.pdf' and b'%PDF-' not in raw[:1024], 'PDF payload')
    text = raw.decode('utf-8-sig')
    for pattern in [r'-----BEGIN [A-Z ]*PRIVATE KEY-----', r'gh[pousr]_[A-Za-z0-9]{30,}',
                    r'github_pat_[A-Za-z0-9_]{30,}', r'(?i)bearer\s+[a-z0-9._-]{25,}']:
        require(re.search(pattern, text) is None, 'credential-pattern match: ' + name)
    if path.suffix == '.json':
        json.loads(text)
    if path.suffix == '.py':
        ast.parse(text)  # Parse only. Never import or run received scripts.
    if path.suffix == '.csv':
        rows = list(csv.reader(io.StringIO(text, newline='')))
        require(bool(rows) and len(rows[0]) == len(set(rows[0])), 'CSV header: ' + name)
        require(all(len(row) == len(rows[0]) for row in rows), 'CSV shape: ' + name)
        tables[name] = list(csv.DictReader(io.StringIO(text, newline='')))
findings = tables['FINDINGS.csv']
ids = [f'CC{i:03}' for i in range(1, 49)]
require([r['finding_id'] for r in findings] == ids, '48 ordered unique findings')
for name in ['FINDING_RECONCILIATION.csv', 'IMPLEMENTATION_WORK_ORDER.csv']:
    require({r['finding_id'] for r in tables[name]} == set(ids) and len(tables[name]) == 48, name)
require(all(r['evidence_target_commit'] == target and r['closure'].startswith('NOT_CLOSED') for r in findings), 'target and no closure')
counts = {'TARGET_75_DISPOSITIONS.csv': 75, 'EXECUTION_EVIDENCE_REQUESTS.csv': 70,
          'REQUIREMENTS_MATRIX.csv': 195, 'ITEM_ASSESSMENT_AUDIT.csv': 626,
          'UNIT_COVERAGE.csv': 30, 'CURRICULUM_ENTITY_COVERAGE.csv': 12553,
          'REPOSITORY_FILE_COVERAGE.csv': 8596, 'CURRENT_INPUT_SUPPLEMENT.csv': 19}
for name, count in counts.items():
    require(len(tables[name]) == count, 'row count: ' + name)
require(all(r['execution_status'] == 'NOT_EXECUTED' for r in tables['EXECUTION_EVIDENCE_REQUESTS.csv']), 'original execution status')
coverage = tables['REPOSITORY_FILE_COVERAGE.csv']
expected_paths = set(git('ls-tree', '-r', '--name-only', target).decode().splitlines())
require({r['path'] for r in coverage} == expected_paths and len(coverage) == len(expected_paths), 'target file coverage identities')
scope = json.loads((folder / 'AUDIT_SCOPE_AND_EVIDENCE.json').read_text())
require(scope['outcome'] == 'INCOMPLETE', 'independent verdict')
require(collections.Counter(r['inspection_status'] for r in coverage)['NOT_INSPECTED'] == 8040, 'uninspected count')
require(collections.Counter(r['verdict'] for r in tables['ITEM_ASSESSMENT_AUDIT.csv']) == scope['coverage']['itemVerdicts'], 'item verdict totals')
supplement = tables['CURRENT_INPUT_SUPPLEMENT.csv']
actual = {line.split('\t')[1] for line in git('diff', '--name-status', target, delivery).decode().splitlines()}
require({r['path'] for r in supplement if r['change'] != 'NONE'} == actual and len(actual) == 18, 'delivery supplement')
for row in supplement:
    if row['change'] == 'NONE':
        continue
    raw = git('show', delivery + ':' + row['path'])
    require(hashlib.sha256(raw).hexdigest() == row['sha256'] and len(raw) == int(row['bytes']), 'supplement identity')
print(json.dumps({
    'status': 'MECHANICAL_RECEIPT_VERIFIED_AUDIT_INCOMPLETE',
    'externalCommit': external, 'deliveryCommit': delivery, 'auditTargetCommit': target,
    'requiredRootFiles': 25, 'receivedFiles': len(files), 'payloadHashesVerified': len(manifest),
    'manifestSha256': hashlib.sha256((folder / 'SHA256SUMS.txt').read_bytes()).hexdigest(),
    'allReceivedBytesMatchExternalCommit': True,
    'csvRecordCounts': {n: len(v) for n, v in tables.items() if '/' not in n},
    'findingSeverities': dict(collections.Counter(r['severity'] for r in findings)),
    'newFindingSeverities': dict(collections.Counter(r['severity'] for r in findings[17:])),
    'originalExecutionRequestsStillNotExecuted': 70,
    'returnedScriptsExecuted': False, 'androidCommandsRun': False,
    'pdfPayloadDetected': False, 'credentialPatternHits': 0,
    'privateReferenceConsultedForReceipt': False,
    'semanticAuditComplete': False, 'independentFindingsClosed': 0,
    'physicalAcceptance': 'OUTSTANDING_LAST',
    'limitations': ['Pattern checks are not exhaustive credential or legal classification.',
                    'No claim of exhaustive book-prose comparison or full semantic audit.'],
}, indent=2))
