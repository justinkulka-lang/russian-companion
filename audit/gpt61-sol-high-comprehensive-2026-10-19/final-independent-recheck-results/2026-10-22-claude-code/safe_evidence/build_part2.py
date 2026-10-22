"""Build item, unit, curriculum-entity and book coverage returns."""
import csv, json, os, sys, hashlib, collections

S = sys.argv[1]; OUT = sys.argv[2]
d = json.load(open(os.path.join(S, 'work', 'curriculum.json'), encoding='utf-8'))
flags = json.load(open(os.path.join(S, 'work', 'item_flags.json'), encoding='utf-8'))
F = flags['flags']; REQ = set(flags['required']); GUIDED = set(flags['guided'])
CUR = 'app/src/main/assets/content/v2-curriculum.json'

def wcsv(name, rows, fields):
    with open(os.path.join(OUT, name), 'w', newline='', encoding='utf-8') as f:
        w = csv.DictWriter(f, fieldnames=fields, lineterminator='\n'); w.writeheader()
        for r in rows: w.writerow({k: r.get(k, '') for k in fields})

def read_mode(unit):
    return ('READ_FULL_RENDER (work/units/unit%02d.txt, all fields)' % unit if unit <= 16 else
            'READ_COMPACT_RENDER (work/view_unit.py: why/distractorDifference/contrast lines omitted, long remediation bodies truncated; remediation coverage tested mechanically)')

DEFECT = {'CC003', 'CC004', 'CC010', 'CC011', 'CC015', 'CC021', 'CC022', 'CC024', 'CC026', 'CC027', 'CC028', 'CC030', 'CC031', 'CC009'}
DESC = {'CC003': 'partner recall instead of contextual aspect', 'CC004': 'not a purpose check', 'CC009': 'source qualification debt',
        'CC010': 'original same-task duplicate group', 'CC011': 'source-layout recall', 'CC015': 'answer stated in teaching; pattern not taught',
        'CC021': 'remediation lacks cue and key', 'CC022': 'teaching scripts the key', 'CC024': 'keyed mistranslation (speak)',
        'CC026': 'label/meta/English-policy recall', 'CC027': 'non-diagnostic stimulus/options', 'CC028': 'absurd distractors',
        'CC029': 'governance jargon in learner text', 'CC030': 'additional duplicate task', 'CC031': 'genitive before Unit 9',
        'CC035': 'self-authored content-audit source', 'CC036': 'two-option required check'}
obj_unit = {}
for u in d['units']:
    for o in u['objectives']: obj_unit[o['id']] = (u['number'], o)

rows = []
for i in d['items']:
    unit, o = obj_unit[i['objectiveId']]
    fl = F.get(i['id'], [])
    role = 'REQUIRED_CHECK' if i['id'] in REQ else ('GUIDED' if i['id'] in GUIDED else 'OPTIONAL_OR_LAB')
    verdict = 'DEFECT' if set(fl) & DEFECT else ('CONCERN' if fl else 'NO_ITEM_LEVEL_DEFECT_RECORDED')
    cue_ok = 'NO' if 'CC027' in fl else 'YES_NOT_CONTRADICTED'
    uniq = 'NO (two defensible answers)' if i['id'] == 'u17-i-calendar-date-1' else 'YES'
    dist = 'NO' if ({'CC028', 'CC027'} & set(fl)) else ('WEAK_TWO_OPTION' if len(i['options']) == 2 else 'YES')
    fb = ('JARGON' if 'CC029' in fl else 'OK') + '; wrong-answer view shows same feedback, rationale not rendered (CC034)'
    teach = ';'.join(x for x in ['SCRIPTED_KEY' if ({'CC022', 'CC015'} & set(fl)) else '', 'REMEDIATION_GAP' if 'CC021' in fl else ''] if x) or 'PRESENT'
    rows.append(dict(item_id=i['id'], unit=unit, objective_id=i['objectiveId'],
                     tested_feature=(i.get('assessment') or {}).get('testedFeature', i['kind']), role=role, options=len(i['options']),
                     inspected=read_mode(unit), verdict=verdict, cue_sufficient=cue_ok, answer_unique=uniq,
                     distractors_valid=dist, feedback_valid=fb, teaching_support=teach,
                     source_binding=i['sourceId'] + ('; self-authored research note (CC035)' if 'CC035' in fl else ''),
                     reason='; '.join(DESC[x] for x in fl if x in DESC) or 'No item-level defect recorded by this recheck (not an approval)',
                     finding_ids=';'.join(fl)))
