from pathlib import Path
from collections import Counter
import csv,json,re,fnmatch,hashlib,shutil
R=Path('/workspace/russian-companion');B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';W=Path(__file__).parent
D=B/'engineering-post-broad-work-order-2026-10-24';D.mkdir(exist_ok=False)
A=B/'final-independent-recheck-results/2026-10-22-claude-code';H=B/'engineering-contract-broad-2026-10-24'
finding=list(csv.DictReader((A/'FINDINGS.csv').open()));work=list(csv.DictReader((A/'IMPLEMENTATION_WORK_ORDER.csv').open()));requests=list(csv.DictReader((A/'EXECUTION_EVIDENCE_REQUESTS.csv').open()));runtime=json.loads((H/'RUNTIME_RESULT.json').read_text());tests=runtime['tests'];classes=sorted({t['testClass'] for t in tests});sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
assert len(finding)==len(work)==48 and len(requests)==70
inputs={str(p.relative_to(R)):sha(p) for p in [A/'FINDINGS.csv',A/'IMPLEMENTATION_WORK_ORDER.csv',A/'EXECUTION_EVIDENCE_REQUESTS.csv',A/'FINAL_NONPHYSICAL_SCOPE_DECISION.md',H/'RUNTIME_RESULT.json',H/'RUNTIME_CONTEXT.json',H/'PERSISTED_VERIFICATION.json',H/'RAW_BROAD.zip',H/'RAW_BROAD_INDEX.json']}
jsonwrite=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
def writecsv(n,rows):
 with (D/n).open('w',newline='') as f:
  writer=csv.DictWriter(f,fieldnames=list(rows[0]),lineterminator='\n');writer.writeheader();writer.writerows(rows)
notes={
'CC002':('FOUNDATION_CONTENT_AND_RIGHTS_RESEARCH','Host passing tests do not supply missing licensed model audio, handwriting or full literacy instruction.'),
'CC018':('HOST_CONTROLS_PASSED_EXTERNAL_UI_RECHECK_REQUIRED','Seven V2InsetsAccessibilityTest controls passed; API33 geometry is not API35/36, keyboard, font scaling or physical evidence.'),
'CC019':('HOST_CONTROLS_PASSED_ACCESSIBILITY_RECHECK_REQUIRED','V2InsetsAccessibilityTest semantics controls passed; no actual TalkBack speech/focus audit.'),
'CC020':('SUBSTANTIVE_REVIEW_RESEARCH_NEXT','Current production export:28policies/59pools/82candidate placements/48unique items;26policies retain repeated print-case singleton. Units5/6are revised exceptions. Pool inventory is not skill validity.'),
'CC044':('LEGACY_SCOPE_SEPARATION_PENDING','Broad host methods include legacy presentation code; package names and pass counts are not shipped-flow reachability proof. Keep original tests/evidence.'),
'CC046':('SELECTOR_IDENTITY_CORRECTED_IN_SUPPLEMENT','Requests22/60 name nonexistent HostActivityLifecycleTest; supplemental selectors use LearnerReloadRecoveryTest and HostActivityRecreationTest. Actual matching methods passed; original requests unchanged.'),
'CC047':('AUTHENTIC_FIXTURE_SCOPE_REVIEW_PENDING','64original histories and extra genuine Unit6v2capture remain exact; do not infer schema6/recording-bearing historical coverage from archive count.'),
'CC048':('AUTHORITY_CONFLICT_REQUIRES_EXPLICIT_DISPOSITION','Preserve existing f8b8b09aactivation and MA2-178/Amendment1conflict; no new owner decision inferred or code toggle performed.')}
rows=[]
for f,w in zip(finding,work):
 assert f['finding_id']==w['finding_id'];fid=f['finding_id'];direct=B/('engineering-'+fid.lower());dirs=[direct] if direct.exists() else []
 if fid in ['CC018','CC019']:dirs=[B/'engineering-v2-insets-accessibility-2026-10-22']
 if fid=='CC020':dirs=[B/n for n in ['engineering-cc020-review-scope-2026-10-22','engineering-cc020-unit5-grammar-2026-10-22','engineering-unit6-present-correction-2026-10-23']]
 reports=[];historical_ids=set()
 for d in dirs:
  reports.extend(str(p.relative_to(R)) for p in d.glob('*.md'))
  for p in d.rglob('RESULT.json'):
   try:data=json.loads(p.read_text())
   except (ValueError,OSError):continue
   historical_ids.update((t.get('testClass'),t.get('method')) for t in data.get('tests',[]) if t.get('testClass') and t.get('method'))
 matched=[t for t in tests if (t['testClass'],t['method']) in historical_ids]
 disposition,note=notes.get(fid,('PRIOR_ENGINEERING_SCOPE_REQUIRES_CURRENT_SEMANTIC_TRACE' if dirs else 'WORK_ORDER_CORRECTION_REQUIRES_BOUNDED_BASELINE','Historical engineering records and same method IDs are candidate evidence only; trace complete current acceptance scenario and required qualification before any closure.'))
 rows.append(dict(finding_id=fid,severity=f['severity'],category=f['category'],original_status=f['status'],independent_disposition=f['independent_disposition_2026_10_22'],current_independent_status='OPEN_NOT_CLOSED',engineering_next_step=disposition,engineering_records=';'.join(sorted(reports)),same_historical_method_ids_in_fresh_host=len(matched),matched_host_passes=sum(t['outcome']=='PASSED' for t in matched),matched_host_skips=sum(t['outcome']=='SKIPPED' for t in matched),candidate_method_match_is_acceptance='NO',note=note,required_correction=w['bounded_correction'],acceptance_scenario=w['negative_regression'],qualified_or_independent_recheck='REQUIRED'))
