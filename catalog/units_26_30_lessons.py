"""Units 26–30 checks. Every Russian span is an exact frozen substring.

This module does not decline, conjugate, or compose new Russian. Partial strands
do not use the broad required claim ids. Unsupported topics are gaps in the intake.
"""

from pathlib import Path

from units_11_15_lessons import _shared_response_kind
from units_3_5_intake import format_wikibooks_emphasis
from units_6_10_lessons import _check

ROOT = Path(__file__).resolve().parents[1]
_PAGE_FILE = {
    "grammar": ROOT / "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt",
    "alphabet": ROOT / "catalog/sources/units-26-30/wikipedia/russian-alphabet-1371795481.txt",
    "address": ROOT / "catalog/sources/units-26-30/wikibooks/various-stuff-3353858.txt",
}
_CIRCULAR = (
    "does this answer",
    "does this row",
    "does the admitted record show the governed contrast",
)
_PAGES = {key: path.read_text(encoding="utf-8") for key, path in _PAGE_FILE.items()}
SOURCE_IDS = {
    "grammar": "src-wikipedia-russian-grammar-1369617627",
    "alphabet": "src-wikipedia-russian-alphabet-1371795481",
    "address": "src-wikibooks-russian-various-stuff-3353858",
}


def _span(page: str, start: str, end: str) -> str:
    text = _PAGES[page]
    begin = text.find(start)
    finish = text.find(end, begin if begin >= 0 else 0)
    if begin < 0 or finish < 0:
        raise SystemExit(f"Frozen span is missing on {page}: {start!r} .. {end!r}")
    if text.find(start, begin + 1) >= 0 and text.find(start, begin + 1) < finish + len(end):
        pass
    return text[begin:finish + len(end)]


def _wiki(page, needle, ru, en):
    return ("wiki", page, needle, ru, en)


def _extra(page, needle, text):
    return (page, needle, text, "label")


def _clean(page: str, needle: str, shipped: str) -> str:
    if shipped not in needle and shipped not in format_wikibooks_emphasis(needle):
        raise SystemExit(f"Shipped text is not in the frozen {page} span: {shipped!r}")
    if "'''" in shipped or "{{" in shipped or "[[" in shipped or ".ogg" in shipped:
        raise SystemExit(f"Shipped text keeps markup or audio: {shipped!r}")
    return shipped


def _distinction(page, prompt, shown, answer, wrongs, feature, why):
    folded = why.casefold()
    if why.endswith("?") or any(phrase in folded for phrase in _CIRCULAR):
        raise SystemExit(f"Circular feedback is not a source distinction: {why!r}")
    if "which admitted line is the " in prompt.casefold():
        raise SystemExit(f"Prompt discloses a line label: {prompt!r}")
    labels = [text for text, _rationale, _difference in wrongs]
    if len({rationale for _text, rationale, _difference in wrongs}) != len(wrongs):
        raise SystemExit(f"Repeated distractor rationale: {prompt!r}")
    if len({difference for _text, _rationale, difference in wrongs}) != len(wrongs):
        raise SystemExit(f"Repeated distractor difference: {prompt!r}")
    return _check(
        prompt,
        shown,
        answer,
        wrongs,
        source=SOURCE_IDS[page],
        feature=feature,
        why=why,
        response_kind=_shared_response_kind([answer, *labels]),
    )


def populate(lesson, number: int) -> None:
    lesson.capabilities = []
    {
        26: _unit_26,
        27: _unit_27,
        28: _unit_28,
        29: _unit_29,
        30: _unit_30,
    }[number](lesson)


def _bind(lesson, objective: str, claim: str, page: str, needle: str) -> None:
    lesson.capabilities.append({
        "objectiveId": objective,
        "supportedClaim": claim,
        "page": page,
        "exactSpan": needle,
    })


