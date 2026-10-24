from pathlib import Path
import json,csv,hashlib,zipfile,subprocess
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit7-substantive-research-2026-10-24';C=json.loads((D/'CHAINS.json').read_text());sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
def writecsv(name,rows):
 with (D/name).open('w',newline='') as f:
  w=csv.DictWriter(f,fieldnames=list(rows[0]),lineterminator='\n');w.writeheader();w.writerows(rows)
notes={
'obj-u1-decoding':('REPLACE_FOR_NEW_UNIT7_ONLY','Printed upper/lower-case matching; no whole-word decoding. Preserve legacy task, not an adequate new grammar review slot.'),
'obj-u3-gender':('SELECTED_FINITE_LEXICAL_GENDER','Five taught noun labels across all3genders; prepares identifying a noun property relevant to Unit7agreement. Does not select adjective endings or infer unseen gender.'),
'obj-u4-pronouns':('DEFER_WEAKER_TARGET_FIT','Finite pronoun gloss recall, not noun/adjective agreement; generic repair and plural-only interpretation limit claim.'),
'obj-u4-present':('DEFER_IMMEDIATE_PRIOR_REVIEW_OVERLAP','Explicit supplied person/form teaching and repair; all4checks occur in currentUnit6pool, so this replacement would repeat an immediately preceding skill/pool.'),
'obj-u4-nominative':('DEFER_IMMEDIATE_PRIOR_REVIEW_OVERLAP','Two supplied clause subject-role checks; same pool occurs in currentUnit6(andUnit5). Does not check adjective agreement.'),
'obj-u5-prepositional':('DEFER_CONSTRUCT_PROXY','Two English-description/gloss checks do not require independent prepositional inflection; not a justification for case-production claims.'),
'obj-u5-indeclinable':('DEFER_IMMEDIATE_PRIOR_REVIEW_OVERLAP','Five finite change/unchanged noun-behavior checks; currentUnit6alreadyreviews this pool. Not general declension or adjective agreement.'),
'obj-u6-animacy':('RETAIN_ONLY_EXISTING_INANIMATE_SLOT','CurrentUnit7uses onlyu6-i-animacy-3; its cue supplies gender/animacy and asks for English case-form classification. Retain this existing obligation with its limits; do not expand to animate-form mastery implicitly.')}
rows=[]
for c in C['chains']:
 o=c['objective'];items={i['id']:i for i in c['items']};guided={i for s in c['steps'] if s['kind']=='GUIDED' for i in s.get('itemIds',[])}
 signature=lambda i:(i.get('prompt'),i.get('promptLetter'),tuple(sorted(i['options'])),i['answer'])
 for iid in o['checkItemIds']:
  i=items[iid];decision,limit=notes[o['id']]
  if o['id']=='obj-u6-animacy' and iid!='u6-i-animacy-3':decision='DEFER_SEPARATE_ANIMATE_FORM_SCOPE';limit='Valid supplied same-lemma form distinction with2options; not in currentUnit7inanimate-only pool. Broadening requires a differently named skill/claim and separate scope.'
  rows.append(dict(unit=c['unit'],objective_id=o['id'],item_id=iid,source_id=i['sourceId'],repository_review_status=i['reviewStatus'],option_count=len(i['options']),raw_key_index=i['options'].index(i['answer']),same_tuple_as_guided=';'.join(sorted(g for g in guided if signature(items[g])==signature(i))),has_feedback=bool(i.get('feedback')),generated_russian=i.get('containsGeneratedRussian'),decision=decision,evidence_limit=limit,item_sha256=hashlib.sha256((json.dumps(i,ensure_ascii=False,indent=2)+'\n').encode()).hexdigest()))
writecsv('CANDIDATE_ASSESSMENT.csv',rows)
F=R/'audit/independent-gpt61-2026-10-12/ia003-unit7-agreement-teaching';labels=['BEFORE_UNIT7_WORK','ORDINARY_UNIT7','UNSTARTED_REVIEW','PARTIAL_REVIEW','ANSWERED_REVIEW','COMPLETED_UNIT7','POST_COMPLETION_WRONG','COMPLETED_UNIT8'];fixtures=[]
for label in labels:
 p=F/(label+'.json');z=F/(label+'.zip');doc=json.loads(p.read_text())
 with zipfile.ZipFile(z) as archive:
  assert archive.testzip() is None
  assert json.loads(archive.read('learner.json'))==doc
 fixtures.append(dict(label=label,jsonPath=str(p.relative_to(R)),jsonSha256=sha(p),zipPath=str(z.relative_to(R)),zipSha256=sha(z),zipCrcVerified=True,unit7Assignments=[a for a in doc['reviewAssignments'] if a['targetUnit']==7],unit7Answers=[a for a in doc['reviewAnswers'] if a['assignmentId'] in {x['id'] for x in doc['reviewAssignments'] if x['targetUnit']==7}]))
