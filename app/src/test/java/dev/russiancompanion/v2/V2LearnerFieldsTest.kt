package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T15g (owner decision D21e): the validator's Russian coverage and the blocking phrase audit read one shared field set,
 * every pack field the app renders to a learner. This test derives the rendered fields from the UI code and checks them
 * against V2LearnerFields; a regression puts a word without a witness into each field kind.
 */
class V2LearnerFieldsTest {
    private val root = PhraseAudit.root
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val ui by lazy {
        File(root, "app/src/main/java/dev/russiancompanion/v2/ui").listFiles()!!.filter { it.extension == "kt" }
            .associate { it.name to it.readText() }
    }

    // Learner text is a String or a List<String>; lists of entities and ID lists are structure, not text.
    private fun textProperties(type: Class<*>): Set<String> = type.declaredFields.filter { f ->
        f.type == String::class.java || (List::class.java.isAssignableFrom(f.type) &&
            (f.genericType as? java.lang.reflect.ParameterizedType)?.actualTypeArguments?.singleOrNull() == String::class.java)
    }.map { it.name }.filter { !it.endsWith("Ids") }.toSet()

    /**
     * T15h (owner decision D25e): the rendered set derived from the UI rendering dataflow, not from a list of property
     * accesses. Sinks are the arguments of every composable call (the @Composable functions of the ui package and Text).
     * A pack-entity text property counts as rendered when it is read inside a sink argument, or inside the body of a
     * function whose result a sink argument calls (transitively: helpers such as topicLabel() and the answer-feedback
     * builders). Reads used only for a comparison or a call, and code inside lambda blocks of an argument (click handlers,
     * semantics), are not dataflow into the text. Only functions that return text are followed, and digest, hash, seed and
     * canonical/fold helpers are not (their strings order or compare content and are never shown). The offline notice is followed through OfflineSourceNotice: each notice
     * property the attributions screen renders is traced to the pack source or provenance field sourceNotice() builds it from.
     */
    private fun renderedByDataflow(extra: Map<String, String> = emptyMap()): Set<String> {
        val main = File(root, "app/src/main/java/dev/russiancompanion/v2")
        val uiFiles = File(main, "ui").listFiles()!!.filter { it.extension == "kt" }.map { it.name }.toSet() + extra.keys
        val files = (File(main, "ui").listFiles()!!.filter { it.extension == "kt" } + File(main, "curriculum").listFiles()!!.filter { it.extension == "kt" } +
            listOf(File(main, "notices/V2OfflineNotices.kt"))).associate { it.name to it.readText() } + extra
        fun balanced(text: String, open: Int, l: Char, r: Char): Int {
            var depth = 0
            for (i in open until text.length) { if (text[i] == l) depth++ else if (text[i] == r) { depth--; if (depth == 0) return i } }
            return text.length - 1
        }
        fun withoutBlocks(text: String): String {
            val out = StringBuilder(); var i = 0
            while (i < text.length) { if (text[i] == '{' && (i == 0 || text[i - 1] != '$')) i = balanced(text, i, '{', '}') + 1 else { out.append(text[i]); i++ } }
            return out.toString()
        }
        // Functions: name -> body (to the next declaration at the same or lower indentation).
        val declaration = Regex("(?m)^(\\s*)(?:@Composable\\s+)?(?:(?:internal|private|public|inline)\\s+)*fun\\s+(?:<[^>]+>\\s*)?(?:([A-Za-z0-9_]+)\\.)?([a-zA-Z0-9_]+)\\s*\\(")
        val bodies = HashMap<String, String>(); val extensionOf = HashMap<String, String>(); val composables = mutableSetOf("Text")
        val returnsText = mutableSetOf<String>()
        files.forEach { (_, text) ->
            val found = declaration.findAll(text).toList()
            found.forEachIndexed { i, m ->
                val stop = found.drop(i + 1).firstOrNull { it.groupValues[1].length <= m.groupValues[1].length }?.range?.first ?: text.length
                val name = m.groupValues[3]
                bodies[name] = (bodies[name] ?: "") + text.substring(m.range.first, stop)
                if (m.groupValues[2].isNotEmpty()) extensionOf[name] = m.groupValues[2]
                // Only a function that returns text can carry pack text into a sink.
                val afterParams = text.substring(balanced(text, m.range.last, '(', ')') + 1).take(40)
                // Digest, hash, seed and canonical/fold helpers return strings that order or compare content; they are never shown.
                val opaque = Regex("(?i)digest|hash|seed|canonical|fold").containsMatchIn(name)
                if (!opaque && Regex("^\\s*:\\s*(?:String\\??|List<String>)\\s*[={]").containsMatchIn(afterParams)) returnsText += name
                if (text.substring(maxOf(0, m.range.first - 40), m.range.first + m.value.length).contains("@Composable")) composables += name
            }
        }
        val receivers = mapOf(
            "unit" to ("unit" to V2Unit::class.java), "session" to ("session" to V2Session::class.java),
            "step" to ("step" to V2Step::class.java), "item" to ("item" to V2CheckItem::class.java),
            "example" to ("example" to V2Example::class.java), "entry" to ("reference" to V2ReferenceEntry::class.java),
            "objective" to ("objective" to V2Objective::class.java), "letter" to ("letter" to V2Letter::class.java),
            "binderTask" to ("binderTask" to V2BinderTask::class.java), "tutorTask" to ("tutorTask" to V2TutorTask::class.java),
        )
        // A read compared with == or != is a condition, not text (lookbehind and lookahead).
        val access = Regex("(?<![!=]=\\s{0,2})\\b(unit|session|step|item|example|entry|objective|letter|task|source|reviewCopy)\\??\\.([a-zA-Z]+)\\b(?!\\s*(?:[!=]=|\\())")
        // Branch conditions decide which text shows; their reads are not the shown text.
        fun withoutConditions(text: String): String {
            val out = StringBuilder(text); val condition = Regex("\\b(?:if|when|while)\\s*\\(")
            condition.findAll(text).toList().asReversed().forEach { m ->
                val open = m.range.last; out.replace(open + 1, balanced(text, open, '(', ')'), "")
            }
            return out.toString()
        }
        val call = Regex("\\b([a-zA-Z0-9_]+)\\s*\\(")
        val noticeProperties = mutableSetOf<String>(); val derived = mutableSetOf<String>()
        fun reads(text: String) {
            access.findAll(text).forEach { m ->
                val (receiver, property) = m.destructured
                when (receiver) {
                    "task" -> {
                        if (property in textProperties(V2BinderTask::class.java)) derived += "binderTask.$property"
                        if (property in textProperties(V2TutorTask::class.java)) derived += "tutorTask.$property"
                    }
                    "source" -> noticeProperties += property
                    "reviewCopy" -> when (property) { "introductions" -> derived += "reviewCopy.introduction"; "titles" -> derived += "reviewCopy.title" }
                    else -> receivers[receiver]?.let { (kind, type) -> if (property in textProperties(type)) derived += "$kind.$property" }
                }
            }
            if (Regex("typedAnswerKey(?:!!|\\?)?\\.acceptedAnswers").containsMatchIn(text)) derived += "item.acceptedAnswers"
        }
        val localValue = Regex("\\b(?:val|var)\\s+(\\w+)\\s*(?::[^=]+)?=\\s*(.+)")
        val visited = mutableSetOf<String>()
        // String literal text is not code: keep only its ${...} templates.
        fun withoutLiterals(text: String): String = Regex("\"(?:[^\"\\\\]|\\\\.)*\"").replace(text) { m ->
            Regex("\\$\\{([^}]*)\\}|\\$(\\w+)").findAll(m.value).joinToString(" ") { it.groupValues[1] + it.groupValues[2] }
        }
        // Arguments passed to a project function flow into its body, which is followed; they are not shown at the call.
        fun withoutProjectArguments(text: String): String {
            val out = StringBuilder(text)
            call.findAll(text).toList().asReversed().filter { it.groupValues[1] in bodies && it.groupValues[1] !in composables }.forEach { m ->
                val open = m.range.last; val close = balanced(text, open, '(', ')')
                if (close > open + 1) out.replace(open + 1, close, "")
            }
            return out.toString()
        }
        fun follow(raw: String, scope: String) {
            val text = withoutProjectArguments(withoutConditions(raw))
            reads(text)
            // A local value rendered as itself (not used as a receiver or callee): follow its initializer in the same function.
            Regex("(?<![.\\w])([a-z]\\w*)\\b(?!\\s*[.(?])").findAll(withoutLiterals(text)).map { it.groupValues[1] }.distinct().forEach { name ->
                localValue.findAll(scope).filter { it.groupValues[1] == name && visited.add("val:$name:${scope.hashCode()}:${it.range.first}") }
                    .forEach { follow(withoutBlocks(it.groupValues[2]), scope) }
            }
            call.findAll(text).map { it.groupValues[1] }.filter { it in returnsText && it !in composables && visited.add(it) }.forEach { name ->
                // A function's whole body (its result is what the sink shows); an extension's bare receiver properties.
                val body = bodies.getValue(name)
                val bodyText = body.substring(body.indexOf(')').coerceAtLeast(0))
                follow(bodyText, body)
                val type = mapOf("V2Unit" to V2Unit::class.java, "V2Step" to V2Step::class.java, "V2CheckItem" to V2CheckItem::class.java)[extensionOf[name]]
                if (type != null) {
                    val kind = mapOf(V2Unit::class.java to "unit", V2Step::class.java to "step", V2CheckItem::class.java to "item").getValue(type)
                    Regex("(?<![.\\w])([a-z]\\w*)\\b").findAll(withoutConditions(bodyText)).map { it.groupValues[1] }
                        .filter { it in textProperties(type) }.forEach { derived += "$kind.$it" }
                }
            }
        }
        // Arguments of a call, split at top-level commas, each with the parameter it binds (named, else by position).
        fun arguments(call: String): List<Pair<String?, String>> {
            val out = mutableListOf<Pair<String?, String>>(); var depth = 0; var quote = false; var startAt = 0
            fun push(end: Int) {
                val a = call.substring(startAt, end).trim()
                if (a.isNotEmpty()) Regex("^([a-zA-Z]\\w*)\\s*=(?!=)\\s*([\\s\\S]*)$").find(a)?.let { out += it.groupValues[1] to it.groupValues[2] } ?: run { out += null to a }
            }
            for (i in call.indices) {
                val c = call[i]
                if (c == '"' && (i == 0 || call[i - 1] != '\\')) quote = !quote
                if (quote) continue
                if (c in "([{") depth++ else if (c in ")]}") depth-- else if (c == ',' && depth == 0) { push(i); startAt = i + 1 }
            }
            push(call.length)
            return out
        }
        // Parameter names of each composable, and which parameters reach visible text (fixed point from Text's text).
        val parameters = composables.associateWith { name ->
            val body = bodies[name] ?: return@associateWith listOf("text")
            val open = body.indexOf('(')
            arguments(body.substring(open + 1, balanced(body, open, '(', ')'))).map { (_, a) -> a.substringBefore(':').trim().removePrefix("vararg ").trim() }
        }
        val renderedParameters = HashMap<String, MutableSet<String>>().apply { put("Text", mutableSetOf("text")) }
        data class Site(val file: String, val text: String, val callee: String, val at: IntRange, val args: List<Pair<String, String>>)
        val sites = files.filterKeys { it in uiFiles }.flatMap { (file, text) ->
            call.findAll(text).filter { it.groupValues[1] in composables && !text.substring(0, it.range.first).trimEnd().endsWith("fun") }.map { m ->
                val open = m.range.last
                val args = arguments(text.substring(open + 1, balanced(text, open, '(', ')')))
                val names = parameters.getValue(m.groupValues[1])
                Site(file, text, m.groupValues[1], m.range, args.mapIndexed { i, (named, a) -> (named ?: names.getOrNull(i) ?: "") to a })
            }.toList()
        }
        fun enclosing(site: Site): String? = bodies.entries.filter { (name, body) -> name in composables && body in site.text &&
            site.text.indexOf(body).let { at -> site.at.first in at until at + body.length } }.minByOrNull { it.value.length }?.key
        var changed = true
        while (changed) {
            changed = false
            sites.forEach { site ->
                val owner = enclosing(site) ?: return@forEach
                site.args.filter { (param, _) -> param in renderedParameters[site.callee].orEmpty() }.forEach { (_, a) ->
                    parameters.getValue(owner).filter { p -> Regex("\\b$p\\b").containsMatchIn(withoutBlocks(a)) }.forEach { p ->
                        if (renderedParameters.getOrPut(owner) { mutableSetOf() }.add(p)) changed = true
                    }
                }
            }
        }
        // A value rendered through a scope-function parameter (x?.let { Text(it) }, xs.forEach { o -> Text(o) }).
        val scope = Regex("(?m)^[ \\t]*(.*?)(?:\\?\\.|\\.)(?:let|forEach|forEachIndexed|also|map)\\s*\\{\\s*(?:(\\w+)(?:\\s*,\\s*(\\w+))?\\s*->)?")
        val scopesByFile = files.filterKeys { it in uiFiles }.mapValues { (_, text) ->
            scope.findAll(text).map { m ->
                val open = m.range.last.let { e -> text.lastIndexOf('{', e) }
                Triple(open..balanced(text, open, '{', '}'), m.groupValues[1], listOf(m.groupValues[2], m.groupValues[3]).filter { it.isNotEmpty() }.ifEmpty { listOf("it") })
            }.toList()
        }
        sites.forEach { site ->
            site.args.filter { (param, _) -> param in renderedParameters[site.callee].orEmpty() }.forEach { (_, a) ->
                val argument = withoutBlocks(a)
                val scopeText = enclosing(site)?.let { bodies.getValue(it) } ?: ""
                follow(argument, scopeText)
                scopesByFile.getValue(site.file).filter { (range, _, params) -> site.at.first in range && params.any { p -> Regex("\\b$p\\b").containsMatchIn(withoutLiterals(argument)) } }
                    .forEach { (_, receiver, _) -> follow(receiver, scopeText) }
            }
        }
        // The offline notice: OfflineSourceNotice(name = expr) in sourceNotice(), each expr traced to the pack field.
        val notices = files.getValue("V2OfflineNotices.kt")
        val builder = bodies.getValue("sourceNotice")
        val construction = builder.substring(builder.indexOf("OfflineSourceNotice("))
        Regex("(\\w+) = ([^,\\n]+)").findAll(construction.substring(0, balanced(construction, construction.indexOf('('), '(', ')'))).forEach { m ->
            val (name, expr) = m.destructured
            if (name !in noticeProperties) return@forEach
            val direct = Regex("^source\\.(\\w+)$").find(expr.trim())?.groupValues?.get(1)
            when {
                direct == "id" -> derived += "notice.sourceId"
                direct != null && direct in textProperties(V2SourceRecord::class.java) -> derived += "notice.$direct"
                else -> {
                    // A local value built from provenance records: follow it (and the values it uses) to record fields.
                    val local = Regex("val ${Regex.escape(expr.trim())} = ([\\s\\S]*?)\\n    (?:val|return)").find(builder)?.groupValues?.get(1).orEmpty()
                    val used = Regex("\\b(\\w+)\\.isNotEmpty\\(\\)|\\b(\\w+)\\.joinToString").findAll(local).flatMap { it.groupValues.drop(1) }.filter { it.isNotEmpty() }
                    val chain = local + used.joinToString("\n") { v -> Regex("val $v = (.*)").find(builder)?.groupValues?.get(1).orEmpty() }
                    Regex("\\bit\\.(\\w+)").findAll(chain).map { it.groupValues[1] }.filter { it in textProperties(V2ProvenanceRecord::class.java) }.forEach {
                        derived += when (it) { "licenseUrl" -> "notice.licenseUrl"; "shareAlikeTreatment" -> "notice.shareAlike"; else -> "notice.$it" }
                    }
                }
            }
        }
        assertTrue(notices.contains("OfflineSourceNotice("))
        if (files.values.any { "reviewCopy.text(" in it }) derived += "reviewCopy.message"
        return derived
    }

