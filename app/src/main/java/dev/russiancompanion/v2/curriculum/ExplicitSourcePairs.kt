package dev.russiancompanion.v2.curriculum

/** Exact frozen spans. Equality is the whole retained slice, not a keyword. */
internal object ExplicitSourcePairs {
    val adverbSpan = "most adverbs are formed by removing the adjectival suffixes \"-\u0438\u0439\" \"-\u0430\" or \"-\u0435\" from an adjective, and replacing them with the adverbial \"-\u043e\". For example, in Ukrainian, \"\u0448\u0432\u0438\u0434\u043a\u0438\u0439\", \"\u0433\u0430\u0440\u043d\u0430\", and \"\u0441\u043c\u0430\u0447\u043d\u0435\" (fast, nice, tasty) become \"\u0448\u0432\u0438\u0434\u043a\u043e\", \"\u0433\u0430\u0440\u043d\u043e\", and \"\u0441\u043c\u0430\u0447\u043d\u043e\" (quickly, nicely, tastily), while in Russian, \"\u0431\u044b\u0441\u0442\u0440\u044b\u0439\", \"\u0445\u043e\u0440\u043e\u0448\u0438\u0439\" and \"\u043f\u0440\u0435\u043a\u0440\u0430\u0441\u043d\u044b\u0439\" (quick, good, wonderful) become \"\u0431\u044b\u0441\u0442\u0440\u043e\", \"\u0445\u043e\u0440\u043e\u0448\u043e\", \"\u043f\u0440\u0435\u043a\u0440\u0430\u0441\u043d\u043e\" (quickly, well, wonderfully)."
    val clockSpan = "uses the same convention: ''\u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c \u0432\u043e\u0441\u044c\u043c\u043e\u0433\u043e'' (\"quarter of the eighth hour\"), ''\u043f\u043e\u043b\u0432\u043e\u0441\u044c\u043c\u043e\u0433\u043e'' (\"half of eight\"), ''\u0431\u0435\u0437 \u0447\u0435\u0442\u0432\u0435\u0440\u0442\u0438 \u0432\u043e\u0441\u0435\u043c\u044c'' (\"eight without a quarter\") meaning 7:15, 7:30, 7:45 respectively"
    val dateSpan = "In [[Russia]], dates are usually written in \"day month year\" (DMY) order. This order is used in both the all-numeric date (for example \"{{lang|ru|28.08.17}}\") and the expanded form (for example \"{{lang|ru|28 \u0430\u0432\u0433\u0443\u0441\u0442\u0430 2017 \u0433.}}\"); the trailing \"\u0433\" is short for \"\u0433\u043e\u0434\u0430\" (''goda'', \"of the year\"). Coincidentally, in Polish the word for year is \"rok\", so a similar date format is used by the Poles e.g. 1987r). Single-digit numbers for day or month may have a preceding zero (for example \"{{lang|ru|28.08.2017}}\") is more usual.\n\nWhen saying the date, it is usually pronounced using the ordinal number of the day first (in neutral [[grammatical gender]]), then the month in [[genitive case]] (for example \"{{lang|ru|\u0414\u0432\u0430\u0434\u0446\u0430\u0442\u044c \u0432\u043e\u0441\u044c\u043c\u043e\u0435 \u0430\u0432\u0433\u0443\u0441\u0442\u0430}}\")."
    val glosses: Map<String, Map<String, Set<String>>> = mapOf(
        adverbSpan to mapOf(
            "\u0431\u044b\u0441\u0442\u0440\u043e" to setOf("quickly"),
            "\u0445\u043e\u0440\u043e\u0448\u043e" to setOf("well"),
            "\u043f\u0440\u0435\u043a\u0440\u0430\u0441\u043d\u043e" to setOf("wonderfully"),
        ),
        clockSpan to mapOf(
            "\u0447\u0435\u0442\u0432\u0435\u0440\u0442\u044c \u0432\u043e\u0441\u044c\u043c\u043e\u0433\u043e" to setOf("7:15", "quarter of the eighth hour"),
            "\u043f\u043e\u043b\u0432\u043e\u0441\u044c\u043c\u043e\u0433\u043e" to setOf("7:30", "half of eight"),
            "\u0431\u0435\u0437 \u0447\u0435\u0442\u0432\u0435\u0440\u0442\u0438 \u0432\u043e\u0441\u0435\u043c\u044c" to setOf("7:45", "eight without a quarter"),
        ),
        dateSpan to mapOf(
            "\u0414\u0432\u0430\u0434\u0446\u0430\u0442\u044c \u0432\u043e\u0441\u044c\u043c\u043e\u0435 \u0430\u0432\u0433\u0443\u0441\u0442\u0430" to setOf("the month in genitive case"),
            "28 \u0430\u0432\u0433\u0443\u0441\u0442\u0430 2017 \u0433." to setOf("day month year", "of the year"),
        ),
    )
}
