from pathlib import Path
import subprocess,json,hashlib
r=Path('/workspace/russian-companion');w=Path(__file__).parent
p=r/'app/src/test/java/dev/russiancompanion/v2/audit/OwnerGovernanceDecisionTest.kt';s=p.read_text().replace('import dev.russiancompanion.release.computeReleaseSourceDigest','import dev.russiancompanion.v2.RecordedIntakeCorrectionScope\nimport dev.russiancompanion.release.computeReleaseSourceDigest')
needle='''        assertEquals(
            "e90420abb0bc18f68b07514b60335cc36be3a9eeacc870c004ece95e8d4c0ee4",
            V2Validator.contentReviewDigest(pack),
        )''';assert s.count(needle)==1
s=s.replace(needle,'''        // Keep the prior aggregate identity; bind the current pack to its last actual asset edit.
        val prior = RecordedIntakeCorrectionScope.load(root(), "49a926c5ace0459edf7712ca5d90db4a63ff9d31")
        assertEquals(
            "e90420abb0bc18f68b07514b60335cc36be3a9eeacc870c004ece95e8d4c0ee4",
            V2Validator.contentReviewDigest(prior),
        )
        val currentSource = RecordedIntakeCorrectionScope.load(root(), "b7e53230e2119b1e57d781b1e1b955f074a38601")
        assertEquals(currentSource, pack)
        assertEquals(V2Validator.contentReviewDigest(currentSource), V2Validator.contentReviewDigest(pack))
        org.junit.Assert.assertNotEquals(V2Validator.contentReviewDigest(prior), V2Validator.contentReviewDigest(pack))''');(w/p.name).write_text(s)
p=r/'app/src/test/java/dev/russiancompanion/v2/audit/CurriculumReadinessReconciliationTest.kt';s=p.read_text().replace('import dev.russiancompanion.release.computeReleaseSourceDigest','import dev.russiancompanion.v2.RecordedIntakeCorrectionScope\nimport dev.russiancompanion.release.computeReleaseSourceDigest')
needle='''        // Retain original and later historical/optional records; these counts are not required checks.
''';assert s.count(needle)==1
s=s.replace(needle,needle+'''        val beforeAvailability = RecordedIntakeCorrectionScope.load(root(), "2c455b96dde9abd1fc8e2b5350df161216cf7e31")
        val beforeAvailabilityModel = reconcileCurriculumReadiness(root(), beforeAvailability)
''')
s=s.replace('ex-u4-verb-работать", model.orphanedExampleIds)','ex-u4-verb-работать", beforeAvailabilityModel.orphanedExampleIds)',1).replace('assertEquals(6, model.orphanedExampleRecords)','assertEquals(6, beforeAvailabilityModel.orphanedExampleRecords)',1)
needle='        assertEquals(14, model.orphanedCheckItems)';assert s.count(needle)==1
s=s.replace(needle,needle+'''
        // The positive-existence correction retains its two old absence examples as historical records.
        val availabilitySource = RecordedIntakeCorrectionScope.load(root(), "fe919656bfa2775b5cc2c22f2496d4258343cfea")
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack, availabilitySource, 8)
        assertEquals(beforeAvailability.examples, pack.examples)
        val retainedAbsenceExamples = listOf("ex-u8-availability-2", "ex-u8-availability-3")
        assertEquals((beforeAvailabilityModel.orphanedExampleIds.split(", ") + retainedAbsenceExamples)
            .sorted().joinToString(", "), model.orphanedExampleIds)
        assertEquals(beforeAvailabilityModel.orphanedExampleRecords + retainedAbsenceExamples.size, model.orphanedExampleRecords)''');(w/p.name).write_text(s)
asset='app/src/main/assets/content/v2-curriculum.json';last='b7e53230e2119b1e57d781b1e1b955f074a38601';raw=subprocess.check_output(['git','show',last+':'+asset],cwd=r);assert raw==(r/asset).read_bytes()
(w/'EXACT_TARGET_COMPARISON.json').write_text(json.dumps(dict(currentCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=r,text=True).strip(),lastAssetEditCommit=last,lastAssetEditAndCurrentBytesEqual=True,assetSha256=hashlib.sha256(raw).hexdigest(),historicalAggregatePinCommit='49a926c5ace0459edf7712ca5d90db4a63ff9d31',beforeAvailabilityCommit='2c455b96dde9abd1fc8e2b5350df161216cf7e31',availabilityImplementationCommit='fe919656bfa2775b5cc2c22f2496d4258343cfea',declaredNewOrphans=['ex-u8-availability-2','ex-u8-availability-3']),indent=2)+'\n')
print('Prepared two test-only corrections; current pack bytes equal last asset implementation exactly.')