def _unit_26(lesson) -> None:
    order = _span(
        "grammar",
        "Basic word order, both in conversation and written language",
        "Word order can express logical stress, and degree of definiteness.",
    )
    boys = _span(
        "grammar",
        "Word order may also be used for this purpose",
        '("The boy rushed into the room").',
    )
    capital = _span(
        "grammar",
        "The respectful form of the singular",
        "otherwise it begins with minuscule.",
    )
    address = _span(
        "address",
        "The lines are marked with abbreviations:",
        "ул. Петрова, д. 14, кв. 15 <br>",
    )
    for text in (
        "В магазин пошёл я.",
        "Пошёл я в магазин.",
        "Я пошёл в магазин.",
        "To the shop went I",
        "Went I to the shop",
        "I went to the shop",
        "considerable latitude",
    ):
        _clean("grammar", order, text)
    _clean("grammar", boys, "В ко́мнату вбежа́л ма́льчик")
    _clean("grammar", boys, "Ма́льчик вбежа́л в ко́мнату")
    _clean("grammar", capital, "Вы")
    _clean("address", address, "115114 г. Москва")
    lesson.objective(
        "word-order",
        "Choose the admitted label for the shown arrangement",
        "CONTRAST_SELECTION",
        ("grammar", order, "Word order can express logical stress, and degree of definiteness."),
        [
            _wiki("grammar", order, "В магазин пошёл я.", "To the shop went I"),
            _wiki("grammar", order, "Пошёл я в магазин.", "Went I to the shop"),
            _wiki("grammar", order, "Я пошёл в магазин.", "I went to the shop"),
            _wiki("grammar", boys, "В ко́мнату вбежа́л ма́льчик", "Into the room rushed a boy"),
            _wiki("grammar", boys, "Ма́льчик вбежа́л в ко́мнату", "The boy rushed into the room"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label is written beside the shown arrangement?",
                "В магазин пошёл я.",
                "To the shop went I",
                [
                    ("Went I to the shop", "This label is written beside a different arrangement for Went I to the shop.", "The frozen line pairs this label with another arrangement (Went I to the shop)."),
                    ("I went to the shop", "This label is written beside the opening arrangement, not the shown one for I went to the shop.", "The frozen line pairs this label with the opening arrangement (I went to the shop)."),
                ],
                feature="WORD_ORDER",
                why="The frozen line says considerable latitude in word order is allowed. This label is written beside the shown arrangement.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside this shown arrangement?",
                "Пошёл я в магазин.",
                "Went I to the shop",
                [
                    ("To the shop went I", "This label is written beside the closing arrangement, not the shown one for To the shop went I.", "The frozen line pairs this label with the closing arrangement (To the shop went I)."),
                    ("I went to the shop", "This label is written beside the opening arrangement, not the shown one for I went to the shop.", "The frozen line pairs this label with the opening arrangement (I went to the shop)."),
                ],
                feature="WORD_ORDER",
                why="The frozen line says considerable latitude in word order is allowed. This label is written beside the shown arrangement.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside the opening arrangement?",
                "Я пошёл в магазин.",
                "I went to the shop",
                [
                    ("To the shop went I", "This label is written beside the closing arrangement for To the shop went I.", "The frozen line pairs this label with the closing arrangement (To the shop went I)."),
                    ("Went I to the shop", "This label is written beside a story-opening arrangement for Went I to the shop.", "The frozen line pairs this label with a different arrangement (Went I to the shop)."),
                ],
                feature="WORD_ORDER",
                why="The frozen line says considerable latitude in word order is allowed. This label is written beside the shown arrangement.",
            ),
        ],
    )
    _bind(lesson, "obj-u26-word-order", "word-order", "grammar", order)
    lesson.objective(
        "vy-capital",
        "Choose the admitted label for the written capital form",
        "CONTRAST_SELECTION",
        ("grammar", capital, "It begins with a capital letter"),
        [
            _wiki("grammar", capital, "Вы", "capital letter"),
            _wiki("grammar", capital, "Вас", "capital letter"),
            _wiki("grammar", capital, "Вам", "capital letter"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label states how this written form begins?",
                "Вы",
                "capital letter",
                [
                    ("minuscule", "The frozen line gives minuscule to the other situation.", "Minuscule is the other beginning named in the line."),
                    ("plural form", "The frozen line uses this phrase for the shared shape, not for the capital beginning for plural form.", "This phrase is not the beginning named for letters and papers (plural form)."),
                ],
                feature="CAPITAL_FORM",
                why="The frozen line says it begins with a capital letter in personal letters and official papers.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label states how the shown written form begins?",
                "Вас",
                "capital letter",
                [
                    ("plural form", "The frozen line uses this phrase for the shared shape for plural form.", "This phrase does not name the capital beginning (plural form)."),
                    ("minuscule", "The frozen line gives minuscule when the capital situation does not apply.", "Minuscule is the other beginning."),
                ],
                feature="CAPITAL_FORM",
                why="The frozen line says it begins with a capital letter in personal letters and official papers.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label states the beginning used for the shown form?",
                "Вам",
                "capital letter",
                [
                    ("minuscule", "The frozen line gives minuscule to the remaining situation.", "Minuscule is not the beginning named for letters and papers."),
                    ("plural form", "The frozen line uses this phrase for the shared shape for plural form.", "This phrase is a different statement in the same line (plural form)."),
                ],
                feature="CAPITAL_FORM",
                why="The frozen line says it begins with a capital letter in personal letters and official papers.",
            ),
        ],
        extra=[
            _extra("grammar", capital, "minuscule"),
            _extra("grammar", capital, "plural form"),
        ],
    )
    lesson.objective(
        "address-notation",
        "Choose the admitted gloss for the shown address mark",
        "CONTRAST_SELECTION",
        ("address", address, "The lines are marked with abbreviations:"),
        [
            _wiki("address", address, "115114 г. Москва", "city"),
            _wiki("address", address, "ул. Петрова, д. 14, кв. 15", "street"),
            _wiki("address", address, "д.", "building"),
        ],
        [
            _distinction(
                "address",
                "Which admitted English gloss is written for the mark in the shown line?",
                "115114 г. Москва",
                "city",
                [
                    ("street", "The frozen gloss street belongs to a different mark.", "Street is the gloss of another mark in the same note."),
                    ("building", "The frozen gloss building belongs to a different mark.", "Building is the gloss of another mark in the same note."),
                ],
                feature="ADDRESS_MARK",
                why="The lines are marked with abbreviations. This label is the gloss written for the mark in the shown line.",
            ),
            _distinction(
                "address",
                "Which admitted English label glosses the first mark in the shown line?",
                "ул. Петрова, д. 14, кв. 15",
                "street",
                [
                    ("city", "The frozen gloss city belongs to the place mark, not the first mark here.", "City is the gloss of a different mark."),
                    ("apartment", "The frozen gloss apartment belongs to a later mark in the shown line.", "Apartment is the gloss of a later mark."),
                ],
                feature="ADDRESS_MARK",
                why="The lines are marked with abbreviations. This label is the gloss written for the first mark in the shown line.",
            ),
            _distinction(
                "address",
                "Which admitted English gloss is written for the shown mark?",
                "д.",
                "building",
                [
                    ("city", "The frozen gloss city belongs to a different mark.", "City is the gloss of the place mark."),
                    ("street", "The frozen gloss street belongs to a different mark.", "Street is the gloss of a different mark."),
                ],
                feature="ADDRESS_MARK",
                why="The lines are marked with abbreviations. This label is the gloss written for the shown mark.",
            ),
        ],
        extra=[
            _extra("address", address, "building"),
            _extra("address", address, "apartment"),
        ],
    )


