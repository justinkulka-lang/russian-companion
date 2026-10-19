#!/usr/bin/env python3
"""Build Claude instructions/templates over a complete, verified pinned snapshot."""
import argparse
import csv
import hashlib
import io
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import zipfile

sys.dont_write_bytecode = True
ROOT = Path(__file__).resolve().parents[2]
DIRECTORY = "audit/claude-code-comprehensive-2026-10-19"

def digest(data):
    return hashlib.sha256(data).hexdigest()

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--target", required=True)
    parser.add_argument("--output", required=True, type=Path)
    args = parser.parse_args()
    target = subprocess.check_output(["git", "-C", str(ROOT), "rev-parse", args.target + "^{commit}"], text=True).strip()
    args.output.parent.mkdir(parents=True, exist_ok=True)
    base_builder = ROOT / "audit/independent-gpt61-2026-10-12/current-comprehensive-audit/build_packet.py"
    # Preserve the old packet and use the existing private-safe full snapshot checks.
    with tempfile.TemporaryDirectory(prefix="claude-audit-base-", dir=args.output.parent) as temporary:
        base = Path(temporary) / "base.zip"
        subprocess.run([sys.executable, str(base_builder), "--target", target, "--output", str(base)],
                       cwd=ROOT, env=dict(os.environ, PYTHONDONTWRITEBYTECODE="1"), check=True,
                       stdout=subprocess.DEVNULL)
        with zipfile.ZipFile(base) as archive:
            entries = {n: archive.read(n) for n in archive.namelist()
                       if n != "MANIFEST.json" and not n.startswith("output_templates/")}
    prefix = "repo/" + DIRECTORY + "/"
    for name in ["AUDIT_PROMPT.md", "DELIVERABLES.md", "EXECUTION_GUIDE.md"]:
        entries[name] = entries[prefix + name]
    entries["START_HERE.md"] = entries[prefix + "START_CLAUDE_CODE.md"]
    for path, data in list(entries.items()):
        if path.startswith(prefix + "templates/"):
            name = path.removeprefix(prefix + "templates/")
            if name.endswith(".json"):
                record = json.loads(data)
                record["targetCommit"] = target
                data = (json.dumps(record, indent=2) + "\n").encode()
            entries["output_templates/" + name] = data
    templates = [n for n in entries if n.startswith("output_templates/")]
    assert len(templates) == 12
    inventory = json.loads(entries["TRACKED_REPOSITORY_INVENTORY.json"])["files"]
    coverage = list(csv.DictReader(io.StringIO(entries["output_templates/REPOSITORY_FILE_COVERAGE.csv"].decode())))
    assert len(coverage) == len(inventory)
    assert {r["path"] for r in coverage} == {r["path"] for r in inventory}
    entities = list(csv.DictReader(io.StringIO(entries["output_templates/CURRICULUM_ENTITY_COVERAGE.csv"].decode())))
    assert len(entities) == 12003
    assert len({r["json_pointer"] for r in entities}) == len(entities)
    assert sum(r["entity_type"] == "examples" for r in entities) == 817
    assert sum(r["entity_type"] == "items" for r in entities) == 622
    assert sum(r["entity_type"] == "provenanceRecords" for r in entities) == 6943
    identity = json.loads(entries["PACKET_IDENTITY.json"])
    identity.update(purpose="Claude Code comprehensive independent app and curriculum audit",
                    requestedClient="Claude Code", requestedModel="Opus 5.5", requestedEffort="Extra",
                    actualAuditorModel="NOT_VERIFIED_BY_PACKET_BUILDER",
                    actualAuditorEffort="NOT_VERIFIED_BY_PACKET_BUILDER",
                    modelAvailability="Auditor must confirm exact requested configuration; no silent substitution",
                    requiredReturnDeliverables=12, auditPerformed=False,
                    repositoryCoverageRows=len(coverage), curriculumEntityCoverageRows=len(entities))
    entries["PACKET_IDENTITY.json"] = (json.dumps(identity, indent=2) + "\n").encode()
    manifest = dict(schemaVersion=1, auditTarget=target, manifestSelfExcluded=True,
                    files=[dict(path=n, bytes=len(b), sha256=digest(b)) for n, b in sorted(entries.items())])
    entries["MANIFEST.json"] = (json.dumps(manifest, indent=2) + "\n").encode()
    with zipfile.ZipFile(args.output, "x", zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
        for name, data in sorted(entries.items()):
            info = zipfile.ZipInfo(name, date_time=(2026, 10, 19, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o100644 << 16
            archive.writestr(info, data, compresslevel=9)
    with zipfile.ZipFile(args.output) as archive:
        assert archive.testzip() is None
        for row in manifest["files"]:
            data = archive.read(row["path"])
            assert len(data) == row["bytes"] and digest(data) == row["sha256"]
    packet_sha = digest(args.output.read_bytes())
    args.output.with_suffix(args.output.suffix + ".sha256").write_text(packet_sha + "  " + args.output.name + "\n")
    print(json.dumps(dict(path=str(args.output), bytes=args.output.stat().st_size, sha256=packet_sha,
                         entries=len(entries), trackedFiles=len(inventory), auditTarget=target,
                         requiredDeliverables=12, entityRows=len(entities), auditPerformed=False)))

if __name__ == "__main__":
    main()
