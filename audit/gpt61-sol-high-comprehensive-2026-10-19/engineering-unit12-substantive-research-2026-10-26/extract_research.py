"""Pinned Unit12 research; --check reproduces bytes, with zero JUnit/Gradle execution."""
from pathlib import Path
import collections
import csv
import hashlib
import io
import json
import re
import subprocess
import sys
import zipfile

BASE = '05407a70d1d70b196ef21b25cff51357c072c087'
D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())
PRE = 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit11-source-reconciliation-2026-10-26/'
CORE = 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit11-noun-correction-2026-10-26/'
RAW = 'app/src/main/assets/content/v2-curriculum.json'
KERNEL = 'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'


def git(*args):
    return subprocess.check_output(['git', *args], cwd=R)


def blob(name):
    return git('show', BASE + ':' + name)


def sha(data):
    return hashlib.sha256(data).hexdigest()


def enc(value):
    return (json.dumps(value, ensure_ascii=False, indent=2) + '\n').encode()


def csv_bytes(rows):
    stream = io.StringIO(newline='')
    writer = csv.DictWriter(stream, fieldnames=list(rows[0]), lineterminator='\n')
    writer.writeheader()
    writer.writerows(rows)
    return stream.getvalue().encode()


p = json.loads(blob(RAW))
items = {i['id']: i for i in p['items']}
examples = {e['id']: e for e in p['examples']}
references = {r['id']: r for r in p['references']}
pool = p['sourceTextPool']
assert all(sha(value.encode()) == key for key, value in pool.items())
objectives = [
    'obj-u1-decoding', 'obj-u3-gender', 'obj-u4-present', 'obj-u4-nominative',
    'obj-u5-indeclinable', 'obj-u6-animacy', 'obj-u7-endings', 'obj-u8-plural',
    'obj-u9-odindva', 'obj-u10-have', 'obj-u10-absence', 'obj-u11-past-read',
    'obj-u11-past-be', 'obj-u11-past-aspect', 'obj-u11-reflexive',
    'obj-u12-future-imp', 'obj-u12-future-perf', 'obj-u12-aspect',
    'obj-u12-dative', 'obj-u12-spelling', 'obj-u12-prep-plural',
]
selected = {
    'obj-u3-gender': 'SELECT_EXACT_EXISTING_LEXICAL_GENDER_POOL',
    'obj-u11-past-aspect': 'SELECT_EXACT_EXISTING_TWO_ITEM_PAST_GENDER_POOL',
}
chains, rows = [], []
for oid in objectives:
    unit = next(u for u in p['units'] if any(o['id'] == oid for o in u['objectives']))
    objective = next(o for o in unit['objectives'] if o['id'] == oid)
    all_steps = {s['id']: s for session in unit['sessions'] for s in session['steps']}
    steps = [all_steps[key] for key in dict.fromkeys(
        objective['instructionStepIds'] + objective['practiceStepIds'] + objective['remediationStepIds'])]
    item_ids = list(dict.fromkeys(objective['checkItemIds'] + [key for s in steps for key in s.get('itemIds', [])]))
    example_ids = list(dict.fromkeys(objective['exampleIds'] + [key for s in steps for key in s.get('exampleIds', [])]))
    reference_ids = list(dict.fromkeys(key for s in steps for key in s.get('referenceIds', [])))
    provenance = []
    for record in p['provenanceRecords']:
        if record.get('objectiveId') != oid:
            continue
        pin = {key: record[key] for key in [
            'itemId', 'recordType', 'sourceId', 'sourceUrl', 'sourceRevision',
            'sourceRecordId', 'sourceHash', 'contentHash', 'reviewStatus', 'licenseId', 'licenseUrl',
        ] if key in record}
        pin['canonicalFullRecordSha256'] = sha(enc(record))
        if 'originalSourceTextRef' in record:
            key = record['originalSourceTextRef']
            assert key == record['sourceHash'] and key in pool
            pin['resolvedSourcePoolSha256'] = sha(pool[key].encode())
        elif 'originalSourceText' in record:
            assert sha(record['originalSourceText'].encode()) == record['sourceHash']
            pin['inlineSourceSha256'] = record['sourceHash']
        provenance.append(pin)
    source_ids = {items[key]['sourceId'] for key in item_ids} | {r['sourceId'] for r in provenance}
    teaching = [s for s in steps if s['kind'] == 'TEACH']
    repair = all_steps[objective['remediationStepIds'][0]] if objective['remediationStepIds'] else None
    chains.append(dict(
        unit=unit['number'], objective=objective, steps=steps,
        items=[items[key] for key in item_ids], examples=[examples[key] for key in example_ids],
        references=[references[key] for key in reference_ids], provenancePins=provenance,
        sourceRecords=[s for s in p['sources'] if s['id'] in source_ids],
        rightsRecords=[r for r in p['rights'] if r['sourceId'] in source_ids],
        capabilities=[c for c in p['sourceCapabilities'] if c.get('objectiveId') == oid],
        capabilityLimit='Existing capability objects only; absence is not newly granted capability or acceptance.',
        teachingCards=len(teaching),
        allTeachingBodiesInRepair=bool(repair) and all(s['body'] in repair['body'] for s in teaching),
        teachingExampleIds=sorted({key for s in teaching for key in s.get('exampleIds', [])}),
        repairExampleIds=repair.get('exampleIds', []) if repair else [],
    ))
    def signature(item):
        return (item.get('prompt'), item.get('promptLetter'), tuple(sorted(item['options'])), item['answer'])
    guided = [items[key] for s in steps if s['kind'] == 'GUIDED' for key in s.get('itemIds', [])]
    for key in objective['checkItemIds']:
        item = items[key]
        decision = selected.get(oid, 'DEFER_ALTERNATIVE')
        if unit['number'] == 12:
            decision = 'CURRENT_TARGET_ORDINARY_NOT_PRIOR_CUMULATIVE'
        rows.append(dict(
            unit=unit['number'], objective=oid, item=key, decision=decision,
            rawKeyIndex=item['options'].index(item['answer']),
            sameTupleAsGuided=';'.join(g['id'] for g in guided if signature(g) == signature(item)),
            sourceId=item['sourceId'], reviewStatus=item['reviewStatus'], itemSha256=sha(enc(item)),
        ))

