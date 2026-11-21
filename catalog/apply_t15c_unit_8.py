#!/usr/bin/env python3
"""T15c (Claude Code, owner direction 11/16/2026): Unit 8 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer. Russian comes only from frozen files under catalog/sources/ already in the source inventory:
OpenRussian rows (units-3-5/openrussian/nouns.csv and units-6-10/openrussian/adjectives.csv, commit 50e210c4) and the
Wikibooks pages adjectives-4292379, genitive-4567338, gender-4310736 and pronouns-4585698. No source is added.

- A2-24: every Unit 8 check moves to an end-of-unit check session after all teaching; cue-only checks get a third form
  from the same OpenRussian row (prep report audit/prep/unit-08-prep-at-9a9b9760.txt, section 1). u8-i-agreement-3 keeps
  its pinned content-audit rule context; owner decision 11/16/2026 re-pins that tuple with the third form си́няя.
- A2-23: nine typed checks whose keys are the taught nominative plural cells (prep report section 2).
- Facts: every feedback, rationale and remediation sentence is marked with frozen-source citations or is a reviewed
  instruction; the 47 English-only inventory entries are marked, reworded or removed (prep report section 3).
- A2-26: governance wording and wiki markup leave Unit 8 learner text.
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
BASELINE_SHA256 = "999d11efae578bcb7abd569f5e7c67b2ddb2d45982e8c46a3d7c8360718d25aa"
SRC = ROOT / "catalog/sources"
ADJ = SRC / "units-6-10/openrussian/adjectives.csv"
NOUNS = SRC / "units-3-5/openrussian/nouns.csv"
ADJ_PAGE = SRC / "units-6-10/wikibooks/adjectives-4292379.txt"
GEN_PAGE = SRC / "units-6-10/wikibooks/genitive-4567338.txt"
GENDER_PAGE = SRC / "units-3-5/wikibooks/gender-4310736.txt"
PRON_PAGE = SRC / "units-3-5/wikibooks/pronouns-4585698.txt"
TASK = "T15c, Claude Code, 2026-11-16"
ACUTE = "́"
CYR = "Ѐ-ӿ"
UNIT = 8


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


def csv_rows(path: Path) -> list[dict]:
    with path.open(encoding="utf-8") as handle:
        return list(csv.DictReader(handle, delimiter="\t", quoting=csv.QUOTE_NONE))


ROWS = {"adjectives.csv": csv_rows(ADJ), "nouns.csv": csv_rows(NOUNS)}


def cell(file: str, bare: str, field: str) -> str:
    matches = [r for r in ROWS[file] if r["bare"] == bare]
    if len(matches) != 1:
        raise SystemExit(f"{file}: {len(matches)} rows for {bare}")
    value = matches[0][field].strip()
    if not value or ("," in value and field != "translations_en"):
        raise SystemExit(f"Frozen {file}:{bare}#{field} is not a single form: {value!r}")
    return value


LATIN = dict(zip("абвгдеёжзийклмнопрстуфхцчшщъыьэюя",
                 ["a", "b", "v", "g", "d", "e", "yo", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u",
                  "f", "kh", "ts", "ch", "sh", "shch", "", "y", "", "e", "yu", "ya"]))


def translit(word: str) -> str:
    return "".join(LATIN[c] for c in word)


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

    def lexical(self, objective: str, file: str, bare: str, field: str) -> str:
        """An OpenRussian cell record for [objective]; reuses an existing Unit 8 record of the same cell."""
        source_record = f"{file}:{bare}#{field}"
        for record in self.records.values():
            if (record["unitNumber"] == UNIT and record["objectiveId"] == objective and record["sourceRecordId"] == source_record
                    and record["sourceId"] == "src-openrussian-50e210c4" and record.get("synthesis") is None
                    and record["shippedText"] == (stressed(record["originalSourceText"]) if field != "translations_en"
                                                  else record["originalSourceText"])):
                return record["itemId"]
        template = self.records["prov-ex-or-u8-plural-0"]
        original = cell(file, bare, field)
        shipped = stressed(original) if field != "translations_en" else original
        record = copy.deepcopy(template)
        record.update({"itemId": f"prov-t15c-u8-{objective[7:]}-{translit(bare)}-{field}".replace("_", "-"), "objectiveId": objective,
                       "sourceRecordId": source_record,
                       "sourceUrl": re.sub(r"(adjectives|nouns|others)\.csv", file, template["sourceUrl"]),
                       "originalSourceText": original, "shippedText": shipped, "sourceHash": sha(original),
                       "contentHash": sha(shipped), "recordType": "lexical record"})
        return self.add(record)["itemId"]

    def span(self, key: str, objective: str, template_id: str, path: Path, needle: str, prefix: str,
             url: str | None = None, revision: str | None = None) -> str:
        """A record of one exact span of a frozen page line."""
        text = path.read_text(encoding="utf-8")
        if text.count(needle) != 1:
            raise SystemExit(f"Span {key} occurs {text.count(needle)} times in {path.name}")
        template = self.records[template_id]
        record = copy.deepcopy(template)
        record.update({"itemId": f"prov-t15c-u8-{key}", "objectiveId": objective, "sourceRecordId": prefix + needle[:80],
                       "originalSourceText": needle, "shippedText": needle, "sourceHash": sha(needle),
                       "contentHash": sha(needle), "recordType": "instruction"})
        if url:
            record["sourceUrl"] = url
        if revision:
            record["sourceRevision"] = revision
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
        record.update({"itemId": f"prov-ca-t15c-{key}", "unitNumber": UNIT, "objectiveId": objective,
                       "sourceRecordId": f"ca-coverage:{objective}:{key}", "shippedText": text,
                       "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      " this record's English text is new, authored 2026-11-16 for T15c."})
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


# Whole instructions that state no Russian fact (reviewed, as Unit 7's reviewedNonFacts).
NONFACTS = [
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    "Copy the displayed form carefully.",
    "Use the forms taught here.",
    "Practise the displayed pairs.",
    "Copy the whole displayed form.",
    "Use this taught pair for the item.",
    "Review these seven pairs.",
    "This lesson compares singular and plural nominative forms, then explains the spelling in the plurals.",
    "Read the requested form before answering.",
    "Identify number from the requested form and context.",
    "Choose the requested form.",
    "Compare the examples.",
    "Read the whole phrase before answering.",
    "Practise the shown forms.",
    "Compare the shown singular and plural forms.",
    "Recognize what exists and where the line places it.",
    "Keep each whole source line with its own supplied translation.",
    "Distinguish that noun from the location phrase.",
    "These two supplied examples do not establish an unrestricted sentence-generation rule.",
]

BOILERPLATE = (" Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent"
               " check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.")

TEACH = {
    "u8-st-teach-plural-1": ("сто́л is singular nominative; столы́ is plural nominative. In the selected table sense, the plural"
                             " refers to tables. Copy the displayed form carefully."),
    "u8-st-teach-plural-2": ("Each noun retains its dictionary gender; plural adjective agreement does not require separate"
                             " masculine, feminine and neuter plural endings. Use the forms taught here."),
    "u8-st-teach-plural-3": ("го́род is singular nominative; города́ is plural nominative. The noun го́род means town. The plural"
                             " ending differs from the table pair and its supplied stress is on the final syllable. Practise the"
                             " displayed pairs."),
    "u8-st-teach-plural-4": ("кни́га is singular nominative; кни́ги is plural nominative. The noun кни́га means book. The supplied"
                             " plural retains its stress. Its final vowel follows the stem’s last consonant, one of the seven"
                             " spelling-rule letters taught in the later spelling session."),
    "u8-st-teach-plural-5": ("окно́ is singular nominative; о́кна is plural nominative. The noun окно́ means window. Notice the"
                             " supplied stress moves to the first syllable. Copy the whole displayed form."),
    "u8-st-teach-plural-6": ("ма́ма is singular nominative; ма́мы is plural nominative. The noun ма́ма means mom. The ending"
                             " changes while the supplied stress stays in place. Use this taught pair for the item."),
    "u8-st-teach-plural-7": ("до́м is singular nominative; дома́ is plural nominative. The noun до́м means house. The stress"
                             " moves to the ending in дома́."),
    "u8-st-teach-plural-8": ("сло́во is singular nominative; слова́ is plural nominative. The noun сло́во means word. Copy the"
                             " displayed form carefully. Review these seven pairs."),
    "u8-st-teach-spelling-1": ("Adjectives agree with their nouns in gender, number and case. In хорошее письмо, the adjective"
                               " describes a neuter singular noun. хоро́ший is the dictionary form: masculine singular nominative."
                               " This lesson compares singular and plural nominative forms, then explains the spelling in the"
                               " plurals."),
    "u8-st-teach-spelling-2": ("After г, к, х, ж, ч, ш, or щ, write и rather than ы. The seven-letter condition is required. This"
                               " rule applies to the spelling of noun and adjective endings. In кни́ги, и follows г; in хоро́шие,"
                               " и follows ш. Read the requested form before answering."),
    "u8-st-teach-spelling-3": ("кни́га means book in the singular, and кни́ги means books in the plural. To form this plural,"
                               " replace the singular ending with the plural ending: кни́га becomes кни́ги. The stem ends in г, so"
                               " the seven-letter rule requires и. In the other direction, кни́ги becomes кни́га in the singular"
                               " nominative."),
    "u8-st-teach-spelling-5": ("хоро́шее is neuter singular nominative; хоро́шие is plural nominative. The neuter singular form"
                               " хоро́шее becomes хоро́шие in the plural nominative, and хоро́шие becomes хоро́шее in the neuter"
                               " singular. The masculine singular dictionary form is хоро́ший. Choose the requested form."),
    "u8-st-teach-irregular-1": "пять столов — five tables. брат — brother.",
    "u8-st-teach-agreement-1": "синие книги — the dark blue books. Наши — Our. Compare the examples.",
    "u8-st-teach-agreement-2": ("In the nominative example синие книги — the dark blue books, the adjective modifies a plural"
                                " noun. Read the whole phrase before answering."),
    "u8-st-teach-agreement-3": ("мо́й and мои́ both retain the meaning my. мо́й is masculine singular nominative and мои́ is plural"
                                " nominative. The requested plural is a number distinction, not a different owner or a change"
                                " of English meaning."),
    "u8-st-teach-agreement-4": ("но́вый and но́вые both mean new. The masculine singular nominative form is но́вый; the plural"
                                " nominative is но́вые."),
    "u8-st-teach-agreement-5": ("э́та and э́ти are forms of the same demonstrative, meaning this. In ordinary English the plural"
                                " demonstrative reading is these. э́та is feminine singular nominative; э́ти is plural nominative."
                                " The plural does not have a feminine-only gender."),
    "u8-st-teach-agreement-6": ("синий is the masculine singular form and синие, as in синие книги, is the plural form of the"
                                " same adjective meaning dark blue. си́няя is the feminine singular form. Practise the shown"
                                " forms."),
}

TITLES = {"obj-u8-irregular": "Irregular plural nouns", "obj-u8-agreement": "Plural adjective forms"}
SESSION_TITLES = {"u8-s-orient": "Plurals, spelling and existence", "u8-s-plural": "Nominative plural nouns",
                  "u8-s-irregular": "Irregular plural nouns", "u8-s-agreement": "Plural adjective forms"}

FIELD_NAME = {"sg_nom": "singular nominative", "pl_nom": "plural nominative", "sg_dat": "singular dative",
              "decl_m_nom": "masculine singular nominative", "decl_f_nom": "feminine singular nominative",
              "decl_n_nom": "neuter singular nominative", "decl_pl_nom": "plural nominative"}

# Cue-only checks: a third option from the same OpenRussian row (prep report section 1).
THIRD = {"u8-i-plural-1": ("nouns.csv", "город", "sg_dat"), "u8-i-plural-2": ("nouns.csv", "окно", "sg_dat"),
         "u8-i-plural-3": ("nouns.csv", "мама", "sg_dat"), "u8-i-plural-4": ("nouns.csv", "дом", "sg_dat"),
         "u8-i-plural-5": ("nouns.csv", "слово", "sg_dat"), "u8-i-plural-6": ("nouns.csv", "книга", "sg_dat"),
         "u8-i-spelling-1": ("nouns.csv", "книга", "sg_dat"), "u8-i-spelling-2": ("adjectives.csv", "хороший", "decl_f_nom"),
         "u8-i-spelling-3": ("adjectives.csv", "хороший", "decl_m_nom"), "u8-i-spelling-4": ("adjectives.csv", "хороший", "decl_f_nom"),
         "u8-i-irregular-1": ("nouns.csv", "человек", "sg_dat"), "u8-i-irregular-2": ("nouns.csv", "друг", "sg_dat"),
         "u8-i-irregular-3": ("nouns.csv", "сын", "sg_dat"), "u8-i-agreement-1": ("adjectives.csv", "мой", "decl_f_nom"),
         "u8-i-agreement-2": ("adjectives.csv", "этот", "decl_n_nom"),
         # The guided spelling item repeats u8-i-plural-6's task (RepeatedTaskCreditTest), so it shows the same three forms.
         "u8-g-spelling-0": ("nouns.csv", "книга", "sg_dat")}

# Nine typed checks (prep report section 2): (id, objective, noun row).
TYPED = [("u8-i-type-goroda", "obj-u8-plural", "город"), ("u8-i-type-okna", "obj-u8-plural", "окно"),
         ("u8-i-type-mamy", "obj-u8-plural", "мама"), ("u8-i-type-doma", "obj-u8-plural", "дом"),
         ("u8-i-type-slova", "obj-u8-plural", "слово"), ("u8-i-type-knigi", "obj-u8-plural", "книга"),
         ("u8-i-type-lyudi", "obj-u8-irregular", "человек"), ("u8-i-type-druzya", "obj-u8-irregular", "друг"),
         ("u8-i-type-synovya", "obj-u8-irregular", "сын")]
TYPED_PROMPT = "Type the plural nominative form of the shown noun."
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."

AGREEMENT3_FEEDBACK = ("синий is the masculine singular nominative form; синие is the plural nominative form of the same"
                       " adjective meaning dark blue. In the nominative example синие книги, the modifier is plural. Compare the"
                       " shown singular and plural forms.")
AVAILABILITY2_FEEDBACK = ("На э́той у́лице есть францу́зы means There are Frenchmen on this street. The other meaning concerns"
                          " different things in a different place.")


def tuple_text(cue: str, answer: str, distractors: list[str]) -> str:
    def field(value: str) -> str:
        return f"{len(value.encode('utf-8'))}:{value}"
    return (f"tuple-v1:1;{field(cue)}{field(answer)}{len(distractors)};" + "".join(field(d) for d in distractors))


def part1(pack: dict):
    b = Builder(pack)
    unit = b.unit
    baseline = copy.deepcopy(pack)
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R = b.records

    # --- Frozen spans cited by markers (exact text of the named frozen line) -------------------------------------
    spans = {}
    adj_url = R["prov-ex-u8-agreement-0-ru"]["sourceUrl"]

    def page_span(key, objective, path, needle, title, revision, template, prefix):
        url = adj_url.replace("Russian/Grammar/Adjectives", title).replace("4292379", revision)
        spans[key] = b.span(key, objective, template, path, needle, prefix, url=url, revision=revision)
    A, ADJT = "Russian/Grammar/Adjectives", "prov-ex-u8-agreement-0-ru"
    page_span("w1", "obj-u8-spelling", ADJ_PAGE, "The endings of adjectives change according to the gender, number, and case of"
              " the nouns they modify.", A, "4292379", ADJT, "adjectives:")
    page_span("w6", "obj-u8-spelling", ADJ_PAGE, "The default form of an adjective is its nominative, masculine, singular form,"
              " and this is the form given in dictionaries.", A, "4292379", ADJT, "adjectives:")
    page_span("w14", "obj-u8-agreement", ADJ_PAGE, "With plural nouns, regardless of gender, the adjective replaces '''-ый''' or"
              " '''-ой''' with '''-ые'''", A, "4292379", ADJT, "adjectives:")
    page_span("w14b", "obj-u8-agreement", ADJ_PAGE, "\"these good new students\" is '''эти хорошие новые студенты'''", A,
              "4292379", ADJT, "adjectives:")
    page_span("w24", "obj-u8-spelling", ADJ_PAGE, "When forming the nominative plural of nouns, recall that if the stem ends in"
              " '''г, ж, к, х, ч, ш''', or '''щ''', you add '''и''', not '''ы'''. This 7-letter spelling rule also applies to"
              " adjectives.", A, "4292379", ADJT, "adjectives:")
    G, GENT = "Russian/Grammar/Genitive_case", "prov-u8-irregular-rem"
    gen_lines = GEN_PAGE.read_text(encoding="utf-8").splitlines()
    page_span("g49", "obj-u8-availability", GEN_PAGE, gen_lines[48], G, "4567338", GENT, "genitive:")
    page_span("g50", "obj-u8-availability", GEN_PAGE, gen_lines[49], G, "4567338", GENT, "genitive:")
    page_span("g51", "obj-u8-availability", GEN_PAGE, gen_lines[50], G, "4567338", GENT, "genitive:")
    gender_lines = GENDER_PAGE.read_text(encoding="utf-8").splitlines()
    d9 = gender_lines[8].split(" But why")[0]
    d17 = re.search(r"However, the important thing.*?independent\s+of gender\.", gender_lines[16]).group(0)
    page_span("d9", "obj-u8-plural", GENDER_PAGE, d9, "Russian/Grammar/Gender", "4310736", "prov-u8-plural-rem", "gender:")
    page_span("d17", "obj-u8-plural", GENDER_PAGE, d17, "Russian/Grammar/Gender", "4310736", "prov-u8-plural-rem", "gender:")
    page_span("p81", "obj-u8-agreement", PRON_PAGE, "Unlike personal pronouns, but like adjectives, possessive pronouns also"
              " change to match the gender and number of the noun they modify.", "Russian/Grammar/Pronouns", "4585698", ADJT,
              "pronouns:")

    # --- A2-26: markup leaves the shipped wiki span (the frozen original keeps it) ------------------------------
    record = R["prov-u8-irregular-rem"]
    old = record["shippedText"]
    record["shippedText"] = old.replace("'''", "")
    record["contentHash"] = sha(record["shippedText"])
    for step_id in ("u8-st-teach-irregular-2",):
        if steps[step_id]["body"] != old:
            raise SystemExit(f"{step_id} baseline changed")
        steps[step_id]["body"] = record["shippedText"]

    # --- Teaching bodies ------------------------------------------------------------------------------------------
    for step in steps.values():
        if BOILERPLATE in step["body"]:
            step["body"] = step["body"].replace(BOILERPLATE, "")
    for step_id, body in TEACH.items():
        steps[step_id]["body"] = body
    # Remediation repeats the whole teaching of its session.
    for session in unit["sessions"]:
        teach = [s for s in session["steps"] if s["kind"] == "TEACH"]
        for rem in (s for s in session["steps"] if s["kind"] == "REMEDIATE"):
            rem["body"] = "\n\n".join(s["body"] for s in teach)
            rem["exampleIds"] = list(dict.fromkeys(e for s in teach for e in s.get("exampleIds", [])))
    return b, baseline, items, steps, objectives, spans


def field_of(record: dict) -> str:
    return record["sourceRecordId"].rsplit("#", 1)[-1]


def part2(b: Builder, items: dict, steps: dict, objectives: dict) -> list[dict]:
    R = b.records
    pack = b.pack

    # --- A2-24 cue-only checks: a third form from the same OpenRussian row ----------------------------------------
    for item_id, (file, row, field) in THIRD.items():
        item = items[item_id]
        a = item["assessment"]
        record_id = b.lexical(item["objectiveId"], file, row, field)
        answer = R[a["answerSourceIds"][0]]
        answer_field = field_of(answer)
        if not answer["sourceRecordId"].startswith(f"{file}:{row}#"):
            raise SystemExit(f"{item_id}: third form is not from the answer's row")
        if field == answer_field or R[record_id]["shippedText"] in item["options"]:
            raise SystemExit(f"{item_id}: third form {field} is not a new distractor")
        rationale = (f"{R[record_id]['shippedText']} is the {FIELD_NAME[field]} form, so it does not supply the requested"
                     f" {FIELD_NAME[answer_field]} form.")
        item["options"].append(R[record_id]["shippedText"])
        item["distractorRationales"].append(rationale)
        a["contrastMembers"].append(R[record_id]["shippedText"])
        a["distractorDifference"].append(rationale)
        a["distractorSourceIds"].append(record_id)
        a["relationParticipantIds"].append(record_id)

    # --- u8-i-agreement-3: owner decision 11/16/2026 re-pins its rule-context tuple with the third form си́няя -------
    item = items["u8-i-agreement-3"]
    a = item["assessment"]
    syn = b.lexical("obj-u8-agreement", "adjectives.csv", "синий", "decl_f_nom")
    b.lexical("obj-u8-agreement", "adjectives.csv", "синий", "translations_en")
    third = R[syn]["shippedText"]
    context = [R[i] for i in a["relationParticipantIds"]]
    old_tuple = tuple_text(item["promptLetter"], item["answer"], [o for o in item["options"] if o != item["answer"]])
    new_tuple = tuple_text(item["promptLetter"], item["answer"], [o for o in item["options"] if o != item["answer"]] + [third])
    added = copy.deepcopy(context[2])
    added.update({"itemId": "prov-ca-u8-agreement-transfer-u8-i-agreement-3-3", "shippedText": third, "contentHash": sha(third)})
    added["synthesis"]["objectiveOrClaimBinding"] = "obj-u8-agreement:transfer:u8-i-agreement-3-3"
    b.add(added)
    note = (f" {TASK}, owner decision 11/16/2026: the third option {third} is the feminine singular nominative cell of the"
            " frozen OpenRussian adjectives.csv row синий (commit 50e210c4), added for A2-24; the cue, key and first distractor"
            " are unchanged.")
    for record in context + [added]:
        text = record["originalSourceText"]
        if not text.endswith("\nTuple-v1:" + old_tuple):
            raise SystemExit("agreement-3 tuple baseline changed")
        text = text[: -len("\nTuple-v1:" + old_tuple)] + note + "\nTuple-v1:" + new_tuple
        record["originalSourceText"] = text
        record["sourceHash"] = sha(text)
        record["synthesis"]["applications"] = [{"cue": item["promptLetter"], "answer": item["answer"],
                                                "distractors": [o for o in item["options"] if o != item["answer"]] + [third]}]
    rationale = f"{third} is the feminine singular nominative form, so it does not provide the requested plural nominative form."
    item["options"].append(third)
    item["distractorRationales"].append(rationale)
    item["feedback"] = AGREEMENT3_FEEDBACK
    a.update({"whyAnswerDemonstratesObjective": AGREEMENT3_FEEDBACK})
    a["contrastMembers"].append(third)
    a["distractorDifference"].append(rationale)
    a["distractorSourceIds"].append(added["itemId"])
    a["relationParticipantIds"].append(added["itemId"])

    # --- u8-i-availability-2: feedback without source narration ----------------------------------------------------
    av2 = items["u8-i-availability-2"]
    av2["feedback"] = AVAILABILITY2_FEEDBACK
    av2["assessment"]["whyAnswerDemonstratesObjective"] = AVAILABILITY2_FEEDBACK

    # --- A2-23: typed checks ------------------------------------------------------------------------------------
    typed = []
    for item_id, objective, row in TYPED:
        cue = b.lexical(objective, "nouns.csv", row, "sg_nom")
        key = b.lexical(objective, "nouns.csv", row, "pl_nom")
        typed.append(typed_item(item_id, objective, TYPED_PROMPT, R[cue]["shippedText"], R[key]["shippedText"], TYPED_FEEDBACK,
                                "src-openrussian-50e210c4"))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    return typed


def part3(b: Builder, items: dict, steps: dict, objectives: dict) -> dict:
    unit = b.unit
    # --- A2-26 titles and instruction text ------------------------------------------------------------------------
    for objective_id, title in TITLES.items():
        objectives[objective_id]["title"] = title
    unit["topicSummary"] = ("Unit 8 covers nominative plural nouns, the seven-letter spelling rule, plural adjective forms and"
                            " positive statements that something exists, alongside Lesson 8 in your book.")
    orient = steps["u8-st-orient"]
    orient["title"] = SESSION_TITLES["u8-s-orient"]
    orient["body"] = ("This unit covers nominative plural nouns, the seven-letter spelling rule, plural adjective forms and"
                      " positive statements that something exists.")
    for session in unit["sessions"]:
        if session["id"] in SESSION_TITLES:
            session["title"] = SESSION_TITLES[session["id"]]
    for step in steps.values():
        if step["title"] == "Admitted lines":
            step["title"] = "Examples"
        elif step["title"] == "Admitted instruction":
            step["title"] = "One table, five tables"
        if step["kind"] == "GUIDED" and step["body"] == "Choose the admitted record. The feedback states the frozen field.":
            step["body"] = "Choose an answer, then read the feedback."

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
        step["body"] = "Answer without looking back. A miss returns to the explanation."
    book = next(index for index, session in enumerate(unit["sessions"]) if session["id"] == "u8-s-book")
    unit["sessions"].insert(book, {"id": "u8-s-checks", "title": "Unit 8 checks", "estimatedMinutes": 20,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})
    return {s["id"]: s for se in unit["sessions"] for s in se["steps"]}


LEX = lambda file, bare, field: ("lex", file, bare, field)  # noqa: E731
G49, G50, G51 = "span:g49", "span:g50", "span:g51"
EXTRA: dict[str, list] = {
    # plural teaching (repeated in remediation)
    "In the selected table sense, the plural refers to tables.": ["prov-ex-or-u8-plural-1", "prov-ex-or-u8-plural-1-gloss"],
    "Each noun retains its dictionary gender; plural adjective agreement does not require separate masculine, feminine and neuter plural endings.":
        ["span:d9", "span:d17", "prov-u8-plural-rem", "span:w14"],
    "The plural ending differs from the table pair and its supplied stress is on the final syllable.":
        ["prov-ex-or-u8-plural-3", "prov-ex-or-u8-plural-1"],
    "The supplied plural retains its stress.": ["prov-ex-or-u8-plural-4", "prov-ex-or-u8-plural-5"],
    "Its final vowel follows the stem’s last consonant, one of the seven spelling-rule letters taught in the later spelling session.":
        ["prov-ex-or-u8-plural-5", "span:w24"],
    "Notice the supplied stress moves to the first syllable.": ["prov-ex-or-u8-plural-6", "prov-ex-or-u8-plural-7"],
    "The ending changes while the supplied stress stays in place.": ["prov-ex-or-u8-plural-8", "prov-ex-or-u8-plural-9"],
    "The stress moves to the ending in дома́.": ["prov-ex-or-u8-plural-10", "prov-ex-or-u8-plural-11"],
    # spelling teaching
    "Adjectives agree with their nouns in gender, number and case.": ["span:w1"],
    "In хорошее письмо, the adjective describes a neuter singular noun.": ["prov-ex-u8-spelling-0-ru", LEX("nouns.csv", "письмо", "gender")],
    "хоро́ший is the dictionary form: masculine singular nominative.": ["prov-ex-or-u8-spelling-1", "span:w6"],
    "After г, к, х, ж, ч, ш, or щ, write и rather than ы.": ["span:w24"],
    "The seven-letter condition is required.": ["span:w24"],
    "This rule applies to the spelling of noun and adjective endings.": ["span:w24"],
    "In кни́ги, и follows г; in хоро́шие, и follows ш.": ["span:w24"],
    "The stem ends in г, so the seven-letter rule requires и.": ["span:w24"],
    "дни is also plural nominative and means days.": ["prov-ex-or-u8-spelling-5", "prov-ex-or-u8-spelling-5-gloss"],
    "Its и does not follow a letter in the seven-letter set, so дни is not an example of that spelling replacement.":
        ["span:w24", "prov-ex-or-u8-spelling-5"],
    "Not every plural ending in и comes from the seven-letter rule.": ["span:w24", "prov-ex-or-u8-spelling-5"],
    "The masculine singular dictionary form is хоро́ший.": ["prov-ex-or-u8-spelling-1", "span:w6"],
    "кни́га means book in the singular, and кни́ги means books in the plural.":
        ["prov-ex-or-u8-spelling-3", "prov-ex-or-u8-spelling-3-gloss", "prov-ex-or-u8-spelling-4"],
    # irregular
    "Table, five tables - Стол, пять столов": ["prov-u8-irregular-rem"],
    "пять столов — five tables.": ["prov-ex-u8-irregular-0-ru", "prov-ex-u8-irregular-0-en"],
    # agreement
    "синие книги — the dark blue books.": ["prov-ex-u8-agreement-0-ru", "prov-ex-u8-agreement-0-en"],
    "Наши — Our.": ["prov-ex-u8-agreement-1-ru", "prov-ex-u8-agreement-1-en"],
    "In the nominative example синие книги — the dark blue books, the adjective modifies a plural noun.":
        ["prov-ex-u8-agreement-0-ru", "prov-ex-u8-agreement-0-en", "prov-u8-agreement-transfer-native-plural"],
    "The requested plural is a number distinction, not a different owner or a change of English meaning.":
        ["prov-ex-or-u8-agreement-2-gloss", "prov-ex-or-u8-agreement-3", "prov-ex-or-u8-agreement-3-gloss", "span:p81"],
    "In ordinary English the plural demonstrative reading is these.": ["prov-ex-or-u8-agreement-7", "span:w14b"],
    "The plural does not have a feminine-only gender.": ["span:w14", "prov-ex-or-u8-agreement-7"],
    "синий is the masculine singular form and синие, as in синие книги, is the plural form of the same adjective meaning dark blue.":
        ["prov-u8-agreement-transfer-native-singular", "prov-u8-agreement-transfer-native-plural", "prov-ex-u8-agreement-0-ru",
         LEX("adjectives.csv", "синий", "translations_en")],
    "синий is the masculine singular nominative form; синие is the plural nominative form of the same adjective meaning dark blue.":
        ["prov-u8-agreement-transfer-native-singular", "prov-u8-agreement-transfer-native-plural",
         LEX("adjectives.csv", "синий", "translations_en")],
    "In the nominative example синие книги, the modifier is plural.":
        ["prov-ex-u8-agreement-0-ru", "prov-u8-agreement-transfer-native-plural"],
    "синий is the supplied masculine singular form, so it does not provide the requested plural nominative form.":
        ["prov-u8-agreement-transfer-native-singular", "prov-u8-agreement-transfer-native-plural"],
    # availability
    "На э́той у́лице есть францу́зы — There are Frenchmen on this street.": [G50],
    "В Москве́ есть ста́рые зда́ния — There are old buildings in Moscow.": [G51],
    "This positive line states that something exists at the named place.": [G49, G50],
    "It is a statement, not a question asking where something is.": [G49, G50],
    "This line states that old buildings exist in Moscow.": [G51],
    "Compare its full meaning with the street example: the things and the named place differ.": [G50, G51],
    "In both supplied positive statements, the noun naming what exists is nominative.": [G49, G50, G51],
    "The previously introduced prepositional case is not the case of the noun naming what exists here.": [G49, G51],
    "The supplied line states that Frenchmen exist on this street; it is a statement, not a location question.": [G49, G50],
    "The supplied line states that old buildings exist in Moscow; it is a statement, not a location question.": [G49, G51],
    "На э́той у́лице есть францу́зы means There are Frenchmen on this street.": [G50],
    "The other meaning concerns different things in a different place.": [G50, G51],
    "The other supplied meaning concerns old buildings in Moscow rather than Frenchmen on this street.": [G50, G51],
    "The noun naming the old buildings is nominative in this positive existence statement.": [G49, G51],
    "The location phrase is a separate part of the line.": [G49, G51],
    "This alternative confuses the location phrase with the noun naming what exists.": [G49, G51],
}
# The same sentence cites the line its own item shows.
EXTRA_BY_OWNER = {
    ("u8-g-availability-0", "This alternative describes a question, while the supplied line makes a positive statement."): [G49, G50],
    ("u8-i-availability-1", "This alternative describes a question, while the supplied line makes a positive statement."): [G49, G51],
}


def is_frozen(pack: dict, record: dict) -> bool:
    sources = {s["id"] for s in pack["sources"]}
    rights = {r["sourceId"] for r in pack["rights"] if r.get("permitsAppUse")}
    return (record["sourceId"] not in ("src-content-audit-research",) and record.get("licenseId") != "Original app text"
            and record.get("synthesis") is None and not record["sourceRecordId"].startswith("ca-coverage:")
            and bool(record.get("sourceRevision")) and record["sourceHash"] == sha(record["originalSourceText"])
            and record["sourceId"] in sources and record["sourceId"] in rights)


def part4(b: Builder, baseline: dict, items: dict, steps: dict, spans: dict) -> dict:
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
        if isinstance(ref, tuple):
            return b.lexical(objective, ref[1], ref[2], ref[3])
        if ref.startswith("span:"):
            return spans[ref[5:]]
        if ref not in R:
            raise SystemExit(f"Unknown record {ref}")
        return ref

    frozen8 = lambda: [r for r in R.values() if r["unitNumber"] == UNIT and is_frozen(pack, r)]  # noqa: E731

    def cite_forms(sentence: str, objective: str, participants: list[str]) -> list[str]:
        out = []
        pool = frozen8()
        for form in sorted(forms(sentence)):
            bare = form.replace(ACUTE, "")

            def ok(r):
                f = forms(r["originalSourceText"])
                return form in f if ACUTE in form else bare in {x.replace(ACUTE, "") for x in f}
            ranked = ([r for r in pool if r["itemId"] in participants and ok(r)] +
                      [r for r in pool if r["objectiveId"] == objective and r["shippedText"].lower() == form and ok(r)] +
                      [r for r in pool if r["objectiveId"] == objective and ok(r)] +
                      [r for r in pool if r["shippedText"].lower() == form and ok(r)] + [r for r in pool if ok(r)])
            if not ranked:
                raise SystemExit(f"No frozen Unit 8 record shows {form!r} ({sentence})")
            chosen = ranked[0]
            out.append(chosen["itemId"])
            m = re.match(r"^(adjectives|nouns)\.csv:([^#]+)#", chosen["sourceRecordId"])
            if m:
                gloss = next((r for r in pool if r["sourceRecordId"] == f"{m.group(1)}.csv:{m.group(2)}#translations_en"
                              and r["objectiveId"] == chosen["objectiveId"]), None)
                if gloss and re.search(rf"(?<![A-Za-z]){re.escape(gloss['shippedText'])}(?![A-Za-z])", sentence):
                    out.append(gloss["itemId"])
        return out

    report = {}
    missing = []

    def mark(host: dict, texts: list[str], objective: str, owner: str, participants: list[str]) -> None:
        facts, sources = [], {}
        for text in texts:
            for sentence in sentences(text):
                if not any(c.isalpha() for c in sentence) or sentence in NONFACTS:
                    continue
                refs = EXTRA_BY_OWNER.get((owner, sentence), EXTRA.get(sentence))
                if refs is None and not forms(sentence):
                    missing.append((owner, sentence))
                    continue
                ids = [resolve(ref, objective) for ref in refs or []] + cite_forms(sentence, objective, participants)
                ids = list(dict.fromkeys(ids))
                if not ids:
                    missing.append((owner, sentence))
                    continue
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
        mark(item, [item["feedback"]] + [r for r in item.get("distractorRationales", []) if r], item["objectiveId"], item["id"], participants)
    if missing:
        raise SystemExit("Sentences without a disposition: " + json.dumps(missing, ensure_ascii=False, indent=1))

    # --- Coverage of new English wording (never a marker source) -----------------------------------------------
    shipped8 = {r["shippedText"] for r in R.values() if r["unitNumber"] == UNIT}
    base_items = {i["id"]: i for i in baseline["items"]}
    base_steps = {s["id"]: s for u in baseline["units"] if u["number"] == UNIT for se in u["sessions"] for s in se["steps"]}
    counter = {}

    def cover(objective: str, owner: str, text: str) -> None:
        if not text or text in shipped8:
            return
        counter[owner] = counter.get(owner, 0) + 1
        b.coverage(objective, f"{owner}-{counter[owner]}", text)
        shipped8.add(text)
    for step in steps.values():
        if step["kind"] in ("TEACH", "REMEDIATE") and step["body"] != base_steps.get(step["id"], {}).get("body"):
            cover(owner_objective(step["id"]), step["id"], step["body"])
    for item in pack["items"]:
        if not item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            continue
        old = base_items.get(item["id"], {})
        a, oa = item.get("assessment") or {}, old.get("assessment") or {}
        texts = [item["feedback"]] + item.get("distractorRationales", []) + [a.get("whyAnswerDemonstratesObjective", "")] + a.get("distractorDifference", [])
        olds = set([old.get("feedback", "")] + old.get("distractorRationales", []) + [oa.get("whyAnswerDemonstratesObjective", "")] + oa.get("distractorDifference", []))
        for text in texts:
            if text and text not in olds:
                cover(item["objectiveId"], item["id"], text)
    # A marked sentence is covered by a record of its own text (V2Validator.markedSentenceSourceErrors).
    for owner, sources in report.items():
        for sentence in sources:
            objective = items[owner]["objectiveId"] if owner in items else owner_objective(owner)
            cover(objective, owner, sentence)
    pack["provenanceRecords"].extend(r for r in b.new if r not in pack["provenanceRecords"])
    return report


def apply(pack: dict) -> tuple[dict, dict]:
    b, baseline, items, steps, objectives, spans = part1(pack)
    part2(b, items, steps, objectives)
    steps = part3(b, items, steps, objectives)
    report = part4(b, baseline, items, steps, spans)
    return pack, report


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15c baseline; this transformer runs only once")
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