    @Test fun theSharedFieldSetIsTheSetTheUiRenders() {
        // 1. Each rendered kind names the UI file and expression that renders it.
        V2LearnerFields.RENDERED.forEach { (kind, where) ->
            val (file, expression) = where
            assertTrue("$kind: $file does not contain $expression", ui.getValue(file).contains(expression))
        }
        // 2. T15h (D25e): the set derived from the UI rendering dataflow equals the shared rendered set.
        assertEquals(V2LearnerFields.RENDERED.keys.sorted(), renderedByDataflow().sorted())
        // 2b. The analysis is sensitive: a composable that renders a field, directly or through a helper's result, adds it;
        // reading the same field only to branch or as a key does not.
        val probe = "@Composable\ninternal fun ProbeScreen(unit: V2Unit, step: V2Step) {\n    if (unit.topicSummary.isNotEmpty()) Text(\"Unit\", modifier = Modifier.semantics { testTag = step.id })\n}\n"
        assertFalse("unit.topicSummary" in renderedByDataflow(mapOf("Probe.kt" to probe)))
        val shown = "@Composable\ninternal fun ProbeScreen(unit: V2Unit) {\n    Text(probeLabel(unit))\n}\ninternal fun probeLabel(unit: V2Unit): String = \"About: \${unit.topicSummary}\"\n"
        assertTrue("unit.topicSummary" in renderedByDataflow(mapOf("Probe.kt" to shown)))
        // 3. The shared set is the rendered set plus the declared, scanned fields no screen renders; nothing else.
        // T16-n2 (D31b): reference.reviewStatus is shown only through its plain label, so it moves from RENDERED to the
        // declared, scanned set (was setOf("unit.topicSummary", "item.answer")).
        // T16c-n2 (D37a/D37b): step.kind is no longer rendered raw (stepStatus shows a plain label); it stays scanned as a declared
        // field (was: the first three only, with step.kind in RENDERED).
        assertEquals(setOf("unit.topicSummary", "item.answer", "reference.reviewStatus", "step.kind"), V2LearnerFields.DECLARED_NOT_RENDERED)
        assertTrue(V2LearnerFields.DECLARED_NOT_RENDERED.none { it in renderedByDataflow() })
        // Every kind the shared set emits for the current pack is declared; step.unavailableReason has no value in the
        // current pack, and the regression below shows it is scanned. T15i (WC011): the Units 6-10 binder tasks give
        // binderTask.modelAnswer values, so it leaves the unproduced set (was {binderTask.modelAnswer, step.unavailableReason}).
        val produced = V2LearnerFields.fields(pack).map { it.kind }.toSet()
        assertTrue(V2LearnerFields.KINDS.containsAll(produced))
        assertEquals(setOf("step.unavailableReason"), V2LearnerFields.KINDS - produced)
        // The audit and the validator read the same list.
        assertEquals(V2LearnerFields.fields(pack).map { Triple(it.unit, it.ownerField, it.text) },
            PhraseAudit.fields(pack).map { Triple(it.unit, it.ownerField, it.text) })
    }

