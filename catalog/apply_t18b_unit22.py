#!/usr/bin/env python3
"""T18b (Claude Code, owner decisions D56a-D56f): Unit 22 to the Block 4 standard.

Exact-baseline transformer; runs once on the T18a pack (88519768, 9043034a; T18a DONE at 81832957). Plan: Codex prep
audit/prep/unit-22-prep-at-7d6e66e1.txt (owner/prep-reports). The Unit 22 rows of the Units 16-30 learner-text sweep
(audit/T17a-learner-text-sweep-units-16-30.csv) are applied first; three of them carry pre-T17g wording ("collective column")
and are applied to the T17g wording (T17G_CURRENT). Russian comes only from frozen files already in the source inventory:
Wikibooks numbers 4371358 (R), ru Wikipedia Морфология 148939075 (M), Wiktionary полтора 89737719 (W), Wikipedia Russian
grammar 1369617627 (G), Wikibooks instrumental 4219602 (I) and OpenRussian others.csv (O).
- A2-24: every required check moves to the end-of-unit session u22-s-checks, after all teaching; the prep's five semantic
  prompts replace the label/position prompts.
- D56c A2-23: the 14 closed checks stay; six typed checks keyed двум, двумя, обеим, обоим, обеими, обоими (M:568-569,
  581-582, unstressed verbatim), each request naming gender and case; production practice is taught first; within the
  check session each objective's typed checks come before its closed checks, so no accepted key is shown before its check.
  20 checked, 6 typed.
- Facts (prep 3): MARK per the evidence ranges; REWORD 2587 and 2658-2659 exactly as the prep words them (D56b); REMOVE the
  listed lines; D56a: REMOVE 2694-2696 (the collective-usage restrictions), keeping the form table and the quantity
  distinction. One-and-a-half sentences are marked whole (the decimal point does not split them).
- A2-26: governance and narration leave Unit 22 learner text; D56e: the raw table-cell quotations leave the feedback
  (provenance stays in the assessment metadata).
- A2-36 references, binder and tutor tasks.
D56d needs no change here: the phrase-audit extractor (TwoPartPhraseRule.unstressed) already reads [[target|display]] as its
display text, and Unit 22 already passes the phrase audit.
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
BASELINE_SHA256 = "88519768214601858c3baa3a8b6ff91661165ce828dbdbe30063370c4d1a83be"  # the T18a pack (9043034a)
SWEEP_CSV = ROOT / "audit/T17a-learner-text-sweep-units-16-30.csv"
SWEEP_CSV_SHA256 = "0ebf273853d01641275fce68bc396d4374f3fb082cd3d501210a6e84badec96f"
LOG = ROOT / "audit/T18b-unit22-changes.csv"
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T18b", "2026-11-27", 22
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "R": ("units-6-10/wikibooks/numbers-4371358.txt", "prov-ex-u22-noun-forms-0-ru", "numbers:"),
    "M": ("units-21-25/supplemental/morfologiya-148939075.txt", "prov-ex-u22-declension-0-ru", "morfologiya:"),
    "G": ("units-26-30/wikipedia/russian-grammar-1369617627.txt", "prov-ex-u22-collective-0-ru", "grammar:"),
    "I": ("units-16-20/wikibooks/instrumental-4219602.txt", "prov-ex-u16-inst-sg-8-ru", "instrumental:"),
}
SOURCE = {"M": "src-ruwiki-morfologiya-148939075"}
TABLES = {"O": SRC / "units-3-5/openrussian/others.csv"}
TABLE_NAME = {"O": "others.csv"}
ROWS = {k: list(csv.DictReader(p.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE)) for k, p in TABLES.items()}
CELL_TEMPLATE = {"O": "prov-ex-or-u10-pronoun-13"}
N, C, D, B, H, K = ("obj-u22-noun-forms", "obj-u22-compounds", "obj-u22-declension", "obj-u22-both-forms", "obj-u22-half-forms",
                    "obj-u22-collective")
T17G_CURRENT = {
    ("u22-st-orient.body", "Proposed Unit 22 uses frozen item-form lines, the frozen numeral table, the frozen half-form usage note, and the frozen collective column."):
        "Proposed Unit 22 uses frozen item-form lines, the frozen numeral table, the frozen half-form usage note, and the frozen collective number forms.",
    ("u22-st-rem-collective.body", "Selecting the collective column does not demonstrate choosing its use with every noun or constructing a declined collective phrase."):
        "Selecting the collective form does not demonstrate choosing its use with every noun or constructing a declined collective phrase.",
    ("u22-st-teach-collective-3.body", "Selecting the collective column does not demonstrate choosing its use with every noun or constructing a declined collective phrase."):
        "Selecting the collective form does not demonstrate choosing its use with every noun or constructing a declined collective phrase.",
}

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 22 covers counting phrases and their noun forms, compound numbers, declined numerals, the forms for both and one"
         " and a half, and collective numbers, alongside Lesson 22 in your book.")
MAPPING = ("This unit goes with Lesson 22 in your book, printed pages 269-282: numeral behavior beyond basic counting, including"
           " declined numerals, forms for both and one and a half, compound quantities, and collective or quantity expressions.")
TWO_NOM = "The nominative forms are два (masculine and neuter) and две (feminine); the dative двум and the instrumental двумя are the same for every gender."
TWO_PRACTICE = "Practise writing the dative and the instrumental form of the numeral for two."
BOTH_NOM = "The nominative forms are оба (masculine and neuter) and обе (feminine)."
BOTH_DAT_PRACTICE = "Practise writing the feminine and the masculine/neuter dative forms of both."
BOTH_INS_PRACTICE = "Practise writing the feminine and the masculine/neuter instrumental forms of both."
WITH = "с полу́тора рубля́ми — with 1.5 roubles. Here ‘with’ gives the preposition’s meaning, and the noun is instrumental plural."  # D56b, prep 3
OBLIQUE = "In the oblique constructions shown here, the numeral and counted noun take the case required by the construction."  # D56b
COLLECTIVE_2 = ("два — дво́е. три — тро́е. четы́ре — че́тверо. Each collective keeps the quantity of its cardinal: дво́е is a group of"
                " two, тро́е a group of three and че́тверо a group of four.")  # D56a: the form table and the quantity distinction
TEACH = {  # step: [(old, new)]; new "" deletes the sentence and its leading space
    "u22-st-teach-noun-forms-1": [(" Find another counting phrase with one; the noun can be different.", "")],  # CSV 2511 REMOVE
    "u22-st-teach-compounds-1": [(" Identify the final-digit group; it can differ from the displayed reference phrase.", "")],  # 2548
    "u22-st-teach-compounds-2": [(" These are numeric rule comparisons, not newly supplied Russian phrases.", "")],  # 2553
    "u22-st-teach-compounds-3": [(" Distinguish the final digit nine from the quantity or translation of the displayed 41 phrase.", "")],  # 2558
    "u22-st-teach-declension-1": [("it is not enough to recognize the quantity two.",
                                   "it is not enough to recognize the quantity two. " + TWO_NOM + " " + TWO_PRACTICE)],
    "u22-st-teach-declension-2": [("In oblique number phrases the plural noun normally carries the case required by the construction.", OBLIQUE)],  # 2587
    "u22-st-teach-declension-3": [(" Learn these case forms, then practise declining complete number phrases separately.", "")],  # 2592
    "u22-st-teach-both-forms-1": [("both displayed forms are dative.", "both displayed forms are dative. " + BOTH_NOM + " " + BOTH_DAT_PRACTICE)],
    "u22-st-teach-both-forms-2": [("The dative forms двум / обеим / полутора places two", "The dative forms двум / обеим / полутора place two")],
    "u22-st-teach-both-forms-3": [("practise choosing gender and case in new sentences separately.",
                                   "practise choosing gender and case in new sentences separately. " + BOTH_INS_PRACTICE)],
    "u22-st-teach-half-forms-3": [("The English phrase with 1.5 roubles describes accompaniment/means with an instrumental noun in this construction.", WITH)],  # 2658-2659
}
TITLES = {"u22-st-teach-collective-2": "Collective forms and their quantities"}
PROMPTS = {  # prep 1: the five semantic prompts for the label/position findings
    "u22-i-declension-1": "Which case does this numeral form have?",
    "u22-i-declension-2": "Which case do the numerals have after the shown prepositions?",
    "u22-i-both-forms-1": "Which case does this numeral form have?",
    "u22-i-both-forms-2": "Which case does this numeral form have?",
    "u22-i-both-forms-3": "Which case does this numeral form have?",
}
GENERIC = " It does not match the category or whole-phrase meaning."  # prep 3: REMOVE (sweep wording of the repeated sentence)
CELL_QUOTE = re.compile(r' In context: " ?(?:Дательный|Творительный|Предложный) ?"\.')  # D56e
TYPED_FEEDBACK = "Compare your answer with the case forms above, then try again. Stress marks are optional when you type."
TYPED = [  # (id, objective, (page, line, cue), (page, line, key), prompt)
    ("u22-i-type-dvum", D, ("M", 560, "два"), ("M", 568, "двум"),
     "The shown numeral means two. Type its dative form, used with masculine, neuter and feminine nouns."),
    ("u22-i-type-dvumya", D, ("M", 560, "два"), ("M", 581, "двумя"),
     "The shown numeral means two. Type its instrumental form, used with masculine, neuter and feminine nouns."),
    ("u22-i-type-obeim", B, ("M", 561, "обе"), ("M", 569, "обеим"),
     "The shown numeral means both with a feminine noun. Type its feminine dative form."),
    ("u22-i-type-oboim", B, ("M", 560, "оба"), ("M", 568, "обоим"),
     "The shown numeral means both with a masculine or neuter noun. Type its masculine/neuter dative form."),
    ("u22-i-type-obeimi", B, ("M", 561, "обе"), ("M", 582, "обеими"),
     "The shown numeral means both with a feminine noun. Type its feminine instrumental form."),
    ("u22-i-type-oboimi", B, ("M", 560, "оба"), ("M", 581, "обоими"),
     "The shown numeral means both with a masculine or neuter noun. Type its masculine/neuter instrumental form."),
]
REFERENCES = [
    {"id": "ref-u22-counting", "title": "Counting phrases and noun forms",
     "summary": "Which noun form follows one, two to four, and five and higher, including compound numbers.",
     "detail": ("один мальчик — one boy. одна девушка — one girl. две девушки — two girls. три кошки — three cats. пять девушек —"
                " five girls. десять копеек — ten kopeks. In nominative counting phrases one takes a singular noun, two through four"
                " a genitive singular noun, and five through twenty a genitive plural noun. Compound numbers follow their last digit:"
                " тридцать три рюмки — thirty-three shot glasses, сорок один год — forty-one years, шестьдесят девять слов — sixty-nine"
                " words; last two digits 11 through 14 take genitive plural."),
     "searchTerms": ["counting", "number and noun", "genitive after numbers"],
     "steps": ["u22-st-teach-noun-forms-1", "u22-st-teach-noun-forms-2", "u22-st-teach-noun-forms-3", "u22-st-teach-noun-forms-4",
               "u22-st-teach-compounds-1", "u22-st-teach-compounds-2", "u22-st-teach-compounds-3"]},
    {"id": "ref-u22-declined", "title": "Two, both and one and a half by case",
     "summary": "The case forms of two, both and one and a half.",
     "detail": ("два, две — two; оба, обе — both. Dative: двум; обоим (masculine and neuter), обеим (feminine). Instrumental: двумя;"
                " обоими (masculine and neuter), обеими (feminine). Prepositional: (о) двух / (об) обоих / (о) полутора. полтора́"
                " рубля́ — 1.5 roubles; полторы́ мину́ты — 1.5 minutes; из полу́тора рубле́й — from 1.5 roubles; к полу́тора рубля́м —"
                " to 1.5 roubles."),
     "searchTerms": ["declined numerals", "both", "one and a half"],
     "steps": ["u22-st-teach-declension-1", "u22-st-teach-declension-2", "u22-st-teach-declension-3", "u22-st-teach-both-forms-1",
               "u22-st-teach-both-forms-2", "u22-st-teach-both-forms-3", "u22-st-teach-half-forms-1", "u22-st-teach-half-forms-2",
               "u22-st-teach-half-forms-3"]},
    {"id": "ref-u22-collective", "title": "Collective numbers",
     "summary": "Collective forms for two, three and four, and how they differ from ordinals.",
     "detail": ("два — дво́е, три — тро́е, четы́ре — че́тверо. A collective presents a quantity as a group; an ordinal such as второ́й"
                " (second), тре́тий (third) or четвёртый (fourth) states a position."),
     "searchTerms": ["collective numbers", "ordinal", "cardinal"],
     "steps": ["u22-st-teach-collective-1", "u22-st-teach-collective-2", "u22-st-teach-collective-3"]},
]
BINDER = {"id": "binder-u22-numerals", "objectiveIds": [N, C, D, B, H, K],
          "prompt": ("In your binder, write the dative and instrumental forms of two and of both, with the gender each form of both"
                     " goes with, then the one-and-a-half phrases and the collective forms with their meanings. Compare your work"
                     " with the model answer."),
          "modelAnswer": ("двум — dative, двумя — instrumental (every gender).\n"
                          "обоим, обоими — masculine and neuter; обеим, обеими — feminine.\n"
                          "полтора́ рубля́ — 1.5 roubles, из полу́тора рубле́й — from 1.5 roubles, к полу́тора рубля́м — to 1.5 roubles.\n"
                          "дво́е, тро́е, че́тверо — two, three and four as a group.")}
TUTOR = {"id": "tutor-u22-numerals",
         "bring": "Your binder numeral forms and one-and-a-half phrases, to read aloud and say which case and quantity each shows."}

# --------------------------------------------------------------------------------------------------------------
# Facts. (page, n) = a page line record, (table, accented, field) = an OpenRussian cell, "prov-..." = an existing record.
# --------------------------------------------------------------------------------------------------------------
NONFACTS = {
    "Compare your answer with the case forms above, then try again.",
    "Stress marks are optional when you type.",
    TWO_PRACTICE, BOTH_DAT_PRACTICE, BOTH_INS_PRACTICE,
    # Reviewed instructions (sweep wording and operating directions); they state no Russian fact.
    "Find another counting phrase in the two-through-four group, rather than translating the cue.",
    "Learn the number phrases, then practise producing other noun endings separately.",
    "Identify the specified final-digit group.",
    "Identify the specified numeral category.",
    "Identify the case of each form; practise choosing gender and case in new sentences separately.",
    "Compare the preposition and the noun ending.",
    "Interpret the whole phrase, including from or to.",
    "Learn the collective forms, then practise their use with different nouns and in declined phrases separately.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


NOUN = ["prov-u22-noun-forms-rem"] + p("R", 86) + p("M", 498)
COMP = ["prov-u22-compounds-rem"] + p("R", 96, 98)
DECL = ["prov-u22-declension-rem"] + p("G", 34, 42) + p("I", 14)
BOTH = ["prov-u22-both-forms-rem", ("O", "о'ба", "translations_en")]
HALF = ["prov-u22-half-forms-rem", "prov-ex-u22-half-forms-0-ru", "prov-ex-u22-half-forms-1-ru", "prov-ex-u22-half-forms-2-ru",
        "prov-ex-u22-half-forms-3-ru", "prov-u22-half-forms-x-0"]
COLL = ["prov-u22-collective-rem", ("O", "дво'е", "translations_en"), ("O", "тро'е", "translations_en"),
        ("O", "че'тверо", "translations_en")]
DEFAULT = {N: NOUN, C: COMP, D: DECL, B: BOTH, H: HALF, K: COLL}
EXTRA: dict[str, list] = {}
FACTS: set[str] = {  # prep 3 MARK sentences with no Cyrillic form (evidence ranges 2500-2709); DEFAULT evidence of their objective
    "The numeral meaning one agrees with its noun in gender, number and case.",
    "These displayed counting phrases use a singular noun: the masculine boy takes the masculine numeral, and the feminine girl takes the feminine numeral.",
    "For nominative counting phrases, one takes a singular noun, two through four take a genitive singular noun, and five through twenty take a genitive plural noun.",
    "Apply this shortcut to an accusative phrase only where its forms coincide with the nominative.",
    "Animate accusative phrases and other cases need their own case forms; the shortcut is not a rule that all number phrases keep these endings.",
    "Both displayed nouns are genitive singular after these numerals, although the quantity is greater than one and these endings can resemble nominative plural.",
    "The numeral for two has a feminine form; the form for three does not change for the noun gender.",
    "Here the nouns are genitive plural.",
    "Five and ten belong to the same noun-form group; one and two do not.",
    "Numbers whose last two digits are 11 through 14 also use this group, including 111 through 114.",
    "In these nominative counting phrases, 33 ends in three and takes a genitive singular noun; 41 ends in one and takes a singular noun.",
    "For these whole-number counting phrases, check the last two digits before the final digit.",
    "Endings 11 through 14 require genitive plural.",
    "Otherwise a final one takes singular, final two through four take genitive singular, and final zero or five through nine take genitive plural.",
    "For example, 111 differs from 41; 113 differs from 33.",
    "In other cases the numeral and noun need the relevant declined forms.",
    "The final nine places 69 in the genitive-plural group.",
    "Both are declined forms of the numeral meaning two.",
    "Dative commonly marks a recipient or goal; instrumental can mark accompaniment or means.",
    "The final part of the form distinguishes these two shown examples; it is not enough to recognize the quantity two.",
    "A form must be interpreted with its construction.",
    "The complete expression includes its preposition and is prepositional; a bare ending does not always uniquely identify a case.",
    "In the oblique constructions shown here, the numeral and counted noun take the case required by the construction.",
    "These are the prepositional forms for two, both and one and a half.",
    "The parentheses show the accompanying preposition; it is not part of the numeral itself.",
    "Both forms mean both in the dative.",
    "Gender refers to the counted noun.",
    "Changing the gender series does not change the case: both displayed forms are dative.",
    "Both refers to the full pair, rather than an unspecified quantity of two.",
    "The nominative forms distinguish masculine/neuter from feminine.",
    "The feminine and masculine/neuter series remain distinct, but both shown forms are instrumental.",
    "The first numeral form is masculine/neuter; the second is feminine.",
    "In the nominative and accusative construction, one and a half takes a genitive singular noun.",
    "The noun determines the numeral gender even though the amount exceeds one.",
    "The alternative translation one and a half hours / an hour and a half names hours, not roubles or minutes.",
    "The numeral form alone therefore cannot tell you whether a phrase is genitive, dative or instrumental.",
    "This differs from the genitive singular noun after the nominative/accusative forms.",
    "Here ‘with’ gives the preposition’s meaning, and the noun is instrumental plural.",
    "The amount stays one and a half while the preposition changes the relationship.",
    "A cardinal states a quantity; a collective presents that quantity as a group.",
    "An ordinal states position in a sequence.",
    "The collective form keeps the quantity four while presenting it as a group.",
    "Use the grammatically appropriate form of one.",
    "This line is the other example with one.",
    "With two, three and four, use the corresponding form of the following item.",
    "This line is the three example.",
    "Use the corresponding form of the following item with these numerals.",
    "This line is another example from that group.",
    "For numerals above twenty, apply these rules using the last digit.",
    "This example ends in one.",
    "This example ends in nine.",
    "This example ends in three.",
    "In context: \"In the nominative and accusative\".",
    "1.5 minutes: a time quantity measured in minutes.",
    "one and a half hours / an hour and a half: a time quantity measured in hours.",
    "1.5 roubles: a money quantity without a from/to/with relationship.",
    "In context: \"from 1.5 roubles\".",
    "to 1.5 roubles: the to relationship with a dative plural noun.",
    "with 1.5 roubles: the with relationship with an instrumental plural noun.",
    "In context: \"to 1.5 roubles\".",
    "from 1.5 roubles: the from relationship with a genitive plural noun.",
    "In context: \"collective numbers\".",
}
FACT_EVIDENCE: dict[str, list] = {}
REMOVED = {  # prep 3 REMOVE and D56a: CSV line -> sentence (as the sweep left it)
    2511: "Find another counting phrase with one; the noun can be different.",
    2548: "Identify the final-digit group; it can differ from the displayed reference phrase.",
    2553: "These are numeric rule comparisons, not newly supplied Russian phrases.",
    2558: "Distinguish the final digit nine from the quantity or translation of the displayed 41 phrase.",
    2592: "Learn these case forms, then practise declining complete number phrases separately.",
    2694: "Collective number forms are not a general replacement for cardinal numbers with every noun.",
    2695: "Standard uses include groups of male people",
    2696: "Use ordinary cardinals with female-person nouns",
    2587: "In oblique number phrases the plural noun normally carries the case required by the construction.",
    2658: "accompaniment/means",
    0: "It does not match the category or whole-phrase meaning",
}
DECIMAL = re.compile(r"(?<=\d)\.(?=\d)")


def whole_sentences(text: str) -> list[str]:
    """Sentences as V2Validator segments them, except that a decimal point (1.5) never ends one (prep 3: rejoin)."""
    masked = DECIMAL.sub("․", text)
    from apply_t17a_unit_16 import sentences
    return [s.replace("․", ".") for s in sentences(masked)]


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
        record_id = f"prov-t18b-u22-{page.lower()}{number}-{objective[8:]}"
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
        record_id = f"prov-t18b-u22-{page.lower()}{number}-{sha(text + objective)[:8]}"
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
        record.update({"itemId": f"prov-t18b-u22-{table.lower()}-{sha(accented + field + objective)[:8]}-{field}", "unitNumber": UNIT,
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
    """The Unit 22 rows of the Units 16-30 sweep CSV. The reviewCopy rows were applied before this task (T16-n2 messages,
    T18a-2 Unit 22 introduction) and the reviewStatus row is met by the D31b label; each is verified, not reapplied."""
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
        old = T17G_CURRENT.get((ref, old), old)  # T17g (column lint) reworded these sweep sentences first
        if ref.endswith(".reviewStatus"):
            log.append({"owner.field": ref, "old": old, "new": "met by the D31b label (not reapplied)"})
            continue
        m = re.fullmatch(r"reviewCopy\.(messages|introductions)\[(\w+)\]", ref)
        if m:
            value = pack["reviewCopy"][m.group(1)][m.group(2)]
            if old in value:
                raise SystemExit(f"{ref}: sweep row not yet applied")
            log.append({"owner.field": ref, "old": old, "new": "already applied before T18b (verified absent)"})
            continue
        owner, field = ref.split(".", 1)
        host = owners[owner]
        m2 = re.fullmatch(r"distractorRationales\[(\d+)\]", field)
        if m2:
            k = int(m2.group(1))
            old_r = host["distractorRationales"][k]
            host["distractorRationales"][k] = edit(old_r, old, new, ref)
            d = (host.get("assessment") or {}).get("distractorDifference") or []
            if k < len(d) and d[k] == old_r:
                d[k] = host["distractorRationales"][k]
            log.append({"owner.field": ref, "old": old, "new": new})
            continue
        if not field.startswith("distractorRationales["):
            host[field] = edit(host[field], old, new, ref)
        # D56e: the assessment metadata keeps its frozen quotations as provenance; feedback rows are not mirrored into it.
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
                raise SystemExit(f"No frozen Unit 22 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    report, missing = {}, []

    def mark(host: dict, texts: list[str], objective: str, owner: str, participants: list[str]) -> None:
        facts, sources = [], {}
        for text in texts:
            for sentence in whole_sentences(text):
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
        out = Path("/tmp/claude-0/-home-user-russian-companion/8476c374-e1ab-5f3d-9af8-fc234c0d6e90/scratchpad/t18b/missing.json")
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
        item_id = f"prov-ca-t18b-{key}"
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
            pieces = sentences(fact)  # as V2Validator segments the marked sentence: a decimal point splits it
            if len(pieces) > 1:
                for k, piece in enumerate(pieces):
                    coverage(unit, objective, f"{owner}-{field}-fact{n}-part{k}", piece)
    print(f"english coverage records: {count}")




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

    # Russian the new teaching shows (nominative forms of two and both; the with-phrase of W:18 has its own record).
    for text in ("два", "оба"):
        b.select("M", 560, text, D if text == "два" else B)
    for text in ("две", "обе"):
        b.select("M", 561, text, D if text == "две" else B)
    # --- teaching ---------------------------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        old = steps[step_id]["body"]
        for a, c in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], a, c, step_id)
        logged(f"{step_id}.body", old, steps[step_id]["body"], "prep 3/D56b/D56c")
    old = steps["u22-st-teach-collective-2"]["body"]
    if "Standard uses include groups of male people" not in old:
        raise SystemExit("u22-st-teach-collective-2 changed")
    steps["u22-st-teach-collective-2"]["body"] = COLLECTIVE_2
    logged("u22-st-teach-collective-2.body", old, COLLECTIVE_2, "D56a")
    for sid, title in TITLES.items():
        logged(f"{sid}.title", steps[sid]["title"], title, "D56a")
        steps[sid]["title"] = title
    # --- items: prompts, D56e cell quotations, the repeated generic sentence ------------------------------------------
    for item_id, prompt in PROMPTS.items():
        logged(f"{item_id}.prompt", items[item_id]["prompt"], prompt, "A2-24 prep 1")
        items[item_id]["prompt"] = prompt
    for item in [i for i in items.values() if i["objectiveId"].startswith(f"obj-u{UNIT}-")]:
        old = item["feedback"]
        item["feedback"] = CELL_QUOTE.sub("", item["feedback"])
        logged(f"{item['id']}.feedback", old, item["feedback"], "D56e")
        for k, rationale in enumerate(item.get("distractorRationales") or []):
            if GENERIC in rationale:
                new = rationale.replace(GENERIC, "")
                logged(f"{item['id']}.distractorRationales[{k}]", rationale, new, "prep 3 REMOVE")
                item["distractorRationales"][k] = new
                d = (item.get("assessment") or {}).get("distractorDifference") or []
                if k < len(d) and d[k] == rationale:
                    d[k] = new
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
            logged(f"{rem[0]['id']}.body", old, rem[0]["body"], "prep 3")
    # Learner-visible text only: the assessment metadata keeps the frozen quotations as provenance (D56e).
    shown = "\n".join([s["body"] for se in unit["sessions"] for s in se["steps"]] + [x for i in items.values()
                       if i["objectiveId"].startswith(f"obj-u{UNIT}-") for x in [i["prompt"], i["feedback"]] + (i.get("distractorRationales") or [])])
    for line, sentence in REMOVED.items():
        if sentence in shown:
            raise SystemExit(f"CSV {line}: removed sentence still shown")
    if "| Дательный" in shown or "| Творительный" in shown or "| Предложный" in shown or 'In context: " ' in shown:
        raise SystemExit("D56e: a raw table-cell quotation is still shown")
    # --- typed checks -------------------------------------------------------------------------------------------------
    typed = []
    for item_id, objective, cue, key, prompt in TYPED:
        cue_text = gapped(b, cue, objective)
        key_id = b.select(key[0], key[1], key[2], objective, "lexical record")
        if R_[key_id]["shippedText"] != key[2] or ACUTE in key[2]:
            raise SystemExit(f"{item_id}: key is not the exact (unstressed) frozen text")
        item = typed_item(item_id, objective, prompt, cue_text, key[2], TYPED_FEEDBACK, SOURCE[key[0]])
        item["keyCitation"] = key_id
        typed.append(item)
    for item in reversed(typed):  # D56c: each objective's typed checks come first in its check list
        objectives[item["objectiveId"]]["checkItemIds"].insert(0, item["id"])
    order = {o: [t["id"] for t in typed if t["objectiveId"] == o] for o in (D, B)}
    for o, ids in order.items():
        if objectives[o]["checkItemIds"][:len(ids)] != ids:
            raise SystemExit(f"{o}: typed checks are not first")
    pack["items"].extend(typed)
    items.update({i["id"]: i for i in typed})
    for item in typed:  # typed keys are taught answers, in the exact frozen spelling
        taught = " ".join(steps[s]["body"] for s in objectives[item["objectiveId"]]["instructionStepIds"])
        if not re.search(f"(?<![{CYR}{ACUTE}]){re.escape(item['answer'])}(?![{CYR}{ACUTE}])", taught):
            raise SystemExit(f"{item['id']}: key {item['answer']} is not taught")
        if item["answer"] in item["prompt"]:
            raise SystemExit(f"{item['id']}: key in prompt")
    for check in pack["contentChecks"]:
        for pin in check["objectives"]:
            if pin["id"] in objectives:
                raise SystemExit(f"{pin['id']} has a content-check pin; this transformer expects none")
    # --- unit fields; checks move to an end-of-unit session ---------------------------------------------------------
    logged("unit-22.topicSummary", unit["topicSummary"], TOPIC, "A2-26")
    logged("unit-22.mappingNote", unit["mappingNote"], MAPPING, "A2-26")
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
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u22-s-book")
    unit["sessions"].insert(book, {"id": "u22-s-checks", "title": "Unit 22 checks", "estimatedMinutes": 25,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})
    # No accepted typed key is displayed in the check session before its own check.
    seen = ""
    for step in check_steps:
        for iid in step["itemIds"]:
            item = items[iid]
            if item.get("typedAnswerKey") and re.search(f"(?<![{CYR}{ACUTE}]){re.escape(item['answer'])}(?![{CYR}{ACUTE}])", seen):
                raise SystemExit(f"{iid}: its key is shown earlier in the check session")
            seen += " " + " ".join([item["prompt"], item.get("promptLetter") or ""] + item["options"])
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
        raise SystemExit("Unit 22 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 22 learner field: Russian only through frozen Unit 22 records ------------------------------------
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
        raise SystemExit(f"Unit 22 regions without a frozen record: {sorted(unshown)}")
    report = mark_facts(b, steps, items)
    cover_english(pack, baseline)
    return report, sweep_log, change_log


OLD_PRACTICE_BODY = ("Read the shown form and the requested category. Use the full explanation and examples above. A missed check"
                     " returns to that complete explanation.")
PRACTICE_BODY = ("Choose the form or meaning asked for, or type the Russian asked for, using the explanations and examples above."
                 " A missed check returns to the complete explanation.")


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T18b baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report, sweep_log, change_log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    out = json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n"
    if "--dry" in sys.argv:
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
    print(f"{len(sweep_log)} Unit 22 sweep rows applied; {len(change_log)} changes; wrote {ASSET.relative_to(ROOT)}"
          f" sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
