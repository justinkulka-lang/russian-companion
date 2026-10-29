"""Pinned public-source field checks for the Unit13 proposal; no private PDF or network."""
from pathlib import Path
import csv
import hashlib
import io
import json
import subprocess
import sys

BASE = '5971ed56cb4e17b15e4430202d206ec8df59337a'
D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())
blob = lambda name: subprocess.check_output(['git', 'show', BASE + ':' + name], cwd=R)
sha = lambda data: hashlib.sha256(data).hexdigest()
enc = lambda x: (json.dumps(x, ensure_ascii=False, indent=2) + '\n').encode()
p = json.loads(blob('app/src/main/assets/content/v2-curriculum.json'))
items = {x['id']: x for x in p['items']}
records = {x['itemId']: x for x in p['provenanceRecords']}
examples = {x['id']: x for x in p['examples']}
resolve = lambda record: p['sourceTextPool'][record['originalSourceTextRef']] if 'originalSourceTextRef' in record else record['originalSourceText']
accent = lambda s: s.replace("'", '\u0301')
freeze_path = 'catalog/sources/units-3-5/FREEZE.json'
freeze = json.loads(blob(freeze_path))['openrussian']
assert freeze['commit'] == '50e210c4803237779cb562bc1abcea529066031c'
verbs_path = 'catalog/sources/units-3-5/' + freeze['files']['verbs.csv']['path']
verbs = blob(verbs_path)
assert sha(verbs) == freeze['files']['verbs.csv']['sha256']
assert subprocess.check_output(['git', 'hash-object', '--stdin'], input=verbs, cwd=R).decode().strip() == freeze['files']['verbs.csv']['gitBlob']
table = list(csv.DictReader(io.StringIO(verbs.decode()), delimiter='\t'))
license_path = 'catalog/sources/units-3-5/' + freeze['licenseFile']
assert sha(blob(license_path)) == freeze['licenseSha256']

def verify_field(record, row, field):
    assert record['sourceRecordId'] == 'verbs.csv:' + row['accented'] + '#' + field
    assert resolve(record) == row[field]
    assert sha(resolve(record).encode()) == record['sourceHash']
    assert accent(row[field]) == record['shippedText']
    assert sha(record['shippedText'].encode()) == record['contentHash']
    return dict(provenanceId=record['itemId'], sourceRecordId=record['sourceRecordId'],
                sourceHash=record['sourceHash'], contentHash=record['contentHash'], field=field)

present = []
pronoun_fields = {'мы': 'presfut_pl1', 'он': 'presfut_sg3', 'вы': 'presfut_pl2', 'они': 'presfut_pl3'}
for lemma, pronoun in [('говорить', 'мы'), ('видеть', 'он'), ('писать', 'вы'), ('идти', 'они')]:
    item = items['u4-i-verb-' + lemma + '-' + pronoun]
    row, = [r for r in table if accent(r['accented']) == item['promptLetter']]
    assert row['bare'] == lemma
    if lemma == 'писать':
        assert row['accented'] == "писа'ть" and row['presfut_pl2'] == "пи'шете"
        assert len([r for r in table if r['bare'] == lemma]) == 2
    prompt = verify_field(records['prov-u4-verb-' + lemma], row, 'accented')
    answer = verify_field(records['prov-u4-form-' + lemma + '-' + pronoun], row, pronoun_fields[pronoun])
    assert accent(row[pronoun_fields[pronoun]]) == item['answer']
    options = []
    for value in item['options']:
        candidates = [r for r in records.values() if r.get('objectiveId') == 'obj-u4-present'
                      and r.get('sourceRecordId', '').startswith('verbs.csv:' + row['accented'] + '#presfut_')
                      and r['shippedText'] == value]
        assert candidates
        record = candidates[0]
        options.append(verify_field(record, row, record['sourceRecordId'].split('#')[-1]))
    present.append(dict(itemId=item['id'], exactAccentedLemma=row['accented'],
                        promptBinding=prompt, answerBinding=answer, optionBindings=options,
                        rawKeyIndex=item['options'].index(item['answer'])))
past = []
row, = [r for r in table if r['accented'] == "прочита'ть"]
for rid, field in [('prov-ex-or-u11-past-aspect-2', 'past_m'),
                   ('prov-ex-or-u11-past-aspect-3', 'past_f'),
                   ('prov-ex-or-u11-past-aspect-4', 'past_n')]:
    past.append(verify_field(records[rid], row, field))