    /**
     * Regression: a word without a witness in Unit 29 in each field kind is rejected by the validator, and an unattested
     * phrase there is reported by the audit. сущ is allowlisted only for Unit 30; Книга лежит на столе is allowlisted only
     * for Unit 30 and attested in no frozen line. A single unstressed word is outside the audit's phrase rule (and сущ
     * has the frozen homograph су́щ), so the audit side uses the phrase; the validator side uses the word.
     */
    @Test fun aWordOrPhraseWithoutWitnessInAnyRenderedFieldKindIsRejected() {
        val word = "сущ"
        val phrase = "Книга лежит на столе"
        assertFalse(PhraseAudit.present("$phrase и"))
        val baseline = V2Validator.validate(pack)
        assertTrue(baseline.errors.joinToString("\n"), baseline.ok)
        val baselineAudit = PhraseAudit.failures(PhraseAudit.fields(pack))
        // T15h (D25e): every declared kind, including the four letter kinds and the notice kinds (was 30 of 34 kinds).
        val kinds = V2LearnerFields.KINDS.sorted()
        assertEquals(V2LearnerFields.RENDERED.keys + V2LearnerFields.DECLARED_NOT_RENDERED, kinds.toSet())
        for (kind in kinds) {
            if (kind.startsWith("notice.")) {
                // The offline notice is app-wide (unit 0), outside the validator's per-unit scan; its Cyrillic is accepted only
                // as text a FREEZE.json records for that source, so the audit rejects any other Cyrillic, word or phrase.
                for (text in listOf("($word)", "($phrase)")) {
                    val audit = PhraseAudit.failures(PhraseAudit.fields(insert(pack, kind, text))) - baselineAudit
                    assertTrue("$kind: $audit", audit.any { it.unit == V2LearnerFields.NOTICE_UNIT && it.type == "NOTICE_UNRECORDED" && text in it.text })
                }
                continue
            }
            // Letters: an unattested letter name (сущ is no letter or name cell of the alphabet table) is rejected.
            val withWord = insert(pack, kind, "($word)")
            val errors = V2Validator.validate(withWord).errors
            assertTrue("$kind: $errors", errors.any { it == "Russian text has no admitted source record: $word" })
            val withPhrase = insert(pack, kind, "($phrase)")
            val audit = PhraseAudit.failures(PhraseAudit.fields(withPhrase)) - baselineAudit
            assertTrue("$kind: $audit", audit.any { it.unit != 30 && it.text == phrase && it.type == "UNATTESTED" })
        }
    }

