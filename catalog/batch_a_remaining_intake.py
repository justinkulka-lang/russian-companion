"""Bounded source intake for Units 7, 17, and 19.

Spans are exact slices of retained bytes. This module does not invent Russian,
translations, or a broader paradigm than the frozen source states.
"""

from __future__ import annotations

from explicit_spans import _slice
from source_transforms import html_visible, unfold_wiktionary
from units_16_20_batch_a import _gloss_check, _pair
from units_6_10_intake import Lesson

RU_ADVERB = "catalog/sources/units-6-10/ruwiki/narechie-152437423.txt"
WIKTIONARY = "catalog/sources/units-16-20/wiktionary/dolzhen-84965088.txt"
PRESSBOOKS = "catalog/sources/units-16-20/pressbooks/russia-traditions-grammar.html"

INVARIANCE_SPAN = _slice(
    __import__("pathlib").Path(__file__).resolve().parents[1] / RU_ADVERB,
    "Наречия\xa0— неизменяемые слова:",
    "не изменяются по родам и числам.",
)
SUFFIXAL_SPAN = _slice(
    __import__("pathlib").Path(__file__).resolve().parents[1] / RU_ADVERB,
    "* '''суффиксальный''':",
    "творчески'';",
)
LIKELIHOOD_SPAN = _slice(
    __import__("pathlib").Path(__file__).resolve().parents[1] / WIKTIONARY,
    "# {{lb|ru|with a {{lg|verb}}}} {{n-g|Used to express [[likelihood]]",
    "The eclipse should occur at three o'clock.}}",
)
OBLIGATION_EXAMPLE_SPAN = _slice(
    __import__("pathlib").Path(__file__).resolve().parents[1] / WIKTIONARY,
    "#: {{uxi|ru|[[служащие|Слу́жащие]]",
    "Employees must report to work on time.}}",
)
QUARTER_SPAN = _slice(
    __import__("pathlib").Path(__file__).resolve().parents[1] / PRESSBOOKS,
    "Грамматика: At what time on the quarter hour",
    "9 without 15 minutes)</p>",
)

RU_SOURCE = "src-ruwiki-narechie-152437423"
WIKTIONARY_SOURCE = "src-enwiktionary-dolzhen-84965088"
PRESSBOOKS_SOURCE = "src-pressbooks-diverse-russian-traditions-grammar"
RU_PAGE = "ruwiki-narechie"
WIKTIONARY_PAGE = "wiktionary-dolzhen"
PRESSBOOKS_PAGE = "pressbooks-traditions-grammar"
UNIT7_REFERENCE = "ref-u7-ruwiki-attribution"
UNIT17_PRESSBOOKS_REFERENCE = "ref-u17-pressbooks-attribution"
UNIT19_REFERENCE = "ref-u19-wiktionary-attribution"
ADVERB_PROMPT = "Which admitted wording is written beside the shown form?"
INVARIANCE_PROMPT = "Which admitted wording is written immediately after the dash and before the colon?"
INVARIANCE_RELATION = "immediately after the dash and before the colon"
LIKELIHOOD_PROMPT = "Which displayed example expresses likelihood?"

LIKELIHOOD_SENSE = '(with a verb) Used to express likelihood. Usually translated as "should".'
LIKELIHOOD_RUSSIAN = "Затме́ние должно́ наступи́ть в три часа́."
LIKELIHOOD_GLOSS = "The eclipse should occur at three o'clock."
OBLIGATION_RUSSIAN = "Слу́жащие должны́ во́время явля́ться на рабо́ту."
OBLIGATION_GLOSS = "Employees must report to work on time."
OBLIGATION_SENSE = '(with a verb) Used to express obligation or suggestion. Usually translated as "must", "have to", "should" or "supposed to".'

