#!/usr/bin/env python3
"""T17c (Claude Code, owner direction 2026-11-23): Unit 18 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T17b DONE pack (e88562bd). Plan: Codex prep audit/prep/unit-18-prep-at-20bfde5c.txt
(owner/prep-reports); the Units 16-30 learner-text sweep (audit/T17a-learner-text-sweep-units-16-30.csv, Unit 18 rows, applied
first). Russian comes only from frozen files already in the source inventory: Wikipedia Russian grammar 1369617627 (G),
Wikibooks Interrogative 4220316 (Q), ru Wikipedia Морфология 148939075 (MOR), OpenRussian nouns.csv (N) and adjectives.csv (AD).
- A2-24: every check moves to an end-of-unit session; the answer-selection scripts leave the comparison teaching.
- A2-23: sixteen typed checks with keys taught first (prep 2): 20 checked, 16 typed. Teaching adds the positive forms the
  comparison cues show and bounded production practice; two guided typed items practise production before the checks.
- Facts: 45 English-only entries -> 44 MARK, 1 REMOVE (prep 3); "simple comparative" replaces "short comparative" and the
  superlative gloss is not limited to size (prep 4).
- A2-26: governance and narration leave Unit 18 learner text; the mapping note follows the Units 1-17 style.
- A2-36 references, binder and tutor tasks. ref-u1620-attribution (Units 16, 18, 19, 20) is not edited.
New English wording is covered by content-audit coverage records; coverage records never satisfy a marker.
"""
from __future__ import annotations

import copy
import csv
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
BASELINE_SHA256 = "5cb1b53fc59c28cc2a52f5f584abd55f5c9cc7c3aa9b1e3f8519dd4e96efed19"  # the T17b DONE pack (e88562bd)
SWEEP_CSV = ROOT / "audit/T17a-learner-text-sweep-units-16-30.csv"
SWEEP_CSV_SHA256 = "0ebf273853d01641275fce68bc396d4374f3fb082cd3d501210a6e84badec96f"
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T17c", "2026-11-23", 18
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "G": ("units-26-30/wikipedia/russian-grammar-1369617627.txt", "prov-ex-u18-comparison-0-ru", "grammar:"),
    "Q": ("units-3-5/wikibooks/interrogative-4220316.txt", "prov-ex-u18-relative-0-ru", "interrogative:"),
    "MOR": ("units-21-25/supplemental/morfologiya-148939075.txt", "prov-ex-u22-declension-0-ru", "morfologiya:"),
}
SOURCE = {"G": "src-wikipedia-russian-grammar-1369617627", "Q": "src-wikibooks-russian-cc-by-sa"}
TABLES = {"N": SRC / "units-3-5/openrussian/nouns.csv", "AD": SRC / "units-6-10/openrussian/adjectives.csv"}
TABLE_NAME = {"N": "nouns.csv", "AD": "adjectives.csv"}
ROWS = {k: list(csv.DictReader(p.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE)) for k, p in TABLES.items()}
CELL_TEMPLATE = {"N": "prov-ex-or-u16-inst-sg-0", "AD": "prov-ex-or-u16-inst-adj-0"}

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 18 covers comparative and superlative adjective forms, two-word comparisons, and the word for which in questions"
         " and relative clauses, alongside Lesson 18 in your book.")
MAPPING = ("This unit goes with Lesson 18 in your book, printed pages 215-229: comparative forms, superlative expression, and"
           " relative clauses built with the inflected relative pronoun.")
