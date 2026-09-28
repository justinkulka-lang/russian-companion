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
    "morfologiya": ROOT / "catalog/sources/units-21-25/supplemental/morfologiya-148939075.txt",
    "neopredelennoe": ROOT / "catalog/sources/units-26-30/ruwiki/neopredelennoe-mestoimenie-149963314.txt",
    "prichastie": ROOT / "catalog/sources/units-26-30/ruwiki/prichastie-153078996.txt",
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
    "morfologiya": "src-ruwiki-morfologiya-148939075",
    "neopredelennoe": "src-ruwiki-neopredelennoe-mestoimenie-149963314",
    "prichastie": "src-ruwiki-prichastie-153078996",
}


def _marker_indexes(text: str, marker: str) -> list[int]:
    if not marker:
        return []
    indexes = []
    start = 0
    while True:
        found = text.find(marker, start)
        if found < 0:
            return indexes
        indexes.append(found)
        start = found + 1


def _span_in(text: str, start: str, end: str, page: str = "span") -> str:
    """Return one span only when the start and end markers are each unique."""
    starts = _marker_indexes(text, start)
    ends = _marker_indexes(text, end)
    if len(starts) != 1 or len(ends) != 1:
        raise SystemExit(f"Frozen span markers are ambiguous on {page}: {start!r} .. {end!r}")
    begin = starts[0]
    finish = ends[0]
    if finish < begin + len(start):
        raise SystemExit(f"Frozen span markers are ambiguous on {page}: {start!r} .. {end!r}")
    return text[begin:finish + len(end)]


def _span(page: str, start: str, end: str) -> str:
    return _span_in(_PAGES[page], start, end, page)


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


def _distinction(page, prompt, shown, answer, wrongs, feature, why, frozen_quote=False):
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
        frozen_quote=frozen_quote,
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
    indefinite = _span(
        "neopredelennoe",
        "==== Серия на ''-то'' ====",
        "Надо сдать кое-какие бумаги''.",
    )
    referential = "референтность и неизвестность говорящему"
    nonreferential = "являются нереферентными"
    known = "референтный денотативный статус слабой определённости"
    overlap = "Местоимение на -то может употребляться также и в значении местоимения на -нибудь"
    shown_to = "В доме действительно кто-то был"
    shown_any = "куда-нибудь"
    shown_koe = "кое-какие"
    meaning_prompt = "Which admitted phrase classifies the shown form?"
    lesson.objective(
        "indefinite",
        "Classify the shown form by its admitted series meaning",
        "CONTRAST_SELECTION",
        ("neopredelennoe", indefinite, overlap),
        [
            _wiki("neopredelennoe", indefinite, shown_to, referential),
            _wiki("neopredelennoe", indefinite, shown_any, nonreferential),
            _wiki("neopredelennoe", indefinite, shown_koe, known),
        ],
        [
            _distinction(
                "neopredelennoe",
                meaning_prompt,
                shown_to,
                referential,
                [
                    (nonreferential, f"The source assigns {nonreferential} to {shown_any}.", f"{nonreferential} does not classify the shown form."),
                    (known, f"The source assigns {known} to {shown_koe}.", f"{known} does not classify the shown form."),
                ],
                feature="INDEFINITE_PRONOUN",
                why=f"The source says this series expresses {referential} and gives {shown_to}. The keyed phrase is {referential}.",
            ),
            _distinction(
                "neopredelennoe",
                meaning_prompt,
                shown_any,
                nonreferential,
                [
                    (referential, f"The source assigns {referential} to {shown_to}.", f"{referential} does not classify the shown form."),
                    (known, f"The source assigns {known} to {shown_koe}.", f"{known} does not classify this shown form."),
                ],
                feature="INDEFINITE_PRONOUN",
                why=f"The source says the series {nonreferential} and gives {shown_any}. The keyed phrase is {nonreferential}.",
            ),
            _distinction(
                "neopredelennoe",
                meaning_prompt,
                shown_koe,
                known,
                [
                    (referential, f"The source assigns {referential} to {shown_to}.", f"{referential} does not classify the shown form."),
                    (nonreferential, f"The source assigns {nonreferential} to {shown_any}.", f"{nonreferential} does not classify the shown form."),
                ],
                feature="INDEFINITE_PRONOUN",
                why=f"The source says this series expresses {known} and gives {shown_koe}. The keyed phrase is {known}.",
            ),
        ],
    )
    _bind(lesson, "obj-u26-indefinite", "indefinite-pronoun", "neopredelennoe", indefinite)


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
    plural = _clean("grammar", present, format_wikibooks_emphasis("оставля́'''ем'''"))
    base = _clean("grammar", present, "оставля́ть")
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
            _wiki("grammar", present, plural, "we leave"),
        ],
        [
            _distinction(
                "grammar",
                "Which admitted English gloss follows the listed forms for the shown form?",
                discussed,
                "being discussed",
                [
                    ("we leave", "The frozen parenthetical we leave belongs to the plural form in the same note.", "This parenthetical belongs to the plural form (we leave)."),
                    ("to discuss", "The frozen line writes to discuss on the base, not as this gloss.", "This label is the base gloss (to discuss)."),
                ],
                feature="PRESENT_PASSIVE",
                why="The frozen line says being discussed or able to be discussed after the listed forms. This label is that gloss.",
            ),
            _distinction(
                "grammar",
                "Which admitted English parenthetical follows the shown form?",
                plural,
                "we leave",
                [
                    ("being discussed", "The frozen line gives being discussed to the discuss forms, not this parenthetical.", "This gloss belongs to the discuss forms (being discussed)."),
                    ("to leave", "The frozen parenthetical to leave belongs to the other form in the same row.", "This parenthetical belongs to the other form (to leave)."),
                ],
                feature="PRESENT_PASSIVE",
                why="The frozen line writes (we leave) after the shown form. This label is that parenthetical.",
            ),
            _distinction(
                "grammar",
                "Which admitted English parenthetical follows this shown form?",
                base,
                "to leave",
                [
                    ("we leave", "The frozen parenthetical we leave belongs to the plural form, not this form.", "This parenthetical belongs to the plural form (we leave)."),
                    ("being discussed", "The frozen line gives being discussed to the discuss forms, not this parenthetical.", "This gloss belongs to the discuss forms (being discussed)."),
                ],
                feature="PRESENT_PASSIVE",
                why="The frozen line writes (to leave) after the shown form. This label is that parenthetical.",
            ),
        ],
        extra=[
            _extra("grammar", present, "to discuss"),
            _extra("grammar", present, base),
            _extra("grammar", present, "to leave"),
        ],
    )