def _unit_27(lesson) -> None:
    past = _span(
        "grammar",
        ":сде́лать – to do/to make (perfective verb)",
        "уби́'''т'''ый (killed) / уби́'''т''' (short form)",
    )
    use = _span(
        "grammar",
        "logical subject of passive clause:",
        "the letter was written by Ivan",
    )
    present = _span(
        "grammar",
        ":обсужда́ть – to discuss;",
        "оставля́'''ем'''ый",
    )
    made = _clean("grammar", past, format_wikibooks_emphasis("сде́ла'''нн'''ый"))
    written = _clean("grammar", past, format_wikibooks_emphasis("напи́са'''нн'''ый"))
    killed = _clean("grammar", past, format_wikibooks_emphasis("уби́'''т'''ый"))
    short = _clean("grammar", past, format_wikibooks_emphasis("напи́са'''н'''"))
    discussed = _clean("grammar", present, "обсужда́емый")
    left = _clean("grammar", present, format_wikibooks_emphasis("оставля́'''ем'''ый"))
    lesson.objective(
        "passive",
        "Choose the admitted gloss for the shown past passive form",
        "CONTRAST_SELECTION",
        ("grammar", past, "Passive past participles are formed by means of the suffixes"),
        [
            _wiki("grammar", past, made, "done/made"),
            _wiki("grammar", past, written, "written"),
            _wiki("grammar", past, killed, "killed"),
            _wiki("grammar", past, short, "short form"),
            _wiki("grammar", use, "письмо написано Иваном", "the letter was written by Ivan"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label is written beside the shown form?",
                made,
                "done/made",
                [
                    ("written", "The frozen line writes this label beside a different form for written.", "This label is paired with the writing form (written)."),
                    ("killed", "The frozen line writes this label beside a different form for killed.", "This label is paired with the other listed form (killed)."),
                ],
                feature="PAST_PASSIVE",
                why="The frozen line says these forms are formed by means of the listed suffixes. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside this shown form?",
                written,
                "written",
                [
                    ("done/made", "The frozen line writes this label beside a different form for done/made.", "This label is paired with the making form (done/made)."),
                    ("killed", "The frozen line writes this label beside a different form for killed.", "This label is paired with the other listed form (killed)."),
                ],
                feature="PAST_PASSIVE",
                why="The frozen line says these forms are formed by means of the listed suffixes. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside the remaining shown form?",
                killed,
                "killed",
                [
                    ("written", "The frozen line writes this label beside the writing form for written.", "This label is paired with a different form (written)."),
                    ("done/made", "The frozen line writes this label beside the making form for done/made.", "This label is paired with a different form (done/made)."),
                ],
                feature="PAST_PASSIVE",
                why="The frozen line says these forms are formed by means of the listed suffixes. This label is written beside the shown form.",
            ),
        ],
    )
    _bind(lesson, "obj-u27-passive", "passive-participle", "grammar", past)
    lesson.objective(
        "present-passive",
        "Optional present passive lines",
        "CONTRAST_SELECTION",
        ("grammar", present, "In order to form the passive present participle"),
        [
            _wiki("grammar", present, discussed, "being discussed"),
            _wiki("grammar", present, left, "we leave"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label is written beside the shown form?",
                discussed,
                "being discussed",
                [
                    ("we leave", "The frozen line writes this label beside a different form for we leave.", "This label is paired with the other listed form (we leave)."),
                    ("to discuss", "The frozen line writes this label beside the base, not the shown form for to discuss.", "This label is paired with the base (to discuss)."),
                ],
                feature="PRESENT_PASSIVE",
                why="The frozen line says an ending is added to form the passive present form. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside this shown form?",
                left,
                "we leave",
                [
                    ("being discussed", "The frozen line writes this label beside a different form for being discussed.", "This label is paired with the discussed form (being discussed)."),
                    ("to discuss", "The frozen line writes this label beside the base for to discuss.", "This label is paired with the base, not the shown form (to discuss)."),
                ],
                feature="PRESENT_PASSIVE",
                why="The frozen line says an ending is added to form the passive present form. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label names the base beside the discussed form?",
                discussed,
                "to discuss",
                [
                    ("we leave", "The frozen line writes this label beside a different form for we leave.", "This label is paired with the leave form (we leave)."),
                    ("being discussed", "The frozen line writes this label beside the shown form itself for being discussed.", "This label is the gloss of the shown form, not its base (being discussed)."),
                ],
                feature="PRESENT_PASSIVE",
                why="The frozen line says an ending is added to form the passive present form. This label is the base written beside the shown form.",
            ),
        ],
        extra=[_extra("grammar", present, "to discuss")],
    )