wcsv('ITEM_ASSESSMENT_AUDIT.csv', rows, ['item_id', 'unit', 'objective_id', 'tested_feature', 'role', 'options', 'inspected', 'verdict',
     'cue_sufficient', 'answer_unique', 'distractors_valid', 'feedback_valid', 'teaching_support', 'source_binding', 'reason', 'finding_ids'])
item_verdicts = collections.Counter(r['verdict'] for r in rows)

# ---------------- UNIT_COVERAGE.csv ----------------
GAPS = {
 1: ('No sound instruction/audio; 8 letters (Ж З Л Ф Ц Ч Ш Щ) never presented in steps/checks; 13 glyph-matching gating checks; letter names lack provenance (src-unicode-cyrillic); learner text exposes PDF index locators.', 'CC002;CC027;CC035;CC029'),
 2: ('Unit 1 review only; no handwriting, stress, softness or devoicing content; book-delegated.', 'CC002;CC027'),
 3: ('Gender taught as per-word labels; learner told not to infer gender from endings; checks retest taught pairs; governance jargon.', 'CC022;CC029'),
 4: ('Present-form checks answered verbatim in teaching (CC015); no type-2 pattern; raw italics markup; nominative-role constant key; stress = visual counting.', 'CC015;CC022;CC023;CC027'),
 5: ('Prepositional case not taught as grammar (phrase recall); remediation covers no checked item for questions/prepositional; cue-identical distractors.', 'CC021;CC022;CC027'),
 6: ('Raw wiki fragments as teaching/remediation; obj-u6-third untutored (meanings only in feedback); cue-identical distractors; past-tense chunks before Unit 11.', 'CC021;CC022;CC023;CC027;CC036'),
 7: ('CC011 persists; genitive check u7-i-endings-1 before Unit 9; manner checks key English/meta; э́ти glossed this; scripted keys.', 'CC011;CC022;CC025;CC026;CC027;CC031'),
 8: ('CC012 corrected; CC010 group persists; plural rule withheld; spelling objective tests form not spelling; irregular remediation = raw fragment; Ex 8/3 locator omitted.', 'CC010;CC021;CC022;CC023;CC027;CC033'),
 9: ('No genitive formation taught; number remediation omits every checked value; quantity checks reuse taught examples; u9 pattern/quantity duplicate.', 'CC021;CC022;CC030;CC032'),
 10: ('CC010 three groups persist; preposition objective tests no preposition; genitive pronoun glosses; "genetive"; raw-markup remediation; Ex 10/1 omitted.', 'CC010;CC021;CC023;CC025;CC032;CC033'),
 11: ('No transfer to unseen verbs; irregular past and reflexive function not taught; raw markup; reflexive remediation lacks checked verb.', 'CC021;CC023;CC032'),
 12: ('Unlabelled dative/prep-plural form cards; raw markup and wiki links as entire remediation; сказать glossed speak (keyed); spelling rules 3-4 not taught.', 'CC021;CC022;CC023;CC024;CC025;CC032'),
 13: ('Infinitive-aspect checks duplicate Unit 12; authoring notes leak; example notes not translations; label-matching check.', 'CC022;CC026;CC029;CC030'),
 14: ('Future-aspect teaching = tautological fragment (entire remediation); keyword-match checks; cross-unit duplicates; state words not taught.', 'CC023;CC027;CC030;CC032'),
 15: ('CC003 persists; triplicate checks across Units 12/13/15; imperative formation not taught; §15.8 locator omitted.', 'CC003;CC022;CC023;CC030;CC032;CC033'),
 16: ('CC005 persists; all checks scripted; decoy cue u16-i-inst-sg-2; complements test English meanings.', 'CC005;CC022;CC027'),
 17: ('Decoy cues; noun-form objectives answerable from numerals; u17-i-calendar-date-1 ambiguous; label-not-gloss notes.', 'CC026;CC027;CC028;CC029'),
 18: ('Thin unit (4 checks); comparative formation/чем/са́мый not taught; scripted keys.', 'CC022;CC032'),
 19: ('если бы conditional not taught; obligation checks are English label matching; prefix meanings not taught.', 'CC022;CC026;CC032'),
 20: ('CC007 persists; checks recall taught classifications; remediation covers 2 of 8 checks.', 'CC007;CC021;CC022;CC035'),
 21: ('CC004 persists; свой vs его not taught; svoy checks are lexical matching.', 'CC004;CC029;CC032'),
 22: ('CC006 persists; noun-form/compound checks answerable from numerals; raw table markup in feedback.', 'CC006;CC027;CC029;CC032'),
 23: ('CC010 three groups persist; all checks 2-option meaning recall; example notes are labels.', 'CC010;CC029;CC036'),
 24: ('CC010 concord group persists; placement near-duplicates; label recall ("not called predicative"); decoy cue; EXTRA Ex 24/3 omitted.', 'CC010;CC026;CC027;CC029;CC030;CC033'),
 25: ('Diminutives tested in English only; duplicate objectives; app-policy key; Russian Wikipedia labels; §25.4 omitted; common-noun diminutives absent.', 'CC026;CC029;CC030;CC032;CC033'),
 26: ('11 postal checks key English/policy statements with absurd distractors; academic labels; status text contradiction; address-order divergence from book (reviewer item).', 'CC026;CC028;CC029;CC032;CC037'),
 27: ('Comprehension absurd distractors; app-policy stress key; PPP formation not taught; variance activated before final audit.', 'CC026;CC028;CC032;CC048'),
 28: ('CC009 persists; non-diagnostic option families; formation not taught; Ex 28/4 omitted.', 'CC009;CC022;CC028;CC033'),
 29: ('Register checks key English app-scope statements; punctuation cues show commas; absurd reading distractors; formation not taught; status text; variance activated before final audit.', 'CC026;CC027;CC028;CC032;CC037;CC048'),
 30: ('Particles §30.4 not taught; letter names only А/Б/В (duplicate of Unit 1); address marks duplicate Unit 26; label recall; wording discloses keys; status text; variance activated before final audit.', 'CC026;CC027;CC029;CC030;CC032;CC037;CC048'),
}
rows = []
for u in d['units']:
    n = u['number']
    steps = [st for s in u['sessions'] for st in s['steps']]
    items = [i for i in d['items'] if obj_unit[i['objectiveId']][0] == n]
    exs = sorted({e for o in u['objectives'] for e in o.get('exampleIds', [])} | {e for st in steps for e in (st.get('exampleIds') or [])})
    gaps, fids = GAPS[n]
    rm = read_mode(n)
    rows.append(dict(unit=n, objectives_checked='%d (%s)' % (len(u['objectives']), rm), instruction_checked='%d TEACH/ORIENT/BOOK steps read' % sum(1 for st in steps if st['kind'] in ('TEACH', 'ORIENT', 'BOOK')),
                     examples_checked='%d referenced examples read' % len(exs), guided_checked='%d guided items read' % sum(1 for i in items if i['id'] in GUIDED),
                     independent_checked='%d required checks read' % sum(1 for i in items if i['id'] in REQ),
                     remediation_checked='%d REMEDIATE steps read%s; coverage tested mechanically' % (sum(1 for st in steps if st['kind'] == 'REMEDIATE'), '' if n <= 16 else ' (bodies truncated in compact view)'),
                     prerequisites_checked='prereqs %s read; advancement %s' % (u.get('prerequisiteUnitNumbers'), json.dumps(u.get('advancement'))[:160]),
                     mapping_checked='Book Lesson %d read in full in the owned PDF; mapped section/exercise locators verified' % n,
                     rights_checked='sources/rights records read; per-item sourceId recorded; external span fidelity not verifiable (no snapshots; Wikipedia unreachable)',
                     application_behavior_checked='static only (no Gradle/Robolectric/device)', verdict='DEFECTS_FOUND_NOT_ACCEPTED', gaps=gaps, finding_ids=fids))
