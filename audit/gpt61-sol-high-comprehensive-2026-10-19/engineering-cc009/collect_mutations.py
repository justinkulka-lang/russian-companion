"""Reconcile the 24 matrix cases printed into the preserved JUnit XML."""
from pathlib import Path
import json, sys, xml.etree.ElementTree as E
root = Path(sys.argv[1])
rows = []
for path in (root / 'VERIFIED/XML').glob('TEST-*.xml'):
    for output in E.parse(path).getroot().findall('system-out'):
        for line in (output.text or '').splitlines():
            if line.startswith('CC009_CASE '):
                rows.append(dict(part.split('=', 1) for part in line.split()[1:]))
expected = {(f'u28-i-{kind}-adverb-{number}', field, mode)
            for kind in ['imperfective', 'perfective'] for number in [1, 2]
            for field in ['cue', 'answer', 'distractors'] for mode in ['FROZEN_CORPUS', 'MIRROR']}
assert len(rows) == 24
assert {(r['item'], r['field'], r['mode']) for r in rows} == expected
assert all(r['rejected'] == 'true' and r['unitIdentityChanged'] ==
           ('true' if r['mode'] == 'FROZEN_CORPUS' else 'false') for r in rows)
recorded = json.loads((root / 'MUTATION_CASES.json').read_text())['cases']
assert sorted(rows, key=str) == sorted(recorded, key=str)
print('24 distinct case records match raw XML; these are not additional JUnit methods')
