#!/usr/bin/env python3
"""T14-n2 (owner direction after the T14 checkpoint): Unit 2 concept checks, learner voice, Unit 5 keys.

Exact-baseline transformer; it refuses any input other than the recorded baseline asset.

1. Unit 2: six of the fifteen print-pair checks (З, И, Л, П, Ф, Ы) are replaced by checks on the
   unit's own words: which stress-pair word has a meaning (lock, castle, flour, torment) and which
   word ends in the same sound (лук, плот). Only words already in Unit 2 are used; кот gets no new
   use, so the Block 1 listen-check sheet is unchanged. obj-u2-pairs becomes a checked objective.
2. Learner voice: Units 1-5 teaching, feedback, prompts and the eight Block 1 reference entries
   stop narrating provenance ("The dictionary gives...", "The grammar page says...", "Form
   glossed...", "quoted exactly...") or talking about the app's checks. Attribution stays in the
   source and provenance records. "Whither"/"Whence" become "where to"/"where from" (sense
   selection from the same frozen rows). Rewritten hosts that an exact research-synthesis record
   covered get a dated T14-n2 record; other new English is recorded as authored scaffold.
3. Keys: Unit 5 question-word keys list the stressed form first (Куда́, Отку́да, Ско́лько, Како́й;
   the stress comes from the same frozen Wikibooks row's transliteration); Unit 3's cue for
   письмо́ becomes "letter (mail)".
No Russian is generated.
"""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from apply_t14a_units_1_2 import PENDING, provenance, sha, step  # noqa: E402
from apply_t14b_units_1_2_owner import typed_key  # noqa: E402
from apply_t14b_units_3_4 import CYRILLIC, GRAMMAR_TERM, LINGUISTIC_CLAIM, coverage, gaps, hosts  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "9e57c5ec4bfea3c74dabe3d082e6ff12c66360643bb65313c83d6d6817b8b7ea"
WB = "src-wikibooks-russian-cc-by-sa"

