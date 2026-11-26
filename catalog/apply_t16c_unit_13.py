#!/usr/bin/env python3
"""T16c (Claude Code, owner direction 2026-11-21): Unit 13 meets A2-23/24/26 with frozen-source facts.

Exact-baseline transformer; runs once on the T16-n2 pack (b1c94322). Plan: Codex prep audit/prep/units-13-15-prep-at-4c402ef7.txt
(owner/prep-reports), Unit 13 section; the S1 claim verdicts in audit/prep/s1-claim-verification.csv; and the D31c learner-text
sweep (audit/T16-n2-learner-text-sweep-units-01-15.csv, Unit 13 rows, applied first). Russian comes only from frozen files already
in the source inventory: Wikibooks Past tense 4567337 (P), Future tense 3353822 (F), Verbal aspect 4059032 (B); OpenRussian
verbs.csv (V); and the S1 pages Sequence of tenses 1363198135 (SOT), Grammatical aspect 1384289498 (GA) and
Вид (лингвистика) 153277825 (VID). SOT and VID become pack sources here (D29a admits their revisions).
- A2-24: every check moves to an end-of-unit session; prompts name the target (sweep); u13-i-tense-keep-2 chooses between the
  two Russian nationality reports of F:22; u13-i-reported-1 and -2 take different foils (report section 1); the infinitive
  teaching no longer states the check answers.
- A2-23: eleven typed checks whose keys are taught answers with exact frozen stress (report section 2); the five forms not yet
  taught in Unit 13 get worked examples and a teaching card first.
- Facts: 68 English-only entries -> MARK, REWORD or REMOVE (report section 3); S1 verdicts: 1462, 1470, 1482, 1511, 1528
  SUPPORTED (marked, SOT); 1443, 1508, 1523, 1542, 1543 PARTIAL (reworded to what GA:63 and SOT:21, :39, :17 state, then
  marked); 1517 NOT_SUPPORTED (removed).
- A2-26: governance, markup and narration leave Unit 13 learner text; the mapping note follows the Units 1-12 style.
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
import apply_t16n2 as sweep  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "dfe088a66d4981f0472b206042ef843a6f0ae820d89d42890928afb02471e76c"  # the T16-n2 pack (b1c94322)
SRC = ROOT / "catalog/sources"
TASK, DATE, UNIT = "T16c", "2026-11-21", 13
ACUTE, CYR = "́", "Ѐ-ӿ"
FREEZE = json.loads((SRC / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
S1 = {p["key"]: p for p in FREEZE["pages"] + FREEZE["supplemental"]}
GA_ID = "src-enwiki-grammatical-aspect-1384289498"
PAGES = {  # key: (file, template record, sourceRecordId prefix)
    "P": ("units-11-15/wikibooks/past-tense-4567337.txt", "prov-u13-past-aspect-rem", "past:"),
    "F": ("units-11-15/wikibooks/future-tense-3353822.txt", "prov-u13-tense-keep-rem", "future:"),
    "B": ("units-11-15/wikibooks/verbal-aspect-4059032.txt", "prov-u13-infinitive-rem", "aspect:"),
    "GA": ("s1-intake/wikipedia/grammatical-aspect-1384289498.txt", "S1:aspect", "grammatical-aspect:"),
    "SOT": ("s1-intake/wikipedia/sequence-of-tenses-1363198135.txt", "S1:sequence", "sequence-of-tenses:"),
    "VID": ("s1-intake/ruwiki/vid-lingvistika-153277825.txt", "S1:vid", "ruwiki-vid:"),
}
VERBS = SRC / "units-3-5/openrussian/verbs.csv"
# VID:9 carries a <ref> citation with web URLs between its two claim spans; only the spans are recorded.
VID_SPANS = [
    "В русском языке, как и во многих других [[славянские языки|славянских]], вид является грамматической категорией: это "
    "значит, что каждый глагол охарактеризован как относящийся либо к ''[[Совершенный вид|совершенному]]'', либо к "
    "''несовершенному'' виду",
    "(за исключением небольшого числа ''двувидовых'' глаголов вроде «жениться», «обжаловать»).",
]


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(text: str) -> str:
    return re.sub(f"(?<=[{CYR}])'(?!')", ACUTE, text)


VERB_ROWS = list(csv.DictReader(VERBS.open(encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE))

# --------------------------------------------------------------------------------------------------------------
# Wording (after the sweep rows)
# --------------------------------------------------------------------------------------------------------------
TOPIC = ("Unit 13 covers aspect in the past tense, keeping the tense in reported statements, aspect partners and reported"
         " speech, alongside Lesson 13 in your book.")
MAPPING = ("This unit goes with Lesson 13 in your book, printed pages 149-161: aspect in past-tense contexts, tense selection,"
           " infinitive aspect and reported speech.")
CHECK_OLD = "Answer using the examples taught here. A miss returns to the explanation."
CHECK_NEW = "Answer without looking back. A miss returns to the explanation."
S1443_OLD = ("An activity may have ended and still be described imperfectively when the focus is its process or occurrence rather"
             " than its endpoint.")
S1443 = "The imperfective presents an event as a process, or as a repeated or habitual event."
VID_SENTENCE = "Each Russian verb is either perfective or imperfective, apart from a small number of verbs that are both."
S1508 = ("English backshift after a past reporting verb is optional; Russian indirect speech generally keeps the tense of the"
         " original statement.")
S1523 = "Russian indirect speech generally keeps the tense of the original statement."
S1542 = "In the natural sequence of tenses, the embedded verb's tense follows the sense of its own clause."
NEW_PAST = ("чита́ло is the neuter singular past form of чита́ть, and чита́ли is its plural past form. прочита́ть is the perfective"
            " partner of чита́ть. Its masculine, feminine and neuter singular past forms are прочита́л, прочита́ла and прочита́ло.")
TEACH = {  # step: [(old, new)]; old None replaces the body
    "u13-st-teach-past-aspect-2": [(S1443_OLD, S1443)],
    "u13-st-teach-past-aspect-3": [(" The English gloss read alone does not distinguish past tense from infinitive.", "")],
    "u13-st-teach-tense-keep-2": [("Classify these supplied labels rather than applying an always-change or never-change rule to"
                                   " unfamiliar reports.",
                                   "Compare the two labels with the examples rather than applying an always-change or never-change rule.")],
    "u13-st-teach-tense-keep-3": [("It corresponds to the direct evening statement by the event described; pronoun changes depend on"
                                   " who is speaking.", "Compare the supplied direct and reported evening examples.")],
    "u13-st-teach-infinitive-1": [("The gloss to speak is broad; these are context-dependent counterparts for saying/telling, not"
                                   " interchangeable verbs for every meaning of English speak.", "Use the saying/telling pair shown here."),
                                  ("Infinitives by themselves do not specify tense.", "Learn the supplied infinitives. " + VID_SENTENCE)],
    "u13-st-teach-infinitive-2": [(None, "Keep both the aspect and the meaning of the pair in view. Learn the two verb pairs, then"
                                         " practise choosing aspect in sentence contexts and producing verbs separately.")],
    "u13-st-teach-infinitive-3": [(" Given де́лать and a request for its perfective partner, choose сде́лать. Given сказа́ть and a"
                                   " request for its imperfective partner in the saying/telling pair, choose говори́ть.", "")],
    "u13-st-teach-reported-1": [
        ("Past reporting verb with present nationality statement. is the matching description.",
         "It has a past reporting verb with a present nationality statement."),
        (" That alternative can be grammatical in a different context; it is not this supplied present-nationality report.", "")],
    "u13-st-teach-reported-2": [
        ("Past reporting verb with a future embedded action. describes its two finite verbs: said is past, and the embedded telling"
         " action retains future form within reported content.",
         "It has a past reporting verb with a future embedded action. The reporting verb said is past, and the embedded telling"
         " action keeps its future form."),
        ("This does not imply the action must still be future compared with the present moment.", S1523),
        ("Pronoun choice depends on the speaker; do not mechanically replace every I with she or manufacture reported clauses.",
         "Use the supplied examples.")],
}
NEW_STEP = {"id": "u13-st-teach-past-aspect-5", "after": "u13-st-teach-past-aspect-4", "title": "More past forms",
            "body": NEW_PAST}
NEW_EXAMPLES = [  # (example id, accented row, field, gloss row)
    ("ex-or-u13-past-aspect-5", "чита'ть", "past_n"), ("ex-or-u13-past-aspect-6", "чита'ть", "past_pl"),
    ("ex-or-u13-past-aspect-7", "прочита'ть", "accented"), ("ex-or-u13-past-aspect-8", "прочита'ть", "past_m"),
    ("ex-or-u13-past-aspect-9", "прочита'ть", "past_f"), ("ex-or-u13-past-aspect-10", "прочита'ть", "past_n"),
]
FEEDBACK = [  # (item, old sentence, new sentence)
    ("u13-i-tense-keep-1", "Recognize the supplied examples; pronoun changes depend on who is speaking.", "Compare the supplied examples."),
    ("u13-g-infinitive-0", "The pairing here does not mean every use of speak or talk can be replaced by the same perfective verb.",
     "Use the saying/telling pair shown here."),
    ("u13-g-reported-0", "In Он сказа́л, что он англича́нин, Past reporting verb with present nationality statement.",
     "Он сказа́л, что он англича́нин has a past reporting verb with a present nationality statement."),
    # 1507 states the same unsupported claim as 1517 (S1 NOT_SUPPORTED): the clause is removed; the sentence stays marked.
    ("u13-g-reported-0", "The alternative with был explicitly supplies a past copula and changes the stated reading; it is not"
                         " universally ungrammatical.", "The alternative with был explicitly supplies a past copula and changes the"
                                                       " stated reading."),
    ("u13-g-reported-0", "Recognize this supplied context; English backshift and Russian tense/pronoun choices are not automatic rules"
                         " for every report.", S1508),
    ("u13-i-report-frame-time", "In Она́ сказа́ла, что она́ ска́жет ему́ ве́чером, Past reporting verb with a future embedded action.",
     "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером has a past reporting verb with a future embedded action."),
    ("u13-i-report-frame-time", "Future is interpreted within the reported content, not a guarantee that the action is after the"
                                " present moment or has been completed.", S1542),
    ("u13-i-report-frame-time", "Recognize this supplied context; English backshift and Russian tense/pronoun choices are not"
                                " automatic rules for every report.", S1508),
]
RATIONALES = [  # (item, index, new)
    ("u13-g-reported-0", 1, "A past nationality statement does not describe this report, whose nationality statement has no past copula."),
    ("u13-i-report-frame-time", 1, "A future reporting verb with a past embedded action does not describe this report, whose reporting"
                                   " verb is past and whose embedded verb is future."),
]
# Report section 1: three redesigned checks (prompt, cue record, options [(record, text)], answer, feedback, rationale, feature).
TENSE_KEEP_2 = {
    "prompt": "In English the reported verb changes tense. Which Russian report of “I am English” keeps the same tense instead?",
    "cue": "prov-u13-tense-keep-x-2", "answer": "prov-ex-u13-tense-keep-0-ru", "cueLanguage": "rule",
    "foil": "prov-t16c-u13-tense-keep-x-past", "feature": "TENSE_KEPT", "response": "RUSSIAN_CLAUSE",
    "feedback": ("Он сказа́л, что он англича́нин keeps the same tense as the original statement “I am English”. Он сказа́л, что он"
                 " был англича́нином adds a past copula and changes the stated reading. In English, the reported verb usually"
                 " changes tense after a past reporting verb. In Russian, this report keeps the tense of the original statement."
                 " English backshift can follow a past reporting verb, but is not mandatory in every report."),
    "rationale": "Он сказа́л, что он был англича́нином adds a past copula, so it does not keep the same tense as the original statement.",
}
# u13-i-reported-1 keeps its Moscow foil: the nationality report the prep proposed comes from another frozen span (F:22) and
# fails the source-supplied relation of F:29-31; only its prompt changes (listed in the report).
PROMPTS = {"u13-i-reported-1": "Which sentence is the direct evening statement that the shown report retells?",
           # D31c sweep row adjusted: ENGLISH_CLASSIFICATION prompts name the label or English (the response-type rule).
           "u13-g-tense-keep-0": "Which English description keeps the same tense?"}
REPORTED_2 = {
    "prompt": "Which sentence is the future plan about Moscow, rather than a report of the shown statement?",
    "cue": "prov-ex-u13-reported-1-ru", "answer": "prov-u13-reported-x-1", "foil": "prov-ex-u13-reported-0-ru",
    "feature": "MOSCOW_FUTURE", "response": "RUSSIAN_CLAUSE",
    "feedback": ("Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски is the future plan about Moscow: when the speaker is"
                 " there, the speaker will speak only Russian. Both clauses refer to the future, including the clause meaning when."
                 " Она́ сказа́ла, что она́ ска́жет ему́ ве́чером instead reports the shown statement with a past reporting verb."
                 " Only the evening report has a past reporting frame."),
    "rationale": "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером reports the shown statement, not the future plan about Moscow.",
}
REDESIGN = {"u13-i-tense-keep-2": TENSE_KEEP_2, "u13-i-reported-2": REPORTED_2}
EX_REPORTED_3 = "Он сказа́л, что он был англича́нином"  # ex-u13-reported-3 without wiki emphasis (prep section 5, markup)

TYPED_FEEDBACK = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
TYPED = [  # (id, objective, cue (row, field), key (row, field), prompt)
    ("u13-i-type-chital", "obj-u13-past-aspect", ("чита'ть", "accented"), ("чита'ть", "past_m"),
     "Type the masculine singular past form of the shown verb."),
    ("u13-i-type-chitala", "obj-u13-past-aspect", ("чита'ть", "accented"), ("чита'ть", "past_f"),
     "Type the feminine singular past form of the shown verb."),
    ("u13-i-type-chitalo", "obj-u13-past-aspect", ("чита'ть", "accented"), ("чита'ть", "past_n"),
     "Type the neuter singular past form of the shown verb."),
    ("u13-i-type-chitali", "obj-u13-past-aspect", ("чита'ть", "accented"), ("чита'ть", "past_pl"),
     "Type the plural past form of the shown verb."),
    ("u13-i-type-prochital", "obj-u13-past-aspect", ("прочита'ть", "accented"), ("прочита'ть", "past_m"),
     "Type the masculine singular past form of the shown verb."),
    ("u13-i-type-prochitala", "obj-u13-past-aspect", ("прочита'ть", "accented"), ("прочита'ть", "past_f"),
     "Type the feminine singular past form of the shown verb."),
    ("u13-i-type-prochitalo", "obj-u13-past-aspect", ("прочита'ть", "accented"), ("прочита'ть", "past_n"),
     "Type the neuter singular past form of the shown verb."),
    ("u13-i-type-govorit", "obj-u13-infinitive", ("сказа'ть", "accented"), ("говори'ть", "accented"),
     "Type the imperfective partner of the shown verb."),
    ("u13-i-type-skazat", "obj-u13-infinitive", ("говори'ть", "accented"), ("сказа'ть", "accented"),
     "Type the perfective partner of the shown verb."),
    ("u13-i-type-delat", "obj-u13-infinitive", ("сде'лать", "accented"), ("де'лать", "accented"),
     "Type the imperfective partner of the shown verb."),
    ("u13-i-type-sdelat", "obj-u13-infinitive", ("де'лать", "accented"), ("сде'лать", "accented"),
     "Type the perfective partner of the shown verb."),
]

REFERENCES = [
    {"id": "ref-u13-past-aspect", "title": "Aspect in the past",
     "summary": "Imperfective and perfective past forms of the verb meaning to read.",
     "detail": ("я читал — I was reading. я прочитал — I read. The imperfective presents an event as a process, or as a repeated"
                " or habitual event; the perfective presents it as a complete whole.\n\nчита́л, чита́ла, чита́ло, чита́ли are the"
                " masculine, feminine, neuter and plural past forms of чита́ть. прочита́л, прочита́ла, прочита́ло are the masculine,"
                " feminine and neuter singular past forms of прочита́ть."),
     "searchTerms": ["past tense aspect", "imperfective past", "perfective past"],
     "steps": ["u13-st-teach-past-aspect-1", "u13-st-teach-past-aspect-5"]},
    {"id": "ref-u13-reported", "title": "Reported speech",
     "summary": "Reporting a statement: English backshift and the Russian tense of the original.",
     "detail": ("In English, the reported verb usually changes tense after a past reporting verb, but this backshift is not"
                " mandatory in every report. Russian indirect speech generally keeps the tense of the original statement.\n\n"
                "Он сказа́л, что он англича́нин reports “I am English” with no past copula. Я скажу ему ве́чером — I will tell him"
                " in the evening. Она́ сказа́ла, что она́ ска́жет ему́ ве́чером — She said that she would speak to him in the"
                " evening."),
     "searchTerms": ["reported speech", "indirect speech", "backshift"],
     "steps": ["u13-st-teach-tense-keep-1", "u13-st-teach-reported-1"]},
    {"id": "ref-u13-aspect-partners", "title": "Aspect partners",
     "summary": "Two aspect pairs, learned as pairs.",
     "detail": (VID_SENTENCE + " говори́ть is imperfective and сказа́ть is perfective. де́лать is imperfective and сде́лать is"
                " perfective. A prefix can make a perfective partner, but it can also make a verb with a new meaning, so learn"
                " each pair as a pair."),
     "searchTerms": ["aspect pair", "perfective partner", "imperfective partner"],
     "steps": ["u13-st-teach-infinitive-1"]},
]
BINDER = {"id": "binder-u13-aspect", "objectiveIds": ["obj-u13-past-aspect", "obj-u13-infinitive"],
          "prompt": ("In your binder, write this unit's past forms of the verb meaning to read, first the imperfective verb and then"
                     " its perfective partner, and label each form with its gender or number. Then write the two aspect pairs with"
                     " each verb's aspect. Compare your work with the model answer."),
          "modelAnswer": ("чита́л: masculine. чита́ла: feminine. чита́ло: neuter. чита́ли: plural. These are imperfective.\nпрочита́л:"
                          " masculine. прочита́ла: feminine. прочита́ло: neuter. These are perfective.\nговори́ть: imperfective."
                          " сказа́ть: perfective. де́лать: imperfective. сде́лать: perfective.")}
TUTOR = {"id": "tutor-u13-aspect",
         "bring": "Your binder past forms and aspect pairs, and the two evening sentences, to read aloud and say which verb is past and which is future."}

# --------------------------------------------------------------------------------------------------------------
# Facts: English-only sentences and their evidence. ("F", n) = a page line record, ("V", row, field) = an OpenRussian cell,
# ("VID", i) = a VID:9 span, else a record id.
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
    "Compare the entire supplied forms.",
    "Identify the aspect in each example; practise constructing sentences separately.",
    "Learn the supplied forms rather than inferring an unfamiliar ending or stress pattern.",
    "Compare the two labels with the examples rather than applying an always-change or never-change rule.",
    "Compare the supplied direct and reported evening examples.",
    "Interpret each example in its context; practise constructing reports separately.",
    "Compare the supplied examples.",
    "Use the saying/telling pair shown here.",
    "Learn the supplied infinitives.",
    "Keep both the aspect and the meaning of the pair in view.",
    "Learn the two verb pairs, then practise choosing aspect in sentence contexts and producing verbs separately.",
    "Identify the imperfective member of the verb pair while keeping the meaning the same.",
    "Review the nationality example you learned earlier.",
    "Use the supplied examples.",
    "Distinguish the Moscow plan from the direct evening statement.",
    "In the evening report, distinguish the past reporting verb from the future embedded action.",
    "Study these examples in context; practise creating reports separately.",
}


def p(page: str, *numbers: int) -> list:
    return [(page, n) for n in numbers]


EXTRA: dict[str, list] = {
    # past aspect
    "Both are past forms.": p("P", 4) + [("V", "чита'ть", "past_m"), ("V", "прочита'ть", "past_m")],
    "English simple past wording alone does not determine Russian aspect; identify the perfective form.": p("P", 4, 14, 15, 16, 17) + p("B", 6, 12),
    "Its process focus does not mean the activity could never have finished.": p("P", 16, 17) + p("B", 12),
    "The first presents reading as activity or process; the second presents it as a completed whole.": p("P", 4) + p("B", 12),
    "These English labels describe the examples; they do not match every English past tense to one Russian aspect.": p("P", 4, 14, 15, 16, 17),
    "Do not treat English past progressive and simple past as a fixed Russian aspect code.": p("P", 4, 14, 15, 16, 17) + p("B", 6, 12),
    "The imperfective does not always mean the action was unfinished.": p("P", 16, 17) + p("B", 12),
    S1443: p("GA", 63),
    "A completed activity can still be described with imperfective aspect when the focus is on the process; English simple past"
    " can also translate an imperfective verb.": p("P", 14, 15, 16, 17) + p("B", 12),
    "This contrast changes gender agreement, not aspect.": [("V", "чита'ть", "past_m"), ("V", "чита'ть", "past_f"), ("V", "чита'ть", "aspect")],
    "Both forms belong to the same imperfective verb; changing gender agreement does not change aspect.":
        [("V", "чита'ть", "past_m"), ("V", "чита'ть", "past_f"), ("V", "чита'ть", "aspect")],
    "The pronoun I does not determine past-tense gender for every speaker; match the past form to the speaker’s gender.": p("P", 62, 65),
    # tense keep
    "The same tense label fits this nationality report: we keep the tense the same.": p("F", 22),
    "The other label describes changing the reported tense in the English comparison.": p("F", 22),
    "we usually change the tense is the label for the English backshift comparison, not the same tense classification of the"
    " shown Russian nationality report.": p("F", 22),
    "This is the shown example, not a rule that every report must preserve every tense or word unchanged.": p("SOT", 4, 34),
    "Keeping the tense the same describes this Russian reporting comparison; it does not mean every verb in a report has the same tense.":
        p("F", 22, 30),
    "It expresses a future act of telling him in the evening.": p("F", 29),
    "In this comparison, English can shift a present statement to a past form after a past reporting verb.": p("F", 22),
    "The label we usually change the tense names that English backshift comparison.": p("F", 22),
    "Backshift is not mandatory in every English report.": p("SOT", 21, 22),
    "The contrasting Russian example does not require automatic English-style backshift of the embedded statement.": p("F", 22),
    "The reporting verb is past while the embedded verb remains future.": p("F", 30),
    "The Moscow option instead gives a future time clause and a plan to speak Russian, without a past reporting frame.": p("F", 24, 31),
    "In English, the reported verb usually changes tense after a past reporting verb.": p("F", 22) + p("SOT", 9),
    "In Russian, this report keeps the tense of the original statement.": p("F", 22) + p("SOT", 39),
    "English backshift can follow a past reporting verb, but is not mandatory in every report.": p("SOT", 9, 21),
    # infinitive
    VID_SENTENCE: [("VID", 0), ("VID", 1)] + p("B", 6),
    "Learn the two taught pairs rather than assuming that any prefix gives the corresponding perfective of any verb.": p("B", 25, 26, 27, 28, 29),
    "Learn this pair rather than assuming that adding any prefix gives the matching perfective of any verb.": p("B", 25, 26, 27, 28, 29),
    # reported
    "A past reporting frame can contain a statement whose tense is interpreted from the reported speaker’s perspective.": p("SOT", 17, 39),
    "The supplied original is “I am English”.": p("F", 22),
    "It has a past reporting verb with a present nationality statement.": p("F", 22),
    "The alternative with a past copula changes the stated reading.": p("F", 22),
    "It has a past reporting verb with a future embedded action.": p("F", 30),
    "The reporting verb said is past, and the embedded telling action keeps its future form.": p("F", 30),
    S1523: p("SOT", 39, 17),
    S1542: p("SOT", 17, 39),
    "It has no past reporting frame.": p("F", 31),
    "English backshift is not compulsory in every report; preserving these readings is not a universal rule that every tense or"
    " word stays unchanged.": p("SOT", 21, 4, 34),
    "The reporting verb is past and the nationality statement has no past copula.": p("F", 22),
    S1508: p("SOT", 21, 39),
    "A past nationality statement does not describe this report, whose nationality statement has no past copula.": p("F", 22),
    "It has a first-person future verb without a past reporting frame.": p("F", 29),
    "The shown reported example instead says that she said she would tell him.": p("F", 30),
    "Both clauses refer to the future, including the clause meaning when.": p("F", 24, 31),
    "Only the evening report has a past reporting frame.": p("F", 30, 31),
    "The reporting verb said is past; the embedded verb about telling him retains future form.": p("F", 30),
    "A future reporting verb with a past embedded action does not describe this report, whose reporting verb is past and whose"
    " embedded verb is future.": p("F", 30),
    # Cyrillic sentences that also state a relation (forms are cited automatically)
    "прочита́ть is the perfective partner of чита́ть.": [("V", "прочита'ть", "aspect"), ("V", "прочита'ть", "partner"), ("V", "чита'ть", "partner")],
    "Он сказа́л, что он был англича́нином adds a past copula and changes the stated reading.": p("F", 22),
    "The alternative with был explicitly supplies a past copula and changes the stated reading.": p("F", 22),
    "Он сказа́л, что он был англича́нином adds a past copula, so it does not keep the same tense as the original statement.": p("F", 22),
    "Он сказа́л, что он англича́нин keeps the same tense as the original statement “I am English”.": p("F", 22),
    "The Moscow option describes a different future plan, not this direct evening statement.": p("F", 31),
}


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
        if "http" in record["originalSourceText"] or ".pdf" in record["originalSourceText"]:
            raise SystemExit(f"{record['itemId']} would carry a URL")
        self.records[record["itemId"]] = record
        self.pack["provenanceRecords"].append(record)
        if record["sourceId"] != "src-content-audit-research" and not d7b.frozen_record(self.pack, record):
            raise SystemExit(f"{record['itemId']} is not a frozen-source record")
        return record["itemId"]

    def template(self, page: str) -> dict:
        tmpl = PAGES[page][1]
        if not tmpl.startswith("S1:"):
            return copy.deepcopy(self.records[tmpl])
        s1 = S1[tmpl[3:]]
        base = "prov-ex-u7-adverb-invariance-0-ru" if s1["licenseRoute"] == "ruwiki" else "prov-ex-u16-complements-0-ru"
        record = copy.deepcopy(self.records[base])
        record.update({"sourceId": s1["sourceId"], "sourceRevision": s1["oldid"], "attribution": s1["attribution"],
                       "sourceUrl": s1["url"]})
        return record

    def _record(self, page: str, raw: str, record_id: str, objective: str) -> str:
        if record_id in self.records:
            return record_id
        rel, _, prefix = PAGES[page]
        if (SRC / rel).read_text(encoding="utf-8").count(raw) != 1:
            raise SystemExit(f"{page}: span is not unique on its page ({raw[:60]!r})")
        record = self.template(page)
        record.update({"itemId": record_id, "unitNumber": UNIT, "objectiveId": objective, "sourceRecordId": prefix + raw[:80],
                       "originalSourceText": raw, "shippedText": raw, "sourceHash": sha(raw), "contentHash": sha(raw),
                       "recordType": "instruction", "transformation": "selection"})
        return self.add(record)

    def line(self, page: str, number: int, objective: str) -> str:
        if page == "VID":
            raw = VID_SPANS[number]
            if raw not in (SRC / PAGES["VID"][0]).read_text(encoding="utf-8").split("\n")[8]:
                raise SystemExit("VID span is not on line 9")
            return self._record(page, raw, f"prov-t16c-u13-vid9-{number}", objective)
        raw = (SRC / PAGES[page][0]).read_text(encoding="utf-8").split("\n")[number - 1].strip()
        return self._record(page, raw, f"prov-t16c-u13-{page.lower()}{number}", objective)

    def cell(self, accented: str, field: str, objective: str) -> str:
        hits = [r for r in VERB_ROWS if r["accented"] == accented]
        if len(hits) != 1:
            raise SystemExit(f"V: {len(hits)} rows for {accented}")
        original = hits[0][field].strip()
        if not original:
            raise SystemExit(f"V:{accented}#{field} is empty")
        for r in self.records.values():
            if r["unitNumber"] == UNIT and r["sourceRecordId"] == f"verbs.csv:{accented}#{field}" and is_frozen(self.pack, r):
                return r["itemId"]
        record = copy.deepcopy(self.records["prov-ex-or-u13-past-aspect-3"])
        shipped = stressed(original) if field in ("accented", "past_m", "past_f", "past_n", "past_pl") else original
        if field == "translations_en":
            shipped = original.split(",")[0].strip()  # the first English sense, as the Unit 13 gloss records show it
        record.update({"itemId": f"prov-t16c-u13-v-{sha(accented + field)[:8]}-{field}", "objectiveId": objective,
                       "sourceRecordId": f"verbs.csv:{accented}#{field}", "originalSourceText": original,
                       "shippedText": shipped, "sourceHash": sha(original), "contentHash": sha(shipped),
                       "recordType": "lexical record"})
        return self.add(record)


def is_frozen(pack: dict, record: dict) -> bool:
    return d7b.frozen_record(pack, record)


def admit_s1(pack: dict) -> None:
    """S1 froze Sequence of tenses 1363198135 and Вид (лингвистика) 153277825; D29a admits their revisions."""
    templates = {"wikipedia": "src-wikipedia-russian-grammar-1369617627", "ruwiki": "src-ruwiki-narechie-152437423"}
    for key, title in (("sequence", "Sequence of tenses"), ("vid", "Вид (лингвистика)")):
        page = S1[key]
        data = (SRC / "s1-intake" / page["file"]).read_bytes()
        if hashlib.sha256(data).hexdigest() != page["sha256"]:
            raise SystemExit(f"{key} does not match its FREEZE.json entry")
        if any(s["id"] == page["sourceId"] for s in pack["sources"]):
            raise SystemExit(f"{key} is already a pack source")
        template = next(s for s in pack["sources"] if s["id"] == templates[page["licenseRoute"]])
        pack["sources"].append({**copy.deepcopy(template), "id": page["sourceId"], "title": title, "attribution": page["attribution"]})
        pack["rights"].append({"id": "rights-" + page["sourceId"][4:], "sourceId": page["sourceId"], "license": "CC BY-SA 4.0",
                               "permitsAppUse": True})


def apply(pack: dict) -> tuple[dict, list]:
    baseline = copy.deepcopy(pack)  # before the sweep, so sweep wording is covered too
    sweep_log: list = []
    sweep.apply_sweep(pack, [], sweep_log, units={UNIT})
    admit_s1(pack)
    b = Builder(pack)
    unit = b.unit
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    R = b.records

    # --- teaching -------------------------------------------------------------------------------------------------
    for step_id, pairs in TEACH.items():
        for old, new in pairs:
            steps[step_id]["body"] = new if old is None else edit(steps[step_id]["body"], old, new, step_id)
    # --- the five forms not yet taught in Unit 13: worked examples and a teaching card (prep section 2) -----------
    template_ex = next(e for e in pack["examples"] if e["id"] == "ex-or-u13-past-aspect-3")
    new_ids = []
    for ex_id, accented, field in NEW_EXAMPLES:
        form_id = b.cell(accented, field, "obj-u13-past-aspect")
        gloss_id = b.cell(accented, "translations_en", "obj-u13-past-aspect")
        display = R[form_id]["shippedText"]
        for suffix, source in (("", form_id), ("-gloss", gloss_id)):
            record = copy.deepcopy(R[source])
            record.update({"itemId": f"prov-{ex_id}{suffix}"})
            b.add(record)
        pack["examples"].append({**copy.deepcopy(template_ex), "id": ex_id, "display": display, "note": R[gloss_id]["shippedText"]})
        new_ids.append(ex_id)
    session = next(se for se in unit["sessions"] if se["id"] == "u13-s-past-aspect")
    anchor = next(i for i, s in enumerate(session["steps"]) if s["id"] == NEW_STEP["after"])
    new_step = {**copy.deepcopy(steps[NEW_STEP["after"]]), "id": NEW_STEP["id"], "title": NEW_STEP["title"], "body": NEW_STEP["body"],
                "exampleIds": new_ids}
    session["steps"].insert(anchor + 1, new_step)
    objectives["obj-u13-past-aspect"]["instructionStepIds"].append(NEW_STEP["id"])
    objectives["obj-u13-past-aspect"]["exampleIds"] += new_ids
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    # --- remediation repeats each session's whole teaching ---------------------------------------------------------
    for se in unit["sessions"]:
        rem = [s for s in se["steps"] if s["kind"] == "REMEDIATE"]
        if rem:
            rem[0]["body"] = "\n\n".join(s["body"] for s in se["steps"] if s["kind"] == "TEACH")
            rem[0]["exampleIds"] = list(dict.fromkeys(x for s in se["steps"] if s["kind"] == "TEACH" for x in s.get("exampleIds") or []))
    # --- ex-u13-reported-3 without wiki emphasis --------------------------------------------------------------------
    ex3 = next(e for e in pack["examples"] if e["id"] == "ex-u13-reported-3")
    ex3["display"] = EX_REPORTED_3
    record = R["prov-ex-u13-reported-3-ru"]
    if R["prov-u13-report-transfer-native-past-nationality"]["shippedText"] != EX_REPORTED_3:
        raise SystemExit("The unfolded nationality alternative is not a frozen Unit 13 record")
    alt = copy.deepcopy(R["prov-u13-report-transfer-native-past-nationality"])
    for key in ("originalSourceText", "sourceRecordId", "sourceHash"):
        record[key] = alt[key]
    record.update({"shippedText": EX_REPORTED_3, "contentHash": sha(EX_REPORTED_3)})
    clause = EX_REPORTED_3.split(", ", 1)[1]  # the subordinate clause is its own Russian region after the comma
    alt.update({"itemId": "prov-t16c-u13-past-nationality-clause", "shippedText": clause, "contentHash": sha(clause)})
    b.add(alt)
    # --- items ----------------------------------------------------------------------------------------------------
    for item_id, old, new in FEEDBACK:
        item = items[item_id]
        item["feedback"] = edit(item["feedback"], old, new, item_id)
        item["assessment"]["whyAnswerDemonstratesObjective"] = edit(item["assessment"]["whyAnswerDemonstratesObjective"], old, new, item_id)
    for item_id, index, new in RATIONALES:
        item = items[item_id]
        item["distractorRationales"][index] = new
        item["assessment"]["distractorDifference"][index] = new
    for item_id, prompt in PROMPTS.items():
        items[item_id]["prompt"] = prompt
    # The rejected F:22 alternative as a selection of the same frozen span as the kept report (u13-i-tense-keep-2's foil).
    foil = copy.deepcopy(R["prov-ex-u13-tense-keep-0-ru"])
    foil.update({"itemId": "prov-t16c-u13-tense-keep-x-past", "shippedText": EX_REPORTED_3, "contentHash": sha(EX_REPORTED_3)})
    b.add(foil)
    for item_id, spec in REDESIGN.items():
        item, a = items[item_id], items[item_id]["assessment"]
        answer, foil = R[spec["answer"]]["shippedText"], R[spec["foil"]]["shippedText"]
        item.update({"prompt": spec["prompt"], "promptLetter": R[spec["cue"]]["shippedText"], "options": [answer, foil],
                     "answer": answer, "feedback": spec["feedback"], "distractorRationales": ["", spec["rationale"]]})
        a.update({"testedFeature": spec["feature"], "cueLanguage": spec.get("cueLanguage", "ru"), "answerSourceIds": [spec["answer"]],
                  "distractorSourceIds": ["", spec["foil"]], "distractorDifference": ["", spec["rationale"]],
                  "whyAnswerDemonstratesObjective": spec["feedback"], "contrastMembers": [answer, foil],
                  "relationParticipantIds": [spec["cue"], spec["answer"], spec["foil"]], "responseKind": spec["response"]})
    typed = []
    for item_id, objective, cue, key, prompt in TYPED:
        cue_id, key_id = b.cell(cue[0], cue[1], objective), b.cell(key[0], key[1], objective)
        key_text = R[key_id]["shippedText"]
        if stressed(next(r for r in VERB_ROWS if r["accented"] == key[0])[key[1]]) != key_text:
            raise SystemExit(f"{item_id}: key is not the exact frozen stress")
        typed.append(typed_item(item_id, objective, prompt, R[cue_id]["shippedText"], key_text, TYPED_FEEDBACK, "src-openrussian-50e210c4"))
    for item in typed:
        objectives[item["objectiveId"]]["checkItemIds"].append(item["id"])
    pack["items"].extend(typed)
    items.update({i["id"]: i for i in typed})
    # --- unit fields; checks move to an end-of-unit session ---------------------------------------------------------
    unit["topicSummary"], unit["mappingNote"] = TOPIC, MAPPING
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
    book = next(i for i, s in enumerate(unit["sessions"]) if s["id"] == "u13-s-book")
    unit["sessions"].insert(book, {"id": "u13-s-checks", "title": "Unit 13 checks", "estimatedMinutes": 25,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    # --- references, binder and tutor -------------------------------------------------------------------------------
    template = next(r for r in pack["references"] if r["id"] == "ref-u10-possession")
    for spec in REFERENCES:
        reference = {k: template[k] for k in ("sourceId", "reviewStatus", "classification")}
        reference.update({k: spec[k] for k in ("id", "title", "summary", "detail", "searchTerms")})
        reference["introducedInUnit"] = UNIT
        pack["references"].append({k: reference[k] for k in template if k in reference})
        for sid in spec["steps"]:
            steps[sid]["referenceIds"] = list(dict.fromkeys((steps[sid].get("referenceIds") or []) + [spec["id"]]))
    if unit.get("binderTask") or unit.get("tutorTask"):
        raise SystemExit("Unit 13 already has a binder or tutor task")
    unit["binderTask"], unit["tutorTask"] = copy.deepcopy(BINDER), copy.deepcopy(TUTOR)
    # --- every Unit 13 learner field: Russian only through frozen Unit 13 records ------------------------------------
    frozen = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT and is_frozen(pack, r)}
    texts = [s["body"] for s in steps.values()] + [unit["topicSummary"], unit["mappingNote"], BINDER["prompt"], BINDER["modelAnswer"]]
    texts += [r[k] for r in pack["references"] if r["id"] in {s["id"] for s in REFERENCES} for k in ("title", "summary", "detail")]
    texts += [e["display"] for e in pack["examples"] if e.get("objectiveId", "").startswith(f"obj-u{UNIT}-")]
    for item in items.values():
        if item["objectiveId"].startswith(f"obj-u{UNIT}-"):
            texts += [item["prompt"], item.get("promptLetter") or "", item["answer"], item["feedback"]] + item["options"] + item.get("distractorRationales", [])
    for text in texts:
        for m in re.finditer("[Ѐ-ӿ́](?:[Ѐ-ӿ́]|[ \t]+(?=[Ѐ-ӿ]))*[?!.,]*", text):
            region = m.group(0).strip()
            if region not in frozen and region.rstrip("?!.,") not in frozen:
                raise SystemExit(f"Unit 13 region without a frozen record: {region!r} in {text[:80]!r}")
    report = mark_facts(b, steps, items)
    cover_english(pack, baseline)
    return report, sweep_log


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
                raise SystemExit(f"No frozen Unit 13 record shows {form!r} ({sentence})")
            out.append(ranked[0]["itemId"])
        return out

    def resolve(ref, objective: str) -> str:
        if isinstance(ref, tuple) and len(ref) == 2:
            return b.line(ref[0], ref[1], objective)
        if isinstance(ref, tuple):
            return b.cell(ref[1], ref[2], objective)
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
        out[(i["id"], "prompt")] = (n, o, i["prompt"], [])
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
        if r["id"] in {s["id"] for s in REFERENCES}:
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
        item_id = f"prov-ca-t16c-{key}"
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
        raise SystemExit("Asset is not the T16c baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    report, sweep_log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(json.dumps({"facts": report, "sweep": sweep_log}, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{len(sweep_log)} Unit 13 sweep rows applied; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
