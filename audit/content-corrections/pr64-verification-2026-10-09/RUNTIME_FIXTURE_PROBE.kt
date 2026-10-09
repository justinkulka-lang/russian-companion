package dev.russiancompanion.v2.curriculum

import java.io.File

fun main() {
    val pack = V2PackLoader.parse(File("app/src/main/assets/content/v2-curriculum.json").readText())
    val unit = pack.unit(1)!!
    val session = pack.session("u1-s-iotated")!!
    val activeIds = session.steps.map { it.id }.toSet()
    val completed = unit.sessions.flatMap { it.steps }.filter { it.id !in activeIds }
    val attempts = mutableListOf<GovernedAttemptView>()
    fun seed(step: V2Step, correct: Boolean = true) {
        stepCompletionItemIds(pack, step.id).forEach { itemId ->
            val item = pack.item(itemId)!!
            val selected = if (correct) item.answer else item.options.first { it != item.answer }
            val write = prepareGovernedAttempt(pack, itemId, selected, PRESCRIBED_CHECK, step.id, "2026-10-02T12:00")!!
            attempts += GovernedAttemptView(write.id, write.itemId, write.selected, write.correct, write.recordedAtLocal,
                write.itemDigest, write.unitContentDigest, write.ruleDigest, write.identitySchema,
                write.attemptRole, write.objectiveId, write.unitNumber, write.stepId, attempts.size.toLong() + 1)
        }
    }
    completed.forEach { seed(it) }
    val digests = completed.associate { it.id to stepContentDigest(pack, it) }.toMutableMap()
    fun learner(index: Int) = V2LearnerView(
        onboardingCompleted = true, currentUnitNumber = 1, startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
        hasPenguin = false, hasAnki = false, hasBinder = false, hasTutor = false,
        dailyMinutes = 25, transliterationEnabled = false, currentSessionId = session.id, currentStepIndex = index,
        completedStepIds = digests.keys, correctItemIds = attempts.filter { it.correct }.map { it.itemId }.toSet(),
        bookAttestedUnits = emptySet(), completedUnitNumbers = emptySet(), governedAttempts = attempts.toList(),
        governedStepDigests = digests.toMap(), governedStepSchemas = digests.mapValues { STEP_IDENTITY_SCHEMA },
        resumeContentDigest = stepContentDigest(pack, session.steps[index]), resumeIdentitySchema = GOVERNED_IDENTITY_SCHEMA,
    )
    completed.forEach { check(stepReadyForGovernedCompletion(pack, learner(0), it.id)) }
    session.steps.forEachIndexed { index, step ->
        val target = continueTarget(pack, learner(index))
        check(target.sessionId == session.id && target.stepIndex == index && !target.unavailable) { target.toString() }
        seed(step, correct = index != session.steps.lastIndex)
        check(stepReadyForGovernedCompletion(pack, learner(index), step.id))
        digests[step.id] = stepContentDigest(pack, step)
        println("step=${step.id}: prepared governed answers and completion; Continue opens exact unfinished step")
    }
    check(continueTarget(pack, learner(session.steps.lastIndex)).opensUnitReview)
    println("PASS: initial fixture, guided route, wrong-answer completion, and advancement routing")
    println("Scope: production domain logic only; Compose, persistence, remediation UI, and Android compilation require CI")
}
