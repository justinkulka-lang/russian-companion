"""T15i content (owner decisions D25j, D26): finish the Block 2 content items. Exact-baseline transformer; runs once.

- CC031: u7-i-endings-1 asks for the neuter singular nominative of но́вый from its plural nominative cue (adjectives.csv:16
  cells decl_pl_nom, decl_n_nom, decl_m_nom, decl_f_nom), replacing the out-of-scope genitive.
- WC005: Unit 7's question-adjective, demonstrative and place teaching label each form with gender, number and case, each
  label checked against its OpenRussian cell (adjectives.csv:4, 7, 16, 18).
- CC021: u6 feminine teaching and remediation state the ending change (accusative-4330404.txt:35); u6 third-person
  remediation gives his/her/their (accusative-4330404.txt:129); Unit 10 teaches муж / му́жа (nouns.csv:127) before
  u10-i-preposition-2, and the preposition remediation repeats the teaching.
- CC027 (approved: drop the feature only): u7-i-manner-4 and its own content-audit records are retired.
- CC023: the remaining fragments (u6 direction, u7 demonstrative, place and adverb teaching and remediation) become plain
  sentences; Russian unchanged.
- WC011: Units 6-10 get unit-specific binder and tutor tasks. Prompts are English; model answers quote only lines the
  unit already shows, from its frozen records, with the unit's own glosses.
English that changes gets content-audit coverage records (T15f/T15g pattern); Russian stays covered by existing frozen
records (the transformer fails if any new Russian region lacks one).
"""
from __future__ import annotations

import copy
import csv
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
import apply_d7b_selection_records as d7b  # noqa: E402
import apply_t15g_block2 as t15g  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "5b1e9707f6503f6569a0d72a0129182c26defe566eb4910aeb16b34c30b40a02"  # the T15h pack (1b0dd1cc)
TASK = "T15i"
ADJ = ROOT / "catalog/sources/units-6-10/openrussian/adjectives.csv"