wcsv('UNIT_COVERAGE.csv', rows, ['unit', 'objectives_checked', 'instruction_checked', 'examples_checked', 'guided_checked', 'independent_checked',
     'remediation_checked', 'prerequisites_checked', 'mapping_checked', 'rights_checked', 'application_behavior_checked', 'verdict', 'gaps', 'finding_ids'])

# ---------------- CURRICULUM_ENTITY_COVERAGE.csv ----------------
rows = []
def add(t, eid, ptr, unit, status, sem, src, ev, lim, fids=''):
    rows.append(dict(entity_type=t, entity_id=eid, json_pointer=ptr, unit=unit, inspection_status=status, semantic_verdict=sem,
                     source_binding_verdict=src, evidence=ev, limitations=lim, finding_ids=fids))
for ui, u in enumerate(d['units']):
    n = u['number']
    add('unit', 'unit-%d' % n, '/units/%d' % ui, n, read_mode(n), 'DEFECTS_FOUND', 'mapping PENDING_CURRICULUM_REVIEWER', 'UNIT_COVERAGE.csv row', 'static', GAPS[n][1])
    for oi, o in enumerate(u['objectives']):
        add('objective', o['id'], '/units/%d/objectives/%d' % (ui, oi), n, read_mode(n), 'SEE_UNIT_AND_ITEMS', '', 'kind=%s gating=%s' % (o['kind'], o.get('gating', True)), '', '')
    for si, s in enumerate(u['sessions']):
        add('session', s['id'], '/units/%d/sessions/%d' % (ui, si), n, read_mode(n), 'READ', '', '', '', '')
        for ti, st in enumerate(s['steps']):
            add('step', st['id'], '/units/%d/sessions/%d/steps/%d' % (ui, si, ti), n, read_mode(n), 'READ', '', 'kind=' + st['kind'], 'remediation bodies truncated in compact view' if (n > 16 and st['kind'] == 'REMEDIATE') else '', '')
