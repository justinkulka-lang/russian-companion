"""Batch A spans for Units 7 and 16–20.

Every shipped Russian or English span is an exact frozen substring. This module
does not decline, conjugate, or compose new Russian. Mnemonic stories, the
dolphin aside, and bibliography lines are not shipped.
"""

from pathlib import Path

from units_11_15_lessons import _shared_response_kind, _wiki
from units_26_30_lessons import _check, _span_in
from explicit_spans import CLOCK_SPAN, DATE_SPAN
from units_3_5_intake import format_wikibooks_emphasis, text_in_frozen, unfold_wiki_labels

_ROOT = Path(__file__).resolve().parents[1]
_PAGE_FILE = {
    "grammar": _ROOT / "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt",
    "days": _ROOT / "catalog/sources/units-16-20/wikibooks/days-3594124.txt",
    "months": _ROOT / "catalog/sources/units-16-20/wikibooks/months-4376719.txt",
    "interrogative": _ROOT / "catalog/sources/units-3-5/wikibooks/interrogative-4220316.txt",
    "lesson3": _ROOT / "catalog/sources/units-16-20/wikibooks/lesson3-4460699.txt",
    "clock-country": _ROOT / "catalog/sources/units-16-20/wikipedia/date-time-country-1366690592.txt",
    "date-russia": _ROOT / "catalog/sources/units-16-20/wikipedia/date-notation-russia-1357134172.txt",
}
CLOCK_SOURCE = "src-wikipedia-date-time-country-1366690592"
DATE_SOURCE = "src-wikipedia-date-notation-russia-1357134172"


def _span(page: str, start: str, end: str) -> str:
    return _span_in(_PAGE_FILE[page].read_text(encoding="utf-8"), start, end, page)

WIKI = "src-wikibooks-russian-cc-by-sa"
WIKIPEDIA = "src-wikipedia-russian-grammar-1369617627"
PROMPT = "Which admitted English gloss is written beside the shown form?"
FORBIDDEN = (
    "dolphin",
    "pronounced",
    "Remember that",
    "pony",
    "pizza",
    "Sabbath",
    "Wade",
    "Blackwell",
    "ponies",
)


def _form(needle: str, raw: str) -> str:
    shipped = format_wikibooks_emphasis(raw)
    if not text_in_frozen(needle, shipped):
        raise SystemExit(f"Shipped text is not in the frozen span: {shipped!r}")
    for banned in FORBIDDEN:
        if banned.casefold() in shipped.casefold():
            raise SystemExit(f"Shipped text keeps excluded commentary: {shipped!r}")
    if "'''" in shipped or "{{" in shipped or "[[" in shipped or ".ogg" in shipped:
        raise SystemExit(f"Shipped text keeps markup or audio: {shipped!r}")
    return shipped


def _require(needle: str, anchor: str) -> str:
    if anchor not in needle:
        raise SystemExit(f"Anchor is not in the frozen span: {anchor!r}")
    return anchor


def _frame(lesson) -> None:
    """English frame of one complete pair sentence. The quoted source strings stay provenance."""
    lesson.builder.say(lesson.number, "The frozen source pairs the shown form")
    lesson.builder.say(lesson.number, "with the admitted gloss")


def _pair(shown: str, answer: str, citation: str = "") -> str:
    if '"' in shown or '"' in answer or '"' in citation:
        raise SystemExit(f"Pair quotation would split {shown!r} / {answer!r}")
    text = f'The frozen source pairs the shown form "{shown}" with the admitted gloss "{answer}"'
    if citation:
        text += f' ("{citation}")'
    text += "."
    if text.endswith("?") or "The frozen line writes" in text or text.count("The frozen source pairs") != 1:
        raise SystemExit(f"Pair sentence is not one complete explanation: {text!r}")
    if shown not in text or answer not in text:
        raise SystemExit(f"Explanation omits the keyed relationship {shown!r} -> {answer!r}: {text!r}")
    return text


def _why(anchor: str, shown: str, answer: str) -> str:
    if answer not in anchor:
        raise SystemExit(f"Anchor omits the keyed answer {answer!r}: {anchor!r}")
    if shown in anchor:
        text = anchor
    else:
        text = _pair(shown, answer)
    if "The frozen line writes" in text or text.endswith("?"):
        raise SystemExit(f"Explanation is not a complete sentence: {text!r}")
    if shown not in text or answer not in text:
        raise SystemExit(f"Explanation omits the keyed relationship {shown!r} -> {answer!r}: {text!r}")
    return text


