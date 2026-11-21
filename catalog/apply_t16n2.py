#!/usr/bin/env python3
"""T16-n2 (owner decision D30; audit/prep/t16n1-t16b-codex-check-at-62b4b084.txt items 3 and 9): learner-text fixes.

Exact-baseline transformer; runs once on the D29a pack (9c07d59f, unchanged since T16b).
The widened learner lint (V2DesignDiagnostics.workflowAndFraming) reports review/workflow status, table/record framing and
question framing. Every Units 1-12 hit, and every letter note, is rewritten as direct teaching or a plain instruction:
- workflow and release status leaves learner text (it stays in mappingStatus, reviewStatus and the status UI);
- "requested ... form", "row", "table", "recorded ... form", "keyed", "from the dictionary" and "the question here is"
  framing is replaced by the grammatical distinction itself.
No Russian changes: every substitution below is English only and is checked to leave each Cyrillic region untouched.
Marked sentences keep their frozen records: a changed russianFacts sentence moves its russianFactSources entry unchanged.
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

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "9c07d59f7305c435289c831c770ee9f9210c396fb1b23690a1df7ceb2e252af9"  # the D29a pack (adbc7a80)
TASK, DATE = "T16-n2", "2026-11-21"
# D31c: the Codex learner-text sweep, copied verbatim from owner/prep-reports df4e5044
# (audit/prep/learner-text-sweep-units-01-15.csv, blob 9c2d500d). Its Units 1-12 rows are applied first; Units 13-15 rows go
# to their unit tasks. reviewStatus rows are met by the D31b UI label mapping (the metadata stays unchanged).
SWEEP = ROOT / "audit/T16-n2-learner-text-sweep-units-01-15.csv"
SWEEP_SHA256 = "9517b59405d1d5931edd71041bb4d05d18faa2cfcf171d87d13d0fac03f050fd"
# Sweep rows whose suggested wording is adjusted to keep a marker, a pinned tuple or a frozen binding (listed in the report).
ADJUSTED: dict[tuple[str, str], str] = {
    # The explicit-target rule needs "rejected spelling" in the rationale (TARGET_PHRASES[REJECTED_SPELLING]);
    # the suggested "is incorrect spelling." drops it.
    ("u12-i-spelling-2.feedback", "хорошое письмо is the rejected spelling requested by this question."):
        "хорошое письмо is the rejected spelling.",
}
MAX_UNIT = 12

# Exact English substitutions, applied in order to every learner field, its assessment mirrors and its fact keys.
SUBS = [
    # (a) review and workflow status
    (" Qualified review and the owned-edition mapping are still pending.", ""),
    ("Content-complete development unit for printed letters:", "This unit covers printed letters:"),
    ("Content-complete development unit after Unit 1:", "This unit follows Unit 1:"),
    ("Proposed Unit 3 is a development unit awaiting qualified review. It covers", "Unit 3 covers"),
    ("Proposed Unit 4 is a development unit awaiting qualified review. It covers", "Unit 4 covers"),
    ("Proposed Unit 5 is a development unit awaiting qualified review. It covers", "Unit 5 covers"),
    (" It is not installed and the owned-edition mapping is not signed.", ""),
    ("This is a proposed Lesson 1 mapping, not an approved owned-edition alignment. The development unit teaches",
     "This unit goes with Lesson 1 in your book. It teaches"),
    (" English sound notes are pending qualified review.", ""),
    (" English sound notes are pending review.", ""),
    ("Detailed phonetics remain in the book until a reviewer signs an app note.", "For detailed phonetics, use your book."),
    ("Word, phrase, and sign reading wait for reviewed examples.", "Practise reading words, phrases, and signs in your book."),
    (" Public release stays blocked until review and device evidence exist.", ""),
    ("Some units contain proposed lesson bodies that still require qualified review.",
     "Course status shows which units have a lesson body in this app."),
    (" This reference does not approve the packaged records.", ""),
    # (b) table and record framing
    ("CC BY-SA 4.0 dictionary rows", "CC BY-SA 4.0 dictionary data"),
    ("Bounded practice for completion over a period is implemented and review-pending. It is not approved coverage of every temporal clause.",
     "Practice for completion over a period covers the bounded examples here, not every temporal clause."),
    ("Two speaker rows", "Two speaker forms"), ("Two addressee rows", "Two addressee forms"),
    ("Four other-person rows", "Four other-person forms"),
    ("Written stress mark from the dictionary", "Written stress mark"),
    ("The plural ending differs from the table pair and", "The plural ending differs from the pair meaning table, and"),
    ("Read the requested form before answering.", "Read the prompt before answering."),
    ("Identify number from the requested form and context.", "Identify number from the form and its context."),
    ("Choose the requested form.", "Choose the correct form."),
    ("Choose the requested singular or plural nominative form.", "Choose the correct singular or plural nominative form."),
    ("Choose the requested adjective form.", "Choose the correct adjective form."),
    ("Use the requested case or the supplied context to decide", "Use the case or the supplied context to decide"),
    ("Keep the noun and the requested agreement in view.", "Keep the noun and its agreement in view."),
    ("Use the requested gender, number and case to decide the answer.", "Use the gender, number and case of the phrase to decide the answer."),
    ("The requested plural is a number distinction,", "The plural is a number distinction,"),
    ("and choose the requested function, supplied meaning or noun case.", "and choose the matching function, supplied meaning or noun case."),
    ("The phrase requested here is the nominative subject form.", "The correct phrase is the nominative subject form."),
    ("; the requested phrase is the nominative subject phrase.", "; the correct phrase is the nominative subject phrase."),
    ("Choose the requested sentence.", "Choose the correct sentence."),
    ("Choose the past form with the feminine singular agreement requested here.", "Choose the past form with feminine singular agreement."),
    ("Check the requested gender and number.", "Check the gender and number."),
    ("Choose the requested person and number.", "Choose the correct person and number."),
    ("; choose the plural line requested here.", "; choose the plural line."),
    ("Knowing the English translation alone does not answer the requested role.",
     "Knowing the English translation alone does not show the noun's role."),
    ("The requested noun is the subject here;", "This noun is the subject here;"),
    ("The cue shows the feminine singular form, but the requested form is masculine singular of the same verb.",
     "The cue shows the feminine singular form, but the answer is the masculine singular form of the same verb."),
    ("The cue shows feminine singular, but the requested form is plural;", "The cue shows feminine singular, but the answer is plural;"),
    ("is the second-person plural future row: the auxiliary agrees with you in that row.",
     "is the second-person plural future: the auxiliary agrees with you."),
    ("changes the person as well as the table number.", "changes the person as well as the number."),
    ("not the requested second-person plural future row.", "not the second-person plural future."),
    ("of the perfective verb in this table, agreeing with we.", "of the perfective verb taught here, agreeing with we."),
    ("is the recorded dative singular form of", "is the dative singular form of"),
    ("The requested row changes case while keeping the noun singular;", "The dative singular changes case while keeping the noun singular;"),
    ("the neuter form requested here is also the form used", "the neuter form is also the form used"),
    ("is the rejected spelling requested by this question.", "is the rejected spelling."),
    ("The keyed counterexample is incorrect Russian,", "This counterexample is incorrect Russian,"),
    ("is the recorded prepositional plural form of", "is the prepositional plural form of"),
    # (c) question framing
    ("is useful, but the question here is whether the noun is the subject or the direct object.",
     "is useful, but also decide whether the noun is the subject or the direct object."),
]
# Distractor-rationale formulas ("X is the A form, so it does not supply the requested B form.").
RULES = [
    (re.compile(r", so it does not (?:supply|provide) the requested ((?:[\w-]+ ){0,4})form\."), r", not the \1form."),
    (re.compile(r"\bnot the requested "), "not the "),
    (re.compile(r"\brather than the requested "), "rather than the "),
    (re.compile(r"\b(not|rather than) the ([^.]*?) requested here\."), r"\1 the \2."),
]
LETTER_STATUS = " Pending review."

# Mirror of V2DesignDiagnostics.workflowAndFraming (kept identical; checked after the rewrite).
LINT = re.compile(
    r"\bpending (?:qualified )?review\b|\bqualified review\b|\breviewed examples\b|\breviewers?\b|\bapprov(?:e|es|ed|ing)\b|"
    r"\bsigns? an app note\b|\brelease (?:stays|remains|is) blocked\b|\brows?\b(?! for\b)|"
    r"\b(?:in|from|of) (?:this|the|that) table\b|\bthis table\b|\btable (?:rows?|numbers?|cells?|fields?|positions?)\b|"
    r"\brecorded (?:\w+ ){0,3}forms?\b|\brequested (?:[\w-]+ ){0,4}(?:forms?|rows?)\b|\bthe requested\b|\brequested here\b|"
    r"\bkeyed\b|\bcells?\b|\bfrom the dictionary\b|\bthe question here is\b|\brequested by (?:this|the) question\b",
    re.IGNORECASE,
)
CYRILLIC = re.compile("[Ѐ-ӿ][Ѐ-ӿ́]*")


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


USED: set[str] = set()


def fix(text: str) -> str:
    for old, new in SUBS:
        if old in text:
            USED.add(old)
            text = text.replace(old, new)
    for rx, new in RULES:
        text = rx.sub(new, text)
    return text


def apply_sweep(pack: dict, coverage: list, log: list, units: set[int] | None = None) -> None:
    """Apply the sweep rows of `units` (default: Units 1-12; T16c passes {13})."""
    if hashlib.sha256(SWEEP.read_bytes()).hexdigest() != SWEEP_SHA256:
        raise SystemExit("Sweep CSV changed")
    unit_of = {o["id"]: u["number"] for u in pack["units"] for o in u["objectives"]}
    owners = {}
    for u in pack["units"]:
        owners[u["id"]] = (u, u["number"], u["objectives"][0]["id"])
        for o in u["objectives"]:
            owners[o["id"]] = (o, u["number"], o["id"])
        for se in u["sessions"]:
            owners[se["id"]] = (se, u["number"], u["objectives"][0]["id"])
            for st in se["steps"]:
                owners[st["id"]] = (st, u["number"], st.get("objectiveId") or u["objectives"][0]["id"])
    first = {u["number"]: u["objectives"][0]["id"] for u in pack["units"]}
    for item in pack["items"]:
        owners[item["id"]] = (item, unit_of.get(item["objectiveId"]), item["objectiveId"])
    for e in pack["examples"]:
        owners[e["id"]] = (e, unit_of.get(e.get("objectiveId")), e.get("objectiveId"))
    for r in pack["references"]:
        owners[r["id"]] = (r, r.get("introducedInUnit"), first.get(r.get("introducedInUnit")))
    for l in pack["letters"]:
        owners[l["id"]] = (l, None, None)
    done_global = set()
    for row in csv.DictReader(SWEEP.open(encoding="utf-8", newline="")):
        number = int(row["unit"])
        if (number not in units if units else number > MAX_UNIT) or row["owner.field"].endswith(".reviewStatus"):
            continue
        ref, old, new = row["owner.field"], row["current text"], row["suggested rewrite"]
        new = ADJUSTED.get((ref, old), new)
        m = re.fullmatch(r"reviewCopy\.(messages|introductions)\[(\w+)\]", ref)
        if m:
            host, key, unit, objective, mirrors = pack["reviewCopy"][m.group(1)], m.group(2), None, None, []
            if (ref, old) in done_global:
                continue
            done_global.add((ref, old))
        else:
            owner, field = ref.split(".", 1)
            host_obj, unit, objective = owners[owner]
            fm = re.fullmatch(r"(\w+)(?:\[(\d+)\])?", field)
            if fm.group(2) is None:
                host, key = host_obj, fm.group(1)
            else:
                host, key = host_obj[fm.group(1)], int(fm.group(2))
            assessment = host_obj.get("assessment") or {}
            mirrors = []
            if field == "feedback" and "whyAnswerDemonstratesObjective" in assessment:
                mirrors.append((assessment, "whyAnswerDemonstratesObjective"))
            if fm.group(1) == "distractorRationales" and key < len(assessment.get("distractorDifference", [])):
                mirrors.append((assessment["distractorDifference"], key))
        value = host[key]
        if value.count(old) != 1:
            if old not in value and new in value:
                continue  # an earlier row of the same field already applied it
            raise SystemExit(f"{ref}: sweep text occurs {value.count(old)} times: {old!r}")
        if CYRILLIC.findall(new) != CYRILLIC.findall(old):
            raise SystemExit(f"{ref}: the suggested rewrite changes Russian: {old!r} -> {new!r}")
        host[key] = value.replace(old, new)
        log.append((ref, old, new))
        for mh, mk in mirrors:
            if mh[mk].count(old) == 1:
                mh[mk] = mh[mk].replace(old, new)
        if not m:
            facts = host_obj.get("russianFacts") or []
            if any(old in f for f in facts):
                sources = host_obj["russianFactSources"]
                moved, new_sources = [], {}
                for f in facts:
                    g = f.replace(old, new) if old in f else f
                    if g not in moved:
                        moved.append(g)
                    new_sources[g] = list(dict.fromkeys(new_sources.get(g, []) + sources[f]))
                    if g != f:
                        coverage.append((unit, objective, f"{owner}-sweep-fact-{len(coverage)}", g))
                host_obj["russianFacts"] = moved
                host_obj["russianFactSources"] = {f: new_sources[f] for f in moved}
            if unit is not None and objective and (field in ("feedback", "body", "detail") or field.startswith("distractorRationales")):
                coverage.append((unit, objective, f"{owner}-sweep-{field.replace('[', '-').replace(']', '')}", host[key]))


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T16-n2 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    units = {u["number"]: u for u in pack["units"]}
    unit_of = {o["id"]: u["number"] for u in pack["units"] for o in u["objectives"]}
    changes = []  # (owner, field, old, new)
    coverage = []  # (unit, objective, key, text)
    sweep_log = []
    apply_sweep(pack, coverage, sweep_log)

    def change(owner: str, field: str, unit, host: dict, key: str, objective=None, cover: bool = False):
        old = host.get(key)
        if not isinstance(old, str):
            return
        new = fix(old)
        if new == old:
            return
        if CYRILLIC.findall(new) != CYRILLIC.findall(old):
            raise SystemExit(f"{owner}.{field}: Russian changed")
        host[key] = new
        changes.append((owner, field, old, new))
        if cover and objective:
            coverage.append((unit, objective, f"{owner}-{field}", new))

    def move_facts(owner: str, host: dict, unit, objective):
        facts = host.get("russianFacts")
        if not facts:
            return
        sources = host["russianFactSources"]
        new_facts, new_sources = [], {}
        for fact in facts:
            new = fix(fact)
            if new != fact and CYRILLIC.findall(new) != CYRILLIC.findall(fact):
                raise SystemExit(f"{owner}: fact Russian changed")
            if new != fact:
                coverage.append((unit, objective, f"{owner}-fact-{len(new_facts)}", new))
            if new not in new_facts:
                new_facts.append(new)
            new_sources[new] = list(dict.fromkeys(new_sources.get(new, []) + sources[fact]))
        if new_facts != facts:
            host["russianFacts"] = new_facts
            host["russianFactSources"] = {f: new_sources[f] for f in new_facts}

    for n, unit in sorted(units.items()):
        if n > MAX_UNIT:
            continue
        for key in ("title", "shortLabel", "topicSummary", "mappingNote"):
            change(unit["id"], key, n, unit, key)
        for objective in unit["objectives"]:
            change(objective["id"], "title", n, objective, "title")
        for session in unit["sessions"]:
            change(session["id"], "title", n, session, "title")
            for step in session["steps"]:
                objective = step.get("objectiveId") or unit["objectives"][0]["id"]
                for key in ("title", "bookDirection", "companionDirection", "unavailableReason", "dominantAction"):
                    change(step["id"], key, n, step, key)
                change(step["id"], "body", n, step, "body", objective, cover=True)
                move_facts(step["id"], step, n, objective)
    for example in pack["examples"]:
        n = unit_of.get(example.get("objectiveId"))
        if n is not None and n <= MAX_UNIT:
            change(example["id"], "note", n, example, "note")
            change(example["id"], "display", n, example, "display")
    for item in pack["items"]:
        n = unit_of.get(item["objectiveId"])
        if n is None or n > MAX_UNIT:
            continue
        change(item["id"], "prompt", n, item, "prompt")
        change(item["id"], "feedback", n, item, "feedback", item["objectiveId"], cover=True)
        assessment = item.get("assessment") or {}
        if "whyAnswerDemonstratesObjective" in assessment:
            change(item["id"], "assessment.whyAnswerDemonstratesObjective", n, assessment, "whyAnswerDemonstratesObjective")
        for j in range(len(item.get("distractorRationales", []))):
            old = item["distractorRationales"][j]
            new = fix(old)
            if new != old:
                if CYRILLIC.findall(new) != CYRILLIC.findall(old):
                    raise SystemExit(f"{item['id']}: rationale Russian changed")
                item["distractorRationales"][j] = new
                changes.append((item["id"], f"distractorRationales[{j}]", old, new))
                coverage.append((n, item["objectiveId"], f"{item['id']}-rationale-{j}", new))
        for j in range(len(assessment.get("distractorDifference", []))):
            old = assessment["distractorDifference"][j]
            new = fix(old)
            if new != old:
                assessment["distractorDifference"][j] = new
                changes.append((item["id"], f"assessment.distractorDifference[{j}]", old, new))
        move_facts(item["id"], item, n, item["objectiveId"])
    for ref in pack["references"]:
        n = ref.get("introducedInUnit")
        if n is not None and n > MAX_UNIT and ref["id"].endswith("-attribution"):
            # The batch attribution notices of later units carry the same "dictionary rows" wording.
            change(ref["id"], "summary", n, ref, "summary")
            change(ref["id"], "detail", n, ref, "detail")
        if n is not None and n <= MAX_UNIT:
            objective = units[n]["objectives"][0]["id"]
            change(ref["id"], "summary", n, ref, "summary")
            change(ref["id"], "detail", n, ref, "detail", objective, cover=True)
    for letter in pack["letters"]:
        note = letter.get("englishNote")
        if isinstance(note, str) and note.endswith(LETTER_STATUS):
            letter["englishNote"] = note[: -len(LETTER_STATUS)]
            changes.append((letter["id"], "englishNote", note, letter["englishNote"]))

    # The sweep already covers most SUBS; the rest are the lint hits the sweep did not list.

    # Every Units 1-12 learner field and letter note is clear of the widened patterns.
    left = []

    def scan(owner, value):
        if isinstance(value, str) and LINT.search(value):
            left.append((owner, LINT.search(value).group(0)))
    for n, unit in units.items():
        if n > MAX_UNIT:
            continue
        for key in ("title", "shortLabel", "topicSummary", "mappingNote"):
            scan(unit["id"], unit.get(key))
        for session in unit["sessions"]:
            scan(session["id"], session["title"])
            for step in session["steps"]:
                for key in ("title", "body", "bookDirection", "companionDirection", "unavailableReason", "dominantAction"):
                    scan(step["id"], step.get(key))
    for item in pack["items"]:
        if (unit_of.get(item["objectiveId"]) or 99) <= MAX_UNIT:
            for value in [item.get("prompt"), item.get("feedback")] + item.get("options", []) + item.get("distractorRationales", []):
                scan(item["id"], value)
    for example in pack["examples"]:
        if (unit_of.get(example.get("objectiveId")) or 99) <= MAX_UNIT:
            scan(example["id"], example.get("note")), scan(example["id"], example.get("display"))
    for ref in pack["references"]:
        if (ref.get("introducedInUnit") or 99) <= MAX_UNIT:
            for key in ("title", "summary", "detail"):
                scan(ref["id"], ref.get(key))
    for letter in pack["letters"]:
        scan(letter["id"], letter.get("englishNote"))
    if left:
        raise SystemExit(f"Lint hits remain: {left}")

    # Coverage of new English wording in teaching fields, rationales and marked sentences (never a marker source),
    # recomputed from the final texts so that each field is covered once, by its shipped wording.
    base = loads(text)
    coverage = []
    def hosts(p):
        num = {o["id"]: u["number"] for u in p["units"] for o in u["objectives"]}
        firsts = {u["number"]: u["objectives"][0]["id"] for u in p["units"]}
        for u in p["units"]:
            # A step's coverage binds to the objective that owns it (instruction, practice or remediation), else the first.
            owning = {}
            for o in u["objectives"]:
                for sid in o.get("instructionStepIds", []) + o.get("remediationStepIds", []) + o.get("practiceStepIds", []):
                    owning.setdefault(sid, o["id"])
            for se in u["sessions"]:
                for st in se["steps"]:
                    yield st["id"], u["number"], owning.get(st["id"], firsts[u["number"]]), {"body": st.get("body")}, st.get("russianFacts") or []
        for it in p["items"]:
            fields = {"feedback": it.get("feedback")}
            fields.update({f"rationale-{j}": v for j, v in enumerate(it.get("distractorRationales", []))})
            yield it["id"], num.get(it["objectiveId"]), it["objectiveId"], fields, it.get("russianFacts") or []
        for r in p["references"]:
            n = r.get("introducedInUnit")
            yield r["id"], n, firsts.get(n), {"detail": r.get("detail")}, []
    before = {h[0]: h for h in hosts(base)}
    for owner, unit, objective, fields, facts in hosts(pack):
        if unit is None or not objective:
            continue
        _, _, _, old_fields, old_facts = before[owner]
        for key, value in fields.items():
            if value and value != old_fields.get(key):
                coverage.append((unit, objective, f"{owner}-{key}", value))
        for j, fact in enumerate(facts):
            if fact not in old_facts:
                coverage.append((unit, objective, f"{owner}-fact-{j}", fact))
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shipped = {}
    for r in pack["provenanceRecords"]:
        shipped.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    template = records["prov-t15an1-u6-possessive-our-your"]
    for unit, objective, key, value in coverage:
        # Unit 1 is outside the Units 2-30 English-coverage scope (T15g D23); its changed text states no linguistic claim.
        if unit < 2 or value in shipped.get(unit, set()):
            continue
        record = copy.deepcopy(template)
        note = (f"New English wording authored on {DATE} ({TASK}); it did not exist before that date. It covers the wording"
                " only; the Russian facts it states are cited from frozen source records in the marker's russianFactSources."
                " 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{value}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + value
        item_id = f"prov-ca-t16n2-{key}"
        if item_id in records:
            raise SystemExit(f"Duplicate record {item_id}")
        record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                       "shippedText": value, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(value),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      f" this record's English text is new, authored {DATE} for {TASK}."})
        pack["provenanceRecords"].append(record)
        records[item_id] = record
        shipped.setdefault(unit, set()).add(value)

    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps({"sweep": sweep_log, "lint": changes}, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(sweep_log)} sweep rows applied; {len(changes)} further field changes; {len(coverage)} coverage candidates; wrote {ASSET.relative_to(ROOT)}"
          f" sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