for ii, i in enumerate(d['items']):
    n = obj_unit[i['objectiveId']][0]
    fl = F.get(i['id'], [])
    add('item', i['id'], '/items/%d' % ii, n, read_mode(n), 'DEFECT' if set(fl) & DEFECT else ('CONCERN' if fl else 'NO_ITEM_LEVEL_DEFECT_RECORDED'), i['sourceId'], 'ITEM_ASSESSMENT_AUDIT.csv', '', ';'.join(fl))
ex_unit = {}
for u in d['units']:
    for o in u['objectives']:
        for e in o.get('exampleIds', []): ex_unit[e] = u['number']
    for s in u['sessions']:
        for st in s['steps']:
            for e in (st.get('exampleIds') or []): ex_unit.setdefault(e, u['number'])
for ei, e in enumerate(d['examples']):
    n = ex_unit.get(e['id'], '')
    sem = 'ORPHAN_NOT_IN_ANY_STEP_OR_OBJECTIVE' if e['id'] not in ex_unit else 'READ'
    fl = 'CC025' if e['id'] in ('ex-or-u7-demonstrative-3', 'ex-or-u8-agreement-7', 'ex-or-u10-pronoun-13', 'ex-or-u10-pronoun-14') else ''
    add('example', e['id'], '/examples/%d' % ei, n, read_mode(n) if n else 'READ_VIA_MECHANICAL_LISTING', sem, e.get('sourceId', ''), '', '', fl)