COMPARISON_PRACTICE = "Practise writing each form above from its English meaning."
RELATIVE_PRACTICE = "Practise writing each example from its English meaning, then write only its form of the word for which."
QUESTION_PRACTICE = "Practise writing each question from its English meaning."
TEACH = {  # step: [(old, new)]; new "" deletes the sentence and its leading space
    "u18-st-teach-comparison-1": [
        ("бо́льший — bigger. лу́чший — better.", "бо́льший — bigger (from большо́й — big). лу́чший — better (from хоро́ший — good)."),
        ("unchanging short comparative.", "unchanging simple comparative."),  # prep 4: terminology
    ],
    "u18-st-teach-comparison-2": [
        ("красне́е — redder. бо́лее до́брый — kinder.", "красне́е — redder (from кра́сный — red). бо́лее до́брый — kinder (from до́брый — kind)."),
        ("The first is a short comparative with", "The first is a simple comparative with"),
        ("this short form does not change", "this simple comparative does not change"),
        ("In this two-word construction the adjective still changes for agreement.",
         "In this two-word construction the adjective still changes for agreement. This two-word way is rarely used when a"
         " one-word comparative exists."),  # prep 4: G:303
    ],
    "u18-st-teach-comparison-3": [
        ("наибо́льший — the biggest.", "наибо́льший — the biggest (from большо́й — big)."),
        ("This superlative identifies the greatest size in the relevant group, rather than just a greater degree.",
         "This superlative identifies the highest degree in the relevant group, rather than just a greater degree; besides the"
         " biggest it can mean the greatest or the largest."),  # prep 4: AD:1017
        ("Contrast the forms бо́лее — more, ме́нее — less, and наиме́нее — least.",
         "Contrast the forms бо́лее — more, ме́нее — less, са́мый and наибо́лее — most, and наиме́нее — least."),
        ("More and less express comparison; least identifies the lowest degree.",
         "More and less express comparison; most identifies the highest degree and least the lowest. For example, са́мый до́брый"
         " — the kindest."),
        (" For наибо́льший select the biggest, not more or less.", ""),  # prep 1: answer-selection script
        (" For бо́лее до́брый select kinder, not least or bigger.", ""),
        ("Learn these comparison forms,", COMPARISON_PRACTICE + " Learn these comparison forms,"),
    ],
    "u18-st-teach-relative-1": [
        ("Match the whole question: ownership of an apple differs from wanting a book.",
         "Compare Который час? — What time is it?, where the selecting form agrees with a masculine singular subject. Match the"
         " whole question: ownership of an apple differs from wanting a book. " + QUESTION_PRACTICE),
    ],
    "u18-st-teach-relative-3": [  # prep 3, line 2183: REMOVE (the sweep instruction is replaced by the practice instruction)
        ("Learn these meanings, then practise choosing relative case forms and producing new clauses separately.", RELATIVE_PRACTICE),
    ],
}
OLD_PRACTICE_BODY = ("Read the shown form and select its English meaning using the distinctions above. A missed check returns to the"
                     " complete explanation and examples.")
PRACTICE_BODY = ("Select the English meaning of a shown form, or type the Russian asked for, using the explanations above. A missed"
                 " check returns to the complete explanation and examples.")
