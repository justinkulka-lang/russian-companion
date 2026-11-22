#!/usr/bin/env python3
"""T16f (Claude Code, owner direction 2026-11-22): Block 3 close-out content fixes for Units 12, 13, 14 and 15.

Exact-baseline transformer; runs once on the T16e DONE pack (25b3ade2). Russian comes only from frozen files already in the
source inventory and from records the pack already holds: OpenRussian nouns.csv, adjectives.csv and verbs.csv; Wikibooks
Future tense 3353822 (F). No Russian is generated or reworded.
- WC005 (Units 12, 14, 15): each form card in the Unit 12 future, dative, spelling and prepositional-plural teaching, the Unit 14
  future-person teaching and the Unit 15 say-request teaching names the person, case, number, gender or imperative number of its
  forms. Each label is the field of the OpenRussian cell that holds the form (nouns.csv sg_dat, pl_nom,
  pl_prep; adjectives.csv decl_n_nom, decl_f_nom; verbs.csv presfut_sg2, imperative_sg, imperative_pl) or the person and number headings of the Future tense
  page (F:38-47 compound future, F:57-63 perfective future).
- CC021 (Unit 12): each remediation step repeats its session's whole teaching (cards and examples), as in Units 11 and 13-15;
  the Unit 14 and 15 remediation repeats the new labels too.
- WC009 (Unit 12): remediation links the reference entry about its own topic.
- CC024 (Units 12, 13, 15): the infinitive сказа́ть is glossed "say, tell" from verbs.csv:3 translations_en, as the items use it.
- WC004 (Unit 13): the nationality report examples are glossed with the English of F:22 ("He said he is English" and "He said
  he was English") instead of a label or a Russian word; the label record stays the answer source of u13-g-tense-keep-0.
New English wording is covered by content-audit coverage records; coverage records never satisfy a marker.
"""
from __future__ import annotations

import copy
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
import apply_d7b_selection_records as d7b  # noqa: E402
from apply_t16e_unit_15 import sentences, forms, ACUTE  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "a24de906292644fed9a3506245ee0b8c736c70b906b43a752196932219fcd3cb"  # the T16e DONE pack (25b3ade2)
SRC = ROOT / "catalog/sources"
TASK, DATE = "T16f", "2026-11-22"
F_FILE = SRC / "units-11-15/wikibooks/future-tense-3353822.txt"
F_LINES = F_FILE.read_text(encoding="utf-8").split("\n")


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


