"""Independent mechanical checks over the pinned raw curriculum (not semantic review)."""
import json, hashlib, collections, sys

P = sys.argv[1]
raw = open(P, 'rb').read()
d = json.loads(raw)
out = {}

def sha(s): return hashlib.sha256(s.encode('utf-8')).hexdigest()
def ctext(v): return f"{len(v.encode('utf-8'))}:{v}"
def cparts(parts): return f"{len(parts)}{{" + ";".join(ctext(p) for p in parts) + "}"

# 1. sourceTextPool expansion (mirror of V2PackLoader.expandedDocument)
pool = d['sourceTextPool']
bad_pool = [k for k, v in pool.items() if sha(v) != k]
used = set(); ref_err = []
for r in d['provenanceRecords']:
    ref = r.get('originalSourceTextRef')
    if ref is None:
        continue
    if 'originalSourceText' in r or r.get('sourceHash') != ref or ref not in pool:
        ref_err.append(r.get('itemId'))
    used.add(ref)
out['sourceTextPool'] = dict(entries=len(pool), badHashes=len(bad_pool), referencing=sum(1 for r in d['provenanceRecords'] if 'originalSourceTextRef' in r),
                             inline=sum(1 for r in d['provenanceRecords'] if 'originalSourceText' in r), refErrors=len(ref_err), unused=len(set(pool) - used))

# 2. identities
units = d['units']
steps = {}; sessions = {}; objectives = {}
dup = collections.Counter()
for u in units:
    for o in u['objectives']:
        dup['obj:' + o['id']] += 1; objectives[o['id']] = (u['number'], o)
    for s in u['sessions']:
        dup['ses:' + s['id']] += 1; sessions[s['id']] = (u['number'], s)
        for st in s['steps']:
            dup['step:' + st['id']] += 1; steps[st['id']] = (u['number'], s['id'], st)
items = {i['id']: i for i in d['items']}; examples = {e['id']: e for e in d['examples']}
refs = {r['id']: r for r in d['references']}; sources = {s['id']: s for s in d['sources']}
rights = {r['id']: r for r in d['rights']}; letters = {l['id']: l for l in d['letters']}
for coll, name in [(d['items'], 'item'), (d['examples'], 'ex'), (d['references'], 'ref'), (d['sources'], 'src'), (d['rights'], 'rights'), (d['letters'], 'letter'), (d['sourceCapabilities'], 'cap'), (d['authoredEnglishRecords'], 'eng'), (d['ownedEditionMappings'], 'map'), (d['unitReviews'], 'urev')]:
    for x in coll: dup[name + ':' + x['id']] += 1
out['duplicateIds'] = [k for k, v in dup.items() if v > 1]

# 3. link integrity
missing = collections.Counter()
step_items = collections.defaultdict(list)
for sid, (un, ses, st) in steps.items():
    for i in st.get('itemIds', []):
        step_items[i].append(sid)
        if i not in items: missing['step->item'] += 1
    for e in st.get('exampleIds', []):
        if e not in examples: missing['step->example'] += 1
    for r in st.get('referenceIds', []):
        if r not in refs: missing['step->reference'] += 1
    if st.get('remediationStepId') and st['remediationStepId'] not in steps: missing['step->remediation'] += 1
for oid, (un, o) in objectives.items():
    for k in ['instructionStepIds', 'practiceStepIds', 'remediationStepIds']:
        for s in o.get(k, []):
            if s not in steps: missing['obj->' + k] += 1
            elif steps[s][0] != un: missing['obj->' + k + ':otherUnit'] += 1
    for i in o.get('checkItemIds', []):
        if i not in items: missing['obj->check'] += 1
        elif items[i]['objectiveId'] != oid: missing['obj->check:objectiveMismatch'] += 1
    for e in o.get('exampleIds', []):
        if e not in examples: missing['obj->example'] += 1
for i in d['items']:
    if i['objectiveId'] not in objectives: missing['item->objective'] += 1
    if i['sourceId'] not in sources: missing['item->source'] += 1
for e in d['examples']:
    if e['objectiveId'] not in objectives: missing['example->objective'] += 1
    if e['sourceId'] not in sources: missing['example->source'] += 1
    if e.get('letterId') and e['letterId'] not in letters: missing['example->letter'] += 1
for r in d['rights']:
    if r['sourceId'] not in sources: missing['rights->source'] += 1
for r in d['references']:
    if r['sourceId'] not in sources: missing['reference->source'] += 1
for c in d['sourceCapabilities']:
    if c['sourceId'] not in sources: missing['cap->source'] += 1
    if c['objectiveId'] not in objectives: missing['cap->objective'] += 1
out['missingLinks'] = dict(missing)
out['sourcesWithoutRights'] = sorted(set(sources) - {r['sourceId'] for r in d['rights']})
out['rightsNotPermitting'] = [r['id'] for r in d['rights'] if not r['permitsAppUse']]

# 4. orphan content
all_step_items = set(step_items)
check_items = {i for _, o in objectives.values() for i in o.get('checkItemIds', [])}
out['itemsInNoStep'] = sorted(set(items) - all_step_items)
out['itemsInMultipleSteps'] = sum(1 for v in step_items.values() if len(v) > 1)
step_examples = {e for _, _, st in steps.values() for e in st.get('exampleIds', [])}
obj_examples = {e for _, o in objectives.values() for e in o.get('exampleIds', [])}
out['examplesInNoStep'] = len(set(examples) - step_examples)
out['examplesInNoStepNorObjective'] = sorted(set(examples) - step_examples - obj_examples)
out['objectiveCheckItemsTotal'] = len(check_items)
ref_steps = {r for _, _, st in steps.values() for r in st.get('referenceIds', [])}
out['referencesNotLinkedFromSteps'] = sorted(set(refs) - ref_steps)

