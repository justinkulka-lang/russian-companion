package dev.russiancompanion.v2.curriculum

/**
 * Exact frozen identities for the Batch A remaining intake.
 * A capability claim, a keyword, or a neighboring sentence is not an entry.
 */
internal data class GovernedSourceAdmission(
    val sourceId: String,
    val sourceRevision: String,
    val sourceRecordId: String,
    val sourceHash: String,
    val exactSpan: String,
    val unitNumber: Int,
    val objectiveId: String,
    val governedClaim: String,
)

internal object BatchARemainingSources {
    const val RU_SOURCE = "src-ruwiki-narechie-152437423"
    const val RU_REVISION = "152437423"
    const val RU_COPYRIGHT_OLDID = "154562152"
    const val WIKTIONARY_SOURCE = "src-enwiktionary-dolzhen-84965088"
    const val WIKTIONARY_REVISION = "84965088"
    const val WIKTIONARY_COPYRIGHT_OLDID = "87545419"
    const val PRESSBOOKS_SOURCE = "src-pressbooks-diverse-russian-traditions-grammar"
    const val PRESSBOOKS_REVISION = "ab2170e37d529a318f395f6e657d8024d0eaed8d7c5c9b34f8831d348b8b76b3"
    const val USA_GRAMMAR_SOURCE = "src-pressbooks-diverse-russian-usa-grammar"
    const val USA_GRAMMAR_REVISION = "87815b8724cb11aecba5bb6d8b6438f783408f2354e2560a3c0fe026fac1e171"
    val PRESSBOOKS_REVISIONS = mapOf(
        PRESSBOOKS_SOURCE to PRESSBOOKS_REVISION,
        USA_GRAMMAR_SOURCE to USA_GRAMMAR_REVISION,
    )

