#!/usr/bin/env python3
"""T18a-2 (Claude Code, owner decisions D54b, D54c, D54e, D54f, D54g): Unit 21 to the Block 4 standard.

Exact-baseline transformer; runs once on the T18a-1 pack (50edd641, ad00f774). Plan: Codex prep audit/prep/unit-21-prep-at-
7d6e66e1.txt (owner/prep-reports), with the T18a-1 correction: source R (Wikipedia "Reflexive pronoun" oldid 1374712982) is
frozen at catalog/sources/s1-intake/wikipedia/reflexive-pronoun-1374712982.txt, so CSV 2466, 2467 and 2476 are MARK bound to
that file (not PENDING-S1). The Units 16-30 learner-text sweep (audit/T17a-learner-text-sweep-units-16-30.csv, Unit 21 rows)
is applied first. Russian comes only from frozen files already in the source inventory: Wikibooks possessive pronouns 4220315
(P), Wikipedia Russian grammar 1369617627 (G), Wikipedia Reflexive pronoun 1374712982 (R) and OpenRussian nouns.csv (N).
- D54e A2-23/24: every required check moves to the end-of-unit session u21-s-checks, after all teaching; fifteen typed checks
  from the prep's fifteen frozen candidates (prep 2), keys exact frozen text, taught first, with bounded production practice
  in the teaching (agreement features, reflexive object versus possessed noun, conjunction recall, the contextual past forms).
  21 checked, 15 typed.
- D54f: the two two-option checks stay two-option; each has its own CC036 row.
- Facts (prep 3): every English-only entry MARK with frozen markers, except REMOVE 2422, 2435, 2469 and (D54b) 2484. D54c: the
  register explanation of u21-st-teach-purpose-2 is removed beyond what G:2479 shows verbatim. The R paragraph's suffix and
  accidentality statements are not imported.
- D54g A2-26: governance and narration leave Unit 21 learner text and ref-u2125-attribution (shared with Units 22-25).
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
from apply_t17a_unit_16 import sentences, forms, stressed, sha, edit, MARKUP, ACUTE, CYR  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "50edd641e8f4a7a79491adeee71e0dff6c020a17551b131266ad7636c34afa96"  # the T18a-1 pack (ad00f774)
SWEEP_CSV = ROOT / "audit/T17a-learner-text-sweep-units-16-30.csv"
SWEEP_CSV_SHA256 = "0ebf273853d01641275fce68bc396d4374f3fb082cd3d501210a6e84badec96f"
LOG = ROOT / "audit/T18a-2-unit21-changes.csv"
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T18a-2", "2026-11-26", 21
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "P": ("units-21-25/wikibooks/possessive-pronouns-4220315.txt", "prov-ex-u21-svoy-0-ru", "possessive:"),
    "G": ("units-26-30/wikipedia/russian-grammar-1369617627.txt", "prov-ex-u21-object-reference-0-ru", "grammar:"),
    "R": ("s1-intake/wikipedia/reflexive-pronoun-1374712982.txt", "prov-u21-object-transfer-native-self", "reflexive:"),
}
SOURCE = {"P": "src-wikibooks-russian-cc-by-sa", "G": "src-wikipedia-russian-grammar-1369617627"}
TABLES = {"N": SRC / "units-3-5/openrussian/nouns.csv"}
TABLE_NAME = {"N": "nouns.csv"}
ROWS = {k: list(csv.DictReader(p.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE)) for k, p in TABLES.items()}
CELL_TEMPLATE = {"N": "prov-ex-or-u16-inst-sg-0"}
S, O, U = "obj-u21-svoy", "obj-u21-object-reference", "obj-u21-purpose"

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 21 covers the reflexive possessive, the reflexive object pronoun, and conjunctions of purpose, condition, cause"
         " and concession, alongside Lesson 21 in your book.")
MAPPING = ("This unit goes with Lesson 21 in your book, printed pages 257-268: possession with the reflexive possessive pronoun,"
           " reflexive reference, and purpose clauses.")
FORMS_PRACTICE = "Practise naming the noun gender or number that goes with each dictionary form above."
SENTENCE_PRACTICE = "Practise writing each sentence above from its English meaning."
OBJECT_PRACTICE = "Practise writing both sentences above from their English meanings, keeping the reflexive object apart from the possessed noun."
CONJUNCTION_PRACTICE = "Practise recalling each conjunction above from its English meaning."
AGREEMENT = "свой goes with a masculine noun, своя with a feminine noun, своё with a neuter noun and свои with a plural noun."
TOLD = "рассказа́л is the past form used with Borya and рассказа́ла the past form used with Anna."
PUT_ON = "наде́л is the past form used with Borya and наде́ла the past form used with Anna."
TEACH = {  # step: [(old, new)]; new "" deletes the sentence and its leading space
    "u21-st-teach-svoy-1": [
        ("The dictionary forms are свой, своя, своё and свои.", "The dictionary forms are свой, своя, своё and свои. " + AGREEMENT),
        ("with the same noun.", "with the same noun. " + FORMS_PRACTICE),
    ],
    "u21-st-teach-svoy-2": [  # prep 3: the parenthesized translations rejoin as whole sentences
        ("— (Borya told about his favorite movie.).", "— Borya told about his favorite movie."),
        ("— (Anna told about her favorite movie.).", "— Anna told about her favorite movie."),
        (" Keep the movie as the object while changing the subject.", ""),  # prep 3, CSV 2422: REMOVE
        ("The name and past-verb agreement change.", "The name and past-verb agreement change. " + TOLD + " " + SENTENCE_PRACTICE),
    ],
    "u21-st-teach-svoy-3": [
        ("— (She never wrote her own book.).", "— She never wrote her own book."),
        ("— (He never wrote his own book.).", "— He never wrote his own book."),
        ("not a movie or jeans.", "not a movie or jeans. " + SENTENCE_PRACTICE),
    ],
    "u21-st-teach-svoy-4": [
        ("— (Borya put on his own jeans.).", "— Borya put on his own jeans."),
        ("— (Anna put on her own jeans.).", "— Anna put on her own jeans."),
        (" Keep the object the same while changing the subject.", ""),  # prep 3, CSV 2435: REMOVE
        ("regardless of whether Borya or Anna owns them.", "regardless of whether Borya or Anna owns them. " + PUT_ON + " " + SENTENCE_PRACTICE),
    ],
    "u21-st-teach-object-reference-1": [
        ("describe different objects.", "describe different objects. " + OBJECT_PRACTICE),
    ],
    "u21-st-teach-object-reference-3": [  # prep 3, CSV 2469: REMOVE (the sweep rewrite of it goes too)
        (" Interpret these direct objects in their sentence contexts; practise other pronoun cases and emphasis separately,"
         " without assuming the reflexive-verb suffix always means accidental action.", ""),
    ],
    "u21-st-teach-purpose-1": [  # D54b, CSV 2484: REMOVE the same-subject purpose infinitive (and its sweep follow-on)
        (" In a purpose construction with the same subject for both actions, an infinitive after the purpose conjunction can"
         " name the intended action. Study the purpose-clause rule, then practise composing purpose clauses separately.",
         " " + CONJUNCTION_PRACTICE),
    ],
    "u21-st-teach-purpose-3": [
        ("These conjunction meanings are distinct from purpose and condition.",
         "These conjunction meanings are distinct from purpose and condition. " + CONJUNCTION_PRACTICE),
    ],
}
# D54c: the register explanation goes; only what G:2479 shows verbatim stays (both glossed so that, the qualifier after дабы).
PURPOSE_2 = "чтобы — so that. дабы (bookish, archaic) — so that. The English meaning so that differs from the conditional if and the causal because."
FEEDBACK = {  # D54c: the guided item's "ordinary" register claim goes with it
    "u21-g-purpose-0": [("чтобы means so that: the ordinary conjunction can introduce a purpose.",
                         "чтобы means so that: the conjunction can introduce a purpose.")],
}
OLD_PRACTICE_BODY = ("Read the shown Russian sentence or conjunction and choose the requested meaning or matching object example"
                     " using the explanation above. A missed check returns to the complete explanation.")
PRACTICE_BODY = ("Choose the meaning or sentence asked for, or type the Russian asked for, using the explanations above. A missed"
                 " check returns to the complete explanation.")
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
POSS = "Type the missing reflexive possessive for: {}"
VERB = "Type the missing past verb for: {}"
WORD = "Type the missing word for: {}"
DICT = "The shown form is the {} dictionary form of the reflexive possessive. Type its {} form."
CONJ = "The shown conjunction means {}. Type the conjunction that means {}."
TYPED = [  # (id, objective, (page, line, cue), (page, line, key), prompt); a cue with "___" is the frozen example without its key
    ("u21-i-type-svoi", S, ("P", 220, "Бо́ря наде́л ___ джи́нсы."), ("P", 220, "свои́"), POSS.format("Borya put on his own jeans.")),
    ("u21-i-type-svoem", S, ("P", 206, "Бо́ря рассказа́л о ___ люби́мом фи́льме."), ("P", 206, "своём"),
     POSS.format("Borya told about his favorite movie.")),
    ("u21-i-type-svoei", S, ("P", 213, "___ кни́ги он никогда́ не написа́л."), ("P", 213, "Свое́й"), POSS.format("He never wrote his own book.")),
    ("u21-i-type-rasskazala", S, ("P", 207, "А́нна ___ о своём люби́мом фи́льме."), ("P", 207, "рассказа́ла"),
     VERB.format("Anna told about her favorite movie.")),
    ("u21-i-type-nadela", S, ("P", 221, "А́нна ___ свои́ джи́нсы."), ("P", 221, "наде́ла"), VERB.format("Anna put on her own jeans.")),
    ("u21-i-type-svoy", S, ("P", 203, "своя"), ("P", 203, "свой"), DICT.format("feminine", "masculine")),
    ("u21-i-type-svoya", S, ("P", 203, "свой"), ("P", 203, "своя"), DICT.format("masculine", "feminine")),
    ("u21-i-type-svoyo", S, ("P", 203, "свои"), ("P", 203, "своё"), DICT.format("plural", "neuter")),
    ("u21-i-type-sebya", O, ("G", 509, "Я люблю́ ___"), ("G", 509, "себя́"), WORD.format("I love myself")),
    ("u21-i-type-svoyu", O, ("G", 508, "Я люблю́ (___) жену́"), ("G", 508, "свою́"), WORD.format("I love my wife")),
    ("u21-i-type-zhenu", O, ("G", 508, "Я люблю́ (свою́) ___"), ("G", 508, "жену́"), WORD.format("I love my wife")),
    ("u21-i-type-esli", U, ("G", 2479, "чтобы"), ("G", 2476, "если"), CONJ.format("so that", "if")),
    ("u21-i-type-chtoby", U, ("G", 2476, "если"), ("G", 2479, "чтобы"), CONJ.format("if", "so that")),
    ("u21-i-type-potomu-chto", U, ("G", 2481, "хотя"), ("G", 2477, "потому что"), CONJ.format("although", "because")),
    ("u21-i-type-khotya", U, ("G", 2477, "потому что"), ("G", 2481, "хотя"), CONJ.format("because", "although")),
]
REFERENCES = [
    {"id": "ref-u21-svoy", "title": "The reflexive possessive",
     "summary": "The possessive that refers back to the subject as owner, and its agreement with the possessed noun.",
     "detail": ("свой goes with a masculine noun, своя with a feminine noun, своё with a neuter noun and свои with a plural noun."
                " The form agrees in gender, number and case with the possessed noun, not with the owner. Бо́ря рассказа́л о своём"
                " люби́мом фи́льме. — Borya told about his favorite movie. А́нна рассказа́ла о своём люби́мом фи́льме. — Anna told"
                " about her favorite movie. Свое́й кни́ги он никогда́ не написа́л. — He never wrote his own book. А́нна наде́ла свои́"
                " джи́нсы. — Anna put on her own jeans."),
     "searchTerms": ["reflexive possessive", "own", "possession"],
     "steps": ["u21-st-teach-svoy-1", "u21-st-teach-svoy-2", "u21-st-teach-svoy-3", "u21-st-teach-svoy-4"]},
    {"id": "ref-u21-object-reference", "title": "Oneself or a possessed object",
     "summary": "The reflexive object pronoun, and a possessed noun as the object.",
     "detail": ("Я люблю́ себя́ — I love myself. Я люблю́ (свою́) жену́ — I love my wife. Он поранил себя — He has wounded"
                " himself. Он поранил его — He has wounded him (someone else). The reflexive object refers back to the subject;"
                " a possessed noun names a different object."),
     "searchTerms": ["reflexive pronoun", "myself", "oneself"],
     "steps": ["u21-st-teach-object-reference-1", "u21-st-teach-object-reference-2", "u21-st-teach-object-reference-3"]},
    {"id": "ref-u21-conjunctions", "title": "Purpose, condition, cause and concession",
     "summary": "Conjunctions meaning so that, if, because and although.",
     "detail": ("чтобы — so that. дабы (bookish, archaic) — so that. если — if. потому что — because. хотя — although."
                " A purpose differs from a condition, a reason and a concession."),
     "searchTerms": ["so that", "purpose clause", "conjunction"],
     "steps": ["u21-st-teach-purpose-1", "u21-st-teach-purpose-2", "u21-st-teach-purpose-3"]},
]
BINDER = {"id": "binder-u21-svoy-conjunctions", "objectiveIds": [S, O, U],
          "prompt": ("In your binder, write the four dictionary forms of the reflexive possessive with the noun gender or number"
                     " each goes with, then the two love sentences and the four conjunctions with their meanings. Compare your"
                     " work with the model answer."),
          "modelAnswer": ("свой — masculine, своя — feminine, своё — neuter, свои — plural.\n"
                          "Я люблю́ себя́ — I love myself. Я люблю́ (свою́) жену́ — I love my wife.\n"
                          "чтобы — so that, если — if, потому что — because, хотя — although.")}
TUTOR = {"id": "tutor-u21-svoy",
         "bring": "Your binder possessive forms, love sentences and conjunctions, to read aloud and say what each one means."}
# D54g: ref-u2125-attribution (shared with Units 22-25) without governance terms; every licence and attribution fact stays.
REF_SUMMARY_OLD = ("Proposed Units 21–25 adapt admitted CC BY-SA 4.0 spans. Pressbooks evidence is separate from Wikimedia evidence."
                   " Bounded completion practice is review-pending, not full coverage.")
REF_SUMMARY_NEW = "Units 21–25 adapt CC BY-SA 4.0 spans. Pressbooks material is separate from Wikimedia material."
REF_DETAIL = [
    ("Wikibooks excerpts are frozen by oldid", "Wikibooks excerpts are pinned by oldid"),
    ("Linked audio on the useful-words page is not admitted.", "Linked audio on the useful-words page is not included."),
    ("The one-and-a-half entry is frozen at English Wiktionary oldid 89737719", "The one-and-a-half entry is pinned at English Wiktionary oldid 89737719"),
    ("Linked audio is not admitted.", "Linked audio is not included."),
    ("Unit 23 time expressions are frozen Pressbooks chapter bytes", "Unit 23 time expressions are pinned Pressbooks chapter bytes"),
    ("English Wikipedia Russian grammar oldid 1369617627 is used under CC BY-SA 4.0 via Wikipedia:Copyrights oldid 1369466282.",
     "English Wikipedia Russian grammar oldid 1369617627 is used under CC BY-SA 4.0 via Wikipedia:Copyrights oldid 1369466282."
     " The Unit 21 reflexive-object sentences come from English Wikipedia Reflexive pronoun oldid 1374712982 under that same"
     " route."),
]

# --------------------------------------------------------------------------------------------------------------
# Facts. (page, n) = a page line record, (table, accented, field) = an OpenRussian cell.
# --------------------------------------------------------------------------------------------------------------
NONFACTS = {
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    FORMS_PRACTICE, SENTENCE_PRACTICE, OBJECT_PRACTICE, CONJUNCTION_PRACTICE,
    # Reviewed instructions (sweep wording and operating directions); they state no Russian fact.
    "Study the matched sentences; practise declining the possessive and constructing other ownership sentences separately.",
    "Study the love and wounded-object sentences; practise other reflexive case forms separately.",
    "Choose the whole-sentence meaning rather than translating the object word as the verb.",
    "Keep object identity separate from ownership.",
    "Contrast because with although and after.",
    "Read the whole sentence, then practise other reflexive cases and reflexive-verb meanings separately.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


MOVIE = p("P", 205, 206, 207)
BOOK = p("P", 212, 213, 214) + [("N", "кни'га", "gender")]
JEANS = p("P", 219, 220, 221)
LOVE = p("G", 508, 509)
# R: only the four admitted Reflexive pronoun records may cite that source (V2Validator native-source identity rule).
WOUND = ["prov-u21-object-transfer-native-self", "prov-u21-object-transfer-native-other"]
DEFAULT = {  # prep 3 MARK evidence by objective
    S: p("P", 203) + p("G", 502, 503),
    O: LOVE,
    U: p("G", 2476, 2477, 2479, 2480, 2481),
}
EXTRA: dict[str, list] = {
    "The movie phrase retains its agreement; the subject and past verb change.": MOVIE + p("G", 502),
    "concerns a book, not the movie object.": BOOK + MOVIE,
    "concerns jeans and keeps Borya as subject.": JEANS,
    "In each sentence the favorite movie belongs to the subject.": MOVIE + p("G", 503),
    "The movie noun has the same gender, number and case in both, so the possessive form stays the same.": MOVIE + p("P", 203) + p("G", 502),
    "The name and past-verb agreement change.": MOVIE,
    "The possessive agrees with the feminine singular book noun; the shown form is genitive in this negative sentence.":
        BOOK + p("G", 502) + [("N", "кни'га", "sg_gen")],
    "Changing the subject changes the past verb's agreement, not the book phrase.": BOOK,
    "These sentences are about a book, not a movie or jeans.": BOOK + MOVIE + JEANS,
    "Plural jeans require plural possessive agreement regardless of whether Borya or Anna owns them.": JEANS + p("P", 203),
    "The plural possessive stays plural with jeans while subject and past-verb agreement change.": JEANS + p("P", 203),
    "concerns a movie, not jeans.": MOVIE + JEANS,
    "concerns a book, not jeans.": BOOK + JEANS,
    "The feminine singular book phrase retains its form while the subject and past-verb agreement change.": BOOK + p("G", 502),
    "concerns a movie, not the book object.": MOVIE + BOOK,
    "concerns jeans, not the book object.": JEANS + BOOK,
    "I love my wife names a wife as the object rather than the subject themself.": LOVE + [("N", "жена'", "translations_en")],
    "In the second the object is a wife; the parenthesized possessive makes ownership explicit.": LOVE + p("G", 503),
    "жену́ is the accusative form of the noun meaning wife.": LOVE + [("N", "жена'", "sg_acc"), ("N", "жена'", "translations_en")],
    "It does not mean I love.": LOVE,
    "In this contrast the other person is the object; the person wounded is different from the subject.": WOUND,
    "In both wounded sentences the object is a person, not a possessed wife.": WOUND + LOVE,
    "He has wounded him (someone else).": ["prov-u21-object-transfer-native-other"],
    "чтобы means so that: the conjunction can introduce a purpose.": p("G", 2479),
}
FACTS: set[str] = {  # prep 3 MARK sentences with no Cyrillic form; DEFAULT evidence of their objective
    "The reflexive possessive refers to the subject as the owner.",
    "Its gender, number and case agree with the possessed noun, not with the owner.",
    "A masculine owner and a feminine owner can therefore use the same possessive form with the same noun.",
    "— Borya told about his favorite movie.", "— Anna told about her favorite movie.",
    "— She never wrote her own book.", "— He never wrote his own book.",
    "— Borya put on his own jeans.", "— Anna put on her own jeans.",
    "In the first sentence the object refers back to the subject.",
    "Loving oneself and loving a wife describe different objects.",
    "I love myself makes the subject themself the object.",
    "The first conjunction can introduce a purpose; the second introduces a condition.",
    "The English meaning so that differs from the conditional if and the causal because.",
    "The first gives a reason; the second introduces a concession.",
    "The English label after names a temporal relationship rather than a reason.",
    "English since can express a reason, so it can overlap in meaning with because.",
    "These conjunction meanings are distinct from purpose and condition.",
    "if introduces a condition.", "because supplies a reason.", "so that introduces a purpose.",
    "although introduces a concession.", "after introduces a temporal relationship.",
}
FACT_EVIDENCE = {  # FACTS sentences whose evidence is narrower than the objective default
    "— Borya told about his favorite movie.": p("P", 206), "— Anna told about her favorite movie.": p("P", 207),
    "— She never wrote her own book.": p("P", 214), "— He never wrote his own book.": p("P", 213),
    "— Borya put on his own jeans.": p("P", 220), "— Anna put on her own jeans.": p("P", 221),
    "English since can express a reason, so it can overlap in meaning with because.": p("G", 2477, 2478),
    "The English label after names a temporal relationship rather than a reason.": p("G", 2477, 2480),
}
REMOVED = {  # prep 3 REMOVE and D54b/D54c: CSV line -> sentence (as the sweep left it, where it rewrote one)
    2422: "Keep the movie as the object while changing the subject.",
    2435: "Keep the object the same while changing the subject.",
    2469: ("Interpret these direct objects in their sentence contexts; practise other pronoun cases and emphasis separately,"
           " without assuming the reflexive-verb suffix always means accidental action."),
    2484: "In a purpose construction with the same subject for both actions, an infinitive after the purpose conjunction can name the intended action.",
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

    def template(self, page: str) -> dict:
        record = copy.deepcopy(self.records[PAGES[page][1]])
        record.pop("originalSourceTextRef", None)
        return record

    def line(self, page: str, number: int, objective: str) -> str:
        record_id = f"prov-t18a2-u21-{page.lower()}{number}-{objective[8:]}"
        if record_id in self.records:
            return record_id
        raw = self.raw(page, number)
        if (SRC / PAGES[page][0]).read_text(encoding="utf-8").count(raw) != 1:
            raise SystemExit(f"{page}: line is not unique on its page ({raw[:60]!r})")
        record = self.template(page)
        # Units 16-30 provenance ships no MediaWiki markup: the longest markup-free stretch of the line.
        shipped = max((c.strip(" *:#-") for c in MARKUP.split(raw) if re.search("[A-Za-zЀ-ӿ]", c)), key=len)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": PAGES[page][2] + raw[:80],
                       "originalSourceText": raw, "shippedText": shipped, "sourceHash": sha(raw), "contentHash": sha(shipped),
                       "recordType": "instruction", "transformation": "selection"})
        return self.add(record)

    def select(self, page: str, number: int, text: str, objective: str, record_type: str = "example") -> str:
        raw = self.raw(page, number)
        if not d7b.selectable(raw, text):  # T16c-n1 (D39a): whole words only
            raise SystemExit(f"{text!r} is not displayed on {page}:{number}")
        record_id = f"prov-t18a2-u21-{page.lower()}{number}-{sha(text + objective)[:8]}"
        if record_id in self.records:
            return record_id
        record = self.template(page)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": PAGES[page][2] + raw[:80],
                       "originalSourceText": raw, "shippedText": text, "sourceHash": sha(raw), "contentHash": sha(text),
                       "recordType": record_type, "transformation": "selection"})
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
        record.update({"itemId": f"prov-t18a2-u21-{table.lower()}-{sha(accented + field + objective)[:8]}-{field}", "unitNumber": UNIT,
                       "objectiveId": objective, "sourceRecordId": source_id, "originalSourceText": original,
                       "shippedText": shipped, "sourceHash": sha(original), "contentHash": sha(shipped),
                       "recordType": "lexical record", "transformation": "formatting"})
        return self.add(record)

    def ref(self, spec, objective: str) -> str:
        if isinstance(spec, str):  # an existing frozen record, cited as it is
            if spec not in self.records or not d7b.frozen_record(self.pack, self.records[spec]):
                raise SystemExit(f"{spec} is not a frozen record")
            return spec
        return self.line(spec[0], spec[1], objective) if len(spec) == 2 else self.cell(*spec, objective)


def apply_unit_sweep(pack: dict) -> list[dict]:
    """The Unit 21 rows of the Units 16-30 sweep CSV. The reviewCopy rows were applied before this task (T16-n2 messages,
    T17e Unit 21 introduction) and the reviewStatus row is met by the D31b label; each is verified, not reapplied."""
    if hashlib.sha256(SWEEP_CSV.read_bytes()).hexdigest() != SWEEP_CSV_SHA256:
        raise SystemExit("Sweep CSV changed")
    unit = next(u for u in pack["units"] if u["number"] == UNIT)
    owners = {unit["id"]: unit}
    for se in unit["sessions"]:
        owners[se["id"]] = se
        for st in se["steps"]:
            owners[st["id"]] = st
    owners.update({i["id"]: i for i in pack["items"] if i["objectiveId"].startswith(f"obj-u{UNIT}-")})
    owners.update({r["id"]: r for r in pack["references"]})
    log = []
    for row in csv.DictReader(SWEEP_CSV.open(encoding="utf-8", newline="")):
        if int(row["unit"]) != UNIT:
            continue
        ref, old, new = row["owner.field"], row["current text"], row["suggested rewrite"]
        if ref.endswith(".reviewStatus"):
            log.append({"owner.field": ref, "old": old, "new": "met by the D31b label (not reapplied)"})
            continue
        m = re.fullmatch(r"reviewCopy\.(messages|introductions)\[(\w+)\]", ref)
        if m:
            value = pack["reviewCopy"][m.group(1)][m.group(2)]
            if old in value:
                raise SystemExit(f"{ref}: sweep row not yet applied")
            log.append({"owner.field": ref, "old": old, "new": "already applied before T18a-2 (verified absent)"})
            continue
        owner, field = ref.split(".", 1)
        host = owners[owner]
        host[field] = edit(host[field], old, new, ref)
        if field == "feedback":
            a = host.get("assessment") or {}
            if old in (a.get("whyAnswerDemonstratesObjective") or ""):
                a["whyAnswerDemonstratesObjective"] = a["whyAnswerDemonstratesObjective"].replace(old, new)
        log.append({"owner.field": ref, "old": old, "new": new})
    return log


REGION = re.compile("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*")


def gapped(b: Builder, spec: tuple, objective: str) -> str:
    """A cue: a frozen selection, or the frozen example with its key replaced by a blank (the rest stays exact)."""
    page, number, text = spec
    if "___" not in text:
        return b.records[b.select(page, number, text, objective, "lexical record")]["shippedText"]
    shown = d7b.displayed(b.raw(page, number))
    pattern = re.escape(text).replace("___", f"[{CYR}{ACUTE}]+")
    if not re.search(pattern, shown):
        raise SystemExit(f"{text!r} is not the frozen line {page}:{number} with one word blanked")
    for m in REGION.finditer(text):  # every remaining Russian run (as V2Validator reads it) has its own frozen record
        b.select(page, number, m.group(0).strip().rstrip("?!.,"), objective, "lexical record")
    return text


def apply(pack: dict) -> tuple[dict, list, list]:
    baseline = copy.deepcopy(pack)  # before the sweep, so sweep wording is covered too
    sweep_log = apply_unit_sweep(pack)
    b = Builder(pack)
    unit = b.unit
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R_ = b.records
    change_log: list[dict] = []

    def logged(owner: str, old: str, new: str, finding: str) -> None:
        if old != new:
            change_log.append({"task": TASK, "finding": finding, "owner.field": owner, "old": old, "new": new})

    # Russian the new teaching shows.
    for text in ("рассказа́л", "рассказа́ла"):
        b.select("P", 206 if text.endswith("л") else 207, text, S)
    for text in ("наде́л", "наде́ла"):
        b.select("P", 220 if text.endswith("л") else 221, text, S)
    b.select("G", 2479, "дабы", U)
    b.select("G", 2479, "чтобы", U)
    b.select("G", 509, "себя́", O)
    # --- teaching and feedback ---------------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        old = steps[step_id]["body"]
        for a, c in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], a, c, step_id)
        logged(f"{step_id}.body", old, steps[step_id]["body"], "D54e/prep 3")
    old = steps["u21-st-teach-purpose-2"]["body"]
    if "ordinary purpose conjunction" not in old or "bookish or archaic" not in old:
        raise SystemExit("u21-st-teach-purpose-2 changed")
    steps["u21-st-teach-purpose-2"]["body"] = PURPOSE_2
    logged("u21-st-teach-purpose-2.body", old, PURPOSE_2, "D54c")
    for item_id, pairs in FEEDBACK.items():
        item = items[item_id]
        old = item["feedback"]
        for a, c in pairs:
            item["feedback"] = edit(item["feedback"], a, c, item_id)
            ass = item.get("assessment") or {}
            if a in (ass.get("whyAnswerDemonstratesObjective") or ""):
                ass["whyAnswerDemonstratesObjective"] = ass["whyAnswerDemonstratesObjective"].replace(a, c)
        logged(f"{item_id}.feedback", old, item["feedback"], "D54c")
    for step in steps.values():
        if step["kind"] in ("GUIDED", "CHECK"):
            old = step["body"]
            step["body"] = edit(step["body"], OLD_PRACTICE_BODY, PRACTICE_BODY, step["id"])
            logged(f"{step['id']}.body", old, step["body"], "A2-26")
    # --- remediation repeats each session's whole teaching ---------------------------------------------------------
    for se in unit["sessions"]:
        rem = [s for s in se["steps"] if s["kind"] == "REMEDIATE"]
        if rem:
            old = rem[0]["body"]
            rem[0]["body"] = "\n\n".join(s["body"].strip() for s in se["steps"] if s["kind"] == "TEACH")
            rem[0]["exampleIds"] = list(dict.fromkeys(x for s in se["steps"] if s["kind"] == "TEACH" for x in s.get("exampleIds") or []))
            logged(f"{rem[0]['id']}.body", old, rem[0]["body"], "D54e/prep 3")
    for line, sentence in REMOVED.items():
        if any(sentence in s["body"] for s in steps.values()) or sentence in json.dumps(unit, ensure_ascii=False):
            raise SystemExit(f"CSV {line}: removed sentence still shown")
    for s in steps.values():
        if "ordinary" in s["body"] or "the same subject for both actions" in s["body"]:
            raise SystemExit(f"{s['id']}: D54b/D54c wording left")
    # --- typed checks -------------------------------------------------------------------------------------------------
    typed = []
    for item_id, objective, cue, key, prompt in TYPED:
        cue_text = gapped(b, cue, objective)
        key_id = b.select(key[0], key[1], key[2], objective, "lexical record")
        if R_[key_id]["shippedText"] != key[2]:
            raise SystemExit(f"{item_id}: key is not the exact frozen text")
        item = typed_item(item_id, objective, prompt, cue_text, key[2], TYPED_FEEDBACK, SOURCE[key[0]])
        item["keyCitation"] = key_id
        typed.append(item)
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    # The objectives' check-list data pins (pack contentChecks) follow the new check lists; nothing else in them changes.
    for check in pack["contentChecks"]:
        for pin in check["objectives"]:
            if pin["id"] in objectives and pin["checkItemIds"] != objectives[pin["id"]]["checkItemIds"]:
                logged(f"contentChecks[{check['id']}].objectives[{pin['id']}].checkItemIds", json.dumps(pin["checkItemIds"]),
                       json.dumps(objectives[pin["id"]]["checkItemIds"]), "D54e")
                pin["checkItemIds"] = list(objectives[pin["id"]]["checkItemIds"])
    pack["items"].extend(typed)
    items.update({i["id"]: i for i in typed})
    bare = lambda x: x.replace(ACUTE, "").lower()  # noqa: E731
    if len({bare(i["answer"]) for i in typed}) != len(typed):
        raise SystemExit("two typed keys are spellings of one form")
    for item in typed:  # typed keys are taught answers, in the exact frozen spelling
        taught = " ".join(steps[s]["body"] for s in objectives[item["objectiveId"]]["instructionStepIds"])
        if not re.search(f"(?<![{CYR}{ACUTE}]){re.escape(item['answer'])}(?![{CYR}{ACUTE}])", taught):
            raise SystemExit(f"{item['id']}: key {item['answer']} is not taught")
        if item["answer"] in item["prompt"]:
            raise SystemExit(f"{item['id']}: key in prompt")
    # --- unit fields; checks move to an end-of-unit session ---------------------------------------------------------
    logged("unit-21.topicSummary", unit["topicSummary"], TOPIC, "A2-26")
    logged("unit-21.mappingNote", unit["mappingNote"], MAPPING, "A2-26")
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
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u21-s-book")
    unit["sessions"].insert(book, {"id": "u21-s-checks", "title": "Unit 21 checks", "estimatedMinutes": 20,
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
        raise SystemExit("Unit 21 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    attribution = next(r for r in pack["references"] if r["id"] == "ref-u2125-attribution")
    old_summary, old_detail = attribution["summary"], attribution["detail"]
    attribution["summary"] = edit(attribution["summary"], REF_SUMMARY_OLD, REF_SUMMARY_NEW, "ref-u2125-attribution.summary")
    for a, c in REF_DETAIL:
        attribution["detail"] = edit(attribution["detail"], a, c, "ref-u2125-attribution.detail")
    logged("ref-u2125-attribution.summary", old_summary, attribution["summary"], "D54g")
    logged("ref-u2125-attribution.detail", old_detail, attribution["detail"], "D54g")
    # --- every Unit 21 learner field: Russian only through frozen Unit 21 records ------------------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and d7b.frozen_record(pack, r)}
    texts = [s["body"] for s in steps.values()] + [unit["topicSummary"], unit["mappingNote"], BINDER["prompt"], BINDER["modelAnswer"]]
    texts += [r[k] for r in pack["references"] if r["id"] in {s["id"] for s in REFERENCES} for k in ("title", "summary", "detail")]
    texts += [e["display"] for e in pack["examples"] if e.get("objectiveId", "").startswith(f"obj-u{UNIT}-")]
    for item in items.values():
        if item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            texts += [item["prompt"], item.get("promptLetter") or "", item["answer"], item["feedback"]] + item["options"] + item.get("distractorRationales", [])
    unshown = set()
    for text in texts:
        for m in REGION.finditer(text):
            region = m.group(0).strip()
            if region not in frozen and region.rstrip("?!.,") not in frozen:  # exact, as V2Validator requires
                unshown.add(region)
    if unshown:
        raise SystemExit(f"Unit 21 regions without a frozen record: {sorted(unshown)}")
    report = mark_facts(b, steps, items)
    cover_english(pack, baseline)
    return report, sweep_log, change_log


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
                raise SystemExit(f"No frozen Unit 21 record shows {form!r} ({sentence})")
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
                    refs = FACT_EVIDENCE.get(sentence, DEFAULT[objective])
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
        out = Path("/tmp/claude-0/-home-user-russian-companion/8476c374-e1ab-5f3d-9af8-fc234c0d6e90/scratchpad/t18a2/missing.json")
        out.write_text(json.dumps(missing, ensure_ascii=False, indent=1), encoding="utf-8")
        raise SystemExit(f"{len(missing)} sentences without a disposition (written to {out})")
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
        if r["id"] in {s["id"] for s in REFERENCES} or r["id"] == "ref-u2125-attribution":
            for k in ("title", "summary", "detail"):
                out[(r["id"], k)] = (UNIT, first, r[k], [])
    for k in ("topicSummary", "mappingNote"):
        out[(unit["id"], k)] = (UNIT, first, unit.get(k) or "", [])
    for se in unit["sessions"]:
        out[(se["id"], "title")] = (UNIT, first, se["title"], [])
        for st in se["steps"]:
            out[(st["id"], "title")] = (UNIT, objective_of.get(st["id"], first), st["title"], [])
    if unit.get("binderTask"):
        for k in ("prompt", "modelAnswer"):
            out[(unit["binderTask"]["id"], k)] = (UNIT, unit["binderTask"]["objectiveIds"][0], unit["binderTask"][k], [])
    if unit.get("tutorTask"):
        out[(unit["tutorTask"]["id"], "bring")] = (UNIT, first, unit["tutorTask"]["bring"], [])
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
        item_id = f"prov-ca-t18a2-{key}"
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
        raise SystemExit("Asset is not the T18a-2 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report, sweep_log, change_log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    out = json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n"
    if "--dry" in sys.argv:
        dry = [a for a in sys.argv[1:] if a != "--dry"]
        if dry:
            Path(dry[0]).write_text(out, encoding="utf-8")
        print(f"dry run; {len(sweep_log)} sweep rows, {len(change_log)} changes; asset not written")
        return
    ASSET.write_text(out, encoding="utf-8")
    rows = [{"task": TASK, "finding": "sweep", **r} for r in sweep_log] + change_log
    with LOG.open("w", encoding="utf-8", newline="") as f:
        w = csv.DictWriter(f, fieldnames=["task", "finding", "owner.field", "old", "new"], lineterminator="\n")
        w.writeheader()
        w.writerows(rows)
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps({"facts": report}, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(sweep_log)} Unit 21 sweep rows applied; {len(change_log)} changes; wrote {ASSET.relative_to(ROOT)}"
          f" sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