observation_path = CORE + 'CURRENT_POLICY_OBSERVATION.json'
ob = json.loads(blob(observation_path))
assert git('rev-parse', ob['sourceCommit'] + ':app/src/main') == git('rev-parse', BASE + ':app/src/main')
count_keys = ['activePolicies', 'objectivePools', 'candidatePlacements', 'uniqueCandidateItems', 'printSingletonPolicies']
assert [ob[key] for key in count_keys] == [28, 59, 103, 51, 21]
current = next(policy for policy in ob['policies'] if policy['unit'] == 12)
current_pools = [q for q in ob['pools'] if q['unit'] == 12]
gender = next(q for q in ob['pools'] if q['unit'] == 5 and q['slot'] == 0)
past = next(q for q in ob['pools'] if q['unit'] == 13 and q['slot'] == 2)
by = {chain['objective']['id']: chain for chain in chains}
assert current['schema'] == 1 and current_pools[1]['items'] == ['u11-i-past-aspect-1']
for candidate, oid, cards, count in [
    (gender, 'obj-u3-gender', 5, 16), (past, 'obj-u11-past-aspect', 4, 3),
]:
    chain = by[oid]
    assert candidate['items'] == chain['objective']['checkItemIds']
    assert chain['allTeachingBodiesInRepair'] and chain['teachingCards'] == cards
    assert len(chain['teachingExampleIds']) == count
    assert set(chain['teachingExampleIds']) == set(chain['repairExampleIds'])
proposed = [dict(gender, unit=12, slot=0), dict(past, unit=12, slot=1)]
assert [items[key]['options'].index(items[key]['answer']) for key in gender['items']] == [0, 0, 1, 2, 0]
assert [items[key]['options'].index(items[key]['answer']) for key in past['items']] == [0, 0]
kernel = blob(KERNEL).decode()
policy_blocks = re.findall(r'internal object (\w+CumulativeReviewPolicy)\s*\{(.*?)\n\}', kernel, re.S)
assert len(policy_blocks) == 38
policy_hashes = {name: sha(('internal object ' + name + ' {' + body + '\n}').encode()) for name, body in policy_blocks}
unit12 = next(u for u in p['units'] if u['number'] == 12)
assert unit12['advancement']['minimumDistinctChecks'] == 12
assert sum(len(o['checkItemIds']) for o in unit12['objectives']) == 12
assert len(unit12['sessions']) == 8
downstream = [q for q in ob['pools'] if any(items[key]['objectiveId'].startswith('obj-u12-') for key in q['items'])]
assert [(q['unit'], q['objective']) for q in downstream] == [(13, 'obj-u12-aspect')]

conservation = json.loads(blob(PRE + 'CONSERVATION.json'))
assert len(conservation['archives']) == 72
fixtures = []
for name, expected in conservation['archives'].items():
    data = blob(name)
    assert sha(data) == expected
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        assert archive.testzip() is None
        if 'learner.json' not in archive.namelist():
            continue
        raw = archive.read('learner.json')
        document = json.loads(raw)
        assignments = [a for a in document.get('reviewAssignments', []) if a['targetUnit'] == 12]
        if not assignments:
            continue
        assignment_ids = {a['id'] for a in assignments}
        fixtures.append(dict(
            zipPath=name, zipSha256=expected, learnerSha256=sha(raw), assignments=assignments,
            answers=[a for a in document.get('reviewAnswers', []) if a['assignmentId'] in assignment_ids],
            completions=[c for c in document.get('unitCompletions', []) if c['unitNumber'] == 12],
        ))