# Whole sentences and phrases, longest first. Each is plain teaching English; none adds a claim.
VOICE = [
    # Unit 1
    (" Worked examples come before the guided choice, and the independent check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.", ""),
    ("Handwritten forms wait for verified assets. The checks ask about А, О, and Т.", "Your book shows the handwritten forms."),
    ("Handwritten forms wait for verified assets.", "Your book shows the handwritten forms."),
    (" The check asks about А.", ""),
    # Unit 2
    ("then identify the pair for the form on the check card.", "then identify the pair for each shown letter."),
    ("Unit 1 checked a staged set of letters.", "Unit 1 covered a staged set of letters."),
    ("The dictionary gives за́мок, stressed on the first vowel, the meaning castle, and замо́к, stressed on the second vowel, "
     "the meaning lock. It gives му́ка the meaning torment and мука́ the meaning flour.",
     "за́мок, stressed on the first vowel, means castle, and замо́к, stressed on the second vowel, means lock. "
     "му́ка means torment and мука́ means flour."),
    # Unit 3
    ("Unit 3 uses dictionary entries and grammar excerpts for the three groups, a small phrase set, and naming phrases.",
     "Unit 3 teaches three word groups, a small set of everyday phrases, and naming phrases."),
    ("The grammar page says: ", ""),
    ("An ending is not a substitute for the dictionary entry.", "Learn each word with its group; an ending alone does not decide it."),
    ("The dictionary lists these words as ", "These words are "),
    ("The dictionary entry for друг — friend is also masculine.", "друг — friend is also masculine."),
    ("The dictionary entry for дверь — door is feminine.", "дверь — door is feminine too."),
    ("Learn the group with each word; an ending is not a substitute for the dictionary entry.",
     "Learn the group with each word; an ending alone does not decide it."),
    ("Choose the answer the dictionary gives. The feedback shows the dictionary entry.",
     "Choose the answer for the shown word. The feedback explains it."),
    ("The dictionary lists this word as ", "This word is "),
    ("Four records in the third group", "Four words in the third group"),
    ("Two records the page still places in the first group", "Two more words in the first group"),
    ("These greetings come from the dictionary. ", "Three greetings: "),
    ("These short answers come from the dictionary. ", "Three short answers: "),
    ("Two written questions from the grammar page", "Two written questions"),
    ("These written questions are quoted exactly from the grammar page. ", ""),
    ("Compare the shown form with the dictionary entry. Do not invent a form that the entry does not give.",
     "Compare the shown form with the taught form. Use only the forms this unit teaches."),
    ("Two naming phrases from the grammar page", "Two naming phrases"),
    ("Two naming questions from the grammar page", "Two naming questions"),
    ("These two phrases are quoted exactly from the grammar page. Do not substitute another word into them. ",
     "Learn these two phrases as whole phrases. "),
    ("These two questions are quoted exactly from the grammar page. ", ""),
    ("Stress marks are optional; the check ignores them.", "Stress marks are optional when you type."),
    ("The dictionary gives this meaning: ", "This means "),
    ("The grammar page gives this meaning: ", "This means: "),
    ("The grammar page gives this example as: ", "This means: "),
    # Unit 4
    ("Unit 4 uses dictionary entries for the person words, the present forms, the written stress mark, and the "
     "dictionary-form examples.",
     "Unit 4 teaches the person words, present forms, the written stress mark, and the dictionary form in example sentences."),
    ("The dictionary gives this pair. ", ""),
    ("The dictionary gives this group. ", ""),
    ("How the grammar page forms the present", "How the present is formed"),
    ("Forms from one dictionary entry", "Present forms of чита́ть"),
    ("These forms come from the dictionary entry for чита́ть. Form glossed I: чита́ю. Form glossed you: чита́ешь. "
     "Form glossed he: чита́ет. ", "чита́ть in the present: I чита́ю, you чита́ешь, he чита́ет. "),
    ("These forms come from the dictionary entry for чита́ть. Form glossed we: чита́ем. Form glossed you (plural): "
     "чита́ете. Form glossed they: чита́ют. ", "чита́ть in the present: we чита́ем, you (plural) чита́ете, they чита́ют. "),
    ("The dictionary gives де́лаю for the form glossed I.", "With I: де́лаю."),
    ("Which present form does the dictionary give for the form glossed ", "Which present form goes with "),
    ("These infinitives come from the dictionary. ", "More infinitives: "),
    (" First practise a different person from this same entry, then answer the requested person without using option "
     "position. These supplied choices do not demonstrate producing an unseen form.", ""),
    (" When the check requests we, choose говори́м.", ""),
    (" When the check requests he, choose ви́дит.", ""),
    (" When the check requests you (plural), choose пи́шете.", ""),
    (" When the check requests they, choose иду́т.", ""),
    ("this card does not teach", "this lesson does not teach"),
    ("Compare the supplied forms above. Choose I here; the independent check asks for a different person.",
     "Compare the supplied forms above. Choose the form for I."),
    (" No audio is mapped to these forms.", ""),
    ("The dictionary places the written mark on vowel ", "The written mark is on vowel "),
    ("but the role check asks whether the noun is a subject or direct object.",
     "but the question here is whether the noun is the subject or the direct object."),
    ("; the optional reference choices do not demonstrate this case role. These supplied examples are deliberately "
     "taught, not unseen evidence or unrestricted productive case use.", "."),
    (" Compare that subject with the example before the independent check.", ""),
    # Unit 5
    ("Unit 5 uses grammar excerpts and dictionary entries for questions, place examples, and words that do not change "
     "form. It does not teach question intonation and it does not supply audio. The owned book remains separate work. "
     "Qualified review is pending.",
     "Unit 5 teaches question words, written questions, place examples, and words that do not change form. It does "
     "not teach question intonation or supply audio. Your own book remains separate work."),
    ("These words come from the grammar table. ", ""),
    ("These examples are quoted exactly from the grammar page. ", ""),
    ("Use these words only as quoted. Do not build a new phrase from them.", "Use these words only in the phrases shown."),
    ("Each pair shows the first form, then the second form, from the same dictionary entry.",
     "Each pair shows the first form, then the second form of the same word."),
    ("Which is the second form in the dictionary entry, not the first?", "Which is the second form of the shown word, not the first?"),
    ("The dictionary entry gives the second form as ", "The second form is "),
    (" These checks classify the shown noun; they do not test production of a sentence or a complete paradigm.", ""),
    ("It contains grammar excerpts and dictionary entries for ", "It covers "),
    ("It contains dictionary entries for ", "It covers "),
    ("Whither, where to", "where to"),
    ("Whence, where from", "where from"),
    ("The question words and written questions from the grammar table.", "Question words and written questions."),
    ("How the grammar page forms the present, with the forms of чита́ть and де́лать.",
     "How the present is formed, with the forms of чита́ть and де́лать."),
]
GRAMMAR_TABLE = re.compile(r"The grammar table gives (\S+) for (.+)\.$")
FORM_GLOSSED = re.compile(r"^The dictionary gives (\S+) for the form glossed (.+)\.$")
NARRATION = re.compile(r"(?i)(\bthe dictionary (gives|lists|places|records)\b|\bdictionary entr(y|ies)\b|\bthe grammar (page|table) "
                       r"(says|gives|records)\b|\b(form|row) glossed\b|\bquoted exactly\b|\bcomes? from the (dictionary|grammar)\b|"
                       r"\bfrom the grammar (page|table)\b|\bchecks? (asks?|requests?)\b|\bwhen the check\b|\bwhither\b|\bwhence\b)")