    val INVARIANCE_SPAN = "\u041d\u0430\u0440\u0435\u0447\u0438\u044f\u00a0\u2014 \u043d\u0435\u0438\u0437\u043c\u0435\u043d\u044f\u0435\u043c\u044b\u0435 \u0441\u043b\u043e\u0432\u0430: \u043e\u043d\u0438 \u043d\u0435 \u0441\u043a\u043b\u043e\u043d\u044f\u044e\u0442\u0441\u044f \u0438 \u043d\u0435 \u0441\u043f\u0440\u044f\u0433\u0430\u044e\u0442\u0441\u044f, \u043d\u0435 \u0438\u043c\u0435\u044e\u0442 \u043e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0439, \u043d\u0435 \u0438\u0437\u043c\u0435\u043d\u044f\u044e\u0442\u0441\u044f \u043f\u043e \u0440\u043e\u0434\u0430\u043c \u0438 \u0447\u0438\u0441\u043b\u0430\u043c."
    val SUFFIXAL_SPAN = "* '''\u0441\u0443\u0444\u0444\u0438\u043a\u0441\u0430\u043b\u044c\u043d\u044b\u0439''': ''\u0431\u044b\u0441\u0442\u0440\u044b\u0439\u00a0\u2014 \u0431\u044b\u0441\u0442\u0440\u043e'', ''\u0442\u0432\u043e\u0440\u0447\u0435\u0441\u043a\u0438\u0439\u00a0\u2014 \u0442\u0432\u043e\u0440\u0447\u0435\u0441\u043a\u0438'';"
    val LIKELIHOOD_SPAN = "# {{lb|ru|with a {{lg|verb}}}} {{n-g|Used to express [[likelihood]]. Usually translated as \"[[should]]\".}}\n#: {{syn|ru|\u0432\u0435\u0440\u043e\u044f\u0301\u0442\u043d\u043e|\u043d\u0430\u0432\u0435\u0301\u0440\u043d\u043e\u0435|\u043d\u0430\u0432\u0435\u0440\u043d\u044f\u043a\u0430\u0301|\u0441\u043a\u043e\u0440\u0435\u0301\u0435 \u0432\u0441\u0435\u0433\u043e\u0301}}\n#: {{uxi|ru|[[\u0437\u0430\u0442\u043c\u0435\u043d\u0438\u0435|\u0417\u0430\u0442\u043c\u0435\u0301\u043d\u0438\u0435]] '''\u0434\u043e\u043b\u0436\u043d\u043e\u0301''' [[\u043d\u0430\u0441\u0442\u0443\u043f\u0438\u0301\u0442\u044c]] [[\u0432]] [[\u0442\u0440\u0438]] [[\u0447\u0430\u0441|\u0447\u0430\u0441\u0430\u0301]].|The eclipse should occur at three o'clock.}}"
    val OBLIGATION_EXAMPLE_SPAN = "#: {{uxi|ru|[[\u0441\u043b\u0443\u0436\u0430\u0449\u0438\u0435|\u0421\u043b\u0443\u0301\u0436\u0430\u0449\u0438\u0435]] '''\u0434\u043e\u043b\u0436\u043d\u044b\u0301''' [[\u0432\u043e\u0301\u0432\u0440\u0435\u043c\u044f]] [[\u044f\u0432\u043b\u044f\u0301\u0442\u044c\u0441\u044f]] [[\u043d\u0430]] [[\u0440\u0430\u0431\u043e\u0442\u0430|\u0440\u0430\u0431\u043e\u0301\u0442\u0443]].|Employees must report to work on time.}}"
    val QUARTER_SPAN = "\u0413\u0440\u0430\u043c\u043c\u0430\u0442\u0438\u043a\u0430: At what time on the quarter hour</strong></p>\n</header>\n<div class=\"textbox__content\">\n<table class=\"grid\" style=\"border-collapse: collapse;width: 100%\">\n<thead>\n<tr>\n<td style=\"width: 33.3333%\"><strong>Time</strong></td>\n<td style=\"width: 66.6667%\"><strong>Explanation and examples</strong></td>\n</tr>\n</thead>\n<tbody>\n<tr>\n<td style=\"width: 33.3333%\"><strong>At what time on the hour (0.00)</strong></td>\n<td style=\"width: 66.6667%\">Use <strong>\u0432</strong> + accusative, but note that the number then often requires genitive case after it. In informal speech, sometimes people leave out \u0447\u0430\u0441 / \u0447\u0430\u0441\u0430 / \u0447\u0430\u0441\u043e\u0432. Remember: \u043e\u0434\u0438\u043d \u0447\u0430\u0441; \u0434\u0432\u0430 / \u0442\u0440\u0438 / \u0447\u0435\u0442\u044b\u0440\u0435 \u0447\u0430\u0441\u0430; \u043f\u044f\u0442\u044c \u0447\u0430\u0441\u043e\u0432.\n<p>\u041a\u043e\u043d\u0446\u0435\u0440\u0442 \u043d\u0430\u0447\u0438\u043d\u0430\u0435\u0442\u0441\u044f <strong>\u0432 \u0448\u0435\u0441\u0442\u044c \u0447\u0430\u0441\u043e\u0432</strong>. (18.00)<br>\n\u041e\u043d\u0430 \u0431\u044b\u043b\u0430 \u0434\u043e\u043c\u0430 <strong>\u0432 \u0447\u0430\u0441</strong>. (13.00)</p></td>\n</tr>\n<tr>\n<td style=\"width: 33.3333%\"><strong>At what time on the quarter hour (0.15)</strong></td>\n<td style=\"width: 66.6667%\">Use <strong>\u0432</strong> + accusative with either <strong>\u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c</strong> (quarter) or <strong>\u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442</strong>. Then you use the ordinal number of the next hour in the genitive case. In essence you are saying quarter of the next hour. Note that while you can say 6:15 as \u201c\u0448\u0435\u0441\u0442\u044c \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c\u201d, in the exercises on this page we would like to focus on this other way of expressing the time.\n<p>\u042f \u043f\u0440\u0438\u0434\u0443 <strong>\u0432 \u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c \u0432\u043e\u0441\u044c\u043c\u043e\u0433\u043e</strong>. (7.15 \u2013 quarter of the eighth hour)<br>\n\u041e\u043d \u043d\u0430\u043c \u043f\u043e\u0437\u0432\u043e\u043d\u0438\u043b <strong>\u0432 \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442 \u0434\u0435\u0441\u044f\u0442\u043e\u0433\u043e</strong>. (9.15 \u2013 15 minutes of the tenth hour)</p>\n<p>If you need to review the ordinal numbers up to twelve, you can find them here:</p>\n<div id=\"h5p-256\">\n<div class=\"h5p-iframe-wrapper\"><iframe id=\"h5p-iframe-256\" class=\"h5p-iframe\" data-content-id=\"256\" style=\"height:1px\" src=\"about:blank\" frameborder=\"0\" scrolling=\"no\" title=\"Ordinal numbers\"></iframe></div>\n</div>\n</td>\n</tr>\n<tr>\n<td style=\"width: 33.3333%\"><strong>At what time on the half hour (0.30)</strong></td>\n<td style=\"width: 66.6667%\">Use <strong>\u0432</strong> + prepositional with <strong>\u043f\u043e\u043b\u043e\u0432\u0438\u043d\u0430</strong> and the genitive case of the ordinal number of the next hour, or colloquially, <strong>\u043f\u043e\u043b_____</strong> (genitive case of the ordinal number of the next hour). Note that the spelling of 10:30 adds a hyphen: \u043f\u043e\u043b-\u043e\u0434\u0438\u043d\u043d\u0430\u0434\u0446\u0430\u0442\u043e\u0433\u043e.\n<p>\u041d\u0430\u0448 \u0440\u0435\u0439\u0441 \u0443\u0435\u0437\u0436\u0430\u0435\u0442 <strong>\u0432 \u043f\u043e\u043b\u043e\u0432\u0438\u043d\u0435 \u043f\u044f\u0442\u043e\u0433\u043e</strong>. (4.30 \u2013 half of the fifth hour)<br>\n\u041c\u044b \u0431\u0443\u0434\u0435\u043c \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u044b <strong>\u0432 \u043f\u043e\u043b\u0432\u0442\u043e\u0440\u043e\u0433\u043e</strong>. (1.30 \u2013 half of the second hour)</p>\n<p>If you need to review the ordinal numbers up to twelve, you can find them here:</p>\n<div id=\"h5p-256\">\n<div class=\"h5p-iframe-wrapper\"><iframe id=\"h5p-iframe-256\" class=\"h5p-iframe\" data-content-id=\"256\" style=\"height:1px\" src=\"about:blank\" frameborder=\"0\" scrolling=\"no\" title=\"Ordinal numbers\"></iframe></div>\n</div>\n</td>\n</tr>\n<tr>\n<td style=\"width: 33.3333%\"><strong>At what time on the three quarters hour (0.45)</strong></td>\n<td style=\"width: 66.6667%\">Use <strong>\u0431\u0435\u0437</strong> + genitive with <strong>\u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c</strong> or <strong>\u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442</strong>. Then you use the cardinal number of the next hour in the nominative case. In essence you are saying X hours without X minutes.\n<p>\u041f\u0435\u0440\u0435\u0434\u0430\u0447\u0430 \u043d\u0430\u0447\u0438\u043d\u0430\u0435\u0442\u0441\u044f <strong>\u0431\u0435\u0437 \u0447\u0435\u0442\u0432\u0435\u0440\u0442\u0438 \u0442\u0440\u0438</strong>. (2.45 \u2013 3 without a quarter)<br>\n\u041e\u043d\u0430 \u043e\u0431\u044b\u0447\u043d\u043e \u043f\u0440\u0438\u0445\u043e\u0434\u0438\u0442 \u043d\u0430 \u0440\u0430\u0431\u043e\u0442\u0443 <strong>\u0431\u0435\u0437 \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u0438 \u0434\u0435\u0432\u044f\u0442\u044c</strong>. (8.45 \u2013 9 without 15 minutes)</p>"

