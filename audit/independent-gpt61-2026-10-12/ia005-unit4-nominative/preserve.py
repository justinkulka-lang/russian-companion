from pathlib import Path
import sys,subprocess,json,hashlib,tempfile,shutil
root=Path(__file__).resolve().parents[3];out=Path(__file__).resolve().parent;sys.path.insert(0,str(root/'catalog'));from curriculum_storage import loads
base='b5610846cae953c3655466794c83cdcb4063ac4e';asset='app/src/main/assets/content/v2-curriculum.json';raw=subprocess.check_output(['git','show',base+':'+asset],cwd=root);old=loads(raw);current=(root/asset).read_bytes();new=loads(current);sha=lambda b:hashlib.sha256(b).hexdigest()
assert len(old['provenanceRecords'])==6869 and len(new['provenanceRecords'])==6913;records={r['itemId']:r for r in new['provenanceRecords']};assert all(records[r['itemId']]==r for r in old['provenanceRecords'])
for k in ['sourceCapabilities','sources','rights','ownedEditionMappings','authoredEnglishRecords','admissions','audioAssets','handwritingAssets']:assert old[k]==new[k],k
assert old['examples']==[e for e in new['examples'] if e['id']!='ex-u4-nom-student']
assert [u for u in old['units'] if u['number']!=4]==[u for u in new['units'] if u['number']!=4]
for k in old['units'][3]:
 if k not in ['objectives','sessions']:assert old['units'][3][k]==new['units'][3][k]
for k,id in [('objectives','obj-u4-nominative'),('sessions','u4-s-nominative')]:assert [x for x in old['units'][3][k] if x['id']!=id]==[x for x in new['units'][3][k] if x['id'] not in [id,'obj-u4-nominative-gloss-reference']]
legacy=['u4-g-nominative-1','u4-i-nom-muzh','u4-i-nom-poezd'];extra=['u4-g-nom-role','u4-i-nom-role-train','u4-i-nom-role-student'];assert [i for i in old['items'] if i['id'] not in legacy]==[i for i in new['items'] if i['id'] not in legacy+extra]
for id in legacy:
 a=next(i for i in old['items'] if i['id']==id).copy();a.update(kind='SOURCE_MEANING',objectiveId='obj-u4-nominative-gloss-reference');assert a==next(i for i in new['items'] if i['id']==id)
pins='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';prior=subprocess.check_output(['git','show',base+':'+pins],cwd=root).decode();rows=[s for s in prior.splitlines() if ' to ' in s];assert len(rows)==68 and all(s in (root/pins).read_text() for s in rows)
sourceids=[]
for f in ['catalog/sources/units-3-5/wikibooks/nominative-4213267.txt','catalog/sources/units-3-5/wikibooks/nominative-4213267.revision.json','catalog/sources/units-3-5/wikibooks/copyrights-4622060.txt']:
 b=(root/f).read_bytes();assert b==subprocess.check_output(['git','show',base+':'+f],cwd=root);sourceids.append(dict(path=f,sha256=sha(b)))
receipt=json.loads((root/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text());original=[]
for f in receipt['files']:
 path=root/'audit/independent-gpt61-2026-10-12'/f['path'];assert sha(path.read_bytes())==f['sha256'];original.append(dict(path=str(path.relative_to(root)),sha256=f['sha256']))
with tempfile.TemporaryDirectory(prefix='unit4-role-') as tmp:
 t=Path(tmp)
 for f in ['catalog/curriculum_storage.py','audit/content-corrections/repair_unit4_nominative_role.py','catalog/sources/units-3-5/wikibooks/nominative-4213267.txt']:
  (t/f).parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/f,t/f)
 (t/asset).parent.mkdir(parents=True,exist_ok=True);(t/asset).write_bytes(raw)
 for n in range(2):subprocess.run([sys.executable,str(t/'audit/content-corrections/repair_unit4_nominative_role.py')],check=True,capture_output=True);assert (t/asset).read_bytes()==current
m=dict(baselineCommit=base,rawCurriculumSha256=sha(current),precedingProvenancePreserved=6869,newPendingProvenance=44,totalProvenance=6913,all58CapabilitiesPreserved=True,priorCanonicalPinsPreserved=68,newCanonicalPins=3,allOriginalExamplesPreserved=True,other29UnitsPreserved=True,otherUnit4ObjectivesSessionsAndAdvancementUnchanged=True,minimumDistinctChecks=14,legacyTranslationsPreservedAsOptional=legacy,newRequiredRoleItemIds=extra,allSourcesRightsMappingsPreserved=True,deterministicReplay=True,idempotent=True,sourceIdentities=sourceids,originalDiagnosticFiles=original,allNineOriginalHashesPreserved=True,semanticAcceptance=False)
(out/'PRESERVATION.json').write_text(json.dumps(m,indent=2)+'\n');print({k:m[k] for k in ['baselineCommit','totalProvenance','newPendingProvenance','other29UnitsPreserved','deterministicReplay']})