def _unit_28(lesson) -> None:
    contrast = "imperfective ones are usually present, while perfective ones can only be past"
    perfect_contrast = "usually present, while perfective ones can only be past"
    imperfect = _span(
        "grammar",
        contrast,
        "| учась",
    )
    perfect = _span(
        "grammar",
        perfect_contrast,
        "войдя (вошед",
    )
    if "usually present" not in imperfect or "can only be past" not in perfect:
        raise SystemExit("Verbal-adverb span lost the present-versus-past statement")
    if "bookish speech" not in imperfect:
        raise SystemExit("Imperfective span lost the neighboring bookish paragraph it must not bind")
    present = "usually present"
    past = "can only be past"
    rare = "very rarely past"
    imperfective = "imperfective ones"
    perfective = "perfective ones"
    for text in ("думая", "учась", present, past, rare, imperfective):
        _clean("grammar", imperfect, text)
    for text in ("сказав", "научившись", "войдя", present, past, rare, perfective):
        _clean("grammar", perfect, text)
    from explicit_spans import EXPLICIT_GLOSSES

    EXPLICIT_GLOSSES[imperfect] = {imperfective: frozenset({present})}
    EXPLICIT_GLOSSES[perfect] = {perfective: frozenset({past})}
    present_why = "The source says imperfective ones are usually present."
    past_why = "The source says perfective ones can only be past."
    rare_why = "The source uses very rarely past for a different construction."
    past_only = "The source assigns can only be past to perfective ones."
    present_only = "The source assigns usually present to imperfective ones."
    lesson.objective(
        "imperfective-adverb",
        "Choose the time relationship the source states for imperfective adverbial forms",
        "CONTRAST_SELECTION",
        ("grammar", imperfect, contrast),
        [
            _wiki("grammar", imperfect, "думая", present),
            _wiki("grammar", imperfect, "учась", present),
        ],
        [
            _distinction(
                "grammar",
                "Which English time relationship does the source state for imperfective forms?",
                imperfective,
                present,
                [
                    (past, past_only, "This relationship is the one stated for perfective ones (can only be past)."),
                    (rare, rare_why, "This phrase is a different construction in the same span (very rarely past)."),
                ],
                feature="IMPERFECTIVE_ADVERB",
                why=present_why,
            ),
            _distinction(
                "grammar",
                "Which English time relationship belongs to imperfective forms?",
                imperfective,
                present,
                [
                    (rare, rare_why, "This phrase is a different construction in the same span (very rarely past)."),
                    (past, past_only, "This relationship is the one stated for perfective ones (can only be past)."),
                ],
                feature="IMPERFECTIVE_ADVERB",
                why=present_why,
            ),
            _distinction(
                "grammar",
                "Which English time relationship does the source state for these imperfective forms?",
                imperfective,
                present,
                [
                    (past, past_only, "This relationship is the one stated for perfective ones (can only be past)."),
                    (rare, rare_why, "This phrase names a different construction (very rarely past)."),
                ],
                feature="IMPERFECTIVE_ADVERB",
                why=present_why,
            ),
        ],
        extra=[
            _extra("grammar", imperfect, imperfective),
            _extra("grammar", imperfect, past),
            _extra("grammar", imperfect, rare),
        ],
    )
    _bind(lesson, "obj-u28-imperfective-adverb", "imperfective-verbal-adverb", "grammar", imperfect)
    lesson.objective(
        "perfective-adverb",
        "Choose the time relationship the source states for perfective adverbial forms",
        "CONTRAST_SELECTION",
        ("grammar", perfect, perfect_contrast),
        [
            _wiki("grammar", perfect, "сказав", past),
            _wiki("grammar", perfect, "научившись", past),
            _wiki("grammar", perfect, "войдя", past),
        ],
        [
            _distinction(
                "grammar",
                "Which English time relationship does the source state for perfective forms?",
                perfective,
                past,
                [
                    (present, present_only, "This relationship is the one stated for imperfective ones (usually present)."),
                    (rare, rare_why, "This phrase is a different construction in the same span (very rarely past)."),
                ],
                feature="PERFECTIVE_ADVERB",
                why=past_why,
            ),
            _distinction(
                "grammar",
                "Which English time relationship belongs to perfective forms?",
                perfective,
                past,
                [
                    (rare, rare_why, "This phrase is a different construction in the same span (very rarely past)."),
                    (present, present_only, "This relationship is the one stated for imperfective ones (usually present)."),
                ],
                feature="PERFECTIVE_ADVERB",
                why=past_why,
            ),
            _distinction(
                "grammar",
                "Which English time relationship does the source state for these perfective forms?",
                perfective,
                past,
                [
                    (present, present_only, "This relationship is the one stated for imperfective ones (usually present)."),
                    (rare, rare_why, "This phrase names a different construction (very rarely past)."),
                ],
                feature="PERFECTIVE_ADVERB",
                why=past_why,
            ),
        ],
        extra=[
            _extra("grammar", perfect, perfective),
            _extra("grammar", perfect, present),
            _extra("grammar", perfect, rare),
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
    punctuation = _span(
        "prichastie",
        "В русском языке причастный оборот часто выделяется",
        "Написанная в спешке программа выполнила недопустимую операцию.",
    )
    both_sides = "выделяется запятыми с двух сторон"
    no_commas = "запятые не ставятся"
    exception = "кроме тех случаев"
    after_example = "Программа, написанная в спешке, выполнила недопустимую операцию."
    before_example = "Написанная в спешке программа выполнила недопустимую операцию."
    personal = "личным местоимением"
    cause = "обозначает причину"
    end_rule = "запятая ставится только перед причастным оборотом"
    exception_sentence = "Когда причастный оборот стоит перед определяемым словом, запятые не ставятся, кроме тех случаев, когда определяемое слово выражено личным местоимением или причастный оборот обозначает причину."
    punctuation_prompt = "Which admitted phrase classifies the shown form?"
    lesson.objective(
        "punctuation",
        "Classify the shown phrase by the admitted punctuation rule",
        "CONTRAST_SELECTION",
        ("prichastie", punctuation, exception_sentence),
        [
            _wiki("prichastie", punctuation, after_example, both_sides),
            _wiki("prichastie", punctuation, before_example, no_commas),
            _wiki("prichastie", punctuation, personal, exception),
        ],
        [
            _distinction(
                "prichastie",
                punctuation_prompt,
                after_example,
                both_sides,
                [
                    (no_commas, f"The source assigns {no_commas} when the phrase stands before the determined word.", f"{no_commas} does not classify the shown form."),
                    (exception, f"The source reserves {exception} for {personal} and for a phrase that {cause}.", f"{exception} does not classify the shown form."),
                ],
                feature="PARTICIPLE_PUNCTUATION",
                why=f"The source says a phrase after the determined word {both_sides} and gives {after_example} The keyed phrase is {both_sides}.",
            ),
            _distinction(
                "prichastie",
                punctuation_prompt,
                before_example,
                no_commas,
                [
                    (both_sides, f"The source assigns {both_sides} to {after_example}", f"{both_sides} does not classify the shown form."),
                    (end_rule, f"The source assigns {end_rule} when the sentence ends after the phrase.", f"{end_rule} does not classify the shown form."),
                ],
                feature="PARTICIPLE_PUNCTUATION",
                why=f"The source says a phrase before the determined word has {no_commas} and gives {before_example} The keyed phrase is {no_commas}.",
            ),
            _distinction(
                "prichastie",
                punctuation_prompt,
                personal,
                exception,
                [
                    (no_commas, f"The source states {no_commas} as the default before the determined word.", f"{no_commas} does not classify the shown form."),
                    (both_sides, f"The source assigns {both_sides} to a phrase after the determined word.", f"{both_sides} does not classify the shown form."),
                ],
                feature="PARTICIPLE_PUNCTUATION",
                why=f"The source says the determined word can be {personal}. The keyed phrase is {exception}.",
            ),
        ],
        extra=[
            _extra("prichastie", punctuation, cause),
            _extra("prichastie", punctuation, end_rule),
            _extra("prichastie", punctuation, "конец предложения"),
        ],
    )
    _bind(lesson, "obj-u29-punctuation", "participle-punctuation", "prichastie", punctuation)


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

    abbreviation = _span(
        "morfologiya",
        "Ряд существительных являются несклоняемым",
        "названия населённых пунктов",
    )
    particle = _span(
        "morfologiya",
        "Повелительное наклонение может быть выражено также при помощи частиц",
        "предположительность или возможность действия",
    )
    for text in (
        "СНГ",
        "ЭВМ",
        "ГАИ",
        "гороно",
        "в МИД",
        "в МИДе",
        "в МИД и в МИДе",
        "Шевченко",
        "пальто",
        "Засулич",
        "радио",
        "названия населённых пунктов",
        "сложносокращённые слова",
        "буквенные аббревиатуры",
        "звуковые аббревиатуры на гласную",
        "звуковые аббревиатуры на согласную",
        "буквенные аббревиатуры и звуковые аббревиатуры на гласную",
    ):
        _clean("morfologiya", abbreviation, text)
    for text in (
        "Пусть он читает",
        "Да будет мир!",
        "читал бы",
        "читали бы",
        "Повелительное наклонение",
        "предположительность или возможность",
        "предположительность или возможность действия",
    ):
        _clean("morfologiya", particle, text)
    letter_or_vowel = "буквенные аббревиатуры и звуковые аббревиатуры на гласную"
    consonant = "звуковые аббревиатуры на согласную"
    place_names = "названия населённых пунктов"
    shortened = "сложносокращённые слова"
    both_ways = "в МИД и в МИДе"
    lesson.objective(
        "abbreviation",
        "Classify the shown form by its admitted category",
        "CONTRAST_SELECTION",
        ("morfologiya", abbreviation, letter_or_vowel),
        [
            _wiki("morfologiya", abbreviation, "СНГ", letter_or_vowel),
            _wiki("morfologiya", abbreviation, "ЭВМ", letter_or_vowel),
            _wiki("morfologiya", abbreviation, "ГАИ", letter_or_vowel),
            _wiki("morfologiya", abbreviation, "гороно", letter_or_vowel),
            _wiki("morfologiya", abbreviation, "в МИД", consonant),
            _wiki("morfologiya", abbreviation, "в МИДе", consonant),
        ],
        [
            _distinction(
                "morfologiya",
                "Which admitted category label classifies the shown form?",
                "СНГ",
                letter_or_vowel,
                [
                    (consonant, f"The frozen line assigns {consonant} to {both_ways}.", f"{consonant} does not classify the shown form."),
                    (place_names, f"The frozen line names {place_names} as a separate category.", f"{place_names} does not classify the shown form."),
                ],
                feature="ABBREVIATION_DECLENSION",
                why=f"The frozen line classifies the shown form under {letter_or_vowel}. The keyed category is {letter_or_vowel}.",
            ),
            _distinction(
                "morfologiya",
                "Which admitted category label classifies the shown form?",
                "в МИД",
                consonant,
                [
                    (letter_or_vowel, f"The frozen line assigns {letter_or_vowel} to СНГ, ЭВМ, ГАИ, and гороно.", f"{letter_or_vowel} does not classify the shown form."),
                    (place_names, f"The frozen line names {place_names} apart from {consonant}.", f"{place_names} does not classify the shown consonant-final form."),
                ],
                feature="ABBREVIATION_DECLENSION",
                why=f"The frozen line classifies the shown form under {consonant} and writes {both_ways}. The keyed category is {consonant}.",
            ),
            _distinction(
                "morfologiya",
                "Which admitted category label classifies the shown form?",
                "гороно",
                letter_or_vowel,
                [
                    (consonant, f"The frozen line assigns {consonant} to {both_ways}.", f"{consonant} does not classify this shown form."),
                    (shortened, f"The frozen line names {shortened} as its own category.", f"{shortened} does not classify the shown form."),
                ],
                feature="ABBREVIATION_DECLENSION",
                why=f"The frozen line classifies гороно under {letter_or_vowel}. The keyed category is {letter_or_vowel}.",
            ),
        ],
        extra=[
            _extra("morfologiya", abbreviation, place_names),
            _extra("morfologiya", abbreviation, shortened),
            _extra("morfologiya", abbreviation, both_ways),
        ],
    )
    _bind(lesson, "obj-u30-abbreviation", "abbreviation", "morfologiya", abbreviation)
    command = "Повелительное наклонение"
    supposition = "предположительность или возможность"
    lesson.objective(
        "particle",
        "Classify the shown line by its admitted use",
        "CONTRAST_SELECTION",
        ("morfologiya", particle, "предположительность или возможность действия"),
        [
            _wiki("morfologiya", particle, "Пусть он читает", command),
            _wiki("morfologiya", particle, "Да будет мир!", command),
            _wiki("morfologiya", particle, "читал бы", supposition),
            _wiki("morfologiya", particle, "читали бы", supposition),
        ],
        [
            _distinction(
                "morfologiya",
                "Which admitted use label applies to the shown line?",
                "Пусть он читает",
                command,
                [
                    (supposition, f"The frozen line assigns {supposition} to читал бы and читали бы.", f"{supposition} does not apply to the shown line."),
                ],
                feature="PARTICLE_USE",
                why=f"The frozen line says {command} can also be expressed with these particles and gives Пусть он читает. The keyed use is {command}.",
            ),
            _distinction(
                "morfologiya",
                "Which admitted use label applies to the shown line?",
                "читал бы",
                supposition,
                [
                    (command, f"The frozen line assigns {command} to Пусть он читает and to Да будет мир!", f"{command} does not apply to the shown line."),
                ],
                feature="PARTICLE_USE",
                why=f"The frozen line says читал бы expresses {supposition} действия. The keyed use is {supposition}.",
            ),
            _distinction(
                "morfologiya",
                "Which admitted use label applies to the shown line?",
                "Да будет мир!",
                command,
                [
                    (supposition, f"The frozen line assigns {supposition} to читал бы and читали бы.", f"{supposition} applies to читал бы and читали бы, not to the shown line."),
                ],
                feature="PARTICLE_USE",
                why=f"The frozen line says {command} can also be expressed with these particles and gives Да будет мир! The keyed use is {command}.",
            ),
        ],
    )
    _bind(lesson, "obj-u30-particle", "particle", "morfologiya", particle)
