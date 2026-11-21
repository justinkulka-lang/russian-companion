#!/usr/bin/env python3
"""T15e (Claude Code, owner direction 11/17/2026): Unit 10 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer. Russian comes only from frozen files under catalog/sources/ already in the source inventory:
Wikibooks Russian/Grammar/Genitive_case oldid 4567338 (units-6-10/wikibooks/genitive-4567338.txt), Accusative_case
oldid 4330404 (accusative-4330404.txt), Adjectives oldid 4292379 (adjectives-4292379.txt) and OpenRussian nouns.csv /
adjectives.csv (commit 50e210c4). No source is added; every typed key is an already taught answer.

Plan: Codex's read-only preparation report audit/prep/units-09-10-prep-at-9a9b9760.txt (branch owner/prep-reports),
Unit 10 section only.
- A2-24: every Unit 10 check moves to an end-of-unit check session after all teaching; "admitted ... row" prompts become
  ordinary instructions; cue-only checks get a third option from the same frozen row (report section 1). u10-i-animate-1
  gets любят меня from its cue's frozen span (accusative-4330404.txt:28): the report's adjectives-4292379.txt:14 phrase
  is on another page, which V2Validator.classifyRelation does not accept for a SOURCE_SUPPLIED_PAIR item.
- A2-23: ten typed checks whose keys are taught answers (report section 2; же́нщин replaces но́вого, which would repeat
  u7-i-type-novogo exactly).
- Facts: every feedback, rationale and remediation sentence is marked with frozen-source citations or is a reviewed
  instruction; the 68 English-only inventory entries get the report's MARK, REWORD or REMOVE disposition.
- A2-26: governance wording and wiki markup leave Unit 10 learner text.
New English wording is covered by content-audit coverage records; coverage records never satisfy a marker.
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
from apply_t14b_units_1_2_owner import typed_item  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "de29422861b7219cfccd5ee3e0683270af5add67357938321f490f1d65313d17"
SRC = ROOT / "catalog/sources"
GENITIVE = SRC / "units-6-10/wikibooks/genitive-4567338.txt"
ACCUSATIVE = SRC / "units-6-10/wikibooks/accusative-4330404.txt"
ADJ_PAGE = SRC / "units-6-10/wikibooks/adjectives-4292379.txt"
NOUNS = SRC / "units-3-5/openrussian/nouns.csv"
TASK = "T15e, Claude Code, 2026-11-17"
ACUTE = "́"
CYR = "Ѐ-ӿ"
UNIT = 10


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


with NOUNS.open(encoding="utf-8") as _handle:
    NOUN_ROWS = list(csv.DictReader(_handle, delimiter="\t", quoting=csv.QUOTE_NONE))


def noun_cell(bare: str, field: str) -> str:
    rows = [r for r in NOUN_ROWS if r["bare"] == bare]
    if len(rows) != 1:
        raise SystemExit(f"nouns.csv: {len(rows)} rows for {bare}")
    value = rows[0][field].strip()
    if not value or "," in value:
        raise SystemExit(f"nouns.csv:{bare}#{field} is not a single form: {value!r}")
    return value


class Builder:
    def __init__(self, pack: dict):
        self.pack = pack
        self.records = {r["itemId"]: r for r in pack["provenanceRecords"]}
        self.new: list[dict] = []
        self.unit = next(u for u in pack["units"] if u["number"] == UNIT)

    def add(self, record: dict) -> dict:
        if record["itemId"] in self.records:
            raise SystemExit(f"Duplicate record {record['itemId']}")
        self.records[record["itemId"]] = record
        self.new.append(record)
        return record

    def noun(self, objective: str, bare: str, field: str, key: str) -> str:
        """An OpenRussian nouns.csv cell record: the named field of the named frozen row."""
        original = noun_cell(bare, field)
        record = copy.deepcopy(self.records["prov-ex-or-u10-preposition-14"])
        record.update({"itemId": f"prov-t15e-u10-{key}", "objectiveId": objective, "sourceRecordId": f"nouns.csv:{bare}#{field}",
                       "originalSourceText": original, "shippedText": stressed(original), "sourceHash": sha(original),
                       "contentHash": sha(stressed(original)), "recordType": "lexical record"})
        return self.add(record)["itemId"]

    def span(self, key: str, objective: str, path: Path, needle: str, template: str, prefix: str, record_type: str) -> str:
        """A record of one exact span of a frozen page line."""
        text = path.read_text(encoding="utf-8")
        if text.count(needle) != 1:
            raise SystemExit(f"Span {key} occurs {text.count(needle)} times in {path.name}")
        record = copy.deepcopy(self.records[template])
        record.update({"itemId": f"prov-t15e-u10-{key}", "objectiveId": objective, "sourceRecordId": prefix + needle[:80],
                       "originalSourceText": needle, "shippedText": needle, "sourceHash": sha(needle),
                       "contentHash": sha(needle), "recordType": record_type})
        return self.add(record)["itemId"]

    def line(self, page: str, number: int) -> str:
        """A record of one whole line of a frozen Wikibooks page, cited by markers."""
        record_id = f"prov-t15e-u10-{page}{number}"
        if record_id in self.records:
            return record_id
        path, template, prefix = {"g": (GENITIVE, "prov-u10-pronoun-rem", "genitive:"),
                                  "c": (ACCUSATIVE, "prov-ex-u10-animate-0-ru", "accusative:"),
                                  "w": (ADJ_PAGE, "prov-ex-u10-have-4-ru", "adjectives:")}[page]
        text = path.read_text(encoding="utf-8")
        needle = text.splitlines()[number - 1].strip()
        if text.count(needle) != 1:
            # A line repeated on the page: the span is that line with the line before it, which occurs once.
            needle = "\n".join(text.splitlines()[number - 2:number])
        return self.span(f"{page}{number}", self.records[template]["objectiveId"], path, needle, template, prefix, "instruction")

    def coverage(self, objective: str, key: str, text: str, unit: int = UNIT) -> str:
        template = self.records["prov-t15an1-u6-possessive-our-your"]
        record = copy.deepcopy(template)
        note = (f"New English wording authored on 2026-11-17 ({TASK}); it did not exist before that date. It covers the"
                " wording only; the Russian facts it states are cited from frozen source records in the marker's"
                " russianFactSources. 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        record.update({"itemId": f"prov-ca-t15e-{key}", "unitNumber": unit, "objectiveId": objective,
                       "sourceRecordId": f"ca-coverage:{objective}:{key}", "shippedText": text,
                       "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      " this record's English text is new, authored 2026-11-17 for T15e."})
        return self.add(record)["itemId"]


# --------------------------------------------------------------------------------------------------------------
# Sentence segmentation and fact forms, as V2Validator.englishSentenceRanges and V2Validator.factWordForms
# --------------------------------------------------------------------------------------------------------------
QUOTED = re.compile("\"([^\"]+)\"|“([^”]+)”|‘((?:[^’]|(?<=[A-Za-z])’(?=[A-Za-z]))+)’|"
                    "«([^»]+)»|(?<![A-Za-z])'((?:[^']|(?<=[A-Za-z])'(?=[A-Za-z]))+)'(?![A-Za-z])")
ENGLISH_SENTENCE = re.compile(r"[^.!?\n]+[.!?]*")
FACT_FORM = re.compile(f"[{CYR}][{CYR}́]*")


def sentences(text: str) -> list[str]:
    masked = list(text)
    for quote in QUOTED.finditer(text):
        for index in range(quote.start(), quote.end()):
            if masked[index] in ".!?\n":
                masked[index] = "_"
    out = []
    for match in ENGLISH_SENTENCE.finditer("".join(masked)):
        value = text[match.start():match.end()]
        if value.strip():
            out.append(value.strip())
    return out


def forms(text: str) -> set[str]:
    return {m.group(0).lower() for m in FACT_FORM.finditer(stressed(text))}


# Whole instructions and task directions that state no Russian fact (reviewed, as in Units 7 to 9).
NONFACTS = [
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    "Compare the two displayed phrases.",
    "Read the displayed phrase carefully.",
    "Decide whether the whole shown line affirms or denies possession.",
    "Choose the genitive absence explanation, rather than the simple positive possession explanation.",
    "Choose from the meaning and construction of the whole supplied line.",
    "Keep the absent thing separate from any possessor or place mentioned in the line.",
    "Compare the supplied forms and their case roles, rather than translating either phrase as a whole sentence.",
    "These choices practise recognition of the supplied pair; they do not supply unseen sentence production.",
    "Choose the object form, not the subject form.",
    "Match the shown pronoun, rather than replacing it with the other you form.",
    "Preserve the shown pronoun and its form of address.",
]

# Teaching wording (remediation repeats each session's teaching).
TEACH_REPLACE = {
    "u10-st-teach-animate-1": [
        (" In the source object example it is accusative; the displayed phrase alone does not state a complete action or"
         " identify a speaker.", " Compare the two displayed phrases."),
        (" It is the subject phrase in the other source example; the display contains no predicate or object.",
         " Read the displayed phrase carefully."),
        (" This explanation remains review-pending. Guided work and independent checks are separate; activity is not"
         " demonstrated ability.", ""),
    ],
    "u10-st-teach-animate-2": [
        ("A complete source example can establish which phrase is the subject or object, but only the noun phrases are"
         " displayed here. ", ""),
        (" Full sentence meanings belong to that source context, not to either isolated phrase. The short dictionary glosses"
         " on later cards name the entry meaning, not the number or case of a displayed inflected form.", ""),
    ],
}

# Feedback and rationale wording: (item, field, old, new); field "feedback" or a rationale index.
ITEM_REPLACE = [
    ("u10-i-pronoun-4", "feedback", "This checks the bare table form; third-person forms can change after prepositions.",
     "Third-person forms can change after prepositions."),
    ("u10-i-animate-1", "feedback", "The other row requested here is the nominative subject form.",
     "The phrase requested here is the nominative subject form."),
    ("u10-i-animate-1", 1, "the requested other row is the nominative subject phrase.",
     "the requested phrase is the nominative subject phrase."),
]

PROMPTS = {
    "Which admitted form is the genitive row of the shown form?": "Which genitive form belongs to the shown pronoun?",
    "Which admitted form is the object row of the shown form?": "Which phrase is the object form of the shown phrase?",
    "Which admitted form is the other plural row of the shown form?":
        "Which nominative plural phrase refers to the same women as the shown phrase?",
}

OBJECTIVE_TITLES = {"obj-u10-have": "Expressing possession", "obj-u10-absence": "Expressing absence",
                    "obj-u10-pronoun": "Genitive pronouns", "obj-u10-preposition": "The genitive after prepositions",
                    "obj-u10-adjective": "Genitive adjective forms", "obj-u10-animate": "Animate plural objects"}
SESSION_TITLES = {"u10-s-orient": "Possession and the genitive",
                  **{f"u10-s-{k[8:]}": v for k, v in OBJECTIVE_TITLES.items()}}
STEP_TITLES = {"Admitted lines": "Examples", "Admitted instruction": "Source example",
               "Admitted possession and expanded rows": "Possession and the genitive"}
ORIENT_BODY = ("This unit covers possession and absence, the genitive forms of pronouns, nouns and adjectives after"
               " prepositions, and animate plural objects.")
TOPIC = ("Unit 10 covers possession and absence, genitive pronouns, the genitive after prepositions, genitive adjective forms"
         " and animate plural objects, alongside Lesson 10 in your book.")
GUIDED_OLD = "Choose the admitted record. The feedback states the frozen field."
GUIDED_NEW = "Choose an answer, then read the feedback."
BOOK_OLD = "This unit does not teach a pattern that leaves the admitted word out."
BOOK_NEW = "This unit does not teach a possession pattern that leaves out the verb shown in its examples."
CHECK_BODY = "Answer without looking back. A miss returns to the explanation."
MARKUP_RECORDS = ("prov-u10-pronoun-rem", "prov-u10-preposition-rem", "prov-u10-adjective-rem")

FIELD_NAME = {"sg_nom": "singular nominative", "sg_gen": "singular genitive", "sg_dat": "singular dative",
              "pl_nom": "plural nominative", "pl_acc": "plural accusative"}
# Cue-only checks: a third form from the same OpenRussian row (report section 1).
THIRD = {"u10-i-preposition-1": ("окно", "sg_dat", "okno-sg-dat"), "u10-i-preposition-2": ("друг", "sg_dat", "drug-sg-dat"),
         "u10-i-animate-2": ("женщина", "sg_dat", "zhenshchina-sg-dat"), "u10-i-animate-3": ("брат", "sg_dat", "brat-sg-dat")}
# The report's W:14 phrase is on a different page, so V2Validator.classifyRelation no longer finds the
# SOURCE_SUPPLIED_PAIR relation (every option must come from the cue's frozen span); любят меня is on that span (C:28).
ANIMATE1_THIRD = "любят меня"
ANIMATE1_RATIONALE = ("любят меня is the rest of the sentence meaning These women like me, not a phrase that names the"
                      " women.")

# Ten typed checks (report section 2): (id, objective, closed item whose taught cue and answer it reuses, prompt).
P_PRON = "Type the genitive form of the shown pronoun."
P_NOUN = "Type the singular genitive form of the shown noun."
P_ADJ = "Type the masculine singular genitive form of the shown adjective."
P_ACC = "Type the plural accusative form of the shown noun."
TYPED = [("u10-i-type-nas", "obj-u10-pronoun", "u10-i-pronoun-1", P_PRON),
         ("u10-i-type-tebya", "obj-u10-pronoun", "u10-i-pronoun-2", P_PRON),
         ("u10-i-type-vas", "obj-u10-pronoun", "u10-i-pronoun-3", P_PRON),
         ("u10-i-type-ikh", "obj-u10-pronoun", "u10-i-pronoun-4", P_PRON),
         ("u10-i-type-okna", "obj-u10-preposition", "u10-i-preposition-1", P_NOUN),
         ("u10-i-type-druga", "obj-u10-preposition", "u10-i-preposition-2", P_NOUN),
         ("u10-i-type-moego", "obj-u10-preposition", "u10-i-preposition-3", P_ADJ),
         # но́вого (report) would repeat u7-i-type-novogo exactly (IDENTICAL_REQUIRED_DISPLAY_TASK); же́нщин is taught too.
         ("u10-i-type-zhenshchin", "obj-u10-animate", "u10-i-animate-2", P_ACC),
         ("u10-i-type-khoroshego", "obj-u10-adjective", "u10-i-adjective-3", P_ADJ),
         ("u10-i-type-bratev", "obj-u10-animate", "u10-i-animate-3", P_ACC)]
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."


def edit(text: str, old: str, new: str, where: str) -> str:
    if text.count(old) != 1:
        raise SystemExit(f"{where}: baseline text changed ({old!r})")
    return text.replace(old, new)


# Codex check of T15d (owner direction 11/17/2026): learner wording that narrates a source, outside the A2-26 lint.
U9_1009_OLD = "The dictionary classifies this noun as neuter; choose by that grammatical gender."
U9_1009_NEW = "This noun is neuter; choose by its grammatical gender."
U3_PROMPT_OLD = "Which group does the dictionary give for the shown word?"
U3_PROMPT_NEW = "Which gender does the shown word have?"


def part0(b: Builder) -> list[tuple[str, str, str, str]]:
    """Units 1-10 sweep for "the dictionary/source/table/page/entry <verb>" narration; returns (owner, field, old, new)."""
    swept = []
    item = next(i for i in b.pack["items"] if i["id"] == "u9-i-odindva-2")
    a = item["assessment"]
    old_feedback = item["feedback"]
    item["feedback"] = edit(old_feedback, U9_1009_OLD, U9_1009_NEW, item["id"])
    a["whyAnswerDemonstratesObjective"] = edit(a["whyAnswerDemonstratesObjective"], U9_1009_OLD, U9_1009_NEW, item["id"])
    item["russianFacts"] = [U9_1009_NEW if f == U9_1009_OLD else f for f in item["russianFacts"]]
    sources = item["russianFactSources"]
    item["russianFactSources"] = {(U9_1009_NEW if k == U9_1009_OLD else k): v for k, v in sources.items()}
    if item["russianFactSources"][U9_1009_NEW] != ["prov-t15d-u9-zhivotnoe-gender"]:
        raise SystemExit("u9-i-odindva-2 no longer cites nouns.csv:14496")
    b.coverage(item["objectiveId"], "u9-i-odindva-2-1", item["feedback"], unit=9)
    b.coverage(item["objectiveId"], "u9-i-odindva-2-2", U9_1009_NEW, unit=9)
    swept.append((item["id"], "feedback", U9_1009_OLD, U9_1009_NEW))
    for item in b.pack["items"]:
        if item["prompt"] == U3_PROMPT_OLD:
            if not item["objectiveId"].startswith("obj-u3-"):
                raise SystemExit(f"{item['id']}: unexpected owner of the Unit 3 prompt")
            item["prompt"] = U3_PROMPT_NEW
            swept.append((item["id"], "prompt", U3_PROMPT_OLD, U3_PROMPT_NEW))
    if len(swept) != 13:
        raise SystemExit(f"sweep changed {len(swept)} fields, expected 13")
    return swept


def part1(pack: dict):
    b = Builder(pack)
    part0(b)
    unit = b.unit
    baseline = copy.deepcopy(pack)
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R = b.records

    # --- A2-26 markup leaves the shipped wiki spans (each frozen original keeps it) ------------------------------
    for record_id in MARKUP_RECORDS:
        record = R[record_id]
        old = record["shippedText"]
        record["shippedText"] = old.replace("'''", "")
        record["contentHash"] = sha(record["shippedText"])
        for step in steps.values():
            if step["kind"] == "TEACH" and step["body"] == old:
                step["body"] = record["shippedText"]
    for step_id, pairs in TEACH_REPLACE.items():
        for old, new in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], old, new, step_id)
    # Remediation repeats the whole teaching of its session (three sessions showed one markup line).
    for session in unit["sessions"]:
        teach = [s for s in session["steps"] if s["kind"] == "TEACH"]
        for rem in (s for s in session["steps"] if s["kind"] == "REMEDIATE"):
            rem["body"] = "\n\n".join(s["body"] for s in teach)
            rem["exampleIds"] = list(dict.fromkeys(e for s in teach for e in s.get("exampleIds", [])))

    for item_id, field, old, new in ITEM_REPLACE:
        item = items[item_id]
        a = item["assessment"]
        if field == "feedback":
            item["feedback"] = edit(item["feedback"], old, new, item_id)
            if a.get("whyAnswerDemonstratesObjective") and old in a["whyAnswerDemonstratesObjective"]:
                a["whyAnswerDemonstratesObjective"] = a["whyAnswerDemonstratesObjective"].replace(old, new)
        else:
            item["distractorRationales"][field] = edit(item["distractorRationales"][field], old, new, item_id)
            a["distractorDifference"] = [d.replace(old, new) for d in a.get("distractorDifference", [])]
    for item in pack["items"]:
        if item["objectiveId"].startswith("obj-u10-") and item["prompt"] in PROMPTS:
            item["prompt"] = PROMPTS[item["prompt"]]
    return b, baseline, items, steps, objectives


def part2(b: Builder, items: dict, objectives: dict) -> list[dict]:
    R = b.records
    pack = b.pack
    # --- A2-24 cue-only checks: a third form from the answer's frozen row ------------------------------------------
    for item_id, (bare, field, key) in THIRD.items():
        item = items[item_id]
        a = item["assessment"]
        answer = R[a["answerSourceIds"][0]]
        answer_field = answer["sourceRecordId"].rsplit("#", 1)[-1]
        if not answer["sourceRecordId"].startswith(f"nouns.csv:{bare}#"):
            raise SystemExit(f"{item_id}: third form is not from the answer's row")
        record_id = b.noun(item["objectiveId"], bare, field, key)
        form = R[record_id]["shippedText"]
        if form in item["options"]:
            raise SystemExit(f"{item_id}: {form} is already an option")
        rationale = f"{form} is the {FIELD_NAME[field]} form, so it does not supply the requested {FIELD_NAME[answer_field]} form."
        item["options"].append(form)
        item["distractorRationales"].append(rationale)
        a["contrastMembers"].append(form)
        a["distractorDifference"].append(rationale)
        a["distractorSourceIds"].append(record_id)
        a["relationParticipantIds"].append(record_id)
    # --- u10-i-animate-1: a third phrase from the cue's frozen span (accusative-4330404.txt:28) ---------------------
    item = items["u10-i-animate-1"]
    a = item["assessment"]
    cue_record = R[a["relationParticipantIds"][0]]
    if ANIMATE1_THIRD not in ACCUSATIVE.read_text(encoding="utf-8").splitlines()[27] or ANIMATE1_THIRD not in cue_record["originalSourceText"]:
        raise SystemExit("любят меня is not on the cue's frozen span (accusative-4330404.txt:28)")
    record = copy.deepcopy(cue_record)
    record.update({"itemId": "prov-t15e-u10-animate-1-c28-lyubyat-menya", "shippedText": ANIMATE1_THIRD,
                   "contentHash": sha(ANIMATE1_THIRD)})
    phrase = b.add(record)["itemId"]
    item["options"].append(ANIMATE1_THIRD)
    item["distractorRationales"].append(ANIMATE1_RATIONALE)
    a["contrastMembers"].append(ANIMATE1_THIRD)
    a["distractorDifference"].append(ANIMATE1_RATIONALE)
    a["distractorSourceIds"].append(phrase)
    a["relationParticipantIds"].append(phrase)

    # --- A2-23: typed checks whose keys are taught answers --------------------------------------------------------
    typed = []
    for item_id, objective, closed_id, prompt in TYPED:
        closed = items[closed_id]
        if R[closed["assessment"]["answerSourceIds"][0]]["shippedText"] != closed["answer"]:
            raise SystemExit(f"{item_id}: {closed['answer']} is not its frozen record's text")
        typed.append(typed_item(item_id, objective, prompt, closed["promptLetter"], closed["answer"], TYPED_FEEDBACK,
                                closed["sourceId"]))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    return typed


def part3(b: Builder, steps: dict, objectives: dict) -> dict:
    unit = b.unit
    for objective_id, title in OBJECTIVE_TITLES.items():
        objectives[objective_id]["title"] = title
    unit["topicSummary"] = TOPIC
    for session in unit["sessions"]:
        if session["id"] in SESSION_TITLES:
            session["title"] = SESSION_TITLES[session["id"]]
    for step in steps.values():
        if step["title"] in STEP_TITLES:
            step["title"] = STEP_TITLES[step["title"]]
        if step["kind"] == "GUIDED" and step["body"] == GUIDED_OLD:
            step["body"] = GUIDED_NEW
    steps["u10-st-orient"]["body"] = ORIENT_BODY
    steps["u10-st-book-work"]["body"] = edit(steps["u10-st-book-work"]["body"], BOOK_OLD, BOOK_NEW, "u10-st-book-work")

    # --- A2-24: every check moves to an end-of-unit check session, after all teaching ---------------------------
    check_steps = []
    for session in unit["sessions"]:
        kept = []
        for step in session["steps"]:
            (check_steps if step["kind"] == "CHECK" else kept).append(step)
        session["steps"] = kept
    for step in check_steps:
        owner = next(o for o in objectives.values() if set(o["checkItemIds"]) & set(step["itemIds"]))
        step["itemIds"] = list(owner["checkItemIds"])
        step["title"] = "Check: " + owner["title"]
        if step["body"].startswith("These checks use only admitted records."):
            step["body"] = CHECK_BODY
    book = next(index for index, session in enumerate(unit["sessions"]) if session["id"] == "u10-s-book")
    unit["sessions"].insert(book, {"id": "u10-s-checks", "title": "Unit 10 checks", "estimatedMinutes": 20,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})
    return {s["id"]: s for se in unit["sessions"] for s in se["steps"]}


def g(*numbers): return [("g", n) for n in numbers]  # noqa: E704
def c(*numbers): return [("c", n) for n in numbers]  # noqa: E704
def w(*numbers): return [("w", n) for n in numbers]  # noqa: E704


# English-only sentences and the frozen lines that state them (report section 3): G = genitive-4567338.txt,
# C = accusative-4330404.txt, W = adjectives-4292379.txt.
EXTRA: dict[str, list] = {
    "The possessor after the possession preposition is genitive; the possessed thing or slot is nominative.": g(42, 43),
    "The absence explanation would deny possession, which this line does not do.": g(43, 53),
    "The other explanation describes absence rather than the presence or simple possession shown here.": g(43, 53),
    "In the simple possession pattern shown here, the possessor after the possession preposition is genitive.": g(43),
    "The thing possessed is nominative.": g(43),
    "The schematic slot in the first example represents that possessed thing.": g(42, 43),
    "— Do you have a son?": g(45),
    "A simple positive possession line places the possessed thing in the nominative.": g(43),
    "An absence line places the absent thing in the genitive.": g(53),
    "The son examples keep the same simple possession pattern in a statement or question: genitive possessor and nominative"
    " possessed thing.": g(43, 44, 45, 46),
    "The form of a question does not turn the noun into an absence construction.": g(44, 53),
    "— Yes, I have a son.": g(46),
    "The many-friends example includes a quantity expression.": w(59) + g(21),
    "Quantity patterns are separate from the simple nominative possessed thing in the son checks.": g(21, 43),
    "The no-son example below denies possession and uses the genitive for the absent thing.": g(53, 59),
    "The no-money example also denies possession.": g(53, 60),
    "uses the simple possession construction.": g(43, 45, 46),
    "The noun naming the absent thing is genitive.": g(53),
    "The presence or simple possession explanation would assert that the thing exists or is possessed, which this line"
    " does not assert.": g(43, 53),
    "The other explanation asserts presence or simple possession rather than the absence shown here.": g(43, 53),
    "In an absence line, the absent thing is genitive.": g(53),
    "An absence line may describe what a person does not have or what does not exist at a place.": g(53, 54, 59),
    "The absence construction uses a genitive noun for the absent thing.": g(53),
    "The simple positive possession construction instead uses a genitive possessor and a nominative possessed thing.": g(43),
    "Both negative examples below use the genitive for the absent thing.": g(53, 55, 60),
    "The positive son example below affirms possession.": g(46),
    "Its possessed noun is nominative, so the genitive absence explanation does not classify this line.": g(43, 46),
    "Keep the same first-person singular referent when moving to the genitive row.": g(132, 135, 138),
    "Both are first person, but the cue refers to the plural group, not one speaker.": g(132, 135, 138),
    "Both refer to a plural group, but they is third person and we is first person.": g(132, 135, 138),
    "Third-person forms can change after prepositions.": g(138, 141, 142, 143),
    "Its forms match the genitive plural.": c(23, 29),
    "For these supplied animate plural forms, the accusative object form matches the genitive plural form.": c(23, 28, 29),
    "The nominative phrase names the subject in the source example.": c(28),
    "The phrase requested here is the nominative subject form.": c(28),
}


def is_frozen(pack: dict, record: dict) -> bool:
    sources = {s["id"] for s in pack["sources"]}
    rights = {r["sourceId"] for r in pack["rights"] if r.get("permitsAppUse")}
    return (record["sourceId"] not in ("src-content-audit-research",) and record.get("licenseId") != "Original app text"
            and record.get("synthesis") is None and not record["sourceRecordId"].startswith("ca-coverage:")
            and bool(record.get("sourceRevision")) and record["sourceHash"] == sha(record["originalSourceText"])
            and record["sourceId"] in sources and record["sourceId"] in rights)


def part4(b: Builder, baseline: dict, items: dict, steps: dict) -> dict:
    pack = b.pack
    R = b.records
    unit = b.unit

    def owner_objective(step_id: str) -> str:
        for o in unit["objectives"]:
            if step_id in o["instructionStepIds"] or step_id in o["remediationStepIds"] or step_id in o["practiceStepIds"]:
                return o["id"]
        session = next(se for se in unit["sessions"] if any(s["id"] == step_id for s in se["steps"]))
        return session["objectiveIds"][0]

    frozen10 = lambda: [x for x in R.values() if x["unitNumber"] == UNIT and is_frozen(pack, x)]  # noqa: E731

    def cite_forms(sentence: str, objective: str, participants: list[str]) -> list[str]:
        out = []
        pool = frozen10()
        for form in sorted(forms(sentence)):
            bare = form.replace(ACUTE, "")

            def ok(x):
                f = forms(x["originalSourceText"])
                return form in f if ACUTE in form else bare in {y.replace(ACUTE, "") for y in f}
            ranked = ([x for x in pool if x["itemId"] in participants and ok(x)] +
                      [x for x in pool if x["objectiveId"] == objective and x["shippedText"].lower() == form and ok(x)] +
                      [x for x in pool if x["objectiveId"] == objective and ok(x)] +
                      [x for x in pool if x["shippedText"].lower() == form and ok(x)] + [x for x in pool if ok(x)])
            if not ranked:
                raise SystemExit(f"No frozen Unit 10 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    report = {}
    missing = []

    def mark(host: dict, texts: list[str], objective: str, owner: str, participants: list[str]) -> None:
        facts, sources = [], {}
        for text in texts:
            for sentence in sentences(text):
                if not any(ch.isalpha() for ch in sentence) or sentence in NONFACTS:
                    continue
                refs = EXTRA.get(sentence)
                if refs is None and not forms(sentence):
                    missing.append((owner, sentence))
                    continue
                ids = [b.line(*ref) for ref in refs or []] + cite_forms(sentence, objective, participants)
                ids = list(dict.fromkeys(ids))
                if sentence not in sources:
                    facts.append(sentence)
                    sources[sentence] = ids
        if facts:
            host["russianFacts"] = facts
            host["russianFactSources"] = sources
        else:
            host.pop("russianFacts", None)
            host.pop("russianFactSources", None)
        report[owner] = sources

    for step in steps.values():
        if step["kind"] == "REMEDIATE":
            mark(step, [step["body"]], owner_objective(step["id"]), step["id"], [])
    for item in pack["items"]:
        if not item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            continue
        participants = (item.get("assessment") or {}).get("relationParticipantIds", [])
        mark(item, [item["feedback"]] + [x for x in item.get("distractorRationales", []) if x], item["objectiveId"], item["id"],
             participants)
    if missing:
        raise SystemExit("Sentences without a disposition: " + json.dumps(missing, ensure_ascii=False, indent=1))

    # --- Coverage of new English wording (never a marker source) -----------------------------------------------
    shipped10 = {x["shippedText"] for x in R.values() if x["unitNumber"] == UNIT}
    base_items = {i["id"]: i for i in baseline["items"]}
    base_steps = {s["id"]: s for u in baseline["units"] if u["number"] == UNIT for se in u["sessions"] for s in se["steps"]}
    counter = {}

    def cover(objective: str, owner: str, text: str) -> None:
        if not text or text in shipped10:
            return
        counter[owner] = counter.get(owner, 0) + 1
        b.coverage(objective, f"{owner}-{counter[owner]}", text)
        shipped10.add(text)
    for step in steps.values():
        if step["kind"] in ("TEACH", "REMEDIATE") and step["body"] != base_steps.get(step["id"], {}).get("body"):
            cover(owner_objective(step["id"]), step["id"], step["body"])
    for item in pack["items"]:
        if not item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            continue
        old = base_items.get(item["id"], {})
        a, oa = item.get("assessment") or {}, old.get("assessment") or {}
        texts = ([item["feedback"]] + item.get("distractorRationales", []) + [a.get("whyAnswerDemonstratesObjective", "")]
                 + a.get("distractorDifference", []))
        olds = set([old.get("feedback", "")] + old.get("distractorRationales", []) + [oa.get("whyAnswerDemonstratesObjective", "")]
                   + oa.get("distractorDifference", []))
        for text in texts:
            if text and text not in olds:
                cover(item["objectiveId"], item["id"], text)
    for owner, sources in report.items():
        for sentence in sources:
            objective = items[owner]["objectiveId"] if owner in items else owner_objective(owner)
            cover(objective, owner, sentence)
    pack["provenanceRecords"].extend(x for x in b.new if x not in pack["provenanceRecords"])
    return report


def apply(pack: dict) -> tuple[dict, dict]:
    b, baseline, items, steps, objectives = part1(pack)
    part2(b, items, objectives)
    steps = part3(b, steps, objectives)
    report = part4(b, baseline, items, steps)
    return pack, report


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15e baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack, report = apply(loads(text))
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
