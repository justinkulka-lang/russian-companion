"""Typed Units 16, 17, and 19 checks. Every Russian span is an exact frozen substring or cell.

Units 18 and 20 have no lesson body here. This module does not conjugate, decline,
or compose new Russian.
"""

from pathlib import Path

from units_11_15_lessons import _field, _shared_response_kind, _target, _wiki
from units_3_5_intake import format_wikibooks_emphasis

ROOT = Path(__file__).resolve().parents[1]
_PAGE_FILE = {
    "instrumental": ROOT / "catalog/sources/units-16-20/wikibooks/instrumental-4219602.txt",
    "adjectives": ROOT / "catalog/sources/units-6-10/wikibooks/adjectives-4292379.txt",
    "numbers": ROOT / "catalog/sources/units-6-10/wikibooks/numbers-4371358.txt",
    "aspect": ROOT / "catalog/sources/units-11-15/wikibooks/verbal-aspect-4059032.txt",
}


def _span(page: str, start: str, end: str) -> str:
    text = _PAGE_FILE[page].read_text(encoding="utf-8")
    begin = text.find(start)
    finish = text.find(end, begin if begin >= 0 else 0)
    if begin < 0 or finish < 0:
        raise SystemExit(f"Frozen span is missing on {page}: {start!r} .. {end!r}")
    return text[begin:finish + len(end)]


def populate(lesson, number: int) -> None:
    {
        16: _unit_16,
        17: _unit_17,
        19: _unit_19,
    }[number](lesson)