# --------------------------------------------------------------------------------------------------------------
# Teaching labels (WC005), Units 12, 14 and 15. Each value is appended to the card as one sentence.
# --------------------------------------------------------------------------------------------------------------
LABELS = {
    "u12-st-teach-future-imp-1": "Я бу́ду говори́ть is the first-person singular, Мы бу́дем говори́ть the first-person plural and"
                                 " Вы бу́дете говори́ть the second-person plural compound future.",
    "u12-st-teach-future-imp-3": "Ты бу́дешь говори́ть is the second-person singular and Он бу́дет говори́ть the third-person"
                                 " singular compound future.",
    "u12-st-teach-future-perf-1": "Я скажу́ is the first-person singular and Мы ска́жем the first-person plural future of сказа́ть.",
    "u12-st-teach-future-perf-4": "ска́жешь is the second-person singular future of сказа́ть.",
    "u12-st-teach-dative-1": "кни́ге is the dative singular of кни́га.",
    "u12-st-teach-dative-4": "столу́ is the dative singular of сто́л, and столы́ is its nominative plural.",
    "u12-st-teach-dative-5": "до́му is the dative singular of до́м.",
    "u12-st-teach-dative-6": "дома́ is the nominative plural of до́м.",
    "u12-st-teach-spelling-1": "хоро́шее is the neuter nominative singular of хоро́ший.",
    "u12-st-teach-spelling-3": "хоро́шая is the feminine nominative singular of хоро́ший.",
    "u12-st-teach-prep-plural-1": "кни́ги is the nominative plural of кни́га, and кни́гах is its prepositional plural.",
    "u12-st-teach-prep-plural-3": "кни́ге is the dative singular of кни́га, and столы́ is the nominative plural of сто́л.",
    "u12-st-teach-prep-plural-4": "стола́х is the prepositional plural of сто́л, and столу́ is its dative singular.",
    "u12-st-teach-prep-plural-5": "дома́ is the nominative plural of до́м, and дома́х is its prepositional plural.",
    "u12-st-teach-prep-plural-6": "до́му is the dative singular of до́м.",
    "u14-st-teach-future-person-1": "Я бу́ду говори́ть is the first-person singular and Мы бу́дем говори́ть the first-person plural"
                                    " compound future.",
    "u14-st-teach-future-person-5": "Ты бу́дешь говори́ть is the second-person singular, Вы бу́дете говори́ть the second-person"
                                    " plural, Он бу́дет говори́ть the third-person singular and Они бу́дут говори́ть the"
                                    " third-person plural compound future.",
    "u14-st-teach-future-person-3": "Я скажу́ is the first-person singular and Мы ска́жем the first-person plural future.",
    "u14-st-teach-future-person-4": "Ты ска́жешь is the second-person singular, Вы ска́жете the second-person plural, Он ска́жет"
                                    " the third-person singular and Они ска́жут the third-person plural future.",
    "u15-st-teach-say-request-1": "скажи́ is the singular imperative and скажи́те the plural imperative of сказа́ть.",
    "u15-st-teach-say-request-4": "да́й is the singular imperative and да́йте the plural imperative of дать.",
}
# Evidence for each label sentence: the OpenRussian cell records whose field is the label, or the page headings.
CELL = {  # Unit 12: existing records of the cells that hold each form
    "кни́га": "prov-ex-or-u12-dative-0", "кни́ге": "prov-ex-or-u12-dative-1", "кни́ги": "prov-ex-or-u12-dative-2",
    "кни́гах": "prov-ex-or-u12-prep-plural-1", "сто́л": "prov-ex-or-u12-dative-3", "столу́": "prov-ex-or-u12-dative-4",
    "столы́": "prov-ex-or-u12-dative-5", "стола́х": "prov-ex-or-u12-prep-plural-4", "до́м": "prov-ex-or-u12-dative-6",
    "до́му": "prov-ex-or-u12-dative-7", "дома́": "prov-ex-or-u12-dative-8", "дома́х": "prov-ex-or-u12-prep-plural-7",
    "хоро́ший": "prov-ex-or-u12-spelling-0", "хоро́шее": "prov-ex-or-u12-spelling-1", "хоро́шая": "prov-ex-or-u12-spelling-2",
    "ска́жешь": "prov-ex-or-u12-future-perf-4",
}
CELL_FIELDS = {  # Unit 15: verbs.csv cells (accented headword, field) for the imperative labels
    "скажи́": ("сказа'ть", "imperative_sg"), "скажи́те": ("сказа'ть", "imperative_pl"), "сказа́ть": ("сказа'ть", "accented"),
    "да́й": ("дать", "imperative_sg"), "да́йте": ("дать", "imperative_pl"), "дать": ("дать", "accented"),
}
LABEL_EVIDENCE = {  # sentence -> Future tense heading lines (person and number) and table lines
    LABELS["u12-st-teach-future-imp-1"]: [38, 40, 41, 43, 44],
    LABELS["u12-st-teach-future-imp-3"]: [38, 43, 44, 46, 47],
    LABELS["u12-st-teach-future-perf-1"]: [57, 59, 60],
    LABELS["u12-st-teach-future-perf-4"]: [57, 62, 63],
    LABELS["u14-st-teach-future-person-1"]: [38, 40, 41],
    LABELS["u14-st-teach-future-person-5"]: [38, 43, 44, 46, 47],
    LABELS["u14-st-teach-future-person-3"]: [57, 59, 60],
    LABELS["u14-st-teach-future-person-4"]: [57, 62, 63, 65, 66],
}
NONFACTS = {  # reviewed instructions (Unit12FactProvenanceTest.reviewedNonFacts)
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    "Use the person forms taught here.",
    "Choose the correct person and number.",
    "Learn the two supplied infinitives.",
    "Select the prepositional plural form while keeping the plural number of the cue.",
}
REMEDIATION_REFERENCES = {
    "u12-st-rem-future-imp": ["ref-u12-future"], "u12-st-rem-future-perf": ["ref-u12-future"],
    "u12-st-rem-dative": ["ref-u12-dative-prepositional"], "u12-st-rem-prep-plural": ["ref-u12-dative-prepositional"],
    "u12-st-rem-spelling": ["ref-u12-spelling"], "u12-st-rem-aspect": ["ref-ca-aspect"],
}
SAY_TELL = "say, tell"  # verbs.csv:3 translations_en "say, tell, speak, talk": a contiguous selection of its first two senses
SAY_GLOSS = ["ex-u12-aspect-1", "ex-u13-infinitive-1", "ex-u15-aspect-consolidate-1"]
NATIONALITY = {  # example -> displayed span of F:22
    "ex-u13-tense-keep-0": "He said he is English",
    "ex-u13-reported-2": "He said he is English",
    "ex-u13-reported-3": "He said he was English",
}


