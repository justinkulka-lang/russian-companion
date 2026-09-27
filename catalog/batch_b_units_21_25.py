"""Batch B spans for Units 21–25.

Every shipped form is an exact frozen substring or its mechanical markup
unfolding. A keyword, a neighboring cell, or a dictionary row is not a
capability. This module does not decline, conjugate, or invent Russian.
"""

from __future__ import annotations

from pathlib import Path

from explicit_spans import _slice
from source_transforms import unfold_wiktionary
from units_11_15_lessons import _shared_response_kind, _wiki
from units_16_20_batch_a import _gloss_check, _pair
from units_3_5_intake import text_in_frozen

_ROOT = Path(__file__).resolve().parents[1]
_GRAMMAR = _ROOT / "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt"
_MORF = _ROOT / "catalog/sources/units-21-25/supplemental/morfologiya-148939075.txt"
_POL = _ROOT / "catalog/sources/units-21-25/supplemental/poltora-89737719.txt"
_OTR = _ROOT / "catalog/sources/units-21-25/supplemental/otritsatelnye-141256493.txt"
_OBR = _ROOT / "catalog/sources/units-21-25/supplemental/obraschenie-153012874.txt"

GRAMMAR_SOURCE = "src-wikipedia-russian-grammar-1369617627"
MORF_SOURCE = "src-ruwiki-morfologiya-148939075"
POLTORA_SOURCE = "src-enwiktionary-poltora-89737719"
OTR_SOURCE = "src-ruwiki-otritsatelnye-141256493"
OBR_SOURCE = "src-ruwiki-obraschenie-153012874"

REFLEXIVE_SPAN = _slice(
    _GRAMMAR,
    '** \'\'"Я люблю́ \'\'\'(свою́)\'\'\' жену́ = I love \'\'\'my\'\'\' wife"\'\'',
    '** \'\'"Я люблю́ \'\'\'себя́\'\'\' = I love \'\'\'myself\'\'\'"\'\'',
)
PURPOSE_SPAN = _slice(
    _GRAMMAR,
    "* если {{IPA|[ˈjesʲlʲɪ]}} 'if'",
    "* хотя {{IPA|[xɐˈtʲa]}} 'although'",
)
STUDY_SPAN = _slice(
    _GRAMMAR,
    "In order to study, the student brought",
    "на детскую площадь",
)
MORF_SPAN = _slice(_MORF, "Мужской и средний род", "|}")
POLTORA_SPAN = _slice(
    _POL,
    "{{head|ru|numeral|head=полтора́|g=m|g2=n}}",
    "in the same case as the number.",
)
COLLECTIVE_SPAN = _slice(_GRAMMAR, "several classes of numerals", "(де́сятеро)\n|}")
NEGATION_SPAN = _slice(
    _GRAMMAR,
    "The use of a direct object in the genitive",
    '("I don\'t see the book").',
)
SCOPE_SPAN = _slice(
    _GRAMMAR,
    "In some cases, alternative word order can change the meaning entirely:",
    "as I will do it anyway''].)",
)
PRONOUN_SPAN = _slice(_OTR, "Образуются от", "|чего\n|ничего́\n|не́чего")
CONCORD_SPAN = _slice(_OTR, "* Никто никому", "сказал.")
CONCORD_RULE_SPAN = _slice(_OTR, "Основным правилом правильного использования", "сказал.")
ADDRESS_SPAN = _slice(_OBR, "Есть полные имена", "одного возраста")
AGO_SPAN = _slice(_GRAMMAR, "He arrived in Moscow a week ago.", "через месяц.")
DURING_SPAN = _slice(
    _GRAMMAR,
    '{{wikt-lang|ru|в течение}} and {{wikt-lang|ru|в продолжение}}',
    '"in a time of an hour".',
)
STEP_SPAN = _slice(
    _GRAMMAR,
    "Step out for a short period of time",
    "чтобы успеть на поезд/самолёт.",
)

BOUNDED_TIME_SPANS = (AGO_SPAN, DURING_SPAN, STEP_SPAN)


def _require(span: str, needle: str) -> str:
    if needle not in span:
        raise SystemExit(f"Anchor is not in the frozen span: {needle!r}")
    return needle


def _ship(span: str, text: str, wiktionary: bool = False) -> str:
    visible = unfold_wiktionary(span) if wiktionary else None
    if wiktionary:
        if text not in span and text not in visible:
            raise SystemExit(f"Shipped text is not in the Wiktionary unfold: {text!r}")
    elif not text_in_frozen(span, text):
        raise SystemExit(f"Shipped text is not in the frozen span: {text!r}")
    for dirty in ("''", "{{", "}}", "[[", "]]", ".ogg", "<ref", "{|", "|}", "<br"):
        if dirty in text:
            raise SystemExit(f"Shipped text keeps markup: {text!r}")
    return text


def _frame(lesson) -> None:
    lesson.builder.say(lesson.number, "The frozen source pairs the shown form")
    lesson.builder.say(lesson.number, "with the admitted gloss")


def _quoted(shown: str, answer: str, citation: str = "") -> tuple[str, str, str]:
    text = _pair(shown, answer, citation)
    return (answer, text, text)


def _bind(lesson, slug: str, claim: str, needle: str) -> None:
    if not hasattr(lesson, "capabilities"):
        lesson.capabilities = []
    lesson.capabilities.append({
        "objectiveId": f"obj-u{lesson.number}-{slug}",
        "supportedClaim": claim,
        "exactSpan": needle,
    })


def _extra(page: str, needle: str, texts: list[str]) -> list[tuple]:
    return [(page, needle, text, "label") for text in texts]


