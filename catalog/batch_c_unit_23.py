"""Unit 23 elapsed-time lessons from one frozen Pressbooks chapter.

Every shipped string is an exact substring of the retained chapter. This module
does not invent Russian, translations, or a contrast the chapter does not state.
"""

from __future__ import annotations

from pathlib import Path

from explicit_spans import _slice
from units_11_15_lessons import _wiki

_PAGE = Path(__file__).resolve().parents[1] / "catalog/sources/units-21-25/pressbooks/usa-grammar.html"
PRESS_SOURCE = "src-pressbooks-diverse-russian-usa-grammar"
PAGE = "usa-grammar"

RANGE_SPAN = _slice(_PAGE, "in a certain decade", "in the 1990s")
BEGIN_SPAN = _slice(_PAGE, "(at the end, at the beginning, in the middle of)", "in the middle of the week")
DURING_EVENT_SPAN = _slice(_PAGE, "actions occurring during another event", "During the trip I met new friends.")
DURATION_SPAN = _slice(
    _PAGE,
    "It emphasizes the duration over which something occurs.",
    "During the course of the day, the weather changed several times.",
)
ELAPSED_SPAN = _slice(_PAGE, "relate events in time", "Мы ездили в Казахстан год назад.")


def _need(span: str, text: str) -> str:
    if text not in span:
        raise SystemExit(f"Shipped text is not in the frozen Pressbooks span: {text!r}")
    if any(mark in text for mark in ("<", ">", "{{", "}}", "[[", "]]")):
        raise SystemExit(f"Shipped Pressbooks text keeps markup: {text!r}")
    return text


SIXTIES = _need(RANGE_SPAN, "В шестидесятых годах")
NINETIES = _need(RANGE_SPAN, "В девяностых годах")
IN_1960 = _need(RANGE_SPAN, "in the 1960s")
IN_1990 = _need(RANGE_SPAN, "in the 1990s")
RANGE_CITE = _need(RANGE_SPAN, "in a certain decade")

END_YEAR = _need(BEGIN_SPAN, "В конце года")
BEGIN_CENTURY = _need(BEGIN_SPAN, "В начале века")
MID_WEEK = _need(BEGIN_SPAN, "В середине недели")
END_EN = _need(BEGIN_SPAN, "at the end of the year")
BEGIN_EN = _need(BEGIN_SPAN, "at the beginning of the century")
MID_EN = _need(BEGIN_SPAN, "in the middle of the week")
BEGIN_CITE = _need(BEGIN_SPAN, "at the end, at the beginning, in the middle of")

WAR = _need(DURING_EVENT_SPAN, "Она родилась во время войны.")
TRIP = _need(DURING_EVENT_SPAN, "Во время поездки я познакомился с новыми друзьями.")
WAR_EN = _need(DURING_EVENT_SPAN, "She was born during the war.")
TRIP_EN = _need(DURING_EVENT_SPAN, "During the trip I met new friends.")
DURING_CITE = _need(DURING_EVENT_SPAN, "during another event")

LESSON_RU = _need(DURATION_SPAN, "В течение урока ученики работали над проектом.")
DAY_RU = _need(DURATION_SPAN, "В течение дня погода менялась несколько раз.")
LESSON_EN = _need(DURATION_SPAN, "Throughout the lesson, the students worked on the project.")
DAY_EN = _need(DURATION_SPAN, "During the course of the day, the weather changed several times.")
DURATION_CITE = _need(DURATION_SPAN, "It emphasizes the duration over which something occurs.")

BEFORE_RU = _need(ELAPSED_SPAN, "Перед ужином мы ходили в парк.")
AFTER_RU = _need(ELAPSED_SPAN, "После работы я смотрела фильм.")
IN_RU = _need(ELAPSED_SPAN, "Мы едем в Латвию через год.")
AGO_RU = _need(ELAPSED_SPAN, "Мы ездили в Казахстан год назад.")
BEFORE_EN = _need(ELAPSED_SPAN, "before")
AFTER_EN = _need(ELAPSED_SPAN, "after")
IN_EN = _need(ELAPSED_SPAN, "in (a period of time)")
AGO_EN = _need(ELAPSED_SPAN, "ago")
ELAPSED_CITE = _need(ELAPSED_SPAN, "relate events in time")

if "emphasizes the duration" not in DURATION_SPAN:
    raise SystemExit("Duration span lost the stated duration contrast")
if any(mark in span for span in (RANGE_SPAN, BEGIN_SPAN, DURING_EVENT_SPAN, DURATION_SPAN, ELAPSED_SPAN) for mark in (".ogg", ".mp3")):
    raise SystemExit("Pressbooks time span includes audio")


def pressbooks_glosses() -> dict[str, dict[str, frozenset[str]]]:
    return {
        RANGE_SPAN: {
            SIXTIES: frozenset({IN_1960}),
            NINETIES: frozenset({IN_1990}),
        },
        BEGIN_SPAN: {
            END_YEAR: frozenset({END_EN}),
            BEGIN_CENTURY: frozenset({BEGIN_EN}),
            MID_WEEK: frozenset({MID_EN}),
        },
        DURING_EVENT_SPAN: {
            WAR: frozenset({WAR_EN}),
            TRIP: frozenset({TRIP_EN}),
        },
        DURATION_SPAN: {
            LESSON_RU: frozenset({LESSON_EN}),
            DAY_RU: frozenset({DAY_EN}),
        },
        ELAPSED_SPAN: {
            BEFORE_RU: frozenset({BEFORE_EN}),
            AFTER_RU: frozenset({AFTER_EN}),
            IN_RU: frozenset({IN_EN}),
            AGO_RU: frozenset({AGO_EN}),
        },
    }


