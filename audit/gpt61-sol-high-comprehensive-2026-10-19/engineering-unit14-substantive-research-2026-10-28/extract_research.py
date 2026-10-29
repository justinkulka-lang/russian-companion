"""Pinned Unit14 research. --check reproduces outputs; no app/test/Gradle/network/PDF changes."""
from pathlib import Path
from functools import lru_cache
import collections,csv,hashlib,io,json,re,subprocess,sys,zipfile

BASE='2926452c190bac07b1363788ae555322f9b22eae'
D=Path(__file__).resolve().parent
R=next(p for p in D.parents if (p/'.git').exists())
B='audit/gpt61-sol-high-comprehensive-2026-10-19/'
BROAD=B+'engineering-unit13-broad-host-regression-2026-10-27/'
CORE=B+'engineering-unit13-present-correction-2026-10-27/'
RAW='app/src/main/assets/content/v2-curriculum.json'
KERNEL='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
def git(*a):return subprocess.check_output(['git',*a],cwd=R)
@lru_cache(None)
def blob(n):return git('show',BASE+':'+n)
def sha(b):return hashlib.sha256(b).hexdigest()
def enc(x):return (json.dumps(x,ensure_ascii=False,indent=2)+'\n').encode()
def csv_bytes(rows):
    s=io.StringIO(newline='');w=csv.DictWriter(s,fieldnames=list(rows[0]),lineterminator='\n');w.writeheader();w.writerows(rows);return s.getvalue().encode()

p=json.loads(blob(RAW));items={i['id']:i for i in p['items']};examples={e['id']:e for e in p['examples']};refs={r['id']:r for r in p['references']};sourcePool=p['sourceTextPool']
assert all(sha(v.encode())==k for k,v in sourcePool.items())
observationPath=CORE+'CURRENT_POLICY_OBSERVATION.json';ob=json.loads(blob(observationPath))
assert git('rev-parse',ob['sourceCommit']+':app/src/main')==git('rev-parse',BASE+':app/src/main')
currentCounts=dict(activePolicies=ob['activePolicies'],objectivePools=ob['pools'],candidatePlacements=ob['placements'],uniqueCandidateItems=ob['uniqueCandidates'],printSingletonPolicies=ob['printSingletonPolicies'])
assert list(currentCounts.values())==[28,59,112,52,19]
flat=[dict(unit=q['unit'],slot=n,**pool) for q in ob['policies'] for n,pool in enumerate(q['pools'])]
current14=next(q for q in ob['policies'] if q['unit']==14);assert current14['id']=='unit14-prior-reading-reported-time-v1'
assert current14['pools'][0]['candidates']==['u1-i-decode-pair'] and current14['pools'][1]['candidates']==['u13-i-report-frame-time']
pluralIds=[f'u8-i-plural-{n}' for n in range(1,7)];possessionIds=[f'u10-i-have-{n}' for n in range(1,5)]
proposal=[dict(unit=14,slot=0,ownerUnit=8,objective='obj-u8-plural',skill='SUPPLIED_NOMINATIVE_PLURAL_RECOGNITION',candidates=pluralIds,remediation='u8-st-rem-plural',reuse='Unit9CumulativeReviewPolicy.definition.pool[1].copy(candidateItemIds=allSixExistingChecks)'),dict(unit=14,slot=1,ownerUnit=10,objective='obj-u10-have',skill='SUPPLIED_POSSESSION_ABSENCE_RECOGNITION',candidates=possessionIds,remediation='u10-st-rem-have',reuse='Unit11NounCumulativeReviewPolicy.definition.pool[1]'),dict(unit=14,slot=2,ownerUnit=13,objective='obj-u13-reported',skill='SUPPLIED_REPORTED_FRAME_TIME_RECOGNITION',candidates=['u13-i-report-frame-time'],remediation='u13-st-rem-reported',reuse='Unit14CumulativeReviewPolicy.definition.pool[1]')]
selected={q['objective']:q for q in proposal};selectedIds={i for q in proposal for i in q['candidates']}
assert len(selectedIds)==11 and [items[i]['objectiveId'] for i in pluralIds]==['obj-u8-plural']*6
assert next(q for q in ob['policies'] if q['unit']==11)['pools'][1]['candidates']==possessionIds
projected=[q for q in flat if q['unit']!=14]+proposal
projection=dict(activePolicies=28,objectivePools=len(projected),candidatePlacements=sum(len(q['candidates']) for q in projected),uniqueCandidateItems=len({i for q in projected for i in q['candidates']}),printSingletonPolicies=len({q['unit'] for q in projected if q['candidates']==['u1-i-decode-pair']}))
assert list(projection.values())==[28,60,121,57,18]

