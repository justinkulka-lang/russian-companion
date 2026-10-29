"""Single-owner broad completion: preserve actual evidence; publish audit/docs only."""
from pathlib import Path
from datetime import datetime,timezone
from zoneinfo import ZoneInfo
import json,time,os,subprocess,hashlib,zipfile,traceback
R=Path('/workspace/russian-companion');W=Path(__file__).parent;B=W.parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit11-source-reconciliation-2026-10-26';branch='cursor/content-audit-corrections-964f'
run=lambda a:subprocess.check_output(a,cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,x:p.write_text(json.dumps(x,indent=2)+'\n')
try:
 with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
 save(W/'AUTO_COMPLETION_PROCESS.json',dict(pid=os.getpid(),startedUtc=datetime.now(timezone.utc).isoformat(),scope='actual broad evidence collection/verification; audit/docs-only commit,normal push,DraftPR64 update; no app/test fixes'))
 while not (W/'REGRESSION_COMPLETION.json').exists():time.sleep(45)
 done=json.loads((W/'REGRESSION_COMPLETION.json').read_text());assert done['resultExists'] and done['manifestExists'],'Incomplete runner;preserve raw evidence and diagnose'
 r=json.loads((W/'REGRESSION/RESULT.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());source=r['sourceCommit'];assert run(['git','rev-parse','HEAD'])==source==c['sourceCommit'];assert run(['git','branch','--show-current'])==branch and not run(['git','status','--porcelain']);assert r['testCounts']['reported']==1441 and r['classes']==249 and done['runnerExit']==r['exitCode']==int(done['gradleExit'])
 # Wait for the monitor to close before archiving its log/status.
 for _ in range(12):
  m=json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text())
  if m['completionExists']:break
  time.sleep(5)
 assert m['completionExists']
 for root,h in c['inputTrees'].items():assert run(['git','rev-parse',source+':'+root])==h
 comparison=json.loads(run(['python3',str(D/'verify_comparisons.py')]))
 applied=json.loads((D/'COMPARISON_APPLIED_COMMIT.json').read_text())['commit']
 assert run(['git','rev-parse',source+':app/src/test'])==run(['git','rev-parse',applied+':app/src/test'])
 rec=json.loads((D/'COMPARISON_RECONCILIATION.json').read_text())
 for n,h in rec['fixedInputTrees'].items():assert run(['git','rev-parse',source+':'+n])==h
 run(['python3',str(D/'BROAD_DISCOVERY/verify.py')]);run(['python3',str(B/'collect_attempt.py'),'ATTEMPT_3','BROAD']);A=D/'ATTEMPT_3';idx={};save(A/'ASSERTION_CONSERVATION.json',comparison)

 with zipfile.ZipFile(A/'MONITORING.zip','x',zipfile.ZIP_DEFLATED) as z:
  for n in ['durable_monitor.py','DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json','complete_broad.py']:
   p=W/n;z.write(p,n);idx[n]=dict(bytes=p.stat().st_size,sha256=sha(p.read_bytes()))
 save(A/'MONITORING_INDEX.json',idx)
 original=json.loads((D/'ATTEMPT_1/REGRESSION_RESULT.json').read_text());ids={(x['testClass'],x['method']) for x in original['tests'] if x['outcome'] in ['FAILED','ERROR']};rechecked=[x for x in r['tests'] if (x['testClass'],x['method']) in ids];assert len(rechecked)==len(ids)==8;save(A/'PRIOR_FAILURE_RECHECK.json',dict(originalSource=original['sourceCommit'],originalCounts=original['testCounts'],broadSource=source,rechecked=rechecked,scopesNotSummed=True))
 n=r['testCounts'];counts=f"{n['reported']}reported/{n['passed']}passed/{n['failed']}failed/{n['errors']}errors/{n['skipped']}skipped";failures=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']];skips=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome']=='SKIPPED'];good=not failures and r['exitCode']==0 and not m['newNativeReports']
 text=f'''# Unit11 current selected-host broad regression completed

Actual tested source `{source}`,started `{datetime.fromisoformat(c['startedUtc']).astimezone(ZoneInfo('America/Chicago')).isoformat()}`,finished `{datetime.fromisoformat(r['finishedUtc']).astimezone(ZoneInfo('America/Chicago')).isoformat()}` America/Chicago;raw UTC retained: **{counts}**,249classes,actual Gradle/runner exit{r['exitCode']}. Fresh discovery/XML/unique events/outcomes/exact method set/source roots/six input trees agree. Discovery's own20429e6e source/root is retained separately;docs-only checkpoints preserve identical six inputs. All1425earlier method identities plus16Unit11 methods/four classes retained,zero removals. Full commands/activation/initializers/source/root/inputs/XML/events/GC/lint/rawlogs/exits in ATTEMPT_3/RAW.zip;every failure preserved. Failed/error identities:{json.dumps(failures)}. Skips:{json.dumps(skips)}.

This is selected-host regression,not complete app/curriculum/device acceptance or independent audit/finding closure. Original e3115899395reported/386pass/8fail/1physicalskip,71classes,exit1(ATTEMPT_1) and separateca57f63788/88methods,16classes,exit0(ATTEMPT_2) remain immutable/separate/unsummed. Eight original failed identities re-executed in this broad run;see PRIOR_FAILURE_RECHECK.json. Prior658d8eab1425reported/1424pass/1physicalskip predates Unit11 changes. Mainff539220563reported562pass1skip was another selected scope;420f41ff691was source inventory/unverified handoff execution with67focused methods verified separately. No counts are added across these scopes.

Lint:{json.dumps(r['lintIssuesBySeverity'])}. Task/cache states:{json.dumps(r['tasks'])}. Gate attachment is not release approval. All original assertions/source pins conserved;five historical comparison files/explicit genuine reference-kernel inputs/current controls verified independently. Actual broad test tree equals the reviewed comparison-source tree;production/catalog/docs/review/release/155records unchanged. All72archives SHA/CRC verified. Earlier genuine v1 capture/core14/14/two153file renders/all30packages each/95identity-only records/60worksheets/95historical versions retained. Current corec2d297e7/canonical61bbaafebb57184f2702bf321f09ecaa74ec3e2603bd8bc8faf91f4e2e4c1b19;29other source choices/reviewer-owner-rights-mapping-admission-approval fields unchanged. No generator ran here.

Four screenshot exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Provisional SQLite wrapper JIT compilation exclusion remains unadopted/native causeUNCONFIRMED;no additional test exclusion. New native reports:{json.dumps(m['newNativeReports'])}. APK identity/signature null;assembly/install/signingNOT_RUN. All48findingsOPEN/auditINCOMPLETE;21print-singleton policies/qualified linguistic-source-rights-mapping-owner reviews/CC048authority conflict/originalUnit7restorecause unresolved. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance outstanding and LAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. Private Penguin unread/unpackaged/environment settings unchanged;earlier publication user-confirmed independently unobserved.
'''

 (D/'BROAD_REPORT.md').write_text(text);(D/'BROAD_STATUS.md').write_text(text)
 next_task='Next large task: bounded Unit12 cumulative-review research using actual current source/authority requirements and admitted material. Inventory objective/teaching/practice/check/feedback/repair/source chains and genuine historical compatibility before proposing a new obligation. Preserve existing policies,all saved versions and captures. Research is not qualified linguistic/rights/mapping/owner acceptance or automatic CC020/CC027 closure.' if good else 'Next required stop: inspect ATTEMPT_3/FAILED_METHODS.json and every full raw failure before edits. Preserve this actual broad result;separate current defects from exact historical assumptions,retain all original assertions/pins/current controls,and verify affected full classes separately without relabeling or summing scopes.'
 (D/'NEXT_TASK.md').write_text('# Next task after actual Unit11 broad regression\n\n'+next_task+'\n\nRead BROAD_REPORT.md and preserved INTEGRATION_REPORT.md/FOLLOWUP_REPORT.md. One agent/existing checkout. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval. No test job remains active at this completed checkpoint.\n')

 pointers=[R/'AGENTS.md',R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md',R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
 for p in pointers:
  link=('audit/' if p.name=='AGENTS.md' else '../')+'gpt61-sol-high-comprehensive-2026-10-19/engineering-unit11-source-reconciliation-2026-10-26/BROAD_REPORT.md';p.write_text(f'Current checkpoint: [Unit11 current broad selected-host regression completed]({link}). Actual{source}:{counts},249classes,exit{r["exitCode"]}. Fresh XML/events/discovery/six inputs/raw hashes verified;all failures preserved. Earlier395/88/1425scopes separate/unsummed. Read NEXT_TASK.md. All48OPEN/auditINCOMPLETE/physicalLAST/four exporters excluded/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
 def manifest():
  save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes()) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'});run(['git','add',str(D),*[str(p) for p in pointers]])
 manifest();v=json.loads(run(['python3',str(D/'verify_saved_results.py')]));save(A/'FINAL_VERIFICATION.json',v);manifest();allowed={p.relative_to(R).as_posix() for p in pointers};changed=run(['git','diff','--cached','--name-only']).splitlines();assert changed and all(x in allowed or x.startswith(D.relative_to(R).as_posix()+'/') for x in changed);run(['git','diff','--cached','--check']);run(['git','commit','-m','Preserve Unit11 broad regression evidence and continuation checkpoint']);head=run(['git','rev-parse','HEAD']);run(['git','push','origin','HEAD:'+branch]);old=run(['gh','pr','view','64','--json','body','--jq','.body']);(W/'PR64_BEFORE_BROAD_COMPLETION.md').write_text(old)
 body=f'''Russian Companion curriculum corrections remain in progress. Fresh Unit11 review requires noun behavior and possession/absence while genuine saved v1 assignments retain their meaning. Authentic source bindings and original historical assertions/pins are preserved.

Current checkpoint `{head}`;DraftPR64 remains unmerged/unapproved.

Actual selected-host broad regression at `{source}`: **{counts}**,249classes,exit{r['exitCode']}. Fresh discovery/XML/events/exact1441method identities/source roots/six inputs verified;all1425oldIDs+16Unit11 additions/no removals. Failed/error identities:{json.dumps(failures)}. Lint:{json.dumps(r['lintIssuesBySeverity'])};gate:{r['tasks']['verifyReleaseGateAttachment']}. Full actual raw evidence in ATTEMPT_3. Not complete app/curriculum/device acceptance or independent audit closure.

Original e3115899395scope386pass/8fail/1physicalskip/exit1 remains immutable;separateca57f63788/88methods/16classes passed. All eight original failed IDs rechecked here;earlier scopes overlap/not summed. Every original assertion/source pin/72archives conserved;five historical comparison files and current controls verified. Production/155records unchanged during comparison reconciliation and broad runtime. Corec2d297e7/canonical61bbaafe…1b19;no APK assembly/install/signing/identity/signature.

Report/next task:`audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit11-source-reconciliation-2026-10-26/BROAD_REPORT.md`,NEXT_TASK.md;AGENTS/continuation/checkpoint updated. {next_task}

All48OPEN/auditINCOMPLETE/21print-singleton policies/qualified reviews/CC048authority conflict/originalUnit7restorecause outstanding. Four exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. Private PDF/environment settings unchanged;publication user-confirmed independently unobserved;provisional JITdirective unadopted/native causeUNCONFIRMED.
'''

 p=W/'PR64_BROAD_COMPLETED_BODY.md';p.write_text(body);response=json.loads(run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(p),'--jq','{number,draft,head:.head.sha}']));assert response['draft'] and response['head']==head and not run(['git','status','--porcelain']);save(W/'AUTO_COMPLETION_RESULT.json',dict(completedUtc=datetime.now(timezone.utc).isoformat(),testedSource=source,evidenceCommit=head,counts=n,classes=249,exitCode=r['exitCode'],pushed=True,prUpdated=True,failures=failures,scopesNotSummed=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'));print((W/'AUTO_COMPLETION_RESULT.json').read_text(),flush=True)
except BaseException:
 save(W/'AUTO_COMPLETION_ERROR.json',dict(atUtc=datetime.now(timezone.utc).isoformat(),traceback=traceback.format_exc(),instruction='Preserve raw/staged evidence;diagnose actual error;never force-push or duplicate collector'));raise
