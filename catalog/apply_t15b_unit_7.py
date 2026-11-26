#!/usr/bin/env python3
"""T15b (Claude Code, owner direction 11/16/2026): Unit 7 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer. Russian comes only from frozen files under catalog/sources/ already in the source inventory:
OpenRussian rows (units-6-10/openrussian/adjectives.csv, units-3-5/openrussian/nouns.csv and others.csv, commit 50e210c4),
the Wikibooks pages adjectives-4292379, adverbs-3197995, accusative-4330404 and lesson3-4460699, the English Wikipedia
Adverb page oldid 1374548415 and the Russian Wikipedia Наречие page oldid 152437423. No source is added.

- A2-24: every Unit 7 check moves to an end-of-unit check session after all teaching; cue-only items get a third form
  from the same OpenRussian row; position and label prompts are reworded to ask about meaning or form.
- Phrases: the nine Unit 7 phrases no frozen file attests are replaced by single sourced forms.
- A2-23: fifteen typed checks whose keys are OpenRussian cells already taught in Unit 7.
- Facts: every feedback, rationale and remediation sentence is marked with frozen-source citations or is a reviewed
  instruction; claims no frozen file states are reworded or removed.
- A2-26: governance wording, wiki markup and provenance narration leave Unit 7 learner text.
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
BASELINE_SHA256 = "ac724c72a7948b1ab7f1c74a1b577a8a71ca5b352260fd7da067321cbdb6de5c"
SRC = ROOT / "catalog/sources"
ADJ = SRC / "units-6-10/openrussian/adjectives.csv"
NOUNS = SRC / "units-3-5/openrussian/nouns.csv"
OTHERS = SRC / "units-3-5/openrussian/others.csv"
ADJ_PAGE = SRC / "units-6-10/wikibooks/adjectives-4292379.txt"
ADV_PAGE = SRC / "units-6-10/wikibooks/adverbs-3197995.txt"
ACC_PAGE = SRC / "units-6-10/wikibooks/accusative-4330404.txt"
LESSON3 = SRC / "units-16-20/wikibooks/lesson3-4460699.txt"
RUWIKI = SRC / "units-6-10/ruwiki/narechie-152437423.txt"
TASK = "T15b, Claude Code, 2026-11-16"
ACUTE = "́"
CYR = "Ѐ-ӿ"


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


# --------------------------------------------------------------------------------------------------------------
# Frozen records
# --------------------------------------------------------------------------------------------------------------
def csv_rows(path: Path) -> list[dict]:
    with path.open(encoding="utf-8") as handle:
        return list(csv.DictReader(handle, delimiter="\t", quoting=csv.QUOTE_NONE))


ROWS = {"adjectives.csv": csv_rows(ADJ), "nouns.csv": csv_rows(NOUNS), "others.csv": csv_rows(OTHERS)}
CSV_PATH = {"adjectives.csv": ADJ, "nouns.csv": NOUNS, "others.csv": OTHERS}


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
        self.unit = next(u for u in pack["units"] if u["number"] == 7)

    def add(self, record: dict) -> dict:
        if record["itemId"] in self.records:
            raise SystemExit(f"Duplicate record {record['itemId']}")
        self.records[record["itemId"]] = record
        self.new.append(record)
        return record

    def lexical(self, objective: str, file: str, bare: str, field: str) -> str:
        """An OpenRussian cell record for [objective]; reuses an existing Unit 7 record of the same cell."""
        source_record = f"{file}:{bare}#{field}"
        for record in self.records.values():
            if (record["unitNumber"] == 7 and record["objectiveId"] == objective and record["sourceRecordId"] == source_record
                    and record["sourceId"] == "src-openrussian-50e210c4" and record.get("synthesis") is None):
                return record["itemId"]
        template = self.records["prov-ex-or-u7-default-0"]
        original = cell(file, bare, field)
        shipped = stressed(original) if field != "translations_en" else original
        record = copy.deepcopy(template)
        record.update({"itemId": f"prov-t15b-u7-{objective[7:]}-{translit(bare)}-{field}".replace("_", "-"), "objectiveId": objective,
                       "sourceRecordId": source_record, "sourceUrl": template["sourceUrl"].replace("adjectives.csv", file),
                       "originalSourceText": original, "shippedText": shipped, "sourceHash": sha(original),
                       "contentHash": sha(shipped), "recordType": "lexical record"})
        return self.add(record)["itemId"]

    def span(self, key: str, objective: str, template_id: str, path: Path, needle: str, prefix: str,
             shipped: str | None = None, url: str | None = None, revision: str | None = None) -> str:
        """A record of one exact span of a frozen page, shipped as the span (or a displayed part of it)."""
        text = path.read_text(encoding="utf-8")
        if text.count(needle) != 1:
            raise SystemExit(f"Span {key} occurs {text.count(needle)} times in {path.name}")
        template = self.records[template_id]
        record = copy.deepcopy(template)
        record.update({"itemId": f"prov-t15b-u7-{key}", "objectiveId": objective, "sourceRecordId": prefix + needle[:80],
                       "originalSourceText": needle, "shippedText": shipped or needle, "sourceHash": sha(needle),
                       "contentHash": sha(shipped or needle), "recordType": "instruction"})
        if url:
            record["sourceUrl"] = url
        if revision:
            record["sourceRevision"] = revision
        return self.add(record)["itemId"]

    def coverage(self, objective: str, key: str, text: str, item_id: str | None = None) -> str:
        template = self.records["prov-t15an1-u6-possessive-our-your"]
        record = copy.deepcopy(template)
        note = (f"New English wording authored on 2026-11-16 ({TASK}); it did not exist before that date. It covers the"
                " wording only; the Russian facts it states are cited from frozen source records in the marker's"
                " russianFactSources. 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        record.update({"itemId": item_id or f"prov-ca-t15b-{key}", "unitNumber": 7, "objectiveId": objective,
                       "sourceRecordId": f"ca-coverage:{objective}:{key}", "shippedText": text,
                       "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      " this record's English text is new, authored 2026-11-16 for T15b."})
        if item_id and item_id in self.records:
            self.records[item_id].clear()
            self.records[item_id].update(record)
            return item_id
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


# Whole instructions that state no Russian fact (reviewed, as Unit 6's reviewedNonFacts).
NONFACTS = [
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    "Learn the forms of each adjective as you meet them.",
    "Keep the noun and the requested agreement in view.",
    "Use these nominative examples as models, then check the case the task names.",
    "Use the requested gender, number and case to decide the answer.",
    "Match the form to the noun, not to the dictionary form or a plural form.",
    "Keep the same noun when you change the case.",
    "Treat the fragment as a phrase on its own, not as a complete sentence.",
    "Read the cat and dog sentence separately from the fragment.",
    "The choices below ask which Russian expression names the kind of words adverbs are.",
    "The choices below ask where wording occurs in a Russian source line, using the dash and colon.",
    "This wording is written after the colon, so it is not immediately after the dash and before the colon.",
    "That wording task does not demonstrate applying invariance in a sentence.",
    "Use this explanation to understand the rule; do not infer a general adverb-formation rule from these choices.",
    "Check each lexical pair rather than applying an unrestricted generator.",
    "an unrestricted rule for every adjective goes beyond this check; it asks only about the pair shown.",
    "This question covers only the pairs taught in this unit; it makes no claim about other Russian words.",
    "the unit establishes a general conversion rule goes beyond this unit; use only the adjective and adverb pairs it teaches.",
]

BOILERPLATE = (" Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent"
               " check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.")
NBSP = " "

TEACH = {
    "u7-st-teach-endings-1": (
        "новый карандаш is masculine singular nominative; новая машина is feminine singular nominative. The adjective agrees "
        "with the noun in the phrase, so the car phrase uses the feminine form но́вая rather than masculine но́вый or plural "
        "но́вые. To make a feminine singular noun phrase accusative, change both words: the adjective но́вая becomes но́вую, "
        "and the noun кни́га becomes кни́гу. Both words change in these supplied case pairs."),
    "u7-st-teach-endings-2": (
        "The dictionary form of this adjective is masculine singular nominative но́вый. A phrase can need another gender, "
        "number or case even though the dictionary form is masculine singular nominative. In the neuter, the adjective "
        "но́вое becomes но́вого in the genitive, and the noun окно́ becomes окна́. Both adjective and noun change case while "
        "remaining neuter singular."),
    "u7-st-teach-endings-3": (
        "новое платье is neuter singular nominative. Compare the adjective with the masculine pencil and feminine car "
        "examples above: the noun controls the agreement required in that phrase. синий стол uses the masculine nominative "
        "form of a different adjective meaning dark blue. Learn the forms of each adjective as you meet them."),
    "u7-st-teach-endings-4": (
        "синяя книга and синее письмо show feminine and neuter singular nominative agreement for the adjective meaning dark "
        "blue. Keep the noun and the requested agreement in view. Use these nominative examples as models, then check the "
        "case the task names."),
    "u7-st-teach-endings-5": (
        "синие книги has plural nominative agreement. For the adjective meaning new, но́вый is masculine singular nominative "
        "and но́вые is plural nominative. For the plural nominative of the same adjective as но́вый, choose но́вые rather than "
        "feminine singular но́вая. The comparison changes number; it does not change the adjective to a different vocabulary item."),
    "u7-st-teach-endings-6": (
        "но́вая and но́вое are feminine and neuter singular nominative forms of the same adjective. The corresponding "
        "dictionary form is masculine singular nominative но́вый. When the task shows но́вая and asks for that dictionary "
        "form, choose но́вый rather than neuter но́вое. Use the requested gender, number and case to decide the answer."),
    "u7-st-teach-endings-7": (
        "но́вые is plural nominative of this adjective. Compare all four nominative forms: но́вый, но́вая, но́вое and но́вые. "
        "A feminine singular phrase such as новая машина needs но́вая, not the masculine dictionary form or the plural form."),
    "u7-st-teach-spelling-3": (
        "хоро́шая is feminine singular nominative; хоро́шее is neuter singular nominative. To make a feminine singular noun "
        "phrase accusative, change both words: the adjective хоро́шая becomes хоро́шую, and the noun кни́га becomes кни́гу. "
        "Keep the same noun when you change the case. хоро́шего is masculine singular genitive; хоро́шие is plural nominative."),
    "u7-st-teach-demonstrative-3": "э́тот — this. э́то — this. э́ти — this. ",
    "u7-st-teach-objectadj-3": (
        "Моя русская кошка любит мою новую собаку — My Russian cat likes my new dog. The separate fragment этих женщин means "
        "these women. Its plural forms can be accusative or genitive; the source illustrates an animate accusative object. "
        "Treat the fragment as a phrase on its own, not as a complete sentence. Read the cat and dog sentence separately "
        "from the fragment."),
    "u7-st-teach-adverb-invariance-english": (
        "Adverbs do not decline or conjugate: they do not change for case, gender or number agreement. This grammatical "
        "invariance does not mean that comparative forms cannot exist. The choices below ask which Russian expression names "
        "the kind of words adverbs are. That wording task does not demonstrate applying invariance in a sentence. Use this "
        "explanation to understand the rule; do not infer a general adverb-formation rule from these choices."),
}
MANNER_BODY = ("бы́стрый → бы́стро is a documented adjective-to-manner-adverb pair: the adjective describes a noun, while the "
               "adverb describes how an action is performed. Check each lexical pair rather than applying an unrestricted generator.")

TITLES = {"obj-u7-default": "The dictionary form of an adjective",
          "obj-u7-endings": "Adjective endings that agree with the noun",
          "obj-u7-question": "Forms of the question adjective",
          "obj-u7-demonstrative": "Forms of the demonstrative",
          "obj-u7-place": "Adjectives in the prepositional case",
          "obj-u7-objectadj": "Adjectives in the accusative case",
          "obj-u7-adverb": "Adverb meanings",
          "obj-u7-adverb-invariance": "Adverbs do not change form",
          "obj-u7-adverb-pairs": "Adverbs formed from adjectives"}

# Cue-only checks: a third option from the same OpenRussian row (prep report section 1).
THIRD = {"u7-i-default-1": ("новый", "decl_f_nom"), "u7-i-default-2": ("хороший", "decl_n_nom"),
         "u7-i-default-3": ("синий", "decl_n_nom"), "u7-i-default-4": ("какой", "decl_n_nom"),
         "u7-i-spelling-1": ("хороший", "decl_f_nom"), "u7-i-question-1": ("какой", "decl_f_nom"),
         "u7-i-demonstrative-1": ("этот", "decl_pl_nom"), "u7-i-demonstrative-2": ("этот", "decl_f_nom"),
         "u7-i-demonstrative-3": ("этот", "decl_pl_nom"), "u7-i-place-2": ("хороший", "decl_f_nom"),
         "u7-i-place-3": ("хороший", "decl_f_nom")}
FIELD_NAME = {"decl_m_nom": "masculine singular nominative", "decl_f_nom": "feminine singular nominative",
              "decl_n_nom": "neuter singular nominative", "decl_pl_nom": "plural nominative",
              "decl_f_acc": "feminine singular accusative", "decl_m_gen": "masculine singular genitive",
              "decl_n_gen": "neuter singular genitive", "decl_m_prep": "masculine singular prepositional"}

# Fifteen typed checks (prep report section 2): (id, objective, adjective row, cue field, key field, prompt noun).
TYPED = [
    ("u7-i-type-novaya", "obj-u7-default", "новый", "decl_m_nom", "decl_f_nom", "adjective"),
    ("u7-i-type-novoe", "obj-u7-endings", "новый", "decl_m_nom", "decl_n_nom", "adjective"),
    ("u7-i-type-novye", "obj-u7-endings", "новый", "decl_f_nom", "decl_pl_nom", "adjective"),
    ("u7-i-type-novogo", "obj-u7-objectadj", "новый", "decl_m_nom", "decl_m_gen", "adjective"),
    ("u7-i-type-novuyu", "obj-u7-objectadj", "новый", "decl_m_nom", "decl_f_acc", "adjective"),
    ("u7-i-type-novom", "obj-u7-place", "новый", "decl_m_nom", "decl_m_prep", "adjective"),
    ("u7-i-type-khoroshaya", "obj-u7-spelling", "хороший", "decl_m_nom", "decl_f_nom", "adjective"),
    ("u7-i-type-khoroshee", "obj-u7-spelling", "хороший", "decl_m_nom", "decl_n_nom", "adjective"),
    ("u7-i-type-khoroshie", "obj-u7-spelling", "хороший", "decl_m_nom", "decl_pl_nom", "adjective"),
    ("u7-i-type-khoroshem", "obj-u7-place", "хороший", "decl_f_nom", "decl_m_prep", "adjective"),
    ("u7-i-type-kakaya", "obj-u7-question", "какой", "decl_n_nom", "decl_f_nom", "adjective"),
    ("u7-i-type-kakoe", "obj-u7-question", "какой", "decl_f_nom", "decl_n_nom", "adjective"),
    ("u7-i-type-eta", "obj-u7-demonstrative", "этот", "decl_m_nom", "decl_f_nom", "demonstrative"),
    ("u7-i-type-etu", "obj-u7-demonstrative", "этот", "decl_m_nom", "decl_f_acc", "demonstrative"),
    ("u7-i-type-eti", "obj-u7-demonstrative", "этот", "decl_m_nom", "decl_pl_nom", "demonstrative"),
]
# T15b owner decision (11/16/2026): V2Validator.invarianceRelationshipErrors now governs a meaning prompt.
INVARIANCE_MEANING = True
INVARIANCE_TASK_MEANING = "The choices below ask which Russian expression names the kind of words adverbs are."
INVARIANCE_TASK_POSITION = "The choices below ask where wording occurs in a Russian source line, using the dash and colon."
INVARIANCE_FEEDBACK = "Наречия\u00a0— неизменяемые слова: adverbs are words that do not change form."
TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."


def part1(pack: dict):
    b = Builder(pack)
    unit = b.unit
    baseline = copy.deepcopy(pack)
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    examples = {e["id"]: e for e in pack["examples"]}
    R = b.records
    adj_url = R["prov-u7-default-rem"]["sourceUrl"]

    # --- Frozen spans cited by markers (exact text of the named frozen line) -------------------------------------
    spans = {}
    def page_span(key, objective, path, needle, title, revision, template="prov-u7-default-rem", prefix=None):
        url = adj_url.replace("Russian/Grammar/Adjectives", title).replace("4292379", revision)
        spans[key] = b.span(key, objective, template, path, needle, prefix or (title.split("/")[-1].lower().split("_")[0] + ":"),
                            url=url, revision=revision)
    A = "Russian/Grammar/Adjectives"
    page_span("a1", "obj-u7-endings", ADJ_PAGE, "The endings of adjectives change according to the gender, number, and case of the nouns they modify.", A, "4292379", prefix="adjectives:")
    page_span("a11", "obj-u7-endings", ADJ_PAGE, "When adjectives modify a masculine noun, they use their default ending.", A, "4292379", prefix="adjectives:")
    page_span("a12", "obj-u7-endings", ADJ_PAGE, "With feminine nouns, the adjective ends in '''-ая'''.", A, "4292379", prefix="adjectives:")
    page_span("a14", "obj-u7-demonstrative", ADJ_PAGE, "With plural nouns, regardless of gender, the adjective replaces '''-ый''' or '''-ой''' with '''-ые'''", A, "4292379", prefix="adjectives:")
    page_span("a24", "obj-u7-spelling", ADJ_PAGE, "if the stem ends in '''г, ж, к, х, ч, ш''', or '''щ''', you add '''и''', not '''ы'''. This 7-letter spelling rule also applies to adjectives.", A, "4292379", prefix="adjectives:")
    page_span("a26", "obj-u7-spelling", ADJ_PAGE, "if the stem ends in '''ж, ц, ч, ш''', or '''щ''', any unstressed '''o''' in an ending is instead written as '''e'''.", A, "4292379", prefix="adjectives:")
    page_span("a29", "obj-u7-objectadj", ADJ_PAGE, "Adjectival endings in the accusative case are similar to noun endings, in that they all use endings from other cases, with the exception of feminine nouns.", A, "4292379", prefix="adjectives:")
    page_span("a31", "obj-u7-objectadj", ADJ_PAGE, "However, if the modified masculine noun is a person or animal, then both it and its adjectives take the genitive form.", A, "4292379", prefix="adjectives:")
    page_span("a33", "obj-u7-objectadj", ADJ_PAGE, "Feminine adjectives simply have '''-ую''' in the accusative.", A, "4292379", prefix="adjectives:")
    gen = SRC / "units-6-10/wikibooks/genitive-4567338.txt"
    page_span("g88", "obj-u7-spelling", gen, "* if it ends in о, replace with а", "Russian/Grammar/Genitive_case", "4567338", prefix="genitive:")
    page_span("g89", "obj-u7-spelling", gen, "* if it ends in е, replace with я", "Russian/Grammar/Genitive_case", "4567338", prefix="genitive:")
    page_span("b1", "obj-u7-adverb-paradigm", ADV_PAGE, "Adverbs describe a verb much like adjectives describe nouns.", "Russian/Grammar/Adverbs", "3197995", prefix="adverbs:")
    spans["r8b"] = b.span("r8b", "obj-u7-adverb-invariance", "prov-u7-adverb-invariance-rem", RUWIKI,
                          "И только качественные наречия, образованные от прилагательных, имеют степени сравнения и формы субъективной оценки",
                          "ruwiki-narechie:")

    # --- A2-26: markup leaves the shipped wiki spans (the frozen originals keep it) -----------------------------
    def unmarked(record_id: str, shipped: str) -> str:
        record = R[record_id]
        old = record["shippedText"]
        record["shippedText"] = shipped
        record["contentHash"] = sha(shipped)
        return old
    old_dem = unmarked("prov-u7-demonstrative-rem", "these good new students\" is эти хорошие новые студенты")
    old_place = unmarked("prov-u7-place-rem", "They were talking about a good new cinema' - Они говорили о хорошем новом кино")
    old_obj = unmarked("prov-u7-objectadj-rem", "I want a new rabbit - Я хочу нового кролика.")
    old_pairs = unmarked("prov-u7-adverb-pairs-rem", f"суффиксальный: быстрый{NBSP}— быстро, творческий{NBSP}— творчески;")
    for step_id, old, record_id in (("u7-st-teach-demonstrative-2", old_dem, "prov-u7-demonstrative-rem"),
                                    ("u7-st-rem-demonstrative", old_dem, "prov-u7-demonstrative-rem"),
                                    ("u7-st-teach-place-2", old_place, "prov-u7-place-rem"),
                                    ("u7-st-rem-place", old_place, "prov-u7-place-rem"),
                                    ("u7-st-teach-objectadj-2", old_obj, "prov-u7-objectadj-rem"),
                                    ("u7-st-teach-adverb-pairs-2", old_pairs, "prov-u7-adverb-pairs-rem"),
                                    ("u7-st-rem-adverb-pairs", old_pairs, "prov-u7-adverb-pairs-rem")):
        if steps[step_id]["body"] != old:
            raise SystemExit(f"{step_id} baseline changed")
        steps[step_id]["body"] = R[record_id]["shippedText"]

    # --- Teaching bodies ------------------------------------------------------------------------------------------
    for step in steps.values():
        if BOILERPLATE in step["body"]:
            step["body"] = step["body"].replace(BOILERPLATE, "")
    inv = steps["u7-st-teach-adverb-invariance-1"]
    if inv["body"] != "Наречия — неизменяемые слова. Наречия — неизменяемые слова.":
        raise SystemExit("invariance teaching baseline changed: " + repr(inv["body"]))
    inv["body"] = "Наречия — неизменяемые слова."
    for step_id, body in TEACH.items():
        steps[step_id]["body"] = body
    if not INVARIANCE_MEANING:
        english = steps["u7-st-teach-adverb-invariance-english"]
        english["body"] = english["body"].replace(INVARIANCE_TASK_MEANING, INVARIANCE_TASK_POSITION)
    steps["u7-st-teach-manner-3"]["body"] = MANNER_BODY
    steps["u7-st-rem-manner"]["body"] = MANNER_BODY
    # Remediation repeats the whole teaching of its session.
    for objective_id, rem_id in (("obj-u7-endings", "u7-st-rem-endings"), ("obj-u7-spelling", "u7-st-rem-spelling"),
                                 ("obj-u7-objectadj", "u7-st-rem-objectadj"), ("obj-u7-adverb-invariance", "u7-st-rem-adverb-invariance")):
        session = next(se for se in unit["sessions"] if objective_id in se.get("objectiveIds", []) and len(se["objectiveIds"]) == 1)
        steps[rem_id]["body"] = "\n\n".join(s["body"] for s in session["steps"] if s["kind"] == "TEACH")
    return b, baseline, items, steps, objectives, examples, spans


def set_choice(b: Builder, item: dict, prompt: str, cue_id: str, answer_id: str, distractor_ids: list[str],
               feedback: str, rationales: list[str], relation: str, feature: str | None = None) -> None:
    """Rebuild a closed item from records: answer first, then the distractors in order."""
    R = b.records
    options = [R[answer_id]["shippedText"]] + [R[d]["shippedText"] for d in distractor_ids]
    if R[cue_id]["shippedText"] != item["promptLetter"] and item.get("_newcue") is None:
        pass
    item.update({"prompt": prompt, "promptLetter": R[cue_id]["shippedText"], "options": options, "answer": options[0],
                 "feedback": feedback, "distractorRationales": [""] + rationales})
    a = item["assessment"]
    a.update({"evidenceMode": relation, "relationKind": relation, "answerSourceIds": [answer_id],
              "distractorSourceIds": [""] + distractor_ids, "distractorDifference": [""] + rationales,
              "whyAnswerDemonstratesObjective": feedback, "contrastMembers": options,
              "relationParticipantIds": [cue_id, answer_id] + distractor_ids})
    if feature:
        a["testedFeature"] = feature


def field_of(record: dict) -> str:
    return record["sourceRecordId"].rsplit("#", 1)[-1]


def lex_item(b: Builder, item_id: str, prompt: str, row: str, cue_field: str, answer_field: str, distractor_fields: list[str],
             feature: str | None = None) -> None:
    items = {i["id"]: i for i in b.pack["items"]}
    item = items[item_id]
    objective = item["objectiveId"]
    cue = b.lexical(objective, "adjectives.csv", row, cue_field)
    answer = b.lexical(objective, "adjectives.csv", row, answer_field)
    distractors = [b.lexical(objective, "adjectives.csv", row, f) for f in distractor_fields]
    R = b.records
    want = FIELD_NAME[answer_field]
    feedback = (f"{R[cue]['shippedText']} is the {FIELD_NAME[cue_field]} form; {R[answer]['shippedText']} is the {want} form"
                " of the same adjective.")
    rationales = [f"{R[d]['shippedText']} is the {FIELD_NAME[f]} form, so it does not supply the requested {want} form."
                  for d, f in zip(distractors, distractor_fields)]
    set_choice(b, item, prompt, cue, answer, distractors, feedback, rationales, "SAME_LEMMA_DIFFERENT_FIELD", feature)


def part2(b: Builder, items: dict, steps: dict, objectives: dict, examples: dict) -> list[dict]:
    R = b.records
    pack = b.pack

    # --- A2-24 cue-only checks: a third form from the same OpenRussian row ----------------------------------------
    for item_id, (row, field) in THIRD.items():
        item = items[item_id]
        a = item["assessment"]
        record_id = b.lexical(item["objectiveId"], "adjectives.csv", row, field)
        answer_field = field_of(R[a["answerSourceIds"][0]])
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

    # --- Unattested phrases become single sourced forms ---------------------------------------------------------
    lex_item(b, "u7-i-endings-1", "Which neuter singular genitive form of the same adjective corresponds to the shown form?",
             "новый", "decl_n_nom", "decl_n_gen", ["decl_f_acc", "decl_m_nom"], "NEUTER_ADJECTIVE_GENITIVE")
    lex_item(b, "u7-i-spelling-3", "Which feminine singular accusative form of the same adjective corresponds to the shown form?",
             "хороший", "decl_f_nom", "decl_f_acc", ["decl_m_gen", "decl_pl_nom"], "FEMININE_ADJECTIVE_ACCUSATIVE")
    spelling3 = items["u7-i-spelling-3"]
    kniga, knigu = (b.lexical("obj-u7-spelling", "nouns.csv", "книга", f) for f in ("sg_nom", "sg_acc"))
    spelling3["feedback"] += (f" In a feminine singular noun phrase, the noun changes too: {R[kniga]['shippedText']} becomes"
                              f" {R[knigu]['shippedText']}. Both words change while the same noun and feminine singular"
                              " agreement are kept.")
    spelling3["assessment"]["whyAnswerDemonstratesObjective"] = spelling3["feedback"]
    lex_item(b, "u7-g-endings-0", "Which feminine singular accusative form of the same adjective corresponds to the shown form?",
             "синий", "decl_f_nom", "decl_f_acc", ["decl_n_nom", "decl_pl_nom"], "FEMININE_ADJECTIVE_OBJECT")
    for row, fields in (("новый", ("decl_f_acc", "decl_n_gen")),):
        for f in fields:
            b.lexical("obj-u7-endings", "adjectives.csv", row, f)
    for bare, f in (("книга", "sg_nom"), ("книга", "sg_acc"), ("окно", "sg_nom"), ("окно", "sg_gen"), ("окно", "gender")):
        b.lexical("obj-u7-endings", "nouns.csv", bare, f)

    # endings-4: the prompt names the adjective, not the app's record set.
    e4 = items["u7-i-endings-4"]
    e4["prompt"] = "Which nominative form of но́вый agrees with the shown noun?"
    e4["feedback"] = ("но́вая agrees with the shown feminine singular noun in the nominative, as in новая машина. но́вый is"
                      " masculine singular and но́вые is plural. Match the form to the noun, not to the dictionary form or a plural form.")
    e4["assessment"]["whyAnswerDemonstratesObjective"] = e4["feedback"]

    # --- Adverb meaning, invariance and pair checks ask about meaning, not position -------------------------------
    glosses = {"быстро": "quickly", "хорошо": "well", "прекрасно": "wonderfully"}
    by_gloss = {v: k for k, v in glosses.items()}
    adjective = {"быстро": "быстрый", "хорошо": "хороший", "прекрасно": "прекрасный"}
    for key, form in (("prekrasnyy", "прекрасный"), ("khoroshiy", "хороший")):
        record = copy.deepcopy(R["prov-ex-u7-adverb-2-ru"])
        record.update({"itemId": f"prov-t15b-u7-adverb-{key}", "shippedText": form, "contentHash": sha(form)})
        b.add(record)
    for item_id in ("u7-g-adverb-0", "u7-i-adverb-1", "u7-i-adverb-2"):
        item = items[item_id]
        cue = item["promptLetter"]
        item["prompt"] = "Which English meaning matches the shown Russian adverb?"
        item["feedback"] = f"{cue} means {glosses[cue]}; it is formed from {adjective[cue]} with the adverbial ending."
        rationales = [""] + [f"{o} is the meaning of {by_gloss[o]}, not of {cue}." for o in item["options"][1:]]
        item["distractorRationales"] = rationales
        item["assessment"].update({"whyAnswerDemonstratesObjective": item["feedback"], "distractorDifference": rationales})
    inv_reason = {"не склоняются": "says that adverbs do not decline", "не спрягаются": "says that adverbs do not conjugate",
                  "не имеют окончаний": "says that adverbs have no endings"}
    for item_id in ("u7-g-adverb-invariance-0", "u7-i-adverb-invariance-1", "u7-i-adverb-invariance-2"):
        item = items[item_id]
        if INVARIANCE_MEANING:
            item["prompt"] = "Which expression names the kind of words that adverbs are?"
            item["feedback"] = INVARIANCE_FEEDBACK
            rationales = [""] + [f"{o} {inv_reason[o]}; it does not name the kind of words they are." for o in item["options"][1:]]
            item["distractorRationales"] = rationales
        else:
            item["prompt"] = "Which wording comes immediately after the dash and before the colon?"
            item["feedback"] = INVARIANCE_FEEDBACK
        item["assessment"].update({"whyAnswerDemonstratesObjective": item["feedback"],
                                   "distractorDifference": list(item["distractorRationales"])})
    pair_feedback = {"быстрый": f"быстрый{NBSP}— быстро: the adverb быстро is formed from the adjective быстрый with a suffix.",
                     "творческий": f"творческий{NBSP}— творчески: the adverb творчески is formed from the adjective творческий with a suffix."}
    pair_reason = {("быстрый", "творчески"): "творчески is formed from творческий, not from быстрый.",
                   ("творческий", "быстро"): "быстро is formed from быстрый, so it is not the adverb asked for here.",
                   ("быстрый", "творческий"): "творческий is an adjective, not the adverb formed from быстрый.",
                   ("творческий", "быстрый"): "быстрый is an adjective, not an adverb formed from творческий."}
    for item_id in ("u7-g-adverb-pairs-0", "u7-i-adverb-pairs-1", "u7-i-adverb-pairs-2"):
        item = items[item_id]
        cue = item["promptLetter"]
        item["prompt"] = "Which adverb is formed from the shown adjective?"
        item["feedback"] = pair_feedback[cue]
        rationales = [""] + [pair_reason[(cue, o)] for o in item["options"][1:]]
        item["distractorRationales"] = rationales
        item["assessment"].update({"whyAnswerDemonstratesObjective": item["feedback"], "distractorDifference": rationales})
    b.lexical("obj-u7-adverb-pairs", "adjectives.csv", "творческий", "accented")
    b.lexical("obj-u7-adverb-pairs", "adjectives.csv", "быстрый", "accented")

    # --- Demonstrative guided prompt ---------------------------------------------------------------------------
    items["u7-g-demonstrative-0"]["prompt"] = "Which form of the demonstrative does the shown phrase use?"

    # --- Manner: guided practice on the frozen suffixal pairs; the sentence cue becomes the adverb alone -------------
    paradigm = "obj-u7-adverb-paradigm"
    def ruwiki(source_id: str, key: str) -> str:
        record = copy.deepcopy(R[source_id])
        record.update({"itemId": f"prov-t15b-u7-manner-{key}", "objectiveId": paradigm})
        return b.add(record)["itemId"]
    tv, tvi, by, byi = (ruwiki("prov-ex-u7-adverb-pairs-1-ru", "tvorcheskiy"), ruwiki("prov-ex-u7-adverb-pairs-1-en", "tvorcheski"),
                        ruwiki("prov-ex-u7-adverb-pairs-0-en", "bystro"), ruwiki("prov-ex-u7-adverb-pairs-0-ru", "bystryy"))
    g = items["u7-g-manner-1"]
    set_choice(b, g, "Which adverb is formed from the shown adjective?", tv, tvi, [by, byi], pair_feedback["творческий"],
               [pair_reason[("творческий", "быстро")], pair_reason[("творческий", "быстрый")]], "EXPLICIT_SOURCE_PAIR",
               "DOCUMENTED_MANNER_PAIR")
    steps["u7-st-guided-manner-1"]["body"] = "Choose an answer, then read the feedback."
    m1 = items["u7-i-manner-1"]
    m1["distractorRationales"][2] = "an unrestricted rule for every adjective goes beyond this check; it asks only about the pair shown."
    m1["assessment"]["distractorDifference"][2] = m1["distractorRationales"][2]
    m4 = items["u7-i-manner-4"]
    m4["feedback"] = ("For си́ний, this unit supplies no documented manner-adverb counterpart. This question covers only the pairs"
                      " taught in this unit; it makes no claim about other Russian words.")
    m4["distractorRationales"][1] = ("the unit establishes a general conversion rule goes beyond this unit; use only the"
                                     " adjective and adverb pairs it teaches.")
    m4["assessment"].update({"whyAnswerDemonstratesObjective": m4["feedback"], "distractorDifference": list(m4["distractorRationales"])})
    # The content-audit capability's applications: the bare adverb replaces the unsourced sentence cue.
    host = copy.deepcopy(R["prov-ca-repair-7-11069"])
    host.update({"itemId": "prov-ca-repair-7-t15b-bystro", "shippedText": "быстро", "contentHash": sha("быстро")})
    b.add(host)
    old_app = {"cue": "Она говорит быстро.", "answer": "manner adverb modifying the action", "distractors": ["an adjective describing the speaker"]}
    new_app = {"cue": "быстро", "answer": "a manner adverb modifying an action", "distractors": ["an adjective describing a noun", "a verb naming the action"]}
    changed = 0
    for record in R.values():
        apps = (record.get("synthesis") or {}).get("applications")
        if record["sourceRecordId"] == host["sourceRecordId"] and apps and old_app in apps:
            apps[apps.index(old_app)] = copy.deepcopy(new_app)
            changed += 1
    if changed != 38:  # 37 capability hosts and the new bare-adverb host
        raise SystemExit(f"Manner applications changed in {changed} records, expected 38")
    m2 = items["u7-i-manner-2"]
    set_choice(b, m2, "Which English description identifies the function of быстро?", "prov-ca-repair-7-t15b-bystro",
               "prov-ca-repair-7-11069", ["prov-ca-repair-7-11065", "prov-ca-repair-7-11070"],
               "быстро is a manner adverb modifying an action: adverbs describe a verb much like adjectives describe nouns.",
               ["an adjective describing a noun would be бы́стрый, not the adverb быстро.",
                "a verb naming the action names what happens; быстро describes how it happens."], "STATED_APPLICATION")
    examples["ex-u7-manner-0"]["note"] = "an adjective describing a noun"
    examples["ex-u7-manner-1"].update({"display": "быстро", "note": "a manner adverb modifying an action"})
    for bare, file in (("быстрый", "adjectives.csv"), ("быстро", "others.csv")):
        b.lexical(paradigm, file, bare, "accented")

    # --- A2-23: typed checks ------------------------------------------------------------------------------------
    typed = []
    for item_id, objective, row, cue_field, key_field, noun in TYPED:
        cue = b.lexical(objective, "adjectives.csv", row, cue_field)
        key = b.lexical(objective, "adjectives.csv", row, key_field)
        typed.append(typed_item(item_id, objective, f"Type the {FIELD_NAME[key_field]} form of the shown {noun}.",
                                R[cue]["shippedText"], R[key]["shippedText"], TYPED_FEEDBACK, "src-openrussian-50e210c4"))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    return typed


def part3(b: Builder, items: dict, steps: dict, objectives: dict, typed: list[dict]) -> None:
    unit = b.unit
    pack = b.pack
    # --- A2-26 titles and instruction text ------------------------------------------------------------------------
    for objective_id, title in TITLES.items():
        objectives[objective_id]["title"] = title
    unit["topicSummary"] = ("Unit 7 covers adjective agreement, adjective spelling after ш, question and demonstrative forms,"
                            " adjective case forms, and adverbs, including a bounded manner-adverb lesson, alongside Lesson 7 in your book.")
    orient = steps["u7-st-orient"]
    orient["title"] = "Adjectives, demonstratives and adverbs"
    orient["body"] = ("This unit covers adjective agreement, adjective spelling after ш, question and demonstrative forms,"
                      " adjective case forms, and adverbs.")
    for session in unit["sessions"]:
        if session["id"] == "u7-s-orient":
            session["title"] = "Adjectives, demonstratives and adverbs"
        elif len(session.get("objectiveIds", [])) == 1 and session["objectiveIds"][0] in TITLES:
            session["title"] = TITLES[session["objectiveIds"][0]]
    for step in steps.values():
        if step["title"] == "Admitted lines":
            step["title"] = "Examples"
        elif step["title"] == "Admitted instruction":
            step["title"] = "The rule"
        if step["kind"] == "GUIDED" and step["body"] == "Choose the admitted record. The feedback states the frozen field.":
            step["body"] = "Choose an answer, then read the feedback."
    steps["u7-st-teach-adverb-invariance-english"]["title"] = "What the rule means"
    steps["u7-st-rem-adverb-invariance"]["title"] = "Review the rule"

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
    unit["sessions"].append({"id": "u7-s-checks", "title": "Unit 7 checks", "estimatedMinutes": 20,
                             "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                             "steps": check_steps})

    # --- Reference notice -------------------------------------------------------------------------------------
    for reference in pack["references"]:
        if reference["id"] == "ref-u7-ruwiki-attribution":
            reference["summary"] = "The adverb lines in this unit adapt one Russian Wikipedia revision."
            reference["detail"] = ("The invariance sentence and the suffixal pairs come from Russian Wikipedia oldid 152437423 under"
                                   " CC BY-SA 4.0, via the Russian Wikipedia copyrights page oldid 154562152. That license route"
                                   " is separate from the English Wikipedia, Wiktionary, Wikibooks, Pressbooks and OpenRussian"
                                   " routes. The two pairs are быстрый — быстро and творческий — творчески. They do not state a"
                                   " universal adverb paradigm. Review is pending.")


LEX = lambda file, bare, field: ("lex", file, bare, field)  # noqa: E731
EXTRA: dict[str, list] = {
    # endings teaching (repeated in remediation)
    "новый карандаш is masculine singular nominative; новая машина is feminine singular nominative.":
        ["prov-ex-u7-endings-0-ru", "prov-ex-u7-endings-1-ru", "span:a11", "span:a12"],
    "The adjective agrees with the noun in the phrase, so the car phrase uses the feminine form но́вая rather than masculine но́вый or plural но́вые.":
        ["span:a1", "span:a12"],
    "To make a feminine singular noun phrase accusative, change both words: the adjective но́вая becomes но́вую, and the noun кни́га becomes кни́гу.":
        ["span:a33"],
    "Both words change in these supplied case pairs.":
        ["span:a1", LEX("adjectives.csv", "новый", "decl_f_nom"), LEX("adjectives.csv", "новый", "decl_f_acc"),
         LEX("nouns.csv", "книга", "sg_nom"), LEX("nouns.csv", "книга", "sg_acc")],
    "The dictionary form of this adjective is masculine singular nominative но́вый.": ["prov-u7-default-rem"],
    "A phrase can need another gender, number or case even though the dictionary form is masculine singular nominative.":
        ["span:a1", "prov-u7-default-rem"],
    "In the neuter, the adjective но́вое becomes но́вого in the genitive, and the noun окно́ becomes окна́.": ["span:g88"],
    "Both adjective and noun change case while remaining neuter singular.":
        ["span:a1", LEX("adjectives.csv", "новый", "decl_n_nom"), LEX("adjectives.csv", "новый", "decl_n_gen"),
         LEX("nouns.csv", "окно", "sg_nom"), LEX("nouns.csv", "окно", "sg_gen"), LEX("nouns.csv", "окно", "gender")],
    "новое платье is neuter singular nominative.": ["prov-ex-u7-endings-2-ru", LEX("nouns.csv", "платье", "gender")],
    "Compare the adjective with the masculine pencil and feminine car examples above: the noun controls the agreement required in that phrase.":
        ["span:a1", "span:a11", "span:a12", "prov-ex-u7-endings-0-en", "prov-ex-u7-endings-1-en"],
    "синий стол uses the masculine nominative form of a different adjective meaning dark blue.":
        ["prov-ex-u7-endings-3-ru", "prov-ex-or-u7-default-6-gloss", "span:a11"],
    "синяя книга and синее письмо show feminine and neuter singular nominative agreement for the adjective meaning dark blue.":
        ["prov-ex-u7-endings-4-ru", "prov-ex-u7-endings-5-ru", "prov-ex-or-u7-default-6-gloss"],
    "синие книги has plural nominative agreement.": ["prov-ex-u7-endings-6-ru", "span:a14"],
    "For the adjective meaning new, но́вый is masculine singular nominative and но́вые is plural nominative.": ["prov-ex-or-u7-endings-7-gloss"],
    "The comparison changes number; it does not change the adjective to a different vocabulary item.":
        ["prov-ex-or-u7-endings-7", "prov-ex-or-u7-endings-10"],
    "The corresponding dictionary form is masculine singular nominative но́вый.": ["prov-u7-default-rem"],
    "A feminine singular phrase such as новая машина needs но́вая, not the masculine dictionary form or the plural form.":
        ["prov-ex-u7-endings-1-ru", "span:a12", "prov-u7-default-rem"],
    # spelling teaching
    "An adjective agrees with its noun in gender, number and case.": ["span:a1"],
    "In хорошее письмо, the adjective describes a neuter singular noun.": ["prov-ex-u7-spelling-0-ru", LEX("nouns.csv", "письмо", "gender")],
    "хоро́ший is the dictionary form: masculine singular nominative.": ["prov-u7-default-rem"],
    "The following forms belong to the same adjective meaning good.":
        ["prov-ex-or-u7-spelling-1", "prov-ex-or-u7-spelling-2", "prov-ex-or-u7-spelling-3", "prov-ex-or-u7-spelling-4",
         "prov-ex-or-u7-spelling-1-gloss"],
    "After ж, ц, ч, ш, or щ, an unstressed о in an ending is written е.": ["span:a26", "span:g88", "span:g89"],
    "That replacement is not a rule for every unstressed о.": ["span:a26", "span:g88"],
    "In хоро́шее, the unstressed ending after ш uses е.": ["span:a26", "span:g89"],
    "The и in хоро́шие follows a different spelling condition: write и rather than ы after ш.": ["span:a24", "span:a26"],
    "Spelling limits the letters in an ending; gender, number and case determine which form is needed.": ["span:a1", "span:a24", "span:a26"],
    "To make a feminine singular noun phrase accusative, change both words: the adjective хоро́шая becomes хоро́шую, and the noun кни́га becomes кни́гу.":
        ["span:a33"],
    "To supply the dictionary form, choose хоро́ший.": ["prov-u7-default-rem"],
    "These choices test the requested form of the adjective; all the options are already correctly spelled.":
        ["prov-ex-or-u7-spelling-1", "prov-ex-or-u7-spelling-2", "prov-ex-or-u7-spelling-3", "prov-ex-or-u7-spelling-4", "span:a24", "span:a26"],
    # single-span remediation
    "The default form of an adjective is its nominative, masculine, singular form, and this is the form given in dictionaries.":
        ["prov-u7-default-rem"],
    "\"these good new students\" is эти хорошие новые студенты": ["prov-u7-demonstrative-rem"],
    "\"быстро\", \"хорошо\", \"прекрасно\" (quickly, well, wonderfully)": ["prov-u7-adverb-rem"],
    f"суффиксальный: быстрый{NBSP}— быстро, творческий{NBSP}— творчески;": ["prov-u7-adverb-pairs-rem"],
    # object agreement teaching
    "Я хочу новый стул и новое письмо — I want a new chair and a new letter.": ["prov-ex-u7-objectadj-0-ru"],
    "Я хочу нового кролика.": ["prov-ex-u7-objectadj-1-ru"],
    "— I want a new rabbit.": ["prov-ex-u7-objectadj-1-en", "span:a31"],
    "I want a new rabbit - Я хочу нового кролика.": ["prov-u7-objectadj-rem"],
    "Моя русская кошка любит мою новую собаку — My Russian cat likes my new dog.": ["prov-ex-u7-objectadj-2-ru"],
    "The separate fragment этих женщин means these women.": ["prov-ex-u7-objectadj-3-ru", "prov-ex-u7-objectadj-3-en"],
    "Its plural forms can be accusative or genitive; the source illustrates an animate accusative object.":
        ["prov-ex-u7-objectadj-3-ru", LEX("nouns.csv", "женщина", "pl_acc"), LEX("nouns.csv", "женщина", "pl_gen"),
         LEX("nouns.csv", "женщина", "animate")],
    # invariance
    "Наречия — неизменяемые слова.": ["prov-u7-adverb-invariance-rem"],
    "Adverbs do not decline or conjugate: they do not change for case, gender or number agreement.": ["prov-u7-adverb-invariance-rem"],
    "This grammatical invariance does not mean that comparative forms cannot exist.": ["prov-u7-adverb-invariance-rem", "span:r8b"],
    "Наречия — неизменяемые слова: adverbs are words that do not change form.": ["prov-u7-adverb-invariance-rem"],
    # manner
    "бы́стрый → бы́стро is a documented adjective-to-manner-adverb pair: the adjective describes a noun, while the adverb describes how an action is performed.":
        ["prov-u7-adverb-pairs-rem", "span:b1"],
    "two forms of the same noun would remain noun forms; this pair links an adjective with an adverb.":
        ["prov-u7-adverb-pairs-rem", "span:b1", LEX("adjectives.csv", "быстрый", "accented"), LEX("others.csv", "быстро", "accented")],
    "бы́стрый is an adjective describing a noun; the documented manner adverb is бы́стро.": ["prov-u7-adverb-pairs-rem", "span:b1"],
    "They have different grammatical jobs.": ["span:b1", LEX("adjectives.csv", "быстрый", "accented"), LEX("others.csv", "быстро", "accented")],
    "a manner adverb modifying an action is a different grammatical job, exemplified by бы́стро.": ["span:b1"],
    "a verb naming the action names what happens; бы́стрый instead describes a noun.": ["span:b1"],
    "быстро is a manner adverb modifying an action: adverbs describe a verb much like adjectives describe nouns.": ["span:b1"],
    "an adjective describing a noun would be бы́стрый, not the adverb быстро.": ["span:b1"],
    "a verb naming the action names what happens; быстро describes how it happens.": ["span:b1"],
    "For си́ний, this unit supplies no documented manner-adverb counterpart.": [],
    # item feedback
    "но́вая agrees with the shown feminine singular noun in the nominative, as in новая машина.":
        ["prov-ex-u7-endings-1-ru", "span:a12", LEX("nouns.csv", "машина", "gender")],
    "In a feminine singular noun phrase, the noun changes too: кни́га becomes кни́гу.": ["span:a1"],
    "Both words change while the same noun and feminine singular agreement are kept.":
        ["span:a1", LEX("adjectives.csv", "хороший", "decl_f_nom"), LEX("adjectives.csv", "хороший", "decl_f_acc"),
         LEX("nouns.csv", "книга", "sg_nom"), LEX("nouns.csv", "книга", "sg_acc")],
    "The shown line means these good new students and refers to more than one student.": ["prov-ex-u7-demonstrative-0-en", "span:a14"],
    "э́ти is the nominative plural form of this demonstrative, so it matches the plural phrase.": ["prov-ex-u7-demonstrative-0-ru"],
    "For a masculine animate object, но́вого is the accusative singular form of но́вый.": ["span:a31"],
    "It has the same form as the masculine singular genitive.": ["span:a31", "prov-ex-or-u7-objectadj-5"],
    "Feminine agreement uses a different ending.": ["span:a29", "span:a33", "prov-ex-or-u7-objectadj-6"],
    "Keep feminine singular agreement; the masculine dictionary form changes gender and does not answer this question.":
        ["prov-u7-default-rem", "span:a33", "prov-ex-or-u7-objectadj-6", "prov-ex-or-u7-objectadj-4"],
}
# The R:8 sentence the remediation repeats verbatim.
EXTRA["Наречия — неизменяемые слова: они не склоняются и не спрягаются, не имеют окончаний, не изменяются по родам и числам."] = [
    "prov-u7-adverb-invariance-rem"]
EXTRA[INVARIANCE_FEEDBACK] = ["prov-u7-adverb-invariance-rem"]
NONFACT_CYRILLIC = {"For си́ний, this unit supplies no documented manner-adverb counterpart."}


def is_frozen(pack: dict, record: dict) -> bool:
    sources = {s["id"] for s in pack["sources"]}
    rights = {r["sourceId"] for r in pack["rights"] if r.get("permitsAppUse")}
    return (record["sourceId"] not in ("src-content-audit-research",) and record.get("licenseId") != "Original app text"
            and record.get("synthesis") is None and not record["sourceRecordId"].startswith("ca-coverage:")
            and bool(record.get("sourceRevision")) and record["sourceHash"] == sha(record["originalSourceText"])
            and record["sourceId"] in sources and record["sourceId"] in rights)


def part4(b: Builder, baseline: dict, items: dict, steps: dict, objectives: dict, examples: dict, spans: dict) -> dict:
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

    frozen7 = lambda: [r for r in R.values() if r["unitNumber"] == 7 and is_frozen(pack, r)]  # noqa: E731

    def cite_forms(sentence: str, objective: str, participants: list[str]) -> list[str]:
        out = []
        pool = frozen7()
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
                raise SystemExit(f"No frozen Unit 7 record shows {form!r} ({sentence})")
            chosen = ranked[0]
            out.append(chosen["itemId"])
            m = re.match(r"^(adjectives|nouns|others)\.csv:([^#]+)#", chosen["sourceRecordId"])
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
                if not any(c.isalpha() for c in sentence) or sentence in NONFACTS or sentence in NONFACT_CYRILLIC:
                    continue
                if sentence not in EXTRA and not forms(sentence):
                    missing.append((owner, sentence))
                    continue
                ids = [resolve(ref, objective) for ref in EXTRA.get(sentence, [])] + cite_forms(sentence, objective, participants)
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
        if not item["objectiveId"].startswith("obj-u7-"):
            continue
        participants = (item.get("assessment") or {}).get("relationParticipantIds", [])
        mark(item, [item["feedback"]] + [r for r in item.get("distractorRationales", []) if r], item["objectiveId"], item["id"], participants)
    if missing:
        raise SystemExit("Sentences without a disposition: " + json.dumps(missing, ensure_ascii=False, indent=1))

    # --- Coverage of new English wording (never a marker source) -----------------------------------------------
    shipped7 = {r["shippedText"] for r in R.values() if r["unitNumber"] == 7}
    base_items = {i["id"]: i for i in baseline["items"]}
    base_steps = {s["id"]: s for u in baseline["units"] if u["number"] == 7 for se in u["sessions"] for s in se["steps"]}
    base_examples = {e["id"]: e for e in baseline["examples"]}
    counter = {}

    def cover(objective: str, owner: str, text: str) -> None:
        if not text or text in shipped7:
            return
        counter[owner] = counter.get(owner, 0) + 1
        b.coverage(objective, f"{owner}-{counter[owner]}", text)
        shipped7.add(text)
    endings_rem = "prov-ca-u7-agreement-u7-st-rem-endings-complete-v1"
    b.coverage("obj-u7-endings", "u7-st-rem-endings", steps["u7-st-rem-endings"]["body"], item_id=endings_rem)
    shipped7.add(steps["u7-st-rem-endings"]["body"])
    for step in steps.values():
        if step["kind"] in ("TEACH", "REMEDIATE") and step["body"] != base_steps.get(step["id"], {}).get("body"):
            cover(owner_objective(step["id"]), step["id"], step["body"])
    for item in pack["items"]:
        if not item["objectiveId"].startswith("obj-u7-"):
            continue
        old = base_items.get(item["id"], {})
        a, oa = item.get("assessment") or {}, old.get("assessment") or {}
        texts = [item["feedback"]] + item.get("distractorRationales", []) + [a.get("whyAnswerDemonstratesObjective", "")] + a.get("distractorDifference", [])
        olds = set([old.get("feedback", "")] + old.get("distractorRationales", []) + [oa.get("whyAnswerDemonstratesObjective", "")] + oa.get("distractorDifference", []))
        for text in texts:
            if text and text not in olds:
                cover(item["objectiveId"], item["id"], text)
    for example in examples.values():
        if example.get("objectiveId", "").startswith("obj-u7-") and example != base_examples.get(example["id"]):
            cover(example["objectiveId"], example["id"], example["note"])
    # A marked sentence is covered by a record of its own text (V2Validator.markedSentenceSourceErrors).
    for owner, sources in report.items():
        for sentence in sources:
            objective = items[owner]["objectiveId"] if owner in items else owner_objective(owner)
            cover(objective, owner, sentence)
    pack["provenanceRecords"].extend(r for r in b.new if r not in pack["provenanceRecords"])
    return report


def apply(pack: dict) -> tuple[dict, dict]:
    b, baseline, items, steps, objectives, examples, spans = part1(pack)
    typed = part2(b, items, steps, objectives, examples)
    part3(b, items, steps, objectives, typed)
    report = part4(b, baseline, items, steps, objectives, examples, spans)
    return pack, report


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15b baseline; this transformer runs only once")
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
