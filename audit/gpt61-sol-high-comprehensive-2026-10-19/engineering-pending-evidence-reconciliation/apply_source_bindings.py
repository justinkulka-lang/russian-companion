from pathlib import Path
r=Path('/workspace/russian-companion');w=Path(__file__).parent;p=r/'app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt';s=p.read_text()
insert='''// Current pending-review sources; prior implementation constants above remain historical.
internal const val UNIT4_PRESENT_TEACHING_CURRICULUM_COMMIT = "e6db0bdfe0bbb40edf190623e2123014946f305b"
internal const val UNIT7_INVARIANCE_PREPARATION_CURRICULUM_COMMIT = "b7e53230e2119b1e57d781b1e1b955f074a38601"
internal const val UNIT8_EXISTENCE_SEQUENCE_CURRICULUM_COMMIT = "fe919656bfa2775b5cc2c22f2496d4258343cfea"
internal const val UNIT10_FRAGMENT_MEANING_CURRICULUM_COMMIT = "7efff9e18328c612de235236d98e834bb85cd1db"
internal const val UNIT15_ASPECT_PREPARATION_CURRICULUM_COMMIT = "0f8001a8ec2cb76a7bc994a2c8d77db09a272d04"

'''
needle='internal fun curriculumCommitForUnit(unitNumber: Int): String =';assert s.count(needle)==1;s=s.replace(needle,insert+needle)
for n,old,new in [(4,'UNIT4_CUMULATIVE_REVIEW','UNIT4_PRESENT_TEACHING'),(7,'UNIT7_AGREEMENT_TEACHING','UNIT7_INVARIANCE_PREPARATION'),(8,'UNIT8_CUMULATIVE_REVIEW','UNIT8_EXISTENCE_SEQUENCE'),(10,'UNIT10_CUMULATIVE_REVIEW','UNIT10_FRAGMENT_MEANING'),(15,'UNIT15_IMPERATIVE_REPAIR','UNIT15_ASPECT_PREPARATION')]:
 old=f'else if (unitNumber == {n}) {old}_CURRICULUM_COMMIT';new=f'else if (unitNumber == {n}) {new}_CURRICULUM_COMMIT';assert s.count(old)==1;s=s.replace(old,new)
needle='''internal fun buildUnitReviewPackage(
    pack: V2CurriculumPack,
    unitNumber: Int,
    mappingFacts: MappingPackageFacts = governedMappingFacts(unitNumber),
): Unit1ReviewPackage {''';assert s.count(needle)==1
s=s.replace(needle,'''internal fun buildUnitReviewPackage(
    pack: V2CurriculumPack,
    unitNumber: Int,
    mappingFacts: MappingPackageFacts = governedMappingFacts(unitNumber),
): Unit1ReviewPackage = buildUnitReviewPackageWithSource(pack, unitNumber, mappingFacts, curriculumCommitForUnit(unitNumber))

/** Reconstruct a historical digest; current rendering/validation always use the current source binding. */
internal fun buildHistoricalUnitReviewPackage(
    pack: V2CurriculumPack,
    unitNumber: Int,
    recordedSourceCommit: String,
): Unit1ReviewPackage {
    require(Regex("[0-9a-f]{40}").matches(recordedSourceCommit))
    return buildUnitReviewPackageWithSource(pack, unitNumber, governedMappingFacts(unitNumber), recordedSourceCommit)
}

private fun buildUnitReviewPackageWithSource(
    pack: V2CurriculumPack,
    unitNumber: Int,
    mappingFacts: MappingPackageFacts,
    sourceCommit: String,
): Unit1ReviewPackage {''')
# Both missing-unit and ordinary-unit package construction bind the supplied digest context.
a=s.index('private fun buildUnitReviewPackageWithSource(');b=s.index('internal fun renderUnit',a)
part=s[a:b];assert part.count('            distinctUnresolved,')==1 and part.count('                distinctUnresolved,')==1 if False else True
# The two calls end with distinctUnresolved; add sourceCommit without touching record traversal.
part=part.replace('                distinctUnresolved,\n            ),','                distinctUnresolved,\n                sourceCommit,\n            ),').replace('            distinctUnresolved,\n        ),','            distinctUnresolved,\n            sourceCommit,\n        ),');assert part.count('                sourceCommit,')==1 and part.count('            sourceCommit,')==2
s=s[:a]+part+s[b:]
needle='''    unresolved: List<String>,
): String {''';assert s.count(needle)==1;s=s.replace(needle,'''    unresolved: List<String>,
    sourceCommit: String,
): String {''')
needle='"curriculumSourceCommit" to canonStr(curriculumCommitForUnit(unitNumber))';assert s.count(needle)==1;s=s.replace(needle,'"curriculumSourceCommit" to canonStr(sourceCommit)');p.write_text(s)
p=r/'app/src/test/java/dev/russiancompanion/v2/Units610SourceIntakeTest.kt';s=p.read_text().replace('import dev.russiancompanion.v2.review.buildUnitReviewPackage','import dev.russiancompanion.v2.review.buildHistoricalUnitReviewPackage\nimport dev.russiancompanion.v2.review.buildUnitReviewPackage');needle='buildUnitReviewPackage(priorUnit4, 4)';assert s.count(needle)==2;s=s.replace(needle,'buildHistoricalUnitReviewPackage(priorUnit4, 4, "a1e8f319b0b55f8e1dbcced680c34f32d240d217")');p.write_text(s)
p=r/'app/src/test/java/dev/russiancompanion/v2/review/UnitReviewPipelineTest.kt';s=p.read_text();needle='        assertTrue(curriculumSourceCommitErrors(UNITS_1_5_CURRICULUM_COMMIT, root, 1).isNotEmpty())';assert s.count(needle)==1
s=s.replace(needle,'''        // A reconstructed historical digest cannot relabel a current packet as current source evidence.
        val currentFour = buildUnitReviewPackage(pack, 4)
        val historicalFour = buildHistoricalUnitReviewPackage(pack, 4, UNIT4_CUMULATIVE_REVIEW_CURRICULUM_COMMIT)
        assertNotEquals(currentFour.reviewPackageDigest, historicalFour.reviewPackageDigest)
        val relabelled = renderUnitReviewFiles(pack, 4).files.mapValues { (_, text) ->
            text.replace(currentFour.reviewPackageDigest, historicalFour.reviewPackageDigest)
                .replace(curriculumCommitForUnit(4), UNIT4_CUMULATIVE_REVIEW_CURRICULUM_COMMIT)
        }
        val historicalErrors = validateUnitReviewFiles(pack, 4, relabelled)
        assertTrue(historicalErrors.joinToString("\\n"), historicalErrors.any { it.contains("curriculum source commit") })
        assertTrue(historicalErrors.joinToString("\\n"), historicalErrors.any { it.contains("Review package digest") })
'''+needle);p.write_text(s)