FEEDBACK = {"u18-i-comparison-1": [("This superlative identifies the greatest size in the relevant group.",
                                    "This superlative identifies the highest degree in the relevant group.")]}
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
FULL = "Type the full adjective that means {}."
ADVERB = "Type the word that means {} in a two-word comparison."
QUESTION = "Type the Russian question for: {}"
PHRASE = "Type the Russian phrase for: {}"
GAP_Q = "Type the missing word of the question for: {}"
GAP_P = "Type the missing word of the phrase for: {}"
C, R = "obj-u18-comparison", "obj-u18-relative"
TYPED = [  # (id, objective, (page, line, cue), (page, line, key), prompt); a cue of the form "___ ..." is the example without its key
    ("u18-i-type-bolshii", C, ("G", 301, "большо́й"), ("G", 301, "бо́льший"), FULL.format("bigger")),
    ("u18-i-type-luchshii", C, ("G", 301, "хоро́ший"), ("G", 301, "лу́чший"), FULL.format("better")),
    ("u18-i-type-krasnee", C, ("G", 301, "кра́сный"), ("G", 301, "красне́е"), "Type the one-word comparative that means redder."),
    ("u18-i-type-naibolshii", C, ("G", 301, "бо́льший"), ("G", 301, "наибо́льший"), FULL.format("the biggest")),
    ("u18-i-type-bolee", C, ("G", 303, "ме́нее"), ("G", 303, "бо́лее"), ADVERB.format("more")),
    ("u18-i-type-menee", C, ("G", 303, "бо́лее"), ("G", 303, "ме́нее"), ADVERB.format("less")),
    ("u18-i-type-naimenee", C, ("G", 303, "наибо́лее"), ("G", 303, "наиме́нее"), ADVERB.format("least")),
    ("u18-i-type-bolee-dobryi", C, ("G", 303, "до́брый"), ("G", 303, "бо́лее до́брый"), "Type the two-word comparison that means kinder."),
    ("u18-i-type-apple", R, ("Q", 170, "Которую книгу вы хотите?"), ("Q", 168, "Которое яблоко моё?"), QUESTION.format("Which apple is mine?")),
    ("u18-i-type-book", R, ("Q", 168, "Которое яблоко моё?"), ("Q", 170, "Которую книгу вы хотите?"), QUESTION.format("Which book do you want?")),
    ("u18-i-type-woman", R, ("Q", 173, "Человек, который изменил мир"), ("Q", 172, "Же́нщина, с кото́рой я дружу́"),
     PHRASE.format("The woman (who) I'm friends with")),
    ("u18-i-type-man", R, ("Q", 172, "Же́нщина, с кото́рой я дружу́"), ("Q", 173, "Человек, который изменил мир"),
     PHRASE.format("The man who changed the world")),
    ("u18-i-type-kotoroe", R, ("Q", 168, "___ яблоко моё?"), ("Q", 168, "Которое"), GAP_Q.format("Which apple is mine?")),
    ("u18-i-type-kotoruyu", R, ("Q", 170, "___ книгу вы хотите?"), ("Q", 170, "Которую"), GAP_Q.format("Which book do you want?")),
    ("u18-i-type-kotoroi", R, ("Q", 172, "Же́нщина, с ___ я дружу́"), ("Q", 172, "кото́рой"), GAP_P.format("The woman (who) I'm friends with")),
    ("u18-i-type-kotoryi", R, ("Q", 173, "Человек, ___ изменил мир"), ("Q", 173, "который"), GAP_P.format("The man who changed the world")),
]
GUIDED = [  # practice before the checks; the keys differ from every check key
    ("u18-g-type-samyi-dobryi", C, "u18-st-guided-comparison-1", ("G", 303, "до́брый"), ("G", 303, "са́мый до́брый"),
     "Practice: type the two-word superlative that means the kindest."),
    ("u18-g-type-kotoryi-chas", R, "u18-st-guided-relative-1", ("Q", 169, "___ час?"), ("Q", 169, "Который"),
     "Practice: type the missing word of the question for: What time is it?"),
]
REFERENCES = [
    {"id": "ref-u18-comparison", "title": "Comparison forms",
     "summary": "Comparative and superlative adjectives, and two-word comparisons.",
     "detail": ("большо́й — big, бо́льший — bigger, наибо́льший — the biggest. хоро́ший — good, лу́чший — better. кра́сный — red,"
                " красне́е — redder. до́брый — kind, бо́лее до́брый — kinder, са́мый до́брый — the kindest. бо́лее — more, ме́нее —"
                " less, наибо́лее — most, наиме́нее — least. Full forms such as бо́льший and лу́чший change like ordinary adjectives;"
                " a simple comparative such as красне́е does not change."),
     "searchTerms": ["comparative", "superlative", "more and less"],
     "steps": ["u18-st-teach-comparison-1", "u18-st-teach-comparison-2", "u18-st-teach-comparison-3"]},
    {"id": "ref-u18-relative", "title": "The word for which",
     "summary": "Questions with the word for which, and relative clauses.",
     "detail": ("Которое яблоко моё? — Which apple is mine? Которую книгу вы хотите? — Which book do you want? Который час? —"
                " What time is it? Же́нщина, с кото́рой я дружу́ — The woman (who) I'm friends with. Человек, который изменил мир —"
                " The man who changed the world. The word takes the gender and number of the noun it refers to, and the case of"
                " its role in its own sentence or clause."),
     "searchTerms": ["which", "relative clause", "relative pronoun"],
     "steps": ["u18-st-teach-relative-1", "u18-st-teach-relative-2", "u18-st-teach-relative-3"]},
]
BINDER = {"id": "binder-u18-comparison-relative", "objectiveIds": [C, R],
          "prompt": ("In your binder, write the comparison forms with their meanings, then the two questions and the two relative"
                     " phrases with their meanings. Compare your work with the model answer."),
          "modelAnswer": ("бо́льший — bigger, лу́чший — better, красне́е — redder, наибо́льший — the biggest.\n"
                          "бо́лее — more, ме́нее — less, наиме́нее — least, бо́лее до́брый — kinder.\n"
                          "Которое яблоко моё? — Which apple is mine? Которую книгу вы хотите? — Which book do you want?\n"
                          "Же́нщина, с кото́рой я дружу́ — The woman (who) I'm friends with. Человек, который изменил мир — The man"
                          " who changed the world.")}
