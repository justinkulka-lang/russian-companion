"""Pinned, reproducible Unit9 engineering research; --check writes nothing and runs no tests."""
from pathlib import Path
import subprocess,json,hashlib,base64,csv,io,zipfile,sys,re
BASE='5ba4076382050b02dc51e0dcf022e9a138f826b4';HERE=Path(__file__).parent;R=Path(subprocess.check_output(['git','rev-parse','--show-toplevel'],text=True).strip());B='audit/gpt61-sol-high-comprehensive-2026-10-19/';PRE=B+'engineering-unit8-source-reconciliation-2026-10-24/';CORE=B+'engineering-unit8-present-correction-2026-10-24/';RAW='app/src/main/assets/content/v2-curriculum.json';KERNEL='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
OBJECTIVES=['obj-u1-decoding','obj-u3-gender','obj-u4-nominative','obj-u5-indeclinable','obj-u6-animacy','obj-u7-endings','obj-u8-plural','obj-u8-irregular','obj-u9-odindva','obj-u9-quantity']
def git(*a):return subprocess.check_output(['git',*a],cwd=R)
def blob(n):return git('show',BASE+':'+n)
def sha(b):return hashlib.sha256(b).hexdigest()
def enc(v):return (json.dumps(v,ensure_ascii=False,indent=2)+'\n').encode()
def csvbytes(rows):
 o=io.StringIO(newline='');w=csv.DictWriter(o,fieldnames=list(rows[0]),lineterminator='\n');w.writeheader();w.writerows(rows);return o.getvalue().encode()
p=json.loads(blob(RAW));items={x['id']:x for x in p['items']};examples={x['id']:x for x in p['examples']};references={x['id']:x for x in p['references']};pool=p.get('sourceTextPool',{});assert all(sha(v.encode())==k for k,v in pool.items());assert git('rev-parse','370d8716e2747c439da1a450a83b71b129a5c879:app/src/main')==git('rev-parse',BASE+':app/src/main')
chains=[]
for oid in OBJECTIVES:
 u=next(u for u in p['units'] if any(o['id']==oid for o in u['objectives']));o=next(o for o in u['objectives'] if o['id']==oid);allsteps={s['id']:s for ss in u['sessions'] for s in ss['steps']};steps=[allsteps[s] for s in dict.fromkeys(o['instructionStepIds']+o['practiceStepIds']+o['remediationStepIds'])];ii=list(dict.fromkeys(o['checkItemIds']+[i for s in steps for i in s.get('itemIds',[])]));ee=list(dict.fromkeys(o['exampleIds']+[i for s in steps for i in s.get('exampleIds',[])]));rr=list(dict.fromkeys(i for s in steps for i in s.get('referenceIds',[])));provenance=[x for x in p['provenanceRecords'] if x.get('objectiveId')==oid];sources=sorted({items[i]['sourceId'] for i in ii}|{x['sourceId'] for x in provenance});pins=[]
 for x in provenance:
  pin={k:x[k] for k in ['itemId','recordType','sourceId','sourceUrl','sourceRevision','sourceRecordId','sourceHash','contentHash','reviewStatus','licenseId','licenseUrl'] if k in x};pin['canonicalFullRecordSha256']=sha(enc(x))
  if 'originalSourceTextRef' in x:
   k=x['originalSourceTextRef'];assert k==x['sourceHash'] and k in pool;pin['resolvedPoolSha256']=sha(pool[k].encode());pin['resolvedPoolBytes']=len(pool[k].encode())
  pins.append(pin)
 chains.append(dict(unit=u['number'],objective=o,steps=steps,items=[items[i] for i in ii],examples=[examples[i] for i in ee],references=[references[i] for i in rr],provenancePins=pins,sourceRecords=[x for x in p['sources'] if x['id'] in sources],rightsRecords=[x for x in p['rights'] if x['sourceId'] in sources],capabilities=[x for x in p['sourceCapabilities'] if x.get('objectiveId')==oid]))