    private fun insert(p: V2CurriculumPack, kind: String, text: String): V2CurriculumPack {
        // Binder and tutor tasks exist in Units 3-5 only and typed items in Units 1-10 only; every other kind is probed in Unit 29.
        val n = when {
            kind.startsWith("binderTask.") || kind.startsWith("tutorTask.") -> 5
            kind == "item.acceptedAnswers" -> 10
            else -> 29
        }
        val u = p.units.single { it.number == n }
        fun units(f: (V2Unit) -> V2Unit) = p.copy(units = p.units.map { if (it.number == n) f(it) else it })
        fun steps(f: (V2Step) -> V2Step): V2CurriculumPack {
            val target = u.sessions.flatMap { it.steps }.first { kind != "step.dominantAction" || it.dominantAction != null }
            return units { unit -> unit.copy(sessions = unit.sessions.map { s -> s.copy(steps = s.steps.map { if (it.id == target.id) f(it) else it }) }) }
        }
        val objectives = u.objectives.map { it.id }.toSet()
        val placed = p.units.flatMap { x -> x.objectives.flatMap { it.checkItemIds } + x.sessions.flatMap { s -> s.steps.flatMap { it.itemIds } } }.toSet()
        // An item linked only by item.objectiveId, and a typed item for accepted answers.
        // Prefer an objective-only item; a kind that no objective-only item carries (typed answers, distractor
        // rationales) is probed on a placed item of the same unit.
        val item by lazy {
            val fits = p.items.filter { it.objectiveId in objectives && (kind != "item.acceptedAnswers" || it.typedAnswerKey != null) &&
                (kind != "item.distractorRationales" || it.distractorRationales.isNotEmpty()) }
            fits.firstOrNull { it.id !in placed } ?: fits.firstOrNull() ?: fail("Unit $n has no item for $kind") as Nothing
        }
        fun items(f: (V2CheckItem) -> V2CheckItem) = p.copy(items = p.items.map { if (it.id == item.id) f(it) else it })
        val linked = p.units.flatMap { x -> x.objectives.flatMap { it.exampleIds } + x.sessions.flatMap { s -> s.steps.flatMap { it.exampleIds } } }.toSet()
        val example by lazy { p.examples.firstOrNull { it.objectiveId in objectives && it.id !in linked } ?: p.examples.first { it.objectiveId in objectives } }
        fun examples(f: (V2Example) -> V2Example) = p.copy(examples = p.examples.map { if (it.id == example.id) f(it) else it })
        // An unlinked reference introduced in Unit 29.
        val unlinked = p.references.first().copy(id = "ref-t15g-probe", title = "Probe", summary = "Probe", detail = "Probe", introducedInUnit = n)
        fun reference(f: (V2ReferenceEntry) -> V2ReferenceEntry) = p.copy(references = p.references + f(unlinked))
        return when (kind) {
            "unit.title" -> units { it.copy(title = it.title + " $text") }
            "unit.shortLabel" -> units { it.copy(shortLabel = it.shortLabel + " $text") }
            "unit.topicSummary" -> units { it.copy(topicSummary = it.topicSummary + " $text") }
            "unit.mappingNote" -> units { it.copy(mappingNote = it.mappingNote + " $text") }
            "binderTask.prompt" -> units { it.copy(binderTask = it.binderTask!!.copy(prompt = it.binderTask!!.prompt + " $text")) }
            "binderTask.modelAnswer" -> units { it.copy(binderTask = it.binderTask!!.copy(modelAnswer = (it.binderTask!!.modelAnswer ?: "") + " $text")) }
            "tutorTask.bring" -> units { it.copy(tutorTask = it.tutorTask!!.copy(bring = it.tutorTask!!.bring + " $text")) }
            "objective.title" -> units { unit -> unit.copy(objectives = unit.objectives.mapIndexed { i, o -> if (i == 0) o.copy(title = o.title + " $text") else o }) }
            "session.title" -> units { unit -> unit.copy(sessions = unit.sessions.mapIndexed { i, s -> if (i == 0) s.copy(title = s.title + " $text") else s }) }
            "step.title" -> steps { it.copy(title = it.title + " $text") }
            "step.body" -> steps { it.copy(body = it.body + " $text") }
            "step.bookDirection" -> steps { it.copy(bookDirection = (it.bookDirection ?: "") + " $text") }
            "step.companionDirection" -> steps { it.copy(companionDirection = (it.companionDirection ?: "") + " $text") }
            "step.unavailableReason" -> steps { it.copy(unavailableReason = (it.unavailableReason ?: "") + " $text") }
            "step.dominantAction" -> steps { it.copy(dominantAction = it.dominantAction + " $text") }
            "item.prompt" -> items { it.copy(prompt = it.prompt + " $text") }
            "item.promptLetter" -> items { it.copy(promptLetter = (it.promptLetter ?: "") + " $text") }
            "item.answer" -> items { it.copy(answer = it.answer + " $text") }
            "item.feedback" -> items { it.copy(feedback = it.feedback + " $text") }
            "item.options" -> items { it.copy(options = it.options + text) }
            "item.distractorRationales" -> items { it.copy(distractorRationales = it.distractorRationales + text) }
            "item.acceptedAnswers" -> items { it.copy(typedAnswerKey = it.typedAnswerKey!!.copy(acceptedAnswers = it.typedAnswerKey!!.acceptedAnswers + text)) }
            "example.display" -> examples { it.copy(display = it.display + " $text") }
            "example.note" -> examples { it.copy(note = it.note + " $text") }
            "reference.title" -> reference { it.copy(title = "Probe $text") }
            "reference.summary" -> reference { it.copy(summary = "Probe $text") }
            "reference.detail" -> reference { it.copy(detail = "Probe $text") }
            "reviewCopy.title" -> p.copy(reviewCopy = p.reviewCopy.copy(titles = p.reviewCopy.titles + (n to (p.reviewCopy.titles[n].orEmpty() + " $text"))))
            "reviewCopy.introduction" -> p.copy(reviewCopy = p.reviewCopy.copy(introductions = p.reviewCopy.introductions + (n to (p.reviewCopy.introductions[n].orEmpty() + " $text"))))
            "reviewCopy.message" -> p.copy(reviewCopy = p.reviewCopy.copy(messages = p.reviewCopy.messages + ("t15gProbe" to "Probe $text")))
            "step.kind" -> steps { it.copy(kind = it.kind + " $text") }
            "reference.reviewStatus" -> reference { it.copy(reviewStatus = "Probe $text") }
            "letter.uppercase", "letter.lowercase", "letter.group", "letter.latinLookalike" -> {
                val target = p.letters.first()
                p.copy(letters = p.letters.map { l -> if (l.id != target.id) l else when (kind) {
                    "letter.uppercase" -> l.copy(uppercase = "${l.uppercase} $text")
                    "letter.lowercase" -> l.copy(lowercase = "${l.lowercase} $text")
                    "letter.group" -> l.copy(group = "${l.group} $text")
                    else -> l.copy(latinLookalike = text)
                } })
            }
            "notice.sourceId", "notice.title", "notice.license", "notice.attribution", "notice.modification" -> {
                val target = p.sources.first { s -> p.provenanceRecords.any { it.sourceId == s.id } }
                p.copy(sources = p.sources.map { s -> if (s.id != target.id) s else when (kind) {
                    "notice.sourceId" -> s.copy(id = "${s.id} $text")
                    "notice.title" -> s.copy(title = "${s.title} $text")
                    "notice.license" -> s.copy(license = "${s.license} $text")
                    "notice.attribution" -> s.copy(attribution = "${s.attribution} $text")
                    else -> s.copy(modification = "${s.modification} $text")
                } })
            }
            "notice.licenseUrl", "notice.shareAlike" -> {
                val target = p.provenanceRecords.first()
                p.copy(provenanceRecords = p.provenanceRecords.map { r -> if (r !== target) r else
                    if (kind == "notice.licenseUrl") r.copy(licenseUrl = "${r.licenseUrl}#$text") else r.copy(shareAlikeTreatment = "${r.shareAlikeTreatment} $text") })
            }
            else -> fail("No insertion for $kind") as Nothing
        }
    }
}
