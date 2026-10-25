from pathlib import Path
import json,csv,hashlib,zipfile,subprocess
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit8-substantive-research-2026-10-24';B=D.parent;S='fe7b26853448b8c3bd8098126d85983b013b5cd7'
assert not (D/'CANDIDATE_ASSESSMENT.csv').exists(), 'Historical procedure refuses to overwrite tracked research; use a distinct reviewed external attempt'
sha=lambda b:hashlib.sha256(b).hexdigest()
write=lambda n,x:(D/n).write_text(json.dumps(x,ensure_ascii=False,indent=2)+'\n')
def csvout(n,rows):
 with (D/n).open('w',newline='') as f:
  w=csv.DictWriter(f,fieldnames=list(rows[0]),lineterminator='\n');w.writeheader();w.writerows(rows)
chains=json.loads((D/'CHAINS.json').read_text())['chains'];notes={
'obj-u1-decoding':('REPLACE_FRESH_UNIT8_ONLY','Printed case-pair matching, not word reading; preserve old schema1 obligation.'),
'obj-u3-gender':('DEFER_IMMEDIATE_REVIEW_REPETITION','Five finite noun labels; currentUnit7alreadyassigns this pool, so do not repeat it immediately.'),
'obj-u4-present':('SELECT_SUPPLIED_PRESENT_PERSON_RECOGNITION','Four supplied same-lemma person/form checks; different guided-person tasks and complete teaching/repair. Used byUnit6, absent from immediateUnit7; not novel/held-out or productive conjugation.'),
'obj-u4-nominative':('DEFER_WEAKER_CONSTRUCT','Two supplied English subject-role classifications, alreadyUnit5/6; no form choice or plural adjective application.'),
'obj-u5-indeclinable':('DEFER_FINITE_BEHAVIOR_LABELS','Noun behavior labels relevant to invariance but do not select plural forms; used byUnit6. Candidate alternative, not rejected as linguistically invalid.'),
'obj-u6-possessive':('DEFER_MIXED_GENDER_CASE_SCOPE','Three checks mix masculine/feminine nominative and feminine accusative phrases. New precise skill scope required; do not borrow unrelated possession-absence enum.'),
'obj-u6-animacy':('DEFER_IMMEDIATE_REVIEW_REPETITION','CurrentUnit7alreadyassigns inanimate singleton; other2checks are animate forms and require their own claim.'),
'obj-u7-endings':('RETAIN_EXISTING_NOMINATIVE_SLOT_ONLY','Keepu7-i-endings-4 and fullu7-st-rem-endings. No new case/number/dictionary-form pooling in this bounded replacement.'),
'obj-u7-spelling':('DEFER_SAME_LESSON_EXPANSION','Specific adjective forms afterш; avoid making both review slots sampleUnit7 alone; broader spelling not established.'),
'obj-u8-plural':('CURRENT_TARGET_NOT_EARLIER_OBJECTIVE','CurrentUnit8ordinary instruction/checks remain required; do not turn its own work into an earlier cumulative skill.'),
'obj-u8-agreement':('CURRENT_TARGET_NOT_EARLIER_OBJECTIVE','CurrentUnit8plural-agreement ordinary checks are not a previous-unit review pool.')}
rows=[]
for c in chains:
 o=c['objective'];items={x['id']:x for x in c['items']};guided={i for s in c['steps'] if s['kind']=='GUIDED' for i in s.get('itemIds',[])}
 sig=lambda i:(i.get('prompt'),i.get('promptLetter'),tuple(sorted(i['options'])),i['answer'])
 for iid in o['checkItemIds']:
  i=items[iid];decision,limit=notes[o['id']]
  if o['id']=='obj-u7-endings' and iid!='u7-i-endings-4':decision='DEFER_EXISTING_SLOT_EXPANSION';limit='Mixed genitive/plural/dictionary-form checks remain ordinaryUnit7work; only original nominative agreement check retained in proposal.'
  rows.append(dict(unit=c['unit'],objective=o['id'],item=iid,decision=decision,limit=limit,sourceId=i['sourceId'],reviewStatus=i['reviewStatus'],options=len(i['options']),rawKeyIndex=i['options'].index(i['answer']),sameTupleAsGuided=';'.join(g for g in sorted(guided) if sig(items[g])==sig(i)),itemSha256=sha((json.dumps(i,ensure_ascii=False,indent=2)+'\n').encode())))
csvout('CANDIDATE_ASSESSMENT.csv',rows);assert len(rows)==39
archives=json.loads((B/'engineering-unit56-conservation-2026-10-23/ORIGINAL_ARCHIVE_CONSERVATION.json').read_text());fixtures=[]
for n,h in archives.items():
 p=R/n;assert sha(p.read_bytes())==h
 with zipfile.ZipFile(p) as z:
  assert z.testzip() is None
  if 'learner.json' not in z.namelist():continue
  raw=z.read('learner.json');doc=json.loads(raw);assignments=[a for a in doc.get('reviewAssignments',[]) if a['targetUnit']==8]
  if assignments:
   ids={a['id'] for a in assignments};fixtures.append(dict(zipPath=n,zipSha256=h,learnerSha256=sha(raw),unit8Assignments=assignments,unit8Answers=[a for a in doc.get('reviewAnswers',[]) if a['assignmentId'] in ids],unit8Completions=[c for c in doc.get('unitCompletions',[]) if c['unitNumber']==8]))
