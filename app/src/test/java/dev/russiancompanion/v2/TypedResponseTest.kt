package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Synthetic English keys only; the two single-letter alphabet fixtures are writing-system facts. */
class TypedResponseTest {
    private fun reviewed(key: V2TypedAnswerKey): V2TypedAnswerKey = key.copy(review = V2TypedKeyReview(
        "English fixture reviewer", RESEARCH_VERIFIED, typedAnswerKeyDigest(key)))

    private fun item(answers: List<String> = listOf("Hello", "Hi"), kind: String = "TYPED_FORM"): V2CheckItem =
        V2CheckItem("typed-fixture", kind, "en", "Type the taught response.", options = emptyList(),
            answer = answers.first(), feedback = "Compare with the taught response.", objectiveId = "obj-fixture",
            sourceId = "src-app-english-v2", reviewStatus = IMPLEMENTED_PENDING_FINAL_AUDIT,
            typedAnswerKey = reviewed(V2TypedAnswerKey("english-key", 1, answers)))

    private fun pack(): V2CurriculumPack {
        val root = listOf(File("."), File("..")).first { File(it, "app/src/main").exists() }
        return V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
    }

    @Test fun exactListedVariantsAndOnlyThePermittedNormalizationAreScored() {
        val i = item()
        assertTrue(typedItemDefects(i).isEmpty())
        for (answer in listOf("Hello", "hello", "Hi", "hi", "He\u0301llo")) assertEquals(answer, true, scoreCheckedResponse(i, answer))
        for (answer in listOf("HELLO", "hEllo", " Hello", "Hello ", "Hello!", "Hell o", "Hello\n", "Helo", "Hey"))
            assertEquals(answer, false, scoreCheckedResponse(i, answer))
        assertNull(scoreCheckedResponse(i, ""))
        assertNull(scoreCheckedResponse(i, "  "))
        assertEquals(true, scoreCheckedResponse(item(listOf("two words")), "Two words"))
        assertEquals(false, scoreCheckedResponse(item(listOf("two words")), "Two Words"))
        assertEquals(false, scoreCheckedResponse(item(listOf("two words")), "two  words"))
        assertEquals(false, scoreCheckedResponse(item(listOf("café")), "cafe"))
        assertEquals(false, scoreCheckedResponse(item(listOf("test")), "te\u0300st"))
    }

    @Test fun alphabetVariantsAndStressAreAllowedWithoutUnicodeDecompositionOrOtherCaseFolding() {
        val i = item(listOf("\u0451"))
        for (answer in listOf("\u0451", "\u0435", "\u0401", "\u0415", "\u0435\u0301"))
            assertEquals(true, scoreCheckedResponse(i, answer))
        assertEquals(false, scoreCheckedResponse(i, "\u0435\u0308"))
        assertEquals(false, scoreCheckedResponse(item(listOf("a\u0451")), "a\u0415"))
        assertEquals("(hello)", normalizeTypedResponse("(Hello)"))
        assertEquals("hello", normalizeTypedResponse(normalizeTypedResponse("He\u0301llo")))
        // Simple first-letter case applies to a full code point, not half a surrogate pair.
        val upper = String(Character.toChars(0x10400))
        val lower = String(Character.toChars(0x10428))
        assertEquals(lower, normalizeTypedResponse(upper))
    }

    @Test fun allClosedTypedKindsUseFiniteKeysAndUnverifiedDictationAndOpenWritingStayUnavailable() {
        for (kind in listOf("TYPED_FORM", "TYPED_TRANSFORMATION", "TYPED_TRANSLATION")) {
            assertEquals(true, scoreCheckedResponse(item(kind = kind), "Hi"))
            assertEquals(false, scoreCheckedResponse(item(kind = kind), "Other response"))
        }
        assertNull(scoreCheckedResponse(item(kind = "FREE_WRITING"), "Hi"))
        val dictation = item(kind = "TYPED_DICTATION").copy(dictationAudioAssetId = "unverified-catalog-id")
        assertNull(scoreCheckedResponse(dictation, "Hello"))
        assertTrue(typedItemDefects(dictation).any { it.contains("verified clip") })
        val choice = item().copy(kind = "LETTER_IDENTITY", options = listOf("Hello", "Other"), typedAnswerKey = null)
        assertEquals(true, scoreCheckedResponse(choice, "Hello"))
        assertEquals(false, scoreCheckedResponse(choice, "Other"))
        assertNull(scoreCheckedResponse(choice, "hello"))
        assertNull(scoreCheckedResponse(choice, "Not an option"))
    }

    @Test fun everyOtherUtf16CharacterIsPreservedAfterTheFirstLetter() {
        // The first letter is fixed; no later character may gain case folding,
        // decomposition, punctuation stripping, whitespace repair or fuzzy matching.
        for (code in 0..0xffff) {
            val expected = when (code) {
                0x0301 -> "q"
                0x0401 -> "q" + 0x0415.toChar()
                0x0451 -> "q" + 0x0435.toChar()
                else -> "q" + code.toChar()
            }
            assertEquals("Code point $code", expected, normalizeTypedResponse("q" + code.toChar()))
        }
    }

