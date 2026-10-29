"""Pinned Unit13 research; --check reproduces bytes, with zero JUnit/Gradle execution."""
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

BASE = '5971ed56cb4e17b15e4430202d206ec8df59337a'
D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())
PRE = 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-source-reconciliation-2026-10-27/'
CORE = 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-gender-correction-2026-10-27/'
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
    'obj-u13-past-aspect', 'obj-u13-tense-keep', 'obj-u13-infinitive', 'obj-u13-reported',
]
selected = {
    'obj-u4-present': 'PROPOSE_EXACT_EXISTING_FOUR_ITEM_PRESENT_POOL',
    'obj-u12-aspect': 'PROPOSE_BOTH_EXISTING_TAUGHT_ASPECT_PAIR_CHECKS',
    'obj-u11-past-aspect': 'RETAIN_EXACT_EXISTING_TWO_ITEM_PAST_GENDER_POOL',
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
        if unit['number'] == 13:
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
assert [ob[k] for k in count_keys] == [28, 59, 108, 51, 20]
current = next(q for q in ob['policies'] if q['unit'] == 13)
current_pools = [q for q in ob['pools'] if q['unit'] == 13]
present = next(q for q in ob['pools'] if q['unit'] == 6 and q['slot'] == 0)
past = next(q for q in ob['pools'] if q['unit'] == 13 and q['slot'] == 2)
assert current['schema'] == 6 and current['pools'] == 3
assert current_pools[0]['items'] == ['u1-i-decode-pair']
assert current_pools[1]['items'] == ['u12-i-aspect-1']
by = {chain['objective']['id']: chain for chain in chains}
aspect_ids = by['obj-u12-aspect']['objective']['checkItemIds']
assert aspect_ids == ['u12-i-aspect-1', 'u12-i-aspect-2']
proposed = [dict(present, unit=13, slot=0), dict(current_pools[1], items=aspect_ids), dict(past)]
for candidate, cards, count in [(proposed[0], 11, 6), (proposed[1], 3, 4), (proposed[2], 4, 3)]:
    chain = by[candidate['objective']]
    assert candidate['items'] == chain['objective']['checkItemIds']
    assert chain['allTeachingBodiesInRepair'] and chain['teachingCards'] == cards
    assert len(chain['teachingExampleIds']) == count
    assert set(chain['teachingExampleIds']) == set(chain['repairExampleIds'])
assert all(items[key]['options'].index(items[key]['answer']) == 0 for q in proposed for key in q['items'])
new_pools = [q for q in ob['pools'] if q['unit'] != 13] + proposed
projection = dict(activePolicies=ob['activePolicies'], objectivePools=len(new_pools),
                  candidatePlacements=sum(len(q['items']) for q in new_pools),
                  uniqueCandidateItems=len({key for q in new_pools for key in q['items']}),
                  printSingletonPolicies=len({q['unit'] for q in new_pools if q['items'] == ['u1-i-decode-pair']}))
assert [projection[k] for k in count_keys] == [28, 59, 112, 52, 19]
kernel = blob(KERNEL).decode()
policy_blocks = list(re.finditer(r'internal object (\w+CumulativeReviewPolicy)\s*\{(.*?)\n\}', kernel, re.S))
assert len(policy_blocks) == 39
policy_hashes = {m.group(1): sha(m.group().encode()) for m in policy_blocks}
schemas = {name: int(value) for name, value in re.findall(r'internal const val (\w*SCHEMA)\s*=\s*(\d+)', kernel)}
assert sorted(schemas.values()) == list(range(1, 15))
assert 'unit13-prior-present-aspect-past-gender-v3' not in kernel
assert 'Unit13WiderCumulativeReviewPolicy.definition.pool[2]' in kernel
current_title = re.search(r'    13 -> "([^"]+)"', kernel).group(1)
unit13 = next(u for u in p['units'] if u['number'] == 13)
checks13 = list(dict.fromkeys(i for o in unit13['objectives'] for i in o['checkItemIds']))
guided13 = list(dict.fromkeys(i for session in unit13['sessions'] for s in session['steps'] if s['kind'] == 'GUIDED' for i in s.get('itemIds', [])))
assert len(checks13) == 9 and len(guided13) == 4 and len(unit13['sessions']) == 6
assert unit13['advancement']['minimumDistinctChecks'] == 8
owner13 = [q for q in ob['pools'] if any(items[key]['objectiveId'].startswith('obj-u13-') for key in q['items'])]
assert [(q['unit'], q['objective'], q['items']) for q in owner13] == [(14, 'obj-u13-reported', ['u13-i-report-frame-time'])]
conservation = json.loads(blob(PRE + 'CONSERVATION.json'))
assert len(conservation['archives']) == 73
fixtures13, fixtures14 = [], []
archive_inventory = []
archive_document_identities = collections.defaultdict(list)
for name, expected in conservation['archives'].items():
    data = blob(name)
    assert sha(data) == expected
    with zipfile.ZipFile(io.BytesIO(data)) as z:
        assert z.testzip() is None
        archive_inventory.append(dict(path=name, sha256=expected, bytes=len(data), members=len(z.namelist()), crcVerified=True))
        if 'learner.json' not in z.namelist():
            continue
        raw = z.read('learner.json')
        document = json.loads(raw)
        canonical_document = sha(json.dumps(document,ensure_ascii=False,sort_keys=True,separators=(',',':')).encode())
        archive_document_identities[canonical_document].append(name)
        for target, destination in [(13, fixtures13), (14, fixtures14)]:
            assignments = [a for a in document.get('reviewAssignments', []) if a['targetUnit'] == target]
            if not assignments:
                continue
            ids = {a['id'] for a in assignments}
            destination.append(dict(zipPath=name, zipSha256=expected, learnerSha256=sha(raw),
                assignments=assignments,
                answers=[a for a in document.get('reviewAnswers', []) if a['assignmentId'] in ids],
                completions=[c for c in document.get('unitCompletions', []) if c['unitNumber'] == target],
                recordingMembers=sum(not i.is_dir() and i.filename.startswith('recordings/') for i in z.infolist()),
                manifest=json.loads(z.read('manifest.json')) if 'manifest.json' in z.namelist() else None))
history13 = collections.Counter(f"{a['schema']}:{a['policyId']}" for f in fixtures13 for a in f['assignments'])
history14 = collections.Counter(f"{a['schema']}:{a['policyId']}" for f in fixtures14 for a in f['assignments'])
assert set(history13) <= {'1:unit13-prior-reading-aspect-partner-v1', '6:unit13-prior-reading-aspect-past-gender-v2'}
assert set(history14) <= {'1:unit14-prior-reading-reported-time-v1'}
owner12_digests = collections.Counter(s['unitDigest'] for f in fixtures13 for a in f['assignments'] for s in a['slots'] if s['ownerUnit'] == 12)
owner11_digests = collections.Counter(s['unitDigest'] for f in fixtures13 for a in f['assignments'] for s in a['slots'] if s['ownerUnit'] == 11)
owner13_digests = collections.Counter(s['unitDigest'] for f in fixtures14 for a in f['assignments'] for s in a['slots'] if s['ownerUnit'] == 13)
historical_json_files = [name for name in git('ls-tree','-r','--name-only',BASE,'--','audit/independent-gpt61-2026-10-12').decode().splitlines() if '/ia003-unit13-' in name]
json_documents = []
for name in historical_json_files:
    if not name.endswith('.json'):
        continue
    value = json.loads(blob(name))
    if not isinstance(value,dict) or not isinstance(value.get('reviewAssignments'),list):
        continue
    tasks = [a for a in value['reviewAssignments'] if a['targetUnit'] in [13,14]]
    canonical = sha(json.dumps(value,ensure_ascii=False,sort_keys=True,separators=(',',':')).encode())
    context_path = str(Path(name).parent/'BASELINE_CONTEXT.json')
    context = json.loads(blob(context_path))
    json_documents.append(dict(path=name,sha256=sha(blob(name)),canonicalDocumentSha256=canonical,
        matchingArchivePaths=archive_document_identities.get(canonical,[]),assignments=tasks,
        contextPath=context_path,contextSha256=sha(blob(context_path)),
        sourceAsRecorded={k:context[k] for k in ['baselineCommit','sourceCommit'] if k in context},
        attribution='Historical baseline context only;not a fresh current-v2 producer or replay result.'))
json_counts = collections.Counter(f"{a['schema']}:{a['policyId']}" for d in json_documents for a in d['assignments'] if a['targetUnit']==13)
assert len(json_documents)==24 and sum(any(a['targetUnit']==13 for a in d['assignments']) for d in json_documents)==18
assert dict(json_counts)=={'1:unit13-prior-reading-aspect-partner-v1':18}
assert all(d['matchingArchivePaths'] for d in json_documents)
manifest_paths = {n: f'review/unit-{n}/UNIT_{n}_REVIEW_MANIFEST.json' for n in [11, 12, 13, 14]}
identity_keys = {'unit','unitNumber','curriculumSourceCommit','contentReviewDigest','packContentReviewDigest','reviewPackageDigest','repositorySourceDigest','mappingPackageDigest','status','unitReviewStatus'}
current_bindings = {str(n): dict(path=path, sha256=sha(blob(path)),
    gitBlob=git('rev-parse',BASE+':'+path).decode().strip(),
    data={k:v for k,v in json.loads(blob(path)).items() if k in identity_keys}) for n,path in manifest_paths.items()}
protected = dict(conservation['archives'])
records = json.loads(blob(PRE + 'APPLIED_RECORDS.json'))['currentRecordHashes']
assert len(records) == 155
protected.update(records)
for name in historical_json_files:
    protected[name] = sha(blob(name))
for name in git('ls-tree', '-r', '--name-only', BASE, '--', PRE, 'app/src/test').decode().splitlines():
    protected[name] = sha(blob(name))
assert all(sha(blob(name)) == expected for name, expected in protected.items())
summary = dict(sourceCommit=BASE, status='PROPOSED_NOT_ACTIVATED', activated=False,
    objectiveChains=len(chains), inventoriedCheckRecords=len(rows),
    proposedId='unit13-prior-present-aspect-past-gender-v3', proposedFreshSchema=15,
    proposedFreshTitle='Review supplied present forms, aspect partners and past agreement',
    currentSavedV1V2Title=current_title, proposedPools=proposed,
    assignedSlots=3, selectedPresentCandidates=4, selectedAspectCandidates=2, selectedPastGenderCandidates=2,
    registeredPolicyObjectsPreserved=39, schemasPreserved=list(range(1, 15)),
    currentCounts={k: ob[k] for k in count_keys}, projectedCountsNotActivated=projection,
    historicalArchivesChecked=73, archivesContainingUnit13=len(fixtures13), archivesContainingUnit14=len(fixtures14),
    historicalUnit13Assignments=sum(history13.values()), historicalUnit14Assignments=sum(history14.values()),
    historicalUnit13PolicySchemaCounts=dict(history13), historicalUnit14PolicySchemaCounts=dict(history14),
    archivedOwner12Digests=dict(owner12_digests), archivedOwner11Digests=dict(owner11_digests), archivedOwner13Digests=dict(owner13_digests),
    currentUnit11ContentDigest=current_bindings['11']['data']['contentReviewDigest'],
    currentUnit12ContentDigest=current_bindings['12']['data']['contentReviewDigest'],
    currentUnit13ContentDigest=current_bindings['13']['data']['contentReviewDigest'],
    historicalStandaloneJsonDocuments=len(json_documents), historicalStandaloneUnit13Assignments=sum(json_counts.values()),
    standaloneJsonLearnersDuplicateArchiveMembers=True, historicalStandaloneUnit13PolicySchemaCounts=dict(json_counts),
    freshActualCurrentUnit13Capture='NOT_YET_PRODUCED', newRuntimeMethodsExecuted=0,
    newOrdinaryCheckItems=0, newRussian=0, newSkillEnums=0, heldOutItemsAdded=0,
    ordinaryCheckIds=checks13, ordinaryGuidedIds=guided13, ordinaryMinimumDistinctChecks=8,
    ordinaryThresholdIsNotPermissionToOmitAnObjectiveCheck=True,
    qualifiedAcceptance='PENDING_NOT_GRANTED', all48Findings='OPEN', audit='INCOMPLETE',
    physicalAcceptance='OUTSTANDING_LAST', apkIdentity=None, apkSignature=None,
    exclusions={'VisualReviewCaptureTest': 2, 'Units35ChunkCaptureTest': 2,
                'Units610ChunkCaptureTest': 10, 'Units1115GapCaptureTest': 2})
outputs = {
    'CHAINS.json': enc(dict(sourceCommit=BASE, sourcePoolIntegrityVerified=True, chains=chains)),
    'CANDIDATE_ASSESSMENT.csv': csv_bytes(rows),
    'CURRENT_POLICY_INVENTORY.json': enc(dict(sourceCommit=BASE, reusedCompiledObservation=observation_path,
        observationSha256=sha(blob(observation_path)), producerSource=ob['sourceCommit'], appMainTreeMatches=True,
        currentUnit13=current, currentPools=current_pools, proposedPools=proposed,
        registeredPolicyObjectHashes=policy_hashes, existingSchemaConstants=schemas,
        currentCounts={k: ob[k] for k in count_keys}, projectedCountsNotActivated=projection,
        downstreamOwner13Pools=owner13,
        dependency='Current Unit12 v2 reuses the immutable Unit13Wider pool[2]; never edit that object to implement Unit13 v3.',
        existingOwnerBridges='Preserve Unit12 v1 owner identity support for Unit13 v1/v2 and existing Unit13 v1 owner support for Unit14; future v3 needs exact v2 owner13 support too.',
        currentSourceBoundManifests=current_bindings)),
    'HISTORICAL_UNIT13_UNIT14_INVENTORY.json': enc(dict(sourceCommit=BASE,
        scope='Historical byte/member observations only; not replay/test counts or a fresh actual-current capture.',
        archivesChecked=73, archives=archive_inventory, unit13Fixtures=fixtures13, unit14Fixtures=fixtures14,
        unit13PolicySchemaCounts=dict(history13), unit14PolicySchemaCounts=dict(history14),
        historicalStandaloneDocuments=json_documents, standaloneUnit13PolicySchemaCounts=dict(json_counts),
        standaloneDocumentsAreArchiveDuplicatesNotExtraEvidence=True,
        unit13Owner12Digests=dict(owner12_digests), unit13Owner11Digests=dict(owner11_digests), unit14Owner13Digests=dict(owner13_digests))),
    'UNIT13_ORDINARY_SCOPE.json': enc(dict(sourceCommit=BASE, ordinaryUnit=unit13,
        independentCheckIds=checks13, guidedItemIds=guided13, rawMinimumDistinctChecks=8,
        ruleSource='app/src/main/java/dev/russiancompanion/v2/curriculum/V2ProgressRules.kt:241,347',
        ruleExplanation='ordinaryUnitRequirements also requires every gating recognition objective to be demonstrated; objectiveState considers all related item IDs. Baseline must finish guided steps and all nine CHECK items,not just eight answers.',
        scope='Full Unit13 declaration/six sessions retained as engineering input,not qualified curriculum acceptance.')),
    'PROTECTED_EVIDENCE_HASHES.json': enc(protected),
    'SUMMARY.json': enc(summary),
    'INPUTS.json': enc(dict(sourceCommit=BASE, sourceRootTree=git('rev-parse', BASE + '^{tree}').decode().strip(),
        inputTrees={name: git('rev-parse', BASE + ':' + name).decode().strip() for name in ['app/src/main','app/src/test','catalog','docs','review','release']},
        rawCurriculumSha256=sha(blob(RAW)), kernelSha256=sha(blob(KERNEL)),
        authorityHashes={name: sha(blob('docs/governance/' + name)) for name in ['MASTER_AUTHORITY_v2.0.md','MASTER_AUTHORITY_v2.0_AMENDMENT_1.md']},
        sourceCapabilityObjectHashes={c['id']:sha(enc(c)) for c in p['sourceCapabilities']},
        protectedHashScope='Pinned research input bytes. --current verifies checkpoint conservation; historical reproduction uses BASE Git blobs after later authorized changes.',
        privatePdf='NOT_READ_OR_PACKAGED', newRuntimeMethods=0)),
}
outputs['GENERATED_SHA256.json'] = enc({name: sha(data) for name,data in outputs.items()})
assert sys.argv[1:] in [[], ['--check']]
for name,data in outputs.items():
    if sys.argv[1:] == ['--check']:
        assert (D/name).read_bytes() == data, name
    else:
        assert not (D/name).exists(), name
        (D/name).write_bytes(data)
print(json.dumps(dict(verified=sys.argv[1:] == ['--check'], chains=len(chains), inventoriedCheckRecords=len(rows),
    archivesShaCrc=73, archivesContainingUnit13=len(fixtures13), archivesContainingUnit14=len(fixtures14),
    unit13PolicySchemaCounts=dict(history13), unit14PolicySchemaCounts=dict(history14),
    rawUnit13MinimumChecks=8, declaredUnit13CheckItems=9, guidedUnit13Items=4, runtimeMethodsExecuted=0)))
