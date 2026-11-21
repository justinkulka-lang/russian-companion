"""Build the year-one audio manifest from the curriculum pack and the bundled clip catalog.

Usage: python3 -I catalog/build_audio_manifest.py PACK.json BUNDLED.json OUT.csv
Rerun after every content change; it lists every learner-facing Russian string that needs audio.
"""
import json, sys, re, unicodedata, collections, csv
pack = json.load(open(sys.argv[1])); bundled = json.load(open(sys.argv[2])); out = sys.argv[3]
CYR = 'А-Яа-яЁё'
ACC = '́̀'
span_re = re.compile(rf"[{CYR}][{CYR}{ACC}\-‑ ,]*[{CYR}{ACC}]|[{CYR}]")
def nfc(s): return unicodedata.normalize('NFC', s)
def strip_acc(s): return ''.join(c for c in unicodedata.normalize('NFD', s) if c not in ACC).replace('й','й')
def key(s): return unicodedata.normalize('NFC', strip_acc(s)).lower().strip(' ,-')
def has_acc(s): return any(c in ACC for c in unicodedata.normalize('NFD', s))
obj2u = {}
for u in pack['units']:
    for o in u['objectives']: obj2u[o['id']] = u['number']
occ = collections.defaultdict(lambda: {'units': set(), 'forms': collections.Counter(), 'where': set(), 'sentence': False})
def add(text, unit, where, full=None):
    if unit is None: return
    for m in span_re.finditer(nfc(text)):
        s = m.group(0).strip(' ,')
        if not s: continue
        # split comma lists into separate words/phrases
        parts = [p.strip() for p in s.split(',')] if ',' in s and len(s.split()) <= 6 else [s]
        for p in parts:
            p = p.strip(' -‑')
            if not p: continue
            k = key(p)
            if not k: continue
            r = occ[k]; r['units'].add(unit); r['forms'][p] += 1; r['where'].add(where)
            # sentence if followed by terminal punctuation in source
            end = m.end()
            if full is None and end < len(text) and text[end:end+1] in '.!?' and len(p.split()) >= 2: r['sentence'] = True
# examples
for e in pack['examples']:
    u = obj2u.get(e.get('objectiveId'))
    if e['kind'] in ('PRINT_PAIR','LETTER_FORM','CONFUSABLE_PAIR'): continue
    add(e['display'], u, 'example')
for it in pack['items']:
    u = obj2u.get(it.get('objectiveId'))
    # Only the cue and the reviewed answer: wrong options may show deliberately wrong forms.
    for f in ('promptLetter','answer'):
        if it.get(f): add(it[f], u, 'item')
for u in pack['units']:
    for s in u['sessions']:
        for st in s['steps']:
            add(st.get('title') or '', u['number'], 'teaching'); add(st.get('body') or '', u['number'], 'teaching')
for r in pack['references']:
    for f in ('title','summary','detail'): add(r.get(f) or '', r.get('introducedInUnit'), 'reference')
# clips
clips = {}
txt = {t['id']: t['stressMarkedText'] for t in bundled['sourceTexts']}
for a in bundled['audioModels']:
    t = txt[a['sourceTextId']]; clips.setdefault(key(t), []).append((a['id'], nfc(t)))
letters = pack['letters']
rows = []
for L in letters:
    k = key(L['lowercase'])
    c = clips.get(k)
    rows.append(dict(id='L-'+L['id'].split('-')[1], kind='LETTER', text=f"{L['uppercase']} {L['lowercase']}", first_unit=1, units='1',
        priority='P1', status='NEEDS_RECORDING', clip=';'.join(x[0] for x in c) if c else '',
        clip_text=';'.join(x[1] for x in c) if c else '',
        flags='record letter name, then its sound' + ('; existing word clip covers the sound only' if c else ''), where='letters'))
skipped = collections.Counter()
LETTER_NAMES = {'а':'а','б':'бэ','в':'вэ','г':'гэ','д':'дэ','е':'е','ё':'ё','ж':'жэ','з':'зэ','и':'и','й':'и краткое','к':'ка','л':'эль','м':'эм','н':'эн','о':'о','п':'пэ','р':'эр','с':'эс','т':'тэ','у':'у','ф':'эф','х':'ха','ц':'цэ','ч':'че','ш':'ша','щ':'ща','ъ':'твёрдый знак','ы':'ы','ь':'мягкий знак','э':'э','ю':'ю','я':'я'}
for r_ in rows:
    lc = r_['text'].split()[-1]
    r_['text'] = r_['text'] + ' (name: ' + LETTER_NAMES[lc] + ')'
