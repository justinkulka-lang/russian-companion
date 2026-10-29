package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit13PresentFixtures.root
import dev.russiancompanion.v2.Unit13PresentFixtures.pack
import dev.russiancompanion.v2.Unit13PresentFixtures.context
import dev.russiancompanion.v2.Unit13PresentFixtures.document
import dev.russiancompanion.v2.Unit13PresentFixtures.v2Capture
import dev.russiancompanion.v2.Unit13PresentFixtures.advance
import dev.russiancompanion.v2.Unit13PresentFixtures.ordinary
import dev.russiancompanion.v2.Unit13PresentFixtures.prepare
import dev.russiancompanion.v2.Unit13PresentFixtures.open
import dev.russiancompanion.v2.Unit13PresentFixtures.answer
import dev.russiancompanion.v2.Unit13PresentFixtures.archive
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlinx.serialization.json.*
import dev.russiancompanion.v2.Unit13PresentFixtures.before13
import dev.russiancompanion.v2.Unit13PresentFixtures.v1Capture

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit13PresentCompatibilityTest {
    @Test fun exactPriorDeclarationsRefuseNewTasksAndPreserveBothUnit14OwnerBindings() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(pack, 13)) {
        val old = before13
        val before14 = RecordedCurriculumConservationScope.beforeUnit14PluralPossessionReview(root, pack)
        val restored = before14.copy(units = before14.units.map { u -> if (u.number != 13) u else
            u.copy(advancement = u.advancement!!.copy(cumulativeReviewPolicyId = Unit13WiderCumulativeReviewPolicy.definition.id)) })
        assertEquals("Entire actual baseline pack differs only by Unit13 declaration", old, restored)
        // Original Unit13 inventory is a historical contract; all task/mutation checks below stay current.
        val policies = (3..30).map { if (it == 14) Unit14CumulativeReviewPolicy.definition else cumulativeReviewPolicy(it)!! }
        val currentPolicies = (3..30).map { cumulativeReviewPolicy(it)!! }
        assertEquals(Unit14PluralPossessionCumulativeReviewPolicy.definition, currentPolicies.single { it.targetUnit == 14 })
        assertEquals(60, currentPolicies.sumOf { it.pool.size })
        assertEquals(121, currentPolicies.sumOf { p -> p.pool.sumOf { it.candidateItemIds.size } })
        assertEquals(57, currentPolicies.flatMap { it.pool }.flatMap { it.candidateItemIds }.distinct().size)
        assertEquals(18, currentPolicies.count { p -> p.pool.any { it.candidateItemIds == listOf("u1-i-decode-pair") } })
        assertEquals(28, policies.size); assertEquals(59, policies.sumOf { it.pool.size })
        assertEquals(112, policies.sumOf { p -> p.pool.sumOf { it.candidateItemIds.size } })
        assertEquals(52, policies.flatMap { it.pool }.flatMap { it.candidateItemIds }.distinct().size)
        assertEquals(19, policies.count { p -> p.pool.any { it.candidateItemIds == listOf("u1-i-decode-pair") } })
        assertEquals(Unit13WiderCumulativeReviewPolicy.definition.pool[2], Unit12GenderCumulativeReviewPolicy.definition.pool[1])
        val observation = buildJsonObject {
            put("canonicalSourceDigest", V2Validator.contentReviewDigest(pack))
            put("canonicalSourceDigestLegacyLabelMeansPackContentReviewDigest", true)
            put("policyInventoryScope", "historical Unit13 activation, before Unit14 v2 declaration")
            put("currentPools", 60); put("currentPlacements", 121)
            put("currentUniqueCandidates", 57); put("currentPrintSingletonPolicies", 18)
            put("unit13BeforeV2Digest", V2Validator.unitContentReviewDigest(old, 13))
            put("unit13CurrentDigest", V2Validator.unitContentReviewDigest(pack, 13))
            put("activePolicies", policies.size); put("pools", 59); put("placements", 112)
            put("uniqueCandidates", 52); put("printSingletonPolicies", 19)
            put("policies", buildJsonArray { policies.forEach { p -> add(buildJsonObject {
                put("unit", p.targetUnit); put("id", p.id); put("digest", p.digest)
                put("pools", buildJsonArray { p.pool.forEach { o -> add(buildJsonObject {
                    put("objective", o.objectiveId); put("skill", o.skill.name)
                    put("candidates", buildJsonArray { o.candidateItemIds.forEach { add(it) } })
                }) } })
            }) } })
        }
        println("UNIT13_COMPILED_OBSERVATION $observation")
        System.getProperty("audit.unit13.observation")?.let { path ->
            require(path.startsWith("/workspace/artifacts/")); check(!File(path).exists())
            File(path).writeText(observation.toString()+"\n")
        }
        for (legacy in listOf(false, true)) {
            // v2 is the genuine baseline; v1 here is an explicit declaration-only variant.
            // Separate historical-archive coverage below preserves the actual old v1 bytes.
            val producer = if (!legacy) old else old.copy(units = old.units.map { u -> if (u.number != 13) u else
                u.copy(advancement = u.advancement!!.copy(cumulativeReviewPolicyId = Unit13CumulativeReviewPolicy.definition.id)) })
            LocalStore.openInMemory(context()).use { store ->
                val original = document(v2Capture)
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                val future = createCumulativeReviewAssignment(pack, store.v2LearnerView(), 13, "future-unit13", 0)!!
                assertEquals(15, future.schema); assertTrue(reviewAssignmentCurrent(pack, future))
                assertFalse(reviewAssignmentCurrent(producer, future)); assertFalse(reviewAssignmentEligible(producer, store.v2LearnerView(), future))
                if (legacy) {
                    // Complete and record a genuine schema1 assignment using supported old declaration APIs.
                    // Do not relabel the v2 capture or claim this is an older historical archive.
                    val ordinary = original.copy(reviewAssignments = original.reviewAssignments.filter { it.targetUnit != 13 },
                        reviewAnswers = original.reviewAnswers.filter { it.assignmentId != original.reviewAssignments.single { it.targetUnit == 13 }.id })
                    assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(ordinary, emptyList()))
                    ordinary(store, 13, producer)
                }
                val saved = store.openCumulativeReview(producer, 13, store.learnerMutationEpoch())!!
                assertEquals(if (legacy) 1 else 6, saved.schema)
                saved.slots.indices.forEach { answer(store, saved, it, p = producer) }
                assertTrue(advance(store, 13, producer)); ordinary(store, 14, producer)
                val downstream = store.openCumulativeReview(producer, 14, store.learnerMutationEpoch())!!
                val owner = downstream.slots.single { it.ownerUnit == 13 }
                assertEquals(V2Validator.unitContentReviewDigest(producer, 13), owner.unitDigest)
                assertNotEquals(V2Validator.unitContentReviewDigest(pack, 13), owner.unitDigest)
                assertTrue(reviewAssignmentCurrent(pack, downstream)); assertTrue(reviewAssignmentEligible(pack, store.v2LearnerView(), downstream))
                val changed = pack.copy(items = pack.items.map { if (it.id == owner.itemId) it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changed, downstream))
                val changedContent = pack.copy(items = pack.items.map { if (it.id == "u13-i-infinitive-1") it.copy(prompt = it.prompt + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changedContent, downstream))
                val changedRule = pack.copy(units = pack.units.map { u -> if (u.number != 13) u else
                    u.copy(advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) })
                assertFalse(reviewAssignmentCurrent(changedRule, downstream)); assertFalse(13 in currentCompletedUnitNumbers(changedRule, store.v2LearnerView()))
                val changedTeaching = pack.copy(units = pack.units.map { u -> if (u.number != 13) u else
                    u.copy(sessions = u.sessions.map { session -> session.copy(steps = session.steps.map { step ->
                        if (step.kind == "TEACH") step.copy(body = step.body + " changed") else step }) }) })
                assertFalse(reviewAssignmentCurrent(changedTeaching, downstream))
                downstream.slots.indices.forEach { answer(store, downstream, it) }
                assertTrue(advance(store, 14)); assertTrue(14 in currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        }
        }
    }

    @Test fun historicalUnit13BothVersionsAndOwner12BridgesKeepExactSavedIdentities() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(pack, 13)) {
        for (path in listOf(v1Capture, v2Capture)) {
            val original = document(path);val task = original.reviewAssignments.single { it.targetUnit == 13 }
            assertEquals(if (path == v1Capture) 1 else 6, task.schema)
            assertTrue(reviewAssignmentCurrent(pack, task))
            LocalStore.openInMemory(context()).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                assertEquals(original, store.governedLearnerDocument())
                assertEquals(task, store.cumulativeReviewHistory().first.single { it.targetUnit == 13 })
                val bad = pack.copy(items = pack.items.map { if (it.id == task.slots[1].itemId) it.copy(feedback=it.feedback+" changed") else it })
                assertFalse(reviewAssignmentCurrent(bad, task))
            }
        }
        }
    }

    @Test fun wrongSchemasResponseTargetAndEpochCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertEquals(15, task.schema)
            for (schema in (1..14) + 16) {
                val bad = task.copy(schema = schema).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse("schema $schema", reviewAssignmentCurrent(pack, bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            for (bad in listOf(task.copy(targetUnit = 6), task.copy(responseRuleDigest = "0".repeat(64)), task.copy(policyDigest = "0".repeat(64)))) {
                val signed = bad.copy(digest = reviewAssignmentDigest(bad))
                assertFalse(reviewAssignmentCurrent(pack, signed))
            }
            val unit9 = store.cumulativeReviewHistory().first.single { it.targetUnit == 9 }
            val wrongUnit9Schema = unit9.copy(schema = 15).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(pack, wrongUnit9Schema))
            assertNotNull(reviewHistoryDefects(listOf(wrongUnit9Schema.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            val epoch = store.learnerMutationEpoch()
            val before = store.governedLearnerDocument()
            store.openLearnerReplacement()
            try {
                assertNull(store.openCumulativeReview(pack, 13, epoch))
                assertFalse(store.answerCumulativeReview(pack, task.id, 0, pack.item(task.slots[0].itemId)!!.answer, epoch))
                assertEquals(before, store.governedLearnerDocument())
            } finally { store.closeLearnerReplacement() }
        }
    }

    @Test fun missingOwnersAndChangedTeachingOrItemsRefuseFreshAndSavedTasks() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            val learner = store.v2LearnerView()
            for (owner in listOf(4, 12, 11)) {
                assertNull(createCumulativeReviewAssignment(pack, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - owner), 13, "missing-$owner", 0))
                val slot = task.slots.single { it.ownerUnit == owner }
                val changed = pack.copy(items = pack.items.map { if (it.id == slot.itemId) it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changed, task))
                val stepId = pack.unit(owner)!!.objectives.single { it.id == slot.objectiveId }.instructionStepIds.last()
                val teaching = pack.copy(units = pack.units.map { u -> if (u.number != owner) u else u.copy(sessions = u.sessions.map { session ->
                    session.copy(steps = session.steps.map { if (it.id == stepId) it.copy(body = it.body + " changed") else it })
                }) })
                assertFalse(reviewAssignmentCurrent(teaching, task))
            }
            assertNull(createCumulativeReviewAssignment(pack, learner.copy(governedStepDigests = emptyMap()), 13, "missing-teaching", 0))
        }
    }

    @Test fun recentPresentMissRemainsAssignedThroughCorrectionRollbackBackupAndReopen() {
        val context = context(); val name = "unit13-gender-miss-${System.nanoTime()}"; val stage = File(context.cacheDir, name)
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store)
                val item = pack.item("u4-i-verb-писать-вы")!!
                val step = pack.unit(4)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && item.id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "gender-miss")!!)
                val task = open(store)
                assertEquals(item.id, task.slots[0].itemId)
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, step.id, "gender-corrected")!!)
                assertEquals(task, open(store))
                assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
                task.slots.indices.forEach { answer(store, task, it) }
                val before = store.governedLearnerDocument()
                answer(store, task, 0)
                assertEquals(before, store.governedLearnerDocument())
                store.failAdvanceAfterCompletionForTests = true
                assertTrue(runCatching { advance(store, 13) }.isFailure)
                assertEquals(before, store.governedLearnerDocument())
                assertTrue(advance(store, 13))
                val document = store.governedLearnerDocument()
                val decoded = readGovernedBackup(archive(store).inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((decoded as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(document, target.governedLearnerDocument())
                    assertEquals(task, open(target))
                    assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals("u4-i-verb-писать-вы", open(store).slots[0].itemId)
                assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively(); context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
    }

    @Test fun genuineV2PartialCompletesWithoutRelabellingAndHistoricalV1CanRefreshPrerequisites() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(pack, 13)) {
        for (path in listOf(v2Capture, v1Capture)) {
            val original = document(path); val saved = original.reviewAssignments.single { it.targetUnit == 13 }
            val context = context(); val name = "unit13-genuine-${saved.schema}-${System.nanoTime()}"
            try {
                LocalStore.openNamed(context, name).use { store ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                    assertEquals(original, store.governedLearnerDocument())
                    if (path == v1Capture) prepare(store) // actual fresh work; never raw-flag old prerequisites
                    assertEquals(saved, open(store))
                    assertEquals(setOf(0), completedReviewSlots(pack, saved, store.v2LearnerView().reviewAnswers))
                    val relabelled = saved.copy(schema = 15).let { it.copy(digest = reviewAssignmentDigest(it)) }
                    assertFalse(reviewAssignmentCurrent(pack, relabelled))
                    saved.slots.indices.drop(1).forEach { answer(store, saved, it) }
                    assertTrue(advance(store, 13))
                    val current = store.governedLearnerDocument()
                    assertEquals(saved, current.reviewAssignments.single { it.id == saved.id })
                    assertEquals(original.reviewAnswers, current.reviewAnswers.take(original.reviewAnswers.size))
                    assertEquals(saved.policyDigest, current.unitCompletions.single { it.unitNumber == 13 }.reviewPolicyDigest)
                    assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                }
                LocalStore.openNamed(context, name).use { store ->
                    assertEquals(saved, open(store)); assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                }
            } finally { context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
        }
        }
    }

    @Test fun allEightCandidatesAllocateFromGovernedMissesWhilePracticeMalformedAndDuplicateOrdersCannotSelect() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val policy = Unit13PresentCumulativeReviewPolicy.definition
            assertEquals(listOf(4,2,2), policy.pool.map { it.candidateItemIds.size })
            for ((slot, spec) in policy.pool.withIndex()) {
                val ids = spec.candidateItemIds
                val viewBefore = store.v2LearnerView()
                assertEquals(ids.first(), createCumulativeReviewAssignment(pack, viewBefore, 13, "least-$slot", 0)!!.slots[slot].itemId)
                for (id in ids) {
                    recordMiss(store,id)
                    val task = createCumulativeReviewAssignment(pack, store.v2LearnerView(),13,"miss-$id",0)!!
                    assertEquals(id, task.slots[slot].itemId)
                    val item=pack.item(id)!!;val presented=reviewPresentedOptions(item,task,slot)
                    assertEquals(item.options.toSet(),presented.toSet());assertEquals(item.options.size,presented.size)
                    assertEquals(presented,reviewPresentedOptions(item.copy(answer=item.options.first { it!=item.answer }),task,slot))
                }
                val item=pack.item(ids.first())!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,item.id,item.options.first { it!=item.answer },OPTIONAL_PRACTICE,practiceStepId("mixed"),"optional")!!)
                val view=store.v2LearnerView();val candidates=ids.map { pack.item(it)!! }
                assertEquals(ids.last(),createCumulativeReviewAssignment(pack,view,13,"ignore-optional-$slot",0)!!.slots[slot].itemId)
                assertEquals(ids.first(),recentReviewCandidate(pack,view.copy(governedAttempts=view.governedAttempts!!.map { it.copy(itemDigest="0".repeat(64)) }),candidates)!!.id)
                assertEquals(ids.first(),recentReviewCandidate(pack,view.copy(governedAttempts=view.governedAttempts!!.map { it.copy(acceptedOrder=1) }),candidates)!!.id)
            }
        }
    }
    private fun recordMiss(store: LocalStore, id: String) {
        val item=pack.item(id)!!;val step=pack.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.kind=="CHECK" && id in it.itemIds }
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,id,item.options.first { it!=item.answer },PRESCRIBED_CHECK,step.id,"unit13-miss")!!)
    }
}