LOVE_SELF = _ship(REFLEXIVE_SPAN, "Я люблю́ себя́")
LOVE_WIFE = _ship(REFLEXIVE_SPAN, "Я люблю́ (свою́) жену́")
MYSELF = _ship(REFLEXIVE_SPAN, "I love myself")
MY_WIFE = _ship(REFLEXIVE_SPAN, "I love my wife")
WIFE_CITE = _require(REFLEXIVE_SPAN, "жену́ = I love")
_SELF_LINE = REFLEXIVE_SPAN.split("\n")[1].replace("'''", "").replace("''", "")
_WIFE_LINE = REFLEXIVE_SPAN.split("\n")[0].replace("'''", "").replace("''", "")
SELF_CITE = "Я люблю́ себя́ = I love myself"
if SELF_CITE not in _SELF_LINE or SELF_CITE in _WIFE_LINE:
    raise SystemExit("Self citation is not unique to the self line")
if WIFE_CITE not in _WIFE_LINE or WIFE_CITE in _SELF_LINE:
    raise SystemExit("Wife citation is not unique to the wife line")

CHTOBY = _ship(PURPOSE_SPAN, "чтобы")
ESLI = _ship(PURPOSE_SPAN, "если")
POTOMU = _ship(PURPOSE_SPAN, "потому что")
KHOTYA = _ship(PURPOSE_SPAN, "хотя")
SO_THAT = _ship(PURPOSE_SPAN, "so that")
IF_GLOSS = _ship(PURPOSE_SPAN, "if")
BECAUSE = _ship(PURPOSE_SPAN, "because")
SINCE = _ship(PURPOSE_SPAN, "since")
AFTER = _ship(PURPOSE_SPAN, "after")
ALTHOUGH = _ship(PURPOSE_SPAN, "although")
PURPOSE_CITE = _require(PURPOSE_SPAN, "(bookish, archaic)")
PURPOSE_REMEDY = _require(PURPOSE_SPAN, "(bookish, archaic) 'so that'")
IF_CITE = _require(PURPOSE_SPAN, "meaning 'in case where'")
BECAUSE_CITE = _require(PURPOSE_SPAN, "for the reason that")
TAK_KAK = _ship(PURPOSE_SPAN, "так как")
POSLE = _ship(PURPOSE_SPAN, "после того, как")

DVUM = _ship(MORF_SPAN, "двум")
DVUMYA = _ship(MORF_SPAN, "двумя")
OBEIM = _ship(MORF_SPAN, "обеим")
OBOIM = _ship(MORF_SPAN, "обоим")
OBEIMI = _ship(MORF_SPAN, "обеими")
OBOIMI = _ship(MORF_SPAN, "обоими")
DATIVE = _ship(MORF_SPAN, "Дательный")
INSTRUMENTAL = _ship(MORF_SPAN, "Творительный")
PREPOSITIONAL = _ship(MORF_SPAN, "Предложный")
PREP_CELL = _ship(MORF_SPAN, "(о) двух / (об) обоих / (о) полутора")
BOTH_CELL = _ship(MORF_SPAN, "двум / обеим / полутора")
DATIVE_CITE = _require(MORF_SPAN, "| Дательный ")
INSTRUMENTAL_CITE = _require(MORF_SPAN, "| Творительный")
PREPOSITIONAL_CITE = _require(MORF_SPAN, "| Предложный")

POL_RUB = _ship(POLTORA_SPAN, "полтора́ рубля́", wiktionary=True)
POL_MIN = _ship(POLTORA_SPAN, "полторы́ мину́ты", wiktionary=True)
POL_FROM = _ship(POLTORA_SPAN, "из полу́тора рубле́й", wiktionary=True)
POL_TO = _ship(POLTORA_SPAN, "к полу́тора рубля́м", wiktionary=True)
POL_WITH = _ship(POLTORA_SPAN, "с полу́тора рубля́ми", wiktionary=True)
ROUBLES = _ship(POLTORA_SPAN, "1.5 roubles", wiktionary=True)
MINUTES = _ship(POLTORA_SPAN, "1.5 minutes", wiktionary=True)
FROM_ROUBLES = _ship(POLTORA_SPAN, "from 1.5 roubles", wiktionary=True)
TO_ROUBLES = _ship(POLTORA_SPAN, "to 1.5 roubles", wiktionary=True)
WITH_ROUBLES = _ship(POLTORA_SPAN, "with 1.5 roubles", wiktionary=True)
HOURS = _ship(POLTORA_SPAN, "one and a half hours / an hour and a half", wiktionary=True)
POL_HOUR = _ship(POLTORA_SPAN, "полтора́ часа́", wiktionary=True)
POL_CITE = _require(POLTORA_SPAN, "In the nominative and accusative")
USAGE_NOTE = _ship(
    POLTORA_SPAN,
    "In the nominative and accusative, this number governs the genitive singular of the noun. "
    "In all of the other cases, it governs the plural of the noun in the same case as the number.",
)
if ".ogg" in POLTORA_SPAN:
    raise SystemExit("Poltora span includes audio")

DVA = _ship(COLLECTIVE_SPAN, "два")
TRI = _ship(COLLECTIVE_SPAN, "три")
CHETYRE = _ship(COLLECTIVE_SPAN, "четы́ре")
DVOE = _ship(COLLECTIVE_SPAN, "дво́е")
TROE = _ship(COLLECTIVE_SPAN, "тро́е")
CHETVERO = _ship(COLLECTIVE_SPAN, "че́тверо")
VTOROY = _ship(COLLECTIVE_SPAN, "второ́й")
TRETIY = _ship(COLLECTIVE_SPAN, "тре́тий")
CHETVERTYY = _ship(COLLECTIVE_SPAN, "четвёртый")
COLLECTIVE_CITE = _require(COLLECTIVE_SPAN, "collective numbers")
ORDINAL_CITE = _require(COLLECTIVE_SPAN, "ordinal numbers")

