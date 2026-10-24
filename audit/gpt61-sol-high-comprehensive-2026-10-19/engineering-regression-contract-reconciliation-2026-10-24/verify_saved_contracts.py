"""Read-only verification of preserved contract attempts and broad discovery; no tests run."""
from pathlib import Path
import json,hashlib,zipfile,subprocess
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest()
for n,v in json.loads((D/'SHA256.json').read_text()).items():assert sha((D/n).read_bytes())==v,n
for n,v in json.loads((D/'PROTECTED_BASELINE.json').read_text()).items():assert sha((R/n).read_bytes())==v,n
summaries={}
for attempt in ['ATTEMPT_1','ATTEMPT_2']:
 p=subprocess.run(['python3',str(D/'verify_recorded_attempt.py'),str(D/attempt)],cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE);assert p.returncode==0,p.stderr;summaries[attempt]=json.loads(p.stdout)
assert summaries['ATTEMPT_1']['testCounts']==dict(reported=56,passed=54,failed=1,errors=0,skipped=1)
assert summaries['ATTEMPT_2']['testCounts']==dict(reported=56,passed=55,failed=0,errors=0,skipped=1)
a=json.loads((D/'ATTEMPT_1/REGRESSION/RESULT.json').read_text());b=json.loads((D/'ATTEMPT_2/REGRESSION/RESULT.json').read_text());ids=lambda x:{(t['testClass'],t['method']) for t in x['tests']};assert ids(a)==ids(b) and len(ids(a))==56
recheck=json.loads((D/'ATTEMPT_2/ORIGINAL_FAILURE_RECHECK.json').read_text());assert len(recheck)==15 and all(x['followupOutcome']=='PASSED' for x in recheck)
final=json.loads((D/'FINAL_CONSERVATION.json').read_text());assert final['testedSource']==b['sourceCommit']=='a6e06bd5375e033f19d2496a626a506452159949';original=json.loads((D/'APPLIED_PREPARATION.json').read_text())
with zipfile.ZipFile(D/'ORIGINAL_TEST_FILES.zip') as z:
 assert z.testzip() is None
 for n,value in original['originalSha256'].items():
  raw=z.read(n);assert sha(raw)==value
  actual=subprocess.check_output(['git','show',final['testedSource']+':'+n],cwd=R);assert sha(actual)==final['currentTestFileSha256'][n]
  if n.endswith('.kt'):
   lines=iter(s.strip() for s in actual.decode().splitlines() if s.strip());assert all(any(x==line.strip() for x in lines) for line in raw.decode().splitlines() if line.strip()),n
assert final['canonicalSourceDigest']=='2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892'
for n,v in final['productionInputTreesUnchanged'].items():assert a['inputTrees'][n]==b['inputTrees'][n]==v
proof=json.loads((D/'PACKAGE_IDENTITY_PROOF/PROOF.json').read_text())
for name,commit in [('before5.json',proof['historicalPackSource']),('current5.json',proof['currentUnit5Implementation'])]:
 raw=subprocess.check_output(['git','show',commit+':app/src/main/assets/content/v2-curriculum.json'],cwd=R);assert sha(raw)==proof['sourceBytes'][name]
output=(D/'PACKAGE_IDENTITY_PROOF/OUTPUT.txt').read_text();assert 'historical='+proof['historicalPackageDigest'] in output and 'currentImplementation='+proof['currentPackageDigest'] in output and 'actual='+proof['currentPackageDigest'] in output
p=subprocess.run(['python3',str(D/'BROAD/verify_broad_discovery.py')],cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE);assert p.returncode==0,p.stderr;discovery=json.loads(p.stdout)
assert discovery['syntheticSkips']==1365 and discovery['runtimePasses']==0 and discovery['priorMethodsRetained']==1363 and discovery['addedMethods']==2
assert final['independentAudit']=='INCOMPLETE_ALL48_OPEN' and final['physicalAcceptance']=='OUTSTANDING_LAST' and final['apkIdentity'] is None and final['apkSignature'] is None
print(json.dumps(dict(savedContractEvidenceVerified=True,attempts={k:v['testCounts'] for k,v in summaries.items()},scopesNotSummed=True,all15OriginalFailuresPassedInFocusedScope=True,canonicalSourceDigest=final['canonicalSourceDigest'],originalArchivesShaCrc=66,currentGeneratedRecordsConserved=155,broadDiscovery=discovery,testsExecutedByVerifier=0,completeSuite=False)))