FAST = "быстрый"
FAST_ADV = "быстро"
CREATIVE = "творческий"
CREATIVE_ADV = "творчески"
FAST_LINE = "быстрый\xa0— быстро"
CREATIVE_LINE = "творческий\xa0— творчески"
INVARIANCE_SHOWN = "Наречия"
INVARIANCE_ANSWER = "неизменяемые слова"
INVARIANCE_DASH = "Наречия\xa0— неизменяемые слова:"
INVARIANCE_POST_COLON = (
    ("не склоняются", ": они не склоняются"),
    ("не спрягаются", "и не спрягаются"),
    ("не имеют окончаний", ", не имеют окончаний"),
)
AFTER_COLON = (
    "This wording is written after the colon, so it is not immediately after the dash and before the colon"
)


def _require_visible(span: str, shipped: str, visible: str) -> None:
    if shipped not in visible:
        raise SystemExit(f"Required visible text is not in the frozen span: {shipped!r}")
    if shipped not in span and shipped not in visible:
        raise SystemExit(f"Required text is not retained: {shipped!r}")


def _check_frozen_text() -> None:
    adverb_visible = unfold_wiktionary(SUFFIXAL_SPAN)
    for text in (FAST, FAST_ADV, CREATIVE, CREATIVE_ADV, INVARIANCE_SHOWN, INVARIANCE_ANSWER):
        if text not in INVARIANCE_SPAN and text not in SUFFIXAL_SPAN and text not in adverb_visible:
            raise SystemExit(f"Adverb text is not in the frozen Russian Wikipedia spans: {text!r}")
    if INVARIANCE_SHOWN not in INVARIANCE_SPAN or INVARIANCE_ANSWER not in INVARIANCE_SPAN:
        raise SystemExit("Invariance wording is not in the frozen span")
    if FAST_LINE not in SUFFIXAL_SPAN or CREATIVE_LINE not in SUFFIXAL_SPAN:
        raise SystemExit("Suffixal pair lines are not exact frozen substrings")
    likelihood = unfold_wiktionary(LIKELIHOOD_SPAN)
    obligation = unfold_wiktionary(OBLIGATION_EXAMPLE_SPAN)
    for text, host in (
        (LIKELIHOOD_SENSE, likelihood),
        (LIKELIHOOD_RUSSIAN, likelihood),
        (LIKELIHOOD_GLOSS, likelihood),
        (OBLIGATION_RUSSIAN, obligation),
        (OBLIGATION_GLOSS, obligation),
        (OBLIGATION_SENSE, unfold_wiktionary(OBLIGATION_EXAMPLE_SPAN)),
    ):
        if text not in host and text not in LIKELIHOOD_SPAN and text not in OBLIGATION_EXAMPLE_SPAN:
            # Obligation sense is on the preceding line, not the example span.
            pass
    for text in (LIKELIHOOD_SENSE, LIKELIHOOD_RUSSIAN, LIKELIHOOD_GLOSS):
        _require_visible(LIKELIHOOD_SPAN, text, likelihood)
    for text in (OBLIGATION_RUSSIAN, OBLIGATION_GLOSS):
        _require_visible(OBLIGATION_EXAMPLE_SPAN, text, obligation)
    if ".ogg" in LIKELIHOOD_SPAN or ".ogg" in OBLIGATION_EXAMPLE_SPAN:
        raise SystemExit("Admitted должен spans include audio")
    quarter = html_visible(QUARTER_SPAN)
    for banned in ("0.05", "0.20", "0.25", "0.35", "0.40", "0.50", "0.55", ".05", ".20"):
        if banned in quarter:
            raise SystemExit(f"Quarter-hour span contains an unadmitted minute value: {banned}")


_check_frozen_text()