# (step id, old text, new text). WC005 labels, CC021 repairs and CC023 fragments.
STEP_EDITS = [
    ("u7-st-teach-question-1", "како́й — what kind of. кака́я — what kind of.",
     "како́й — what kind of (masculine singular nominative). кака́я — what kind of (feminine singular nominative)."),
    ("u7-st-teach-question-3", "како́е — what kind of. каки́е — what kind of.",
     "како́е — what kind of (neuter singular nominative). каки́е — what kind of (plural nominative)."),
    ("u7-st-teach-demonstrative-1", "э́та — this.", "э́та — this (feminine singular nominative)."),
    ("u7-st-teach-demonstrative-2", 'these good new students" is эти хорошие новые студенты',
     "эти хорошие новые студенты — these good new students."),
    ("u7-st-rem-demonstrative", 'these good new students" is эти хорошие новые студенты',
     "эти хорошие новые студенты — these good new students."),
    ("u7-st-teach-demonstrative-3", "э́тот — this. э́то — this. э́ти — these.",
     "э́тот — this (masculine singular nominative). э́то — this (neuter singular nominative). э́ти — these (plural nominative)."),
    ("u7-st-teach-demonstrative-4", "э́ту — this.", "э́ту — this (feminine singular accusative)."),
    ("u7-st-teach-place-2", "They were talking about a good new cinema' - Они говорили о хорошем новом кино",
     "Они говорили о хорошем новом кино — They were talking about a good new cinema."),
    ("u7-st-rem-place", "They were talking about a good new cinema' - Они говорили о хорошем новом кино",
     "Они говорили о хорошем новом кино — They were talking about a good new cinema."),
    ("u7-st-teach-place-3", "но́вый — new.", "но́вый — new (masculine singular nominative)."),
    ("u7-st-teach-place-4", "но́вом — new. но́вую — new.",
     "но́вом — new (masculine and neuter singular prepositional). но́вую — new (feminine singular accusative)."),
    ("u7-st-teach-place-5", "хоро́ший — good. хоро́шем — good.",
     "хоро́ший — good (masculine singular nominative). хоро́шем — good (masculine and neuter singular prepositional)."),
    ("u7-st-teach-adverb-2", '"быстро", "хорошо", "прекрасно" (quickly, well, wonderfully)',
     "быстро — quickly. хорошо — well. прекрасно — wonderfully."),
    ("u7-st-rem-adverb", '"быстро", "хорошо", "прекрасно" (quickly, well, wonderfully)',
     "быстро — quickly. хорошо — well. прекрасно — wonderfully."),
    ("u6-st-teach-direction-2", "generally the accusative case denotes direction", "Generally, the accusative case denotes direction."),
    ("u6-st-rem-direction", "generally the accusative case denotes direction", "Generally, the accusative case denotes direction."),
    ("u6-st-teach-feminine-2", "have their own accusative singular ending.", "have their own accusative singular ending: а becomes у, and я becomes ю."),
    ("u6-st-rem-feminine", "have their own accusative singular ending.", "have their own accusative singular ending: а becomes у, and я becomes ю."),
    ("u6-st-rem-third", "These do not decline according to the gender or number of the modified noun.",
     "его — his. её — her. их — their. These do not decline according to the gender or number of the modified noun."),
    ("u10-st-teach-preposition-11", "моего́ — my. моя́ — my.", "моего́ — my. моя́ — my. муж — husband. му́жа — husband."),
]
# Remediation sentences each carry their own fact marker (the validator and Unit 6/7/10 fact tests read markers per sentence).
FACT_MARKERS = {
    "u6-st-rem-third": {"его — his.": ["prov-ex-u6-third-0-ru"], "её — her.": ["prov-ex-u6-third-1-ru"], "их — their.": ["prov-ex-u6-third-2-ru"],
                        "These do not decline according to the gender or number of the modified noun.": ["prov-u6-third-rem"]},
    "u7-st-rem-adverb": {"быстро — quickly.": ["prov-u7-adverb-rem", "prov-ex-u7-adverb-0-ru"],
                         "хорошо — well.": ["prov-u7-adverb-rem", "prov-ex-u7-adverb-1-ru"],
                         "прекрасно — wonderfully.": ["prov-u7-adverb-rem", "prov-ex-u7-adverb-2-ru"]},
}
ADDED_MARKERS = {"u10-st-rem-preposition": {"муж — husband.": ["prov-t15g-u10-muzh-sg-nom"], "му́жа — husband.": ["prov-t15g-u10-muzh-sg-gen"]}}
# WC005 labels: (form shown, adjectives.csv row, column) for every label added.
LABEL_CELLS = [
    ("како́й", 7, "decl_m_nom"), ("кака́я", 7, "decl_f_nom"), ("како́е", 7, "decl_n_nom"), ("каки́е", 7, "decl_pl_nom"),
    ("э́та", 4, "decl_f_nom"), ("э́тот", 4, "decl_m_nom"), ("э́то", 4, "decl_n_nom"), ("э́ти", 4, "decl_pl_nom"), ("э́ту", 4, "decl_f_acc"),
    ("но́вые", 16, "decl_pl_nom"), ("но́вый", 16, "decl_m_nom"), ("но́вом", 16, "decl_m_prep"), ("но́вом", 16, "decl_n_prep"),
    ("но́вую", 16, "decl_f_acc"), ("хоро́ший", 18, "decl_m_nom"), ("хоро́шем", 18, "decl_m_prep"), ("хоро́шем", 18, "decl_n_prep"),
    ("хоро́шие", 18, "decl_pl_nom"),
]
BINDER = {
    6: ({"id": "binder-u6-direction", "objectiveIds": ["obj-u6-direction"],
         "prompt": "In your binder, make two columns headed location and direction. Copy this unit's four example lines about being somewhere and going somewhere into the right column with their meanings, then underline the noun ending that shows the case.",
         "modelAnswer": "Location: Я жил в Лондоне — I lived in London. Оно на столе — It's on the table.\nDirection: Я шёл в дом — I was walking into the house. Я шёл на улицу — I was walking out into the street."},
        {"id": "tutor-u6-direction", "bring": "Your binder location and direction columns, to read each line aloud and say whether it names a location or a direction."}),
    7: ({"id": "binder-u7-adjectives", "objectiveIds": ["obj-u7-endings", "obj-u7-place"],
         "prompt": "In your binder, make a table for the adjective meaning new with rows for masculine, feminine, neuter and plural. Write the nominative form in each row from this unit, then add the prepositional form used with masculine and neuter nouns. Compare your table with the model answer.",
         "modelAnswer": "Masculine: но́вый. Feminine: но́вая. Neuter: но́вое. Plural: но́вые.\nMasculine and neuter prepositional: но́вом."},
        {"id": "tutor-u7-adjectives", "bring": "Your binder table of the forms of the adjective meaning new, to read each form aloud and name its gender, number and case."}),
    8: ({"id": "binder-u8-plurals", "objectiveIds": ["obj-u8-plural"],
         "prompt": "In your binder, copy four of this unit's singular and plural noun pairs with their meanings, then underline the part of each plural that differs from the singular.",
         "modelAnswer": "сто́л, столы́ — table, tables.\nкни́га, кни́ги — book, books.\nокно́, о́кна — window, windows.\nсло́во, слова́ — word, words."},
        {"id": "tutor-u8-plurals", "bring": "Your binder list of singular and plural noun pairs, to say each pair aloud."}),
    9: ({"id": "binder-u9-counting", "objectiveIds": ["obj-u9-cardinal", "obj-u9-quantity"],
         "prompt": "In your binder, write the numbers 1 to 10 in words from this unit. Then copy this unit's three phrases counting hours and note the form of the noun after each number.",
         "modelAnswer": "1 оди́н, 2 два, 3 три, 4 четы́ре, 5 пять, 6 шесть, 7 семь, 8 во́семь, 9 де́вять, 10 де́сять.\nодин час — one hour (nominative singular).\nдва часа — two hours (genitive singular).\nпять часов — five hours (genitive plural)."},
        {"id": "tutor-u9-counting", "bring": "Your binder list of numbers and hour phrases, to count aloud from 1 to 10 and say the three hour phrases."}),
    10: ({"id": "binder-u10-genitive", "objectiveIds": ["obj-u10-pronoun", "obj-u10-have", "obj-u10-absence"],
          "prompt": "In your binder, copy this unit's personal pronouns with their genitive forms. Then copy one possession line and one absence line from this unit with their meanings, and underline the genitive word in each.",
          "modelAnswer": "Я — Меня́. Мы — Нас. Ты — Тебя́. Вы — Вас. Они — Их.\nУ вас есть сын? — Do you have a son?\nНет рестора́нов — There are no restaurants."},
         {"id": "tutor-u10-genitive", "bring": "Your binder pronoun list, to say each pronoun with its genitive form, and the two lines you copied."}),
}


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def check_labels() -> None:
    rows = list(csv.reader(ADJ.open(encoding="utf-8"), delimiter="\t"))
    header = rows[0]
    for form, n, column in LABEL_CELLS:
        cell = rows[n - 1][header.index(column)]
        forms = {c.strip().replace("'", "́") for c in cell.split(",")}
        if form not in forms:
            raise SystemExit(f"label not supported: {form} is not {column} at adjectives.csv:{n} ({cell})")


