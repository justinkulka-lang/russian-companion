#!/usr/bin/env python3
"""T17f-n1 (owner decision D53, Codex recheck of T17f): Block 4 content fixes.

- D53d, u17-i-calendar-date-1: the old check keyed the order "day month year" against two options that are not orders (a
  date component and the all-numeric form). It now asks which English term names the written form of the shown date. The frozen date-notation
  span names exactly two written forms, each with its example: "the all-numeric date (for example 28.08.17)" and "the
  expanded form (for example 28 августа 2017 г.)". The shown date is the expanded form's example, so the key is "the expanded
  form" and the only distractor "the all-numeric date", both on the cue's own span (frozen selection records). The Unit 17
  date teaching and its remediation name the two forms first (same span: the order is used in both). Two options: a CC036 row.
- D53f, CC027 (decoy-cue relevance): three Unit 16 required checks displayed a cue their prompt never used (u16-i-inst-sg-2
  showed утром for "Which instrumental singular noun means pencil?"; u16-i-inst-means-1 and -2 showed the third sentence of
  the set). Each prompt now says what the shown form is, in the unit's own teaching terms, before it asks its question;
  u20-i-motion-3 now names the shown sentence it asks about.
- D53f, CC028 (u17-g-age-0): the guided age item's two misreadings (ownership; people performing an action) are now both named
  in the age teaching and in u17-st-rem-age (was: ownership only).
No Russian is added or changed. New English wording gets a coverage record (prov-ca-t17fn1-). Every change is logged in
audit/T17f-n1-block4-changes.csv.
"""
from __future__ import annotations

import copy
import csv
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from apply_t17a_unit_16 import sentences, sha  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
LOG = ROOT / "audit/T17f-n1-block4-changes.csv"
BASELINE_SHA256 = "af88935447e2574f7f0858246cb80110053ade4ac653922bc003043a102c7412"  # the T17g pack (ee665dd1)
TASK, DATE = "T17f-n1", "2026-11-26"

DATE_ITEM = "u17-i-calendar-date-1"
DATE_CUE = "prov-ex-u17-calendar-date-1-ru"
DATE_FROM = "prov-ex-u17-calendar-date-1-en"
EXPANDED, ALL_NUMERIC = "the expanded form", "the all-numeric date"
EXPANDED_RECORD, ALL_NUMERIC_RECORD = "prov-t17fn1-u17-calendar-date-expanded", "prov-t17fn1-u17-calendar-date-all-numeric"
DATE_FACTS = ["prov-t17b-u17-f10-calendar-date", "prov-t17b-u17-f12-calendar-date"]
DATE_PROMPT = "Which English term names the written form of the shown date?"  # ENGLISH_CLASSIFICATION prompt
# The explanation names only the key and the shown form, and the rationale only its own option (keyedGlossErrors).
DATE_FEEDBACK_TAIL = "is the expanded form of the date. Both written forms give the day, then the month, then the year."
DATE_RATIONALE = "the all-numeric date writes the day, month and year in figures only; the shown date writes the month as a word."
DATE_TEACH_AFTER = "15 мая 1996 г. means 15 May 1996."
DATE_TEACH_NEW = ("Written with the month as a word, a date is in the expanded form; the all-numeric date writes the same day, month"
                  " and year in figures only.")

PROMPTS = {
    "u16-i-inst-sg-2": ("Which instrumental singular noun means pencil?",
                        "The shown noun is instrumental singular. Which other instrumental singular noun means pencil?"),
    "u16-i-inst-means-1": ("Which sentence uses the means instrumental to name a tool used for an action?",
                           "The shown sentence is about a meeting with someone. Which sentence uses the means instrumental to name a tool used for an action?"),
    "u16-i-inst-means-2": ("Which sentence is the between line, describing a location relative to two houses?",
                           "The shown sentence names a tool used for an action. Which sentence is the between line, describing a location relative to two houses?"),
    "u20-i-motion-3": ("Choose the English description. Which reading matches the perfective setting-out verb?",
                       "Choose the English description. Which reading matches the perfective setting-out verb in the shown sentence?"),
}
AGE_REM = "u17-st-rem-age"
AGE_OLD = "This is an age statement, not a statement of ownership."
AGE_NEW = "This is an age statement, not a statement of ownership, and it does not describe people performing an action."


def lettered(text: str) -> list[str]:
    return [x for x in sentences(text) if any(ch.isalpha() for ch in x)]