# 5. item key/option sanity
iss = collections.Counter(); first_raw = 0; positions = collections.Counter()
for i in d['items']:
    o = i['options']
    if i['answer'] not in o: iss['answerNotInOptions'] += 1
    if o.count(i['answer']) != 1: iss['answerNotUnique'] += 1
    if len(set(o)) != len(o): iss['duplicateOptions'] += 1
    if len(o) < 2: iss['fewerThan2Options'] += 1
    if any(not x.strip() for x in o): iss['blankOption'] += 1
    if not i['feedback'].strip(): iss['blankFeedback'] += 1
    if i.get('distractorRationales') and len(i['distractorRationales']) != len(o) - 1: iss['rationaleCountMismatch'] += 1
    if o and o[0] == i['answer']: first_raw += 1
    positions[len(o)] += 1
out['itemIssues'] = dict(iss)
out['rawFirstPositionKeys'] = f"{first_raw}/{len(d['items'])}"
out['optionCountDistribution'] = dict(sorted(positions.items()))
out['itemsWithDistractorRationales'] = sum(1 for i in d['items'] if i.get('distractorRationales'))
out['itemsWithAssessment'] = sum(1 for i in d['items'] if i.get('assessment'))
out['assessmentIndependentTrue'] = sum(1 for i in d['items'] if (i.get('assessment') or {}).get('independent'))

# 6. reproduce presentation positions
VERSION = "sha256-option-order-v1"
def presented(item, context):
    return sorted(item['options'], key=lambda o: (sha(cparts([VERSION, context, item['id'], o])), o))
lesson_pos = collections.Counter(); req_pos = collections.Counter(); lesson_n = 0
for iid, sids in step_items.items():
    it = items[iid]
    for sid in sids:
        p = presented(it, cparts(["lesson", sid])).index(it['answer'])
        lesson_pos[p] += 1; lesson_n += 1
        if iid in check_items: req_pos[p] += 1
out['lessonPresentationKeyPosition'] = dict(sorted(lesson_pos.items()))
out['lessonPresentationKeyPositionObjectiveChecks'] = dict(sorted(req_pos.items()))
# expected uniform share for first position given option counts
exp_first = sum(1 / len(items[i]['options']) for i, s in step_items.items() for _ in s)
out['lessonFirstPositionExpectedUnderUniform'] = round(exp_first, 1)

# 7. per-unit counts
per = []
for u in units:
    n = u['number']
    objs = u['objectives']
    per.append(dict(unit=n, installState=u['installState'], readiness=u.get('readiness'), mappingStatus=u['mappingStatus'], penguinLesson=u['penguinLesson'],
                    prerequisites=u.get('prerequisiteUnitNumbers'), objectives=len(objs), gatingObjectives=sum(1 for o in objs if o.get('gating', True)),
                    sessions=len(u['sessions']), steps=sum(len(s['steps']) for s in u['sessions']),
                    items=sum(1 for i in d['items'] if objectives.get(i['objectiveId'], (None,))[0] == n),
                    examples=sum(1 for e in d['examples'] if objectives.get(e['objectiveId'], (None,))[0] == n),
                    checkItems=sum(len(o.get('checkItemIds', [])) for o in objs),
                    caps=sum(1 for c in d['sourceCapabilities'] if c['unitNumber'] == n),
                    provenance=sum(1 for r in d['provenanceRecords'] if r['unitNumber'] == n),
                    stepKinds=dict(collections.Counter(st['kind'] for s in u['sessions'] for st in s['steps'])),
                    minimumDistinctChecks=(u.get('advancement') or {}).get('minimumDistinctChecks'),
                    cumulativePolicy=(u.get('advancement') or {}).get('cumulativeReviewPolicyId')))
out['perUnit'] = per
out['installStates'] = dict(collections.Counter(u['installState'] for u in units))
out['readiness'] = dict(collections.Counter(u.get('readiness') for u in units))
out['mappingStatus'] = dict(collections.Counter(u['mappingStatus'] for u in units))
out['unitPenguinLessonEqualsNumber'] = all(u['penguinLesson'] == u['number'] for u in units)
out['reviewStatuses'] = dict(collections.Counter(x.get('reviewStatus') for coll in ['items', 'examples', 'references', 'letters', 'provenanceRecords'] for x in d[coll]))
out['unitReviewStatuses'] = dict(collections.Counter(r['status'] for r in d['unitReviews']))
out['unitReviewStages'] = dict(collections.Counter('|'.join(r.get('reviewChainStages', [])) for r in d['unitReviews']))
out['mappingStatuses'] = dict(collections.Counter(m['status'] for m in d['ownedEditionMappings']))
out['provenanceRecordTypes'] = dict(collections.Counter(r['recordType'] for r in d['provenanceRecords']))
out['provenanceSources'] = dict(collections.Counter(r['sourceId'] for r in d['provenanceRecords']))
out['provenanceWithSynthesis'] = sum(1 for r in d['provenanceRecords'] if r.get('synthesis'))
out['provenanceReviewerPresent'] = sum(1 for r in d['provenanceRecords'] if r.get('reviewerIdentity'))
out['containsGeneratedRussianTrue'] = sum(1 for i in d['items'] if i.get('containsGeneratedRussian'))
out['itemKinds'] = dict(collections.Counter(i['kind'] for i in d['items']))
out['itemLanguages'] = dict(collections.Counter(i['language'] for i in d['items']))
out['referenceClassifications'] = dict(collections.Counter(r.get('classification') for r in d['references']))
out['authoredEnglishClassifications'] = dict(collections.Counter(r['classification'] for r in d['authoredEnglishRecords']))
print(json.dumps(out, ensure_ascii=False, indent=1))