chains=[];rows=[];byObjective={}
for u in p['units']:
    if u['number']>14:continue
    allSteps={s['id']:s for se in u['sessions'] for s in se['steps']}
    for o in u['objectives']:
        if not o['checkItemIds']:continue
        oid=o['id'];steps=[allSteps[k] for k in dict.fromkeys(o['instructionStepIds']+o['practiceStepIds']+o['remediationStepIds'])]
        ii=list(dict.fromkeys(o['checkItemIds']+[i for s in steps for i in s.get('itemIds',[])]));ee=list(dict.fromkeys(o['exampleIds']+[i for s in steps for i in s.get('exampleIds',[])]));rr=list(dict.fromkeys(i for s in steps for i in s.get('referenceIds',[])))
        pins=[]
        for record in p['provenanceRecords']:
            if record.get('objectiveId')!=oid:continue
            pin={k:record[k] for k in ['itemId','recordType','sourceId','sourceUrl','sourceRevision','sourceRecordId','sourceHash','contentHash','reviewStatus','licenseId','licenseUrl'] if k in record};pin['canonicalFullRecordSha256']=sha(enc(record))
            if 'originalSourceTextRef' in record:
                ref=record['originalSourceTextRef'];assert ref==record['sourceHash'] and ref in sourcePool;pin['sourcePoolSha256Verified']=True
            elif 'originalSourceText' in record:assert sha(record['originalSourceText'].encode())==record['sourceHash']
            pins.append(pin)
        sourceIds={items[i]['sourceId'] for i in ii}|{r['sourceId'] for r in pins};teach=[s for s in steps if s['kind']=='TEACH'];repair=allSteps[o['remediationStepIds'][0]] if o['remediationStepIds'] else None
        chain=dict(unit=u['number'],objective=o,steps=steps,items=[items[i] for i in ii],examples=[examples[i] for i in ee],references=[refs[i] for i in rr],provenancePins=pins,sourceRecords=[s for s in p['sources'] if s['id'] in sourceIds],rightsRecords=[r for r in p['rights'] if r['sourceId'] in sourceIds],capabilities=[c for c in p['sourceCapabilities'] if c.get('objectiveId')==oid],teachingCards=len(teach),teachingExampleIds=sorted({i for s in teach for i in s.get('exampleIds',[])}),repairExampleIds=repair.get('exampleIds',[]) if repair else [],allTeachingBodiesInRepair=bool(repair) and all(s['body'] in repair['body'] for s in teach),capabilityLimit='Existing objects/metadata only;absence does not grant new capability or qualified acceptance.')
        chains.append(chain);byObjective[oid]=chain
        signature=lambda i:(i.get('prompt'),i.get('promptLetter'),tuple(sorted(i['options'])),i['answer'])
        guided=[items[i] for s in steps if s['kind']=='GUIDED' for i in s.get('itemIds',[])]
        for key in o['checkItemIds']:
            item=items[key];a=item.get('assessment') or {}
            choice='SELECT_EXISTING_CHECK' if key in selectedIds else 'DEFER_ALTERNATIVE'
            if u['number']==14:choice='TARGET_ORDINARY_NOT_PRIOR_CUMULATIVE'
            rows.append(dict(unit=u['number'],objective=oid,item=key,decision=choice,rawKeyIndex=item['options'].index(item['answer']),sameTupleAsGuided=';'.join(i['id'] for i in guided if signature(i)==signature(item)),testedFeature=a.get('testedFeature',''),responseKind=a.get('responseKind',''),sourceId=item['sourceId'],reviewStatus=item['reviewStatus'],itemSha256=sha(enc(item)),scope='Mechanical inventory;not qualified item audit or executed test'))
