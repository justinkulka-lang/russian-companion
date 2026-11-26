#!/usr/bin/env python3
"""T17a / owner decision D34a (2026-11-22): CC033 owned-edition locators.

Exact-baseline transformer; runs once on the T16f DONE pack (f2ad5aad). Adds the three locators the audits found missing
(CC033) to the owned-edition mapping text: §15.8 p177 (PDF 189) in Unit 15, exercise 8/3 p89 (PDF 101) in Unit 8 and
exercise 10/1 p109 (PDF 121) in Unit 10. Each PDF index follows its lesson's stated offset (PDF = printed + 12), as every other
locator of those lessons does. Locators only: no book content is copied. No learner-visible field changes.
"""
from __future__ import annotations

import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "9b10daab0d93dfe1105556cd073c8b70c2fcd6ba6f4287ddae4265bc32ed9aa5"  # the T16f DONE pack (f2ad5aad)
EDITS = {  # unit: (old, new)
    15: ("§15.7 p177 (PDF 189); §15.9 p178 (PDF 190)", "§15.7 p177 (PDF 189); §15.8 p177 (PDF 189); §15.9 p178 (PDF 190)"),
    8: ("8/2 p85/PDF97; 8/4 p89/PDF101", "8/2 p85/PDF97; 8/3 p89/PDF101; 8/4 p89/PDF101"),
    10: ("Observed exercise locators: 10/2 p112/PDF124", "Observed exercise locators: 10/1 p109/PDF121; 10/2 p112/PDF124"),
}


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the D34 baseline; this transformer runs only once")
    order = list(json.loads(text).keys())
    pack = loads(text)
    for m in pack["ownedEditionMappings"]:
        if m["unitNumber"] in EDITS:
            old, new = EDITS[m["unitNumber"]]
            if m["topicDescription"].count(old) != 1:
                raise SystemExit(f"Unit {m['unitNumber']}: baseline locator text changed")
            m["topicDescription"] = m["topicDescription"].replace(old, new)
            print(f"Unit {m['unitNumber']}: {old!r} -> {new!r}")
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
