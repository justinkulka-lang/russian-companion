#!/usr/bin/env python3
"""Read-only evidence integrity and finite-chain consistency checks; not runtime tests."""
import hashlib
import json
from pathlib import Path
import subprocess
import sys

here = Path(__file__).resolve().parent
subprocess.run([sys.executable, str(here / "extract_research.py"), "--check"], check=True)
for name, expected in json.loads((here / "SHA256.json").read_text()).items():
    assert hashlib.sha256((here / name).read_bytes()).hexdigest() == expected, name
research = json.loads((here / "CHAINS.json").read_text())
chains = {c["objective"]["id"]: c for c in research["chains"]}
assert len(chains) == 8
assert sum(len(c["objective"]["checkItemIds"]) for c in chains.values()) == 27
chain = chains["obj-u4-present"]
items = {i["id"]: i for i in chain["items"]}
steps = {s["id"]: s for s in chain["steps"]}
checks = chain["objective"]["checkItemIds"]
assert len(checks) == 4
for iid in checks:
    verb = iid.split("-")[3]
    item = items[iid]
    assert item["answer"] in steps[f"u4-st-teach-present-{verb}"]["body"]
    assert item["answer"] in steps["u4-st-rem-present"]["body"]
    guided = items[steps[f"u4-st-guided-present-{verb}"]["itemIds"][0]]
    assert guided["answer"] != item["answer"]
    assert guided["objectiveId"] == item["objectiveId"]
    assert len(item["options"]) == len(set(item["options"])) == 4
    assert item["containsGeneratedRussian"] is False
    assert any(p["sourceId"] == item["sourceId"] for p in chain["provenancePins"])
print("Research evidence and finite-chain checks verified. Runtime tests executed: 0.")