KNIGI = _ship(NEGATION_SPAN, "Я не ви́жу кни́ги")
KNIGU = _ship(NEGATION_SPAN, "Я не ви́жу кни́гу")
ANY_BOOKS = _ship(NEGATION_SPAN, "I don't see any books")
THE_BOOK = _ship(NEGATION_SPAN, "I don't see the book")
NEVER_RU = _ship(SCOPE_SPAN, "Не надо меня уговаривать.")
ANYWAY_RU = _ship(SCOPE_SPAN, "Меня не надо уговаривать.")
NEVER_EN = _ship(SCOPE_SPAN, "One should not persuade me")
ANYWAY_EN = _ship(SCOPE_SPAN, "There is no need to persuade me")
NEVER_CITE = _require(SCOPE_SPAN, "as I would never agree to do something")
ANYWAY_CITE = _require(SCOPE_SPAN, "as I will do it anyway")
SCOPE_RULE = _require(SCOPE_SPAN, "alternative word order can change the meaning entirely")
if "logical stress" in SCOPE_SPAN:
    raise SystemExit("Scope span includes the neighboring emphasis sentence")

KTO = _ship(PRONOUN_SPAN, "кто")
CHTO = _ship(PRONOUN_SPAN, "что")
NIKTO = _ship(PRONOUN_SPAN, "никто́")
NEKTO = _ship(PRONOUN_SPAN, "не́кто")
NICHTO = _ship(PRONOUN_SPAN, "ничто́")
NECHTO = _ship(PRONOUN_SPAN, "не́что")
KTO_CITE = _require(PRONOUN_SPAN, "|кто\n|никто́")
CHTO_CITE = _require(PRONOUN_SPAN, "|что\n|ничто́")
NEKTO_CITE = _require(PRONOUN_SPAN, "|кто\n|никто́\n|не́кто")
NECHTO_CITE = _require(PRONOUN_SPAN, "|не́что")
PREDICATIVE_CITE = _require(PRONOUN_SPAN, "называются предикативными")
NEGATIVE_CITE = _require(PRONOUN_SPAN, "Отрицательные местоимения")
CONCORD_NIKTO = _ship(CONCORD_RULE_SPAN, "никто́")
CONCORD_NICHTO = _ship(CONCORD_RULE_SPAN, "ничто́")
CONCORD_NE = _ship(CONCORD_RULE_SPAN, "не")
CONCORD_NI = _ship(CONCORD_RULE_SPAN, "ни-")
CONCORD_NIKOMU = _ship(CONCORD_RULE_SPAN, "никому")
CONCORD_CITE = _require(CONCORD_RULE_SPAN, "отрицательной частицы не")
CONCORD_HEAD = _require(CONCORD_RULE_SPAN, "Основным правилом")

IRINA = _ship(ADDRESS_SPAN, "Ирина")
IRA = _ship(ADDRESS_SPAN, "Ира")
IROCHKA = _ship(ADDRESS_SPAN, "Ирочка")
FULL_NAMES = _ship(ADDRESS_SPAN, "полные имена")
SHORT_NAMES = _ship(ADDRESS_SPAN, "краткие имена")
AFFECTIONATE = _ship(ADDRESS_SPAN, "уменьшительно-ласкательные")
FULL_ADDRESS = _ship(ADDRESS_SPAN, "полному имени")
OFFICIAL = _ship(ADDRESS_SPAN, "отстранённо-официального регистра")
FRIENDLY = _ship(ADDRESS_SPAN, "дружески-неофициальном общении")
SEMI = _ship(ADDRESS_SPAN, "полуофициальной обстановке")
FULL_CITE = _require(ADDRESS_SPAN, "полные имена\u00a0— Ирина")
SHORT_CITE = _require(ADDRESS_SPAN, "краткие имена\u00a0— Ира")
AFFECTIONATE_CITE = _require(ADDRESS_SPAN, "уменьшительно-ласкательные\u00a0— Ирочка")
OFFICIAL_CITE = _require(ADDRESS_SPAN, "отстранённо-официального регистра")

AGO_RU = _ship(AGO_SPAN, "Он приехал в Москву неделю назад")
AGO_EN = _ship(AGO_SPAN, "He arrived in Moscow a week ago.")
MONTH_RU = _ship(AGO_SPAN, "Они у́летят из Владивостока через месяц.")
MONTH_EN = _ship(AGO_SPAN, "They will leave Vladivostok in a month.")
AGO_BIT = _ship(AGO_SPAN, "неделю назад")

DURING_HOUR_RU = _ship(DURING_SPAN, "в тече́ние ча́са")
DURING_HOUR_EN = _ship(DURING_SPAN, "in a time of an hour")
DURING_TE = _ship(DURING_SPAN, "в течение")
DURING_PRO = _ship(DURING_SPAN, "в продолжение")
DURING_WHILE = _ship(DURING_SPAN, "while, during the time of")

STEP_RU = _ship(STEP_SPAN, "Секретарь вышел на десять минут")
STEP_EN = _ship(STEP_SPAN, "The secretary left for ten minutes.")
STEP_BIT = _ship(STEP_SPAN, "на десять минут")
STEP_BIT_EN = _ship(STEP_SPAN, "for ten minutes")
STEP_HEADING = _require(STEP_SPAN, "Step out for a short period of time")
TRAIN_EN = _ship(STEP_SPAN, "They left early in the morning to catch their train/plane .")
TRAIN_RU = _ship(STEP_SPAN, "Они выехали рано утром, чтобы успеть на поезд/самолёт.")
TRAIN_BIT = _ship(STEP_SPAN, "to catch their train/plane")
STEP_OTHER = _ship(STEP_SPAN, "Leave at a specific time frame")