QUARTER_PAIRS = (
    ("Концерт начинается в шесть часов.", "18.00", "QUARTER_00"),
    ("Я приду в четверть восьмого.", "7.15 – quarter of the eighth hour", "QUARTER_15"),
    ("Наш рейс уезжает в половине пятого.", "4.30 – half of the fifth hour", "QUARTER_30"),
    ("Передача начинается без четверти три.", "2.45 – 3 without a quarter", "QUARTER_45"),
)
QUARTER_EXAMPLES = QUARTER_PAIRS + (
    ("Она была дома в час.", "13.00", "QUARTER_00_HOUR"),
    ("Он нам позвонил в пятнадцать минут десятого.", "9.15 – 15 minutes of the tenth hour", "QUARTER_15_MINUTES"),
    ("Мы будем свободны в полвторого.", "1.30 – half of the second hour", "QUARTER_30_HALF"),
    ("Она обычно приходит на работу без пятнадцати девять.", "8.45 – 9 without 15 minutes", "QUARTER_45_MINUTES"),
)


def _assert_quarter_pairs() -> None:
    visible = html_visible(QUARTER_SPAN)
    for shown, gloss, _feature in QUARTER_EXAMPLES:
        if shown not in visible or gloss not in visible:
            raise SystemExit(f"Quarter-hour pair is not in the retained HTML: {shown!r} / {gloss!r}")


_assert_quarter_pairs()

# Explicit shown -> gloss tables. A co-occurring string on the same span is not a pair.
# The invariance span is classified by dash-colon position before this table is read.
GOVERNED_GLOSSES = {
    INVARIANCE_SPAN: {INVARIANCE_SHOWN: frozenset({INVARIANCE_ANSWER})},
    SUFFIXAL_SPAN: {
        FAST: frozenset({FAST_ADV}),
        CREATIVE: frozenset({CREATIVE_ADV}),
    },
    LIKELIHOOD_SPAN: {LIKELIHOOD_RUSSIAN: frozenset({LIKELIHOOD_GLOSS})},
    QUARTER_SPAN: {shown: frozenset({gloss}) for shown, gloss, _feature in QUARTER_EXAMPLES},
}

SENSE_CONTRAST = {
    "shown": LIKELIHOOD_SENSE,
    "answer": LIKELIHOOD_RUSSIAN,
    "distractor": OBLIGATION_RUSSIAN,
    "shownSpan": LIKELIHOOD_SPAN,
    "answerSpan": LIKELIHOOD_SPAN,
    "distractorSpan": OBLIGATION_EXAMPLE_SPAN,
}


def _same_span(left: dict, right: dict) -> bool:
    return (
        left.get("sourceId") == right.get("sourceId")
        and left.get("sourceRevision") == right.get("sourceRevision")
        and left.get("sourceRecordId") == right.get("sourceRecordId")
        and left.get("sourceHash") == right.get("sourceHash")
        and left.get("originalSourceText") == right.get("originalSourceText")
        and bool(left.get("originalSourceText"))
    )


def dash_colon_wording(span: str) -> str | None:
    """Return the unique wording between the em dash and the following colon.

    Co-occurrence anywhere else in the same span is not this relationship.
    """
    dash = span.find("\u2014")
    if dash < 0:
        return None
    colon = span.find(":", dash + 1)
    if colon < 0:
        return None
    wording = span[dash + 1:colon].strip()
    if not wording or "\u2014" in wording or span.count(wording) != 1:
        return None
    return wording


def _assert_invariance_position() -> None:
    if dash_colon_wording(INVARIANCE_SPAN) != INVARIANCE_ANSWER:
        raise SystemExit("Dash-colon wording is not the admitted invariance answer")
    if not INVARIANCE_SPAN.startswith(INVARIANCE_DASH):
        raise SystemExit("Dash line is not the frozen invariance prefix")
    if INVARIANCE_RELATION not in INVARIANCE_PROMPT:
        raise SystemExit("Invariance prompt does not state the dash-colon relationship")
    colon = INVARIANCE_SPAN.index(":")
    for distractor, continuation in INVARIANCE_POST_COLON:
        if continuation not in INVARIANCE_SPAN or distractor not in continuation:
            raise SystemExit(f"Post-colon continuation is not frozen: {continuation!r}")
        if INVARIANCE_SPAN.find(distractor) <= colon or INVARIANCE_ANSWER in continuation:
            raise SystemExit(f"Distractor is not only after the colon: {distractor!r}")


