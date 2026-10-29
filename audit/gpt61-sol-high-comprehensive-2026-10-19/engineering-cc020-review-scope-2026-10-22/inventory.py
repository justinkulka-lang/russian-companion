from pathlib import Path
import json,csv,base64,hashlib,subprocess,collections
R=Path('/workspace/russian-companion');W=Path(__file__).parent;out=W/'INVENTORY';out.mkdir(exist_ok=True)
raw=(R/'app/src/main/assets/content/v2-curriculum.json').read_bytes();pack=json.loads(raw);units={u['number']:u for u in pack['units']};items={i['id']:i for i in pack['items']};objectives={o['id']:(u['number'],o) for u in pack['units'] for o in u['objectives']};steps={s['id']:s for u in pack['units'] for session in u['sessions'] for s in session['steps']};decode=lambda s:base64.b64decode(s).decode();policies={};pools=[]
for line in (W/'EXPORT.log').read_text().splitlines():
 fields=line.split('\t')
 if fields[0]=='POLICY':
  _,target,id,digest,schema,count=fields;n=int(target);policies[n]=dict(target_unit=n,policy_id=decode(id),policy_digest=digest,assignment_schema=int(schema),pool_count=int(count))
 elif fields[0]=='POOL':
  _,target,index,objective,skill,remed,candidates=fields;pools.append(dict(target_unit=int(target),pool_index=int(index),objective_id=decode(objective),declared_skill=skill,remediation_step_id=decode(remed),candidate_item_ids=decode(candidates).splitlines()))
 else:raise AssertionError(line)
assert set(policies)==set(range(3,31));bindings=[];objrows=[]
for pool in pools:
 n=pool['target_unit'];owner,obj=objectives[pool['objective_id']];pool['owner_unit']=owner;assert owner<n and units[n]['advancement']['requireDueReview'];assert all(s in steps for s in obj['instructionStepIds']+obj['practiceStepIds']);assert obj['instructionStepIds'] and obj['practiceStepIds'];assert pool['remediation_step_id']=='' or pool['remediation_step_id'] in obj['instructionStepIds']+obj['remediationStepIds']
 objrows.append(dict(target_unit=n,pool_index=pool['pool_index'],owner_unit=owner,objective_id=obj['id'],kind=obj['kind'],gating=obj.get('gating',True),gating_default_basis='V2Models.kt V2Objective default=true',instruction_step_ids=';'.join(obj['instructionStepIds']),practice_step_ids=';'.join(obj['practiceStepIds']),all_check_item_ids=';'.join(obj['checkItemIds']),remediation_step_ids=';'.join(obj['remediationStepIds']),learner_specific_eligibility='NOT_ESTABLISHED_BY_STATIC_GRAPH'))
 for id in pool['candidate_item_ids']:
  item=items[id];assert id in obj['checkItemIds'] and item['objectiveId']==obj['id']
  bindings.append(dict(target_unit=n,pool_index=pool['pool_index'],owner_unit=owner,objective_id=obj['id'],declared_skill=pool['declared_skill'],item_id=id,item_kind=item['kind'],prompt=item['prompt'],cue=item.get('promptLetter',''),options=json.dumps(item['options'],ensure_ascii=False),answer=item['answer'],feedback=item['feedback'],assessment_tested_feature=item.get('assessment',{}).get('testedFeature','NOT_DECLARED'),assessment_evidence_mode=item.get('assessment',{}).get('evidenceMode','NOT_DECLARED'),source_id=item['sourceId'],candidate_in_objective_check_ids=True,item_record_canonical_json_sha256=hashlib.sha256(json.dumps(item,sort_keys=True,ensure_ascii=False,separators=(',',':')).encode()).hexdigest(),semantic_acceptance='NOT_ADJUDICATED'))
for n,p in policies.items():
 pp=[x for x in pools if x['target_unit']==n];assert len(pp)==p['pool_count'];assert p['policy_id']==units[n]['advancement']['cumulativeReviewPolicyId'];p['declared_distinct_skills']=len({x['declared_skill'] for x in pp});p['owner_units']=';'.join(str(x) for x in sorted({x['owner_unit'] for x in pp}));p['candidate_counts']=';'.join(str(len(x['candidate_item_ids'])) for x in pp);p['selection_rule']='FIRST_ELIGIBLE_PER_POOL' if p['assignment_schema']==1 else 'LATEST_CURRENT_MISS_THEN_LEAST_RECENT_ACCEPTED_ORDER';p['shared_print_case_item']='u1-i-decode-pair' in [i for x in pp for i in x['candidate_item_ids']];p['semantic_skill_breadth']='NOT_PROVED_BY_ENUM_NAMES'
def table(name,rows):
 with (out/name).open('w',newline='') as f:
  w=csv.DictWriter(f,fieldnames=list(rows[0]),lineterminator='\n');w.writeheader();w.writerows(rows)
table('POLICY_INVENTORY.csv',list(policies.values()));table('POOL_BINDINGS.csv',[dict(p, candidate_item_ids=';'.join(p['candidate_item_ids'])) for p in pools]);table('CANDIDATE_ITEM_BINDINGS.csv',bindings);table('OBJECTIVE_BINDINGS.csv',objrows)
unique={}
for row in bindings:
 id=row['item_id']
 if id not in unique:unique[id]=dict(row,used_at_targets=[])
 unique[id]['used_at_targets'].append(row['target_unit'])
for row in unique.values():row['used_at_targets']=';'.join(map(str,sorted(set(row['used_at_targets']))))
table('UNIQUE_CANDIDATE_ASSESSMENT.csv',list(unique.values()))
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();policy_path='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt';raw_path='app/src/main/assets/content/v2-curriculum.json'
stats=dict(sourceCommit=git('rev-parse','HEAD'),externalAuditTarget='8606c4d74185f594d93941bef6858b2af28e0b75',samePolicyCodeAtAuditTarget=git('rev-parse','HEAD:'+policy_path)==git('rev-parse','8606c4d7:'+policy_path),sameRawPackAtAuditTarget=git('rev-parse','HEAD:'+raw_path)==git('rev-parse','8606c4d7:'+raw_path),rawCurriculumSha256=hashlib.sha256(raw).hexdigest(),policyCount=len(policies),pools=len(pools),candidatePlacements=len(bindings),uniqueCandidateItems=len(unique),poolCountDistribution=dict(collections.Counter(p['pool_count'] for p in policies.values())),assignmentSchemaDistribution=dict(collections.Counter(p['assignment_schema'] for p in policies.values())),widerTargets=[n for n,p in policies.items() if p['pool_count']>2],historySensitiveTargets=[n for n,p in policies.items() if p['assignment_schema']!=1],sharedPrintCaseTargets=[n for n,p in policies.items() if p['shared_print_case_item']],declaredDistinctSkillDistribution=dict(collections.Counter(p['declared_distinct_skills'] for p in policies.values())),allObjectiveOwnersPrior=True,allCandidatesBoundToObjectiveChecks=True,learnerSpecificEligibility='NOT_ESTABLISHED_BY_EXPORT',linguisticAcceptance='NOT_ESTABLISHED',findingClosed=False)
assert stats['samePolicyCodeAtAuditTarget'] and stats['sameRawPackAtAuditTarget'];(out/'INVENTORY_FACTS.json').write_text(json.dumps(stats,indent=2)+'\n');print(json.dumps(stats,indent=2))
