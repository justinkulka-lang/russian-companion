#!/usr/bin/env python3
"""Static source/teaching inventory; does not run or certify app behavior."""
import csv
import hashlib
import io
import json
import subprocess
import sys
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
OUT = Path(__file__).with_name("INVENTORY.json")
BASELINE = "dea9b73b61f5cae6c7d0330324a67e32a0f926b4"
ASSET = "app/src/main/assets/content/v2-curriculum.json"
INPUTS = [ASSET,
    "app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt",
    "app/src/main/java/dev/russiancompanion/v2/curriculum/V2ProgressRules.kt",
    "app/src/main/java/dev/russiancompanion/v2/ui/V2Screens.kt",
    "catalog/sources/units-3-5/openrussian/nouns.csv",
    "catalog/sources/units-3-5/wikibooks/gender-4310736.txt",
    "catalog/sources/units-3-5/wikibooks/gender-4310736.revision.json",
    "catalog/sources/units-3-5/wikibooks/copyrights-4622060.txt"]

def normalized(text):
    return "".join(c for c in unicodedata.normalize("NFD", text.lower()) if c != "\u0301")

def build():
    inputs = {name: (ROOT / name).read_bytes() for name in INPUTS}
    # Bind the source inspected, even when the later receipt commit changes HEAD.
    for name, data in inputs.items():
        assert data == subprocess.check_output(["git", "show", BASELINE + ":" + name], cwd=ROOT), name
    pack = json.loads(inputs[ASSET])
    unit = next(u for u in pack["units"] if u["number"] == 3)
    objective = next(o for o in unit["objectives"] if o["id"] == "obj-u3-gender")
    steps = {s["id"]: s for session in unit["sessions"] for s in session["steps"]}
    items = {i["id"]: i for i in pack["items"]}
    examples = {e["id"]: e for e in pack["examples"]}
    teaching = [steps[i] for i in objective["instructionStepIds"] if steps[i]["kind"] == "TEACH"]
    repair = steps["u3-st-rem-gender"]
    rows = {r["bare"]: r for r in csv.DictReader(io.StringIO(inputs[INPUTS[4]].decode()), delimiter="\t")}
    candidates = []
    gender_names = {"m": "masculine", "f": "feminine", "n": "neuter"}
    for ident in objective["checkItemIds"]:
        item = items[ident]
        word = normalized(item["promptLetter"])
        matching_teaching = [s["id"] for s in teaching if any(
            normalized(examples[e]["display"]) == word for e in s.get("exampleIds", []))]
        source_row = rows.get(word)
        candidates.append({
            "item": item,
            "prescribedCheckSteps": [s["id"] for s in steps.values() if s["kind"] == "CHECK" and ident in s.get("itemIds", [])],
            "teachingStepsWithExactExample": matching_teaching,
            "exactWordInOrdinaryRepairBody": word in normalized(repair["body"]),
            "ordinaryRepairExampleIdsMatchingWord": [e for e in repair.get("exampleIds", []) if normalized(examples[e]["display"]) == word],
            "sourceRow": source_row,
            "keySourceRoute": "frozen Wikibooks masculine examples and explicit uncle exception" if item["sourceId"] == "src-wikibooks-russian-cc-by-sa" else "frozen OpenRussian gender field",
            "sourceGenderAgreesWithKey": gender_names.get(source_row.get("gender")) == item["answer"] if source_row else None,
            "existingProvenanceIds": [r["itemId"] for r in pack["provenanceRecords"] if r["unitNumber"] == 3 and word in normalized(r["itemId"])],
            "wideningDisposition": "BLOCKED_MISSING_EXPLICIT_TEACHING_AND_FULL_REPAIR" if not matching_teaching else "NEEDS_FULL_REPAIR_BEFORE_WIDER_POOL",
        })
    missing = [r["item"]["id"] for r in candidates if not r["teachingStepsWithExactExample"]]
    assert missing == ["u3-i-gender-друг", "u3-i-gender-дверь"]
    assert len(candidates) == 5 and len(teaching) == 5
    assert all(r["sourceGenderAgreesWithKey"] for r in candidates if r["item"]["sourceId"] == "src-openrussian-50e210c4")
    uncle = next(r for r in candidates if r["item"]["id"] == "u3-i-gender-дядя")
    gender_page = inputs[INPUTS[5]].decode()
    masculine_section = gender_page.split("=== Masculine ===", 1)[1].split("=== Feminine ===", 1)[0]
    assert "Дядя" in masculine_section and "inherently' masculine" in masculine_section
    assert uncle["item"]["answer"] == "masculine" and uncle["item"]["sourceId"] == "src-wikibooks-russian-cc-by-sa"
    assert [r["item"]["id"] for r in candidates if r["exactWordInOrdinaryRepairBody"]] == ["u3-i-gender-дядя"]
    return {
        "kind": "STATIC_ENGINEERING_INVENTORY_NOT_EXECUTION_OR_QUALIFIED_APPROVAL",
        "inspectedCommit": BASELINE,
        "command": "python3 audit/independent-gpt61-2026-10-12/ia003-unit3-gender-readiness/inventory.py --check",
        "inputSha256": {name: hashlib.sha256(data).hexdigest() for name, data in inputs.items()},
        "objective": objective,
        "teaching": teaching,
        "ordinaryRepair": repair,
        "guidedSteps": [steps[s] for s in objective["practiceStepIds"]],
        "independentCandidates": candidates,
        "missingExactTeachingExampleItems": missing,
        "currentDueReview": {"targetUnit": 4, "policyId": "unit4-prior-reading-gender-v1", "candidateItemIds": ["u3-i-gender-город"], "remediationStepId": "u3-st-teach-gender-2", "note": "Existing due city item returns to matching masculine teaching; do not conflate this with ordinary shared repair."},
        "limitations": ["Exact-example membership is a static diagnostic, not proof of pedagogical quality or runtime eligibility.", "Dictionary gender agreement checks a frozen field, not teaching, independent ability, or qualified rights clearance.", "No candidate pool, policy, schema, asset, score, source admission, or binding is changed.", "Five independent gender items are inspected; no app tests are executed by this script.", "All existing provenance, including historical source terminology, remains unchanged."]}

result = json.dumps(build(), ensure_ascii=False, indent=2) + "\n"
if "--check" in sys.argv:
    assert OUT.read_text() == result, "Inventory is not reproducible from the bound inputs"
    print("PASS: bound static inventory reproduced; five independent candidates; two missing exact teaching examples; zero app tests executed")
else:
    OUT.write_text(result)
    print("Wrote", OUT.relative_to(ROOT))
