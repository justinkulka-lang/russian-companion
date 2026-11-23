#!/usr/bin/env python3
"""T17a (Claude Code, owner direction 2026-11-22): Unit 16 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T17a D34 pack (6d6fd938). Plan: Codex prep audit/prep/units-16-17-prep-at-20bfde5c.txt
(owner/prep-reports), Unit 16 section; the S1 claim verdict (audit/prep/s1-claim-verification.csv, Unit 16 row: SUPPORTED by
Wiktionary под 92761073); the Units 16-30 learner-text sweep (audit/T17a-learner-text-sweep-units-16-30.csv, a copy of
audit/prep/learner-text-sweep-units-16-30.csv at owner/prep-reports, Unit 16 rows, applied first). Russian comes only from frozen
files already in the source inventory: Wikibooks Instrumental 4219602 (I) and Adjectives 4292379 (A); OpenRussian nouns.csv (N),
adjectives.csv (AD) and verbs.csv (V); Wikipedia Russian grammar 1369617627 (G); ru-Wikipedia Морфология 148939075 (MOR); and the S1
page Wiktionary под 92761073 (POD), which becomes a pack source here (D29a admits its revision).
- A2-24: every check moves to an end-of-unit session; u16-i-inst-means-1 and -2 get a third attested sentence and no cue (prep 1).
- A2-23: eight typed checks with keys taught first (prep 2): 20 checked, 8 typed.
- Facts: 66 English-only entries -> MARK, REWORD or REMOVE (prep 3; 1766 SUPPORTED by S1 POD:106, :135, :136).
- A2-26: governance and narration leave Unit 16 learner text; the mapping note follows the Units 1-15 style.
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

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "41224090d55dc376be24c057facde2f1ba51d586e374b38e0e39a3f9343b6242"  # the T17a D34 pack (6d6fd938)
SWEEP_CSV = ROOT / "audit/T17a-learner-text-sweep-units-16-30.csv"
SWEEP_CSV_SHA256 = "0ebf273853d01641275fce68bc396d4374f3fb082cd3d501210a6e84badec96f"
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T17a", "2026-11-22", 16
ACUTE, CYR = "́", "Ѐ-ӿ"
FREEZE = json.loads((SRC / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
S1 = {p["key"]: p for p in FREEZE["pages"] + FREEZE["supplemental"]}
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "I": ("units-16-20/wikibooks/instrumental-4219602.txt", "prov-ex-u16-inst-sg-8-ru", "instrumental:"),
    "A": ("units-6-10/wikibooks/adjectives-4292379.txt", "prov-ex-u16-inst-means-0-ru", "adjectives:"),
    "G": ("units-26-30/wikipedia/russian-grammar-1369617627.txt", "prov-ex-u16-complements-0-ru", "grammar:"),
    "MOR": ("units-21-25/supplemental/morfologiya-148939075.txt", "prov-ex-u22-declension-0-ru", "morfologiya:"),
    "POD": ("s1-intake/wiktionary/pod-92761073.txt", "S1:pod", "wiktionary-pod:"),
}
TABLES = {"N": SRC / "units-3-5/openrussian/nouns.csv", "AD": SRC / "units-6-10/openrussian/adjectives.csv",
          "V": SRC / "units-3-5/openrussian/verbs.csv"}
TABLE_NAME = {"N": "nouns.csv", "AD": "adjectives.csv", "V": "verbs.csv"}
ROWS = {k: list(csv.DictReader(p.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE)) for k, p in TABLES.items()}
CELL_TEMPLATE = {"N": "prov-ex-or-u16-inst-sg-0", "AD": "prov-ex-or-u16-inst-adj-0", "V": "prov-ex-or-u12-future-perf-2"}


MARKUP = re.compile("'''|''|\\[\\[|\\]\\]|\\{\\{|\\}\\}|\\||<[^>]*>")


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 16 covers the instrumental case: singular and plural noun forms, adjective agreement, means and accompaniment,"
         " prepositions that take the instrumental and verbs with instrumental complements, alongside Lesson 16 in your book.")
MAPPING = ("This unit goes with Lesson 16 in your book, printed pages 188-200: the instrumental case, its singular and plural noun"
           " forms, adjective agreement, prepositions that take the instrumental and instrumental complements.")
NOUN_REVIEW = "Review the noun forms above before retrying."  # prep 3, lines 1677-1678
PLURAL_REVIEW = "Compare the singular and plural forms above before retrying."  # prep 3, line 1688
EXAMPLE_REVIEW = "Review these examples before trying again."  # prep 3, line 1771
VERB_REVIEW = "Review the verb meanings above before retrying."  # prep 3, lines 1785-1787, 1797, 1800
SAME_PATTERN = "до́мом has the same ending pattern as столо́м."
CHECK_BODY = "Identify the case and number asked for, then compare the displayed forms. A missed check returns to the full explanation and examples."
TEACH = {  # step: [(old, new)]; new "" deletes the sentence and its following space
    "u16-st-teach-inst-sg-7": [("Do not attach the same ending to every noun. These examples introduce selected singular patterns, not"
                                " the full instrumental paradigm.", NOUN_REVIEW)],
    "u16-st-teach-inst-pl-1": [("Instrumental plural refers to more than one.", PLURAL_REVIEW)],
    # 1668: "table form" is source framing for the widened lint; the same fact names the forms (N:15, N:32 sg_inst).
    "u16-st-teach-inst-sg-4": [("This is the same ending pattern as the table form.", SAME_PATTERN)],
    "u16-st-teach-inst-pl-2": [("The book, house and table forms shown here use -ами.", "кни́гами, дома́ми and стола́ми all use -ами.")],
    "u16-st-teach-inst-prep-3": [("Learn the meaning first; sentence use still requires the appropriate case.", EXAMPLE_REVIEW)],
    "u16-st-teach-complements-1": [("For the first form choose to become.", VERB_REVIEW),
                                   (" The English past-tense word became does not correctly label either infinitive.", "")],
}
OLD_GUIDED = "Identify the requested case and number, then compare the displayed forms. A missed check returns to the full explanation and examples."
BECAME = "became is a past-tense English form, not the meaning of this infinitive."
FEEDBACK = [  # (item, field, old sentence, new sentence); 1797, 1800: a distractor rationale names its competing gloss (V2Validator)
    ("u16-i-complements-1", 1, "became is an English past-tense word and does not correctly label the shown infinitive.", BECAME),
    ("u16-i-complements-2", 1, "became is an English past-tense word and does not correctly label the shown infinitive.", BECAME),
]
# Prep 1 (third option and no cue for u16-i-inst-means-1 and -2) is not applied: V2Validator requires a prompt letter on every
# closed item ("has no prompt letter"), and keeping the cue while adding it as an option would show the cue among the options.
MEANS: dict = {}
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
SG = "Type the instrumental singular form of the shown noun."
PL = "Type the instrumental plural form of the shown noun."
TYPED = [  # (id, objective, cue (table, row, field), key (table, row, field), prompt)
    ("u16-i-type-domom", "obj-u16-inst-sg", ("N", "дом", "sg_nom"), ("N", "дом", "sg_inst"), SG),
    ("u16-i-type-stolom", "obj-u16-inst-sg", ("N", "стол", "sg_nom"), ("N", "стол", "sg_inst"), SG),
    ("u16-i-type-karandashom", "obj-u16-inst-sg", ("I", 27, "карандаш"), ("I", 27, "карандашом"), SG),
    ("u16-i-type-knigami", "obj-u16-inst-pl", ("N", "кни'га", "sg_nom"), ("N", "кни'га", "pl_inst"), PL),
    ("u16-i-type-domami", "obj-u16-inst-pl", ("N", "дом", "sg_nom"), ("N", "дом", "pl_inst"), PL),
    ("u16-i-type-stolami", "obj-u16-inst-pl", ("N", "стол", "sg_nom"), ("N", "стол", "pl_inst"), PL),
    ("u16-i-type-khoroshim", "obj-u16-inst-adj", ("AD", "хоро'ший", "decl_m_nom"), ("AD", "хоро'ший", "decl_m_inst"),
     "Type the masculine instrumental singular form of the shown adjective."),
    ("u16-i-type-khoroshimi", "obj-u16-inst-adj", ("AD", "хоро'ший", "decl_m_nom"), ("AD", "хоро'ший", "decl_pl_inst"),
     "Type the instrumental plural form of the shown adjective."),
]
REFERENCES = [
    {"id": "ref-u16-instrumental-forms", "title": "Instrumental forms",
     "summary": "Instrumental singular and plural nouns, and instrumental adjective endings.",
     "detail": ("Instrumental singular: сто́л — столо́м, до́м — до́мом. Instrumental plural: кни́га — кни́гами, до́м — дома́ми,"
                " сто́л — стола́ми. Adjectives: хоро́ший — хоро́шим (masculine or neuter singular), хоро́шими (plural)."),
     "searchTerms": ["instrumental case", "instrumental plural", "instrumental adjective"],
     "steps": ["u16-st-teach-inst-sg-1", "u16-st-teach-inst-pl-1", "u16-st-teach-inst-adj-1"]},
    {"id": "ref-u16-instrumental-uses", "title": "Uses of the instrumental",
     "summary": "Means, accompaniment, location prepositions and verbs with instrumental complements.",
     "detail": ("Means: Я написал хорошим новым карандашом — I wrote with a good new pencil. Accompaniment: Я хочу встречу с"
                " интересной русской женщиной — I want a meeting with an interesting Russian woman. Location: над — over, под — under,"
                " перед — before, между — between. Verbs with an instrumental complement: стать — to become, остаться — to remain,"
                " казаться — appear to be, оказаться — turn out to be."),
     "searchTerms": ["instrumental uses", "instrumental prepositions", "instrumental complement"],
     "steps": ["u16-st-teach-inst-means-1", "u16-st-teach-inst-prep-1", "u16-st-teach-complements-1"]},
]
BINDER = {"id": "binder-u16-instrumental", "objectiveIds": ["obj-u16-inst-sg", "obj-u16-inst-pl", "obj-u16-inst-adj"],
          "prompt": ("In your binder, write the instrumental singular and plural of this unit's nouns and the instrumental forms of"
                     " хоро́ший. Compare your work with the model answer."),
          "modelAnswer": ("сто́л: столо́м, стола́ми.\nдо́м: до́мом, дома́ми.\nкни́га: кни́гами.\nхоро́ший: хоро́шим (singular),"
                          " хоро́шими (plural).")}
TUTOR = {"id": "tutor-u16-instrumental",
         "bring": "Your binder instrumental forms and the three example sentences, to read aloud and say whether each describes a tool, a companion or a place."}

# --------------------------------------------------------------------------------------------------------------
# Facts: English-only sentences and their evidence. (page, n) = a page line record, (table, row, field) = an OpenRussian cell,
# ("SENSE", table, row, sense) = one delimited sense of a translations_en cell.
# --------------------------------------------------------------------------------------------------------------
QUOTED = re.compile("\"([^\"]+)\"|“([^”]+)”|‘((?:[^’]|(?<=[A-Za-z])’(?=[A-Za-z]))+)’|"
                    "«([^»]+)»|(?<![A-Za-z])'((?:[^']|(?<=[A-Za-z])'(?=[A-Za-z]))+)'(?![A-Za-z])")
ENGLISH_SENTENCE = re.compile(r"[^.!?\n]+[.!?]*")
FACT_FORM = re.compile(f"[{CYR}][{CYR}́]*")


def sentences(text: str) -> list[str]:
    masked = list(text)
    for quote in QUOTED.finditer(text):
        for index in range(quote.start(), quote.end()):
            if masked[index] in ".!?\n":
                masked[index] = "_"
    return [text[m.start():m.end()].strip() for m in ENGLISH_SENTENCE.finditer("".join(masked)) if text[m.start():m.end()].strip()]


def forms(text: str) -> set[str]:
    return {m.group(0).lower() for m in FACT_FORM.finditer(stressed(text))}


NONFACTS = {
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    NOUN_REVIEW, PLURAL_REVIEW, EXAMPLE_REVIEW, VERB_REVIEW,
    "Learn its instrumental forms first.",
    "Identify both the pencil meaning and the instrumental case.",
    "Distinguish the pencil meaning from train and husband.",
    "Identify both the noun and its case.",
    "Learn the displayed forms; practise forming other nouns separately.",
    "Choose the masculine instrumental form, rather than an English translation.",
    "Learn these forms, then practise adjective agreement in sentences separately.",
    "Identify location rather than treating every instrumental phrase as a means phrase.",
    "Read what the phrase contributes to the event.",
    "Learn the meanings of the isolated prepositions; practise choosing noun endings separately.",
    "Use a complete movement context when choosing the case after these prepositions.",
    "Compare the specific spatial meaning, including the alternative behind.",
    "Learn the meanings of these verbs.",
    "Learn each verb’s meaning, then practise complete sentences with instrumental complements separately.",
    "Keep these meanings distinct from becoming and remaining.",
    "Learn the stress with the whole form.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


def c(table: str, row: str, *fields: str) -> list:
    return [(table, row, f) for f in fields]


def sense(table: str, row: str, text: str) -> tuple:
    return ("SENSE", table, row, text)


BECOME, REMAIN, TURN_OUT, SEEM = sense("V", "стать", "become"), sense("V", "оста'ться", "remain"), \
    sense("V", "оказа'ться", "to turn out to be"), sense("V", "каза'ться", "seem")
MEANS_A, MEET_A, BETWEEN_A = p("A", 67), p("A", 69), p("A", 81)
EXTRA: dict[str, list] = {  # prep 3 evidence, by inventory line (sweep wording where a sweep row rewrote the sentence)
    # 1656-1674: instrumental meaning and noun contrasts (I:4, 25-28; N:15, 32)
    "The instrumental case can mark the means used to do something.": p("I", 4),
    "The word still names a table: the ending marks its grammatical role, not a new vocabulary meaning.":
        p("I", 4) + c("N", "стол", "sg_nom", "sg_inst") + [sense("N", "стол", "table")],
    "Both belong to the same noun, but neither has the instrumental singular ending.": c("N", "стол", "sg_dat", "sg_prep", "sg_inst"),
    "These forms are not instrumental singular.": c("N", "дом", "sg_dat", "sg_prep", "sg_inst"),
    # 1694-1729: endings, case, number and agreement (I:51-53, 76-78; N:15, 32, 60; AD:18; G:249)
    "This is a noun ending; adjectives have their own agreement endings.": p("I", 51, 76, 78),
    "Neither is instrumental plural.": c("N", "кни'га", "pl_dat", "pl_prep", "pl_inst") + c("N", "стол", "pl_dat", "sg_inst", "pl_inst"),
    "The first is not instrumental; the second is instrumental singular rather than plural.": c("N", "дом", "pl_dat", "sg_inst", "pl_inst"),
    "An adjective agrees with its noun in case and number, and in gender in the singular.": p("G", 249) + p("I", 76),
    "These are adjective declension endings.": p("I", 76, 78) + p("A", 67),
    "Neither is the masculine instrumental singular form.": c("AD", "хоро'ший", "decl_m_dat", "decl_pl_nom", "decl_m_inst"),
    # 1740-1755: tool, accompaniment and location (A:67, 69, 81; I:14, 17)
    "The pencil is the tool used for writing: this is the means instrumental, without a preposition before the tool phrase.":
        MEANS_A + p("I", 4, 14),
    "Here with introduces the person involved in the meeting: this is the accompaniment line, using a preposition followed by"
    " instrumental forms.": MEET_A + p("I", 13, 14),
    "For a means instrumental request, choose the writing sentence because its pencil is the instrument.": MEANS_A + p("I", 14),
    "For an accompaniment line request, choose the meeting sentence because it describes being with a person.": MEET_A + p("I", 14),
    "English with can express both roles; its presence alone does not decide which Russian construction is used.": p("I", 14),
    "This is the between line: the phrase locates the speaker relative to two houses.": BETWEEN_A + p("I", 17),
    "The houses are neither a writing tool nor a companion.": BETWEEN_A + MEANS_A + MEET_A,
    "The accompaniment line describes a meeting with a person, not an instrument used to perform an action.": MEET_A + p("I", 14),
    "The sentence about living between two houses describes location, not an accompaniment line.": BETWEEN_A + p("I", 17),
    "The means instrumental in the writing sentence names the pencil used as the tool.": MEANS_A + p("I", 4),
    "The sentence about living between two houses describes location, not a means instrumental naming a tool.": BETWEEN_A + p("I", 17),
    "The meeting sentence is an accompaniment line describing a person involved in a meeting, not a tool.": MEET_A + p("I", 14),
    "The between line locates the speaker relative to two houses.": BETWEEN_A + p("I", 17),
    "The meeting sentence is an accompaniment line describing a person involved in a meeting, not location between houses.":
        MEET_A + BETWEEN_A,
    "The writing sentence names the pencil used as a tool, not a location between houses.": MEANS_A + BETWEEN_A,
    # 1756-1777: preposition meanings and case distinctions (I:13-18), without the source's blanket motion rule
    "The over label identifies the meaning of the shown preposition: above something.": p("I", 15),
    "The under label places something below, not above.": p("I", 15),
    "The before label describes being in front of, not above.": p("I", 15, 16),
    "The under label identifies the meaning of the shown preposition: below something.": p("I", 15),
    "The over label places something above, not below.": p("I", 15),
    "The behind label places something at the back, not below.": p("I", 15, 18),
    "The before label identifies the spatial meaning of the shown preposition: in front of something.": p("I", 16),
    "The over label places something above, not in front of.": p("I", 15, 16),
    "The between label places something relative to two things, not in front of.": p("I", 16, 17),
    "These spatial meanings contrast vertical positions.": p("I", 15),
    "The over label belongs to the first preposition; the under label belongs to the second.": p("I", 15),
    "In a location construction, both can govern instrumental forms.": p("I", 13, 15),
    "The same preposition can govern different cases in different uses.": p("I", 15, 18),
    "For the preposition meaning under, instrumental describes location, while accusative can describe a destination under"
    " something.": p("I", 15) + p("POD", 106, 135),
    # 1766: S1 SUPPORTED (Wiktionary под 92761073: accusative for movement to beneath; instrumental for being beneath, including
    # swimming underwater)
    "Movement within a location does not by itself require accusative: distinguish where something is from where it is going.":
        p("POD", 106, 135, 136),
    "In the spatial use introduced here, before means in front of; it is not a universal translation for every English time"
    " expression with before.": p("I", 16),
    "Between places something relative to two things.": p("I", 17),
    # 1779-1801: verb meanings, infinitives and connective-verb complements (V:23, 28, 32, 14828; G:42-48; MOR:838)
    "Both are infinitives: they name an action or state without marking a past event.":
        p("MOR", 838) + c("V", "стать", "accented") + c("V", "оста'ться", "accented"),
    "Becoming expresses a change; remaining expresses continuation.": [BECOME, REMAIN],
    "The second form means remain.": [REMAIN],
    "An instrumental complement can describe the role or state of the subject after a linking verb, rather than an instrument"
    " used to carry out an action.": p("G", 42, 43, 45, 48),
    "The verbs shown in this lesson can occur in that construction.": p("G", 48),
    "The first concerns how something seems; the second concerns what it proves to be.": [SEEM, TURN_OUT],
    "The second form means turn out to be, rather than the meaning of continuation.": [TURN_OUT, REMAIN],
    "A displayed infinitive still does not specify a past event.": p("MOR", 838),
    "to remain expresses continuation, not a change of state.": [REMAIN, BECOME],
    "to turn out to be expresses what something proves to be, not becoming.": [TURN_OUT, BECOME],
    "It belongs to the connective verbs introduced here.": p("G", 48),
    "turn out to be expresses what something proves to be, not continuation.": [TURN_OUT, REMAIN],
    "remain expresses continuation of a state, not what something proves to be.": [REMAIN, TURN_OUT],
}
# Sentences that name Russian forms and are also bound to the dictionary row they quote (prep 4: стать — to become, not became).
EXTRA[BECAME] = p("G", 48) + p("MOR", 838) + [BECOME]
EXTRA["стать means to become."] = c("V", "стать", "accented") + [BECOME]
EXTRA["остаться means to remain."] = c("V", "оста'ться", "accented") + [REMAIN]


def edit(text: str, old: str, new: str, where: str) -> str:
    if text.count(old) != 1:
        raise SystemExit(f"{where}: baseline text changed ({old!r})")
    return text.replace(old, new)


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
        base = next(r for r in self.pack["provenanceRecords"] if r["sourceId"] == "src-enwiktionary-nekogo-80719285")
        record = copy.deepcopy(base)
        record.update({"sourceId": s1["sourceId"], "sourceRevision": s1["oldid"], "attribution": s1["attribution"],
                       "sourceUrl": s1["url"]})
        return record

    def _record(self, page: str, raw: str, record_id: str, objective: str) -> str:
        if record_id in self.records:
            return record_id
        rel, _, prefix = PAGES[page]
        if (SRC / rel).read_text(encoding="utf-8").count(raw) != 1:
            raise SystemExit(f"{page}: span is not unique on its page ({raw[:60]!r})")
        record = self.template(page)
        # Units 16-30 provenance ships no MediaWiki markup: the record keeps the whole line as its original text and ships the
        # longest markup-free stretch of it (a contiguous selection).
        shipped = max((c.strip(" *:#-") for c in MARKUP.split(raw) if re.search("[A-Za-z\u0400-\u04ff]", c)), key=len)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": prefix + raw[:80],
                       "originalSourceText": raw, "shippedText": shipped, "sourceHash": sha(raw), "contentHash": sha(shipped),
                       "recordType": "instruction", "transformation": "selection"})
        record.pop("originalSourceTextRef", None)
        return self.add(record)

    def line(self, page: str, number: int, objective: str) -> str:
        raw = (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()
        return self._record(page, raw, f"prov-t17a-u16-{page.lower()}{number}", objective)

    def select(self, page: str, number: int, text: str, objective: str, record_type: str = "example") -> str:
        raw = (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()
        if text not in d7b.displayed(raw):
            raise SystemExit(f"{text!r} is not displayed on {page}:{number}")
        record_id = f"prov-t17a-u16-{page.lower()}{number}-{sha(text + objective)[:8]}"
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
        transformation = "formatting"
        if field == "translations_en":
            shipped, transformation = original, "selection"
        record.update({"itemId": f"prov-t17a-u16-{table.lower()}-{sha(accented + field + objective)[:8]}-{field}", "unitNumber": UNIT,
                       "objectiveId": objective, "sourceRecordId": source_id, "originalSourceText": original,
                       "shippedText": shipped, "sourceHash": sha(original), "contentHash": sha(shipped),
                       "recordType": "lexical record", "transformation": transformation})
        return self.add(record)

    def sense(self, table: str, accented: str, sense: str, objective: str) -> str:
        """One delimited sense of an OpenRussian translations_en cell."""
        hits = [r for r in ROWS[table] if r["accented"] == accented]
        original = hits[0]["translations_en"].strip()
        if sense not in [s.strip() for s in re.split(r"\s*[,;]\s*", original)]:
            raise SystemExit(f"{sense!r} is not a delimited sense of {table}:{accented}")
        record_id = f"prov-t17a-u16-{table.lower()}-{sha(accented + sense)[:8]}-sense"
        if record_id in self.records:
            return record_id
        record = copy.deepcopy(self.records[CELL_TEMPLATE[table]])
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective,
                       "sourceRecordId": f"{TABLE_NAME[table]}:{accented}#translations_en", "originalSourceText": original,
                       "shippedText": sense, "sourceHash": sha(original), "contentHash": sha(sense),
                       "recordType": "lexical record", "transformation": "sense selection"})
        return self.add(record)


def admit_s1(pack: dict) -> None:
    """S1 froze Wiktionary под 92761073; D29a admits its revision."""
    page = S1["pod"]
    data = (SRC / "s1-intake" / page["file"]).read_bytes()
    if hashlib.sha256(data).hexdigest() != page["sha256"]:
        raise SystemExit("pod does not match its FREEZE.json entry")
    if any(s["id"] == page["sourceId"] for s in pack["sources"]):
        raise SystemExit("pod is already a pack source")
    template = next(s for s in pack["sources"] if s["id"] == "src-enwiktionary-nekogo-80719285")
    pack["sources"].append({**copy.deepcopy(template), "id": page["sourceId"], "title": "под", "attribution": page["attribution"]})
    rights = next(r for r in pack["rights"] if r["sourceId"] == "src-enwiktionary-nekogo-80719285")
    pack["rights"].append({**copy.deepcopy(rights), "id": "rights-" + page["sourceId"][4:], "sourceId": page["sourceId"]})


ATTRIBUTION = [("OpenRussian dictionary data is frozen at commit", "OpenRussian dictionary data is pinned at commit"),
               ("Wikibooks excerpts are frozen by oldid", "Wikibooks excerpts are pinned by oldid"),
               ("Wikipedia grammar spans are frozen at oldid", "Wikipedia grammar spans are pinned at oldid"),
               ("Proposed Units 16–20 adapt", "Units 16–20 adapt")]


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
    # --- the Units 16-20 attribution notice (shared with Units 18, 19 and 20): governance wording only ----------------
    notice = next(x for x in pack["references"] if x["id"] == "ref-u1620-attribution")
    for old, new in ATTRIBUTION:
        field = "summary" if old in notice["summary"] else "detail"
        notice[field] = edit(notice[field], old, new, "ref-u1620-attribution")
    # --- teaching ---------------------------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], old, new, step_id)
    for step in steps.values():
        if step["kind"] in ("GUIDED", "CHECK") and step["body"] == OLD_GUIDED:
            step["body"] = CHECK_BODY
    # --- remediation repeats each session's whole teaching ---------------------------------------------------------
    for se in unit["sessions"]:
        for s in se["steps"]:
            if s["kind"] == "TEACH":
                s["body"] = s["body"].strip()
        rem = [s for s in se["steps"] if s["kind"] == "REMEDIATE"]
        if rem:
            rem[0]["body"] = "\n\n".join(s["body"] for s in se["steps"] if s["kind"] == "TEACH")
            rem[0]["exampleIds"] = list(dict.fromkeys(x for s in se["steps"] if s["kind"] == "TEACH" for x in s.get("exampleIds") or []))
    # --- items ----------------------------------------------------------------------------------------------------
    for item_id, index, old, new in FEEDBACK:
        item = items[item_id]
        item["distractorRationales"][index] = edit(item["distractorRationales"][index], old, new, item_id)
        diff = item["assessment"].get("distractorDifference") or []
        if index < len(diff):
            diff[index] = edit(diff[index], old, new, item_id)
    for item_id, spec in MEANS.items():
        item, a = items[item_id], items[item_id]["assessment"]
        added = R[spec["add"]]["shippedText"]
        if item.get("promptLetter") != added:
            raise SystemExit(f"{item_id}: baseline cue changed")
        item["promptLetter"] = None
        item["options"].append(added)
        item["distractorRationales"].append(spec["rationale"])
        a["distractorSourceIds"].append(spec["add"])
        a["distractorDifference"].append(spec["rationale"])
        a["contrastMembers"].append(added)
        a["relationParticipantIds"] = [x for x in a.get("relationParticipantIds", []) if x != spec["add"]] + [spec["add"]]
        if "prompt" in spec:
            item["prompt"] = spec["prompt"]
    typed = []
    for item_id, objective, cue, key, prompt in TYPED:
        if cue[0] == "I":
            cue_id, key_id = b.select("I", cue[1], cue[2], objective, "lexical record"), b.select("I", key[1], key[2], objective, "lexical record")
            source = "src-wikibooks-russian-cc-by-sa"
        else:
            cue_id, key_id = b.cell(*cue, objective), b.cell(*key, objective)
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
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u16-s-book")
    unit["sessions"].insert(book, {"id": "u16-s-checks", "title": "Unit 16 checks", "estimatedMinutes": 25,
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
        raise SystemExit("Unit 16 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 16 learner field: Russian only through frozen Unit 16 records ------------------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and d7b.frozen_record(pack, r)}
    texts = [s["body"] for s in steps.values()] + [unit["topicSummary"], unit["mappingNote"], BINDER["prompt"], BINDER["modelAnswer"]]
    texts += [r[k] for r in pack["references"] if r["id"] in {s["id"] for s in REFERENCES} for k in ("title", "summary", "detail")]
    texts += [e["display"] for e in pack["examples"] if e.get("objectiveId", "").startswith(f"obj-u{UNIT}-")]
    for item in items.values():
        if item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            texts += [item["prompt"], item.get("promptLetter") or "", item["answer"], item["feedback"]] + item["options"] + item.get("distractorRationales", [])
    for text in texts:
        for m in re.finditer("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*", text):
            region = m.group(0).strip()
            if region not in frozen and region.rstrip("?!.,") not in frozen:
                raise SystemExit(f"Unit 16 region without a frozen record: {region!r} in {text[:80]!r}")
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
                raise SystemExit(f"No frozen Unit 16 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    def resolve(ref, objective: str) -> str:
        if ref[0] == "SENSE":
            return b.sense(ref[1], ref[2], ref[3], objective)
        if len(ref) == 2:
            return b.line(ref[0], ref[1], objective)
        return b.cell(ref[0], ref[1], ref[2], objective)

    report, missing = {}, []

    def mark(host: dict, texts: list[str], objective: str, owner: str, participants: list[str]) -> None:
        facts, sources = [], {}
        for text in texts:
            for sentence in sentences(text):
                if not any(ch.isalpha() for ch in sentence) or sentence in NONFACTS:
                    continue
                refs = EXTRA.get(sentence)
                if refs is None and not forms(sentence):
                    missing.append((owner, sentence))
                    continue
                ids = [resolve(r, objective) for r in refs or []] + cite_forms(sentence, objective, participants)
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
        Path("/tmp/claude-0/-home-user-russian-companion/8476c374-e1ab-5f3d-9af8-fc234c0d6e90/scratchpad/t17a/missing.json").write_text(
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
        item_id = f"prov-ca-t17a-{key}"
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
        if old is None or old[2] != text:
            coverage(unit, objective, f"{owner}-{field}", text)
    for (owner, field), (unit, objective, text, facts) in sorted(hosts.items()):
        for n, fact in enumerate(facts):
            coverage(unit, objective, f"{owner}-{field}-fact{n}", fact)
    print(f"english coverage records: {count}")


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T17a baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report, sweep_log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    if "--dry" in sys.argv:
        print("dry run; asset not written")
        return
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps({"facts": report, "sweep": sweep_log}, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(sweep_log)} Unit 16 sweep rows applied; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
