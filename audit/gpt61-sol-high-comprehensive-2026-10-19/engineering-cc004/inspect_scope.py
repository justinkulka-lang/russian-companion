"""Read-only CC004 inventory. Counts describe content, never executed tests."""
from pathlib import Path
from html.parser import HTMLParser
import hashlib, json, subprocess, sys
sys.dont_write_bytecode = True
repo = Path(sys.argv[1]).resolve()
out = Path(sys.argv[2]).resolve()
sys.path.insert(0, str(repo))
from catalog.curriculum_storage import loads
sha = lambda data: hashlib.sha256(data).hexdigest()
asset = repo / 'app/src/main/assets/content/v2-curriculum.json'
pack = loads(asset.read_bytes())
unit = next(u for u in pack['units'] if u['number'] == 21)
objective = next(o for o in unit['objectives'] if o['id'] == 'obj-u21-purpose')
session = next(s for s in unit['sessions'] if s['id'] == 'u21-s-purpose')
items = [i for i in pack['items'] if i['objectiveId'] == objective['id']]
teaches = [s for s in session['steps'] if s['kind'] == 'TEACH']
repair = next(s for s in session['steps'] if s['kind'] == 'REMEDIATE')
assert repair['body'] == '\n\n'.join(s['body'] for s in teaches)
assert repair['exampleIds'] == list(dict.fromkeys(e for s in teaches for e in s.get('exampleIds', [])))
assert all(session['steps'].index(s) < next(n for n, t in enumerate(session['steps']) if t['kind'] == 'GUIDED') for s in teaches)
class VisibleText(HTMLParser):
    def __init__(self):
        super().__init__(); self.parts = []
    def handle_data(self, data): self.parts.append(data)
scans = []
for path in sorted((repo / 'catalog/sources').rglob('*')):
    if path.suffix not in {'.txt', '.html'}: continue
    raw = path.read_bytes(); text = raw.decode('utf-8')
    if path.suffix == '.html':
        parser = VisibleText(); parser.feed(text); text = ' '.join(parser.parts)
    hits = [{'line': n, 'lineSha256': sha(line.encode()), 'occurrences': line.count('чтобы') + line.count('дабы')}
            for n, line in enumerate(text.splitlines(), 1) if 'чтобы' in line or 'дабы' in line]
    scans.append({'path': str(path.relative_to(repo)), 'sha256': sha(raw), 'hits': hits})
result = dict(
    sourceCommit=subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=repo, text=True).strip(),
    assetSha256=sha(asset.read_bytes()),
    corpusCounts={k: len(pack[k]) for k in ['items', 'examples', 'provenanceRecords', 'sourceCapabilities']},
    objective=objective, session=session,
    items=[{k: i[k] for k in ['id', 'prompt', 'promptLetter', 'options', 'answer', 'assessment']} for i in items],
    capabilities=[c for c in pack['sourceCapabilities'] if c.get('objectiveId') == objective['id']],
    independentPurposeCues=[i['id'] for i in items if i['id'] in objective['checkItemIds'] and 'чтобы' in i.get('promptLetter', '')],
    independentCompleteRussianPurposeContexts=[],
    completeContextCountBasis='Manual inspection of the three displayed cues: чтобы / если / потому что; all are isolated conjunctions.',
    completeTeachingBeforeGuided=True, completeExistingRepair=True,
    retainedTextSearch=scans,
    searchLimit='Literal lowercase чтобы/дабы in retained TXT and HTML data nodes only. HTML lines are normalized parser text. No claim of exhaustive linguistic classification, live-site inspection, rights qualification or PDF inspection.',
    engineeringDisposition='CC004_OPEN_SOURCE_ADMISSION_AND_CONTEXTUAL_APPLICATION_REQUIRED',
    executedTests=False,
)
out.write_text(json.dumps(result, ensure_ascii=False, indent=2) + '\n')
print(json.dumps({'source': result['sourceCommit'], 'corpus': result['corpusCounts'], 'filesScanned': len(scans), 'filesWithHits': sum(bool(s['hits']) for s in scans), 'independentPurposeCues': result['independentPurposeCues'], 'existingRepair': 'COMPLETE'}, indent=2))