    val INVARIANCE_SHOWN = "\u041d\u0430\u0440\u0435\u0447\u0438\u044f"
    val INVARIANCE_ANSWER = "\u043d\u0435\u0438\u0437\u043c\u0435\u043d\u044f\u0435\u043c\u044b\u0435 \u0441\u043b\u043e\u0432\u0430"
    val INVARIANCE_DASH_LINE = "\u041d\u0430\u0440\u0435\u0447\u0438\u044f\u00a0\u2014 \u043d\u0435\u0438\u0437\u043c\u0435\u043d\u044f\u0435\u043c\u044b\u0435 \u0441\u043b\u043e\u0432\u0430:"
    const val INVARIANCE_RELATION = "immediately after the dash and before the colon"
    val INVARIANCE_POST_COLON = mapOf(
        "\u043d\u0435 \u0441\u043a\u043b\u043e\u043d\u044f\u044e\u0442\u0441\u044f" to ": \u043e\u043d\u0438 \u043d\u0435 \u0441\u043a\u043b\u043e\u043d\u044f\u044e\u0442\u0441\u044f",
        "\u043d\u0435 \u0441\u043f\u0440\u044f\u0433\u0430\u044e\u0442\u0441\u044f" to "\u0438 \u043d\u0435 \u0441\u043f\u0440\u044f\u0433\u0430\u044e\u0442\u0441\u044f",
        "\u043d\u0435 \u0438\u043c\u0435\u044e\u0442 \u043e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0439" to ", \u043d\u0435 \u0438\u043c\u0435\u044e\u0442 \u043e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0439",
    )
    val FAST = "\u0431\u044b\u0441\u0442\u0440\u044b\u0439"
    val FAST_ADV = "\u0431\u044b\u0441\u0442\u0440\u043e"
    val CREATIVE = "\u0442\u0432\u043e\u0440\u0447\u0435\u0441\u043a\u0438\u0439"
    val CREATIVE_ADV = "\u0442\u0432\u043e\u0440\u0447\u0435\u0441\u043a\u0438"
    val LIKELIHOOD_SENSE = "(with a verb) Used to express likelihood. Usually translated as \"should\"."
    val LIKELIHOOD_RUSSIAN = "\u0417\u0430\u0442\u043c\u0435\u0301\u043d\u0438\u0435 \u0434\u043e\u043b\u0436\u043d\u043e\u0301 \u043d\u0430\u0441\u0442\u0443\u043f\u0438\u0301\u0442\u044c \u0432 \u0442\u0440\u0438 \u0447\u0430\u0441\u0430\u0301."
    val LIKELIHOOD_GLOSS = "The eclipse should occur at three o'clock."
    val OBLIGATION_RUSSIAN = "\u0421\u043b\u0443\u0301\u0436\u0430\u0449\u0438\u0435 \u0434\u043e\u043b\u0436\u043d\u044b\u0301 \u0432\u043e\u0301\u0432\u0440\u0435\u043c\u044f \u044f\u0432\u043b\u044f\u0301\u0442\u044c\u0441\u044f \u043d\u0430 \u0440\u0430\u0431\u043e\u0301\u0442\u0443."

