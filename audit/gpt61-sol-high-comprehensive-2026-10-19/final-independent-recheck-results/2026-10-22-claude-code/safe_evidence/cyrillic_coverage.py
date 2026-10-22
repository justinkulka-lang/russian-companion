"""Mechanical MA2-092 probe: every displayed Cyrillic span vs provenance shippedText (same unit, then any unit).
A covered span is only a text-identity match, not source/rights/semantic qualification."""
import json, re, sys, collections
d = json.load(open(sys.argv[1]))
CYR = re.compile(r"[Ѐ-ӿ́̀]+(?:[\s\-‐–—'’.,!?:;«»„“\"()]*[Ѐ-ӿ́̀]+)*")
prov_by_unit = collections.defaultdict(list)
for r in d['provenanceRecords']:
    prov_by_unit[r['unitNumber']].append(r['shippedText'])
all_prov = "\u0000".join(r['shippedText'] for r in d['provenanceRecords'])
unit_blob = {u: "\u0000".join(v) for u, v in prov_by_unit.items()}
obj_unit = {o['id']: u['number'] for u in d['units'] for o in u['objectives']}
def strip_stress(s): return s.replace('́', '').replace('̀', '')
all_prov_ns = strip_stress(all_prov)
unit_blob_ns = {u: strip_stress(b) for u, b in unit_blob.items()}
texts = []  # (unit, kind, id, field, text)
for u in d['units']:
    for s in u['sessions']:
        for st in s['steps']:
            for f in ['title', 'body', 'bookDirection', 'companionDirection', 'unavailableReason']:
                if st.get(f): texts.append((u['number'], 'step', st['id'], f, st[f]))
for i in d['items']:
    un = obj_unit[i['objectiveId']]
    for f in ['prompt', 'promptLetter', 'answer', 'feedback']:
        if i.get(f): texts.append((un, 'item', i['id'], f, i[f]))
    for k, o in enumerate(i['options']): texts.append((un, 'item', i['id'], f'option{k}', o))
    for k, o in enumerate(i.get('distractorRationales', [])):
        if o: texts.append((un, 'item', i['id'], f'rationale{k}', o))
for e in d['examples']:
    un = obj_unit[e['objectiveId']]
    for f in ['display', 'note']:
        texts.append((un, 'example', e['id'], f, e[f]))
for r in d['references']:
    for f in ['title', 'summary', 'detail']:
        texts.append((r['introducedInUnit'], 'reference', r['id'], f, r[f]))
stats = collections.Counter(); uncovered = []
for un, kind, oid, f, t in texts:
    for m in CYR.finditer(t):
        span = m.group(0).strip(" -‐–—'’.,!?:;«»„“\"()")
        if not span or not re.search(r"[Ѐ-ӿ]", span): continue
        stats['spans'] += 1
        if span in unit_blob.get(un, ''): stats['sameUnitExact'] += 1; continue
        if span in all_prov: stats['otherUnitExact'] += 1; continue
        ns = strip_stress(span)
        if ns in unit_blob_ns.get(un, '') or ns in all_prov_ns: stats['stressInsensitiveOnly'] += 1; continue
        # try word-level: every word appears somewhere
        words = [w for w in re.findall(r"[Ѐ-ӿ́̀]+", span)]
        if all(strip_stress(w) in all_prov_ns for w in words): stats['allWordsCoveredSeparately'] += 1; uncovered.append((un, kind, oid, f, span, 'WORDS_ONLY')); continue
        stats['uncovered'] += 1; uncovered.append((un, kind, oid, f, span, 'UNCOVERED'))
print(json.dumps(dict(stats), ensure_ascii=False))
by = collections.Counter((u[0], u[1], u[5]) for u in uncovered)
print(json.dumps({f"{k[0]}|{k[1]}|{k[2]}": v for k, v in sorted(by.items())}, ensure_ascii=False))
with open(sys.argv[2], 'w', encoding='utf-8') as fh:
    for u in uncovered: fh.write(json.dumps(u, ensure_ascii=False) + "\n")
