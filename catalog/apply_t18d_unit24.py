#!/usr/bin/env python3
"""Apply the Unit 24 Block 4 packet (D62) to bc5f1e07."""
from __future__ import annotations

import copy
import csv
import hashlib
import json
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from apply_d7b_selection_records import frozen_record, selectable
from apply_t14b_units_1_2_owner import typed_item
from apply_t17a_unit_16 import forms, sentences, stressed
from curriculum_storage import encode, loads

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
LOG = ROOT / "audit/T18d-unit24-changes.csv"
SRC = ROOT / "catalog/sources"
BASELINE = "108fa7e35761737a58d233e9e763395f8830f9634b88150fa1d8f774365abd69"
TASK, DATE = "T18d", "2026-11-28"
SLUGS = ("verbal-ne", "absence", "alternation", "stand-ins", "concord", "word-order-contrast", "placement")
UNSUPPORTED = re.compile(r"(?:Я не ви́жу книг|Я не ви́жу э́ту кни́гу|э́ту кни́гу|не́кого спросить|не́кому|не́кем)(?![\w\u0301])")
RETIRED_ITEMS = {"u24-i-alternation-2", "u24-i-stand-ins-2"}
RETIRED_EXAMPLES = {"ex-u24-alternation-0", "ex-u24-stand-ins-2"}
REWORD = {
    2876: "Compare the two sentence structures.", 2899: "Compare the two sentence structures.",
    2885: "Compare the affirmative and negative replies.", 2932: "Review the existence and absence patterns.",
    3002: "Compare the meanings before choosing.", 3041: "Check both parts of the sentence.",
    3075: "Use the context supplied with each example.", 3076: "Use the context supplied with each example.",
    3084: "Use the context supplied with each example.", 3118: "Compare the two complete sentences.",
}
REMOVE = {2875, 2904, 2912, 2920, 2939, 2948, 2955, 2958, 2960, 2961, 2973, 2974, 2979, 2983, 2985, 2986,
          2988, 2989, 2994, 3016, 3025, 3033, 3049, 3057, 3064, 3072, 3082, 3083, 3087, 3090, 3094, 3097,
          3101, 3102, 3106, 3121, 3122, 3126, 3131, 3132, 3136, 3141, 3144}
