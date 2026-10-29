from pathlib import Path
import json,hashlib,zipfile,subprocess,shutil,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/unit-identity-cache-correction-2026-10-28');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit-identity-cache-correction-2026-10-28'
read=lambda p:json.loads(p.read_text());sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
assert(W/'REGRESSION_COMPLETION.json').exists(),'Correction supervisor still active; do not collect'
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
prep=read(D/'PREPARATION.json');scope=read(W/'SCOPE.json');expected={(t['testClass'],t['method'])for t in scope['expectedMethods']}
for key in ['testHashes','currentRecordHashes','protectedEvidenceHashes']:
 for n,h in prep[key].items():assert sha((R/n).read_bytes())==h,n
for n,h in prep['archives'].items():
 assert sha((R/n).read_bytes())==h,n
 with zipfile.ZipFile(R/n)as z:assert z.testzip()is None,n
assert sha((R/prep['changedProductionFile']).read_bytes())==prep['newSourceSha256']
for mode,label in [('DISCOVERY','RAW_DISCOVERY'),('REGRESSION','RAW_CORRECTED')]:
 c=read(W/mode/'CONTEXT.json');r=read(W/mode/'RESULT.json');end=read(W/(mode+'_COMPLETION.json'))
 assert r['inputTrees']==c['inputTrees']==prep['inputTrees']and r['sourceCommit']==c['sourceCommit']
 assert r['exitCode']==end['runnerExit']==int(end['gradleExit'])and c['selectors']==scope['selectors']
 assert {(t['testClass'],t['method'])for t in r['tests']}==expected and len(expected)==25
 files={p.relative_to(W).as_posix():p for p in(W/mode).rglob('*')if p.is_file()}
 for p in W.glob(mode+'_*'):
  if p.is_file():files[p.name]=p
 for n in ['run.py','launch.py','supervise.py','resources.gradle','prepare.py']:files['TOOLING/'+n]=W/n
 files['TOOLING/SCOPE.json']=W/'SCOPE.json';files['TOOLING/collect.py']=Path(__file__)
 for n,h in c['initScriptSha256'].items():
  p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
 idx={}
 with zipfile.ZipFile(D/(label+'.zip'),'x',zipfile.ZIP_DEFLATED)as z:
  for n,p in sorted(files.items()):
   assert not n.lower().endswith('.pdf');data=p.read_bytes();z.writestr(n,data);idx[n]={'bytes':len(data),'sha256':sha(data)}
 save(D/(label+'_INDEX.json'),idx)
 with zipfile.ZipFile(D/(label+'.zip'))as z:
  assert z.testzip()is None and set(z.namelist())==set(idx)
  for n,h in idx.items():data=z.read(n);assert len(data)==h['bytes']and sha(data)==h['sha256']
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copyfile(W/mode/n,D/(mode+'_'+n))
r=read(W/'REGRESSION/RESULT.json');failures=[]
for p in(W/'REGRESSION/XML').glob('*.xml'):
 for t in ET.parse(p).getroot().findall('testcase'):
  for kind in ['failure','error']:
   f=t.find(kind)
   if f is not None:failures.append({'testClass':t.get('classname'),'method':t.get('name'),'kind':kind,'message':f.get('message',''),'trace':f.text or ''})
