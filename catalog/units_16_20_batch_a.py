"""Batch A spans for Units 7 and 16–20.

Every shipped Russian or English span is an exact frozen substring. This module
does not decline, conjugate, or compose new Russian. Mnemonic stories, the
dolphin aside, and bibliography lines are not shipped.
"""

from pathlib import Path

from units_11_15_lessons import _shared_response_kind, _wiki
from units_26_30_lessons import _check, _span_in
from units_3_5_intake import format_wikibooks_emphasis

_ROOT = Path(__file__).resolve().parents[1]
_PAGE_FILE = {
    "grammar": _ROOT / "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt",
    "days": _ROOT / "catalog/sources/units-16-20/wikibooks/days-3594124.txt",
    "months": _ROOT / "catalog/sources/units-16-20/wikibooks/months-4376719.txt",
    "interrogative": _ROOT / "catalog/sources/units-3-5/wikibooks/interrogative-4220316.txt",
    "lesson3": _ROOT / "catalog/sources/units-16-20/wikibooks/lesson3-4460699.txt",
}


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
    formatted = format_wikibooks_emphasis(needle)
    if shipped not in needle and shipped not in formatted:
        raise SystemExit(f"Shipped text is not in the frozen span: {shipped!r}")
    for banned in FORBIDDEN:
        if banned.casefold() in shipped.casefold():
            raise SystemExit(f"Shipped text keeps excluded commentary: {shipped!r}")
    if "'''" in shipped or "{{" in shipped or "[[" in shipped or ".ogg" in shipped:
        raise SystemExit(f"Shipped text keeps markup or audio: {shipped!r}")
    return shipped


def _gloss_check(shown: str, answer: str, wrongs: list[str], feature: str, why: str, source: str) -> dict:
    options = [answer, *wrongs]
    kind = _shared_response_kind(options)
    distractors = []
    for index, text in enumerate(wrongs, start=1):
        _form_ok = text  # already checked by the caller
        del _form_ok
        rationale = f"The frozen line writes other gloss {index} beside a different shown form."
        difference = f"The frozen line writes other gloss {index} beside another shown form."
        distractors.append((text, rationale, difference))
    if len(why) < 12:
        raise SystemExit(f"Feedback window is too short: {why!r}")
    return _check(
        PROMPT,
        shown,
        answer,
        distractors,
        source=source,
        feature=feature,
        why=why,
        response_kind=kind,
    )


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
    needle = _span("grammar", "associates of connective verbs:", "turn out to be")
    become = _form(needle, "стать")
    remain = _form(needle, "остаться")
    seem = _form(needle, "казаться")
    turn = _form(needle, "оказаться")
    became = _form(needle, "became")
    remain_en = _form(needle, "remain")
    seem_en = _form(needle, "appear to be")
    turn_en = _form(needle, "turn out to be")
    why = "The frozen line writes appear to be beside the shown form."
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
            _gloss_check(become, became, [remain_en, turn_en], "COMPLEMENT_BECAME", why, WIKIPEDIA),
            _gloss_check(remain, remain_en, [became, turn_en], "COMPLEMENT_REMAIN", why, WIKIPEDIA),
            _gloss_check(turn, turn_en, [became, remain_en], "COMPLEMENT_TURN", why, WIKIPEDIA),
        ],
    )
    _bind(lesson, "complements", "instrumental-complements", needle)


def _calendar_partial(lesson) -> None:
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
    why_age = "The frozen line writes I am 20 (years old) beside the shown form."
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
            _gloss_check(age_ru, age_en, [literal], "AGE_STATEMENT", why_age, WIKIPEDIA),
            _gloss_check(age_ru, age_en, [heading], "AGE_HEADING", why_age, WIKIPEDIA),
            _gloss_check(years_ru, years_en, [heading], "AGE_YEARS", why_age, WIKIPEDIA),
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
    why_days = "The frozen line writes days of the week beside the shown form."
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
            _gloss_check(shown["понедельник"], gloss["Monday"], [gloss["Tuesday"], gloss["Wednesday"]], "WEEKDAY_MONDAY", why_days, WIKI),
            _gloss_check(shown["четверг"], gloss["Thursday"], [gloss["Friday"], gloss["Saturday"]], "WEEKDAY_THURSDAY", why_days, WIKI),
            _gloss_check(shown["воскресенье"], gloss["Sunday"], [gloss["Monday"], gloss["Saturday"]], "WEEKDAY_SUNDAY", why_days, WIKI),
        ],
        extra=_extra("days", days, ["пятница", "суббота", "воскресенье", "Friday", "Saturday", "Sunday"]),
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
    why_months = "The frozen line writes Months of the year beside the shown form."
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
            _gloss_check(month_ru["Январь"], month_en["January"], [month_en["February"], month_en["March"]], "MONTH_JANUARY", why_months, WIKI),
            _gloss_check(month_ru["Июнь"], month_en["June"], [month_en["July"], month_en["August"]], "MONTH_JUNE", why_months, WIKI),
            _gloss_check(month_ru["Декабрь"], month_en["December"], [month_en["January"], month_en["April"]], "MONTH_DECEMBER", why_months, WIKI),
        ],
        extra=_extra("months", months, ["Июнь", "Июль", "Август", "Декабрь", "June", "July", "August", "December"]),
    )
    _bind(lesson, "months", "months", months)

    clock_ru = _form(clock, "'''Который час?'''")
    clock_word = _form(clock, "час")
    clock_en = _form(clock, "What time is it?")
    book_en = _form(clock, "Which book do you want?")
    why_clock = "The frozen line writes What time is beside the shown form."
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
            _gloss_check(clock_ru, clock_en, [book_en], "CLOCK_QUESTION", why_clock, WIKI),
            _gloss_check(clock_ru, clock_en, [book_en], "CLOCK_QUESTION_AGAIN", why_clock, WIKI),
            _gloss_check(clock_ru, clock_en, [book_en], "CLOCK_QUESTION_CHECK", why_clock, WIKI),
        ],
        extra=_extra("interrogative", clock, ["Which book do you want?"]),
    )
    _bind(lesson, "clock", "clock-question", clock)


