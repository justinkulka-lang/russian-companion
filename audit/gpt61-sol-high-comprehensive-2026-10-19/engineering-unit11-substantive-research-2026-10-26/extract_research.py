"""Pinned Unit11 research extraction. --check reproduces bytes without writing or executing tests."""
from pathlib import Path
import json,subprocess,hashlib,zipfile,io,csv,re,sys
BASE='1d96cb4e89768f69246960e263d9b51412a3b9f4';D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());PRE='audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit10-source-reconciliation-2026-10-25/';CORE='audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit10-present-correction-2026-10-25/';RAW='app/src/main/assets/content/v2-curriculum.json';KERNEL='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R);blob=lambda n:git('show',BASE+':'+n);sha=lambda b:hashlib.sha256(b).hexdigest();enc=lambda x:(json.dumps(x,ensure_ascii=False,indent=2)+'\n').encode()
def csvbytes(rows):
 o=io.StringIO(newline='');w=csv.DictWriter(o,fieldnames=list(rows[0]),lineterminator='\n');w.writeheader();w.writerows(rows);return o.getvalue().encode()
p=json.loads(blob(RAW));items={i['id']:i for i in p['items']};examples={e['id']:e for e in p['examples']};refs={r['id']:r for r in p['references']};pool=p['sourceTextPool'];assert all(sha(v.encode())==k for k,v in pool.items())
objectives=['obj-u1-decoding','obj-u3-gender','obj-u4-present','obj-u4-nominative','obj-u5-indeclinable','obj-u6-animacy','obj-u7-endings','obj-u8-plural','obj-u9-odindva','obj-u10-have','obj-u10-absence','obj-u11-past-read','obj-u11-past-be','obj-u11-past-aspect','obj-u11-reflexive']
selected={'obj-u5-indeclinable':'SELECT_EXISTING_NOUN_BEHAVIOR_POOL','obj-u10-have':'SELECT_FOUR_POSSESSION_ABSENCE_CHECKS'};chains=[];rows=[]
for oid in objectives:
 u=next(u for u in p['units'] if any(o['id']==oid for o in u['objectives']));o=next(o for o in u['objectives'] if o['id']==oid);ss={s['id']:s for session in u['sessions'] for s in session['steps']};steps=[ss[k] for k in dict.fromkeys(o['instructionStepIds']+o['practiceStepIds']+o['remediationStepIds'])];ids=list(dict.fromkeys(o['checkItemIds']+[i for s in steps for i in s.get('itemIds',[])]));ee=list(dict.fromkeys(o['exampleIds']+[e for s in steps for e in s.get('exampleIds',[])]));rr=list(dict.fromkeys(r for s in steps for r in s.get('referenceIds',[])));provenance=[]
 for v in p['provenanceRecords']:
  if v.get('objectiveId')!=oid:continue
  pin={k:v[k] for k in ['itemId','recordType','sourceId','sourceUrl','sourceRevision','sourceRecordId','sourceHash','contentHash','reviewStatus','licenseId','licenseUrl'] if k in v};pin['canonicalFullRecordSha256']=sha(enc(v))
  if 'originalSourceTextRef' in v:
   key=v['originalSourceTextRef'];assert key==v['sourceHash'] and key in pool;pin['resolvedSourcePoolSha256']=sha(pool[key].encode())
  provenance.append(pin)
 sources={items[i]['sourceId'] for i in ids}|{v['sourceId'] for v in provenance};teach=[s for s in steps if s['kind']=='TEACH'];repair=ss[o['remediationStepIds'][0]] if o['remediationStepIds'] else None
 chain=dict(unit=u['number'],objective=o,steps=steps,items=[items[i] for i in ids],examples=[examples[e] for e in ee],references=[refs[r] for r in rr],provenancePins=provenance,sourceRecords=[s for s in p['sources'] if s['id'] in sources],rightsRecords=[r for r in p['rights'] if r['sourceId'] in sources],capabilities=[c for c in p['sourceCapabilities'] if c.get('objectiveId')==oid],teachingCards=len(teach),allTeachingBodiesInRepair=bool(repair) and all(s['body'] in repair['body'] for s in teach),teachingExampleIds=sorted({e for s in teach for e in s.get('exampleIds',[])}),repairExampleIds=repair.get('exampleIds',[]) if repair else [])
 chains.append(chain)
 sig=lambda i:(i.get('prompt'),i.get('promptLetter'),tuple(sorted(i['options'])),i['answer'])
 guided=[items[i] for s in steps if s['kind']=='GUIDED' for i in s.get('itemIds',[])]
 for iid in o['checkItemIds']:
  i=items[iid];decision=selected.get(oid,'DEFER_ALTERNATIVE')
  if u['number']==11:decision='CURRENT_TARGET_NOT_PRIOR'
  rows.append(dict(unit=u['number'],objective=oid,item=iid,decision=decision,rawKeyIndex=i['options'].index(i['answer']),sameTupleAsGuided=';'.join(g['id'] for g in guided if sig(g)==sig(i)),sourceId=i['sourceId'],reviewStatus=i['reviewStatus'],itemSha256=sha(enc(i))))
