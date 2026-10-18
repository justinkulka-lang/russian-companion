#!/usr/bin/env python3
"""Reconcile an exported compiled registry with pinned source; no learner execution."""
from pathlib import Path
import json,hashlib,subprocess,collections,sys
ROOT=Path(__file__).resolve().parents[3]
DIR=Path(__file__).resolve().parent
CTX=json.loads((DIR/'INVENTORY_CONTEXT.json').read_text())
COMMIT=CTX['inspectedCommit']
asset=subprocess.check_output(['git','show',COMMIT+':app/src/main/assets/content/v2-curriculum.json'],cwd=ROOT)
assert hashlib.sha256(asset).hexdigest()==CTX['inputSha256']['app/src/main/assets/content/v2-curriculum.json']
p=json.loads(asset);registry=json.loads((DIR/'COMPILED_REGISTRY.json').read_text())
units={u['number']:u for u in p['units']};objectives={o['id']:(u,o) for u in p['units'] for o in u['objectives']}
steps={s['id']:(se,i,s) for u in p['units'] for se in u['sessions'] for i,s in enumerate(se['steps'])}
items={i['id']:i for i in p['items']};examples={e['id']:e for e in p['examples']}
policies=[];targets=collections.defaultdict(list);hard=[];flags=[]
for a in registry:
 n=a['targetUnit'];assert units[n]['advancement']['requireDueReview'] and units[n]['advancement']['cumulativeReviewPolicyId']==a['id']
 specs=[]
 for spec in a['slots']:
  oid=spec['objectiveId'];owner,o=objectives[oid];targets[oid].append(n)
  teachers=[steps[x] for x in o['instructionStepIds'] if steps[x][2]['kind']=='TEACH']
  early=True;late=[]
  for se,i,s in teachers:
   earlier=[z['id'] for z in se['steps'][:i] if z['kind'] in ['GUIDED','CHECK'] and (oid in se.get('objectiveIds',[]) or any(items.get(k,{}).get('objectiveId')==oid for k in z.get('itemIds',[])))]
   if earlier:early=False;late.append({'teaching':s['id'],'earlierPracticeOrChecks':earlier})
  rem=steps[spec['remediationStepId']][2] if spec['remediationStepId'] else None
  covered=all(t[2]['body'].strip() in rem['body'] for t in teachers) if rem else None
  excovered=set(o['exampleIds'])<=set(rem.get('exampleIds',[])) if rem else None
  candidates=[items[x] for x in spec['candidateItemIds']]
  errors=[]
  if owner['number']>=n:errors.append('OWNER_NOT_EARLIER')
  if not o.get('gating',True) or o['kind']!='RECOGNITION':errors.append('NOT_GATING_RECOGNITION')
  if any(x['id'] not in o['checkItemIds'] or (x.get('assessment') is not None and not x['assessment']['independent']) or not any(st['kind']=='CHECK' and x['id'] in st.get('itemIds',[]) for _,_,st in steps.values()) or x['objectiveId']!=oid or x['answer'] not in x['options'] for x in candidates):errors.append('CANDIDATE_NOT_VALID_DECLARED_INDEPENDENT')
  if any(x not in steps for x in o['instructionStepIds']+o['practiceStepIds']):errors.append('MISSING_INSTRUCTION_OR_PRACTICE')
  if rem and spec['remediationStepId'] not in o['instructionStepIds']+o['remediationStepIds']:errors.append('REPAIR_NOT_BOUND_TO_OBJECTIVE')
  hard.extend({'target':n,'objective':oid,'issue':e} for e in errors)
  row={**spec,'ownerUnit':owner['number'],'objectiveTitle':o['title'],'candidateCount':len(candidates),'instructionStepIds':o['instructionStepIds'],'practiceStepIds':o['practiceStepIds'],'independentCheckIds':o['checkItemIds'],'teachingCardCount':len(teachers),'allTeachingBeforeOwnPracticeAndChecks':early,'lateTeaching':late,'repairContainsEveryTeachingBodyLiterally':covered,'repairIncludesAllObjectiveExamples':excovered,'repairExampleCount':len(rem.get('exampleIds',[])) if rem else None,'hardStructuralIssues':errors,'legacyCandidatesWithoutAssessmentMetadata':[x['id'] for x in candidates if x.get('assessment') is None],'learnerEligibilityProven':False,'qualifiedSemanticAcceptance':False}
  specs.append(row)
  if not early or covered is False or excovered is False:flags.append({'target':n,'objective':oid,'teachingBeforePractice':early,'literalRepairCoverage':covered,'allObjectiveExamplesInRepair':excovered})
 earlier=[oid for oid,(u,o) in objectives.items() if u['number']<n and o.get('gating',True) and o['kind']=='RECOGNITION' and o['checkItemIds']]
 policies.append({'targetUnit':n,'declaredPolicyId':a['id'],'policyDigest':a['digest'],'slots':specs,'distinctSkillTags':len({s['skill'] for s in specs}),'earlierRecognitionDeclarationCount':len(earlier),'earlierRecognitionDeclarationsAbsentFromThisPolicy':[oid for oid in earlier if oid not in [s['objectiveId'] for s in specs]],'absentDeclarationsAreNotAutomaticallyEligible':True})
assert [a['targetUnit'] for a in policies]==list(range(3,31))
coverage=[{'objectiveId':oid,'ownerUnit':objectives[oid][0]['number'],'targets':ns,'candidateIdsAcrossPolicies':list(dict.fromkeys(i for a in registry for spec in a['slots'] if spec['objectiveId']==oid for i in spec['candidateItemIds']))} for oid,ns in targets.items()]
j={'type':'COMPILED_DECLARATION_AND_PINNED_STATIC_STRUCTURE_INVENTORY','inspectedCommit':COMMIT,'junitMethodsExecuted':0,'registrySha256':hashlib.sha256((DIR/'COMPILED_REGISTRY.json').read_bytes()).hexdigest(),'unit2LegacyReviewSeparate':units[2]['advancement']['requireDueReview'],'policies':policies,'objectiveTargetCoverage':coverage,'distinctDeclaredReviewObjectives':len(targets),'totalFixedSlots':sum(len(a['slots']) for a in policies),'multiCandidateTargets':[a['targetUnit'] for a in policies if any(s['candidateCount']>1 for s in a['slots'])],'moreThanTwoSlotTargets':[a['targetUnit'] for a in policies if len(a['slots'])>2],'nonReadingObjectivesAtMultipleTargets':[r for r in coverage if r['objectiveId']!='obj-u1-decoding' and len(r['targets'])>1],'laterTargets14Through30AllTwoSlots':all(len(a['slots'])==2 for a in policies if a['targetUnit']>=14),'hardStructuralIssues':hard,'instructionAndRepairInspectionFlags':flags,'flagsRequireFullContextReviewNotAutomaticEdits':True,'potentialDeclarationsAreNotAdmissionOrEligibility':True,'allSkillsCoverageClaim':False,'formalAcceptance':False,'completeSuite':False}
out=DIR/'COVERAGE_INVENTORY.json';s=json.dumps(j,ensure_ascii=False,indent=2)+'\n'
if '--check' in sys.argv:assert out.read_text()==s;print('Pinned compiled/static inventory matches;zero JUnit methods.')
else:out.write_text(s);print(json.dumps({k:j[k] for k in ['distinctDeclaredReviewObjectives','totalFixedSlots','multiCandidateTargets','moreThanTwoSlotTargets','hardStructuralIssues','instructionAndRepairInspectionFlags']},ensure_ascii=False))
