"""Receive reviewer CSV continuation as data, retaining every original byte."""
from pathlib import Path
import csv, hashlib, io, json, re, subprocess

out=Path(__file__).parent
source=Path('/workspace/attachments/3d791b7a-0fa5-4577-912b-84dd90f31b7e/Pasted text.txt')
repo=Path('/workspace/russian-companion')
target='8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1'
raw=source.read_bytes()
(out/'RECEIVED_MESSAGE_3.txt').write_bytes(raw)
blocks=re.findall(rb'```csv\r?\n(.*?)```',raw,re.S)
assert len(blocks)==1
(out/'CURSOR_DOWNSTREAM_FIXTURE_MAP_ROWS_27_48.csv.fragment').write_bytes(blocks[0])
rows=list(csv.DictReader(io.StringIO(blocks[0].decode())))
expected='test_class,test_methods,helper_locator,loaded_pack_identity,unit14_policy,answering_behavior,advance_locator,scenario_intent,static_observation,evidence_locator,proposed_minimal_action,assertions_to_preserve,runtime_confirmation_needed'.split(',')
assert list(rows[0])==expected and len(rows)==22
assert all(None not in r and all(v is not None for v in r.values()) for r in rows)
assert len({(r['test_class'],r['test_methods']) for r in rows})==22
methods=[]
for row in rows:
    path='app/src/test/java/dev/russiancompanion/v2/'+row['test_class']+'.kt'
    body=subprocess.check_output(['git','-C',str(repo),'show',target+':'+path]).decode()
    for name,line in re.findall(r'([A-Za-z0-9_]+)@(\d+)',row['test_methods']):
        assert re.search(r'\bfun\s+'+re.escape(name)+r'\s*\(',body), (path,name)
        actual=next(i for i,s in enumerate(body.splitlines(),1) if re.search(r'\bfun\s+'+re.escape(name)+r'\s*\(',s))
        methods.append({'class':row['test_class'],'method':name,'claimedLine':int(line),'actualLine':actual})
status={
    'deliveryStatus':'PARTIAL_MESSAGE_3_ONLY',
    'attachment':{'sedimentFileId':'file_00000000238481fd99c69080f4091f23','bytes':len(raw),'sha256':hashlib.sha256(raw).hexdigest()},
    'received':'CSV header and claimed data rows 27-48; Unit21 historical-format/placement and four grouped rows each for Units22-26',
    'parsedRows':len(rows),'columns':len(expected),'allGroupedMethodsExistAtTarget':True,
    'methodLineChecks':methods,
    'missing':['CURSOR_DOWNSTREAM_FIXTURE_REVIEW.md','CSV data rows1-26','CSV data rows49-64'],
    'replacementInstruction':'The received preamble replaces a previously cut-off row. That fragment is not available here. Preserve any future original fragments; exclude superseded incomplete fragments only from assembled CSV.',
    'independentSpotCheck':'Pinned Unit22 prepare() opens Unit14, answers only indices0/1 at line333, then calls advanceFrom14 at334. It does not answer index2. Static observation only; no final runtime cause inferred.',
    'allOtherEngineeringClaims':'Not independently validated by structural receipt checks',
    'repositoryMutation':False,'testExecution':False,
    'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'Outstanding and LAST'
}
(out/'MESSAGE_3_VERIFICATION.json').write_text(json.dumps(status,indent=2)+'\n')
(out/'CURRENT_RECEIPT_STATUS.md').write_text('''# Downstream fixture review: partial receipt

Only Message3 was supplied. It contains the CSV header and 22 complete grouped data rows, claimed rows27–48. All rows have13 fields; all named methods exist at the pinned source. Report and rows1–26/49–64 are missing. No complete map, full coverage or runtime cause is claimed.

The original attachment is byte-preserved. A cut-off fragment referenced by the reviewer was not received; preserve it if it later arrives rather than deleting historical input. Only complete replacement rows should enter assembly.

Unit22 prepare() source was independently spot-checked: its Unit14 answers cover indices0/1 before advancement and omit index2. This is source inspection, not a test execution or final failure diagnosis. All other semantic claims remain subject to review.

Files stay outside the checkout while the broad worker owns completion/publication. All48 findings remain OPEN; auditINCOMPLETE; physicalLAST. No app/test/Git/runtime changes or tests/builds/exporters/privatePDF access/merge/freeze/signing/approval.
''')
manifest={p.name:{'bytes':p.stat().st_size,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()} for p in sorted(out.iterdir()) if p.is_file() and p.name!='SHA256.json'}
(out/'SHA256.json').write_text(json.dumps(manifest,indent=2)+'\n')
print(json.dumps({'status':status['deliveryStatus'],'rows':len(rows),'methodsChecked':len(methods),'lineMismatches':[m for m in methods if m['claimedLine']!=m['actualLine']],'missing':status['missing']},indent=2))