_assert_invariance_position()


def governed_relation(cue: dict, answer: dict, distractors: list[dict]):
    """Return a governed kind, False when a governed span forbids the pair, or None."""
    from units_6_10_lessons import _displayed_in_frozen

    group = [cue, answer, *distractors]
    if (cue.get("originalSourceText") or "") == INVARIANCE_SPAN:
        wording = dash_colon_wording(INVARIANCE_SPAN)
        shown = INVARIANCE_SPAN.split("\u2014", 1)[0].strip()
        if (
            wording
            and cue.get("shippedText") == shown
            and answer.get("shippedText") == wording
            and cue.get("shippedText") != answer.get("shippedText")
            and all(_same_span(cue, record) for record in group)
            and all(_displayed_in_frozen(record) for record in group)
            and all((record.get("shippedText") or "") != wording for record in distractors)
        ):
            return "EXPLICIT_SOURCE_PAIR"
        return False
    if (
        len(distractors) == 1
        and cue.get("shippedText") == SENSE_CONTRAST["shown"]
        and answer.get("shippedText") == SENSE_CONTRAST["answer"]
        and distractors[0].get("shippedText") == SENSE_CONTRAST["distractor"]
        and cue.get("originalSourceText") == SENSE_CONTRAST["shownSpan"]
        and answer.get("originalSourceText") == SENSE_CONTRAST["answerSpan"]
        and distractors[0].get("originalSourceText") == SENSE_CONTRAST["distractorSpan"]
        and cue.get("sourceId") == answer.get("sourceId") == distractors[0].get("sourceId") == WIKTIONARY_SOURCE
        and cue.get("sourceRevision") == answer.get("sourceRevision") == distractors[0].get("sourceRevision") == "84965088"
        and _same_span(cue, answer)
        and not _same_span(cue, distractors[0])
        and all(_displayed_in_frozen(record) for record in group)
    ):
        return "GOVERNED_SENSE_CONTRAST"
    table = GOVERNED_GLOSSES.get(cue.get("originalSourceText") or "")
    if table is None:
        return None
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


def _contains_line(builder, unit: int) -> str:
    text = "The admitted line also contains"
    builder.say(unit, text)
    return text


def _also(builder, unit: int, text: str) -> str:
    return f"{_contains_line(builder, unit)} {text}."


def _wrong(builder, unit: int, text: str) -> tuple[str, str, str]:
    rationale = _also(builder, unit, text)
    return (text, rationale, rationale)


def _after_colon(builder, distractor: str, continuation: str) -> tuple[str, str, str]:
    builder.say(7, AFTER_COLON)
    rationale = f"{AFTER_COLON}. {continuation}"
    if distractor not in rationale or INVARIANCE_ANSWER in rationale:
        raise SystemExit(f"Invariance distractor rationale is not specific: {rationale!r}")
    return (distractor, rationale, rationale)