    val GOVERNED_GLOSSES: Map<String, Map<String, Set<String>>> = mapOf(
        INVARIANCE_SPAN to mapOf(INVARIANCE_SHOWN to setOf(INVARIANCE_ANSWER)),
        SUFFIXAL_SPAN to mapOf(
            FAST to setOf(FAST_ADV),
            CREATIVE to setOf(CREATIVE_ADV),
        ),
        LIKELIHOOD_SPAN to mapOf(LIKELIHOOD_RUSSIAN to setOf(LIKELIHOOD_GLOSS)),
        QUARTER_SPAN to mapOf(
            "\u041a\u043e\u043d\u0446\u0435\u0440\u0442 \u043d\u0430\u0447\u0438\u043d\u0430\u0435\u0442\u0441\u044f \u0432 \u0448\u0435\u0441\u0442\u044c \u0447\u0430\u0441\u043e\u0432." to setOf("18.00"),
            "\u042f \u043f\u0440\u0438\u0434\u0443 \u0432 \u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c \u0432\u043e\u0441\u044c\u043c\u043e\u0433\u043e." to setOf("7.15 \u2013 quarter of the eighth hour"),
            "\u041d\u0430\u0448 \u0440\u0435\u0439\u0441 \u0443\u0435\u0437\u0436\u0430\u0435\u0442 \u0432 \u043f\u043e\u043b\u043e\u0432\u0438\u043d\u0435 \u043f\u044f\u0442\u043e\u0433\u043e." to setOf("4.30 \u2013 half of the fifth hour"),
            "\u041f\u0435\u0440\u0435\u0434\u0430\u0447\u0430 \u043d\u0430\u0447\u0438\u043d\u0430\u0435\u0442\u0441\u044f \u0431\u0435\u0437 \u0447\u0435\u0442\u0432\u0435\u0440\u0442\u0438 \u0442\u0440\u0438." to setOf("2.45 \u2013 3 without a quarter"),
            "\u041e\u043d\u0430 \u0431\u044b\u043b\u0430 \u0434\u043e\u043c\u0430 \u0432 \u0447\u0430\u0441." to setOf("13.00"),
            "\u041e\u043d \u043d\u0430\u043c \u043f\u043e\u0437\u0432\u043e\u043d\u0438\u043b \u0432 \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442 \u0434\u0435\u0441\u044f\u0442\u043e\u0433\u043e." to setOf("9.15 \u2013 15 minutes of the tenth hour"),
            "\u041c\u044b \u0431\u0443\u0434\u0435\u043c \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u044b \u0432 \u043f\u043e\u043b\u0432\u0442\u043e\u0440\u043e\u0433\u043e." to setOf("1.30 \u2013 half of the second hour"),
            "\u041e\u043d\u0430 \u043e\u0431\u044b\u0447\u043d\u043e \u043f\u0440\u0438\u0445\u043e\u0434\u0438\u0442 \u043d\u0430 \u0440\u0430\u0431\u043e\u0442\u0443 \u0431\u0435\u0437 \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u0438 \u0434\u0435\u0432\u044f\u0442\u044c." to setOf("8.45 \u2013 9 without 15 minutes")
        ),
    )

