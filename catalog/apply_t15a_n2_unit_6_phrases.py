#!/usr/bin/env python3
"""T15a-n2 (Claude Code, owner direction 11/15/2026): Unit 6 shows no Russian phrase absent from the frozen files.

Exact-baseline transformer. Owner decision: every multi-word Russian phrase shown to the learner must appear verbatim
in a frozen file under catalog/sources/; individually attested forms do not attest a phrase built from them. The
worked "sister" phrases (на́ша сестра́, на́шу сестру́, на́шего бра́та) are attested by no frozen file, so the worked
explanation, its remediation and the check u6-i-possessive-2 now show single forms. Every stressed form shown is cut
from a frozen file: the наш table of catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt (lines
592-607: на́ша, на́шу, на́шего, на́ши), catalog/sources/units-16-20/wikibooks/lesson3-4460699.txt (lines 197-199:
на́ша), and catalog/sources/units-3-5/openrussian/nouns.csv (сестра́, сестру́, па́па). No source is added.
"""
from __future__ import annotations

import copy
import csv
import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "f8f9bdd8556e44d454edd7803d85cf85109f15b4a29a0c702a44e2c853266ee2"
GRAMMAR = ROOT / "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt"
LESSON3 = ROOT / "catalog/sources/units-16-20/wikibooks/lesson3-4460699.txt"
NOUNS = ROOT / "catalog/sources/units-3-5/openrussian/nouns.csv"
TASK_NOTE = "T15a-n2, Claude Code, 2026-11-15"
OBJ = "obj-u6-possessive"


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def lines(path: Path, first: int, last: int) -> str:
    """Lines first..last (1-based, inclusive) of a frozen file, joined verbatim."""
    return "\n".join(path.read_text(encoding="utf-8").split("\n")[first - 1:last])


def frozen_cell(bare: str, field: str) -> str:
    with NOUNS.open(encoding="utf-8") as handle:
        for row in csv.DictReader(handle, delimiter="\t", quoting=csv.QUOTE_NONE):
            if row["bare"] == bare:
                return row[field].strip()
    raise SystemExit(f"No frozen row {bare}")


NASH_TABLE = lines(GRAMMAR, 592, 607)
LESSON3_NASHA = lines(LESSON3, 197, 199)
assert NASH_TABLE.startswith("|+наш (our, ours)") and "на́шу" in NASH_TABLE and "на́шего" in NASH_TABLE, NASH_TABLE
assert LESSON3_NASHA == "|на́ша (feminine)\n|на́шей\n|our", LESSON3_NASHA

OLD_WORKED = ("Наш means our; Ваш means your (formal or plural). In the worked phrase на́ша сестра́, сестра́ is a feminine"
              " singular noun. To put this phrase in the direct-object accusative, change both words: на́ша сестра́ becomes"
              " на́шу сестру́. Keep the same noun and feminine singular agreement. на́шего бра́та changes the referent, and"
              " на́ши is plural.")
F_OUR = "Наш means our; Ваш means your (formal or plural)."
F_NOM = "на́ша is the feminine singular nominative form of наш, and сестра́ is a feminine singular noun."
F_ACC = "In the direct-object accusative, change both words: на́ша becomes на́шу, and сестра́ becomes сестру́."
F_KEEP = "Keep the same noun and feminine singular agreement."
F_OTHER = "на́шего is the masculine and neuter genitive form, and на́ши is plural."
NEW_WORKED = " ".join([F_OUR, F_NOM, F_ACC, F_KEEP, F_OTHER])
OLD_FACTS = ["In the worked phrase на́ша сестра́, сестра́ is a feminine singular noun.",
             "To put this phrase in the direct-object accusative, change both words: на́ша сестра́ becomes на́шу сестру́.",
             "на́шего бра́та changes the referent, and на́ши is plural."]

ITEM_ID = "u6-i-possessive-2"
I_PROMPT = "Which form is the direct-object (accusative) form of the shown feminine form of our?"
I_CUE = "на́ша"
I_OPTIONS = ["на́шу", "на́шего", "на́ши"]
I_FEEDBACK = "на́ша is the feminine singular nominative form of наш; its accusative form is на́шу."
I_RATIONALES = ["", "на́шего is the masculine and neuter genitive form, not the feminine accusative form.",
                "на́ши is plural and does not mark the feminine singular object row."]
I_WHY = "The shown feminine nominative на́ша corresponds to the accusative form на́шу."
# Contrast records of the check: itemId -> new shipped text.
CONTRAST = {"prov-ca-6-190": I_CUE, "prov-ca-6-191": "на́шу", "prov-ca-6-192": I_PROMPT, "prov-ca-6-193": I_FEEDBACK,
            "prov-ca-6-194": I_WHY, "prov-ca-6-195": "на́шего", "prov-ca-6-196": "на́ши", "prov-ca-6-197": I_RATIONALES[1],
            "prov-ca-6-198": I_RATIONALES[2], "prov-ca-6-199": "на́шу."}