NAMES = set(LETTER_NAMES.values())
NOT_RECORDED = {'ть','ах','ях','ами','ом','ым','ями','ович','кв','ул','беж','нов','сущ','ий','ая','ое','ые','ого','ому','ем','ой','ую','ей','ов','ев'}
for k, r in occ.items():
    if k in NAMES:
        skipped['letter name (covered by letter row)'] += 1; continue
    if k in NOT_RECORDED:
        skipped['ending or abbreviation (not recorded)'] += 1; continue
    if all(len(w) == 1 for w in k.replace(',', ' ').split()):
        skipped['letter list (covered by letter rows)'] += 1; continue
    if len(k) == 1:  # single letters covered by LETTER rows
        skipped['single-letter (covered by letter rows)'] += 1; continue
    forms = r['forms']
    stressed = [f for f in forms if has_acc(f)]
    stress_keys = {nfc(f).lower() for f in stressed}
    variants = sorted(stress_keys) if len(stress_keys) > 1 else [ (stressed[0] if stressed else forms.most_common(1)[0][0]) ]
    # Teaching shows some wrong-stress forms only as the rejected contrast; never record those.
    REJECTED = {nfc(x) for x in ['написа́нный','прочита́нный','обсуждаемы́й','оставляемы́й']}
    kept = [v for v in variants if nfc(v) not in REJECTED]
    skipped['rejected wrong-stress form (teaching contrast only)'] += len(variants) - len(kept)
    homograph = len(kept) > 1
    variants = kept
    for v in variants:
        words = len(k.split())
        kind = 'LETTER' if False else ('SENTENCE' if r['sentence'] or words >= 5 else ('PHRASE' if words >= 2 else 'WORD'))
        fu = min(r['units'])
        pr = 'P1' if fu <= 5 else ('P2' if fu <= 15 else 'P3')
        c = clips.get(k)
        flags = []
        if homograph: flags.append('HOMOGRAPH: stress variants in course')
        if not has_acc(v) and len(k.replace(' ','')) > 1 and any(ch in 'аеиоуыэюяё' for ch in k) and sum(ch in 'аеиоуыэюя' for ch in k) > 1: flags.append('course text has no stress mark')
        if len(k) <= 3 and not c: flags.append('short: check it is a word, not an ending')
        status = 'NEEDS_RECORDING'
        clip_ids = clip_txt = ''
        if c:
            clip_ids = ';'.join(x[0] for x in c); clip_txt = ';'.join(x[1] for x in c)
            if has_acc(v) and all(nfc(x[1]).lower() != nfc(v).lower() and has_acc(x[1]) for x in c):
                flags.append('clip stress differs from course text'); status = 'NEEDS_RECORDING'
            else: status = 'EXISTING_CLIP_CANDIDATE'
        rows.append(dict(id='', kind=kind, text=v, first_unit=fu, units=','.join(map(str, sorted(r['units']))), priority=pr,
            status=status, clip=clip_ids, clip_text=clip_txt, flags='; '.join(flags), where=','.join(sorted(r['where']))))
# Owner-approved T14 additions (homographs and final devoicing). Status PLANNED until T14 adds them from a licensed source.
PLANNED = [('за́мок','castle (stress on first syllable)'),('замо́к','lock (stress on second syllable)'),('му́ка','torment'),('мука́','flour'),
           ('луг','meadow: sounds like лук (final devoicing)'),('лук','onion'),('код','code: sounds like кот (final devoicing)'),('кот','cat'),
           ('плод','fruit: sounds like плот (final devoicing)'),('плот','raft')]
for t, note in PLANNED:
    k = key(t)
    if k in occ: continue
    c = clips.get(k)
    rows.append(dict(id='', kind='WORD', text=t, first_unit=2, units='2', priority='P1', status='PLANNED_T14',
        clip=';'.join(x[0] for x in c) if c else '', clip_text=';'.join(x[1] for x in c) if c else '',
        flags='planned addition: ' + note + ('; existing clip candidate' if c else ''), where='planned'))
ALPHA = 'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ'
rows.sort(key=lambda r: (r['first_unit'], {'LETTER':0,'WORD':1,'PHRASE':2,'SENTENCE':3}[r['kind']],
    ALPHA.index(r['text'][0]) if r['kind'] == 'LETTER' else 0, key(r['text'])))
n = collections.Counter()
for r in rows:
    if r['kind'] != 'LETTER':
        n[r['first_unit']] += 1; r['id'] = f"A{r['first_unit']:02d}-{n[r['first_unit']]:03d}"
with open(out, 'w', newline='', encoding='utf-8') as f:
    w = csv.DictWriter(f, fieldnames=['id','priority','first_unit','units','kind','text','status','clip','clip_text','flags','where'])
    w.writeheader(); [w.writerow({k: r[k] for k in w.fieldnames}) for r in rows]
print('rows', len(rows)); print('skipped', dict(skipped))
c = collections.Counter((r['priority'], r['status']) for r in rows); print(sorted(c.items()))
print(collections.Counter(r['kind'] for r in rows))
print('flags', collections.Counter(fl for r in rows for fl in r['flags'].split('; ') if fl))