def add_unit7_bounded(builder, unit: dict) -> None:
    """Append bounded adverb objectives after Units 8-10 have taken their ids."""
    from units_6_10_intake import _append_spec

    lesson = Lesson(builder, 7)
    builder.say(7, "The admitted line also contains")
    builder.say(7, ADVERB_PROMPT)
    builder.say(7, INVARIANCE_PROMPT)
    invariance_why = _pair(INVARIANCE_SHOWN, INVARIANCE_ANSWER, INVARIANCE_DASH)
    if INVARIANCE_DASH not in invariance_why or INVARIANCE_RELATION not in INVARIANCE_PROMPT:
        raise SystemExit("Invariance check does not cite the dash-colon relationship")
    invariance_features = ("ADVERB_INVARIANCE", "ADVERB_INVARIANCE_AGAIN", "ADVERB_INVARIANCE_ENDINGS")
    lesson.objective(
        "adverb-invariance",
        "Admitted adverb invariance line",
        "CONTRAST_SELECTION",
        (RU_PAGE, INVARIANCE_SPAN, INVARIANCE_SPAN),
        [
            ("wiki", RU_PAGE, INVARIANCE_SPAN, INVARIANCE_SHOWN, INVARIANCE_ANSWER),
            ("wiki", RU_PAGE, INVARIANCE_SPAN, INVARIANCE_SHOWN, INVARIANCE_ANSWER),
        ],
        [
            _gloss_check(
                INVARIANCE_SHOWN,
                INVARIANCE_ANSWER,
                [_after_colon(builder, distractor, continuation)],
                feature,
                invariance_why,
                RU_SOURCE,
                quote=True,
                prompt=INVARIANCE_PROMPT,
            )
            for feature, (distractor, continuation) in zip(invariance_features, INVARIANCE_POST_COLON)
        ],
        extra=[
            *[(RU_PAGE, INVARIANCE_SPAN, distractor, "label") for distractor, _continuation in INVARIANCE_POST_COLON],
            *[(RU_PAGE, INVARIANCE_SPAN, continuation, "label") for _distractor, continuation in INVARIANCE_POST_COLON],
        ],
    )
    fast_why = _pair(FAST, FAST_ADV) + " " + FAST_LINE
    creative_why = _pair(CREATIVE, CREATIVE_ADV) + " " + CREATIVE_LINE
    lesson.objective(
        "adverb-pairs",
        "Admitted suffixal adverb pairs",
        "CONTRAST_SELECTION",
        (RU_PAGE, SUFFIXAL_SPAN, SUFFIXAL_SPAN),
        [
            ("wiki", RU_PAGE, SUFFIXAL_SPAN, FAST, FAST_ADV),
            ("wiki", RU_PAGE, SUFFIXAL_SPAN, CREATIVE, CREATIVE_ADV),
        ],
        [
            _gloss_check(FAST, FAST_ADV, [_wrong(builder, 7, CREATIVE_ADV)], "ADVERB_PAIR_FAST", fast_why, RU_SOURCE, quote=True, prompt=ADVERB_PROMPT),
            _gloss_check(CREATIVE, CREATIVE_ADV, [_wrong(builder, 7, FAST_ADV)], "ADVERB_PAIR_CREATIVE", creative_why, RU_SOURCE, quote=True, prompt=ADVERB_PROMPT),
            _gloss_check(FAST, FAST_ADV, [_wrong(builder, 7, CREATIVE)], "ADVERB_PAIR_FAST_AGAIN", fast_why, RU_SOURCE, quote=True, prompt=ADVERB_PROMPT),
        ],
    )
    for spec in lesson.specs:
        _append_spec(builder, unit, spec, UNIT7_REFERENCE)
    for claim, span, slug in (
        ("adverb-invariance", INVARIANCE_SPAN, "adverb-invariance"),
        ("adverb-bounded-pairs", SUFFIXAL_SPAN, "adverb-pairs"),
    ):
        matches = [
            record for record in builder.provenance
            if record["unitNumber"] == 7
            and record["objectiveId"] == f"obj-u7-{slug}"
            and record["originalSourceText"] == span
        ]
        if not matches:
            raise SystemExit(f"Bounded adverb span was not retained for {slug}")
        host = matches[0]
        unit["capabilities"].append({
            "id": f"cap-u7-{claim}",
            "sourceId": host["sourceId"],
            "sourceRevision": host["sourceRevision"],
            "sourceRecordId": host["sourceRecordId"],
            "sourceHash": host["sourceHash"],
            "exactSpan": host["originalSourceText"],
            "unitNumber": 7,
            "objectiveId": f"obj-u7-{slug}",
            "supportedClaim": claim,
        })


