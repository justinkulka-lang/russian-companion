"""T15g A4 (owner decision D21a): re-cite six D7b selection records to same-sense frozen witnesses.

Exact-baseline transformer; runs once on the T15g pack. Each record keeps its itemId, unit, objective and shippedText;
its source binding (source, revision, record id, licence route) is copied from an existing frozen record of the target
file, and originalSourceText becomes the target line verbatim. Each record must still pass the D19a two-part rule
against that single line. Learner text does not change.
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
import apply_d7b_selection_records as d7b  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "176574db13487c837113f9195174feb3072d5f50b280461abd8637f0b7522f9d"  # the T15g Part B pack

RECITE = [  # (record, frozen file under catalog/sources, line, why)
    ("prov-d7b-u16-029", "units-6-10/wikibooks/adjectives-4292379.txt", 67, "instrumental ending -им (was dative plural, dative-case:96)"),
    ("prov-d7b-u25-038", "units-26-30/wikipedia/russian-grammar-1369617627.txt", 1169, "imperative suffix -те (was the demonstrative те, lesson3:232)"),
    ("prov-d7b-u30-055", "units-3-5/wikibooks/prepositions-4446858.txt", 134, "стоит, a vase stands on the table (was figurative, neopredelennoe:143)"),
    ("prov-d7b-u30-056", "units-3-5/openrussian/verbs.csv", 11, "стоять: stand, past стоял (was Toulon's location, vechera:1091)"),
    ("prov-d7b-u26-041", "units-3-5/openrussian/nouns.csv", 52, "Россия: Russia (was a quoted name, lichnoe-imya:286)"),
    ("prov-d7b-u12-028", "units-3-5/openrussian/verbs.csv", 3, "сказать: say, tell, perfective of говорить (was dative-case:7)"),
]


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15g A4 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    tmpl = d7b.templates(pack)
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    report = []
    for rid, rel, line, why in RECITE:
        r = records[rid]
        raw = (d7b.SOURCES / rel).read_text(encoding="utf-8").split("\n")[line - 1]
        region = r["shippedText"]
        if not d7b.two_part(d7b.displayed(raw), region):
            raise SystemExit(f"{rid}: {region} fails D19a against {rel}:{line}")
        t = tmpl[rel]
        prefix = t["sourceRecordId"].split(":", 1)[0]
        old = (r["sourceId"], r["sourceRecordId"])
        keep = {k: r[k] for k in ("itemId", "unitNumber", "objectiveId", "shippedText", "contentHash", "recordType")}
        new = copy.deepcopy(t)
        new.update(keep)
        new.update({"originalSourceText": raw, "sourceHash": sha(raw), "transformation": "selection",
                    "sourceRecordId": f"{prefix}:{raw.split(chr(9), 1)[0]}" if t["sourceId"].startswith("src-openrussian-") else f"{prefix}:{raw[:80]}"})
        n = r["unitNumber"]
        if 26 <= n <= 30 and t["sourceId"].startswith("src-wikibooks-") and "Wikibooks:Copyrights oldid 4622060" not in t["attribution"]:
            routed = next(x for x in pack["provenanceRecords"] if x["sourceId"] == t["sourceId"] and 26 <= x["unitNumber"] <= 30
                          and d7b.frozen_record(pack, x) and "Wikibooks:Copyrights oldid 4622060" in x["attribution"])
            new["attribution"] = routed["attribution"]
            new["shareAlikeTreatment"] = routed["shareAlikeTreatment"]
        r.clear()
        r.update(new)
        if not d7b.frozen_record(pack, r):
            raise SystemExit(f"{rid}: not a frozen-source record after re-citing")
        report.append([n, region, rid, f"catalog/sources/{rel}:{line}", t["sourceId"], f"{old[0]} {old[1][:60]}", why])
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        with open(sys.argv[1], "w", newline="", encoding="utf-8") as fh:
            csv.writer(fh).writerows(report)
    print(f"{len(report)} records; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