def _other(anchor: str, gloss: str) -> tuple[str, str, str]:
    if gloss not in anchor:
        raise SystemExit(f"Competing anchor omits {gloss!r}: {anchor!r}")
    return (gloss, anchor, anchor)


def _quoted(anchor: str, gloss: str) -> tuple[str, str, str]:
    if gloss not in anchor:
        raise SystemExit(f"Competing anchor omits {gloss!r}: {anchor!r}")
    return (gloss, anchor, anchor)


def _gloss_check(
    shown: str,
    answer: str,
    wrongs: list[tuple[str, str, str]],
    feature: str,
    why: str,
    source: str,
    quote: bool = False,
    prompt: str = PROMPT,
) -> dict:
    if answer not in why or shown not in why:
        raise SystemExit(f"Explanation does not name {shown!r} -> {answer!r}: {why!r}")
    if len(why) < 12:
        raise SystemExit(f"Feedback window is too short: {why!r}")
    for text, rationale, difference in wrongs:
        if text not in rationale or text not in difference:
            raise SystemExit(f"Distractor explanation omits {text!r}: {rationale!r} / {difference!r}")
        if text not in answer and text in why:
            raise SystemExit(f"Explanation names competing gloss {text!r}: {why!r}")
        if answer not in text and answer in rationale:
            raise SystemExit(f"Distractor explanation names the keyed answer {answer!r}: {rationale!r}")
    payload = _check(
        prompt,
        shown,
        answer,
        wrongs,
        source=source,
        feature=feature,
        why=why,
        response_kind=_shared_response_kind([answer, *[text for text, _rationale, _difference in wrongs]]),
    )
    payload["frozenQuote"] = True
    if quote and not payload["frozenQuote"]:
        raise SystemExit("Frozen quote flag was dropped")
    return payload


def _bind(lesson, slug: str, claim: str, needle: str) -> None:
    lesson.capabilities.append({
        "objectiveId": f"obj-u{lesson.number}-{slug}",
        "supportedClaim": claim,
        "exactSpan": needle,
    })


def _extra(page: str, needle: str, texts: list[str]) -> list[tuple]:
    rows = []
    for text in texts:
        rows.append((page, needle, _form(needle, text), "label"))
    return rows


def add_batch(lesson) -> None:
    {
        16: _complements,
        17: _calendar_partial,
        18: _comparison_and_relative,
        19: _conditional_and_obligation,
        20: _motion,
    }[lesson.number](lesson)


def _complements(lesson) -> None:
    _frame(lesson)
    needle = _span("grammar", "associates of connective verbs:", "turn out to be")
    become = _form(needle, "стать")
    remain = _form(needle, "остаться")
    seem = _form(needle, "казаться")
    turn = _form(needle, "оказаться")
    became = _form(needle, "became")
    remain_en = _form(needle, "remain")
    seem_en = _form(needle, "appear to be")
    turn_en = _form(needle, "turn out to be")
    citation = _require(needle, "associates of connective verbs")
    _require(needle, "стать}} 'became'")
    _require(needle, "остаться}} 'remain'")
    _require(needle, "оказаться}} 'turn out to be")
    became_why = _pair(become, became, citation)
    remain_why = _pair(remain, remain_en, citation)
    turn_why = _pair(turn, turn_en, citation)
    lesson.objective(
        "complements",
        "Choose the admitted gloss beside the shown complement",
        "CONTRAST_SELECTION",
        ("grammar", needle, "associates of connective verbs"),
        [
            _wiki("grammar", needle, become, became),
            _wiki("grammar", needle, remain, remain_en),
            _wiki("grammar", needle, seem, seem_en),
            _wiki("grammar", needle, turn, turn_en),
        ],
        [
            _gloss_check(become, became, [_quoted(remain_why, remain_en), _quoted(turn_why, turn_en)], "COMPLEMENT_BECAME", became_why, WIKIPEDIA),
            _gloss_check(remain, remain_en, [_quoted(became_why, became), _quoted(turn_why, turn_en)], "COMPLEMENT_REMAIN", remain_why, WIKIPEDIA),
            _gloss_check(turn, turn_en, [_quoted(became_why, became), _quoted(remain_why, remain_en)], "COMPLEMENT_TURN", turn_why, WIKIPEDIA),
        ],
        extra=_extra("grammar", needle, [citation]),
    )
    _bind(lesson, "complements", "instrumental-complements", needle)


