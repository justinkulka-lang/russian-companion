"""Reassemble the three audit parts without overwriting any existing file."""
import argparse
import hashlib
import json
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument("manifest", type=Path, help="The delivered .zip.parts.json file")
parser.add_argument("--output", type=Path)
args = parser.parse_args()
record = json.loads(args.manifest.read_text())
parent = args.manifest.resolve().parent
output = args.output or parent / record["file"]
for item in record["parts"]:
    assert Path(item["name"]).name == item["name"], "Unsafe part name"
    part = parent / item["name"]
    assert part.stat().st_size == item["bytes"], part
    with part.open("rb") as stream:
        assert hashlib.file_digest(stream, "sha256").hexdigest() == item["sha256"], part
with output.open("xb") as combined:
    for item in record["parts"]:
        with (parent / item["name"]).open("rb") as stream:
            while data := stream.read(1024 * 1024):
                combined.write(data)
assert output.stat().st_size == record["bytes"]
with output.open("rb") as stream:
    assert hashlib.file_digest(stream, "sha256").hexdigest() == record["sha256"]
print(f"Verified complete audit ZIP: {output}; SHA256 {record['sha256']}")
print("Extract it and run VERIFY_PACKET.py. Integrity is not an audit pass.")
