import json,re,collections,sys,unicodedata
d=json.load(open(sys.argv[1])); remcov=sys.argv[2]
items={i['id']:i for i in d['items']}
obj_unit={}; req=set(); guided=set(); objective_checks={}
for u in d['units']:
    for o in u['objectives']:
        obj_unit[o['id']]=u['number']
        if o['kind']=='RECOGNITION' and o.get('gating',True):
            req|=set(o['checkItemIds']); objective_checks[o['id']]=o['checkItemIds']
    for s in u['sessions']:
        for st in s['steps']:
            if st['kind']=='GUIDED': guided|=set(st.get('itemIds') or [])
def norm(t): return unicodedata.normalize('NFC',re.sub('[́̀]','',unicodedata.normalize('NFD',t or ''))).strip().lower()
F=collections.defaultdict(set)
def add(fid,ids):
    for i in ids:
        if i in items: F[i].add(fid)
        else: print('WARN unknown item',fid,i,file=sys.stderr)
# mechanical: cue-identical sole/any distractor
for i in items.values():
    cue=norm(i.get('promptLetter'))
    if cue and any(norm(o)==cue for o in i['options'] if o!=i['answer']): F[i['id']].add('CC027')
# constant-key objectives
for oid,chk in objective_checks.items():
    keys={items[c]['answer'] for c in chk if c in items}
    if len(chk)>=2 and len(keys)==1: add('CC027',chk)
# two-option required checks
for c in req:
    if c in items and len(items[c]['options'])==2: F[c].add('CC036')
# jargon in learner-facing prompt/feedback/options
for i in items.values():
    txt=' '.join([i['prompt'],i['feedback']]+i['options'])
    if re.search(r'\b(frozen|admitted|Admitted|governed|retained|keyed)\b',txt): F[i['id']].add('CC029')
# remediation coverage NEITHER
for line in open(remcov):
    p=line.rstrip('\n').split('\t')
    if len(p)>=4 and p[3]=='NEITHER': add('CC021',[p[2]])
