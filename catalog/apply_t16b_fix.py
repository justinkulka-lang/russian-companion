#!/usr/bin/env python3
"""T16b CI fix (exact-commit CI 44825173504 on 38bee8a6 failed on Units2630SourceIntakeTest:499,
Units2630StructuralMappingTest:396 and Units35SourceIntakeTest:64).

prov-t16b-u12-ga20 recorded the whole of grammatical-aspect-1384289498.txt:20, including a <ref> citation whose URL ends in
.pdf; the pack must not carry a .pdf reference. The record is re-cited to the exact span of that line that states the claim
(the process / completed-whole distinction), with its hashes recomputed. Exact-baseline transformer; runs once on the T16-n1
pack (b5abd07b).
"""
from __future__ import annotations

import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
import apply_d7b_selection_records as d7b  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "3d26c35b1dd548c751f114b03a5edd71a26e4a879a35e72fc01e4161de1dbb5d"  # the T16-n1 pack (b5abd07b)
LINE = ROOT / "catalog/sources/s1-intake/wikipedia/grammatical-aspect-1384289498.txt"
SPAN = ("The Indian linguist [[Yaska]] (circa 7th century BCE) dealt with grammatical aspect, distinguishing actions that are"
        " processes (''bhāva''), from those where the action is considered as a completed whole (''mūrta''). This is the key"
        " distinction between the imperfective and perfective.")


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T16b-fix baseline; this transformer runs only once")
    if LINE.read_text(encoding="utf-8").split("\n")[19].count(SPAN) != 1:
        raise SystemExit("span is not on grammatical-aspect:20")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    record = next(r for r in pack["provenanceRecords"] if r["itemId"] == "prov-t16b-u12-ga20")
    record.update({"originalSourceText": SPAN, "shippedText": SPAN, "sourceHash": sha(SPAN), "contentHash": sha(SPAN),
                   "sourceRecordId": "grammatical-aspect:" + SPAN[:80]})
    if not d7b.frozen_record(pack, record) or ".pdf" in json.dumps(record, ensure_ascii=False):
        raise SystemExit("re-cited record is not a clean frozen-source record")
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"prov-t16b-u12-ga20 re-cited; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