def voice(text: str) -> str:
    for old, new in VOICE:
        text = text.replace(old, new)
    m = GRAMMAR_TABLE.match(text)
    if m:
        text = f"{m.group(1)} means {m.group(2)}."
    m = FORM_GLOSSED.match(text)
    if m:
        text = f"{m.group(1)} goes with {m.group(2)}."
    return text


STRESSED = {"kuda": ("Куда", "Куда́", "kudá"), "otkuda": ("Откуда", "Отку́да", "otkúda"),
            "skolko": ("Сколько", "Ско́лько", "skól'ko"), "kakoy": ("Какой", "Како́й", "kakój")}


def pair_item(item_id: str, prompt: str, cue: str, options: list[str], answer: str, feedback: str,
              rationales: dict[str, str]) -> dict:
    return {"id": item_id, "kind": "SOURCE_MEANING", "language": "en", "prompt": prompt, "promptLetter": cue,
            "options": options, "answer": answer, "feedback": feedback, "objectiveId": "obj-u2-pairs",
            "sourceId": "src-openrussian-50e210c4", "reviewStatus": PENDING, "containsGeneratedRussian": False,
            "distractorRationales": [rationales.get(o, "") for o in options]}


def apply(pack: dict) -> dict:
    units = {u["number"]: u for u in pack["units"]}
    items = {i["id"]: i for i in pack["items"]}
    examples = {e["id"]: e for e in pack["examples"]}
    steps = {s["id"]: s for u in pack["units"] for se in u["sessions"] for s in se["steps"]}

    # --- 1. Unit 2 concept checks replace six print pairs --------------------------------------
    u2 = units[2]
    obj2 = {o["id"]: o for o in u2["objectives"]}
    dropped = [f"u2-i-print-{c}" for c in ("з", "и", "л", "п", "ф", "ы")]
    obj2["obj-u2-alphabet"]["checkItemIds"] = [i for i in obj2["obj-u2-alphabet"]["checkItemIds"] if i not in dropped]
    kept = obj2["obj-u2-alphabet"]["checkItemIds"]
    groups = [kept[0:3], kept[3:6], kept[6:9]]
    for n, ids in enumerate(groups, start=1):
        st = steps[f"u2-st-alphabet-check-{n}"]
        st["itemIds"] = ids
        st["title"] = "Print pairs: " + ", ".join(items[i]["promptLetter"] for i in ids)
    pack["items"] = [i for i in pack["items"] if i["id"] not in dropped]
    items = {i["id"]: i for i in pack["items"]}
    meaning = "Which word has this meaning?"
    sound = "Which word ends in the same sound as the shown word?"
    new2 = [
        pair_item("u2-i-pair-lock", meaning, "lock", ["за́мок", "замо́к", "му́ка"], "замо́к",
                  "замо́к, stressed on the second vowel, means lock.",
                  {"за́мок": "за́мок, stressed on the first vowel, means castle.", "му́ка": "му́ка means torment."}),
        pair_item("u2-i-pair-castle", meaning, "castle", ["замо́к", "мука́", "за́мок"], "за́мок",
                  "за́мок, stressed on the first vowel, means castle.",
                  {"замо́к": "замо́к, stressed on the second vowel, means lock.", "мука́": "мука́ means flour."}),
        pair_item("u2-i-pair-flour", meaning, "flour", ["му́ка", "мука́", "замо́к"], "мука́",
                  "мука́, stressed on the second vowel, means flour.",
                  {"му́ка": "му́ка, stressed on the first vowel, means torment.", "замо́к": "замо́к means lock."}),
        pair_item("u2-i-pair-torment", meaning, "torment", ["мука́", "за́мок", "му́ка"], "му́ка",
                  "му́ка, stressed on the first vowel, means torment.",
                  {"мука́": "мука́, stressed on the second vowel, means flour.", "за́мок": "за́мок means castle."}),
        pair_item("u2-i-pair-sound-лук", sound, "лук", ["плот", "луг", "код"], "луг",
                  "луг and лук end in the same sound: a final г sounds like к.",
                  {"плот": "плот ends in the т sound, not the к sound.", "код": "код ends in the т sound: a final д sounds like т."}),
        pair_item("u2-i-pair-sound-плот", sound, "плот", ["луг", "плод", "лук"], "плод",
                  "плод and плот end in the same sound: a final д sounds like т.",
                  {"луг": "луг ends in the к sound: a final г sounds like к.", "лук": "лук ends in the к sound, not the т sound."}),
    ]
    guided2 = pair_item("u2-g-pair-meadow", meaning, "meadow", ["лук", "луг", "плот"], "луг",
                        "луг means meadow. лук means onion.",
                        {"лук": "лук means onion.", "плот": "плот means raft."})
    pairs = obj2["obj-u2-pairs"]
    pairs.update({"kind": "RECOGNITION", "checkItemIds": [i["id"] for i in new2],
                  "practiceStepIds": ["u2-st-pairs-guided"], "remediationStepIds": ["u2-st-stress-pairs", "u2-st-devoicing"]})
    pairs_session = next(se for se in u2["sessions"] if se["id"] == "u2-s-pairs")
    at = next(i for i, st in enumerate(pairs_session["steps"]) if st["id"] == "u2-st-devoicing") + 1
    pairs_session["steps"].insert(at, step("u2-st-pairs-guided", "GUIDED", "Guided practice",
                                           "Choose the word for the shown meaning. This is practice, not the independent check.",
                                           dominantAction="Check", itemIds=[guided2["id"]], remediationStepId="u2-st-devoicing"))
    u2_check = next(se for se in u2["sessions"] if se["id"] == "u2-s-check")
    u2_check["steps"].insert(1, step("u2-st-check-pairs", "CHECK", "Stress pairs and final sounds",
                                     "Choose the word for each meaning or sound. A miss returns to the explanation.",
                                     itemIds=[i["id"] for i in new2], remediationStepId="u2-st-stress-pairs"))
    if "obj-u2-pairs" not in u2_check["objectiveIds"]:
        u2_check["objectiveIds"].append("obj-u2-pairs")
    pack["items"].extend(new2 + [guided2])
    items = {i["id"]: i for i in pack["items"]}

    # --- 3a. Unit 3 cue for письмо́ --------------------------------------------------------------
    g = items["u3-g-type-письмо"]
    g["promptLetter"] = "letter (mail)"
    g["feedback"] = "письмо́ means letter (mail). Stress marks are optional when you type."

    # --- 2. Learner voice over Units 1-5 and the Block 1 reference entries -----------------------
    changed: dict[str, str] = {}

    def fix(holder: dict, field: str) -> None:
        old = holder.get(field)
        if old:
            new = voice(old)
            if new != old:
                holder[field] = new
                changed[old] = new
    for n in range(1, 6):
        u = units[n]
        fix(u, "topicSummary")
        for o in u["objectives"]:
            fix(o, "title")
        unit_items, unit_examples = set(), set()
        for o in u["objectives"]:
            unit_items.update(o["checkItemIds"]); unit_examples.update(o["exampleIds"])
        for se in u["sessions"]:
            fix(se, "title")
            for st in se["steps"]:
                for f in ("title", "body", "bookDirection", "companionDirection"):
                    fix(st, f)
                unit_items.update(st.get("itemIds", [])); unit_examples.update(st.get("exampleIds", []))
        for i in unit_items:
            x = items[i]
            for f in ("prompt", "feedback", "promptLetter"):
                fix(x, f)
            if x.get("distractorRationales"):
                x["distractorRationales"] = [voice(r) for r in x["distractorRationales"]]
        for e in unit_examples:
            fix(examples[e], "note")
    # Remediation bodies that repeat their teaching follow the rewritten teaching.
    teach_sets = {
        "u3-st-rem-gender": ["u3-st-teach-gender-1", "u3-st-teach-gender-2", "u3-st-teach-gender-3", "u3-st-teach-gender-4", "u3-st-teach-gender-5"],
        "u4-st-rem-type-present": ["u4-st-teach-present-2", "u4-st-teach-present-3"],
    }
    for rem, teach in teach_sets.items():
        old = steps[rem]["body"]
        steps[rem]["body"] = "\n\n".join(steps[s]["body"] for s in teach)
        if steps[rem]["body"] != old:
            changed[old] = steps[rem]["body"]
    present = next(se for se in units[4]["sessions"] if se["id"] == "u4-s-present")
    old = steps["u4-st-rem-present"]["body"]
    steps["u4-st-rem-present"]["body"] = "\n\n".join(st["body"] for st in present["steps"] if st["kind"] == "TEACH")
    changed[old] = steps["u4-st-rem-present"]["body"]
    old = steps["u4-st-rem-nominative"]["body"]
    steps["u4-st-rem-nominative"]["body"] = "\n\n".join(steps[s]["body"] for s in (
        "u4-st-teach-nominative-1", "u4-st-teach-nominative-2", "u4-st-teach-nominative-3"))
    changed[old] = steps["u4-st-rem-nominative"]["body"]

    # Reference entries: Block 1 entries restate the rewritten teaching.
    ref_steps = {
        "ref-u3-gender": ["u3-st-teach-gender-1", "u3-st-teach-gender-2", "u3-st-teach-gender-3", "u3-st-teach-gender-4", "u3-st-teach-gender-5"],
        "ref-u4-pronouns": ["u4-st-teach-pronouns-1", "u4-st-teach-pronouns-2", "u4-st-teach-pronouns-3"],
        "ref-u4-present": ["u4-st-teach-present-1", "u4-st-teach-present-2", "u4-st-teach-present-3", "u4-st-teach-present-4"],
        "ref-u4-nominative": ["u4-st-teach-nominative-1", "u4-st-teach-nominative-2", "u4-st-teach-nominative-3"],
        "ref-u5-questions": ["u5-st-teach-questions-1", "u5-st-teach-questions-2", "u5-st-teach-questions-3", "u5-st-teach-questions-4", "u5-st-teach-questions-5"],
        "ref-u5-prepositional": ["u5-st-teach-prepositional-1", "u5-st-teach-prepositional-2", "u5-st-teach-prepositional-3",
                                 "u5-st-teach-prepositional-4", "u5-st-teach-prepositional-5", "u5-st-teach-prepositional-6"],
        "ref-u5-indeclinable": ["u5-st-teach-indeclinable-1", "u5-st-teach-indeclinable-2", "u5-st-teach-indeclinable-3", "u5-st-teach-indeclinable-4"],
    }
    for r in pack["references"]:
        if r["id"] in ref_steps:
            r["detail"] = "\n\n".join(steps[s]["body"].strip() for s in ref_steps[r["id"]])
        if r["introducedInUnit"] <= 5:
            r["summary"] = voice(r["summary"])
            r["title"] = voice(r["title"])

    # --- 3b. Unit 5 stressed keys and plain glosses ---------------------------------------------
    records = []
    by_item = {r["itemId"]: r for r in pack["provenanceRecords"]}
    for key, (plain, stressed, translit) in STRESSED.items():
        row = by_item[f"prov-u5-q-{key}"]
        if f"''{translit}''" not in row["originalSourceText"]:
            raise SystemExit(f"{key}: transliteration {translit} not in the frozen row")
        new = dict(row)
        new.update({"itemId": f"prov-u5-key-{key}-stressed", "shippedText": stressed, "transformation": "formatting",
                    "contentHash": sha(stressed)})
        records.append(new)
        item = items[f"u5-i-type-{key}"]
        item["answer"] = stressed
        item["typedAnswerKey"] = typed_key(item["typedAnswerKey"]["id"], [stressed, plain])
        item["feedback"] = item["feedback"].replace(f"{plain} means", f"{stressed} means", 1)
    for key, gloss in (("kuda", "where to"), ("otkuda", "where from")):
        row = by_item[f"prov-u5-q-{key}-en"]
        new = dict(row)
        new.update({"itemId": f"prov-u5-q-{key}-en-plain", "shippedText": gloss, "transformation": "sense selection",
                    "contentHash": sha(gloss)})
        records.append(new)
    pack["provenanceRecords"].extend(records)

    # --- Coverage: exact research-synthesis records follow their rewritten hosts ----------------
    research = [r for r in pack["provenanceRecords"] if r["sourceId"] == "src-content-audit-research"]
    added = []
    for r in research:
        if r["shippedText"] in changed and "synthesis" in r:
            new_text = changed[r["shippedText"]]
            synthesis = dict(r["synthesis"])
            synthesis["bibliography"] = synthesis["bibliography"] + (
                " T14-n2 learner-voice rewording dated 2026-11-12: provenance narration and check talk removed from the "
                "learner text; attribution stays in the provenance records. No new Russian or claim.")
            synthesis["objectiveOrClaimBinding"] = synthesis["objectiveOrClaimBinding"] + ":t14n2-voice-v1"
            original = synthesis["bibliography"] + "\n" + new_text
            new = dict(r)
            new.update({"itemId": r["itemId"] + "-t14n2-voice-v1", "sourceRecordId": r["sourceRecordId"] + ":t14n2-voice-v1",
                        "originalSourceText": original, "shippedText": new_text, "synthesis": synthesis,
                        "sourceHash": sha(original), "contentHash": sha(new_text)})
            added.append(new)
    seen = {r["itemId"] for r in pack["provenanceRecords"]}
    pack["provenanceRecords"].extend(r for r in added if r["itemId"] not in seen)

    forms = {f for l in pack["letters"] if l["sourceId"] == "src-unicode-cyrillic" for f in (l["uppercase"], l["lowercase"])}
    region = re.compile(r"[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*")

    def letter_forms_only(text: str) -> bool:
        # Mirrors V2Validator.letterFormRegion: Units 1-2 may show whole letter forms without a source record.
        for m in region.finditer(text):
            parts = [p for p in m.group().strip().rstrip("?!.,").split() if p]
            if not parts or any(p not in forms for p in parts):
                return False
        return True

    for n in range(2, 6):
        u = units[n]
        shipped = [r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == n]
        scaffolds = [r["text"] for r in pack["authoredEnglishRecords"] if r["unitNumber"] == n]
        count = 0
        for host in hosts(pack, u):
            for gap in gaps(host, coverage(host, shipped + scaffolds)):
                if gap in scaffolds:
                    continue
                if (CYRILLIC.search(gap) and not (n <= 2 and letter_forms_only(gap))) \
                        or LINGUISTIC_CLAIM.search(gap) or (GRAMMAR_TERM.search(gap) and "?" not in gap):
                    raise SystemExit(f"Unit {n}: fragment needs a source span: {gap!r} in {host[:90]!r}")
                count += 1
                scaffolds.append(gap)
                pack["authoredEnglishRecords"].append({"id": f"en-u{n}-t14n2-{count:03d}", "unitNumber": n, "text": gap,
                                                       "classification": "INDEPENDENTLY_AUTHORED_SCAFFOLD"})
    # No narration remains in Units 1-5 learner text or the Block 1 references.
    leftover = []
    for n in range(1, 6):
        for h in hosts(pack, units[n]) + [units[n]["topicSummary"]] + [se["title"] for se in units[n]["sessions"]]:
            if NARRATION.search(h):
                leftover.append(h[:120])
    for r in pack["references"]:
        if r["introducedInUnit"] <= 5 and r["classification"] != "LICENSE_OR_ATTRIBUTION":
            for f in ("title", "summary", "detail"):
                if NARRATION.search(r[f]):
                    leftover.append(f"{r['id']}: {r[f][:100]}")
    if leftover:
        raise SystemExit("Narration left:\n" + "\n".join(leftover))
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T14-n2 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
