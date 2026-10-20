"""Publish the separate missing/interrupted scope after validated collection.
No app, test, review, release, approval or legacy-history changes.
"""
from pathlib import Path
import hashlib,json,subprocess,time,zipfile,shutil
ROOT=Path('/workspace/russian-companion')
BASE=Path('/workspace/artifacts/post-correction-broad-2026-10-20')
RUN=BASE/'continuation/MISSING_AND_INTERRUPTED'
EVIDENCE=ROOT/'audit/gpt61-sol-high-comprehensive-2026-10-19/post-correction-broad-2026-10-20'
def cmd(*args):return subprocess.check_output(list(args),cwd=ROOT,text=True).strip()
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def save(p,x):p.write_text(json.dumps(x,indent=2)+'\n')
deadline=time.monotonic()+6*3600
while not (RUN/'LAUNCHER_RESULT.json').exists():
    if time.monotonic()>deadline:raise RuntimeError('No collected continuation within6h; no publication')
    time.sleep(15)
launch=json.loads((RUN/'LAUNCHER_RESULT.json').read_text())
assert launch['collectorExit']==0
result=json.loads((RUN/'RESULT.json').read_text());context=json.loads((RUN/'CONTEXT.json').read_text())
assert result['exitCode']==launch['gradleExit']==int((RUN/'RUN.exit').read_text())
assert result['selectedSourceInventoryMatchesFreshXmlAndEvents'] and result['xmlFreshRelativeToRunScript']
assert result['testCounts']['reported']==248 and context['expectedMethods']==248
assert cmd('git','branch','--show-current')=='cursor/content-audit-corrections-964f'
assert not cmd('git','status','--porcelain'),'Unexpected changes; no automatic overwrite'
for p,h in context['inputTrees'].items():assert cmd('git','rev-parse','HEAD:'+p)==h,p
head=cmd('git','rev-parse','HEAD')
assert cmd('git','ls-remote','origin','refs/heads/cursor/content-audit-corrections-964f').split()[0]==head
pr=json.loads(cmd('gh','api','repos/justinkulka-lang/russian-companion/pulls/64?continuation='+str(int(time.time())),'--jq','{state,draft,sha:.head.sha}'))
assert pr['state']=='open' and pr['draft'] and pr['sha']==head
stopped=json.loads((BASE/'STOPPED_RECOVERY/PARTIAL_BINARY_RECORDS.json').read_text())
original=json.loads((BASE/'CURRENT_REGRESSION/CONTEXT.json').read_text())
assert original['inputTrees']==context['inputTrees']
a={(x['class'],x['name']):x for x in stopped['methods']}
b={(x['testClass'],x['method']):x for x in result['tests']}
assert len(a)==1074 and len(b)==248
assert len(a.keys()&b.keys())==2 and all(a[k]['resultType']=='SKIPPED' for k in a.keys()&b.keys())
discovery=json.loads((BASE/'CURRENT_DISCOVERY/MANIFEST.json').read_text())
expected={(x['class'],x['name']) for x in discovery['tests']}
assert a.keys()|b.keys()==expected
out=EVIDENCE/'CONTINUATION_COMPLETE';out.mkdir(exist_ok=False)
for name in ['RESULT.json','CONTEXT.json','RUN.exit','LINT.xml','LAUNCHER_RESULT.json']:shutil.copy2(RUN/name,out/name)
files=[p for p in sorted(RUN.rglob('*')) if p.is_file()]
index={str(p.relative_to(RUN)):sha(p) for p in files}
archive=out/'RAW_CONTINUATION_EVIDENCE.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p in files:z.write(p,str(p.relative_to(RUN)))
with zipfile.ZipFile(archive) as z:
    assert z.testzip() is None and set(z.namelist())==set(index)
    for name,h in index.items():assert hashlib.sha256(z.read(name)).hexdigest()==h
assert archive.stat().st_size<80*1024*1024
save(out/'RAW_EVIDENCE_INDEX.json',dict(archiveSha256=sha(archive),archiveBytes=archive.stat().st_size,members=index,crcAndAllMemberHashesVerified=True))
ledger=[dict(testClass=c,method=m,stoppedResult=a[(c,m)]['resultType'] if (c,m) in a else None,
    continuationResult=b[(c,m)]['outcome'] if (c,m) in b else None) for c,m in sorted(expected)]
save(out/'CROSS_SCOPE_IDENTITY_LEDGER.json',dict(identities=1320,stoppedMethods=1074,continuationMethods=248,
    overlappingInterruptedSkips=2,inputTreesMatch=True,uninterruptedFullRun=False,completeSuite=False,
    originalFailuresStillOpen=33,rows=ledger))
