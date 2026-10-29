#!/usr/bin/env python3
"""Reproduce pinned repository research; --check never writes generated evidence."""
import hashlib
import json
from pathlib import Path
import subprocess
import sys

BASE = "bfa0e9f834621e9a94e3031e226351f1946f2c68"
HERE = Path(__file__).resolve().parent
ROOT = Path(subprocess.check_output(["git", "rev-parse", "--show-toplevel"], cwd=HERE, text=True).strip())
RAW = "app/src/main/assets/content/v2-curriculum.json"
OBJECTIVES = ["obj-u1-decoding", "obj-u3-gender", "obj-u4-pronouns",
              "obj-u4-present", "obj-u4-stress", "obj-u4-nominative",
              "obj-u5-prepositional", "obj-u5-indeclinable"]


def blob(path):
    return subprocess.check_output(["git", "show", f"{BASE}:{path}"], cwd=ROOT)


def digest(data):
    return hashlib.sha256(data).hexdigest()


def encode(value):
    return (json.dumps(value, ensure_ascii=False, indent=2) + "\n").encode()


raw = blob(RAW)
pack = json.loads(raw)
items = {x["id"]: x for x in pack["items"]}
examples = {x["id"]: x for x in pack["examples"]}
references = {x["id"]: x for x in pack["references"]}
chains = []
for oid in OBJECTIVES:
    unit = next(u for u in pack["units"] if any(o["id"] == oid for o in u["objectives"]))
    objective = next(o for o in unit["objectives"] if o["id"] == oid)
    steps = {s["id"]: s for session in unit["sessions"] for s in session["steps"]}
    step_ids = list(dict.fromkeys(objective["instructionStepIds"] + objective["practiceStepIds"] + objective["remediationStepIds"]))
    chosen_steps = [steps[s] for s in step_ids]
    item_ids = list(dict.fromkeys(objective["checkItemIds"] + [i for s in chosen_steps for i in s.get("itemIds", [])]))
    example_ids = list(dict.fromkeys(objective["exampleIds"] + [i for s in chosen_steps for i in s.get("exampleIds", [])]))
    reference_ids = list(dict.fromkeys(i for s in chosen_steps for i in s.get("referenceIds", [])))
    provenance = [r for r in pack["provenanceRecords"] if r.get("objectiveId") == oid]
    source_ids = sorted(set([items[i]["sourceId"] for i in item_ids] + [r["sourceId"] for r in provenance]))
    # Record pins/hashes, not another copy of source passages or the private reference.
    pins = [{k: r[k] for k in ["itemId", "recordType", "sourceId", "sourceUrl", "sourceRevision", "sourceRecordId", "sourceHash", "contentHash", "reviewStatus"] if k in r}
            | {"completeRecordCanonicalSHA256": digest(encode(r))} for r in provenance]
    chains.append({"unit": unit["number"], "objective": objective,
                   "steps": chosen_steps, "items": [items[i] for i in item_ids],
                   "examples": [examples[i] for i in example_ids],
                   "references": [references[i] for i in reference_ids],
                   "sourceIds": source_ids, "provenancePins": pins,
                   "rightsRecords": [r for r in pack["rights"] if r.get("sourceId") in source_ids]})

paths = [RAW, "app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt",
         "app/src/main/java/dev/russiancompanion/v2/curriculum/V2OptionPresentation.kt",
         "app/src/main/java/dev/russiancompanion/data/local/LocalStore.kt",
         "docs/governance/MASTER_AUTHORITY_v2.0.md"]
outputs = {
    "CHAINS.json": encode({"sourceCommit": BASE, "rawCurriculumSHA256": digest(raw),
                           "scope": "Eight selected objective chains; repository evidence, not fresh upstream or qualified review", "chains": chains}),
    "INPUT_IDENTITY.json": encode({"sourceCommit": BASE, "files": {p: {"sha256": digest(blob(p)), "bytes": len(blob(p))} for p in paths},
                                  "commands": ["python3 audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit6-substantive-research-2026-10-23/extract_research.py", "python3 audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit6-substantive-research-2026-10-23/extract_research.py --check"],
                                  "runtimeTestsExecuted": 0, "lint": "NOT_RUN_RESEARCH_ONLY", "releaseGateAttachment": "NOT_RUN_RESEARCH_ONLY", "apk": None,
                                  "screenshotExporterExclusions": ["VisualReviewCaptureTest (2 methods)", "Units35ChunkCaptureTest (2 methods)", "Units610ChunkCaptureTest (10 methods)", "Units1115GapCaptureTest (2 methods)"]}),
}
outputs["GENERATED_SHA256.json"] = encode({name: digest(data) for name, data in outputs.items()})
check = sys.argv[1:] == ["--check"]
if sys.argv[1:] not in ([], ["--check"]):
    raise SystemExit("Usage: extract_research.py [--check]")
for name, data in outputs.items():
    if check:
        assert (HERE / name).read_bytes() == data, f"Pinned evidence differs: {name}"
    else:
        assert not (HERE / name).exists(), f"Refusing to overwrite {name}"
        (HERE / name).write_bytes(data)
print(json.dumps({"verified" if check else "generated": list(outputs), "objectiveChains": len(chains),
                  "scoredCandidates": sum(len(c["objective"]["checkItemIds"]) for c in chains), "runtimeTestsExecuted": 0}))