def _comparison_and_relative(lesson) -> None:
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
    why = "The frozen line writes comparative forms beside the shown form."
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
            _gloss_check(ru["бо́льший"], en["bigger"], [en["better"], en["redder"]], "COMPARISON_BIGGER", why, WIKIPEDIA),
            _gloss_check(ru["наибо́льший"], en["the biggest"], [en["more"], en["less"]], "COMPARISON_BIGGEST", why, WIKIPEDIA),
            _gloss_check(ru["бо́лее до́брый"], en["kinder"], [en["least"], en["bigger"]], "COMPARISON_KINDER", why, WIKIPEDIA),
        ],
        extra=_extra("grammar", comparison, ["бо́лее до́брый", "more", "less", "least", "kinder", "redder", "better"]),
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
    why_rel = "The frozen line writes changed the world beside the shown form."
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
            _gloss_check(apple_ru, apple_en, [book_en, man_en], "RELATIVE_APPLE", why_rel, WIKI),
            _gloss_check(woman_ru, woman_en, [man_en, apple_en], "RELATIVE_WOMAN", why_rel, WIKI),
            _gloss_check(man_ru, man_en, [woman_en, book_en], "RELATIVE_MAN", why_rel, WIKI),
        ],
    )
    _bind(lesson, "relative", "relative-kotoryy", relative)


def _conditional_and_obligation(lesson) -> None:
    conditional = _span("grammar", "The [[conditional mood]]", "я бы поспала́).")
    obligation = _span("lesson3", '"I have to" translates to я должен', "it's должны.")
    male_a = _form(conditional, "я спал бы")
    male_b = _form(conditional, "я бы поспа́л")
    female_a = _form(conditional, "я спала́ бы")
    female_b = _form(conditional, "я бы поспала́")
    male_en = _form(conditional, "a male speaker")
    female_en = _form(conditional, "a female speaker")
    sleep_en = _form(conditional, "I would like to sleep")
    why = "The frozen line writes a male speaker beside the shown form."
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
            _gloss_check(male_a, male_en, [female_en, sleep_en], "CONDITIONAL_MALE", why, WIKIPEDIA),
            _gloss_check(female_a, female_en, [male_en, sleep_en], "CONDITIONAL_FEMALE", why, WIKIPEDIA),
            _gloss_check(female_b, female_en, [male_en, sleep_en], "CONDITIONAL_FEMALE_ALT", why, WIKIPEDIA),
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
    why_ob = "The frozen line writes translates to beside the shown form."
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
            _gloss_check(must, have, [feminine_en, plural_en], "OBLIGATION_HAVE", why_ob, WIKI),
            _gloss_check(feminine, feminine_en, [have, plural_en], "OBLIGATION_FEMININE_FORM", why_ob, WIKI),
            _gloss_check(plural, plural_en, [have, feminine_en], "OBLIGATION_PLURAL_FORM", why_ob, WIKI),
        ],
    )
    _bind(lesson, "obligation", "obligation-dolzhen", obligation)


def _motion(lesson) -> None:
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
            _gloss_check(walk, uni, [multi], "MOTION_DIRECTION", "The frozen line writes unidirectional beside the shown form.", WIKIPEDIA),
            _gloss_check(walk, walk_en, [ride_en, carry_en], "MOTION_FOOT", "The frozen line writes to go by vehicle beside the shown form.", WIKIPEDIA),
            _gloss_check(carry, carry_en, [lead_en, convey_en], "MOTION_CARRY", "The frozen line writes to convey, transport beside the shown form.", WIKIPEDIA),
            _gloss_check(went, begin, [set_out, friend], "MOTION_PO", "The frozen line writes beginning of a movement beside the shown form.", WIKIPEDIA),
            _gloss_check(back_ru, back_en, [leave_en, roof_en], "MOTION_PREFIX", "The frozen line writes stepped back beside the shown form.", WIKIPEDIA),
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
