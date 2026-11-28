#!/usr/bin/env python3
"""Apply the Unit 23 Block 4 packet (D57b-e) to ebe03cfe.

The ten production keys are selections from the frozen Pressbooks chapter.
The five closed contrasts keep their existing assessment evidence and options.
"""
from __future__ import annotations

import copy
import csv
import hashlib
import json
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from apply_d7b_selection_records import displayed, frozen_record, selectable
from apply_t14b_units_1_2_owner import typed_item
from apply_t17a_unit_16 import MARKUP, sentences, stressed
from curriculum_storage import encode, loads

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
LOG = ROOT / "audit/T18c-2-unit23-changes.csv"
SRC = ROOT / "catalog/sources"
TASK, DATE = "T18c-2", "2026-11-28"
BASELINE = "07d1f99dcd4a43762e22845e77544a6c67908a8a289e4d4bce109963ddd05382"
SLUGS = ("ranges", "beginning", "during-event", "duration", "elapsed")
MARK_RANGES = dict(zip(SLUGS, ("2713–2729 / U:212–222", "2732–2750 / U:306–311, N:23,383,700",
                             "2753–2780 / U:313–318", "2783–2810 / U:320–325, VA:12",
                             "2813–2843 / U:387–408, G:21–25")))
TYPED = (
    ("ranges", "sixties", 215, "В шестидесятых годах", "in the 1960s"),
    ("ranges", "nineties", 216, "В девяностых годах", "in the 1990s"),
    ("beginning", "end-year", 309, "В конце года", "at the end of the year"),
    ("beginning", "start-century", 310, "В начале века", "at the beginning of the century"),
    ("beginning", "middle-week", 311, "В середине недели", "in the middle of the week"),
    ("during-event", "during-war", 317, "во время войны", "during the war"),
    ("elapsed", "before-dinner", 390, "Перед ужином", "before dinner"),
    ("elapsed", "after-work", 396, "После работы", "after work"),
    ("elapsed", "in-year", 402, "через год", "in a year"),
    ("elapsed", "year-ago", 408, "год назад", "a year ago"),
)
DECADE = ("Assume the twentieth century. Type the full expression with the preposition meaning in, the ordinal numeral "
          "in the prepositional plural and the noun for years in the prepositional plural.")
PROMPT = "Type the complete Russian time expression for this meaning."
PRODUCTION = "Say each time expression aloud, then cover the examples and write each expression from its meaning."
FEEDBACK = "Compare your spelling with the answer."
CENTURY = "For these examples, assume the twentieth century."
DURING_NOTE = "For the time phrase meaning during the war, write во время войны."
AGO_NOTE = "In the example, the time span comes before the word for ago."
GENERIC = " This is a different time relation, interval, event or completion claim from the requested meaning."
REMOVE_TEACH = {
    "ranges-1": ("The two displayed choices distinguish the sixties from the nineties.",),
    "ranges-2": ("Read the decade word, not just the shared ending for years.",
                 "The checks recognize these two supplied ranges; they do not test forming a new decade expression."),
    "beginning-3": ("The checks match complete supplied expressions, not independently produced period-and-position combinations.",),
    "during-event-2": ("The supplied translations do not demonstrate constructing every during-an-event sentence.",),
    "duration-2": ("These checks identify supplied meanings, not task-completion time.",),
    "elapsed-1": ("The English labels classify only this time relation, not the full sentence translation.",),
    "elapsed-3": ("Ago follows an accusative time expression.",
                  "The choices classify the shown time relation, not independently produce the whole sentence."),
}
BOOK_NOTICE = "Book work stays in the copy you own."


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def replace(text: str, old: str, new: str) -> str:
    if text.count(old) != 1:
        raise ValueError(f"Expected one occurrence of {old!r}")
    return text.replace(old, new)


