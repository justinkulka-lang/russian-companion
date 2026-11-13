#!/usr/bin/env python3
"""Bounded authoring stage from the retained pre-correction pack, never an in-place rebuild."""
from __future__ import annotations
import argparse
import hashlib
import json
from pathlib import Path

SPEC = Path(__file__).resolve().parent / 'corrections/literacy-2026-10-29/CORRECTION.json'

def sha(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()

def render(raw: bytes, spec: dict) -> tuple[bytes, dict]:
    if sha(raw) != spec['baselineAssetSha256']:
        raise ValueError('Expected the exact retained pre-correction curriculum; do not apply to another source')
    pack = json.loads(raw)
    before = json.loads(raw)
    assert (json.dumps(before, ensure_ascii=False, separators=(',', ':')) + '\n').encode() == raw, 'Preserve the retained compact serialization'
    by_id = {item['id']: item for item in pack['items']}
    for item_id, values in spec['rationales'].items():
        item = by_id[item_id]
        if item.get('distractorRationales'):
            raise ValueError('Expected a missing rationale, not permission to replace an authored one')
        if item_id in spec['unitTwoCaseVariants']:
            item.update(spec['unitTwoCaseVariants'][item_id])
            item['prompt'] = spec['variantPrompt']
            item['kind'] = 'PRINT_PAIR'
            item['feedback'] = 'The lowercase and uppercase forms belong to the same Cyrillic letter. Compare both strokes and shape in the letter-board pair.'
        if len(values) != len(item['options']) or any((not v) != (o == item['answer']) for o, v in zip(item['options'], values)):
            raise ValueError('Rationale/value alignment is invalid: ' + item_id)
        item['distractorRationales'] = values
    unit = next(u for u in pack['units'] if u['number'] == 2)
    objective = next(o for o in unit['objectives'] if o['id'] == 'obj-u2-review')
    objective['exampleIds'] = spec['unitTwoTeachingExampleIds']
    teaching = next(st for s in unit['sessions'] for st in s['steps'] if st['id'] == 'u2-st-review')
    teaching['body'] = spec['unitTwoTeachingBody']
    teaching['exampleIds'] = spec['unitTwoTeachingExampleIds']
    # The patch neither regenerates the intake nor changes scores, identities, review or approval.
    assert len(pack['items']) == len(before['items']) == 626
    assert {i['id']: i['answer'] for i in pack['items']} == {i['id']: i['answer'] for i in before['items']}
    assert pack['units'][2:] == before['units'][2:]
    assert pack['units'][0] == before['units'][0]
    assert all(pack[k] == before[k] for k in pack if k not in ['items', 'units'])
    for old, new in zip(before['items'], pack['items']):
        allowed = {'distractorRationales'} if old['id'] in spec['rationales'] else set()
        if old['id'] in spec['unitTwoCaseVariants']:
            allowed |= {'prompt', 'kind', 'promptLetter', 'options', 'feedback'}
        assert all(old.get(k) == new.get(k) for k in set(old) | set(new) if k not in allowed), old['id']
    encoded = (json.dumps(pack, ensure_ascii=False, separators=(',', ':')) + '\n').encode()
    report = dict(baselineSha256=sha(raw), resultSha256=sha(encoded), resultBytes=len(encoded),
        itemIds=list(spec['rationales']), authoredWrongOptionExplanations=46, caseVariantIds=list(spec['unitTwoCaseVariants']),
        itemCountUnchanged=True, allItemIdsAndAnswerValuesUnchanged=True, allSourcesRightsProvenanceAndUnit3Through30DataUnchanged=True,
        reviewStatus='IMPLEMENTED_PENDING_FINAL_AUDIT', independentAudit='INCOMPLETE', all59Findings='OPEN')
    return encoded, report

def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--input', required=True, type=Path)
    parser.add_argument('--output', required=True, type=Path)
    parser.add_argument('--report', required=True, type=Path)
    args = parser.parse_args()
    if len({args.input.resolve(), args.output.resolve(), args.report.resolve()}) != 3 or args.output.exists() or args.report.exists():
        raise ValueError('Use distinct input and fresh output/report paths; never overwrite retained files')
    encoded, report = render(args.input.read_bytes(), json.loads(SPEC.read_text()))
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.report.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open('xb') as out:
        out.write(encoded)
    with args.report.open('x') as out:
        json.dump(report, out, indent=2); out.write('\n')
    print(json.dumps(report))

if __name__ == '__main__':
    main()
