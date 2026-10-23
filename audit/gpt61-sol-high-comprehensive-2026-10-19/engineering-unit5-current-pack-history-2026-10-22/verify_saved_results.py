"""Read-only verification of scoped execution and archive conservation; not audit approval."""
from pathlib import Path,PurePosixPath
from datetime import datetime,timezone
import argparse,json,hashlib,zipfile,subprocess,xml.etree.ElementTree as ET,collections
p=argparse.ArgumentParser();p.add_argument('--repository',type=Path,required=True);R=p.parse_args().repository;D=Path(__file__).resolve().parent
read=lambda p:json.loads(p.read_text());sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
def check(v,m):
 if not v:raise ValueError(m)
manifest=read(D/'SHA256_CURRENT.json');check(set(manifest)=={f.relative_to(D).as_posix() for f in D.rglob('*') if f.is_file() and f.name!='SHA256_CURRENT.json'},'complete saved manifest')
for rel,h in manifest.items():check(sha((D/rel).read_bytes())==h,'saved hash '+rel)
def archive(label):
 z=zipfile.ZipFile(D/('RAW_'+label+'.zip'));idx=read(D/('RAW_'+label+'_INDEX.json'));check(z.testzip() is None and len(z.namelist())==len(set(z.namelist()))==len(idx) and set(z.namelist())==set(idx),'archive CRC/inventory '+label)
 for rel,v in idx.items():
  path=PurePosixPath(rel);check(not path.is_absolute() and '..' not in path.parts,'safe path');b=z.read(rel);check(sha(b)==v['sha256'] and len(b)==v['bytes'],'raw bytes '+rel)
 return z
for prefix in ['INITIAL','FINAL']:
 discovery=read(D/(prefix+'_DISCOVERY')/'RESULT.json');result=read(D/(prefix+'_REGRESSION')/'RESULT.json');ctx=read(D/(prefix+'_REGRESSION')/'CONTEXT.json');expected=dict(reported=6,passed=5,failed=1,errors=0,skipped=0) if prefix=='INITIAL' else dict(reported=6,passed=6,failed=0,errors=0,skipped=0)
 check(result['testCounts']==expected and result['classes']==2 and result['exitCode']==(1 if prefix=='INITIAL' else 0),'actual scope '+prefix);check(discovery['testCounts']==dict(reported=6,passed=0,failed=0,errors=0,skipped=6),'discovery is synthetic');check(result['inputTrees']==discovery['inputTrees']==ctx['inputTrees'],'same scope inputs');check(result['sourceCommit']==discovery['sourceCommit']==ctx['sourceCommit'],'same source')
 for mode in ['DISCOVERY','REGRESSION']:
  label=prefix+'_'+mode;rr=read(D/label/'RESULT.json');cc=read(D/label/'CONTEXT.json')
  with archive(label) as z:
   events=[json.loads(l) for l in z.read('EVENTS.jsonl').decode().splitlines()];ev={(e['className'],e['name']):e['resultType'] for e in events if e['kind']=='RESULT'};xx={};files=[s for s in z.namelist() if s.startswith('XML/') and s.endswith('.xml')];check(len(files)==2,'two full classes')
   for rel in files:
    suite=ET.fromstring(z.read(rel));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;check(stamp.timestamp()>=datetime.fromisoformat(cc['startedUtc']).timestamp()-1,'fresh XML')
    for t in suite.findall('testcase'):
     key=(t.get('classname'),t.get('name'));check(key not in xx,'unique method');xx[key]='FAILURE' if t.find('failure') is not None or t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'SUCCESS'
   outcomes={'FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED','PASSED':'SUCCESS'};expectedRows={(r['testClass'],r['method']):outcomes[r['outcome']] for r in rr['tests']};check(len(xx)==6 and ev==xx==expectedRows,'exact XML/events/results');check(set(xx)=={(r['testClass'],r['method']) for r in discovery['tests']},'discovery identities');check({k[0] for k in xx}==set(cc['selectors']),'full selected classes');check(sum(cc['exclusions'].values())==16 and len(cc['exclusions'])==4 and not any(k[0].split('.')[-1] in cc['exclusions'] for k in xx),'exporter exclusions');check(int(z.read('RUN.exit'))==rr['exitCode'],'actual exit')
   if mode=='REGRESSION':
    issues=ET.fromstring(z.read('LINT.xml')).findall('issue');check({s:sum(i.get('severity')==s for i in issues) for s in ['Fatal','Error','Warning','Hint']}==dict(Fatal=0,Error=0,Warning=20,Hint=1),'lint counts');log=z.read('RUN.log').decode();check('> Task :app:verifyReleaseGateAttachment' in log.splitlines() and '> Task :app:lintDebug' in log.splitlines(),'executed lint and attachment');check(('BUILD FAILED' if prefix=='INITIAL' else 'BUILD SUCCESSFUL') in log,'original build outcome')
