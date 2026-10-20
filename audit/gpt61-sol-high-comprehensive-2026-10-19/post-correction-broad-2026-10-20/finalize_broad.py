"""Publish verification evidence after the already-running Gradle job completes.

Does not change runtime, test, source, review, release or approval inputs.
Fails closed on incomplete identities, changed inputs, dirty Git or remote drift.
"""
from pathlib import Path
import csv, hashlib, json, shutil, subprocess, time, zipfile
from datetime import datetime, timezone

ROOT = Path('/workspace/russian-companion')
HERE = Path('/workspace/artifacts/post-correction-broad-2026-10-20')
RUN = HERE/'CURRENT_REGRESSION'
EVIDENCE = ROOT/'audit/gpt61-sol-high-comprehensive-2026-10-19/post-correction-broad-2026-10-20'
BRANCH = 'cursor/content-audit-corrections-964f'
def command(*args):
    return subprocess.check_output(list(args), cwd=ROOT, text=True).strip()
def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()
def save(path, data):
    path.write_text(json.dumps(data, indent=2)+'\n')

deadline = time.monotonic()+6*60*60
while not (RUN/'MANIFEST.json').exists():
    if time.monotonic() > deadline:
        raise RuntimeError('No final manifest within6h; preserve run and investigate, no publication')
    time.sleep(15)

manifest = json.loads((RUN/'MANIFEST.json').read_text())
context = manifest['sourceContext']
assert (RUN/'RUN.exit').exists()
assert int((RUN/'RUN.exit').read_text()) == manifest['exitCode']
assert not context['syntheticDryRun']
assert manifest['allDiscoveredDebugHostMethodsReported']
assert manifest['eventOutcomeCountsMatchXml']
assert manifest['eventComparison']['sameNamesAsXml']
assert manifest['exporterExclusionsObserved']
assert not manifest['discoveryComparison']['discoveredNotReported']
assert not manifest['discoveryComparison']['reportedNotDiscovered']
assert manifest['testCounts']['reported']==1320 and manifest['classes']==218
assert command('git','branch','--show-current')==BRANCH
assert not command('git','status','--porcelain'), 'Unexpected changes: preserve them and investigate'
for path,tree in context['inputTrees'].items():
    assert command('git','rev-parse','HEAD:'+path)==tree, path
local_head=command('git','rev-parse','HEAD')
remote=command('git','ls-remote','origin','refs/heads/'+BRANCH).split()[0]
assert remote==local_head, 'Remote drift; no force or automatic reconciliation'
pr=json.loads(command('gh','api','repos/justinkulka-lang/russian-companion/pulls/64',
    '--jq','{state,draft,sha:.head.sha,ref:.head.ref}'))
assert pr['state']=='open' and pr['draft'] and pr['sha']==local_head and pr['ref']==BRANCH
for rel,expected in json.loads((RUN/'SHA256.json').read_text()).items():
    assert sha(RUN/rel)==expected, rel

out=EVIDENCE/'COMPLETED'
out.mkdir(exist_ok=False)
for name in ['CONTEXT.json','MANIFEST.json','RUN.exit','LINT.xml']:
    shutil.copy2(RUN/name,out/name)
for name in ['HISTORICAL_SCOPE_RECONCILIATION.json','SOURCE_GUARD_DIFF_REVIEW.json']:
    shutil.copy2(HERE/name,out/name)
raw_files=[RUN/'RUN.log',RUN/'EVENTS.jsonl',*sorted((RUN/'XML').glob('TEST-*.xml'))]
raw_index={str(p.relative_to(RUN)):sha(p) for p in raw_files}
archive=out/'RAW_EXECUTION_EVIDENCE.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p in raw_files:z.write(p,str(p.relative_to(RUN)))
with zipfile.ZipFile(archive) as z:
    assert z.testzip() is None
    assert set(z.namelist())==set(raw_index)
    for name,digest in raw_index.items():
        assert hashlib.sha256(z.read(name)).hexdigest()==digest,name
assert archive.stat().st_size<80*1024*1024, 'Preserve archive externally; review large delivery before publication'
save(out/'RAW_EVIDENCE_INDEX.json',dict(archiveSha256=sha(archive),archiveBytes=archive.stat().st_size,
    members=raw_index,crcAndEveryMemberHashVerified=True))
failures=[t for t in manifest['tests'] if t['outcome'] in ['FAILED','ERROR']]
with (out/'FAILURE_DISPOSITIONS.csv').open('w',newline='') as f:
    w=csv.writer(f);w.writerow(['class','method','outcome','message','engineering_disposition'])
    for t in failures:w.writerow([t['class'],t['name'],t['outcome'],t['message'],'OPEN_REQUIRES_CAUSE_REVIEW_NO_ASSERTION_WAIVER'])
allow=json.loads((ROOT/'audit/independent-gpt61-2026-10-12/current-comprehensive-audit/RETAINED_ARCHIVE_ALLOWLIST.json').read_text())['sha256']
assert len(allow)==66 and all(sha(ROOT/p)==h for p,h in allow.items())
save(out/'CONSERVATION.json',dict(testedSourceCommit=context['sourceCommit'],inputTrees=context['inputTrees'],
    all66ArchiveHashesMatched=allow,appTestsCatalogDocsReviewChanged=False,releaseInputsChanged=False,
    apkAssemblyInstallSigning=False,privateReferenceInspected=False))
