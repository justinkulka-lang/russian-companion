"""Read-only CC010 reconciliation. Inventory counts are not executed tests."""
import collections, csv, hashlib, json, subprocess, sys
from pathlib import Path
sys.dont_write_bytecode = True
repo = Path(sys.argv[1]).resolve(); out = Path(sys.argv[2]).resolve()
sys.path.insert(0, str(repo))
from catalog.curriculum_storage import loads
raw = (repo/'app/src/main/assets/content/v2-curriculum.json').read_bytes(); pack = loads(raw)
items = {i['id']: i for i in pack['items']}
objectives = {o['id']: o for u in pack['units'] for o in u['objectives']}
positions = collections.defaultdict(list)
for u in pack['units']:
    for sn, s in enumerate(u['sessions']):
        for tn, t in enumerate(s['steps']):
            for item in t.get('itemIds', []):
                positions[item].append(dict(unit=u['number'], sessionId=s['id'], sessionIndex=sn,
                    stepId=t['id'], stepIndex=tn, kind=t['kind']))
pairs = [
 ['u8-g-spelling-0','u8-i-plural-6'], ['u10-i-have-1','u10-i-absence-3'],
 ['u10-g-absence-0','u10-i-have-3'], ['u10-i-have-4','u10-i-absence-4'],
 ['u23-g-ranges-0','u23-i-ranges-2'], ['u23-g-during-event-0','u23-i-during-event-2'],
 ['u23-g-duration-0','u23-i-duration-2'], ['u24-g-concord-0','u24-i-concord-2'],
]
profiles = {}
for profile in ['PROMPT_CUE_KEY_ORDERED_OPTIONS','CUE_KEY_ORDERED_OPTIONS','CUE_KEY_OPTION_SET']:
    grouped = collections.defaultdict(list)
    for item in pack['items']:
        objective = objectives[item['objectiveId']]
        if objective['unitNumber'] < 6 or objective['kind'] != 'RECOGNITION' or not objective.get('gating', True): continue
        key = (objective['unitNumber'], item.get('promptLetter'), item['answer'],
               tuple(sorted(item['options']) if profile == 'CUE_KEY_OPTION_SET' else item['options']))
        if profile == 'PROMPT_CUE_KEY_ORDERED_OPTIONS': key += (item['prompt'],)
        grouped[key].append(item['id'])
    groups = [ids for ids in grouped.values() if len(ids) > 1]
    profiles[profile] = dict(count=len(groups), groups=groups)
rows = []
for number, (a,b) in enumerate(pairs, 1):
    first, second = items[a], items[b]
    assert first.get('promptLetter') == second.get('promptLetter') and first['answer'] == second['answer']
    assert sorted(first['options']) == sorted(second['options'])
    owners = list(dict.fromkeys([first['objectiveId'], second['objectiveId']]))
    route = [positions[a][0], positions[b][0]]
    order = lambda v: (v['sessionIndex'], v['stepIndex'])
    rows.append(dict(group=f'CC010-G{number:02}', itemIds=[a,b], items=[first,second], routes=route,
        firstBeforeSecondInPrescribedPath=order(route[0]) < order(route[1]),
        exactPromptEqual=first['prompt']==second['prompt'], orderedOptionsEqual=first['options']==second['options'],
        objectiveChains=[dict(objective=objectives[owner], sessions=[s for u in pack['units'] for s in u['sessions']
            if owner in s['objectiveIds'] and any(t['kind']=='TEACH' for t in s['steps'])],
            requiredChecksOutsideThisPair=[i for i in objectives[owner]['checkItemIds'] if i not in [a,b]]) for owner in owners],
        engineeringDisposition='RETAIN_BOUNDED_RECOGNITION_REHEARSAL_PENDING_QUALIFIED_RECHECK',
        unseenTransferClaim=False, independentSemanticAcceptance=False))
result = dict(sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=repo,text=True).strip(),
    assetSha256=hashlib.sha256(raw).hexdigest(),authority='MA2-023;MA2-082;original CC010 work order',
    auditedGroups=rows, profiles=profiles,
    oldInventory='audit/independent-gpt61-2026-10-12/IA016_CURRENT_DUPLICATE_INVENTORY.json',
    explanation='Old mixed-role count2 plus3 same-role groups equals5 full-prompt/ordered-option groups. Dropping prompt adds3 Unit23 groups;ignoring option order adds Unit24. The ninth relaxed group is guided-only Unit9, outside the eight original CC010 targets.',
    scope='Static signatures and prescribed ordering, not exhaustive semantic equivalence, novel transfer, source/rights qualification or execution counts.')
(out/'REUSE_RECONCILIATION.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
with (out/'FINDING_ENGINEERING_DISPOSITIONS.csv').open('w',newline='') as f:
    writer=csv.writer(f); writer.writerow(['group','item_ids','first_before_second','exact_prompt_equal','ordered_options_equal','disposition','independent_acceptance'])
    for row in rows: writer.writerow([row['group'],';'.join(row['itemIds']),row['firstBeforeSecondInPrescribedPath'],row['exactPromptEqual'],row['orderedOptionsEqual'],row['engineeringDisposition'],'PENDING'])
print('Eight audit targets retained;profile counts', {k:v['count'] for k,v in profiles.items()})