def _calendar_partial(lesson) -> None:
    _frame(lesson)
    lesson.builder.say(lesson.number, "The heading")
    lesson.builder.say(lesson.number, "labels the section. It is not the gloss of the shown form.")
    age = _span("grammar", "age statements:", "20 years")
    days = _span("days", "Russians don't capitalize", "воскресенье || noun || Sunday")
    months = _span("months", "==Months of the year==", "Декабрь || December")
    clock = _span("interrogative", "What time is it?", "'''Которую книгу вы хотите?'''")
    age_ru = _form(age, "мне двадцать лет")
    years_ru = _form(age, "двадцать лет")
    age_en = _form(age, "I am 20 (years old)")
    literal = _form(age, "to_me (is) 20 years")
    years_en = _form(age, "20 years")
    heading = _form(age, "age statements")
    _require(age, "мне двадцать лет}} – 'I am 20 (years old)'")
    years_anchor = _require(age, "20 years")
    literal_row = _require(age, "to_me (is) 20 years")
    age_why = _pair(age_ru, age_en)
    years_why = _pair(years_ru, years_en)
    literal_why = _pair(age_ru, literal)
    heading_why = 'The heading "age statements" labels the section. It is not the gloss of the shown form.'
    lesson.objective(
        "age",
        "Choose the admitted gloss beside the shown age line",
        "CONTRAST_SELECTION",
        ("grammar", age, "I am 20 (years old)"),
        [
            _wiki("grammar", age, age_ru, age_en),
            _wiki("grammar", age, years_ru, years_en),
        ],
        [
            _gloss_check(age_ru, age_en, [_quoted(literal_why, literal)], "AGE_STATEMENT", age_why, WIKIPEDIA),
            _gloss_check(age_ru, age_en, [_quoted(heading_why, heading)], "AGE_HEADING", age_why, WIKIPEDIA),
            _gloss_check(years_ru, years_en, [_quoted(heading_why, heading)], "AGE_YEARS", years_why, WIKIPEDIA),
        ],
        extra=_extra("grammar", age, [literal, heading]),
    )
    _bind(lesson, "age", "age", age)

    pairs = [
        ("понедельник", "Monday"),
        ("вторник", "Tuesday"),
        ("среда", "Wednesday"),
        ("четверг", "Thursday"),
        ("пятница", "Friday"),
        ("суббота", "Saturday"),
        ("воскресенье", "Sunday"),
    ]
    shown = {ru: _form(days, ru) for ru, _en in pairs}
    gloss = {en: _form(days, en) for _ru, en in pairs}
    day_row = {ru: _require(days, f"{ru} || noun || {en}") for ru, en in pairs}
    lesson.objective(
        "weekdays",
        "Choose the admitted gloss beside the shown weekday",
        "CONTRAST_SELECTION",
        ("days", days, "Russians don't capitalize days of the week."),
        [
            _wiki("days", days, shown[ru], gloss[en])
            for ru, en in pairs[:4]
        ],
        [
            _gloss_check(shown["понедельник"], gloss["Monday"], [_quoted(day_row["вторник"], gloss["Tuesday"]), _quoted(day_row["среда"], gloss["Wednesday"])], "WEEKDAY_MONDAY", day_row["понедельник"], WIKI, quote=True),
            _gloss_check(shown["четверг"], gloss["Thursday"], [_quoted(day_row["пятница"], gloss["Friday"]), _quoted(day_row["суббота"], gloss["Saturday"])], "WEEKDAY_THURSDAY", day_row["четверг"], WIKI, quote=True),
            _gloss_check(shown["воскресенье"], gloss["Sunday"], [_quoted(day_row["понедельник"], gloss["Monday"]), _quoted(day_row["суббота"], gloss["Saturday"])], "WEEKDAY_SUNDAY", day_row["воскресенье"], WIKI, quote=True),
        ],
        extra=_extra("days", days, ["пятница", "суббота", "воскресенье", "Friday", "Saturday", "Sunday", "понедельник || noun || Monday", "вторник || noun || Tuesday", "среда || noun || Wednesday", "четверг || noun || Thursday", "пятница || noun || Friday", "суббота || noun || Saturday", "воскресенье || noun || Sunday"]),
    )
    _bind(lesson, "weekdays", "weekdays", days)

    month_pairs = [
        ("Январь", "January"),
        ("Февраль", "February"),
        ("Март", "March"),
        ("Апрель", "April"),
        ("Июнь", "June"),
        ("Июль", "July"),
        ("Август", "August"),
        ("Декабрь", "December"),
    ]
    month_ru = {ru: _form(months, ru) for ru, _en in month_pairs}
    month_en = {en: _form(months, en) for _ru, en in month_pairs}
    month_row = {ru: _require(months, f"{ru} || {en}") for ru, en in month_pairs}
    lesson.objective(
        "months",
        "Choose the admitted gloss beside the shown month",
        "CONTRAST_SELECTION",
        ("months", months, "Months of the year"),
        [
            _wiki("months", months, month_ru[ru], month_en[en])
            for ru, en in month_pairs[:4]
        ],
        [
            _gloss_check(month_ru["Январь"], month_en["January"], [_other(month_row["Февраль"], month_en["February"]), _other(month_row["Март"], month_en["March"])], "MONTH_JANUARY", _why(month_row["Январь"], month_ru["Январь"], month_en["January"]), WIKI),
            _gloss_check(month_ru["Июнь"], month_en["June"], [_other(month_row["Июль"], month_en["July"]), _other(month_row["Август"], month_en["August"])], "MONTH_JUNE", _why(month_row["Июнь"], month_ru["Июнь"], month_en["June"]), WIKI),
            _gloss_check(month_ru["Декабрь"], month_en["December"], [_other(month_row["Январь"], month_en["January"]), _other(month_row["Апрель"], month_en["April"])], "MONTH_DECEMBER", _why(month_row["Декабрь"], month_ru["Декабрь"], month_en["December"]), WIKI),
        ],
        extra=_extra("months", months, ["Июнь", "Июль", "Август", "Декабрь", "June", "July", "August", "December"]),
    )
    _bind(lesson, "months", "months", months)

    clock_ru = _form(clock, "'''Который час?'''")
    book_ru = _form(clock, "'''Которую книгу вы хотите?'''")
    clock_word = _form(clock, "час")
    clock_en = _form(clock, "What time is it?")
    book_en = _form(clock, "Which book do you want?")
    _require(clock, "What time is it? - '''Который час?'''")
    book_row = _require(clock, "Which book do you want?")
    clock_why = _pair(clock_ru, clock_en)
    book_why = _pair(book_ru, book_en)
    lesson.objective(
        "clock",
        "Choose the admitted gloss beside the shown clock question",
        "CONTRAST_SELECTION",
        ("interrogative", clock, "What time is it?"),
        [
            _wiki("interrogative", clock, clock_ru, clock_en),
            _wiki("interrogative", clock, clock_word, _form(clock, "time")),
        ],
        [
            _gloss_check(clock_ru, clock_en, [_quoted(book_why, book_en)], "CLOCK_QUESTION", clock_why, WIKI),
            _gloss_check(clock_ru, clock_en, [_quoted(book_why, book_en)], "CLOCK_QUESTION_AGAIN", clock_why, WIKI),
            _gloss_check(clock_ru, clock_en, [_quoted(book_why, book_en)], "CLOCK_QUESTION_CHECK", clock_why, WIKI),
        ],
        extra=_extra("interrogative", clock, ["Which book do you want?", book_ru]),
    )
    _bind(lesson, "clock", "clock-question", clock)
    _clock_answers(lesson)
    _calendar_date(lesson)


