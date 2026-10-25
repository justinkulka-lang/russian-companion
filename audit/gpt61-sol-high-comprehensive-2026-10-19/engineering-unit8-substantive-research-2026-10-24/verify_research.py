"""Read-only pinned research verification; executes no JUnit/Android tasks."""
from pathlib import Path
import subprocess,json,hashlib,csv,zipfile,base64
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());S='fe7b26853448b8c3bd8098126d85983b013b5cd7';sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
m=json.loads((D/'SHA256.json').read_text());assert set(m)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'}
for n,h in m.items():assert sha((D/n).read_bytes())==h,n
tracked=set(git('ls-files').decode().splitlines());assert all(str(p.relative_to(R)) in tracked for p in D.rglob('*') if p.is_file())
subprocess.run(['python3',str(D/'extract_research.py'),'--check'],cwd=R,check=True,stdout=subprocess.DEVNULL)
c=json.loads((D/'CHAINS.json').read_text());s=json.loads((D/'SUMMARY.json').read_text());assert c['sourceCommit']==s['sourceCommit']==S and len(c['chains'])==11
rows=list(csv.DictReader((D/'CANDIDATE_ASSESSMENT.csv').open()));assert len(rows)==sum(len(x['objective']['checkItemIds']) for x in c['chains'])==39
selected=[x for x in rows if x['decision']=='SELECT_SUPPLIED_PRESENT_PERSON_RECOGNITION'];assert len(selected)==4 and [x['item'] for x in selected]==s['selectedPresentCandidates'];assert all(x['rawKeyIndex']=='0' and not x['sameTupleAsGuided'] for x in selected)
by={x['objective']['id']:x for x in c['chains']};present=by['obj-u4-present'];repair=next(x for x in present['steps'] if x['id']=='u4-st-rem-present')['body'];assert all(x['body'] in repair for x in present['steps'] if x['kind']=='TEACH')
assert s['activated'] is False and s['proposedFreshSchema']==10 and s['extraJUnitMethodsExecuted']==0
plan=list(csv.DictReader((D/'BASELINE_AND_ACCEPTANCE_PLAN.csv').open()));assert len(plan)==10 and all(x['execution_status']=='PLANNED_NOT_EXECUTED' and x['test_method_identity']=='NOT_CREATED' for x in plan)
p=json.loads((D/'CURRENT_POLICY_INVENTORY.json').read_text());assert p['sourceCommit']==S and p['activePolicies']==28 and p['objectivePools']==59 and p['candidatePlacements']==86 and p['uniqueCandidateItems']==48
assert p['unitsStillContainingPrintCaseSingleton']==[3,4]+list(range(8,31))
pol=[];pools=[];dec=lambda x:base64.b64decode(x).decode()
for line in (D/'EXPORT.log').read_text().splitlines():
 v=line.split('\t')
 if v[0]=='POLICY':pol.append(dict(unit=int(v[1]),id=dec(v[2]),digest=v[3],schema=int(v[4]),pools=int(v[5])))
 if v[0]=='POOL':pools.append(dict(unit=int(v[1]),slot=int(v[2]),objective=dec(v[3]),skill=v[4],remediationStep=dec(v[5]),items=dec(v[6]).splitlines()))
assert pol==p['policies'] and pools==p['pools'];assert next(x for x in pol if x['unit']==8)['schema']==1
assert next(x for x in pools if x['unit']==6 and x['slot']==0)['items']==s['selectedPresentCandidates']
assert next(x for x in pools if x['unit']==8 and x['slot']==1)['items']==[s['retainedAgreementCandidate']]
h=json.loads((D/'HISTORICAL_UNIT8_INVENTORY.json').read_text());assert h['originalArchivesChecked']==66 and h['archivesContainingUnit8Tasks']==len(h['fixtures'])==33
for f in h['fixtures']:
 assert sha((R/f['zipPath']).read_bytes())==f['zipSha256']
 with zipfile.ZipFile(R/f['zipPath']) as z:
  assert z.testzip() is None and sha(z.read('learner.json'))==f['learnerSha256'];a=json.loads(z.read('learner.json'));assert [x for x in a['reviewAssignments'] if x['targetUnit']==8]==f['unit8Assignments']
for n,v in json.loads((D/'PROTECTED_EVIDENCE_HASHES.json').read_text()).items():assert sha((R/n).read_bytes())==v,n
records=json.loads((D/'CURRENT_RECORD_HASHES.json').read_text());assert len(records)==155
for n,v in records.items():assert sha(git('show',S+':'+n))==v,n
prior=json.loads((D/'PRIOR_HOST_VERIFICATION.json').read_text());assert prior['verified'] and prior['sourceCommit']=='dcc9314889566ba6f4f22333e2c98b34e9c9dd64' and prior['testCounts']==dict(reported=1380,passed=1379,failed=0,errors=0,skipped=1)
con=json.loads((D/'CONSERVATION.json').read_text())
for n,v in con['inputTrees'].items():assert git('rev-parse',S+':'+n).decode().strip()==v
assert not any(p.suffix.lower()=='.pdf' for p in D.rglob('*') if p.is_file())
print(json.dumps(dict(verified=True,sourceCommit=S,objectiveChains=11,scoredCandidates=39,proposedCandidates=4,proposalActivated=False,originalArchives=66,historicalUnit8Archives=33,currentRecords=155,protectedEvidenceFiles=len(json.loads((D/'PROTECTED_EVIDENCE_HASHES.json').read_text())),runtimeTestsExecuted=0,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')))