for iid in ['u11-i-past-aspect-1', 'u11-i-past-aspect-2']:
    item = items[iid]
    assert [records[key]['shippedText'] for key in item['assessment']['relationParticipantIds']] == [item['promptLetter'], *item['options']]
    assert records[item['assessment']['answerSourceIds'][0]]['shippedText'] == item['answer']

wiki_freeze_path = 'catalog/sources/units-11-15/FREEZE.json'
wiki_freeze = json.loads(blob(wiki_freeze_path))
page, = [x for x in wiki_freeze['wikibooks']['pages'] if x['oldid'] == '4059032']
wiki_path = 'catalog/sources/units-11-15/' + page['file']
wiki = blob(wiki_path)
assert sha(wiki) == page['sha256'] and len(wiki) == page['size']
revision_path = 'catalog/sources/units-11-15/' + page['revisionFile']
revision = json.loads(blob(revision_path))
assert revision['revid'] == 4059032 and revision['sha1'] == page['mediawikiSha1']
aspect = []
for n in range(4):
    record = records[f'prov-ex-u12-aspect-{n}-ru']
    text = resolve(record)
    assert record['sourceRevision'] == '4059032'
    assert sha(text.encode()) == record['sourceHash'] and text in wiki.decode()
    assert record['shippedText'] in text
    assert sha(record['shippedText'].encode()) == record['contentHash']
    assert examples[f'ex-u12-aspect-{n}']['display'] == record['shippedText']
    aspect.append(dict(provenanceId=record['itemId'], sourceHash=record['sourceHash'],
                       contentHash=record['contentHash'], shippedText=record['shippedText'],
                       frozenExcerptByteOffset=wiki.find(text.encode())))
for iid in ['u12-i-aspect-1', 'u12-i-aspect-2']:
    item = items[iid]
    assert item['assessment']['relationKind'] == 'SOURCE_SUPPLIED_PAIR'
    assert [records[key]['shippedText'] for key in item['assessment']['relationParticipantIds']] == [item['promptLetter'], *item['options']]
    assert records[item['assessment']['answerSourceIds'][0]]['shippedText'] == item['answer']
result = dict(verified=True, sourceCommit=BASE,
    dictionary=dict(freezePath=freeze_path, freezeSha256=sha(blob(freeze_path)), sourceCommit=freeze['commit'],
        path=verbs_path, sha256=sha(verbs), gitBlob=freeze['files']['verbs.csv']['gitBlob'],
        licensePath=license_path, licenseSha256=freeze['licenseSha256']),
    presentBindings=present, pastGenderFields=past,
    aspectSource=dict(freezePath=wiki_freeze_path, freezeSha256=sha(blob(wiki_freeze_path)),
        path=wiki_path, sha256=sha(wiki), gitBlob=subprocess.check_output(['git','rev-parse',BASE+':'+wiki_path],cwd=R).decode().strip(),
        oldid='4059032', revisionPath=revision_path, revisionSha256=sha(blob(revision_path)),
        recordedMediaWikiSha1=page['mediawikiSha1'], observedFileSha1=hashlib.sha1(wiki).hexdigest(),
        fields=aspect, limit='The frozen quote uses speak for the saying/telling pair; selection retains the existing context-limited teaching and does not assert universal aspect partners.'),
    inheritedGlossRisk=dict(exampleId='ex-u12-aspect-1',note=examples['ex-u12-aspect-1']['note'],
        disposition='The generic to speak gloss is retained historical source-derived text,not newly endorsed;current teaching narrows the pair to saying/telling. CC024/qualified semantic review remains open.'),
    scope='Frozen field/record/link verification only;not live-source,qualified linguistic,rights,mapping or owner acceptance.',
    privatePdf='NOT_READ_OR_PACKAGED', newJUnitMethodsExecuted=0)
target = D / 'SELECTED_SOURCE_BINDINGS.json'
assert sys.argv[1:] in [[], ['--check']]
if sys.argv[1:] == ['--check']:
    assert target.read_bytes() == enc(result)
else:
    assert not target.exists()
    target.write_bytes(enc(result))
print(json.dumps(dict(verified=True,presentItems=4,pastGenderFields=3,aspectSourceForms=4,
                      writingHomographDisambiguated=True,newJUnitMethodsExecuted=0)))
