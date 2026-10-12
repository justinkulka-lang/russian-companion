#!/usr/bin/env python3
"""Complete bounded Unit 27 reading and optional present-passive instruction."""
import argparse
from copy import deepcopy
import hashlib
import json
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'catalog'))
from curriculum_storage import loads, dumps

PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
PREFIX = 'prov-ca-u27-remaining-'
BIBLIOGRAPHY = (
    'Retained licensed Russian grammar revision 1369617627 passive-present section, '
    'retained Wiktionary present-passive stress records including revision 74231596, '
    'and the existing hash-bound obj-u27-comprehension research corpus and applications. '
    'Independent English explanation of the retained forms and readings, 2026-10-12. '
    'Fresh Cornell passive-page retrieval returned HTTP 403; no fresh primary retrieval is claimed. '
    'No new Russian, copied book prose, source asset or general productive rule is supplied. '
    'Implementation remains pending final curriculum audit and owner acceptance.'
)
LESSONS = {
    'obj-u27-comprehension': (
        'Read the described letter, writer and separate main action',
        [
            ('Separate the letter description from the sentence action',
             'Письмо, написанное Анной, лежит на столе. means the letter Anna wrote is on the table. '
             'The participial phrase написанное Анной describes the letter: written is the action affecting it, '
             'and Anna is the named writer. The main statement locates the letter on the table. '
             'Being written and being on the table are separate parts of the reading. '
             'Neither the table nor the letter is the writer. The sentence does not say that Anna put the letter away, '
             'and the writer is not identified as its reader. A passive participle describes the affected entity; '
             'an agent phrase can name the person responsible for that particular action.'),
            ('Do not transfer the main-action subject to an unnamed participle agent',
             'Анна убрала прочитанное письмо. means Anna put away the letter that had been read. '
             'Anna performs the putting-away action; the description прочитанное письмо says that the letter had already been read. '
             'The sentence does not name the reader. Anna might or might not have read it; neither reading can be asserted from this sentence alone. '
             'Already read is different from just written or still reading aloud. '
             'The supplied passive description expresses the completed reading of the letter, not an ongoing action by Anna. '
             'Recognize the main verb, the affected letter and any explicitly named agent separately. '
             'These checks interpret the supplied readings; accent placement alone cannot answer who wrote or read the letter, '
             'and recognizing these examples does not demonstrate production of an unfamiliar participial sentence.')
        ]
    ),
    'obj-u27-present-passive': (
        'Optional: distinguish a present passive form from its supporting verb forms',
        [
            ('Meaning differs between a passive description, a finite verb and an infinitive',
             'The supplied обсужда́емый has the meaning being discussed. It describes something affected by discussion, '
             'rather than naming the person doing the discussing. The source also permits an able-to-be-discussed reading; '
             'the checked English option here is being discussed. The supporting verb form оставля́ем means we leave: '
             'it is a first-person plural present-tense form, with we performing the action, not a passive description. '
             'The infinitive оставля́ть means to leave and does not by itself name a we subject. '
             'Choose we leave for the finite form and to leave for the infinitive. '
             'The option to discuss is an infinitive meaning rather than the checked passive description. '
             'Different action meanings and grammatical functions must not be exchanged simply because forms share a stem.'),
            ('Recognize only the retained formation and accent placements',
             'The retained source illustrates the relation between the infinitive оставля́ть, the finite form оставля́ем '
             'and the present passive оставля́емый with its adjective ending. The finite we leave form is a supporting example, '
             'not itself a participle. This small set does not establish which other verbs permit a present passive '
             'or a complete formation algorithm for every verb. In the supplied leave contrast, оставля́емый is retained '
             'and оставляемы́й is rejected. In the supplied discuss contrast, обсужда́емый is retained '
             'and обсуждаемы́й is rejected. The retained accents are on the third syllable; the alternatives move them to the final syllable. '
             'Compare the accented vowel in each particular form; do not predict an unlisted form or its stress from this set. '
             'This entire session is optional and non-gating. Its present-passive stress choices belong to the optional objective, '
             'not to the required past-passive stress gate. Active-participle instruction belongs to Unit 29, not this session.')
        ]
    )
}