observation=json.loads(blob(CORE+'CURRENT_POLICY_OBSERVATION.json'));assert (observation['activePolicies'],observation['objectivePools'],observation['candidatePlacements'],observation['uniqueCandidateItems'],observation['printSingletonPolicies'])==(28,59,89,48,24);current=next(x for x in observation['policies'] if x['unit']==9);old_pools=[x for x in observation['pools'] if x['unit']==9];noun=next(x for x in observation['pools'] if x['unit']==6 and x['slot']==1);assert current['schema']==5 and current['id']=='unit9-prior-reading-plural-gender-v2';assert [x['objective'] for x in old_pools]==['obj-u1-decoding','obj-u8-plural','obj-u3-gender'];assert noun['objective']=='obj-u5-indeclinable' and len(noun['items'])==5
proposed=[dict(noun,unit=9,slot=0)]+old_pools[1:];by={x['objective']['id']:x for x in chains}
notes={
'obj-u1-decoding':('REPLACE_PRINT_SINGLETON','Print-case recognition does not establish word reading or noun behavior; CC002/CC027 foundation gaps remain.'),
'obj-u3-gender':('RETAIN_CURRENT_GENDER_POOL','Retain all five exact existing candidates/repair/order. Supplied lexical gender supports one/two context; not generated numeral agreement.'),
'obj-u4-nominative':('DEFER_SUBJECT_ROLE_CONTEXT','Valid bounded subject-role evidence, but less direct than noun behavior for Unit9 case/quantity forms; already reviewed in Units5/6.'),
'obj-u5-indeclinable':('SELECT_EXISTING_NOUN_BEHAVIOR_POOL','Five finite binary classifications, all explicit in full teaching/repair. Already reused in Unit6; no new or held-out items and no productive inflection claim.'),
'obj-u6-animacy':('DEFER_CASE_SCOPE','Earlier case contrast is useful but not genitive quantity evidence; inanimate singleton already assigned by Unit7; do not broaden to animate forms here.'),
'obj-u7-endings':('DEFER_MIXED_CASE_ENDINGS','Mixed adjective/case/number checks require separate scope; earlier genitive-teaching concerns remain unclosed. Do not infer mastery from enum.'),
'obj-u8-plural':('RETAIN_CURRENT_PLURAL_SINGLETON','Keep exact u8-i-plural-2/u8-st-rem-plural. Supplied nominative plural is not genitive plural or unseen production.'),
'obj-u8-irregular':('DEFER_LEXICAL_PLURAL_DUPLICATION','Additional irregular plural selection overlaps retained plural skill and immediate Unit8; not a substitute independent earlier skill.'),
'obj-u9-odindva':('CURRENT_TARGET_NOT_PRIOR','Current Unit9 ordinary one/two teaching/checks remain mandatory,not a prior cumulative pool.'),
'obj-u9-quantity':('CURRENT_TARGET_NOT_PRIOR','Current Unit9 ordinary quantity/genitive work remains mandatory; no invented earlier genitive paradigm or pooled current credit.')}
rows=[]
for c in chains:
 guided={i for s in c['steps'] if s['kind']=='GUIDED' for i in s.get('itemIds',[])};ii={x['id']:x for x in c['items']};signature=lambda i:(i.get('prompt'),i.get('promptLetter'),tuple(sorted(i['options'])),i['answer'])
 for iid in c['objective']['checkItemIds']:
  i=ii[iid];decision,limit=notes[c['objective']['id']]
  if c['objective']['id']=='obj-u8-plural' and iid!='u8-i-plural-2':decision='DEFER_PLURAL_POOL_EXPANSION';limit='Additional plural candidates remain ordinary Unit8 work; do not change retained obligation in this slice.'
  rows.append(dict(unit=c['unit'],objective=c['objective']['id'],item=iid,decision=decision,limit=limit,options=len(i['options']),rawKeyIndex=i['options'].index(i['answer']),sameTupleAsGuided=';'.join(g for g in sorted(guided) if signature(ii[g])==signature(i)),sourceId=i['sourceId'],reviewStatus=i['reviewStatus'],itemSha256=sha(enc(i))))
