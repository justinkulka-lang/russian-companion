"""Read-only focused-test evidence verification; never executes Gradle/JUnit."""
from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,zipfile,subprocess,xml.etree.ElementTree as ET
D=Path(__file__).resolve().parent;R=Path('/workspace/russian-companion');read=lambda n:json.loads((D/n).read_text());sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
r=read('BINDING_REGRESSION_RESULT.json');S=r['sourceCommit'];expected={'currentDeclarationReconstructsOnlyUnit14ThenEveryGenuineEarlierPack','kernelReferenceChangesOnlyItsExactHashAndAppendsPendingEngineeringRecord'}
assert r['testCounts']==dict(reported=2,passed=2,failed=0,errors=0,skipped=0) and r['exitCode']==0
with zipfile.ZipFile(D/'RAW_BINDING_CHECKS.zip') as z:
 assert z.testzip() is None;idx=read('RAW_BINDING_CHECKS_INDEX.json');assert set(z.namelist())==set(idx)
 for n,v in idx.items():assert sha(z.read(n))==v['sha256'] and len(z.read(n))==v['bytes'],n
 for mode in ['DISCOVERY','REGRESSION']:
  c=read('BINDING_'+mode+'_CONTEXT.json');s=read('BINDING_'+mode+'_RESULT.json');assert c==json.loads(z.read(mode+'/CONTEXT.json')) and s==json.loads(z.read(mode+'/RESULT.json'))
  assert c['sourceCommit']==s['sourceCommit']==S and c['sourceRootTree']==git('rev-parse',S+'^{tree}') and c['inputTrees']==s['inputTrees'] and c['workingTreeAtStart']=='CLEAN'
  for n,h in c['inputTrees'].items():assert git('rev-parse',S+':'+n)==h and git('rev-parse','HEAD:'+n)==h,n
  for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h,n
  assert sha(z.read('RUNNER/run.py'))==c['runnerSha256']
  xmlnames=[n for n in z.namelist() if n.startswith(mode+'/XML/') and n.endswith('.xml')];assert len(xmlnames)==1
  suite=ET.fromstring(z.read(xmlnames[0]));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;assert stamp.timestamp()>=datetime.fromisoformat(c['startedUtc']).timestamp()-1
  rows=suite.findall('testcase');assert len(rows)==2 and {t.get('name') for t in rows}==expected
  assert {t.get('classname') for t in rows}=={'dev.russiancompanion.v2.Unit14BindingConservationTest'}
  status='SKIPPED' if mode=='DISCOVERY' else 'SUCCESS';events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').splitlines()];results=[x for x in events if x['kind']=='RESULT'];assert len(results)==2 and {x['name'] for x in results}==expected and all(x['resultType']==status for x in results)
  for t in rows:assert t.find('failure') is None and t.find('error') is None and (t.find('skipped') is not None)==(mode=='DISCOVERY')
  assert s['testCounts']==dict(reported=2,passed=0,failed=0,errors=0,skipped=2) if mode=='DISCOVERY' else s['testCounts']==dict(reported=2,passed=2,failed=0,errors=0,skipped=0)
  assert int(z.read(mode+'/RUN.exit'))==s['exitCode']==json.loads(z.read(mode+'_COMPLETION.json'))['runnerExit']==0
  for n,h in read('BINDING_'+mode+'_SHA256.json').items():assert sha(z.read(mode+'/'+n))==h,n
  assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
 issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue');assert r['lintIssuesBySeverity']=={s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}
 assert r['lintIssuesBySeverity']['Fatal']==r['lintIssuesBySeverity']['Error']==0
 log=z.read('REGRESSION/RUN.log').decode();assert '> Task :app:verifyReleaseGateAttachment\n' in log and '> Task :app:verifyReleaseGateAttachment FAILED' not in log and 'BUILD SUCCESSFUL' in log
assert r['discoveryComparison']['exactSameMethodSet'] and r['discoveryComparison']['sameInputTrees'] and not r['completeSuite']
if (D/'SHA256.json').exists():
 m=read('SHA256.json')
 for n,h in m.items():assert sha((D/n).read_bytes())==h,n
print(json.dumps(dict(verified=True,sourceCommit=S,counts=r['testCounts'],classes=1,exitCode=0,lint=r['lintIssuesBySeverity'],gateAttachment='PASSED_NOT_RELEASE_APPROVAL',fullAffectedScope='87CLASS_INVENTORY_NOT_LAUNCHED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',newJUnitByVerifier=0)))