def apply(pack: dict) -> dict:
    baseline = copy.deepcopy(pack)
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    examples = {e["id"]: e for e in pack["examples"]}
    items = {i["id"]: i for i in pack["items"]}
    log: list = []

    def add(record: dict) -> str:
        if record["itemId"] in records:
            raise SystemExit(f"Duplicate record {record['itemId']}")
        if "http" in record["originalSourceText"] or ".pdf" in record["originalSourceText"]:
            raise SystemExit(f"{record['itemId']} would carry a URL")
        if not d7b.frozen_record(pack, record):
            raise SystemExit(f"{record['itemId']} is not a frozen-source record")
        pack["provenanceRecords"].append(record)
        records[record["itemId"]] = record
        return record["itemId"]

    def f_line(number: int, unit: int, objective: str) -> str:
        raw = F_LINES[number - 1].strip()
        record_id = f"prov-t16f-u{unit}-f{number}"
        if record_id in records:
            return record_id
        record = copy.deepcopy(records["prov-ex-u12-future-perf-0-en"])  # a Future tense 3353822 record
        record.update({"itemId": record_id, "unitNumber": unit, "objectiveId": objective, "sourceRecordId": "future:" + raw[:80],
                       "originalSourceText": raw, "shippedText": raw, "sourceHash": sha(raw), "contentHash": sha(raw),
                       "recordType": "instruction", "transformation": "selection"})
        return add(record)

    for unit in [u for u in pack["units"] if u["number"] in (12, 14, 15)]:
        n = unit["number"]
        steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
        objective_of = {sid: o["id"] for o in unit["objectives"]
                        for sid in o["instructionStepIds"] + o.get("remediationStepIds", []) + o.get("practiceStepIds", [])}
        # --- WC005: labels on the form cards --------------------------------------------------------------------------
        for step_id, label in LABELS.items():
            if step_id in steps:
                step = steps[step_id]
                old = step["body"]
                step["body"] = old.strip() + " " + label
                log.append([f"{step_id}.body", old, step["body"]])
        # --- CC021: remediation repeats each session's whole teaching ------------------------------------------------
        changed = {}
        for se in unit["sessions"]:
            for s in se["steps"]:
                if s["kind"] == "TEACH" and s["body"] != s["body"].strip():
                    s["body"] = s["body"].strip()
            rem = [s for s in se["steps"] if s["kind"] == "REMEDIATE"]
            if not rem:
                continue
            r = rem[0]
            body = "\n\n".join(s["body"] for s in se["steps"] if s["kind"] == "TEACH")
            example_ids = list(dict.fromkeys(x for s in se["steps"] if s["kind"] == "TEACH" for x in s.get("exampleIds") or []))
            if body != r["body"]:
                log.append([f"{r['id']}.body", r["body"], body])
                changed[r["id"]] = r["body"]
                r["body"] = body
            if example_ids != r.get("exampleIds"):
                log.append([f"{r['id']}.exampleIds", ",".join(r.get("exampleIds") or []), ",".join(example_ids)])
                r["exampleIds"] = example_ids
            # WC009 (Unit 12): the remediation reference is about the same topic.
            want = REMEDIATION_REFERENCES.get(r["id"])
            if want is not None and r.get("referenceIds") != want:
                log.append([f"{r['id']}.referenceIds", ",".join(r.get("referenceIds") or []), ",".join(want)])
                r["referenceIds"] = want
        # --- markers on the changed remediation bodies ------------------------------------------------------------
        known: dict[str, list[str]] = {}
        for host in list(steps.values()) + [i for i in pack["items"] if i["objectiveId"].startswith(f"obj-u{n}-")]:
            for fact, ids in (host.get("russianFactSources") or {}).items():
                known.setdefault(fact, ids)
        pool = [r for r in pack["provenanceRecords"] if r["unitNumber"] == n and d7b.frozen_record(pack, r)]

        def cell_record(form: str) -> str | None:
            if n == 12 and form in CELL:
                return CELL[form]
            if n == 15 and form in CELL_FIELDS:
                accented, field = CELL_FIELDS[form]
                hits = [r for r in pool if r["sourceRecordId"] == f"verbs.csv:{accented}#{field}"]
                if not hits:
                    raise SystemExit(f"No Unit 15 record of verbs.csv:{accented}#{field}")
                return hits[0]["itemId"]
            return None

        def cite_forms(sentence: str, objective: str) -> list[str]:
            out = []
            for form in sorted(forms(sentence)):
                cell = cell_record(form)
                if cell:
                    out.append(cell)
                    continue
                bare = form.replace(ACUTE, "")

                def ok(x):
                    f = forms(x["originalSourceText"])
                    return form in f if ACUTE in form else bare in {y.replace(ACUTE, "") for y in f}
                ranked = ([x for x in pool if x["objectiveId"] == objective and x["shippedText"].lower() == form and ok(x)] +
                          [x for x in pool if x["objectiveId"] == objective and ok(x)] +
                          [x for x in pool if x["shippedText"].lower() == form and ok(x)] + [x for x in pool if ok(x)])
                if not ranked:
                    raise SystemExit(f"No frozen Unit {n} record shows {form!r} ({sentence})")
                out.append(ranked[0]["itemId"])
            return out

        missing = []
        for se in unit["sessions"]:
            for step in se["steps"]:
                if step["kind"] != "REMEDIATE" or step["id"] not in changed:
                    continue
                objective = objective_of.get(step["id"]) or se["objectiveIds"][0]
                old_sentences = set(sentences(changed[step["id"]]))
                old_facts = set(step.get("russianFacts") or [])
                facts, sources = [], {}
                for sentence in sentences(step["body"]):
                    if not any(ch.isalpha() for ch in sentence) or sentence in NONFACTS or sentence in sources:
                        continue
                    if sentence in known:
                        ids = known[sentence]
                    elif sentence in old_sentences and sentence not in old_facts:
                        continue  # a reviewed instruction that was already unmarked in this remediation
                    elif forms(sentence):
                        ids = [f_line(k, n, objective) for k in LABEL_EVIDENCE.get(sentence, [])] + cite_forms(sentence, objective)
                    else:
                        missing.append((step["id"], sentence))
                        continue
                    facts.append(sentence)
                    sources[sentence] = list(dict.fromkeys(ids))
                step["russianFacts"], step["russianFactSources"] = facts, sources
        if missing:
            raise SystemExit("Sentences without a disposition: " + json.dumps(missing, ensure_ascii=False, indent=1))
        # --- every learner step: Russian only through frozen records of this unit -----------------------------------
        frozen = {r["shippedText"] for r in pool}
        frozen_forms = set().union(*(forms(r["originalSourceText"]) for r in pool))
        for step in steps.values():
            for m in re.finditer("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*", step["body"]):
                region = m.group(0).strip().rstrip("?!.,")
                if region not in frozen and not all(f in frozen_forms for f in forms(region)):
                    raise SystemExit(f"Unit {n} region without a frozen record: {region!r} in {step['id']}")
    # --- CC024: сказа́ть is glossed say, tell (verbs.csv:3) --------------------------------------------------------------
    template = records["prov-ex-or-u12-future-perf-2-gloss"]  # verbs.csv:сказа'ть#translations_en
    if template["originalSourceText"] != "say, tell, speak, talk":
        raise SystemExit("verbs.csv:3 translations_en changed")
    for ex_id in SAY_GLOSS:
        e, rid = examples[ex_id], f"prov-{ex_id}-en"
        if e["display"] != "сказа́ть" or e["note"] != "to speak":
            raise SystemExit(f"{ex_id}: baseline changed")
        old = records[rid]
        new = copy.deepcopy(template)
        new.update({"itemId": rid, "unitNumber": old["unitNumber"], "objectiveId": old["objectiveId"], "shippedText": SAY_TELL,
                    "contentHash": sha(SAY_TELL), "transformation": "selection", "recordType": "meaning"})
        pack["provenanceRecords"][pack["provenanceRecords"].index(old)] = new
        records[rid] = new
        if not d7b.frozen_record(pack, new):
            raise SystemExit(f"{rid} is not frozen")
        log.append([f"{ex_id}.note", e["note"], SAY_TELL])
        e["note"] = SAY_TELL
    # --- WC004: Unit 13 nationality report glosses (F:22) ---------------------------------------------------------------
    raw22 = F_LINES[21].strip()
    label = records["prov-ex-u13-tense-keep-0-en"]
    if label["shippedText"] != "we keep the tense the same":
        raise SystemExit("tense-keep label record changed")
    keep = copy.deepcopy(label)
    keep["itemId"] = "prov-t16f-u13-tense-keep-label"
    add(keep)
    for item in pack["items"]:
        a = item.get("assessment") or {}
        for key in ("answerSourceIds", "distractorSourceIds", "relationParticipantIds"):
            if "prov-ex-u13-tense-keep-0-en" in (a.get(key) or []):
                a[key] = ["prov-t16f-u13-tense-keep-label" if x == "prov-ex-u13-tense-keep-0-en" else x for x in a[key]]
                log.append([f"{item['id']}.assessment.{key}", "prov-ex-u13-tense-keep-0-en", "prov-t16f-u13-tense-keep-label"])
        for fact, ids in (item.get("russianFactSources") or {}).items():
            if "prov-ex-u13-tense-keep-0-en" in ids:
                raise SystemExit(f"{item['id']} cites the label record in a marker")
    for ex_id, span in NATIONALITY.items():
        e, rid = examples[ex_id], f"prov-{ex_id}-en"
        if span not in d7b.displayed(raw22):
            raise SystemExit(f"{span!r} is not displayed on F:22")
        record = records[rid]
        record.update({"originalSourceText": raw22, "sourceRecordId": "future:" + raw22[:80], "shippedText": span,
                       "sourceHash": sha(raw22), "contentHash": sha(span), "transformation": "selection", "recordType": "meaning"})
        record.pop("originalSourceTextRef", None)
        if not d7b.frozen_record(pack, record):
            raise SystemExit(f"{rid} is not frozen")
        log.append([f"{ex_id}.note", e["note"], span])
        e["note"] = span
    cover_english(pack, baseline)
    return {"changes": log}


