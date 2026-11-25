package dev.russiancompanion.v2

import dev.russiancompanion.v2.UiLiteralScanner.Literal
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
 *
 * T16c-n3 (owner decision D46f, approved rule change), replacing the call-context exemptions above:
 * - Escapes are decoded before the lint reads a literal (UiLiteralScanner).
 * - Every string literal in a UI-reachable file is scanned, whatever call it sits in. An identifier-like literal is reported
 *   (RAW_IDENTIFIER) like any other lint hit; the "immediate call is not a renderer", identifier-parameter, when/comparison/
 *   map-key, diagnostic-call and defect-list exemptions are gone.
 * - The only exemptions are the D31b label strings and the explicit allowlist ui-lint-allowlist.tsv: (file, exact literal,
 *   reason), one reason per entry. An entry never exempts a literal passed straight to a rendering call, and a stale or
 *   unneeded entry fails the test.
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
    private fun identifierLike(text: String) = text.none(Char::isWhitespace) &&
        (text.any { it == '-' || it == '_' || it == '$' } || text.all { !it.isLetter() || it.isUpperCase() })
    /**
     * A literal passed straight to a text renderer, or as a text parameter of any call, is shown; no allowlist entry exempts it
     * there (an allowlisted identifier rendered by Text() is still reported). This only removes exemptions, never adds one.
     */
    private val textRenderers = setOf("Text", "BasicText", "ClickableText")
    private val textParameters = setOf("text", "contentDescription", "placeholder")
    private fun renderedDirectly(literal: Literal) = (literal.call in textRenderers && literal.named == null) || literal.named in textParameters

    /** D46f: the explicit allowlist, (file, exact literal) to reason. Escapes in the file: \\ \t \n. */
    private val allowlist: Map<Pair<String, String>, String> by lazy {
        File(root, "app/src/test/resources/ui-lint-allowlist.tsv").readLines().drop(1).filter { it.isNotBlank() }.associate { line ->
            val (file, text, reason) = line.split('\t').also { require(it.size == 3) { "Allowlist row needs file, literal and reason: $line" } }
            (file to unescape(text)) to reason
        }
    }
    private fun unescape(text: String) = buildString {
        var i = 0
        while (i < text.length) {
            if (text[i] == '\\' && i + 1 < text.length) { append(when (text[i + 1]) { 't' -> '\t'; 'n' -> '\n'; else -> text[i + 1] }); i += 2 }
            else { append(text[i]); i++ }
        }
    }

    /** Every lint problem of one literal, before any exemption. */
    private fun problems(literal: Literal): List<String> = buildList {
        if (identifierLike(literal.text)) add("RAW_IDENTIFIER")
        addAll(V2DesignDiagnostics.learnerVoiceRules(neutral(literal.text)))
    }

    /** The lint result for one literal of [name], or null when it passes or an explicit exemption covers it. */
    private fun verdict(name: String, literal: Literal): List<String>? {
        val found = problems(literal)
        if (found.isEmpty() || literal.text in d31bLabels) return null
        if ((name to literal.text) in allowlist && !renderedDirectly(literal)) return null
        return found
    }

    private fun hits(name: String, source: String): List<String> =
        UiLiteralScanner.literals(source).mapNotNull { literal ->
            verdict(name, literal)?.let { "$name:${literal.line} $it \"${literal.text}\"" }
        }

    private fun stringResources(): List<Pair<String, String>> =
        File(root, "app/src/main/res").walkTopDown().filter { it.name == "strings.xml" }.flatMap { file ->
            Regex("<string name=\"([^\"]+)\"[^>]*>(.*?)</string>", RegexOption.DOT_MATCHES_ALL).findAll(file.readText())
                .map { it.groupValues[1] to it.groupValues[2].replace("\\'", "'").replace(Regex("%\\d\\$[sd]"), "value") }
        }.toList()

    @Test fun everyLearnerVisibleHardcodedStringPassesTheLearnerLint() {
        val fileHits = learnerFiles.flatMap { file ->
            hits(rel(file), file.readText())
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
        // T16c-n3 expectation change (owner decision D46f): the label RAW_IDENTIFIER_RENDERED -> RAW_IDENTIFIER, and 4 -> 6 hits:
        // in a file with no allowlist entries the test tag and the when label are reported too (they passed by call context).
        fun reported(text: String) = found.any { "RAW_IDENTIFIER" in it && it.endsWith(" \"$text\"") }
        assertTrue(found.joinToString("\n"), reported("review-pending"))
        assertTrue(found.joinToString("\n"), reported("APPROVED"))
        assertTrue(found.joinToString("\n"), found.any { "\"Release stays blocked.\"" in it })
        assertTrue(found.joinToString("\n"), found.any { "\"Choose the requested form.\"" in it })
        assertTrue(found.joinToString("\n"), reported("v2-review-pending"))
        // Plain feedback passes.
        assertEquals(found.joinToString("\n"), 6, found.size)
        // The D31b labels pass only as those exact strings: the same wording elsewhere in a status sentence is still linted.
        assertTrue(hits("probe", "Text(\"Status: approved by the reviewer.\")").isNotEmpty())
        // "Not the reviewed answer." (replaced by "Not quite. Try again.", D32b) must not come back.
        assertTrue(learnerFiles.none { "Not the reviewed answer" in it.readText() })
    }

    /**
     * T16c-n3 (owner decision D46f): Unicode escapes are decoded before the check; a literal is scanned whatever call it is in
     * (an identifier reached through a variable, a lowercase helper or a collection builder, and a sentence in a diagnostic
     * call, all passed before); an allowlisted literal rendered straight by Text() is still reported.
     */
    @Test fun escapesAndEveryCallContextAreScannedAndOnlyTheAllowlistExempts() {
        val probes = listOf(
            "Text(\"Status: \\u0061pproved\")" to "LEARNER",
            "Text(\"\\u0052\\u0065\\u006c\\u0065\\u0061\\u0073\\u0065 stays blocked.\")" to "LEARNER",
            "val status = \"REVIEW_PENDING\"\nText(status)" to "RAW_IDENTIFIER",
            "statusLine(\"review-pending\")" to "RAW_IDENTIFIER",
            "val labels = listOf(\"APPROVED\", \"Ready\")" to "RAW_IDENTIFIER",
            "error(\"Approved by the reviewer.\")" to "LEARNER",
            "errors += \"Release stays blocked.\"" to "LEARNER",
        )
        for ((source, rule) in probes) {
            val found = hits("probe", source)
            assertTrue("$source: $found", found.any { rule in it })
        }
        // The decoded text is what the lint reads.
        assertEquals(listOf("Status: approved"), UiLiteralScanner.literals("Text(\"Status: \\u0061pproved\")").map { it.text })
        // An allowlisted identifier passes where the allowlist names it, but not when rendered straight by Text().
        val (file, text) = allowlist.keys.first { it.second == "v2-continue" }
        assertTrue(hits(file, "V2PrimaryButton(\"Continue\", tag = \"$text\")").isEmpty())
        assertTrue(hits(file, "Text(\"$text\")").isNotEmpty())
        assertTrue(hits("another/File.kt", "V2PrimaryButton(\"Continue\", tag = \"$text\")").isNotEmpty())
    }

    /** D46f: every allowlist entry has a reason, names a literal that is in its file now, and is needed (the literal fails without it). */
    @Test fun theAllowlistIsExplicitCurrentAndNeeded() {
        val byFile = learnerFiles.associate { rel(it) to UiLiteralScanner.literals(it.readText()) }
        val stale = allowlist.entries.filter { (key, reason) ->
            val (file, text) = key
            reason.isBlank() || byFile[file].orEmpty().none { it.text == text && problems(it).isNotEmpty() && it.text !in d31bLabels }
        }.map { "${it.key.first}\t${it.key.second}" }
        assertTrue(stale.joinToString("\n"), stale.isEmpty())
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