assert len(chains)==72 and len(rows)==226
for q,cards,ee in [(proposal[0],8,14),(proposal[1],5,7),(proposal[2],3,4)]:
    chain=byObjective[q['objective']];assert chain['teachingCards']==cards and len(chain['teachingExampleIds'])==ee and chain['allTeachingBodiesInRepair'] and set(chain['teachingExampleIds'])==set(chain['repairExampleIds'])
assert byObjective['obj-u8-plural']['objective']['checkItemIds']==pluralIds and byObjective['obj-u10-have']['objective']['checkItemIds']==possessionIds
assert [items[i]['assessment']['testedFeature'] for i in pluralIds]==['PLURAL_FORMATION']*6
assert items['u13-i-report-frame-time']['assessment']['testedFeature']=='REPORTED_FRAME_EMBEDDED_TIME'
assert not any(r['sameTupleAsGuided'] for r in rows if r['item'] in selectedIds)

kernel=blob(KERNEL).decode();objects=list(re.finditer(r'internal object (\w+CumulativeReviewPolicy)\s*\{(.*?)\n\}',kernel,re.S));assert len(objects)==40
objectHashes={m.group(1):sha(m.group().encode()) for m in objects};schemas={k:int(v) for k,v in re.findall(r'internal const val (\w*SCHEMA)\s*=\s*(\d+)',kernel)};assert sorted(schemas.values())==list(range(1,16))
assert 'unit14-prior-plural-possession-reported-time-v2' not in kernel
assert '14 -> Unit14CumulativeReviewPolicy.definition' in kernel
title=re.search(r'    14 -> "([^"]+)"',kernel).group(1)
unit14=next(u for u in p['units'] if u['number']==14);assert unit14['advancement']['cumulativeReviewPolicyId']==current14['id']
checks14=[i for o in unit14['objectives'] for i in o['checkItemIds']];guided14=[i for se in unit14['sessions'] for s in se['steps'] if s['kind']=='GUIDED' for i in s.get('itemIds',[])]
assert len(checks14)==6 and len(guided14)==3 and unit14['advancement']['minimumDistinctChecks']==6
downstream14=[q for q in flat if any(items[i]['objectiveId'].startswith('obj-u14-') for i in q['candidates'])]
assert [(q['unit'],q['objective'],q['candidates']) for q in downstream14]==[(15,'obj-u14-impersonal',['u14-i-impersonal-2'])]

prep=json.loads(blob(BROAD+'PREPARATION.json'));assert len(prep['archives'])==74 and len(prep['currentRecordHashes'])==155 and len(prep['existingTestFiles'])==276
archives=[];fixtures={14:[],15:[]};duplicates=collections.defaultdict(list)
for name,h in prep['archives'].items():
    data=blob(name);assert sha(data)==h
    with zipfile.ZipFile(io.BytesIO(data)) as z:
        assert not any(n.lower().endswith('.pdf') for n in z.namelist());assert z.testzip() is None
        archives.append(dict(path=name,sha256=h,bytes=len(data),members=len(z.namelist()),crcVerified=True))
        if 'learner.json' not in z.namelist():continue
        raw=z.read('learner.json');doc=json.loads(raw);canonical=sha(json.dumps(doc,ensure_ascii=False,sort_keys=True,separators=(',',':')).encode());duplicates[canonical].append(name)
        for target in [14,15]:
            tasks=[a for a in doc.get('reviewAssignments',[]) if a['targetUnit']==target]
            if not tasks:continue
            ids={a['id'] for a in tasks};fixtures[target].append(dict(zipPath=name,zipSha256=h,learnerSha256=sha(raw),canonicalLearnerSha256=canonical,assignments=tasks,answers=[a for a in doc.get('reviewAnswers',[]) if a['assignmentId'] in ids],completions=[c for c in doc.get('unitCompletions',[]) if c['unitNumber']==target],recordingMembers=sum(not i.is_dir() and i.filename.startswith('recordings/') for i in z.infolist()),manifest=json.loads(z.read('manifest.json')) if 'manifest.json' in z.namelist() else None))