ob=json.loads(blob(CORE+'CURRENT_POLICY_OBSERVATION.json'));assert git('rev-parse',ob['sourceCommit']+':app/src/main')==git('rev-parse',BASE+':app/src/main');assert [ob[k] for k in ['activePolicies','objectivePools','candidatePlacements','uniqueCandidateItems','printSingletonPolicies']]==[28,59,96,48,22]
current=next(x for x in ob['policies'] if x['unit']==11);oldp=[x for x in ob['pools'] if x['unit']==11];noun=next(x for x in ob['pools'] if x['unit']==6 and x['slot']==1);by={c['objective']['id']:c for c in chains};have=by['obj-u10-have'];nounchain=by['obj-u5-indeclinable'];assert current['schema']==1 and oldp[1]['items']==['u10-i-have-4']
assert noun['items']==nounchain['objective']['checkItemIds'] and len(noun['items'])==5
for c,n,e in [(nounchain,4,8),(have,5,7)]:assert c['allTeachingBodiesInRepair'] and c['teachingCards']==n and len(c['teachingExampleIds'])==e and set(c['teachingExampleIds'])==set(c['repairExampleIds'])
assert [items[i]['options'].index(items[i]['answer']) for i in have['objective']['checkItemIds']]==[0,0,1,1]
proposed=[dict(noun,unit=11,slot=0),dict(oldp[1],items=have['objective']['checkItemIds'])];registered=re.findall(r'internal object (\w+CumulativeReviewPolicy)\s*\{',blob(KERNEL).decode());assert len(registered)==37
con=json.loads(blob(PRE+'CONSERVATION.json'));assert len(con['archives'])==71;fixtures=[]
for n,h in con['archives'].items():
 data=blob(n);assert sha(data)==h
 with zipfile.ZipFile(io.BytesIO(data)) as z:
  assert z.testzip() is None
  if 'learner.json' not in z.namelist():continue
  raw=z.read('learner.json');doc=json.loads(raw);aa=[a for a in doc.get('reviewAssignments',[]) if a['targetUnit']==11]
  if aa:
   ids={a['id'] for a in aa};fixtures.append(dict(zipPath=n,zipSha256=h,learnerSha256=sha(raw),assignments=aa,answers=[a for a in doc.get('reviewAnswers',[]) if a['assignmentId'] in ids],completions=[c for c in doc.get('unitCompletions',[]) if c['unitNumber']==11]))
protected=dict(con['archives']);protected.update(json.loads(blob(PRE+'APPLIED_RECORDS.json'))['currentRecordHashes'])
for n in git('ls-tree','-r','--name-only',BASE,'--',PRE).decode().splitlines():protected[n]=sha(blob(n))
assert all(sha(blob(n))==h for n,h in protected.items())
summary=dict(sourceCommit=BASE,status='PROPOSED_NOT_ACTIVATED',activated=False,objectiveChains=len(chains),assessedChecks=len(rows),proposedId='unit11-prior-noun-behavior-possession-absence-v2',proposedFreshSchema=13,proposedPools=proposed,registeredPolicyObjectsPreserved=37,schemasPreserved=list(range(1,13)),selectedNounCandidates=5,selectedPossessionAbsenceCandidates=4,nounRawKeys=[0,0,0,1,1],possessionAbsenceRawKeys=[0,0,1,1],historicalArchivesChecked=71,archivesContainingUnit11=len(fixtures),currentPrintSingletonPolicies=22,newRuntimeMethodsExecuted=0,heldOutItemsAdded=0,qualifiedAcceptance='PENDING_NOT_GRANTED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None,exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2})
outputs={'CHAINS.json':enc(dict(sourceCommit=BASE,sourcePoolIntegrityVerified=True,chains=chains)),'CANDIDATE_ASSESSMENT.csv':csvbytes(rows),'CURRENT_POLICY_INVENTORY.json':enc(dict(sourceCommit=BASE,reusedObservation=CORE+'CURRENT_POLICY_OBSERVATION.json',observationSha256=sha(blob(CORE+'CURRENT_POLICY_OBSERVATION.json')),producerSource=ob['sourceCommit'],appMainTreeMatches=True,currentUnit11=current,currentPools=oldp,proposedPools=proposed,registeredPolicyObjects=registered,currentCounts={k:ob[k] for k in ['activePolicies','objectivePools','candidatePlacements','uniqueCandidateItems','printSingletonPolicies']})),'HISTORICAL_UNIT11_INVENTORY.json':enc(dict(sourceCommit=BASE,scope='Historical byte inventory;not replay/test counts or fresh actual-current capture',archivesChecked=71,archivesContainingUnit11=len(fixtures),fixtures=fixtures)),'PROTECTED_EVIDENCE_HASHES.json':enc(protected),'SUMMARY.json':enc(summary),'INPUTS.json':enc(dict(sourceCommit=BASE,sourceRootTree=git('rev-parse',BASE+'^{tree}').decode().strip(),inputTrees={n:git('rev-parse',BASE+':'+n).decode().strip() for n in ['app/src/main','app/src/test','catalog','docs','review','release']},rawCurriculumSha256=sha(blob(RAW)),kernelSha256=sha(blob(KERNEL)),authoritySha256=sha(blob('docs/governance/MASTER_AUTHORITY_v2.0.md')),privatePdf='NOT_READ_OR_PACKAGED',newRuntimeMethods=0))}
outputs['GENERATED_SHA256.json']=enc({n:sha(b) for n,b in outputs.items()});check=sys.argv[1:]==['--check'];assert sys.argv[1:] in [[],['--check']]
for n,b in outputs.items():
 if check:assert (D/n).read_bytes()==b,n
 else:assert not (D/n).exists(),n;(D/n).write_bytes(b)
print(json.dumps(dict(verified=check,chains=len(chains),assessedChecks=len(rows),archivesShaCrc=71,archivesContainingUnit11=len(fixtures),runtimeMethodsExecuted=0)))
