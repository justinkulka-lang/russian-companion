"""Read-only pinned research verification; no writes, builds or JUnit executions."""
from pathlib import Path
import subprocess,json,hashlib,csv,zipfile,collections
D=Path(__file__).parent;R=Path(subprocess.check_output(['git','rev-parse','--show-toplevel'],text=True).strip());S='5ba4076382050b02dc51e0dcf022e9a138f826b4';sha=lambda b:hashlib.sha256(b).hexdigest()
manifest=json.loads((D/'SHA256.json').read_text());assert set(manifest)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'}
tracked=set(subprocess.check_output(['git','ls-files'],cwd=R,text=True).splitlines())
for name,h in manifest.items():assert sha((D/name).read_bytes())==h and (D/name).relative_to(R).as_posix() in tracked
subprocess.run(['python3',str(D/'extract_research.py'),'--check'],cwd=R,stdout=subprocess.DEVNULL,check=True)
c=json.loads((D/'CHAINS.json').read_text());s=json.loads((D/'SUMMARY.json').read_text());assert c['sourceCommit']==s['sourceCommit']==S and len(c['chains'])==10
rows=list(csv.DictReader((D/'CANDIDATE_ASSESSMENT.csv').open()));assert len(rows)==sum(len(x['objective']['checkItemIds']) for x in c['chains'])==43;selected=[x for x in rows if x['decision']=='SELECT_EXISTING_NOUN_BEHAVIOR_POOL'];assert len(selected)==5 and [x['item'] for x in selected]==s['selectedNounCandidates'];assert collections.Counter(x['rawKeyIndex'] for x in selected)=={'0':3,'1':2};assert all(not x['sameTupleAsGuided'] for x in selected)
p=json.loads((D/'CURRENT_POLICY_INVENTORY.json').read_text());assert (p['activePolicies'],p['objectivePools'],p['candidatePlacements'],p['uniqueCandidateItems'],p['printSingletonPolicies'])==(28,59,89,48,24);assert p['unitsWithPrintSingleton']==[3,4]+list(range(9,31));assert s['proposedPools'][0]['items']==p['reusedNounBehaviorPool']['items'];assert s['proposedPools'][1:]==p['currentUnit9Pools'][1:];assert s['activated'] is False and s['status']=='PROPOSED_NOT_ACTIVATED' and s['proposedFreshSchema']==11 and s['runtimeMethodsExecuted']==0
plans=list(csv.DictReader((D/'BASELINE_AND_ACCEPTANCE_PLAN.csv').open()));assert len(plans)==10 and all(x['executionStatus']=='PLANNED_NOT_EXECUTED' and x['testMethodIdentity']=='NOT_CREATED' for x in plans)
h=json.loads((D/'HISTORICAL_UNIT9_INVENTORY.json').read_text());assert h['archivesChecked']==69 and h['archivesContainingUnit9']==len(h['fixtures'])==30;counts=collections.Counter((a['policyId'],a.get('schema',1)) for f in h['fixtures'] for a in f['assignments']);assert counts=={('unit9-prior-reading-nominative-plural-v1',1):6,('unit9-prior-reading-plural-gender-v2',5):24}
record_hashes=json.loads(subprocess.check_output(['git','show',S+':audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit8-source-reconciliation-2026-10-24/APPLIED_RECORDS.json'],cwd=R))['currentRecordHashes']
for name,v in json.loads((D/'PROTECTED_EVIDENCE_HASHES.json').read_text()).items():
 data=subprocess.check_output(['git','show',S+':'+name],cwd=R) if name in record_hashes else (R/name).read_bytes();assert sha(data)==v,name
inputs=json.loads((D/'INPUTS.json').read_text());assert inputs['sourceCommit']==S
introductions=subprocess.check_output(['git','log','--diff-filter=A','--format=%H','--',str((D/'DECISION.md').relative_to(R))],cwd=R,text=True).splitlines()
research_commit=introductions[-1] if introductions else 'HEAD'
for name,h in inputs['inputTrees'].items():assert subprocess.check_output(['git','rev-parse',research_commit+':'+name],cwd=R,text=True).strip()==h
if not introductions:assert not subprocess.check_output(['git','diff',S,'--name-only','--','app/src/main','app/src/test','catalog','docs','review','release'],cwd=R,text=True).strip()
assert not any(p.suffix.lower()=='.pdf' for p in D.rglob('*'))
print(json.dumps(dict(verified=True,sourceCommit=S,objectiveChains=10,assessedChecks=43,selectedNounCandidates=5,historicalArchivesShaCrcChecked=69,archivesContainingUnit9=30,historicalV1Schema1=6,historicalV2Schema5=24,productionTestsAnd155RecordsUnchanged=True,newRuntimeMethodsExecuted=0,activated=False,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')))
