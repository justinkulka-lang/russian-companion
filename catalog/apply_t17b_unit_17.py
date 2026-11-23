#!/usr/bin/env python3
"""T17b (Claude Code, owner direction 2026-11-23): Unit 17 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T17b D1 pack (246cc437; D1 changes the validator only). Plan: Codex prep
audit/prep/units-16-17-prep-at-20bfde5c.txt (owner/prep-reports), Unit 17 section; owner decision D1
(audit/prep/owner-decisions-2026-11-16.txt); the S1 claim verdicts (audit/prep/s1-claim-verification.csv, Unit 17 rows 1920, 1947
and 1948: SUPPORTED by ReadyRussian Grammar 24 — Capitalization, frozen by S1; only its CC BY-SA handout text is used); the Units
16-30 learner-text sweep (audit/T17a-learner-text-sweep-units-16-30.csv, Unit 17 rows, applied first). Russian comes only from
frozen files already in the source inventory: Wikibooks Numbers 4371358 (R), Days 3594124 (D), Months 4376719 (M) and
Interrogative 4220316 (Q); Wikipedia Russian grammar 1369617627 (G), Date notation 1357134172 (F) and Date and time by country
1366690592 (C); the Pressbooks chapter (P); ReadyRussian Grammar 20 (T) and Grammar 24 (G24, S1; becomes a pack source here);
OpenRussian nouns.csv (N) and adjectives.csv (AD).
- A2-24: every check moves to an end-of-unit session.
- A2-23: eleven typed checks with keys taught first (prep 2): 36 checked, 11 typed.
- Facts: 197 English-only entries -> MARK, REWORD or REMOVE (prep 3; 1920, 1947, 1948 SUPPORTED by S1 G24:60, 62, 64, 65).
- A2-26: governance and narration leave Unit 17 learner text; the mapping note follows the Units 1-16 style.
- A2-36 references, binder and tutor tasks.
New English wording is covered by content-audit coverage records; coverage records never satisfy a marker.
"""
from __future__ import annotations

import copy
import csv
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from apply_t14b_units_1_2_owner import typed_item  # noqa: E402
import apply_d7b_selection_records as d7b  # noqa: E402
import apply_t16n2 as sweep  # noqa: E402
from apply_t17a_unit_16 import sentences, forms, stressed, sha, edit, MARKUP, ACUTE, CYR  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "2af9fe53490586212b05293893fd6249a8f6ce3d4ed40f92276f232a67426ce3"  # the T17b D1 pack (246cc437)
SWEEP_CSV = ROOT / "audit/T17a-learner-text-sweep-units-16-30.csv"
SWEEP_CSV_SHA256 = "0ebf273853d01641275fce68bc396d4374f3fb082cd3d501210a6e84badec96f"
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T17b", "2026-11-23", 17
FREEZE = json.loads((SRC / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
S1 = {p["key"]: p for p in FREEZE["pages"] + FREEZE["supplemental"]}
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "R": ("units-6-10/wikibooks/numbers-4371358.txt", "prov-ex-u17-ordinals-0-en", "numbers:"),
    "G": ("units-26-30/wikipedia/russian-grammar-1369617627.txt", "prov-ex-u17-age-0-ru", "grammar:"),
    "D": ("units-16-20/wikibooks/days-3594124.txt", "prov-ex-u17-weekdays-0-ru", "days:"),
    "M": ("units-16-20/wikibooks/months-4376719.txt", "prov-ex-u17-months-0-ru", "months:"),
    "Q": ("units-3-5/wikibooks/interrogative-4220316.txt", "prov-ex-u17-clock-0-ru", "interrogative:"),
    "F": ("units-16-20/wikipedia/date-notation-russia-1357134172.txt", "prov-ex-u17-calendar-date-0-ru", "date-russia:"),
    "C": ("units-16-20/wikipedia/date-time-country-1366690592.txt", "prov-ex-u17-clock-answers-0-ru", "clock-country:"),
    "P": ("units-16-20/pressbooks/russia-traditions-grammar.html", "prov-ex-u17-quarter-hour-0-ru", "pressbooks-traditions-grammar:"),
    "T": ("readyrussian/grammar20.html", "prov-ex-u17-clock-productive-0-ru", "grammar20:"),
    "G24": ("readyrussian/grammar24.html", "S1:grammar24", "grammar24:"),
}
# HTML pages: the record keeps the whole frozen line and ships one tag-free stretch of its visible text.
HTML = {"P": "pressbooks markup selection", "T": "readyrussian markup selection", "G24": "readyrussian markup selection"}
TABLES = {"N": SRC / "units-3-5/openrussian/nouns.csv", "AD": SRC / "units-6-10/openrussian/adjectives.csv"}
TABLE_NAME = {"N": "nouns.csv", "AD": "adjectives.csv"}
ROWS = {k: list(csv.DictReader(p.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE)) for k, p in TABLES.items()}
CELL_TEMPLATE = {"N": "prov-ex-or-u16-inst-sg-0", "AD": "prov-ex-or-u16-inst-adj-0"}
HTML_CHUNK = re.compile("<[^>]*>|&[#a-zA-Z0-9]+;")

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 17 covers ordinal numbers, counting hours, years and minutes, age statements, weekday and month names, clock-time"
         " questions and expressions, and calendar dates, alongside Lesson 17 in your book.")
MAPPING = ("This unit goes with Lesson 17 in your book, printed pages 201-214: time, weekdays, calendar dates, years, age expressions"
           " and ordinal numbers.")
NUMBER_REVIEW = "Compare the number forms above before retrying."  # prep 3, line 1810
EXAMPLE_REVIEW = "Review the complete example above."  # prep 3, lines 1814, 1899
WEEKDAY_PRACTICE = "Practise the seven isolated names here."  # prep 3, line 1923
MONTH_PRACTICE = "Practise the names shown above."  # prep 3, line 1940
CLOCK_MATCH = "Match the complete expression to a clock reading."  # prep 3, line 2012
DATE_PRACTICE = "Practise these standalone dates here."  # prep 3, line 2105
OLD_COUNT_BODY = ("Read the requested position or quantity and compare the forms explained above, then choose from the displayed"
                  " alternatives. A missed check returns to all explanations and examples.")
