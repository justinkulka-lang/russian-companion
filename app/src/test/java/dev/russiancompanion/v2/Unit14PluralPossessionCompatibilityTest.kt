package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.root
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.pack
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.before14
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.context
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.prepare
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.open
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.answer
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.advance
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.ordinary
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.archive
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.miss
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.deleteStore
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit14PluralPossessionCompatibilityTest {
    @Test fun exactPriorDeclarationRefusesNewSchemaAndCompiledObservationRecordsBoundedPools() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(pack, 14)) {
        val old = before14
        val restored = pack.copy(units = pack.units.map { u -> if (u.number != 14) u else
            u.copy(advancement = u.advancement!!.copy(cumulativeReviewPolicyId = Unit14CumulativeReviewPolicy.definition.id)) })
        assertEquals("The entire actual baseline differs only by the Unit14 declaration", old, restored)
        val policies = (3..30).map { cumulativeReviewPolicy(it)!! }
        assertEquals(28, policies.size); assertEquals(60, policies.sumOf { it.pool.size })
        assertEquals(121, policies.sumOf { p -> p.pool.sumOf { it.candidateItemIds.size } })
        assertEquals(57, policies.flatMap { it.pool }.flatMap { it.candidateItemIds }.distinct().size)
        assertEquals(18, policies.count { p -> p.pool.any { it.candidateItemIds == listOf("u1-i-decode-pair") } })
        val policy = Unit14PluralPossessionCumulativeReviewPolicy.definition
        assertEquals(listOf(6,4,1), policy.pool.map { it.candidateItemIds.size })
        assertEquals(Unit9CumulativeReviewPolicy.definition.pool[1].copy(candidateItemIds=(1..6).map { "u8-i-plural-$it" }), policy.pool[0])
        assertEquals(Unit11NounCumulativeReviewPolicy.definition.pool[1], policy.pool[1])
        assertEquals(Unit14CumulativeReviewPolicy.definition.pool[1], policy.pool[2])
        LocalStore.openInMemory(Unit14PluralPossessionFixtures.context()).use { store ->
            Unit14PluralPossessionFixtures.prepare(store)
            val task = Unit14PluralPossessionFixtures.open(store)
            assertEquals(1, task.schema)
            assertTrue(reviewAssignmentCurrent(Unit14PluralPossessionFixtures.pack, task))
            assertFalse(reviewAssignmentCurrent(old, task))
            assertFalse(reviewAssignmentEligible(old, store.v2LearnerView(), task))
            assertEquals(listOf(8, 10, 13), task.slots.map { it.ownerUnit })
        }
        val observation=buildJsonObject {
            put("repositorySourceDigest",computeReleaseSourceDigest(root))
            put("packContentReviewDigest",V2Validator.contentReviewDigest(pack))
            put("unit14BeforeV1Digest",V2Validator.unitContentReviewDigest(old,14))
            put("unit14CurrentDigest",V2Validator.unitContentReviewDigest(pack,14))
            put("activePolicies",policies.size);put("pools",60);put("placements",121)
            put("uniqueCandidates",57);put("printSingletonPolicies",18)
            put("policies",buildJsonArray { policies.forEach { p -> add(buildJsonObject {
                put("unit",p.targetUnit);put("id",p.id);put("digest",p.digest)
                put("pools",buildJsonArray { p.pool.forEach { o -> add(buildJsonObject {
                    put("objective",o.objectiveId);put("skill",o.skill.name)
                    put("candidates",buildJsonArray { o.candidateItemIds.forEach { add(it) } })
                }) } })
            }) } })
        }
        println("UNIT14_COMPILED_OBSERVATION $observation")
        System.getProperty("audit.unit14.observation")?.let { path ->
            require(path.startsWith("/workspace/artifacts/"));check(!File(path).exists())
            File(path).writeText(observation.toString()+"\n")
        }
        }
    }





    @Test fun eachMissingOwnerOrChangedItemAndInstructionRefusesFreshAndSavedTasks() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store);val task=open(store);val learner=store.v2LearnerView()
            for(owner in listOf(8,10,13)) {
                assertNull(createCumulativeReviewAssignment(pack,learner.copy(completedUnitNumbers=learner.completedUnitNumbers-owner),14,"missing-$owner",0))
                val slot=task.slots.single { it.ownerUnit==owner }
                val bad=pack.copy(items=pack.items.map { if(it.id==slot.itemId) it.copy(feedback=it.feedback+" changed") else it })
                assertFalse(reviewAssignmentCurrent(bad,task))
                val sid=pack.unit(owner)!!.objectives.single { it.id==slot.objectiveId }.instructionStepIds.last()
                val teaching=pack.copy(units=pack.units.map { u -> if(u.number!=owner) u else u.copy(sessions=u.sessions.map { s ->
                    s.copy(steps=s.steps.map { if(it.id==sid) it.copy(body=it.body+" changed") else it }) }) })
                assertFalse(reviewAssignmentCurrent(teaching,task))
            }
            assertNull(createCumulativeReviewAssignment(pack,learner.copy(governedStepDigests=emptyMap()),14,"missing-instruction",0))
        }
    }

    @Test fun wrongSchemasTargetsResponseRolesAndReplacementEpochCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store);val task=open(store);assertEquals(1, task.schema)
            for (schema in 2..17) {
                val bad=task.copy(schema=schema).let { it.copy(digest=reviewAssignmentDigest(it)) }
                assertFalse("schema $schema",reviewAssignmentCurrent(pack,bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary=0).let { it.copy(digest=reviewAssignmentDigest(it)) }),emptyList()))
            }
            for(bad in listOf(task.copy(targetUnit=13),task.copy(responseRuleDigest="0".repeat(64)),task.copy(policyDigest="0".repeat(64))))
                assertFalse(reviewAssignmentCurrent(pack,bad.copy(digest=reviewAssignmentDigest(bad))))
            answer(store,task,0)
            val row=store.v2LearnerView().reviewAnswers.last()
            for(bad in listOf(row.copy(role=OPTIONAL_PRACTICE),row.copy(schema=16),row.copy(assignmentDigest="0".repeat(64)),row.copy(acceptedOrder=task.boundary)))
                assertTrue(completedReviewSlots(pack,task,listOf(bad)).isEmpty())
            assertTrue(completedReviewSlots(pack,task,listOf(row,row.copy(id="duplicate"))).isEmpty())
            val before=store.governedLearnerDocument();val epoch=store.learnerMutationEpoch()
            store.openLearnerReplacement()
            try {
                assertNull(store.openCumulativeReview(pack,14,epoch))
                assertFalse(store.answerCumulativeReview(pack,task.id,1,pack.item(task.slots[1].itemId)!!.answer,epoch))
                assertEquals(before,store.governedLearnerDocument())
            } finally { store.closeLearnerReplacement() }
            assertFalse(store.answerCumulativeReview(pack,task.id,1,pack.item(task.slots[1].itemId)!!.answer,epoch))
            assertEquals(before,store.governedLearnerDocument())
        }
    }

    @Test fun assignedMissSurvivesOrdinaryCorrectionIdempotenceRollbackBackupReopenAndLateAnswer() {
        val context=context();val name="unit14-current-${System.nanoTime()}.db";val stage=File(context.cacheDir,name)
        try {
            LocalStore.openNamed(context,name).use { store ->
                prepare(store);miss(store,"u8-i-plural-6");miss(store,"u10-i-have-4")
                val task=open(store);assertEquals(listOf("u8-i-plural-6","u10-i-have-4","u13-i-report-frame-time"),task.slots.map { it.itemId })
                ordinary(store,8);ordinary(store,10)
                assertEquals(task,open(store));assertTrue(completedReviewSlots(pack,task,store.v2LearnerView().reviewAnswers).isEmpty())
                task.slots.indices.forEach { answer(store,task,it) }
                val before=store.governedLearnerDocument();answer(store,task,0);assertEquals(before,store.governedLearnerDocument())
                store.failAdvanceAfterCompletionForTests=true
                assertTrue(runCatching { advance(store) }.isFailure);assertEquals(before,store.governedLearnerDocument())
                assertTrue(advance(store));assertEquals(15,store.v2Profile().currentUnitNumber)
                answer(store,task,2,correct=false)
                assertFalse(cumulativeReviewComplete(pack,store.v2LearnerView(),14))
                assertTrue(14 in currentCompletedUnitNumbers(pack,store.v2LearnerView()))
                val document=store.governedLearnerDocument()
                val read=readGovernedBackup(archive(store).inputStream(),"dev.russiancompanion",pack.packId,pack.packVersion,stage) as BackupReadResult.Valid
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED,target.replaceGovernedLearner(read.backup.document,emptyList()))
                    assertEquals(document,target.governedLearnerDocument());assertEquals(task,open(target))
                    assertTrue(14 in currentCompletedUnitNumbers(pack,target.v2LearnerView()))
                }
            }
            LocalStore.openNamed(context,name).use { store ->
                assertEquals("u8-i-plural-6",open(store).slots[0].itemId)
                assertEquals((1..14).toSet(),currentCompletedUnitNumbers(pack,store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively();deleteStore(context,name) }
    }

    @Test fun allTwentyFourAllocationsUseGovernedMissesIgnoringPracticeMalformedAndDuplicateOrders() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store);val policy=Unit14PluralPossessionCumulativeReviewPolicy.definition
            val seen=mutableSetOf<List<String>>()
            for(plural in policy.pool[0].candidateItemIds) for(possession in policy.pool[1].candidateItemIds) {
                miss(store,plural);miss(store,possession)
                val task=createCumulativeReviewAssignment(pack,store.v2LearnerView(),14,"variant-$plural-$possession",0)!!
                assertEquals(listOf(plural,possession,"u13-i-report-frame-time"),task.slots.map { it.itemId })
                seen+=task.slots.map { it.itemId }
                task.slots.forEachIndexed { index,slot ->
                    val item=pack.item(slot.itemId)!!;val order=reviewPresentedOptions(item,task,index)
                    assertEquals(item.options.toSet(),order.toSet());assertEquals(item.options.size,order.size)
                    assertEquals(order,reviewPresentedOptions(item.copy(answer=item.options.first { it!=item.answer }),task,index))
                }
            }
            assertEquals(24,seen.size)
            for(spec in policy.pool) {
                val item=pack.item(spec.candidateItemIds.first())!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,item.id,item.options.first { it!=item.answer },OPTIONAL_PRACTICE,practiceStepId("mixed"),"optional-unit14")!!)
                val view=store.v2LearnerView();val candidates=spec.candidateItemIds.map { pack.item(it)!! }
                assertEquals(spec.candidateItemIds.last(),recentReviewCandidate(pack,view,candidates)!!.id)
                assertEquals(spec.candidateItemIds.first(),recentReviewCandidate(pack,view.copy(governedAttempts=view.governedAttempts!!.map { it.copy(itemDigest="0".repeat(64)) }),candidates)!!.id)
                assertEquals(spec.candidateItemIds.first(),recentReviewCandidate(pack,view.copy(governedAttempts=view.governedAttempts!!.map { it.copy(acceptedOrder=1) }),candidates)!!.id)
            }
            assertFalse(store.cumulativeReviewHistory().first.any { it.targetUnit==14 })
            println("UNIT14_ALLOCATION_VARIANTS count=${seen.size} variants=$seen; this is one JUnit method")
        }
    }
}
