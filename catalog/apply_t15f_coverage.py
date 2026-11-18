"""T15f: source coverage for the Units 1-10 wording changed by apply_t15f_units_1_10.py and apply_t15f_directions.py.

Runs last. Follows the T15e Builder.coverage pattern: every changed teaching body, remediation body, item feedback and
distractor rationale in Units 1-10 gets a content-audit coverage record for its new English wording (the whole field,
and each new marked sentence on its own), so the existing V2Validator English-coverage rules hold unchanged. The
Russian facts those sentences state stay cited by the frozen-source records in each marker's russianFactSources.
Russian words and phrases that the restored Unit 7 sentences show on their own get selection records of the same
frozen segments as their examples (C:79, W:62, W:82). Instruction-step fields (book, AnkiDroid, binder, tutor,
cumulative, guided, check) are field-exempt in the validator and need no coverage.
"""
from __future__ import annotations

import copy
import hashlib
import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "24bb64f960e0df20fb04368ed0dfbea8a4b591c9c6c1157e4998472cc401db0b"
BEFORE_COMMIT = "1bf80b92"  # the pack before T15f
TASK = "T15f"

# (record key, example record whose frozen segment is selected, shipped Russian)
SELECTIONS = [
    ("novuyu", "prov-ex-u7-endings-11-ru", "новую"),
    ("uchitelnitsu", "prov-ex-u7-endings-11-ru", "учительницу"),
    ("moego-starogo-doma", "prov-ex-u7-endings-12-ru", "моего старого дома"),
    ("khoroshimi", "prov-ex-u7-spelling-5-ru", "хоро́шими"),
    ("knigami", "prov-ex-u7-spelling-5-ru", "кни́гами"),
]


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def hosts(pack: dict) -> dict[tuple[str, str], tuple[int, str, str, list[str]]]:
    """(owner, field) -> (unit, objective, text, marked sentences) for the Units 1-10 fields with English coverage."""
    out = {}
    objective_of = {}
    for unit in pack["units"]:
        for objective in unit["objectives"]:
            for step_id in objective.get("instructionStepIds", []) + objective.get("remediationStepIds", []):
                objective_of.setdefault(step_id, objective["id"])
    unit_of = {o["id"]: u["number"] for u in pack["units"] for o in u["objectives"]}
    for unit in pack["units"]:
        if unit["number"] > 10:
            continue
        for session in unit["sessions"]:
            for step in session["steps"]:
                if step["kind"] in ("TEACH", "REMEDIATE"):
                    objective = objective_of.get(step["id"], unit["objectives"][0]["id"])
                    out[(step["id"], "body")] = (unit["number"], objective, step["body"], list(step.get("russianFacts", [])))
    for item in pack["items"]:
        number = unit_of.get(item["objectiveId"], 99)
        if number > 10:
            continue
        facts = list(item.get("russianFacts", []))
        out[(item["id"], "feedback")] = (number, item["objectiveId"], item["feedback"], facts)
        for n, value in enumerate(item.get("distractorRationales", [])):
            out[(item["id"], f"rationale{n}")] = (number, item["objectiveId"], value, facts)
    return out


def apply(pack: dict, before: dict) -> dict:
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shipped_by_unit: dict[int, set[str]] = {}
    for r in pack["provenanceRecords"]:
        shipped_by_unit.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    template = records["prov-t15an1-u6-possessive-our-your"]
    report: dict = {"coverage": [], "selections": []}

    def coverage(unit: int, objective: str, key: str, text: str) -> None:
        if text in shipped_by_unit.get(unit, set()):
            return
        record = copy.deepcopy(template)
        note = (f"New English wording authored on 2026-11-17 ({TASK}); it did not exist before that date. It covers the"
                " wording only; the Russian facts it states are cited from frozen source records in the marker's"
                " russianFactSources. 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        item_id = f"prov-ca-t15f-{key}"
        if item_id in records:
            raise SystemExit(f"Duplicate record {item_id}")
        record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective,
                       "sourceRecordId": f"ca-coverage:{objective}:{key}", "shippedText": text,
                       "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      " this record's English text is new, authored 2026-11-17 for T15f."})
        pack["provenanceRecords"].append(record)
        records[item_id] = record
        shipped_by_unit.setdefault(unit, set()).add(text)
        report["coverage"].append([item_id, unit, text])

    old_hosts = hosts(before)
    for (owner, field), (unit, objective, text, facts) in sorted(hosts(pack).items()):
        old = old_hosts.get((owner, field))
        if old is not None and old[2] == text:
            continue
        coverage(unit, objective, f"{owner}-{field}", text)
        old_facts = set(old[3]) if old else set()
        for n, fact in enumerate(f for f in facts if f not in old_facts):
            coverage(unit, objective, f"{owner}-{field}-fact{n}", fact)

    for key, source_id, shipped in SELECTIONS:
        source = records[source_id]
        if shipped not in source["originalSourceText"]:
            raise SystemExit(f"{key}: not in the frozen segment of {source_id}")
        record = copy.deepcopy(source)
        record.update({"itemId": f"prov-t15f-u7-{key}", "recordType": "lexical record", "shippedText": shipped,
                       "contentHash": sha(shipped)})
        pack["provenanceRecords"].append(record)
        report["selections"].append([record["itemId"], source["sourceRecordId"], shipped])
    return report


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15f coverage baseline; this transformer runs only once")
    before = loads(subprocess.check_output(["git", "show", f"{BEFORE_COMMIT}:app/src/main/assets/content/v2-curriculum.json"],
                                           cwd=ROOT, text=True))
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report = apply(pack, before)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