COUNT_BODY = ("Read the position or quantity asked for and compare the forms explained above, then choose from the displayed"
              " alternatives. A missed check returns to all explanations and examples.")
TEACH = {  # step: [(old, new)]; new "" deletes the sentence and its leading space
    "u17-st-teach-ordinals-1": [("An ordinal identifies position in an ordered sequence; a cardinal tells how many.", NUMBER_REVIEW)],
    "u17-st-teach-ordinals-2": [("This fragment comes from a sentence about photographing her; it alone does not mean She just"
                                 " photographed.", EXAMPLE_REVIEW)],
    "u17-st-teach-hours-3": [("Read the requested quantity rather than copying the cue.", "Read the quantity asked for rather than copying the cue.")],
    "u17-st-teach-age-2": [("The natural English meaning is I am 20 years old.", EXAMPLE_REVIEW),
                           (" Ownership of summers and a group of people asking a question would describe different events.", "")],
    "u17-st-teach-weekdays-3": [("An isolated weekday name differs from saying on a particular weekday, which needs its own preposition"
                                 " and case construction.", WEEKDAY_PRACTICE)],
    "u17-st-teach-months-1": [("These vocabulary labels name months, not complete dates.", MONTH_PRACTICE)],
    "u17-st-teach-clock-2": [(" This is the guided ownership/selection example.", "")],
    "u17-st-teach-clock-answers-2": [("Match the construction to the clock reading, rather than treating the named eighth hour as the"
                                      " elapsed whole hour.", CLOCK_MATCH)],
    "u17-st-teach-calendar-date-2": [("A sentence saying when something happens can require a different ordinal case; practise that"
                                      " change separately.", DATE_PRACTICE)],
}
# The sweep row for u17-i-clock-productive-2 replaced its cue, a frozen Grammar 20 span (prov-u17-clock-productive-x-2), with new
# English; V2Validator requires the cue's contrast provenance, so the frozen cue is restored (a departure from the sweep row).
RESTORE_CUE = ("u17-i-clock-productive-2", "Reading the dial numbers is always possible.", "This option is always available.")
# Sweep wording "Choose <answer>." repeats the marked meaning sentence inside a new sentence; it is an answer-selection script and
# is removed (prep 3 removes the supplied-interpretation commentary of the same three items, lines 1963, 1987, 1996).
QUESTION_ITEMS = [("u17-g-question-apple", "Asks which apple belongs to the speaker."),
                  ("u17-i-question-clock", "Asks for the current clock time."),
                  ("u17-i-question-book", "Asks which book the listener wants.")]
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
ORD = "Type the masculine form of the Russian ordinal for {}."
PHRASE = "Type the Russian phrase for {}."
TYPED = [  # (id, objective, cue, key, prompt); ("R", line, text) = a Numbers line selection, (table, row, field) = a cell
    ("u17-i-type-pervyi", "obj-u17-ordinals", ("AD", "тре'тий", "decl_m_nom"), ("AD", "пе'рвый", "decl_m_nom"), ORD.format("first")),
    ("u17-i-type-vtoroi", "obj-u17-ordinals", ("AD", "тре'тий", "decl_m_nom"), ("AD", "второ'й", "decl_m_nom"), ORD.format("second")),
    ("u17-i-type-odin-chas", "obj-u17-hours", ("R", 110, "два часа"), ("R", 109, "один час"), PHRASE.format("one hour")),
    ("u17-i-type-dva-chasa", "obj-u17-hours", ("R", 109, "один час"), ("R", 110, "два часа"), PHRASE.format("two hours")),
    ("u17-i-type-pyat-chasov", "obj-u17-hours", ("R", 109, "один час"), ("R", 111, "пять часов"), PHRASE.format("five hours")),
    ("u17-i-type-odin-god", "obj-u17-years", ("R", 118, "два года"), ("R", 117, "один год"), PHRASE.format("one year")),
    ("u17-i-type-dva-goda", "obj-u17-years", ("R", 117, "один год"), ("R", 118, "два года"), PHRASE.format("two years")),
    ("u17-i-type-pyat-let", "obj-u17-years", ("R", 117, "один год"), ("R", 121, "пять лет"), PHRASE.format("five years")),
    ("u17-i-type-odna-minuta", "obj-u17-minutes", ("R", 127, "две минуты"), ("R", 126, "одна минута"), PHRASE.format("one minute")),
    ("u17-i-type-dve-minuty", "obj-u17-minutes", ("R", 126, "одна минута"), ("R", 127, "две минуты"), PHRASE.format("two minutes")),
    ("u17-i-type-pyat-minut", "obj-u17-minutes", ("R", 126, "одна минута"), ("R", 130, "пять минут"), PHRASE.format("five minutes")),
]
REFERENCES = [
    {"id": "ref-u17-ordinals-counts", "title": "Ordinals and counted nouns",
     "summary": "Ordinal forms, and the noun forms after one, two and five.",
     "detail": ("Ordinals: пе́рвый — first, второ́й — second, тре́тий — third, четвёртый — fourth. Hours: один час — one hour, два"
                " часа — two hours, пять часов — five hours. Years: один год — one year, два года — two years, пять лет — five years."
                " Minutes: одна минута — one minute, две минуты — two minutes, пять минут — five minutes. After one the noun is"
                " nominative singular, after two genitive singular and after five genitive plural."),
     "searchTerms": ["ordinal numbers", "counting nouns", "hours years minutes"],
     "steps": ["u17-st-teach-ordinals-1", "u17-st-teach-hours-1", "u17-st-teach-years-1", "u17-st-teach-minutes-1"]},
    {"id": "ref-u17-time-dates", "title": "Time, days and dates",
     "summary": "The clock question, quarter and half hours, weekday and month names, and written dates.",
     "detail": ("Который час? — What time is it? четверть восьмого — 7:15, полвосьмого — 7:30, без четверти восемь — 7:45."
                " Weekdays: понедельник — Monday, вторник — Tuesday, среда — Wednesday, четверг — Thursday, пятница — Friday,"
                " суббота — Saturday, воскресенье — Sunday. Months: Январь — January, Июнь — June, Декабрь — December. Dates are"
                " written day, month, year: 28 августа 2017 г. is 28 August 2017, and г. marks the year."),
     "searchTerms": ["telling time", "days of the week", "months", "dates"],
     "steps": ["u17-st-teach-clock-1", "u17-st-teach-clock-answers-1", "u17-st-teach-weekdays-1", "u17-st-teach-months-1",
               "u17-st-teach-calendar-date-1"]},
]
BINDER = {"id": "binder-u17-counts", "objectiveIds": ["obj-u17-ordinals", "obj-u17-hours", "obj-u17-years", "obj-u17-minutes"],
          "prompt": ("In your binder, write пе́рвый and второ́й with their meanings, and the phrases for one, two and five hours, years"
                     " and minutes. Compare your work with the model answer."),
          "modelAnswer": ("пе́рвый — first, второ́й — second.\nодин час — one hour, два часа — two hours, пять часов — five hours.\n"
                          "один год — one year, два года — two years, пять лет — five years.\n"
                          "одна минута — one minute, две минуты — two minutes, пять минут — five minutes.")}
