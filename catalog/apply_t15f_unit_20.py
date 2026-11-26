"""T15f Part A2: the bold-prefix stress misreading in Unit 20 (exact-baseline transformer).

russian-grammar-1369617627.txt:1383 bolds the prefix letter of '''у'''летят to show the prefix,
not stress. The old Wikibooks formatter (catalog/units_3_5_intake.py) turned that bold into an
acute, so the pack showed the misreading. The frozen sentence is unstressed, so the learner text,
the provenance shipped text and every content-audit and capability copy now read "улетят", as the
frozen line spells it. Hashes follow the existing rules: sourceHash = sha256(originalSourceText),
contentHash = sha256(shippedText), capability sourceHash = sha256(exactSpan). Only hashes that
matched those rules before the change are recomputed.
"""
from __future__ import annotations

import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from units_3_5_intake import format_wikibooks_emphasis  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "698fa1cef6e292abf85e257f8b43f327932d4506c0a9e7a823f246035dcee11e"
FROZEN = ROOT / "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt"
OLD = "у́летят"  # the misread form
NEW = "улетят"


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def fix(value):
    if isinstance(value, str):
        return value.replace(OLD, NEW)
    if isinstance(value, list):
        return [fix(v) for v in value]
    if isinstance(value, dict):
        return {k: fix(v) for k, v in value.items()}
    return value


def apply(pack: dict) -> dict:
    line = FROZEN.read_text(encoding="utf-8").splitlines()[1382]
    formatted = format_wikibooks_emphasis(line)
    if NEW not in formatted or OLD in formatted:
        raise SystemExit("The fixed formatter does not give the unstressed frozen form at G:1383")
    report = {"changedPaths": [], "hashes": []}
    for key in ("examples", "units", "sourceCapabilities", "provenanceRecords"):
        rows = pack[key]
        for index, row in enumerate(rows):
            if OLD not in json.dumps(row, ensure_ascii=False):
                continue
            before = row
            after = fix(row)
            ident = after.get("itemId") or after.get("id")
            if key == "provenanceRecords":
                for field, source in (("sourceHash", "originalSourceText"), ("contentHash", "shippedText")):
                    if before.get(source) != after.get(source):
                        if before[field] != sha(before[source]):
                            raise SystemExit(f"{ident}: {field} did not follow {source}")
                        after[field] = sha(after[source])
                        report["hashes"].append([ident, field, before[field], after[field]])
            if key == "sourceCapabilities" and before["exactSpan"] != after["exactSpan"]:
                if before["sourceHash"] != sha(before["exactSpan"]):
                    raise SystemExit(f"{ident}: sourceHash did not follow exactSpan")
                after["sourceHash"] = sha(after["exactSpan"])
                report["hashes"].append([ident, "sourceHash", before["sourceHash"], after["sourceHash"]])
            rows[index] = after
            report["changedPaths"].append(f"{key}:{ident}")
    if OLD in json.dumps(pack, ensure_ascii=False):
        raise SystemExit("A misread form remains")
    return report


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15f baseline; this transformer runs only once")
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
