"""T15f Parts B and C for Units 1-10 (exact-baseline transformer; runs after apply_t15f_unit_20.py).

Part B (owner decisions D6c and D8): the widened A2-26 narration lint is blocking for Units 1-10. Its hits are fixed
here. Short operating instructions ("A miss returns to the explanation", retry wording) are kept (D8).
- Unit 10 source framing and exercise commentary named by the Codex T15e check are rewritten as direct teaching;
  every marker keeps its cited records, and no answer, option or pinned tuple changes;
- Unit 8's availability teaching keeps each line with its translation without calling it a source line;
- Unit 6's rule card and remediation showed a raw table row ("| I/me || ..."); they now show the unit's existing
  marked fact sentence, which cites the same frozen accusative row through the example records;
- Unit 7's invariance teaching drops two sentences about what the choices ask and do not demonstrate;
- Unit 7's spelling sentence "These choices test the requested form of the adjective; all the options are already
  correctly spelled." keeps only its fact: "All the options are already correctly spelled." (same cited records).
Self-attestation notices (the AnkiDroid direction) are exempt and unchanged.

Part C: Unit 7's complete case-phrase examples, replaced by single forms in T15b, are restored from phrases attested
verbatim in the frozen Wikibooks pages (the two-part phrase rule):
- accusative-4330404.txt:79  Анна встретила новую учительницу. - '''Anna met the new teacher.'''
- adjectives-4292379.txt:62  I arrived from my old house - '''Я пришёл из моего старого дома'''
- adjectives-4292379.txt:82  It's under the good books - '''Оно́ под хоро́шими кни́гами'''
No frozen file attests a neuter adjective-noun case phrase (the T15b window pair), so none is shown.
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
BASELINE_SHA256 = "0bced05e6cf480b51b34b220a421ef35818efbfb76d9fc7b3874a40a7f916c0e"
SRC = ROOT / "catalog/sources/units-6-10/wikibooks"

# (owner, field, old, new); a body edit applies to the step, an item edit to feedback (and the assessment why).
D8_STEPS = [
    ("u10-st-teach-pronoun-2", "title", "Source example", "Example"),
    ("u10-st-teach-preposition-2", "title", "Source example", "Example"),
    ("u10-st-teach-adjective-2", "title", "Source example", "Example"),
    ("u10-st-teach-animate-2", "title", "Source example", "Example"),
    ("u10-st-teach-animate-2", "body", "The nominative phrase names the subject in the source example.",
     "The nominative phrase names the subject in the example."),
    ("u10-st-teach-animate-2", "body", " These choices practise recognition of the supplied pair; they do not supply unseen"
     " sentence production.", ""),
    ("u10-st-rem-animate", "body", "The nominative phrase names the subject in the source example.",
     "The nominative phrase names the subject in the example."),
    ("u10-st-rem-animate", "body", " These choices practise recognition of the supplied pair; they do not supply unseen"
     " sentence production.", ""),
    ("u10-st-teach-have-4", "body", "Quantity patterns are separate from the simple nominative possessed thing in the son"
     " checks.", "Quantity patterns are separate from the simple nominative possessed thing."),
    ("u10-st-rem-have", "body", "Quantity patterns are separate from the simple nominative possessed thing in the son"
     " checks.", "Quantity patterns are separate from the simple nominative possessed thing."),
    ("u8-st-teach-availability-2", "body", "Keep each whole source line with its own supplied translation.",
     "Keep each whole line with its own translation."),
    ("u8-st-rem-availability", "body", "Keep each whole source line with its own supplied translation.",
     "Keep each whole line with its own translation."),
]
# Source framing the widened lint finds in Units 6, 7 and 9 (same rule as D8).
for _step in ("u6-st-teach-animacy-1", "u6-st-rem-animacy"):
    D8_STEPS.append((_step, "body", "сто́л is the supplied masculine inanimate table form.",
                     "сто́л is the supplied masculine inanimate form."))
for _step in ("u7-st-teach-objectadj-3", "u7-st-rem-objectadj"):
    D8_STEPS.append((_step, "body", "Its plural forms can be accusative or genitive; the source illustrates an animate"
                     " accusative object.", "Its plural forms can be accusative or genitive; the example illustrates an"
                     " animate accusative object."))
for _step in ("u9-st-teach-odindva-6", "u9-st-rem-odindva"):
    D8_STEPS.append((_step, "body", "The additional source example один час uses", "The additional example один час uses"))
for _step in ("u9-st-teach-quantity-9", "u9-st-rem-quantity"):
    D8_STEPS.append((_step, "body", "The isolated table forms below illustrate genitive singular and genitive plural noun"
                     " forms.", "The isolated forms below illustrate genitive singular and genitive plural noun forms."))
D8_ITEMS = [
    ("u10-g-pronoun-0", "Keep the same first-person singular referent when moving to the genitive row.",
     "Keep the same first-person singular referent in the genitive."),
    ("u10-i-pronoun-4", "Их is the bare genitive table form of Они, meaning they.",
     "Их is the genitive form of Они, meaning they."),
    ("u10-g-animate-0", "этих женщин is the animate plural accusative object phrase in the source example meaning I like"
     " these women.", "этих женщин is the animate plural accusative object phrase in the example meaning I like these"
     " women."),
    ("u10-i-animate-1", "Эти женщины is the nominative plural subject phrase in the source example meaning These women"
     " like me.", "Эти женщины is the nominative plural subject phrase in the example meaning These women like me."),
]

U6_FACT = "Меня is the accusative object form for Я, I/me: first person singular."
U6_SOURCES = ["prov-ex-u6-pronoun-0-ru", "prov-ex-u6-pronoun-0-en"]
U6_ROW = "| I/me || Я || Меня"

U7_INVARIANCE_OLD = (" The choices below ask which Russian expression names the kind of words adverbs are."
                     " That wording task does not demonstrate applying invariance in a sentence.")
U7_SPELLING_OLD = "These choices test the requested form of the adjective; all the options are already correctly spelled."
U7_SPELLING_NEW = "All the options are already correctly spelled."

WIKIBOOKS = {
    "accusative": ("https://en.wikibooks.org/wiki/Russian/Grammar/Accusative_case?oldid=4330404", "4330404",
                   SRC / "accusative-4330404.txt"),
    "adjectives": ("https://en.wikibooks.org/wiki/Russian/Grammar/Adjectives?oldid=4292379", "4292379",
                   SRC / "adjectives-4292379.txt"),
}
# (example id, objective, page, line, frozen segment, Russian display, English note, teaching step, sentence)
RESTORED = [
    ("ex-u7-endings-11", "obj-u7-endings", "accusative", 79,
     "Анна встретила новую учительницу. - '''Anna met the new teacher.'''",
     "Анна встретила новую учительницу", "Anna met the new teacher", "u7-st-teach-endings-1",
     "In Анна встретила новую учительницу (Anna met the new teacher), the adjective новую and the noun учительницу"
     " are both accusative."),
    ("ex-u7-endings-12", "obj-u7-endings", "adjectives", 62,
     "I arrived from my old house - '''Я пришёл из моего старого дома'''",
     "Я пришёл из моего старого дома", "I arrived from my old house", "u7-st-teach-endings-2",
     "In Я пришёл из моего старого дома (I arrived from my old house), the words моего старого дома are all"
     " genitive."),
    ("ex-u7-spelling-5", "obj-u7-spelling", "adjectives", 82,
     "It's under the good books - '''Оно́ под хоро́шими кни́гами'''",
     "Оно́ под хоро́шими кни́гами", "It's under the good books", "u7-st-teach-spelling-3",
     "In Оно́ под хоро́шими кни́гами (It's under the good books), the adjective хоро́шими and the noun кни́гами are"
     " both instrumental plural."),
]


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def edit(text: str, old: str, new: str, where: str) -> str:
    if text.count(old) != 1:
        raise SystemExit(f"{where}: expected one {old!r}")
    return text.replace(old, new)


def record(template: dict, item_id: str, record_type: str, page: str, original: str, shipped: str, objective: str) -> dict:
    url, revision, path = WIKIBOOKS[page]
    if original not in path.read_text(encoding="utf-8") or shipped not in original:
        raise SystemExit(f"{item_id}: not a frozen selection")
    out = dict(template)
    out.update({"itemId": item_id, "unitNumber": 7, "objectiveId": objective, "recordType": record_type,
                "sourceUrl": url, "sourceRevision": revision, "sourceRecordId": f"{page}:{original}",
                "originalSourceText": original, "shippedText": shipped, "transformation": "selection",
                "sourceHash": sha(original), "contentHash": sha(shipped)})
    return out


def rename_marker(host: dict, old: str, new: str) -> None:
    """A reworded marked sentence keeps its marker and exactly the same cited records."""
    if any(old in f for f in host.get("russianFacts", [])):
        host["russianFacts"] = [f.replace(old, new) for f in host["russianFacts"]]
        host["russianFactSources"] = {k.replace(old, new): v for k, v in host["russianFactSources"].items()}


def apply(pack: dict) -> dict:
    report: dict = {"changes": [], "unit6": [], "unit7": [], "examples": [], "records": []}
    steps = {s["id"]: (u, session, s) for u in pack["units"] for session in u["sessions"] for s in session["steps"]}
    items = {i["id"]: i for i in pack["items"]}

    # Part B (D8): Unit 10 and Unit 8 source framing and exercise commentary.
    for step_id, field, old, new in D8_STEPS:
        step = steps[step_id][2]
        before = step[field]
        step[field] = before if field == "title" and before != old else edit(before, old, new, f"{step_id}.{field}")
        if field == "title" and before != old:
            raise SystemExit(f"{step_id}: unexpected title {before!r}")
        if new:
            rename_marker(step, old, new)
        elif old.strip() in step.get("russianFacts", []):
            raise SystemExit(f"{step_id}: removed sentence is a marker")
        report["changes"].append([step_id, field, old.strip(), new])
    for item_id, old, new in D8_ITEMS:
        item = items[item_id]
        item["feedback"] = edit(item["feedback"], old, new, f"{item_id}.feedback")
        why = (item.get("assessment") or {}).get("whyAnswerDemonstratesObjective")
        if why and old in why:
            item["assessment"]["whyAnswerDemonstratesObjective"] = why.replace(old, new)
        rename_marker(item, old, new)
        report["changes"].append([item_id, "feedback", old, new])

    # Part B: Unit 6 raw table row.
    for step_id in ("u6-st-teach-pronoun-2", "u6-st-rem-pronoun"):
        step = steps[step_id][2]
        if step["body"] != U6_ROW:
            raise SystemExit(f"{step_id}: unexpected body")
        step["body"] = U6_FACT
        report["unit6"].append([step_id, U6_ROW, U6_FACT])
    rem6 = steps["u6-st-rem-pronoun"][2]
    rem6["russianFacts"] = [U6_FACT]
    rem6["russianFactSources"] = {U6_FACT: U6_SOURCES}
    item = next(i for i in pack["items"] if i["id"] == "u6-g-pronoun-0")
    if item["russianFactSources"][U6_FACT] != U6_SOURCES:
        raise SystemExit("Unit 6 fact sources differ from the item's marker")

    # Part B: Unit 7 invariance and spelling sentences.
    for step_id in ("u7-st-teach-adverb-invariance-english", "u7-st-rem-adverb-invariance"):
        step = steps[step_id][2]
        step["body"] = edit(step["body"], U7_INVARIANCE_OLD, "", step_id)
        report["unit7"].append([step_id, U7_INVARIANCE_OLD.strip(), ""])
    for step_id in ("u7-st-teach-spelling-4", "u7-st-rem-spelling"):
        step = steps[step_id][2]
        step["body"] = edit(step["body"], U7_SPELLING_OLD, U7_SPELLING_NEW, step_id)
        report["unit7"].append([step_id, U7_SPELLING_OLD, U7_SPELLING_NEW])
    rem_spelling = steps["u7-st-rem-spelling"][2]
    rem_spelling["russianFacts"] = [U7_SPELLING_NEW if f == U7_SPELLING_OLD else f for f in rem_spelling["russianFacts"]]
    rem_spelling["russianFactSources"] = {(U7_SPELLING_NEW if k == U7_SPELLING_OLD else k): v
                                          for k, v in rem_spelling["russianFactSources"].items()}

    # Part C: restored complete case phrases.
    template = next(r for r in pack["provenanceRecords"] if r["itemId"] == "prov-ex-u7-objectadj-3-ru")
    example_template = next(e for e in pack["examples"] if e["id"] == "ex-u7-objectadj-3")
    existing = {r["itemId"] for r in pack["provenanceRecords"]}
    facts: dict[str, dict[str, list[str]]] = {}
    for ex_id, objective, page, line, original, ru, en, step_id, sentence in RESTORED:
        frozen_line = WIKIBOOKS[page][2].read_text(encoding="utf-8").splitlines()[line - 1]
        if original not in frozen_line:
            raise SystemExit(f"{ex_id}: segment is not on {page}:{line}")
        ru_id, en_id = f"prov-{ex_id}-ru", f"prov-{ex_id}-en"
        if ru_id in existing or en_id in existing or any(e["id"] == ex_id for e in pack["examples"]):
            raise SystemExit(f"{ex_id}: already present")
        pack["provenanceRecords"].append(record(template, ru_id, "example", page, original, ru, objective))
        pack["provenanceRecords"].append(record(template, en_id, "meaning", page, original, en, objective))
        example = dict(example_template)
        example.update({"id": ex_id, "display": ru, "note": en, "objectiveId": objective})
        pack["examples"].append(example)
        teach = steps[step_id][2]
        teach["body"] = teach["body"] + " " + sentence
        teach["exampleIds"] = list(teach.get("exampleIds") or []) + [ex_id]
        facts.setdefault(steps[step_id][1]["id"], {})[sentence] = [ru_id, en_id]
        report["examples"].append([ex_id, f"{WIKIBOOKS[page][2].relative_to(ROOT)}:{line}", ru, en, step_id, sentence])
        report["records"] += [ru_id, en_id]
    for unit in pack["units"]:
        for session in unit["sessions"]:
            if session["id"] not in facts:
                continue
            teach = [s for s in session["steps"] if s["kind"] == "TEACH"]
            for rem in (s for s in session["steps"] if s["kind"] == "REMEDIATE"):
                rem["body"] = "\n\n".join(s["body"] for s in teach)
                rem["exampleIds"] = list(dict.fromkeys(e for s in teach for e in (s.get("exampleIds") or [])))
                for sentence, sources in facts[session["id"]].items():
                    rem["russianFacts"] = list(rem["russianFacts"]) + [sentence]
                    rem["russianFactSources"] = dict(rem["russianFactSources"], **{sentence: sources})
            # The objective lists its examples in the order its remediation shows them.
            restored = {ex_id for ex_id, objective, *_ in RESTORED}
            for objective in unit["objectives"]:
                if any(e in restored and next(r[1] for r in RESTORED if r[0] == e) == objective["id"] for e in rem["exampleIds"]):
                    if not set(objective["exampleIds"]) <= set(rem["exampleIds"]):
                        raise SystemExit(f"{objective['id']}: an objective example is missing from its remediation")
                    objective["exampleIds"] = list(rem["exampleIds"])
    return report


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15f Units 1-10 baseline; this transformer runs only once")
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
