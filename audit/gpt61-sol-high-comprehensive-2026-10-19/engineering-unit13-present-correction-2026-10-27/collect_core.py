"""Preserve actual complete selected run, including all failures; never runs tests."""
from pathlib import Path
import json,hashlib,subprocess,zipfile,shutil,re
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-present-correction-2026-10-27'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
def save(n,v):(D/n).write_text(json.dumps(v,indent=2)+'\n')
assert not git('status','--porcelain')
prep=json.loads((D/'PREPARATION.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());S=c['sourceCommit'];assert S==git('rev-parse','HEAD')==r['sourceCommit']
files={}
for mode in ['DISCOVERY','REGRESSION']:
 done=json.loads((W/(mode+'_COMPLETION.json')).read_text());result=json.loads((W/mode/'RESULT.json').read_text())
 assert done['sourceCommit']==result['sourceCommit']==S and done['runnerExit']==result['exitCode']==int(done['gradleExit'])
 assert done['resultExists'] and done['manifestExists']
 for n,h in json.loads((W/mode/'SHA256.json').read_text()).items():assert sha(W/mode/n)==h,(mode,n)
 for p in (W/mode).rglob('*'):
  if p.is_file():files[p.relative_to(W).as_posix()]=p
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:
  p=W/(mode+'_'+suffix);assert p.is_file();files[p.name]=p
for n in ['implement_core.py','create_tests.py','prepare.py','run.py','resources.gradle','launch.py','supervise.py']:files['TOOLING/'+n]=W/n
for n,h in c['initScriptSha256'].items():
 p=Path(n);assert sha(p)==h;files['INIT/'+p.name]=p
files['TOOLING/PREPARATION_COMMANDS.json']=W/'PREPARATION_COMMANDS.json'
index=[]
with zipfile.ZipFile(D/'RAW_CORE.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,p in sorted(files.items()):
  assert not n.lower().endswith('.pdf');z.write(p,n);index.append(dict(path=n,bytes=p.stat().st_size,sha256=sha(p)))
save('RAW_CORE_INDEX.json',index)
# Preserve all outcomes before any success/conservation assertions.
failed=[x for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']]
save('ACTUAL_FAILURES.json',dict(sourceCommit=S,tests=failed,count=len(failed),originalAssertionsPreserved=True))
assert r['testCounts']['reported']==17 and r['classes']==3 and r['selectedFreshXmlAndEventsMatch'] and r['eventOutcomeCountsMatchXml']
assert r['discoveryComparison']['exactSameMethodSet'] and r['discoveryComparison']['sameInputTrees']
protected=json.loads((D/'PROTECTED_EVIDENCE_HASHES.json').read_text())
for n,h in protected.items():assert sha(R/n)==h,n
for n,h in prep['existingTestFileHashes'].items():assert sha(R/n)==h,n
for n,h in prep['newTestFiles'].items():assert sha(R/n)==h,n
for n,h in prep['unchangedTrees'].items():assert c['inputTrees'][n]==h,n
p='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
objects={m[1]:m[0] for m in re.findall(r'(internal object (\w+CumulativeReviewPolicy) \{.*?\n\})',(R/p).read_text(),re.S)};assert len(objects)==40
for n,h in json.loads((D/'OLD_POLICY_OBJECT_HASHES.json').read_text()).items():assert hashlib.sha256(objects[n].encode()).hexdigest()==h,n
archives=json.loads((D/'PRIOR_ARCHIVE_HASHES.json').read_text());assert len(archives)==74
for n,h in archives.items():
 assert sha(R/n)==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
records=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-source-reconciliation-2026-10-27/APPLIED_RECORDS.json').read_text())['currentRecordHashes']
assert len(records)==155 and all(sha(R/n)==h for n,h in records.items())
save('SOURCE_BINDING_HASHES.json',records)
save('CONSERVATION.json',dict(sourceCommit=S,baselineParent=prep['baselineParent'],inputTrees=c['inputTrees'],priorTestFilesUnchanged=272,wholeUnit13BaselineUnchanged=True,oldPolicyObjectsUnchanged=39,newPolicyObjects=1,oldSchemas=list(range(1,15)),newSchema=15,priorArchivesShaCrc=74,sourceBindingsUnchanged=155,sourceBindingsStatus='STALE_UNCHANGED_NOT_REGENERATED',protectedFiles=len(protected),all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None,environmentSettingsChanged=False))
observation=W/'REGRESSION/CURRENT_POLICY_OBSERVATION.json'
if observation.exists():
 o=json.loads(observation.read_text());assert (o['activePolicies'],o['pools'],o['placements'],o['uniqueCandidates'],o['printSingletonPolicies'])==(28,59,112,52,19)
 assert o['unit13BeforeV2Digest']=='7102c47201a3d4973d781d315dbbb8ceccca3424174b9c90ad127bac037828df'
 o['sourceCommit']=S;o['producer']='Actual compiled V2Validator and policy objects in exactPriorDeclarationsRefuseNewTasksAndPreserveBothUnit14OwnerBindings';save('CURRENT_POLICY_OBSERVATION.json',o)
shutil.copy2(Path(__file__),D/'collect_core.py')
print(json.dumps(dict(source=S,counts=r['testCounts'],classes=r['classes'],gradleExit=r['exitCode'],lint=r['lintIssuesBySeverity'],failures=[x['method'] for x in failed],officialDigestObserved=observation.exists(),oldTests=272,oldObjects=39,priorArchives=74,bindings='155_STALE_UNCHANGED')))