TUTOR = {"id": "tutor-u17-time",
         "bring": "Your binder count phrases and this unit's clock expressions, to read aloud and say what quantity or time each one gives."}
ATTRIBUTION = {
    "ref-u17-attribution": {
        "summary": "Unit 17 adapts CC BY-SA 4.0 dictionary data, Wikibooks excerpts, Wikipedia clock and date spans, and ReadyRussian handouts.",
        "detail": ("OpenRussian dictionary data is pinned at commit 50e210c4803237779cb562bc1abcea529066031c under CC BY-SA 4.0."
                   " Wikibooks excerpts are pinned by oldid under the CC BY-SA 4.0 route recorded on Wikibooks:Copyrights oldid 4622060."
                   " The clock phrases come from Wikipedia oldid 1366690592. The calendar-date examples come from Wikipedia oldid"
                   " 1357134172. Those license routes are not interchangeable. The three clock phrases are bounded recognition."
                   " Quarter-hour lines use a separate Pressbooks notice. Productive clock-time is the ReadyRussian Grammar 20 system"
                   " (Grammar 20 — Telling Time, ReadyRussian.org, CC BY-SA 4.0) applied to exact expressions for minutes into the"
                   " hour, minutes remaining after the half hour, and dial reading. The capitalization notes for"
                   " weekday and month names come from ReadyRussian Grammar 24 — Capitalization (ReadyRussian.org, CC BY-SA 4.0)."
                   " The clock image, data URI and linked audio are not used.")},
    "ref-u17-pressbooks-attribution": {
        "summary": "The quarter-hour lines adapt one Pressbooks chapter.",
        "detail": ("Anna Tumarkin and Shannon Donnally Quinn, Diverse Russian: A Multicultural Exploration, CC BY-SA 4.0, except where"
                   " otherwise noted. The chapter HTML sha256 is ab2170e37d529a318f395f6e657d8024d0eaed8d7c5c9b34f8831d348b8b76b3,"
                   " retrieved 2026-09-25T15:45:47Z. That Pressbooks license route is not a Wikipedia, Wiktionary, Wikibooks, or"
                   " OpenRussian route. The lines used are the 0.00, 0.15, 0.30 and 0.45 patterns only.")},
}