    val ADMITTED_ADVERB_INVARIANCE = setOf(
        GovernedSourceAdmission(
            sourceId = "src-ruwiki-narechie-152437423",
            sourceRevision = "152437423",
            sourceRecordId = "ruwiki-narechie:\u041d\u0430\u0440\u0435\u0447\u0438\u044f\u00a0\u2014 \u043d\u0435\u0438\u0437\u043c\u0435\u043d\u044f\u0435\u043c\u044b\u0435 \u0441\u043b\u043e\u0432\u0430: \u043e\u043d\u0438 \u043d\u0435 \u0441\u043a\u043b\u043e\u043d\u044f\u044e\u0442\u0441\u044f \u0438 \u043d\u0435 \u0441\u043f\u0440\u044f\u0433\u0430\u044e\u0442\u0441\u044f, \u043d\u0435 \u0438\u043c\u0435\u044e\u0442 \u043e\u043a\u043e\u043d\u0447\u0430",
            sourceHash = "4af07512f61d6b248b17029ee4ba056f08726cdaea72fd5c375628fc9ea356f5",
            exactSpan = "\u041d\u0430\u0440\u0435\u0447\u0438\u044f\u00a0\u2014 \u043d\u0435\u0438\u0437\u043c\u0435\u043d\u044f\u0435\u043c\u044b\u0435 \u0441\u043b\u043e\u0432\u0430: \u043e\u043d\u0438 \u043d\u0435 \u0441\u043a\u043b\u043e\u043d\u044f\u044e\u0442\u0441\u044f \u0438 \u043d\u0435 \u0441\u043f\u0440\u044f\u0433\u0430\u044e\u0442\u0441\u044f, \u043d\u0435 \u0438\u043c\u0435\u044e\u0442 \u043e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0439, \u043d\u0435 \u0438\u0437\u043c\u0435\u043d\u044f\u044e\u0442\u0441\u044f \u043f\u043e \u0440\u043e\u0434\u0430\u043c \u0438 \u0447\u0438\u0441\u043b\u0430\u043c.",
            unitNumber = 7,
            objectiveId = "obj-u7-adverb-invariance",
            governedClaim = "adverb-invariance",
        )
    )
    val ADMITTED_ADVERB_BOUNDED_PAIRS = setOf(
        GovernedSourceAdmission(
            sourceId = "src-ruwiki-narechie-152437423",
            sourceRevision = "152437423",
            sourceRecordId = "ruwiki-narechie:* '''\u0441\u0443\u0444\u0444\u0438\u043a\u0441\u0430\u043b\u044c\u043d\u044b\u0439''': ''\u0431\u044b\u0441\u0442\u0440\u044b\u0439\u00a0\u2014 \u0431\u044b\u0441\u0442\u0440\u043e'', ''\u0442\u0432\u043e\u0440\u0447\u0435\u0441\u043a\u0438\u0439\u00a0\u2014 \u0442\u0432\u043e\u0440\u0447\u0435\u0441\u043a\u0438'';",
            sourceHash = "025ab52d81a99a683e3803289dd41c0abfb2d283fe036a56de70214731fc97de",
            exactSpan = "* '''\u0441\u0443\u0444\u0444\u0438\u043a\u0441\u0430\u043b\u044c\u043d\u044b\u0439''': ''\u0431\u044b\u0441\u0442\u0440\u044b\u0439\u00a0\u2014 \u0431\u044b\u0441\u0442\u0440\u043e'', ''\u0442\u0432\u043e\u0440\u0447\u0435\u0441\u043a\u0438\u0439\u00a0\u2014 \u0442\u0432\u043e\u0440\u0447\u0435\u0441\u043a\u0438'';",
            unitNumber = 7,
            objectiveId = "obj-u7-adverb-pairs",
            governedClaim = "adverb-bounded-pairs",
        )
    )
    val ADMITTED_ADVERB_PARADIGM: Set<GovernedSourceAdmission> = emptySet()
    val ADMITTED_QUARTER_HOUR = setOf(
        GovernedSourceAdmission(
            sourceId = "src-pressbooks-diverse-russian-traditions-grammar",
            sourceRevision = "ab2170e37d529a318f395f6e657d8024d0eaed8d7c5c9b34f8831d348b8b76b3",
            sourceRecordId = "pressbooks-traditions-grammar:\u0413\u0440\u0430\u043c\u043c\u0430\u0442\u0438\u043a\u0430: At what time on the quarter hour</strong></p>\n</header>\n<div class=\"",
            sourceHash = "acb19271d29f20803d8790208264b0c98aa9b8948681e029ce1f475694841913",
            exactSpan = "\u0413\u0440\u0430\u043c\u043c\u0430\u0442\u0438\u043a\u0430: At what time on the quarter hour</strong></p>\n</header>\n<div class=\"textbox__content\">\n<table class=\"grid\" style=\"border-collapse: collapse;width: 100%\">\n<thead>\n<tr>\n<td style=\"width: 33.3333%\"><strong>Time</strong></td>\n<td style=\"width: 66.6667%\"><strong>Explanation and examples</strong></td>\n</tr>\n</thead>\n<tbody>\n<tr>\n<td style=\"width: 33.3333%\"><strong>At what time on the hour (0.00)</strong></td>\n<td style=\"width: 66.6667%\">Use <strong>\u0432</strong> + accusative, but note that the number then often requires genitive case after it. In informal speech, sometimes people leave out \u0447\u0430\u0441 / \u0447\u0430\u0441\u0430 / \u0447\u0430\u0441\u043e\u0432. Remember: \u043e\u0434\u0438\u043d \u0447\u0430\u0441; \u0434\u0432\u0430 / \u0442\u0440\u0438 / \u0447\u0435\u0442\u044b\u0440\u0435 \u0447\u0430\u0441\u0430; \u043f\u044f\u0442\u044c \u0447\u0430\u0441\u043e\u0432.\n<p>\u041a\u043e\u043d\u0446\u0435\u0440\u0442 \u043d\u0430\u0447\u0438\u043d\u0430\u0435\u0442\u0441\u044f <strong>\u0432 \u0448\u0435\u0441\u0442\u044c \u0447\u0430\u0441\u043e\u0432</strong>. (18.00)<br>\n\u041e\u043d\u0430 \u0431\u044b\u043b\u0430 \u0434\u043e\u043c\u0430 <strong>\u0432 \u0447\u0430\u0441</strong>. (13.00)</p></td>\n</tr>\n<tr>\n<td style=\"width: 33.3333%\"><strong>At what time on the quarter hour (0.15)</strong></td>\n<td style=\"width: 66.6667%\">Use <strong>\u0432</strong> + accusative with either <strong>\u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c</strong> (quarter) or <strong>\u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442</strong>. Then you use the ordinal number of the next hour in the genitive case. In essence you are saying quarter of the next hour. Note that while you can say 6:15 as \u201c\u0448\u0435\u0441\u0442\u044c \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c\u201d, in the exercises on this page we would like to focus on this other way of expressing the time.\n<p>\u042f \u043f\u0440\u0438\u0434\u0443 <strong>\u0432 \u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c \u0432\u043e\u0441\u044c\u043c\u043e\u0433\u043e</strong>. (7.15 \u2013 quarter of the eighth hour)<br>\n\u041e\u043d \u043d\u0430\u043c \u043f\u043e\u0437\u0432\u043e\u043d\u0438\u043b <strong>\u0432 \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442 \u0434\u0435\u0441\u044f\u0442\u043e\u0433\u043e</strong>. (9.15 \u2013 15 minutes of the tenth hour)</p>\n<p>If you need to review the ordinal numbers up to twelve, you can find them here:</p>\n<div id=\"h5p-256\">\n<div class=\"h5p-iframe-wrapper\"><iframe id=\"h5p-iframe-256\" class=\"h5p-iframe\" data-content-id=\"256\" style=\"height:1px\" src=\"about:blank\" frameborder=\"0\" scrolling=\"no\" title=\"Ordinal numbers\"></iframe></div>\n</div>\n</td>\n</tr>\n<tr>\n<td style=\"width: 33.3333%\"><strong>At what time on the half hour (0.30)</strong></td>\n<td style=\"width: 66.6667%\">Use <strong>\u0432</strong> + prepositional with <strong>\u043f\u043e\u043b\u043e\u0432\u0438\u043d\u0430</strong> and the genitive case of the ordinal number of the next hour, or colloquially, <strong>\u043f\u043e\u043b_____</strong> (genitive case of the ordinal number of the next hour). Note that the spelling of 10:30 adds a hyphen: \u043f\u043e\u043b-\u043e\u0434\u0438\u043d\u043d\u0430\u0434\u0446\u0430\u0442\u043e\u0433\u043e.\n<p>\u041d\u0430\u0448 \u0440\u0435\u0439\u0441 \u0443\u0435\u0437\u0436\u0430\u0435\u0442 <strong>\u0432 \u043f\u043e\u043b\u043e\u0432\u0438\u043d\u0435 \u043f\u044f\u0442\u043e\u0433\u043e</strong>. (4.30 \u2013 half of the fifth hour)<br>\n\u041c\u044b \u0431\u0443\u0434\u0435\u043c \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u044b <strong>\u0432 \u043f\u043e\u043b\u0432\u0442\u043e\u0440\u043e\u0433\u043e</strong>. (1.30 \u2013 half of the second hour)</p>\n<p>If you need to review the ordinal numbers up to twelve, you can find them here:</p>\n<div id=\"h5p-256\">\n<div class=\"h5p-iframe-wrapper\"><iframe id=\"h5p-iframe-256\" class=\"h5p-iframe\" data-content-id=\"256\" style=\"height:1px\" src=\"about:blank\" frameborder=\"0\" scrolling=\"no\" title=\"Ordinal numbers\"></iframe></div>\n</div>\n</td>\n</tr>\n<tr>\n<td style=\"width: 33.3333%\"><strong>At what time on the three quarters hour (0.45)</strong></td>\n<td style=\"width: 66.6667%\">Use <strong>\u0431\u0435\u0437</strong> + genitive with <strong>\u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c</strong> or <strong>\u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442</strong>. Then you use the cardinal number of the next hour in the nominative case. In essence you are saying X hours without X minutes.\n<p>\u041f\u0435\u0440\u0435\u0434\u0430\u0447\u0430 \u043d\u0430\u0447\u0438\u043d\u0430\u0435\u0442\u0441\u044f <strong>\u0431\u0435\u0437 \u0447\u0435\u0442\u0432\u0435\u0440\u0442\u0438 \u0442\u0440\u0438</strong>. (2.45 \u2013 3 without a quarter)<br>\n\u041e\u043d\u0430 \u043e\u0431\u044b\u0447\u043d\u043e \u043f\u0440\u0438\u0445\u043e\u0434\u0438\u0442 \u043d\u0430 \u0440\u0430\u0431\u043e\u0442\u0443 <strong>\u0431\u0435\u0437 \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u0438 \u0434\u0435\u0432\u044f\u0442\u044c</strong>. (8.45 \u2013 9 without 15 minutes)</p>",
            unitNumber = 17,
            objectiveId = "obj-u17-quarter-hour",
            governedClaim = "productive-quarter-hour-time",
        )
    )
    val ADMITTED_PROBABILITY_DOLZHEN = setOf(
        GovernedSourceAdmission(
            sourceId = "src-enwiktionary-dolzhen-84965088",
            sourceRevision = "84965088",
            sourceRecordId = "wiktionary-dolzhen:# {{lb|ru|with a {{lg|verb}}}} {{n-g|Used to express [[likelihood]]. Usually tra",
            sourceHash = "56264aa3e877023aca19039f728b603db4678eac1344b20211ebe92ffb175a28",
            exactSpan = "# {{lb|ru|with a {{lg|verb}}}} {{n-g|Used to express [[likelihood]]. Usually translated as \"[[should]]\".}}\n#: {{syn|ru|\u0432\u0435\u0440\u043e\u044f\u0301\u0442\u043d\u043e|\u043d\u0430\u0432\u0435\u0301\u0440\u043d\u043e\u0435|\u043d\u0430\u0432\u0435\u0440\u043d\u044f\u043a\u0430\u0301|\u0441\u043a\u043e\u0440\u0435\u0301\u0435 \u0432\u0441\u0435\u0433\u043e\u0301}}\n#: {{uxi|ru|[[\u0437\u0430\u0442\u043c\u0435\u043d\u0438\u0435|\u0417\u0430\u0442\u043c\u0435\u0301\u043d\u0438\u0435]] '''\u0434\u043e\u043b\u0436\u043d\u043e\u0301''' [[\u043d\u0430\u0441\u0442\u0443\u043f\u0438\u0301\u0442\u044c]] [[\u0432]] [[\u0442\u0440\u0438]] [[\u0447\u0430\u0441|\u0447\u0430\u0441\u0430\u0301]].|The eclipse should occur at three o'clock.}}",
            unitNumber = 19,
            objectiveId = "obj-u19-probability",
            governedClaim = "probability-dolzhen",
        )
    )