history={str(n):dict(collections.Counter(f"{a['schema']}:{a['policyId']}" for f in fixtures[n] for a in f['assignments'])) for n in [14,15]}
assert set(history['14'])<= {'1:unit14-prior-reading-reported-time-v1'} and set(history['15'])<= {'1:unit15-prior-reading-impersonal-state-v1'}
owners={str(n):dict(collections.Counter(s['unitDigest'] for f in fixtures[n] for a in f['assignments'] for s in a['slots'] if s['ownerUnit']==n-1)) for n in [14,15]}
standalone=[]
for name in git('ls-tree','-r','--name-only',BASE,'--','audit/independent-gpt61-2026-10-12').decode().splitlines():
    if not name.endswith('.json') or not any('/ia003-unit'+str(n)+'-' in name for n in [14,15]):continue
    doc=json.loads(blob(name))
    if not isinstance(doc,dict) or not isinstance(doc.get('reviewAssignments'),list):continue
    canonical=sha(json.dumps(doc,ensure_ascii=False,sort_keys=True,separators=(',',':')).encode());tasks=[a for a in doc['reviewAssignments'] if a['targetUnit'] in [14,15]]
    if tasks:standalone.append(dict(path=name,sha256=sha(blob(name)),canonicalLearnerSha256=canonical,matchingArchivePaths=duplicates.get(canonical,[]),assignments=tasks))

bindingPaths=sorted(prep['currentRecordHashes']);assert all(sha(blob(n))==h for n,h in prep['currentRecordHashes'].items())
manifests={}
for n in [8,10,13,14,15]:
    name=f'review/unit-{n}/UNIT_{n}_REVIEW_MANIFEST.json';data=json.loads(blob(name));manifests[str(n)]=dict(path=name,sha256=sha(blob(name)),gitBlob=git('rev-parse',BASE+':'+name).decode().strip(),data=data)
candidate=json.loads(blob('release/acceptance/candidate.json'));assert candidate['sourceDigest']==prep['repositorySourceDigest'] and not candidate['frozen'] and candidate['sourceCommit'] is None
refPath='app/src/test/resources/reviewed-curriculum-cyrillic.json';ref=json.loads(blob(refPath));kr=next(x for x in ref['files'] if x['path']=='src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt');assert kr['sha256']==sha(blob(KERNEL))
protected={**prep['archives'],**prep['currentRecordHashes'],**prep['existingTestFiles']}
protected.update({name:sha(blob(name)) for name in [RAW,KERNEL,refPath,'release/acceptance/candidate.json','docs/governance/MASTER_AUTHORITY_v2.0.md','docs/governance/MASTER_AUTHORITY_v2.0_AMENDMENT_1.md']})
for name in git('ls-tree','-r','--name-only',BASE,'--',BROAD).decode().splitlines():protected[name]=sha(blob(name))
assert all(sha(blob(n))==h for n,h in protected.items())
decisionRows=[]
for c in chains:
    oid=c['objective']['id'];unit=c['unit'];d='DEFER_ALTERNATIVE';why='Not selected for this bounded three-pool proposal;no new skill label or source admission inferred.'
    if oid=='obj-u8-plural':d='PROPOSE_ALL_SIX_EXISTING_CHECKS';why='Earlier morphology objective beyondN-1;expand the existing singleton under the same precise skill,with complete8-card14-example repair.'
    elif oid=='obj-u10-have':d='REUSE_EXACT_EXISTING_FOUR_ITEM_POOL';why='Earlier clause/case interpretation beyondN-1;reuse Unit11Noun pool1 unchanged;not automatic mastery of case production.'
    elif oid=='obj-u13-reported':d='RETAIN_EXACT_EXISTING_REPORTED_TIME_POOL';why='Keep only frame/embedded-time item;direct evening and Moscow choices test different features and are not extra reported-time candidates.'
    elif oid=='obj-u4-present':why='Feasible finite pool,but immediately reused in Unit13v3 and Units6/8/10;defer here to vary earlier objectives.'
    elif oid=='obj-u5-indeclinable':why='Feasible five-item existing pool;defer in favor of plural morphology plus possession/clause interpretation,not a source rejection.'
    elif unit==14:d='TARGET_ORDINARY_NOT_PRIOR';why='Unit14 ordinary six checks/three guided tasks remain separate;not earlier-objective review.'
    decisionRows.append(dict(unit=unit,objective=oid,decision=d,reason=why))