def _glosses() -> dict[str, dict[str, frozenset[str]]]:
    table = {
        REFLEXIVE_SPAN: {
            LOVE_SELF: frozenset({MYSELF}),
            LOVE_WIFE: frozenset({MY_WIFE}),
        },
        PURPOSE_SPAN: {
            CHTOBY: frozenset({SO_THAT}),
            ESLI: frozenset({IF_GLOSS}),
            POTOMU: frozenset({BECAUSE}),
            KHOTYA: frozenset({ALTHOUGH}),
        },
        MORF_SPAN: {
            DVUM: frozenset({DATIVE}),
            DVUMYA: frozenset({INSTRUMENTAL}),
            PREP_CELL: frozenset({PREPOSITIONAL}),
            OBEIM: frozenset({DATIVE}),
            OBOIM: frozenset({DATIVE}),
            OBEIMI: frozenset({INSTRUMENTAL}),
            OBOIMI: frozenset({INSTRUMENTAL}),
        },
        POLTORA_SPAN: {
            POL_RUB: frozenset({ROUBLES}),
            POL_MIN: frozenset({MINUTES}),
            POL_FROM: frozenset({FROM_ROUBLES}),
            POL_TO: frozenset({TO_ROUBLES}),
            POL_WITH: frozenset({WITH_ROUBLES}),
        },
        COLLECTIVE_SPAN: {
            DVA: frozenset({DVOE}),
            TRI: frozenset({TROE}),
            CHETYRE: frozenset({CHETVERO}),
        },
        NEGATION_SPAN: {
            KNIGI: frozenset({ANY_BOOKS}),
            KNIGU: frozenset({THE_BOOK}),
        },
        SCOPE_SPAN: {
            NEVER_RU: frozenset({NEVER_EN}),
            ANYWAY_RU: frozenset({ANYWAY_EN}),
        },
        PRONOUN_SPAN: {
            KTO: frozenset({NIKTO, NEKTO}),
            CHTO: frozenset({NICHTO, NECHTO}),
        },
        ADDRESS_SPAN: {
            IRINA: frozenset({FULL_NAMES}),
            IRA: frozenset({SHORT_NAMES}),
            IROCHKA: frozenset({AFFECTIONATE}),
            FULL_ADDRESS: frozenset({OFFICIAL}),
        },
        AGO_SPAN: {
            AGO_RU: frozenset({AGO_EN}),
            AGO_BIT: frozenset({AGO_EN}),
            MONTH_RU: frozenset({MONTH_EN}),
        },
        DURING_SPAN: {
            DURING_HOUR_RU: frozenset({DURING_HOUR_EN}),
            DURING_TE: frozenset({DURING_WHILE}),
            DURING_PRO: frozenset({DURING_WHILE}),
        },
        STEP_SPAN: {
            STEP_RU: frozenset({STEP_EN}),
            STEP_BIT: frozenset({STEP_BIT_EN}),
        },
        CONCORD_RULE_SPAN: {
            "никто́": frozenset({"не"}),
            "ничто́": frozenset({"не"}),
        },
    }
    from batch_c_unit_23 import pressbooks_glosses

    table.update(pressbooks_glosses())
    return table


GOVERNED_GLOSSES = _glosses()


def batch_b_relation(cue: dict, answer: dict, distractors: list[dict]):
    """Return the governed kind, False when a Batch B span forbids the pair, or None."""
    from batch_a_remaining_intake import _same_span
    from units_6_10_lessons import _displayed_in_frozen

    table = GOVERNED_GLOSSES.get(cue.get("originalSourceText") or "")
    if table is None:
        return None
    # The self line pairs the whole sentence. The token себя́ does not inherit it.
    if cue.get("originalSourceText") == REFLEXIVE_SPAN and cue.get("shippedText") == "себя́":
        return False
    group = [cue, answer, *distractors]
    allowed = table.get(cue.get("shippedText") or "")
    if (
        allowed
        and answer.get("shippedText") in allowed
        and cue.get("shippedText") != answer.get("shippedText")
        and all(_same_span(cue, record) for record in group)
        and all(_displayed_in_frozen(record) for record in group)
        and all(record.get("shippedText") != answer.get("shippedText") for record in distractors)
    ):
        return "EXPLICIT_SOURCE_PAIR"
    return False


def _check(shown, answer, wrongs, feature, why, source, prompt):
    return _gloss_check(
        shown,
        answer,
        wrongs,
        feature,
        why,
        source,
        prompt=prompt,
    )


def _reflexive(lesson) -> None:
    _frame(lesson)
    why_self = _pair(LOVE_SELF, MYSELF, SELF_CITE)
    why_wife = _pair(LOVE_WIFE, MY_WIFE, WIFE_CITE)
    explore = "Which admitted English gloss is written after the equals sign on the shown line?"
    concept = "Choose the English meaning of the shown sentence."
    lesson.objective(
        "object-reference",
        "Choose the English meaning of the shown sentence",
        "CONTRAST_SELECTION",
        ("grammar", REFLEXIVE_SPAN, WIFE_CITE),
        [
            _wiki("grammar", REFLEXIVE_SPAN, LOVE_SELF, MYSELF),
            _wiki("grammar", REFLEXIVE_SPAN, LOVE_WIFE, MY_WIFE),
        ],
        [
            _check(LOVE_SELF, MYSELF, [_quoted(LOVE_WIFE, MY_WIFE, WIFE_CITE)], "REFLEXIVE_MYSELF", why_self, GRAMMAR_SOURCE, explore),
            _check(LOVE_WIFE, MY_WIFE, [_quoted(LOVE_SELF, MYSELF, SELF_CITE)], "REFLEXIVE_WIFE", why_wife, GRAMMAR_SOURCE, concept),
            _check(LOVE_SELF, MYSELF, [_quoted(LOVE_WIFE, MY_WIFE, WIFE_CITE)], "REFLEXIVE_MYSELF_SENTENCE", why_self, GRAMMAR_SOURCE, concept),
        ],
        extra=_extra("grammar", REFLEXIVE_SPAN, [WIFE_CITE]),
    )
    _bind(lesson, "object-reference", "reflexive-object", REFLEXIVE_SPAN)