def english_hosts(pack: dict, units: set[int]) -> dict:
    out = {}
    for unit in pack["units"]:
        if unit["number"] not in units:
            continue
        first = unit["objectives"][0]["id"]
        objective_of = {}
        for o in unit["objectives"]:
            for sid in o.get("instructionStepIds", []) + o.get("remediationStepIds", []) + o.get("practiceStepIds", []):
                objective_of.setdefault(sid, o["id"])
        for s in unit["sessions"]:
            for st in s["steps"]:
                out[(st["id"], "body")] = (unit["number"], objective_of.get(st["id"], first), st["body"],
                                           list(st.get("russianFacts", [])))
    for e in pack["examples"]:
        m = re.match(r"obj-u(\d+)-", e.get("objectiveId") or "")
        if m and int(m.group(1)) in units:
            out[(e["id"], "note")] = (int(m.group(1)), e["objectiveId"], e.get("note") or "", [])
    return out


def cover_english(pack: dict, before: dict) -> None:
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shipped: dict[int, set] = {}
    for r in pack["provenanceRecords"]:
        shipped.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    template = records["prov-t15an1-u6-possessive-our-your"]
    count = 0

    def coverage(unit: int, objective: str, key: str, text: str) -> None:
        nonlocal count
        if not text or text in shipped.get(unit, set()):
            return
        record = copy.deepcopy(template)
        note = (f"New English wording authored on {DATE} ({TASK}); it did not exist before that date. It covers the wording"
                " only; the Russian facts it states are cited from frozen source records in the marker's russianFactSources."
                " 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        item_id = f"prov-ca-t16f-{key}"
        if item_id in records:
            raise SystemExit(f"Duplicate record {item_id}")
        record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                       "shippedText": text, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      f" this record's English text is new, authored {DATE} for {TASK}."})
        pack["provenanceRecords"].append(record)
        records[item_id] = record
        shipped.setdefault(unit, set()).add(text)
        count += 1

    units = {12, 13, 14, 15}
    old_hosts = english_hosts(before, units)
    hosts = english_hosts(pack, units)
    for (owner, field), (unit, objective, text, facts) in sorted(hosts.items()):
        old = old_hosts.get((owner, field))
        if old is None or old[2] != text:
            coverage(unit, objective, f"{owner}-{field}", text)
            for n, fact in enumerate(facts):
                coverage(unit, objective, f"{owner}-{field}-fact{n}", fact)
    print(f"english coverage records: {count}")


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T16f baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(report['changes'])} changes; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