assert len(chains)==10 and len(rows)==43
n=by['obj-u5-indeclinable'];repair=next(s for s in n['steps'] if s['id']=='u5-st-rem-indeclinable');assert all(s['body'] in repair['body'] for s in n['steps'] if s['kind']=='TEACH');assert set(repair['exampleIds'])=={e for s in n['steps'] if s['kind']=='TEACH' for e in s.get('exampleIds',[])};assert [x['item'] for x in rows if x['decision']=='SELECT_EXISTING_NOUN_BEHAVIOR_POOL']==noun['items']
con=json.loads(blob(PRE+'CONSERVATION.json'));assert len(con['archives'])==69;fixtures=[]
for path,h in con['archives'].items():
 b=(R/path).read_bytes();assert sha(b)==h
 with zipfile.ZipFile(io.BytesIO(b)) as z:
  assert z.testzip() is None
  if 'learner.json' not in z.namelist():continue
  raw=z.read('learner.json');doc=json.loads(raw);assignments=[a for a in doc.get('reviewAssignments',[]) if a['targetUnit']==9]
  if not assignments:continue
  ids={a['id'] for a in assignments};fixtures.append(dict(zipPath=path,zipSha256=h,learnerSha256=sha(raw),assignments=assignments,answers=[a for a in doc.get('reviewAnswers',[]) if a['assignmentId'] in ids],completions=[x for x in doc.get('unitCompletions',[]) if x['unitNumber']==9]))