def _clock_answers(lesson) -> None:
    needle = CLOCK_SPAN
    if needle != _span("clock-country", "uses the same convention:", "7:45 respectively"):
        raise SystemExit("Clock span identity drifted")
    forms = (
        ("четверть восьмого", "7:15", "quarter of the eighth hour", "CLOCK_QUARTER"),
        ("полвосьмого", "7:30", "half of eight", "CLOCK_HALF"),
        ("без четверти восемь", "7:45", "eight without a quarter", "CLOCK_WITHOUT"),
    )
    reasons = {
        answer: _pair(shown, answer, citation)
        for shown, answer, citation, _feature in forms
    }
    lesson.objective(
        "clock-answers",
        "Choose the admitted time beside the shown form",
        "CONTRAST_SELECTION",
        ("clock-country", needle, unfold_wiki_labels(needle)),
        [
            _wiki("clock-country", needle, _form(needle, shown), _form(needle, answer))
            for shown, answer, _citation, _feature in forms
        ],
        [
            _gloss_check(
                _form(needle, shown),
                _form(needle, answer),
                [
                    _quoted(reasons[other], other)
                    for _other_shown, other, _other_citation, _other_feature in forms
                    if other != answer
                ],
                feature,
                reasons[answer],
                CLOCK_SOURCE,
            )
            for shown, answer, _citation, feature in forms
        ],
        extra=_extra("clock-country", needle, [citation for _shown, _answer, citation, _feature in forms]),
    )
    _bind(lesson, "clock-answers", "clock-expression-recognition", needle)
    from batch_a_remaining_intake import add_unit17_quarter
    from remaining_lessons import add_clock_productive

    add_unit17_quarter(lesson)
    add_clock_productive(lesson)