def _purpose(lesson) -> None:
    _frame(lesson)
    why_so = _pair(CHTOBY, SO_THAT, PURPOSE_CITE)
    why_if = _pair(ESLI, IF_GLOSS, IF_CITE)
    why_because = _pair(POTOMU, BECAUSE, BECAUSE_CITE)
    why_although = _pair(KHOTYA, ALTHOUGH, ALTHOUGH)
    explore = "Which admitted English gloss is written for the shown conjunction?"
    concept = "Choose the English meaning of the shown word."
    lesson.objective(
        "purpose",
        "Choose the English meaning of the shown word",
        "CONTRAST_SELECTION",
        ("grammar", PURPOSE_SPAN, PURPOSE_REMEDY),
        [
            _wiki("grammar", PURPOSE_SPAN, CHTOBY, SO_THAT),
            _wiki("grammar", PURPOSE_SPAN, ESLI, IF_GLOSS),
            _wiki("grammar", PURPOSE_SPAN, POTOMU, BECAUSE),
            _wiki("grammar", PURPOSE_SPAN, KHOTYA, ALTHOUGH),
        ],
        [
            _check(CHTOBY, SO_THAT, [_quoted(ESLI, IF_GLOSS, IF_CITE), _quoted(POTOMU, BECAUSE, BECAUSE_CITE)], "PURPOSE_SO_THAT", why_so, GRAMMAR_SOURCE, explore),
            _check(ESLI, IF_GLOSS, [_quoted(CHTOBY, SO_THAT, PURPOSE_CITE), _quoted(KHOTYA, ALTHOUGH, ALTHOUGH)], "PURPOSE_IF", why_if, GRAMMAR_SOURCE, concept),
            _check(POTOMU, BECAUSE, [_quoted(TAK_KAK, SINCE, SINCE), _quoted(POSLE, AFTER, AFTER)], "PURPOSE_BECAUSE", why_because, GRAMMAR_SOURCE, concept),
        ],
        extra=_extra("grammar", PURPOSE_SPAN, [SINCE, AFTER, ALTHOUGH, TAK_KAK, POSLE, PURPOSE_CITE, PURPOSE_REMEDY, IF_CITE, BECAUSE_CITE]),
    )
    _bind(lesson, "purpose", "purpose-clause", PURPOSE_SPAN)
    if "so that" not in PURPOSE_SPAN:
        raise SystemExit("Purpose capability span does not state so that")
    if "In order to study" in PURPOSE_SPAN:
        raise SystemExit("Purpose capability span is the study example")


def _declension(lesson) -> None:
    _frame(lesson)
    why_dat = _pair(DVUM, DATIVE, DATIVE_CITE)
    why_ins = _pair(DVUMYA, INSTRUMENTAL, INSTRUMENTAL_CITE)
    why_prep = _pair(PREP_CELL, PREPOSITIONAL, PREPOSITIONAL_CITE)
    explore = "Which admitted case name is written on the frozen line with the shown form?"
    concept = "Which case is the shown form?"
    lesson.objective(
        "declension",
        "Choose the case of the shown form",
        "CASE_RECOGNITION",
        ("morfologiya", MORF_SPAN, DATIVE_CITE),
        [
            _wiki("morfologiya", MORF_SPAN, DVUM, DATIVE),
            _wiki("morfologiya", MORF_SPAN, DVUMYA, INSTRUMENTAL),
            _wiki("morfologiya", MORF_SPAN, PREP_CELL, PREPOSITIONAL),
        ],
        [
            _check(DVUM, DATIVE, [_quoted(DVUMYA, INSTRUMENTAL, INSTRUMENTAL_CITE), _quoted(PREP_CELL, PREPOSITIONAL, PREPOSITIONAL_CITE)], "NUMERAL_DATIVE", why_dat, MORF_SOURCE, explore),
            _check(DVUMYA, INSTRUMENTAL, [_quoted(DVUM, DATIVE, DATIVE_CITE), _quoted(PREP_CELL, PREPOSITIONAL, PREPOSITIONAL_CITE)], "NUMERAL_INSTRUMENTAL", why_ins, MORF_SOURCE, concept),
            _check(PREP_CELL, PREPOSITIONAL, [_quoted(DVUM, DATIVE, DATIVE_CITE), _quoted(DVUMYA, INSTRUMENTAL, INSTRUMENTAL_CITE)], "NUMERAL_PREPOSITIONAL", why_prep, MORF_SOURCE, concept),
        ],
        extra=_extra("morfologiya", MORF_SPAN, [DATIVE_CITE, INSTRUMENTAL_CITE, PREPOSITIONAL_CITE]),
    )
    _bind(lesson, "declension", "declined-numeral", MORF_SPAN)


def _both(lesson) -> None:
    _frame(lesson)
    why_obeim = _pair(OBEIM, DATIVE, DATIVE_CITE)
    why_oboim = _pair(OBOIM, DATIVE, DATIVE_CITE)
    why_obeimi = _pair(OBEIMI, INSTRUMENTAL, INSTRUMENTAL_CITE)
    why_oboimi = _pair(OBOIMI, INSTRUMENTAL, INSTRUMENTAL_CITE)
    explore = "Which admitted case name is written on the frozen line with the shown form?"
    concept = "Which case is the shown form?"
    lesson.objective(
        "both-forms",
        "Choose the case of the shown form",
        "CASE_RECOGNITION",
        ("morfologiya", MORF_SPAN, BOTH_CELL),
        [
            _wiki("morfologiya", MORF_SPAN, OBEIM, DATIVE),
            _wiki("morfologiya", MORF_SPAN, OBOIM, DATIVE),
            _wiki("morfologiya", MORF_SPAN, OBEIMI, INSTRUMENTAL),
            _wiki("morfologiya", MORF_SPAN, OBOIMI, INSTRUMENTAL),
        ],
        [
            _check(OBEIM, DATIVE, [_quoted(OBEIMI, INSTRUMENTAL, INSTRUMENTAL_CITE), _quoted(PREP_CELL, PREPOSITIONAL, PREPOSITIONAL_CITE)], "BOTH_OBEIM", why_obeim, MORF_SOURCE, explore),
            _check(OBOIM, DATIVE, [_quoted(OBOIMI, INSTRUMENTAL, INSTRUMENTAL_CITE), _quoted(PREP_CELL, PREPOSITIONAL, PREPOSITIONAL_CITE)], "BOTH_OBOIM", why_oboim, MORF_SOURCE, concept),
            _check(OBEIMI, INSTRUMENTAL, [_quoted(OBEIM, DATIVE, DATIVE_CITE), _quoted(PREP_CELL, PREPOSITIONAL, PREPOSITIONAL_CITE)], "BOTH_OBEIMI", why_obeimi, MORF_SOURCE, concept),
            _check(OBOIMI, INSTRUMENTAL, [_quoted(OBOIM, DATIVE, DATIVE_CITE), _quoted(PREP_CELL, PREPOSITIONAL, PREPOSITIONAL_CITE)], "BOTH_OBOIMI", why_oboimi, MORF_SOURCE, concept),
        ],
        extra=_extra("morfologiya", MORF_SPAN, [PREP_CELL, PREPOSITIONAL, DATIVE_CITE, INSTRUMENTAL_CITE, PREPOSITIONAL_CITE]),
    )
    _bind(lesson, "both-forms", "both-forms", MORF_SPAN)