def digest(text):
    return hashlib.sha256(text.encode()).hexdigest()


def repair(data):
    if any(r['itemId'].startswith(PREFIX) for r in data['provenanceRecords']):
        return data, []
    unit = next(u for u in data['units'] if u['number'] == 27)
    template = next(r for r in data['provenanceRecords'] if r['itemId'] == 'prov-ca-17-72')
    added = []
    seen = {}

    def record(text, objective, label):
        key = (objective, text)
        if key in seen:
            return
        original = BIBLIOGRAPHY + '\n' + text
        row = deepcopy(template)
        row.update(itemId=PREFIX + label, unitNumber=27, objectiveId=objective,
                   sourceRecordId='ca-coverage:' + objective + ':' + label,
                   originalSourceText=original, shippedText=text,
                   sourceHash=digest(original), contentHash=digest(text),
                   attribution='Russian Companion independently authored English instruction, 2026-10-12; retained examples; no book prose.')
        row['synthesis'].update(bibliography=BIBLIOGRAPHY,
                                authorshipDeclaration='Independent English instruction, 2026-10-12. No new Russian or copied book expression.',
                                objectiveOrClaimBinding=objective + ':' + label)
        row['synthesis'].pop('applications', None)
        data['provenanceRecords'].append(row)
        added.append(row['itemId'])
        seen[key] = row['itemId']

    for objective, (title, cards) in LESSONS.items():
        session = next(s for s in unit['sessions'] if s['objectiveIds'] == [objective])
        session['title'] = title
        next(o for o in unit['objectives'] if o['id'] == objective)['title'] = title
        teaching = [s for s in session['steps'] if s['kind'] == 'TEACH']
        assert len(teaching) == len(cards)
        for step, (heading, body) in zip(teaching, cards):
            step.update(title=heading, body=body)
        session['steps'] = teaching + [s for s in session['steps'] if s['kind'] != 'TEACH']
        remediation = next(s for s in session['steps'] if s['kind'] == 'REMEDIATE')
        remediation.update(title='Review the complete supplied-form reading',
                           body='\n\n'.join(body for _, body in cards),
                           exampleIds=list(dict.fromkeys(e for s in teaching for e in s.get('exampleIds', []))))
        for step in session['steps']:
            if step['kind'] in ('GUIDED', 'CHECK'):
                step['body'] = ('Use the explained meaning and the supplied form or sentence. '
                                'A missed choice returns to both teaching cards and every worked example. '
                                'Optional present-passive study does not gate Unit 27.')
        texts = [title] + [text for s in session['steps'] for text in (s['title'], s['body'])]
        for n, text in enumerate(dict.fromkeys(texts)):
            record(text, objective, objective + '-lesson-' + str(n))

    meanings = {'being discussed': 'a passive description of something affected by discussion',
                'we leave': 'a finite first-person plural action with a we subject',
                'to leave': 'an infinitive meaning without a stated we subject',
                'to discuss': 'an infinitive meaning rather than a passive description'}
    explanations = {
        'u27-g-comp-1': 'The letter is on the table; Anna is the writer named in its passive description. Putting it away is a different action, and the table is not a writer.',
        'u27-i-comp-1': 'The description identifies Anna as the writer, not as the reader. The letter is the affected entity, not an agent acting on Anna.',
        'u27-i-comp-2': 'Anna performs the putting-away action. The passive description leaves the reader unspecified; naming the main-action subject does not identify that reader.',
        'u27-i-comp-3': 'Already read describes the completed reading of the letter. Just written substitutes a different action, and still reading aloud invents an ongoing action and its agent.'
    }
    changed = []
    for item in data['items']:
        objective = item['objectiveId']
        if objective not in LESSONS:
            continue
        if item['id'] in explanations:
            extra = explanations[item['id']]
        elif item['assessment']['testedFeature'] == 'PRESENT_PASSIVE':
            item['prompt'] = 'Which English meaning fits this supplied form?'
            extra = item['answer'] + ' identifies ' + meanings[item['answer']] + '.'
            wrong = ['' if option == item['answer'] else option + ' identifies ' + meanings[option] + ', a different action or grammatical function.' for option in item['options']]
            item['distractorRationales'] = wrong
            item['assessment']['distractorDifference'] = wrong
        else:
            continue  # Preserve exact optional stress applications and their source binding.
        feedback = item['assessment']['whyAnswerDemonstratesObjective'] + '\n' + extra
        item['feedback'] = feedback
        item['assessment']['whyAnswerDemonstratesObjective'] = feedback
        for n, text in enumerate(dict.fromkeys([item['prompt'], feedback] + [s for s in item['distractorRationales'] if s])):
            record(text, objective, item['id'] + '-text-' + str(n))
        changed.append(item['id'])
    capability = next(c for c in data['sourceCapabilities'] if c['id'] == 'cap-obj-u27-comprehension')
    previous_capability = deepcopy(capability)
    hosts = [r for r in data['provenanceRecords']
             if r['sourceRecordId'] == capability['sourceRecordId'] and r['sourceHash'] == capability['sourceHash']]
    assert len(hosts) == 29
    applications = deepcopy(hosts[0]['synthesis']['applications'])
    assert all(r['synthesis']['applications'] == applications for r in hosts)
    reading = next(s for s in unit['sessions'] if s['id'] == 'u27-s-gap-comprehension')
    instruction = '\n'.join(s['body'] for s in reading['steps'] if s['kind'] in ('TEACH', 'REMEDIATE'))
    corpus = capability['exactSpan'] + '\n' + BIBLIOGRAPHY + '\n' + instruction + '\n' + json.dumps(applications, ensure_ascii=False)
    capability.update(exactSpan=corpus, sourceHash=digest(corpus))
    ids = {}
    for n, host in enumerate(hosts):
        row = deepcopy(host)
        row.update(itemId=PREFIX + 'comprehension-host-' + str(n), originalSourceText=corpus, sourceHash=digest(corpus))
        row['synthesis']['bibliography'] = BIBLIOGRAPHY
        data['provenanceRecords'].append(row)
        ids[host['itemId']] = row['itemId']
        added.append(row['itemId'])
    for item in data['items']:
        if item['objectiveId'] != 'obj-u27-comprehension':
            continue
        for key in ('relationParticipantIds', 'answerSourceIds', 'distractorSourceIds'):
            item['assessment'][key] = [ids.get(value, value) for value in item['assessment'][key]]
    return data, {'addedProvenanceIds': added, 'changedItemIds': changed,
                  'previousCapability': previous_capability, 'updatedCapabilitySha256': capability['sourceHash']}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--pack', type=Path, default=PACK)
    parser.add_argument('--ledger', type=Path, default=ROOT / 'audit/content-corrections/UNIT27_REMAINING_INSTRUCTION_CORRECTION.json')
    args = parser.parse_args()
    before = args.pack.read_bytes()
    after, changes = repair(loads(before))
    if not changes:
        print('Already repaired')
        return
    encoded = dumps(after).encode()
    args.pack.write_bytes(encoded)
    if args.pack == PACK:
        pins = ROOT / 'app/src/main/java/dev/russiancompanion/v2/curriculum/ContentAuditCapabilityPins.kt'
        pins.write_text(pins.read_text().replace(changes['previousCapability']['sourceHash'], changes['updatedCapabilitySha256']))
    ledger = dict(schemaVersion=1, previousHead=subprocess.check_output(['git', '-C', str(ROOT), 'rev-parse', 'HEAD'], text=True).strip(),
                  disposition='IMPLEMENTED_PENDING_FINAL_AUDIT', sessionObjectives=list(LESSONS),
                  beforeCurriculumFileSha256=hashlib.sha256(before).hexdigest(),
                  afterCurriculumFileSha256=hashlib.sha256(encoded).hexdigest(),
                  bibliography=BIBLIOGRAPHY, **changes)
    args.ledger.write_text(json.dumps(ledger, ensure_ascii=False, indent=2) + '\n')
    print(len(changes['addedProvenanceIds']), 'pending records;', len(changes['changedItemIds']), 'items; four teaching cards')


if __name__ == '__main__':
    main()
