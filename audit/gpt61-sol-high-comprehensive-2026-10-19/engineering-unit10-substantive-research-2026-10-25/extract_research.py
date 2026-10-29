"""Pinned, reproducible Unit10 engineering research; --check writes nothing and runs no tests."""
from pathlib import Path
import subprocess,json,hashlib,base64,csv,io,zipfile,sys,re
BASE='a1986f9de49062866be3c9fe395473cf3796d888';HERE=Path(__file__).parent;R=Path(subprocess.check_output(['git','rev-parse','--show-toplevel'],text=True).strip());B='audit/gpt61-sol-high-comprehensive-2026-10-19/';PRE=B+'engineering-unit9-source-reconciliation-2026-10-25/';CORE=B+'engineering-unit9-noun-behavior-correction-2026-10-25/';RAW='app/src/main/assets/content/v2-curriculum.json';KERNEL='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
OBJECTIVES=['obj-u1-decoding','obj-u3-gender','obj-u4-present','obj-u4-nominative','obj-u5-indeclinable','obj-u6-possessive','obj-u9-odindva','obj-u10-have','obj-u10-absence','obj-u10-pronoun','obj-u10-preposition','obj-u10-adjective','obj-u10-animate']
def git(*a):return subprocess.check_output(['git',*a],cwd=R)
def blob(n):return git('show',BASE+':'+n)
def sha(b):return hashlib.sha256(b).hexdigest()
def enc(v):return (json.dumps(v,ensure_ascii=False,indent=2)+'\n').encode()
def csvbytes(rows):
 o=io.StringIO(newline='');w=csv.DictWriter(o,fieldnames=list(rows[0]),lineterminator='\n');w.writeheader();w.writerows(rows);return o.getvalue().encode()
p=json.loads(blob(RAW));items={x['id']:x for x in p['items']};examples={x['id']:x for x in p['examples']};references={x['id']:x for x in p['references']};pool=p.get('sourceTextPool',{});assert all(sha(v.encode())==k for k,v in pool.items());assert git('rev-parse','83a7b075983be5f7f9117e72d2967a9aaa06ce56:app/src/main')==git('rev-parse',BASE+':app/src/main')
chains=[]
for oid in OBJECTIVES:
 u=next(u for u in p['units'] if any(o['id']==oid for o in u['objectives']));o=next(o for o in u['objectives'] if o['id']==oid);allsteps={s['id']:s for ss in u['sessions'] for s in ss['steps']};steps=[allsteps[s] for s in dict.fromkeys(o['instructionStepIds']+o['practiceStepIds']+o['remediationStepIds'])];ii=list(dict.fromkeys(o['checkItemIds']+[i for s in steps for i in s.get('itemIds',[])]));ee=list(dict.fromkeys(o['exampleIds']+[i for s in steps for i in s.get('exampleIds',[])]));rr=list(dict.fromkeys(i for s in steps for i in s.get('referenceIds',[])));provenance=[x for x in p['provenanceRecords'] if x.get('objectiveId')==oid];sources=sorted({items[i]['sourceId'] for i in ii}|{x['sourceId'] for x in provenance});pins=[]
 for x in provenance:
  pin={k:x[k] for k in ['itemId','recordType','sourceId','sourceUrl','sourceRevision','sourceRecordId','sourceHash','contentHash','reviewStatus','licenseId','licenseUrl'] if k in x};pin['canonicalFullRecordSha256']=sha(enc(x))
  if 'originalSourceTextRef' in x:
   k=x['originalSourceTextRef'];assert k==x['sourceHash'] and k in pool;pin['resolvedPoolSha256']=sha(pool[k].encode());pin['resolvedPoolBytes']=len(pool[k].encode())
  pins.append(pin)
 chains.append(dict(unit=u['number'],objective=o,steps=steps,items=[items[i] for i in ii],examples=[examples[i] for i in ee],references=[references[i] for i in rr],provenancePins=pins,sourceRecords=[x for x in p['sources'] if x['id'] in sources],rightsRecords=[x for x in p['rights'] if x['sourceId'] in sources],capabilities=[x for x in p['sourceCapabilities'] if x.get('objectiveId')==oid]))
