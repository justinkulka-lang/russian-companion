"""Whole-curriculum mechanical item analysis used by the 2026-10-29 audit (supplementary to the six returns). Usage: python3 -I mechanical_analysis.py v2-curriculum.json out.json"""
import json, re, sys, collections, unicodedata, hashlib

d = json.load(open(sys.argv[1], encoding='utf-8'))
out = sys.argv[2]
items = {i['id']: i for i in d['items']}
ex = {e['id']: e for e in d['examples']}
steps = {}
step_unit = {}
item_steps = collections.defaultdict(list)
for u in d['units']:
    for s in u['sessions']:
        for st in s['steps']:
            steps[st['id']] = st
            step_unit[st['id']] = u['number']
            for i in st.get('itemIds') or []:
                item_steps[i].append(st['id'])
obj = {}
obj_unit = {}
for u in d['units']:
    for o in u['objectives']:
        obj[o['id']] = o
        obj_unit[o['id']] = u['number']


def norm(t):
    t = unicodedata.normalize('NFD', t or '')
    t = re.sub('[́̀]', '', t)
    return unicodedata.normalize('NFC', t).strip().lower()


def role(i):
    o = obj[i['objectiveId']]
    if i['id'] in (o.get('checkItemIds') or []):
        if o['kind'] == 'RECOGNITION' and o.get('gating', True):
            return 'REQUIRED_CHECK'
        return 'OPTIONAL_CHECK'
    kinds = {steps[s]['kind'] for s in item_steps.get(i['id'], [])}
    if 'GUIDED' in kinds:
        return 'GUIDED'
    if 'CHECK' in kinds:
        return 'CHECK_STEP_UNLISTED'
    return 'LAB_ONLY'


def step_text(sid):
    st = steps[sid]
    t = [st['body'], st.get('bookDirection') or '', st.get('companionDirection') or '']
    for e in st.get('exampleIds') or []:
        if e in ex:
            t += [ex[e]['display'], ex[e].get('note') or '']
    return '\n'.join(t)


# duplicate task groups: same cue + same option set + same key
sig = collections.defaultdict(list)
for i in items.values():
    k = (norm(i.get('promptLetter')), tuple(sorted(norm(o) for o in i['options'])), norm(i['answer']))
    sig[k].append(i['id'])
dup_group = {}
for k, v in sig.items():
    if len(v) > 1:
        gid = 'DUP-' + hashlib.sha256(repr(k).encode()).hexdigest()[:8]
        for x in v:
            dup_group[x] = gid
# same cue + same key (options may differ)
sig2 = collections.defaultdict(list)
for i in items.values():
    if i.get('promptLetter'):
        sig2[(norm(i['promptLetter']), norm(i['answer']))].append(i['id'])
near_dup = {}
for k, v in sig2.items():
    if len(v) > 1:
        for x in v:
            near_dup[x] = sorted(set(v) - {x})

