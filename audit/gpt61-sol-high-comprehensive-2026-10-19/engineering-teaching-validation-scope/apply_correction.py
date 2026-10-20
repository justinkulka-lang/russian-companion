from pathlib import Path
import subprocess,json,hashlib
r=Path('/workspace/russian-companion');o=Path(__file__).parent
asset='app/src/main/assets/content/v2-curriculum.json'
now=json.loads((r/asset).read_bytes()); proofs=[]
for n,ref,cls,var,oldname in [(13,'2ff71f197efb73d99662bc8a64180dc01a79aec3','Unit13ContextTeachingTest','current','result'),(14,'96242a17a87cc2ec0f5d7418cd98a9d3220a9e99','Unit14ImpersonalRemediationTest','p','valid'),(15,'96242a17a87cc2ec0f5d7418cd98a9d3220a9e99','Unit15ImperativeRemediationTest','p','valid')]:
 raw=subprocess.check_output(['git','show',ref+':'+asset],cwd=r); hist=json.loads(raw)
 unit=lambda p:next(u for u in p['units'] if u['number']==n)
 hu,cu=unit(hist),unit(now)
 ids={a['id'] for a in hu['objectives']} if n!=15 else {'obj-u15-imperative'}
 sessions=hu['sessions'] if n!=15 else [s for s in hu['sessions'] if s['id']=='u15-s-imperative']
 objectives=[a for a in hu['objectives'] if a['id'] in ids]
 examples={i for a in objectives for i in a.get('exampleIds',[])}|{i for s in sessions for st in s['steps'] for i in st.get('exampleIds',[])}
 checks={k:[x for x in hist[k] if pred(x)]==[x for x in now[k] if pred(x)] for k,pred in [('items',lambda x:x['objectiveId'] in ids),('examples',lambda x:x['id'] in examples),('provenanceRecords',lambda x:x.get('objectiveId') in ids if n==15 else x.get('unitNumber')==n),('sourceCapabilities',lambda x:x.get('objectiveId') in ids if n==15 else x.get('unitNumber')==n)]}
 checks['targetUnitOrSession']=hu==cu if n!=15 else sessions==[s for s in cu['sessions'] if s['id']=='u15-s-imperative']
 checks['targetObjectives']=objectives==[a for a in cu['objectives'] if a['id'] in ids]
 assert all(checks.values()),checks
 proofs.append(dict(unit=n,historicalCommit=ref,historicalAssetSha256=hashlib.sha256(raw).hexdigest(),currentAssetSha256=hashlib.sha256((r/asset).read_bytes()).hexdigest(),wholeUnitEqual=hu==cu,comparisons=checks,objectiveIds=sorted(ids),exampleIds=sorted(examples)))
 p=r/f'app/src/test/java/dev/russiancompanion/v2/{cls}.kt';s=p.read_text()
 old=f'        val {oldname} = V2Validator.validate({var}); assertTrue({oldname}.errors.joinToString("\\n"), {oldname}.ok)'
 assert s.count(old)==1
 block='''        // Historical conservation stays above; current full-pack validation has current rule pins.
        val validationPack = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
            .also { V2PackIdentityIndex.warm(it) }
'''
 if n!=15:
  block+=f'''        val historicalUnit = {var}.unit({n})!!
        assertEquals(historicalUnit, validationPack.unit({n}))
        val objectiveIds = historicalUnit.objectives.map {{ it.id }}.toSet()
        assertEquals({var}.items.filter {{ it.objectiveId in objectiveIds }}, validationPack.items.filter {{ it.objectiveId in objectiveIds }})
        assertEquals({var}.provenanceRecords.filter {{ it.unitNumber == {n} }}, validationPack.provenanceRecords.filter {{ it.unitNumber == {n} }})
        assertEquals({var}.sourceCapabilities.filter {{ it.unitNumber == {n} }}, validationPack.sourceCapabilities.filter {{ it.unitNumber == {n} }})
        val exampleIds = (historicalUnit.objectives.flatMap {{ it.exampleIds }} +
            historicalUnit.sessions.flatMap {{ it.steps }}.flatMap {{ it.exampleIds }}).toSet()
'''
 else:
  block+='''        // The later aspect-session correction is separate from this exact imperative chain.
        val objectiveId = "obj-u15-imperative"
        val historicalObjective = p.unit(15)!!.objectives.single { it.id == objectiveId }
        assertEquals(session(p), session(validationPack))
        assertEquals(historicalObjective, validationPack.unit(15)!!.objectives.single { it.id == objectiveId })
        assertEquals(p.items.filter { it.objectiveId == objectiveId }, validationPack.items.filter { it.objectiveId == objectiveId })
        assertEquals(p.provenanceRecords.filter { it.objectiveId == objectiveId }, validationPack.provenanceRecords.filter { it.objectiveId == objectiveId })
        assertEquals(p.sourceCapabilities.filter { it.objectiveId == objectiveId }, validationPack.sourceCapabilities.filter { it.objectiveId == objectiveId })
        val exampleIds = (historicalObjective.exampleIds + session(p).steps.flatMap { it.exampleIds }).toSet()
'''
 block+=f'''        assertEquals({var}.examples.filter {{ it.id in exampleIds }}, validationPack.examples.filter {{ it.id in exampleIds }})
        val {oldname} = V2Validator.validate(validationPack); assertTrue({oldname}.errors.joinToString("\\n"), {oldname}.ok)'''
 p.write_text(s.replace(old,block))
(o/'TARGET_COMPARISON.json').write_text(json.dumps(proofs,indent=2)+'\n')