def _half(lesson) -> None:
    _frame(lesson)
    why_rub = _pair(POL_RUB, ROUBLES, POL_CITE)
    why_min = _pair(POL_MIN, MINUTES, POL_CITE)
    why_from = _pair(POL_FROM, FROM_ROUBLES, FROM_ROUBLES)
    why_to = _pair(POL_TO, TO_ROUBLES, TO_ROUBLES)
    explore = "Which admitted English gloss is written for the shown form?"
    concept = "Choose the English meaning of the shown phrase."
    lesson.objective(
        "half-forms",
        "Choose the English meaning of the shown phrase",
        "CONTRAST_SELECTION",
        ("poltora", POLTORA_SPAN, USAGE_NOTE),
        [
            _wiki("poltora", POLTORA_SPAN, POL_RUB, ROUBLES),
            _wiki("poltora", POLTORA_SPAN, POL_MIN, MINUTES),
            _wiki("poltora", POLTORA_SPAN, POL_FROM, FROM_ROUBLES),
            _wiki("poltora", POLTORA_SPAN, POL_TO, TO_ROUBLES),
        ],
        [
            _check(POL_RUB, ROUBLES, [_quoted(POL_MIN, MINUTES, MINUTES), _quoted(POL_HOUR, HOURS, HOURS)], "HALF_ROUBLES", why_rub, POLTORA_SOURCE, explore),
            _check(POL_MIN, MINUTES, [_quoted(POL_RUB, ROUBLES, ROUBLES), _quoted(POL_HOUR, HOURS, HOURS)], "HALF_MINUTES", why_min, POLTORA_SOURCE, concept),
            _check(POL_FROM, FROM_ROUBLES, [_quoted(POL_TO, TO_ROUBLES, TO_ROUBLES), _quoted(POL_WITH, WITH_ROUBLES, WITH_ROUBLES)], "HALF_FROM", why_from, POLTORA_SOURCE, concept),
            _check(POL_TO, TO_ROUBLES, [_quoted(POL_FROM, FROM_ROUBLES, FROM_ROUBLES), _quoted(POL_WITH, WITH_ROUBLES, WITH_ROUBLES)], "HALF_TO", why_to, POLTORA_SOURCE, concept),
        ],
        extra=_extra("poltora", POLTORA_SPAN, [POL_WITH, WITH_ROUBLES, HOURS, POL_HOUR, POL_CITE]),
    )
    _bind(lesson, "half-forms", "one-and-a-half", POLTORA_SPAN)
    if "governs the" not in POLTORA_SPAN:
        raise SystemExit("One-and-a-half span omits the usage note")


def _collective(lesson) -> None:
    _frame(lesson)
    why_dva = _pair(DVA, DVOE, COLLECTIVE_CITE)
    why_tri = _pair(TRI, TROE, COLLECTIVE_CITE)
    why_four = _pair(CHETYRE, CHETVERO, COLLECTIVE_CITE)
    explore = "Which admitted form is written in the collective numbers column for the shown cardinal?"
    concept = "Choose the collective number for the shown cardinal."
    lesson.objective(
        "collective",
        "Choose the collective number for the shown cardinal",
        "CONTRAST_SELECTION",
        ("grammar", COLLECTIVE_SPAN, COLLECTIVE_CITE),
        [
            _wiki("grammar", COLLECTIVE_SPAN, DVA, DVOE),
            _wiki("grammar", COLLECTIVE_SPAN, TRI, TROE),
            _wiki("grammar", COLLECTIVE_SPAN, CHETYRE, CHETVERO),
        ],
        [
            _check(DVA, DVOE, [_quoted(DVA, VTOROY, ORDINAL_CITE), _quoted(TRI, TROE, COLLECTIVE_CITE)], "COLLECTIVE_TWO", why_dva, GRAMMAR_SOURCE, explore),
            _check(TRI, TROE, [_quoted(TRI, TRETIY, ORDINAL_CITE), _quoted(CHETYRE, CHETVERO, COLLECTIVE_CITE)], "COLLECTIVE_THREE", why_tri, GRAMMAR_SOURCE, concept),
            _check(CHETYRE, CHETVERO, [_quoted(CHETYRE, CHETVERTYY, ORDINAL_CITE), _quoted(DVA, DVOE, COLLECTIVE_CITE)], "COLLECTIVE_FOUR", why_four, GRAMMAR_SOURCE, concept),
        ],
        extra=_extra("grammar", COLLECTIVE_SPAN, [VTOROY, TRETIY, CHETVERTYY, COLLECTIVE_CITE, ORDINAL_CITE]),
    )
    _bind(lesson, "collective", "collective-numeral", COLLECTIVE_SPAN)
    if "collective numbers" not in COLLECTIVE_SPAN:
        raise SystemExit("Collective span omits the column heading")


