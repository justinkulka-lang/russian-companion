package dev.russiancompanion.domain.gate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GateSchemaTest {
    @Test
    fun shippedDefaultGateIsTheConjunctionOfBookAnkiAndPrescribedWork() {
        val rule = defaultAdvancementRule()
        assertEquals(
            listOf(
                GateEvidenceKind.BOOK_REVISION_SCORE,
                GateEvidenceKind.ANKI_DROID_STATUS,
                GateEvidenceKind.PRESCRIBED_WORK_COMPLETION,
            ),
            rule.map { it.kind },
        )
        assertEquals(GateComparison.AT_LEAST, rule[0].comparison)
        assertEquals(DEFAULT_BOOK_REVISION_MINIMUM, rule[0].operand)
        assertEquals(GateComparison.EQUALS, rule[1].comparison)
        assertEquals(AnkiStatusValue.MATURE.name, rule[1].operand)
        assertEquals(GateComparison.PRESENT, rule[2].comparison)
        assertNull(rule[2].operand)
        assertTrue(rule.none { it.kind == GateEvidenceKind.TUTOR_SIGN_OFF })
    }

    @Test
    fun admissionRequiresTheDefaultKindsAndRejectsTutorOrOpenAnkiText() {
        assertTrue(admit(defaultDraft()).accepted)
        assertEquals("tutor-not-shipped", admit(defaultDraft(includeTutor = true)).reason)
        assertEquals(
            "default-kinds-required",
            admit(
                draft(
                    GateConditionDraft("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                    GateConditionDraft("c-anki", "ANKI_DROID_STATUS", "EQUALS", "MATURE", 1),
                ),
            ).reason,
        )
        assertEquals(
            "anki-status-required",
            admit(
                draft(
                    GateConditionDraft("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                    GateConditionDraft("c-anki", "ANKI_DROID_STATUS", "EQUALS", "mature", 1),
                    GateConditionDraft("c-work", "PRESCRIBED_WORK_COMPLETION", "PRESENT", null, 2),
                ),
            ).reason,
        )
        assertEquals("missing-segment", admitGateDefinition(defaultDraft(), segmentExists = false).reason)
        assertEquals("identity", admitGateDefinition(defaultDraft().copy(id = ""), segmentExists = true).reason)
        assertEquals("identity", admitGateDefinition(defaultDraft().copy(id = "gate\t1"), segmentExists = true).reason)
        assertEquals("identity-token", admit(defaultDraft().copy(conditions = defaultDraft().conditions.map { it.copy(id = if (it.id == "c-book") "c-book,1" else it.id) })).reason)
        assertEquals("rule-text-mismatch", admit(defaultDraft().copy(displayedRuleText = "Displayed test rule")).reason)
        assertEquals("rule-blank", admit(defaultDraft().copy(ruleVersion = "1\n2")).reason)
    }

    private fun admit(draft: GateDefinitionDraft): GateAdmission {
        return admitGateDefinition(draft, segmentExists = true)
    }

    private fun defaultDraft(includeTutor: Boolean = false): GateDefinitionDraft {
        val conditions = defaultAdvancementRule().mapIndexed { index, condition ->
            GateConditionDraft(
                id = condition.id,
                evidenceType = condition.kind.name,
                operator = condition.comparison.name,
                operand = condition.operand,
                sortOrder = index,
            )
        }
        return draft(
            *(if (includeTutor) {
                conditions + GateConditionDraft("c-tutor", "TUTOR_SIGN_OFF", "PRESENT", null, 3)
            } else {
                conditions
            }).toTypedArray(),
        )
    }

    private fun draft(vararg conditions: GateConditionDraft): GateDefinitionDraft {
        return GateDefinitionDraft(
            id = "gate-1",
            segmentId = "segment-1",
            ruleVersion = "1",
            displayedRuleText = displayedRuleText(defaultAdvancementRule()),
            conditions = conditions.toList(),
        )
    }
}