for k in ['sources', 'rights', 'references', 'letters', 'unitReviews', 'ownedEditionMappings']:
    for xi, x in enumerate(d[k]):
        st = 'READ_FULL' if k != 'ownedEditionMappings' else 'READ_FULL; locators verified against owned PDF'
        fl = {'references': 'CC017;CC045' if x['id'].startswith('ref-ca-') and x['id'] != 'ref-ca-letter-names' else ('CC035' if x['id'] == 'ref-ca-letter-names' else 'CC017'),
              'letters': 'CC038', 'ownedEditionMappings': 'CC033'}.get(k, '')
        add(k[:-1] if k.endswith('s') else k, x['id'], '/%s/%d' % (k, xi), x.get('unitNumber', x.get('introducedInUnit', '')), st,
            'PENDING_REVIEW' if k in ('unitReviews', 'ownedEditionMappings') else 'READ', '', '', '', fl)
for ci, c in enumerate(d['sourceCapabilities']):
    add('sourceCapability', c['id'], '/sourceCapabilities/%d' % ci, c['unitNumber'], 'MECHANICAL_ALL (hash, binding, pool membership) + TARGETED_READ',
        'SELF_HASH_OK', c['sourceId'], 'safe_evidence/caps_out.txt', 'third-party fidelity unverifiable', 'CC035' if c['sourceId'] == 'src-content-audit-research' else '')
for pi, p in enumerate(d['provenanceRecords']):
    add('provenanceRecord', p['itemId'], '/provenanceRecords/%d' % pi, p.get('unitNumber', ''), 'MECHANICAL_ONLY (sourceHash/contentHash recomputed)', 'NOT_SEMANTICALLY_READ', p.get('sourceId', ''), 'safe_evidence/mech_curriculum_out.json', 'per-record semantics not reviewed', '')
for ai, a in enumerate(d['authoredEnglishRecords']):
    add('authoredEnglishRecord', a['id'], '/authoredEnglishRecords/%d' % ai, a['unitNumber'], 'MECHANICAL_ONLY (shipped-text match test)', 'NOT_SEMANTICALLY_READ', '', '', '538 of 1488 long records match no shipped text', '')
for ki, (k, v) in enumerate(d['sourceTextPool'].items()):
    add('sourceTextPoolEntry', k, '/sourceTextPool/' + k, '', 'MECHANICAL_ONLY (sha256 key == content)', 'NOT_SEMANTICALLY_READ', '', '', '', '')
add('linguisticReview', d['linguisticReview']['id'], '/linguisticReview', '', 'READ_FULL', 'PENDING', '', '', '', '')
add('admissions', 'admissions', '/admissions', '', 'READ_FULL', 'ALL_ZERO', '', '', '', '')
wcsv('CURRICULUM_ENTITY_COVERAGE.csv', rows, ['entity_type', 'entity_id', 'json_pointer', 'unit', 'inspection_status', 'semantic_verdict',
     'source_binding_verdict', 'evidence', 'limitations', 'finding_ids'])
entity_counts = collections.Counter(r['entity_type'] for r in rows)

# ---------------- BOOK_COVERAGE_CONTINUATION.csv ----------------
ranges = [(1, 1, 11), (2, 12, 23), (3, 24, 35), (4, 36, 45), (5, 46, 55), (6, 56, 67), (7, 68, 80), (8, 81, 91), (9, 92, 106), (10, 107, 121),
          (11, 122, 132), (12, 133, 148), (13, 149, 161), (14, 162, 170), (15, 171, 187), (16, 188, 200), (17, 201, 214), (18, 215, 229), (19, 230, 239),
          (20, 240, 256), (21, 257, 268), (22, 269, 282), (23, 283, 293), (24, 294, 304), (25, 305, 319), (26, 320, 331), (27, 332, 342), (28, 343, 354),
          (29, 355, 367), (30, 368, 377)]