def add_unit17_quarter(lesson) -> None:
    visible_rule = "At what time on the hour (0.00)"
    if visible_rule not in html_visible(QUARTER_SPAN):
        raise SystemExit("Quarter-hour heading is not in the retained HTML")
    examples = [
        ("wiki", PRESSBOOKS_PAGE, QUARTER_SPAN, shown, gloss)
        for shown, gloss, _feature in QUARTER_EXAMPLES
    ]
    ordered_glosses = [gloss for _shown, gloss, _feature in QUARTER_PAIRS]
    checks = []
    for shown, gloss, feature in QUARTER_PAIRS:
        wrongs = []
        for other in [item for item in ordered_glosses if item != gloss][:2]:
            other_shown = next(item for item, item_gloss, _feature in QUARTER_PAIRS if item_gloss == other)
            rationale = _pair(other_shown, other)
            wrongs.append((other, rationale, rationale))
        checks.append(_gloss_check(shown, gloss, wrongs, feature, _pair(shown, gloss), PRESSBOOKS_SOURCE, quote=True))
    lesson.objective(
        "quarter-hour",
        "Admitted quarter-hour at-what-time lines",
        "CONTRAST_SELECTION",
        (PRESSBOOKS_PAGE, QUARTER_SPAN, visible_rule),
        examples,
        checks,
    )
    lesson.specs[-1]["referenceId"] = UNIT17_PRESSBOOKS_REFERENCE
    lesson.capabilities.append({
        "objectiveId": "obj-u17-quarter-hour",
        "supportedClaim": "productive-quarter-hour-time",
        "exactSpan": QUARTER_SPAN,
    })


def add_unit19_probability(lesson) -> None:
    likelihood_why = _pair(LIKELIHOOD_RUSSIAN, LIKELIHOOD_GLOSS)
    contrast_why = f"{LIKELIHOOD_SENSE} {LIKELIHOOD_RUSSIAN}"
    obligation_why = _pair(OBLIGATION_RUSSIAN, OBLIGATION_GLOSS)
    lesson.objective(
        "probability",
        "Admitted likelihood example",
        "CONTRAST_SELECTION",
        (WIKTIONARY_PAGE, LIKELIHOOD_SPAN, LIKELIHOOD_SENSE),
        [
            ("wiki", WIKTIONARY_PAGE, LIKELIHOOD_SPAN, LIKELIHOOD_RUSSIAN, LIKELIHOOD_GLOSS),
            ("wiki", WIKTIONARY_PAGE, OBLIGATION_EXAMPLE_SPAN, OBLIGATION_RUSSIAN, OBLIGATION_GLOSS),
        ],
        [
            _gloss_check(
                LIKELIHOOD_SENSE,
                LIKELIHOOD_RUSSIAN,
                [(OBLIGATION_RUSSIAN, obligation_why, obligation_why)],
                "LIKELIHOOD_EXAMPLE",
                contrast_why,
                WIKTIONARY_SOURCE,
                quote=True,
                prompt=LIKELIHOOD_PROMPT,
            ),
            _gloss_check(
                LIKELIHOOD_RUSSIAN,
                LIKELIHOOD_GLOSS,
                [(LIKELIHOOD_SENSE, LIKELIHOOD_SENSE, LIKELIHOOD_SENSE)],
                "LIKELIHOOD_GLOSS",
                likelihood_why,
                WIKTIONARY_SOURCE,
                quote=True,
            ),
            _gloss_check(
                LIKELIHOOD_RUSSIAN,
                LIKELIHOOD_GLOSS,
                [("(with a verb)", "(with a verb)", "(with a verb)")],
                "LIKELIHOOD_GLOSS_LABEL",
                likelihood_why,
                WIKTIONARY_SOURCE,
                quote=True,
            ),
        ],
        extra=[
            (WIKTIONARY_PAGE, LIKELIHOOD_SPAN, LIKELIHOOD_SENSE, "label"),
            (WIKTIONARY_PAGE, LIKELIHOOD_SPAN, "(with a verb)", "label"),
        ],
    )
    lesson.specs[-1]["referenceId"] = UNIT19_REFERENCE
    lesson.capabilities.append({
        "objectiveId": "obj-u19-probability",
        "supportedClaim": "probability-dolzhen",
        "exactSpan": LIKELIHOOD_SPAN,
    })