def _calendar_date(lesson) -> None:
    needle = DATE_SPAN
    if needle != _span("date-russia", "In [[Russia]], dates are usually written", 'Двадцать восьмое августа}}").'):
        raise SystemExit("Calendar span identity drifted")
    spoken = "Двадцать восьмое августа"
    written = "28 августа 2017 г."
    genitive = "the month in genitive case"
    order = "day month year"
    year = "of the year"
    numeric = "28.08.17"
    spoken_citation = "the ordinal number of the day first"
    order_citation = "dates are usually written"
    year_citation = "is short for"
    numeric_citation = "the all-numeric date"
    for anchor in (spoken, written, genitive, order, year, numeric, spoken_citation, order_citation, year_citation, numeric_citation):
        if not text_in_frozen(needle, anchor):
            raise SystemExit(f"Calendar anchor is not in the frozen span: {anchor!r}")
    spoken_why = _pair(spoken, genitive, spoken_citation)
    order_why = _pair(written, order, order_citation)
    year_why = _pair(written, year, year_citation)
    numeric_why = _pair(numeric, numeric_citation)
    lesson.objective(
        "calendar-date",
        "Choose the admitted date relation beside the shown form",
        "CONTRAST_SELECTION",
        ("date-russia", needle, unfold_wiki_labels(needle)),
        [
            _wiki("date-russia", needle, spoken, genitive),
            _wiki("date-russia", needle, written, order),
        ],
        [
            _gloss_check(spoken, genitive, [_quoted(order_why, order), _quoted(numeric_why, numeric)], "DATE_SPOKEN", spoken_why, DATE_SOURCE),
            _gloss_check(written, order, [_quoted(spoken_why, genitive), _quoted(numeric_why, numeric)], "DATE_ORDER", order_why, DATE_SOURCE),
            _gloss_check(written, year, [_quoted(order_why, order), _quoted(numeric_why, numeric)], "DATE_YEAR", year_why, DATE_SOURCE),
        ],
        extra=_extra("date-russia", needle, [year, numeric, spoken_citation, order_citation, year_citation, numeric_citation]),
    )
    _bind(lesson, "calendar-date", "calendar-date", needle)


