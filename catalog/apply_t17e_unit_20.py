#!/usr/bin/env python3
"""T17e (Claude Code, owner direction 2026-11-23, run after T-AUDIO-1a): Unit 20 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T-AUDIO-1a DONE pack (81a8623c; T16c-n3 fc91121a is the last pack change). Plan: Codex
prep audit/prep/unit-20-prep-at-20bfde5c.txt (owner/prep-reports); the Units 16-30 learner-text sweep
(audit/T17a-learner-text-sweep-units-16-30.csv, Unit 20 rows, applied first; the two reviewCopy.introductions[20] rows are
superseded by the T16c-n2 rewrite under D37c/D43e and are skipped and logged). Russian comes only from frozen files already in the
source inventory: Wikipedia Russian grammar 1369617627 (G) and OpenRussian verbs.csv (V).
- Prep corrections: Он шёл к другу renders as "He was on his way to a friend's place" (G:1307); the unsupported "for a long time"
  duration leaves teaching and the rationale (G:1302, 1305-1307); the setting-out and round-trip readings stay bounded to their
  examples (G:1299, G:1302); the leading verb also drives a vehicle (G:1321).
- A2-24: every check moves to an end-of-unit session; the answer-selection script leaves teaching.
- A2-23: twelve typed checks with keys taught first in a new forms step (prep 2): 20 checked, 12 typed; two typed guided items.
  летят, ходит and ездил are unstressed in their frozen lines and get no stress.
- Facts: 89 English-only entries -> MARK, REWORD or REMOVE (prep 3).
- A2-26: governance and narration leave Unit 20 learner text; the mapping note follows the Units 1-19 style.
- The six pinned directionality tuples (four checks, two guided) keep their cues, keys, distractors and sources; the checked-item
  list in catalog/content-checks.json and the pack's contentChecks grows by the twelve typed checks (approved data update).
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
BASELINE_SHA256 = "a34662ee0067bbf3ed441ebb28652ba5f32fbae566f8bd6df18826ea630fbd35"  # the T-AUDIO-1a DONE pack (81a8623c)
SWEEP_CSV = ROOT / "audit/T17a-learner-text-sweep-units-16-30.csv"
SWEEP_CSV_SHA256 = "0ebf273853d01641275fce68bc396d4374f3fb082cd3d501210a6e84badec96f"
CONTENT_CHECKS = ROOT / "catalog/content-checks.json"
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T17e", "2026-11-25", 20
PREFIX = "prov-t17e-u20"
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "G": ("units-26-30/wikipedia/russian-grammar-1369617627.txt", "prov-d7b-u20-034", "grammar:"),
}
SOURCE = {"G": "src-wikipedia-russian-grammar-1369617627", "V": "src-openrussian-50e210c4"}
TABLES = {"V": SRC / "units-3-5/openrussian/verbs.csv"}
TABLE_NAME = {"V": "verbs.csv"}
ROWS = {k: list(csv.DictReader(p.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE)) for k, p in TABLES.items()}
CELL_TEMPLATE = {"V": "prov-ex-or-u12-future-perf-2"}
M = "obj-u20-motion"
# The two sweep rows of reviewCopy.introductions[20] carry the pre-T16c-n2 text; D37c/D43e rewrote that introduction.
SUPERSEDED_SWEEP = {"reviewCopy.introductions[20]"}

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 20 covers verbs of motion: one direction or several, on foot or by vehicle, carrying and leading, setting out,"
         " and selected prefixed paths, alongside Lesson 20 in your book.")
MAPPING = ("This unit goes with Lesson 20 in your book, printed pages 240-256: unidirectional and multidirectional verbs of motion,"
           " motion on foot or by transport, carrying and leading pairs, and selected prefixed motion verbs.")
PRACTICE_BODY = ("Select the answer for a shown form, or type the Russian asked for, using the explanations above. A missed check"
                 " returns to the complete explanation and examples.")
REVIEW_EXAMPLE = "Review the complete example before answering."  # prep 3 REWORD (2291, 2299, 2375, 2383, 2391, 2399)
PERFECTIVE_OPTION = "The form is perfective because this trip is completed."
PERFECTIVE_WHY = "These unprefixed motion forms are imperfective; completing a trip does not by itself change that classification."
DURATION_WHY = "he was already on the way for a long time adds a long duration that the sentence does not state."  # prep 3, 2365
FORMS_STEP = {
    "id": "u20-st-teach-forms", "kind": "TEACH", "title": "Partners, past forms and present forms",
    "body": ("Each unidirectional verb has a multidirectional partner: идти́ and ходи́ть, е́хать and е́здить, нести́ and носи́ть,"
             " вести́ and води́ть, везти́ and вози́ть. Practise writing each verb from its meaning and from its partner."
             " Past forms agree with the subject: шёл is the masculine past form of идти́, пошёл of пойти́, and отошёл of отойти́."
             " ездил is the masculine past form of е́здить. Present forms agree with the subject's person and number: идём is the"
             " we form of идти́, and ходит is the he or she form of ходи́ть. For flying, летят is the unidirectional they form and"
             " летают the multidirectional they form. Мы идём в библиотеку. Птицы летят на юг. Ребёнок ходит шесть месяцев."
             " В прошлом году я ездил в Россию. Practise writing each of these forms from the verb it belongs to."),
}
TEACH = {  # step: [(old, new)]; new "" deletes the sentence and its leading space
    "u20-st-teach-motion-3": [
        (" For нести́ choose to carry.", ""),  # prep 1: answer-selection script (ANSWER_IN_INSTRUCTION_SIGNAL)
        (" Distinguish a motion verb’s meaning from its directionality.", ""),  # prep 3, 2317 REMOVE (sweep wording)
        ("вести́ — to lead.", "вести́ — to lead; it can also mean to drive a vehicle."),  # prep 4, G:1321
    ],
    "u20-st-teach-motion-4": [
        ("Он шёл к другу. — He went to a friend's place.", "Он шёл к другу. — He was on his way to a friend's place."),  # G:1307
        (" The distractor he was already on the way for a long time adds duration that is not supplied, and he refused to go"
         " contradicts the setting-out sentence.",
         " The reading he was already on the way for a long time adds a long duration that the sentence does not state, and he"
         " refused to go contradicts the setting-out sentence."),  # prep 1 and 4: no distractor framing; the duration is not endorsed
    ],
    "u20-st-teach-direction-return": [
        # The sweep reworded the answer commentary; the wrong option stays quoted (the tests require each option in teaching) and
        # is marked wrong, never stated as a fact.
        ("A completed trip does not make these verb forms perfective.",
         "The statement “The form is perfective because this trip is completed.” is wrong: a completed trip does not make these"
         " verb forms perfective."),
        ("The past round-trip interpretation includes a return;", "In this example the past round-trip interpretation includes a"
                                                                  " return;"),  # prep 4: bounded to G:1299
    ],
}
NOTE_FIX = {"ex-u20-motion-5": ("He went to a friend's place", "He was on his way to a friend's place")}  # G:1307
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
PARTNER = "Type the unidirectional partner of the shown verb ({})."
PAST = "Type the masculine past form of the shown verb."
TYPED = [  # (id, cue spec, key spec, prompt); a spec is (page, line, text) or ("V", accented, field)
    ("u20-i-type-idti", ("G", 1268, "ходи́ть"), ("G", 1268, "идти́"), PARTNER.format("to go, walk")),
    ("u20-i-type-ekhat", ("G", 1266, "е́здить"), ("G", 1266, "е́хать"), PARTNER.format("to go by vehicle, ride")),
    ("u20-i-type-nesti", ("G", 1276, "носи́ть"), ("G", 1276, "нести́"), PARTNER.format("to carry")),
    ("u20-i-type-vesti", ("G", 1262, "води́ть"), ("G", 1262, "вести́"), PARTNER.format("to lead")),
    ("u20-i-type-vezti", ("G", 1260, "вози́ть"), ("G", 1260, "везти́"), PARTNER.format("to convey, transport")),
    ("u20-i-type-poshel", ("V", "пойти'", "accented"), ("V", "пойти'", "past_m"), PAST),
    ("u20-i-type-shel", ("V", "идти'", "accented"), ("V", "идти'", "past_m"), PAST),
    ("u20-i-type-otoshel", ("V", "отойти'", "accented"), ("V", "отойти'", "past_m"), PAST),
    ("u20-i-type-idem", ("V", "идти'", "accented"), ("V", "идти'", "presfut_pl1"), "Type the we form of the shown verb."),
    ("u20-i-type-letyat", ("G", 1293, "летают"), ("G", 1289, "летят"),
     "Type the unidirectional they form that matches the shown multidirectional form."),
    ("u20-i-type-khodit", ("G", 1268, "ходи́ть"), ("G", 1292, "ходит"), "Type the he or she form of the shown verb."),
    ("u20-i-type-ezdil", ("G", 1266, "е́здить"), ("G", 1299, "ездил"), PAST),
]
GUIDED = [  # practice before the checks; the keys differ from every check key
    ("u20-g-type-nosit", "u20-st-guided-motion-1", ("G", 1276, "нести́"), ("G", 1276, "носи́ть"),
     "Practice: type the multidirectional partner of the shown verb."),
    ("u20-g-type-vodit", "u20-st-guided-motion-1", ("G", 1262, "вести́"), ("G", 1262, "води́ть"),
     "Practice: type the multidirectional partner of the shown verb."),
]
REFERENCES = [
    {"id": "ref-u20-motion-pairs", "title": "Verbs of motion: pairs and forms",
     "summary": "The five motion pairs with their meanings, and the past and present forms taught in Unit 20.",
     "detail": ("идти́ and ходи́ть — to go, walk. е́хать and е́здить — to go by vehicle, ride. нести́ and носи́ть — to carry."
                " вести́ and води́ть — to lead; вести́ can also mean to drive a vehicle. везти́ and вози́ть — to convey, transport."
                " The first verb of each pair is unidirectional, the second multidirectional. шёл, пошёл and отошёл are masculine"
                " past forms; ездил is the masculine past form of е́здить. идём is the we form of идти́, ходит the he or she form"
                " of ходи́ть, and летят the unidirectional they form for flying."),
     "searchTerms": ["verbs of motion", "unidirectional", "multidirectional", "carry", "lead"],
     "steps": ["u20-st-teach-motion-1", "u20-st-teach-motion-3", "u20-st-teach-forms"]},
    {"id": "ref-u20-directionality", "title": "Directionality in context",
     "summary": "How a whole example shows one direction, general motion, various directions, repeated trips or a round trip.",
     "detail": ("Мы идём в библиотеку. — We are headed to the library: motion in progress in one direction. Ребёнок ходит шесть"
                " месяцев. — The child has been walking for six months: general walking ability. Мы ходили по городу весь день."
                " — We walked around the city all day: movement in various directions. Она ходит в супермаркет каждую неделю."
                " — She goes to the supermarket every week: repeated trips. В прошлом году я ездил в Россию. — I went to Russia"
                " (and returned) last year: one completed round trip in this example. The unprefixed motion verbs are imperfective."
                " Он пошёл к другу. — he set off toward a friend: the prefixed verb presents the beginning of the journey."),
     "searchTerms": ["directionality", "round trip", "setting out", "motion verbs"],
     "steps": ["u20-st-teach-direction-contexts", "u20-st-teach-direction-return", "u20-st-teach-motion-4"]},
]
ATTRIBUTION: dict = {}  # ref-u1620-attribution is shared with Units 16, 18 and 19 and is not edited
BINDER = {"id": "binder-u20-motion", "objectiveIds": [M],
          "prompt": ("In your binder, write the five motion pairs with their meanings, then each past and present form from this"
                     " unit with the verb it belongs to. Compare your work with the model answer."),
          "modelAnswer": ("идти́ and ходи́ть — to go, walk; е́хать and е́здить — to go by vehicle, ride; нести́ and носи́ть — to"
                          " carry; вести́ and води́ть — to lead; везти́ and вози́ть — to convey, transport.\n"
                          "шёл — masculine past of идти́; пошёл — masculine past of пойти́; отошёл — masculine past of отойти́;"
                          " ездил — masculine past of е́здить.\n"
                          "идём — the we form of идти́; ходит — the he or she form of ходи́ть; летят — the unidirectional they form"
                          " for flying; летают — the multidirectional they form.")}
TUTOR = {"id": "tutor-u20-motion",
         "bring": "Your binder motion pairs and forms, to read aloud and say what each one means."}

# --------------------------------------------------------------------------------------------------------------
# Facts. (page, n) = a page line record, (table, accented, field) = an OpenRussian cell.
# --------------------------------------------------------------------------------------------------------------
NONFACTS = {
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    REVIEW_EXAMPLE, PRACTICE_BODY.split(". ")[0] + ".", PERFECTIVE_OPTION,
    "Practise writing each verb from its meaning and from its partner.",
    "Practise writing each of these forms from the verb it belongs to.",
    "Interpret the whole sentence, rather than translating an isolated verb or naming its directionality category.",
    "Do not equate a destination with unidirectionality or event completion with perfective aspect.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


DIRECTION = p("G", 1248, 1286, 1287, 1289, 1290, 1291, 1292, 1294, 1295, 1296, 1297, 1298, 1299)
MEANING = p("G", 1260, 1262, 1266, 1268, 1276, 1313, 1316, 1321, 1327)
SETTING_OUT = p("G", 1302, 1305, 1307)
PATHS = p("G", 1382, 1383, 1394, 1395, 1415)  # G:1335 carries a URL and is not shipped
FORMS = [("V", "идти'", "past_m"), ("V", "пойти'", "past_m"), ("V", "отойти'", "past_m"), ("V", "идти'", "presfut_pl1")]
DEFAULT = {M: DIRECTION}


def evidence(sentence: str) -> list:
    """The prep 3 MARK evidence ranges, chosen by what the sentence is about."""
    s = sentence.lower()
    if any(w in s for w in ("past form", "we form", "he or she form", "they form", "agree with the subject")):
        return FORMS + p("G", 1268, 1289, 1292, 1293, 1299)
    if any(w in s for w in ("set off", "setting out", "setting-out", "beginning of the journey", "arrived", "on his way", "on the way",
                            "duration", "friend")):
        return SETTING_OUT
    if any(w in s for w in ("stranger", "vladivostok", "roof", "path", "relative clause", "prefix", "subjects")):
        return PATHS
    if any(w in s for w in ("walk", "vehicle", "carry", "lead", "convey", "transport", "foot", "means of moving", "drive",
                            "partner")):
        return MEANING + p("G", 1248)
    return DIRECTION


EXTRA: dict[str, list] = {
    "Directionality is a property of the whole supplied use, not an English lexical gloss or an automatic claim that the traveller"
    " arrived.": DIRECTION + SETTING_OUT,
    "Prefixed setting-out and path examples require their separately taught meanings.": SETTING_OUT + PATHS,
    "he refused to go contradicts the motion verb.": SETTING_OUT,
    "is a different motion sentence.": PATHS,
}
FACTS: set[str] = {  # prep 3 MARK sentences without a Cyrillic form; evidence(sentence) or EXTRA
    "The statement “The form is perfective because this trip is completed.” is wrong: a completed trip does not make these verb forms perfective.",
    "The reading he was already on the way for a long time adds a long duration that the sentence does not state, and he refused to go contradicts the setting-out sentence.",
    "A completed trip does not make these verb forms perfective.",
    "Carrying on foot, leading someone and transporting by vehicle are distinct meanings.",
    "Directionality is a property of the whole supplied use, not an English lexical gloss or an automatic claim that the traveller arrived.",
    "In the context this is One completed round trip in the past (imperfective).",
    "In this example the past round-trip interpretation includes a return; the unprefixed form nevertheless remains imperfective.",
    "Its other column is multidirectional, covering repeated, return or different-direction movement according to context.",
    "Motion in progress in one direction (imperfective).",
    "Movement in various directions (imperfective).",
    "One directed journey in progress is not the supplied general, various-direction, repeated-trip or completed-return reading.",
    "Prefixed setting-out and path examples require their separately taught meanings.",
    "Repeated trips (imperfective).",
    "Repeated trips are not a single movement now in progress or merely movement around an area.",
    "The answer is General walking ability (imperfective).",
    "The answer is Motion in progress in one direction (imperfective).",
    "The answer is Movement in various directions (imperfective).",
    "The answer is One completed round trip in the past (imperfective).",
    "The answer is Repeated trips (imperfective).",
    "The ball flew onto the roof of the house.",
    "The basic unprefixed forms in these columns are imperfective; unidirectional does not itself mean perfective or arrival.",
    "The basic unprefixed pairs are imperfective.",
    "The displayed sentence supplies no additional relative clause.",
    "The first describes movement on foot; the second movement by vehicle.",
    "The past example is a completed round trip in the supplied interpretation; its unprefixed verb is still imperfective.",
    "The perfective prefixed verb presents the beginning of the journey; it does not say that he arrived.",
    "The prefix contributes to the motion meaning, while the accompanying phrases identify the path and time; do not substitute a translation of a different example.",
    "The supplied child example describes walking ability over time, rather than a journey now aimed at one destination.",
    "The supplied city example describes movement around the city in various directions, not a single directed journey.",
    "The supplied context describes motion in progress in one direction; it does not assert completed arrival.",
    "The supplied weekly example describes repeated trips, not one current journey toward the shop.",
    "The verb and the phrase after it express movement away from the stranger.",
    "There is no additional relative clause in this displayed sentence.",
    "These are different means of moving, not an aspect pair.",
    "These are different subjects and paths.",
    "These directionality labels are not English verb meanings.",
    "These supplied contexts show Motion in progress in one direction (imperfective).",
    "These unprefixed motion forms are imperfective; completing a trip does not by itself change that classification.",
    "They will leave Vladivostok in a month.",
    "This English rendering accompanies an imperfective form describing motion toward the friend; the Russian form alone does not specify how long he was on the way.",
    "This is General walking ability (imperfective).",
    "This is Movement in various directions (imperfective).",
    "This is Repeated trips (imperfective).",
    "This label identifies directionality, not aspect.",
    "Various-direction movement around an area is not the supplied directed flight or weekly repeated trips.",
    "he refused to go contradicts the motion verb.",
    "he was already on the way for a long time adds a long duration that the sentence does not state.",
    "is a different motion sentence.",
    "multidirectional is the other motion column, covering different-direction or repeated movement according to context.",
    "to carry describes carrying something on foot.",
    "to convey, transport describes transporting by vehicle.",
    "to go by vehicle, ride describes movement by vehicle.",
    "to lead describes leading someone.",
    "— He was on his way to a friend's place.",
    "— I went to Russia (and returned) last year.",
    "— She goes to the supermarket every week.",
    "— The ball flew onto the roof of the house.",
    "— The birds are flying south.",
    "— The child has been walking for six months.",
    "— They will leave Vladivostok in a month.",
    "— We are headed to the library.",
    "— We walked around the city all day.",
    "— he set off toward a friend.",
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
        if not d7b.contains_whole(shown, text):  # T16c-n1 (D39a): whole words only (was: any substring)
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


def spec_record(b: "Builder", spec: tuple, objective: str) -> str:
    """A cue or key record: a whole word on a frozen G line, or an OpenRussian cell (stress apostrophe as the mark)."""
    if spec[0] == "V":
        return b.cell("V", spec[1], spec[2], objective)
    return b.select(spec[0], spec[1], spec[2], objective, "lexical record")


def filtered_sweep() -> Path:
    """The sweep CSV without the superseded rows, written beside the scratch outputs; the skipped rows are logged."""
    rows = list(csv.DictReader(SWEEP_CSV.open(encoding="utf-8", newline="")))
    kept = [r for r in rows if not (r["unit"] == str(UNIT) and r["owner.field"] in SUPERSEDED_SWEEP)]
    out = Path("/tmp/claude-0/-home-user-russian-companion/8476c374-e1ab-5f3d-9af8-fc234c0d6e90/scratchpad/t17e/sweep-filtered.csv")
    out.parent.mkdir(parents=True, exist_ok=True)
    with out.open("w", encoding="utf-8", newline="") as f:
        w = csv.DictWriter(f, fieldnames=list(rows[0].keys()))
        w.writeheader()
        w.writerows(kept)
    return out


def apply(pack: dict) -> tuple[dict, list]:
    baseline = copy.deepcopy(pack)  # before the sweep, so sweep wording is covered too
    if hashlib.sha256(SWEEP_CSV.read_bytes()).hexdigest() != SWEEP_CSV_SHA256:
        raise SystemExit("Sweep CSV changed")
    filtered = filtered_sweep()
    sweep.SWEEP, sweep.SWEEP_SHA256 = filtered, hashlib.sha256(filtered.read_bytes()).hexdigest()
    sweep_log: list = [(ref, "(superseded by the T16c-n2 introduction rewrite, D37c/D43e)", "") for ref in sorted(SUPERSEDED_SWEEP)]
    sweep.apply_sweep(pack, [], sweep_log, units={UNIT})
    b = Builder(pack)
    unit = b.unit
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R_ = b.records
    # --- teaching corrections (prep 1, 3, 4) -------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], old, new, step_id)
    # Он шёл к другу renders as G:1307; the example's English note and its selection record follow (prep 4).
    on_way = "He was on his way to a friend's place"
    for example_id, (old, new) in NOTE_FIX.items():
        example = next(e for e in pack["examples"] if e["id"] == example_id)
        example["note"] = edit(example["note"], old, new, example_id)
        record = R_["prov-ex-u20-motion-5-en"]
        if record["shippedText"] != old or not d7b.contains_whole(d7b.displayed(record["originalSourceText"]), new):
            raise SystemExit("the motion-5 English record does not hold the G:1307 rendering")
        record["shippedText"], record["contentHash"] = new, sha(new)
    b.select("G", 1307, on_way, M, "meaning")
    b.select("G", 1321, "to drive (a vehicle)", M, "meaning")
    # --- the new forms step: partners, past and present forms, taught before any typed retrieval (prep 2) -------------
    for text, line in (("ходи́ть", 1268), ("е́здить", 1266), ("носи́ть", 1276), ("води́ть", 1262), ("вози́ть", 1260),
                       ("летят", 1289), ("ходит", 1292), ("ездил", 1299), ("летают", 1293)):
        b.select("G", line, text, M)
    for spec in FORMS + [("V", "пойти'", "accented"), ("V", "отойти'", "accented")]:
        b.cell(*spec, M)
    # Они улетят ... (G:1383 bolds the prefix у-, which is not a stress mark): the form is cited to its OpenRussian cell (V:1177).
    b.cell("V", "улете'ть", "presfut_pl3", M)
    b.cell("V", "залете'ть", "past_m", M)  # Мяч залетел ... (G:1415 bolds за-)
    motion = next(se for se in unit["sessions"] if se["id"] == "u20-s-motion")
    guided_at = next(k for k, st in enumerate(motion["steps"]) if st["kind"] == "GUIDED")
    motion["steps"].insert(guided_at, copy.deepcopy(FORMS_STEP))
    objectives[M]["instructionStepIds"].append(FORMS_STEP["id"])
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    for step in steps.values():
        if step["kind"] in ("GUIDED", "CHECK"):
            step["body"] = PRACTICE_BODY
    # --- the six directionality items: wording only; cues, keys, distractors and sources stay pinned -------------------
    for item in items.values():
        if not item["objectiveId"] == M or "direction" not in item["id"]:
            continue
        item["feedback"] = edit(item["feedback"], "The supplied interpretation is bounded; these labels do not classify every"
                                " prefixed verb.", REVIEW_EXAMPLE, item["id"])
        a = item["assessment"]
        a["whyAnswerDemonstratesObjective"] = item["feedback"]
        for k, why in enumerate(item["distractorRationales"]):
            if why.startswith(PERFECTIVE_OPTION):
                item["distractorRationales"][k] = PERFECTIVE_OPTION + " " + PERFECTIVE_WHY
                a["distractorDifference"][k] = item["distractorRationales"][k]
    # --- u20-i-motion-3: the duration distractor is explained without endorsing the duration (prep 3, 2365) ------------
    m3 = items["u20-i-motion-3"]
    old = "he was already on the way for a long time is an imperfective duration reading."
    k = m3["distractorRationales"].index(old)
    m3["distractorRationales"][k] = DURATION_WHY
    if m3["assessment"]["distractorDifference"][k] == old:
        m3["assessment"]["distractorDifference"][k] = DURATION_WHY
    # --- remediation repeats the whole teaching -----------------------------------------------------------------------
    for se in unit["sessions"]:
        for st in se["steps"]:
            if st["kind"] == "TEACH":
                st["body"] = st["body"].strip()
        rem = [st for st in se["steps"] if st["kind"] == "REMEDIATE"]
        if rem:
            rem[0]["body"] = "\n\n".join(st["body"] for st in se["steps"] if st["kind"] == "TEACH")
            rem[0]["exampleIds"] = list(dict.fromkeys(x for st in se["steps"] if st["kind"] == "TEACH" for x in st.get("exampleIds") or []))
    # --- typed checks and guided typed practice ------------------------------------------------------------------------
    typed, guided = [], []

    def build(item_id, cue, key, prompt):
        cue_text = R_[spec_record(b, cue, M)]["shippedText"]
        key_id = spec_record(b, key, M)
        answer = R_[key_id]["shippedText"]
        if key[0] != "V" and answer != key[2]:
            raise SystemExit(f"{item_id}: key is not the exact frozen text")
        item = typed_item(item_id, M, prompt, cue_text, answer, TYPED_FEEDBACK, SOURCE[key[0]])
        ordered = {}
        for kk, vv in item.items():
            ordered[kk] = vv
            if kk == "typedAnswerKey":
                ordered["keyCitation"] = key_id  # D39b: the frozen record the key comes from
        return ordered
    for item_id, cue, key, prompt in TYPED:
        typed.append(build(item_id, cue, key, prompt))
    for item_id, step_id, cue, key, prompt in GUIDED:
        item = build(item_id, cue, key, prompt)
        guided.append(item)
        steps[step_id]["itemIds"].append(item_id)
    for item in typed:
        objectives[M]["checkItemIds"].append(item["id"])
    pack["items"].extend(guided + typed)
    items.update({i["id"]: i for i in guided + typed})
    taught = " ".join(steps[s]["body"] for s in objectives[M]["instructionStepIds"])
    for item in typed + guided:  # typed keys are taught answers
        if not re.search(f"(?<![{CYR}{ACUTE}]){re.escape(item['answer'])}(?![{CYR}{ACUTE}])", taught):
            raise SystemExit(f"{item['id']}: key {item['answer']} is not taught")
    if len({i["answer"] for i in typed + guided}) != len(typed + guided):
        raise SystemExit("a key repeats")
    for k in ("летят", "ходит", "ездил"):  # unstressed in their frozen lines: no stress added
        if not any(i["answer"] == k for i in typed):
            raise SystemExit(f"{k} must stay unstressed")
    # --- the content-check pin: the twelve typed checks join the checked-item list (approved data update) -------------
    checks = json.loads(CONTENT_CHECKS.read_text(encoding="utf-8"))
    for container in (checks, pack["contentChecks"]):
        pin = next(o for c in container for o in c["objectives"] if o["id"] == M)
        if pin["checkItemIds"] != baseline_check_ids(baseline):
            raise SystemExit("the obj-u20-motion content-check pin changed")
        pin["checkItemIds"] = list(objectives[M]["checkItemIds"])
    CONTENT_CHECKS_OUT.append(json.dumps(checks, ensure_ascii=False, indent=2) + "\n")
    # --- unit fields; checks move to an end-of-unit session ---------------------------------------------------------
    unit["topicSummary"], unit["mappingNote"] = TOPIC, MAPPING
    check_steps = []
    for session in unit["sessions"]:
        kept = []
        for step in session["steps"]:
            (check_steps if step["kind"] == "CHECK" else kept).append(step)
        session["steps"] = kept
    for step in check_steps:
        step["itemIds"] = list(objectives[M]["checkItemIds"])
        step["title"] = "Check: " + objectives[M]["title"]
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u20-s-book")
    unit["sessions"].insert(book, {"id": "u20-s-checks", "title": "Unit 20 checks", "estimatedMinutes": 20,
                                   "objectiveIds": [M], "steps": check_steps})
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
        raise SystemExit("Unit 20 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 20 learner field: Russian only through frozen Unit 20 records ------------------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and d7b.frozen_record(pack, r)}
    texts = [s["body"] for s in steps.values()] + [unit["topicSummary"], unit["mappingNote"], BINDER["prompt"], BINDER["modelAnswer"]]
    texts += [r[k] for r in pack["references"] if r["id"] in {s["id"] for s in REFERENCES} for k in ("title", "summary", "detail")]
    texts += [e["display"] for e in pack["examples"] if e.get("objectiveId", "").startswith(f"obj-u{UNIT}-")]
    for item in items.values():
        if item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            texts += [item["prompt"], item.get("promptLetter") or "", item["answer"], item["feedback"]] + item["options"] + item.get("distractorRationales", [])
    unshown = set()
    for text in texts:
        for mm in REGION.finditer(text):
            region = mm.group(0).strip()
            if region not in frozen and region.rstrip("?!.,") not in frozen:  # exact, as V2Validator requires
                unshown.add(region)
    if unshown:
        raise SystemExit(f"Unit 20 regions without a frozen record: {sorted(unshown)}")
    report = mark_facts(b, steps, items)
    cover_english(pack, baseline)
    rebind_capability_spans(pack, baseline)
    return report, sweep_log


CAPABILITY_SPANS = ("cap-u20-motion-po-setting-out", "cap-u20-motion-prefixed-path")
SPAN_HASHES: list = []  # (capability, old hash, new hash) for ContentAuditCapabilityPins.kt


def rebind_capability_spans(pack: dict, before: dict) -> None:
    """The two corrected-intake capabilities hold verbatim copies of every Unit 20 instruction and remediation body, and the
    validator requires each current body in the span (ContentAuditCapabilityChecks). As in T15f, each old body copy is replaced
    by the new body, the new forms step is appended, the host records that carry the span follow, and the hashes are
    recomputed: capability sourceHash = sha256(exactSpan), record sourceHash = sha256(originalSourceText)."""
    def bodies(p: dict) -> dict:
        unit = next(u for u in p["units"] if u["number"] == UNIT)
        objective = next(o for o in unit["objectives"] if o["id"] == M)
        steps = {st["id"]: st for se in unit["sessions"] for st in se["steps"]}
        return {sid: steps[sid]["body"] for sid in objective["instructionStepIds"] + objective["remediationStepIds"]}
    old, new = bodies(before), bodies(pack)
    for cap in pack["sourceCapabilities"]:
        if cap["id"] not in CAPABILITY_SPANS:
            continue
        span = cap["exactSpan"]
        if cap["sourceHash"] != sha(span):
            raise SystemExit(f"{cap['id']}: sourceHash did not follow exactSpan")
        updated = span
        for sid in sorted(old, key=lambda k: -len(old[k])):  # longest first, so a body inside another is not split
            if old[sid] != new[sid]:
                if old[sid] not in span:
                    raise SystemExit(f"{cap['id']}: {sid} was not bound")
                updated = updated.replace(old[sid], new[sid])
        for sid in new:
            if sid not in old:
                updated = updated + "\n\n" + new[sid]
        missing = [sid for sid in new if new[sid] not in updated]
        if missing:
            raise SystemExit(f"{cap['id']}: bodies still unbound {missing}")
        for record in pack["provenanceRecords"]:
            if record["originalSourceText"] == span:
                if record["sourceHash"] != sha(span):
                    raise SystemExit(f"{record['itemId']}: sourceHash did not follow originalSourceText")
                record["originalSourceText"], record["sourceHash"] = updated, sha(updated)
                # A host that ships a step body ships the new body (content-audit identity: shipped text inside the span).
                shipped = record["shippedText"]
                for sid in sorted(old, key=lambda k: -len(old[k])):
                    if old[sid] != new[sid] and shipped == old[sid]:
                        if record["contentHash"] != sha(shipped):
                            raise SystemExit(f"{record['itemId']}: contentHash did not follow shippedText")
                        record["shippedText"], record["contentHash"] = new[sid], sha(new[sid])
                        break
                if record["shippedText"] not in updated:
                    raise SystemExit(f"{record['itemId']}: shipped text is not in the rebound span")
        SPAN_HASHES.append((cap["id"], cap["sourceHash"], sha(updated)))
        cap["exactSpan"], cap["sourceHash"] = updated, sha(updated)


CONTENT_CHECKS_OUT: list = []


def baseline_check_ids(pack: dict) -> list:
    unit = next(u for u in pack["units"] if u["number"] == UNIT)
    return next(o for o in unit["objectives"] if o["id"] == M)["checkItemIds"]


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
                raise SystemExit(f"No frozen Unit 20 record shows {form!r} ({sentence})")
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
                    refs = evidence(sentence)
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
        Path("/tmp/claude-0/-home-user-russian-companion/8476c374-e1ab-5f3d-9af8-fc234c0d6e90/scratchpad/t17e/missing.json").write_text(
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
        item_id = f"prov-ca-t17e-{key}"
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
        raise SystemExit("Asset is not the T17e baseline; this transformer runs only once")
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
    CONTENT_CHECKS.write_text(CONTENT_CHECKS_OUT[0], encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps({"facts": report, "sweep": sweep_log}, ensure_ascii=False, indent=1), encoding="utf-8")
    print("capability span hashes:", SPAN_HASHES)
    print(f"{len(sweep_log)} Unit 20 sweep rows applied; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
