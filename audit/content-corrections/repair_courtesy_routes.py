#!/usr/bin/env python3
"""Carry the existing register correction through Unit 25 learner routes."""
import hashlib
import json
from pathlib import Path

HERE = Path(__file__).resolve().parent
PACK = HERE.parents[1] / "app/src/main/assets/content/v2-curriculum.json"


def main():
    before = PACK.read_bytes()
    pack = json.loads(before)
    example = next(x for x in pack["examples"] if x["id"] == "ex-u25-courtesy-2")
    corrected = example["note"]
    assert corrected == "Извини́те is the polite or plural form. The familiar singular is Извини́."
    coverage = [r["itemId"] for r in pack["provenanceRecords"]
                if r["unitNumber"] == 25 and r["objectiveId"] == "obj-u25-courtesy"
                and r["shippedText"] == corrected]
    assert coverage, "No exact existing correction provenance"
    unit = next(u for u in pack["units"] if u["number"] == 25)
    session = next(s for s in unit["sessions"] if s["id"] == "u25-s-courtesy")
    steps = {s["id"]: s for s in session["steps"]}
    steps["u25-st-teach-courtesy-3"]["body"] = corrected
    steps["u25-st-rem-courtesy"]["body"] = corrected
    steps["u25-st-rem-courtesy"]["exampleIds"] = [f"ex-u25-courtesy-{i}" for i in range(3)]
    # Keep every step and the order within each phase. All worked teaching precedes practice.
    teaching = [s for s in session["steps"] if s["kind"] == "TEACH"]
    others = [s for s in session["steps"] if s["kind"] != "TEACH"]
    session["steps"] = teaching + others
    guided = next(i for i in pack["items"] if i["id"] == "u25-g-courtesy-0")
    assert guided["options"][2] == "Извини́те" and guided["answer"] == "Пожа́луйста"
    guided["distractorRationales"][2] = corrected
    guided["assessment"]["distractorDifference"][2] = corrected
    after = (json.dumps(pack, ensure_ascii=False, indent=2) + "\n").encode()
    if after == before:
        print("Courtesy correction already present; retained evidence unchanged")
        return
    PACK.write_bytes(after)
    evidence = {"schemaVersion": 1, "findingId": "CA016", "unitNumber": 25,
                "scope": "Reuse existing register correction in teaching, guided feedback and remediation; teaching precedes guided practice",
                "existingProvenanceIds": coverage,
                "beforeCurriculumFileSha256": hashlib.sha256(before).hexdigest(),
                "afterCurriculumFileSha256": hashlib.sha256(after).hexdigest(),
                "disposition": "IMPLEMENTED_PENDING_FINAL_AUDIT"}
    (HERE / "COURTESY_ROUTE_CORRECTION.json").write_text(json.dumps(evidence, ensure_ascii=False, indent=2) + "\n")
    print("Updated courtesy routes using existing objective-bound correction; keys and gates unchanged")


if __name__ == "__main__":
    main()