writecsv('FINDING_CONTINUATION.csv',rows)
mapping=[];members=[]
for r in requests:
 n=int(r['request_no']);literal=sorted(set(re.findall(r'\*[A-Za-z0-9_]+Test',r['command_or_test_identity'])));effective=[x for x in literal if x!='*HostActivityLifecycleTest']
 if n in [22,60]:effective+=['*LearnerReloadRecoveryTest','*HostActivityRecreationTest']
 matched_classes=sorted({c for c in classes if any(fnmatch.fnmatchcase(c,s) for s in effective)});matched=[t for t in tests if t['testClass'] in matched_classes]
 unmatched=[s for s in literal if not any(fnmatch.fnmatchcase(c,s) for c in classes)]
 state='SCENARIO_TO_METHOD_TRACE_PENDING';note='No scenario fulfillment inferred from broad inclusion or class names.'
 if n==1:state='CORRECTED_SOURCE_HOST_SCOPE_VERIFIED_OLD_TARGET_NOT_REEXECUTED';note='1365reported/1364passed/0failed/0errors/1physicalskip,229classes atd7067532; original audited8606c4d7not reexecuted.'
 if n in [2,3,4]:
  task={2:'compileDebugSources',3:'lintDebug',4:'verifyReleaseGateAttachment'}[n];state='CORRECTED_SOURCE_TASK_RECORDED_OLD_TARGET_NOT_REEXECUTED';note=runtime['tasks'][task]
  if n==3:note+=';lint0fatal/0errors/20warnings/1hint; main/unit lint analysis UP-TO-DATE.'
 if literal:state='MATCHING_HOST_CLASSES_REPORTED_SCENARIO_TRACE_PENDING';note+=' Corrected supplemental selector identity only; original selector retained.' if n in [22,60] else ''
 if n==70:state='ARCHIVE_CONSERVATION_AND_CURRENT_REPLAY_RECORDED';note='66original archives SHA/CRC protected. CurrentPackHistoricalBackupTest.allOriginalArchivesRestoreAndReopenButDoNotAcquireNewCredit PASSED;64cases within one method,not64tests. Original histories and prior failure evidence unchanged. Semantic/owner acceptance not inferred.'
 if 'Authorized emulator/UI' in r['required_environment']:note+=' Requested emulator/window/API/font-scale evidence is not supplied by API33host passes.'
 mapping.append(dict(request_no=n,scenario=r['scenario'],original_execution_status=r['execution_status'],original_independent_execution=r['independent_execution_this_session'],supplemental_disposition=state,original_selectors=';'.join(literal),effective_candidate_selectors=';'.join(effective),unmatched_original_selectors=';'.join(unmatched),matched_classes=';'.join(matched_classes),matched_methods=len(matched),host_passes=sum(t['outcome']=='PASSED' for t in matched),host_skips=sum(t['outcome']=='SKIPPED' for t in matched),acceptance_fulfillment='NOT_CLAIMED',note=note))
 for t in matched:members.append(dict(request_no=n,testClass=t['testClass'],method=t['method'],outcome=t['outcome'],scope='Overlapping candidate class match; not scenario acceptance'))
writecsv('EXECUTION_REQUEST_RECONCILIATION.csv',mapping);writecsv('REQUEST_MATCHED_METHODS.csv',members)
writecsv('HOST_METHOD_SCOPE.csv',[dict(testClass=t['testClass'],method=t['method'],outcome=t['outcome'],package_bucket='LEGACY_PRESENTATION_REQUIRES_REACHABILITY_REVIEW' if '.presentation.' in t['testClass'] else 'V2_PACKAGE_REQUIRES_SCENARIO_TRACE' if '.v2.' in t['testClass'] else 'OTHER_HOST_SCOPE_REQUIRES_SCENARIO_TRACE',shipped_flow_coverage='NOT_INFERRED_FROM_PACKAGE_OR_PASS') for t in tests])
for name in ['CURRENT_POLICY_INVENTORY.json','EXPORT.log','DIGEST.log','COMPILE.log','ExportReviewPolicies.java','export_current.py','build_reconciliation.py']:shutil.copy2(W/name,D/name)
jsonwrite('INPUT_IDENTITIES.json',inputs)
jsonwrite('SUMMARY.json',dict(checkpointSource='c695bb75b96b81d7bf3158fec02ff3cfbedd7a07',testedSource=runtime['sourceCommit'],hostCounts=runtime['testCounts'],classes=runtime['classes'],findings=len(rows),allIndependentFindingsOpen=True,executionRequests=len(mapping),requestsWithCandidateSelectors=sum(bool(x['effective_candidate_selectors']) for x in mapping),unmatchedOriginalSelectors=sorted({s for x in mapping for s in x['unmatched_original_selectors'].split(';') if s}),acceptanceFulfillmentNotClaimed=True,originalAuditStatusesUnchanged=True,extraJUnitMethodsExecuted=0,currentPolicyFacts={k:json.loads((D/'CURRENT_POLICY_INVENTORY.json').read_text())[k] for k in ['activePolicies','objectivePools','candidatePlacements','uniqueCandidateItems','policiesStillContainingPrintCaseSingleton']},scopeBuckets=dict(Counter('legacyPresentation' if '.presentation.' in t['testClass'] else 'v2Package' if '.v2.' in t['testClass'] else 'otherHost' for t in tests)),independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None,environmentSettings='Previously saved; publication user-confirmed,not independently observed; no change this task'))
print((D/'SUMMARY.json').read_text())
