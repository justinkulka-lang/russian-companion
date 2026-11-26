#!/usr/bin/env python3
"""T17g (owner decisions D51b, D52c): learner text that frames a table column.

D51b: the five Unit 20 sentences that describe the motion verbs as table columns (u20-st-teach-motion-2 and u20-st-rem-motion,
two sentences each; u20-g-motion-0 distractorRationales[1]) are rewritten to describe the verbs directly. The facts stay the
ones the old sentences stated and keep their frozen records (the motion lines G:1248, 1286-1299): a multidirectional partner
covers repeated, return or different-direction movement according to context, and the basic unprefixed verbs of both kinds are
imperfective. The extended learner-voice lint (column framing, V2DesignDiagnostics) also found column framing in Units 3, 6,
22 and 30; each is reworded here without changing what it states:
- Units 3 and 6 binder tasks: the learner's own table, now in the exempt own-table wording ("make a table with columns headed
  ...", "... column of your table"); the Unit 6 tutor task names the learner's own binder table.
- Unit 22: "the collective column" -> "the collective number forms" (the frozen collective forms the unit teaches).
- Unit 30: "in another column for father/bad" -> the frozen alphabet table's "Approximate English equivalent" heading
  (russian-alphabet-1371795481.txt lines 49, 56-57, 60-61: father is the equivalent for А, bad for Б).
No Russian is added or changed. New English wording gets a coverage record (prov-ca-t17g-). The Unit 20 corrected-intake
capability spans carry every Unit 20 motion instruction and remediation body; they are rebound as in T17e. Every change is
logged in audit/T17g-column-changes.csv.
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
from apply_t17a_unit_16 import sha  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
LOG = ROOT / "audit/T17g-column-changes.csv"
BASELINE_SHA256 = "73b1d5bd"  # prefix of the T17f pack (98a95ab3); checked in main()
TASK, DATE = "T17g", "2026-11-26"

U20_OTHER = "Its other column is multidirectional, covering repeated, return or different-direction movement according to context."
U20_OTHER_NEW = "Its multidirectional partner covers repeated, return or different-direction movement according to context."
U20_BASIC = "The basic unprefixed forms in these columns are imperfective; unidirectional does not itself mean perfective or arrival."
U20_BASIC_NEW = ("The basic unprefixed unidirectional and multidirectional verbs are both imperfective; unidirectional does not itself"
                 " mean perfective or arrival.")
U20_RATIONALE = "multidirectional is the other motion column, covering different-direction or repeated movement according to context."
U20_RATIONALE_NEW = ("multidirectional describes a partner verb that covers different-direction or repeated movement according to"
                     " context, not motion in one direction.")

U3_OLD = ("In your binder, make three columns headed masculine, feminine and neuter. Write each word from this unit's three groups"
          " in its column with its meaning. Add дя́дя and мужчи́на to the masculine column.")
U3_NEW = ("In your binder, make a table with columns headed masculine, feminine and neuter. Write each word from this unit's three"
          " groups in the right column of your table with its meaning. Add дя́дя and мужчи́на to the masculine column of your table.")
U6_OLD_HEAD = "In your binder, make two columns headed location and direction. "
U6_NEW_HEAD = "In your binder, make a table with columns headed location and direction. "
U6_OLD_ROW = "into the right column with their meanings"
U6_NEW_ROW = "into the right column of your table with their meanings"
U6_TUTOR_OLD = "Your binder location and direction columns, to read each line aloud and say whether it names a location or a direction."
U6_TUTOR_NEW = "Your binder table of location and direction lines, to read each line aloud and say whether it names a location or a direction."

U22 = [
    ("topicSummary", "the half-form usage note, and the collective column await qualified review",
     "the half-form usage note, and the collective number forms await qualified review"),
    ("u22-st-orient", "the frozen half-form usage note, and the frozen collective column.",
     "the frozen half-form usage note, and the frozen collective number forms."),
    ("u22-st-teach-collective-3", "Selecting the collective column does not demonstrate", "Selecting the collective form does not demonstrate"),
    ("u22-st-rem-collective", "Selecting the collective column does not demonstrate", "Selecting the collective form does not demonstrate"),
]
U30 = {
    ("u30-g-letter-names-0", "father"): "father is the approximate English equivalent of the letter's sound, not the respelling beside its name.",
    ("u30-i-letter-names-1", "bad"): "bad is the approximate English equivalent of the letter's sound, not the respelling beside its name.",
}

CAPABILITY_SPANS = ("cap-u20-motion-po-setting-out", "cap-u20-motion-prefixed-path")
SPAN_HASHES: list = []


def coverage(pack: dict, records: dict, unit: int, objective: str, key: str, text: str) -> None:
    template = records["prov-t15an1-u6-possessive-our-your"]
    record = copy.deepcopy(template)
    note = (f"New English wording authored on {DATE} ({TASK}); it did not exist before that date. It covers the wording"
            " only; the Russian facts it states are cited from frozen source records in the marker's russianFactSources."
            " 2026-10-08 is the date of the reused research context only.")
    record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                "objectiveOrClaimBinding": f"{objective}:{key}"})
    original = note + "\n" + text
    item_id = f"prov-ca-t17g-{key}"
    if item_id in records:
        raise SystemExit(f"Duplicate record {item_id}")
    record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                   "shippedText": text, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                   "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                  f" this record's English text is new, authored {DATE} for {TASK}."})
    pack["provenanceRecords"].append(record)
    records[item_id] = record


def replace_once(text: str, old: str, new: str, where: str) -> str:
    if text.count(old) != 1:
        raise SystemExit(f"{where}: expected one {old!r}")
    return text.replace(old, new)


def step_objective(unit: dict, sid: str) -> str:
    for o in unit["objectives"]:
        if sid in o["instructionStepIds"] + o["remediationStepIds"] + o.get("practiceStepIds", []):
            return o["id"]
    return next(se for se in unit["sessions"] if any(s["id"] == sid for s in se["steps"]))["objectiveIds"][0]


def swap_fact(host: dict, old: str, new: str, where: str) -> None:
    facts, sources = host.get("russianFacts"), host.get("russianFactSources")
    if not facts or old not in facts:
        return
    if new in facts:
        raise SystemExit(f"{where}: {new!r} already marked")
    facts[facts.index(old)] = new
    sources[new] = sources.pop(old)
    host["russianFactSources"] = {f: sources[f] for f in facts}


def u20_bodies(pack: dict) -> dict:
    unit = next(u for u in pack["units"] if u["number"] == 20)
    objective = next(o for o in unit["objectives"] if o["id"] == "obj-u20-motion")
    steps = {st["id"]: st for se in unit["sessions"] for st in se["steps"]}
    return {sid: steps[sid]["body"] for sid in objective["instructionStepIds"] + objective["remediationStepIds"]}


def rebind_capability_spans(pack: dict, old: dict, new: dict) -> None:
    """As T17e: each old Unit 20 body copy in the two corrected-intake spans is replaced by its new body; the host records that
    carry the span follow (a host that ships a changed body ships the new one), and the hashes are recomputed."""
    for cap in pack["sourceCapabilities"]:
        if cap["id"] not in CAPABILITY_SPANS:
            continue
        span = cap["exactSpan"]
        if cap["sourceHash"] != sha(span):
            raise SystemExit(f"{cap['id']}: sourceHash did not follow exactSpan")
        updated = span
        for sid in sorted(old, key=lambda k: -len(old[k])):
            if old[sid] != new[sid]:
                if old[sid] not in span:
                    raise SystemExit(f"{cap['id']}: {sid} was not bound")
                updated = updated.replace(old[sid], new[sid])
        missing = [sid for sid in new if new[sid] not in updated]
        if missing or "column" in updated.replace("columns headed", ""):
            raise SystemExit(f"{cap['id']}: bodies unbound {missing} or column wording left")
        for record in pack["provenanceRecords"]:
            if record.get("originalSourceText") == span:
                if record["sourceHash"] != sha(span):
                    raise SystemExit(f"{record['itemId']}: sourceHash did not follow originalSourceText")
                record["originalSourceText"], record["sourceHash"] = updated, sha(updated)
                for sid in sorted(old, key=lambda k: -len(old[k])):
                    if old[sid] != new[sid] and record["shippedText"] == old[sid]:
                        if record["contentHash"] != sha(old[sid]):
                            raise SystemExit(f"{record['itemId']}: contentHash did not follow shippedText")
                        record["shippedText"], record["contentHash"] = new[sid], sha(new[sid])
                        break
                if record["shippedText"] not in updated:
                    raise SystemExit(f"{record['itemId']}: shipped text is not in the rebound span")
        SPAN_HASHES.append((cap["id"], cap["sourceHash"], sha(updated)))
        cap["exactSpan"], cap["sourceHash"] = updated, sha(updated)


def apply(pack: dict) -> list[dict]:
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    units = {u["number"]: u for u in pack["units"]}
    steps = {s["id"]: (u, s) for u in pack["units"] for se in u["sessions"] for s in se["steps"]}
    items = {i["id"]: i for i in pack["items"]}
    log: list[dict] = []

    def body(sid: str, pairs: list[tuple[str, str]], finding: str) -> None:
        unit, step = steps[sid]
        old = step["body"]
        new = old
        for a, b in pairs:
            new = replace_once(new, a, b, sid)
            swap_fact(step, a, b, sid)
        step["body"] = new
        log.append({"task": TASK, "finding": finding, "owner.field": f"{sid}.body", "old": old, "new": new})
        objective = step_objective(unit, sid)
        coverage(pack, records, unit["number"], objective, f"{sid}-body", new)
        for k, (a, b) in enumerate(pairs):
            if b.endswith("."):
                coverage(pack, records, unit["number"], objective, f"{sid}-s{k}", b)

    # D51b: Unit 20
    before = u20_bodies(pack)
    for sid in ("u20-st-teach-motion-2", "u20-st-rem-motion"):
        body(sid, [(U20_OTHER, U20_OTHER_NEW), (U20_BASIC, U20_BASIC_NEW)], "D51b")
    item = items["u20-g-motion-0"]
    k = item["distractorRationales"].index(U20_RATIONALE)
    item["distractorRationales"][k] = U20_RATIONALE_NEW
    difference = item["assessment"]["distractorDifference"]
    assert difference[k] == U20_RATIONALE
    difference[k] = U20_RATIONALE_NEW
    swap_fact(item, U20_RATIONALE, U20_RATIONALE_NEW, item["id"])
    log += [{"task": TASK, "finding": "D51b", "owner.field": f"u20-g-motion-0.distractorRationales[{k}]", "old": U20_RATIONALE, "new": U20_RATIONALE_NEW},
            {"task": TASK, "finding": "D51b", "owner.field": f"u20-g-motion-0.assessment.distractorDifference[{k}]", "old": U20_RATIONALE, "new": U20_RATIONALE_NEW}]
    coverage(pack, records, 20, item["objectiveId"], f"u20-g-motion-0-rationale{k}", U20_RATIONALE_NEW)
    rebind_capability_spans(pack, before, u20_bodies(pack))

    # Column-framing lint findings outside the D51b five.
    task = units[3]["binderTask"]
    assert task["prompt"] == U3_OLD
    task["prompt"] = U3_NEW
    log.append({"task": TASK, "finding": "column lint", "owner.field": f"{task['id']}.prompt", "old": U3_OLD, "new": U3_NEW})
    coverage(pack, records, 3, task["objectiveIds"][0], f"{task['id']}-prompt", U3_NEW)
    task = units[6]["binderTask"]
    old = task["prompt"]
    task["prompt"] = replace_once(replace_once(old, U6_OLD_HEAD, U6_NEW_HEAD, task["id"]), U6_OLD_ROW, U6_NEW_ROW, task["id"])
    log.append({"task": TASK, "finding": "column lint", "owner.field": f"{task['id']}.prompt", "old": old, "new": task["prompt"]})
    coverage(pack, records, 6, task["objectiveIds"][0], f"{task['id']}-prompt", task["prompt"])
    tutor = units[6]["tutorTask"]
    assert tutor["bring"] == U6_TUTOR_OLD
    tutor["bring"] = U6_TUTOR_NEW
    log.append({"task": TASK, "finding": "column lint", "owner.field": f"{tutor['id']}.bring", "old": U6_TUTOR_OLD, "new": U6_TUTOR_NEW})
    coverage(pack, records, 6, units[6]["binderTask"]["objectiveIds"][0], f"{tutor['id']}-bring", U6_TUTOR_NEW)

    for owner, a, b in U22:
        if owner == "topicSummary":
            old = units[22]["topicSummary"]
            units[22]["topicSummary"] = replace_once(old, a, b, "unit-22 topicSummary")
            log.append({"task": TASK, "finding": "column lint", "owner.field": "unit-22.topicSummary", "old": old, "new": units[22]["topicSummary"]})
            coverage(pack, records, 22, units[22]["objectives"][0]["id"], "unit-22-topicSummary", units[22]["topicSummary"])
        else:
            body(owner, [(a, b)], "column lint")

    for (iid, option), new in U30.items():
        item = items[iid]
        k = item["options"].index(option)
        old = item["distractorRationales"][k]
        assert "another column for " + option in old, (iid, old)
        item["distractorRationales"][k] = new
        assert not (item.get("assessment") or {}).get("distractorDifference")
        log.append({"task": TASK, "finding": "column lint", "owner.field": f"{iid}.distractorRationales[{k}]", "old": old, "new": new})
        coverage(pack, records, 30, item["objectiveId"], f"{iid}-rationale{k}", new)
    print("capability span hashes:", SPAN_HASHES)
    return log


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if not sha(text).startswith(BASELINE_SHA256):
        raise SystemExit("Asset is not the T17f baseline; this transformer runs only once")
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
