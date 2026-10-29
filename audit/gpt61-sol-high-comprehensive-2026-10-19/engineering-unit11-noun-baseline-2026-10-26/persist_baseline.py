"""Preserve a completed Unit11 baseline and genuine producer capture; never rerun tests."""
from pathlib import Path
import subprocess,json,hashlib,zipfile,shutil,re,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path(__file__).parent;B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-unit11-noun-baseline-2026-10-26';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
r=json.loads((W/'REGRESSION/RESULT.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());done=json.loads((W/'REGRESSION_COMPLETION.json').read_text());S=c['sourceCommit'];assert not git('status','--porcelain') and git('rev-parse','HEAD')==S==r['sourceCommit']==done['sourceCommit'];assert done['resultExists'] and done['manifestExists'] and done['runnerExit']==r['exitCode']==int(done['gradleExit']);assert r['testCounts']['reported']==2 and r['classes']==1
prep=json.loads((W/'PREPARATION.json').read_text());assert git('rev-parse',S+'^')==prep['baselineParent'];assert sha(R/prep['newTestFile'])==prep['newTestSha256']
for n,h in prep['preexistingTestFiles'].items():assert sha(R/n)==h
for n,h in prep['unchangedTrees'].items():assert git('rev-parse',S+':'+n)==h
assert not (D/'RAW_BASELINE.zip').exists();files={}
for mode in ['DISCOVERY','REGRESSION']:
 for p in (W/mode).rglob('*'):
  if p.is_file() and p.relative_to(W/mode).as_posix() not in ['CAPTURE/PARTIAL_UNIT11_V1.zip','CAPTURE/IDENTITY.json']:files[p.relative_to(W).as_posix()]=p
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=W/(mode+'_'+suffix)
for n in ['run.py','resources.gradle','supervise.py','durable_monitor.py','PREPARATION.json']:files[n]=W/n
for n in ['DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json','TOOLCHAIN.txt']:
 if (W/n).exists():files[n]=W/n
for n in c['initScriptSha256']:files['INIT/'+Path(n).name]=Path(n)
index=[]
with zipfile.ZipFile(D/'RAW_BASELINE.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,p in sorted(files.items()):assert not n.lower().endswith('.pdf');z.write(p,n);index.append(dict(path=n,bytes=p.stat().st_size,sha256=sha(p)))
save('RAW_BASELINE_INDEX.json',index)
cap=W/'REGRESSION/CAPTURE';assert (cap/'PARTIAL_UNIT11_V1.zip').exists() and (cap/'IDENTITY.json').exists(),'Raw outcomes preserved but genuine capture absent; investigate actual positive result'
shutil.copy2(cap/'PARTIAL_UNIT11_V1.zip',D/'BASELINE_CURRENT_UNIT11_V1.zip');shutil.copy2(cap/'IDENTITY.json',D/'CAPTURE_IDENTITY.json');identity=json.loads((D/'CAPTURE_IDENTITY.json').read_text());assert identity['producerCommit']==S
with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip','x',zipfile.ZIP_DEFLATED) as z:z.write(R/prep['newTestFile'],prep['newTestFile'])
prior=json.loads((B/'engineering-unit10-source-reconciliation-2026-10-25/CONSERVATION.json').read_text());assert len(prior['archives'])==71
for n,h in prior['archives'].items():
 assert sha(R/n)==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
records=json.loads((B/'engineering-unit10-source-reconciliation-2026-10-25/APPLIED_RECORDS.json').read_text())['currentRecordHashes'];assert len(records)==155
for n,h in records.items():assert sha(R/n)==h
save('CURRENT_RECORD_HASHES.json',records);save('CONSERVATION.json',dict(sourceCommit=S,inputTrees=c['inputTrees'],productionUnchangedFrom=prep['baselineParent'],priorArchives=prior['archives'],priorArchiveCount=71,newUnit11V1Capture=identity,preexistingTestFilesUnchanged=len(prep['preexistingTestFiles']),currentGeneratedRecordsUnchanged=155,canonicalSourceDigest='072ea177f73cf7f8ac2a2eedc4afc61f42c2e55d9aeb2c6b2adaf0184cef21f6',sourceDigestBasis='Included production/source inputs unchanged from prior official Kotlin calculation; no new/approximate calculation or generators',apkIdentity=None,apkSignature=None,settingsChanged=False,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'))
xml=ET.parse(W/'REGRESSION/XML/TEST-dev.russiancompanion.v2.Unit11NounReviewBaselineTest.xml').getroot();out=xml.findtext('system-out','');observation=re.search(r'actualAdvanceWithoutNounBehavior=(true|false)',out);save('BASELINE_OBSERVATION.json',dict(sourceCommit=S,testCounts=r['testCounts'],tests=r['tests'],actualAdvanceWithoutAssignedNounBehaviorAnswer=None if not observation else observation.group(1)=='true',currentDeclaredGateBypassed=False,ordinaryPresentKnowledgeBypassed=False,proposedPolicyActivated=False,policyId=identity['policyId'],schema=identity['schema'],lint=r['lintIssuesBySeverity'],tasks=r['tasks'],completeSuite=False))
for n in ['persist_baseline.py','verify_saved_results.py']:shutil.copy2(W/n,D/n)
print(json.dumps(dict(collected=True,source=S,counts=r['testCounts'],identity=identity)))
