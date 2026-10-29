"""Verify selected fields against existing frozen public sources; no private PDF/network."""
from pathlib import Path
import csv
import hashlib
import io
import json
import subprocess
import sys

BASE = '05407a70d1d70b196ef21b25cff51357c072c087'
D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())


def blob(name):
    return subprocess.check_output(['git', 'show', BASE + ':' + name], cwd=R)


def sha(data):
    return hashlib.sha256(data).hexdigest()


def enc(value):
    return (json.dumps(value, ensure_ascii=False, indent=2) + '\n').encode()


p = json.loads(blob('app/src/main/assets/content/v2-curriculum.json'))
items = {i['id']: i for i in p['items']}
records = {r['itemId']: r for r in p['provenanceRecords']}
freeze_path = 'catalog/sources/units-3-5/FREEZE.json'
freeze = json.loads(blob(freeze_path))['openrussian']
assert freeze['commit'] == '50e210c4803237779cb562bc1abcea529066031c'
frozen_files = {}
tables = {}
for name in ['nouns.csv', 'verbs.csv']:
    path = 'catalog/sources/units-3-5/' + freeze['files'][name]['path']
    data = blob(path)
    assert sha(data) == freeze['files'][name]['sha256']
    assert subprocess.check_output(['git', 'hash-object', '--stdin'], input=data, cwd=R).decode().strip() == freeze['files'][name]['gitBlob']
    frozen_files[path] = dict(sha256=sha(data), gitBlob=freeze['files'][name]['gitBlob'])
    tables[name] = list(csv.DictReader(io.StringIO(data.decode()), delimiter='\t'))
license_path = 'catalog/sources/units-3-5/' + freeze['licenseFile']
assert sha(blob(license_path)) == freeze['licenseSha256']
gender_rows = []
labels = {'m': 'masculine', 'f': 'feminine', 'n': 'neuter'}
for lemma in ['город', 'друг', 'дверь', 'море', 'дядя']:
    row, = [r for r in tables['nouns.csv'] if r['bare'] == lemma]
    item = items['u3-i-gender-' + lemma]
    assert row['accented'].replace("'", '\u0301') == item['promptLetter']
    if lemma != 'дядя':
        assert labels[row['gender']] == item['answer']
        binding = dict(source='Frozen dictionary gender field', field='gender', rawField=row['gender'])
    else:
        assert row['gender'] == ''
        record = records['prov-u3-dyadya-note']
        key = record.get('originalSourceTextRef')
        text = p['sourceTextPool'][key] if key else record['originalSourceText']
        assert sha(text.encode()) == record['sourceHash']
        assert "'uncle' is 'inherently' masculine" in text
        assert item['answer'] == 'masculine'
        binding = dict(source='Existing frozen Wikibooks uncle note; dictionary gender is empty',
                       provenanceId=record['itemId'], sourceRevision=record['sourceRevision'],
                       sourceHash=record['sourceHash'], dictionaryRawGender='',
                       limit='Original note misuses conjugates for nouns; existing corrected teaching says decline. No new qualification/rights approval.')
    gender_rows.append(dict(itemId=item['id'], noun=lemma, promptFieldVerified=True,
                            answer=item['answer'], binding=binding))
past_rows = []
for rid, field in [('prov-ex-or-u11-past-aspect-2', 'past_m'),
                   ('prov-ex-or-u11-past-aspect-3', 'past_f'),
                   ('prov-ex-or-u11-past-aspect-4', 'past_n')]:
    record = records[rid]
    row, = [r for r in tables['verbs.csv'] if r['bare'] == 'прочитать']
    assert row['accented'] == "прочита'ть"
    assert record['sourceRecordId'] == 'verbs.csv:' + row['accented'] + '#' + field
    assert record['originalSourceText'] == row[field]
    assert sha(row[field].encode()) == record['sourceHash']
    assert row[field].replace("'", '\u0301') == record['shippedText']
    past_rows.append(dict(provenanceId=rid, field=field, sourceHash=record['sourceHash'], shippedText=record['shippedText']))
for iid in ['u11-i-past-aspect-1', 'u11-i-past-aspect-2']:
    item = items[iid]
    participants = [records[key]['shippedText'] for key in item['assessment']['relationParticipantIds']]
    assert participants == [item['promptLetter'], *item['options']]
    assert item['answer'] == records[item['assessment']['answerSourceIds'][0]]['shippedText']
result = dict(
    verified=True, sourceCommit=BASE, frozenSourceCommit=freeze['commit'],
    freezePath=freeze_path, freezeSha256=sha(blob(freeze_path)), frozenFiles=frozen_files,
    licensePath=license_path, licenseSha256=freeze['licenseSha256'],
    genderBindings=gender_rows, pastGenderFields=past_rows,
    scope='Existing frozen field/record byte and source-link verification; not live-source, qualified linguistic, rights, mapping or owner acceptance.',
    privatePdf='NOT_READ_OR_PACKAGED', newJUnitMethodsExecuted=0,
)
target = D / 'SELECTED_SOURCE_BINDINGS.json'
assert sys.argv[1:] in [[], ['--check']]
if sys.argv[1:] == ['--check']:
    assert target.read_bytes() == enc(result)
else:
    assert not target.exists()
    target.write_bytes(enc(result))
print(json.dumps(dict(verified=True, frozenDictionaryFiles=2, genderBindings=5,
                      pastGenderFields=3, newJUnitMethodsExecuted=0)))
