"""Authorized, reviewed four-field metadata correction after pending execution.
Stops on drift; preserves historical evidence and every test assertion.
"""
from pathlib import Path
import subprocess,json,hashlib,time,zipfile,shutil
ROOT=Path('/workspace/russian-companion')
BASE=Path('/workspace/artifacts/post-correction-broad-2026-10-20')
HERE=BASE/'metadata-proposal'
def cmd(*args):return subprocess.check_output(list(args),cwd=ROOT,text=True).strip()
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def save(p,x):p.write_text(json.dumps(x,indent=2)+'\n')
deadline=time.monotonic()+6*3600
while not (BASE/'continuation/PUBLICATION_COMPLETE.json').exists():
    if time.monotonic()>deadline:raise RuntimeError('Continuation publication not verified within6h; no metadata changes')
    time.sleep(15)
previous=json.loads((BASE/'continuation/PUBLICATION_COMPLETE.json').read_text())
assert cmd('git','rev-parse','HEAD')==previous['commit']
assert not cmd('git','status','--porcelain')
assert cmd('git','branch','--show-current')=='cursor/content-audit-corrections-964f'
assert cmd('git','ls-remote','origin','refs/heads/cursor/content-audit-corrections-964f').split()[0]==previous['commit']
pr=json.loads(cmd('gh','api','repos/justinkulka-lang/russian-companion/pulls/64?metadata='+str(int(time.time())),'--jq','{state,draft,sha:.head.sha}'))
assert pr['state']=='open' and pr['draft'] and pr['sha']==previous['commit']
action=json.loads((HERE/'QUEUED_ACTION.json').read_text())
old_context=json.loads((BASE/'CURRENT_REGRESSION/CONTEXT.json').read_text())
for path,digest in old_context['inputTrees'].items():assert cmd('git','rev-parse','HEAD:'+path)==digest,path
for path,data in action['files'].items():assert sha(ROOT/path)==data['beforeSha256'],path
assert sha(HERE/'PROPOSED_METADATA_ONLY.patch')==action['patchSha256']
guard_path='app/src/test/resources/reviewed-curriculum-cyrillic.json'
candidate_path='release/acceptance/candidate.json'
before_guard=json.loads((ROOT/guard_path).read_text())
before_candidate=json.loads((ROOT/candidate_path).read_text())
subprocess.run(['git','apply','--check',str(HERE/'PROPOSED_METADATA_ONLY.patch')],cwd=ROOT,check=True)
subprocess.run(['git','apply',str(HERE/'PROPOSED_METADATA_ONLY.patch')],cwd=ROOT,check=True)
for path,data in action['files'].items():assert sha(ROOT/path)==data['afterSha256'],path
after_guard=json.loads((ROOT/guard_path).read_text());after_candidate=json.loads((ROOT/candidate_path).read_text())
assert len(after_guard['files'])==19
assert sum(len(x['bindings']) for x in after_guard['files'])==288
assert all(a['bindings']==b['bindings'] for a,b in zip(before_guard['files'],after_guard['files']))
assert after_guard['semanticAcceptance']==after_guard['rightsAcceptance']==False
assert {k:v for k,v in before_candidate.items() if k!='sourceDigest'}=={k:v for k,v in after_candidate.items() if k!='sourceDigest'}
assert after_candidate['frozen']==False and after_candidate['sourceCommit'] is None and after_candidate['publicRelease']=='NOT_APPROVED'
assert after_candidate['artifact']=={'status':'NOT_RECORDED'}
assert set(cmd('git','diff','--name-only').splitlines())==set(action['files'])
subprocess.run(['git','add','--',*action['files']],cwd=ROOT,check=True)
subprocess.run(['git','diff','--cached','--check'],cwd=ROOT,check=True)
subprocess.run(['git','commit','-qm','Refresh two exact source-guard hashes and the unfrozen pending source binding'],cwd=ROOT,check=True)
source=cmd('git','rev-parse','HEAD')
save(HERE/'APPLIED.json',dict(sourceCommit=source,previousEvidenceCommit=previous['commit'],changedFields=4,appCodeChanged=False,testAssertionsChanged=False,acceptanceChanged=False))
subprocess.run(['python',str(HERE/'prepare_run.py'),'VERIFIED'],cwd=ROOT,check=True)
run=HERE/'VERIFIED'
execution=subprocess.run(['bash',str(run/'run.sh')],cwd=ROOT)
collection=subprocess.run(['python',str(HERE/'collect_run.py'),'VERIFIED'],cwd=ROOT)
if collection.returncode!=0:raise RuntimeError('Fresh result collection failed; preserve metadata commit and all logs; no passing claim')
result=json.loads((run/'RESULT.json').read_text());context=json.loads((run/'CONTEXT.json').read_text())
assert result['exitCode']==execution.returncode and result['testCounts']['reported']==20
assert result['selectedSourceInventoryMatchesFreshXmlAndEvents'] and result['xmlFreshRelativeToRunScript']
assert context['sourceCommit']==source and cmd('git','rev-parse','HEAD')==source
assert not cmd('git','status','--porcelain')
for path,data in action['files'].items():assert sha(ROOT/path)==data['afterSha256']
for path in ['app/src/main','catalog','docs','review']:
    assert cmd('git','rev-parse','HEAD:'+path)==old_context['inputTrees'][path]