observation=json.loads(blob(CORE+'CURRENT_POLICY_OBSERVATION.json'));assert (observation['activePolicies'],observation['objectivePools'],observation['candidatePlacements'],observation['uniqueCandidateItems'],observation['printSingletonPolicies'])==(28,59,93,48,23)
current=next(x for x in observation['policies'] if x['unit']==10);old_pools=[x for x in observation['pools'] if x['unit']==10];present=next(x for x in observation['pools'] if x['unit']==6 and x['slot']==0);assert current['schema']==1 and current['id']=='unit10-prior-reading-numeral-gender-v1';assert [x['objective'] for x in old_pools]==['obj-u1-decoding','obj-u9-odindva'];assert present['objective']=='obj-u4-present' and len(present['items'])==4
proposed=[dict(present,unit=10,slot=0),old_pools[1]];by={x['objective']['id']:x for x in chains}
notes={
'obj-u1-decoding':('REPLACE_PRINT_SINGLETON','The repeated print-case item is not word reading or substantive cumulative grammar evidence; foundational CC002/CC027 remains open.'),
'obj-u3-gender':('DEFER_IMMEDIATE_REPETITION','Bounded lexical-label recognition already assigned in current Unit9; no unseen agreement or genitive evidence.'),
'obj-u4-present':('SELECT_EXISTING_PRESENT_POOL','Exact existing four-item Unit6Present.pool[0],also used in Unit8. Finite supplied form/person lookup; explicit teaching,not productive conjugation or independent generalization. All four raw answer keys are option0; governed accepted order must be verified in actual target10 controls.'),
'obj-u4-nominative':('DEFER_CONSTANT_LABEL_POOL','Both checks have the same nominative-subject answer label. Repeating it does not test the positive-possession/genitive-absence distinction; no new case mastery claim.'),
'obj-u5-indeclinable':('DEFER_IMMEDIATE_REPETITION','Already assigned by fresh current Unit9; binary supplied classification is not genitive production.'),
'obj-u6-possessive':('DEFER_MIXED_NEW_SKILL_ADMISSION','Three existing items mix supplied nominative forms and a feminine object-row phrase. A new skill/claim would need separate evidence; lexical matching may suffice for the object phrase. Do not silently treat them as genitive possessives.'),
'obj-u9-odindva':('RETAIN_NUMERAL_SINGLETON','Keep exact existing u9-i-odindva-4/u9-st-rem-odindva. Masculine supplied one recognition,not all one/two forms or declined numeral cases.'),
}
rows=[]
for chain in chains:
 oid=chain['objective']['id'];guided={i for s in chain['steps'] if s['kind']=='GUIDED' for i in s.get('itemIds',[])};ii={i['id']:i for i in chain['items']};signature=lambda i:(i.get('prompt'),i.get('promptLetter'),tuple(sorted(i['options'])),i['answer'])
 for iid in chain['objective']['checkItemIds']:
  item=ii[iid];decision,limit=notes.get(oid,('CURRENT_TARGET_NOT_PRIOR','Current Unit10 ordinary checks remain mandatory; do not count current-unit answers as a prior cumulative pool. No new forms or rules are inferred.'))
  if oid=='obj-u9-odindva' and iid!='u9-i-odindva-4':decision='DEFER_NUMERAL_POOL_EXPANSION';limit='Other Unit9 one/two checks remain ordinary prior work;retain the exact existing obligation here.'
  rows.append(dict(unit=chain['unit'],objective=oid,item=iid,decision=decision,limit=limit,options=len(item['options']),rawKeyIndex=item['options'].index(item['answer']),sameTupleAsGuided=';'.join(g for g in sorted(guided) if signature(ii[g])==signature(item)),sourceId=item['sourceId'],reviewStatus=item['reviewStatus'],itemSha256=sha(enc(item))))
