"""Separate review administration that the offline app does not consume.

Source spans, identities, hashes, transformations, licenses, attribution,
share-alike treatment, synthesis evidence and review status stay in the pack:
the runtime validator or offline notices read them. This file does not grant
review approval or change the content-review digest.
"""
from __future__ import annotations

from copy import deepcopy
import json
from pathlib import Path
import sys

FIELDS = ("reviewerIdentity", "reviewerQualification", "reviewDate", "reviewedDigest")
METADATA_PATH = Path(__file__).with_name("provenance-review-metadata.json")


def separate(document: dict, metadata: dict | None = None) -> tuple[dict, dict]:
    pack = deepcopy(document)
    result = deepcopy(metadata) if metadata is not None else {
        "schemaVersion": 1, "packId": pack["packId"], "records": []
    }
    if result.get("schemaVersion") != 1 or result.get("packId") != pack["packId"]:
        raise ValueError("Review metadata does not match the pack")
    rows = {}
    for row in result["records"]:
        if set(row) - {"itemId", *FIELDS} or row["itemId"] in rows:
            raise ValueError("Invalid or duplicate review metadata")
        rows[row["itemId"]] = row
    seen = set()
    for record in pack["provenanceRecords"]:
        identity = record["itemId"]
        if identity in seen:
            raise ValueError("Duplicate provenance identity")
        seen.add(identity)
        fields = {key: record.pop(key) for key in FIELDS if key in record}
        if not fields:
            continue
        row = rows.setdefault(identity, {"itemId": identity})
        for key, value in fields.items():
            if key in row and row[key] != value:
                raise ValueError(f"Conflicting review metadata: {identity}/{key}")
            row[key] = value
    result["records"] = sorted(rows.values(), key=lambda row: row["itemId"])
    return pack, result


def read_metadata(path: Path = METADATA_PATH) -> dict | None:
    return json.loads(path.read_text(encoding="utf-8")) if path.exists() else None


def write_metadata(metadata: dict, path: Path = METADATA_PATH) -> None:
    path.write_text(json.dumps(metadata, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> None:
    source, metadata_path, destination = map(Path, sys.argv[1:])
    pack, metadata = separate(json.loads(source.read_text(encoding="utf-8")), read_metadata(metadata_path))
    write_metadata(metadata, metadata_path)
    destination.write_text(json.dumps(pack, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