# --------------------------------------------------------------------------------------------------------------
# Facts. (page, n) = a page line record, (table, row, field) = an OpenRussian cell.
# --------------------------------------------------------------------------------------------------------------
NONFACTS = {
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    NUMBER_REVIEW, EXAMPLE_REVIEW, WEEKDAY_PRACTICE, MONTH_PRACTICE, CLOCK_MATCH, DATE_PRACTICE,
    # Reviewed instructions (sweep wording and operating directions); they state no Russian fact.
    "Compare the complete clock, book and apple questions and their meanings.",
    "Compare the masculine hour and year phrases.",
    "Distinguish Sunday from Monday and Saturday.",
    "Distinguish Thursday from Friday and Saturday.",
    "Distinguish elapsed minutes in the current hour, minutes remaining and reading the dial numbers.",
    "For extra practice, repeat the clock question and its meaning.",
    "Identify the specified position rather than copying a different position from the cue.",
    "Identify the specified quantity rather than copying a different quantity from the cue.",
    "Interpret each time expression in context to distinguish morning from evening.",
    "Interpret these time expressions in their sentence contexts.",
    "Learn hour counts, then practise telling clock time separately.",
    "Learn minute counts, then practise expressing minutes past an hour separately.",
    "Learn quantities of years; practise calendar-year dates and complete age sentences separately.",
    "Learn the Sunday form before answering independently.",
    "Learn the complete question as a unit.",
    "Learn the construction types, then practise producing new clock expressions separately.",
    "Learn the ordinal forms, then practise agreement in sentences separately.",
    "Learn the position expressed by each form before choosing it.",
    "Learn these event-time examples, then practise building new event-time sentences separately.",
    "Learn these meanings, then practise asking for and giving new times and forming other questions separately.",
    "Learn this age construction, then practise ages for other people and numbers separately.",
    "Learn this question’s meaning, then practise forming new questions separately.",
    "Learn this selection of month names; practise using all twelve month names separately.",
    "Learn those constructions separately.",
    "Match the shown word to its English meaning rather than selecting a neighboring day.",
    "Match this expression to the request for minutes into the hour in progress.",
    "Read each complete example before choosing its meaning.",
    "Read the English request even if the separately shown cue has a different count.",
    "Read the English request: the separately shown Russian cue may name a different position and is not the target to copy.",
    "Read the complete supplied question and its noun.",
    "Read the quantity asked for rather than copying the cue.",
    "Read the relevant date components and distinguish the whole date from an individual component.",
    "Read the shown Russian word and select its matching English month.",
    "The separately shown cue can have a different count.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


DEFAULT = {  # prep 3 MARK evidence by objective
    "obj-u17-ordinals": p("G", 713, 720, 723, 726) + p("R", 139),
    "obj-u17-hours": p("R", 86, 90, 92, 96, 98, 109, 110, 111),
    "obj-u17-years": p("R", 86, 90, 92, 94, 96, 98, 117, 118, 121),
    "obj-u17-minutes": p("R", 86, 88, 90, 92, 96, 98, 126, 127, 130),
    "obj-u17-age": p("G", 35, 38),
    "obj-u17-weekdays": p("D", 23, 29, 35, 41, 47, 53, 59),
    "obj-u17-months": p("M", 19, 23, 27, 31, 39, 43, 47, 63) + p("F", 12),
    "obj-u17-clock": p("Q", 168, 169, 170),
    "obj-u17-clock-vocabulary": p("Q", 168, 169, 170),
    "obj-u17-clock-answers": p("C", 41) + p("T", 105, 106, 107, 110) + p("P", 229, 240, 251),
    "obj-u17-quarter-hour": p("P", 223, 224, 225, 229, 230, 231, 240, 241, 242, 251, 252, 253) + p("T", 180, 182),
    "obj-u17-clock-productive": p("T", 104, 105, 106, 110, 118, 119, 127, 151, 180, 182) + p("P", 229),
    "obj-u17-calendar-date": p("F", 10, 12),
}
EXTRA: dict[str, list] = {
    # S1 SUPPORTED (ReadyRussian Grammar 24 — Capitalization, 933d0deb): 1920, 1947, 1948
    "Weekday names normally stay lowercase within a Russian sentence; the first word of a sentence still begins with a capital.":
        p("G24", 60, 62, 65) + p("D", 7),
    "Month names normally use lowercase within a Russian sentence.": p("G24", 62, 64),
    "These vocabulary examples begin with capitals; that presentation is not a rule to capitalize every month name.":
        p("G24", 62, 64) + p("M", 19),
}


class Builder:
    def __init__(self, pack: dict):
        self.pack = pack
        self.records = {r["itemId"]: r for r in pack["provenanceRecords"]}
        self.unit = next(u for u in pack["units"] if u["number"] == UNIT)

    def add(self, record: dict) -> str:
        if record["itemId"] in self.records:
            raise SystemExit(f"Duplicate record {record['itemId']}")
        if "http" in record["originalSourceText"] or ".pdf" in record["originalSourceText"]:
            raise SystemExit(f"{record['itemId']} would carry a URL")
        self.records[record["itemId"]] = record
        self.pack["provenanceRecords"].append(record)
        if not d7b.frozen_record(self.pack, record):
            raise SystemExit(f"{record['itemId']} is not a frozen-source record")
        return record["itemId"]

    def template(self, page: str) -> dict:
        tmpl = PAGES[page][1]
        if not tmpl.startswith("S1:"):
            return copy.deepcopy(self.records[tmpl])
        s1 = S1[tmpl[3:]]
        record = copy.deepcopy(self.records["prov-ex-u17-clock-productive-0-ru"])
        record.update({"sourceId": s1["sourceId"], "sourceRevision": s1["oldid"], "attribution": s1["attribution"],
                       "sourceUrl": s1["url"]})
        return record

    def raw(self, page: str, number: int) -> str:
        return (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()

    def line(self, page: str, number: int, objective: str) -> str:
        record_id = f"prov-t17b-u17-{page.lower()}{number}-{objective[8:]}"
        if record_id in self.records:
            return record_id
        raw = self.raw(page, number)
        if (SRC / PAGES[page][0]).read_text(encoding="utf-8").count(raw) != 1:
            raise SystemExit(f"{page}: line is not unique on its page ({raw[:60]!r})")
        record = self.template(page)
        if page in HTML:
            # The visible text between tags; no entity, so the shipped stretch is in both the line and its visible text.
            shipped = max((c.strip() for c in HTML_CHUNK.split(raw) if re.search("[A-Za-zЀ-ӿ]", c)), key=len)
            transformation = HTML[page]
        else:
            # Units 16-30 provenance ships no MediaWiki markup: the longest markup-free stretch of the line.
            shipped = max((c.strip(" *:#-") for c in MARKUP.split(raw) if re.search("[A-Za-zЀ-ӿ]", c)), key=len)
            transformation = "selection"
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": PAGES[page][2] + raw[:80],
                       "originalSourceText": raw, "shippedText": shipped, "sourceHash": sha(raw), "contentHash": sha(shipped),
                       "recordType": "instruction", "transformation": transformation})
        record.pop("originalSourceTextRef", None)
        return self.add(record)

    def select(self, page: str, number: int, text: str, objective: str, record_type: str = "example") -> str:
        raw = self.raw(page, number)
        if text not in d7b.displayed(raw):
            raise SystemExit(f"{text!r} is not displayed on {page}:{number}")
        record_id = f"prov-t17b-u17-{page.lower()}{number}-{sha(text + objective)[:8]}"
        if record_id in self.records:
            return record_id
        record = self.template(page)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": PAGES[page][2] + raw[:80],
                       "originalSourceText": raw, "shippedText": text, "sourceHash": sha(raw), "contentHash": sha(text),
                       "recordType": record_type, "transformation": "selection"})
        record.pop("originalSourceTextRef", None)
        return self.add(record)

    def cell(self, table: str, accented: str, field: str, objective: str) -> str:
        hits = [r for r in ROWS[table] if r["accented"] == accented]
        if len(hits) != 1:
            raise SystemExit(f"{table}: {len(hits)} rows for {accented}")
        original = hits[0][field].strip()
        if not original:
            raise SystemExit(f"{table}:{accented}#{field} is empty")
        source_id = f"{TABLE_NAME[table]}:{accented}#{field}"
        for r in self.records.values():
            if (r["unitNumber"] == UNIT and r["objectiveId"] == objective and r["sourceRecordId"] == source_id
                    and d7b.frozen_record(self.pack, r)):
                return r["itemId"]
        record = copy.deepcopy(self.records[CELL_TEMPLATE[table]])
        shipped = stressed(original)
        record.update({"itemId": f"prov-t17b-u17-{table.lower()}-{sha(accented + field + objective)[:8]}-{field}", "unitNumber": UNIT,
                       "objectiveId": objective, "sourceRecordId": source_id, "originalSourceText": original,
                       "shippedText": shipped, "sourceHash": sha(original), "contentHash": sha(shipped),
                       "recordType": "lexical record", "transformation": "formatting"})
        return self.add(record)


