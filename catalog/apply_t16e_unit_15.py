#!/usr/bin/env python3
"""T16e (Claude Code, owner direction 2026-11-22): Unit 15 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T16d DONE pack (91b8bf27). Plan: Codex prep
audit/prep/units-13-15-prep-at-4c402ef7.txt and audit/prep/units-13-15-prep-unit-15-remainder.txt (owner/prep-reports), Unit 15
section; the S1 claim verdicts (no Unit 15 rows); the D31c learner-text sweep (audit/T16-n2-learner-text-sweep-units-01-15.csv,
Unit 15 rows, applied first); owner decision D4 (aspect in requests is accepted as book-directed: a documented gap, no new
content). Russian comes only from frozen files already in the source inventory: OpenRussian verbs.csv (V); Wikibooks Verbal
aspect 4059032 (B) and Accusative 4330404 (C); Wikipedia Russian grammar 1369617627 (RG); and the S1 page Повелительное наклонение
152579064 (PN), which becomes a pack source here (D29a admits its revision) and is cited where it supports the plural/polite
imperative claims (PN:58, with RG:1169).
- A2-24: every check moves to an end-of-unit session; prompts name the target (sweep); u15-i-imperative-2 now asks for the plural
  imperative of the shown infinitive (key чита́йте), repairing the constant key (prep section 1).
- A2-23: fourteen typed checks with keys taught first; the do/make imperatives (V:19, V:20) get a worked card (prep section 2).
- Facts: 21 English-only entries -> MARK, REWORD or REMOVE (prep section 3; no S1 verdicts for Unit 15).
- A2-26: governance and markup leave Unit 15 learner text; the mapping note follows the Units 1-12 style.
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
BASELINE_SHA256 = "344d22fd27e02eba35e8846b664fe7b14fc4675004e416590ee0c5a78d52a4a4"  # the T16d DONE pack (91b8bf27)
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T16e", "2026-11-22", 15
ACUTE, CYR = "́", "Ѐ-ӿ"
FREEZE = json.loads((SRC / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
S1 = {p["key"]: p for p in FREEZE["pages"] + FREEZE["supplemental"]}
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "B": ("units-11-15/wikibooks/verbal-aspect-4059032.txt", "prov-u15-say-request-rem", "aspect:"),
    "C": ("units-6-10/wikibooks/accusative-4330404.txt", "prov-ex-u15-imperative-3-ru", "accusative:"),
    "RG": ("units-26-30/wikipedia/russian-grammar-1369617627.txt", "prov-ex-u16-complements-0-ru", "grammar:"),
    "PN": ("s1-intake/ruwiki/povelitelnoe-naklonenie-152579064.txt", "S1:povelitelnoe", "ruwiki-povelitelnoe:"),
}
VERBS = SRC / "units-3-5/openrussian/verbs.csv"
VID_SPANS: list[str] = []
# RG:1169 ends with a <ref> citation carrying a web URL; the record holds the contiguous claim span before it.
_RG1169 = (ROOT / "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt").read_text(encoding="utf-8").split("\n")[1168]
SPANS = {("RG", 1169): _RG1169[:_RG1169.index("<ref>")].strip()}


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


VERB_ROWS = list(csv.DictReader(VERBS.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE))

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 15 covers requests, the singular and plural imperative, and the aspect pairs learned so far, alongside Lesson 15"
         " in your book.")
MAPPING = ("This unit goes with Lesson 15 in your book, printed pages 171-187: requests, imperative forms and consolidation of"
           " aspect use.")
CHECK_OLD = "Answer using the examples taught here. A miss returns to the explanation."
CHECK_NEW = "Answer without looking back. A miss returns to the explanation."
SHOWN = "Use the imperative form shown here."
PAIR = "Use the saying/telling pair shown here."
SAY_PAIR = "говори́ть is imperfective and сказа́ть is perfective; both can mean to say, speak or tell."
DO_IMPERATIVES = ("де́лай and де́лайте are the singular and plural imperatives of the imperfective де́лать. сде́лай and сде́лайте"
                  " are the singular and plural imperatives of the perfective сде́лать. Each imperative keeps the aspect of its"
                  " verb.")
BOOK_GAP = "For choosing the aspect of a request, use the matching lesson in your book."
PREFIX = "Adding a prefix does not always produce the partner of a verb; learn each pair as it is taught."
TEACH = {  # step: [(old, new)]; old None replaces the body
    "u15-st-teach-imperative-contrast": [("This identifies the supplied form, not every possible way to express a command.", SHOWN)],
    "u15-st-teach-say-request-2": [(None, SAY_PAIR)],
    "u15-st-teach-aspect-consolidate-1": [("The short English label beside both examples does not mean that every use of speaking"
                                           " has this same partner.", PAIR)],
    "u15-st-teach-aspect-consolidate-2": [("Preserve their supplied spelling and stress.", "Copy each displayed form carefully."),
                                          # 1642: "does not establish" is A2-26 assessment commentary; the fact stays marked (B:25-29).
                                          ("This one example does not establish a general rule that adding any prefix produces the"
                                           " correct partner for any verb.", PREFIX)],
    "u15-st-teach-aspect-consolidate-3": [(None, "Identify each member of the two infinitive pairs. Compare the aspect and the"
                                                 " meaning group before selecting a supplied form. " + BOOK_GAP)],
}
NEW_STEPS = [  # (id, after, session, objective, title, body, examples [(id, "V", row, field)])
    ("u15-st-teach-aspect-consolidate-4", "u15-st-teach-aspect-consolidate-2", "u15-s-aspect-consolidate",
     "obj-u15-aspect-consolidate", "Imperatives of the do/make pair", DO_IMPERATIVES,
     [("ex-or-u15-aspect-consolidate-4", "V", "де'лать", "imperative_sg"), ("ex-or-u15-aspect-consolidate-5", "V", "де'лать", "imperative_pl"),
      ("ex-or-u15-aspect-consolidate-6", "V", "сде'лать", "imperative_sg"), ("ex-or-u15-aspect-consolidate-7", "V", "сде'лать", "imperative_pl")]),
]
STEP_EXAMPLES: dict = {}
PROMPTS = {"u15-i-imperative-2": "Which form is the plural imperative of the shown verb?"}
FEEDBACK = [  # (item, old sentence, new sentence)
    ("u15-g-imperative-0", "This identifies the supplied form, not every possible way to express a command.", SHOWN),
    ("u15-g-say-request-0", "The singular and plural imperative forms keep the stress shown in these examples; politeness also"
                            " depends on context.", "Copy the displayed stress."),
    ("u15-g-aspect-consolidate-0", "This pairing does not mean every use of speak or talk can be replaced by the same perfective"
                                   " verb.", PAIR),
]
# u15-i-imperative-2: the plural imperative of the shown infinitive (prep section 1, constant-key repair).
IMPERATIVE_2 = {
    "cue": "prov-ex-or-u15-imperative-2", "answer": "prov-ex-or-u15-imperative-1", "foil": "prov-ex-or-u15-imperative-0",
    "feature": "PLURAL_IMPERATIVE",
    "feedback": ("чита́йте is the plural imperative of the shown verb; it can address several people or one person politely."
                 " чита́й is the singular imperative, for one informal addressee. Both are imperative forms of the same"
                 " imperfective verb."),
    "rationale": "чита́й is the singular imperative, not the plural imperative of the shown verb.",
}

TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
TYPED = [  # (id, objective, cue (row, field), key (row, field), prompt)
    ("u15-i-type-chitai", "obj-u15-imperative", ("чита'ть", "accented"), ("чита'ть", "imperative_sg"), "Type the singular imperative of the shown verb."),
    ("u15-i-type-chitaite", "obj-u15-imperative", ("чита'ть", "accented"), ("чита'ть", "imperative_pl"), "Type the plural imperative of the shown verb."),
    ("u15-i-type-skazhi", "obj-u15-say-request", ("сказа'ть", "accented"), ("сказа'ть", "imperative_sg"), "Type the singular imperative of the shown verb."),
    ("u15-i-type-skazhite", "obj-u15-say-request", ("сказа'ть", "accented"), ("сказа'ть", "imperative_pl"), "Type the plural imperative of the shown verb."),
    ("u15-i-type-dai", "obj-u15-say-request", ("дать", "accented"), ("дать", "imperative_sg"), "Type the singular imperative of the shown verb."),
    ("u15-i-type-daite", "obj-u15-say-request", ("дать", "accented"), ("дать", "imperative_pl"), "Type the plural imperative of the shown verb."),
    ("u15-i-type-govorit", "obj-u15-aspect-consolidate", ("сказа'ть", "accented"), ("говори'ть", "accented"),
     "Type the imperfective verb paired with the shown perfective verb."),
    ("u15-i-type-skazat", "obj-u15-aspect-consolidate", ("говори'ть", "accented"), ("сказа'ть", "accented"),
     "Type the perfective verb paired with the shown imperfective verb."),
    ("u15-i-type-delat", "obj-u15-aspect-consolidate", ("сде'лать", "accented"), ("де'лать", "accented"),
     "Type the imperfective verb paired with the shown perfective verb."),
    ("u15-i-type-sdelat", "obj-u15-aspect-consolidate", ("де'лать", "accented"), ("сде'лать", "accented"),
     "Type the perfective verb paired with the shown imperfective verb."),
    ("u15-i-type-delai", "obj-u15-aspect-consolidate", ("де'лать", "accented"), ("де'лать", "imperative_sg"), "Type the singular imperative of the shown verb."),
    ("u15-i-type-delaite", "obj-u15-aspect-consolidate", ("де'лать", "accented"), ("де'лать", "imperative_pl"), "Type the plural imperative of the shown verb."),
    ("u15-i-type-sdelai", "obj-u15-aspect-consolidate", ("сде'лать", "accented"), ("сде'лать", "imperative_sg"), "Type the singular imperative of the shown verb."),
    ("u15-i-type-sdelaite", "obj-u15-aspect-consolidate", ("сде'лать", "accented"), ("сде'лать", "imperative_pl"), "Type the plural imperative of the shown verb."),
]

REFERENCES = [
    {"id": "ref-u15-imperative", "title": "The imperative",
     "summary": "Singular and plural imperatives, and the plural for polite address.",
     "detail": ("The singular imperative is for one informal addressee. The plural imperative adds an ending to the singular form"
                " and is used for several people or politely for one person: чита́й — чита́йте, скажи́ — скажи́те, да́й — да́йте."
                "\n\nКупи, пожалуйста, свежие фрукты. — Please, buy fresh fruits."),
     "searchTerms": ["imperative", "request", "polite form"],
     "steps": ["u15-st-teach-imperative-1", "u15-st-teach-say-request-1"]},
    {"id": "ref-u15-imperative-aspect", "title": "Imperatives of aspect pairs",
     "summary": "Each imperative keeps the aspect of its verb.",
     "detail": (DO_IMPERATIVES + " " + SAY_PAIR + " " + BOOK_GAP),
     "searchTerms": ["imperative aspect", "aspect pair", "perfective imperative"],
     "steps": ["u15-st-teach-aspect-consolidate-4"]},
]
BINDER = {"id": "binder-u15-imperative", "objectiveIds": ["obj-u15-imperative", "obj-u15-say-request", "obj-u15-aspect-consolidate"],
          "prompt": ("In your binder, write the singular and plural imperatives of this unit's five verbs, with each verb's aspect."
                     " Compare your work with the model answer."),
          "modelAnswer": ("чита́ть (imperfective): чита́й, чита́йте.\nсказа́ть (perfective): скажи́, скажи́те.\nдать (perfective): да́й,"
                          " да́йте.\nде́лать (imperfective): де́лай, де́лайте.\nсде́лать (perfective): сде́лай, сде́лайте.")}
TUTOR = {"id": "tutor-u15-imperative",
         "bring": "Your binder imperatives and the fruit request, to read aloud and say which form you would use for one friend or for several people."}

# --------------------------------------------------------------------------------------------------------------
# Facts: English-only sentences and their evidence. ("PN", n) = a page line record, ("V", row, field) = an OpenRussian cell.
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
    SHOWN, PAIR, BOOK_GAP,
    "Copy the displayed stress.",
    "Copy each displayed form carefully.",
    "The cue shows the plural/polite imperative; choose the singular imperative.",
    "Identify each member of the two infinitive pairs.",
    "Compare the aspect and the meaning group before selecting a supplied form.",
    "Identify the imperfective member of the verb pair while keeping the meaning the same.",
    "Read the explanation and compare the examples.",
    "Read the worked examples, then practise before answering independently.",
    "A wrong answer returns to this contrast.",
    "Practise first, then answer without the explanation in view.",
    "Recognize the member of this supplied pair.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


PLURAL_POLITE = p("RG", 1169) + p("PN", 58)
EXTRA: dict[str, list] = {
    "It extends the shown singular form with the plural/polite ending.": PLURAL_POLITE + [("V", "чита'ть", "imperative_sg"), ("V", "чита'ть", "imperative_pl")],
    "Both are imperative forms of the shown infinitive; the singular form is informal, while spelling alone does not identify the"
    " actual number of listeners.": p("RG", 1169) + [("V", "чита'ть", "imperative_sg"), ("V", "чита'ть", "imperative_pl")],
    "— Please, buy fresh fruits.": p("C", 81),
    "It can address multiple people or one person politely.": PLURAL_POLITE,
    "Select the plural imperative of this verb; its dictionary form is an infinitive, not a command.":
        [("V", "дать", "accented"), ("V", "дать", "imperative_pl")],
    "The cue shows the plural/polite imperative; choose the singular imperative of the same say/tell verb, keeping meaning and aspect"
    " the same.": [("V", "сказа'ть", "imperative_sg"), ("V", "сказа'ть", "imperative_pl"), ("V", "сказа'ть", "aspect")],
    PREFIX: p("B", 25, 26, 27, 28, 29),
    "Learn this supplied pair rather than assuming that adding any prefix gives the matching perfective of any verb.": p("B", 25, 26, 27, 28, 29),
    "Each imperative keeps the aspect of its verb.": [("V", "де'лать", "aspect"), ("V", "сде'лать", "aspect")],
    "Both are imperative forms of the same imperfective verb.": [("V", "чита'ть", "aspect"), ("V", "чита'ть", "imperative_sg"), ("V", "чита'ть", "imperative_pl")],
    SAY_PAIR: p("B", 42),
    IMPERATIVE_2["feedback"].split(". ")[0] + ".": PLURAL_POLITE,
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
            return self._record(page, raw, f"prov-t16e-u15-vid9-{number}", objective)
        raw = (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()
        if (page, number) in SPANS:  # a line that carries a citation URL: only its claim span is recorded
            span = SPANS[(page, number)]
            if span not in raw:
                raise SystemExit(f"{page}:{number} span is not on its line")
            raw = span
        return self._record(page, raw, f"prov-t16e-u15-{page.lower()}{number}", objective)

    def select(self, page: str, number: int, text: str, objective: str, record_type: str = "example") -> str:
        """A record of one displayed span of a frozen page line (an example sentence, a gloss or a word)."""
        raw = (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()
        if text not in d7b.displayed(raw):
            raise SystemExit(f"{text!r} is not displayed on {page}:{number}")
        record_id = f"prov-t16e-u15-{page.lower()}{number}-{sha(text)[:8]}"
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
            if (r["unitNumber"] == UNIT and r["objectiveId"] == objective and r["sourceRecordId"] == f"verbs.csv:{accented}#{field}"
                    and is_frozen(self.pack, r)):
                return r["itemId"]
        record = copy.deepcopy(self.records["prov-ex-or-u15-imperative-0"])
        shipped = stressed(original) if field == "accented" or field.startswith(("past_", "presfut_", "imperative_")) else original
        if field == "translations_en":
            shipped = original.split(",")[0].strip()  # the first English sense, as the Unit 13 gloss records show it
        record.update({"itemId": f"prov-t16e-u15-v-{sha(accented + field)[:8]}-{field}", "objectiveId": objective,
                       "sourceRecordId": f"verbs.csv:{accented}#{field}", "originalSourceText": original,
                       "shippedText": shipped, "sourceHash": sha(original), "contentHash": sha(shipped),
                       "recordType": "lexical record"})
        return self.add(record)


def is_frozen(pack: dict, record: dict) -> bool:
    return d7b.frozen_record(pack, record)


def admit_s1(pack: dict) -> None:
    """S1 froze Повелительное наклонение 152579064; D29a admits their revisions."""
    templates = {"wikipedia": "src-wikipedia-russian-grammar-1369617627", "ruwiki": "src-ruwiki-narechie-152437423"}
    for key, title in (("povelitelnoe", "Повелительное наклонение"),):
        page = S1[key]
        data = (SRC / "s1-intake" / page["file"]).read_bytes()
        if hashlib.sha256(data).hexdigest() != page["sha256"]:
            raise SystemExit(f"{key} does not match its FREEZE.json entry")
        if any(s["id"] == page["sourceId"] for s in pack["sources"]):
            raise SystemExit(f"{key} is already a pack source")
        template = next(s for s in pack["sources"] if s["id"] == templates[page["licenseRoute"]])
        pack["sources"].append({**copy.deepcopy(template), "id": page["sourceId"], "title": title, "attribution": page["attribution"]})
        pack["rights"].append({"id": "rights-" + page["sourceId"][4:], "sourceId": page["sourceId"], "license": "CC BY-SA 4.0",
                               "permitsAppUse": True})


def apply(pack: dict) -> tuple[dict, list]:
    baseline = copy.deepcopy(pack)  # before the sweep, so sweep wording is covered too
    sweep_log: list = []
    sweep.apply_sweep(pack, [], sweep_log, units={UNIT})
    admit_s1(pack)
    b = Builder(pack)
    unit = b.unit
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    sessions = {se["id"]: se for se in unit["sessions"]}
    R = b.records
    sentence_template = next(e for e in pack["examples"] if e["id"] == "ex-u15-imperative-3")
    lexical_template = next(e for e in pack["examples"] if e["id"] == "ex-or-u15-imperative-0")

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
    item, spec = items["u15-i-imperative-2"], IMPERATIVE_2
    a = item["assessment"]
    answer, foil = R[spec["answer"]]["shippedText"], R[spec["foil"]]["shippedText"]
    item.update({"promptLetter": R[spec["cue"]]["shippedText"], "options": [answer, foil], "answer": answer,
                 "feedback": spec["feedback"], "distractorRationales": ["", spec["rationale"]]})
    a.update({"testedFeature": spec["feature"], "answerSourceIds": [spec["answer"]], "distractorSourceIds": ["", spec["foil"]],
              "distractorDifference": ["", spec["rationale"]], "whyAnswerDemonstratesObjective": spec["feedback"],
              "contrastMembers": [answer, foil], "relationParticipantIds": [spec["cue"], spec["answer"], spec["foil"]]})
    typed = []
    for item_id, objective, cue, key, prompt in TYPED:
        cue_id, key_id = b.cell(cue[0], cue[1], objective), b.cell(key[0], key[1], objective)
        key_text = R[key_id]["shippedText"]
        if stressed(next(r for r in VERB_ROWS if r["accented"] == key[0])[key[1]]) != key_text:
            raise SystemExit(f"{item_id}: key is not the exact frozen stress")
        typed.append(typed_item(item_id, objective, prompt, R[cue_id]["shippedText"], key_text, TYPED_FEEDBACK, "src-openrussian-50e210c4"))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    items.update({i["id"]: i for i in typed})
    # Typed keys are taught answers: each key appears in the teaching of its objective.
    for item in typed:
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
        if step["body"] == CHECK_OLD:
            step["body"] = CHECK_NEW
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u15-s-book")
    unit["sessions"].insert(book, {"id": "u15-s-checks", "title": "Unit 15 checks", "estimatedMinutes": 25,
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
        raise SystemExit("Unit 15 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 15 learner field: Russian only through frozen Unit 15 records ------------------------------------
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
                raise SystemExit(f"Unit 15 region without a frozen record: {region!r} in {text[:80]!r}")
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
                raise SystemExit(f"No frozen Unit 15 record shows {form!r} ({sentence})")
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
        item_id = f"prov-ca-t16e-{key}"
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
        raise SystemExit("Asset is not the T16e baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report, sweep_log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps({"facts": report, "sweep": sweep_log}, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(sweep_log)} Unit 15 sweep rows applied; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