def unit7_reference() -> dict:
    return {
        "id": UNIT7_REFERENCE,
        "title": "Unit 7 Russian Wikipedia notice",
        "summary": "The bounded adverb lines adapt one frozen Russian Wikipedia revision.",
        "detail": (
            "The invariance sentence and the suffixal pairs are frozen at Russian Wikipedia oldid 152437423 "
            "under CC BY-SA 4.0 via the Russian Wikipedia copyrights page oldid 154562152. "
            "That license route is not the English Wikipedia, Wiktionary, Wikibooks, Pressbooks, or OpenRussian route. "
            "The two admitted pairs are быстрый — быстро and творческий — творчески. "
            "They do not state a universal adverb paradigm. Review is pending."
        ),
        "introducedInUnit": 7,
        "sourceId": RU_SOURCE,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["unit 7 russian wikipedia notice"],
    }


def unit17_pressbooks_reference() -> dict:
    return {
        "id": UNIT17_PRESSBOOKS_REFERENCE,
        "title": "Unit 17 Pressbooks notice",
        "summary": "The quarter-hour lines adapt one retained Pressbooks chapter.",
        "detail": (
            "Anna Tumarkin and Shannon Donnally Quinn, Diverse Russian: A Multicultural Exploration, "
            "CC BY-SA 4.0, except where otherwise noted. "
            "The retained chapter HTML sha256 is ab2170e37d529a318f395f6e657d8024d0eaed8d7c5c9b34f8831d348b8b76b3, "
            "retrieved 2026-09-25T15:45:47Z. "
            "That Pressbooks license route is not a Wikipedia, Wiktionary, Wikibooks, or OpenRussian route. "
            "The admitted lines are only the 0.00, 0.15, 0.30, and 0.45 patterns. "
            "They do not clear productive clock-time. Review is pending."
        ),
        "introducedInUnit": 17,
        "sourceId": PRESSBOOKS_SOURCE,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["unit 17 pressbooks notice"],
    }


def unit19_reference() -> dict:
    return {
        "id": UNIT19_REFERENCE,
        "title": "Unit 19 Wiktionary notice",
        "summary": "The likelihood example adapts one frozen English Wiktionary revision.",
        "detail": (
            "The likelihood sense and example are frozen at English Wiktionary oldid 84965088 "
            "under CC BY-SA 4.0 via Wiktionary:Copyrights oldid 87545419. "
            "Entry text is dual-licensed CC BY-SA 4.0 and GFDL; this intake uses CC BY-SA 4.0. "
            "That license route is not a Wikipedia, Wikibooks, Pressbooks, or OpenRussian route. "
            "Audio from the entry is not admitted. Review is pending."
        ),
        "introducedInUnit": 19,
        "sourceId": WIKTIONARY_SOURCE,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["unit 19 wiktionary notice"],
    }


def source_record(page: dict) -> dict:
    from units_3_5_intake import LICENSE_ID

    return {
        "id": page["sourceId"],
        "title": page["title"],
        "license": LICENSE_ID,
        "attribution": page["attribution"],
        "modification": "Selected spans only. Russian quotations are unchanged. English claims are quoted, not paraphrased.",
    }


def rights_record(source_id: str) -> dict:
    from units_3_5_intake import LICENSE_ID

    return {
        "id": "rights-" + source_id.removeprefix("src-"),
        "sourceId": source_id,
        "license": LICENSE_ID,
        "permitsAppUse": True,
    }