def _unit_16(lesson) -> None:
    singular = _span("instrumental", "Train - '''поезд'''", "'''мужем'''")
    agreement = _span(
        "adjectives",
        "To form masculine and neuter adjectives",
        "'''Я живу́ ме́жду двумя но́выми дома́ми'''",
    )
    preps = _span("instrumental", "There are six in total:", "'''За''' generally means 'behind' or 'beyond'")
    table = "nouns.csv:стол"
    house = "nouns.csv:дом"
    book = "nouns.csv:кни'га"
    good = "adjectives.csv:хоро'ший"
    wrote = "Я написал хорошим новым карандашом"
    meeting = "Я хочу встречу с интересной русской женщиной"
    between = "Я живу́ ме́жду двумя но́выми дома́ми"
    lesson.objective(
        "inst-sg",
        "Choose the admitted instrumental singular row",
        "FORM_SELECTION",
        ("instrumental", "Nouns ending in '''о''' or a consonant have '''-ом''' in the instrumental.", "Nouns ending in '''о''' or a consonant have '''-ом''' in the instrumental."),
        [
            _field(table, "sg_nom"),
            _field(table, "sg_inst"),
            _field(table, "sg_dat"),
            _field(table, "sg_prep"),
            _field(house, "sg_nom"),
            _field(house, "sg_inst"),
            _field(house, "sg_dat"),
            _field(house, "sg_prep"),
            _wiki("instrumental", singular, "поездом", "Train"),
            _wiki("instrumental", singular, "карандашом", "Pencil"),
            _wiki("instrumental", "Book - '''книга''' --> '''книгой'''", "книгой", "Book"),
        ],
        [
            _target("Which admitted row is the instrumental singular?", "сто́л", "столо́м", ["столу́", "столе́"], feature="INSTRUMENTAL_SINGULAR", phrase="instrumental singular", source="src-openrussian-50e210c4"),
            _target("Which admitted row is the instrumental singular?", "до́м", "до́мом", ["до́му", "до́ме"], feature="INSTRUMENTAL_SINGULAR", phrase="instrumental singular", source="src-openrussian-50e210c4"),
            _target("Which admitted line is the pencil instrumental?", "утром", "карандашом", ["поездом", "мужем"], feature="PENCIL_INSTRUMENTAL", phrase="pencil instrumental"),
        ],
        extra=[
            ("instrumental", singular, "утром", "example"),
            ("instrumental", singular, "мужем", "example"),
        ],
    )
    lesson.objective(
        "inst-pl",
        "Choose the admitted instrumental plural row",
        "FORM_SELECTION",
        ("instrumental", "There are two general endings for the instrumental plural: '''ами''' and '''ями'''.", "There are two general endings for the instrumental plural: '''ами''' and '''ями'''."),
        [
            _field(book, "sg_nom"),
            _field(book, "pl_inst"),
            _field(book, "pl_dat"),
            _field(book, "pl_prep"),
            _field(house, "sg_nom"),
            _field(house, "pl_inst"),
            _field(house, "pl_dat"),
            _field(house, "sg_inst"),
            _field(table, "sg_nom"),
            _field(table, "pl_inst"),
            _field(table, "pl_dat"),
            _field(table, "sg_inst"),
        ],
        [
            _target("Which admitted row is the instrumental plural?", "кни́га", "кни́гами", ["кни́гам", "кни́гах"], feature="INSTRUMENTAL_PLURAL", phrase="instrumental plural", source="src-openrussian-50e210c4"),
            _target("Which admitted row is the instrumental plural?", "до́м", "дома́ми", ["дома́м", "до́мом"], feature="INSTRUMENTAL_PLURAL", phrase="instrumental plural", source="src-openrussian-50e210c4"),
            _target("Which admitted row is the instrumental plural?", "сто́л", "стола́ми", ["стола́м", "столо́м"], feature="INSTRUMENTAL_PLURAL", phrase="instrumental plural", source="src-openrussian-50e210c4"),
        ],
    )
    lesson.objective(
        "inst-adj",
        "Choose the admitted instrumental adjective row",
        "FORM_SELECTION",
        ("adjectives", "To form masculine and neuter adjectives, the ending is '''-ым'''.", "To form masculine and neuter adjectives, the ending is '''-ым'''."),
        [
            _field(good, "decl_m_nom"),
            _field(good, "decl_m_inst"),
            _field(good, "decl_m_dat"),
            _field(good, "decl_pl_nom"),
            _field(good, "decl_pl_inst"),
            _field(good, "decl_f_nom"),
        ],
        [
            _target("Which admitted row is the masculine instrumental?", "хоро́ший", "хоро́шим", ["хоро́шему", "хоро́шими"], feature="MASCULINE_INSTRUMENTAL", phrase="masculine instrumental", source="src-openrussian-50e210c4"),
            _target("Which admitted row is the plural instrumental?", "хоро́шие", "хоро́шими", ["хоро́шим", "хоро́шая"], feature="PLURAL_INSTRUMENTAL", phrase="plural instrumental", source="src-openrussian-50e210c4"),
            _target("Which admitted row is the masculine instrumental?", "хоро́шему", "хоро́шим", ["хоро́шая", "хоро́шие"], feature="MASCULINE_INSTRUMENTAL", phrase="masculine instrumental", source="src-openrussian-50e210c4"),
        ],
    )
    lesson.objective(
        "inst-means",
        "Choose the admitted means or accompaniment line",
        "CONTRAST_SELECTION",
        ("adjectives", agreement, "I wrote with a good new pencil"),
        [
            _wiki("adjectives", agreement, wrote, "I wrote with a good new pencil"),
            _wiki("adjectives", agreement, meeting, "I want a meeting with an interesting Russian woman"),
            _wiki("adjectives", agreement, between, "I live between two new houses"),
        ],
        [
            _target("Which admitted line is the accompaniment line?", wrote, meeting, [between], feature="ACCOMPANIMENT_LINE", phrase="accompaniment line"),
            _target("Which admitted line is the means instrumental?", meeting, wrote, [between], feature="MEANS_INSTRUMENTAL", phrase="means instrumental"),
            _target("Which admitted line is the between line?", wrote, between, [meeting], feature="BETWEEN_LINE", phrase="between line"),
        ],
    )
    lesson.objective(
        "inst-prep",
        "Choose the admitted English label for the shown preposition",
        "CONTRAST_SELECTION",
        ("instrumental", preps, "There are six in total:"),
        [
            _wiki("instrumental", preps, "над", "over"),
            _wiki("instrumental", preps, "под", "under"),
            _wiki("instrumental", preps, "перед", "before"),
            _wiki("instrumental", preps, "между", "between"),
        ],
        [
            _target("Which admitted English label is the over label?", "над", "over", ["under", "before"], feature="OVER_LABEL", phrase="over label"),
            _target("Which admitted English label is the under label?", "под", "under", ["over", "behind"], feature="UNDER_LABEL", phrase="under label"),
            _target("Which admitted English label is the before label?", "перед", "before", ["over", "between"], feature="BEFORE_LABEL", phrase="before label"),
        ],
        extra=[
            ("instrumental", preps, "behind", "meaning"),
        ],
    )


