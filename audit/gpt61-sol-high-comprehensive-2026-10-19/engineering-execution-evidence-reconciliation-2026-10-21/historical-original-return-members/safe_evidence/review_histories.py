"""Independent JSON structure/key comparison. This does not execute Kotlin or restore Android."""
import json, hashlib, csv
from pathlib import Path
W=Path(__file__).resolve().parent
A=json.loads((W/'available_target_files.json').read_text())
paths=json.loads((W/'history_paths.json').read_text())
pack=json.loads((W.parent/'continuation_audit/expanded_pack.json').read_text())
items={x['id']:x for x in pack['items']}
def canonical(parts):
    return str(len(parts))+'{'+ ';'.join(str(len(p.encode('utf-8')))+':'+p for p in parts)+'}'
def digest(a):
    parts=[str(a[k]) for k in ['schema','id','targetUnit','policyId','policyDigest','responseRuleDigest','boundary']]
    parts += [canonical([str(s[k]) for k in ['objectiveId','skill','itemId','ownerUnit','itemDigest','unitDigest','teachingDigest']]) for s in a['slots']]
    return hashlib.sha256(canonical(parts).encode()).hexdigest()
rows=[]; groups={}
for path in paths:
    data=Path(A[path]['local']).read_bytes(); x=json.loads(data)
    assignments={a['id']:a for a in x['reviewAssignments']}; answers=x['reviewAnswers']
    defects=[]
    if len(assignments)!=len(x['reviewAssignments']):defects.append('duplicate assignment ID')
    if len({a['id'] for a in answers})!=len(answers):defects.append('duplicate answer ID')
    if len({a['acceptedOrder'] for a in answers})!=len(answers):defects.append('duplicate answer order')
    for a in assignments.values():
        if digest(a)!=a['digest']:defects.append('assignment digest mismatch')
        if a['boundary']<0 or a['boundary']>max([r['acceptedOrder'] for r in answers]+[0]):defects.append('boundary outside answer history')
    for r in answers:
        a=assignments.get(r['assignmentId'])
        if a is None or r['assignmentDigest']!=a['digest'] or r['slotIndex'] not in range(len(a['slots'])) or r['acceptedOrder']<=a['boundary'] or r['role']!='CUMULATIVE_REVIEW' or r['schema']!=1: defects.append('invalid answer link/order/role/schema')
    completions=[]
    for c in x['unitCompletions']:
        if not c.get('reviewAssignmentId'):continue
        a=assignments.get(c['reviewAssignmentId'])
        if a is None: defects.append('missing completion assignment');continue
        cutoff=c['reviewCompletionOrder']; latest={}
        for r in answers:
            if r['assignmentId']==a['id'] and a['boundary']<r['acceptedOrder']<=cutoff:
                if r['acceptedOrder']>latest.get(r['slotIndex'],{}).get('acceptedOrder',-1):latest[r['slotIndex']]=r
        matches=sum(i in latest and latest[i]['selected']==items.get(s['itemId'],{}).get('answer') for i,s in enumerate(a['slots']))
        completions.append({'unit':c['unitNumber'],'slots':len(a['slots']),'answeredAtCutoff':len(latest),'currentKeyMatchesAtCutoff':matches,'laterAnswers':sum(r['assignmentId']==a['id'] and r['acceptedOrder']>cutoff for r in answers)})
    groups.setdefault(str(Path(path).parent),{})[Path(path).stem]=(path,x)
    rows.append({'path':path,'git_blob':A[path]['gitBlob'],'sha256':hashlib.sha256(data).hexdigest(),'bytes':len(data),'document_schema':x['schemaVersion'],'assignment_schemas':';'.join(map(str,sorted({a['schema'] for a in assignments.values()}))),'assignments':len(assignments),'answers':len(answers),'completions':len(x['unitCompletions']),'structural_defects':';'.join(sorted(set(defects))) or 'NONE_FOUND_IN_CHECKED_FIELDS','completion_cutoff_comparison':json.dumps(completions,separators=(',',':')),'inspection':'JSON structural/hash/link/order review; supplied capture, not Android replay','remaining_limit':'Current key equality is not current-source eligibility; archive bytes, capture process and Android restore NOT_VERIFIED'})
witnesses=[]
for group,states in groups.items():
    lp,left=states['UNSTARTED_REVIEW'];rp,right=states['PARTIAL_REVIEW']
    changes=[k for k in left if left[k]!=right[k]]
    assert changes==['reviewAnswers'],(group,changes)
    count=1+int(left['learnerState'] is not None)+sum(len(left[k]) for k in ['steps','attempts','bookmarks','notes','tutorQueue','bookAttestations','unitCompletions','planInstances','activityLogs','evidence','tutorRequirements','decisions','recordings'])
    witnesses.append({'group':group,'before':lp,'after':rp,'changedCollections':changes,'addedReviewAnswers':len(right['reviewAnswers'])-len(left['reviewAnswers']),'sourceAlgorithmPrediction':{'additions':0,'replacements':count,'omissions':0,'conflicts':0},'kotlinOrAndroidExecuted':False})
with (W/'historical_lifecycle_review.csv').open('w',newline='') as f:
    writer=csv.DictWriter(f,fieldnames=list(rows[0]));writer.writeheader();writer.writerows(rows)
(W/'history_review_summary.json').write_text(json.dumps({'histories':len(rows),'groups':len(groups),'checkedStructuralDefects':sum(r['structural_defects']!='NONE_FOUND_IN_CHECKED_FIELDS' for r in rows),'reviewOnlyPreviewWitnesses':witnesses,'kotlinOrAndroidExecuted':False,'archiveBytesConservationVerified':False,'originalUnit7RestoreCause':'UNCONFIRMED'},indent=2))
print(json.dumps({'histories':len(rows),'groups':len(groups),'checkedStructuralDefects':sum(r['structural_defects']!='NONE_FOUND_IN_CHECKED_FIELDS' for r in rows),'witnessPredictions':[w['sourceAlgorithmPrediction'] for w in witnesses],'currentKeyCutoffComparisons':'recorded per history; not Kotlin current-credit results'}))