omitted = {8: 'Ex 8/3 (p89) omitted from mapping', 10: 'Ex 10/1 (p109) omitted', 15: '§15.8 (p177) omitted', 24: 'EXTRA Ex 24/3 (p298) omitted',
           25: '§25.4 (p310) omitted', 28: 'Ex 28/4 (p353) omitted', 30: 'No numbered exercise exists; empty exercise list is accurate'}
rows = []
for n, a, b in ranges:
    rows.append(dict(lesson=n, section_or_page_locators='printed pp%d-%d (PDF %d-%d)' % (a, b, a + 12, b + 12),
                     actual_material_inspected='Entire lesson (all sections, exercises, vocabulary, texts) read visually from the owned PDF',
                     inspection_method='Visual page reading of image-only PDF (sha256 2bcc0c45..., 13596737 bytes) outside the checkout; neutral locators only recorded',
                     alignment_conclusion=('All mapped section/exercise locators verified exact. ' + omitted.get(n, '')).strip() + ' Scope gaps: see UNIT_COVERAGE.csv/CC032.',
                     unread_material='none within the lesson range',
                     remaining_qualification='Owner mapping acceptance (Amendment 1), qualified review, formal ISBN registry verification'))
rows.append(dict(lesson='front-matter', section_or_page_locators='PDF 1-12', actual_material_inspected='Not re-read in this session',
                 inspection_method='n/a', alignment_conclusion='Repository records: introduction signed SSEES London 1996; PDF 2 and 4 blank (not re-verified)',
                 unread_material='PDF 1-12', remaining_qualification='copyright-page year unresolved'))
for name, rng, what, unread in [
    ('grammatical-tables', 'pp378-386 (PDF 390-398)', 'Read in full', 'none'),
    ('spelling-rules', 'p387 (PDF 399)', 'Read in full (four rules; rule 2 confirms хорошее)', 'none'),
    ('russian-english-vocabulary', 'pp388-427 (PDF 400-439)', 'Sampled: PDF 400-401, 429-430, 432-433, 439 (сказать glossed say/tell, p420)', 'PDF 402-428, 431, 434-438'),
    ('english-russian-vocabulary', 'pp428-452 (PDF 440-464)', 'Sampled: PDF 440, 464', 'PDF 441-463'),
    ('key-to-exercises', 'pp453-502 (PDF 465-514)', 'Sampled: PDF 465 (1/1-2/1), 513-514 (29/6, 30.6)', 'PDF 466-512'),
    ('glossary', 'pp503-507 (PDF 515-519)', 'Read in full', 'none'),
    ('index', 'pp508-514 (PDF 520-526)', 'Read in full (topic-section cross-checks for CC005/CC006/CC007/CC032)', 'none'),
    ('back-cover', 'PDF 527-528', 'Read; ISBN 0-14-012041-6 / 9780140120417 check digits valid', 'none')]:
    rows.append(dict(lesson=name, section_or_page_locators=rng, actual_material_inspected=what, inspection_method='Visual page reading',
                     alignment_conclusion='Back matter consulted for verification only; no protected text reproduced', unread_material=unread,
                     remaining_qualification='Registry ISBN verification; owner acceptance'))
wcsv('BOOK_COVERAGE_CONTINUATION.csv', rows, ['lesson', 'section_or_page_locators', 'actual_material_inspected', 'inspection_method',
     'alignment_conclusion', 'unread_material', 'remaining_qualification'])
json.dump({'itemVerdicts': item_verdicts, 'entityCounts': entity_counts}, open(os.path.join(S, 'work', 'part2_stats.json'), 'w'), indent=1)
print('part2 ok', dict(item_verdicts), sum(entity_counts.values()))