def _comparison_and_relative(lesson) -> None:
    _frame(lesson)
    comparison = _span("grammar", "forms are usual only for qualitative", "special comparative forms exist.")
    relative = _span("interrogative", "Which apple is mine?", "изменил мир")
    forms = [
        ("бо́льший", "bigger"),
        ("лу́чший", "better"),
        ("красне́е", "redder"),
        ("наибо́льший", "the biggest"),
        ("бо́лее", "more"),
        ("ме́нее", "less"),
        ("наиме́нее", "least"),
        ("бо́лее до́брый", "kinder"),
    ]
    ru = {raw: _form(comparison, raw) for raw, _en in forms}
    en = {gloss: _form(comparison, gloss) for _raw, gloss in forms}
    bigger_row = _require(comparison, "бо́льший 'bigger'")
    better_row = _require(comparison, "лу́чший 'better'")
    redder_row = _require(comparison, "красне́е 'redder'")
    biggest_row = _require(comparison, "наибо́льший 'the biggest'")
    more_row = _require(comparison, "бо́лее 'more'")
    less_row = _require(comparison, "ме́нее 'less'")
    least_row = _require(comparison, "наиме́нее 'least'")
    kinder_row = _require(comparison, "бо́лее до́брый 'kinder'")
    lesson.objective(
        "comparison",
        "Choose the admitted gloss beside the shown comparison",
        "CONTRAST_SELECTION",
        ("grammar", comparison, "forms are usual only for qualitative"),
        [
            _wiki("grammar", comparison, ru[raw], en[gloss])
            for raw, gloss in forms[:4]
        ],
        [
            _gloss_check(ru["бо́льший"], en["bigger"], [_other(better_row, en["better"]), _other(redder_row, en["redder"])], "COMPARISON_BIGGER", _why(bigger_row, ru["бо́льший"], en["bigger"]), WIKIPEDIA),
            _gloss_check(ru["наибо́льший"], en["the biggest"], [_other(more_row, en["more"]), _other(less_row, en["less"])], "COMPARISON_BIGGEST", _why(biggest_row, ru["наибо́льший"], en["the biggest"]), WIKIPEDIA),
            _gloss_check(ru["бо́лее до́брый"], en["kinder"], [_other(least_row, en["least"]), _other(bigger_row, en["bigger"])], "COMPARISON_KINDER", _why(kinder_row, ru["бо́лее до́брый"], en["kinder"]), WIKIPEDIA),
        ],
        extra=_extra("grammar", comparison, [
            "бо́лее до́брый", "more", "less", "least", "kinder", "redder", "better",
            bigger_row, better_row, redder_row, biggest_row, more_row, less_row, least_row, kinder_row,
        ]),
    )
    _bind(lesson, "comparison", "comparison", comparison)

    apple_ru = _form(relative, "'''Которое яблоко моё?'''")
    book_ru = _form(relative, "'''Которую книгу вы хотите?'''")
    woman_ru = _form(relative, "'''Же́нщина, с кото́рой я дружу́'''")
    man_ru = _form(relative, "Человек, который изменил мир")
    apple_en = _form(relative, "Which apple is mine?")
    book_en = _form(relative, "Which book do you want?")
    woman_en = _form(relative, "The woman (who) I'm friends with")
    man_en = _form(relative, "The man who changed the world")
    _require(relative, "Which apple is mine? - '''Которое яблоко моё?'''")
    _require(relative, "Which book do you want? - '''Которую книгу вы хотите?'''")
    _require(relative, "The woman (who) I'm friends with - '''Же́нщина, с кото́рой я дружу́'''")
    _require(relative, "The man who changed the world - '''Человек, который изменил мир")
    apple_why = _pair(apple_ru, apple_en)
    book_rel_why = _pair(book_ru, book_en)
    woman_why = _pair(woman_ru, woman_en)
    man_why = _pair(man_ru, man_en)
    lesson.objective(
        "relative",
        "Choose the admitted gloss beside the shown relative line",
        "CONTRAST_SELECTION",
        ("interrogative", relative, "The man who changed the world"),
        [
            _wiki("interrogative", relative, apple_ru, apple_en),
            _wiki("interrogative", relative, book_ru, book_en),
            _wiki("interrogative", relative, woman_ru, woman_en),
            _wiki("interrogative", relative, man_ru, man_en),
        ],
        [
            _gloss_check(apple_ru, apple_en, [_quoted(book_rel_why, book_en), _quoted(man_why, man_en)], "RELATIVE_APPLE", apple_why, WIKI),
            _gloss_check(woman_ru, woman_en, [_quoted(man_why, man_en), _quoted(apple_why, apple_en)], "RELATIVE_WOMAN", woman_why, WIKI),
            _gloss_check(man_ru, man_en, [_quoted(woman_why, woman_en), _quoted(book_rel_why, book_en)], "RELATIVE_MAN", man_why, WIKI),
        ],
    )
    _bind(lesson, "relative", "relative-kotoryy", relative)