save(D/'ACTUAL_FAILURES.json',{'sourceCommit':r['sourceCommit'],'count':len(failures),'failures':failures,'originalAssertionsNotWaived':True})
good=r['testCounts']==dict(reported=25,passed=25,failed=0,errors=0,skipped=0)and r['exitCode']==0 and r['lintIssuesBySeverity']['Fatal']==0 and r['lintIssuesBySeverity']['Error']==0
save(D/'VERIFICATION_SUMMARY.json',{'sourceCommit':r['sourceCommit'],'counts':r['testCounts'],'classes':r['classes'],'exitCode':r['exitCode'],'good':good,'all282TestsByteExact':True,'baselineEntireTestUnchanged':True,'bindings':'RECONCILIATION_PENDING;155records byte-exact','archivesShaCrc':77,'sourceBindingReconciliationNotApproval':True,'completeSuite':False,'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST','apkIdentity':None,'apkSignature':None})
shutil.copyfile(__file__,D/'collect.py')
summary=f'{r["testCounts"]["reported"]}reported/{r["testCounts"]["passed"]}passed/{r["testCounts"]["failed"]}failed/{r["testCounts"]["errors"]}errors/{r["testCounts"]["skipped"]}skipped;2FULLclasses;exit{r["exitCode"]}'
(D/'COMPLETED_STATUS.md').write_text(f'''# Unit identity read-through correction result

Actual source `{r['sourceCommit']}`: **{summary}**. Whole unchanged3method baseline and existing22method RemainingReauditCorrectionTest ran. Original source7c3005f8 had2count failures(12canonical executions vs1)and1strictidentitycontrolPASS;that original result stays immutable and unsummed. The corrected baseline now enforces one canonical computation after a cold read or bounded eviction while retaining changed-pack rejection. Existing cold/warm/off-main, weak-reference reclamation, staging/restore/epoch and snapshot controls remain unmodified.

All282tests/wholebaseline/155bindingbytes/77archiveSHA+CRC/prior1499+interrupted119+baseline raw evidence conserved. Only the documented production cache file changed. **Source binding reconciliation remains PENDING**; no current qualified acceptance is inferred from unchanged binding bytes. No native text/item/policy/schema/answer/history meaning or canonical digest definition changed. No raised cache cap or hash allowlist.

Discovery25synthetic skips/zero runtimepasses is separate. Raw source/command/inputtrees/initSHA/XML/events/logs/GC/lint/gate/cache/exits/full failures and archive memberSHA/CRC retained. Lint `{json.dumps(r['lintIssuesBySeverity'])}`;actual task/cache states `{json.dumps(r['tasks'])}`. Compile/cache states are not fresh execution. Gate attachment is not release approval. APKidentity/signaturenull;no assembly/install/newsigning.

Next:review any unexpected actual outcome before further edits. Continue the prepared UI baseline, then authentic source-binding reconciliation and full affected119retry at exact new inputs before the next stable broad milestone/curriculum advancement. Codex only;no further Cursor use. All48OPEN/auditINCOMPLETE/physicalGalaxyacceptanceLAST/no merge/freeze/formal advancement/releaseapproval. PrivatePDFunread/unpackaged;environmentunchanged;JITdirectiveunadopted/notatestexclusion/nativecauseUNCONFIRMED. Earlier scopes remain separate/unsummed,not complete-suiteproof.
''')
rel=D.relative_to(R).as_posix();pointers=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
for n in pointers:
 p=R/n;link=rel+'/COMPLETED_STATUS.md'if n=='AGENTS.md'else'../'+rel.removeprefix('audit/')+'/COMPLETED_STATUS.md'
 p.write_text(f'Current checkpoint: [Unit identity cache correction outcome]({link}).Actual{r["sourceCommit"]}:{summary}.All282tests/whole3methodbaseline and original22methodfullclass unchanged;155bindingsRECONCILIATION_PENDING/77archives/priorraw exact. Read actual result before next UI baseline/bindings/full119retry. Codex only/no further Cursor use. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in sorted(D.rglob('*'))if p.is_file()and p!=D/'SHA256.json'})
subprocess.run(['git','add',rel,*pointers],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True)
subprocess.run(['git','commit','-m','Preserve actual unit identity cache correction verification'],cwd=R,check=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(json.dumps({'checkpoint':subprocess.check_output(['git','rev-parse','HEAD'],cwd=R).decode().strip(),'source':r['sourceCommit'],'counts':r['testCounts'],'good':good,'lint':r['lintIssuesBySeverity']}))
