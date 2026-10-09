#!/usr/bin/env python3
"""Reuse admitted corrected explanations on the corresponding remediation routes."""

import hashlib
import json
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
PAIRS = [(3, "gender", 5), (6, "feminine", 2), (7, "spelling", 2),
         (8, "spelling", 2), (11, "past-read", 2), (11, "past-aspect", 2),
         (12, "future-imp", 2), (12, "aspect", 2), (13, "past-aspect", 2),
         (16, "inst-sg", 2), (16, "inst-adj", 2),
         (28, "imperfective-adverb", 2), (28, "perfective-adverb", 2),
         (24, "stand-ins", 3), (24, "word-order-contrast", 2),
         (26, "letter-writing", 2), (21, "purpose", 2), (16, "complements", 1)]


def main():
    original = PACK.read_bytes()
    pack = json.loads(original)
    steps = {step["id"]: step for unit in pack["units"] for session in unit["sessions"] for step in session["steps"]}
    changes = []
    for number, topic, suffix in PAIRS:
        teach_id = f"u{number}-st-teach-{topic}-{suffix}"
        rem_id = f"u{number}-st-rem-{topic}"
        teach, remediation = steps[teach_id], steps[rem_id]
        unit = next(unit for unit in pack["units"] if unit["number"] == number)
        objectives = [obj for obj in unit["objectives"] if teach_id in obj["instructionStepIds"] and rem_id in obj["remediationStepIds"]]
        if len(objectives) != 1 or teach["kind"] != "TEACH" or remediation["kind"] != "REMEDIATE":
            raise ValueError(f"Unverified teaching/remediation relationship: {teach_id}, {rem_id}")
        objective = objectives[0]
        coverage = [row["itemId"] for row in pack["provenanceRecords"]
                    if row["unitNumber"] == number and row["objectiveId"] == objective["id"]
                    and row["shippedText"] == teach["body"]]
        if not coverage:
            raise ValueError(f"Corrected body has no exact objective-bound provenance: {teach_id}")
        if remediation["body"] == teach["body"]:
            continue
        changes.append({"unitNumber": number, "objectiveId": objective["id"], "teachingStepId": teach_id,
                        "remediationStepId": rem_id, "before": remediation["body"], "after": teach["body"],
                        "existingProvenanceIds": coverage, "disposition": "IMPLEMENTED_PENDING_FINAL_AUDIT"})
        remediation["body"] = teach["body"]
    if not changes:
        print("All 18 remediation explanations already match; retained change evidence unchanged")
        return
    updated = (json.dumps(pack, ensure_ascii=False, indent=2) + "\n").encode("utf-8")
    PACK.write_bytes(updated)
    evidence = {"schemaVersion": 1, "scope": "Propagate existing corrections to remediation; no new Russian or approval",
                "beforeCurriculumFileSha256": hashlib.sha256(original).hexdigest(),
                "afterCurriculumFileSha256": hashlib.sha256(updated).hexdigest(), "changes": changes}
    (HERE / "REMEDIATION_EXPLANATION_CORRECTIONS.json").write_text(json.dumps(evidence, ensure_ascii=False, indent=2) + "\n")
    print(f"Updated {len(changes)} remediation bodies; original provenance and approval state preserved")


if __name__ == "__main__":
    main()