c=result['testCounts']
summary=(f"Separate continuation at `{context['sourceCommit']}`:248reported/{c['passed']}passed/{c['failed']}failed/"
    f"{c['errors']}errors/{c['skipped']}skipped;fresh selected XML/events reconcile exactly. "
    'Original stopped broad:1074partial binary/event records,1038SUCCESS/33FAILURE/3SKIPPED,no finalXML/exit. '
    'Two interrupted skips overlap explicitly;all1320discovered identities have per-scope records at identical input trees. '
    'This is not an uninterrupted complete-suite result or full app/curriculum acceptance. Original33failures and all new failures remainOPEN.')
report='# Missing/interrupted host scope completed\n\n'+summary+'\n\n'
report+='Exact commands,cwd,activation,source/root/input/init/test hashes are in CONTEXT.json. RESULT.json records underlying exit,counts,method outcomes,task execution/cache status and lint. Raw XML,events,stdout and workerGC logs are preserved unchanged in RAW_CONTINUATION_EVIDENCE.zip,with CRC/every-member SHA256 verification. The original stopped evidence and all earlier snapshots remain immutable. CROSS_SCOPE_IDENTITY_LEDGER.json records both outcomes for the two interrupted overlaps;no counts are silently added or old failures overwritten.\n\n'
report+='Lint/task observations: '+json.dumps(dict(lint=result['lintIssuesBySeverity'],tasks=result['tasks']))+'. Gate attachment is not release approval. Two6GiBworkers/recycle4 retain12GiBtotalheap;GC logs provide measured evidence without asserting the original termination cause.\n\n'
report+='All four screenshot exporters16methods remain excluded;noWRITE_* regeneration. No app,test,catalog,docs,review,release,policy,schema,accepted history or approval changed. Configured dev.russiancompanion/2.0.0-dev/code5;noAPK assembly/install/signing,new artifact hash/signature NOT_RECORDED. Environment install/start previously saved unchanged;publication user-confirmed independently unverified. Qualified source/rights/linguistic/reference debts remainOPEN;oldUnit7restore causeUNCONFIRMED and historical audit/fourCSVs missing. AuditINCOMPLETE;physicalGalaxyS26Ultraairplane-mode/reboot acceptanceLAST after final comprehensive independent nonphysical audit. No merge,freeze,formal stage advancement,owner acceptance or release approval.\n\n'
report+='Next:review the original33failures plus any continuation failures against exact authority,source and current-versus-historical fixture scope. Current source-binding candidates forUnits4/7/8/15 are static comparisons only;do not refresh all hashes,overwrite historical packets,fill reviewer fields or relax guards. Apply justified bounded fixes only now that execution has ended,and preserve every raw failure before focused regression.\n'
(out/'ENGINEERING_CHANGE_REPORT.md').write_text(report)
save(out/'SHA256.json',{str(p.relative_to(out)):sha(p) for p in sorted(out.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
for relative in ['audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=ROOT/relative;p.write_text(p.read_text()+'\n\n## Missing/interrupted continuation completed\n\n'+summary+' Read post-correction-broad-2026-10-20/CONTINUATION_COMPLETE/ENGINEERING_CHANGE_REPORT.md under audit/gpt61-sol-high-comprehensive-2026-10-19/. No acceptance;physicalLAST.\n')
p=ROOT/'AGENTS.md';s=p.read_text();p.write_text(s.replace('# Codex continuation\n','# Codex continuation\n\nCurrent execution checkpoint: [missing/interrupted scope completed](audit/gpt61-sol-high-comprehensive-2026-10-19/post-correction-broad-2026-10-20/CONTINUATION_COMPLETE/ENGINEERING_CHANGE_REPORT.md). '+summary+'\n',1))
subprocess.run(['git','add','--','AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md',str(out.relative_to(ROOT))],cwd=ROOT,check=True)
subprocess.run(['git','diff','--cached','--check'],cwd=ROOT,check=True)
subprocess.run(['git','commit','-qm','Retain separate 248-method continuation and exact cross-scope identity ledger'],cwd=ROOT,check=True)
subprocess.run(['git','push','origin','HEAD'],cwd=ROOT,check=True)
body=(BASE/'PR64_BODY_RUNNING.md').read_text();start=body.index('Current execution:');end=body.index('Latest engineering checkpoint: **CC002',start)
body=body[:start]+summary+' [Report/raw evidence](audit/gpt61-sol-high-comprehensive-2026-10-19/post-correction-broad-2026-10-20/CONTINUATION_COMPLETE/ENGINEERING_CHANGE_REPORT.md).\n\n'+body[end:]
body_file=BASE/'continuation/PR64_BODY_COMPLETED.md';body_file.write_text(body)
subprocess.run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(body_file),'--jq','{state,draft,sha:.head.sha}'],cwd=ROOT,check=True)
save(BASE/'continuation/PUBLICATION_COMPLETE.json',dict(commit=cmd('git','rev-parse','HEAD'),counts=c,pr=64,draft=True,scopeComplete=True,completeSuite=False))