TUTOR = {"id": "tutor-u18-comparison",
         "bring": "Your binder comparison forms and relative phrases, to read aloud and say what each one means."}

# --------------------------------------------------------------------------------------------------------------
# Facts. (page, n) = a page line record, (table, accented, field) = an OpenRussian cell.
# --------------------------------------------------------------------------------------------------------------
NONFACTS = {
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    COMPARISON_PRACTICE, RELATIVE_PRACTICE, QUESTION_PRACTICE, PRACTICE_BODY.split(". ")[0] + ".",
    # Reviewed instructions (sweep wording and operating directions); they state no Russian fact.
    "Interpret the gloss in its sentence context.",
    "Compare the woman described by friendship with the man described by changing the world and the two direct questions.",
    "Learn these comparison forms, then practise forming other comparisons and complete comparison sentences separately.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


DEFAULT = {  # prep 3 MARK evidence by objective
    C: p("G", 301, 303),
    R: p("Q", 40, 168, 170, 172, 173),
}
EXTRA: dict[str, list] = {
    "The second gloss can mean best in context; better is not its universal translation.":
        p("G", 301) + [("AD", "лу'чший", "translations_en")],
    "These full adjective forms can change for gender, number and case; they are not the same construction as an unchanging"
    " simple comparative.": p("G", 301) + p("MOR", 488),
    "The first is a simple comparative with an ending expressing a higher degree of redness; this simple comparative does not"
    " change for gender, number or case.": p("G", 301) + p("MOR", 488),
    "In this two-word construction the adjective still changes for agreement.": p("G", 249, 303),
    "This two-word way is rarely used when a one-word comparative exists.": p("G", 303) + [("AD", "до'брый", "comparative")],
    "This superlative identifies the highest degree in the relevant group, rather than just a greater degree; besides the"
    " biggest it can mean the greatest or the largest.": p("G", 301) + [("AD", "наибо'льший", "translations_en")],
    "In the apple question the selecting form agrees with a neuter singular subject in the nominative.":
        p("Q", 40, 168) + p("G", 17, 18) + [("N", "я'блоко", "gender")],
    "In the book question it agrees with a feminine singular direct object in the accusative.":
        p("Q", 40, 170) + p("G", 21, 22) + [("N", "кни'га", "gender")],
    "The relative form takes masculine singular agreement from that noun, and nominative case because it is the subject of"
    " changed inside its own clause.": p("Q", 40, 173) + p("G", 17, 18) + [("N", "челове'к", "gender")],
    "Its relative form has feminine singular agreement and instrumental case after the preposition meaning with.":
        p("Q", 40, 172) + p("G", 42, 49) + [("N", "же'нщина", "gender")],
    # The Который час? sentence (new teaching) splits at its quoted question mark.
    "— What time is it?": p("Q", 169),
    ", where the selecting form agrees with a masculine singular subject.": p("Q", 40, 169) + p("G", 17, 18) + [("N", "час", "gender")],
    "For a relative form, gender and number come from the noun it describes; case comes from the role inside the relative clause,"
    " not automatically from the preceding noun.": p("Q", 40),
}
FACTS: set[str] = {  # the prep 3 MARK sentences (and their sweep wording) without a Cyrillic form; DEFAULT evidence
    "Match the whole question: ownership of an apple differs from wanting a book.",
    "More and less express comparison; most identifies the highest degree and least the lowest.",
    "Not every adjective has a one-word comparative, and these examples do not give a complete formation rule for unfamiliar adjectives.",
    "Russian keeps this preposition before the relative form, while the English translation places with at the end.",
    "The clause following the comma describes the preceding person; it does not ask which person.",
    "The first compares size.",
    "The full adjective compares size.",
    "The man who changed the world describes a different person and action.",
    "The man who changed the world describes a person rather than asking which apple is owned.",
    "The relative clause describes the man; the relative form is the subject of changed in its own clause.",
    "The relative clause describes the woman.",
    "The relative clause describes the woman; its preposition meaning with takes instrumental case.",
    "The second combines a word meaning more with the ordinary long adjective meaning kind.",
    "The woman (who) I'm friends with describes a different person and relationship.",
    "The word meaning more combines with the adjective meaning kind.",
    "These are direct questions asking which member is selected, not relative clauses modifying an earlier noun.",
    "These forms concern degree of a quality, rather than naming an unrelated object.",
    "This direct question selects an apple and asks about ownership.",
    "This superlative identifies the highest degree in the relevant group.",
    "Which apple is mine?",
    "Which book do you want?",
    "asks a direct question about a book rather than describing the man.",
    "asks a direct question about ownership, rather than describing the woman.",
    "asks about a wanted book, not ownership of an apple.",
    "better compares quality rather than size.",
    "bigger compares size rather than kindness.",
    "least identifies the lowest degree rather than a higher degree of kindness.",
    "less indicates a lower degree rather than a maximum.",
    "means Which apple is mine?.",
    "more indicates a greater degree without identifying a maximum.",
    "redder compares redness rather than size.",
    "— Which apple is mine?",
    "— Which book do you want?",
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

    def raw(self, page: str, number: int) -> str:
        return (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()

    def line(self, page: str, number: int, objective: str) -> str:
        record_id = f"prov-t17c-u18-{page.lower()}{number}-{objective[8:]}"
        if record_id in self.records:
            return record_id
        raw = self.raw(page, number)
        if (SRC / PAGES[page][0]).read_text(encoding="utf-8").count(raw) != 1:
            raise SystemExit(f"{page}: line is not unique on its page ({raw[:60]!r})")
        record = copy.deepcopy(self.records[PAGES[page][1]])
        # Units 16-30 provenance ships no MediaWiki markup: the longest markup-free stretch of the line.
        shipped = max((c.strip(" *:#-") for c in MARKUP.split(raw) if re.search("[A-Za-zЀ-ӿ]", c)), key=len)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": PAGES[page][2] + raw[:80],
                       "originalSourceText": raw, "shippedText": shipped, "sourceHash": sha(raw), "contentHash": sha(shipped),
                       "recordType": "instruction", "transformation": "selection"})
        record.pop("originalSourceTextRef", None)
        return self.add(record)

    def select(self, page: str, number: int, text: str, objective: str, record_type: str = "example") -> str:
        raw = self.raw(page, number)
        if text not in d7b.displayed(raw):
            raise SystemExit(f"{text!r} is not displayed on {page}:{number}")
        record_id = f"prov-t17c-u18-{page.lower()}{number}-{sha(text + objective)[:8]}"
        if record_id in self.records:
            return record_id
        record = copy.deepcopy(self.records[PAGES[page][1]])
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
        record.update({"itemId": f"prov-t17c-u18-{table.lower()}-{sha(accented + field + objective)[:8]}-{field}", "unitNumber": UNIT,
                       "objectiveId": objective, "sourceRecordId": source_id, "originalSourceText": original,
                       "shippedText": shipped, "sourceHash": sha(original), "contentHash": sha(shipped),
                       "recordType": "lexical record", "transformation": "formatting"})
        return self.add(record)

    def ref(self, spec: tuple, objective: str) -> str:
        return self.line(spec[0], spec[1], objective) if len(spec) == 2 else self.cell(*spec, objective)


REGION = re.compile("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*")


def gapped(b: Builder, spec: tuple, objective: str) -> str:
    """A cue: a frozen selection, or the frozen example with its key replaced by a blank (the rest stays exact)."""
    page, number, text = spec
    if not text.startswith("___") and "___" not in text:
        return b.records[b.select(page, number, text, objective, "lexical record")]["shippedText"]
    shown = d7b.displayed(b.raw(page, number))
    pattern = re.escape(text).replace("___", f"[{CYR}{ACUTE}]+")
    if not re.search(pattern, shown):
        raise SystemExit(f"{text!r} is not the frozen line {page}:{number} with one word blanked")
    for m in REGION.finditer(text):  # every remaining Russian run (as V2Validator reads it) has its own frozen record
        b.select(page, number, m.group(0).strip().rstrip("?!.,"), objective, "lexical record")
    return text


def apply(pack: dict) -> tuple[dict, list]:
    baseline = copy.deepcopy(pack)  # before the sweep, so sweep wording is covered too
    sweep.SWEEP, sweep.SWEEP_SHA256 = SWEEP_CSV, SWEEP_CSV_SHA256
    sweep_log: list = []
    sweep.apply_sweep(pack, [], sweep_log, units={UNIT})
    b = Builder(pack)
    unit = b.unit
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R_ = b.records
    # Russian the new teaching shows: the positive forms of G:301 and G:303, and the clock question of Q:169.
    for text in ("большо́й", "хоро́ший", "кра́сный"):
        b.select("G", 301, text, C)
    for text in ("до́брый", "наибо́лее", "са́мый", "са́мый до́брый"):
        b.select("G", 303, text, C)
    b.select("Q", 169, "Который час?", R)
    # --- teaching and feedback ---------------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], old, new, step_id)
    for item_id, pairs in FEEDBACK.items():
        item = items[item_id]
        for old, new in pairs:
            item["feedback"] = edit(item["feedback"], old, new, item_id)
            a = item.get("assessment") or {}
            if old in (a.get("whyAnswerDemonstratesObjective") or ""):
                a["whyAnswerDemonstratesObjective"] = a["whyAnswerDemonstratesObjective"].replace(old, new)
    for step in steps.values():
        if step["kind"] in ("GUIDED", "CHECK"):
            step["body"] = edit(step["body"], OLD_PRACTICE_BODY, PRACTICE_BODY, step["id"])
    # --- remediation repeats each session's whole teaching ---------------------------------------------------------
    for se in unit["sessions"]:
        for s in se["steps"]:
            if s["kind"] == "TEACH":
                s["body"] = s["body"].strip()
        rem = [s for s in se["steps"] if s["kind"] == "REMEDIATE"]
        if rem:
            rem[0]["body"] = "\n\n".join(s["body"] for s in se["steps"] if s["kind"] == "TEACH")
            rem[0]["exampleIds"] = list(dict.fromkeys(x for s in se["steps"] if s["kind"] == "TEACH" for x in s.get("exampleIds") or []))
    # --- typed checks and guided typed practice ------------------------------------------------------------------------
    typed, guided = [], []

    def build(item_id, objective, cue, key, prompt):
        cue_text = gapped(b, cue, objective)
        key_id = b.select(key[0], key[1], key[2], objective, "lexical record")
        if R_[key_id]["shippedText"] != key[2]:
            raise SystemExit(f"{item_id}: key is not the exact frozen text")
        return typed_item(item_id, objective, prompt, cue_text, key[2], TYPED_FEEDBACK, SOURCE[key[0]])
    for item_id, objective, cue, key, prompt in TYPED:
        typed.append(build(item_id, objective, cue, key, prompt))
    for item_id, objective, step_id, cue, key, prompt in GUIDED:
        item = build(item_id, objective, cue, key, prompt)
        guided.append(item)
        steps[step_id]["itemIds"].append(item_id)
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(guided + typed)
    items.update({i["id"]: i for i in guided + typed})
    for item in typed + guided:  # typed keys are taught answers
        taught = " ".join(steps[s]["body"] for s in objectives[item["objectiveId"]]["instructionStepIds"])
        if not re.search(f"(?<![{CYR}{ACUTE}]){re.escape(item['answer'])}(?![{CYR}{ACUTE}])", taught):
            raise SystemExit(f"{item['id']}: key {item['answer']} is not taught")
    if len({i["answer"] for i in typed + guided}) != len(typed + guided):
        raise SystemExit("a guided key repeats a check key")
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
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u18-s-book")
    unit["sessions"].insert(book, {"id": "u18-s-checks", "title": "Unit 18 checks", "estimatedMinutes": 20,
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
        raise SystemExit("Unit 18 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 18 learner field: Russian only through frozen Unit 18 records ------------------------------------
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
            if region not in frozen and region.rstrip("?!.,") not in frozen:  # exact, as V2Validator requires
                unshown.add(region)
    if unshown:
        raise SystemExit(f"Unit 18 regions without a frozen record: {sorted(unshown)}")
    report = mark_facts(b, steps, items)
    cover_english(pack, baseline)
    return report, sweep_log


def mark_facts(b: Builder, steps: dict, items: dict) -> dict:
    pack, R_, unit = b.pack, b.records, b.unit

    def owner_objective(step_id: str) -> str:
        for o in unit["objectives"]:
            if step_id in o["instructionStepIds"] or step_id in o["remediationStepIds"] or step_id in o["practiceStepIds"]:
                return o["id"]
        return next(se for se in unit["sessions"] if any(s["id"] == step_id for s in se["steps"]))["objectiveIds"][0]

    def cite_forms(sentence: str, objective: str, participants: list[str]) -> list[str]:
        out, pool = [], [x for x in R_.values() if x["unitNumber"] == UNIT and d7b.frozen_record(pack, x)]
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
                raise SystemExit(f"No frozen Unit 18 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    report, missing = {}, []

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
                ids = [b.ref(r, objective) for r in refs or []] + cite_forms(sentence, objective, participants)
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
        Path("/tmp/claude-0/-home-user-russian-companion/8476c374-e1ab-5f3d-9af8-fc234c0d6e90/scratchpad/t17c/missing.json").write_text(
            json.dumps(missing, ensure_ascii=False, indent=1), encoding="utf-8")
        raise SystemExit(f"{len(missing)} sentences without a disposition (written to the scratchpad missing.json)")
    return report


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
        if r["id"] in {s["id"] for s in REFERENCES}:
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
        item_id = f"prov-ca-t17c-{key}"
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
        raise SystemExit("Asset is not the T17c baseline; this transformer runs only once")
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
    print(f"{len(sweep_log)} Unit 18 sweep rows applied; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