out=ROOT/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-metadata-binding'
out.mkdir(exist_ok=False)
for name in ['PROPOSED_METADATA_ONLY.patch','PROPOSAL.json','QUEUED_ACTION.json','APPLIED.json','STOPPED_BASELINE_PROJECTION.json','run_after_continuation.py','prepare_run.py','collect_run.py']:
    shutil.copy2(HERE/name,out/name)
for name in ['CONTEXT.json','RESULT.json','RUN.exit','LINT.xml']:shutil.copy2(run/name,out/name)
files=[p for p in sorted(run.rglob('*')) if p.is_file()]
index={str(p.relative_to(run)):sha(p) for p in files};archive=out/'RAW_VERIFICATION.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p in files:z.write(p,str(p.relative_to(run)))
with zipfile.ZipFile(archive) as z:
    assert z.testzip() is None
    for name,h in index.items():assert hashlib.sha256(z.read(name)).hexdigest()==h
save(out/'RAW_EVIDENCE_INDEX.json',dict(archiveSha256=sha(archive),archiveBytes=archive.stat().st_size,members=index,crcAndEveryMemberHashVerified=True))
allow=json.loads((ROOT/'audit/independent-gpt61-2026-10-12/current-comprehensive-audit/RETAINED_ARCHIVE_ALLOWLIST.json').read_text())['sha256']
assert len(allow)==66 and all(sha(ROOT/p)==h for p,h in allow.items())
save(out/'CONSERVATION.json',dict(all66ArchiveHashesMatched=allow,appMainCatalogDocsReviewUnchanged=True,
    testAssertionsUnchanged=True,all288GuardBindingsExact=True,candidateOnlySourceDigestChanged=True,
    physicalAcceptance='OUTSTANDING_LAST',formalApproval=False,apkAssemblyInstallSigning=False))
c=result['testCounts']
summary=(f"Metadata-only binding correction tested at `{source}`:20selected methods/4classes,"
    f"{c['passed']}passed/{c['failed']}failed/{c['errors']}errors/{c['skipped']}skipped. "
    'Only two exact guarded-file hashes,their reviewed-commit pointer and the unfrozen pending source digest changed. '
    'All288language bindings,semantic/rights acceptance false and every other candidate/artifact field remain exact. '
    'No app/test-assertion/content/schema/policy/history/approval change. This is a focused scope,not a complete suite or independent acceptance.')