initial=read(D/'INITIAL_REGRESSION/RESULT.json');check([r['method'] for r in initial['tests'] if r['outcome']=='FAILED']==['allOriginalArchivesRestoreAndReopenButDoNotAcquireNewCredit'],'one initial harness failure')
with archive('INITIAL_REGRESSION') as z:
 rows=[json.loads(l) for l in z.read('HISTORY_CASES.jsonl').decode().splitlines()];check(sum(r['kind']=='original_archive' for r in rows)==16 and sum(r['kind']=='legacy_unit5_to_current_unit6' for r in rows)==1,'partial initial cases retained')
with archive('FINAL_REGRESSION') as z:check(z.read('HISTORY_CASES.jsonl')==(D/'FINAL_HISTORY_CASES.jsonl').read_bytes(),'case ledger identity')
rows=[json.loads(l) for l in (D/'FINAL_HISTORY_CASES.jsonl').read_text().splitlines()];a=[r for r in rows if r['kind']=='original_archive'];m=[r for r in rows if r['kind']=='legacy_unit5_to_current_unit6'];check(len(a)==len({r['path'] for r in a})==64 and len(m)==1,'64+1 case observations, not methods');check(collections.Counter(tuple(r['currentCompletedUnits']) for r in a)=={(1,2):24,(1,2,3):40},'24/40 expected credit');census=read(D/'CAPTURE_IDENTITY_RECONCILIATION.json');check({r['path'] for r in a}=={r['path'] for r in census['cases']},'all reconciled archives covered')
for c in census['cases']:
 row=next(r for r in a if r['path']==c['path']);check(row['sha256']==c['sha256'] and row['currentCompletedUnits']==c['expectedCurrentCompletedUnits'] and row['restoredAndReopenedUnchanged'] and row['unit6ReviewRefusedWithoutCurrentPrerequisites'],'case outcomes');check(sha((R/c['path']).read_bytes())==c['sha256'],'original archive unchanged')
 with zipfile.ZipFile(R/c['path']) as z:
  doc=json.loads(z.read('learner.json'));u3=sorted({t.get('unitContentDigest','') for t in doc['attempts'] if t.get('unitNumber')==3});u4=sorted({t.get('unitContentDigest','') for t in doc['attempts'] if t.get('unitNumber')==4});check(u3==c['recordedUnit3AttemptDigests'] and u4==c['recordedUnit4AttemptDigests'],'independent recorded-identity census');check(not doc.get('recordings'),'learner-only archive; no recording acceptance')
check(m[0]==read(D/'MIXED_LIFECYCLE.json') and m[0]['legacyUnit5Schema']==3 and m[0]['freshUnit6Schema']==4 and m[0]['restoredRoundtripAndReopenedUnchanged'] and m[0]['wrongSchemaAndChangedOwnerRejected'],'mixed current-pack lifecycle')
con=read(D/'CONSERVATION.json');final=read(D/'FINAL_REGRESSION/RESULT.json');source=final['sourceCommit']
for path,h in con['original66ArchiveHashes'].items():
 check(sha((R/path).read_bytes())==h,'original66 hash')
 with zipfile.ZipFile(R/path) as z:check(z.testzip() is None,'original66 CRC')
for path,h in con['priorUnit5EvidenceHashes'].items():check(sha((R/path).read_bytes())==h,'prior Unit5 evidence')
check(git('diff','--name-only',con['baseCommit'],source,'--','app/src/test').decode().splitlines()==[con['onlyNewTestFile']],'all existing tests unchanged')
for k,v in con['unchangedInputTrees'].items():check(git('rev-parse',source+':'+k).decode().strip()==v==git('rev-parse',con['baseCommit']+':'+k).decode().strip(),'app/corpus/generated tree conservation')
candidate=json.loads(git('show',source+':release/acceptance/candidate.json'));check(candidate['sourceDigest']==con['sourceDigest'] and candidate['frozen'] is False and candidate['sourceCommit'] is None and candidate['artifact']=={'status':'NOT_RECORDED'} and candidate['publicRelease']=='NOT_APPROVED','candidate unapproved')
print(json.dumps(dict(status='CURRENT_PACK_ARCHIVE_AND_MIXED_LIFECYCLE_SCOPE_VERIFIED',testedSource=source,counts=final['testCounts'],classes=2,originalArchiveCases=64,additionalMixedLifecycleCases=1,caseCountsAreNotTestCounts=True,originalArchiveCreditTwo=24,originalArchiveCreditThree=40,initialSeparateCounts=initial['testCounts'],all66ArchiveHashesAndCrcs=True,allPriorUnit5EvidenceConserved=True,existingTestsAndAppInputsUnchanged=True,currentHeadMatchesTestedInputs=all(git('rev-parse','HEAD:'+k).decode().strip()==v for k,v in final['inputTrees'].items()),completeSuite=False,findingsClosed=0,physicalAcceptance='OUTSTANDING_LAST'),indent=2))