c=manifest['testCounts'];source=context['sourceCommit']
summary=(f"Fresh broad debug-host regression at `{source}`: {c['reported']}reported/"
         f"{c['passed']}passed/{c['failed']}failed/{c['errors']}errors/{c['skipped']}skipped,218classes. "
         'Every1320discovered method has a fresh XML/event result;1336source annotations minus16explicit screenshot exporters. '
         'The complete selected debug-host scope is reported;this is not an all-pass result or a full app/curriculum/physical audit. '
         'All failures remain OPEN pending cause review. No app/test/catalog/docs/review/release input changed. '
         'Read post-correction-broad-2026-10-20/COMPLETED/ENGINEERING_CHANGE_REPORT.md under audit/gpt61-sol-high-comprehensive-2026-10-19/. '
         'Physical acceptance LAST after final comprehensive independent nonphysical audit;no merge/freeze/APK/signing/approval.')
report=f'''# Accumulated-correction broad host regression

{summary}

Exact source/root/input identities,commands,activation/init hashes,runner hashes and task statuses are in CONTEXT.json and MANIFEST.json. RAW_EXECUTION_EVIDENCE.zip preserves all218fresh XML files,events and stdout unchanged;CRC and every member hash verified in RAW_EVIDENCE_INDEX.json. The original discovery and partial running snapshot remain immutable. The underlying Gradle exit was {manifest['exitCode']}. No failure was converted into an expected pass or waived. FAILURE_DISPOSITIONS.csv retains every failed/error identity and message for subsequent engineering review.

Lint: {json.dumps(manifest['lint'],sort_keys=True)}. Task outcomes: {json.dumps(manifest['taskStatuses'],sort_keys=True)}. Consult RUN.log inside the archive for cached versus executed compilation/analysis. Gate attachment verification does not approve release. Discovery skips are synthetic and are not added to actual results. All overlap with earlier focused scopes is kept separate.

Historical broad source6d91a7de had1225reported/1208passed/16failed/0errors/1skip across193classes. Current discovery contains every original1225method plus95new methods,none removed;see HISTORICAL_SCOPE_RECONCILIATION.json. Historical mainff539220's562passes/1skip and420f41ff's691source-annotation/handoff claim are different commits/scopes,not this result. Handoff execution of691 remains unverified.

Four screenshot exporters remain excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. NoWRITE_* regeneration. All64authentic lifecycle history archives and two original raw-result archives remain hash-exact. Original historical audit/fourCSVs remain unrecovered;oldUnit7restore causeUNCONFIRMED. No private PDF/book material was inspected or packaged.

Configured app dev.russiancompanion/2.0.0-dev/versionCode5. No APK assembly/install/signing;new APK identity/hash/signature NOT_RECORDED. Existing pending candidate/source records were not refreshed during execution. Environment install_script/start_skill previously saved unchanged;publication user-confirmed independently unverified. All source/rights/linguistic/reference qualification debts and independent changed-source recheck remain outstanding. AuditINCOMPLETE;actual GalaxyS26Ultra airplane-mode/reboot acceptanceLAST after final comprehensive independent nonphysical audit/recheck. No merge,freeze,formal stage advancement,owner acceptance or release approval.

Next:review each exact failure against source,authority and original assertions. SOURCE_GUARD_DIFF_REVIEW.json records two changed whole-file hashes with unchanged native-bearing lines;this permits a narrow review,not blanket source admission. Investigate candidate/source binding and changed-content fixture assumptions separately. Preserve raw failures,old rules and accepted history before any correction or focused rerun.
'''
(out/'ENGINEERING_CHANGE_REPORT.md').write_text(report)
save(out/'SHA256.json',{str(p.relative_to(out)):sha(p) for p in sorted(out.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
for relative in ['audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=ROOT/relative;p.write_text(p.read_text()+'\n\n## Fresh broad host regression completed — '+datetime.now(timezone.utc).strftime('%Y-%m-%d')+'\n\n'+summary+'\n')
p=ROOT/'AGENTS.md';s=p.read_text();p.write_text(s.replace('# Codex continuation\n',
    '# Codex continuation\n\nCurrent engineering checkpoint: [fresh broad host regression completed](audit/gpt61-sol-high-comprehensive-2026-10-19/post-correction-broad-2026-10-20/COMPLETED/ENGINEERING_CHANGE_REPORT.md). '+summary+'\n',1))
paths=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md',str(out.relative_to(ROOT))]
subprocess.run(['git','add','--',*paths],cwd=ROOT,check=True)
subprocess.run(['git','diff','--cached','--check'],cwd=ROOT,check=True)
subprocess.run(['git','commit','-m','Retain completed 1320-method broad host regression and all unresolved failures'],cwd=ROOT,check=True)
subprocess.run(['git','push','origin','HEAD'],cwd=ROOT,check=True)
body=(HERE/'PR64_BODY_RUNNING.md').read_text()
start=body.index('Current execution: **fresh broad host regression IN_PROGRESS**')
end=body.index('Latest engineering checkpoint: **CC002',start)
body=body[:start]+summary+' [Completed report](audit/gpt61-sol-high-comprehensive-2026-10-19/post-correction-broad-2026-10-20/COMPLETED/ENGINEERING_CHANGE_REPORT.md).\n\n'+body[end:]
body_file=HERE/'PR64_BODY_COMPLETED.md';body_file.write_text(body)
subprocess.run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(body_file),'--jq','{state,draft,sha:.head.sha}'],cwd=ROOT,check=True)
save(HERE/'PUBLICATION_COMPLETE.json',dict(commit=command('git','rev-parse','HEAD'),testCounts=c,
    sourceCommit=source,repositoryEvidence=str(out.relative_to(ROOT)),pr=64,draft=True))
print(json.dumps(dict(state='EVIDENCE_SAVED_PUSHED_PR_UPDATED',counts=c)),flush=True)
