"""Evidence construction only; exact public fragments/comparison modes require review, not automatic approval."""
from pathlib import Path
import json,re,hashlib,collections
base=Path('audit/independent-gpt61-2026-10-12/source-guard-scope')
p=json.loads(Path('app/src/main/assets/content/v2-curriculum.json').read_text());records=p['provenanceRecords']
for r in records:r['originalSourceText']=r.get('originalSourceText') or p['sourceTextPool'][r['originalSourceTextRef']]
records.sort(key=lambda r:('synthesis' in r['transformation'],r['itemId']))
manifest_path=Path('app/src/test/resources/reviewed-curriculum-cyrillic.json');manifest=json.loads(manifest_path.read_text());manifest['files']=manifest['files'][:15]
sha=lambda b:hashlib.sha256(b).hexdigest()
def expected(r):return {k:r[k] for k in ['itemId','sourceId','sourceRevision','sourceUrl','sourceRecordId','sourceHash','contentHash','transformation','licenseId','shippedText','reviewStatus']}
def unbold(s):return s.replace("'''",'').replace("''",'')
modes=[('RAW',lambda s:s),('REMOVE_WIKI_EMPHASIS',unbold),('LOWERCASE',str.lower)]
catalogs=[Path('catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt'),Path('catalog/sources/units-26-30/wiktionary/russian-entry-guidelines-92541199.txt')]
for name in ['BatchARemainingSources.kt','BatchBSources.kt','ExplicitSourcePairs.kt','RemainingSources.kt']:
 path=Path('app/src/main/java/dev/russiancompanion/v2/curriculum')/name;values=set()
 for line in path.read_text().splitlines():
  if not re.search(r'\\u04[0-9a-fA-F]{2}',line):continue
  for token in re.findall(r'"(?:\\.|[^"\\])*"',line):
   v=json.loads(token)
   if any(0x400<=ord(c)<=0x4ff for c in v):values.add(v)
 bindings=[]
 for v in sorted(values):
  matches=[(r,f) for r in records for f in ['shippedText','sourceUrl','sourceRecordId','originalSourceText'] if r.get(f)==v]
  if matches:
   r,f=matches[0];b={'kind':'PROVENANCE_FIELD','value':v,'field':f,'purpose':'EXISTING_SOURCE_RECORD_COMPARISON','record':expected(r)}
  else:
   match=None
   for r in records:
    if 'synthesis' in r['transformation']:continue
    for mode,fn in modes:
     if v in fn(r['originalSourceText']):match=(r,mode);break
    if match:break
   if match:
    r,mode=match;b={'kind':'PROVENANCE_FRAGMENT','value':v,'mode':mode,'purpose':'EXISTING_SOURCE_FRAGMENT_COMPARISON','record':expected(r)}
   else:
    found=None
    for f in catalogs:
     for mode,fn in modes:
      if v in fn(f.read_text()):found=(f,mode);break
     if found:break
    assert found,(name,v)
    f,mode=found;b={'kind':'CATALOG_FRAGMENT','value':v,'mode':mode,'purpose':'RETAINED_SOURCE_COMPARISON_NOT_CAPABILITY_ADMISSION','catalogPath':str(f),'catalogSha256':sha(f.read_bytes())}
  bindings.append(b)
 manifest['files'].append({'path':str(path.relative_to('app')),'sha256':sha(path.read_bytes()),'purpose':'Exact existing escaped source comparison implementation; no directory exemption or source acceptance','bindings':bindings})
manifest_path.write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n')
counts=collections.Counter(b['purpose'] for e in manifest['files'] for b in e['bindings']);summary={'files':len(manifest['files']),'bindings':sum(len(e['bindings']) for e in manifest['files']),'purposes':dict(counts),'kinds':dict(collections.Counter(b['kind'] for e in manifest['files'] for b in e['bindings'])),'initialRun':'INITIAL/MANIFEST.json','initialCounts':{'reported':7,'passed':3,'failed':4,'errors':0,'skipped':0},'cause':'Additional Unicode-escape protection discovered four existing comparison files absent from original raw-character scan','productionChanges':False,'formalAcceptance':False}
(base/'ESCAPED_LITERAL_FOLLOWUP.json').write_text(json.dumps(summary,indent=2)+'\n')
print(json.dumps(summary))
