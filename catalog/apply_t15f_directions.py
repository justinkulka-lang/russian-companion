"""T15f, owner decision D8: companion and book directions in every unit (exact-baseline transformer).

Runs after apply_t15f_units_1_10.py. Shared templates, so the change is made in every unit that carries them.

Self-attestation notices. No validator rule requires direction wording (V2Validator only requires a BOOK step or an
instruction path for a SELF_ATTESTATION objective, and treats these step kinds as instructions). MA2 does require the
labels: MA2-076 (AnkiDroid completion is labelled user self-attestation), MA2-082 (self-attested external work is
distinguished from demonstrated performance) and A2-32 (binder results are SELF_CHECK, never demonstrated ability).
So the AnkiDroid, binder and book-record directions each keep one short neutral sentence; tutor (MA2-079: optional)
and cumulative directions keep only their instruction. MA2-044 keeps each task instruction. Pending-reviewer and
approval language, repeated "not demonstrated" commentary, "No audio is mapped here", what a unit does not teach,
and licensing narration leave the steps; the licensing sentence moves to the batch attribution notices.
"""
from __future__ import annotations

import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "6b801cda7d03f56953757b787f43e5b60281ae20ea96473e3faae82d260e6246"

REPORT_NOTICE = "Marking this done records your own report; it is not scored."
CHECK_NOTICE = "Marking this done records your own check; it is not scored."


def shared(n: int) -> str:
    return (f"Unit {n} AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from"
            " this unit that you missed and the correction you will use. Tutor conversation is optional and is not a"
            " demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not"
            " demonstrated ability. Open the offline grammar reference for this unit when one is linked.")


def bodies(n: int) -> dict[str, str]:
    return {
        "ANKI": f"Make Unit {n} AnkiDroid cards only from material you are allowed to use.",
        "BINDER": "Write one form from this unit that you missed and the correction you will use.",
        "TUTOR": "Tutor work for this unit is optional.",
        "CUMULATIVE": "Review completed objectives from earlier units. Open the offline grammar reference for this unit"
                      " when one is linked.",
    }


COMPANION = {
    "Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.": REPORT_NOTICE,
    "Self-attest AnkiDroid work. It remains user-reported.": REPORT_NOTICE,
    "A binder prompt is one specific note, not a score.": CHECK_NOTICE,
    "A binder prompt is a specific note, not 'take notes.'": CHECK_NOTICE,
    "Tutor work is optional and does not count as demonstrated ability.": None,
    "Afterward, mark book work complete as a self-attestation. That is not demonstrated in-app ability.":
        "Afterward, mark the book work done. " + REPORT_NOTICE,
}
# Sentences removed from book directions and book-work bodies wherever they occur.
REMOVED_SENTENCES = [
    "These locators are pending reviewer assignment and are not an approved mapping.",
    "Book activity is not a demonstrated app check.",
    "The app records do not replace that lesson.",
    "The proposed records in this unit are a separate licensed set and do not copy the book.",
    "No audio is mapped here.",
    "This unit does not teach a possession pattern that leaves out the verb shown in its examples.",
    "The short-form adjective section stays out of this unit.",
    "Verified handwriting images are not in this app.",
    "This app does not describe those forms and does not grade the page.",
    "No licensed pronunciation audio is mapped to Unit 2.",
    "This app does not state those sounds and does not score a recording.",
]
BOOK_RECORD = re.compile(r"^When you have done Lesson (\d+) in your copy, self-attest here\. Attestation records your report\."
                         r" It does not grade the lesson, approve a mapping, or approve the proposed records\.$")
NOTICE_REFS = ("ref-u35-attribution", "ref-u610-attribution", "ref-u1115-attribution", "ref-u1620-attribution",
               "ref-u2125-attribution", "ref-u2630-attribution")
NOTICE_SENTENCE = "The course records in these units are a separate licensed set and do not copy the book."
FORBIDDEN = re.compile(r"pending reviewer|approved mapping|approve a mapping|demonstrated (?:app |in-app )?(?:check|ability)|"
                       r"No audio is mapped|separate licensed set|does not teach|stays out of this unit", re.I)


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def drop_sentences(text: str) -> str:
    out = text
    for sentence in REMOVED_SENTENCES:
        out = out.replace(" " + sentence, "").replace(sentence + " ", "").replace(sentence, "")
    return out.strip()


def apply(pack: dict) -> dict:
    changes: list[list] = []

    def change(unit: int, owner: str, field: str, old, new) -> None:
        if old != new:
            changes.append([unit, owner, field, old, new])

    for unit in pack["units"]:
        n = unit["number"]
        for session in unit["sessions"]:
            for step in session["steps"]:
                kind = "CUMULATIVE" if step["id"].endswith("-st-cumulative-direction") else step["kind"]
                if kind not in ("ANKI", "BINDER", "TUTOR", "CUMULATIVE", "BOOK", "ORIENT"):
                    continue
                if kind in ("ANKI", "BINDER", "TUTOR", "CUMULATIVE") and step["body"] == shared(n):
                    new = bodies(n)[kind]
                    change(n, step["id"], "body", step["body"], new)
                    step["body"] = new
                companion = step.get("companionDirection")
                if companion is not None:
                    if kind == "CUMULATIVE" and companion == shared(n):
                        new = None
                    elif companion in COMPANION:
                        new = COMPANION[companion]
                    else:
                        new = companion
                    change(n, step["id"], "companionDirection", companion, new)
                    if new is None:
                        del step["companionDirection"]
                    else:
                        step["companionDirection"] = new
                if kind == "BOOK":
                    match = BOOK_RECORD.match(step["body"])
                    new = (f"When you have done Lesson {match.group(1)} in your copy, mark it done here. {REPORT_NOTICE}"
                           if match else drop_sentences(step["body"]))
                    change(n, step["id"], "body", step["body"], new)
                    step["body"] = new
                if step.get("bookDirection"):
                    new = drop_sentences(step["bookDirection"])
                    change(n, step["id"], "bookDirection", step["bookDirection"], new)
                    step["bookDirection"] = new
                for field in ("body", "bookDirection", "companionDirection"):
                    value = step.get(field)
                    if kind != "ORIENT" or step["id"].endswith("-st-cumulative-direction") or field != "body":
                        if value and FORBIDDEN.search(value):
                            raise SystemExit(f"{step['id']}.{field} keeps direction commentary: {value[:120]}")
    for ref in pack["references"]:
        if ref["id"] in NOTICE_REFS:
            if NOTICE_SENTENCE in ref["detail"]:
                raise SystemExit(f"{ref['id']} already has the notice sentence")
            new = ref["detail"].rstrip() + " " + NOTICE_SENTENCE
            change(ref.get("introducedInUnit"), ref["id"], "detail", ref["detail"], new)
            ref["detail"] = new
    return {"changes": changes}


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15f directions baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