write('HISTORICAL_UNIT8_INVENTORY.json',dict(sourceCommit=S,originalArchivesChecked=66,archivesContainingUnit8Tasks=len(fixtures),scope='Existing genuine historical bytes only; no current capture/replay or fabricated old task',fixtures=fixtures))
scenarios=[
('B01','Actual current ordinary/review prerequisites','Complete real current teaching,checks,book and required reviews throughUnit7, thenUnit8ordinary work. Allocate actualUnit8schema1 task. Record allslots/currentpolicy/source.'),
('B02','Proposed assignment-bound present review baseline','Answer only actual oldUnit8print+agreement slots. Attempt realUnit8advance; test proposed assignment-bound present-form obligation. Ordinary present knowledge is a prerequisite, not due-review credit. Preserve actual failure rather than inferring from source.'),
('B03','Genuine current partial oldUnit8 lifecycle','Use actual store APIs to answer slot0ofreal schema1task; supported backup/read/replace/namedreopen and exactdocument equality. Preserve original exported ZIP/memberSHA/CRC before policy edits. No recording-bearing/device claim.'),
('F01','Fresh schema10 obligations','Only after validated baseline: freshUnit8two slots,4earlierpresentcandidates plus exactoldagreementslot; wrong/missingpresent dueanswer refusesactualadvance; optional/ordinaryanswercannotborrowcredit.'),
('F02','Missing owner teaching and repair','CurrentUnit8placementmissingUnit4 routes toUnit4. With genuinely completedUnit4includingitsdue review and missingUnit7, routes toUnit7. Verify current source/ordinary/prerequisite eligibility, not rawcompletion shortcuts. Actualrepair shows fullteaching and separate guided practice thenreturns.'),
('F03','Selection and response rule','New schema10 only: governed ordinary CHECK latest-miss/leastrecent selection; invalid roles/digests/duplicateorders ignored. Revieworders remain separate; savedtaskfixed afterlaterattempts. Actual options presentation/scoring/resume checked.'),
('F04','Exact old task and completion identity','GenuineB03and relevant originalhistories retain oldschema1slots,answers,boundary,digests,cutoff and saved labels under directedcompatibility onlywhere ownercontent matches. No forward schema10 underolddeclaration; unknownschemas refused; exactoldowner8downstreamtasks preserved.'),
('F05','Source mutation and transactional boundaries','ChangedownerUnit4/7teaching/items,Unit8rules,policy/response/schema/target and staleepoch refuse withoutmutation. Actualadvancefailure rollback, backup/read/replace/reopen and old/currentUnit8→Unit9 progression verified.'),
('F06','Saved/current UI meaning','New labels name suppliedpresentforms and nominativeadjectiveagreement, notreading/productivegrammar; restoredoldlabelsretainprintmeaning. Coverreview/unitdetail/continue as wellaswronganswerrepair.'),
('F07','Authentic bindings/conservation/integration','Afterimplementation pin actualsource and officialKotlindigest; authentic153file render twice; review155records/priorversions structuredidentity-only changes and noacceptancefields changed. Keeporiginal66+2genuinecaptures and baseline. Verifyaffectedfullclasses/lint/gate beforejustifiedbroad.')]
csvout('BASELINE_AND_ACCEPTANCE_PLAN.csv',[dict(scenario_id=i,scenario=s,procedure=p,execution_status='PLANNED_NOT_EXECUTED',test_method_identity='NOT_CREATED',independent_acceptance='NOT_CLAIMED') for i,s,p in scenarios])
prior=B/'engineering-unit7-reconciled-broad-2026-10-24';v=json.loads(subprocess.check_output(['python3',str(prior/'verify_saved_broad.py')],cwd=R,text=True));assert v['verified'];write('PRIOR_HOST_VERIFICATION.json',v)
records=json.loads((prior/'CURRENT_RECORD_INPUTS.json').read_text())['files'];assert len(records)==155
for n,h in records.items():assert sha((R/n).read_bytes())==h
write('CURRENT_RECORD_HASHES.json',records)
protected={n:sha((R/n).read_bytes()) for n in subprocess.check_output(['git','ls-files','audit'],cwd=R,text=True).splitlines() if n not in records and n not in ['audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']};write('PROTECTED_EVIDENCE_HASHES.json',protected)
write('SUMMARY.json',dict(sourceCommit=S,objectiveChains=11,scoredCandidates=39,selectedPresentCandidates=[r['item'] for r in rows if r['decision']=='SELECT_SUPPLIED_PRESENT_PERSON_RECOGNITION'],retainedAgreementCandidate='u7-i-endings-4',proposedPolicy='unit8-prior-present-nominative-agreement-v2',proposedFreshSchema=10,activated=False,existingUnit8Policy='unit8-prior-reading-nominative-agreement-v1',existingUnit8Schema=1,originalArchives=66,oldArchivesWithUnit8Tasks=len(fixtures),currentRecords=155,protectedPriorEvidenceFiles=len(protected),extraJUnitMethodsExecuted=0,lint='NOT_RUN_RESEARCH_ONLY',releaseGateAttachment='NOT_RUN_RESEARCH_ONLY',apkIdentity=None,apkSignature=None,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False))
print(json.dumps({'candidates':39,'historicalUnit8Archives':len(fixtures),'protectedEvidenceFiles':len(protected)}))