def coverage(pack: dict, records: dict, unit: int, objective: str, key: str, text: str) -> None:
    template = records["prov-t15an1-u6-possessive-our-your"]
    record = copy.deepcopy(template)
    note = (f"New English wording authored on {DATE} ({TASK}); it did not exist before that date. It covers the wording"
            " only; the Russian facts it states are cited from frozen source records in the marker's russianFactSources."
            " 2026-10-08 is the date of the reused research context only.")
    record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                "objectiveOrClaimBinding": f"{objective}:{key}"})
    original = note + "\n" + text
    item_id = f"prov-ca-t17fn1-{key}"
    if item_id in records:
        raise SystemExit(f"Duplicate record {item_id}")
    record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                   "shippedText": text, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                   "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                  f" this record's English text is new, authored {DATE} for {TASK}."})
    pack["provenanceRecords"].append(record)
    records[item_id] = record


def mark(host: dict, text: str, default: list[str]) -> None:
    """Marks every lettered sentence of [text] that the host does not mark yet, with [default] records; keeps the rest."""
    facts = list(host.get("russianFacts") or [])
    sources = dict(host.get("russianFactSources") or {})
    for s in lettered(text):
        if s not in sources:
            facts.append(s)
            sources[s] = list(default)
    host["russianFacts"], host["russianFactSources"] = facts, {f: sources[f] for f in facts}


