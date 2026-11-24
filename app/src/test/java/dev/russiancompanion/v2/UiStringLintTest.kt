package dev.russiancompanion.v2

import dev.russiancompanion.v2.UiLiteralScanner.Literal
import dev.russiancompanion.v2.UiLiteralScanner.Role
import dev.russiancompanion.v2.curriculum.V2DesignDiagnostics
import dev.russiancompanion.v2.ui.V2ReviewStatusLabels
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * T16d (owner decision D32b): every hardcoded string literal a learner can see passes the learner-voice lint (governance
 * terms, provenance and widened narration, table-cell markup), as the pack's learner fields do. Template expressions inside a
 * literal are replaced by a neutral word before the lint runs.
 *
 * T16c-n2 (owner decision D37a, approved rule change), replacing the T16d scope of two directories and two exemptions:
 * - Scope: every main-source file that produces learner-visible strings. That is the UI layer (v2/ui, presentation), every
 *   other main file whose declarations the UI layer uses, and the string resources. A file the UI uses that renders no text
 *   is listed in [NON_LEARNER_FILES] with its reason; a newly used file fails [theScopeIsEveryFileTheUiUses] until it is
 *   classified.
 * - No whole-file exemption (was: V2ReviewStatusLabels.kt). Only the D31b label strings themselves are exempt.
 * - An identifier-like literal (test tag, key, route, enum name) is exempt only where it is used as an identifier; as an
 *   argument of Text() or any other rendering call it is reported, whatever its wording.
 * - Raw (triple-quoted) literals and literals nested in template expressions are extracted (UiLiteralScanner).
 * - Developer diagnostics (error lists, require/check/error messages) are not learner text.
 */
class UiStringLintTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val main = File(root, "app/src/main/java/dev/russiancompanion")
    private fun rel(file: File) = file.relativeTo(main).invariantSeparatorsPath
    private val mainFiles: List<File> by lazy { main.walkTopDown().filter { it.extension == "kt" }.sortedBy { rel(it) }.toList() }
    private val uiLayer: List<File> by lazy { mainFiles.filter { rel(it).startsWith("v2/ui/") || rel(it).startsWith("presentation/") } }

    /** Files the UI layer uses that put no literal of their own on screen, with the reason. */
    private val NON_LEARNER_FILES = mapOf(
        "RussianCompanionApplication.kt" to "application class; no text",
        "data/appearance/AppearanceStore.kt" to "preference storage; stored keys only",
        "data/local/LocalStore.kt" to "local storage; table and key names only",
        "data/local/StoredValues.kt" to "stored enum values only",
        "data/local/UserEntities.kt" to "database entities; column names only",
        "domain/appearance/AppearancePreference.kt" to "stored preference identifiers only",
        "domain/practice/PracticeSession.kt" to "audio identifiers only",
        "domain/progress/ProgressSchema.kt" to "export counts; schema identifiers only",
        "v2/audio/V2ClipSession.kt" to "clip playback state; no text",
        "v2/backup/V2BackupEngine.kt" to "backup engine; the messages it returns are in V2BackupFormat.kt",
        "v2/curriculum/EvidenceSchema.kt" to "the UI uses only the CLOSED_REVIEW_STATES identifier set; its literals are status identifiers and defect descriptions",
        "v2/curriculum/V2CompletionEvidence.kt" to "completion arithmetic; no text",
        "v2/curriculum/V2CumulativeReview.kt" to "assignment arithmetic; the review copy comes from the pack",
        "v2/curriculum/V2Models.kt" to "data classes; status identifiers only",
        "v2/curriculum/V2OptionPresentation.kt" to "option ordering; no text",
        "v2/curriculum/V2PackLoader.kt" to "pack parsing; its require() messages are diagnostics",
        "v2/curriculum/V2SpacedReview.kt" to "scheduling; its rejection reasons are diagnostics, not shown",
        "v2/data/V2Entities.kt" to "database entities; column names only",
    )
    /** Declared names that also occur as ordinary words in UI code, so they do not show that a file is used. */
    private val GENERIC_NAMES = setOf("main", "interface", "compare", "indexOf")
    private val declaration = Regex(
        "^(?:internal |private |public |@\\w+ )*(?:data |enum |sealed |abstract |open |const |fun )*(?:fun|val|var|class|object|interface)\\s+" +
            "(?:<[^>]*>\\s*)?(?:[\\w.]+\\.)?(\\w+)",
        RegexOption.MULTILINE,
    )

    private fun usedByUi(): List<File> {
        val uiText = uiLayer.joinToString("\n") { it.readText() }
        return mainFiles.filter { it !in uiLayer }.filter { file ->
            declaration.findAll(file.readText()).map { it.groupValues[1] }.toSet()
                .any { it.length > 2 && it !in GENERIC_NAMES && Regex("\\b${Regex.escape(it)}\\b").containsMatchIn(uiText) }
        }
    }
    private val learnerFiles: List<File> by lazy { uiLayer + usedByUi().filter { rel(it) !in NON_LEARNER_FILES } }

    /** D31b: the plain review-status labels are owner wording; these exact strings (and nothing else in the file) are exempt. */
    private val d31bLabels: Set<String> by lazy { V2ReviewStatusLabels.LABELS.values.toSet() + V2ReviewStatusLabels.NOT_YET_CHECKED }
    /**
     * V2AnswerFeedback's nonExplanatoryFragments are matched against pack rationales to detect a rationale that does not
     * explain; they are never shown. Exempt by that use, by exact text.
     */
    private val matcherFragments: Set<String> by lazy {
        val source = File(main, "v2/ui/V2AnswerFeedback.kt").readText()
        val block = source.substringAfter("val nonExplanatoryFragments = listOf(").substringBefore("\n)")
        UiLiteralScanner.literals(block).map { it.text }.toSet()
    }

    private fun identifierLike(text: String) = text.none(Char::isWhitespace) &&
        (text.any { it == '-' || it == '_' || it == '$' } || text.all { !it.isLetter() || it.isUpperCase() })
    private val nonRenderingCalls = setOf(
        "Suppress", "Regex", "CreateDocument", "CancellationException", "IllegalArgumentException", "IllegalStateException",
        "V2LabRoute", "File", "Locale", "Uri", "Intent", "Thread",
    )
    private val diagnosticCalls = setOf("error", "require", "requireNotNull", "check", "checkNotNull", "IllegalArgumentException",
        "IllegalStateException", "CancellationException", "assert")
    private fun renderingCall(call: String?) = call != null && call.first().isUpperCase() && call !in nonRenderingCalls

    /** Parameters that carry an identifier (a test tag, a record id, a key or a route), never shown as text. */
    private val identifierParameters = setOf("tag", "testTag", "id", "key", "route", "kind", "blockedReason",
        "destinationReference", "documentReference", "PackagedNoticeFile.name", "V2TypedResponseInput.context")
    /** Parameter names of the main-source functions and classes, by name and position, from their declarations. */
    private val parameters: Map<String, List<String>> by lazy {
        val out = mutableMapOf<String, List<String>>()
        mainFiles.forEach { file ->
            val text = file.readText()
            Regex("(?:fun|class)\\s+(?:<[^>]*>\\s*)?(\\w+)\\s*\\(").findAll(text).forEach { match ->
                var depth = 1; var i = match.range.last + 1; val start = i
                while (i < text.length && depth > 0) { if (text[i] == '(') depth++; if (text[i] == ')') depth--; i++ }
                val names = splitTopLevel(text.substring(start, i - 1)).mapNotNull { param ->
                    Regex("(\\w+)\\s*:").find(param.replace(Regex("@\\w+(?:\\([^)]*\\))?"), ""))?.groupValues?.get(1)
                }
                out.putIfAbsent(match.groupValues[1], names)
            }
        }
        out
    }
    private fun splitTopLevel(params: String): List<String> {
        val parts = mutableListOf<String>(); var depth = 0; val sb = StringBuilder()
        params.forEach { c ->
            if (c in "(<[{") depth++; if (c in ")>]}") depth--
            if (c == ',' && depth == 0) { parts += sb.toString(); sb.clear() } else sb.append(c)
        }
        if (sb.isNotBlank()) parts += sb.toString()
        return parts
    }
    /** The parameter the literal binds to: its name if named, else the declared name at its position (null if unknown). */
    private fun boundParameter(literal: Literal): String? =
        literal.named ?: literal.call?.let { parameters[it]?.getOrNull(literal.argument) }

    /** The lint result for one literal in context, or null when it is not learner text or passes. */
    private fun verdict(source: String, literal: Literal): List<String>? {
        val text = literal.text
        if (text in d31bLabels) return null
        if (literal.call in diagnosticCalls || literal.call?.endsWith("Exception") == true) return null
        if (diagnosticTarget(source, literal)) return null
        if (identifierLike(text)) {
            // D37a: an identifier is exempt only where it is used as one, never when it is rendered.
            if (literal.role in setOf(Role.WHEN_LABEL, Role.COMPARISON, Role.MAP_KEY)) return null
            val bound = boundParameter(literal)
            val identifierUse = bound in identifierParameters || "${literal.call}.$bound" in identifierParameters
            return if (renderingCall(literal.call) && !identifierUse) listOf("RAW_IDENTIFIER_RENDERED") else null
        }
        val rules = V2DesignDiagnostics.learnerVoiceRules(neutral(text))
        return rules.ifEmpty { null }
    }

    /** `errors += "..."`-style defect lists are diagnostics. */
    private fun diagnosticTarget(source: String, literal: Literal): Boolean {
        val line = source.lines().getOrNull(literal.line - 1) ?: return false
        return Regex("\\b(?:errors|missing|defects|problems)\\s*\\+=\\s*\"").containsMatchIn(line) &&
            line.substringAfter("+=").contains(literal.text.take(12).substringBefore("\${}"))
    }

    private fun hits(name: String, source: String, exemptFragments: Set<String> = emptySet()): List<String> =
        UiLiteralScanner.literals(source).filter { it.text !in exemptFragments }.mapNotNull { literal ->
            verdict(source, literal)?.let { "$name:${literal.line} $it \"${literal.text}\"" }
        }

    private fun stringResources(): List<Pair<String, String>> =
        File(root, "app/src/main/res").walkTopDown().filter { it.name == "strings.xml" }.flatMap { file ->
            Regex("<string name=\"([^\"]+)\"[^>]*>(.*?)</string>", RegexOption.DOT_MATCHES_ALL).findAll(file.readText())
                .map { it.groupValues[1] to it.groupValues[2].replace("\\'", "'").replace(Regex("%\\d\\$[sd]"), "value") }
        }.toList()

    @Test fun everyLearnerVisibleHardcodedStringPassesTheLearnerLint() {
        val fileHits = learnerFiles.flatMap { file ->
            val fragments = if (file.name == "V2AnswerFeedback.kt") matcherFragments else emptySet()
            hits(rel(file), file.readText(), fragments)
        }
        val resourceHits = stringResources().mapNotNull { (name, value) ->
            V2DesignDiagnostics.learnerVoiceRules(value).ifEmpty { null }?.let { "strings.xml $name $it \"$value\"" }
        }
        val all = fileHits + resourceHits
        assertTrue(all.joinToString("\n"), all.isEmpty())
        assertTrue(learnerFiles.sumOf { UiLiteralScanner.literals(it.readText()).size } > 300)
    }

    @Test fun theScopeIsEveryFileTheUiUses() {
        val used = usedByUi().map(::rel).toSet()
        // Every listed non-learner file is one the UI uses (no stale entry), and every used file is either linted or listed.
        assertTrue((NON_LEARNER_FILES.keys - used).toString(), (NON_LEARNER_FILES.keys - used).isEmpty())
        val linted = learnerFiles.map(::rel).toSet()
        assertTrue(used.all { it in linted || it in NON_LEARNER_FILES })
        // The files whose literals D37a names are in scope.
        for (path in listOf("v2/ui/V2ReviewStatusLabels.kt", "v2/curriculum/V2ProgressRules.kt", "v2/curriculum/V2Coursework.kt",
                "v2/backup/V2BackupFormat.kt", "v2/curriculum/V2Gradebook.kt", "v2/curriculum/V2UnitTest.kt",
                "v2/notices/V2OfflineNotices.kt", "v2/curriculum/V2GovernedIdentity.kt")) assertTrue(path, path in linted)
    }

    @Test fun theScanCatchesRenderedIdentifiersNestedLiteralsAndRawStrings() {
        // D37a regressions: a rendered identifier, a rendered status enum, and a workflow sentence nested in a template.
        val probe = "Text(\"review-pending\")\nText(\"APPROVED\")\n" +
            "Text(\"Status: \${if (blocked) \"Release stays blocked.\" else \"Ready\"}\")\n" +
            "Text(\"\"\"Choose the requested form.\"\"\")\n" +
            "Modifier.semantics { testTag = \"v2-review-pending\" }\nwhen (s) { \"APPROVED\" -> 1 }\nText(\"Not quite. Try again.\")"
        val found = hits("probe", probe)
        assertTrue(found.joinToString("\n"), found.any { "RAW_IDENTIFIER_RENDERED] \"review-pending\"" in it })
        assertTrue(found.joinToString("\n"), found.any { "RAW_IDENTIFIER_RENDERED] \"APPROVED\"" in it })
        assertTrue(found.joinToString("\n"), found.any { "\"Release stays blocked.\"" in it })
        assertTrue(found.joinToString("\n"), found.any { "\"Choose the requested form.\"" in it })
        // The identifier uses (a test tag, a when label) and plain feedback pass.
        assertEquals(found.joinToString("\n"), 4, found.size)
        // The D31b labels pass only as those exact strings: the same wording elsewhere in a status sentence is still linted.
        assertTrue(hits("probe", "Text(\"Status: approved by the reviewer.\")").isNotEmpty())
        // "Not the reviewed answer." (replaced by "Not quite. Try again.", D32b) must not come back.
        assertTrue(learnerFiles.none { "Not the reviewed answer" in it.readText() })
    }

    /** D37a/D37b: the step status line shows a plain kind label for every step kind in the pack, never the raw value. */
    @Test fun theStepStatusLineNamesEveryKindInPlainWords() {
        assertEquals("Step 2 of 5 · Teaching", dev.russiancompanion.v2.ui.stepStatus(1, 5, "TEACH"))
        assertEquals("Step 1 of 3", dev.russiancompanion.v2.ui.stepStatus(0, 3, "UNKNOWN_KIND"))
        val pack = dev.russiancompanion.v2.curriculum.V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
        pack.units.flatMap { it.sessions }.flatMap { it.steps }.map { it.kind }.toSet().forEach { kind ->
            val line = dev.russiancompanion.v2.ui.stepStatus(0, 1, kind)
            assertTrue(kind, " · " in line && kind !in line && V2DesignDiagnostics.learnerVoiceRules(line).isEmpty())
        }
    }

    private fun neutral(literal: String): String =
        literal.replace("\${}", "value").replace(Regex("\\$[A-Za-z_][A-Za-z0-9_.]*"), "value")
}
