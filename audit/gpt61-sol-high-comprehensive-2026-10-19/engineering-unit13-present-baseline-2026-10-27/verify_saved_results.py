"""Read-only verification of saved Unit13 baseline, distinct from running app tests."""
from pathlib import Path
import argparse,hashlib,json,subprocess,xml.etree.ElementTree as ET,zipfile
D=Path(__file__).resolve().parent;R=Path('/workspace/russian-companion')
args=argparse.ArgumentParser();args.add_argument('--current',action='store_true');current=args.parse_args().current
sha=lambda b:hashlib.sha256(b).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
read=lambda n:json.loads((D/n).read_text())
for n,h in read('SHA256.json').items():assert sha((D/n).read_bytes())==h,n
prep=read('PREPARATION.json');con=read('CONSERVATION.json');cap=read('CAPTURE_VERIFICATION.json');S=con['sourceCommit'];base=con['baselineSourceCommit']
assert cap['producerCommit']==S
for p,h in con['inputTrees'].items():assert git('rev-parse',S+':'+p)==h,p
for p,h in prep['inputTrees'].items():
 if p!='app/src/test':assert h==con['inputTrees'][p],p
new=prep['newTestFile'];assert git('diff','--name-only',base,S,'--','app/src/test').splitlines()==[new]
assert sha(subprocess.check_output(['git','show',S+':'+new],cwd=R))==prep['newTestSha256']
assert len(git('ls-tree','-r','--name-only',base,'app/src/test').splitlines())==271
assert len(git('ls-tree','-r','--name-only',S,'app/src/test').splitlines())==272
assert not git('diff','--name-only',base,S,'--','app/src/main','catalog','docs','review','release')
with zipfile.ZipFile(D/'RAW_BASELINE.zip') as z:
 assert z.testzip() is None;idx=read('RAW_BASELINE_INDEX.json');assert set(z.namelist())=={x['path'] for x in idx}
 for x in idx:assert sha(z.read(x['path']))==x['sha256'] and len(z.read(x['path']))==x['bytes'],x['path']
 for mode in ['DISCOVERY','REGRESSION']:
  r=read(mode+'_RESULT.json');c=read(mode+'_CONTEXT.json');assert r['sourceCommit']==c['sourceCommit']==S
  assert c['sourceRootTree']==git('rev-parse',S+'^{tree}') and c['workingTreeAtStart']=='CLEAN'
  assert r['inputTrees']==c['inputTrees']==con['inputTrees']
  assert int(z.read(mode+'/RUN.exit'))==r['exitCode']
  for n,h in read(mode+'_SHA256.json').items():assert sha(z.read(mode+'/'+n))==h,(mode,n)
  events=[json.loads(s) for s in z.read(mode+'/EVENTS.jsonl').decode().splitlines()];er=[x for x in events if x['kind']=='RESULT']
  xmlnames=[n for n in z.namelist() if n.startswith(mode+'/XML/') and n.endswith('.xml')];assert len(xmlnames)==1
  suite=ET.fromstring(z.read(xmlnames[0]));tests=suite.findall('testcase');assert len(tests)==len(er)==r['testCounts']['reported']==2
  assert sorted((x.get('classname'),x.get('name')) for x in tests)==sorted((x['className'],x['name']) for x in er)
  for t in tests:
   name=t.get('name');row=next(x for x in r['tests'] if x['method']==name)
   failure=t.find('failure');error=t.find('error');skip=t.find('skipped');status='FAILED' if failure is not None else 'ERROR' if error is not None else 'SKIPPED' if skip is not None else 'PASSED'
   assert status==row['outcome'];msg=(failure if failure is not None else error if error is not None else skip)
   message=msg.get('message','') if msg is not None else '';assert sha(message.encode())==row['fullMessageSha256']
   event=next(x for x in er if x['name']==name);assert event['resultType']=={'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[status]
  expected=dict(reported=2,passed=0,failed=0,errors=0,skipped=2) if mode=='DISCOVERY' else dict(reported=2,passed=1,failed=1,errors=0,skipped=0)
  assert r['testCounts']==expected and r['exitCode']==(0 if mode=='DISCOVERY' else 1)
  assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
  assert c['completeSuite']==r['completeSuite']==False
  if mode=='REGRESSION':
   assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
   assert 'actualAdvanceWithoutPresent=true' in suite.findtext('system-out','')
   failure=next(x for x in tests if x.find('failure') is not None)
   assert failure.get('name')=='priorPrintAspectAndPastAnswersCannotReplaceFreshAssignedPresentAnswer'
   assert 'The proposed Unit13 obligation must refuse advancement without its fresh assigned present-form answer' in failure.find('failure').get('message')
   assert z.read('REGRESSION/CAPTURE/COMPLETED_UNIT13_V2.json')==(D/'COMPLETED_UNIT13_V2.json').read_bytes()
   assert z.read('REGRESSION/CAPTURE/PARTIAL_UNIT13_V2.zip')==(D/'BASELINE_CURRENT_UNIT13_V2.zip').read_bytes()
   lint=ET.fromstring(z.read('REGRESSION/LINT.xml'));issues=lint.findall('issue')
   assert r['lintIssuesBySeverity']=={s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}
   assert r['lintIssuesBySeverity']['Fatal']==r['lintIssuesBySeverity']['Error']==0
   log=z.read('REGRESSION/RUN.log').decode();assert '> Task :app:verifyReleaseGateAttachment\n' in log and '> Task :app:verifyReleaseGateAttachment FAILED' not in log
archive=D/'BASELINE_CURRENT_UNIT13_V2.zip';assert sha(archive.read_bytes())==cap['zipSha256'] and archive.stat().st_size==cap['zipBytes']
with zipfile.ZipFile(archive) as z:
 assert z.testzip() is None
 for x in cap['members']:
  i=z.getinfo(x['path']);assert sha(z.read(i))==x['sha256'] and i.file_size==x['bytes'] and f'{i.CRC:08x}'==x['crc32']
 m=json.loads(z.read('manifest.json'));d=json.loads(z.read('learner.json'));assert m['formatVersion']==3
 for f in m['files']:assert len(z.read(f['path']))==f['bytes'] and sha(z.read(f['path']))==f['sha256']
 a=next(x for x in d['reviewAssignments'] if x['targetUnit']==13);assert a==cap['assignment'] and a['schema']==6
 assert d['recordings']==[] and m['recordingCount']==0
 assert [x for x in d['reviewAnswers'] if x['assignmentId']==a['id']]==cap['partialAnswers']
 assert {x['slotIndex'] for x in cap['partialAnswers']}=={0}
 assert cap['answerRecordSchema']==1 and all(x['schema']==1 for x in cap['partialAnswers'])
 assert [x['ownerUnit'] for x in a['slots']]==[1,12,11]
 assert sorted(x['unitNumber'] for x in d['unitCompletions'])==list(range(1,13))
completed=read('COMPLETED_UNIT13_V2.json');assert completed['profile']['currentUnitNumber']==14
assert sorted(x['unitNumber'] for x in completed['unitCompletions'])==list(range(1,14))
assert next(x for x in completed['unitCompletions'] if x['unitNumber']==13)==cap['completion']
assert [x for x in completed['reviewAnswers'] if x['assignmentId']==a['id']]==cap['completedAnswers']
assert {x['slotIndex'] for x in cap['completedAnswers']}=={0,1,2}
with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip') as z:
 assert z.testzip() is None and z.namelist()==[new] and sha(z.read(new))==prep['newTestSha256']
protected=read('PROTECTED_EVIDENCE_HASHES.json');archiveHashes=read('PRIOR_ARCHIVE_HASHES.json');archives=list(archiveHashes);assert len(archives)==73
assert all(protected[p]==h for p,h in archiveHashes.items())
if current:
 for p,h in protected.items():assert sha((R/p).read_bytes())==h,p
 assert sha((R/new).read_bytes())==prep['newTestSha256']
 for p,h in con['inputTrees'].items():assert git('rev-parse','HEAD:'+p)==h,p
 for p in archives:
  with zipfile.ZipFile(R/p) as z:assert z.testzip() is None,p
assert con['all48Findings']=='OPEN' and con['audit']=='INCOMPLETE' and con['physicalAcceptance']=='OUTSTANDING_LAST'
assert con['proposedV3Schema15']=='NOT_ACTIVATED' and con['apkIdentity'] is None and con['apkSignature'] is None
print(json.dumps(dict(verified=True,sourceCommit=S,currentConservation=current,actualSelectedMethods=2,passed=1,intentionalFutureRequirementFailures=1,errors=0,skipped=0,exitCode=1,captureSha256=cap['zipSha256'],captureBytes=cap['zipBytes'],assignmentSchema=6,backupFormat=3,partialSlotsAnswered=[0],recordings=0,actualAdvanceTo=14,priorArchivesConserved=73,priorTestFilesConserved=271,bindingsConserved=155,proposal='NOT_ACTIVATED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False)))
