#!/usr/bin/env python3
"""Refresh implementation evidence without granting semantic audit acceptance."""

import argparse
import csv
import hashlib
import json
import sys
from collections import Counter, defaultdict
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
sys.path.insert(0, str(ROOT / 'catalog'))
from curriculum_storage import loads

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
TARGETS = HERE / "ITEM_TARGET_RECONCILIATION_PENDING.csv"
OUTPUT = HERE / "ITEM_TARGET_CURRENT_EVIDENCE.json"
SUMMARY = HERE / "ITEM_TARGET_CURRENT_EVIDENCE.md"


def digest(data):
    return hashlib.sha256(data).hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="Fail if retained evidence differs from the current inputs")
    args = parser.parse_args()
    pack_bytes = PACK.read_bytes()
    targets_bytes = TARGETS.read_bytes()
    pack = loads(pack_bytes)
    index = defaultdict(list)
    units = {unit["number"]: unit for unit in pack["units"]}

    # Index learner content explicitly. Provenance IDs and relation IDs are not
    # content objects, even when they refer to the same target.
    for kind in ("items", "examples", "references"):
        for obj in pack[kind]:
            index[obj["id"]].append((kind, obj))
    for unit in pack["units"]:
        for obj in unit["objectives"]:
            index[obj["id"]].append(("objectives", obj))
        for session in unit["sessions"]:
            for obj in session["steps"]:
                index[obj["id"]].append(("steps", obj))

    rows = list(csv.DictReader(TARGETS.open(encoding="utf-8", newline="")))
    ids = [row["correction_id"] for row in rows]
    if len(ids) != 75 or len(set(ids)) != len(ids):
        raise ValueError("Expected the 75 unique retained correction targets")
    evidence = []
    for row in rows:
        target_id = row["content_id"]
        matches = index[target_id]
        if len(matches) != 1:
            raise ValueError(f"{row['correction_id']}: expected one content object for {target_id}, got {len(matches)}")
        kind, current = matches[0]
        unit = units[int(row["unit"])]
        previous = json.loads(row["current_object"])
        changed = None if previous is None else sorted(
            key for key in set(previous) | set(current)
            if previous.get(key) != current.get(key)
        )
        objective_ids = {current.get("objectiveId", "")}
        if kind == "objectives":
            objective_ids.add(target_id)
        for obj in unit["objectives"]:
            if any(target_id in obj.get(field, []) for field in (
                "instructionStepIds", "practiceStepIds", "remediationStepIds",
                "exampleIds", "checkItemIds",
            )):
                objective_ids.add(obj["id"])
        objectives = [obj for obj in unit["objectives"] if obj["id"] in objective_ids]
        related_ids = {target_id}
        for obj in objectives:
            for field in ("instructionStepIds", "practiceStepIds", "remediationStepIds", "exampleIds", "checkItemIds"):
                related_ids.update(obj.get(field, []))
        steps = []
        for session in unit["sessions"]:
            for step in session["steps"]:
                if step["id"] in related_ids or target_id in step.get("itemIds", []) or target_id in step.get("exampleIds", []):
                    steps.append({"sessionId": session["id"], "step": step})
        provenance = [record for record in pack["provenanceRecords"] if record.get("itemId") in related_ids]
        evidence.append({
            "correctionId": row["correction_id"], "findingId": row["finding_id"],
            "unitNumber": unit["number"], "contentId": target_id, "contentKind": kind,
            "reportedIssue": row["issue"], "proposedCorrection": row["proposed_correction"],
            "snapshotComparison": "SNAPSHOT_ABSENT" if previous is None else "CHANGED" if changed else "UNCHANGED",
            "changedFields": changed, "currentObject": current,
            "relatedObjectives": objectives, "relatedSteps": steps,
            "relatedProvenance": provenance, "unitAdvancement": unit["advancement"],
            "disposition": "IMPLEMENTATION_EVIDENCE_PENDING_SEMANTIC_REVIEW",
        })
    output = {
        "schemaVersion": 1, "purpose": "Current evidence for the retained preliminary audit targets; no semantic closure or approval",
        "inputs": {"curriculumPath": PACK.relative_to(ROOT).as_posix(), "curriculumFileSha256": digest(pack_bytes),
                   "targetSnapshotPath": TARGETS.relative_to(ROOT).as_posix(), "targetSnapshotFileSha256": digest(targets_bytes)},
        "targetCount": len(evidence), "semanticAcceptedCount": 0, "targets": evidence,
    }
    output_text = json.dumps(output, ensure_ascii=False, indent=2) + "\n"
    counts = Counter(row["snapshotComparison"] for row in evidence)
    lines = [
        "# Current evidence for 75 preliminary content-audit targets", "",
        "Generated by `python3 audit/content-corrections/reconcile_item_targets.py`.", "",
        "All 75 target IDs resolve uniquely in the current curriculum. This is implementation evidence, not a semantic audit pass. All 75 dispositions remain pending semantic review.", "",
        f"Snapshot comparison: {counts['UNCHANGED']} unchanged, {counts['CHANGED']} changed, {counts['SNAPSHOT_ABSENT']} absent in the retained snapshot.", "",
        "The four absent snapshots are objective entries that the previous snapshot did not capture. Their absence does not mean those objectives were removed. The current evidence includes their exact gating and check assignments, related steps and provenance, and unit advancement rules.", "",
        "Changed objects reflect ongoing correction work. Unchanged objects may already contain preliminary repairs; unchanged does not mean the original issue remains or that it is accepted.", "",
        f"Curriculum file SHA-256: `{digest(pack_bytes)}`. This is a raw file hash, distinct from the production canonical content-review digest.", "",
        f"Retained target snapshot SHA-256: `{digest(targets_bytes)}`.", "",
        "The JSON companion contains the issue, proposed correction, current object, related objectives and steps, provenance, and advancement context for every target. The retained CSV is unchanged.", "",
        "| Target | Finding | Unit | Content | Snapshot comparison |", "| --- | --- | --- | --- | --- |",
    ]
    lines.extend(f"| {row['correctionId']} | {row['findingId']} | {row['unitNumber']} | `{row['contentId']}` | {row['snapshotComparison']} |" for row in evidence)
    summary_text = "\n".join(lines) + "\n"
    for path, expected in ((OUTPUT, output_text), (SUMMARY, summary_text)):
        if args.check:
            if not path.is_file() or path.read_bytes() != expected.encode("utf-8"):
                raise SystemExit(f"Stale evidence: {path.relative_to(ROOT)}; rerun without --check")
        else:
            path.write_bytes(expected.encode("utf-8"))
    print(json.dumps({"targets": len(evidence), "comparisonCounts": counts, "semanticAccepted": 0}))


if __name__ == "__main__":
    main()
