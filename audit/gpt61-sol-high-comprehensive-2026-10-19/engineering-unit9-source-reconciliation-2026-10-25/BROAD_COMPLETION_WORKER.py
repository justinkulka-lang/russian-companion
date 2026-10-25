"""Single-owner broad completion: preserve actual evidence; publish audit/docs only."""
from pathlib import Path
from datetime import datetime,timezone
import json,time,os,subprocess,hashlib,zipfile,traceback
R=Path('/workspace/russian-companion');W=Path(__file__).parent;B=W.parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit9-source-reconciliation-2026-10-25';branch='cursor/content-audit-corrections-964f'
run=lambda a:subprocess.check_output(a,cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,x:p.write_text(json.dumps(x,indent=2)+'\n')
try:
 with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
 save(W/'AUTO_COMPLETION_PROCESS.json',dict(pid=os.getpid(),startedUtc=datetime.now(timezone.utc).isoformat(),scope='actual broad evidence collection/verification; audit/docs-only commit,normal push,DraftPR64 update; no app/test fixes'))
 while not (W/'REGRESSION_COMPLETION.json').exists():time.sleep(45)
 done=json.loads((W/'REGRESSION_COMPLETION.json').read_text());assert done['resultExists'] and done['manifestExists'],'Incomplete runner;preserve raw evidence and diagnose'
 r=json.loads((W/'REGRESSION/RESULT.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());source=r['sourceCommit'];assert run(['git','rev-parse','HEAD'])==source==c['sourceCommit'];assert run(['git','branch','--show-current'])==branch and not run(['git','status','--porcelain']);assert r['testCounts']['reported']==1410 and r['classes']==241 and done['runnerExit']==r['exitCode']==int(done['gradleExit'])
 # Wait for the monitor to close before archiving its log/status.
 for _ in range(12):
  m=json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text())
  if m['completionExists']:break
  time.sleep(5)
 assert m['completionExists']
 for root,h in c['inputTrees'].items():assert run(['git','rev-parse',source+':'+root])==h
 # Original assertion/source pins remain archived and every original nonblank line survives.
 rows=json.loads((D/'ORIGINAL_COMPARISONS_INDEX.json').read_text())
 with zipfile.ZipFile(D/'ORIGINAL_COMPARISONS.zip') as z:
  assert z.testzip() is None
  for x in rows:
   old=z.read(x['path']);new=(R/x['path']).read_bytes();assert sha(old)==x['sha256'] and sha(new)==x['appliedSha256'];assert old==subprocess.check_output(['git','show',x['sourceCommit']+':'+x['path']],cwd=R);it=iter(t.strip() for t in new.decode().splitlines() if t.strip());assert all(any(t==s.strip() for t in it) for s in old.decode().splitlines() if s.strip())
 run(['python3',str(B/'collect_attempt.py'),'ATTEMPT_3','BROAD']);A=D/'ATTEMPT_3';idx={}
 with zipfile.ZipFile(A/'MONITORING.zip','x',zipfile.ZIP_DEFLATED) as z:
  for n in ['durable_monitor.py','DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json','complete_broad.py']:
   p=W/n;z.write(p,n);idx[n]=dict(bytes=p.stat().st_size,sha256=sha(p.read_bytes()))
 save(A/'MONITORING_INDEX.json',idx)
 original=json.loads((D/'ATTEMPT_1/REGRESSION_RESULT.json').read_text());ids={(x['testClass'],x['method']) for x in original['tests'] if x['outcome'] in ['FAILED','ERROR']};rechecked=[x for x in r['tests'] if (x['testClass'],x['method']) in ids];assert len(rechecked)==len(ids)==5;save(A/'PRIOR_FAILURE_RECHECK.json',dict(originalSource=original['sourceCommit'],originalCounts=original['testCounts'],broadSource=source,rechecked=rechecked,scopesNotSummed=True))
 n=r['testCounts'];counts=f"{n['reported']}reported/{n['passed']}passed/{n['failed']}failed/{n['errors']}errors/{n['skipped']}skipped";failures=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']];skips=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome']=='SKIPPED'];good=not failures and r['exitCode']==0
 text=f'''# Unit9 current selected-host broad regression completed

Actual tested source `{source}`,started `{c['startedUtc']}`,finished `{r['finishedUtc']}`(UTC): **{counts}**,241classes,actual Gradle/runner exit{r['exitCode']}. Fresh discovery/XML/unique events/outcomes/exact method set/six input trees agree. Discovery's actual source/root are preserved independently,with identical six input trees proven across docs-only commits. All1394earlier method identities plus16Unit9 additions are retained. Full commands/activation/initializers/source/root/input trees,XML/events/raw logs/GC/lint/exits are in ATTEMPT_3/RAW.zip;every failure is preserved. Failed/error identities:{json.dumps(failures)}. Skips:{json.dumps(skips)}.

This is current selected-host regression,not complete app/curriculum/device acceptance or independent audit/finding closure. Earlier scopes remain immutable,separate and unsummed:fd246abd242reported/236pass/5fail/1physicalskip,49classes,exit1(ATTEMPT_1);9c328e5374/74pass,15fullclasses,exit0(ATTEMPT_2);93831e85broad1394reported/1393pass/1physicalskip predates Unit9 changes. The five original failed identities are rechecked in PRIOR_FAILURE_RECHECK.json. Historical main562passing and420f41ff691sourceinventory/unverified handoff execution/67verifiedfocused tests are different sources/scopes,not current complete-suite results.

Lint:{json.dumps(r['lintIssuesBySeverity'])}. Task/cache states:{json.dumps(r['tasks'])}. Gate attachment is not release approval. Original assertions/source pins/all70archives/wholebaseline/coreassertions conserved. Production/155source-reviewrecords unchanged during comparison reconciliation and this broad run. The prior authentic two153-file renders/all30packages/95identity-onlychanges/60unchangedworksheets/95historicalversions remain verified;no generator ran here.

Four screenshot exporters/16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Provisional SQLite wrapper JIT compilation exclusion remains unadopted/native causeUNCONFIRMED;no additional test excluded. Newnative reports:{json.dumps(m['newNativeReports'])}. APK identity/signature null;assembly/install/signingNOT_RUN. All48findingsOPEN/auditINCOMPLETE;23print-singleton policies/qualified linguistic/source/rights/mapping/owner reviews/CC048authority conflict/originalUnit7restorecause remain. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance OUTSTANDING/LAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. Private Penguin/environment settings unchanged;earlier publication user-confirmed/independently unobserved.
'''
 (D/'BROAD_REPORT.md').write_text(text);(D/'BROAD_STATUS.md').write_text(text)
 next_task='Next large task: bounded research of the next remaining cumulative-review teaching/assessment gap (begin with Unit10 inventory),using actual current source/authority requirements and existing admitted material. Establish objective/teaching/practice/check/feedback/repair/source chains,compatibility/history implications and limits before proposing any new versioned obligation. Preserve all existing policies and genuine learner captures. This is engineering research,not qualified linguistic/rights/mapping/owner acceptance or automatic closure of CC020/CC027.' if good else 'Review ATTEMPT_3/FAILED_METHODS.json and complete raw XML first. Preserve every failure before changes;separate actual current defects from exact historical-comparison assumptions. Archive originals and retain all assertions/pins;keep current behavior controls. Verify affected full classes separately without relabeling this broad result or summing overlapping scopes.'
 (D/'NEXT_TASK.md').write_text('# Next task after Unit9 broad regression\n\n'+next_task+'\n\nRead BROAD_REPORT.md and the preserved INTEGRATION_REPORT.md/FOLLOWUP_REPORT.md. One agent/existing checkout. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval. No test job remains active at this completed checkpoint.\n')
 pointers=[R/'AGENTS.md',R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md',R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
 for p in pointers:
  link=('audit/' if p.name=='AGENTS.md' else '../')+'gpt61-sol-high-comprehensive-2026-10-19/engineering-unit9-source-reconciliation-2026-10-25/BROAD_REPORT.md';p.write_text(f'Current checkpoint: [Unit9 current broad selected-host regression completed]({link}). Actual{source}:{counts},241classes,exit{r["exitCode"]}. Fresh XML/events/discovery/six inputs/raw hashes verified;all failures preserved. Earlier242/74/1394scopes separate/unsummed. Read NEXT_TASK.md. All48OPEN/auditINCOMPLETE/physicalLAST/four exporters excluded/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
 def manifest():
  save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes()) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'});run(['git','add',str(D),*[str(p) for p in pointers]])
 manifest();v=json.loads(run(['python3',str(D/'verify_saved_results.py')]));save(A/'FINAL_VERIFICATION.json',v);manifest();allowed={p.relative_to(R).as_posix() for p in pointers};changed=run(['git','diff','--cached','--name-only']).splitlines();assert changed and all(x in allowed or x.startswith(D.relative_to(R).as_posix()+'/') for x in changed);run(['git','diff','--cached','--check']);run(['git','commit','-m','Preserve Unit9 broad regression evidence and continuation checkpoint']);head=run(['git','rev-parse','HEAD']);run(['git','push','origin','HEAD:'+branch]);old=run(['gh','pr','view','64','--json','body','--jq','.body']);(W/'PR64_BEFORE_BROAD_COMPLETION.md').write_text(old)
 body=f'''Russian Companion content/curriculum corrections remain in progress. Fresh Unit9 review requires supplied noun behavior/plurals/genders while genuine saved v1/v2 assignments keep their meaning. Authentic source bindings and original historical assertions/pins are preserved.

Current checkpoint `{head}`;DraftPR64 remains unmerged/unapproved.

Actual current selected-host broad regression at `{source}`: **{counts}**,241classes,exit{r['exitCode']}. Fresh discovery/XML/events/exact1410method identities/six input trees verified. Failed/error identities:{json.dumps(failures)}. Lint:{json.dumps(r['lintIssuesBySeverity'])};gate:{r['tasks']['verifyReleaseGateAttachment']}. Full raw evidence in ATTEMPT_3. This is not complete app/curriculum/device acceptance or independent audit closure.

Earlier242scope at fd246abd remains236pass/5fail/1physicalskip/exit1. Separate9c328e5374/74methods/15fullclasses passed,allfive failedIDs rechecked. All earlier raw failures retained;scopes overlap/not summed. Alloriginal assertions/pins/70archives conserved;production/155records unchanged during comparison reconciliation and broad runtime. No APK assembly/install/signing;APK identity/signature null.

Report and next task:`audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit9-source-reconciliation-2026-10-25/BROAD_REPORT.md` and NEXT_TASK.md;AGENTS/continuation/checkpoint updated. {next_task}

All48findingsOPEN/auditINCOMPLETE/23print-singletonpolicies/qualified reviews/CC048authority conflict/originalUnit7restorecause outstanding. Four exporters explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2(16methods). Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. Private reference/environment settings unchanged;publication user-confirmed/independently unobserved. Provisional SQLite JIT directive remains unadopted/native causeUNCONFIRMED.
'''
 p=W/'PR64_BROAD_COMPLETED_BODY.md';p.write_text(body);response=json.loads(run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(p),'--jq','{number,draft,head:.head.sha}']));assert response['draft'] and response['head']==head and not run(['git','status','--porcelain']);save(W/'AUTO_COMPLETION_RESULT.json',dict(completedUtc=datetime.now(timezone.utc).isoformat(),testedSource=source,evidenceCommit=head,counts=n,classes=241,exitCode=r['exitCode'],pushed=True,prUpdated=True,failures=failures,scopesNotSummed=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'));print((W/'AUTO_COMPLETION_RESULT.json').read_text(),flush=True)
except BaseException:
 save(W/'AUTO_COMPLETION_ERROR.json',dict(atUtc=datetime.now(timezone.utc).isoformat(),traceback=traceback.format_exc(),instruction='Preserve raw/staged evidence;diagnose actual error;never force-push or duplicate collector'));raise