# curated (from unit reading notes)
add('CC001',[])
add('CC003',['u15-i-aspect-consolidate-1','u15-i-aspect-consolidate-2'])
add('CC004',['u21-i-purpose-1','u21-i-purpose-2'])
add('CC009',['u28-i-imperfective-adverb-1','u28-i-imperfective-adverb-2','u28-i-perfective-adverb-1','u28-i-perfective-adverb-2'])
add('CC010',['u8-g-spelling-0','u8-i-plural-6','u10-i-have-1','u10-i-absence-3','u10-g-absence-0','u10-i-have-3','u10-i-have-4','u10-i-absence-4','u23-g-ranges-0','u23-i-ranges-2','u23-g-during-event-0','u23-i-during-event-2','u23-g-duration-0','u23-i-duration-2','u24-g-concord-0','u24-i-concord-2'])
add('CC011',['u7-i-adverb-invariance-1','u7-i-adverb-invariance-2'])
add('CC015',['u4-i-verb-говорить-мы','u4-i-verb-видеть-он','u4-i-verb-писать-вы','u4-i-verb-идти-они'])
scripted=['u4-i-verb-говорить-мы','u4-i-verb-видеть-он','u4-i-verb-писать-вы','u4-i-verb-идти-они','u6-i-possessive-1','u6-i-possessive-2','u7-i-endings-1','u7-i-endings-2','u7-i-endings-3','u7-i-endings-4','u7-i-spelling-1','u7-i-spelling-2','u7-i-spelling-3','u8-i-spelling-1','u8-i-agreement-3','u9-i-odindva-4','u12-i-aspect-2','u13-i-past-aspect-2','u13-i-infinitive-1','u13-i-infinitive-2','u15-i-imperative-1','u15-i-imperative-2','u16-i-inst-sg-1','u16-i-inst-pl-1','u16-i-inst-pl-2','u16-i-inst-adj-1','u17-i-years-1','u17-i-years-2','u18-i-comparison-1','u18-i-comparison-2','u19-i-prefix-caution-2','u20-i-motion-2','u20-i-motion-3','u22-i-half-forms-2','u22-i-half-forms-3','u25-i-name-question-1','u25-i-name-question-2','u26-i-correspondence-1','u26-i-correspondence-2','u27-i-stress-2','u27-i-stress-3','u29-i-active-1','u29-i-active-2','u30-i-address-marks-2','u30-i-abbreviation-2','u30-i-particle-1','u30-i-particle-2']
add('CC022',[x for x in scripted if x in items])
add('CC024',[i['id'] for i in items.values() if re.search(r'\bspeak',' '.join([i.get('promptLetter') or '',i['prompt']]+i['options'])) and re.search('ска[жз]',norm(' '.join([i.get('promptLetter') or '']+i['options']+[i['answer']])))])
add('CC031',['u7-i-endings-1'])
labelrecall=['u7-i-adverb-invariance-1','u7-i-adverb-invariance-2','u7-i-manner-1','u7-i-manner-4','u13-i-tense-keep-2','u17-i-calendar-date-1','u17-i-age-1','u17-i-age-2','u19-i-obligation-1','u19-i-obligation-2','u24-i-stand-ins-1','u25-i-name-form-3','u25-i-diminutives-1','u25-i-diminutives-2','u26-i-letter-writing-1','u26-i-letter-writing-2','u27-i-stress-4','u30-i-dictionary-4','u30-i-dictionary-5','u30-i-abbreviation-1','u30-i-abbreviation-2','u30-i-particle-1','u30-i-particle-2']
labelrecall+=[i for i in items if re.match(r'u29-i-register-\d',i) or re.match(r'u26-i-postal',i) or re.match(r'u26-i-vy-capital',i) or re.match(r'u26-i-indefinite',i) or re.match(r'u25-i-(nickname-rule|address)-',i) or re.match(r'u29-i-(active|bookish-note)',i)]
add('CC026',[x for x in labelrecall if x in items])
nondiag=[i for i in items if re.match(r'u17-i-(ordinals|hours|years|minutes)-',i) or re.match(r'u22-i-(noun-forms|compounds)-',i) or re.match(r'u29-i-punctuation',i)]
nondiag+=['u16-i-inst-sg-2','u24-i-verbal-ne-1','u24-i-verbal-ne-2','u30-i-capstone-1','u30-i-capstone-2','u30-i-dictionary-2','u1-i-а-identity','u1-i-м-identity','u1-i-е-identity','u1-i-ё-identity','u1-i-ю-identity','u1-i-я-identity','u1-i-р-not-p','u1-i-н-not-h','u1-i-с-not-c','u1-i-в-not-b','u1-i-у-not-y','u1-i-х-not-x','u1-i-print-pair','u14-i-future-aspect-1','u14-i-future-aspect-2']
add('CC027',[x for x in nondiag if x in items])
absurd=[i for i in items if re.match(r'u27-i-comp-',i) or re.match(r'u29-i-reading-',i) or re.match(r'u26-i-postal',i) or re.match(r'u28-i-(imperfective|perfective)-adverb-',i)]+['u17-g-age-0']
add('CC028',[x for x in absurd if x in items])
dups=['u9-i-pattern-3','u9-i-quantity-4','u12-i-aspect-1','u12-i-aspect-2','u13-i-infinitive-1','u13-i-infinitive-2','u15-i-aspect-consolidate-1','u15-i-aspect-consolidate-2','u14-i-future-person-2','u12-i-future-imp-1','u14-g-future-person-0','u12-g-future-imp-0','u24-i-placement-1','u24-i-placement-2','u25-i-name-form-1','u25-i-name-form-2','u25-i-nickname-rule-1','u25-i-nickname-rule-2','u30-i-address-marks-1','u30-i-address-marks-2','u26-i-address-notation-1','u26-i-address-notation-2','u30-i-native-name-b','u1-i-name-be','u7-i-adverb-pairs-2','u7-g-adverb-pairs-0']
add('CC030',[x for x in dups if x in items])
src_ca={i['id'] for i in items.values() if i['sourceId']=='src-content-audit-research'}
for i in src_ca: F[i].add('CC035')
out={i:sorted(F.get(i,[])) for i in items}
json.dump({'flags':out,'required':sorted(req),'guided':sorted(guided),'obj_unit':obj_unit},open(sys.argv[3],'w'),ensure_ascii=False,indent=0)
c=collections.Counter(f for v in out.values() for f in v); print(sorted(c.items()))
print('items with any flag',sum(1 for v in out.values() if v),'required with flag',sum(1 for i in req if out.get(i)))