    @Test fun malformedUnreviewedChangedAndAmbiguousKeysFailClosed() {
        val i = item(); val k = i.typedAnswerKey!!
        val keys = listOf(k.copy(review = null), k.copy(id = ""), k.copy(version = 0), k.copy(schema = 2),
            k.copy(acceptedAnswers = emptyList()), k.copy(acceptedAnswers = listOf("Hello", "Hello")),
            k.copy(acceptedAnswers = listOf("Hello", " ")), k.copy(acceptedAnswers = listOf("Hello", "Changed")),
            k.copy(review = k.review!!.copy(reviewer = "")), k.copy(review = k.review.copy(status = "REJECTED")),
            k.copy(review = k.review.copy(reviewedDigest = "0".repeat(64))))
        for (key in keys) {
            val changed = i.copy(typedAnswerKey = key)
            assertTrue(typedItemDefects(changed).isNotEmpty())
            assertNull(scoreCheckedResponse(changed, "Hello"))
        }
        assertNull(scoreCheckedResponse(i.copy(typedAnswerKey = null), "Hello"))
        assertNull(scoreCheckedResponse(i.copy(answer = "Not listed"), "Hello"))
        assertNull(scoreCheckedResponse(i.copy(options = listOf("Hello")), "Hello"))
        assertNull(scoreCheckedResponse(i.copy(dictationAudioAssetId = "unexpected"), "Hello"))
        assertNull(scoreCheckedResponse(item(listOf("\u0301")), "\u0301"))
        assertTrue(typedItemDefects(i.copy(kind = "LETTER_IDENTITY")).isNotEmpty())
    }

    @Test fun schemaRoundTripsAndKeyIdentityVariantsAndVersionBindAllRelevantDigests() {
        val i = item(); val key = i.typedAnswerKey!!
        assertEquals(i, Json.decodeFromString(V2CheckItem.serializer(), Json.encodeToString(V2CheckItem.serializer(), i)))
        val p = pack(); val original = p.items.first()
        val candidate = i.copy(id = original.id, objectiveId = original.objectiveId)
        val base = p.copy(items = p.items.map { if (it.id == original.id) candidate else it })
        val number = p.units.first { u -> u.objectives.any { it.id == original.objectiveId } }.number
        for (changed in listOf(key.copy(id = "other-key"), key.copy(version = 2), key.copy(acceptedAnswers = listOf("Hello", "Greetings")))) {
            val replacement = candidate.copy(typedAnswerKey = reviewed(changed))
            val next = p.copy(items = p.items.map { if (it.id == original.id) replacement else it })
            assertNotEquals(typedAnswerKeyDigest(key), typedAnswerKeyDigest(changed))
            assertNotEquals(V2Validator.checkItemContentDigest(candidate), V2Validator.checkItemContentDigest(replacement))
            assertNotEquals(V2Validator.unitContentReviewDigest(base, number), V2Validator.unitContentReviewDigest(next, number))
            assertNotEquals(V2Validator.contentReviewDigest(base), V2Validator.contentReviewDigest(next))
        }
        // Review metadata is not content and cannot sign a changed key into validity.
        val metadataOnly = i.copy(typedAnswerKey = key.copy(review = key.review!!.copy(reviewer = "Another fixture reviewer")))
        assertEquals(V2Validator.checkItemContentDigest(i), V2Validator.checkItemContentDigest(metadataOnly))
        assertNotEquals(closedResponseRuleDigest(), typedResponseRuleDigest())
        assertNull(original.typedAnswerKey)
    }

    @Test fun packValidatorChecksKeysAndEveryAcceptedAlphabetVariantSourceBinding() {
        val p = pack()
        val objective = p.units.first().objectives.first().id
        val valid = item().copy(objectiveId = objective)
        val invalid = valid.copy(typedAnswerKey = valid.typedAnswerKey!!.copy(review = null))
        assertTrue(V2Validator.validate(p.copy(items = p.items + invalid)).errors.any { it.contains("unreviewed or changed") })
        val letter = p.letters.first()
        val sourced = item(listOf(letter.uppercase, letter.lowercase)).copy(objectiveId = objective, sourceId = letter.sourceId)
        assertFalse(V2Validator.validate(p.copy(items = p.items + sourced)).errors.any { it.contains("accepted answer has no admitted source binding") })
        val unbound = sourced.copy(sourceId = "src-app-english-v2")
        assertTrue(V2Validator.validate(p.copy(items = p.items + unbound)).errors.any { it.contains("accepted answer has no admitted source binding") })
        val noTypedErrors = V2Validator.validate(p.copy(items = p.items + valid)).errors.filter { it.contains("Typed item") }
        assertEquals(emptyList<String>(), noTypedErrors)
    }
}