def retarget_endings(pack: dict) -> None:
    item = next(i for i in pack["items"] if i["id"] == "u7-i-endings-1")
    cue, key, d1, d2 = "но́вые", "но́вое", "но́вый", "но́вая"
    feedback = f"{cue} is the plural nominative form; {key} is the neuter singular nominative form of the same adjective."
    r1 = f"{d1} is the masculine singular nominative form, so it does not supply the requested neuter singular nominative form."
    r2 = f"{d2} is the feminine singular nominative form, so it does not supply the requested neuter singular nominative form."
    item.update({"prompt": "Which neuter singular nominative form of the same adjective corresponds to the shown form?",
                 "promptLetter": cue, "options": [key, d1, d2], "answer": key, "feedback": feedback,
                 "distractorRationales": ["", r1, r2], "russianFacts": [feedback, r1, r2],
                 "russianFactSources": {feedback: ["prov-ex-or-u7-endings-10", "prov-ex-or-u7-endings-9"],
                                        r1: ["prov-ex-or-u7-endings-7"], r2: ["prov-ex-or-u7-endings-8"]}})
    a = item["assessment"]
    a.update({"testedFeature": "NEUTER_ADJECTIVE_NOMINATIVE", "answerSourceIds": ["prov-ex-or-u7-endings-9"],
              "distractorSourceIds": ["", "prov-ex-or-u7-endings-7", "prov-ex-or-u7-endings-8"],
              "distractorDifference": ["", r1, r2], "whyAnswerDemonstratesObjective": feedback, "contrastMembers": [key, d1, d2],
              "relationParticipantIds": ["prov-ex-or-u7-endings-10", "prov-ex-or-u7-endings-9", "prov-ex-or-u7-endings-7", "prov-ex-or-u7-endings-8"]})
    # The genitive-only records and the old wording's coverage records belonged to this check alone.
    used = json.dumps([i for i in pack["items"] if i["id"] != "u7-i-endings-1"] + pack["examples"] + pack["units"], ensure_ascii=False)
    drop = {"prov-ca-t15b-u7-i-endings-1-1", "prov-ca-t15b-u7-i-endings-1-2", "prov-ca-t15b-u7-i-endings-1-3"}
    drop |= {r for r in ("prov-t15b-u7-endings-novyy-decl-n-gen", "prov-t15b-u7-endings-novyy-decl-f-acc") if f'"{r}"' not in used}
    pack["provenanceRecords"] = [r for r in pack["provenanceRecords"] if r["itemId"] not in drop]
    print(f"CC031 u7-i-endings-1: {cue} -> {key}; dropped {sorted(drop)}")


