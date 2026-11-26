#!/usr/bin/env python3
"""T16d (Claude Code, owner direction 2026-11-22): Unit 14 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the D32b pack (the T16c DONE pack). Plan: Codex prep
audit/prep/units-13-15-prep-at-4c402ef7.txt (owner/prep-reports), Unit 14 section; the S1 claim verdicts in
audit/prep/s1-claim-verification.csv (Unit 14: 1554 and 1559 PARTIAL); and the D31c learner-text sweep
(audit/T16-n2-learner-text-sweep-units-01-15.csv, Unit 14 rows, applied first). Russian comes only from frozen files already in
the source inventory: Wikibooks Future tense 3353822 (F) and Past tense 4567337 (P); OpenRussian verbs.csv (V); and the S1
pages Grammatical aspect 1384289498 (GA) and Вид (лингвистика) 153277825 (VID), pack sources since T16c. Unit 14 teaches no
imperative, so Повелительное наклонение supports no Unit 14 claim and is not cited.
- A2-24: every check moves to an end-of-unit session; prompts name the target (sweep, prep section 1).
- A2-23: fourteen typed checks (six compound-future forms of быть, six perfective future forms of сказа́ть, поговорю́,
  порабо́таю); the complete paradigms and the two bounded-period verbs are taught first (prep section 2).
- Facts: 13 English-only entries -> MARK or REWORD (prep section 3); S1 1554 and 1559 PARTIAL are reworded to what VID:4,
  GA:86 and P:16-17 state, then marked. быть is taught as imperfective (V:2); the "perfective быть" clause of F is not used.
- Section 4: the future definition is bounded to the supplied statements; "always use было" is scoped to the cold and bored
  states shown (P:24).
- A2-26: governance and markup leave Unit 14 learner text; the mapping note follows the Units 1-12 style.
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
BASELINE_SHA256 = "54ae2ee97d55f171a388797aeed61b0029660a3b6b4ef7ce8e7b1fd3994d0440"  # the D32b pack (d8af77f8)
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T16d", "2026-11-22", 14
ACUTE, CYR = "́", "Ѐ-ӿ"
FREEZE = json.loads((SRC / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
S1 = {p["key"]: p for p in FREEZE["pages"] + FREEZE["supplemental"]}
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "P": ("units-11-15/wikibooks/past-tense-4567337.txt", "prov-ex-u14-impersonal-0-ru", "past:"),
    "F": ("units-11-15/wikibooks/future-tense-3353822.txt", "prov-ex-u14-future-person-0-ru", "future:"),
    "GA": ("s1-intake/wikipedia/grammatical-aspect-1384289498.txt", "S1:aspect", "grammatical-aspect:"),
    "VID": ("s1-intake/ruwiki/vid-lingvistika-153277825.txt", "S1:vid", "ruwiki-vid:"),
}
VERBS = SRC / "units-3-5/openrussian/verbs.csv"
VID_SPANS: list[str] = []  # VID:4 carries no citation markup; it is recorded whole


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


VERB_ROWS = list(csv.DictReader(VERBS.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE))

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 14 covers aspect in the future, the person forms of the compound and perfective future, and impersonal states"
         " in the past, alongside Lesson 14 in your book.")
MAPPING = ("This unit goes with Lesson 14 in your book, printed pages 162-170: aspect in future-tense contexts and impersonal"
           " constructions.")
CHECK_OLD = "Answer using the examples taught here. A miss returns to the explanation."
CHECK_NEW = "Answer without looking back. A miss returns to the explanation."
DURATION = "A stated duration does not by itself require perfective aspect."
S1554 = "A limited stretch of an activity is different from doing it to completion."
S1559 = "Unbounded here describes only the supplied option, which states no duration; an activity can still have a duration."
LIMITED = ("Russian also has verbs that present an activity for a limited stretch, which is different from doing it to"
           " completion.")
FUTURE_AUX = ("The compound future puts a future form of быть before the imperfective infinitive, and the form of быть agrees"
              " with the subject. быть itself is an imperfective verb.")
PERFECTIVE = ("A perfective verb forms its future by conjugating it, with no separate future auxiliary. Я скажу́ — I will speak."
              " Мы ска́жем — We will speak.")
PERFECTIVE_REST = ("Ты ска́жешь — You will speak. Вы ска́жете — You will speak. Он ска́жет — He will speak. Они ска́жут — They"
                   " will speak.")
COMPOUND_REST = ("Ты бу́дешь говори́ть — You will be speaking. Вы бу́дете говори́ть — You will be speaking. Он бу́дет говори́ть"
                 " — He will be speaking. Они бу́дут говори́ть — They will be speaking.")
BOUNDED_VERBS = ("поговорю́ is the first-person singular future of поговори́ть, and порабо́таю is the first-person singular future"
                 " of порабо́тать. Both are perfective.")
IMPERSONAL_RULE = ("For the cold and bored states shown here, the person is in the dative, and the past tense uses было in the"
                   " neuter singular.")
TEACH = {  # step: [(old, new)]; old None replaces the body
    "u14-st-teach-future-aspect-2": [(None, "Read the two future examples. Both supplied statements refer to future situations. "
                                            + DURATION + " " + LIMITED)],
    "u14-st-teach-future-person-2": [(None, FUTURE_AUX)],
    "u14-st-teach-future-person-3": [(None, PERFECTIVE)],
    "u14-st-teach-future-person-4": [(None, PERFECTIVE_REST)],
    "u14-st-teach-impersonal-2": [(None, IMPERSONAL_RULE)],
}
# New teaching cards: (id, after, session, objective, title, body, examples [(id, page, line, ru, en) | (id, "V", row, field)])
NEW_STEPS = [
    ("u14-st-teach-future-person-5", "u14-st-teach-future-person-1", "u14-s-future-person", "obj-u14-future-person",
     "More examples", COMPOUND_REST,
     [("ex-u14-future-person-5", "F", 44, "Ты бу́дешь говори́ть", "You will be speaking"),
      ("ex-u14-future-person-6", "F", 44, "Вы бу́дете говори́ть", "You will be speaking"),
      ("ex-u14-future-person-7", "F", 47, "Он бу́дет говори́ть", "He will be speaking"),
      ("ex-u14-future-person-8", "F", 47, "Они бу́дут говори́ть", "They will be speaking")]),
    ("u14-st-teach-future-aspect-4", "u14-st-teach-future-aspect-3", "u14-s-future-aspect", "obj-u14-future-aspect",
     "The two perfective verbs", BOUNDED_VERBS,
     [("ex-or-u14-future-aspect-4", "V", "поговори'ть", "accented"), ("ex-or-u14-future-aspect-5", "V", "поговори'ть", "presfut_sg1"),
      ("ex-or-u14-future-aspect-6", "V", "порабо'тать", "accented"), ("ex-or-u14-future-aspect-7", "V", "порабо'тать", "presfut_sg1")]),
]
# Sentence examples for the rewritten perfective cards (F:60, :63, :66).
STEP_EXAMPLES = {
    "u14-st-teach-future-person-3": [("ex-u14-future-person-9", "F", 60, "Я скажу́", "I will speak"),
                                     ("ex-u14-future-person-10", "F", 60, "Мы ска́жем", "We will speak")],
    "u14-st-teach-future-person-4": [("ex-u14-future-person-11", "F", 63, "Ты ска́жешь", "You will speak"),
                                     ("ex-u14-future-person-12", "F", 63, "Вы ска́жете", "You will speak"),
                                     ("ex-u14-future-person-13", "F", 66, "Он ска́жет", "He will speak"),
                                     ("ex-u14-future-person-14", "F", 66, "Они ска́жут", "They will speak")],
}
PROMPTS = {  # prep section 1 on top of the sweep wording; each names its target
    "u14-i-future-aspect-1": "Which sentence is the bounded future for the stated three-hour work period?",
    "u14-i-future-aspect-2": "Which sentence is the unbounded future, with a future auxiliary and an imperfective infinitive?",
    "u14-i-future-person-2": "Which compound-future sentence is the first-person singular future?",
}
FEEDBACK = [  # (item, old sentence, new sentence)
    ("u14-i-future-aspect-1", "The limit describes this work period, not necessarily completion of a task.", S1554),
    ("u14-i-future-aspect-2", "Unbounded here describes the supplied option; it does not mean the activity lasts forever or that"
                              " imperfective future can never have a duration.", S1559),
]

TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
BYT = [("budu", "Я бу́ду говори́ть", 41, "бу́ду", "I (first-person singular)"),
       ("budem", "Мы бу́дем говори́ть", 41, "бу́дем", "we (first-person plural)"),
       ("budesh", "Ты бу́дешь говори́ть", 44, "бу́дешь", "you, one person (second-person singular)"),
       ("budete", "Вы бу́дете говори́ть", 44, "бу́дете", "you, more than one person (second-person plural)"),
       ("budet", "Он бу́дет говори́ть", 47, "бу́дет", "he (third-person singular)"),
       ("budut", "Они бу́дут говори́ть", 47, "бу́дут", "they (third-person plural)")]
SKAZAT = [("skazhu", "presfut_sg1", "I (first-person singular)"), ("skazhem", "presfut_pl1", "we (first-person plural)"),
          ("skazhesh", "presfut_sg2", "you, one person (second-person singular)"),
          ("skazhete", "presfut_pl2", "you, more than one person (second-person plural)"),
          ("skazhet", "presfut_sg3", "he (third-person singular)"), ("skazhut", "presfut_pl3", "they (third-person plural)")]

REFERENCES = [
    {"id": "ref-u14-future", "title": "The future: person forms",
     "summary": "The compound future with быть and the future of a perfective verb, for every person.",
     "detail": (FUTURE_AUX + "\n\nЯ бу́ду говори́ть — I will be speaking. Мы бу́дем говори́ть — We will be speaking. " + COMPOUND_REST
                + "\n\n" + PERFECTIVE + " " + PERFECTIVE_REST),
     "searchTerms": ["future tense", "compound future", "perfective future"],
     "steps": ["u14-st-teach-future-person-1", "u14-st-teach-future-person-3"]},
    {"id": "ref-u14-future-aspect", "title": "Aspect in the future",
     "summary": "A limited stretch of an activity, and the compound future without a stated limit.",
     "detail": ("Я порабо́таю три ча́са — I will work for three hours. Я бу́ду рабо́тать — I will work. " + DURATION + " " + LIMITED),
     "searchTerms": ["future aspect", "limited stretch", "duration"],
     "steps": ["u14-st-teach-future-aspect-1"]},
    {"id": "ref-u14-impersonal", "title": "Impersonal states in the past",
     "summary": "Cold and bored states with the person in the dative.",
     "detail": (IMPERSONAL_RULE + " Ему было холодно — He was cold. Им было скучно — They were bored. ей было холодно — she was cold."),
     "searchTerms": ["impersonal", "dative", "was cold"],
     "steps": ["u14-st-teach-impersonal-1"]},
]
BINDER = {"id": "binder-u14-future", "objectiveIds": ["obj-u14-future-person", "obj-u14-future-aspect"],
          "prompt": ("In your binder, write the compound future of the verb meaning to speak for every person, then the future of"
                     " its perfective partner for every person. Compare your work with the model answer."),
          "modelAnswer": ("Я бу́ду говори́ть. Мы бу́дем говори́ть. Ты бу́дешь говори́ть. Вы бу́дете говори́ть. Он бу́дет говори́ть."
                          " Они бу́дут говори́ть.\nЯ скажу́. Мы ска́жем. Ты ска́жешь. Вы ска́жете. Он ска́жет. Они ска́жут.")}
TUTOR = {"id": "tutor-u14-future",
         "bring": "Your binder future forms and the three cold or bored sentences, to read aloud and say who is cold or bored."}

# --------------------------------------------------------------------------------------------------------------
# Facts: English-only sentences and their evidence. ("F", n) = a page line record, ("V", row, field) = an OpenRussian cell.
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
    "Read the two future examples.",
    "Read the explanation and compare the examples.",
    "Read the worked examples, then practise before answering independently.",
    "A wrong answer returns to this contrast.",
    "Practise first, then answer without the explanation in view.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


EXTRA: dict[str, list] = {
    "Both supplied statements refer to future situations.": p("F", 15, 16, 17, 18),
    DURATION: p("P", 16, 17),
    LIMITED: p("VID", 4),
    S1554: p("VID", 4) + p("F", 16),
    S1559: p("F", 17) + p("GA", 86) + p("P", 17),
    "Both refer to the future, not to an event already completed.": p("F", 15, 16, 17, 18),
    "A stated duration does not automatically require perfective aspect in every sentence.": p("P", 16, 17),
    "A definite time period does not by itself force perfective aspect.": p("P", 16, 17),
    "The compound future puts a future form of быть before the imperfective infinitive, and the form of быть agrees with the subject.":
        p("F", 41, 44, 47),
    "быть itself is an imperfective verb.": [("V", "быть", "aspect")],
    "A perfective verb forms its future by conjugating it, with no separate future auxiliary.": p("F", 53, 60),
    "Both are perfective.": [("V", "поговори'ть", "aspect"), ("V", "порабо'тать", "aspect")],
    "The imperfective infinitive stays the same; the subject and auxiliary distinguish the number.": p("F", 41),
    "The cue shows third-person singular; change it to third-person plural of the same verb.":
        [("V", "сказа'ть", "presfut_sg3"), ("V", "сказа'ть", "presfut_pl3")],
    "These forms refer to future events, not events already completed.": p("F", 53, 60, 63, 66),
    "Both use the same imperfective infinitive after the future auxiliary; choose singular agreement.": p("F", 41),
    "Both options are impersonal past and keep the same neuter singular verb; the person and state distinguish them.":
        p("P", 24, 25, 26) + [("V", "быть", "past_n")],
    "Both options are impersonal past; plural experiencers do not turn the verb into a plural form in this construction.":
        p("P", 24, 25, 26),
    "Both forms are impersonal past; a female experiencer changes the pronoun, not the verb to feminine agreement.": p("P", 24, 25),
    IMPERSONAL_RULE: p("P", 24),
}


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
        if record["sourceId"] != "src-content-audit-research" and not d7b.frozen_record(self.pack, record):
            raise SystemExit(f"{record['itemId']} is not a frozen-source record")
        return record["itemId"]

    def template(self, page: str) -> dict:
        tmpl = PAGES[page][1]
        if not tmpl.startswith("S1:"):
            return copy.deepcopy(self.records[tmpl])
        s1 = S1[tmpl[3:]]
        base = "prov-ex-u7-adverb-invariance-0-ru" if s1["licenseRoute"] == "ruwiki" else "prov-ex-u16-complements-0-ru"
        record = copy.deepcopy(self.records[base])
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
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": prefix + raw[:80],
                       "originalSourceText": raw, "shippedText": raw, "sourceHash": sha(raw), "contentHash": sha(raw),
                       "recordType": "instruction", "transformation": "selection"})
        return self.add(record)

    def line(self, page: str, number: int, objective: str) -> str:
        if page == "VID" and number < len(VID_SPANS):
            raw = VID_SPANS[number]
            if raw not in (SRC / PAGES["VID"][0]).read_text(encoding="utf-8").split("\n")[8]:
                raise SystemExit("VID span is not on line 9")
            return self._record(page, raw, f"prov-t16d-u14-vid9-{number}", objective)
        raw = (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()
        return self._record(page, raw, f"prov-t16d-u14-{page.lower()}{number}", objective)

    def select(self, page: str, number: int, text: str, objective: str, record_type: str = "example") -> str:
        """A record of one displayed span of a frozen page line (an example sentence, a gloss or a word)."""
        raw = (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()
        if text not in d7b.displayed(raw):
            raise SystemExit(f"{text!r} is not displayed on {page}:{number}")
        record_id = f"prov-t16d-u14-{page.lower()}{number}-{sha(text)[:8]}"
        if record_id in self.records:
            return record_id
        record = self.template(page)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": PAGES[page][2] + raw[:80],
                       "originalSourceText": raw, "shippedText": text, "sourceHash": sha(raw), "contentHash": sha(text),
                       "recordType": record_type, "transformation": "selection"})
        return self.add(record)

    def cell(self, accented: str, field: str, objective: str) -> str:
        hits = [r for r in VERB_ROWS if r["accented"] == accented]
        if len(hits) != 1:
            raise SystemExit(f"V: {len(hits)} rows for {accented}")
        original = hits[0][field].strip()
        if not original:
            raise SystemExit(f"V:{accented}#{field} is empty")
        for r in self.records.values():
            if r["unitNumber"] == UNIT and r["sourceRecordId"] == f"verbs.csv:{accented}#{field}" and is_frozen(self.pack, r):
                return r["itemId"]
        record = copy.deepcopy(self.records["prov-ex-or-u14-future-person-2"])
        shipped = stressed(original) if field == "accented" or field.startswith(("past_", "presfut_")) else original
        if field == "translations_en":
            shipped = original.split(",")[0].strip()  # the first English sense, as the Unit 13 gloss records show it
        record.update({"itemId": f"prov-t16d-u14-v-{sha(accented + field)[:8]}-{field}", "objectiveId": objective,
                       "sourceRecordId": f"verbs.csv:{accented}#{field}", "originalSourceText": original,
                       "shippedText": shipped, "sourceHash": sha(original), "contentHash": sha(shipped),
                       "recordType": "lexical record"})
        return self.add(record)


def is_frozen(pack: dict, record: dict) -> bool:
    return d7b.frozen_record(pack, record)


def apply(pack: dict) -> tuple[dict, list]:
    baseline = copy.deepcopy(pack)  # before the sweep, so sweep wording is covered too
    sweep_log: list = []
    sweep.apply_sweep(pack, [], sweep_log, units={UNIT})
    b = Builder(pack)
    unit = b.unit
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    sessions = {se["id"]: se for se in unit["sessions"]}
    R = b.records
    sentence_template = next(e for e in pack["examples"] if e["id"] == "ex-u14-future-person-0")
    lexical_template = next(e for e in pack["examples"] if e["id"] == "ex-or-u14-future-person-2")

    def sentence_example(ex_id: str, page: str, number: int, ru: str, en: str, objective: str) -> str:
        for suffix, text, kind in (("ru", ru, "example"), ("en", en, "meaning")):
            source = R[b.select(page, number, text, objective, kind)]
            record = copy.deepcopy(source)
            record["itemId"] = f"prov-{ex_id}-{suffix}"
            b.add(record)
        pack["examples"].append({**copy.deepcopy(sentence_template), "id": ex_id, "display": ru, "note": en, "objectiveId": objective})
        objectives[objective]["exampleIds"].append(ex_id)
        return ex_id

    def lexical_example(ex_id: str, accented: str, field: str, objective: str) -> str:
        form_id = b.cell(accented, field, objective)
        gloss_id = b.cell(accented, "translations_en", objective)
        for suffix, source in (("", form_id), ("-gloss", gloss_id)):
            record = copy.deepcopy(R[source])
            record["itemId"] = f"prov-{ex_id}{suffix}"
            b.add(record)
        pack["examples"].append({**copy.deepcopy(lexical_template), "id": ex_id, "display": R[form_id]["shippedText"],
                                 "note": R[gloss_id]["shippedText"], "objectiveId": objective})
        objectives[objective]["exampleIds"].append(ex_id)
        return ex_id

    # --- teaching -------------------------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = new if old is None else edit(steps[step_id]["body"], old, new, step_id)
    for step_id, specs in STEP_EXAMPLES.items():
        objective = next(o["id"] for o in objectives.values() if step_id in o["instructionStepIds"])
        steps[step_id]["exampleIds"] = [sentence_example(*spec, objective) for spec in specs]
    for step_id, after, session_id, objective, title, body, specs in NEW_STEPS:
        ids = [sentence_example(*spec, objective) if spec[1] != "V" else lexical_example(spec[0], spec[2], spec[3], objective)
               for spec in specs]
        session = sessions[session_id]
        anchor = next(i for i, s in enumerate(session["steps"]) if s["id"] == after)
        new_step = {**copy.deepcopy(session["steps"][anchor]), "id": step_id, "title": title, "body": body, "exampleIds": ids}
        new_step.pop("referenceIds", None)
        session["steps"].insert(anchor + 1, new_step)
        instruction = objectives[objective]["instructionStepIds"]
        instruction.insert(instruction.index(after) + 1, step_id)
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    # --- remediation repeats each session's whole teaching ---------------------------------------------------------
    for se in unit["sessions"]:
        for s in se["steps"]:
            if s["kind"] == "TEACH":
                s["body"] = s["body"].strip()  # the baseline example cards end with a space
        rem = [s for s in se["steps"] if s["kind"] == "REMEDIATE"]
        if rem:
            rem[0]["body"] = "\n\n".join(s["body"] for s in se["steps"] if s["kind"] == "TEACH")
            rem[0]["exampleIds"] = list(dict.fromkeys(x for s in se["steps"] if s["kind"] == "TEACH" for x in s.get("exampleIds") or []))
    # --- items ----------------------------------------------------------------------------------------------------
    for item_id, prompt in PROMPTS.items():
        items[item_id]["prompt"] = prompt
    for item_id, old, new in FEEDBACK:
        item = items[item_id]
        item["feedback"] = edit(item["feedback"], old, new, item_id)
        item["assessment"]["whyAnswerDemonstratesObjective"] = edit(item["assessment"]["whyAnswerDemonstratesObjective"], old, new, item_id)
    typed = []
    byt_cue = b.cell("быть", "accented", "obj-u14-future-person")
    for key, sentence, number, form, person in BYT:
        sentence_id = b.select("F", number, sentence, "obj-u14-future-person")
        form_id = b.select("F", number, form, "obj-u14-future-person")
        if R[form_id]["shippedText"] not in R[sentence_id]["shippedText"]:
            raise SystemExit(f"{form} is not in {sentence}")
        typed.append(typed_item(f"u14-i-type-{key}", "obj-u14-future-person", f"Type the future form of the shown verb for {person}.",
                                R[byt_cue]["shippedText"], R[form_id]["shippedText"], TYPED_FEEDBACK, "src-wikibooks-russian-cc-by-sa"))
    skazat_cue = b.cell("сказа'ть", "accented", "obj-u14-future-person")
    for key, field, person in SKAZAT:
        form_id = b.cell("сказа'ть", field, "obj-u14-future-person")
        typed.append(typed_item(f"u14-i-type-{key}", "obj-u14-future-person", f"Type the future form of the shown verb for {person}.",
                                R[skazat_cue]["shippedText"], R[form_id]["shippedText"], TYPED_FEEDBACK, "src-openrussian-50e210c4"))
    for key, accented in (("pogovoryu", "поговори'ть"), ("porabotayu", "порабо'тать")):
        cue_id = b.cell(accented, "accented", "obj-u14-future-aspect")
        form_id = b.cell(accented, "presfut_sg1", "obj-u14-future-aspect")
        typed.append(typed_item(f"u14-i-type-{key}", "obj-u14-future-aspect", "Type the future form of the shown verb for I (first-person singular).",
                                R[cue_id]["shippedText"], R[form_id]["shippedText"], TYPED_FEEDBACK, "src-openrussian-50e210c4"))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    items.update({i["id"]: i for i in typed})
    # Typed keys are taught answers: each key appears in the teaching of its objective.
    for item in typed:
        taught = " ".join(steps[s]["body"] for s in objectives[item["objectiveId"]]["instructionStepIds"])
        if item["answer"] not in taught:
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
        if step["body"] == CHECK_OLD:
            step["body"] = CHECK_NEW
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u14-s-book")
    unit["sessions"].insert(book, {"id": "u14-s-checks", "title": "Unit 14 checks", "estimatedMinutes": 25,
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
        raise SystemExit("Unit 14 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 14 learner field: Russian only through frozen Unit 14 records ------------------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and is_frozen(pack, r)}
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
                raise SystemExit(f"Unit 14 region without a frozen record: {region!r} in {text[:80]!r}")
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
        out, pool = [], [x for x in R.values() if x["unitNumber"] == UNIT and is_frozen(pack, x)]
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
                raise SystemExit(f"No frozen Unit 14 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    def resolve(ref, objective: str) -> str:
        if isinstance(ref, tuple) and len(ref) == 2:
            return b.line(ref[0], ref[1], objective)
        if isinstance(ref, tuple):
            return b.cell(ref[1], ref[2], objective)
        return ref

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
        raise SystemExit("Sentences without a disposition: " + json.dumps(missing, ensure_ascii=False, indent=1))
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
        item_id = f"prov-ca-t16d-{key}"
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
        raise SystemExit("Asset is not the T16d baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report, sweep_log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps({"facts": report, "sweep": sweep_log}, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(sweep_log)} Unit 14 sweep rows applied; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