def _unit_28(lesson) -> None:
    imperfect = _span(
        "grammar",
        "Present adverbial participles are formed by adding the suffix -а/-я",
        "| учась",
    )
    perfect = _span(
        "grammar",
        "most are formed with the suffix -в",
        "войдя (вошед",
    )
    for text in ("думая", "учась", "to think", "to be learning", "to say"):
        _clean("grammar", imperfect, text)
    for text in ("сказав", "научившись", "войдя", "to say", "to learn", "to enter"):
        _clean("grammar", perfect, text)
    lesson.objective(
        "imperfective-adverb",
        "Choose the admitted gloss for the shown imperfective adverbial form",
        "CONTRAST_SELECTION",
        ("grammar", imperfect, "Present adverbial participles are formed by adding the suffix -а/-я"),
        [
            _wiki("grammar", imperfect, "думая", "to think"),
            _wiki("grammar", imperfect, "учась", "to be learning"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label is written beside the shown form?",
                "думая",
                "to think",
                [
                    ("to be learning", "The frozen table writes this label beside a different form for to be learning.", "This label is paired with the learning form (to be learning)."),
                    ("to say", "The frozen table writes this label beside a completed form for to say.", "This label is paired with a different row (to say)."),
                ],
                feature="IMPERFECTIVE_ADVERB",
                why="The frozen line says these forms are formed by adding the listed suffix. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside this shown form?",
                "учась",
                "to be learning",
                [
                    ("to think", "The frozen table writes this label beside a different form for to think.", "This label is paired with the thinking form (to think)."),
                    ("to say", "The frozen table writes this label beside a completed form for to say.", "This label is paired with a different row (to say)."),
                ],
                feature="IMPERFECTIVE_ADVERB",
                why="The frozen line says these forms are formed by adding the listed suffix. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English gloss is written for the other table form, not the shown form?",
                "думая",
                "to be learning",
                [
                    ("to think", "The frozen table writes this label beside the shown form for to think.", "This label is the gloss of the shown form (to think)."),
                    ("to say", "The frozen table writes this label beside a completed form for to say.", "This label is paired with a different row (to say)."),
                ],
                feature="IMPERFECTIVE_ADVERB",
                why="The frozen line says these forms are formed by adding the listed suffix. This label is the other gloss in the same table.",
            ),
        ],
        extra=[_extra("grammar", imperfect, "to say")],
    )
    _bind(lesson, "obj-u28-imperfective-adverb", "imperfective-verbal-adverb", "grammar", imperfect)
    lesson.objective(
        "perfective-adverb",
        "Choose the admitted gloss for the shown perfective adverbial form",
        "CONTRAST_SELECTION",
        ("grammar", perfect, "most are formed with the suffix -в"),
        [
            _wiki("grammar", perfect, "сказав", "to say"),
            _wiki("grammar", perfect, "научившись", "to learn"),
            _wiki("grammar", perfect, "войдя", "to enter"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label is written beside the shown form?",
                "сказав",
                "to say",
                [
                    ("to learn", "The frozen table writes this label beside a different form for to learn.", "This label is paired with the learning form (to learn)."),
                    ("to enter", "The frozen table writes this label beside a different form for to enter.", "This label is paired with the entering form (to enter)."),
                ],
                feature="PERFECTIVE_ADVERB",
                why="The frozen line says most are formed with the listed suffix. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside this shown form?",
                "научившись",
                "to learn",
                [
                    ("to say", "The frozen table writes this label beside a different form for to say.", "This label is paired with the saying form (to say)."),
                    ("to enter", "The frozen table writes this label beside a different form for to enter.", "This label is paired with the entering form (to enter)."),
                ],
                feature="PERFECTIVE_ADVERB",
                why="The frozen line says most are formed with the listed suffix. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside the remaining shown form?",
                "войдя",
                "to enter",
                [
                    ("to say", "The frozen table writes this label beside a different form for to say.", "This label is paired with the saying form (to say)."),
                    ("to learn", "The frozen table writes this label beside a different form for to learn.", "This label is paired with the learning form (to learn)."),
                ],
                feature="PERFECTIVE_ADVERB",
                why="The frozen line says most are formed with the listed suffix. This label is written beside the shown form.",
            ),
        ],
    )
    _bind(lesson, "obj-u28-perfective-adverb", "perfective-verbal-adverb", "grammar", perfect)