assert len(chains)==13 and len(rows)==47
chain=by['obj-u4-present'];repair=next(s for s in chain['steps'] if s['id']=='u4-st-rem-present');teach=[s for s in chain['steps'] if s['kind']=='TEACH'];assert len(teach)==11 and all(s['body'] in repair['body'] for s in teach);assert set(repair['exampleIds'])=={e for s in teach for e in s.get('exampleIds',[])} and len(repair['exampleIds'])==6;assert [x['item'] for x in rows if x['decision']=='SELECT_EXISTING_PRESENT_POOL']==present['items'];assert {items[i]['answer'] for i in present['items']}=={'говори́м','ви́дит','пи́шете','иду́т'}
registered=re.findall(r'internal object (\w+CumulativeReviewPolicy)\s*\{',blob(KERNEL).decode());assert len(registered)==36
con=json.loads(blob(PRE+'CONSERVATION.json'));assert len(con['archives'])==70;fixtures=[]
for path,h in con['archives'].items():
 data=blob(path);assert sha(data)==h and sha((R/path).read_bytes())==h
 with zipfile.ZipFile(io.BytesIO(data)) as z:
  assert z.testzip() is None
  if 'learner.json' not in z.namelist():continue
  raw=z.read('learner.json');doc=json.loads(raw);assignments=[a for a in doc.get('reviewAssignments',[]) if a['targetUnit']==10]
  if not assignments:continue
  ids={a['id'] for a in assignments};fixtures.append(dict(zipPath=path,zipSha256=h,learnerSha256=sha(raw),assignments=assignments,answers=[a for a in doc.get('reviewAnswers',[]) if a['assignmentId'] in ids],completions=[x for x in doc.get('unitCompletions',[]) if x['unitNumber']==10]))
