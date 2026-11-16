#!/usr/bin/env python3
"""T15d (Claude Code, owner direction 11/16/2026): Unit 9 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer. Russian comes only from frozen files under catalog/sources/ already in the source inventory:
Wikibooks Russian/Numbers oldid 4371358 (units-6-10/wikibooks/numbers-4371358.txt), Russian/Grammar/Genitive_case
oldid 4567338 (genitive-4567338.txt) and OpenRussian units-3-5/openrussian/nouns.csv (commit 50e210c4). No source is
added and no new Russian form is shown: every typed key is an already taught answer.

Plan: Codex's read-only preparation report audit/prep/units-09-10-prep-at-9a9b9760.txt (branch owner/prep-reports),
Unit 9 section only.
- A2-24: every Unit 9 check moves to an end-of-unit check session after all teaching; "admitted form" wording becomes an
  ordinary instruction (report section 1).
- A2-23: fifteen typed checks whose keys are taught table forms (report section 2).
- Within-unit duplicate: u9-i-pattern-3 shows пять лет (numbers-4371358.txt:121) instead of u9-i-quantity-4's
  шестнадцать рублей; its pinned rule-context tuple carries the new cue (owner direction 11/16/2026).
- Facts: every feedback, rationale and remediation sentence is marked with frozen-source citations or is a reviewed
  instruction; the 112 English-only inventory entries get the report's MARK, REWORD or REMOVE disposition.
- A2-26: governance wording leaves Unit 9 learner text.
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
BASELINE_SHA256 = "f3c516266d1fb2bfc124262e66319613e6af0b1f47a2061498c8c4ac76174b0f"
SRC = ROOT / "catalog/sources"
NUMBERS = SRC / "units-6-10/wikibooks/numbers-4371358.txt"
GENITIVE = SRC / "units-6-10/wikibooks/genitive-4567338.txt"
NOUNS = SRC / "units-3-5/openrussian/nouns.csv"
TASK = "T15d, Claude Code, 2026-11-16"
ACUTE = "́"
CYR = "Ѐ-ӿ"
UNIT = 9


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


def nouns_row(bare: str) -> tuple[int, dict]:
    with NOUNS.open(encoding="utf-8") as handle:
        rows = list(csv.DictReader(handle, delimiter="\t", quoting=csv.QUOTE_NONE))
    matches = [(index + 2, row) for index, row in enumerate(rows) if row["bare"] == bare]
    if len(matches) != 1:
        raise SystemExit(f"nouns.csv: {len(matches)} rows for {bare}")
    return matches[0]


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

    def noun_cell(self, objective: str, bare: str, field: str, key: str) -> str:
        """An OpenRussian nouns.csv cell record (the named field of the named frozen row)."""
        _, row = nouns_row(bare)
        original = row[field].strip()
        if not original or "," in original:
            raise SystemExit(f"nouns.csv:{bare}#{field} is not a single value: {original!r}")
        record = copy.deepcopy(self.records["prov-ex-or-u9-quantity-15"])
        record.update({"itemId": f"prov-t15d-u9-{key}", "objectiveId": objective, "sourceRecordId": f"nouns.csv:{bare}#{field}",
                       "originalSourceText": original, "shippedText": original, "sourceHash": sha(original),
                       "contentHash": sha(original), "recordType": "lexical record"})
        return self.add(record)["itemId"]

    def line(self, page: str, number: int) -> str:
        """A record of one whole line of a frozen Wikibooks page, cited by markers."""
        key = f"{page}{number}"
        record_id = f"prov-t15d-u9-{key}"
        if record_id in self.records:
            return record_id
        path, template, prefix = {"r": (NUMBERS, "prov-u9-quantity-rem", "numbers:"),
                                  "g": (GENITIVE, "prov-u9-pattern-rem", "genitive:")}[page]
        text = path.read_text(encoding="utf-8")
        needle = text.splitlines()[number - 1].strip()
        if not needle or text.count(needle) != 1:
            raise SystemExit(f"{path.name}:{number} is not a unique line")
        record = copy.deepcopy(self.records[template])
        record.update({"itemId": record_id, "sourceRecordId": prefix + needle[:80], "originalSourceText": needle,
                       "shippedText": needle, "sourceHash": sha(needle), "contentHash": sha(needle),
                       "recordType": "instruction"})
        return self.add(record)["itemId"]

    def coverage(self, objective: str, key: str, text: str) -> str:
        template = self.records["prov-t15an1-u6-possessive-our-your"]
        record = copy.deepcopy(template)
        note = (f"New English wording authored on 2026-11-16 ({TASK}); it did not exist before that date. It covers the"
                " wording only; the Russian facts it states are cited from frozen source records in the marker's"
                " russianFactSources. 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        record.update({"itemId": f"prov-ca-t15d-{key}", "unitNumber": UNIT, "objectiveId": objective,
                       "sourceRecordId": f"ca-coverage:{objective}:{key}", "shippedText": text,
                       "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      " this record's English text is new, authored 2026-11-16 for T15d."})
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


# Whole instructions, task directions and arithmetic that state no Russian fact (reviewed, as in Units 7 and 8).
NONFACTS = [
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    "Use the displayed counting example.",
    "Read the whole example before answering.",
    "Read the complete counting example.",
    "Match the grammatical gender of the noun in the counting phrase.",
    "Distinguish the teen value from a multiple of ten.",
    "Compare place value: 300 has three hundreds; 30 has three tens.",
    "Compare place value: 500 has five hundreds; 5 has five single units.",
    "Compare place value: 900 has nine hundreds; 90 has nine tens.",
    "Classify the complete number rather than treating every value above five as the same pattern.",
    "Compare the supplied 203 and 213 examples and the two kopeck examples.",
]

BOILERPLATE = (" Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent"
               " check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.")

# Teaching wording (remediation repeats it in the odindva, quantity and pattern sessions).
TEACH_REPLACE = {
    "u9-st-teach-odindva-4": [
        (" in the retained cardinal table.", " in the cardinal table."),
        (" The existing table stress is retained; the separate counting examples are printed without added stress.", ""),
    ],
    "u9-st-teach-quantity-8": [(" Count value and sentence construction govern the explanation.",
                                " Read the whole example before answering.")],
    "u9-st-teach-quantity-9": [(" They do not by themselves state a quantity or sentence context; apply the complete"
                                " counted-noun rules to whole examples.", " Read the complete counting example.")],
    # A statement of the forms replaces the "Choose один rather than одна" script (ANSWER_IN_INSTRUCTION).
    "u9-st-teach-odindva-6": [(" retain their supplied values. The additional source example один час supplies masculine"
                               " singular один with час. The independent masculine check uses this different noun from guided"
                               " мальчик. Choose один rather than feminine одна for час in this counting context; this is"
                               " supplied-form recognition, not all numeral cases or unrestricted sentence production.",
                               ". The additional source example один час uses masculine singular один with час; feminine"
                               " singular одна is the form shown with девушка.")],
    # u9-i-pattern-3 now shows пять лет, so the pattern teaching shows that taught example too.
    "u9-st-teach-pattern-3": [("шестнадцать рублей — sixteen rubles. ", "шестнадцать рублей — sixteen rubles. пять лет — пять лет. ")],
}
PATTERN_EXAMPLE = ("u9-st-teach-pattern-3", "ex-u9-quantity-10")

# Feedback and rationale wording: (item, field, old, new); field "feedback" or a rationale index.
ITEM_REPLACE = [
    ("u9-g-cardinal-0", "feedback", " Zero counts no units; one counts a single unit.", ""),
    ("u9-g-odindva-0", "feedback", "in the retained counting phrase.", "in the counting phrase."),
    ("u9-i-odindva-1", "feedback", "in the retained counting phrase.", "in the counting phrase."),
    ("u9-i-odindva-2", "feedback", "The retained lexical row classifies this noun as neuter;",
     "The dictionary classifies this noun as neuter;"),
    ("u9-i-odindva-3", "feedback", "The retained example means one pair of jeans;", "The example means one pair of jeans;"),
    ("u9-i-odindva-3", 1, "in the retained example.", "in the example."),
    ("u9-i-odindva-4", "feedback", "This recognizes the supplied counting form, not every declined case of the numeral.",
     "Use the displayed counting example."),
    ("u9-i-odindva-5", "feedback", "used in the retained phrase for two girls.", "used in the phrase for two girls."),
    ("u9-i-odindva-5", 1, "the feminine agreement used in the retained phrase.", "the feminine agreement used in the phrase."),
]

PROMPTS = {"Which admitted form matches the shown number?": "Which form matches the shown number?",
           "Which admitted form agrees with the shown word?": "Which form agrees with the shown word?"}

OBJECTIVE_TITLES = {"obj-u9-cardinal": "Numbers 0 through 10", "obj-u9-teens": "Numbers 11 through 20",
                    "obj-u9-tens": "Tens", "obj-u9-hundreds": "Hundreds", "obj-u9-thousands": "Thousands and compound numbers",
                    "obj-u9-odindva": "Forms of one and two", "obj-u9-quantity": "The noun after a number",
                    "obj-u9-pattern": "Counting patterns"}
SESSION_TITLES = {"u9-s-orient": "Numbers and quantities", **{
    f"u9-s-{name}": OBJECTIVE_TITLES[f"obj-u9-{name}"] for name in
    ("cardinal", "teens", "tens", "hundreds", "thousands", "odindva", "quantity", "pattern")}}
STEP_TITLES = {"Admitted lines": "Examples", "Admitted instruction": "Table entry",
               "Admitted number and quantity rows": "Numbers and quantities"}
ORIENT_BODY = ("This unit covers cardinal numbers from zero to the thousands, the forms of one and two, and the case of the noun"
               " after a number.")
TOPIC = ("Unit 9 covers cardinal numbers, the forms of one and two, and the case of the noun after a number, alongside"
         " Lesson 9 in your book.")
GUIDED_OLD = "Choose the admitted record. The feedback states the frozen field."
GUIDED_NEW = "Choose an answer, then read the feedback."
BOOK_OLD = "These frozen quantity examples are not a statement of a present-day amount."
BOOK_NEW = "The quantity examples here are not a statement of a present-day amount."
CHECK_BODY = "Answer without looking back. A miss returns to the explanation."

# Fifteen typed checks (report section 2): (id, objective, closed item whose taught answer is the key).
TYPED = [("u9-i-type-1", "obj-u9-cardinal", "u9-i-cardinal-1"), ("u9-i-type-5", "obj-u9-cardinal", "u9-i-cardinal-2"),
         ("u9-i-type-10", "obj-u9-cardinal", "u9-i-cardinal-3"), ("u9-i-type-4", "obj-u9-cardinal", "u9-i-cardinal-4"),
         ("u9-i-type-14", "obj-u9-teens", "u9-i-teens-1"), ("u9-i-type-19", "obj-u9-teens", "u9-i-teens-2"),
         ("u9-i-type-12", "obj-u9-teens", "u9-i-teens-3"), ("u9-i-type-50", "obj-u9-tens", "u9-i-tens-1"),
         ("u9-i-type-90", "obj-u9-tens", "u9-i-tens-2"), ("u9-i-type-20", "obj-u9-tens", "u9-i-tens-3"),
         ("u9-i-type-300", "obj-u9-hundreds", "u9-i-hundreds-1"), ("u9-i-type-500", "obj-u9-hundreds", "u9-i-hundreds-2"),
         ("u9-i-type-900", "obj-u9-hundreds", "u9-i-hundreds-3"), ("u9-i-type-21", "obj-u9-thousands", "u9-g-thousands-0"),
         ("u9-i-type-101", "obj-u9-thousands", "u9-i-thousands-1")]
TYPED_PROMPT = "Type the shown number in Russian words."
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
# The frozen numbers-page line of each typed key (report section 2).
TYPED_LINE = {"u9-i-type-1": 12, "u9-i-type-5": 36, "u9-i-type-10": 65, "u9-i-type-4": 30, "u9-i-type-14": 31,
              "u9-i-type-19": 61, "u9-i-type-12": 19, "u9-i-type-50": 38, "u9-i-type-90": 62, "u9-i-type-20": 20,
              "u9-i-type-300": 27, "u9-i-type-500": 39, "u9-i-type-900": 63, "u9-i-type-21": 73, "u9-i-type-101": 74}

PATTERN3_CUE = "пять лет"
PATTERN3_FEEDBACK = ("The value is 5; the last two digits do not fall in 11-through14, and the final digit is 5. In this basic"
                     " counting phrase, the counted noun is genitive plural. Classify the complete number rather than treating"
                     " every value above five as the same pattern.")


def tuple_text(cue: str, answer: str, distractors: list[str]) -> str:
    def field(value: str) -> str:
        return f"{len(value.encode('utf-8'))}:{value}"
    return f"tuple-v1:1;{field(cue)}{field(answer)}{len(distractors)};" + "".join(field(d) for d in distractors)


def edit(text: str, old: str, new: str, where: str) -> str:
    if text.count(old) != 1:
        raise SystemExit(f"{where}: baseline text changed ({old!r})")
    return text.replace(old, new)


def part1(pack: dict):
    b = Builder(pack)
    unit = b.unit
    baseline = copy.deepcopy(pack)
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}

    # --- Teaching and remediation ---------------------------------------------------------------------------------
    for step in steps.values():
        if BOILERPLATE in step["body"]:
            step["body"] = step["body"].replace(BOILERPLATE, "")
    for step_id, pairs in TEACH_REPLACE.items():
        for old, new in pairs:
            steps[step_id]["body"] = edit(steps[step_id]["body"], old, new, step_id)
    steps[PATTERN_EXAMPLE[0]]["exampleIds"].append(PATTERN_EXAMPLE[1])
    # Remediation repeats the whole teaching of its session (the five table sessions showed one table line).
    for session in unit["sessions"]:
        teach = [s for s in session["steps"] if s["kind"] == "TEACH"]
        for rem in (s for s in session["steps"] if s["kind"] == "REMEDIATE"):
            rem["body"] = "\n\n".join(s["body"] for s in teach)
            rem["exampleIds"] = list(dict.fromkeys(e for s in teach for e in s.get("exampleIds", [])))

    # --- Feedback, rationales and prompts -------------------------------------------------------------------------
    for item_id, field, old, new in ITEM_REPLACE:
        item = items[item_id]
        a = item["assessment"]
        if field == "feedback":
            item["feedback"] = edit(item["feedback"], old, new, item_id)
            if a.get("whyAnswerDemonstratesObjective") and old in a["whyAnswerDemonstratesObjective"]:
                a["whyAnswerDemonstratesObjective"] = a["whyAnswerDemonstratesObjective"].replace(old, new)
        else:
            previous = item["distractorRationales"][field]
            item["distractorRationales"][field] = edit(previous, old, new, item_id)
            a["distractorDifference"] = [d.replace(old, new) for d in a.get("distractorDifference", [])]
    for item in pack["items"]:
        if item["objectiveId"].startswith("obj-u9-") and item["prompt"] in PROMPTS:
            item["prompt"] = PROMPTS[item["prompt"]]
    return b, baseline, items, steps, objectives


def part2(b: Builder, items: dict, objectives: dict) -> list[dict]:
    R = b.records
    pack = b.pack

    # --- u9-i-pattern-3: a different taught example, so it no longer repeats u9-i-quantity-4 -------------------------
    item = items["u9-i-pattern-3"]
    a = item["assessment"]
    example = R["prov-ex-u9-quantity-10-ru"]
    if example["originalSourceText"] != PATTERN3_CUE or NUMBERS.read_text(encoding="utf-8").splitlines()[120] != "* пять лет":
        raise SystemExit("пять лет is not the frozen numbers-4371358.txt:121 example")
    context = [R[i] for i in a["relationParticipantIds"]]
    distractors = [o for o in item["options"] if o != item["answer"]]
    old_tuple = tuple_text(item["promptLetter"], item["answer"], distractors)
    new_tuple = tuple_text(PATTERN3_CUE, item["answer"], distractors)
    note = (f" {TASK}, owner direction 11/16/2026: the cue is now пять лет, the frozen example at"
            " catalog/sources/units-6-10/wikibooks/numbers-4371358.txt:121 (Wikibooks Russian/Numbers oldid 4371358), so"
            " this check no longer repeats u9-i-quantity-4; the answer and distractors are unchanged.")
    for record in context:
        text = record["originalSourceText"]
        if not text.endswith("\nTuple-v1:" + old_tuple):
            raise SystemExit("pattern-3 tuple baseline changed")
        text = text[: -len("\nTuple-v1:" + old_tuple)] + note + "\nTuple-v1:" + new_tuple
        record["originalSourceText"] = text
        record["sourceHash"] = sha(text)
        record.pop("originalSourceTextRef", None)
        record["synthesis"]["applications"] = [{"cue": PATTERN3_CUE, "answer": item["answer"], "distractors": distractors}]
    context[0]["shippedText"] = PATTERN3_CUE
    context[0]["contentHash"] = sha(PATTERN3_CUE)
    item["promptLetter"] = PATTERN3_CUE
    item["feedback"] = PATTERN3_FEEDBACK
    a["whyAnswerDemonstratesObjective"] = PATTERN3_FEEDBACK
    a["testedFeature"] = "FINAL_DIGIT_FIVE_PLUS"

    # --- A2-23: typed checks whose keys are taught answers --------------------------------------------------------
    lines = NUMBERS.read_text(encoding="utf-8").splitlines()
    typed = []
    for item_id, objective, closed_id in TYPED:
        closed = items[closed_id]
        key = closed["answer"]
        answer_record = R[closed["assessment"]["answerSourceIds"][0]]
        if key not in answer_record["shippedText"] or key not in lines[TYPED_LINE[item_id] - 1]:
            raise SystemExit(f"{item_id}: key {key} is not the frozen line {TYPED_LINE[item_id]}")
        typed.append(typed_item(item_id, objective, TYPED_PROMPT, closed["promptLetter"], key, TYPED_FEEDBACK,
                                "src-wikibooks-russian-cc-by-sa"))
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
    steps["u9-st-orient"]["body"] = ORIENT_BODY
    steps["u9-st-book-work"]["body"] = edit(steps["u9-st-book-work"]["body"], BOOK_OLD, BOOK_NEW, "u9-st-book-work")

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
    book = next(index for index, session in enumerate(unit["sessions"]) if session["id"] == "u9-s-book")
    unit["sessions"].insert(book, {"id": "u9-s-checks", "title": "Unit 9 checks", "estimatedMinutes": 25,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})
    unit["advancement"]["cumulativeReviewPolicyId"] = "unit9-prior-noun-behavior-plural-gender-v4"
    return {s["id"]: s for se in unit["sessions"] for s in se["steps"]}


# English-only sentences and the frozen lines that state them: ("r", n) = numbers-4371358.txt:n,
# ("g", n) = genitive-4567338.txt:n, ("n", bare, field) = OpenRussian nouns.csv cell (report section 3).
def r(*numbers): return [("r", n) for n in numbers]  # noqa: E704
def g(*numbers): return [("g", n) for n in numbers]  # noqa: E704


VALUE_LAST = "the last two digits do not fall in 11-through14, and the final digit is"
EXTRA: dict[str, list] = {
    "The other option gives only 20 and omits the final unit.": r(20, 73),
    "The shorter option gives 20 instead of 21; it omits the final unit.": r(20, 73),
    "The other option gives only 100 and omits the final unit.": r(15, 74),
    "The shorter option gives 100 instead of 101; it omits the final unit.": r(15, 74),
    "The other option gives only 200 and omits the final fifteen.": r(21, 75),
    "The shorter option gives 200 instead of 215; it omits the final fifteen.": r(21, 75),
    "The other option gives only 1,000 and omits the remaining 984.": r(16, 77),
    "The shorter option gives 1,000 instead of 1,984; it omits the remaining 984.": r(16, 77),
    "The shown group counts two, three or four thousands.": r(78),
    "In these whole-thousand examples, the counted word for thousand takes the genitive singular.": r(78),
    "The other option counts five through nine thousands and uses the genitive plural.": r(79),
    "These examples use the basic nominative or inanimate accusative counting construction.": r(78, 79, 86),
    "The other option counts five through nine thousands with genitive plural instead of the shown two, three or four"
    " thousands with genitive singular.": r(78, 79),
    "Both still mean one.": r(12, 88),
    "The following noun is genitive singular in this counting construction; the numeral choice still follows its gender.": r(90),
    "The plural numeral here does not mean several pairs; these are different gender/number forms of one.": r(88),
    "They are feminine and neuter singular forms, respectively.": r(12, 88),
    "The dictionary classifies this noun as neuter; choose by that grammatical gender.": [("n", "животное", "gender")],
    "The example means one pair of jeans; plural grammatical agreement does not mean several pairs here.": r(88),
    "Both forms mean one; the distinction is grammatical gender, not a different number.": r(12, 88),
    "Choose by grammatical gender here; both options express the same numeric value.": r(18, 90),
    f"The value is 1; {VALUE_LAST} 1.": r(94, 102, 109),
    "In this basic counting phrase, the counted noun is nominative singular.": r(86, 100, 102),
    "The genitive-singular explanation applies to final two, three or four outside the teen exception, rather than this"
    " shown counting pattern.": r(90, 92, 94, 104),
    "The genitive-plural explanation applies to zero, five-through-nine endings and the teen exception, rather than this"
    " shown counting pattern.": r(92, 94, 106),
    "These basic counting examples use nominative phrases.": r(86, 100),
    "The related noun-case pattern also applies in inanimate accusative contexts, with the appropriate noun form.": r(86, 100),
    "Other sentence cases require numeral declension as well as noun declension.": r(98),
    "A count ending in one outside the teen exception uses a nominative singular noun in the shown basic phrases.":
        r(92, 94, 102),
    "A count ending in two, three or four outside the teen exception uses a genitive singular noun in the basic construction.":
        r(90, 92, 94, 104),
    "This is a grammatical singular form even though the count is greater than one.": r(90),
    "Counts ending in zero or five through nine use genitive plural in the basic construction.": r(92, 94, 106),
    "The teen exception also uses genitive plural.": r(92),
    "Ten and sixteen therefore fit this pattern.": r(92),
    "For a compound count, inspect the last two digits first.": r(96),
    "If they are 11, 12, 13 or 14, use genitive plural for the counted noun in the basic construction.": r(92, 96),
    "Otherwise, the final digit selects the pattern: one, two-through-four, or zero/five-through-nine.": r(94, 102, 104, 106),
    "Compare 21 and 22: their final digits select different noun cases; being larger than five is not enough to choose"
    " genitive plural.": r(94, 112, 113),
    "All counts from 11 through19 use genitive plural here.": r(92),
    "For 15 through19 the final digit already leads to genitive plural;11 through14 override the one/two/three/four ending"
    " pattern.": r(92, 94, 96),
    "The example with 41 follows the final-one pattern and uses nominative singular for the counted noun.": r(94),
    "The example with 10 follows the zero-ending pattern and uses genitive plural.": r(92, 94),
    "The count 203 ends in 03, so final three selects genitive singular in the basic counting phrase.": r(96),
    "The count 213 ends in 13, so the teen exception selects genitive plural.": r(96),
    "The isolated table forms below illustrate genitive singular and genitive plural noun forms.":
        [("n", "стол", "sg_gen"), ("n", "стол", "pl_gen")],
    f"The value is 22; {VALUE_LAST} 2.": r(94, 113),
    "In this basic counting phrase, the counted noun is genitive singular.": r(90, 104),
    "The nominative-singular explanation applies to a final one outside the teen exception, rather than this shown counting"
    " pattern.": r(92, 94, 102),
    f"The value is 5; {VALUE_LAST} 5.": r(92),
    "In this basic counting phrase, the counted noun is genitive plural.": r(92, 106),
    f"The value is 21; {VALUE_LAST} 1.": r(94, 112),
    f"The value is 16; {VALUE_LAST} 6.": r(92),
    f"The value is 41; {VALUE_LAST} 1.": r(94),
    "The value is 213; its last two digits are 13, so the 11-through14 exception takes priority over the final digit.": r(96),
    f"The value is 10; {VALUE_LAST} 0.": r(92, 94),
    "A final zero follows the same noun-case pattern as ten.": r(94),
    f"The value is 2; {VALUE_LAST} 2.": r(90, 110),
    f"The value is 203; {VALUE_LAST} 3.": r(96),
    "In these basic examples, a final one outside the teen exception uses nominative singular; final two, three or four use"
    " genitive singular.": r(94, 102, 104),
    "All teen counts in the supplied basic examples take genitive plural.": r(92),
    "Five and sixteen both use genitive plural in these counting examples.": r(92),
    "The complete value matters: a compound count ending in one outside the teen exception has a different pattern.": r(94),
    "The I saw two friends sentence has an animate accusative object.": r(98),
    "Both the numeral and noun are declined; do not classify it with the basic nominative/inanimate accusative rule.": r(98),
    "The one-cat example below instead states existence in the basic nominative context.": g(8, 9),
    f"The value is 3; {VALUE_LAST} 3.": r(90),
    "The line means I saw two friends.": r(98),
    "The friends are an animate accusative object.": r(98),
    "Both the numeral and noun are declined, so the basic nominative or inanimate accusative counting pattern does not"
    " describe this sentence.": r(98),
    "The basic nominative/inanimate accusative pattern omits the animate accusative object and declined numeral in this"
    " sentence.": r(98),
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

    def resolve(ref, objective: str) -> str:
        if ref[0] == "n":
            existing = [x for x in R.values() if x["unitNumber"] == UNIT and x["sourceRecordId"] == f"nouns.csv:{ref[1]}#{ref[2]}"
                        and is_frozen(pack, x)]
            if existing:
                return existing[0]["itemId"]
            return b.noun_cell(objective, ref[1], ref[2], f"{ref[1]}-{ref[2]}".replace("_", "-").replace("животное", "zhivotnoe"))
        return b.line(ref[0], ref[1])

    frozen9 = lambda: [x for x in R.values() if x["unitNumber"] == UNIT and is_frozen(pack, x)]  # noqa: E731

    def cite_forms(sentence: str, objective: str, participants: list[str]) -> list[str]:
        out = []
        pool = frozen9()
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
                raise SystemExit(f"No frozen Unit 9 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    report = {}
    missing = []

    def mark(host: dict, texts: list[str], objective: str, owner: str, participants: list[str]) -> None:
        facts, sources = [], {}
        for text in texts:
            for sentence in sentences(text):
                if not any(c.isalpha() for c in sentence) or sentence in NONFACTS:
                    continue
                refs = EXTRA.get(sentence)
                if refs is None and not forms(sentence):
                    missing.append((owner, sentence))
                    continue
                ids = [resolve(ref, objective) for ref in refs or []] + cite_forms(sentence, objective, participants)
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
    shipped9 = {x["shippedText"] for x in R.values() if x["unitNumber"] == UNIT}
    base_items = {i["id"]: i for i in baseline["items"]}
    base_steps = {s["id"]: s for u in baseline["units"] if u["number"] == UNIT for se in u["sessions"] for s in se["steps"]}
    counter = {}

    def cover(objective: str, owner: str, text: str) -> None:
        if not text or text in shipped9:
            return
        counter[owner] = counter.get(owner, 0) + 1
        b.coverage(objective, f"{owner}-{counter[owner]}", text)
        shipped9.add(text)
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
        raise SystemExit("Asset is not the T15d baseline; this transformer runs only once")
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