def _conditional_and_obligation(lesson) -> None:
    _frame(lesson)
    conditional = _span("grammar", "The [[conditional mood]]", "я бы поспала́).")
    obligation = _span("lesson3", '"I have to" translates to я должен', "it's должны.")
    male_a = _form(conditional, "я спал бы")
    male_b = _form(conditional, "я бы поспа́л")
    female_a = _form(conditional, "я спала́ бы")
    female_b = _form(conditional, "я бы поспала́")
    male_en = _form(conditional, "a male speaker")
    female_en = _form(conditional, "a female speaker")
    sleep_en = _form(conditional, "I would like to sleep")
    male_row = _require(conditional, "a male speaker would say я спал бы")
    female_row = _require(conditional, "while a female speaker would say я спала́ бы (or я бы поспала́)")
    sleep_row = _require(conditional, "I would like to sleep")
    lesson.objective(
        "conditional",
        "Choose the admitted gloss beside the shown conditional",
        "CONTRAST_SELECTION",
        ("grammar", conditional, "a male speaker"),
        [
            _wiki("grammar", conditional, male_a, male_en),
            _wiki("grammar", conditional, female_a, female_en),
            _wiki("grammar", conditional, male_b, sleep_en),
            _wiki("grammar", conditional, female_b, female_en),
        ],
        [
            _gloss_check(male_a, male_en, [_other(female_row, female_en), _other(sleep_row, sleep_en)], "CONDITIONAL_MALE", _why(male_row, male_a, male_en), WIKIPEDIA),
            _gloss_check(female_a, female_en, [_other(male_row, male_en), _other(sleep_row, sleep_en)], "CONDITIONAL_FEMALE", _why(female_row, female_a, female_en), WIKIPEDIA),
            _gloss_check(female_b, female_en, [_other(male_row, male_en), _other(sleep_row, sleep_en)], "CONDITIONAL_FEMALE_ALT", _why(female_row, female_b, female_en), WIKIPEDIA),
        ],
    )
    _bind(lesson, "conditional", "conditional-by", conditional)

    must = _form(obligation, "я должен")
    feminine = _form(obligation, "должна")
    neuter = _form(obligation, "должно")
    plural = _form(obligation, "должны")
    have = _form(obligation, "I have to")
    feminine_en = _form(obligation, "If the subject is feminine")
    plural_en = _form(obligation, "If the subject is plural")
    have_row = _require(obligation, '"I have to" translates to я должен')
    feminine_row = _require(obligation, "If the subject is feminine, it's должна.")
    plural_row = _require(obligation, "If the subject is plural, it's должны.")
    lesson.objective(
        "obligation",
        "Choose the admitted gloss beside the shown obligation form",
        "CONTRAST_SELECTION",
        ("lesson3", obligation, "I have to"),
        [
            _wiki("lesson3", obligation, must, have),
            _wiki("lesson3", obligation, feminine, feminine_en),
            _wiki("lesson3", obligation, neuter, _form(obligation, "If the subject is neuter")),
            _wiki("lesson3", obligation, plural, plural_en),
        ],
        [
            _gloss_check(must, have, [_quoted(feminine_row, feminine_en), _quoted(plural_row, plural_en)], "OBLIGATION_HAVE", have_row, WIKI, quote=True),
            _gloss_check(feminine, feminine_en, [_quoted(have_row, have), _quoted(plural_row, plural_en)], "OBLIGATION_FEMININE_FORM", feminine_row, WIKI, quote=True),
            _gloss_check(plural, plural_en, [_quoted(have_row, have), _quoted(feminine_row, feminine_en)], "OBLIGATION_PLURAL_FORM", plural_row, WIKI, quote=True),
        ],
        extra=_extra("lesson3", obligation, [have_row, feminine_row, plural_row])
        + _extra("grammar", conditional, [male_row, female_row, sleep_row]),
    )
    _bind(lesson, "obligation", "obligation-dolzhen", obligation)
    from batch_a_remaining_intake import add_unit19_probability

    add_unit19_probability(lesson)