def _alternation(lesson) -> None:
    _frame(lesson)
    why_gen = _pair(KNIGI, ANY_BOOKS, ANY_BOOKS)
    why_acc = _pair(KNIGU, THE_BOOK, THE_BOOK)
    prompt = "Which English meaning matches the shown negated sentence?"
    lesson.objective(
        "alternation",
        "Choose the admitted gloss written for the shown negated line",
        "CONTRAST_SELECTION",
        ("grammar", NEGATION_SPAN, "the noun is indefinite"),
        [
            _wiki("grammar", NEGATION_SPAN, KNIGI, ANY_BOOKS),
            _wiki("grammar", NEGATION_SPAN, KNIGU, THE_BOOK),
        ],
        [
            _check(KNIGI, ANY_BOOKS, [_quoted(KNIGU, THE_BOOK, THE_BOOK)], "NEGATION_ANY", why_gen, GRAMMAR_SOURCE, prompt),
            _check(KNIGU, THE_BOOK, [_quoted(KNIGI, ANY_BOOKS, ANY_BOOKS)], "NEGATION_THE", why_acc, GRAMMAR_SOURCE, prompt),
            _check(KNIGI, ANY_BOOKS, [_quoted(KNIGU, THE_BOOK, THE_BOOK)], "NEGATION_GENITIVE_LINE", why_gen, GRAMMAR_SOURCE, prompt),
        ],
        extra=_extra("grammar", NEGATION_SPAN, ["the noun is indefinite"]),
    )
    _bind(lesson, "alternation", "negation-alternation", NEGATION_SPAN)


def _placement(lesson) -> None:
    """Two frozen word orders and their stated meanings. This is not a movement rule."""
    _frame(lesson)
    why_never = _pair(NEVER_RU, NEVER_EN, NEVER_CITE)
    why_anyway = _pair(ANYWAY_RU, ANYWAY_EN, ANYWAY_CITE)
    prompt = "Which English meaning matches the shown sentence?"
    lesson.objective(
        "placement",
        "Choose the admitted meaning for the shown sentence",
        "CONTRAST_SELECTION",
        ("grammar", SCOPE_SPAN, SCOPE_RULE),
        [
            _wiki("grammar", SCOPE_SPAN, NEVER_RU, NEVER_EN),
            _wiki("grammar", SCOPE_SPAN, ANYWAY_RU, ANYWAY_EN),
        ],
        [
            _check(NEVER_RU, NEVER_EN, [_quoted(ANYWAY_RU, ANYWAY_EN, ANYWAY_CITE)], "SCOPE_NEVER", why_never, GRAMMAR_SOURCE, prompt),
            _check(ANYWAY_RU, ANYWAY_EN, [_quoted(NEVER_RU, NEVER_EN, NEVER_CITE)], "SCOPE_ANYWAY", why_anyway, GRAMMAR_SOURCE, prompt),
            _check(NEVER_RU, NEVER_EN, [_quoted(ANYWAY_RU, ANYWAY_EN, ANYWAY_CITE)], "SCOPE_NEVER_AGAIN", why_never, GRAMMAR_SOURCE, prompt),
        ],
        extra=_extra("grammar", SCOPE_SPAN, [SCOPE_RULE, NEVER_CITE, ANYWAY_CITE]),
    )
    _bind(lesson, "placement", "negation-scope", SCOPE_SPAN)


def _pronouns(lesson) -> None:
    _frame(lesson)
    why_nikto = _pair(KTO, NIKTO, NEGATIVE_CITE)
    why_nichto = _pair(CHTO, NICHTO, NEGATIVE_CITE)
    why_nekto = _pair(KTO, NEKTO, PREDICATIVE_CITE)
    ni_prompt = "Which negative-pronoun form built from the shown word is not called predicative?"
    ne_prompt = "Which predicative negative-pronoun form is built from the shown word?"
    lesson.objective(
        "stand-ins",
        "Choose the negative-pronoun form built from the shown word",
        "CONTRAST_SELECTION",
        ("otritsatelnye", PRONOUN_SPAN, PREDICATIVE_CITE),
        [
            _wiki("otritsatelnye", PRONOUN_SPAN, KTO, NIKTO),
            _wiki("otritsatelnye", PRONOUN_SPAN, CHTO, NICHTO),
            _wiki("otritsatelnye", PRONOUN_SPAN, KTO, NEKTO),
        ],
        [
            _check(KTO, NIKTO, [_quoted(CHTO, NICHTO, NEGATIVE_CITE), _quoted(KTO, NEKTO, PREDICATIVE_CITE)], "PRONOUN_NIKTO", why_nikto, OTR_SOURCE, ni_prompt),
            _check(CHTO, NICHTO, [_quoted(CHTO, NECHTO, PREDICATIVE_CITE), _quoted(KTO, NIKTO, NEGATIVE_CITE)], "PRONOUN_NICHTO", why_nichto, OTR_SOURCE, ni_prompt),
            _check(KTO, NEKTO, [_quoted(KTO, NIKTO, NEGATIVE_CITE), _quoted(CHTO, NECHTO, PREDICATIVE_CITE)], "PRONOUN_NEKTO", why_nekto, OTR_SOURCE, ne_prompt),
        ],
        extra=_extra("otritsatelnye", PRONOUN_SPAN, [NEKTO, NECHTO, PREDICATIVE_CITE, NEGATIVE_CITE]),
    )
    _bind(lesson, "stand-ins", "negative-pronoun", PRONOUN_SPAN)


