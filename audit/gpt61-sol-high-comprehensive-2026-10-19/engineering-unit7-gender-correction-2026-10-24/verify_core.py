"""Verify preserved Unit7 core attempts and conservation; no tests/builds/generation."""
from pathlib import Path
import json,hashlib,subprocess,re,zipfile
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text());git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
tracked=set(git('ls-files').decode().splitlines())
for n,v in read('SHA256.json').items():
 assert sha((D/n).read_bytes())==v,n
 assert (D/n).relative_to(R).as_posix() in tracked,('Untracked evidence',n)
expected={'ATTEMPT_1':('2e2ebcebc81f54c04db7f0ac24e99374674c97c5',dict(reported=11,passed=10,failed=1,errors=0,skipped=0)), 'ATTEMPT_2':('c4477d273eb43f49816d1b4a7ae73216832713ba',dict(reported=11,passed=11,failed=0,errors=0,skipped=0))};out={}
for name,(source,counts) in expected.items():
 p=subprocess.run(['python3',str(D/'verify_attempt.py'),str(D/name)],cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE);assert p.returncode==0,p.stderr
 out[name]=json.loads(p.stdout);assert out[name]['sourceCommit']==source and out[name]['counts']==counts
first=read('ATTEMPT_1/REGRESSION_RESULT.json');last=read('ATTEMPT_2/REGRESSION_RESULT.json')
ids=lambda x:{(t['testClass'],t['method']) for t in x['tests']};assert ids(first)==ids(last)
for n,v in first['inputTrees'].items():
 if n!='app/src/test':assert last['inputTrees'][n]==v,n
prep=read('PREPARATION.json');path='app/src/test/java/dev/russiancompanion/v2/Unit7GenderReviewBaselineTest.kt';original=git('show','38542882cd9ab2e075691eca19442ddfabd17b6b:'+path)
assert (D/'ORIGINAL_BASELINE_TEST.kt.txt').read_bytes()==original
adaptation=read('BASELINE_API_ADAPTATION.json');assert sha(original)==adaptation['originalTestSha256'];adapted=git('show',expected['ATTEMPT_2'][0]+':'+path);assert sha(adapted)==adaptation['adaptedTestSha256']
lines=iter(adapted.decode().splitlines());assert all(any(x==line for x in lines) for line in original.decode().splitlines() if 'assert' in line)
assert 'if (refusal.message != "current advancement evidence is incomplete") throw refusal' in adapted.decode()
assert 'if (!advancedWithoutGender) assertEquals(beforeAttemptedAdvance, store.governedLearnerDocument())' in adapted.decode()
for n,v in prep['preexistingTestFiles'].items():
 if n!=path:assert sha(git('show',expected['ATTEMPT_2'][0]+':'+n))==v,n
core='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt';objects=lambda s:dict(re.findall(r'internal object (\w+) \{(.*?)^\}',s,re.S|re.M));old=objects(git('show',prep['parent']+':'+core).decode());new=objects(git('show',expected['ATTEMPT_2'][0]+':'+core).decode());assert len(old)==33 and all(new[n]==v for n,v in old.items());assert set(new)-set(old)=={'Unit7GenderCumulativeReviewPolicy'}
asset='app/src/main/assets/content/v2-curriculum.json';before=git('show',prep['parent']+':'+asset);after=git('show',expected['ATTEMPT_2'][0]+':'+asset);assert before.replace(b'unit7-prior-reading-inanimate-case-v1',b'unit7-prior-gender-inanimate-case-v2')==after
for n,v in prep['unchangedTrees'].items():assert git('rev-parse',expected['ATTEMPT_2'][0]+':'+n).decode().strip()==v,n
con=read('CONSERVATION.json');assert len(con['originalArchives'])==66
for n,v in con['originalArchives'].items():
 assert sha((R/n).read_bytes())==v,n
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
for n,v in con['additionalGenuineCaptures'].items():
 assert sha((R/n).read_bytes())==v,n
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
assert read('OFFICIAL_SOURCE_DIGEST.json')['output'].strip()=='d48aa7e0090bd30422172f7b7c87fe52fe2859fa3c5be9b0851d2f9704f30e3a'
assert con['generatedRecords']=='155_UNCHANGED_STALE_PENDING_AUTHENTIC_RECONCILIATION' and con['independentAudit']=='INCOMPLETE_ALL48_OPEN'
print(json.dumps(dict(verified=True,attempts={k:v['counts'] for k,v in out.items()},scopesNotSummed=True,source=expected['ATTEMPT_2'][0],actualImplementation=expected['ATTEMPT_1'][0],oldPolicyObjectsExact=33,originalArchivesShaCrc=66,genuineAdditionalCapturesExact=2,generatedRecords='155_UNCHANGED_STALE',baselineAssertionsRetained=True,baselineSpecificApiRefusalAdaptationRecorded=True,apkIdentity=None,apkSignature=None,independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST',additionalJUnitMethodsExecuted=0,completeSuite=False)))