assert len(fixtures) == 24
history_counts = collections.Counter((a['schema'], a['policyId']) for f in fixtures for a in f['assignments'])
assert dict(history_counts) == {(1, 'unit12-prior-reading-past-gender-v1'): 24}
owner11 = collections.Counter(s['unitDigest'] for f in fixtures for a in f['assignments'] for s in a['slots'] if s['ownerUnit'] == 11)
assert dict(owner11) == {'35de2337c00321867e4c30871b048181738017a79babc4f0135ad4131e1b6c69': 24}
protected = dict(conservation['archives'])
records = json.loads(blob(PRE + 'APPLIED_RECORDS.json'))['currentRecordHashes']
assert len(records) == 155
protected.update(records)
for name in git('ls-tree', '-r', '--name-only', BASE, '--', PRE).decode().splitlines():
    protected[name] = sha(blob(name))
assert all(sha(blob(name)) == expected for name, expected in protected.items())

summary = dict(
    sourceCommit=BASE, status='PROPOSED_NOT_ACTIVATED', activated=False,
    objectiveChains=len(chains), inventoriedCheckRecords=len(rows),
    proposedId='unit12-prior-gender-past-gender-v2', proposedFreshSchema=14,
    proposedFreshTitle='Review noun genders and supplied past agreement', proposedPools=proposed,
    assignedSlots=2, selectedGenderCandidates=5, selectedPastGenderCandidates=2,
    registeredPolicyObjectsPreserved=38, schemasPreserved=list(range(1, 14)),
    nounGenderRawKeys=[0, 0, 1, 2, 0], pastGenderRawKeys=[0, 0],
    historicalArchivesChecked=72, archivesContainingUnit12=24,
    historicalUnit12Assignments=24, allHistoricalUnit11OwnerDigestsPredateCurrentDeclaration=True,
    freshActualCurrentUnit12Capture='NOT_YET_PRODUCED',
    currentPrintSingletonPolicies=21, newRuntimeMethodsExecuted=0, heldOutItemsAdded=0,
    qualifiedAcceptance='PENDING_NOT_GRANTED', all48Findings='OPEN', audit='INCOMPLETE',
    physicalAcceptance='OUTSTANDING_LAST', apkIdentity=None, apkSignature=None,
    exclusions={'VisualReviewCaptureTest': 2, 'Units35ChunkCaptureTest': 2,
                'Units610ChunkCaptureTest': 10, 'Units1115GapCaptureTest': 2},
)
outputs = {
    'CHAINS.json': enc(dict(sourceCommit=BASE, sourcePoolIntegrityVerified=True, chains=chains)),
    'CANDIDATE_ASSESSMENT.csv': csv_bytes(rows),
    'CURRENT_POLICY_INVENTORY.json': enc(dict(
        sourceCommit=BASE, reusedCompiledObservation=observation_path,
        observationSha256=sha(blob(observation_path)), producerSource=ob['sourceCommit'],
        appMainTreeMatches=True, currentUnit12=current, currentPools=current_pools,
        proposedPools=proposed, registeredPolicyObjectHashes=policy_hashes,
        currentCounts={key: ob[key] for key in count_keys}, downstreamOwner12Pools=downstream,
    )),
    'HISTORICAL_UNIT12_INVENTORY.json': enc(dict(
        sourceCommit=BASE, scope='Historical byte inventory; not replay/test counts or a fresh actual-current capture.',
        archivesChecked=72, archivesContainingUnit12=len(fixtures),
        unit11OwnerDigests=dict(owner11), fixtures=fixtures,
    )),
    'UNIT12_ORDINARY_SCOPE.json': enc(dict(
        sourceCommit=BASE, ordinaryUnit=unit12,
        scope='Full Unit12 declaration/eight sessions preserved as engineering input, not qualified curriculum acceptance.',
    )),
    'PROTECTED_EVIDENCE_HASHES.json': enc(protected),
    'SUMMARY.json': enc(summary),
    'INPUTS.json': enc(dict(
        sourceCommit=BASE, sourceRootTree=git('rev-parse', BASE + '^{tree}').decode().strip(),
        inputTrees={name: git('rev-parse', BASE + ':' + name).decode().strip()
                    for name in ['app/src/main', 'app/src/test', 'catalog', 'docs', 'review', 'release']},
        rawCurriculumSha256=sha(blob(RAW)), kernelSha256=sha(blob(KERNEL)),
        authorityHashes={name: sha(blob('docs/governance/' + name)) for name in [
            'MASTER_AUTHORITY_v2.0.md', 'MASTER_AUTHORITY_v2.0_AMENDMENT_1.md']},
        privatePdf='NOT_READ_OR_PACKAGED', newRuntimeMethods=0,
    )),
}
outputs['GENERATED_SHA256.json'] = enc({name: sha(data) for name, data in outputs.items()})
check = sys.argv[1:] == ['--check']
assert sys.argv[1:] in [[], ['--check']]
for name, data in outputs.items():
    if check:
        assert (D / name).read_bytes() == data, name
    else:
        assert not (D / name).exists(), name
        (D / name).write_bytes(data)
print(json.dumps(dict(verified=check, chains=len(chains), inventoriedCheckRecords=len(rows),
                      archivesShaCrc=72, archivesContainingUnit12=24, runtimeMethodsExecuted=0)))