PENDING = {2953, 2956, 2957, 2963, 2966, 2967, 2981, 2984, 3006, 3010, 3023, 3027}
FACT_RANGES = (
    (2871, 2915, "V:18–19,26–31"), (2916, 2951, "V:26,28,33"),
    (2953, 2989, "G:55; N:60; V:26"), (2992, 3027, "O:1,15–21,55; I:15–18; OR:51,72,451"),
    (3031, 3057, "O:97–104"), (3062, 3097, "G:2339–2348; R:211"),
    (3101, 3138, "R:211,217–222"), (3141, 3144, "G:2347–2348"),
)
TYPED = (
    ("verbal-ne", "predicate-ne", "OR", 4, "не", "the separate particle that negates a verb"),
    ("absence", "absence-net", "OR", 41, "нет", "the word for absence in the existence pattern"),
    ("absence", "existence-est", "OR", 4992, "есть", "the word meaning there is or there are"),
    ("alternation", "book-accusative", "G", 55, "кни́гу", "the accusative singular of book"),
    ("stand-ins", "nobody", "O", 16, "никто́", "the negative pronoun meaning nobody"),
    ("stand-ins", "nothing", "O", 20, "ничто́", "the negative pronoun meaning nothing"),
    ("stand-ins", "nobody-available", "O", 55, "не́кого", "the form meaning there is nobody available for an action"),
    ("stand-ins", "someone", "O", 17, "не́кто", "the indefinite pronoun meaning an unspecified person"),
)
TEACHING = {
    "verbal-ne-1": ("Да, говорю́ — Yes, I'm speaking. Нет, не говорю́ — No, I'm not speaking. "
                    "The first word answers yes or no. In the second reply the separate particle before the speaking verb makes it negative. "
                    "Compare the affirmative and negative replies."),
    "verbal-ne-2": ("To negate a verb, place не before it. The present-tense copula is normally omitted in a noun predicate. "
                    "The negative particle then stands before the noun predicate."),
    "verbal-ne-3": ("Вы говори́те? — Are you speaking? Он не студент — He's not a student. "
                    "The question is affirmative. In the student sentence the negative particle precedes the noun."),
    "verbal-ne-4": ("Борис не приходит — Boris isn't coming. The negative particle precedes the verb for coming. "
                    "The shorter expressions не студент and не говорю́ are about being a student and speaking."),
    "absence-1": ("есть expresses existence and нет expresses absence. Да is a yes reply. "
                  "The separate particle не negates a predicate."),
    "absence-2": ("If it isn't there, we use нет followed by the genitive. The noun naming what is absent goes in the genitive. "
                  "Review the existence and absence patterns."),
    "absence-3": ("In conversation нет can also mean no. The pronunciation of не is shown as nye."),
    "alternation-1": ("Я не ви́жу кни́гу — I don't see the book. Я не ви́жу кни́ги — I don't see any books. "
                      "Both sentences are negative. The object in the first sentence is accusative singular. "
                      "In the second sentence the indefinite reading is associated with genitive."),
    "alternation-2": ("A negated direct object can be genitive or accusative. The ending in кни́ги is not unambiguous genitive plural. "
                      "The specified singular object uses the form кни́гу. Genitive under negation is not a mechanical rule."),
    "stand-ins-1": ("кто — никто́. что — ничто́. The first pair concerns a person and nobody. The second concerns a thing and nothing. "
                    "Compare the meanings before choosing."),
    "stand-ins-2": ("не́кого expresses that there is nobody available for an action. This negative form belongs to a set with no nominative."),
    "stand-ins-3": ("не́кто is an indefinite pronoun for an unspecified person. не́что is an indefinite pronoun for an unspecified thing."),
    "concord-1": ("никто́ — не. ничто́ — не. In these patterns the negative pronoun requires the separate particle in the predicate. "
                  "They work together to express one overall negative statement. Check both parts of the sentence."),
    "concord-2": ("ни- is a negative-pronoun prefix. никому is a dative form referring to nobody. The separate predicate particle is не."),
    "word-order-contrast-1": ("Не надо меня уговаривать. — One should not persuade me when the speaker would never agree. "
                              "Меня не надо уговаривать. — There is no need to persuade me when the speaker will do it anyway."),
    "word-order-contrast-2": ("Word order can change what is in focus or in the scope of a particle. "
                              "Intonation and discourse context can clarify the intended reading. Use the context supplied with each example."),
    "word-order-contrast-3": ("В магазин пошёл я. — It was me who went to the shop. Here the speaker is the person in focus. "
                              "В магазин я пошёл. — To the shop—that's where I went. Here the answer identifies the destination."),
    "placement-1": ("Мне не холодно. — To me it is not cold. This can also be expressed in English as I am not cold. "
                    "Не мне холодно. — It is not to me that it is cold. The first negates the cold state for the experiencer. "
                    "The second contrasts the experiencer and does not by itself identify that other person."),
    "placement-2": ("Note that the modifier не negates the word which comes right after it. In this pair its position distinguishes "
                    "the state from the experiencer. Compare the two complete sentences."),
}
EXPLANATIONS = {
    "Нет, не говорю́": "a negative first-person reply, with the particle before the speaking verb",
    "Он не студент": "a negative noun predicate with the present copula omitted",
    "Вы говори́те?": "an affirmative question about speaking",
    "Да, говорю́": "an affirmative first-person reply",
    "Борис не приходит": "the full sentence saying Boris is not coming",
    "не студент": "a negative noun predicate about a student",
    "не говорю́": "a negative form of the speaking verb",
    "нет": "absence in the existence pattern",
    "есть": "existence in the affirmative pattern",
    "не": "the separate negative particle in the predicate",
    "Да": "an affirmative reply",
    "I don't see the book": "the singular accusative reading",
    "I don't see any books": "an indefinite reading of the genitive example",
    "никто́": "the negative pronoun meaning nobody",
    "ничто́": "the negative pronoun meaning nothing",
    "не́кто": "the indefinite pronoun for an unspecified person",
    "не́что": "the indefinite pronoun for an unspecified thing",
    "ни-": "a negative-pronoun prefix",
    "никому": "a dative negative pronoun meaning to nobody",
    "One should not persuade me": "the speaker would never agree",
    "There is no need to persuade me": "the speaker will do it anyway",
    "It was me who went to the shop.": "the speaker is the person in focus",
    "To the shop—that's where I went.": "the reply identifies the destination",
    "To me it is not cold.": "negation of the cold state for the experiencer",
    "It is not to me that it is cold.": "the experiencer is contrasted",
    "I am not cold.": "negation of the cold state for the speaker",
}
OPERATING = set(REWORD.values()) | {
    "Read the examples, then cover them and write the forms from their meanings.",
    "Compare your spelling with the answer.",
}


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def apply(pack: dict) -> list[dict]:
    unit = pack["units"][23]
    assert unit["number"] == 24
    objectives = {o["id"].removeprefix("obj-u24-"): o for o in unit["objectives"]}
    steps = {st["id"]: st for s in unit["sessions"] for st in s["steps"]}
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    items = {i["id"]: i for i in pack["items"] if i["objectiveId"].startswith("obj-u24-")}
    log = []
    authored = {}

    def changed(owner, old, new, finding):
        if old != new:
            log.append(dict(task=TASK, finding=finding, **{"owner.field": owner}, old=old, new=new))

    def set_text(host, field, text, finding, slug):
        owner = f"{host['id']}.{field}"
        changed(owner, host.get(field, ""), text, finding)
        host[field] = text
        authored[owner] = slug, text

    def record(template, ident, slug, raw, shown, source_record_id, transformation=None):
        r = copy.deepcopy(records[template])
        r.update(itemId=ident, unitNumber=24, objectiveId=objectives[slug]["id"], sourceRecordId=source_record_id,
                 originalSourceText=raw, shippedText=shown, sourceHash=sha(raw), contentHash=sha(shown), recordType="lexical record")
        if transformation:
            r["transformation"] = transformation
        if ident in records or not frozen_record(pack, r):
            raise ValueError(f"Invalid frozen record {ident}")
        records[ident] = r
        pack["provenanceRecords"].append(r)
        return ident

    def remove_entities(field, ids, key="id"):
        for obj in pack[field]:
            if obj[key] in ids:
                changed(f"{field}:{obj[key]}", obj.get("display", obj.get("promptLetter", "")), "REMOVED", "D62a–c")
        pack[field] = [obj for obj in pack[field] if obj[key] not in ids]

    remove_entities("items", RETIRED_ITEMS)
    remove_entities("examples", RETIRED_EXAMPLES)
    for i in RETIRED_ITEMS:
        items.pop(i)
    for obj in objectives.values():
        obj["checkItemIds"] = [i for i in obj["checkItemIds"] if i not in RETIRED_ITEMS]
        obj["exampleIds"] = [e for e in obj["exampleIds"] if e not in RETIRED_EXAMPLES]
    for st in steps.values():
        for field, retired in (("itemIds", RETIRED_ITEMS), ("exampleIds", RETIRED_EXAMPLES)):
            if field in st:
                st[field] = [ident for ident in st[field] if ident not in retired]

    # A new example and guided question use only the already attested G:55 pair.
    genitive_ru = records["prov-ex-u24-alternation-0-ru"]
    genitive_en = records["prov-ex-u24-alternation-0-en"]
    accusative = items["u24-i-alternation-1"]
    example = copy.deepcopy(next(e for e in pack["examples"] if e["id"] == "ex-u24-alternation-1"))
    example.update(id="ex-u24-alternation-genitive", display=genitive_ru["shippedText"], note=genitive_en["shippedText"])
    pack["examples"].append(example)
    objectives["alternation"]["exampleIds"].insert(0, example["id"])
    steps["u24-st-teach-alternation-1"].setdefault("exampleIds", []).append(example["id"])
    changed(example["id"], "", example["display"], "D62b / G:55")
    guided = items["u24-g-alternation-0"]
    updated = copy.deepcopy(accusative)
    updated.update(id=guided["id"], promptLetter=genitive_ru["shippedText"], answer=genitive_en["shippedText"],
                   options=list(reversed(accusative["options"])))
    updated["assessment"].update(independent=False, testedFeature="NEGATION_INDEFINITE", contrastSetId=guided["id"],
                                 answerSourceIds=[genitive_en["itemId"]],
                                 distractorSourceIds=["", "prov-ex-u24-alternation-1-en"], contrastMembers=updated["options"],
                                 relationParticipantIds=[genitive_ru["itemId"], genitive_en["itemId"], "prov-ex-u24-alternation-1-en"])
    changed(f"{guided['id']}.promptLetter", guided["promptLetter"], updated["promptLetter"], "D62b / G:55")
    guided.clear()
    guided.update(updated)

    for suffix, text in TEACHING.items():
        slug = suffix.rsplit("-", 1)[0]
        set_text(steps[f"u24-st-teach-{suffix}"], "body", text, "D62a–d / prep 3 and 5", slug)
    set_text(items["u24-g-stand-ins-0"], "prompt", 'Which form means "nobody"?', "D62c", "stand-ins")
    set_text(items["u24-i-stand-ins-1"], "prompt", 'Which form means "nothing"?', "D62c / O:19–21, OR:51", "stand-ins")
    for n in (1, 2):
        set_text(items[f"u24-i-concord-{n}"], "prompt", "Which separate particle goes before the verb with this negative pronoun?",
                 "A2-24 / O:97", "concord")
    for iid in ("u24-g-placement-0", "u24-i-placement-1", "u24-i-placement-2"):
        set_text(items[iid], "prompt", "Which English meaning matches the sentence?", "A2-24 / prep 5", "placement")
    for iid, item in items.items():
        slug = item["objectiveId"].removeprefix("obj-u24-")
        feedback = f"{item['answer']} — {EXPLANATIONS[item['answer']]}."
        rationales = ["" if x == item["answer"] else f"{x} — {EXPLANATIONS[x]}." for x in item["options"]]
        if iid == "u24-g-verbal-ne-0":
            rationales[1] += " " + REWORD[2876]
        if iid == "u24-i-verbal-ne-1":
            feedback += " " + REWORD[2899]
        set_text(item, "feedback", feedback, "D62d / prep 3 and 5", slug)
        changed(f"{iid}.distractorRationales", item["distractorRationales"], rationales, "D62d / prep 3 and 5")
        item["distractorRationales"] = rationales
        for n, rationale in enumerate(rationales):
            if rationale:
                authored[f"{iid}.rationale{n}"] = slug, rationale
        if item.get("assessment"):
            item["assessment"].update(distractorDifference=rationales, whyAnswerDemonstratesObjective=feedback)

    lines = {
        "OR": (SRC / "units-3-5/openrussian/others.csv").read_text(encoding="utf-8").splitlines(),
        "N": (SRC / "units-3-5/openrussian/nouns.csv").read_text(encoding="utf-8").splitlines(),
        "O": (SRC / "units-21-25/supplemental/otritsatelnye-141256493.txt").read_text(encoding="utf-8").splitlines(),
        "I": (SRC / "units-26-30/ruwiki/neopredelennoe-mestoimenie-149963314.txt").read_text(encoding="utf-8").splitlines(),
    }
    def cell(page, line, field, slug):
        header = lines[page][0].split("\t")
        row = dict(zip(header, lines[page][line - 1].split("\t")))
        raw = row[field]
        filename = "others.csv" if page == "OR" else "nouns.csv"
        return record("prov-ex-or-u10-pronoun-13", f"prov-t18d-u24-{page.lower()}{line}-{field}", slug, raw, stressed(raw),
                      f"{filename}:{row['accented']}#{field}", "formatting")

    evidence = {
        "verbal-ne": ["prov-u24-verbal-ne-rem"],
        "absence": ["prov-u24-verbal-ne-rem", "prov-u24-absence-rem"],
        "alternation": ["prov-u24-alternation-rem", cell("N", 60, "sg_acc", "alternation"),
                        cell("N", 60, "sg_gen", "alternation"), cell("N", 60, "pl_gen", "alternation"),
                        cell("N", 60, "pl_acc", "alternation"), "prov-u24-verbal-ne-rem"],
        "stand-ins": ["prov-u24-stand-ins-rem"] + [cell("OR", n, "translations_en", "stand-ins") for n in (51, 72, 451)],
        "concord": ["prov-u24-concord-rem", "prov-u24-stand-ins-rem"],
        "word-order-contrast": ["prov-u24-word-order-contrast-rem", "prov-u24-context-transfer-native-guide-ru",
                                "prov-u24-placement-rem"],
        "placement": ["prov-u24-placement-rem"],
        "persuasion-reference": ["prov-u24-context-transfer-native-reference-prov-ex-u24-word-order-contrast-0-ru"],
    }
    indefinite = next(r["itemId"] for r in records.values() if r["sourceRevision"] == "149963314" and frozen_record(pack, r))
    raw = "\n".join(lines["I"][14:18])
    evidence["stand-ins"].append(record(indefinite, "prov-t18d-u24-i15-18", "stand-ins", raw, "specific unknown",
                                        "neopredelennoe:" + raw[:80], "selection"))
    typed = {slug: [] for slug in SLUGS}
    for slug, suffix, page, line, key, meaning in TYPED:
        if page == "OR":
            citation = cell(page, line, "accented", slug)
        else:
            if page == "G":
                raw = genitive_ru["originalSourceText"]
                template = genitive_ru["itemId"]
                source_id = genitive_ru["sourceRecordId"]
            else:
                raw = lines["O"][line - 1].strip()
                template = "prov-u24-stand-ins-rem"
                source_id = "otritsatelnye:" + raw[:80]
            if not selectable(raw, key):
                raise ValueError(f"{page}:{line} lacks {key!r}")
            citation = record(template, f"prov-t18d-u24-{page.lower()}{line}-{suffix}", slug, raw, key, source_id, "selection")
        assert records[citation]["shippedText"] == key
        evidence[slug].append(citation)
        item = typed_item(f"u24-i-type-{suffix}", objectives[slug]["id"], "Type the single Russian form described.",
                          meaning, key, "Compare your spelling with the answer.", records[citation]["sourceId"])
        item["typedAnswerKey"]["review"]["reviewer"] = "prepared by Codex (implementer); final audit pending"
        item["keyCitation"] = citation
        pack["items"].append(item)
        typed[slug].append(item["id"])
        changed(item["id"], "", key, f"D62e / {page}:{line}")
        for field in ("prompt", "promptLetter", "feedback"):
            authored[f"{item['id']}.{field}"] = slug, item[field]

    typed_steps, closed_steps = [], []
    for session in unit["sessions"]:
        kept = []
        for st in session["steps"]:
            if st["kind"] != "CHECK":
                kept.append(st)
                continue
            slug = next(o.removeprefix("obj-u24-") for o in session["objectiveIds"] if o.removeprefix("obj-u24-") in SLUGS)
            set_text(st, "body", "Read the context and choose the matching answer.", "A2-24 / D62", slug)
            closed_steps.append(st)
            changed(st["id"], session["id"], "u24-s-checks", "A2-24")
            if typed[slug]:
                new_step = copy.deepcopy(st)
                new_step.update(id=f"u24-st-check-type-{slug}", title="Write the forms", body="Type each form from its description.",
                                itemIds=typed[slug])
                objectives[slug]["checkItemIds"] = typed[slug] + objectives[slug]["checkItemIds"]
                typed_steps.append(new_step)
                changed(new_step["id"], "", ", ".join(typed[slug]), "D62e / A2-24")
        session["steps"] = kept
    unit["sessions"].append(dict(id="u24-s-checks", title="Unit 24 checks", estimatedMinutes=20,
                                 objectiveIds=[objectives[slug]["id"] for slug in SLUGS], steps=typed_steps + closed_steps))
    for slug in SLUGS:
        guided = steps[f"u24-st-guided-{slug}-1"]
        directions = "Read the context and choose the matching answer."
        if typed[slug]:
            directions = "Read the examples, then cover them and write the forms from their meanings. " + directions
        set_text(guided, "body", directions, "D62e / prep 5", slug)
        rem = steps[f"u24-st-rem-{slug}"]
        teach = [steps[s] for s in objectives[slug]["instructionStepIds"] if steps[s]["kind"] == "TEACH"]
        set_text(rem, "body", "\n\n".join(st["body"] for st in teach), "CC021 / D62", slug)
        rem["exampleIds"] = list(dict.fromkeys(i for st in teach for i in st.get("exampleIds", [])))
        for iid in typed[slug]:
            assert next(i["answer"] for i in pack["items"] if i["id"] == iid) in rem["body"]

    set_text(unit, "topicSummary", "Study how Russian expresses negation, absence, and contrast.", "D62 / prep 5", "verbal-ne")
    set_text(unit, "mappingNote", "Lesson 24 in your book, printed pages 294–304, covers negation, case and the position of не.",
             "D62 / prep 5", "verbal-ne")
    set_text(unit["sessions"][0], "title", "Negation", "D62 / prep 5", "verbal-ne")
    set_text(steps["u24-st-orient"], "title", "Negation", "D62 / prep 5", "verbal-ne")
    set_text(steps["u24-st-orient"], "body", "Read the examples, practise writing the forms, and do Lesson 24 in your book.", "D62 / prep 5", "verbal-ne")
    for suffix in ("book-work", "book"):
        set_text(steps[f"u24-st-{suffix}"], "bookDirection", "Do Lesson 24, printed pages 294–304, in the copy you own.", "D62 / prep 5", "book")
    set_text(steps["u24-st-binder-direction"], "body", "Write the forms in your binder, then compare them with the model answer.", "WC011", "book")
    set_text(steps["u24-st-tutor-direction"], "body", "Bring your binder work to your tutor if you have one.", "WC011", "book")
    set_text(steps["u24-st-cumulative-direction"], "body", "Complete your assigned review of earlier lessons before moving on.", "D62 / prep 5", "book")
    intro = ("Review earlier letter pairs and time expressions. After a miss, read the explanation and examples again, "
             "then retry. Finish the assigned review before moving on.")
    changed("review-introduction-24.introduction", pack["reviewCopy"]["introductions"]["24"], intro, "D62 / prep 5")
    pack["reviewCopy"]["introductions"]["24"] = intro
    unit["binderTask"] = dict(id="binder-u24-negation", objectiveIds=[objectives[s]["id"] for s in SLUGS if typed[s]],
                              prompt="Write the Russian forms from their meanings, then compare them with the model answer.",
                              modelAnswer="\n".join(f"{key} — {meaning}" for _, _, _, _, key, meaning in TYPED))
    unit["tutorTask"] = dict(id="tutor-u24-negation", bring=("Bring your binder forms to read aloud. With your tutor, use Lesson 24 "
                            "in your Penguin book to practise when negation affects object case and how to use forms for the absence of a person."))
    for task_key in ("binderTask", "tutorTask"):
        for field in ("prompt", "modelAnswer", "bring"):
            task = unit[task_key]
            if field in task:
                changed(f"{task['id']}.{field}", "", task[field], "WC011")
                authored[f"{task['id']}.{field}"] = "book", task[field]

    attribution = next(r for r in pack["references"] if r["id"] == "ref-u2125-attribution")
    detail = attribution["detail"].replace("Dated name-page claims about social address, foreigners, naming creativity, and the number of names are not shipped. ", "")
    set_text(attribution, "detail", detail, "D62 / shared source notice affects Units 21–25", "word-order-contrast")
    for ident, slugs, title in (
        ("particle", ("verbal-ne", "absence", "alternation"), "The negative particle and absence"),
        ("pronouns", ("stand-ins", "concord"), "Negative pronouns and predicate negation"),
        ("scope", ("word-order-contrast", "placement"), "Negation and context"),
    ):
        ref = dict(id=f"ref-u24-{ident}", title=title, summary=title + " in Russian.",
                   detail="\n\n".join(TEACHING[k] for k in TEACHING if k.rsplit("-", 1)[0] in slugs),
                   introducedInUnit=24, classification="LINGUISTIC_INSTRUCTION", reviewStatus="IMPLEMENTED_PENDING_FINAL_AUDIT",
                   sourceId=records[evidence[slugs[0]][0]]["sourceId"], searchTerms=title.lower().split())
        pack["references"].append(ref)
        for slug in slugs:
            for sid in objectives[slug]["instructionStepIds"]:
                if steps[sid]["kind"] == "TEACH":
                    steps[sid].setdefault("referenceIds", []).append(ref["id"])
        for field in ("title", "summary", "detail"):
            changed(f"{ref['id']}.{field}", "", ref[field], "A2-36")
            authored[f"{ref['id']}.{field}"] = slugs[0], ref[field]

    # Whole remediation copies retain the teaching claims and their unit-local frozen evidence.
    for slug in (*SLUGS, "persuasion-reference"):
        frozen = [r for r in records.values() if r["unitNumber"] == 24 and r["objectiveId"] == objectives[slug]["id"]
                  and not UNSUPPORTED.search(json.dumps(r, ensure_ascii=False)) and frozen_record(pack, r)]
        sources_by_form = {}
        for r in frozen:
            for form in forms(r["originalSourceText"]):
                sources_by_form.setdefault(form, r["itemId"])
        hosts = [steps[sid] for sid in objectives[slug]["remediationStepIds"]]
        hosts += [i for i in items.values() if i["objectiveId"] == objectives[slug]["id"]]
        for host in hosts:
            texts = [host["body"]] if "body" in host else [host["feedback"], *filter(None, host["distractorRationales"])]
            facts = {}
            for text in texts:
                for sentence in sentences(text):
                    if not any(c.isalpha() for c in sentence) or sentence in OPERATING:
                        continue
                    ids = [sources_by_form[form] for form in sorted(forms(sentence)) if form in sources_by_form]
                    facts[sentence] = list(dict.fromkeys(evidence[slug] + ids))
            host["russianFacts"], host["russianFactSources"] = list(facts), facts
            for n, sentence in enumerate(facts):
                changed(f"{host['id']}.russianFacts[{n}]", sentence, ", ".join(facts[sentence]), "D62d / MARK")
                authored[f"{host['id']}.fact{n}"] = slug, sentence

    removed_records = [r for r in pack["provenanceRecords"] if r["unitNumber"] == 24 and UNSUPPORTED.search(json.dumps(r, ensure_ascii=False))]
    assert removed_records and all(
        r["sourceId"] == "src-content-audit-research" or r["itemId"] in {"prov-s1-u24-nekomu", "prov-s1-u24-nekem"}
        for r in removed_records
    )
    remove_entities("provenanceRecords", {r["itemId"] for r in removed_records}, "itemId")
    for r in removed_records:
        changed(f"application:{r['itemId']}", r["shippedText"], "REMOVED", "D62a")
    for i in items.values():
        a = i.get("assessment") or {}
        assert not set(a.get("relationParticipantIds", [])) & {r["itemId"] for r in removed_records}, i["id"]

    inventory = (ROOT / "audit/T15-n0-unmarked-russian-facts.csv").read_text(encoding="utf-8").splitlines()
    for first, last, refs in FACT_RANGES:
        for number in range(first, last + 1):
            _, owner, field, text, protection = next(csv.reader([inventory[number - 1]]))
            if not protection.startswith("English-only"):
                continue
            if number in REWORD:
                finding, replacement = "REWORD", REWORD[number]
            elif number in REMOVE or number in PENDING:
                finding, replacement = "REMOVE", "REMOVED"
            else:
                finding, replacement = "MARK", refs
            changed(f"prep:{number}:{owner}.{field}", text, replacement, f"D62d / {finding}")

    template = records["prov-t15an1-u6-possessive-our-your"]
    shipped = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == 24}
    for owner, (slug, text) in authored.items():
        if not text or text in shipped:
            continue
        r = copy.deepcopy(template)
        ident = "prov-ca-t18d-" + owner.replace(".", "-")
        note = f"English wording authored {DATE} by Codex for {TASK}. Unit 24 fact markers cite separate frozen evidence."
        original = note + "\n" + text
        objective = objectives[slug]["id"]
        r["synthesis"].update(bibliography=note, authorshipDeclaration=f'{note} New text: "{text}"',
                              objectiveOrClaimBinding=f"{objective}:{owner}")
        r.update(itemId=ident, unitNumber=24, objectiveId=objective, sourceRecordId=f"ca-coverage:{objective}:{owner}",
                 shippedText=text, originalSourceText=original, sourceHash=sha(original), contentHash=sha(text),
                 attribution=f"Russian Companion content-audit research synthesis. English wording authored {DATE} for {TASK}.")
        pack["provenanceRecords"].append(r)
        shipped.add(text)

    for key in ("items", "examples", "provenanceRecords"):
        for row in pack[key]:
            if row.get("objectiveId", "").startswith("obj-u24-"):
                assert not UNSUPPORTED.search(json.dumps(row, ensure_ascii=False)), (key, row.get("id") or row.get("itemId"))
    assert not UNSUPPORTED.search(json.dumps(unit, ensure_ascii=False))
    assert sum(len(o["checkItemIds"]) for o in objectives.values()) == 20
    assert sum(map(len, typed.values())) == 8
    return log


def main():
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE:
        raise SystemExit("Expected the exact T18c-2 curriculum baseline")
    pack = loads(text)
    log = apply(pack)
    encoded = encode(pack)
    result = {k: encoded[k] for k in json.loads(text) if k in encoded}
    result.update({k: v for k, v in encoded.items() if k not in result})
    if "--dry" in sys.argv:
        print(f"{len(log)} proposed log rows")
        return
    ASSET.write_text(json.dumps(result, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    with LOG.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.DictWriter(stream, ["task", "finding", "owner.field", "old", "new"], lineterminator="\n")
        writer.writeheader()
        writer.writerows(log)


if __name__ == "__main__":
    main()