def _unit_29(lesson) -> None:
    active = _span(
        "grammar",
        "The active past participle is used in order to indicate actions that happened in the past:",
        "in the present''').",
    )
    living = _span(
        "grammar",
        "Лю́ди, '''живу́щие''' в э́том го́роде",
        "very kind and responsible.",
    )
    short = _span(
        "grammar",
        "such as {{wikt-lang|ru|беж}}",
        "('new').",
    )
    bookish = _span(
        "grammar",
        "In standard Russian, adverbial participles are considered a feature of bookish speech",
        '("I ate and went for a walk").',
    )
    for text in ("чита́вшая", "чита́ющая", "in the past", "in the present", "that read"):
        _clean("grammar", active, text)
    lesson.objective(
        "active",
        "Choose the admitted time label for the shown active form",
        "CONTRAST_SELECTION",
        ("grammar", active, "The active past participle is used in order to indicate actions that happened in the past:"),
        [
            _wiki("grammar", active, "чита́вшая", "in the past"),
            _wiki("grammar", active, "чита́ющая", "in the present"),
            _wiki("grammar", living, "живу́щие", "living"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label is written on the line with the shown form?",
                "чита́вшая",
                "in the past",
                [
                    ("in the present", "The frozen line writes this label on the other line for in the present.", "This label is paired with the other form (in the present)."),
                    ("that read", "The frozen line writes this label as a different phrase on the past line for that read.", "This phrase is not the time label asked for here (that read)."),
                ],
                feature="ACTIVE_TIME",
                why="The frozen line says the form indicates actions that happened in the past. This label is written on the shown line.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written on the line with this shown form?",
                "чита́ющая",
                "in the present",
                [
                    ("in the past", "The frozen line writes this label on the other line for in the past.", "This label is paired with the other form (in the past)."),
                    ("reading", "The frozen line writes this word inside the gloss, not as the time label for reading.", "This word is not the time label (reading)."),
                ],
                feature="ACTIVE_TIME",
                why="The frozen line says the other form is happening now, in the present. This label is written on the shown line.",
            ),
            _distinction(
                "grammar",
                "Which admitted English gloss names the other time, not the one on the shown line?",
                "чита́вшая",
                "in the present",
                [
                    ("in the past", "The frozen line writes this label on the shown line for in the past.", "This label is the one on the shown line (in the past)."),
                    ("that read", "The frozen line writes this phrase on the shown line, and it is not the other time label for that read.", "This phrase stays on the shown line (that read)."),
                ],
                feature="ACTIVE_TIME",
                why="The frozen line says the form indicates actions that happened in the past. This label is the other time label.",
            ),
        ],
        extra=[
            _extra("grammar", active, "reading"),
            _extra("grammar", active, "that read"),
        ],
    )
    _bind(lesson, "obj-u29-active", "active-participle", "grammar", active)
    lesson.objective(
        "short-adjective",
        "Choose the admitted gloss for the shown short form",
        "CONTRAST_SELECTION",
        ("grammar", short, "the short form appears only in the nominative"),
        [
            _wiki("grammar", short, "но́вый", "new"),
            _wiki("grammar", short, "нов", "new"),
            _wiki("grammar", short, "беж", "beige"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label is written beside the shown form?",
                "но́вый",
                "new",
                [
                    ("beige", "The frozen line writes this label beside a different form for beige.", "This label is paired with an invariant form (beige)."),
                    ("khaki-colored", "The frozen line writes this label beside a different form for khaki-colored.", "This label is paired with the other invariant form (khaki-colored)."),
                ],
                feature="SHORT_FORM",
                why="The frozen line says the short form appears in the stated role. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside this shown form?",
                "нов",
                "new",
                [
                    ("beige", "The frozen line writes this label beside an invariant form for beige.", "This label is not the gloss of the shown form (beige)."),
                    ("khaki-colored", "The frozen line writes this label beside an invariant form for khaki-colored.", "This label is paired with a different form (khaki-colored)."),
                ],
                feature="SHORT_FORM",
                why="The frozen line says the short form appears in the stated role. This label is written beside the shown form.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside the invariant form?",
                "беж",
                "beige",
                [
                    ("new", "The frozen line writes this label beside the other forms for new.", "This label is paired with a different form (new)."),
                    ("khaki-colored", "The frozen line writes this label beside the other invariant form for khaki-colored.", "This label is paired with a different form (khaki-colored)."),
                ],
                feature="SHORT_FORM",
                why="The frozen line says the short form appears in the stated role. This label is written beside the shown invariant form.",
            ),
        ],
        extra=[_extra("grammar", short, "khaki-colored")],
    )
    _bind(lesson, "obj-u29-short-adjective", "short-form-adjective", "grammar", short)
    lesson.objective(
        "bookish-note",
        "Choose the admitted label for the shown adverbial line",
        "CONTRAST_SELECTION",
        ("grammar", bookish, "adverbial participles are considered a feature of bookish speech"),
        [
            _wiki("grammar", bookish, "Пообедав, я пошёл гулять", "bookish speech"),
            _wiki("grammar", bookish, "Я пообедал и пошёл гулять", "colloquial language"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label names the kind of speech the source gives the shown line?",
                "Пообедав, я пошёл гулять",
                "bookish speech",
                [
                    ("colloquial language", "The frozen line gives this label to the replacement line for colloquial language.", "This label is paired with the other line (colloquial language)."),
                    ("Having eaten, I went for a walk", "The frozen line writes this as the gloss, not as the speech label for Having eaten, I went for a walk.", "This gloss is not the speech label (Having eaten, I went for a walk)."),
                ],
                feature="BOOKISH_SPEECH",
                why="The frozen line says adverbial participles are considered a feature of bookish speech. This label is the one given to that construction.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label names the replacement named for the shown line?",
                "Я пообедал и пошёл гулять",
                "colloquial language",
                [
                    ("bookish speech", "The frozen line gives this label to the other construction for bookish speech.", "This label is paired with the other line (bookish speech)."),
                    ("Having eaten, I went for a walk", "The frozen line writes this gloss beside the other line for Having eaten, I went for a walk.", "This gloss is paired with the other line (Having eaten, I went for a walk)."),
                ],
                feature="BOOKISH_SPEECH",
                why="The frozen line says adverbial participles are considered a feature of bookish speech. This label names the replacement in colloquial language.",
            ),
            _distinction(
                "grammar",
                "Which admitted English gloss is written beside the shown line?",
                "Пообедав, я пошёл гулять",
                "Having eaten, I went for a walk",
                [
                    ("bookish speech", "The frozen line uses this phrase for the kind of speech, not as the gloss for bookish speech.", "This phrase is the speech label (bookish speech)."),
                    ("colloquial language", "The frozen line uses this phrase for the replacement for colloquial language.", "This phrase is the other label (colloquial language)."),
                ],
                feature="BOOKISH_SPEECH",
                why="The frozen line says adverbial participles are considered a feature of bookish speech. This gloss is written beside the shown line.",
            ),
        ],
        extra=[_extra("grammar", bookish, "Having eaten, I went for a walk")],
    )