def apply(pack: dict) -> list[dict]:
    unit = pack["units"][22]
    assert unit["number"] == 23
    objectives = {o["id"].removeprefix("obj-u23-"): o for o in unit["objectives"]}
    steps = {st["id"]: st for s in unit["sessions"] for st in s["steps"]}
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    u_lines = (SRC / "units-21-25/pressbooks/usa-grammar.html").read_text(encoding="utf-8").splitlines()
    g_lines = (SRC / "units-26-30/wikipedia/russian-grammar-1369617627.txt").read_text(encoding="utf-8").splitlines()
    log = []
    coverage_pending = []

    def log_change(owner: str, old, new, finding: str):
        if old != new:
            log.append(dict(task=TASK, finding=finding, **{"owner.field": owner}, old=old, new=new))

    def set_field(host: dict, field: str, value, finding: str, slug="ranges"):
        old = host.get(field, "")
        host[field] = value
        name = f"{host['id']}.{field}"
        log_change(name, old, value, finding)
        if isinstance(value, str) and value != old and value:
            coverage_pending.append((name, slug, value))

    def source(template: str, ident: str, slug: str, raw: str, shown: str, source_record_id: str, **fields) -> str:
        record = copy.deepcopy(records[template])
        record.update(itemId=ident, unitNumber=23, objectiveId=objectives[slug]["id"], sourceRecordId=source_record_id,
                      originalSourceText=raw, shippedText=shown, sourceHash=sha(raw), contentHash=sha(shown), **fields)
        if ident in records or not frozen_record(pack, record):
            raise ValueError(f"Invalid frozen record {ident}")
        records[ident] = record
        pack["provenanceRecords"].append(record)
        return ident

    def pressbooks(template: str, slug: str, first: int, last: int, shown: str, purpose: str) -> str:
        raw = "\n".join(u_lines[first - 1:last])
        if not selectable(raw, shown):
            raise ValueError(f"U:{first}–{last} does not display {shown!r}")
        return source(template, f"prov-t18c2-u23-{purpose}", slug, raw, shown, "usa-grammar:" + raw[:80])

    evidence = {}
    for slug, (first, last, line, shown) in zip(SLUGS, (
        (212, 222, 215, "in the 1960s"),
        (306, 311, 309, "at the end of the year"),
        (313, 318, 317, "She was born during the war."),
        (320, 325, 324, "Throughout the lesson, the students worked on the project."),
        (387, 408, 388, "before"),
    )):
        assert shown in displayed(u_lines[line - 1])
        evidence[slug] = [pressbooks(f"prov-u23-{slug}-rem", slug, first, last, shown, f"{slug}-evidence")]

    noun_lines = (SRC / "units-3-5/openrussian/nouns.csv").read_text(encoding="utf-8").splitlines()
    columns = noun_lines[0].split("\t")
    for number in (23, 383, 700):
        row = dict(zip(columns, noun_lines[number - 1].split("\t")))
        original = row["sg_prep"]
        evidence["beginning"].append(source(
            "prov-ex-or-u10-pronoun-13", f"prov-t18c2-u23-n{number}", "beginning", original, stressed(original),
            f"nouns.csv:{row['accented']}#sg_prep", recordType="lexical record", transformation="formatting"))
    aspect = records["prov-t16b-u12-b12"]
    evidence["duration"].append(source(
        aspect["itemId"], "prov-t18c2-u23-va12", "duration", aspect["originalSourceText"],
        aspect["originalSourceText"].split("  ", 1)[0], aspect["sourceRecordId"]))

    correction_ids = []
    for number in (21, 23, 25):
        raw = g_lines[number - 1].strip()
        shown = max((part.strip(" *:#-") for part in MARKUP.split(raw) if part.strip(" *:#-")), key=len)
        correction_ids.append(source("prov-ex-u22-collective-0-ru", f"prov-t18c2-u23-g{number}", "elapsed",
                                     raw, shown, "grammar:" + raw[:80], recordType="instruction", transformation="selection"))
    raw = g_lines[23].strip()
    source("prov-ex-u22-collective-0-ru", "prov-t18c2-u23-tutor-preposition", "book", raw, "за",
           "grammar:" + raw[:80], recordType="lexical record", transformation="selection")

    for suffix, removals in REMOVE_TEACH.items():
        slug = suffix.rsplit("-", 1)[0]
        step = steps[f"u23-st-teach-{suffix}"]
        body = step["body"]
        for old in removals:
            log_change(f"{step['id']}.body", old, "REMOVED", "D57b / 2833" if old.startswith("Ago follows") else "prep 3/5 REMOVE")
            body = replace(body, " " + old, "")
        set_field(step, "body", body, "prep 3/5 REMOVE", slug)
    step = steps["u23-st-teach-ranges-2"]
    set_field(step, "body", replace(step["body"], "Here the supplied English glosses assume the twentieth century.", CENTURY),
              "prep 3 / 2720 REWORD")
    step = steps["u23-st-teach-elapsed-3"]
    set_field(step, "body", step["body"] + " " + AGO_NOTE, "D57b / retain U:407–408 word order", "elapsed")
    step = steps["u23-st-teach-during-event-2"]
    set_field(step, "body", step["body"] + " " + DURING_NOTE, "D57e / U:317 production", "during-event")

    closed_items = [i for i in pack["items"] if i["objectiveId"] in {o["id"] for o in objectives.values()}]
    for item in closed_items:
        slug = item["objectiveId"].removeprefix("obj-u23-")
        feedback = item["feedback"].split("\n", 1)[1]
        rationale = replace(item["distractorRationales"][1], GENERIC, "")
        if slug == "ranges":
            feedback = replace(feedback, "with the twentieth century assumed in this supplied gloss", "in the twentieth-century context here")
            rationale = replace(rationale, "with the twentieth century assumed in this supplied gloss", "in the twentieth-century context here")
        set_field(item, "feedback", feedback, "D57d / prep 5 direct grammar feedback", slug)
        log_change(f"{item['id']}.distractorRationales[1]", item["distractorRationales"][1], rationale, "prep 3 REMOVE repeated sentence")
        item["distractorRationales"][1] = rationale
        item["assessment"]["distractorDifference"][1] = rationale
        item["assessment"]["whyAnswerDemonstratesObjective"] = feedback
        coverage_pending.append((f"{item['id']}.distractorRationales[1]", slug, rationale))

    types_by_slug = {slug: [] for slug in SLUGS}
    keys_by_slug = {slug: [] for slug in SLUGS}
    for slug, suffix, number, key, meaning in TYPED:
        assert "\u0301" not in key
        cite = pressbooks(f"prov-u23-{slug}-rem", slug, number, number, key, f"u{number}-key")
        item = typed_item(f"u23-i-type-{suffix}", objectives[slug]["id"], DECADE if slug == "ranges" else PROMPT,
                          meaning, key, FEEDBACK, records[cite]["sourceId"])
        item["typedAnswerKey"]["review"]["reviewer"] = "prepared by Codex (implementer); final audit pending"
        item["keyCitation"] = cite
        pack["items"].append(item)
        types_by_slug[slug].append(item["id"])
        keys_by_slug[slug].append(key)
        log_change(item["id"], "", key, f"D57e / U:{number} / {meaning}")
        for field in ("prompt", "promptLetter", "feedback"):
            coverage_pending.append((f"{item['id']}.{field}", slug, item[field]))

    closed_steps, typed_steps = [], []
    for session in unit["sessions"]:
        kept = []
        for step in session["steps"]:
            if step["kind"] != "CHECK":
                kept.append(step)
                continue
            slug = session["objectiveIds"][0].removeprefix("obj-u23-")
            body = "Identify the time relationship expressed." if slug == "elapsed" else "Read the Russian and identify its meaning."
            set_field(step, "body", body, "prep 3 / 2822 REWORD" if slug == "elapsed" else "D57d / prep 5", slug)
            closed_steps.append(step)
            log_change(step["id"], session["id"], "u23-s-checks", "A2-24")
            typed_ids = types_by_slug[slug]
            if typed_ids:
                model = copy.deepcopy(step)
                model.update(id=f"u23-st-check-type-{slug}", title="Write the time expressions",
                             body="Type each expression from its meaning.", itemIds=typed_ids)
                typed_steps.append(model)
                objectives[slug]["checkItemIds"] = typed_ids + objectives[slug]["checkItemIds"]
                log_change(model["id"], "", "u23-s-checks / " + ", ".join(typed_ids), "D57e / A2-24")
                for field in ("title", "body"):
                    coverage_pending.append((f"{model['id']}.{field}", slug, model[field]))
        session["steps"] = kept
    unit["sessions"].append(dict(id="u23-s-checks", title="Unit 23 checks", estimatedMinutes=20,
                                 objectiveIds=[objectives[slug]["id"] for slug in SLUGS], steps=typed_steps + closed_steps))
    coverage_pending.append(("u23-s-checks.title", "ranges", "Unit 23 checks"))

    for slug in SLUGS:
        guided = steps[f"u23-st-guided-{slug}-1"]
        body = "Identify the time relationship expressed." if slug == "elapsed" else "Read the Russian and identify its meaning."
        if types_by_slug[slug]:
            body = f"{PRODUCTION}\n\n{body}"
        set_field(guided, "body", body, "D57d/e / prep 5 production before retrieval", slug)
        teach = [steps[s] for s in objectives[slug]["instructionStepIds"] if steps[s]["kind"] == "TEACH"]
        rem = steps[f"u23-st-rem-{slug}"]
        set_field(rem, "body", "\n\n".join(st["body"] for st in teach), "prep 3 / CC021", slug)
        rem["exampleIds"] = list(dict.fromkeys(e for st in teach for e in st.get("exampleIds", [])))
        for key in keys_by_slug[slug]:
            assert key in rem["body"], (slug, key)

    summary = "Study how Russian expressions place an event in a decade, within a period or in relation to another event."
    set_field(unit, "topicSummary", summary, "D57d / prep 5")
    set_field(unit, "mappingNote", "Lesson 23 in your book, printed pages 283–293, covers time expressions including completion over a period.",
              "D57d / prep 5")
    set_field(unit["sessions"][0], "title", "Time expressions", "D57d / prep 5")
    orient = steps["u23-st-orient"]
    set_field(orient, "title", "Time expressions", "D57d / prep 5")
    set_field(orient, "body", "Read the examples, practise writing the time expressions, and do Lesson 23 in your book.", "D57d / prep 5")
    book_direction = "Do Lesson 23, printed pages 283–293, in the copy you own."
    for sid in ("book-work", "book"):
        set_field(steps[f"u23-st-{sid}"], "bookDirection", book_direction, "D57d / prep 5", "book")
    # Keep the canonical D8 self-attestation and binder self-check notices.
    set_field(steps["u23-st-binder-direction"], "body", "Write the time expressions in your binder, then compare them with the model answer.",
              "D57d / binder direction", "book")
    set_field(steps["u23-st-tutor-direction"], "body", "Bring your binder work to your tutor if you have one.",
              "D57d / optional tutor", "book")
    set_field(steps["u23-st-cumulative-direction"], "body", "Complete your assigned review of earlier lessons before moving on.",
              "D57d / cumulative review", "book")
    review_intro = ("Review earlier letter pairs and quantity phrases. After a miss, read the explanation and examples again, "
                    "then retry. Finish the assigned review before moving on.")
    log_change("review-introduction-23.introduction", pack["reviewCopy"]["introductions"]["23"], review_intro, "D57d / prep 5")
    pack["reviewCopy"]["introductions"]["23"] = review_intro

    binder = dict(id="binder-u23-time", objectiveIds=[objectives[s]["id"] for s in SLUGS],
                  prompt="Write the ten time expressions in your binder from their English meanings, then compare them with the model answer.",
                  modelAnswer="\n".join(f"{key} — {meaning}" for _, _, _, key, meaning in TYPED))
    tutor = dict(id="tutor-u23-time",
                 bring=("Bring your binder time expressions to read aloud and explain. With your tutor, use Lesson 23 of the Penguin "
                        "book to practise expressing completion within a period with за and a perfective verb."))
    unit["binderTask"], unit["tutorTask"] = binder, tutor
    for host, fields in ((binder, ("prompt", "modelAnswer")), (tutor, ("bring",))):
        for field in fields:
            log_change(f"{host['id']}.{field}", "", host[field], "WC011 / D57d")
            coverage_pending.append((f"{host['id']}.{field}", "book", host[field]))

    specs = (
        ("calendar", ("ranges", "beginning"), "Decades and parts of a period", "Place events in a decade or part of a period."),
        ("interval", ("during-event", "duration"), "Events and intervals", "Distinguish when something happens from the interval over which it happens."),
        ("relative", ("elapsed",), "Before, after, in a year and a year ago", "Place an event relative to another event or time."),
    )
    attribution = next(r for r in pack["references"] if r["id"] == "ref-u2125-attribution")
    set_field(attribution, "detail", replace(attribution["detail"], BOOK_NOTICE + " ", ""), "D57d / shared reference Units 21–25")
    for name, slugs, title, description in specs:
        reference = dict(id=f"ref-u23-{name}", title=title, summary=description,
                         detail="\n\n".join(steps[s]["body"] for slug in slugs for s in objectives[slug]["instructionStepIds"]
                                             if steps[s]["kind"] == "TEACH"),
                         sourceId=records[f"prov-u23-{slugs[0]}-rem"]["sourceId"], introducedInUnit=23,
                         classification="LINGUISTIC_INSTRUCTION", reviewStatus="IMPLEMENTED_PENDING_FINAL_AUDIT",
                         searchTerms=title.lower().replace(",", "").split())
        pack["references"].append(reference)
        for slug in slugs:
            for sid in objectives[slug]["instructionStepIds"]:
                if steps[sid]["kind"] == "TEACH":
                    steps[sid].setdefault("referenceIds", []).append(reference["id"])
        for field in ("title", "summary", "detail"):
            log_change(f"{reference['id']}.{field}", "", reference[field], "A2-36")
            coverage_pending.append((f"{reference['id']}.{field}", slugs[0], reference[field]))

    # As in Unit 22, the remediation repeats the teaching verbatim and hosts its fact markers.
    for slug in SLUGS:
        hosts = [steps[sid] for sid in objectives[slug]["remediationStepIds"]]
        hosts += [i for i in closed_items if i["objectiveId"] == objectives[slug]["id"]]
        for host in hosts:
            texts = [host["body"]] if "body" in host else [host["feedback"], host["distractorRationales"][1]]
            facts = {}
            for text in texts:
                for sentence in sentences(text):
                    if not any(c.isalpha() for c in sentence) or sentence in ("Match the whole sentence:", CENTURY):
                        continue
                    refs = evidence[slug]
                    if "takes accusative, not genitive" in sentence:
                        refs = correction_ids
                    facts[sentence] = refs
            host["russianFacts"] = list(facts)
            host["russianFactSources"] = facts
            for n, sentence in enumerate(facts):
                log_change(f"{host['id']}.russianFacts[{n}]", sentence, ", ".join(facts[sentence]), f"MARK {MARK_RANGES[slug]}")
                coverage_pending.append((f"{host['id']}.fact{n}", slug, sentence))

    # Cover authored wording with a separate research record. Facts above cite frozen evidence, not these records.
    template = records["prov-t15an1-u6-possessive-our-your"]
    shipped = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == 23}
    for owner, (slug, text) in {owner: (slug, text) for owner, slug, text in coverage_pending}.items():
        if text in shipped:
            continue
        item_id = f"prov-ca-t18c2-{owner.replace('[', '-').replace(']', '').replace('.', '-')}"
        note = f"English wording authored {DATE} by Codex for {TASK}. The Unit 23 fact markers cite the frozen evidence."
        record = copy.deepcopy(template)
        original = note + "\n" + text
        objective = objectives[slug]["id"]
        record["synthesis"].update(bibliography=note, authorshipDeclaration=f'{note} New text: "{text}"',
                                   objectiveOrClaimBinding=f"{objective}:{owner}")
        record.update(itemId=item_id, unitNumber=23, objectiveId=objective, sourceRecordId=f"ca-coverage:{objective}:{owner}",
                      shippedText=text, originalSourceText=original, sourceHash=sha(original), contentHash=sha(text),
                      attribution=f"Russian Companion content-audit research synthesis. English wording authored {DATE} for {TASK}.")
        if item_id in records:
            raise ValueError(f"Duplicate wording record {item_id}")
        records[item_id] = record
        pack["provenanceRecords"].append(record)
        shipped.add(text)
    return log


def main():
    baseline = ASSET.read_text(encoding="utf-8")
    if sha(baseline) != BASELINE:
        raise SystemExit("Expected the exact T18c-1 curriculum baseline")
    pack = loads(baseline)
    log = apply(pack)
    encoded = encode(pack)
    result = {key: encoded[key] for key in json.loads(baseline) if key in encoded}
    result.update({key: value for key, value in encoded.items() if key not in result})
    if "--dry" in sys.argv:
        print(f"{len(log)} change rows; asset not written")
        return
    ASSET.write_text(json.dumps(result, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    with LOG.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.DictWriter(stream, ["task", "finding", "owner.field", "old", "new"], lineterminator="\n")
        writer.writeheader()
        writer.writerows(log)


if __name__ == "__main__":
    main()