LEAKVERB = re.compile(r'\b(choose|select|selecting|requires|request(?:s)? .{0,40}choose|the answer|matching description|is the matching)\b', re.I)
rows = []
for i in d['items']:
    o = obj[i['objectiveId']]
    u = obj_unit[i['objectiveId']]
    r = role(i)
    opts = i['options']
    cue = i.get('promptLetter') or ''
    cue_role = i.get('promptLetterRole', 'CUE')
    distractors = [x for x in opts if x != i['answer']]
    cue_dup_any = bool(cue) and cue_role == 'CUE' and any(norm(x) == norm(cue) for x in distractors)
    cue_dup_sole = cue_dup_any and len(distractors) == 1
    # teaching leak: key appears in an instruction step sentence that also contains a choose/select verb
    leak = False
    leak_step = ''
    for sid in (o.get('instructionStepIds') or []):
        if sid not in steps:
            continue
        for sent in re.split(r'(?<=[.;!?])\s+', step_text(sid)):
            if norm(i['answer']) and norm(i['answer']) in norm(sent) and LEAKVERB.search(sent):
                leak = True
                leak_step = sid
                break
        if leak:
            break
    # remediation coverage
    rtxt = norm('\n'.join(step_text(s) for s in (o.get('remediationStepIds') or []) if s in steps))
    key_in_rem = norm(i['answer']) in rtxt if rtxt else False
    cue_in_rem = (not cue) or (norm(cue) in rtxt if rtxt else False)
    # rationale checks
    rats = i.get('distractorRationales') or []
    rat_issues = []
    for k, x in enumerate(opts):
        if x == i['answer']:
            continue
        rr = rats[k] if k < len(rats) else ''
        if not rr.strip():
            rat_issues.append('MISSING')
            continue
        m = re.search(r'pairs the shown form "([^"]+)"', rr)
        if m and cue and norm(m.group(1)) != norm(cue):
            rat_issues.append('SHOWN_FORM_MISMATCH')
        if rr.strip().startswith('Rejected reading:'):
            rat_issues.append('UNINFORMATIVE')
        if 'does not match the category or whole-phrase meaning requested here' in rr or 'is a different meaning or construction and does not match' in rr or 'This differs from the meaning or structure requested in this context' in rr or 'This is a different time relation' in rr or 'This differs from the classification or conversational function' in rr:
            rat_issues.append('TEMPLATE')
    jargon = bool(re.search(r'\b(admitted|frozen|retained|governed|supplied row|row glossed|first field|second field|object row)\b', i['prompt'] + ' ' + i['feedback'], re.I))
    key_pos = opts.index(i['answer'])
    rows.append(dict(item_id=i['id'], unit=u, objective_id=i['objectiveId'], role=r, kind=i['kind'],
                     source_id=i['sourceId'], n_options=len(opts), raw_key_position=key_pos,
                     cue_shown=bool(cue) and cue_role == 'CUE', cue_dup_sole=cue_dup_sole, cue_dup_any=cue_dup_any,
                     dup_group=dup_group.get(i['id'], ''), same_cue_key=';'.join(near_dup.get(i['id'], [])),
                     teaching_leak=leak, leak_step=leak_step, key_in_rem=key_in_rem, cue_in_rem=cue_in_rem,
                     rationale_issues=';'.join(sorted(set(rat_issues))), jargon=jargon,
                     steps=';'.join(item_steps.get(i['id'], []))))
# constant-key gating objectives
const = {}
for oid, o in obj.items():
    chk = o.get('checkItemIds') or []
    if o['kind'] == 'RECOGNITION' and o.get('gating', True) and len(chk) >= 2:
        keys = {norm(items[c]['answer']) for c in chk if c in items}
        if len(keys) == 1:
            const[oid] = sorted(keys)[0]
for r in rows:
    r['constant_key_objective'] = r['objective_id'] in const and r['role'] == 'REQUIRED_CHECK'
json.dump(dict(rows=rows, constant_key_objectives=const), open(out, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
req = [r for r in rows if r['role'] == 'REQUIRED_CHECK']
C = collections.Counter
print('roles', C(r['role'] for r in rows))
print('required', len(req), 'two-option', sum(r['n_options'] == 2 for r in req), 'cue_dup_sole', sum(r['cue_dup_sole'] for r in req), 'cue_dup_any', sum(r['cue_dup_any'] for r in req))
print('teaching_leak required', sum(r['teaching_leak'] for r in req), 'all', sum(r['teaching_leak'] for r in rows))
print('dup items', sum(bool(r['dup_group']) for r in rows), 'groups', len({r['dup_group'] for r in rows if r['dup_group']}), 'required in dup groups', sum(bool(r['dup_group']) for r in req))
print('same cue+key items', sum(bool(r['same_cue_key']) for r in rows))
print('rem: key absent', sum(not r['key_in_rem'] for r in req), 'cue absent', sum(not r['cue_in_rem'] for r in req))
print('rationale issues', C(x for r in rows for x in r['rationale_issues'].split(';') if x))
print('constant-key objectives', len(const), sorted(const))
print('jargon items', sum(r['jargon'] for r in rows))
print('raw key position (required)', C(r['raw_key_position'] for r in req))
