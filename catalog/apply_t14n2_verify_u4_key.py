#!/usr/bin/env python3
"""T14-n2 verify (CC021): the Unit 4 nominative remediation states each check's cue and key.

Exact-baseline transformer. The first nominative teaching step said "The subject noun is nominative."; the
checks' key is "The noun names the subject, in the nominative case." The teaching now states the key in those
words (same meaning, already shipped as the option text), so the remediation and the reference entry that
repeat the teaching contain it too. The rewritten host keeps exact research-synthesis coverage with a dated
record, as in T14-n2. No Russian changes.
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from apply_t14a_units_1_2 import sha  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "43f5fe8717c419c11265fae656d076cce10c521d4201c48c3ac7dc4045cfb405"
OLD = "The subject noun is nominative."
KEY = "The noun names the subject, in the nominative case."


def apply(pack: dict) -> dict:
    steps = {s["id"]: s for u in pack["units"] for se in u["sessions"] for s in se["steps"]}
    items = {i["id"]: i for i in pack["items"]}
    if any(items[i]["answer"] != KEY for i in ("u4-i-nom-role-train", "u4-i-nom-role-student")):
        raise SystemExit("Unit 4 nominative key changed")
    changed = {}
    teach = steps["u4-st-teach-nominative-1"]
    if teach["body"].count(OLD) != 1:
        raise SystemExit("u4-st-teach-nominative-1 baseline changed")
    changed[teach["body"]] = teach["body"].replace(OLD, KEY)
    teach["body"] = changed[teach["body"]]
    rem = steps["u4-st-rem-nominative"]
    new_rem = "\n\n".join(steps[s]["body"] for s in ("u4-st-teach-nominative-1", "u4-st-teach-nominative-2", "u4-st-teach-nominative-3"))
    changed[rem["body"]] = new_rem
    rem["body"] = new_rem
    for r in pack["references"]:
        if r["id"] == "ref-u4-nominative":
            r["detail"] = "\n\n".join(steps[s]["body"].strip() for s in (
                "u4-st-teach-nominative-1", "u4-st-teach-nominative-2", "u4-st-teach-nominative-3"))
    added = []
    for r in pack["provenanceRecords"]:
        if r["sourceId"] == "src-content-audit-research" and r["shippedText"] in changed and "synthesis" in r:
            new_text = changed[r["shippedText"]]
            synthesis = dict(r["synthesis"])
            synthesis["bibliography"] += (" T14-n2 verify dated 2026-11-12 (CC021): the teaching states the checks' key "
                                          "sentence in place of its paraphrase. No new Russian or claim.")
            synthesis["objectiveOrClaimBinding"] += ":t14n2-cc021-v1"
            original = synthesis["bibliography"] + "\n" + new_text
            new = dict(r)
            new.update({"itemId": r["itemId"] + "-cc021-v1", "sourceRecordId": r["sourceRecordId"] + ":t14n2-cc021-v1",
                        "originalSourceText": original, "shippedText": new_text, "synthesis": synthesis,
                        "sourceHash": sha(original), "contentHash": sha(new_text)})
            added.append(new)
    if not added:
        raise SystemExit("No research-synthesis record covered the rewritten hosts")
    pack["provenanceRecords"].extend(added)
    if KEY not in rem["body"] or not all(items[i]["promptLetter"] in rem["body"] for i in ("u4-i-nom-role-train", "u4-i-nom-role-student")):
        raise SystemExit("Remediation does not show every cue and key")
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T14-n2 verify baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
