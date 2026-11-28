#!/usr/bin/env python3
"""T18a-1 (owner decisions D54a, D54d): Unit 21 source R and the object-reference love example.

D54a, path A: the pending source R (English Wikipedia "Reflexive pronoun", oldid 1374712982) is already frozen. S1 (owner
decisions D16a, D24c; commit cdfba75e) froze that exact revision at catalog/sources/s1-intake/wikipedia/
reflexive-pronoun-1374712982.txt with its revision metadata and FREEZE.json entry; T18a-1 re-fetched the same oldid (raw
wikitext) and the bytes are identical (sha1 64b35f5856e88d58e8140de214d3aa14d2d26892, the revision sha1; 48,253 bytes). The
four prov-u21-object-transfer-native-{self,other,reflexive,personal} records already name that source and revision, their
originalSourceText and shipped selections occur verbatim in the frozen file, and the FrozenSourceBindingTest exception list
is already empty (S1 shrank it to zero). No second copy is frozen and no record changes for D54a; check_d54a() verifies it.

D54d: wherever Unit 21 shows the love example without its parentheses, it shows exactly the frozen G:508 form, the one
ex-u21-object-reference-1 already displays and prov-ex-u21-object-reference-1-ru ships (Russian grammar oldid 1369617627,
line 508). The sentence is in u21-st-teach-object-reference-2 and, repeated, in u21-st-rem-object-reference. No Russian is
added: the change restores the frozen parentheses. Each changed body gets a coverage record (prov-ca-t18a1-), and every
change is logged in audit/T18a-1-unit21-changes.csv.
"""
from __future__ import annotations

import copy
import csv
import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
LOG = ROOT / "audit/T18a-1-unit21-changes.csv"
BASELINE_SHA256 = "6f2792b6"  # prefix of the T17f-n1 pack (merged into main as ecffb336); checked in main()
TASK, DATE = "T18a-1", "2026-11-26"

R_FILE = ROOT / "catalog/sources/s1-intake/wikipedia/reflexive-pronoun-1374712982.txt"
R_SHA1 = "64b35f5856e88d58e8140de214d3aa14d2d26892"
R_SOURCE = "src-enwiki-reflexive-pronoun-1374712982"
R_RECORDS = [f"prov-u21-object-transfer-native-{k}" for k in ("self", "other", "reflexive", "personal")]

G_FILE = ROOT / "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt"
G_LINE = 508
FROZEN_FORM_RECORD = "prov-ex-u21-object-reference-1-ru"
# The two forms are assembled from the frozen line so that no Russian is typed here.
STEPS = ("u21-st-teach-object-reference-2", "u21-st-rem-object-reference")


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def frozen_forms(records: dict) -> tuple[str, str]:
    """The displayed G:508 form (from its frozen selection record, checked against the line) and the same words without the
    parentheses, the form the two bodies show now."""
    record = records[FROZEN_FORM_RECORD]
    lines = G_FILE.read_text(encoding="utf-8").splitlines()
    line, span = lines[G_LINE - 1], "\n".join(lines[G_LINE - 1:G_LINE + 1])  # the admitted reflexive span is G:508-509
    shown = record["shippedText"]
    if record["originalSourceText"] != span or record["sourceHash"] != sha(span) or record["sourceRevision"] != "1369617627":
        raise SystemExit(f"{FROZEN_FORM_RECORD} is not G:{G_LINE}")
    if shown.count("(") != 1 or shown.count(")") != 1 or shown not in line.replace("'''", ""):
        raise SystemExit(f"G:{G_LINE} does not show {shown!r} with its parenthesized possessive")
    return shown, shown.replace("(", "").replace(")", "")


def check_d54a(pack: dict) -> None:
    raw = R_FILE.read_bytes()
    if hashlib.sha1(raw).hexdigest() != R_SHA1:
        raise SystemExit("the frozen Reflexive pronoun file is not revision 1374712982")
    text = raw.decode("utf-8")
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    for rid in R_RECORDS:
        r = records[rid]
        if r["sourceId"] != R_SOURCE or r["sourceRevision"] != "1374712982":
            raise SystemExit(f"{rid} does not name the frozen revision")
        if r["originalSourceText"] not in text or r["shippedText"] not in r["originalSourceText"]:
            raise SystemExit(f"{rid}: text is not verbatim in the frozen file")
        if r["sourceHash"] != sha(r["originalSourceText"]):
            raise SystemExit(f"{rid}: sourceHash does not match its span")


def coverage(pack: dict, records: dict, objective: str, key: str, text: str) -> None:
    template = records["prov-t15an1-u6-possessive-our-your"]
    record = copy.deepcopy(template)
    note = (f"New English wording authored on {DATE} ({TASK}); it did not exist before that date. It covers the wording"
            " only; the Russian it shows is the frozen Russian grammar oldid 1369617627 line 508 form shipped by"
            f" {FROZEN_FORM_RECORD}. 2026-10-08 is the date of the reused research context only.")
    record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                "objectiveOrClaimBinding": f"{objective}:{key}"})
    original = note + "\n" + text
    item_id = f"prov-ca-t18a1-{key}"
    if item_id in records:
        raise SystemExit(f"Duplicate record {item_id}")
    record.update({"itemId": item_id, "unitNumber": 21, "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                   "shippedText": text, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                   "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                  f" this record's English text is new, authored {DATE} for {TASK}."})
    pack["provenanceRecords"].append(record)
    records[item_id] = record


def apply(pack: dict) -> list[dict]:
    check_d54a(pack)
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shown, bare = frozen_forms(records)
    unit = next(u for u in pack["units"] if u["number"] == 21)
    steps = {st["id"]: st for se in unit["sessions"] for st in se["steps"]}
    objective = "obj-u21-object-reference"
    o = next(o for o in unit["objectives"] if o["id"] == objective)
    log: list[dict] = []
    for sid in STEPS:
        if sid not in o["instructionStepIds"] + o["remediationStepIds"]:
            raise SystemExit(f"{sid} is not an {objective} step")
        step = steps[sid]
        old = step["body"]
        if old.count(bare) != 1:
            raise SystemExit(f"{sid}: expected one unparenthesized form, found {old.count(bare)}")
        if step.get("russianFacts"):
            raise SystemExit(f"{sid}: fact markers are T18a-2 work; this transformer expects none")
        new = old.replace(bare, shown)
        step["body"] = new
        log.append({"task": TASK, "finding": "D54d", "owner.field": f"{sid}.body", "old": old, "new": new})
        coverage(pack, records, objective, f"{sid}-body", new)
    for u in pack["units"]:
        if u["number"] == 21 and bare in json.dumps(u, ensure_ascii=False):
            raise SystemExit("Unit 21 still shows the unparenthesized form")
    return log


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if not sha(text).startswith(BASELINE_SHA256):
        raise SystemExit("Asset is not the T17f-n1 baseline; this transformer runs only once")
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
