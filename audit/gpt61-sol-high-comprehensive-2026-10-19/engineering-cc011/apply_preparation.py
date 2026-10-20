import copy, hashlib, json, subprocess, sys
from pathlib import Path
sys.dont_write_bytecode = True
sys.path.insert(0, str(Path.cwd()))
from catalog.curriculum_storage import loads, dumps

repo = Path.cwd()
out = Path('/workspace/artifacts/cc011-invariance')
asset = repo / 'app/src/main/assets/content/v2-curriculum.json'
before_raw = asset.read_bytes()
old = loads(before_raw)
assert len(old['provenanceRecords']) == 7032
pack = copy.deepcopy(old)
unit = next(u for u in pack['units'] if u['number'] == 7)
objective = next(o for o in unit['objectives'] if o['id'] == 'obj-u7-adverb-invariance')
session = next(s for s in unit['sessions'] if s['id'] == 'u7-s-adverb-invariance')
steps = {s['id']: s for s in session['steps']}
english = dict(
    id='u7-st-teach-adverb-invariance-english', kind='TEACH',
    title='Understand the rule before the wording task',
    body=('Adverbs do not decline or conjugate: they do not change for case, gender or number agreement. '
          'This grammatical invariance does not mean that comparative forms cannot exist. '
          'The retained explanation treats comparison of qualitative adverbs separately. '
          'The choices below ask where wording occurs in a Russian source line, using the dash and colon. '
          'That wording task does not demonstrate applying invariance in a sentence. '
          'Use this explanation to understand the rule; do not infer a general adverb-formation rule from these choices.'),
    referenceIds=['ref-u7-ruwiki-attribution'],
)
teaching = [steps['u7-st-teach-adverb-invariance-1'], steps['u7-st-teach-adverb-invariance-2'], english]
repair = steps['u7-st-rem-adverb-invariance']
repair.update(title='Review the rule and the wording task', body='\n\n'.join(s['body'] for s in teaching))
session['steps'] = teaching + [steps['u7-st-guided-adverb-invariance-1'], repair, steps['u7-st-check-adverb-invariance']]
objective['instructionStepIds'].append(english['id'])
bibliography = ('Complete retained Russian Wikipedia Наречие oldid152437423, Морфологические признаки, '
                'first paragraph: invariance, no declension/conjugation or gender/number change; '
                'comparison of qualitative adverbs is discussed separately. '
                'Independently authored English scaffolding and explanation arrangement2026-10-20. '
                'Existing native lines/examples retain their original provenance. No new native application, '
                'comparative example, private-book wording or scored item is added. '
                'CC011 contextual grammar assessment remains OPEN; no qualification or approval.')
template = next(r for r in pack['provenanceRecords'] if r['sourceId'] == 'src-content-audit-research' and r.get('synthesis'))
sha = lambda s: hashlib.sha256(s.encode()).hexdigest()
added = []
for step in [english, repair]:
    for field in ['title', 'body']:
        text = step[field]; binding = step['id'] + '-' + field
        record = copy.deepcopy(template); original = bibliography + '\n' + text
        record.update(itemId='prov-cc011-preparation-' + binding, unitNumber=7,
            objectiveId=objective['id'], recordType='label',
            sourceRecordId='ca-coverage:' + objective['id'] + ':cc011-preparation:' + binding,
            originalSourceText=original, shippedText=text, sourceHash=sha(original), contentHash=sha(text))
        record['synthesis'] = dict(bibliography=bibliography, sourceDateOrVersion='2026-10-08',
            authorshipDeclaration='Independent English scaffolding and arrangement2026-10-20. Existing native lines retain their separate evidence; no new native application.',
            objectiveOrClaimBinding=objective['id'] + ':cc011-preparation:' + binding, applications=[])
        for key in ['reviewerIdentity','reviewerQualification','reviewDate','reviewedDigest']: record[key] = ''
        added.append(record)
pack['provenanceRecords'] += added
assert len(added) == 4
assert pack['items'] == old['items'] and pack['examples'] == old['examples']
assert pack['provenanceRecords'][:-4] == old['provenanceRecords']
inverse = copy.deepcopy(pack); inverse['units'] = old['units']; inverse['provenanceRecords'] = old['provenanceRecords']
assert inverse == old
assert [u for u in pack['units'] if u['number'] != 7] == [u for u in old['units'] if u['number'] != 7]
asset.write_text(dumps(pack))
(out/'PREPARATION_CORRECTION.json').write_text(json.dumps(dict(
    baselineCommit=subprocess.check_output(['git','rev-parse','HEAD'], text=True).strip(),
    beforeAssetSha256=hashlib.sha256(before_raw).hexdigest(), afterAssetSha256=hashlib.sha256(asset.read_bytes()).hexdigest(),
    beforeUnit=next(u for u in old['units'] if u['number'] == 7), afterUnit=unit,
    addedProvenance=added, all626Items821Examples7032RecordsExact=True,
    coreFinding='CC011_OPEN_NO_INDEPENDENT_INVARIANCE_APPLICATION_ADDED', formalAcceptance=False), ensure_ascii=False, indent=2) + '\n')
print('3 cards before practice; whole repair; 4 pending records; CC011 core application OPEN')