def _concord(lesson) -> None:
    """Obligatory negative concord. This objective is not negation-scope."""
    _frame(lesson)
    why = _pair(CONCORD_NIKTO, CONCORD_NE, CONCORD_CITE)
    prompt = "Which particle does the stated concord rule require in the predicate?"
    lesson.objective(
        "concord",
        "Choose the predicate particle required by negative concord",
        "CONTRAST_SELECTION",
        ("otritsatelnye", CONCORD_RULE_SPAN, CONCORD_CITE),
        [
            _wiki("otritsatelnye", CONCORD_RULE_SPAN, CONCORD_NIKTO, CONCORD_NE),
            _wiki("otritsatelnye", CONCORD_RULE_SPAN, CONCORD_NICHTO, CONCORD_NE),
        ],
        [
            _check(CONCORD_NIKTO, CONCORD_NE, [_quoted(CONCORD_NIKTO, CONCORD_NI, CONCORD_HEAD), _quoted(CONCORD_NIKTO, CONCORD_NIKOMU, CONCORD_HEAD)], "CONCORD_PARTICLE", why, OTR_SOURCE, prompt),
            _check(CONCORD_NICHTO, CONCORD_NE, [_quoted(CONCORD_NIKTO, CONCORD_NI, CONCORD_HEAD), _quoted(CONCORD_NIKTO, CONCORD_NIKOMU, CONCORD_HEAD)], "CONCORD_NICHTO", why.replace(CONCORD_NIKTO, CONCORD_NICHTO), OTR_SOURCE, prompt),
            _check(CONCORD_NIKTO, CONCORD_NE, [_quoted(CONCORD_NIKTO, CONCORD_NIKOMU, CONCORD_HEAD), _quoted(CONCORD_NIKTO, CONCORD_NI, CONCORD_HEAD)], "CONCORD_AGAIN", why, OTR_SOURCE, "Which predicate particle does negative concord require?"),
        ],
        extra=_extra("otritsatelnye", CONCORD_RULE_SPAN, [CONCORD_NI, CONCORD_NIKOMU, CONCORD_NICHTO, CONCORD_CITE, CONCORD_HEAD]),
    )


def _address(lesson) -> None:
    _frame(lesson)
    why_full = _pair(IRINA, FULL_NAMES, FULL_CITE)
    why_short = _pair(IRA, SHORT_NAMES, SHORT_CITE)
    why_dim = _pair(IROCHKA, AFFECTIONATE, AFFECTIONATE_CITE)
    why_reg = _pair(FULL_ADDRESS, OFFICIAL, OFFICIAL_CITE)
    list_prompt = "Which admitted list name is written for the shown form?"
    register_prompt = "Which admitted setting is written for the shown address form?"
    lesson.objective(
        "address",
        "Choose the admitted list or setting written for the shown form",
        "CONTRAST_SELECTION",
        ("obraschenie", ADDRESS_SPAN, OFFICIAL_CITE),
        [
            _wiki("obraschenie", ADDRESS_SPAN, IRINA, FULL_NAMES),
            _wiki("obraschenie", ADDRESS_SPAN, IRA, SHORT_NAMES),
            _wiki("obraschenie", ADDRESS_SPAN, IROCHKA, AFFECTIONATE),
            _wiki("obraschenie", ADDRESS_SPAN, FULL_ADDRESS, OFFICIAL),
        ],
        [
            _check(IRINA, FULL_NAMES, [_quoted(IRA, SHORT_NAMES, SHORT_CITE), _quoted(IROCHKA, AFFECTIONATE, AFFECTIONATE_CITE)], "REGISTER_FULL", why_full, OBR_SOURCE, list_prompt),
            _check(IRA, SHORT_NAMES, [_quoted(IRINA, FULL_NAMES, FULL_CITE), _quoted(IROCHKA, AFFECTIONATE, AFFECTIONATE_CITE)], "REGISTER_SHORT", why_short, OBR_SOURCE, list_prompt),
            _check(IROCHKA, AFFECTIONATE, [_quoted(IRINA, FULL_NAMES, FULL_CITE), _quoted(IRA, SHORT_NAMES, SHORT_CITE)], "REGISTER_DIMINUTIVE", why_dim, OBR_SOURCE, list_prompt),
            _check(FULL_ADDRESS, OFFICIAL, [_quoted(FULL_ADDRESS, FRIENDLY, FRIENDLY), _quoted(FULL_ADDRESS, SEMI, SEMI)], "REGISTER_OFFICIAL", why_reg, OBR_SOURCE, register_prompt),
        ],
        extra=_extra("obraschenie", ADDRESS_SPAN, [FULL_CITE, SHORT_CITE, AFFECTIONATE_CITE, OFFICIAL_CITE, FRIENDLY, SEMI]),
    )
    _bind(lesson, "address", "register", ADDRESS_SPAN)


def add_batch_b(lesson) -> None:
    {
        21: lambda item: (_reflexive(item), _purpose(item)),
        22: lambda item: (_declension(item), _both(item), _half(item), _collective(item)),
        24: lambda item: (_alternation(item), _pronouns(item), _concord(item), _placement(item)),
        25: lambda item: (_address(item),),
    }[lesson.number](lesson)


def add_unit_23(builder, unit: dict) -> None:
    """Wikipedia ago, during, and step-out lines do not build Unit 23."""
    del builder, unit
    raise SystemExit("Unit 23 uses the frozen Pressbooks time spans, not the bounded Wikipedia lines")


def _assert_closed() -> None:
    if "so that" in STUDY_SPAN and "чтобы" in STUDY_SPAN:
        pass
    if "so that" in STUDY_SPAN:
        raise SystemExit("Study span unexpectedly states so that")
    if "Чтобы" not in STUDY_SPAN:
        raise SystemExit("Study span lost Чтобы")
    if "никому" not in CONCORD_SPAN:
        raise SystemExit("Concord span lost the negative pronoun")
    if any(".ogg" in span for span in (REFLEXIVE_SPAN, PURPOSE_SPAN, POLTORA_SPAN, AGO_SPAN, DURING_SPAN, STEP_SPAN)):
        raise SystemExit("Admitted span includes audio")


_assert_closed()
