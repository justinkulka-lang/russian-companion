package dev.russiancompanion.v2.curriculum

/** Exact frozen spans. Equality is the whole retained slice, not a keyword. */
internal object ExplicitSourcePairs {
    val adverbSpan = "most adverbs are formed by removing the adjectival suffixes \"-ий\" \"-а\" or \"-е\" from an adjective, and replacing them with the adverbial \"-о\". For example, in Ukrainian, \"швидкий\", \"гарна\", and \"смачне\" (fast, nice, tasty) become \"швидко\", \"гарно\", and \"смачно\" (quickly, nicely, tastily), while in Russian, \"быстрый\", \"хороший\" and \"прекрасный\" (quick, good, wonderful) become \"быстро\", \"хорошо\", \"прекрасно\" (quickly, well, wonderfully)."
    val clockSpan = "uses the same convention: ''четверть восьмого'' (\"quarter of the eighth hour\"), ''полвосьмого'' (\"half of eight\"), ''без четверти восемь'' (\"eight without a quarter\") meaning 7:15, 7:30, 7:45 respectively"
    val dateSpan = "In [[Russia]], dates are usually written in \"day month year\" (DMY) order. This order is used in both the all-numeric date (for example \"{{lang|ru|28.08.17}}\") and the expanded form (for example \"{{lang|ru|28 августа 2017 г.}}\"); the trailing \"г\" is short for \"года\" (''goda'', \"of the year\"). Coincidentally, in Polish the word for year is \"rok\", so a similar date format is used by the Poles e.g. 1987r). Single-digit numbers for day or month may have a preceding zero (for example \"{{lang|ru|28.08.2017}}\") is more usual.\n\nWhen saying the date, it is usually pronounced using the ordinal number of the day first (in neutral [[grammatical gender]]), then the month in [[genitive case]] (for example \"{{lang|ru|Двадцать восьмое августа}}\")."
    val glosses: Map<String, Map<String, Set<String>>> = mapOf(
        adverbSpan to mapOf(
            "быстро" to setOf("quickly"),
            "хорошо" to setOf("well"),
            "прекрасно" to setOf("wonderfully"),
        ),
        clockSpan to mapOf(
            "четверть восьмого" to setOf("7:15", "quarter of the eighth hour"),
            "полвосьмого" to setOf("7:30", "half of eight"),
            "без четверти восемь" to setOf("7:45", "eight without a quarter"),
        ),
        dateSpan to mapOf(
            "Двадцать восьмое августа" to setOf("the month in genitive case"),
            "28 августа 2017 г." to setOf("day month year", "of the year"),
        ),
    )
}