def _objective(lesson, slug, title, span, cite, examples, checks) -> None:
    from batch_b_units_21_25 import _bind, _check, _extra, _pair, _quoted

    lesson.objective(
        slug,
        title,
        "CONTRAST_SELECTION",
        (PAGE, span, cite),
        [_wiki(PAGE, span, ru, en) for ru, en in examples],
        [
            _check(
                shown,
                answer,
                [_quoted(other_shown, other_answer, other_answer)],
                feature,
                _pair(shown, answer, citation),
                PRESS_SOURCE,
                prompt,
            )
            for shown, answer, other_shown, other_answer, feature, citation, prompt in checks
        ],
        extra=_extra(PAGE, span, [cite]),
    )
    if slug == "duration":
        _bind(lesson, slug, "elapsed-time-duration", span)


def add_unit_23(lesson) -> None:
    """One concept per admitted contrast. Only the duration span clears the required claim."""
    from batch_b_units_21_25 import _frame

    _frame(lesson)
    meaning = "Which English meaning matches the shown time expression?"
    _objective(
        lesson,
        "ranges",
        "Choose the English meaning of the shown decade range",
        RANGE_SPAN,
        RANGE_CITE,
        [(SIXTIES, IN_1960), (NINETIES, IN_1990)],
        [
            (SIXTIES, IN_1960, NINETIES, IN_1990, "DECADE_RANGE", IN_1960, meaning),
            (NINETIES, IN_1990, SIXTIES, IN_1960, "DECADE_RANGE", IN_1990, meaning),
            (SIXTIES, IN_1960, NINETIES, IN_1990, "DECADE_RANGE", IN_1960, "Which English decade meaning matches the shown range?"),
        ],
    )
    _objective(
        lesson,
        "beginning",
        "Choose the English meaning of the shown beginning or middle expression",
        BEGIN_SPAN,
        BEGIN_CITE,
        [(END_YEAR, END_EN), (BEGIN_CENTURY, BEGIN_EN), (MID_WEEK, MID_EN)],
        [
            (END_YEAR, END_EN, BEGIN_CENTURY, BEGIN_EN, "BEGINNING_MIDDLE", END_EN, meaning),
            (BEGIN_CENTURY, BEGIN_EN, MID_WEEK, MID_EN, "BEGINNING_MIDDLE", BEGIN_EN, meaning),
            (MID_WEEK, MID_EN, END_YEAR, END_EN, "BEGINNING_MIDDLE", MID_EN, meaning),
        ],
    )
    _objective(
        lesson,
        "during-event",
        "Choose the English meaning of the shown during-an-event expression",
        DURING_EVENT_SPAN,
        DURING_CITE,
        [(WAR, WAR_EN), (TRIP, TRIP_EN)],
        [
            (WAR, WAR_EN, TRIP, TRIP_EN, "DURING_EVENT", DURING_CITE, meaning),
            (TRIP, TRIP_EN, WAR, WAR_EN, "DURING_EVENT", DURING_CITE, meaning),
            (WAR, WAR_EN, TRIP, TRIP_EN, "DURING_EVENT", DURING_CITE, "Which English meaning matches this during-an-event expression?"),
        ],
    )
    _objective(
        lesson,
        "duration",
        "Choose the English meaning of the shown duration expression",
        DURATION_SPAN,
        DURATION_CITE,
        [(LESSON_RU, LESSON_EN), (DAY_RU, DAY_EN)],
        [
            (LESSON_RU, LESSON_EN, DAY_RU, DAY_EN, "ELAPSED_DURATION", DURATION_CITE, meaning),
            (DAY_RU, DAY_EN, LESSON_RU, LESSON_EN, "ELAPSED_DURATION", DURATION_CITE, meaning),
            (LESSON_RU, LESSON_EN, DAY_RU, DAY_EN, "ELAPSED_DURATION", DURATION_CITE, "Which English meaning matches this duration expression?"),
        ],
    )
    _objective(
        lesson,
        "elapsed",
        "Choose the English meaning of the shown before, after, in, or ago expression",
        ELAPSED_SPAN,
        ELAPSED_CITE,
        [(BEFORE_RU, BEFORE_EN), (AFTER_RU, AFTER_EN), (IN_RU, IN_EN), (AGO_RU, AGO_EN)],
        [
            (BEFORE_RU, BEFORE_EN, AFTER_RU, AFTER_EN, "ELAPSED_RELATION", ELAPSED_CITE, meaning),
            (IN_RU, IN_EN, AGO_RU, AGO_EN, "ELAPSED_RELATION", ELAPSED_CITE, meaning),
            (AGO_RU, AGO_EN, IN_RU, IN_EN, "ELAPSED_RELATION", ELAPSED_CITE, meaning),
        ],
    )