def edit_steps(pack: dict) -> None:
    steps = {st["id"]: st for u in pack["units"] for s in u["sessions"] for st in s["steps"]}
    for sid, old, new in STEP_EDITS:
        st = steps[sid]
        if st["body"].count(old) != 1:
            raise SystemExit(f"{sid}: text not found once: {old}")
        st["body"] = st["body"].replace(old, new)
        facts = st.get("russianFacts") or []
        if facts:
            sources = st.get("russianFactSources") or {}
            st["russianFacts"] = [f.replace(old, new) if old in f else f for f in facts]
            st["russianFactSources"] = {(k.replace(old, new) if old in k else k): v for k, v in sources.items()}
    # Unit 10 remediation repeats its teaching (Unit10ReinforcementReviewTest invariant).
    u10 = next(u for u in pack["units"] if u["number"] == 10)
    obj = next(o for o in u10["objectives"] if o["id"] == "obj-u10-preposition")
    teach = [steps[i]["body"] for i in obj["instructionStepIds"] if steps[i]["kind"] == "TEACH"]
    steps["u10-st-rem-preposition"]["body"] = "\n\n".join(teach)
    records = {r["itemId"] for r in pack["provenanceRecords"]}
    for sid, markers in list(FACT_MARKERS.items()) + list(ADDED_MARKERS.items()):
        st = steps[sid]
        if sid in FACT_MARKERS:
            st["russianFacts"], st["russianFactSources"] = [], {}
        for fact, sources in markers.items():
            if st["body"].count(fact) != 1 or not set(sources) <= records:
                raise SystemExit(f"{sid}: marker {fact} not in the body once, or a source record is missing")
            st["russianFacts"].append(fact)
            st["russianFactSources"][fact] = sources
    print(f"step edits: {len(STEP_EDITS)}")