broad=json.loads(blob(BROAD+'REGRESSION_RESULT.json'));context=json.loads(blob(BROAD+'REGRESSION_CONTEXT.json'))
summary=dict(sourceCommit=BASE,proposalStatus='NOT_ACTIVATED',currentPolicyId=current14['id'],currentSchema=1,currentSavedTitle=title,proposedId='unit14-prior-plural-possession-reported-time-v2',proposedFreshSchema=16,proposedFreshTitle='Review supplied plurals, possession and reported time',assignedSlots=3,candidateItems=11,candidateAllocationCombinations=24,combinationCountIsNotJUnitCount=True,selectedOwners=[8,10,13],priorUnitsBeyondImmediatePredecessor=[8,10],selectedPools=proposal,objectiveChains=72,inventoriedCheckRecords=226,registeredPolicyObjectsPreserved=40,schemasPreserved=list(range(1,16)),currentCounts=currentCounts,projectedCountsNotActivated=projection,historicalArchivesChecked=74,archivesContainingUnit14=len(fixtures[14]),archivesContainingUnit15=len(fixtures[15]),historicalPolicySchemaCounts=history,historicalOwnerDigests=owners,standaloneHistoricalDocuments=len(standalone),standaloneDuplicateArchiveMembers=(all(d['matchingArchivePaths'] for d in standalone) if standalone else None),freshActualCurrentUnit14Capture='NOT_YET_PRODUCED',newJUnitMethodsExecuted=0,newOrdinaryCheckItems=0,newRussian=0,newSkillEnums=0,heldOutItemsAdded=0,wallClockSpacedSchedulerAdded=False,ordinaryCheckIds=checks14,ordinaryGuidedIds=guided14,ordinaryMinimumDistinctChecks=6,qualifiedAcceptance='PENDING_NOT_GRANTED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None,exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2})
outputs={
 'CHAINS.json':enc(dict(sourceCommit=BASE,sourcePoolIntegrityVerified=True,scope='Mechanical inventory of all checked objectives through14;not complete item audit',chains=chains)),
 'CANDIDATE_ASSESSMENT.csv':csv_bytes(rows),
 'POOL_DECISIONS.csv':csv_bytes(decisionRows),
 'CURRENT_POLICY_INVENTORY.json':enc(dict(sourceCommit=BASE,reusedCompiledObservation=observationPath,observationSha256=sha(blob(observationPath)),producerSource=ob['sourceCommit'],appMainTreeMatches=True,currentUnit14=current14,currentSchema=1,currentSavedTitle=title,currentPools=[q for q in flat if q['unit']==14],proposedPools=proposal,registeredPolicyObjectHashes=objectHashes,existingSchemaConstants=schemas,currentCounts=currentCounts,projectedCountsNotActivated=projection,downstreamOwner14Pools=downstream14,currentSourceBoundManifests=manifests,matchingKernelReferencePair=dict(commit=BASE,kernelSha256=sha(blob(KERNEL)),referencePath=refPath,referenceSha256=sha(blob(refPath)),recordedKernelSha256=kr['sha256']),digestIdentityCorrection=dict(originalObservationLabel='canonicalSourceDigest',actualMeaning='V2Validator.contentReviewDigest(pack)',packContentReviewDigest=ob['canonicalSourceDigest'],repositorySourceDigest=prep['repositorySourceDigest'],priorCorrectionPath=B+'engineering-unit13-source-reconciliation-2026-10-27/DIGEST_IDENTITY_CORRECTION.json'))),
 'HISTORICAL_UNIT14_UNIT15_INVENTORY.json':enc(dict(sourceCommit=BASE,scope='Historical byte/member observations only;not replay/test counts or fresh current capture',archivesChecked=74,archives=archives,unit14Fixtures=fixtures[14],unit15Fixtures=fixtures[15],policySchemaCounts=history,owner13DigestsInUnit14=owners['14'],owner14DigestsInUnit15=owners['15'],standaloneDocuments=standalone,standaloneDocumentsNotAdditionalEvidence=True)),
 'UNIT14_ORDINARY_SCOPE.json':enc(dict(sourceCommit=BASE,unit=unit14,checkIds=checks14,guidedIds=guided14,rawMinimumChecks=6,bookSelfAttestationRequired=True,currentPrerequisites='Real current Units1–13,including Unit11v2/schema13,Unit12v2/schema14,Unit13v3/schema15;do not substitute an old ordinal-only learner document.',baselineMustCaptureActualCurrentV1BeforeImplementation=True)),
 'SUMMARY.json':enc(summary),
 'INPUTS.json':enc(dict(sourceCommit=BASE,sourceRootTree=git('rev-parse',BASE+'^{tree}').decode().strip(),inputTrees=prep['inputTrees'],rawCurriculumSha256=sha(blob(RAW)),kernelSha256=sha(blob(KERNEL)),sourceCapabilityObjectHashes={c['id']:sha(enc(c)) for c in p['sourceCapabilities']},privatePdf='NOT_READ_OR_PACKAGED',newRuntimeMethodsExecuted=0,protectedHashScope='Pinned historical Git input bytes;--current verifies present checkpoint conservation.')),
 'PROTECTED_EVIDENCE_HASHES.json':enc(protected),
 'VERIFICATION_CHECKPOINT.json':enc(dict(researchSource=BASE,actualBroadSource=broad['sourceCommit'],checkpointCommit=BASE,reportedCounts=broad['testCounts'],classes=broad['classes'],exitCode=broad['exitCode'],startedUtc=context['startedUtc'],finishedUtc=broad['finishedUtc'],command=context['command'],activation=context['activation'],inputTrees=context['inputTrees'],lint=broad['lintIssuesBySeverity'],tasks=broad['tasks'],newJUnitMethodsExecutedByResearch=0,priorScopesRemainSeparate=True,priorInterruptedScope='316partial events/309SUCCESS/6FAILURE/1physicalSKIP;no final exit/XML/result',priorContinuation='165/165at3248ab59',priorFullClassFollowup='63/63at8e417f21',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None)),
}
outputs['GENERATED_SHA256.json']=enc({n:sha(b) for n,b in outputs.items()})
assert sys.argv[1:] in [[],['--check']]
for n,data in outputs.items():
    if sys.argv[1:]==['--check']:assert (D/n).read_bytes()==data,n
    else:assert not (D/n).exists(),n;(D/n).write_bytes(data)
print(json.dumps(dict(verified=sys.argv[1:]==['--check'],chains=72,checkInventory=226,selectedCandidates=11,assignedSlots=3,archivesShaCrc=74,unit14ArchiveRecords=len(fixtures[14]),unit15ArchiveRecords=len(fixtures[15]),historicalPolicySchemaCounts=history,projection=projection,newJUnitMethodsExecuted=0)))