report='# Metadata source binding correction\n\n'+summary+'\n\n'
report+='The original stopped broad contains this exact20-method subset:13SUCCESS/7FAILURE. STOPPED_BASELINE_PROJECTION.json is a projection of that original binary evidence,not a newly executed baseline. The seven observed failures concern stale sourceguard/candidate hashes. Original full raw failures remain immutable;no failure was waived or assertion changed. SOURCE_GUARD_DIFF_REVIEW.json in post-correction-broad-2026-10-20 retains the two code diffs and exact unchanged Cyrillic-bearing lines. This metadata refresh does not qualify linguistic/source/rights content.\n\n'
report+='The release digest was independently recomputed using the actual ordered release-input algorithm and matches the stopped JVM observation. It is a source binding,not an APK hash/signature or a frozen candidate. Historical failed/frozen artifacts,sourceCommit null,frozen false,NOT_APPROVED,artifact NOT_RECORDED and all outstanding acceptance remain unchanged.\n\n'
report+='Commands,cwd,activation,source/root/input/init/test hashes,fresh XML/event identity reconciliation and underlying exit are retained. Lint/tasks: '+json.dumps(dict(lint=result['lintIssuesBySeverity'],tasks=result['tasks']))+'. Raw stdout,XML,events and command are in the CRC/member-hash-verified RAW_VERIFICATION.zip. These existing tests cover exact source/literal negatives,changed release inputs,source/APK/commit mismatches,unfrozen candidate and failed-frozen candidate refusal. No release was approved.\n\n'
report+='All66retained archive hashes match;all64authentic histories and earlier failures/snapshots remain unchanged. Four screenshot exporters16methods excluded;noWRITE_* regeneration. Configured dev.russiancompanion/2.0.0-dev/code5;noAPK assembly/install/signing,new artifact hash/signature NOT_RECORDED. Environment install/start previously saved unchanged;publication user-confirmed independently unverified. The stopped broad and248-method continuation remain separate scopes with explicit two-interrupted-method overlap;never sum them with this focused scope.\n\n'
report+='Remaining work:review all unresolved broad/continuation failures,including historical/current fixture scope,review-package source pins,review response-rule compatibility and restoreUNCHANGED cause. A passing focused result does not repair the original run or close external findings. Qualified content/media/source/rights/reference and independent changed-source review remainOPEN. Original audit/fourCSVs missing;oldUnit7restore causeUNCONFIRMED. AuditINCOMPLETE;actualGalaxyS26Ultraairplane-mode/reboot acceptanceLAST after final comprehensive independent nonphysical audit. No merge,freeze,formal stage advancement,owner acceptance or release approval.\n'
(out/'ENGINEERING_CHANGE_REPORT.md').write_text(report)
save(out/'SHA256.json',{str(p.relative_to(out)):sha(p) for p in sorted(out.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
for relative in ['audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=ROOT/relative;p.write_text(p.read_text()+'\n\n## Metadata-only binding verification\n\n'+summary+' Read engineering-metadata-binding/ENGINEERING_CHANGE_REPORT.md under audit/gpt61-sol-high-comprehensive-2026-10-19/. Original failures preserved;all remaining qualification/recheck debtsOPEN,physicalLAST.\n')
p=ROOT/'AGENTS.md';s=p.read_text();p.write_text(s.replace('# Codex continuation\n','# Codex continuation\n\nCurrent engineering checkpoint: [metadata binding verification](audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-metadata-binding/ENGINEERING_CHANGE_REPORT.md). '+summary+' AuditINCOMPLETE;physicalLAST.\n',1))
subprocess.run(['git','add','--','AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md',str(out.relative_to(ROOT))],cwd=ROOT,check=True)
subprocess.run(['git','diff','--cached','--check'],cwd=ROOT,check=True)
subprocess.run(['git','commit','-qm','Retain focused metadata verification and preserve all outstanding audit limits'],cwd=ROOT,check=True)
subprocess.run(['git','push','origin','HEAD'],cwd=ROOT,check=True)
body=cmd('gh','api','repos/justinkulka-lang/russian-companion/pulls/64?body='+str(int(time.time())),'--jq','.body')
at=body.index('\n\n')+2
body=body[:at]+summary+' [Report and raw evidence](audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-metadata-binding/ENGINEERING_CHANGE_REPORT.md).\n\n'+body[at:]
body_file=HERE/'PR64_BODY_VERIFIED.md';body_file.write_text(body)
subprocess.run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(body_file),'--jq','{state,draft,sha:.head.sha}'],cwd=ROOT,check=True)
save(HERE/'PUBLICATION_COMPLETE.json',dict(sourceCommit=source,evidenceCommit=cmd('git','rev-parse','HEAD'),counts=c,draftPr=64,completeSuite=False,appCodeChanged=False))