def _unit_17(lesson) -> None:
    ordinals = _span("numbers", "==Ordinal numbers==", "# четв'''ё'''ртый")
    hours = _span("numbers", "* один час", "* двадцать четыре часа")
    years = _span("numbers", "* один год", "* пять лет")
    minutes = _span("numbers", "* одна минута", "* шесть минут")
    year_gloss = _span("numbers", "сорок один год (forty-one years)", "forty-one years")
    raw_first = "п'''е'''рвый"
    raw_second = "втор'''о'''й"
    raw_third = "тр'''е'''тий"
    raw_fourth = "четв'''ё'''ртый"
    first = format_wikibooks_emphasis(raw_first)
    second = format_wikibooks_emphasis(raw_second)
    third = format_wikibooks_emphasis(raw_third)
    fourth = format_wikibooks_emphasis(raw_fourth)
    lesson.objective(
        "ordinals",
        "Choose the admitted ordinal line",
        "FORM_SELECTION",
        ("numbers", ordinals, "Ordinal numbers"),
        [
            _wiki("numbers", ordinals, raw_first, "Ordinal numbers"),
            _wiki("numbers", ordinals, raw_second, "Ordinal numbers"),
            _wiki("numbers", ordinals, raw_third, "Ordinal numbers"),
            _wiki("numbers", ordinals, "восемнадцатую девушку", "She just photographed the eighteenth girl"),
        ],
        [
            _target("Which admitted line is the first ordinal?", second, first, [third, fourth], feature="FIRST_ORDINAL", phrase="first ordinal"),
            _target("Which admitted line is the second ordinal?", first, second, [third, fourth], feature="SECOND_ORDINAL", phrase="second ordinal"),
            _target("Which admitted line is the third ordinal?", first, third, [second, fourth], feature="THIRD_ORDINAL", phrase="third ordinal"),
        ],
        extra=[
            ("numbers", ordinals, raw_fourth, "example"),
        ],
    )
    lesson.objective(
        "hours",
        "Choose the admitted hour row",
        "FORM_SELECTION",
        ("numbers", hours, "* один час"),
        [
            _wiki("numbers", hours, "один час", "один час"),
            _wiki("numbers", hours, "два часа", "два часа"),
            _wiki("numbers", hours, "пять часов", "пять часов"),
        ],
        [
            _target("Which admitted line is the one-hour row?", "два часа", "один час", ["пять часов"], feature="ONE_HOUR_ROW", phrase="one-hour row"),
            _target("Which admitted line is the two-hour row?", "один час", "два часа", ["пять часов"], feature="TWO_HOUR_ROW", phrase="two-hour row"),
            _target("Which admitted line is the five-hour row?", "один час", "пять часов", ["два часа"], feature="FIVE_HOUR_ROW", phrase="five-hour row"),
        ],
    )
    lesson.objective(
        "years",
        "Choose the admitted year row",
        "FORM_SELECTION",
        ("numbers", years, "* один год"),
        [
            _wiki("numbers", years, "один год", "один год"),
            _wiki("numbers", years, "два года", "два года"),
            _wiki("numbers", years, "пять лет", "пять лет"),
            _wiki("numbers", year_gloss, "сорок один год", "forty-one years"),
        ],
        [
            _target("Which admitted line is the one-year row?", "два года", "один год", ["пять лет"], feature="ONE_YEAR_ROW", phrase="one-year row"),
            _target("Which admitted line is the two-year row?", "один год", "два года", ["пять лет"], feature="TWO_YEAR_ROW", phrase="two-year row"),
            _target("Which admitted line is the five-year row?", "один год", "пять лет", ["два года"], feature="FIVE_YEAR_ROW", phrase="five-year row"),
        ],
    )
    lesson.objective(
        "minutes",
        "Choose the admitted minute row",
        "FORM_SELECTION",
        ("numbers", minutes, "* одна минута"),
        [
            _wiki("numbers", minutes, "одна минута", "одна минута"),
            _wiki("numbers", minutes, "две минуты", "две минуты"),
            _wiki("numbers", minutes, "пять минут", "пять минут"),
        ],
        [
            _target("Which admitted line is the one-minute row?", "две минуты", "одна минута", ["пять минут"], feature="ONE_MINUTE_ROW", phrase="one-minute row"),
            _target("Which admitted line is the two-minute row?", "одна минута", "две минуты", ["пять минут"], feature="TWO_MINUTE_ROW", phrase="two-minute row"),
            _target("Which admitted line is the five-minute row?", "одна минута", "пять минут", ["две минуты"], feature="FIVE_MINUTE_ROW", phrase="five-minute row"),
        ],
    )


def _unit_19(lesson) -> None:
    prefix = _span("aspect", "Many verbs come in pairs like", "some prefixes can create whole new verbs.")
    lesson.objective(
        "prefix-caution",
        "Choose the admitted prefix caution",
        "CONTRAST_SELECTION",
        ("aspect", prefix, "some prefixes can create whole new verbs."),
        [
            _wiki("aspect", prefix, "отговорить", "to dissuade"),
            _wiki("aspect", prefix, "договориться", "to agree"),
            _wiki("aspect", prefix, "написать", "to write"),
            _wiki("aspect", prefix, "сделать", "to do"),
        ],
        [
            _target("Which admitted English label is the dissuade meaning?", "отговорить", "to dissuade", ["to agree", "to write"], feature="DISSUADE_MEANING", phrase="dissuade meaning"),
            _target("Which admitted English label is the agree meaning?", "договориться", "to agree", ["to dissuade", "to do"], feature="AGREE_MEANING", phrase="agree meaning"),
            _target("Which admitted line is the dissuade perfective?", "сказать", "отговорить", ["договориться", "сделать"], feature="DISSUADE_PERFECTIVE", phrase="dissuade perfective"),
        ],
        extra=[
            ("aspect", prefix, "сказать", "example"),
        ],
    )
