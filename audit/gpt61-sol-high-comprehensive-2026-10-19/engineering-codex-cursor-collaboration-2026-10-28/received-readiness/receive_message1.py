"""Preserve the later Message 1 without changing the earlier partial receipt."""
from pathlib import Path
from collections import Counter
import csv, hashlib, io, json, re

out = Path(__file__).parent
source = Path('/workspace/attachments/1f2e42af-ce1f-4c6b-ac4e-dd0a701b3dd8/Pasted text.txt')
raw = source.read_bytes()
(out / 'RECEIVED_MESSAGE_1.txt').write_bytes(raw)
blocks = re.findall(rb'```(markdown|csv)\r?\n(.*?)```', raw, re.S)
assert [k for k, v in blocks] == [b'markdown', b'csv']
(out / 'CURSOR_READINESS_REPORT.md').write_bytes(blocks[0][1])
(out / 'CURSOR_FINDING_READINESS_REGISTER.csv').write_bytes(blocks[1][1])
marker = b'## Separate reconciliation addendum for CURSOR-R2-001'
assert raw.count(marker) == 1
addendum = marker + raw.split(marker, 1)[1]
(out / 'CURSOR_R2_001_RECONCILIATION_ADDENDUM.md').write_bytes(addendum)
rows = list(csv.DictReader(io.StringIO(blocks[1][1].decode())))
assert len(rows) == 48 and [r['finding_id'] for r in rows] == [f'CC{i:03d}' for i in range(1,49)]
assert all(None not in r and all(v is not None for v in r.values()) for r in rows)
assert all(r['independent_closure'] == 'NOT_CLOSED' for r in rows)
repo = Path('/workspace/russian-companion')
base = repo / 'audit/gpt61-sol-high-comprehensive-2026-10-19'
original = {r['finding_id']:r for r in csv.DictReader((base / 'final-independent-recheck-results/2026-10-22-claude-code/FINDINGS.csv').open())}
assert all(r['original_severity'] == original[r['finding_id']]['severity'] and r['original_external_status'] == original[r['finding_id']]['status'] for r in rows)
counts = dict(Counter(r['engineering_state'] for r in rows))
scope = json.loads((out / 'CURSOR_READINESS_SCOPE_AND_EVIDENCE.json').read_text())
assert all(counts.get(k, 0) == v for k,v in scope['findingStateCounts'].items())
previous = json.loads((out / 'SHA256.json').read_text())
assert all(hashlib.sha256((out/name).read_bytes()).hexdigest() == identity['sha256'] for name,identity in previous.items())
v = {
    'deliveryStatus': 'REPORT_FINDING_REGISTER_ADDENDUM_AND_REQUESTS_59_70_RECEIVED; REQUEST_HEADER_AND_1_58_MISSING',
    'attachment': {'sedimentFileId': 'file_0000000030b081f8822d1c824d872d10', 'bytes': len(raw), 'sha256': hashlib.sha256(raw).hexdigest()},
    'findingRows': len(rows), 'uniqueOrderedIds': 'CC001-CC048',
    'allOriginalSeveritiesAndStatusesMatch': True,
    'allIndependentClosuresNotClosed': True,
    'findingStateCountsMatchReceivedJson': counts,
    'previousReceiptBytesConserved': True,
    'receivedRequestRows': '59-70', 'missingRequestRows': 'header and 1-58',
    'engineeringDispositionsIndependentlyAccepted': False,
    'addendumDisposition': 'Acknowledges retained assignment schemas 7-15. Further distinction needed between ZIP payload evidence, separate runtime import/replacement/reopen evidence, and current-input equivalence. Absence of logs inside a learner ZIP does not establish absence of retained runtime proof.',
    'nextBoundedReview': ['Receive execution-map header and rows 1-58', 'Reconcile reported verification gaps against actual retained engineering reports, raw runtime identities and scenario bodies without closing findings', 'Resolve correction_commits descriptions into exact SHAs in a separate derived addendum'],
    'repositoryMutation': False, 'testExecution': False,
    'all48Findings': 'OPEN', 'audit': 'INCOMPLETE', 'physicalAcceptance': 'Outstanding and LAST'
}
(out / 'MESSAGE_1_VERIFICATION.json').write_text(json.dumps(v,indent=2)+'\n')
(out / 'CURRENT_RECEIPT_STATUS.md').write_text('''# Current receipt status

Received: complete readiness report, 48-row finding register, separate CURSOR-R2-001 addendum, execution-request rows 59–70, complete scope JSON. Still missing: execution-map header and rows 1–58. The twelve received request rows and all 48 finding rows parse; original identities, severities/statuses and closure limits match. Finding-state counts match the JSON. Engineering dispositions remain provisional until checked against actual retained evidence.

All original receipt files and supplied returns are preserved unchanged. The new addendum corrects the missing-history citation for schemas 7–15. Absence of logs inside a learner archive must not be confused with absence of separate runtime import/replacement/reopen evidence. No comprehensive scenario or schema coverage is inferred.

Pending integration outside the checkout while the broad worker remains active. No app, test, Git state or runtime input changes. All 48 findings remain OPEN; audit INCOMPLETE; physical acceptance LAST.
''')
manifest = {p.name:{'bytes':p.stat().st_size,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()} for p in sorted(out.iterdir()) if p.is_file() and p.name != 'MESSAGE_1_SHA256.json'}
(out / 'MESSAGE_1_SHA256.json').write_text(json.dumps(manifest,indent=2)+'\n')
print(json.dumps(v,indent=2))