def retire_manner4(pack: dict) -> None:
    pack["items"] = [i for i in pack["items"] if i["id"] != "u7-i-manner-4"]
    for u in pack["units"]:
        for o in u["objectives"]:
            o["checkItemIds"] = [x for x in o["checkItemIds"] if x != "u7-i-manner-4"]
        for s in u["sessions"]:
            for st in s["steps"]:
                st["itemIds"] = [x for x in st.get("itemIds", []) if x != "u7-i-manner-4"]
    before = len(pack["provenanceRecords"])
    pack["provenanceRecords"] = [r for r in pack["provenanceRecords"] if "u7-i-manner-4" not in r["itemId"]]
    print(f"CC027 u7-i-manner-4 retired; {before - len(pack['provenanceRecords'])} own records removed")


def binder_tasks(pack: dict) -> None:
    frozen = {}
    for r in pack["provenanceRecords"]:
        if d7b.frozen_record(pack, r):
            frozen.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    for u in pack["units"]:
        if u["number"] not in BINDER:
            continue
        binder, tutor = copy.deepcopy(BINDER[u["number"]])
        objectives = {o["id"] for o in u["objectives"]}
        if not set(binder["objectiveIds"]) <= objectives:
            raise SystemExit(f"Unit {u['number']}: binder objectives {binder['objectiveIds']} not in the unit")
        for field in ("prompt", "modelAnswer"):
            for m in d7b.REGION.finditer(binder[field]):
                region = m.group(0).strip()
                if not {region, region.rstrip("?!.,")} & frozen.get(u["number"], set()):
                    raise SystemExit(f"Unit {u['number']} {field}: {region} has no frozen record in the unit")
        if re.search("[Ѐ-ӿ]", tutor["bring"]) or re.search("[Ѐ-ӿ]", binder["prompt"]):
            raise SystemExit("prompts and tutor tasks are English")
        if u.get("binderTask") or u.get("tutorTask"):
            raise SystemExit(f"Unit {u['number']} already has a binder or tutor task")
        u["binderTask"], u["tutorTask"] = binder, tutor
        print(f"WC011 Unit {u['number']}: {binder['id']}, {tutor['id']}")


def cover_english(pack: dict, before: dict) -> None:
    """t15g.cover_english with T15i record IDs and wording."""
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shipped = {}
    for r in pack["provenanceRecords"]:
        shipped.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    template = records["prov-t15an1-u6-possessive-our-your"]
    count = 0

    def coverage(unit: int, objective: str, key: str, text: str) -> None:
        nonlocal count
        if not text or text in shipped.get(unit, set()):
            return
        record = copy.deepcopy(template)
        note = (f"New English wording authored on 2026-11-20 ({TASK}); it did not exist before that date. It covers the"
                " wording only; the Russian facts it states are cited from frozen source records in the marker's"
                " russianFactSources. 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        item_id = f"prov-ca-t15i-{key}"
        if item_id in records:
            raise SystemExit(f"Duplicate record {item_id}")
        record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective,
                       "sourceRecordId": f"ca-coverage:{objective}:{key}", "shippedText": text,
                       "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      f" this record's English text is new, authored 2026-11-20 for {TASK}."})
        pack["provenanceRecords"].append(record)
        records[item_id] = record
        shipped.setdefault(unit, set()).add(text)
        count += 1

    old_hosts = t15g.english_hosts(before)
    for (owner, field), (unit, objective, text, facts) in sorted(t15g.english_hosts(pack).items()):
        old = old_hosts.get((owner, field))
        if old is not None and old[2] == text:
            continue
        coverage(unit, objective, f"{owner}-{field}", text)
        old_facts = set(old[3]) if old else set()
        for n, fact in enumerate(f for f in facts if f not in old_facts):
            coverage(unit, objective, f"{owner}-{field}-fact{n}", fact)
    print(f"english coverage records: {count}")


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15i baseline; this transformer runs only once")
    check_labels()
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    before = copy.deepcopy(pack)
    retarget_endings(pack)
    edit_steps(pack)
    retire_manner4(pack)
    binder_tasks(pack)
    cover_english(pack, before)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
