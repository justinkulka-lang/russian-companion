#!/usr/bin/env python3
"""T17d (Claude Code, owner direction 2026-11-23): Unit 19 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T17d D2 pack (fecc7ab4; D2 changes the validator only, so the asset is the T17c DONE
pack). Plan: Codex prep audit/prep/unit-19-prep-at-20bfde5c.txt (owner/prep-reports); owner decision D2; the S1 claim verdicts
(audit/prep/s1-claim-verification.csv, Unit 19 rows: 2225 SUPPORTED by ru Сослагательное наклонение 140019162 line 18; 2234
PARTIAL, Wiktionary бы 94001807 line 58 says sentence, not clause); the Units 16-30 learner-text sweep
(audit/T17a-learner-text-sweep-units-16-30.csv, Unit 19 rows, applied first). Russian comes only from frozen files already in the
source inventory: Wikipedia Russian grammar 1369617627 (G), Wikibooks Verbal aspect 4059032 (VA) and Lesson 3 4460699 (L),
Wiktionary должен 84965088 (W), ru Морфология 148939075 (MOR), OpenRussian verbs.csv (V) and nouns.csv (N), and the S1 pages
Сослагательное наклонение (SOS) and Wiktionary бы (BY), which become pack sources here.
- A2-24: every check moves to an end-of-unit session; answer-selection scripts leave teaching; the obligation checks ask which
  subject matches the agreement form (feminine, neuter, plural; L:723); the probability checks get a third, frozen meaning
  (probability-1 by D2: W:38 and W:35 translations; probability-2: obligation, likelihood, indebtedness from W:33-41).
- A2-23: twelve typed checks with keys taught first (prep 2): 20 checked, 12 typed; two typed guided items.
- Facts: 67 English-only entries -> MARK, REWORD or REMOVE (prep 3, S1 verdicts).
- A2-26: governance and narration leave Unit 19 learner text; the mapping note follows the Units 1-18 style.
- A2-36 references, binder and tutor tasks. ref-u1620-attribution (Units 16, 18, 19, 20) is not edited.
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
BASELINE_SHA256 = "4cb05db89a08aa80c0e1f31b56642414185182cf41ea09c3ea63e1de2a7c5ff4"  # the T17c DONE pack (ac0b7766), unchanged by D2 (fecc7ab4)
SWEEP_CSV = ROOT / "audit/T17a-learner-text-sweep-units-16-30.csv"
SWEEP_CSV_SHA256 = "0ebf273853d01641275fce68bc396d4374f3fb082cd3d501210a6e84badec96f"
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T17d", "2026-11-23", 19
PREFIX = "prov-t17d-u19"
FREEZE = json.loads((SRC / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
S1 = {p["key"]: p for p in FREEZE["pages"] + FREEZE["supplemental"]}
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "G": ("units-26-30/wikipedia/russian-grammar-1369617627.txt", "prov-ex-u19-conditional-0-ru", "grammar:"),
    "VA": ("units-11-15/wikibooks/verbal-aspect-4059032.txt", "prov-ex-u19-prefix-caution-0-ru", "aspect:"),
    "L": ("units-16-20/wikibooks/lesson3-4460699.txt", "prov-ex-u19-obligation-0-ru", "lesson3:"),
    "W": ("units-16-20/wiktionary/dolzhen-84965088.txt", "prov-ex-u19-probability-1-en", "wiktionary-dolzhen:"),
    "MOR": ("units-21-25/supplemental/morfologiya-148939075.txt", "prov-ex-u22-declension-0-ru", "morfologiya:"),
    "SOS": ("s1-intake/ruwiki/soslagatelnoe-naklonenie-140019162.txt", "S1:soslagatelnoe", "ruwiki-soslagatelnoe:"),
    "BY": ("s1-intake/wiktionary/by-94001807.txt", "S1:by", "wiktionary-by:"),
}
S1_TEMPLATE = {"soslagatelnoe": "prov-ex-u22-declension-0-ru", "by": "prov-ex-u19-probability-1-en"}
S1_SOURCE_TEMPLATE = {"soslagatelnoe": "src-ruwiki-morfologiya-148939075", "by": "src-enwiktionary-pod-92761073"}
SOURCE = {"G": "src-wikipedia-russian-grammar-1369617627", "VA": "src-wikibooks-russian-cc-by-sa", "L": "src-wikibooks-russian-cc-by-sa",
          "W": "src-enwiktionary-dolzhen-84965088"}
TABLES = {"N": SRC / "units-3-5/openrussian/nouns.csv", "V": SRC / "units-3-5/openrussian/verbs.csv"}
TABLE_NAME = {"N": "nouns.csv", "V": "verbs.csv"}
ROWS = {k: list(csv.DictReader(p.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE)) for k, p in TABLES.items()}
CELL_TEMPLATE = {"N": "prov-ex-or-u16-inst-sg-0", "V": "prov-ex-or-u12-future-perf-2"}
P, CO, O, PR = "obj-u19-prefix-caution", "obj-u19-conditional", "obj-u19-obligation", "obj-u19-probability"
W_BLOCK = (33, 41)  # the indebtedness, obligation and likelihood senses of должен with their examples (W:33-41)

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 19 covers prefixed verbs and their aspect partners, conditional forms with the particle for would, obligation"
         " forms, and the senses of the adjective for must, alongside Lesson 19 in your book.")
MAPPING = ("This unit goes with Lesson 19 in your book, printed pages 230-239: conditional constructions, obligation or probability,"
           " and productive meanings of selected verbal prefixes.")
VERB_PRACTICE = "Practise writing each verb above from its English meaning and its aspect partner."
CONDITIONAL_PRACTICE = "Practise writing each of the four conditional forms for a male and a female speaker."
OBLIGATION_PRACTICE = "Practise writing the form for each kind of subject."
AGREEMENT_REVIEW = "Review the agreement examples before retrying."  # prep 3, line 2227
PRACTICE_BODY = ("Select the answer for a shown form, or type the Russian asked for, using the explanations above. A missed check"
                 " returns to the complete explanation and examples.")
OBLIGATION_PROMPT = "Which English subject description matches the shown agreement form?"
SENSE_PROMPT = "Which English word names what the adjective expresses in this sentence?"
TEACH = {  # step: [(old, new)]; new "" deletes the sentence and its leading space
    "u19-st-teach-prefix-caution-1": [
        ("договориться — to agree.", "договориться — to agree; its imperfective partner is договариваться."),
    ],
    "u19-st-teach-prefix-caution-2": [
        (" For the perfective partner of отговаривать, select отговорить.", ""),  # prep 1: answer-selection script
        ("сказать belongs to a separate speaking pair;", "сказать belongs to a separate speaking pair with говорить;"),
    ],
    "u19-st-teach-prefix-caution-3": [
        ("When it asks for an aspect partner,", "When finding an aspect partner,"),  # the sweep reworded the previous sentence
        ("написать — to write. сделать — to do.",
         "написать — to write, the perfective of писать. сделать — to do, the perfective of делать."),
        ("Learn the prefixed verbs shown here;", VERB_PRACTICE + " Learn the prefixed verbs shown here;"),
    ],
    "u19-st-teach-conditional-2": [
        (" For the first choose a male speaker; for the second choose a female speaker.", " " + AGREEMENT_REVIEW),  # prep 3, 2227
        (" Speaker agreement and sentence meaning are different questions.", ""),  # prep 3, 2229 REMOVE
    ],
    "u19-st-teach-conditional-3": [
        ("It cannot begin its clause.", "The particle cannot begin a sentence."),  # S1 PARTIAL (BY:58): sentence, not clause
        ("whereas the earlier examples put it after the verb.",
         "whereas the earlier examples put it after the verb. They also use поспа́л rather than спал, so the two constructions"
         " are not the same words in a different order."),  # prep 4
        ("Learn these conditional examples,", CONDITIONAL_PRACTICE + " Learn these conditional examples,"),
    ],
    "u19-st-teach-obligation-2": [
        (" Select the feminine label for the feminine form and the plural label for the plural form.", ""),  # answer-selection script
    ],
    "u19-st-teach-obligation-3": [
        ("Learn these obligation forms,", OBLIGATION_PRACTICE + " Learn these obligation forms,"),
    ],
    "u19-st-teach-probability-2": [
        ("Learn these two examples,", "Learn these examples,"),  # a third example joins the two
        ("The English label an obligation describes a required action; an expected event describes a prediction such as the eclipse.",
         "Он до́лжен мне пять ты́сяч рубле́й. — He owes me five thousand rubles. Obligation describes a required action,"
         " likelihood an expected event such as the eclipse, and indebtedness owing something to someone."),
    ],
}
OBLIGATION_ITEMS = {  # item: (answer record, [distractor records], rationales)
    "u19-i-obligation-1": ("prov-ex-u19-obligation-1-en", ["prov-ex-u19-obligation-2-en", "prov-ex-u19-obligation-3-en"],
                           ["If the subject is neuter calls for a different form of the adjective.",
                            "If the subject is plural calls for a different form of the adjective."]),
    "u19-i-obligation-2": ("prov-ex-u19-obligation-3-en", ["prov-ex-u19-obligation-1-en", "prov-ex-u19-obligation-2-en"],
                           ["If the subject is feminine calls for a different form of the adjective.",
                            "If the subject is neuter calls for a different form of the adjective."]),
}
PROBABILITY_1_RATIONALES = [
    "Employees must report to work on time. translates the employee obligation sentence, not the eclipse sentence.",
    "He owes me five thousand rubles. translates a sentence about owing money, not the eclipse sentence.",
]
PROBABILITY_2 = {
    "feedback": "Слу́жащие должны́ во́время явля́ться на рабо́ту. expresses obligation: the employees must report on time.",
    "rationales": ["likelihood describes an expected event such as the eclipse, not the required employee action.",
                   "indebtedness describes owing something, not the required employee action."],
}
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
COND = "Type the conditional a {} speaker uses, with the particle {} the verb."
OBL = "Type the obligation form used with a {} subject."
TYPED = [  # (id, objective, (page, line, cue), (page, line, key), prompt)
    ("u19-i-type-otgovorit", P, ("VA", 25, "отговаривать"), ("VA", 25, "отговорить"), "Type the perfective verb that means to dissuade."),
    ("u19-i-type-otgovarivat", P, ("VA", 25, "отговорить"), ("VA", 25, "отговаривать"),
     "Type the imperfective partner of the verb that means to dissuade."),
    ("u19-i-type-dogovoritsya", P, ("VA", 25, "договариваться"), ("VA", 25, "договориться"), "Type the perfective verb that means to agree."),
    ("u19-i-type-napisat", P, ("VA", 23, "писать"), ("VA", 23, "написать"), "Type the perfective verb that means to write."),
    ("u19-i-type-sdelat", P, ("VA", 23, "делать"), ("VA", 23, "сделать"), "Type the perfective verb that means to do."),
    ("u19-i-type-spal-by", CO, ("G", 1215, "я спала́ бы"), ("G", 1215, "я спал бы"), COND.format("male", "after")),
    ("u19-i-type-spala-by", CO, ("G", 1215, "я спал бы"), ("G", 1215, "я спала́ бы"), COND.format("female", "after")),
    ("u19-i-type-by-pospal", CO, ("G", 1215, "я бы поспала́"), ("G", 1215, "я бы поспа́л"), COND.format("male", "before")),
    ("u19-i-type-by-pospala", CO, ("G", 1215, "я бы поспа́л"), ("G", 1215, "я бы поспала́"), COND.format("female", "before")),
    ("u19-i-type-dolzhna", O, ("L", 723, "я должен"), ("L", 723, "должна"), OBL.format("feminine")),
    ("u19-i-type-dolzhno", O, ("L", 723, "должна"), ("L", 723, "должно"), OBL.format("neuter")),
    ("u19-i-type-dolzhny", O, ("L", 723, "должно"), ("L", 723, "должны"), OBL.format("plural")),
]
GUIDED = [  # practice before the checks; the keys differ from every check key
    ("u19-g-type-skazat", P, "u19-st-guided-prefix-caution-1", ("VA", 26, "говорить"), ("VA", 26, "сказать"),
     "Practice: type the perfective verb paired with the shown imperfective in this unit."),
    ("u19-g-type-dolzhen", O, "u19-st-guided-obligation-1", ("L", 723, "должны"), ("L", 723, "я должен"),
     "Practice: type the phrase that means I have to, for a masculine subject."),
]
REFERENCES = [
    {"id": "ref-u19-prefixes-aspect", "title": "Prefixed verbs and aspect partners",
     "summary": "Verbs whose prefix changes the meaning, and their aspect partners.",
     "detail": ("отговорить — to dissuade, imperfective partner отговаривать. договориться — to agree, imperfective partner"
                " договариваться. написать — to write, the perfective of писать. сделать — to do, the perfective of делать. A prefix"
                " can form a perfective, and other prefixes can make a whole new verb with its own pair."),
     "searchTerms": ["verbal prefixes", "aspect partners", "perfective"],
     "steps": ["u19-st-teach-prefix-caution-1", "u19-st-teach-prefix-caution-2", "u19-st-teach-prefix-caution-3"]},
    {"id": "ref-u19-conditional-obligation", "title": "Conditional and obligation forms",
     "summary": "The conditional with the particle for would, and the forms for obligation and likelihood.",
     "detail": ("A male speaker says я спал бы or я бы поспа́л; a female speaker says я спала́ бы or я бы поспала́. я должен — I have"
                " to, with должна for a feminine subject, должно for a neuter subject and должны for a plural subject. Затме́ние"
                " должно́ наступи́ть в три часа́. — The eclipse should occur at three o'clock. Слу́жащие должны́ во́время явля́ться"
                " на рабо́ту. — Employees must report to work on time."),
     "searchTerms": ["conditional", "would", "must", "obligation"],
     "steps": ["u19-st-teach-conditional-1", "u19-st-teach-obligation-1", "u19-st-teach-probability-1"]},
]
ATTRIBUTION = {
    "ref-u19-wiktionary-attribution": {
        "title": "Unit 19 source notice",
        "summary": "Unit 19 adapts two English Wiktionary entries and one Russian Wikipedia article.",
        "detail": ("The senses and examples of the adjective for must come from English Wiktionary oldid 84965088, and the note on"
                   " the conditional particle from English Wiktionary oldid 94001807, under CC BY-SA 4.0 via Wiktionary:Copyrights"
                   " oldid 87545419. Entry text is dual-licensed CC BY-SA 4.0 and GFDL; this unit uses CC BY-SA 4.0. The note on"
                   " time reference in conditional forms comes from the Russian Wikipedia article on the subjunctive mood, oldid"
                   " 140019162, under CC BY-SA 4.0 via its copyright page oldid 154562152. These license routes are not Wikibooks,"
                   " Pressbooks or OpenRussian routes. Audio from the entries is not used."),
        "searchTerms": ["unit 19 source notice", "unit 19 wiktionary notice"],
        "steps": ["u19-st-teach-conditional-1", "u19-st-teach-conditional-3"]},
}
BINDER = {"id": "binder-u19-forms", "objectiveIds": [P, CO, O],
          "prompt": ("In your binder, write the five verbs with their meanings and partners, the four conditional forms, and the four"
                     " obligation forms with the subject each goes with. Compare your work with the model answer."),
          "modelAnswer": ("отговорить — to dissuade (отговаривать); договориться — to agree (договариваться); написать — to write"
                          " (писать); сделать — to do (делать).\n"
                          "я спал бы — a male speaker, particle after the verb. я бы поспа́л — a male speaker, particle before the"
                          " verb. я спала́ бы — a female speaker, particle after the verb. я бы поспала́ — a female speaker, particle"
                          " before the verb.\n"
                          "я должен — I have to; должна — feminine subject; должно — neuter subject; должны — plural subject.")}
TUTOR = {"id": "tutor-u19-conditional",
         "bring": "Your binder verbs, conditional forms and obligation forms, to read aloud and say what each one means."}

# --------------------------------------------------------------------------------------------------------------
# Facts. (page, n) = a page line record, (table, accented, field) = an OpenRussian cell.
# --------------------------------------------------------------------------------------------------------------
NONFACTS = {
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    VERB_PRACTICE, CONDITIONAL_PRACTICE, OBLIGATION_PRACTICE, AGREEMENT_REVIEW, PRACTICE_BODY.split(". ")[0] + ".",
    # Reviewed instructions (sweep wording and operating directions); they state no Russian fact.
    "Learn these two verbs as an aspect pair.",
    "Translate the verb when identifying its meaning.",
    "Learn the prefixed verbs shown here; practise creating other prefixed verbs and using aspect in sentences separately.",
    "Learn these conditional examples, then practise forming other conditional sentences and choosing aspect separately.",
    "Distinguish subject agreement from the meaning of the whole phrase.",
    "Learn these obligation forms, then practise complete obligation statements and other tense forms separately.",
    "Distinguish the eclipse sentence’s meaning from the employee’s obligation.",
    "Learn these examples, then practise other uses and complete sentences separately.",
    # Feedback that names the keyed answer (kept: the capability rules require the answer in the feedback).
    "Choose I have to.",
    "Choose If the subject is feminine.",
    "Choose If the subject is plural.",
    "Choose The eclipse should occur at three o'clock.",
    "Choose a female speaker.",
    "Choose a male speaker.",
    "Choose the speaker is marked feminine.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


DEFAULT = {  # prep 3 MARK evidence by objective
    P: p("VA", 23, 25, 26, 27) + [("V", "отгова'ривать", "translations_en"), ("V", "отговори'ть", "translations_en"),
                                   ("V", "договори'ться", "translations_en")],
    CO: p("G", 1215) + p("L", 580, 582) + p("MOR", 901, 903),
    O: p("L", 723) + p("W", 36) + p("G", 17, 18),
    PR: p("W", 33, 35, 36, 38, 39, 41) + p("L", 723) + [("N", "затме'ние", "gender")],
}
EXTRA: dict[str, list] = {
    # S1 SUPPORTED (ru Сослагательное наклонение 140019162, line 18: time is shown lexically or by the situation): 2225
    "Context supplies the time reference.": p("SOS", 18) + p("G", 1215),
    # S1 PARTIAL (Wiktionary бы 94001807, line 58: any place within a sentence except the beginning): 2234, reworded
    "The particle cannot begin a sentence.": p("BY", 58),
    "The subject is nominative.": p("G", 17, 18) + p("L", 723),
    "The short adjective agrees with the subject, and an infinitive can name the required action.": p("L", 723) + p("W", 36, 38)
    + p("MOR", 838),
    "The short adjective changes with the subject, while the infinitive naming the action does not acquire a gender ending.":
        p("L", 723) + p("W", 36, 38) + p("MOR", 838),
    "The short adjective agrees with the neuter subject and the infinitive names the event.": p("W", 39, 41) + p("L", 723)
    + [("N", "затме'ние", "gender")],
}
FACTS: set[str] = {  # the prep 3 MARK sentences (and their sweep wording) without a Cyrillic form; DEFAULT evidence
    "A prefix is not always just an aspect marker: learn the whole verb and its meaning.",
    "Both use the first-person pronoun, a past-form verb and the conditional particle.",
    "Context determines which sense the shared adjective-plus-infinitive structure expresses.",
    "Employees must report to work on time.",
    "He owes me five thousand rubles.",
    "Here should expresses an expected event, not a duty imposed on the eclipse.",
    "I have to is the meaning of the first-person phrase.",
    "I would like to sleep is a sentence meaning rather than a speaker-agreement label.",
    "I would like to sleep is a sentence meaning, not a label identifying the gender marked by the shown past form.",
    "If the subject is feminine and If the subject is plural are labels describing which form to use.",
    "If the subject is feminine calls for a different form of the adjective.",
    "If the subject is feminine is an agreement label rather than the meaning of this first-person phrase.",
    "If the subject is neuter calls for a different form of the adjective.",
    "If the subject is plural calls for a different form of the adjective.",
    "If the subject is plural is an agreement label rather than the meaning of this first-person phrase.",
    "In this construction the past form need not report an actual past event: it can express a hypothetical situation, such as I would sleep.",
    "Learn these two verbs as a pair with the same dissuading meaning.",
    "Neither expresses dissuading or agreeing.",
    "Obligation describes a required action, likelihood an expected event such as the eclipse, and indebtedness owing something to someone.",
    "Obligation is one use of this adjective; an expected-event reading is taught separately.",
    "The complete sentence gives an expected eclipse and its time.",
    "The eclipse example predicts an expected event.",
    "The feminine past form has a final vowel that the masculine form lacks.",
    "The first-person phrase expresses obligation with masculine singular agreement.",
    "The first-person pronoun identifies the speaker, rather than another person.",
    "The first-person pronoun identifies the speaker; the feminine past form marks feminine agreement.",
    "The shown phrase does not name an action, so do not infer a particular task from it.",
    "The wish reading comes from the construction and context, not from translating every conditional particle as would like.",
    "These English labels describe agreement; they are not literal translations of each Russian word.",
    "These are different meanings, even though the words share material with a verb of speaking.",
    "These labels describe the speaker marked by the past-form agreement; they are not translations of the whole expressions.",
    "These wish expressions have the conditional particle before the verb, whereas the earlier examples put it after the verb.",
    "This is the subject-agreement label for the feminine form, not its literal translation.",
    "This is the subject-agreement label for the plural form, not its literal translation.",
    "This phrase expresses obligation for a masculine singular speaker.",
    "This states an obligation for plural employees.",
    "Usually translated as \"should\".",
    "When finding an aspect partner, preserve the lexical meaning while changing aspect.",
    "With a verb, this form can express likelihood.",
    "a female speaker is marked by the feminine past form; the pronoun identifies the speaker.",
    "a female speaker would require feminine past agreement.",
    "a male speaker is marked by the masculine past form, not supplied as the sentence translation.",
    "a male speaker would require masculine past agreement.",
    "expresses obligation: the employees must report on time.",
    "indebtedness describes owing something, not the required employee action.",
    "likelihood describes an expected event such as the eclipse, not the required employee action.",
    "states an employee obligation rather than the expected eclipse.",
    "the sentence is about another person does not match the first-person pronoun.",
    "the speaker is marked masculine would require masculine past agreement.",
    "to agree is the meaning of the agreement verb.",
    "to dissuade is the meaning of a different prefixed verb.",
    "to do is the meaning of the doing verb.",
    "to write is the meaning of the writing verb.",
    "translates a sentence about owing money, not the eclipse sentence.",
    "translates the employee obligation sentence, not the eclipse sentence.",
    "— Employees must report to work on time.",
    "— He owes me five thousand rubles.",
    "— The eclipse should occur at three o'clock.",
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

    def template(self, page: str, template: str | None = None) -> dict:
        tmpl = template or PAGES[page][1]
        if not tmpl.startswith("S1:"):
            return copy.deepcopy(self.records[tmpl])
        s1 = S1[tmpl[3:]]
        record = copy.deepcopy(self.records[S1_TEMPLATE[tmpl[3:]]])
        record.update({"sourceId": s1["sourceId"], "sourceRevision": s1["oldid"], "attribution": s1["attribution"],
                       "sourceUrl": s1["url"]})
        return record

    def text(self, page: str) -> str:
        return (SRC / PAGES[page][0]).read_text(encoding="utf-8")

    def raw(self, page: str, number: int) -> str:
        return self.text(page).split("\n")[number - 1].strip()

    def line(self, page: str, number: int, objective: str) -> str:
        record_id = f"{PREFIX}-{page.lower()}{number}-{objective[8:]}"
        if record_id in self.records:
            return record_id
        raw = self.raw(page, number)
        if self.text(page).count(raw) != 1:
            raise SystemExit(f"{page}: line is not unique on its page ({raw[:60]!r})")
        record = self.template(page)
        # Units 16-30 provenance ships no MediaWiki markup: the longest markup-free stretch of the line.
        shipped = max((c.strip(" *:#-") for c in MARKUP.split(raw) if re.search("[A-Za-zЀ-ӿ]", c)), key=len)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": PAGES[page][2] + raw[:80],
                       "originalSourceText": raw, "shippedText": shipped, "sourceHash": sha(raw), "contentHash": sha(shipped),
                       "recordType": "instruction", "transformation": "selection"})
        record.pop("originalSourceTextRef", None)
        return self.add(record)

    def select(self, page: str, number: int, text: str, objective: str, record_type: str = "example",
               transformation: str = "selection", original: str | None = None, template: str | None = None) -> str:
        raw = original or self.raw(page, number)
        if original is not None and original not in self.text(page):
            raise SystemExit(f"{page}: the excerpt is not in its frozen file")
        shown = BATCHA_UNFOLD(raw) if transformation == "wiktionary template selection" else d7b.displayed(raw)
        if text not in shown:
            raise SystemExit(f"{text!r} is not displayed on {page}:{number}")
        record_id = f"{PREFIX}-{page.lower()}{number}-{sha(text + objective + raw[:40])[:8]}"
        if record_id in self.records:
            return record_id
        record = self.template(page, template)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": PAGES[page][2] + raw[:80],
                       "originalSourceText": raw, "shippedText": text, "sourceHash": sha(raw), "contentHash": sha(text),
                       "recordType": record_type, "transformation": transformation})
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
        record.update({"itemId": f"{PREFIX}-{table.lower()}-{sha(accented + field + objective)[:8]}-{field}", "unitNumber": UNIT,
                       "objectiveId": objective, "sourceRecordId": source_id, "originalSourceText": original,
                       "shippedText": shipped, "sourceHash": sha(original), "contentHash": sha(shipped),
                       "recordType": "lexical record", "transformation": "formatting"})
        return self.add(record)

    def ref(self, spec: tuple, objective: str) -> str:
        return self.line(spec[0], spec[1], objective) if len(spec) == 2 else self.cell(*spec, objective)


def BATCHA_UNFOLD(text: str) -> str:
    """Mirror of BatchARemainingSources.unfoldWiktionary for one {{uxi}} line: the Russian label text, then the gloss."""
    out = []
    for line in text.split("\n"):
        m = re.search(r"\{\{uxi\|ru\|(.*)\|([^|{}]*)\}\}", line)
        if m:
            russian = re.sub(r"\[\[(?:[^\]|]*\|)?([^\]]*)\]\]", r"\1", m.group(1)).replace("'''", "").strip()
            out.append(russian + "\n" + m.group(2).strip())
        else:
            out.append(line)
    return "\n".join(out)


REGION = re.compile("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*")


def admit_s1(pack: dict) -> None:
    """S1 froze ru Сослагательное наклонение 140019162 and Wiktionary бы 94001807 (D3); D29a admits their revisions."""
    for key in ("soslagatelnoe", "by"):
        page = S1[key]
        data = (SRC / "s1-intake" / page["file"]).read_bytes()
        if hashlib.sha256(data).hexdigest() != page["sha256"]:
            raise SystemExit(f"{key} does not match its FREEZE.json entry")
        if any(s["id"] == page["sourceId"] for s in pack["sources"]):
            raise SystemExit(f"{key} is already a pack source")
        template = next(s for s in pack["sources"] if s["id"] == S1_SOURCE_TEMPLATE[key])
        pack["sources"].append({**copy.deepcopy(template), "id": page["sourceId"], "title": page["title"], "attribution": page["attribution"]})
        rights = next(r for r in pack["rights"] if r["sourceId"] == S1_SOURCE_TEMPLATE[key])
        pack["rights"].append({**copy.deepcopy(rights), "id": "rights-" + page["sourceId"][4:], "sourceId": page["sourceId"]})


def rebuild(item: dict, prompt: str | None, answer_id: str, distractor_ids: list[str], rationales: list[str], records: dict,
            feedback: str | None = None, why_first: str | None = None, relation: str | None = None, cue_id: str | None = None) -> None:
    """Rewrite a closed item's options from source records: the answer first, then the distractors, each with its rationale."""
    a = item["assessment"]
    answer = records[answer_id]["shippedText"]
    options = [answer] + [records[d]["shippedText"] for d in distractor_ids]
    for option, why in zip(options[1:], rationales):
        if option not in why:
            raise SystemExit(f"{item['id']}: rationale does not name {option!r}")
    if prompt:
        item["prompt"] = prompt
    if feedback:
        item["feedback"] = feedback
    item["options"], item["answer"] = options, answer
    item["distractorRationales"] = [""] + rationales
    first = why_first if why_first is not None else a["whyAnswerDemonstratesObjective"].split("\n")[0]
    a.update({"answerSourceIds": [answer_id], "distractorSourceIds": [""] + distractor_ids, "distractorDifference": [""] + rationales,
              "contrastMembers": options, "whyAnswerDemonstratesObjective": first + "\n" + item["feedback"]})
    cue = cue_id or a["relationParticipantIds"][0]
    a["relationParticipantIds"] = [cue, answer_id] + distractor_ids
    if relation:
        a["evidenceMode"] = a["relationKind"] = relation


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
    R_ = b.records
    # Russian the new teaching shows: the aspect partners (VA:23, VA:25), the two conditional verbs (G:1215) and the
    # indebtedness example (W:35).
    b.select("VA", 25, "договариваться", P)
    for text in ("писать", "делать"):
        b.select("VA", 23, text, P)
    for text in ("поспа́л", "спал"):
        b.select("G", 1215, text, CO)
    owes_ru = b.select("W", 35, "Он до́лжен мне пять ты́сяч рубле́й.", PR, "example", "wiktionary template selection",
                       template="prov-ex-u19-probability-1-ru")
    owes_en = b.select("W", 35, "He owes me five thousand rubles.", PR, "meaning")
    # --- the reference linked by Unit 19 only: governance wording out, the S1 pages named ----------------------------
    for ref_id, fields in ATTRIBUTION.items():
        reference = next(x for x in pack["references"] if x["id"] == ref_id)
        reference.update({k: v for k, v in fields.items() if k != "steps"})
        for sid in fields["steps"]:
            steps[sid]["referenceIds"] = list(dict.fromkeys((steps[sid].get("referenceIds") or []) + [ref_id]))
    # --- teaching ---------------------------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], old, new, step_id)
    for step in steps.values():
        if step["kind"] in ("GUIDED", "CHECK"):
            step["body"] = PRACTICE_BODY
    # --- the obligation checks ask which subject matches the agreement form (L:723) ----------------------------------
    for item_id, (answer_id, distractor_ids, rationales) in OBLIGATION_ITEMS.items():
        rebuild(items[item_id], OBLIGATION_PROMPT, answer_id, distractor_ids, rationales, R_)
    # --- probability-1: the eclipse gloss against two frozen competing meanings (D2: W:38, W:35) ---------------------
    rebuild(items["u19-i-probability-1"], None, "prov-ex-u19-probability-0-en", ["prov-ex-u19-probability-1-en", owes_en],
            PROBABILITY_1_RATIONALES, R_)
    # --- probability-2: obligation, likelihood or indebtedness, all from the W:33-41 excerpt (existing rule) ----------
    lines = b.text("W").split("\n")
    block = "\n".join(lines[W_BLOCK[0] - 1:W_BLOCK[1]])
    cue = b.select("W", 38, "Слу́жащие должны́ во́время явля́ться на рабо́ту.", PR, "example", "wiktionary template selection",
                   original=block, template="prov-ex-u19-probability-1-ru")
    senses = [b.select("W", n, word, PR, "meaning", original=block) for n, word in ((36, "obligation"), (39, "likelihood"),
                                                                                    (33, "indebtedness"))]
    rebuild(items["u19-i-probability-2"], SENSE_PROMPT, senses[0], senses[1:], PROBABILITY_2["rationales"], R_,
            feedback=PROBABILITY_2["feedback"], why_first="Used to express obligation or suggestion.",
            relation="RULE_CLASSIFIES_SOURCE_EXAMPLE", cue_id=cue)
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
        cue_text = R_[b.select(cue[0], cue[1], cue[2], objective, "lexical record")]["shippedText"]
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
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u19-s-book")
    unit["sessions"].insert(book, {"id": "u19-s-checks", "title": "Unit 19 checks", "estimatedMinutes": 20,
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
        raise SystemExit("Unit 19 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 19 learner field: Russian only through frozen Unit 19 records ------------------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and d7b.frozen_record(pack, r)}
    texts = [s["body"] for s in steps.values()] + [unit["topicSummary"], unit["mappingNote"], BINDER["prompt"], BINDER["modelAnswer"]]
    texts += [r[k] for r in pack["references"] if r["id"] in {s["id"] for s in REFERENCES} | set(ATTRIBUTION)
              for k in ("title", "summary", "detail")]
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
        raise SystemExit(f"Unit 19 regions without a frozen record: {sorted(unshown)}")
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
                raise SystemExit(f"No frozen Unit 19 record shows {form!r} ({sentence})")
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
        Path("/tmp/claude-0/-home-user-russian-companion/8476c374-e1ab-5f3d-9af8-fc234c0d6e90/scratchpad/t17d/missing.json").write_text(
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
        item_id = f"prov-ca-t17d-{key}"
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
        raise SystemExit("Asset is not the T17d baseline; this transformer runs only once")
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
    print(f"{len(sweep_log)} Unit 19 sweep rows applied; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
