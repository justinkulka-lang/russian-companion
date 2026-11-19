"""T15g D22/D23: frozen selection records for the Unit 1 letter names and the Unit 4 unstressed они, and two T15g
reference sentences reworded so a single letter is not joined to a phrase by a colon.

Exact-baseline transformer; runs once on the A4 pack.
- D22a (with D23): each of the 23 letter names Unit 1 displays gets a selection record from its own letter's row on
  units-26-30/wikipedia/russian-alphabet-1371795481.txt (lines 59-171), passing the D19a two-part rule.
- D22b: Unit 4's unstressed они cites the stressed они' of units-3-5/openrussian/others.csv:15 (pronoun "they").
- ref-u7-adjectives and ref-u10-genitive: "written е: хорошее письмо" and "gain н: Для него́" become "written е, as in
  хорошее письмо" and "gain н, as in Для него́" (D21c joins; no Russian changes).
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
BASELINE_SHA256 = "eeca80e3df59a3093a17a259aa8f7b48006ccb087f170707a09856c393d6685c"  # the T15g A4 pack
ALPHABET = "units-26-30/wikipedia/russian-alphabet-1371795481.txt"
NAMES = [  # (displayed name, letter whose row attests it, row image name)
    ("бэ", "Б б"), ("вэ", "В в"), ("гэ", "Г г"), ("дэ", "Д д"), ("жэ", "Ж ж"), ("зэ", "З з"), ("и краткое", "Й й"),
    ("ка", "К к"), ("эль", "Л л"), ("эм", "М м"), ("эн", "Н н"), ("пэ", "П п"), ("эр", "Р р"), ("эс", "С с"),
    ("тэ", "Т т"), ("эф", "Ф ф"), ("ха", "Х х"), ("цэ", "Ц ц"), ("че", "Ч ч"), ("ша", "Ш ш"), ("ща", "Щ щ"),
    ("твёрдый знак", "ъ"), ("мягкий знак", "ь"),
]
REWORD = [
    ("ref-u7-adjectives", "an unstressed о in an ending is written е: хорошее письмо", "an unstressed о in an ending is written е, as in хорошее письмо"),
    ("ref-u10-genitive", "After a preposition, его, её and их gain н: Для него́", "After a preposition, его, её and их gain н, as in Для него́"),
]


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def selection(pack: dict, tmpl: dict, rel: str, line: int, region: str, unit: int, objective: str, rid: str) -> dict:
    raw = (d7b.SOURCES / rel).read_text(encoding="utf-8").split("\n")[line - 1]
    if not d7b.two_part(d7b.displayed(raw), region):
        raise SystemExit(f"{region} fails D19a against {rel}:{line}")
    t = tmpl[rel]
    prefix = t["sourceRecordId"].split(":", 1)[0]
    r = copy.deepcopy(t)
    r.update({"itemId": rid, "unitNumber": unit, "objectiveId": objective, "recordType": "lexical record",
              "transformation": "selection", "originalSourceText": raw, "shippedText": region,
              "sourceHash": sha(raw), "contentHash": sha(region),
              "sourceRecordId": f"{prefix}:{raw.split(chr(9), 1)[0]}" if t["sourceId"].startswith("src-openrussian-") else f"{prefix}:{raw[:80]}"})
    if not d7b.frozen_record(pack, r):
        raise SystemExit(f"{rid} is not a frozen-source record")
    return r


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15g D22 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    tmpl = d7b.templates(pack)
    lines = (d7b.SOURCES / ALPHABET).read_text(encoding="utf-8").split("\n")
    report = []
    for k, (name, letter) in enumerate(NAMES, 1):
        rows = [i for i in range(59, 172) if f"Russian alphabet-{letter}.svg" in lines[i - 1]]
        if len(rows) != 1:
            raise SystemExit(f"{name}: no single row for {letter}")
        rid = f"prov-t15g-u1-name-{k:02d}"
        pack["provenanceRecords"].append(selection(pack, tmpl, ALPHABET, rows[0], name, 1, "obj-u1-letter-names", rid))
        report.append([1, name, rid, f"catalog/sources/{ALPHABET}:{rows[0]}"])
    rel = "units-3-5/openrussian/others.csv"
    pack["provenanceRecords"].append(selection(pack, tmpl, rel, 15, "они", 4, "obj-u4-pronouns", "prov-t15g-u4-oni"))
    report.append([4, "они", "prov-t15g-u4-oni", f"catalog/sources/{rel}:15"])
    refs = {r["id"]: r for r in pack["references"]}
    for rid, old, new in REWORD:
        if refs[rid]["detail"].count(old) != 1:
            raise SystemExit(f"{rid}: sentence not found once")
        refs[rid]["detail"] = refs[rid]["detail"].replace(old, new)
        report.append(["reference", rid, old, new])
    encoded = encode(pack)
    ordered = {key: encoded[key] for key in original_order if key in encoded}
    ordered.update({key: v for key, v in encoded.items() if key not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        with open(sys.argv[1], "w", newline="", encoding="utf-8") as fh:
            csv.writer(fh).writerows(report)
    print(f"{len(report)} rows; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
