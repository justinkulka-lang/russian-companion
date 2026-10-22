"""Per gating objective: do remediation steps (body + examples) contain each listed check's cue and key? (audit working check)"""
import json, sys, unicodedata, collections
d = json.load(open(sys.argv[1]))
items = {i['id']: i for i in d['items']}; ex = {e['id']: e for e in d['examples']}
steps = {st['id']: st for u in d['units'] for s in u['sessions'] for st in s['steps']}
def norm(s): return unicodedata.normalize('NFC', s or '').replace('́', '').lower()
rows = []; summary = collections.Counter()
for u in d['units']:
    for o in u['objectives']:
        if not o.get('checkItemIds') or not o.get('remediationStepIds'): continue
        txt = []
        for sid in o['remediationStepIds']:
            st = steps[sid]; txt.append(st['body'])
            for e in st.get('exampleIds', []): txt += [ex[e]['display'], ex[e]['note']]
        T = norm(' '.join(txt))
        for iid in o['checkItemIds']:
            it = items[iid]
            cue = it.get('promptLetter') or ''
            key_in = norm(it['answer']) in T
            cue_in = (not cue) or norm(cue) in T
            status = 'BOTH' if key_in and cue_in else ('KEY_ONLY' if key_in else ('CUE_ONLY' if cue_in and cue else 'NEITHER'))
            summary[status] += 1
            rows.append((u['number'], o['id'], iid, status, cue[:40], it['answer'][:60]))
for r in rows:
    if r[3] in ('NEITHER',): print(*r, sep='\t')
print(summary)
by_obj = collections.defaultdict(list)
for r in rows: by_obj[(r[0], r[1])].append(r[3])
bad = [(k, v) for k, v in by_obj.items() if all(x == 'NEITHER' for x in v)]
print('objectives where remediation contains no check cue/key:', len(bad))
for k, v in bad: print('  ', k, len(v))