def _motion(lesson) -> None:
    _frame(lesson)
    lesson.builder.say(lesson.number, "The table header")
    lesson.builder.say(lesson.number, "names the other motion column.")
    lesson.builder.say(lesson.number, "The source quotes")
    lesson.builder.say(lesson.number, "as an English restatement.")
    table = _span("grammar", "! English !! unidirectional !! multidirectional", "тащи́ть || таска́ть")
    setting = _span("grammar", "The addition of the prefix по-", "Он шёл к другу.")
    paths = _span("grammar", "They will leave Vladivostok", "на крышу дома.")
    walk = _form(table, "идти́")
    ride = _form(table, "е́хать")
    carry = _form(table, "нести́")
    lead = _form(table, "вести́")
    convey = _form(table, "везти́")
    uni = _form(table, "unidirectional")
    multi = _form(table, "multidirectional")
    walk_en = _form(table, "to go, walk")
    ride_en = _form(table, "to go by vehicle, ride")
    carry_en = _form(table, "to carry")
    lead_en = _form(table, "to lead")
    convey_en = _form(table, "to convey, transport")
    went = _form(setting, "Он '''по'''шёл к другу.")
    was = _form(setting, "Он шёл к другу.")
    begin = _form(setting, "beginning of a movement")
    set_out = _form(setting, "setting out")
    friend = _form(setting, "He went to a friend's place")
    leave_ru = _form(paths, "Они '''у'''летят из Владивостока через месяц.")
    back_ru = _form(paths, "Мальчик '''ото'''шёл от незнакомца")
    roof_ru = _form(paths, "Мяч '''за'''летел на крышу дома.")
    leave_en = _form(paths, "They will leave Vladivostok in a month.")
    back_en = _form(paths, "The boy stepped back from the stranger who had offered him candy.")
    roof_en = _form(paths, "The ball flew onto the roof of the house.")
    uni_row = _require(table, "unidirectional")
    multi_row = _require(table, "multidirectional")
    walk_row = _require(table, "| to go, walk || идти́")
    ride_row = _require(table, "| to go by vehicle, ride || е́хать")
    carry_row = _require(table, "| to carry || нести́")
    lead_row = _require(table, "| to lead || вести́")
    convey_row = _require(table, "| to convey, transport || везти́")
    begin_row = _require(setting, "beginning of a movement")
    set_row = _require(setting, "setting out")
    friend_row = _require(setting, "He went to a friend's place")
    back_row = _require(paths, "The boy stepped back from the stranger who had offered him candy.")
    leave_row = _require(paths, "They will leave Vladivostok in a month.")
    roof_row = _require(paths, "The ball flew onto the roof of the house.")
    lesson.objective(
        "motion",
        "Choose the admitted gloss beside the shown motion line",
        "CONTRAST_SELECTION",
        ("grammar", table, "unidirectional"),
        [
            _wiki("grammar", table, walk, walk_en),
            _wiki("grammar", table, ride, ride_en),
            _wiki("grammar", table, carry, carry_en),
            _wiki("grammar", table, lead, lead_en),
            _wiki("grammar", setting, went, begin),
            _wiki("grammar", setting, was, friend),
            _wiki("grammar", paths, leave_ru, leave_en),
            _wiki("grammar", paths, roof_ru, roof_en),
        ],
        [
            _gloss_check(walk, uni, [_quoted('The table header "multidirectional" names the other motion column.', multi)], "MOTION_DIRECTION", _why(uni_row, walk, uni), WIKIPEDIA),
            _gloss_check(walk, walk_en, [_other(ride_row, ride_en), _other(carry_row, carry_en)], "MOTION_FOOT", _why(walk_row, walk, walk_en), WIKIPEDIA),
            _gloss_check(carry, carry_en, [_other(lead_row, lead_en), _other(convey_row, convey_en)], "MOTION_CARRY", _why(carry_row, carry, carry_en), WIKIPEDIA),
            _gloss_check(went, begin, [_quoted('The source quotes "setting out" as an English restatement.', set_out), _other(friend_row, friend)], "MOTION_PO", _why(begin_row, went, begin), WIKIPEDIA),
            _gloss_check(back_ru, back_en, [_other(leave_row, leave_en), _other(roof_row, roof_en)], "MOTION_PREFIX", _why(back_row, back_ru, back_en), WIKIPEDIA),
        ],
        extra=_extra("grammar", table, ["unidirectional", "multidirectional", "to lead", "to convey, transport", "везти́"])
        + _extra("grammar", setting, ["setting out", "He went to a friend's place"])
        + _extra("grammar", paths, [
            "The boy stepped back from the stranger who had offered him candy.",
            "They will leave Vladivostok in a month.",
            "Мальчик '''ото'''шёл от незнакомца",
        ]),
    )
    _bind(lesson, "motion", "motion-directionality", table)
    _bind(lesson, "motion", "motion-foot-transport", table)
    _bind(lesson, "motion", "motion-carrying-leading", table)
    _bind(lesson, "motion", "motion-po-setting-out", setting)
    _bind(lesson, "motion", "motion-prefixed-path", paths)
