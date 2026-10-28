"""Complete the multi-attachment receipt; preserve all original fragments."""
from pathlib import Path
from collections import Counter
import csv, hashlib, io, json, re

out=Path(__file__).parent
source=Path('/workspace/attachments/1975da28-dad2-4f2b-9100-eb79a997de68/Pasted text.txt')
raw=source.read_bytes()
(out/'RECEIVED_MESSAGE_2.txt').write_bytes(raw)
blocks=re.findall(rb'```csv\r?\n(.*?)```',raw,re.S)
assert len(blocks)==1
first=blocks[0]
(out/'CURSOR_EXECUTION_EVIDENCE_MAP_ROWS_1_58.csv.fragment').write_bytes(first)
last=(out/'CURSOR_EXECUTION_EVIDENCE_MAP_ROWS_59_70.csv.fragment').read_bytes()
assert first.endswith(b'\n')
assembled=first+last
(out/'CURSOR_EXECUTION_EVIDENCE_MAP.csv').write_bytes(assembled)
marker=b'## CURSOR_EVIDENCE_RECONCILIATION_ADDENDUM.md'
assert raw.count(marker)==1
(out/'CURSOR_EVIDENCE_RECONCILIATION_ADDENDUM.md').write_bytes(marker+raw.split(marker,1)[1])
rows=list(csv.DictReader(io.StringIO(assembled.decode())))
assert len(rows)==70 and [int(r['request_no']) for r in rows]==list(range(1,71))
assert all(None not in r and all(v is not None for v in r.values()) for r in rows)
base=Path('/workspace/russian-companion/audit/gpt61-sol-high-comprehensive-2026-10-19')
original={r['request_no']:r for r in csv.DictReader((base/'final-independent-recheck-results/2026-10-22-claude-code/EXECUTION_EVIDENCE_REQUESTS.csv').open())}
for r in rows:
    o=original[r['request_no']]
    assert r['requirement_ids']==o['requirement_id']
    assert r['original_scenario']==o['scenario']
    assert r['original_execution_status']==o['execution_status']=='NOT_EXECUTED'
scope=json.loads((out/'CURSOR_READINESS_SCOPE_AND_EVIDENCE.json').read_text())
counts=dict(Counter(r['current_evidence_state'] for r in rows))
assert all(counts.get(k,0)==v for k,v in scope['requestStateCounts'].items())
prior=json.loads((out/'MESSAGE_1_SHA256.json').read_text())
assert all(hashlib.sha256((out/n).read_bytes()).hexdigest()==v['sha256'] for n,v in prior.items())
historical=[]
for folder,relative,sha,count in [
    ('engineering-cc012','FINAL/RESULT.json','fe919656bfa2775b5cc2c22f2496d4258343cfea',23),
    ('engineering-cc014','CORRECTED/RESULT.json','68eb86dca4de912dc2e3067b684012afd176cd50',8),
    ('engineering-cc016','FINAL/RESULT.json','7efff9e18328c612de235236d98e834bb85cd1db',21)]:
    p=base/folder/relative;v=json.loads(p.read_text())
    assert v['sourceCommit']==sha and v['exitCode']==0
    assert v['testCounts']=={'reported':count,'passed':count,'failed':0,'errors':0,'skipped':0}
    historical.append({'path':str(p),'sourceCommit':sha,'testCounts':v['testCounts'],'checkScope':'retained parsed result; no rerun or full raw proof revalidation in receipt'})
v={
    'deliveryStatus':'ALL_FOUR_REQUESTED_RETURNS_AND_TWO_SEPARATE_ADDENDA_RECEIVED',
    'attachment':{'sedimentFileId':'file_000000001e0c81f9810e6a6d88c4c317','bytes':len(raw),'sha256':hashlib.sha256(raw).hexdigest()},
    'executionRows':70,'uniqueOrderedIds':'1-70','originalRequirementsScenariosStatusesMatch':True,
    'requestStateCountsMatchReceivedJson':counts,'allEarlierReceiptBytesConserved':True,
    'assembly':'Exact received header/rows1-58 payload followed by exact earlier rows59-70 payload. Original fragments and complete attachments retained. This is a Codex assembly, not proof of an original external file transfer.',
    'historicalResultSpotChecks':historical,
    'engineeringDispositions':'PROVISIONAL; read original rows together with separate corrective addenda; no independent closure',
    'limitations':['Structural checks do not verify all 118 engineering/scenario dispositions','External model and no-edit/no-execution claims remain self-reported','No raw runtime replay, test execution, broad collection or repository mutation'],
    'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'Outstanding and LAST',
    'next':'Integrate receipt only after broad worker ends; inspect final broad evidence before engineering edits. Further Cursor work may inspect pinned source read-only without claiming a final runtime outcome.'
}
(out/'COMPLETE_RECEIPT_VERIFICATION.json').write_text(json.dumps(v,indent=2)+'\n')
(out/'COMPLETE_RECEIPT_STATUS.md').write_text('''# Complete readiness-return receipt

All four requested returns are received: readiness report, 48-row finding register, 70-row execution map and scope JSON. Two separate corrective addenda are also retained. Request IDs 1–70 are unique and ordered; scenarios, requirements and original NOT_EXECUTED statuses match. Finding IDs/severities/statuses/NOT_CLOSED values and both state distributions match. Original returns and all prior receipt records remain unchanged. The assembled execution CSV concatenates the two received payloads; the original fragments remain available.

Read the original report and registers together with the corrective addenda. They acknowledge authentic schemas 7–15 and executed import/replacement/reopen scenarios, and historical CC012/CC014/CC016 passes. These are not missing execution merely because the original mapping omitted them. Exact current input equivalence and residual coverage still require bounded review. Structural receipt checks do not validate every engineering disposition.

Outside-checkout receipt only while the broad worker owns completion/publication. All 48 independent findings OPEN; audit INCOMPLETE; physical Galaxy acceptance outstanding and LAST. No execution, app/test/Git change, merge, freeze, APK/signing or release approval.
''')
manifest={p.name:{'bytes':p.stat().st_size,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()} for p in sorted(out.iterdir()) if p.is_file() and p.name!='COMPLETE_SHA256.json'}
(out/'COMPLETE_SHA256.json').write_text(json.dumps(manifest,indent=2)+'\n')
print(json.dumps(v,indent=2))