    /**
     * The unique wording written immediately after the em dash and before the colon.
     * A later clause in the same frozen sentence is not this wording.
     */
    fun dashColonWording(span: String): String? {
        val dash = span.indexOf('\u2014')
        if (dash < 0) return null
        val colon = span.indexOf(':', dash + 1)
        if (colon < 0) return null
        val wording = span.substring(dash + 1, colon).trim()
        if (wording.isEmpty() || wording.contains('\u2014') || span.indexOf(wording) != span.lastIndexOf(wording)) {
            return null
        }
        return wording
    }

    fun admits(capability: V2SourceCapability, admitted: Set<GovernedSourceAdmission>, claim: String): Boolean {
        val recorded = GovernedSourceAdmission(
            sourceId = capability.sourceId,
            sourceRevision = capability.sourceRevision,
            sourceRecordId = capability.sourceRecordId,
            sourceHash = capability.sourceHash,
            exactSpan = capability.exactSpan,
            unitNumber = capability.unitNumber,
            objectiveId = capability.objectiveId,
            governedClaim = claim,
        )
        return recorded in admitted
    }

    fun unfoldWiktionary(text: String): String {
        var value = text.replace("{{lb|ru|with a {{lg|verb}}}}", "(with a verb)")
        value = Regex("""\{\{n-g\|((?:[^{}]|\{\{[^{}]*\}\})*)\}\}""").replace(value) { it.groupValues[1] }
        value = Regex("""\{\{uxi\|ru\|(.*)\|([^|{}]*)\}\}""").replace(value) { match ->
            val russian = V2Validator.unfoldWikiLabels(match.groupValues[1]).trim()
            val gloss = match.groupValues[2].trim()
            russian + "\n" + gloss
        }
        return V2Validator.unfoldWikiLabels(value)
    }

    fun htmlVisible(text: String): String {
        val breaks = Regex("(?i)<br\\s*/?>").replace(text, "\n")
        val stripped = Regex("<[^>]+>").replace(breaks, "")
        return stripped
            .replace("&nbsp;", "\u00a0")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
    }
}
