"""Monitor the actual1380-method run; publish preserved evidence only after exact validation."""
from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,zipfile,shutil,time,traceback,os
W=Path(__file__).parent;R=Path('/workspace/russian-companion');REL=Path('audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit7-reconciled-broad-2026-10-24');D=R/REL
EXPECTED='dcc9314889566ba6f4f22333e2c98b34e9c9dd64';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();now=lambda:datetime.now(timezone.utc).isoformat()
def save(p,v):p.write_text(json.dumps(v,indent=2)+'\n')
def cmd(args):return subprocess.check_output(args,cwd=R,text=True).strip()
def state(value,**kw):
 tmp=W/'COLLECTOR_STATUS.tmp';save(tmp,dict(state=value,utc=now(),**kw));tmp.replace(W/'COLLECTOR_STATUS.json')
def manifest():save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p!=D/'SHA256.json'})
try:
 assert not D.exists(),'Never overwrite an evidence attempt'
 launcher=json.loads((W/'REGRESSION_LAUNCHER.json').read_text());assert launcher['sourceCommit']==EXPECTED
 state('WAITING_FOR_RUNNER',sourceCommit=EXPECTED,supervisorPid=launcher['pid'])
 while not (W/'REGRESSION_COMPLETION.json').exists():
  proc=Path('/proc')/str(launcher['pid'])/'cmdline';assert proc.exists() and b'supervise.py' in proc.read_bytes(),'Supervisor exited without completion; preserve partial evidence'
  assert not (W/'MONITOR_ESCALATION.json').exists(),'New native report requires agent investigation; preserve original reports and do not infer a complete result'
  time.sleep(30)
 completed=json.loads((W/'REGRESSION_COMPLETION.json').read_text());assert completed['sourceCommit']==EXPECTED
 assert not (W/'MONITOR_ESCALATION.json').exists(),'New native report requires investigation before publication'
 assert completed['resultExists'] and completed['manifestExists'],'No validated completed RESULT; preserve raw evidence,never substitute stale discovery XML'
 O=W/'REGRESSION';result=json.loads((O/'RESULT.json').read_text());context=json.loads((O/'CONTEXT.json').read_text())
 assert result['sourceCommit']==context['sourceCommit']==EXPECTED
 assert cmd(['git','rev-parse','HEAD'])==EXPECTED and not cmd(['git','status','--porcelain'])
 assert result['testCounts']['reported']==1380 and result['classes']==233
 assert completed['runnerExit']==result['exitCode']==int((O/'RUN.exit').read_text())==int(completed['gradleExit'])
 assert result['exitCode'] in [0,1]
 state('VERIFYING_COMPLETED_SCOPE',sourceCommit=EXPECTED)
 prior=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit7-source-reconciliation-2026-10-24'
 previous=json.loads(cmd(['python3',str(prior/'verify_saved_results.py')]))
 reconciliation=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit7-broad-failure-reconciliation-2026-10-24'
 preceding={n:json.loads(cmd(['python3',str(reconciliation/n)])) for n in ['verify_saved_results.py','verify_corrected_results.py']}
 assert all(x['verified'] for x in preceding.values())
 proposal=json.loads((prior/'PROPOSAL_REVIEW.json').read_text());mutable={row['path']:row['newSha256'] for row in proposal['files']};assert len(mutable)==155
 for name,value in mutable.items():assert sha(R/name)==value,name
 pointers=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
 conserved={name:sha(R/name) for name in cmd(['git','ls-files','audit']).splitlines() if (R/name).is_file() and name not in mutable and name not in pointers}
 D.mkdir();save(D/'CONSERVED_INPUTS.json',conserved);save(D/'CURRENT_RECORD_INPUTS.json',dict(sourceCommit=EXPECTED,files=mutable,basis='Exact tested source Git blobs and live hashes verified at collection; later authentic regeneration does not rewrite this historical scope'))
 save(D/'PRIOR_BINDING_VERIFICATION.json',previous);save(D/'PRIOR_FOLLOWUP_VERIFICATIONS.json',preceding)
 B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';extra={p.relative_to(R).as_posix():sha(p) for p in [B/'engineering-unit6-present-baseline-2026-10-23/BASELINE_CURRENT_UNIT6_V2.zip',B/'engineering-unit7-gender-baseline-2026-10-24/BASELINE_CURRENT_UNIT7_V1.zip']};save(D/'GENUINE_EXTRA_CAPTURES.json',extra)
 files={}
 for mode in ['DISCOVERY','REGRESSION']:
  for p in (W/mode).rglob('*'):
   if p.is_file():files[mode+'/'+p.relative_to(W/mode).as_posix()]=p
 for name in ['run.py','resources.gradle','supervise.py','launch_mode.py','REGRESSION_LAUNCHER.json','REGRESSION_RUNNER_PROCESS.json','REGRESSION_LAUNCHER.exit','REGRESSION_COMPLETION.json','REGRESSION_RUNNER.log','REGRESSION_LAUNCHER.log']:files[name]=W/name
 for name in context['initScriptSha256']:files['INIT/'+Path(name).name]=Path(name)
 index=[]
 with zipfile.ZipFile(D/'RAW_BROAD.zip','x',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for name,p in sorted(files.items()):
   assert p.is_file() and not p.name.lower().endswith('.pdf');index.append(dict(path=name,bytes=p.stat().st_size,sha256=sha(p)));z.write(p,name)
 save(D/'RAW_BROAD_INDEX.json',index)
 for name in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(O/name,D/('RUNTIME_'+name))
 for name in ['verify_saved_broad.py','complete_evidence.py']:shutil.copy2(W/name,D/name)
 failed=[t for t in result['tests'] if t['outcome'] in ['FAILED','ERROR']];skipped=[t for t in result['tests'] if t['outcome']=='SKIPPED'];counts=result['testCounts']
 save(D/'FAILED_METHODS.json',failed);save(D/'SKIPPED_METHODS.json',skipped)
 save(D/'CONSERVATION.json',dict(sourceCommit=EXPECTED,inputTrees=context['inputTrees'],canonicalSourceDigest='d48aa7e0090bd30422172f7b7c87fe52fe2859fa3c5be9b0851d2f9704f30e3a',sourceDigestBasis='Exact same main/catalog/docs/review/release trees as preceding official Kotlin computation; no approximate recomputation',testedGeneratedRecords=155,currentRecordIdentityBasis='CURRENT_RECORD_INPUTS.json uses tested source Git objects; live matches verified at collection',originalArchivesShaCrc=66,genuineExtraCaptures=2,priorProtectedFiles=len(conserved),persistentSettingAdopted=False,settingsSavedThisTask=[],publication='Earlier user-confirmed; independently unobserved. No new save/publication.',nativeRootCause='UNCONFIRMED',directive='EXPERIMENTAL_NOT_ADOPTED',apkIdentity=None,apkSignature=None,apkAssemblyInstallSigning='NOT_RUN',independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False))
 failureList=''.join('- `'+t['testClass']+'.'+t['method']+'`\n' for t in failed) if failed else 'None in this selected runtime.\n'
 nextTask='Review every FAILED_METHODS.json identity and full preserved failure,group affected full classes,and reproduce or prove exact historical scope before correction. Preserve all original assertions/hashes and actual-current behavior; do not replace a whole class with old fixtures or claim focused results turn this run green.' if failed else 'Reconcile this host baseline against the independent work order and remaining substantive content/assessment corrections. Twenty-five policies retain print-case singleton; qualified linguistic/source-rights/mapping/owner review and remaining audit inspection are outstanding. Do not launch another multi-hour run without a justified change or concern.'
 text=f'''# Integrated Unit7 broad selected-host regression completed

Actual tested source **{EXPECTED}**. Started `{context['startedUtc']}`, finished `{result['finishedUtc']}` (timestamps UTC). Actual Gradle exit{result['exitCode']}; runner exit{completed['runnerExit']}. **{counts['reported']} reported / {counts['passed']} passed / {counts['failed']} failed / {counts['errors']} errors / {counts['skipped']} skipped**, {result['classes']} classes. This is one selected debug-host regression, not complete app/curriculum/device acceptance or independent audit closure.

Fresh XML, unique result events/outcomes and all1380discovery identities agree exactly; no missing/extra method. Six input trees match fresh discovery and the tested source. All1378earlier identities are retained plus2new current Unit7placement methods; no removals. Discovery was1380synthetic skips/zero runtime passes. No --tests selectors in this runtime. RUNTIME_CONTEXT.json/RUNTIME_RESULT.json preserve exact source/root/argv/activation/initializers/resources/task/cache results. RAW_BROAD.zip and its member index preserve complete raw discovery/runtime XML/events/logs/lint/GC and actual supervisor exits. Read-only verify_saved_broad.py checks SHA/CRC,source,method sets,freshness,outcomes and conservation,executes zero tests.

Four screenshot exporters/16methods remain excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. No extra exclusions or WRITE_*flags. AcceptanceBoundaryTest.at020_physicalRebootPersistsGovernedRecords remains skipped,never accepted; SKIPPED_METHODS.json records every actual skip. Host counts include legacy/synthetic/history scopes and are not proof every shipped flow or curriculum claim is verified.

OracleJDK21.0.12.1+1-LTS-4/Gradle9.6/offline/API33Robolectric/two6GiBforks/forkEvery4/max-workers2. The isolated `-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare` changes wrapper compilation,excludes no tests and remains EXPERIMENTAL_NOT_ADOPTED. Native root causeUNCONFIRMED; previous fatal/replay evidence preserved. No saved setting or publication change. Previous publication user-confirmed,independently unobserved.

Lint severities `{json.dumps(result['lintIssuesBySeverity'],sort_keys=True)}`. Exact task/cache statuses `{json.dumps(result['tasks'],sort_keys=True)}`. Gate attachmentPASS is not release approval. No APK assembly/install/signing; APK identity/signature null. All155tested generated record identities,original66archives,two additional genuine captures,original historical assertions and prior evidence are conserved. Current-record hashes are bound to this exact tested Git source so later authorized authentic regeneration does not invalidate historical verification. Immutable old evidence remains checked against live bytes.

Earlier a5cafbbe broad remains1378reported/1374passed/3failed/1physicalskip,exit1. Separate df16da98full-class follow-up35reported/34passed/1newsetupfailure and e5cffa70corrected9/9 remain preserved,unsummed. Earlier integrated531fa3e9cohort124reported/121passed/2failed/1physicalskip and separate3bf0a0dd14/14follow-up remain separate. Prior broadd7067532had1365reported/1364passed/1physicalskip; the present run stands on its own. Original Unit7baseline38542882had2reported/1pass/1intended failure; firstcore2e2ebceb10pass/1API-test failure and separatec4477d2711/11remain preserved. Historical mainff539220563reported/562passed/1skip was not complete-suite;420f41ff's691was a different source inventory/unverified handoff execution,with67focused methods verified. No overlapping totals are summed or retroactively relabelled.

All48independent findings remain OPEN/auditINCOMPLETE. Source bindings are current but do not establish qualified linguistic/rights/mapping/owner acceptance. Twenty-five cumulative policies retain repeated print singleton (3,4,8–30); CC020notclosed. Preserve CC048authority conflict and original Unit7restore causeUNCONFIRMED. Final independent comprehensive nonphysical audit FIRST; actual GalaxyS26Ultraairplane-mode/rebootacceptanceLAST/outstanding. No merge,freeze,formal advancement or release approval. PrivatePenguinoutsideapp/assets/auditpackets; no private reference collected.

Failed/error methods ({len(failed)}):

{failureList}
Next large task: {nextTask}
'''
 (D/'REPORT.md').write_text(text);(D/'NEXT_TASK.md').write_text('# Next large task after verified broad scope\n\n'+nextTask+'\n\nRead REPORT.md,exactRUNTIME records and FAILED_METHODS.json first. Existing one-agent checkout/DraftPR64 only. Check processes before any launch. All48OPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/formaladvancement/approval.\n')
 (D/'STATUS.md').write_text('# Completed selected-host runtime\n\nRead REPORT.md and NEXT_TASK.md. No active test job is required by this completed checkpoint. All48OPEN/auditINCOMPLETE/physicalLAST.\n')
 # Stage every collected member before verifying fresh-checkout completeness.
 manifest();cmd(['git','add',str(REL)]);verification=json.loads(cmd(['python3',str(D/'verify_saved_broad.py')]));save(D/'PERSISTED_VERIFICATION.json',verification);manifest();cmd(['git','add',str(REL)]);cmd(['python3',str(D/'verify_saved_broad.py')])
 for file,link in [(R/'AGENTS.md',str(REL/'REPORT.md')),(R/pointers[1],'../gpt61-sol-high-comprehensive-2026-10-19/engineering-unit7-reconciled-broad-2026-10-24/REPORT.md'),(R/pointers[2],'../gpt61-sol-high-comprehensive-2026-10-19/engineering-unit7-reconciled-broad-2026-10-24/REPORT.md')]:
  first,rest=file.read_text().split('\n',1);file.write_text(first+'\n\nCurrent checkpoint: [Integrated Unit7 broad selected-host scope completed]('+link+f'). Tested{EXPECTED[:8]}:{counts["reported"]}reported/{counts["passed"]}passed/{counts["failed"]}failed/{counts["errors"]}errors/{counts["skipped"]}skipped,233classes,exit{result["exitCode"]}. Fresh XML/events/discovery/six trees verified; full raw evidence/assertions/history conserved. Read REPORT.md/NEXT_TASK.md. Selected host scope,not complete acceptance; four exporters excluded; all48OPEN/physicalLAST/noAPK/signing/merge/freeze/approval.\n'+rest)
 cmd(['git','diff','--check']);cmd(['git','add',str(REL),*pointers]);cmd(['git','diff','--cached','--check']);staged=cmd(['git','diff','--cached','--name-only']).splitlines();assert all(p.startswith(str(REL)+'/') or p in pointers for p in staged)
 assert cmd(['git','rev-parse','HEAD'])==EXPECTED
 cmd(['git','commit','-m','Preserve verified integrated Unit7 broad host regression outcomes'])
 commit=cmd(['git','rev-parse','HEAD']);state('COMMITTED_PENDING_PUSH',commit=commit,counts=counts)
 subprocess.run(['git','push','origin','HEAD:cursor/content-audit-corrections-964f'],cwd=R,check=True)
 for attempt in range(12):
  pr=json.loads(cmd(['gh','api','repos/justinkulka-lang/russian-companion/pulls/64']));assert pr['draft'] and pr['state']=='open'
  if pr['head']['sha']==commit:break
  time.sleep(5)
 assert pr['head']['sha']==commit
 prefix=f'''Integrated Unit7selected-host broad run completed at`{EXPECTED}`; evidence commit`{commit}`. **{counts['reported']}reported/{counts['passed']}passed/{counts['failed']}failed/{counts['errors']}errors/{counts['skipped']}skipped,233classes,exit{result['exitCode']}.** Fresh XML/events/discovery/six trees verified; full raw results and historical evidence conserved. This is not complete app/curriculum/device acceptance. Four exporters/16methods excluded; actualphysical acceptance remains LAST.\n\nReport `{REL}/REPORT.md`; exact next task in NEXT_TASK.md. All48findings OPEN/auditINCOMPLETE. Twenty-fiveprint-singletonpolicies and qualified-review gaps remain. NativecauseUNCONFIRMED/directiveexperimental/no persistentsettings change. NoAPK/signing/merge/freeze/formaladvancement/releaseapproval. Earlier results below remain historical and unsummed.\n\n---\n\n'''
 body=W/'PR64_COMPLETED_BODY.md';body.write_text(prefix+pr['body']);cmd(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(body),'--jq','.head.sha'])
 cmd(['python3',str(D/'verify_saved_broad.py')]);assert not cmd(['git','status','--porcelain'])
 state('PUSHED_AND_PR_UPDATED',commit=commit,counts=counts,sourceCommit=EXPECTED,completeSuite=False)
except BaseException:
 (W/'COLLECTOR_ERROR.txt').write_text(traceback.format_exc());state('STOPPED_WITH_ERROR',errorFile=str(W/'COLLECTOR_ERROR.txt'));raise