protected=dict(con['archives']);protected.update(con['immutablePriorEvidence']);protected.update(json.loads(blob(PRE+'APPLIED_RECORDS.json'))['currentRecordHashes']);assert all(sha(blob(n))==h for n,h in protected.items())
for n in git('ls-tree','-r','--name-only',BASE,'--',PRE).decode().splitlines():protected[n]=sha(blob(n))
plan=[
('B01','Actual current positive/contrast baseline','Use actual current Units1–9 completion/current due reviews and current Unit10 ordinary requirements. Unit10v1/schema1 has two slots. Earlier Unit4 present knowledge and Unit6/8 reviews already exist. Contrast a proposed new assigned present answer,not ordinary knowledge or a current-gate bypass.'),
('B02','Genuine current partial schema1 backup','Before policy changes,answer slot0 of a real current Unit10 task;export/read/replace/named reopen/complete actual old obligation. Preserve source/ZIP/memberSHA/CRC/slots/answers/digests. Do not edit old ZIPs or relabel schemas;backup format differs from assignment schema.'),
('F01','Versioned policy only after verified baseline','Proposed schema12 target10 only;new v2 has exact Unit6Present.pool[0] then exact Unit10v1.pool[1]. Preserve all36old policy objects and schemas1–11 meanings. No new Russian/options/answers/skill enum/source admission.'),
('F02','Fresh assignment and selection','Missing/wrong new due answer refuses actual advance;ordinary/optional/earlier reviews cannot substitute. Test all4CHECK-miss candidates/least-recent selection,accepted answer value/order uniqueness/idempotence/rollback/epoch/backup/named reopen. Do not infer balanced raw keys or proven spacing.'),
('F03','Missing owners and full repair','Actual missing current Unit4/9 teaching/source must route to real owner;full present repair contains11TEACH bodies/6examples. Practice return grants no due-review credit. Use genuine completed prerequisites,not injected raw-credit shortcuts.'),
('F04','Historical and downstream owner10 conservation','Genuine current v1/schema1 task keeps exact old slots/digests/answers/cutoffs/title/completion through backup/reopen;new schema under old declaration refused. New Unit10 declaration changes owner10 identity for Unit11:only exact prior declaration compatibility,with changed items/teaching/rules still refused.'),
('F05','Bounded labels and integration','Fresh title supplied present forms/numeral agreement;historical title reading/numeral agreement retained. Official Kotlin digest/two authentic renders/155record field conservation/70prior archives plus genuine capture;then affected integration before justified broad. No independent finding closure.')]
plan=[dict(id=i,scenario=t,instructions=s,executionStatus='PLANNED_NOT_EXECUTED',testMethodIdentity='NOT_CREATED') for i,t,s in plan]
summary=dict(sourceCommit=BASE,status='PROPOSED_NOT_ACTIVATED',activated=False,objectiveChains=13,assessedChecks=47,selectedPresentCandidates=present['items'],proposedPools=proposed,proposedId='unit10-prior-present-numeral-gender-v2',proposedFreshSchema=12,currentUnit10=current,registeredPolicyObjectsPreserved=36,presentRepairTeachingCards=11,presentRepairExamples=6,rawPresentKeyPositions=[0,0,0,0],presentAlreadyAssignedAtTargets=[6,8],heldOutItemsAdded=0,historicalArchivesChecked=70,archivesContainingUnit10=len(fixtures),printSingletonPoliciesStillCurrent=23,newRuntimeMethodsExecuted=0,linguisticRightsMappingOwnerAcceptance='PENDING_NOT_GRANTED',lint='NOT_RUN_RESEARCH_ONLY',releaseGateAttachment='NOT_RUN_RESEARCH_ONLY',apkIdentity=None,apkSignature=None,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2})
outputs={'CHAINS.json':enc(dict(sourceCommit=BASE,sourcePoolIntegrityVerified=True,chains=chains)),'CANDIDATE_ASSESSMENT.csv':csvbytes(rows),'BASELINE_AND_ACCEPTANCE_PLAN.csv':csvbytes(plan),'CURRENT_POLICY_INVENTORY.json':enc(dict(sourceCommit=BASE,reusedCompiledObservationPath=CORE+'CURRENT_POLICY_OBSERVATION.json',reusedCompiledObservationSha256=sha(blob(CORE+'CURRENT_POLICY_OBSERVATION.json')),producerSource=observation['sourceCommit'],appMainTreeMatchesProducer=True,activePolicies=28,objectivePools=59,candidatePlacements=93,uniqueCandidateItems=48,printSingletonPolicies=23,unitsWithPrintSingleton=observation['unitsWithPrintSingleton'],currentUnit10=current,currentUnit10Pools=old_pools,reusedPresentPool=present,registeredPolicyObjects=registered)),'HISTORICAL_UNIT10_INVENTORY.json':enc(dict(sourceCommit=BASE,archivesChecked=70,archivesContainingUnit10=len(fixtures),scope='Historical byte inventory,not new current capture/replay or JUnit count',fixtures=fixtures)),'PROTECTED_EVIDENCE_HASHES.json':enc(protected),'SUMMARY.json':enc(summary),'INPUTS.json':enc(dict(sourceCommit=BASE,sourceRootTree=git('rev-parse',BASE+'^{tree}').decode().strip(),inputTrees={n:git('rev-parse',BASE+':'+n).decode().strip() for n in ['app/src/main','app/src/test','catalog','docs','review','release']},rawCurriculumSha256=sha(blob(RAW)),kernelSha256=sha(blob(KERNEL)),masterAuthoritySha256=sha(blob('docs/governance/MASTER_AUTHORITY_v2.0.md')),privatePdf='NOT_READ_OR_PACKAGED',newRuntimeMethods=0))}
outputs['GENERATED_SHA256.json']=enc({n:sha(b) for n,b in outputs.items()});check=sys.argv[1:]==['--check'];assert sys.argv[1:] in [[],['--check']]
for n,b in outputs.items():
 if check:assert (HERE/n).read_bytes()==b,n
 else:assert not (HERE/n).exists(),n;(HERE/n).write_bytes(b)
print(json.dumps(dict(verified=check,generated=not check,chains=13,checks=47,selectedPresentItems=4,archivesChecked=70,archivesContainingUnit10=len(fixtures),runtimeMethodsExecuted=0)))