APPLICATION = {"cue": I_CUE, "answer": "на́шу", "distractors": ["на́шего", "на́ши"]}


def apply(pack: dict) -> dict:
    unit = next(u for u in pack["units"] if u["number"] == 6)
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    items = {i["id"]: i for i in pack["items"]}
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}

    # Frozen records for every stressed form shown.
    new = {}
    grammar_template = next(r for r in pack["provenanceRecords"] if r["sourceId"] == "src-wikipedia-russian-grammar-1369617627"
                            and "originalSourceText" in r)
    for key, shipped in (("nasha", "на́ша"), ("nashu", "на́шу"), ("nashego", "на́шего"), ("nashi", "на́ши"), ("nash", "наш")):
        record = copy.deepcopy(grammar_template)
        record.pop("originalSourceTextRef", None)
        record.update({"itemId": f"prov-t15an2-u6-grammar-{key}", "unitNumber": 6, "objectiveId": OBJ, "recordType": "lexical record",
                       "sourceRecordId": "grammar:" + NASH_TABLE.split("\n")[0], "originalSourceText": NASH_TABLE,
                       "shippedText": shipped, "transformation": "selection", "sourceHash": sha(NASH_TABLE), "contentHash": sha(shipped)})
        new[record["itemId"]] = record
    lesson_template = next(r for r in pack["provenanceRecords"] if r["sourceRevision"] == "4460699")
    record = copy.deepcopy(lesson_template)
    record.pop("originalSourceTextRef", None)
    record.update({"itemId": "prov-t15an2-u6-lesson3-nasha", "unitNumber": 6, "objectiveId": OBJ, "recordType": "lexical record",
                   "sourceRecordId": "lesson3:" + LESSON3_NASHA.split("\n")[0], "originalSourceText": LESSON3_NASHA,
                   "shippedText": "на́ша", "transformation": "selection", "sourceHash": sha(LESSON3_NASHA), "contentHash": sha("на́ша")})
    new[record["itemId"]] = record
    cell_template = records["prov-ex-or-u6-animacy-0"]
    original = frozen_cell("папа", "sg_nom")
    record = copy.deepcopy(cell_template)
    record.update({"itemId": "prov-t15an2-u6-papa-sg-nom", "objectiveId": "obj-u6-feminine", "sourceRecordId": "nouns.csv:папа#sg_nom",
                   "recordType": "lexical record", "originalSourceText": original, "shippedText": original.replace("'", "́"),
                   "sourceHash": sha(original), "contentHash": sha(original.replace("'", "́"))})
    new[record["itemId"]] = record
    pack["provenanceRecords"].extend(new.values())

    # Worked explanation and its remediation: single attested forms only.
    for owner in ("u6-st-teach-possessive-3", "u6-st-rem-possessive"):
        host = steps[owner]
        if host["body"].count(OLD_WORKED) != 1:
            raise SystemExit(f"{owner}: worked baseline changed")
        host["body"] = host["body"].replace(OLD_WORKED, NEW_WORKED)
    rem = steps["u6-st-rem-possessive"]
    facts = rem["russianFacts"]
    i = facts.index(OLD_FACTS[0])
    if facts[i:i + 4] != [OLD_FACTS[0], OLD_FACTS[1], F_KEEP, OLD_FACTS[2]]:
        raise SystemExit("rem-possessive marker baseline changed")
    facts[i:i + 4] = [F_NOM, F_ACC, F_KEEP, F_OTHER]
    sources = rem["russianFactSources"]
    for old in OLD_FACTS:
        del sources[old]
    sources[F_NOM] = ["prov-t15an2-u6-grammar-nasha", "prov-t15an2-u6-lesson3-nasha", "prov-t15an2-u6-grammar-nash",
                      "prov-t15an1-u6-sestra-gender", "prov-t15an1-u6-sestra-sg-nom"]
    sources[F_ACC] = ["prov-t15an2-u6-grammar-nasha", "prov-t15an2-u6-grammar-nashu", "prov-t15an1-u6-sestra-sg-nom",
                      "prov-t15an1-u6-sestra-sg-acc", "prov-t15an1-u6-w111"]
    sources[F_OTHER] = ["prov-t15an2-u6-grammar-nashego", "prov-t15an2-u6-grammar-nashi"]
    rem["russianFactSources"] = {f: sources[f] for f in rem["russianFacts"]}

    # The check shows single forms; its contrast records follow.
    item = items[ITEM_ID]
    item.update({"prompt": I_PROMPT, "promptLetter": I_CUE, "options": list(I_OPTIONS), "answer": I_OPTIONS[0],
                 "feedback": I_FEEDBACK, "distractorRationales": list(I_RATIONALES),
                 "russianFacts": [I_FEEDBACK, I_RATIONALES[1], I_RATIONALES[2]]})
    item["russianFactSources"] = {
        I_FEEDBACK: ["prov-t15an2-u6-grammar-nasha", "prov-t15an2-u6-lesson3-nasha", "prov-t15an2-u6-grammar-nash",
                     "prov-t15an2-u6-grammar-nashu"],
        I_RATIONALES[1]: ["prov-t15an2-u6-grammar-nashego", "prov-t15an2-u6-grammar-nashu"],
        I_RATIONALES[2]: ["prov-t15an2-u6-grammar-nashi", "prov-u6-possessive-x-3"],
    }
    assessment = item["assessment"]
    assessment.update({"distractorDifference": list(I_RATIONALES), "whyAnswerDemonstratesObjective": I_WHY,
                       "contrastMembers": list(I_OPTIONS), "responseKind": "RUSSIAN_TOKEN"})
    # The ten contrast records share one research note; it now holds the new wording, which is new on 2026-11-15.
    note = (f"Content-audit contrast for {ITEM_ID}, revised on 2026-11-15 ({TASK_NOTE}): the check shows single forms"
            " instead of an unattested phrase. The English wording below is new on 2026-11-15 and did not exist before that"
            " date; the Russian forms are cited from frozen source records (the наш table of the frozen Wikipedia Russian"
            " grammar page, oldid 1369617627, and the frozen Wikibooks Lesson 3 page, oldid 4460699). 2026-10-08 is the date"
            " of the reused research context only. No owned-book prose, exercise, or answer key is copied.")
    contrast_original = note + "\n" + "\n".join(CONTRAST.values())
    for record_id, text in CONTRAST.items():
        record = records[record_id]
        record["shippedText"] = text
        record["contentHash"] = sha(text)
        record["originalSourceText"] = contrast_original
        record["sourceHash"] = sha(contrast_original)
        record.pop("originalSourceTextRef", None)
        synthesis = record["synthesis"]
        synthesis["applications"] = [dict(APPLICATION)]
        synthesis["bibliography"] = note
        synthesis["authorshipDeclaration"] = ("Independently authored for Russian Companion; the contrast wording is new on"
                                              f" 2026-11-15 ({TASK_NOTE}).")

    # Coverage of the new English wording (wording only; the facts cite the frozen records above).
    coverage_template = records["prov-t15an1-u6-possessive-our-your"]

    def coverage(key: str, text: str) -> None:
        record = copy.deepcopy(coverage_template)
        note = (f"New English wording authored on 2026-11-15 ({TASK_NOTE}); it did not exist before that date. It covers the"
                " wording only; the Russian facts it states are cited from frozen source records in the marker's"
                " russianFactSources. 2026-10-08 is the date of the reused research context only.")
        synthesis = record["synthesis"]
        synthesis.update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                          "objectiveOrClaimBinding": f"{OBJ}:{key}"})
        original = note + "\n" + text
        record.update({"itemId": f"prov-t15an2-{key}", "sourceRecordId": f"ca-coverage:{OBJ}:{key}", "shippedText": text,
                       "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08; this record's English text is new, authored 2026-11-15 for T15a-n2."})
        pack["provenanceRecords"].append(record)

    for key, text in (("u6-possessive-nasha-nominative", F_NOM), ("u6-possessive-nasha-accusative", F_ACC),
                      ("u6-possessive-nashego-nashi", F_OTHER), ("u6-st-teach-possessive-3-body", steps["u6-st-teach-possessive-3"]["body"]),
                      ("u6-possessive-remediation-body", rem["body"]), ("u6-i-possessive-2-prompt", I_PROMPT),
                      ("u6-i-possessive-2-feedback", I_FEEDBACK), ("u6-i-possessive-2-rationale-2", I_RATIONALES[1])):
        coverage(key, text)
    # The superseded coverage record of the removed worked-phrase sentence leaves the pack.
    pack["provenanceRecords"] = [r for r in pack["provenanceRecords"] if r["itemId"] != "prov-t15an1-u6-possessive-sestra-feminine"]

    # B3: markers whose cited records lacked a form they show now also cite the frozen cell of that form.
    extra = {("u6-g-possessive-0", "собака is feminine singular, so the nominative form is моя́."): ["prov-t15an1-u6-sobaka-sg-nom"],
             ("u6-i-possessive-1", "стол is masculine singular, so the nominative form is мо́й."): ["prov-ex-or-u6-animacy-0"],
             ("u6-st-rem-possessive", "For стол choose мо́й; for собака choose моя́."): ["prov-ex-or-u6-animacy-0", "prov-t15an1-u6-sobaka-sg-nom"],
             ("u6-st-rem-feminine", "Nouns in the а or я declension, including the masculine noun папа, have their own accusative singular ending."): ["prov-t15an2-u6-papa-sg-nom"]}
    for (owner, fact), ids in extra.items():
        host = steps.get(owner) or items[owner]
        cited = host["russianFactSources"][fact]
        cited.extend(i for i in ids if i not in cited)
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15a-n2 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
