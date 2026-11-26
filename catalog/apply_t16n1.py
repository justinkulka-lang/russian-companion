#!/usr/bin/env python3
"""T16-n1 (owner decisions D28c, D28d): learner-text fixes after the Codex check of T15i, D27a and T16.

Exact-baseline transformer; runs once on the T16b pack (38bee8a6).
- D28c(1): "Review is pending." leaves every ref-*-attribution detail (review status lives in the status UI and metadata).
- D28c(2): u11-i-reflexive-2 and u12-i-prep-plural-1 lose their "the question requires" commentary; each fact stays marked on
  the same frozen records (past-tense:38, :113, :115 and verbs.csv учиться for Unit 11; nouns.csv стол for Unit 12).
- D28c(3): the mapping notes of Units 1-12 say what the unit covers in the book; mapping status stays in mappingStatus.
- D28d: Unit 4's present-tense sentence keeps only what verbs-4233876.txt:44 says without aspect terminology; its frozen
  record prov-u4-present-rule stays.
No Russian changes. New English wording is covered by content-audit coverage records.
"""
from __future__ import annotations

import copy
import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "e64a00a554eab48fea78ea846a1e53e5fa955dad9538eb15558a94c67ca833f4"  # the T16b pack (35c6bc4c)
TASK, DATE = "T16-n1", "2026-11-21"

REVIEW = " Review is pending."
U4_OLD = "So, to form the present tense, you simply take the imperfective aspect of the verb and add the appropriate endings."
U4_NEW = "To form the present tense, add the appropriate endings to the verb. Verbs in Russian conjugate in one of two ways."
FACTS = [  # (item, old sentence, new sentence, extra sources)
    ("u11-i-reflexive-2",
     "Both retain the reflexive ending, so that ending alone does not distinguish them; the question requires feminine singular agreement.",
     "Both retain the reflexive ending, so that ending alone does not distinguish them; the past ending shows gender and number.",
     ["prov-t16-u11-p38"]),
    ("u12-i-prep-plural-1",
     "The question requires both prepositional case and plural number; the singular dative option changes both features.",
     "The prepositional plural differs from the singular dative in both case and number.", []),
]
TOPICS = {
    1: "learning to read Russian", 2: "handwriting and additional pronunciation",
    3: "everyday phrases, naming things, basic grammar and gender",
    4: "verbs, personal pronouns, the present tense, conjugation, word stress, and an introduction to nouns and cases",
    5: "questions, the prepositional case and indeclinable nouns",
    6: "possession, going places, the accusative case and personal names",
    7: "adjectives, agreement, demonstratives, adjective case forms and adverbs",
    8: "plurals, the first spelling rules, existence questions and buying things",
    9: "numbers, the genitive singular and plural, quantities and money",
    10: "expressions of having, more uses of the genitive, absence, genitive prepositions, pronouns and adjectives",
    11: "the past tense, including reflexive verbs",
    12: "the future, an introduction to aspect, the dative case, more spelling rules and the prepositional plural",
}
PAGES = {11: "122-132", 12: "133-148"}


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def edit(text: str, old: str, new: str, where: str) -> str:
    if text.count(old) != 1:
        raise SystemExit(f"{where}: baseline text changed ({old!r})")
    return text.replace(old, new)


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T16-n1 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    changes = []  # (owner, field, old, new)
    coverage_texts = []  # (unit, objective, key, text)
    units = {u["number"]: u for u in pack["units"]}
    first_objective = {n: u["objectives"][0]["id"] for n, u in units.items()}

    # D28c(1)
    for ref in pack["references"]:
        if ref["id"].endswith("-attribution") and REVIEW in ref["detail"]:
            old = ref["detail"]
            ref["detail"] = edit(old, REVIEW, "", ref["id"])
            changes.append((ref["id"], "detail", "Review is pending.", "(sentence removed)"))
    # D28c(2)
    items = {i["id"]: i for i in pack["items"]}
    for item_id, old, new, extra in FACTS:
        item = items[item_id]
        item["feedback"] = edit(item["feedback"], old, new, item_id)
        item["assessment"]["whyAnswerDemonstratesObjective"] = edit(item["assessment"]["whyAnswerDemonstratesObjective"], old, new, item_id)
        sources = item["russianFactSources"].pop(old)
        item["russianFactSources"][new] = list(dict.fromkeys(sources + extra))
        item["russianFacts"] = [new if f == old else f for f in item["russianFacts"]]
        changes.append((item_id, "feedback", old, new))
        unit = next(n for n, u in units.items() if any(o["id"] == item["objectiveId"] for o in u["objectives"]))
        coverage_texts += [(unit, item["objectiveId"], f"{item_id}-feedback", item["feedback"]),
                           (unit, item["objectiveId"], f"{item_id}-fact", new)]
    # D28c(3)
    for n, topic in TOPICS.items():
        unit = units[n]
        pages = f", printed pages {PAGES[n]}" if n in PAGES else ""
        new = f"This unit goes with Lesson {unit['penguinLesson']} in your book{pages}: {topic}."
        changes.append((unit["id"], "mappingNote", unit["mappingNote"], new))
        unit["mappingNote"] = new
    # D28d
    steps = {s["id"]: s for s in (st for se in units[4]["sessions"] for st in se["steps"])}
    for sid in ("u4-st-teach-present-1", "u4-st-rem-present"):
        steps[sid]["body"] = edit(steps[sid]["body"], U4_OLD, U4_NEW, sid)
        changes.append((sid, "body", U4_OLD, U4_NEW))
        coverage_texts.append((4, "obj-u4-present", f"{sid}-body", steps[sid]["body"]))
    ref = next(r for r in pack["references"] if r["id"] == "ref-u4-present")
    ref["detail"] = edit(ref["detail"], U4_OLD, U4_NEW, "ref-u4-present")
    changes.append(("ref-u4-present", "detail", U4_OLD, U4_NEW))
    coverage_texts += [(4, "obj-u4-present", "ref-u4-present-detail", ref["detail"]), (4, "obj-u4-present", "u4-present-rule", U4_NEW)]
    if not any(r["itemId"] == "prov-u4-present-rule" for r in pack["provenanceRecords"]):
        raise SystemExit("prov-u4-present-rule is missing")

    # Coverage of new English wording (never a marker source).
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shipped = {}
    for r in pack["provenanceRecords"]:
        shipped.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    template = records["prov-t15an1-u6-possessive-our-your"]
    for unit, objective, key, value in coverage_texts:
        if value in shipped.get(unit, set()):
            continue
        record = copy.deepcopy(template)
        note = (f"New English wording authored on {DATE} ({TASK}); it did not exist before that date. It covers the wording"
                " only; the Russian facts it states are cited from frozen source records in the marker's russianFactSources."
                " 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{value}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + value
        item_id = f"prov-ca-t16n1-{key}"
        if item_id in records:
            raise SystemExit(f"Duplicate record {item_id}")
        record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                       "shippedText": value, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(value),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      f" this record's English text is new, authored {DATE} for {TASK}."})
        pack["provenanceRecords"].append(record)
        records[item_id] = record
        shipped.setdefault(unit, set()).add(value)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps(changes, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(changes)} field changes; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
