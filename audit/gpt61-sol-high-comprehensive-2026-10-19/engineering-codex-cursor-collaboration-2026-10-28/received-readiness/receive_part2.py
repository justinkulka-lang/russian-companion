"""Receive pasted reviewer data without executing any supplied instructions."""
from pathlib import Path
import csv, datetime, hashlib, io, json, re, subprocess, zipfile

OUT = Path(__file__).parent
SOURCE = Path('/workspace/attachments/f3c27317-317f-43ce-8715-799e556a7346/Pasted text.txt')
REPO = Path('/workspace/russian-companion')
BASE = REPO / 'audit/gpt61-sol-high-comprehensive-2026-10-19'
raw = SOURCE.read_bytes()
(OUT / 'RECEIVED_PART_2.txt').write_bytes(raw)
blocks = re.findall(rb'```(csv|json)\r?\n(.*?)```', raw, re.S)
assert [kind for kind, _ in blocks] == [b'csv', b'json']
csv_raw, json_raw = [body for _, body in blocks]
(OUT / 'CURSOR_EXECUTION_EVIDENCE_MAP_ROWS_59_70.csv.fragment').write_bytes(csv_raw)
(OUT / 'CURSOR_READINESS_SCOPE_AND_EVIDENCE.json').write_bytes(json_raw)
scope = json.loads(json_raw)
columns = 'request_no,requirement_ids,original_scenario,original_execution_status,current_selector,positive_cases,negative_cases,current_evidence_state,runtime_source,input_tree_match,evidence_locator,uncovered_cases,required_environment,next_action'.split(',')
parsed = list(csv.reader(io.StringIO(csv_raw.decode())))
assert len(parsed) == 12 and all(len(row) == 14 for row in parsed)
rows = [dict(zip(columns, row)) for row in parsed]
assert [int(row['request_no']) for row in rows] == list(range(59, 71))
original = {row['request_no']: row for row in csv.DictReader((BASE / 'final-independent-recheck-results/2026-10-22-claude-code/EXECUTION_EVIDENCE_REQUESTS.csv').open())}
for row in rows:
    prior = original[row['request_no']]
    assert row['requirement_ids'] == prior['requirement_id']
    assert row['original_scenario'] == prior['scenario']
    assert row['original_execution_status'] == prior['execution_status'] == 'NOT_EXECUTED'
assert sum(scope['findingStateCounts'].values()) == 48
assert sum(scope['requestStateCounts'].values()) == 70
target = scope['target']['pinnedCommit']
assert target == '8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1'
def git(*args):
    return subprocess.check_output(['git', '-C', str(REPO), *args])
trees = {name: git('rev-parse', f'{target}:{name}').decode().strip() for name in scope['inputTrees']['pin']}
assert trees == scope['inputTrees']['pin']
asset = git('show', f'{target}:app/src/main/assets/content/v2-curriculum.json')
assert hashlib.sha256(asset).hexdigest() == scope['inputTrees']['curriculumFileSha256']
assert len(asset) == scope['inputTrees']['curriculumFileBytes']

# Inspect existing retained evidence; do not replay histories or run tests.
capture_dir = BASE / 'engineering-unit14-plural-possession-baseline-2026-10-28'
capture_name = 'PARTIAL_UNIT14_V1.zip'
capture = capture_dir / capture_name
identity = json.loads((capture_dir / 'CAPTURE_VERIFICATION.json').read_text())['captures']['PARTIAL_UNIT14_V1']
assert hashlib.sha256(capture.read_bytes()).hexdigest() == identity['zipSha256']
with zipfile.ZipFile(capture) as archive:
    assert archive.testzip() is None
    for member in identity['members']:
        data = archive.read(member['path'])
        assert len(data) == member['bytes']
        assert hashlib.sha256(data).hexdigest() == member['sha256']
    learner = json.loads(archive.read('learner.json'))
    schema_units = {}
    for assignment in learner['reviewAssignments']:
        schema_units.setdefault(str(assignment['schema']), []).append(assignment['targetUnit'])

