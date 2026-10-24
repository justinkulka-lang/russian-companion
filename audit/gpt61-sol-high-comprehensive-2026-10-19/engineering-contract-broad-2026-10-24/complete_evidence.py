"""Monitor the detached actual runtime; verify and preserve its selected scope before publishing evidence."""
from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,zipfile,shutil,time,os,traceback
W=Path(__file__).parent;R=Path('/workspace/russian-companion')
REL=Path('audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-contract-broad-2026-10-24');D=R/REL
EXPECTED='d70675321a043688cf270963ab1e84d99a77c193'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
now=lambda:datetime.now(timezone.utc).isoformat()
def save(p,v):p.write_text(json.dumps(v,indent=2)+'\n')
def cmd(args):return subprocess.check_output(args,cwd=R,text=True).strip()
def state(value,**kw):save(W/'COLLECTOR_STATUS.json',dict(state=value,utc=now(),**kw))
def manifest():save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p!=D/'SHA256.json'})
try:
 assert not D.exists(),'Never overwrite an evidence attempt'
 launcher=json.loads((W/'REGRESSION_LAUNCHER.json').read_text());assert launcher['sourceCommit']==EXPECTED
 state('WAITING_FOR_RUNNER',sourceCommit=EXPECTED,supervisorPid=launcher['pid'])
 while not (W/'REGRESSION_COMPLETION.json').exists():
  proc=Path('/proc')/str(launcher['pid'])/'cmdline'
  assert proc.exists() and b'supervise.py' in proc.read_bytes(),'Supervisor exited without completion; preserve raw evidence, no relaunch'
  time.sleep(30)
 completed=json.loads((W/'REGRESSION_COMPLETION.json').read_text())
 assert completed['sourceCommit']==EXPECTED
 if not completed['resultExists'] or not completed['manifestExists']:
  state('INCOMPLETE_HARNESS_RESULT',completion=completed,rawEvidence=str(W/'REGRESSION'),action='Preserve raw evidence and investigate; never substitute stale XML or invent complete counts')
  raise RuntimeError('No validated complete RESULT/manifest; live agent must preserve incomplete evidence separately')
 O=W/'REGRESSION';result=json.loads((O/'RESULT.json').read_text());context=json.loads((O/'CONTEXT.json').read_text())
 assert result['sourceCommit']==context['sourceCommit']==EXPECTED
 assert cmd(['git','rev-parse','HEAD'])==EXPECTED and not cmd(['git','status','--porcelain'])
 assert result['testCounts']['reported']==1365 and result['classes']==229
 assert completed['runnerExit']==result['exitCode']==int((O/'RUN.exit').read_text())==int(completed['gradleExit'])
 assert result['exitCode'] in [0,1]
 state('COLLECTING_VERIFIED_COMPLETION',sourceCommit=EXPECTED)
 # Protect every tracked audit byte, the existing generated records and original histories.
 prior=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-regression-contract-reconciliation-2026-10-24'
 conserved=json.loads((prior/'PROTECTED_BASELINE.json').read_text())
 for name in cmd(['git','ls-files','audit']).splitlines():
  p=R/name
  if p.is_file() and name not in ['audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:conserved[name]=sha(p)
 for name,value in conserved.items():assert sha(R/name)==value,name
 # Reverify the pre-existing evidence, including originals/all assertions/capture/proof.
 verification=cmd(['python3',str(prior/'verify_saved_contracts.py')]);json.loads(verification)
 D.mkdir()
 save(D/'CONSERVED_INPUTS.json',conserved)
 save(D/'PRIOR_CONTRACT_VERIFICATION.json',json.loads(verification))
 files={}
 for mode in ['DISCOVERY','REGRESSION']:
  for p in (W/mode).rglob('*'):
   if p.is_file():files[mode+'/'+p.relative_to(W/mode).as_posix()]=p
 for name in ['run.py','resources.gradle','supervise.py','launch_mode.py','REGRESSION_LAUNCHER.json','REGRESSION_RUNNER_PROCESS.json','REGRESSION_LAUNCHER.exit','REGRESSION_COMPLETION.json','REGRESSION_RUNNER.log','REGRESSION_LAUNCHER.log']:
  files[name]=W/name
 for name in context['initScriptSha256']:files['INIT/'+Path(name).name]=Path(name)
 index=[]
 with zipfile.ZipFile(D/'RAW_BROAD.zip','x',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for name,p in sorted(files.items()):
   assert p.is_file() and not p.name.lower().endswith('.pdf')
   index.append(dict(path=name,bytes=p.stat().st_size,sha256=sha(p)));z.write(p,name)
 save(D/'RAW_BROAD_INDEX.json',index)
 for name in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(O/name,D/('RUNTIME_'+name))
 for name in ['verify_saved_broad.py','complete_evidence.py']:shutil.copy2(W/name,D/name)
 save(D/'FAILED_METHODS.json',[t for t in result['tests'] if t['outcome'] in ['FAILED','ERROR']])
 save(D/'SKIPPED_METHODS.json',[t for t in result['tests'] if t['outcome']=='SKIPPED'])
 counts=result['testCounts'];failed=[t['testClass']+'.'+t['method'] for t in result['tests'] if t['outcome'] in ['FAILED','ERROR']]
 save(D/'CONSERVATION.json',dict(sixInputTrees=context['inputTrees'],currentGeneratedRecords=155,canonicalSourceDigest='2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892',digestBasis='Same six trees as preceding official Kotlin computation saved in FINAL_CONSERVATION.json; no approximate recomputation.',originalArchivesShaCrc=66,protectedFiles=len(conserved),apkIdentity=None,apkSignature=None,apkAssemblyInstallSigning='NOT_RUN',settingsSavedThisTask=[],publication='Earlier user-confirmed; independently unobserved. No new publication or persistent setup change.',nativeRootCause='UNCONFIRMED',directive='EXPERIMENTAL_NOT_ADOPTED',independentAudit='INCOMPLETE_ALL48OPEN',physicalAcceptance='OUTSTANDING_LAST'))
 text=f'''# Fresh broad selected-host regression after contract reconciliation

Actual runtime source `{EXPECTED}`. Started `{context['startedUtc']}`, finished `{result['finishedUtc']}`. Actual Gradle exit {result['exitCode']}; runner exit {completed['runnerExit']}. **{counts['reported']} methods / {result['classes']} classes: {counts['passed']} passed, {counts['failed']} failed, {counts['errors']} errors, {counts['skipped']} skipped**. This is a single selected debug-host regression, not complete app/curriculum/device acceptance or independent closure. Never add overlapping focused/partial attempts to these counts.

Fresh XML, unique events and outcomes agree and all1365 discovery identities occur exactly once; all six input trees match. Full commands/source/root/scripts/resources/task/cache results are in RUNTIME_CONTEXT.json and RUNTIME_RESULT.json. RAW_BROAD.zip preserves raw discovery/runtime XML/events/logs/lint/GC and SHA/member/CRC identities. Discovery at a6e06bd5 had1365synthetic skips/zero runtime passes. This fresh runtime retains all old1363identities plus exactly two meaningful contract methods; no removals.

Four exporters remain excluded: VisualReviewCaptureTest2, Units35ChunkCaptureTest2, Units610ChunkCaptureTest10, Units1115GapCaptureTest2 (16methods). No additional exclusions, method selectors or WRITE_*flags. AcceptanceBoundaryTest.at020_physicalRebootPersistsGovernedRecords is the physical skip, never acceptance.

Oracle JDK21.0.12.1+1-LTS-4, Gradle9.6 offline, two6GiBforks/forkEvery4/max-workers2/API33 host tests. The isolated `-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare` directive changes wrapper compilation only; it excludes no tests. It remains EXPERIMENTAL_NOT_ADOPTED; native root cause UNCONFIRMED. The original C2 fatal/replay and every prior failed attempt remain intact. No persistent setting was adopted, saved or published this task.

Lint severities `{json.dumps(result['lintIssuesBySeverity'],sort_keys=True)}`. Task/cache statuses `{json.dumps(result['tasks'],sort_keys=True)}`. Gate attachment PASS is not approval. No APK assembly/install/signing; APK identity/signature null. All155generated records,66original ZIPs, original assertions/hashes and additional genuine Unit6v2capture are protected by saved prior verification/CONSERVED_INPUTS. The source digest remains2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892 based on unchanged exact inputs and preceding official Kotlin computation.

Separate prior scopes: b07432aa broad1363reported/1347pass/15fail/1physicalskip; cf9415be focused56reported/54pass/1fail/1skip; a6e06bd5 focused56reported/55pass/0fail/1skip. All15 old failures passed in the latter selected nine-class scope. This fresh broad run stands on its own. Earlier e5b5d3c0 broad1334reported/1324pass/9fail/1skip and20c5eb96 focused56/56 are also separate. Historical mainff539220563reported/562passed/1skip was not complete-suite;420f41ff's691 source annotations/unverified handoff execution and67focused verified checks differ in source/scope and are not current runtime evidence.

All48 independent findings remain OPEN and audit INCOMPLETE. Qualified linguistic/source-rights/mapping/owner review, remaining substantive app/curriculum work and final independent comprehensive nonphysical audit remain outstanding. Preserve CC048 authority conflict and original Unit7 restore cause UNCONFIRMED. Actual Galaxy S26 Ultra airplane-mode/reboot acceptance stays LAST, after final comprehensive independent audit. No merge, freeze, formal advancement or release approval. Private Penguin remains outside app/assets/audit packets.

Failed/error methods ({len(failed)}):

'''+(''.join('- `'+s+'`\n' for s in failed) if failed else 'None in this selected runtime.\n')
 text+='\nNext large task: reconcile this verified host result against the independent work order and all48 open findings; identify remaining substantive app/curriculum corrections and external qualified-review requirements. If failures exist, inspect full preserved failures first and keep all assertions/historical pins/current behavior. Host success alone cannot close linguistic/rights/governance/device findings or justify release. Do not launch another multi-hour run without a new justified change or concern.\n'
 (D/'REPORT.md').write_text(text)
 (D/'STATUS.md').write_text('# Completed selected-host runtime\n\nRead REPORT.md and exact saved outcomes. No active test job is needed by this completed checkpoint. Audit INCOMPLETE/all48open/physicalLAST.\n')
 manifest();verification=cmd(['python3',str(D/'verify_saved_broad.py')]);save(D/'PERSISTED_VERIFICATION.json',json.loads(verification));manifest();cmd(['python3',str(D/'verify_saved_broad.py')])
 for file,link in [(R/'AGENTS.md',str(REL/'REPORT.md')),(R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','../gpt61-sol-high-comprehensive-2026-10-19/engineering-contract-broad-2026-10-24/REPORT.md'),(R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md','../gpt61-sol-high-comprehensive-2026-10-19/engineering-contract-broad-2026-10-24/REPORT.md')]:
  first,rest=file.read_text().split('\n',1)
  file.write_text(first+'\n\nCurrent checkpoint: [Fresh contract broad selected-host scope completed]('+link+f'). Source{EXPECTED[:8]}: {counts["reported"]}reported/{counts["passed"]}passed/{counts["failed"]}failed/{counts["errors"]}errors/{counts["skipped"]}skipped in229classes,exit{result["exitCode"]}. Fresh XML/events/discovery/six trees verified; raw failures/history/assertions preserved. Host scope only,not complete acceptance. Directive provisional/root causeUNCONFIRMED;four exporters excluded;all48open/physicalLAST/no APK/signing/merge/freeze/approval.\n'+rest)
 cmd(['git','diff','--check']);cmd(['git','add',str(REL),'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']);cmd(['git','diff','--cached','--check'])
 staged=cmd(['git','diff','--cached','--name-only']).splitlines();assert all(p.startswith(str(REL)+'/') or p in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'] for p in staged)
 assert cmd(['git','rev-parse','HEAD'])==EXPECTED
 cmd(['git','commit','-m','Preserve verified fresh contract broad host regression evidence'])
 commit=cmd(['git','rev-parse','HEAD']);state('COMMITTED_PENDING_PUSH',commit=commit,counts=counts)
 subprocess.run(['git','push','origin','HEAD:cursor/content-audit-corrections-964f'],cwd=R,check=True)
 pr=json.loads(cmd(['gh','api','repos/justinkulka-lang/russian-companion/pulls/64']));assert pr['draft'] and pr['state']=='open' and pr['head']['sha']==commit
 prefix=f'''Fresh selected-host broad run at `{EXPECTED}`; evidence commit `{commit}`. **{counts['reported']} reported: {counts['passed']} passed, {counts['failed']} failed, {counts['errors']} errors, {counts['skipped']} skipped /229classes, exit{result['exitCode']}.** Fresh XML/events/discovery and six inputs verified; raw evidence preserved. Full report `{REL}/REPORT.md`. Four exporters/16methods excluded; not complete app/curriculum/device acceptance.\n\nAll48independent findings remain OPEN; audit INCOMPLETE. Wrapper JIT directive experimental/native cause UNCONFIRMED; persistent setup not changed. Next large task is work-order/finding reconciliation. Physical Galaxy S26 Ultra acceptance LAST after final independent comprehensive audit. No APK/signing/merge/freeze/formal advancement/release approval. Earlier run results below remain historical and separate.\n\n---\n\n'''
 body=W/'PR64_COMPLETED_BODY.md';body.write_text(prefix+pr['body'])
 cmd(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(body),'--jq','.head.sha'])
 cmd(['python3',str(D/'verify_saved_broad.py')]);assert not cmd(['git','status','--porcelain'])
 state('PUSHED_AND_PR_UPDATED',commit=commit,counts=counts,sourceCommit=EXPECTED,completeSuite=False)
except BaseException:
 (W/'COLLECTOR_ERROR.txt').write_text(traceback.format_exc());state('STOPPED_WITH_ERROR',errorFile=str(W/'COLLECTOR_ERROR.txt'));raise
