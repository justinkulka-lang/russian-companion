"""Render every unit's full instructional chain for complete human reading (audit working view; not a deliverable)."""
import json, sys, os, collections
d = json.load(open(sys.argv[1]))
outdir = sys.argv[2]; os.makedirs(outdir, exist_ok=True)
items = {i['id']: i for i in d['items']}; examples = {e['id']: e for e in d['examples']}
refs = {r['id']: r for r in d['references']}
obj_unit = {o['id']: u['number'] for u in d['units'] for o in u['objectives']}
step_loc = {}
for u in d['units']:
    for s in u['sessions']:
        for st in s['steps']:
            step_loc[st['id']] = (u['number'], s['id'])
item_steps = collections.defaultdict(list)
for u in d['units']:
    for s in u['sessions']:
        for st in s['steps']:
            for i in st.get('itemIds', []): item_steps[i].append(st['id'])
mapping = {m['unitNumber']: m for m in d['ownedEditionMappings']}
urev = {r['unitNumber']: r for r in d['unitReviews']}
for u in d['units']:
    n = u['number']; L = []
    w = L.append
    w(f"===== UNIT {n}: {u['title']} | short={u.get('shortLabel')} | install={u['installState']} readiness={u.get('readiness')} penguinLesson={u['penguinLesson']}")
    w(f"topicSummary: {u['topicSummary']}")
    w(f"mapping: {u['mappingStatus']} :: {u['mappingNote']}")
    w(f"prereqs={u.get('prerequisiteUnitNumbers')} minutes={u.get('estimatedMinutes')} advancement={json.dumps(u.get('advancement'))}")
    w("--- OBJECTIVES")
    for o in u['objectives']:
        w(f"[{o['id']}] {o['title']} | kind={o['kind']} gating={o.get('gating', True)}")
        for k in ['instructionStepIds', 'practiceStepIds', 'checkItemIds', 'remediationStepIds', 'exampleIds']:
            if o.get(k): w(f"    {k}: {', '.join(o[k])}")
    w("--- SESSIONS / STEPS")
    seen_bodies = {}
    for s in u['sessions']:
        w(f"## SESSION {s['id']} '{s['title']}' {s['estimatedMinutes']}min objs={s.get('objectiveIds')}")
        for st in s['steps']:
            w(f"  [{st['id']}] {st['kind']} '{st['title']}' action='{st.get('dominantAction', 'Continue')}'")
            b = st['body']
            if b in seen_bodies: w(f"    BODY = same as {seen_bodies[b]}")
            else:
                seen_bodies[b] = st['id']; w("    BODY: " + b.replace("\n", "\n          "))
            for f in ['bookDirection', 'companionDirection', 'unavailableReason', 'remediationStepId']:
                if st.get(f): w(f"    {f}: {st[f]}")
            for r in st.get('referenceIds', []): w(f"    ref: {r} '{refs[r]['title']}'")
            for e in st.get('exampleIds', []):
                ex = examples[e]; w(f"    EX {e} [{ex['kind']}] {ex['display']}  ||  {ex['note']}")
            if st.get('itemIds'): w(f"    ITEMS: {', '.join(st['itemIds'])}")
    w("--- ITEMS (by objective)")
    for o in u['objectives']:
        own = [i for i in d['items'] if i['objectiveId'] == o['id']]
        if not own: continue
        w(f"## {o['id']}  ({len(own)} items; checkList={o.get('checkItemIds')})")
        for i in own:
            a = i.get('assessment') or {}
            role = 'CHECK' if i['id'] in o.get('checkItemIds', []) else 'nonlisted'
            w(f"  <{i['id']}> {i['kind']} {role} steps={item_steps.get(i['id'], [])} src={i['sourceId']}")
            if i.get('promptLetter'): w(f"    CUE({i.get('promptLetterRole', 'CUE')}): {i['promptLetter']}")
            w(f"    Q: {i['prompt']}")
            for k, opt in enumerate(i['options']):
                rat = (i.get('distractorRationales') or [''] * len(i['options']))
                rr = rat[k] if k < len(rat) else ''
                w(f"     {'*' if opt == i['answer'] else '-'} {opt}" + (f"   ~ {rr}" if rr else ""))
            w(f"    FB: {i['feedback']}")
            if a:
                w(f"    A: feature={a.get('testedFeature')} | mode={a.get('evidenceMode')} | indep={a.get('independent')} | cueLang={a.get('cueLanguage')} | relation={a.get('relationKind')} | response={a.get('responseKind')}")
                w(f"       why: {a.get('whyAnswerDemonstratesObjective')}")
                if a.get('distractorDifference'): w(f"       distractorDifference: {a.get('distractorDifference')}")
                if a.get('contrastMembers'): w(f"       contrast: {a.get('contrastMembers')}")
    orphan_ex = [e for e in d['examples'] if obj_unit[e['objectiveId']] == n and not any(e['id'] in st.get('exampleIds', []) for s in u['sessions'] for st in s['steps'])]
    if orphan_ex:
        w("--- EXAMPLES NOT IN ANY STEP")
        for ex in orphan_ex: w(f"  {ex['id']} [{ex['kind']}] {ex['display']} || {ex['note']} (obj {ex['objectiveId']})")
    caps = [c for c in d['sourceCapabilities'] if c['unitNumber'] == n]
    if caps:
        w("--- SOURCE CAPABILITIES")
        for c in caps:
            w(f"  {c['id']} src={c['sourceId']}@{c['sourceRevision'][:12]} rec={c['sourceRecordId']} obj={c['objectiveId']}")
            w(f"     claim: {c['supportedClaim']}")
            w(f"     span: {c['exactSpan'][:1200]}" + (" …[truncated]" if len(c['exactSpan']) > 1200 else ""))
    m = mapping.get(n)
    if m: w("--- OWNED EDITION MAPPING: " + json.dumps(m, ensure_ascii=False))
    r = urev.get(n)
    if r: w("--- UNIT REVIEW: " + json.dumps(r, ensure_ascii=False))
    pr = [p for p in d['provenanceRecords'] if p['unitNumber'] == n]
    w(f"--- PROVENANCE: {len(pr)} records; types={dict(collections.Counter(p['recordType'] for p in pr))}; sources={dict(collections.Counter(p['sourceId'] for p in pr))}")
    open(os.path.join(outdir, f"unit{n:02d}.txt"), 'w', encoding='utf-8').write("\n".join(L) + "\n")
print("ok")