def _unit_30(lesson) -> None:
    names = _span(
        "alphabet",
        "! Name",
        '<br/>"both"',
    )
    abbrev = _span(
        "grammar",
        "* Abbreviations:",
        '"USA", plural).',
    )
    address = _span(
        "address",
        "The lines are marked with abbreviations:",
        "ул. Петрова, д. 14, кв. 15 <br>",
    )
    for text in ("а", "бэ", "ah", "be/beh", "father", "bad", "Approximate English"):
        _clean("alphabet", names, text)
    lesson.objective(
        "letter-names",
        "Review the admitted table name",
        "CONTRAST_SELECTION",
        ("alphabet", names, "Approximate English equivalent"),
        [
            _wiki("alphabet", names, "а", "ah"),
            _wiki("alphabet", names, "бэ", "be/beh"),
        ],
        [
            _distinction(
                "alphabet",
                "Which admitted English respelling is written beside the shown table name?",
                "а",
                "ah",
                [
                    ("be/beh", "The frozen table writes this respelling beside a different name for be/beh.", "This respelling belongs to the other row (be/beh)."),
                    ("father", "The frozen table writes this equivalent in another column for father.", "This equivalent is not the respelling (father)."),
                ],
                feature="LETTER_NAME",
                why="The frozen table places a respelling beside the name in the Approximate English row area. This label is the respelling beside the shown name.",
            ),
            _distinction(
                "alphabet",
                "Which admitted English respelling is written beside this shown table name?",
                "бэ",
                "be/beh",
                [
                    ("ah", "The frozen table writes this respelling beside a different name for ah.", "This respelling belongs to the other row (ah)."),
                    ("bad", "The frozen table writes this equivalent in another column for bad.", "This equivalent is not the respelling (bad)."),
                ],
                feature="LETTER_NAME",
                why="The frozen table places a respelling beside the name in the Approximate English row area. This label is the respelling beside the shown name.",
            ),
            _distinction(
                "alphabet",
                "Which admitted English equivalent is written in the same row as the shown name?",
                "а",
                "father",
                [
                    ("bad", "The frozen table writes this equivalent in a different row for bad.", "This equivalent belongs to the other row (bad)."),
                    ("both", "The frozen table writes this gloss in a different row for both.", "This gloss belongs to the other row (both)."),
                ],
                feature="LETTER_NAME",
                why="The frozen table places a respelling beside the name in the Approximate English row area. This equivalent is written in the shown row.",
            ),
        ],
        extra=[
            _extra("alphabet", names, "father"),
            _extra("alphabet", names, "bad"),
            _extra("alphabet", names, "both"),
        ],
    )
    _bind(lesson, "obj-u30-letter-names", "letter-name-review", "alphabet", names)
    lesson.objective(
        "abbrev-examples",
        "Choose the admitted gloss for the shown abbreviation example",
        "CONTRAST_SELECTION",
        ("grammar", abbrev, "* Abbreviations:"),
        [
            _wiki("grammar", abbrev, "СССР", "USSR"),
            _wiki("grammar", abbrev, "США", "USA"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English label is written beside the shown example?",
                "СССР",
                "USSR",
                [
                    ("USA", "The frozen line writes this label beside the other example for USA.", "This label is paired with the other example (USA)."),
                    ("Abbreviations", "The frozen line uses this word as the heading, not as the gloss for Abbreviations.", "This word is the heading (Abbreviations)."),
                ],
                feature="ABBREVIATION_EXAMPLE",
                why="The frozen line lists these examples under Abbreviations. This label is written beside the shown example.",
            ),
            _distinction(
                "grammar",
                "Which admitted English label is written beside this shown example?",
                "США",
                "USA",
                [
                    ("USSR", "The frozen line writes this label beside the other example for USSR.", "This label is paired with the other example (USSR)."),
                    ("Abbreviations", "The frozen line uses this word as the heading for Abbreviations.", "This word is the heading, not the gloss (Abbreviations)."),
                ],
                feature="ABBREVIATION_EXAMPLE",
                why="The frozen line lists these examples under Abbreviations. This label is written beside the shown example.",
            ),
            _distinction(
                "grammar",
                "Which admitted English word is the heading rather than the gloss of the shown example?",
                "СССР",
                "Abbreviations",
                [
                    ("USSR", "The frozen line writes this label beside the shown example for USSR.", "This label is the gloss (USSR)."),
                    ("USA", "The frozen line writes this label beside the other example for USA.", "This label is the other gloss (USA)."),
                ],
                feature="ABBREVIATION_EXAMPLE",
                why="The frozen line lists these examples under Abbreviations. This word is the heading above the examples.",
            ),
        ],
        extra=[_extra("grammar", abbrev, "Abbreviations")],
    )
    lesson.objective(
        "address-marks",
        "Review the admitted address marks",
        "CONTRAST_SELECTION",
        ("address", address, "The lines are marked with abbreviations:"),
        [
            _wiki("address", address, "115114 г. Москва", "city"),
            _wiki("address", address, "ул. Петрова, д. 14, кв. 15", "street"),
            _wiki("address", address, "д.", "building"),
        ],
        [
            _distinction(
                "address",
                "Which admitted English label glosses the mark in the shown line?",
                "115114 г. Москва",
                "city",
                [
                    ("street", "The frozen note glosses street for a different mark.", "Street belongs to another mark."),
                    ("building", "The frozen note glosses building for a different mark.", "Building belongs to another mark."),
                ],
                feature="ADDRESS_MARK",
                why="The lines are marked with abbreviations. This label glosses the mark in the shown line.",
            ),
            _distinction(
                "address",
                "Which admitted English label glosses the first mark in the shown line?",
                "ул. Петрова, д. 14, кв. 15",
                "street",
                [
                    ("city", "The frozen note glosses city for the place mark.", "City belongs to another mark."),
                    ("apartment", "The frozen note glosses apartment for a later mark.", "Apartment belongs to a later mark."),
                ],
                feature="ADDRESS_MARK",
                why="The lines are marked with abbreviations. This label glosses the first mark in the shown line.",
            ),
            _distinction(
                "address",
                "Which admitted English gloss is written for the shown mark?",
                "д.",
                "building",
                [
                    ("city", "The frozen note glosses city for a different mark.", "City is the gloss of the place mark."),
                    ("street", "The frozen note glosses street for a different mark.", "Street is the gloss of a different mark."),
                ],
                feature="ADDRESS_MARK",
                why="The lines are marked with abbreviations. This label is the gloss written for the shown mark.",
            ),
        ],
        extra=[
            _extra("address", address, "building"),
            _extra("address", address, "apartment"),
        ],
    )
