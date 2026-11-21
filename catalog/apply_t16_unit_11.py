#!/usr/bin/env python3
"""T16 (Claude Code, owner direction 2026-11-20): Unit 11 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T15i pack. Plan: Codex's read-only preparation report
audit/prep/units-11-12-prep-at-9a9b9760.txt (branch owner/prep-reports), Unit 11 section; every cited line re-checked.
Russian comes only from frozen files already in the source inventory: Wikibooks Russian/Grammar/Past_tense oldid 4567337
(P = units-11-15/wikibooks/past-tense-4567337.txt) and OpenRussian verbs.csv (V = units-3-5/openrussian/verbs.csv).
- A2-24: every Unit 11 check moves to an end-of-unit check session after all teaching; "admitted ... row/line" prompts
  become plain questions; u11-i-past-be-2 shows быть and asks for бы́л (V:2), so the two be checks no longer share a key.
- A2-23: twelve typed checks whose keys are taught answers with their exact frozen stress (report section 2).
- Facts: the twelve English-only inventory entries get the report's dispositions; every feedback, rationale and
  remediation sentence is marked with frozen-source citations or is a reviewed instruction.
- Section 4: the past-of-быть sentence no longer puts быть outside the regular past formation (P:38, P:68, P:118), and
  "It conjugates as all regular verbs do" is bounded to the listed past forms (V:2).
- A2-26: governance wording and wiki markup leave Unit 11 learner text (D8).
- A2-36: references ref-u11-past and ref-u11-reflexive; binder and tutor tasks (model answer quotes taught lines only).
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
import apply_t15g_block2 as t15g  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "bff87cc01780ee5191feec4b1c2be3f34b475da249df35b15dca5b91a763b6c3"  # the T15i pack (d74e3d9f)
SRC = ROOT / "catalog/sources"
PAST = SRC / "units-11-15/wikibooks/past-tense-4567337.txt"
VERBS = SRC / "units-3-5/openrussian/verbs.csv"
TASK = "T16"
DATE = "2026-11-20"
ACUTE = "́"
CYR = "Ѐ-ӿ"
UNIT = 11


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


with VERBS.open(encoding="utf-8") as _handle:
    VERB_ROWS = list(csv.DictReader(_handle, delimiter="\t", quoting=csv.QUOTE_NONE))
PAST_LINES = PAST.read_text(encoding="utf-8").split("\n")
ENDING_SPAN = ("If the subject of the verb is feminine, add '''-ла'''. If it is neuter, add '''-ло''', and if it is"
               " plural, add '''-ли''':")


def verb_cell(accented: str, field: str) -> str:
    rows = [r for r in VERB_ROWS if r["accented"] == accented]
    if len(rows) != 1:
        raise SystemExit(f"verbs.csv: {len(rows)} rows for {accented}")
    return rows[0][field].strip()


# --------------------------------------------------------------------------------------------------------------
# Wording (D8): titles, prompts, bodies
# --------------------------------------------------------------------------------------------------------------
OBJECTIVE_TITLES = {"obj-u11-past-read": "Past-tense endings", "obj-u11-past-be": "The past tense of to be",
                    "obj-u11-past-aspect": "Past-tense gender agreement", "obj-u11-reflexive": "Reflexive verbs in the past"}
SESSION_TITLES = {"u11-s-orient": "Past tense and reflexive verbs", **{f"u11-s-{k[8:]}": v for k, v in OBJECTIVE_TITLES.items()}}
STEP_TITLES = {"Admitted lines": "Examples", "Admitted instruction": "Explanation",
               "Admitted past and reflexive rows": "Past tense and reflexive verbs"}
ORIENT_BODY = ("This unit covers the past tense: its endings for gender and number, the past tense of the verb to be, and"
               " reflexive verbs in the past.")
TOPIC = ("Unit 11 covers past-tense endings and agreement, the past tense of the verb to be and reflexive verbs in the past,"
         " alongside Lesson 11 in your book.")
GUIDED_OLD = "Choose the admitted record. The feedback states the frozen field."
GUIDED_NEW = "Choose an answer, then read the feedback."
CHECK_OLD = "These checks use only admitted records. A miss returns to the explanation."
CHECK_NEW = "Answer without looking back. A miss returns to the explanation."
P_FORM = "Which {} form of the same verb corresponds to the shown form?"
P_LINE = "Which line shows the feminine past form with a singular subject?"
FEM, MASC = "feminine past form (singular)", "masculine past form (singular)"
PROMPTS = {"u11-g-past-read-0": P_FORM.format(FEM), "u11-i-past-read-1": P_FORM.format(MASC),
           "u11-i-past-read-2": P_LINE, "u11-g-past-be-0": P_FORM.format(FEM),
           "u11-i-past-be-1": P_FORM.format("plural past form"), "u11-g-reflexive-0": P_FORM.format(FEM),
           "u11-i-reflexive-1": P_LINE, "u11-i-reflexive-2": P_FORM.format(FEM)}

READ2 = ("To form the past tense, remove -ть from the infinitive and add -л. Add -ла if the subject is feminine, -ло if it is"
         " neuter and -ли if it is plural. Some verbs, such as идти, have irregular past forms that cannot be predicted.")
BE2 = ("The past forms of быть, to be, follow the regular pattern: бы́л for a masculine subject, была́ for a feminine"
       " subject, бы́ло for a neuter subject and бы́ли for a plural subject.")
REFLEXIVE2 = ("The infinitive улыба́ться, to smile, has a reflexive ending. To form its past tense, remove the reflexive ending,"
              " form the past tense, then reattach the ending: -ся after a consonant and -сь after a vowel. Compare the past"
              " forms in the worked examples. Copy the whole form.")
TEACH = {
    "u11-st-teach-past-read-2": [(None, READ2)],
    "u11-st-teach-past-be-1": [(" Read this as a review-pending explanation. Worked examples come before the guided choice,"
                                " and the independent check is separate. A wrong answer returns to this contrast. Activity is"
                                " not demonstrated ability.", "")],
    "u11-st-teach-past-be-2": [(None, BE2)],
    "u11-st-teach-past-aspect-1": [("In this recorded set, прочита́л is masculine, прочита́ла feminine, and прочита́ло neuter."
                                    " Compare the endings while keeping the same verb.",
                                    "прочита́л is masculine, прочита́ла feminine, and прочита́ло neuter. Compare the endings"
                                    " of the same verb.")],
    "u11-st-teach-past-aspect-2": [("Compare the entire recorded forms:", "Compare the whole forms:")],
    "u11-st-teach-past-aspect-3": [("The recorded feminine singular past form", "The feminine singular past form")],
    "u11-st-teach-past-aspect-4": [("The recorded neuter singular past form", "The neuter singular past form")],
    "u11-st-teach-reflexive-2": [(None, REFLEXIVE2)],
}
ATTRIBUTION = [("OpenRussian dictionary data is frozen at", "OpenRussian dictionary data is pinned at"),
               ("Wikibooks excerpts are frozen by oldid", "Wikibooks excerpts are pinned by oldid")]

# Feedback edits: (item, old sentence, new sentence) -- inventory 1294 (REWORD), 1303 (REWORD, marked).
FEEDBACK = [
    ("u11-i-past-read-1", "The cue shows the feminine singular form, but the question asks for masculine singular of the same verb.",
     "The cue shows the feminine singular form, but the requested form is masculine singular of the same verb."),
    ("u11-i-past-read-2", "The question asks for the feminine singular line, not the plural line.", "Choose the requested sentence."),
    ("u11-i-past-be-1", "The cue shows feminine singular, but the requested row is plural; past agreement follows the subject number.",
     "The cue shows feminine singular, but the requested form is plural; past agreement follows the subject number."),
]
# u11-i-past-be-2 (report section 1; inventory 1307 REWORD): shows быть, asks for masculine singular бы́л (V:2).
BE2_PROMPT = "Which masculine past form (singular) belongs to the shown verb?"
BE2_FEEDBACK = ("бы́л is the masculine past form (singular) of быть. бы́ли is plural past and бы́ло is neuter singular past."
                " Check the requested gender and number.")
BE2_RATIONALES = ["", "бы́ли is plural past, not the masculine singular past requested here.",
                  "бы́ло is neuter singular past, not the masculine singular past requested here."]

# Twelve typed checks (report section 2): (id, objective, cue record, key record, label).
P_TYPED = "Type the {} past form of the shown verb."
TYPED = [
    ("u11-i-type-chital", "obj-u11-past-read", "prov-ex-or-u11-past-read-1", "prov-ex-or-u11-past-read-0", "masculine singular"),
    ("u11-i-type-chitala", "obj-u11-past-read", "prov-ex-or-u11-past-read-0", "prov-ex-or-u11-past-read-1", "feminine singular"),
    ("u11-i-type-chitalo", "obj-u11-past-read", "prov-ex-or-u11-past-read-0", "prov-ex-or-u11-past-read-2", "neuter singular"),
    ("u11-i-type-chitali", "obj-u11-past-read", "prov-ex-or-u11-past-read-0", "prov-ex-or-u11-past-read-3", "plural"),
    ("u11-i-type-byl", "obj-u11-past-be", "prov-ex-or-u11-past-be-1", "prov-ex-or-u11-past-be-0", "masculine singular"),
    ("u11-i-type-byla", "obj-u11-past-be", "prov-ex-or-u11-past-be-0", "prov-ex-or-u11-past-be-1", "feminine singular"),
    ("u11-i-type-prochital", "obj-u11-past-aspect", "prov-ex-or-u11-past-aspect-4", "prov-ex-or-u11-past-aspect-2", "masculine singular"),
    ("u11-i-type-prochitala", "obj-u11-past-aspect", "prov-ex-or-u11-past-aspect-2", "prov-ex-or-u11-past-aspect-3", "feminine singular"),
    ("u11-i-type-prochitalo", "obj-u11-past-aspect", "prov-ex-or-u11-past-aspect-2", "prov-ex-or-u11-past-aspect-4", "neuter singular"),
    ("u11-i-type-ulybalas", "obj-u11-reflexive", "prov-ex-or-u11-reflexive-1", "prov-ex-or-u11-reflexive-2", "feminine singular"),
    ("u11-i-type-uchilsya", "obj-u11-reflexive", "prov-ex-or-u11-reflexive-7", "prov-ex-or-u11-reflexive-6", "masculine singular"),
    ("u11-i-type-uchilas", "obj-u11-reflexive", "prov-ex-or-u11-reflexive-6", "prov-ex-or-u11-reflexive-7", "feminine singular"),
]
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."

REFERENCES = [
    {"id": "ref-u11-past", "title": "The past tense",
     "summary": "Past-tense endings, agreement with the subject, and the past tense of the verb to be.",
     "detail": ("To form the past tense, remove -ть from the infinitive and add -л. Add -ла if the subject is feminine, -ло if it"
                " is neuter and -ли if it is plural.\n\nThe past tense agrees with its"
                " subject in gender and number, not in person. Он чита́л — He was reading. Она чита́ла — She was reading."
                " Они чита́ли — They were reading.\n\n" + BE2 + " Он был в Москве́ — He was in Moscow. Она была́ краси́вая —"
                " She was beautiful.\n\nSome verbs, such as идти, have irregular past forms that cannot be predicted."),
     "searchTerms": ["past tense", "was", "were", "to be"], "steps": ["u11-st-teach-past-read-1", "u11-st-teach-past-be-1",
                                                                      "u11-st-teach-past-aspect-1"]},
    {"id": "ref-u11-reflexive", "title": "Reflexive verbs in the past",
     "summary": "How a reflexive verb keeps its reflexive ending in the past tense.",
     "detail": ("A reflexive verb such as улыба́ться, to smile, has a reflexive ending. To form its past tense, remove the"
                " reflexive ending, form the past tense, then reattach the ending: -ся after a consonant and -сь after a"
                " vowel.\n\nОн улыба́лся — He was smiling. Она улыба́лась — She was smiling. Они улыба́лись — They were"
                " smiling.\n\nThe verb meaning to study follows the same pattern: учи́лся for a masculine subject, учи́лась"
                " for a feminine subject and учи́лись for a plural subject."),
     "searchTerms": ["reflexive verbs", "past tense", "smile", "study"], "steps": ["u11-st-teach-reflexive-1"]},
]
BINDER = {"id": "binder-u11-past", "objectiveIds": ["obj-u11-past-read", "obj-u11-past-be", "obj-u11-reflexive"],
          "prompt": ("In your binder, make a table of the past-tense forms of the verb meaning to read, with rows for masculine,"
                     " feminine, neuter and plural. Do the same for the verb meaning to be. Then copy one reflexive line from"
                     " this unit with its meaning. Compare your work with the model answer."),
          "modelAnswer": ("To read: masculine чита́л. Feminine чита́ла. Neuter чита́ло. Plural чита́ли.\nTo be: masculine бы́л."
                          " Feminine была́. Neuter бы́ло. Plural бы́ли.\nОна улыба́лась — She was smiling.")}
TUTOR = {"id": "tutor-u11-past", "bring": "Your binder table of past-tense forms, to read each form aloud and name its gender or number."}

# --------------------------------------------------------------------------------------------------------------
# Facts: sentence segmentation and fact forms, as V2Validator.englishSentenceRanges and V2Validator.factWordForms
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
    "Choose the requested sentence.",
    "Check the requested gender and number.",
    "Copy the whole form.",
    "Compare the endings of the same verb.",
    "Use the gender requested in the question.",
    "Compare the past forms in the worked examples.",
    "Choose the past form with the feminine singular agreement requested here.",
}
V62 = ["prov-ex-or-u11-past-read-0", "prov-ex-or-u11-past-read-1"]
V2 = ["prov-ex-or-u11-past-be-1", "prov-ex-or-u11-past-be-3"]
V353 = ["prov-ex-or-u11-past-aspect-2", "prov-ex-or-u11-past-aspect-3", "prov-ex-or-u11-past-aspect-4"]
V209 = ["prov-ex-or-u11-reflexive-7", "prov-ex-or-u11-reflexive-8"]
# English-only sentences and their evidence (report section 3): ("p", line) = P line record, else a record id.
EXTRA: dict[str, list] = {
    "The past form matches the subject in gender and number; choose feminine singular for this question.": [("p", 38), ("p", 56)],
    "The cue shows the feminine singular form, but the requested form is masculine singular of the same verb.": V62 + [("p", 56)],
    "The cue shows feminine singular, but the requested form is plural; past agreement follows the subject number.":
        V2 + [("p", 38), ("p", 65)],
    "In the singular, a past-tense verb agrees with its subject in gender.": [("p", 38), ("p", 65)] + V353,
    "The pronoun I does not select one past-tense gender for every speaker.": [("p", 62), ("p", 65)],
    "These are masculine, feminine and neuter singular forms of the same verb, respectively.": V353 + [("p", 38)],
    "The reflexive ending remains on the past verb; choose feminine singular agreement.": [("p", 104), ("p", 107)],
    "Both verbs keep their reflexive ending, but only the first line is feminine singular.": [("p", 107), ("p", 109)],
    "Both retain the reflexive ending, so that ending alone does not distinguish them; the question requires feminine singular"
    " agreement.": V209 + [("p", 113), ("p", 115)],
    # New teaching wording (section 4 and D8), each with the line that states it.
    "To form the past tense, remove -ть from the infinitive and add -л.": [("p", 38)],
    "Add -ла if the subject is feminine, -ло if it is neuter and -ли if it is plural.": [("p", 38)],
    "Some verbs, such as идти, have irregular past forms that cannot be predicted.": [("p", 118)],
    BE2: [("p", 68)],
    "The infinitive улыба́ться, to smile, has a reflexive ending.": [("p", 102)],
    "To form its past tense, remove the reflexive ending, form the past tense, then reattach the ending: -ся after a consonant"
    " and -сь after a vowel.": [("p", 104)],
}


def edit(text: str, old: str, new: str, where: str) -> str:
    if text.count(old) != 1:
        raise SystemExit(f"{where}: baseline text changed ({old!r})")
    return text.replace(old, new)


class Builder:
    def __init__(self, pack: dict):
        self.pack = pack
        self.records = {r["itemId"]: r for r in pack["provenanceRecords"]}
        self.new: list[dict] = []
        self.unit = next(u for u in pack["units"] if u["number"] == UNIT)

    def add(self, record: dict) -> dict:
        if record["itemId"] in self.records:
            raise SystemExit(f"Duplicate record {record['itemId']}")
        self.records[record["itemId"]] = record
        self.new.append(record)
        self.pack["provenanceRecords"].append(record)
        if record["sourceId"] != "src-content-audit-research" and not d7b.frozen_record(self.pack, record):
            raise SystemExit(f"{record['itemId']} is not a frozen-source record")
        return record

    def verb(self, objective: str, accented: str, field: str, key: str) -> str:
        original = verb_cell(accented, field)
        record = copy.deepcopy(self.records["prov-ex-or-u11-past-be-0"])
        record.update({"itemId": f"prov-t16-u11-{key}", "objectiveId": objective, "sourceRecordId": f"verbs.csv:{accented}#{field}",
                       "originalSourceText": original, "shippedText": stressed(original), "sourceHash": sha(original),
                       "contentHash": sha(stressed(original)), "recordType": "lexical record"})
        return self.add(record)["itemId"]

    def ending(self, objective: str, region: str) -> str:
        """A selection record of an ending shown on P:38 (D19a two-part rule against the displayed line)."""
        raw = ENDING_SPAN
        if PAST_LINES[37].count(raw) != 1 or not d7b.two_part(d7b.displayed(raw), region):
            raise SystemExit(f"{region} fails D19a against past-tense-4567337.txt:38")
        record = copy.deepcopy(self.records["prov-u11-past-read-rem-r1"])
        record.update({"itemId": f"prov-t16-u11-ending-{region}", "objectiveId": objective, "shippedText": region,
                       "originalSourceText": raw, "sourceHash": sha(raw), "sourceRecordId": "past:" + raw[:80],
                       "contentHash": sha(region)})
        return self.add(record)["itemId"]

    def line(self, number: int) -> str:
        record_id = f"prov-t16-u11-p{number}"
        if record_id in self.records:
            return record_id
        raw = PAST_LINES[number - 1].strip()
        if PAST.read_text(encoding="utf-8").count(raw) != 1:
            raise SystemExit(f"P:{number} is not unique on the page")
        record = copy.deepcopy(self.records["prov-u11-past-read-rem"])
        record.update({"itemId": record_id, "sourceRecordId": "past:" + raw[:80], "originalSourceText": raw, "shippedText": raw,
                       "sourceHash": sha(raw), "contentHash": sha(raw), "recordType": "instruction", "transformation": "selection"})
        return self.add(record)["itemId"]


def is_frozen(pack: dict, record: dict) -> bool:
    return d7b.frozen_record(pack, record)


def apply(pack: dict) -> dict:
    b = Builder(pack)
    unit = b.unit
    baseline = copy.deepcopy(pack)
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R = b.records

    # --- endings shown in the new teaching need their own frozen regions -------------------------------------------
    for region in ("ла", "ло", "ли"):
        b.ending("obj-u11-past-read", region)

    # --- teaching (section 4, D8) and remediation that repeats each session's teaching -----------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = new if old is None else edit(steps[step_id]["body"], old, new, step_id)
    for session in unit["sessions"]:
        teach = [s for s in session["steps"] if s["kind"] == "TEACH"]
        for rem in (s for s in session["steps"] if s["kind"] == "REMEDIATE"):
            rem["body"] = "\n\n".join(s["body"] for s in teach)
            rem["exampleIds"] = list(dict.fromkeys(e for s in teach for e in s.get("exampleIds", [])))
    ref = next(r for r in pack["references"] if r["id"] == "ref-u1115-attribution")
    for old, new in ATTRIBUTION:
        ref["detail"] = edit(ref["detail"], old, new, ref["id"])

    # --- items: prompts, feedback, u11-i-past-be-2 ----------------------------------------------------------------
    for item_id, prompt in PROMPTS.items():
        items[item_id]["prompt"] = prompt
    for item_id, old, new in FEEDBACK:
        item = items[item_id]
        item["feedback"] = edit(item["feedback"], old, new, item_id)
        item["assessment"]["whyAnswerDemonstratesObjective"] = edit(item["assessment"]["whyAnswerDemonstratesObjective"], old, new, item_id)
    be2 = items["u11-i-past-be-2"]
    cue = b.verb("obj-u11-past-be", "быть", "accented", "byt-accented")
    if R[cue]["shippedText"] != "быть":
        raise SystemExit("verbs.csv:быть#accented is not быть")
    be2.update({"prompt": BE2_PROMPT, "promptLetter": "быть", "options": ["бы́л", "бы́ли", "бы́ло"], "answer": "бы́л",
                "feedback": BE2_FEEDBACK, "distractorRationales": list(BE2_RATIONALES)})
    be2["assessment"].update({"testedFeature": "MASCULINE_PAST", "answerSourceIds": ["prov-ex-or-u11-past-be-0"],
                              "distractorSourceIds": ["", "prov-ex-or-u11-past-be-3", "prov-ex-or-u11-past-be-2"],
                              "distractorDifference": list(BE2_RATIONALES), "whyAnswerDemonstratesObjective": BE2_FEEDBACK,
                              "contrastMembers": ["бы́л", "бы́ли", "бы́ло"],
                              "relationParticipantIds": [cue, "prov-ex-or-u11-past-be-0", "prov-ex-or-u11-past-be-3",
                                                         "prov-ex-or-u11-past-be-2"]})
    for item_id in ("u11-i-past-be-2",):
        for option, rid in zip(be2["options"], ["prov-ex-or-u11-past-be-0", "prov-ex-or-u11-past-be-3", "prov-ex-or-u11-past-be-2"]):
            if R[rid]["shippedText"] != option:
                raise SystemExit(f"{item_id}: {option} is not {rid}")

    # --- A2-23: typed checks ---------------------------------------------------------------------------------------
    typed = []
    for item_id, objective, cue_id, key_id, label in TYPED:
        cue_text, key = R[cue_id]["shippedText"], R[key_id]["shippedText"]
        accented, field = R[key_id]["sourceRecordId"].split(":", 1)[1].split("#")
        if stressed(verb_cell(accented, field)) != key:
            raise SystemExit(f"{item_id}: {key} is not the exact frozen stress of {accented}#{field}")
        typed.append(typed_item(item_id, objective, P_TYPED.format(label), cue_text, key, TYPED_FEEDBACK, "src-openrussian-50e210c4"))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    items.update({i["id"]: i for i in typed})

    # --- titles, orientation, guided and check bodies; checks move to an end-of-unit session ------------------------
    for objective_id, title in OBJECTIVE_TITLES.items():
        objectives[objective_id]["title"] = title
    unit["topicSummary"] = TOPIC
    for session in unit["sessions"]:
        if session["id"] in SESSION_TITLES:
            session["title"] = SESSION_TITLES[session["id"]]
    for step in steps.values():
        if step["title"] in STEP_TITLES:
            step["title"] = STEP_TITLES[step["title"]]
        if step["kind"] == "GUIDED" and step["body"] == GUIDED_OLD:
            step["body"] = GUIDED_NEW
    steps["u11-st-orient"]["body"] = ORIENT_BODY
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
        step["body"] = edit(step["body"], CHECK_OLD, CHECK_NEW, step["id"])
    book = next(index for index, session in enumerate(unit["sessions"]) if session["id"] == "u11-s-book")
    unit["sessions"].insert(book, {"id": "u11-s-checks", "title": "Unit 11 checks", "estimatedMinutes": 20,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}

    # --- A2-36 references, binder and tutor tasks -------------------------------------------------------------------
    template = next(r for r in pack["references"] if r["id"] == "ref-u10-possession")
    for spec in REFERENCES:
        reference = {k: template[k] for k in ("sourceId", "reviewStatus", "classification")}
        reference.update({k: spec[k] for k in ("id", "title", "summary", "detail", "searchTerms")})
        reference["introducedInUnit"] = UNIT
        pack["references"].append({k: reference[k] for k in template if k in reference})
        for step_id in spec["steps"]:
            steps[step_id]["referenceIds"] = list(dict.fromkeys(steps[step_id].get("referenceIds", []) + ["ref-u1115-attribution", spec["id"]]))
    for session in unit["sessions"]:
        refs = [r for s in session["steps"] if s["kind"] == "TEACH" for r in s.get("referenceIds", []) if r != "ref-u1115-attribution"]
        for rem in (s for s in session["steps"] if s["kind"] == "REMEDIATE"):
            if refs:
                rem["referenceIds"] = list(dict.fromkeys(refs))
    if unit.get("binderTask") or unit.get("tutorTask"):
        raise SystemExit("Unit 11 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)

    # --- every Unit 11 learner field: Russian only through frozen Unit 11 records -----------------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and is_frozen(pack, r)}
    texts = [s["body"] for s in steps.values()] + [unit["topicSummary"], unit["binderTask"]["prompt"], unit["binderTask"]["modelAnswer"]]
    texts += [r[k] for r in pack["references"] if r["id"] in ("ref-u11-past", "ref-u11-reflexive") for k in ("title", "summary", "detail")]
    for item in items.values():
        if item["objectiveId"].startswith("obj-u11-"):
            texts += [item["prompt"], item.get("promptLetter") or "", item["answer"], item["feedback"]] + item["options"] + item.get("distractorRationales", [])
    for text in texts:
        for m in re.finditer("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*", text):
            region = m.group(0).strip()
            if region not in frozen and region.rstrip("?!.,") not in frozen:
                raise SystemExit(f"Unit 11 region without a frozen record: {region!r} in {text[:80]!r}")

    report = mark_facts(b, steps, items)
    cover_english(pack, baseline)
    return report


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
                raise SystemExit(f"No frozen Unit 11 record shows {form!r} ({sentence})")
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
                    missing.append((owner, sentence))
                    continue
                ids = [b.line(r[1]) if isinstance(r, tuple) else r for r in refs or []] + cite_forms(sentence, objective, participants)
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


def english_hosts(pack: dict) -> dict[tuple[str, str], tuple[int, str, str, list[str]]]:
    """t15g.english_hosts for Unit 11, plus its references and binder task."""
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
        out[(i["id"], "cue")] = (n, o, i.get("promptLetter") or "", [])
        out[(i["id"], "answer")] = (n, o, i["answer"], [])
        for k, v in enumerate(i.get("options", [])):
            out[(i["id"], f"option{k}")] = (n, o, v, [])
        for k, v in enumerate(i.get("distractorRationales", [])):
            out[(i["id"], f"rationale{k}")] = (n, o, v, facts)
        a = i.get("assessment") or {}
        out[(i["id"], "why")] = (n, o, a.get("whyAnswerDemonstratesObjective") or "", [])
        for k, v in enumerate(a.get("distractorDifference") or []):
            out[(i["id"], f"difference{k}")] = (n, o, v, [])
    for r in pack["references"]:
        if r["id"] in ("ref-u11-past", "ref-u11-reflexive"):
            for k in ("summary", "detail"):
                out[(r["id"], k)] = (UNIT, first, r[k], [])
    if unit.get("binderTask"):
        for k in ("prompt", "modelAnswer"):
            out[(unit["binderTask"]["id"], k)] = (UNIT, unit["binderTask"]["objectiveIds"][0], unit["binderTask"][k], [])
    return out


def cover_english(pack: dict, before: dict) -> None:
    """Content-audit coverage for changed English (t15g.cover_english pattern, T16 record IDs)."""
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
        item_id = f"prov-ca-t16-{key}"
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
    for (owner, field), (unit, objective, text, facts) in sorted(english_hosts(pack).items()):
        old = old_hosts.get((owner, field))
        if old is not None and old[2] == text:
            continue
        coverage(unit, objective, f"{owner}-{field}", text)
    for (owner, field), (unit, objective, text, facts) in sorted(english_hosts(pack).items()):
        for n, fact in enumerate(facts):
            coverage(unit, objective, f"{owner}-{field}-fact{n}", fact)
    print(f"english coverage records: {count}")


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T16 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