def apply(pack: dict) -> list[dict]:
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    items = {i["id"]: i for i in pack["items"]}
    unit17 = next(u for u in pack["units"] if u["number"] == 17)
    steps = {s["id"]: s for u in pack["units"] for se in u["sessions"] for s in se["steps"]}
    log: list[dict] = []

    # --- D53d: u17-i-calendar-date-1 asks which written form is shown ------------------------------------------------------
    cue = records[DATE_CUE]
    span = cue["originalSourceText"]
    for label, example in ((ALL_NUMERIC, "28.08.17"), (EXPANDED, cue["shippedText"])):
        if f'{label} (for example "{{{{lang|ru|{example}}}}}")' not in span:
            raise SystemExit(f"The frozen span does not give {example!r} as the example of {label!r}")
    for rid, text in ((EXPANDED_RECORD, EXPANDED), (ALL_NUMERIC_RECORD, ALL_NUMERIC)):
        record = copy.deepcopy(records[DATE_FROM])
        assert record["originalSourceText"] == span and text in span
        record.update({"itemId": rid, "shippedText": text, "contentHash": sha(text)})
        pack["provenanceRecords"].append(record)
        records[rid] = record
    item = items[DATE_ITEM]
    before = {k: copy.deepcopy(item.get(k)) for k in ("prompt", "options", "answer", "feedback", "distractorRationales", "assessment")}
    feedback = f"{cue['shippedText']} {DATE_FEEDBACK_TAIL}"
    item.update({"prompt": DATE_PROMPT, "options": [EXPANDED, ALL_NUMERIC], "answer": EXPANDED, "feedback": feedback,
                 "distractorRationales": ["", DATE_RATIONALE]})
    a = item["assessment"]
    a.update({"testedFeature": "DATE_FORM", "answerSourceIds": [EXPANDED_RECORD], "distractorSourceIds": ["", ALL_NUMERIC_RECORD],
              "distractorDifference": ["", DATE_RATIONALE], "whyAnswerDemonstratesObjective": feedback,
              "contrastMembers": [EXPANDED, ALL_NUMERIC], "relationParticipantIds": [DATE_CUE, EXPANDED_RECORD, ALL_NUMERIC_RECORD]})
    item["russianFacts"], item["russianFactSources"] = [], {}
    first, *rest = lettered(feedback)
    item["russianFacts"].append(first)
    item["russianFactSources"][first] = [DATE_CUE]
    mark(item, " ".join(rest + [DATE_RATIONALE]), DATE_FACTS)
    log.append({"task": TASK, "finding": "D53d", "owner.field": DATE_ITEM, "old": json.dumps(before, ensure_ascii=False),
                "new": json.dumps({k: item.get(k) for k in before}, ensure_ascii=False)})
    # Each English sentence of the feedback is covered whole (markedSentenceSourceErrors reads whole sentences).
    whole = [f"{cue['shippedText']} {DATE_FEEDBACK_TAIL.split('. ')[0]}.", DATE_FEEDBACK_TAIL.split(". ", 1)[1]]
    first_tail = DATE_FEEDBACK_TAIL.split(". ")[0] + "."  # the marker piece after the year abbreviation
    for k, text in (("prompt", DATE_PROMPT), ("feedback", feedback), ("rationale1", DATE_RATIONALE),
                    ("feedback-s0", whole[0]), ("feedback-s1", whole[1]), ("feedback-s0-tail", first_tail)):
        coverage(pack, records, 17, item["objectiveId"], f"{DATE_ITEM}-{k}", text)
    for sid in ("u17-st-teach-calendar-date-1", "u17-st-rem-calendar-date"):
        step = steps[sid]
        old = step["body"]
        if old.count(DATE_TEACH_AFTER) != 1:
            raise SystemExit(f"{sid}: anchor")
        step["body"] = old.replace(DATE_TEACH_AFTER, DATE_TEACH_AFTER + " " + DATE_TEACH_NEW)
        if step.get("russianFacts") is not None:
            facts = step["russianFacts"]
            anchor = facts.index("means 15 May 1996.")
            facts.insert(anchor + 1, DATE_TEACH_NEW)
            step["russianFactSources"][DATE_TEACH_NEW] = list(DATE_FACTS)
            step["russianFactSources"] = {f: step["russianFactSources"][f] for f in facts}
        log.append({"task": TASK, "finding": "D53d", "owner.field": f"{sid}.body", "old": old, "new": step["body"]})
        coverage(pack, records, 17, "obj-u17-calendar-date", f"{sid}-body", step["body"])
    coverage(pack, records, 17, "obj-u17-calendar-date", "u17-calendar-date-forms-sentence", DATE_TEACH_NEW)

    # --- D53f CC027: displayed cues the prompt now uses -----------------------------------------------------------------------
    for iid, (old, new) in PROMPTS.items():
        item = items[iid]
        assert item["prompt"] == old, (iid, item["prompt"])
        item["prompt"] = new
        log.append({"task": TASK, "finding": "CC027", "owner.field": f"{iid}.prompt", "old": old, "new": new})
        coverage(pack, records, int(iid.split("-")[0][1:]), item["objectiveId"], f"{iid}-prompt", new)

    # --- D53f CC028: both guided age misreadings are named in the remediation ----------------------------------------------
    step = steps[AGE_REM]
    old = step["body"]
    step["body"] = old.replace(AGE_OLD, AGE_NEW) if old.count(AGE_OLD) == 1 else sys.exit(f"{AGE_REM}: anchor")
    facts, sources = step["russianFacts"], step["russianFactSources"]
    facts[facts.index(AGE_OLD)] = AGE_NEW
    sources[AGE_NEW] = sources.pop(AGE_OLD)
    step["russianFactSources"] = {f: sources[f] for f in facts}
    log.append({"task": TASK, "finding": "CC028", "owner.field": f"{AGE_REM}.body", "old": old, "new": step["body"]})
    coverage(pack, records, 17, "obj-u17-age", f"{AGE_REM}-body", step["body"])
    coverage(pack, records, 17, "obj-u17-age", f"{AGE_REM}-misreadings", AGE_NEW)
    # The remediation repeats the age teaching word for word (Unit17AgeCalendarInstructionTest), so the teaching step that
    # carries the sentence names both misreadings too, before the guided practice.
    taught = [sid for sid, st in steps.items() if sid.startswith("u17-st-teach-age-") and AGE_OLD in st["body"]]
    if len(taught) != 1:
        raise SystemExit(f"age teaching anchor: {taught}")
    step = steps[taught[0]]
    old = step["body"]
    step["body"] = old.replace(AGE_OLD, AGE_NEW)
    log.append({"task": TASK, "finding": "CC028", "owner.field": f"{taught[0]}.body", "old": old, "new": step["body"]})
    coverage(pack, records, 17, "obj-u17-age", f"{taught[0]}-body", step["body"])
    assert unit17
    return log


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T17g baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    out = json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n"
    if "--dry" in sys.argv:
        print(f"dry run; {len(log)} changes; asset not written")
        return
    ASSET.write_text(out, encoding="utf-8")
    with LOG.open("w", encoding="utf-8", newline="") as f:
        w = csv.DictWriter(f, fieldnames=["task", "finding", "owner.field", "old", "new"], lineterminator="\n")
        w.writeheader()
        w.writerows(log)
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}; {len(log)} rows in {LOG.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