protected=dict(con['archives']);protected.update(con['immutablePriorEvidence']);record_hashes=json.loads(blob(PRE+'APPLIED_RECORDS.json'))['currentRecordHashes'];protected.update(record_hashes);assert all(sha(blob(n) if n in record_hashes else (R/n).read_bytes())==h for n,h in protected.items())
for n in git('ls-tree','-r','--name-only',BASE,'--',PRE).decode().splitlines():protected[n]=sha(blob(n))
scenarios=[('B01','Actual current preparation','Complete genuine current Units1–8 ordinary work/book/due reviews,then all Unit9 ordinary requirements. Record actual Unit9v2/schema5/three slots and owner/source identities.'),('B02','Proposed assignment-bound noun-behavior contrast','Answer all three real old slots and attempt actual Unit9 advance. Assert proposed new due noun-behavior obligation separately; preserve actual intentional baseline failure if reproduced. Earlier ordinary/Unit6 noun knowledge already exists; do not call this ordinary-knowledge or current-gate bypass.'),('B03','Genuine current partial v2 backup','Answer slot0 of real schema5 task,export supported backup/read/replace/named reopen and complete actual old obligation; preserve producer/ZIP/memberSHA/CRC/actual slots/digests/accepted orders. Backup format is not assignment schema; no recordings/device claim.'),('F01','Explicit new policy/schema only after baseline','Proposed schema11 target9 only: noun behavior + exact old plural + exact old gender. No new Russian/options/answers; preserve all35registered old objects and schemas1–10 meanings.'),('F02','Fresh assigned due answer and selection','Wrong/missing new slot refuses actual advance; ordinary,optional,earlier review answers cannot supply new assignment credit. Governed latest-miss/least-recent CHECK selection varies within five items; all targets/options scored by value. No cross-review-spacing or held-out claim.'),('F03','Missing owners/repair eligibility','With actual prerequisite completion,missing Unit5/8/3 current teaching/source routes to the true missing owner. Full Unit5 repair and separate practice returns without due credit. Do not fake current completion using raw rows.'),('F04','Historical policy compatibility','Actual B03schema5 and original schema1 obligations retain slots/digests/boundary/cutoffs/labels/answers/completion snapshots; no resampling or forward schema11 under old declarations. Unknown/other-target schemas refused.'),('F05','Owner9/downstream conservation','Versioned Unit9 declaration changes whole owner identity; prove exact pre-declaration pack for genuine saved Unit9→Unit10 histories,while changed items/teaching/rules still refuse. Preserve backup/replace/reopen and current fresh progression.'),('F06','Atomicity and labels','Test epoch refusal,idempotence,accepted-order uniqueness,rollback on actual advance failure,read/replace/named reopen,saved/current title and wrong-answer repair. Titles say supplied noun behavior/plural/gender; no reading/production claim.'),('F07','Authentic bindings and integration after implementation','Official Kotlin digest; two authentic renders; preserve155current prior versions/reviewer/owner/rights/admission fields; scoped current/historical integration before any justified broad. Original failures/archives retained; no independent closure.')]
plan=[dict(id=i,scenario=t,instructions=s,executionStatus='PLANNED_NOT_EXECUTED',testMethodIdentity='NOT_CREATED') for i,t,s in scenarios]
summary=dict(sourceCommit=BASE,selectedNounCandidates=noun['items'],retainedPluralPool=old_pools[1],retainedGenderPool=old_pools[2],proposedPools=proposed,proposedId='unit9-prior-noun-behavior-plural-gender-v3',proposedFreshSchema=11,status='PROPOSED_NOT_ACTIVATED',activated=False,objectiveChains=10,assessedChecks=43,historicalArchivesChecked=69,archivesContainingUnit9=len(fixtures),currentPolicy=current,printSingletonPoliciesStillCurrent=24,currentUnit3And4FoundationGaps='CC002_CC027_OPEN',heldOutItemsAdded=0,qualifications='Engineering trace only; linguistic/source-rights/mapping/owner acceptance remains pending',runtimeMethodsExecuted=0,lint='NOT_RUN_RESEARCH_ONLY',releaseGateAttachment='NOT_RUN_RESEARCH_ONLY',apkIdentity=None,apkSignature=None,physicalAcceptance='OUTSTANDING_LAST',all48Findings='OPEN',audit='INCOMPLETE',exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2})
outputs={
'CHAINS.json':enc(dict(sourceCommit=BASE,sourcePoolIntegrityVerified=True,chains=chains)),
'CANDIDATE_ASSESSMENT.csv':csvbytes(rows),
'BASELINE_AND_ACCEPTANCE_PLAN.csv':csvbytes(plan),
'CURRENT_POLICY_INVENTORY.json':enc(dict(sourceCommit=BASE,reusedCompiledObservationPath=CORE+'CURRENT_POLICY_OBSERVATION.json',reusedCompiledObservationSha256=sha(blob(CORE+'CURRENT_POLICY_OBSERVATION.json')),producerSource=observation['sourceCommit'],appMainTreeMatchesProducer=True,activePolicies=28,objectivePools=59,candidatePlacements=89,uniqueCandidateItems=48,printSingletonPolicies=24,unitsWithPrintSingleton=observation['unitsWithPrintSingleton'],currentUnit9=current,currentUnit9Pools=old_pools,reusedNounBehaviorPool=noun)),
'HISTORICAL_UNIT9_INVENTORY.json':enc(dict(sourceCommit=BASE,archivesChecked=69,archivesContainingUnit9=len(fixtures),scope='Historical bytes inventory,not new replay/current baseline or JUnit count',fixtures=fixtures)),
'PROTECTED_EVIDENCE_HASHES.json':enc(protected),
'SUMMARY.json':enc(summary),
'INPUTS.json':enc(dict(sourceCommit=BASE,sourceRootTree=git('rev-parse',BASE+'^{tree}').decode().strip(),inputTrees={n:git('rev-parse',BASE+':'+n).decode().strip() for n in ['app/src/main','app/src/test','catalog','docs','review','release']},rawCurriculumSha256=sha(blob(RAW)),kernelSha256=sha(blob(KERNEL)),privatePdf='NOT_READ_OR_PACKAGED',newRuntimeMethods=0))}
outputs['GENERATED_SHA256.json']=enc({n:sha(b) for n,b in outputs.items()});check=sys.argv[1:]==['--check'];assert sys.argv[1:] in [[],['--check']]
for n,b in outputs.items():
 if check:assert (HERE/n).read_bytes()==b,n
 else:
  assert not (HERE/n).exists(),n;(HERE/n).write_bytes(b)
print(json.dumps(dict(verified=check,generated=not check,chains=10,checks=43,selectedNounItems=5,archivesChecked=69,archivesContainingUnit9=len(fixtures),runtimeMethodsExecuted=0)))