(D/'HISTORICAL_FIXTURE_INVENTORY.json').write_text(json.dumps(dict(scope='Eight existing authentic states; no new capture,restoration,runtime execution or relabelling',fixtures=fixtures),indent=2)+'\n')
scenarios=[
('B01','Actual current fresh Unit7 baseline','Complete actual current prior teaching/checks/reviews1–6 and Unit7ordinary/book; allocate realUnit7task. Record exactpolicy/schema/slots/source.', 'Current schema1/print+inanimate; proposed schema9/gender+inanimate. Do not infer pass/fail before execution.'),
('B02','Missing new gender review can still advance under old obligation','AfterB01answer only assigned print and inanimate review checks; attempt actual advancement; assert proposed gender-review requirement in a baseline-first method.', 'Baseline expected to expose lack of assignment-bound gender review. Ordinary gender answers are prerequisites,not new review credit; no claim learner lacks all gender knowledge.'),
('B03','Genuine current schema1 partial lifecycle capture','Capture real currentUnit7assignment with slot0 answered via supportedbackup; decode/replace/reopen under sameactualpack; preserve canonicaldoc andarchive SHA.', 'No edited current JSON posing as historical. Newcapture must remain separate from original64 and original8 Unit7states.'),
('F01','Fresh new obligation and ordinary-credit refusal','Fresh declarednewpolicy requires one candidatefrom5genderitems plus exactoldinanimate slot; ordinary and optional answers never supply due review credit.', 'Afterimplementation; missing/wronggender review prevents completion; allassigned correctanswers allowadvance.'),
('F02','Current teaching/repair before assignment','Placement or stale ownerUnit3teaching/checks refuses allocation without mutation and routes to Unit3instruction; finish repairthenreopen. Keep separate staleUnit6repair route.', 'Current prerequisites and allnewobjective eligibility actuallytested; no legacy fixture substitution for currentbehavior.'),
('F03','Governed ordinary-history selection','With eligiblepool choose latestvalidmiss/leastrecentcorrect fromordinaryCHECK acceptedOrder; invalid/digestmutated/duplicateorder/lab/reviewanswers ignored. Preserve savedtaskafterlaterhistory.', 'New schema9only; oldschema1keepsfirst-candidate/static meaning; separateorder namespacesnevermerged.'),
('F04','Saved legacy assignment retains obligation','Restore genuineB03and existing8states undernewpack; unchanged oldslots/boundary/answers/digests/response rule; explicitdirectedcompatibilityonlywhereownercontentmatches.', 'Old assignment remains2slots/schema1 and oldprintlabel; newtasksusegenderlabel; no backwardactivation ofschema9 underolddeclaration.'),
('F05','Mixed historical/current advancement and source mutation','Fresh current ordinaryUnit7may be required after declarationdigestchange; preserve originalcompleted snapshots and oldreview obligations. Mutate selecteditem/ownerteaching/policydigest/response/schema; rejectstaleorunknowncredit.', 'Do not update old hashes or blanket mark alloldcompletionscurrent; preserve actualfirstfailuresandvalidscopes.'),
('F06','UI response and lifecycle','ActualV2review labels savedpolicy accurately; permutation/scoring/resume stable; labelselection doesnotdoubleanswer; wronganswerrepair shows genderchain. Verifyactualcurrent aswellaslegacycontrols.', 'Robolectriccontrols notTalkBack/API35/36/physicalacceptance.'),
('F07','Current source bindings and conservation','Afterimplementation pin actualsource, officialKotlindigest, authenticrender twice withstructuredfielddiff; conserve155records/priorversions/66originalarchives/newcapture.', 'Never regenerate to silence assertions; allreviewer/owner/approvalfieldsstaypending; meaningfulscope/lint/gate before broaderintegration.')]
writecsv('BASELINE_AND_ACCEPTANCE_PLAN.csv',[dict(scenario_id=i,scenario=s,procedure=p,expected_scope=e,execution_status='PLANNED_NOT_EXECUTED',test_method_identity='NOT_CREATED',independent_acceptance='NOT_CLAIMED') for i,s,p,e in scenarios])
(D/'STATIC_SUMMARY.json').write_text(json.dumps(dict(sourceCommit=C['sourceCommit'],objectiveChains=len(C['chains']),scoredCandidates=len(rows),selectedGenderCandidates=[r['item_id'] for r in rows if r['decision']=='SELECTED_FINITE_LEXICAL_GENDER'],retainedInanimateCandidate='u6-i-animacy-3',proposedPolicyId='unit7-prior-gender-inanimate-case-v2',proposedFreshAssignmentSchema=9,policyActivated=False,oldUnit7Policy='unit7-prior-reading-inanimate-case-v1',oldUnit7Schema=1,legacyStatesIndexed=8,originalArchiveCountUnchanged=66,additionalJUnitMethodsExecuted=0,rawKeyPositionCounts={str(k):sum(int(r['raw_key_index'])==k for r in rows) for k in sorted({int(r['raw_key_index']) for r in rows})},completeSuite=False,independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None),indent=2)+'\n')
print((D/'STATIC_SUMMARY.json').read_text())