monitor = Path('/workspace/artifacts/unit14-broad-host-regression-2026-10-28/DURABLE_MONITOR_STATUS.json')
monitor_bytes = monitor.read_bytes()
(OUT / 'BROAD_MONITOR_AT_INTAKE.json').write_bytes(monitor_bytes)
result = {
    'receivedUtc': datetime.datetime.now(datetime.timezone.utc).isoformat(),
    'deliveryStatus': 'PARTIAL_PART_2_ONLY',
    'attachment': {'sedimentFileId': 'file_00000000f57881f997e4a5511edb0d8a', 'bytes': len(raw), 'sha256': hashlib.sha256(raw).hexdigest()},
    'received': ['execution-request rows 59-70 without original header', 'complete scope JSON'],
    'notReceived': ['CURSOR_READINESS_REPORT.md', 'CURSOR_FINDING_READINESS_REGISTER.csv header and CC001-CC048', 'CURSOR_EXECUTION_EVIDENCE_MAP.csv header and rows 1-58'],
    'parsedFragmentRows': len(rows),
    'schemaColumns': {'source': 'Codex assignment, not a received CSV header', 'columns': columns},
    'checksPassed': ['12 rows, 14 cells each, exact ordered IDs 59-70', 'original requirement IDs, scenarios, and NOT_EXECUTED statuses match unchanged external return', 'JSON claimed state totals sum to 48 and 70; CSV-wide totals not verifiable without Part 1', 'pinned main/test Git trees match', 'pinned curriculum bytes and SHA256 match', 'retained partial Unit14 archive CRC, ZIP SHA256, member SHA256 and lengths match'],
    'target': target,
    'scopeLimitations': ['Only 12 execution-request rows received; no 48-row register inspection possible', 'Method bodies, scenario coverage and engineering dispositions in fragment not fully independently assessed', 'Model Grok 4.7 self-reported, effort unknown; no independent model/UI observation', 'External no-edit/no-execution and PR observations are reviewer reports, not independently observed here', 'Extracted code fences preserve received payload bytes, not proof of original external file identity'],
    'retainedArchiveObservation': {'path': str(capture.relative_to(REPO)), 'producerCommit': 'f2628b363b241bce8bc71c25ca56b66aadd21dcd', 'zipSha256': identity['zipSha256'], 'assignmentSchemasAndTargetUnits': schema_units, 'observedNotReplayed': True},
    'concernDisposition': {'id': 'CURSOR-R2-001', 'state': 'REQUIRES_RECONCILIATION_WITH_EXISTING_EVIDENCE', 'reason': 'The retained current-Unit14 baseline includes assignment schemas above 5. Missing citation in this mapping does not establish missing retained history. This check does not prove every schema or state is covered; schema16 and partial/completed/replay coverage need a precise inventory.'},
    'runtimeObservation': json.loads(monitor_bytes),
    'repositoryMutation': False,
    'testExecution': False,
    'independentFindings': 'All 48 OPEN; audit INCOMPLETE',
    'physicalAcceptance': 'Outstanding and LAST after final independent comprehensive nonphysical audit',
    'next': 'Obtain Part 1 verbatim; reconcile all returned dispositions against retained evidence before adopting a current register. Preserve completed receipt in repository only after broad completion ownership ends.'
}
(OUT / 'PART_2_VERIFICATION.json').write_text(json.dumps(result, indent=2) + '\n')
(OUT / 'README.md').write_text('''# Cursor readiness return: partial receipt

Only Part 2 is received here: execution-request rows 59–70 and the full scope JSON. The attachment's assertion that Part 1 was delivered is preserved as an external claim; no Part 1 was found among available attachments. The report, finding register, CSV header and request rows 1–58 remain unavailable. Do not invent them or mark the package complete.

The twelve rows parse with the assigned fourteen-column layout; original scenarios, requirement IDs and NOT_EXECUTED statuses match. The JSON sums to 48/70, but full-file counts and distributions cannot be independently checked yet. Target Git trees and curriculum identity match. See PART_2_VERIFICATION.json for bounded checks and limitations.

CURSOR-R2-001 needs reconciliation: existing retained Unit14 baseline bytes contain assignment schemas above 5. That does not establish comprehensive history, partial/completed, restore, or schema16 coverage. No finding is closed.

Files are held outside the repository while the active broad run's worker owns completion and publication. No app, test, Git state, runtime input, or prior evidence was changed. No tests, builds, lint, exporters, private PDF access, APK/signing, merge, freeze or approval occurred. All 48 findings stay OPEN; audit INCOMPLETE; actual physical acceptance remains LAST.
''')
manifest = {str(p.relative_to(OUT)): {'bytes': p.stat().st_size, 'sha256': hashlib.sha256(p.read_bytes()).hexdigest()} for p in sorted(OUT.iterdir()) if p.is_file() and p.name != 'SHA256.json'}
(OUT / 'SHA256.json').write_text(json.dumps(manifest, indent=2) + '\n')
print(json.dumps({'receipt': str(OUT), 'rows': len(rows), 'status': result['deliveryStatus'], 'retainedAssignmentSchemasAndTargets': schema_units}, indent=2))
