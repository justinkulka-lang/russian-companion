"""Read-only preservation/mapping checks. Executes no tests, builds or exporters."""
from pathlib import Path
from collections import Counter
import json,csv,hashlib,subprocess,fnmatch,base64
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'AGENTS.md').exists());B=D.parent;A=B/'final-independent-recheck-results/2026-10-22-claude-code';H=B/'engineering-contract-broad-2026-10-24';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
for n,v in json.loads((D/'SHA256.json').read_text()).items():assert sha(D/n)==v,n
for n,v in json.loads((D/'INPUT_IDENTITIES.json').read_text()).items():assert sha(R/n)==v,n
read=lambda p:list(csv.DictReader(p.open()))
a=read(A/'FINDINGS.csv');f=read(D/'FINDING_CONTINUATION.csv');assert len(a)==len(f)==48
for old,new in zip(a,f):
 assert old['finding_id']==new['finding_id'] and old['status']==new['original_status']
 assert old['independent_disposition_2026_10_22']==new['independent_disposition']
 assert new['current_independent_status']=='OPEN_NOT_CLOSED' and new['candidate_method_match_is_acceptance']=='NO'
q=read(A/'EXECUTION_EVIDENCE_REQUESTS.csv');m=read(D/'EXECUTION_REQUEST_RECONCILIATION.csv');assert len(q)==len(m)==70
r=json.loads((H/'RUNTIME_RESULT.json').read_text());lookup={(t['testClass'],t['method']):t['outcome'] for t in r['tests']};classes={k[0] for k in lookup}
allmatches=read(D/'REQUEST_MATCHED_METHODS.csv')
for old,new in zip(q,m):
 assert old['request_no']==new['request_no'] and old['execution_status']==new['original_execution_status']
 assert old['independent_execution_this_session']==new['original_independent_execution'] and new['acceptance_fulfillment']=='NOT_CLAIMED'
 selectors=[s for s in new['effective_candidate_selectors'].split(';') if s];expected={k for k in lookup if any(fnmatch.fnmatchcase(k[0],s) for s in selectors)}
 observed=[x for x in allmatches if x['request_no']==new['request_no']];actual={(x['testClass'],x['method']) for x in observed}
 assert actual==expected and len(actual)==len(observed)==int(new['matched_methods'])
 assert all(x['outcome']==lookup[(x['testClass'],x['method'])] for x in observed)
 assert int(new['host_passes'])==sum(lookup[k]=='PASSED' for k in expected)
 assert int(new['host_skips'])==sum(lookup[k]=='SKIPPED' for k in expected)
for n in [22,60]:
 row=next(x for x in m if int(x['request_no'])==n);assert '*HostActivityLifecycleTest'==row['unmatched_original_selectors'];assert '*LearnerReloadRecoveryTest' in row['effective_candidate_selectors'] and '*HostActivityRecreationTest' in row['effective_candidate_selectors']
p=json.loads((D/'CURRENT_POLICY_INVENTORY.json').read_text());assert p['activePolicies']==28 and p['objectivePools']==59 and p['policiesStillContainingPrintCaseSingleton']==26
assert p['candidatePlacements']==sum(len(x['items']) for x in p['pools'])==82
assert p['uniqueCandidateItems']==len({i for x in p['pools'] for i in x['items']})==48
assert p['unitsStillContainingPrintCaseSingleton']==[x['unit'] for x in p['pools'] if 'u1-i-decode-pair' in x['items']]
assert len(p['policies'])==28 and len(p['pools'])==59
assert p['exportSha256']==sha(D/'EXPORT.log')
export_policies=[];export_pools=[];dec=lambda v:base64.b64decode(v).decode()
for line in (D/'EXPORT.log').read_text().splitlines():
 v=line.split('\t')
 if v[0]=='POLICY':export_policies.append(dict(unit=int(v[1]),id=dec(v[2]),digest=v[3],schema=int(v[4]),pools=int(v[5])))
 if v[0]=='POOL':export_pools.append(dict(unit=int(v[1]),slot=int(v[2]),objective=dec(v[3]),skill=v[4],remediationStep=dec(v[5]),items=dec(v[6]).splitlines()))
assert p['policies']==export_policies and p['pools']==export_pools
assert all(x['exitCode']==0 for x in p['commands']) and p['canonicalSourceDigest'] in (D/'DIGEST.log').read_text()
assert p['canonicalSourceDigest']=='2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892'
scope=read(D/'HOST_METHOD_SCOPE.csv');assert len(scope)==1365 and {(x['testClass'],x['method']):x['outcome'] for x in scope}==lookup
for x in scope:assert x['shipped_flow_coverage']=='NOT_INFERRED_FROM_PACKAGE_OR_PASS'
s=json.loads((D/'SUMMARY.json').read_text());assert s['hostCounts']==r['testCounts']==dict(reported=1365,passed=1364,failed=0,errors=0,skipped=1)
assert s['allIndependentFindingsOpen'] and s['independentAudit']=='INCOMPLETE_ALL48_OPEN' and s['physicalAcceptance']=='OUTSTANDING_LAST' and s['extraJUnitMethodsExecuted']==0
for path,value in r['inputTrees'].items():assert subprocess.check_output(['git','rev-parse','HEAD:'+path],cwd=R,text=True).strip()==value
prior=subprocess.run(['python3',str(H/'verify_saved_broad.py')],cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE);assert prior.returncode==0,prior.stderr
print(json.dumps(dict(verified=True,testedSource=r['sourceCommit'],hostCounts=r['testCounts'],originalFindingsPreserved=48,originalRequestsPreserved=70,allIndependentFindingsOpen=True,currentPolicies=28,printSingletonPolicies=26,canonicalSourceDigest=p['canonicalSourceDigest'],originalArchivesShaCrc=66,priorProtectedFiles=7685,additionalJUnitMethodsExecuted=0,scenarioAcceptanceNotInferred=True,completeSuite=False)))
