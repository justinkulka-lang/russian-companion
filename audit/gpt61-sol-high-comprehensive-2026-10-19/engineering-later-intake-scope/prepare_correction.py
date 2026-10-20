from pathlib import Path
import subprocess,json,hashlib,re
r=Path('/workspace/russian-companion');w=Path(__file__).parent
refs={4:'e6db0bdfe0bbb40edf190623e2123014946f305b',7:'b7e53230e2119b1e57d781b1e1b955f074a38601',8:'fe919656bfa2775b5cc2c22f2496d4258343cfea',10:'7efff9e18328c612de235236d98e834bb85cd1db',15:'0f8001a8ec2cb76a7bc994a2c8d77db09a272d04'}
prior='49a926c5ace0459edf7712ca5d90db4a63ff9d31';asset='app/src/main/assets/content/v2-curriculum.json';current=json.loads((r/asset).read_text());rows=[]
for n,ref in refs.items():
 raw=subprocess.check_output(['git','show',ref+':'+asset],cwd=r);p=json.loads(raw);u=next(x for x in p['units'] if x['number']==n);v=next(x for x in current['units'] if x['number']==n);ids={x['id'] for x in u['objectives']};exids={e for x in u['objectives'] for e in x.get('exampleIds',[])}|{e for s in u['sessions'] for st in s['steps'] for e in st.get('exampleIds',[])}
 checks={'unit':u==v,'items':[x for x in p['items'] if x['objectiveId'] in ids]==[x for x in current['items'] if x['objectiveId'] in ids],'provenance':[x for x in p['provenanceRecords'] if x['unitNumber']==n]==[x for x in current['provenanceRecords'] if x['unitNumber']==n],'sourceCapabilities':[x for x in p['sourceCapabilities'] if x['unitNumber']==n]==[x for x in current['sourceCapabilities'] if x['unitNumber']==n],'referencedExamples':[x for x in p['examples'] if x['id'] in exids]==[x for x in current['examples'] if x['id'] in exids]};assert all(checks.values()),(n,checks)
 rows.append(dict(unit=n,implementationCommit=ref,sourceAssetSha256=hashlib.sha256(raw).hexdigest(),comparisons=checks))
(w/'EXACT_TARGET_COMPARISON.json').write_text(json.dumps(dict(currentCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=r,text=True).strip(),currentAssetSha256=hashlib.sha256((r/asset).read_bytes()).hexdigest(),priorPinsCommit=prior,rows=rows),indent=2)+'\n')
helper='''package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import java.io.File

/** Exact recorded sources for intake-era assertions; no semantic acceptance or current hash refresh. */
internal object RecordedIntakeCorrectionScope {
    fun load(root: File, commit: String): V2CurriculumPack {
        val process = ProcessBuilder("git", "show", "$commit:app/src/main/assets/content/v2-curriculum.json")
            .directory(root).start()
        val text = process.inputStream.bufferedReader().readText()
        assertEquals(0, process.waitFor())
        return V2PackLoader.parse(text).also { V2PackIdentityIndex.warm(it) }
    }

    fun assertCurrentUnitMatches(current: V2CurriculumPack, source: V2CurriculumPack, number: Int) {
        val expected = source.unit(number)!!
        assertEquals(expected, current.unit(number))
        val objectiveIds = expected.objectives.map { it.id }.toSet()
        assertEquals(source.items.filter { it.objectiveId in objectiveIds }, current.items.filter { it.objectiveId in objectiveIds })
        assertEquals(source.provenanceRecords.filter { it.unitNumber == number }, current.provenanceRecords.filter { it.unitNumber == number })
        assertEquals(source.sourceCapabilities.filter { it.unitNumber == number }, current.sourceCapabilities.filter { it.unitNumber == number })
        val exampleIds = (expected.objectives.flatMap { it.exampleIds } +
            expected.sessions.flatMap { it.steps }.flatMap { it.exampleIds }).toSet()
        assertEquals(source.examples.filter { it.id in exampleIds }, current.examples.filter { it.id in exampleIds })
        assertEquals(V2Validator.unitContentReviewDigest(source, number), V2Validator.unitContentReviewDigest(current, number))
    }
}
'''
(w/'RecordedIntakeCorrectionScope.kt').write_text(helper)
p=r/'app/src/test/java/dev/russiancompanion/v2/Units1620SourceIntakeTest.kt';s=p.read_text();needle='''            } else {
                assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
            }
''';assert s.count(needle)==1
s=s.replace(needle,'''            } else if (number == 15) {
                // Keep the original pin on its recorded snapshot; preparation changed later.
                val prior = historicalSourcePack("49a926c5ace0459edf7712ca5d90db4a63ff9d31")
                assertEquals("historical unit $number", digest, V2Validator.unitContentReviewDigest(prior, number))
                val correction = historicalSourcePack("0f8001a8ec2cb76a7bc994a2c8d77db09a272d04")
                RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack, correction, number)
                org.junit.Assert.assertNotEquals(digest, V2Validator.unitContentReviewDigest(pack, number))
            } else {
                assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
            }
''');(w/p.name).write_text(s)
p=r/'app/src/test/java/dev/russiancompanion/v2/Units2630SourceIntakeTest.kt';s=p.read_text();needle='''        preservedContentDigests.forEach { (number, digest) ->
            assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
        }
''';assert s.count(needle)==1
block='''        // These later teaching corrections have explicit sources; every other pin stays current.
        val corrections = mapOf(
            4 to "e6db0bdfe0bbb40edf190623e2123014946f305b",
            7 to "b7e53230e2119b1e57d781b1e1b955f074a38601",
            8 to "fe919656bfa2775b5cc2c22f2496d4258343cfea",
            10 to "7efff9e18328c612de235236d98e834bb85cd1db",
            15 to "0f8001a8ec2cb76a7bc994a2c8d77db09a272d04",
        )
        val prior = RecordedIntakeCorrectionScope.load(root, "49a926c5ace0459edf7712ca5d90db4a63ff9d31")
        preservedContentDigests.forEach { (number, digest) ->
            val correction = corrections[number]
            if (correction == null) {
                assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
            } else {
                assertEquals("historical unit $number", digest, V2Validator.unitContentReviewDigest(prior, number))
                RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack,
                    RecordedIntakeCorrectionScope.load(root, correction), number)
                assertNotEquals(digest, V2Validator.unitContentReviewDigest(pack, number))
            }
        }
''';s=s.replace(needle,block);(w/p.name).write_text(s)
print('Prepared exact source guards for five units; no checkout input changed.')
