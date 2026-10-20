"""Static inventory only. No audio listening, admission, or content generation."""
import sys, json, hashlib, subprocess
from pathlib import Path
sys.dont_write_bytecode = True
ROOT = Path('/workspace/russian-companion')
sys.path.insert(0, str(ROOT))
from catalog.curriculum_storage import loads
OUT = Path(sys.argv[1]); OUT.mkdir(parents=True, exist_ok=True)
sha = lambda p: hashlib.sha256(p.read_bytes()).hexdigest()
asset = ROOT/'app/src/main/assets/content/v2-curriculum.json'
pack = loads(asset.read_bytes())
units = pack['units'][:2]
objective_ids = {o['id'] for u in units for o in u['objectives']}
items = [i for i in pack['items'] if i['objectiveId'] in objective_ids]
examples = [e for e in pack['examples'] if e.get('objectiveId') in objective_ids]
reachable_examples = {e for u in units for o in u['objectives'] for e in o['exampleIds']}
reachable_examples |= {e for u in units for s in u['sessions'] for t in s['steps'] for e in t.get('exampleIds', [])}
examples = [e for e in pack['examples'] if e in examples or e['id'] in reachable_examples]
ref_ids = {r for u in units for s in u['sessions'] for t in s['steps'] for r in t.get('referenceIds', [])}
inventory = dict(sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip(),
    scope='Complete Unit1/2 object projection and packaged catalog identity graph; static, not qualified content or listening inspection',
    curriculumSha256=sha(asset), counts={k:len(pack[k]) for k in ['items','examples','provenanceRecords','sourceCapabilities','references']},
    units=units, items=items, examples=examples, letters=pack['letters'],
    references=[r for r in pack['references'] if r['id'] in ref_ids],
    capabilities=[c for c in pack['sourceCapabilities'] if c['unitNumber'] in [1,2]],
    audioAssets=pack['audioAssets'], handwritingAssets=pack['handwritingAssets'], admissions=pack['admissions'])
(OUT/'FOUNDATION_INVENTORY.json').write_text(json.dumps(inventory,ensure_ascii=False,indent=2)+'\n')
catalog = json.loads((ROOT/'app/src/main/assets/content/bundled-content.json').read_bytes())
chains = json.loads((ROOT/'catalog/license-chains.json').read_bytes())
texts = {x['id']:x for x in catalog['sourceTexts']}
rights = {x['id']:x for x in catalog['assetRightsRecords']}
sources = {x['id']:x for x in catalog['sourceRecords']}
files = {x['filename']:x for x in chains['files']}
rows=[]
for audio in catalog['audioModels']:
    path=ROOT/'app/src/main/assets'/audio['localAudioReference']
    text=texts[audio['sourceTextId']]; right=rights[audio['rightsRecordId']]; source=sources[audio['sourceRecordId']]
    chain=files[path.name]; digest=sha(path)
    assert digest==chain['sha256']
    assert right['sourceRecordId']==source['id']==text['sourceRecordId']
    assert right['assetIdentifier']==audio['id']
    rows.append(dict(audioId=audio['id'],path=audio['localAudioReference'],sha256=digest,
        sourceTextId=text['id'],existingCatalogText=text['stressMarkedText'],sourceRecordId=source['id'],
        rightsRecordId=right['id'],licenseStatus=right['licenseStatus'],compatibilityDecision=right['compatibilityDecision'],
        inclusionResult=right['inclusionResult'],catalogMatchVerifiedFlag=text['matchVerified'],
        recordedLicense=chain['licenseAuthority'],actualListeningThisRun=False,unit12Admission=False))
assert len(rows)==len({r['path'] for r in rows})==1000
assert len(list((ROOT/'app/src/main/assets/audio').glob('*.ogg')))==1000
catalog_report=dict(scope='All1000 existing catalog graph links and actual audio SHA256 values checked; flags are inherited metadata, not new listening/rights qualification',
    sourceCommit=inventory['sourceCommit'],catalogSha256=sha(ROOT/'app/src/main/assets/content/bundled-content.json'),
    licenseChainsSha256=sha(ROOT/'catalog/license-chains.json'),models=len(rows),fileHashesMatched=1000,
    sourceTextRightsGraphMatched=1000,unit12Admitted=0,rows=rows)
(OUT/'CATALOG_IDENTITY_INVENTORY.json').write_text(json.dumps(catalog_report,ensure_ascii=False,indent=2)+'\n')
allow = json.loads((ROOT/'audit/independent-gpt61-2026-10-12/current-comprehensive-audit/RETAINED_ARCHIVE_ALLOWLIST.json').read_bytes())['sha256']
assert len(allow)==66
assert all(sha(ROOT/p)==h for p,h in allow.items())
conservation=dict(sourceCommit=inventory['sourceCommit'],curriculumSha256=sha(asset),
    retainedArchiveHashesVerified=allow,archiveCount=66,historyMeaningChanged=False,
    appCatalogReviewDocsModified=False,newAdmissions=0,privatePdfInspectedThisRun=False)
(OUT/'CONSERVATION.json').write_text(json.dumps(conservation,indent=2)+'\n')
print(json.dumps(dict(unit12Items=len(items),unit12Examples=len(examples),unit12Capabilities=len(inventory['capabilities']),
    audioModels=1000,audioHashesMatched=1000,archivesMatched=66)))
