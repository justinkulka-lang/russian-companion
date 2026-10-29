"""Verify pinned Unit7research without tests, builds, source exports or output writes."""
from pathlib import Path
import json,subprocess,hashlib,csv,zipfile
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest()
subprocess.run(['python3',str(D/'extract_research.py'),'--check'],cwd=R,check=True,stdout=subprocess.PIPE)
manifest=json.loads((D/'SHA256.json').read_text());tracked=set(subprocess.check_output(['git','ls-files'],cwd=R,text=True).splitlines())
for n,v in manifest.items():
 assert sha((D/n).read_bytes())==v,n
 assert (D/n).relative_to(R).as_posix() in tracked,('Missing tracked evidence',n)
a=json.loads((D/'CHAINS.json').read_text());chains={c['objective']['id']:c for c in a['chains']};assert len(chains)==8
rows=list(csv.DictReader((D/'CANDIDATE_ASSESSMENT.csv').open()));assert len(rows)==28==sum(len(c['objective']['checkItemIds']) for c in chains.values())
expected={(c['objective']['id'],i) for c in chains.values() for i in c['objective']['checkItemIds']};assert {(r['objective_id'],r['item_id']) for r in rows}==expected
for row in rows:
 c=chains[row['objective_id']];i=next(x for x in c['items'] if x['id']==row['item_id']);assert row['item_sha256']==sha((json.dumps(i,ensure_ascii=False,indent=2)+'\n').encode());assert row['source_id']==i['sourceId'] and row['repository_review_status']==i['reviewStatus'];assert int(row['raw_key_index'])==i['options'].index(i['answer']);assert int(row['option_count'])==len(i['options'])
c=chains['obj-u3-gender'];assert len(c['objective']['checkItemIds'])==5
steps={s['id']:s for s in c['steps']};text='\n'.join(s.get('body','') for s in c['steps'] if s['id'] in c['objective']['instructionStepIds']);repair=steps['u3-st-rem-gender']['body']
for iid in c['objective']['checkItemIds']:
 i=next(x for x in c['items'] if x['id']==iid);assert i['promptLetter'] in text and i['promptLetter'] in repair;assert i['answer'] in repair;assert not i['containsGeneratedRussian'];assert any(p['sourceId']==i['sourceId'] for p in c['provenancePins'])
assert {next(i for i in c['items'] if i['id']==iid)['answer'] for iid in c['objective']['checkItemIds']}=={'masculine','feminine','neuter'}
fixtures=json.loads((D/'HISTORICAL_FIXTURE_INVENTORY.json').read_text())['fixtures'];assert len(fixtures)==8
for f in fixtures:
 doc=json.loads((R/f['jsonPath']).read_text());assert sha((R/f['jsonPath']).read_bytes())==f['jsonSha256'];assert sha((R/f['zipPath']).read_bytes())==f['zipSha256']
 with zipfile.ZipFile(R/f['zipPath']) as z:assert z.testzip() is None and json.loads(z.read('learner.json'))==doc
 assert f['unit7Assignments']==[x for x in doc['reviewAssignments'] if x['targetUnit']==7]
 assert all(x['schema']==1 and x['policyId']=='unit7-prior-reading-inanimate-case-v1' for x in f['unit7Assignments'])
con=json.loads((D/'CONSERVATION.json').read_text());assert len(con['originalArchives'])==66
for n,v in con['originalArchives'].items():
 assert sha((R/n).read_bytes())==v,n
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
source=a['sourceCommit'];assert con['sourceCommit']==source=='27894ced3db0da622968a167c6d4ed282469e10a'
assert subprocess.check_output(['git','rev-parse',source+'^{tree}'],cwd=R,text=True).strip()==con['sourceRootTree']
for n,v in con['inputTrees'].items():assert subprocess.check_output(['git','rev-parse',source+':'+n],cwd=R,text=True).strip()==v
s=json.loads((D/'STATIC_SUMMARY.json').read_text());assert s['selectedGenderCandidates']==c['objective']['checkItemIds'];assert s['proposedFreshAssignmentSchema']==9 and not s['policyActivated'] and s['additionalJUnitMethodsExecuted']==0
assert s['retainedInanimateCandidate']=='u6-i-animacy-3'
plan=list(csv.DictReader((D/'BASELINE_AND_ACCEPTANCE_PLAN.csv').open()));assert len(plan)==10 and all(x['execution_status']=='PLANNED_NOT_EXECUTED' and x['test_method_identity']=='NOT_CREATED' for x in plan)
prior=json.loads((D/'PRIOR_EVIDENCE_VERIFICATION.json').read_text());assert prior['exitCode']==0 and prior['result']['hostCounts']==dict(reported=1365,passed=1364,failed=0,errors=0,skipped=1)
print(json.dumps(dict(verified=True,pinnedSource=source,objectiveChains=8,scoredCandidates=28,selectedGenderCandidates=5,authenticHistoricalStatesIndexed=8,originalArchivesShaCrc=66,plannedScenariosNotExecuted=10,policyActivated=False,runtimeTestsExecuted=0,completeSuite=False,independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST')))
