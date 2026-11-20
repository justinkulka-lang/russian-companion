#!/usr/bin/env python3
"""T16b (Claude Code, owner direction 2026-11-20): Unit 12 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T16a pack (6abc9ec4). Plan: Codex prep audit/prep/units-11-12-prep-at-9a9b9760.txt
(owner/prep-reports), Unit 12 section, and the S1 claim verdicts in audit/prep/s1-claim-verification.csv. Russian comes only
from frozen files already in the source inventory: Wikibooks Future tense 3353822 (F), Verbal aspect 4059032 (B), Dative case
4289617 (D), Prepositional 4567336 (Q), Adjectives 4292379 (W), Verbs 4233876; OpenRussian verbs.csv (V) and nouns.csv (N);
Wikipedia Russian alphabet 1371795481; and, for the S1 aspect claims, Wikipedia "Grammatical aspect" 1384289498 (GA, frozen by
S1 under catalog/sources/s1-intake), admitted here as a pack source. The ru Wikipedia page Вид (лингвистика) is not cited: its
revision is outside the validator's ru-Wikipedia route and the English page states every supported claim.
- A2-24: every check moves to an end-of-unit session; "admitted ... row/line" prompts become plain questions naming the
  target; u12-i-future-imp-2's target sentence gets a worked example (F:44) before guided practice.
- A2-23: eight typed checks whose keys are taught answers with exact frozen stress (report section 2).
- Facts: 30 English-only entries -> MARK or REWORD; S1 verdicts: 1369 SUPPORTED (marked, GA:74, :7); 1370 and 1380 PARTIAL
  (reworded to what GA:63, :20, :7 state, then marked).
- Section 4: the perfective-future recipe is bounded to the taught person forms (F:53, F:60); the plural-prepositional rule
  keeps Q:55's qualification.
- A2-26: governance, markup and narration leave Unit 12 learner text and ref-ca-aspect (D8).
- A2-36 references, binder and tutor tasks.
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
import apply_d7b_selection_records as d7b  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "6b2c65743de3dbab8d96236b98d319b9128c127a2265b8fea8644633eafac6ef"  # the T16a pack (8b1ee268)
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T16b", "2026-11-20", 12
ACUTE, CYR = "́", "Ѐ-ӿ"
GA_ID = "src-enwiki-grammatical-aspect-1384289498"
GA_ATTRIBUTION = ("Wikipedia contributors, CC BY-SA 4.0, via Wikipedia:Copyrights oldid 1369466282. Page history for oldid "
                  "1384289498 identifies the contributors.")
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "F": ("units-11-15/wikibooks/future-tense-3353822.txt", "prov-ex-u12-future-imp-0-ru", "future:"),
    "B": ("units-11-15/wikibooks/verbal-aspect-4059032.txt", "prov-ex-u12-aspect-0-ru", "aspect:"),
    "D": ("units-11-15/wikibooks/dative-case-4289617.txt", "prov-u12-dative-rem", "dative:"),
    "Q": ("units-3-5/wikibooks/prepositional-4567336.txt", "prov-u12-prep-plural-rem", "prepositional:"),
    "W": ("units-6-10/wikibooks/adjectives-4292379.txt", "prov-ex-u12-spelling-3-ru", "adjectives:"),
    "VB": ("units-3-5/wikibooks/verbs-4233876.txt", "prov-u4-govorit-gloss", "verbs:"),
    "AL": ("units-26-30/wikipedia/russian-alphabet-1371795481.txt", "prov-ex-u30-letter-names-0-ru", "alphabet:"),
    "GA": ("s1-intake/wikipedia/grammatical-aspect-1384289498.txt", None, "grammatical-aspect:"),
}
NOUNS = SRC / "units-3-5/openrussian/nouns.csv"
VERBS = SRC / "units-3-5/openrussian/verbs.csv"


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


def rows(path: Path) -> list[dict]:
    with path.open(encoding="utf-8") as handle:
        return list(csv.DictReader(handle, delimiter="\t", quoting=csv.QUOTE_NONE))


NOUN_ROWS, VERB_ROWS = rows(NOUNS), rows(VERBS)

# --------------------------------------------------------------------------------------------------------------
# Wording (D8)
# --------------------------------------------------------------------------------------------------------------
OBJECTIVE_TITLES = {"obj-u12-future-imp": "The compound future", "obj-u12-future-perf": "The perfective future",
                    "obj-u12-aspect": "Two aspect pairs", "obj-u12-dative": "The dative singular",
                    "obj-u12-spelling": "The five-letter spelling rule", "obj-u12-prep-plural": "The prepositional plural"}
SESSION_TITLES = {"u12-s-orient": "The future, aspect and the dative", **{f"u12-s-{k[8:]}": v for k, v in OBJECTIVE_TITLES.items()}}
STEP_TITLES = {"Admitted lines": "Examples", "Admitted instruction": "Explanation",
               "Admitted future, aspect, and row contrasts": "The future, aspect and the dative"}
ORIENT_BODY = ("This unit covers the compound and perfective future, two aspect pairs, the dative singular, the five-letter"
               " spelling rule and the prepositional plural.")
TOPIC = ("Unit 12 covers the compound and perfective future, two aspect pairs, the dative singular, the five-letter spelling"
         " rule and the prepositional plural, alongside Lesson 12 in your book.")
GUIDED_OLD = "Choose the admitted record. The feedback states the frozen field."
GUIDED_NEW = "Choose an answer, then read the feedback."
CHECK_OLD = "These checks use only admitted records. A miss returns to the explanation."
CHECK_NEW = "Answer without looking back. A miss returns to the explanation."
PROMPTS = {
    "u12-g-future-imp-0": "Which sentence is the first-person plural future?",
    "u12-i-future-imp-1": "Which sentence is the first-person singular future?",
    "u12-i-future-imp-2": "Which sentence is the second-person plural future?",
    "u12-g-future-perf-0": "Which sentence is the first-person plural future?",
    "u12-i-future-perf-1": "Which form of the same verb is the first-person plural future?",
    "u12-i-future-perf-2": "Which sentence is the first-person singular future?",
    "u12-g-dative-0": "Which form of the shown noun is the dative singular?",
    "u12-i-dative-1": "Which form of the shown noun is the dative singular?",
    "u12-i-dative-2": "Which form of the shown noun is the dative singular?",
    "u12-g-spelling-0": "Which phrase shows the accepted spelling, rather than the incorrect example?",
    "u12-i-spelling-1": "Which singular nominative neuter form of the shown adjective is correct?",
    "u12-i-spelling-2": "Which phrase is the rejected spelling, with the wrong unstressed vowel in the adjective ending?",
    "u12-g-prep-plural-0": "Which form of the shown noun is the prepositional plural?",
    "u12-i-prep-plural-1": "Which form of the shown noun is the prepositional plural?",
    "u12-i-prep-plural-2": "Which form of the shown noun is the prepositional plural?",
}
PERF2 = ("A perfective verb's person forms have future meaning. They use a single conjugated verb without a separate future"
         " auxiliary. Use the person forms taught here.")
DATIVE2 = "The dative case is traditionally the third grammatical case, and denotes the indirect object of a verb."
SPELLING2 = ("If the stem ends in ж, ц, ч, ш, or щ, any unstressed o in an ending is instead written as e. For example, the good"
             " letter is written хорошее письмо, not хорошое письмо.")
PREP2 = ("The prepositional plural ending is ах or ях. After a consonant the ending is ах. A noun ending in ь or й, in е or я,"
         " or with an irregular nominative plural ending in я, takes ях.")
S1369 = "Aspect describes how an action is viewed, not when it happens."
S1370_OLD = "Imperfective can focus on an activity, repetition, or the fact that an action occurred without emphasizing its endpoint."
S1370 = "Imperfective can present an action as a process or as a repeated or habitual event."
S1374_OLD = "These infinitives do not specify tense on their own."
S1374 = "Learn the two supplied infinitives."
S1380_OLD = "The perfective focuses on accomplishing the action; the imperfective can describe the activity or its occurrence without that focus."
S1380 = "The perfective presents the action as a complete whole; the imperfective presents it as a process."
TEACH = {
    "u12-st-teach-future-perf-2": [(None, PERF2)],
    "u12-st-teach-dative-2": [(None, DATIVE2)],
    "u12-st-teach-spelling-2": [(None, SPELLING2)],
    "u12-st-teach-prep-plural-2": [(None, PREP2)],
    "u12-st-teach-aspect-1": [(S1370_OLD, S1370), (S1374_OLD, S1374)],
    "u12-st-teach-aspect-2": [(" These checks ask you to recognize this vocabulary pair, not choose aspect in a sentence.", "")],
    "u12-st-teach-aspect-3": [(S1380_OLD, S1380), (" Given де́лать and a request for its perfective counterpart, choose сде́лать.", "")],
}
# Remediation keeps each session's existing scheme: aspect repeats its whole teaching; the others repeat the -2 explanation.
REMEDIATION = {"u12-st-rem-future-perf": "u12-st-teach-future-perf-2", "u12-st-rem-dative": "u12-st-teach-dative-2",
               "u12-st-rem-spelling": "u12-st-teach-spelling-2", "u12-st-rem-prep-plural": "u12-st-teach-prep-plural-2"}
FEEDBACK = [  # (item, old sentence, new sentence)
    # A2-26 assessment commentary ("the question asks", "source example", "table forms") leaves the feedback; facts stay.
    ("u12-g-dative-0", "кни́ге is the recorded dative singular form of кни́га. кни́ги is the nominative plural option supplied here, so it changes the number and table row.",
     "кни́ге is the dative singular form of кни́га. кни́ги is nominative plural, so it changes the number."),
    ("u12-g-dative-0", "This question asks for the dative singular row; the same spelling as the answer can also be prepositional singular in a sentence.",
     "The same spelling as the dative singular can also be prepositional singular in a sentence."),
    ("u12-g-prep-plural-0", "кни́гах is the recorded prepositional plural form of the shown noun. кни́ге is the dative singular option supplied here and can also be prepositional singular.",
     "кни́гах is the prepositional plural form of the shown noun. кни́ге is dative singular and can also be prepositional singular."),
    ("u12-g-prep-plural-0", "Neither singular use matches the plural row requested.", "Neither singular use matches the plural form requested."),
    ("u12-g-prep-plural-0", "These are table forms; the prepositional case is used with a governing preposition in a sentence.",
     "The prepositional case is used with a governing preposition in a sentence."),
    ("u12-g-spelling-0", "хорошее письмо is the accepted spelling in the source example.", "хорошее письмо is the accepted spelling."),
    ("u12-i-future-perf-2", "Я скажу́ is the first-person singular future of the perfective verb in the table, agreeing with I.",
     "Я скажу́ is the first-person singular future of this perfective verb, agreeing with I."),
    ("u12-i-future-perf-2", "Both refer to the future with a single conjugated perfective verb; the question asks for one speaker.",
     "Both refer to the future with a single conjugated perfective verb; the singular form refers to one speaker."),
    ("u12-i-prep-plural-2", "дома́х is the recorded prepositional plural form of the shown noun. до́му is the supplied dative singular option. Select the prepositional plural row while keeping the plural number of the cue.",
     "дома́х is the prepositional plural form of the shown noun. до́му is dative singular. Select the prepositional plural form while keeping the plural number of the cue."),
    ("u12-i-prep-plural-2", "These are table forms; a sentence uses the prepositional case after a governing preposition.",
     "A sentence uses the prepositional case after a governing preposition."),
    ("u12-i-future-perf-1", "The cue shows first-person singular, but the requested table row is first-person plural.",
     "Choose the requested person and number."),
    ("u12-i-dative-1", "Keep the noun singular and select its dative row, rather than selecting the plural nominative row.",
     "Keep the noun singular and select its dative form, rather than the plural nominative form."),
]
RATIONALES = [  # (item, index, old, new)
    ("u12-g-dative-0", 1, "кни́ги is the nominative plural option supplied here, not the requested dative singular table row.",
     "кни́ги is nominative plural, not the requested dative singular."),
    ("u12-g-prep-plural-0", 1, "кни́ге is singular, including the supplied dative singular row, rather than prepositional plural.",
     "кни́ге is singular, as dative or prepositional singular, rather than prepositional plural."),
    ("u12-i-spelling-2", 1, "хорошее письмо is the accepted spelling, while this question asks you to identify the rejected spelling.",
     "хорошее письмо is the accepted spelling, not the rejected spelling requested here."),
]
ASPECT_REF = {"title": "Aspect", "summary": "Aspect pairs, the aspect of быть, and what an imperfective past means.",
              "detail": ("Aspect pairs are bounded: not every verb has a partner, and each pair is learned as a pair. быть is"
                         " imperfective. An imperfective past need not mean the event was unfinished."),
              "searchTerms": ["aspect", "perfective", "imperfective"]}
# u12-i-future-imp-2: a worked example of the target sentence before guided practice (F:44).
NEW_EXAMPLE = ("ex-u12-future-imp-4", "Вы бу́дете говори́ть", "You will be speaking", "u12-st-teach-future-imp-1")

TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
TYPED = [  # (id, objective, cue record, key record, prompt)
    ("u12-i-type-knige", "obj-u12-dative", "prov-ex-or-u12-dative-0", "prov-ex-or-u12-dative-1", "Type the dative singular form of the shown noun."),
    ("u12-i-type-stolu", "obj-u12-dative", "prov-ex-or-u12-dative-3", "prov-ex-or-u12-dative-4", "Type the dative singular form of the shown noun."),
    ("u12-i-type-domu", "obj-u12-dative", "prov-ex-or-u12-dative-6", "prov-ex-or-u12-dative-7", "Type the dative singular form of the shown noun."),
    ("u12-i-type-knigakh", "obj-u12-prep-plural", "prov-ex-or-u12-prep-plural-0", "prov-ex-or-u12-prep-plural-1",
     "Type the prepositional plural form of the shown noun."),
    ("u12-i-type-stolakh", "obj-u12-prep-plural", "prov-ex-or-u12-prep-plural-3", "prov-ex-or-u12-prep-plural-4",
     "Type the prepositional plural form of the shown noun."),
    ("u12-i-type-domakh", "obj-u12-prep-plural", "prov-ex-or-u12-prep-plural-6", "prov-ex-or-u12-prep-plural-7",
     "Type the prepositional plural form of the shown noun."),
    ("u12-i-type-skazhu", "obj-u12-future-perf", "CUE:сказа́ть", "prov-ex-or-u12-future-perf-2",
     "Type the first-person singular future form of the shown verb."),
    ("u12-i-type-skazhesh", "obj-u12-future-perf", "CUE:сказа́ть", "prov-ex-or-u12-future-perf-4",
     "Type the second-person singular future form of the shown verb."),
]

REFERENCES = [
    {"id": "ref-u12-future", "title": "The future tense",
     "summary": "The compound future of imperfective verbs and the future of perfective verbs.",
     "detail": ("An imperfective verb forms its future with the future of быть and the infinitive: Я бу́ду говори́ть — I will be"
                " speaking. Мы бу́дем говори́ть — We will be speaking. Ты бу́дешь говори́ть — You will be speaking. Вы бу́дете"
                " говори́ть — You will be speaking. Он бу́дет говори́ть — He will be speaking.\n\nA perfective verb's person"
                " forms have future meaning, with no separate future auxiliary: Я скажу́ — I will speak. Мы ска́жем — We will"
                " speak."),
     "searchTerms": ["future tense", "will", "perfective future"], "steps": ["u12-st-teach-future-imp-1", "u12-st-teach-future-perf-1"]},
    {"id": "ref-u12-dative-prepositional", "title": "The dative singular and prepositional plural",
     "summary": "Dative singular nouns for the indirect object, and prepositional plural endings.",
     "detail": (DATIVE2 + " кни́га — кни́ге. сто́л — столу́. до́м — до́му.\n\n" + PREP2 + " кни́ги — кни́гах. столы́ — стола́х."
                " дома́ — дома́х."),
     "searchTerms": ["dative case", "indirect object", "prepositional plural"],
     "steps": ["u12-st-teach-dative-1", "u12-st-teach-prep-plural-1"]},
    {"id": "ref-u12-spelling", "title": "The five-letter spelling rule",
     "summary": "After ж, ц, ч, ш and щ, an unstressed o in an ending is written e.",
     "detail": SPELLING2, "searchTerms": ["spelling rule", "unstressed ending"], "steps": ["u12-st-teach-spelling-1"]},
]
BINDER = {"id": "binder-u12-future", "objectiveIds": ["obj-u12-future-imp", "obj-u12-future-perf", "obj-u12-aspect"],
          "prompt": ("In your binder, copy this unit's future sentences for I and we with their meanings, first with the verb"
                     " meaning to speak in the compound future and then with its perfective partner. Then write the two aspect"
                     " pairs with each verb's aspect. Compare your work with the model answer."),
          "modelAnswer": ("Я бу́ду говори́ть — I will be speaking. Мы бу́дем говори́ть — We will be speaking.\nЯ скажу́ — I will"
                          " speak. Мы ска́жем — We will speak.\nговори́ть: imperfective. сказа́ть: perfective. де́лать: imperfective."
                          " сде́лать: perfective.")}
TUTOR = {"id": "tutor-u12-future", "bring": "Your binder future sentences and aspect pairs, to read each line aloud and say which aspect each verb has."}

# --------------------------------------------------------------------------------------------------------------
# Facts
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
    return [text[m.start():m.end()].strip() for m in ENGLISH_SENTENCE.finditer("".join(masked)) if text[m.start():m.end()].strip()]


def forms(text: str) -> set[str]:
    return {m.group(0).lower() for m in FACT_FORM.finditer(stressed(text))}


NONFACTS = {
    "Compare your answer with the worked examples for this form, then try again.",
    "Stress marks are optional when you type.",
    "Use the person forms taught here.",
    "Choose the requested person and number.",
    "Learn the two supplied infinitives.",
    "Select the prepositional plural form while keeping the plural number of the cue.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


# English-only sentences and their evidence (report section 3, S1 verdicts): ("F", n) = a page line record, ("N"|"V", row
# accented, field) = an OpenRussian cell record, else a record id.
EXTRA: dict[str, list] = {
    "Both use a future auxiliary plus the same imperfective infinitive; choose the plural line requested here.": p("F", 34, 41),
    "The imperfective infinitive stays the same while the subject and auxiliary change.": p("F", 41, 44, 47),
    "This form can also address one person politely.": p("VB", 8),
    "The same imperfective infinitive follows each auxiliary.": p("F", 41, 44, 47),
    "These perfective future options use a single conjugated verb without a separate future auxiliary; choose the plural line.":
        p("F", 53, 60),
    "Both refer to the future with a single conjugated perfective verb; the singular form refers to one speaker.": p("F", 53, 60),
    "A perfective verb's person forms have future meaning.": p("F", 53, 60),
    "They use a single conjugated verb without a separate future auxiliary.": p("F", 53, 60),
    S1369: p("GA", 74, 7),
    S1370: p("GA", 63, 7),
    "Perfective presents an event with its boundary or result.": p("B", 6, 17),
    "Imperfective does not by itself mean unfinished.": p("B", 12),
    "Many Russian verbs are learned in an aspect pair, but not every verb has two words, and a pair is not automatic.":
        p("B", 6, 25, 26, 27, 28, 29),
    "Both concern doing or making.": p("B", 7) + [("V", "де'лать", "accented"), ("V", "сде'лать", "accented")],
    S1380: p("GA", 63, 20) + p("B", 12),
    "Learn the shown pair rather than assuming every prefix changes only aspect.": p("B", 25, 26, 27, 28, 29),
    "The same spelling as the dative singular can also be prepositional singular in a sentence.":
        [("N", "кни'га", "sg_dat"), ("N", "кни'га", "sg_prep")],
    DATIVE2: p("D", 1, 6),
    "Keep the noun singular and select its dative form, rather than the plural nominative form.":
        ["prov-ex-or-u12-dative-4", "prov-ex-or-u12-dative-5"],
    "The requested row changes case while keeping the noun singular; the plural option changes number instead.":
        ["prov-ex-or-u12-dative-6", "prov-ex-or-u12-dative-7", "prov-ex-or-u12-dative-8"],
    "The adjective stem ends in the consonant transliterated sh; its unstressed neuter ending uses e rather than o under the"
    " five-letter spelling rule.": p("W", 26) + p("AL", 156),
    "The adjective agrees with the gender of the noun it modifies; the neuter form requested here is also the form used with the"
    " neuter noun in the good-letter example.": p("W", 1, 26),
    "Its unstressed adjective ending uses o where the five-letter rule requires e after the stem-final consonant transliterated"
    " sh.": p("W", 26) + p("AL", 156),
    "The keyed counterexample is incorrect Russian, not a form to use.": p("W", 26),
    "Neither singular use matches the plural form requested.": ["prov-ex-or-u12-prep-plural-1", "prov-ex-or-u12-prep-plural-2"],
    "The prepositional case is used with a governing preposition in a sentence.": p("Q", 4),
    "The question requires both prepositional case and plural number; the singular dative option changes both features.":
        ["prov-ex-or-u12-prep-plural-4", "prov-ex-or-u12-prep-plural-5"],
    "In a sentence the prepositional form follows a governing preposition.": p("Q", 4),
    "A sentence uses the prepositional case after a governing preposition.": p("Q", 4),
    "The prepositional plural ending is ах or ях.": p("Q", 55),
    "After a consonant the ending is ах.": p("Q", 55),
    "A noun ending in ь or й, in е or я, or with an irregular nominative plural ending in я, takes ях.": p("Q", 55),
    "If the stem ends in ж, ц, ч, ш, or щ, any unstressed o in an ending is instead written as e.": p("W", 26),
    "For example, the good letter is written хорошее письмо, not хорошое письмо.": p("W", 26),
}
ENDINGS = [("Q", 55, region) for region in ("ь", "й", "е", "я")]


def edit(text: str, old: str, new: str, where: str) -> str:
    if text.count(old) != 1:
        raise SystemExit(f"{where}: baseline text changed ({old!r})")
    return text.replace(old, new)


class Builder:
    def __init__(self, pack: dict):
        self.pack = pack
        self.records = {r["itemId"]: r for r in pack["provenanceRecords"]}
        self.unit = next(u for u in pack["units"] if u["number"] == UNIT)

    def add(self, record: dict) -> str:
        if record["itemId"] in self.records:
            raise SystemExit(f"Duplicate record {record['itemId']}")
        self.records[record["itemId"]] = record
        self.pack["provenanceRecords"].append(record)
        if record["sourceId"] != "src-content-audit-research" and not d7b.frozen_record(self.pack, record):
            raise SystemExit(f"{record['itemId']} is not a frozen-source record")
        return record["itemId"]

    def template(self, page: str) -> dict:
        rel, tmpl, _ = PAGES[page]
        if tmpl:
            return copy.deepcopy(self.records[tmpl])
        record = copy.deepcopy(self.records["prov-ex-u16-complements-0-ru"])  # an English Wikipedia selection record
        record.update({"sourceId": GA_ID, "sourceRevision": "1384289498", "attribution": GA_ATTRIBUTION,
                       "sourceUrl": "https://en.wikipedia.org/w/index.php?title=Grammatical_aspect&oldid=1384289498"})
        return record

    def line(self, page: str, number: int, objective: str) -> str:
        record_id = f"prov-t16b-u12-{page.lower()}{number}"
        if record_id in self.records:
            return record_id
        rel, _, prefix = PAGES[page]
        text = (SRC / rel).read_text(encoding="utf-8")
        raw = text.split("\n")[number - 1].strip()
        if text.count(raw) != 1:
            raise SystemExit(f"{page}:{number} is not unique on its page")
        record = self.template(page)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": prefix + raw[:80],
                       "originalSourceText": raw, "shippedText": raw, "sourceHash": sha(raw), "contentHash": sha(raw),
                       "recordType": "instruction", "transformation": "selection"})
        return self.add(record)

    def ending(self, page: str, number: int, region: str, objective: str) -> str:
        rel, _, prefix = PAGES[page]
        raw = (SRC / rel).read_text(encoding="utf-8").split("\n")[number - 1].strip()
        if not d7b.two_part(d7b.displayed(raw), region):
            raise SystemExit(f"{region} fails D19a against {rel}:{number}")
        record = self.template(page)
        record.update({"itemId": f"prov-t16b-u12-{page.lower()}{number}-{region}", "unitNumber": UNIT, "objectiveId": objective,
                       "sourceRecordId": prefix + raw[:80], "originalSourceText": raw, "shippedText": region,
                       "sourceHash": sha(raw), "contentHash": sha(region), "recordType": "example", "transformation": "selection"})
        return self.add(record)

    def cell(self, kind: str, accented: str, field: str, objective: str) -> str:
        table, template = ((NOUN_ROWS, "prov-ex-or-u12-dative-1") if kind == "N" else (VERB_ROWS, "prov-ex-or-u12-future-perf-2"))
        hits = [r for r in table if r["accented"] == accented]
        if len(hits) != 1:
            raise SystemExit(f"{kind}: {len(hits)} rows for {accented}")
        original = hits[0][field].strip()
        if not original or "," in original:
            raise SystemExit(f"{kind}:{accented}#{field} is not a single form")
        file = "nouns.csv" if kind == "N" else "verbs.csv"
        for r in self.records.values():
            if r["unitNumber"] == UNIT and r["sourceRecordId"] == f"{file}:{accented}#{field}":
                return r["itemId"]
        record = copy.deepcopy(self.records[template])
        record.update({"itemId": f"prov-t16b-u12-{kind.lower()}-{sha(accented + field)[:8]}-{field}", "objectiveId": objective,
                       "sourceRecordId": f"{file}:{accented}#{field}", "originalSourceText": original,
                       "shippedText": stressed(original), "sourceHash": sha(original), "contentHash": sha(stressed(original)),
                       "recordType": "lexical record"})
        return self.add(record)


def is_frozen(pack: dict, record: dict) -> bool:
    return d7b.frozen_record(pack, record)


def admit_ga(pack: dict) -> None:
    """S1 froze Grammatical aspect 1384289498; it becomes a pack source with its rights entry (as S1 did for некого)."""
    manifest = json.loads((SRC / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
    page = next(x for x in manifest["pages"] if x["sourceId"] == GA_ID)
    data = (SRC / "s1-intake" / page["file"]).read_bytes()
    if hashlib.sha256(data).hexdigest() != page["sha256"] or page["attribution"] != GA_ATTRIBUTION:
        raise SystemExit("Grammatical aspect does not match its FREEZE.json entry")
    if any(s["id"] == GA_ID for s in pack["sources"]):
        raise SystemExit("Grammatical aspect is already a pack source")
    template = next(s for s in pack["sources"] if s["id"] == "src-wikipedia-russian-grammar-1369617627")
    pack["sources"].append({**copy.deepcopy(template), "id": GA_ID, "title": "Grammatical aspect", "attribution": GA_ATTRIBUTION})
    pack["rights"].append({"id": "rights-enwiki-grammatical-aspect-1384289498", "sourceId": GA_ID, "license": "CC BY-SA 4.0",
                           "permitsAppUse": True})


def apply(pack: dict) -> dict:
    admit_ga(pack)
    b = Builder(pack)
    unit = b.unit
    baseline = copy.deepcopy(pack)
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R = b.records

    for page, number, region in ENDINGS:
        b.ending(page, number, region, "obj-u12-prep-plural")
    # --- teaching and remediation ----------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = new if old is None else edit(steps[step_id]["body"], old, new, step_id)
    for rem, teach in REMEDIATION.items():
        steps[rem]["body"] = steps[teach]["body"]
    aspect = next(se for se in unit["sessions"] if se["id"] == "u12-s-aspect")
    steps["u12-st-rem-aspect"]["body"] = "\n\n".join(s["body"] for s in aspect["steps"] if s["kind"] == "TEACH")
    # --- worked example of the second-person plural future (F:44) before guided practice -------------------------
    ex_id, display, note, step_id = NEW_EXAMPLE
    template_ex = next(e for e in pack["examples"] if e["id"] == "ex-u12-future-imp-2")
    pack["examples"].append({**copy.deepcopy(template_ex), "id": ex_id, "display": display, "note": note})
    for suffix, text in (("ru", display), ("en", note)):
        record = copy.deepcopy(R[f"prov-ex-u12-future-imp-2-{suffix}"])
        if text not in record["originalSourceText"]:
            raise SystemExit(f"{text} is not on F:44")
        record.update({"itemId": f"prov-{ex_id}-{suffix}", "shippedText": text, "contentHash": sha(text)})
        b.add(record)
    steps[step_id]["body"] += f"{display} — {note}. "
    steps[step_id]["exampleIds"].append(ex_id)
    objectives["obj-u12-future-imp"]["exampleIds"].append(ex_id)
    # --- ref-ca-aspect (D8) ---------------------------------------------------------------------------------------
    ref = next(r for r in pack["references"] if r["id"] == "ref-ca-aspect")
    ref.update(copy.deepcopy(ASPECT_REF))
    # --- items ----------------------------------------------------------------------------------------------------
    for item_id, prompt in PROMPTS.items():
        items[item_id]["prompt"] = prompt
    for item_id, old, new in FEEDBACK:
        item = items[item_id]
        item["feedback"] = edit(item["feedback"], old, new, item_id)
        item["assessment"]["whyAnswerDemonstratesObjective"] = edit(item["assessment"]["whyAnswerDemonstratesObjective"], old, new, item_id)
    for item_id, index, old, new in RATIONALES:
        item = items[item_id]
        if item["distractorRationales"][index] != old or item["assessment"]["distractorDifference"][index] != old:
            raise SystemExit(f"{item_id}: rationale {index} changed")
        item["distractorRationales"][index] = new
        item["assessment"]["distractorDifference"][index] = new
    typed = []
    cue_record = next(r for r in R.values() if r["unitNumber"] == UNIT and r["shippedText"] == "сказа́ть" and is_frozen(pack, r))
    for item_id, objective, cue_id, key_id, prompt in TYPED:
        cue = cue_record["shippedText"] if cue_id.startswith("CUE:") else R[cue_id]["shippedText"]
        key_record = R[key_id]
        file, rest = key_record["sourceRecordId"].split(":", 1)
        accented, field = rest.split("#")
        table = NOUN_ROWS if file == "nouns.csv" else VERB_ROWS
        if stressed(next(r for r in table if r["accented"] == accented)[field]) != key_record["shippedText"]:
            raise SystemExit(f"{item_id}: key is not the exact frozen stress")
        typed.append(typed_item(item_id, objective, prompt, cue, key_record["shippedText"], TYPED_FEEDBACK, "src-openrussian-50e210c4"))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    items.update({i["id"]: i for i in typed})
    # --- titles, bodies; checks move to an end-of-unit session ----------------------------------------------------
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
    steps["u12-st-orient"]["body"] = ORIENT_BODY
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
        if step["body"] == CHECK_OLD:
            step["body"] = CHECK_NEW
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u12-s-book")
    unit["sessions"].insert(book, {"id": "u12-s-checks", "title": "Unit 12 checks", "estimatedMinutes": 25,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    # --- references, binder and tutor -----------------------------------------------------------------------------
    template = next(r for r in pack["references"] if r["id"] == "ref-u10-possession")
    for spec in REFERENCES:
        reference = {k: template[k] for k in ("sourceId", "reviewStatus", "classification")}
        reference.update({k: spec[k] for k in ("id", "title", "summary", "detail", "searchTerms")})
        reference["introducedInUnit"] = UNIT
        pack["references"].append({k: reference[k] for k in template if k in reference})
        for sid in spec["steps"]:
            steps[sid]["referenceIds"] = list(dict.fromkeys(steps[sid].get("referenceIds", []) + [spec["id"]]))
    if unit.get("binderTask") or unit.get("tutorTask"):
        raise SystemExit("Unit 12 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every changed Unit 12 learner field: Russian only through frozen Unit 12 records ------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and is_frozen(pack, r)}
    texts = [s["body"] for s in steps.values()] + [unit["topicSummary"], BINDER["prompt"], BINDER["modelAnswer"]]
    texts += [r[k] for r in pack["references"] if r["id"] in {s["id"] for s in REFERENCES} | {"ref-ca-aspect"}
              for k in ("title", "summary", "detail")]
    for item in items.values():
        if item["objectiveId"].startswith("obj-u12-"):
            texts += [item["prompt"], item.get("promptLetter") or "", item["answer"], item["feedback"]] + item["options"] + item.get("distractorRationales", [])
    for text in texts:
        for m in re.finditer("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*", text):
            region = m.group(0).strip()
            if region not in frozen and region.rstrip("?!.,") not in frozen:
                raise SystemExit(f"Unit 12 region without a frozen record: {region!r} in {text[:80]!r}")
    report = mark_facts(b, steps, items)
    cover_english(pack, baseline)
    return report


def mark_facts(b: Builder, steps: dict, items: dict) -> dict:
    pack, R, unit = b.pack, b.records, b.unit

    def owner_objective(step_id: str) -> str:
        for o in unit["objectives"]:
            if step_id in o["instructionStepIds"] or step_id in o["remediationStepIds"] or step_id in o["practiceStepIds"]:
                return o["id"]
        return next(se for se in unit["sessions"] if any(s["id"] == step_id for s in se["steps"]))["objectiveIds"][0]

    def cite_forms(sentence: str, objective: str, participants: list[str]) -> list[str]:
        out, pool = [], [x for x in R.values() if x["unitNumber"] == UNIT and is_frozen(pack, x)]
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
                raise SystemExit(f"No frozen Unit 12 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    def resolve(ref, objective: str) -> str:
        if isinstance(ref, tuple) and len(ref) == 2:
            return b.line(ref[0], ref[1], objective)
        if isinstance(ref, tuple):
            return b.cell(ref[0], ref[1], ref[2], objective)
        return ref

    report, missing = {}, []

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
                ids = [resolve(r, objective) for r in refs or []] + cite_forms(sentence, objective, participants)
                ids = list(dict.fromkeys(ids))
                if sentence not in sources:
                    facts.append(sentence)
                    sources[sentence] = ids
        if facts:
            host["russianFacts"], host["russianFactSources"] = facts, sources
        else:
            host.pop("russianFacts", None)
            host.pop("russianFactSources", None)
        report[owner] = sources

    for step in steps.values():
        if step["kind"] == "REMEDIATE":
            mark(step, [step["body"]], owner_objective(step["id"]), step["id"], [])
    for item in items.values():
        if item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            participants = (item.get("assessment") or {}).get("relationParticipantIds", [])
            mark(item, [item["feedback"]] + [x for x in item.get("distractorRationales", []) if x], item["objectiveId"], item["id"], participants)
    if missing:
        raise SystemExit("Sentences without a disposition: " + json.dumps(missing, ensure_ascii=False, indent=1))
    return report


def english_hosts(pack: dict) -> dict:
    out = {}
    unit = next(u for u in pack["units"] if u["number"] == UNIT)
    first = unit["objectives"][0]["id"]
    objective_of = {}
    for o in unit["objectives"]:
        for sid in o.get("instructionStepIds", []) + o.get("remediationStepIds", []) + o.get("practiceStepIds", []):
            objective_of.setdefault(sid, o["id"])
    for s in unit["sessions"]:
        for st in s["steps"]:
            if st["kind"] in ("TEACH", "REMEDIATE", "ORIENT", "GUIDED", "CHECK"):
                out[(st["id"], "body")] = (UNIT, objective_of.get(st["id"], first), st["body"], list(st.get("russianFacts", [])))
    for i in pack["items"]:
        if not i["objectiveId"].startswith(f"obj-u{UNIT}-"):
            continue
        n, o, facts = UNIT, i["objectiveId"], list(i.get("russianFacts", []))
        out[(i["id"], "feedback")] = (n, o, i["feedback"], facts)
        for k, v in enumerate(i.get("distractorRationales", [])):
            out[(i["id"], f"rationale{k}")] = (n, o, v, facts)
        a = i.get("assessment") or {}
        out[(i["id"], "why")] = (n, o, a.get("whyAnswerDemonstratesObjective") or "", [])
        for k, v in enumerate(a.get("distractorDifference") or []):
            out[(i["id"], f"difference{k}")] = (n, o, v, [])
    for e in pack["examples"]:
        if e.get("objectiveId", "").startswith(f"obj-u{UNIT}-"):
            out[(e["id"], "note")] = (UNIT, e["objectiveId"], e.get("note") or "", [])
    for r in pack["references"]:
        if r["id"] in {s["id"] for s in REFERENCES} | {"ref-ca-aspect"}:
            for k in ("title", "summary", "detail"):
                out[(r["id"], k)] = (UNIT, first, r[k], [])
    if unit.get("binderTask"):
        for k in ("prompt", "modelAnswer"):
            out[(unit["binderTask"]["id"], k)] = (UNIT, unit["binderTask"]["objectiveIds"][0], unit["binderTask"][k], [])
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
        item_id = f"prov-ca-t16b-{key}"
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

    old_hosts = english_hosts(before)
    hosts = english_hosts(pack)
    for (owner, field), (unit, objective, text, facts) in sorted(hosts.items()):
        old = old_hosts.get((owner, field))
        if old is None or old[2] != text:
            coverage(unit, objective, f"{owner}-{field}", text)
    for (owner, field), (unit, objective, text, facts) in sorted(hosts.items()):
        for n, fact in enumerate(facts):
            coverage(unit, objective, f"{owner}-{field}-fact{n}", fact)
    print(f"english coverage records: {count}")


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T16b baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
