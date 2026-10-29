from pathlib import Path
import hashlib,json,difflib
R=Path('/workspace/russian-companion');W=Path(__file__).parent
path='app/src/main/java/dev/russiancompanion/v2/curriculum/V2GovernedIdentity.kt'
old=(R/path).read_text();new=old
before='''        if (steps.get(pack) != null && units.get(pack) != null) return
        val stepMap = pack.units.flatMap { it.sessions }.flatMap { it.steps }
            .associate { step -> step.id to rawStepContentDigest(pack, step) }
        val unitMap = pack.units.associate { unit ->
            unit.number to V2Validator.unitContentReviewDigest(pack, unit.number)
        }'''
after='''        val cachedUnits = units.get(pack)
        if (steps.get(pack) != null && cachedUnits != null &&
            pack.units.all { it.number in cachedUnits }) return
        val stepMap = pack.units.flatMap { it.sessions }.flatMap { it.steps }
            .associate { step -> step.id to rawStepContentDigest(pack, step) }
        val unitMap = pack.units.associate { unit ->
            unit.number to (cachedUnits?.get(unit.number)
                ?: V2Validator.unitContentReviewDigest(pack, unit.number))
        }'''
assert new.count(before)==1;new=new.replace(before,after,1)
before='''    fun unitDigest(pack: V2CurriculumPack, unitNumber: Int): String? = units.get(pack)?.get(unitNumber)'''
after='''    /** A cold or evicted immutable pack must not rehash a unit for every recorded attempt. */
    fun unitDigest(pack: V2CurriculumPack, unitNumber: Int): String? {
        units.get(pack)?.get(unitNumber)?.let { return it }
        if (pack.units.none { it.number == unitNumber }) return null
        return synchronized(units) {
            units.get(pack)?.get(unitNumber) ?: run {
                val computed = V2Validator.unitContentReviewDigest(pack, unitNumber)
                units.put(pack, units.get(pack).orEmpty() + (unitNumber to computed))
                computed
            }
        }
    }'''
assert new.count(before)==1;new=new.replace(before,after,1)
out=W/'PROPOSED_MAIN'/path;out.parent.mkdir(parents=True,exist_ok=True);out.write_text(new)
(W/'PROPOSED_CORRECTION.diff').write_text(''.join(difflib.unified_diff(old.splitlines(keepends=True),new.splitlines(keepends=True),fromfile='a/'+path,tofile='b/'+path)))
(W/'PROPOSED_SOURCE_IDENTITIES.json').write_text(json.dumps({'path':path,'oldSha256':hashlib.sha256(old.encode()).hexdigest(),'newSha256':hashlib.sha256(new.encode()).hexdigest(),'draftOnly':True,'applied':False},indent=2)+'\n')
print(json.dumps({'draftOnly':True,'repoEdited':False,'newTestExecution':False}))