def admit_s1(pack: dict) -> None:
    """S1 froze ReadyRussian Grammar 24 — Capitalization; only its CC BY-SA handout text is used (no image, scripts or downloads)."""
    page = S1["grammar24"]
    data = (SRC / "s1-intake" / page["file"]).read_bytes()
    if hashlib.sha256(data).hexdigest() != page["sha256"]:
        raise SystemExit("grammar24 does not match its FREEZE.json entry")
    if b"CC BY-SA" not in data:
        raise SystemExit("grammar24 carries no CC BY-SA notice")
    if any(s["id"] == page["sourceId"] for s in pack["sources"]):
        raise SystemExit("grammar24 is already a pack source")
    template = next(s for s in pack["sources"] if s["id"] == "src-readyrussian-grammar20")
    pack["sources"].append({**copy.deepcopy(template), "id": page["sourceId"], "title": "Grammar 24 — Capitalization",
                            "attribution": page["attribution"]})
    rights = next(r for r in pack["rights"] if r["sourceId"] == "src-readyrussian-grammar20")
    pack["rights"].append({**copy.deepcopy(rights), "id": "rights-" + page["sourceId"][4:], "sourceId": page["sourceId"]})


def apply(pack: dict) -> tuple[dict, list]:
    baseline = copy.deepcopy(pack)  # before the sweep, so sweep wording is covered too
    sweep.SWEEP, sweep.SWEEP_SHA256 = SWEEP_CSV, SWEEP_CSV_SHA256
    sweep_log: list = []
    sweep.apply_sweep(pack, [], sweep_log, units={UNIT})
    admit_s1(pack)
    b = Builder(pack)
    unit = b.unit
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R = b.records
    # --- the two Unit 17 attribution notices (linked by Unit 17 only): governance wording out ------------------------
    for ref_id, fields in ATTRIBUTION.items():
        reference = next(x for x in pack["references"] if x["id"] == ref_id)
        reference.update(fields)
    # --- the restored cue ------------------------------------------------------------------------------------------
    item_id, swept, frozen_cue = RESTORE_CUE
    item = items[item_id]
    if item["promptLetter"] != swept or frozen_cue not in {r["shippedText"] for r in R.values() if r["unitNumber"] == UNIT}:
        raise SystemExit(f"{item_id}: cue is not the sweep wording")
    item["promptLetter"] = frozen_cue
    item["feedback"] = edit(item["feedback"], swept, frozen_cue, item_id)
    why = item.get("assessment", {})
    if swept in (why.get("whyAnswerDemonstratesObjective") or ""):
        why["whyAnswerDemonstratesObjective"] = why["whyAnswerDemonstratesObjective"].replace(swept, frozen_cue)
    # --- the three question items: the answer-selection script leaves the feedback (it overlaps the marked meaning) ---
    for item_id, answer in QUESTION_ITEMS:
        item = items[item_id]
        script = f" Choose {answer}"
        item["feedback"] = edit(item["feedback"], script, "", item_id)
        a = item.get("assessment") or {}
        if script in (a.get("whyAnswerDemonstratesObjective") or ""):
            a["whyAnswerDemonstratesObjective"] = edit(a["whyAnswerDemonstratesObjective"], script, "", item_id)
    # --- teaching ---------------------------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], old, new, step_id)
    for step in steps.values():
        if step["kind"] in ("GUIDED", "CHECK") and step["body"] == OLD_COUNT_BODY:
            step["body"] = COUNT_BODY
    # --- remediation repeats each session's whole teaching ---------------------------------------------------------
    for se in unit["sessions"]:
        for s in se["steps"]:
            if s["kind"] == "TEACH":
                s["body"] = s["body"].strip()
        rem = [s for s in se["steps"] if s["kind"] == "REMEDIATE"]
        if rem:
            rem[0]["body"] = "\n\n".join(s["body"] for s in se["steps"] if s["kind"] == "TEACH")
            rem[0]["exampleIds"] = list(dict.fromkeys(x for s in se["steps"] if s["kind"] == "TEACH" for x in s.get("exampleIds") or []))
    # --- typed checks ---------------------------------------------------------------------------------------------
    typed = []

    def ref_id(ref, objective):
        return b.select("R", ref[1], ref[2], objective, "lexical record") if ref[0] == "R" else b.cell(*ref, objective)
    for item_id, objective, cue, key, prompt in TYPED:
        cue_id, key_id = ref_id(cue, objective), ref_id(key, objective)
        if key[0] == "R":
            source = "src-wikibooks-russian-cc-by-sa"
            if R[key_id]["shippedText"] != key[2] or ACUTE in key[2]:
                raise SystemExit(f"{item_id}: key is not the exact frozen (unstressed) line")
        else:
            source = "src-openrussian-50e210c4"
            raw_key = next(r for r in ROWS[key[0]] if r["accented"] == key[1])[key[2]]
            if stressed(raw_key) != R[key_id]["shippedText"]:
                raise SystemExit(f"{item_id}: key is not the exact frozen stress")
        typed.append(typed_item(item_id, objective, prompt, R[cue_id]["shippedText"], R[key_id]["shippedText"], TYPED_FEEDBACK, source))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    items.update({i["id"]: i for i in typed})
    for item in typed:  # typed keys are taught answers
        taught = " ".join(steps[s]["body"] for s in objectives[item["objectiveId"]]["instructionStepIds"])
        if not re.search(f"(?<![{CYR}{ACUTE}]){re.escape(item['answer'])}(?![{CYR}{ACUTE}])", taught):
            raise SystemExit(f"{item['id']}: key {item['answer']} is not taught")
    # --- unit fields; checks move to an end-of-unit session ---------------------------------------------------------
    unit["topicSummary"], unit["mappingNote"] = TOPIC, MAPPING
    check_steps = []
    for session in unit["sessions"]:
        kept = []
        for step in session["steps"]:
            (check_steps if step["kind"] == "CHECK" else kept).append(step)
        session["steps"] = kept
    for step in check_steps:
        owner = next(o for o in objectives.values() if set(o["checkItemIds"]) & set(step["itemIds"]))
        step["itemIds"] = list(owner["checkItemIds"])
        step["title"] = "Check: " + owner["title"]
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u17-s-book")
    unit["sessions"].insert(book, {"id": "u17-s-checks", "title": "Unit 17 checks", "estimatedMinutes": 35,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    # --- references, binder and tutor -------------------------------------------------------------------------------
    template = next(r for r in pack["references"] if r["id"] == "ref-u10-possession")
    for spec in REFERENCES:
        reference = {k: template[k] for k in ("sourceId", "reviewStatus", "classification")}
        reference.update({k: spec[k] for k in ("id", "title", "summary", "detail", "searchTerms")})
        reference["introducedInUnit"] = UNIT
        pack["references"].append({k: reference[k] for k in template if k in reference})
        for sid in spec["steps"]:
            steps[sid]["referenceIds"] = list(dict.fromkeys((steps[sid].get("referenceIds") or []) + [spec["id"]]))
    if unit.get("binderTask") or unit.get("tutorTask"):
        raise SystemExit("Unit 17 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 17 learner field: Russian only through frozen Unit 17 records ------------------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and d7b.frozen_record(pack, r)}
    texts = [s["body"] for s in steps.values()] + [unit["topicSummary"], unit["mappingNote"], BINDER["prompt"], BINDER["modelAnswer"]]
    texts += [r[k] for r in pack["references"] if r["id"] in {s["id"] for s in REFERENCES} for k in ("title", "summary", "detail")]
    texts += [e["display"] for e in pack["examples"] if e.get("objectiveId", "").startswith(f"obj-u{UNIT}-")]
    for item in items.values():
        if item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            texts += [item["prompt"], item.get("promptLetter") or "", item["answer"], item["feedback"]] + item["options"] + item.get("distractorRationales", [])
    unshown = set()
    for text in texts:
        for m in re.finditer("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*", text):
            region = m.group(0).strip()
            if region not in frozen and region.rstrip("?!.,") not in frozen and not any(region in f for f in frozen):
                unshown.add(region)
    if unshown:
        raise SystemExit(f"Unit 17 regions without a frozen record: {sorted(unshown)}")
    report = mark_facts(b, steps, items)
    cover_english(pack, baseline)
    return report, sweep_log


def mark_facts(b: Builder, steps: dict, items: dict) -> dict:
    pack, R, unit = b.pack, b.records, b.unit

    def owner_objective(step_id: str) -> str:
        for o in unit["objectives"]:
            if step_id in o["instructionStepIds"] or step_id in o["remediationStepIds"] or step_id in o["practiceStepIds"]:
                return o["id"]
        return next(se for se in unit["sessions"] if any(s["id"] == step_id for s in se["steps"]))["objectiveIds"][0]

    def cite_forms(sentence: str, objective: str, participants: list[str]) -> list[str]:
        out, pool = [], [x for x in R.values() if x["unitNumber"] == UNIT and d7b.frozen_record(pack, x)]
        for form in sorted(forms(sentence)):
            bare = form.replace(ACUTE, "")

            def ok(x):
                f = forms(x["originalSourceText"])
                return form in f if ACUTE in form else bare in {y.replace(ACUTE, "") for y in f}
            ranked = ([x for x in pool if x["itemId"] in participants and ok(x)] +
                      [x for x in pool if x["objectiveId"] == objective and x["shippedText"].lower() == form and ok(x)] +
                      [x for x in pool if x["objectiveId"] == objective and ok(x)] +
                      [x for x in pool if x["shippedText"].lower() == form and ok(x)] + [x for x in pool if ok(x)])
            if not ranked:
                raise SystemExit(f"No frozen Unit 17 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    report, missing = {}, []
    # The ordinal forms with exact stress in an original text: the grammar numeral table (G:720, 723, 726, 729).
    for number in (720, 723, 726, 729):
        b.line("G", number, "obj-u17-ordinals")

    def mark(host: dict, texts: list[str], objective: str, owner: str, participants: list[str]) -> None:
        facts, sources = [], {}
        for text in texts:
            for sentence in sentences(text):
                if not any(ch.isalpha() for ch in sentence) or sentence in NONFACTS:
                    continue
                refs = EXTRA.get(sentence)
                if refs is None and not forms(sentence):
                    if sentence not in FACTS:
                        missing.append((owner, objective, sentence))
                        continue
                    refs = DEFAULT[objective]
                ids = [b.line(r[0], r[1], objective) for r in refs or []] + cite_forms(sentence, objective, participants)
                ids = list(dict.fromkeys(ids))
                if sentence not in sources:
                    facts.append(sentence)
                    sources[sentence] = ids
        if facts:
            host["russianFacts"], host["russianFactSources"] = facts, sources
        else:
            host.pop("russianFacts", None)
            host.pop("russianFactSources", None)
        report[owner] = sources

    for step in steps.values():
        if step["kind"] == "REMEDIATE":
            mark(step, [step["body"]], owner_objective(step["id"]), step["id"], [])
    for item in items.values():
        if item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            participants = (item.get("assessment") or {}).get("relationParticipantIds", [])
            mark(item, [item["feedback"]] + [x for x in item.get("distractorRationales", []) if x], item["objectiveId"], item["id"], participants)
    if missing:
        Path("/tmp/claude-0/-home-user-russian-companion/8476c374-e1ab-5f3d-9af8-fc234c0d6e90/scratchpad/t17b/missing.json").write_text(
            json.dumps(missing, ensure_ascii=False, indent=1), encoding="utf-8")
        raise SystemExit(f"{len(missing)} sentences without a disposition (written to the scratchpad missing.json)")
    return report


# English-only fact sentences (reviewed): each is marked with its objective's prep evidence (DEFAULT).
# Every other English-only sentence of Unit 17 feedback and remediation is a fact (the 108 inventory sentences the prep marks,
# their sweep wording, and sentence fragments of decimal clock readings, which V2Validator splits at the period).
FACTS: set[str] = {
    "00 is a different meaning or construction and does not match the reading of the shown form.",
    "15 – 15 minutes of the tenth hour.",
    "15 – quarter of the eighth hour is a different meaning or construction and does not match the reading of the shown form.",
    "15 – quarter of the eighth hour.",
    "17 is a different meaning or construction and does not match the reading of the shown form.",
    "20 people asked me invents people performing an action; the shown construction gives an age.",
    "30 – half of the fifth hour is a different meaning or construction and does not match the reading of the shown form.",
    "30 – half of the fifth hour.",
    "30 – half of the second hour.",
    "45 – 3 without a quarter.",
    "45 – 9 without 15 minutes.",
    "7:15 is a different meaning or construction and does not match the reading of the shown form.",
    "7:30 is a different meaning or construction and does not match the reading of the shown form.",
    "7:45 is a different meaning or construction and does not match the reading of the shown form.",
    "A current-time question and the time when an event happens are different requests.",
    "A month in a full date can have a different case form from its standalone name.",
    "A preposition introduces several of them, while the without construction expresses minutes remaining until the upcoming hour.",
    "A quarter of that hour has elapsed after seven.",
    "A quarter or fifteen minutes has elapsed in the hour leading to the named next hour.",
    "April names a different month and does not match the shown word.",
    "Asks for the current clock time.",
    "Asks which apple belongs to the speaker.",
    "Asks which book the listener wants.",
    "August names a different month and does not match the shown word.",
    "Both name a half elapsed in the next ordinal hour.",
    "December names the final month of the year.",
    "Different case constructions require additional numeral and noun changes.",
    "Do not infer that every occurrence of six means eighteen.",
    "Each other phrase names a different quantity.",
    "Each word names a day of the week.",
    "Endings 11 through 14 are exceptions to the final-digit rule and take the five pattern.",
    "Endings 11 through 14 take the five-minute pattern instead of following the final digit alone.",
    "English uses hours for both two and five, but Russian uses different forms.",
    "February names a different month and does not match the shown word.",
    "For basic counts in nominative or inanimate accusative constructions, use the one pattern for numbers ending in 1, the two pattern for endings 2, 3 or 4, and the five pattern for endings 0, 5, 6, 7, 8 or 9.",
    "For basic nominative or inanimate accusative counts, an ending of 1 follows the one-minute pattern, endings 2, 3 or 4 follow the two-minute pattern, and endings 0 or 5 through 9 follow the five-minute pattern.",
    "Friday names a different day of the week and does not match the shown word.",
    "Half of the eighth hour has passed, so the clock shows half past seven.",
    "I own 20 summers invents ownership; the shown construction gives an age.",
    "In Russian we can state the number of minutes into the hour which is in progress.",
    "In basic nominative or inanimate accusative counts, endings 2, 3 or 4 follow the two pattern; endings 0 and 5 through 9 follow the five pattern.",
    "Interpret the whole event-time expression in context; a bare hour alone does not specify AM or PM.",
    "Interpret the whole expression as elapsed or remaining parts of the next hour, rather than copying its hour numeral.",
    "January names a different month and does not match the shown word.",
    "July names a different month and does not match the shown word.",
    "June and July have similar Russian spellings but differ in the consonant before the final soft sign.",
    "March names a different month and does not match the shown word.",
    "Monday names a different day of the week and does not match the shown word.",
    "Numbers ending in 11, 12, 13 or 14 take the five pattern instead.",
    "Numeric day-month-year order and the year abbreviation are separate from speaking a complete year in words.",
    "Ordinals agree with their noun in gender, number and case, like adjectives.",
    "Other case constructions require additional changes to the numeral and noun; this is not a rule for every sentence position.",
    "Other grammatical cases need additional changes.",
    "Reading the dial is always available; context or a 24-hour reading can distinguish AM and PM.",
    "Saturday names a different day of the week and does not match the shown word.",
    "Subtract the remaining quantity from the upcoming hour.",
    "Subtract the stated remaining minutes from the upcoming hour.",
    "The bare six-hour and one-hour expressions do not themselves distinguish evening from morning; use the sentence context.",
    "The basic counting forms are nominative singular after one, genitive singular after two, and genitive plural after five.",
    "The compact half-hour expression is written as one word in this example.",
    "The complete question asks for the current clock time.",
    "The compound ends in 1, so its noun follows the one pattern.",
    "The dative person and the number of years together state the age.",
    "The eighth hour is the period leading up to eight, not the period starting at eight.",
    "The event sentences use at-time expressions.",
    "The full age statement is I am 20 (years old); the shorter quantity phrase is 20 years and does not by itself identify whose age is meant.",
    "The listener is asked to choose a book.",
    "The month form in a date differs from its dictionary form.",
    "The next-hour ordinal is genitive.",
    "The noun changes with the count: nominative singular after one, genitive singular after two, and genitive plural after five.",
    "The noun for minute is feminine, so one and two use feminine forms here.",
    "The noun is nominative singular after one, genitive singular after two, and genitive plural after five.",
    "The ordinal ending follows the noun phrase, rather than staying in the masculine dictionary form.",
    "The ordinal is genitive; the event-time phrase also has its introductory preposition.",
    "The ordinal names the next hour and appears in genitive form.",
    "The person whose age is given is in the dative case; the present-time Russian construction has no expressed verb.",
    "The person whose age is stated is in the dative case: the first word means to me in a literal description.",
    "The preposition meaning without governs the remaining quantity in genitive form.",
    "The present-time Russian construction has no expressed verb, whereas natural English uses am.",
    "The remaining phrase counts twenty years.",
    "The second hour runs from one up to two, so fifteen minutes into it is quarter past one.",
    "The section label age statements names the topic; it is not a translation of either phrase.",
    "The without construction differs from counting a quarter or a half already elapsed.",
    "The without construction uses genitive forms for the remaining minutes and differs from counting elapsed minutes.",
    "Their preposition and noun forms differ; recognize each complete construction rather than exchanging endings mechanically.",
    "These isolated ordinal forms are masculine nominative singular.",
    "This construction counts a quarter hour remaining until eight: subtract fifteen minutes from the named upcoming hour.",
    "This construction states elapsed whole hours and then minutes, instead of naming an ordinal hour in progress.",
    "This convention is used up to the half hour.",
    "This fixed question asks for the current clock time.",
    "This is an age statement, not a statement of ownership.",
    "This is the first ordinal.",
    "This is the five-hour expression.",
    "This is the five-minute expression.",
    "This is the five-year expression.",
    "This is the one-hour expression.",
    "This is the one-minute expression.",
    "This is the one-year expression.",
    "This is the second ordinal.",
    "This is the third ordinal.",
    "This is the two-hour expression.",
    "This is the two-minute expression.",
    "This is the two-year expression.",
    "This is used after the half hour.",
    "This names a day of the week; choose the matching English name.",
    "This names a month; choose the matching English name.",
    "This option is always available.",
    "This quantity phrase does not by itself identify whose age is meant.",
    "This requests the current clock time, not which book the listener wants or which apple belongs to the speaker.",
    "This standalone date naming uses a neuter ordinal for the day and a genitive month name.",
    "To translate the whole date, include the day, month and year; identifying only the month or abbreviation leaves part of the meaning out.",
    "Together with the previous four names, these make the seven-day set.",
    "Tuesday names a different day of the week and does not match the shown word.",
    "Twenty-five minutes remain before two.",
    "Wednesday names a different day of the week and does not match the shown word.",
    "Which book do you want?",
    "age statements is a section label rather than the meaning of this phrase.",
    "corresponds to 15 May 1996.",
    "corresponds to 18.",
    "corresponds to 2.",
    "corresponds to 4.",
    "corresponds to 7.",
    "corresponds to What time is it?.",
    "corresponds to day month year.",
    "corresponds to year, the abbreviation.",
    "gives day month year: 28 August 2017.",
    "is a different meaning or construction and does not match the reading of the shown form.",
    "marks the year after its number.",
    "means 15 May 1996.",
    "means What time is it?",
    "means Which apple is mine?",
    "means Which book do you want?",
    "the day number is a different meaning or construction and does not match the reading of the shown form.",
    "the month alone is a different meaning or construction and does not match the reading of the shown form.",
    "the month in genitive case is a different meaning or construction and does not match the reading of the shown form.",
    "the month name is a different meaning or construction and does not match the reading of the shown form.",
    "the year abbreviation alone is a different meaning or construction and does not match the reading of the shown form.",
    "— What time is it?",
    "— Which apple is mine?",
    "— Which book do you want?",
}


def english_hosts(pack: dict) -> dict:
    out = {}
    unit = next(u for u in pack["units"] if u["number"] == UNIT)
    first = unit["objectives"][0]["id"]
    objective_of = {}
    for o in unit["objectives"]:
        for sid in o.get("instructionStepIds", []) + o.get("remediationStepIds", []) + o.get("practiceStepIds", []):
            objective_of.setdefault(sid, o["id"])
    for s in unit["sessions"]:
        for st in s["steps"]:
            if st["kind"] in ("TEACH", "REMEDIATE", "ORIENT", "GUIDED", "CHECK"):
                out[(st["id"], "body")] = (UNIT, objective_of.get(st["id"], first), st["body"], list(st.get("russianFacts", [])))
    for i in pack["items"]:
        if not i["objectiveId"].startswith(f"obj-u{UNIT}-"):
            continue
        n, o, facts = UNIT, i["objectiveId"], list(i.get("russianFacts", []))
        out[(i["id"], "feedback")] = (n, o, i["feedback"], facts)
        out[(i["id"], "prompt")] = (n, o, i["prompt"], [])
        out[(i["id"], "promptLetter")] = (n, o, i.get("promptLetter") or "", [])
        for k, v in enumerate(i.get("distractorRationales", [])):
            out[(i["id"], f"rationale{k}")] = (n, o, v, facts)
        a = i.get("assessment") or {}
        out[(i["id"], "why")] = (n, o, a.get("whyAnswerDemonstratesObjective") or "", [])
        for k, v in enumerate(a.get("distractorDifference") or []):
            out[(i["id"], f"difference{k}")] = (n, o, v, [])
    for e in pack["examples"]:
        if e.get("objectiveId", "").startswith(f"obj-u{UNIT}-"):
            out[(e["id"], "note")] = (UNIT, e["objectiveId"], e.get("note") or "", [])
    for r in pack["references"]:
        if r["id"] in {s["id"] for s in REFERENCES} | set(ATTRIBUTION):
            for k in ("title", "summary", "detail"):
                out[(r["id"], k)] = (UNIT, first, r[k], [])
    if unit.get("binderTask"):
        for k in ("prompt", "modelAnswer"):
            out[(unit["binderTask"]["id"], k)] = (UNIT, unit["binderTask"]["objectiveIds"][0], unit["binderTask"][k], [])
    return out


def cover_english(pack: dict, before: dict) -> None:
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shipped: dict[int, set] = {}
    for r in pack["provenanceRecords"]:
        shipped.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    template = records["prov-t15an1-u6-possessive-our-your"]
    count = 0

    def coverage(unit: int, objective: str, key: str, text: str) -> None:
        nonlocal count
        if not text or text in shipped.get(unit, set()):
            return
        record = copy.deepcopy(template)
        note = (f"New English wording authored on {DATE} ({TASK}); it did not exist before that date. It covers the wording"
                " only; the Russian facts it states are cited from frozen source records in the marker's russianFactSources."
                " 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        item_id = f"prov-ca-t17b-{key}"
        if item_id in records:
            raise SystemExit(f"Duplicate record {item_id}")
        record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                       "shippedText": text, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      f" this record's English text is new, authored {DATE} for {TASK}."})
        pack["provenanceRecords"].append(record)
        records[item_id] = record
        shipped.setdefault(unit, set()).add(text)
        count += 1

    old_hosts = english_hosts(before)
    hosts = english_hosts(pack)
    for (owner, field), (unit, objective, text, facts) in sorted(hosts.items()):
        old = old_hosts.get((owner, field))
        if field == "promptLetter" and (old is None or old[2] == text):
            continue
        if old is None or old[2] != text:
            coverage(unit, objective, f"{owner}-{field}", text)
    for (owner, field), (unit, objective, text, facts) in sorted(hosts.items()):
        for n, fact in enumerate(facts):
            coverage(unit, objective, f"{owner}-{field}-fact{n}", fact)
    print(f"english coverage records: {count}")


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T17b baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report, sweep_log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    out = json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n"
    if "--dry" in sys.argv:
        dry = [a for a in sys.argv[1:] if a != "--dry"]
        if dry:
            Path(dry[0]).write_text(out, encoding="utf-8")
        print(f"dry run; {len(sweep_log)} sweep rows; asset not written")
        return
    ASSET.write_text(out, encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps({"facts": report, "sweep": sweep_log}, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(sweep_log)} Unit 17 sweep rows applied; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
