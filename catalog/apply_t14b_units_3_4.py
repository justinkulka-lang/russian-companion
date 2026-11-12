#!/usr/bin/env python3
"""T14-b: Units 3-4 (A2-23, A2-24, A2-26, A2-32, A2-33, A2-34).

Exact-baseline transformer; it refuses any input other than the recorded baseline asset.

- A2-26: governance terms (admitted, frozen, review pending) leave Units 3-4 learner text. Quoted
  source spans are unchanged; only the English around them is reworded.
- A2-24 (owner's narrowed reading, T14-n1): every independent check moves out of its teaching
  session into an end-of-unit check session, so no check follows the teaching step that states it.
  Gender stays a per-word dictionary fact (Unit 3 teaches that an ending is not a substitute), so
  the added gender checks are words the unit now teaches, recalled later, not unseen words.
- A2-23/A2-34: Unit 3 gains two taught nouns (учитель, мама) checked for their group and six typed
  checks (type a taught word from its meaning); Unit 4 gains six typed checks that produce present
  forms of four infinitives the unit lists but does not conjugate (знать, работать, понимать,
  слушать). Each unit reaches 20 checked items, 6 typed (30 percent).
- A2-32/A2-33: binder and tutor tasks for both units.
All Russian comes from the frozen OpenRussian snapshot already in the source inventory
(catalog/sources/units-3-5, commit 50e210c4, CC BY-SA 4.0); keys cite their rows in provenance
records. Keys are signed "prepared by Claude Code (implementer); final audit pending" (owner decision).
No Russian is generated. English is independently authored.
"""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from apply_t14a_units_1_2 import FREEZE, OR_SOURCE, PENDING, provenance, sha, step  # noqa: E402
from apply_t14b_units_1_2_owner import typed_item  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "aa517a74e1d9613c37463a9280c9b2078ae80a34f4f648fb2bb8ed65725c2572"
STRESS = "́"

# --- A2-26 rewording: exact phrase replacements, longest phrases first ---------------------------
REWORD = [
    ("Proposed Unit 3 uses frozen dictionary rows and grammar excerpts for the groups, a small phrase set, and naming items. "
     "The owned book remains separate work. Qualified review is pending.",
     "Unit 3 uses dictionary entries and grammar excerpts for the three groups, a small phrase set, and naming phrases. "
     "Your own book remains separate work."),
    ("Proposed Unit 4 uses frozen rows for the person words, the present forms, the written mark, and the dictionary-form "
     "examples. The written mark is not a listening score. The owned book remains separate work. Qualified review is pending.",
     "Unit 4 uses dictionary entries for the person words, the present forms, the written stress mark, and the "
     "dictionary-form examples. The written mark is not a listening score. Your own book remains separate work."),
    ("Choose the admitted record. The feedback states the frozen field.",
     "Choose the answer the dictionary gives. The feedback shows the dictionary entry."),
    ("These checks use only admitted records. A miss returns to the explanation.",
     "These checks use only words taught in this unit. A miss returns to the explanation."),
    ("Compare the shown form with the admitted record. Do not invent a form that is absent from the record.",
     "Compare the shown form with the dictionary entry. Do not invent a form that the entry does not give."),
    ("Learn this recorded label; an ending is not a substitute for the record.",
     "Learn the group with each word; an ending is not a substitute for the dictionary entry."),
    ("Learn this recorded label; do not guess the gender of another word from its ending.",
     "Learn the group with each word; do not guess the group of another word from its ending."),
    ("An ending is not a substitute for the admitted record.", "An ending is not a substitute for the dictionary entry."),
    ("Which admitted present form is recorded for the row glossed ", "Which present form does the dictionary give for the form glossed "),
    ("Which admitted gloss is recorded for the shown personal word?", "What does the shown person word mean?"),
    ("Which admitted label is recorded for the shown word?", "Which group does the dictionary give for the shown word?"),
    ("Which admitted meaning is recorded for the shown item?", "What does the shown phrase mean?"),
    ("Which admitted gloss is recorded for the shown item?", "What does the shown word mean?"),
    ("Which admitted example is the shown sentence?", "Which example is the shown sentence?"),
    ("Which vowel carries the admitted written stress mark?", "Which vowel carries the written stress mark?"),
    ("Compare that subject with the admitted record before the independent check.",
     "Compare that subject with the example before the independent check."),
    ("The frozen accented field places the written mark on vowel ", "The dictionary places the written mark on vowel "),
    ("The frozen dictionary row records this gloss: ", "The dictionary gives this meaning: "),
    ("The frozen page records this meaning: ", "The grammar page gives this meaning: "),
    ("The frozen page records this example as: ", "The grammar page gives this example as: "),
    ("The admitted record lists this as ", "The dictionary lists this word as "),
    ("These admitted records are listed as ", "The dictionary lists these words as "),
    ("The admitted record for ", "The dictionary entry for "),
    ("The admitted dictionary rows supply this pair. ", "The dictionary gives this pair. "),
    ("The admitted dictionary rows supply this group. ", "The dictionary gives this group. "),
    ("These fields are copied from the admitted чита́ть row. ", "These forms come from the dictionary entry for чита́ть. "),
    ("The admitted row records де́лаю for the row glossed I.", "The dictionary gives де́лаю for the form glossed I."),
    ("These infinitives are copied from admitted rows. ", "These infinitives come from the dictionary. "),
    ("These greetings are copied from admitted rows. ", "These greetings come from the dictionary. "),
    ("These short answers are copied from admitted rows. ", "These short answers come from the dictionary. "),
    ("These written questions are exact frozen excerpts. ", "These written questions are quoted exactly from the grammar page. "),
    ("These two items are exact frozen excerpts. ", "These two phrases are quoted exactly from the grammar page. "),
    ("These two questions are exact frozen excerpts. ", "These two questions are quoted exactly from the grammar page. "),
    ("The mark in the admitted form is written. ", "The mark is written in these forms. "),
    ("Written mark from the admitted row", "Written stress mark from the dictionary"),
    ("Read the admitted grouping sentence", "Read the grouping sentence"),
    ("Review the admitted noun genders", "Review the three groups"),
    ("Two written questions from the frozen page", "Two written questions from the grammar page"),
    ("Two naming items from the frozen page", "Two naming phrases from the grammar page"),
    ("Two naming questions from the frozen page", "Two naming questions from the grammar page"),
    ("How the frozen page forms the present", "How the grammar page forms the present"),
    ("Admitted fields from one row", "Forms from one dictionary entry"),
    ("One admitted row for a second infinitive", "One form of a second infinitive"),
    ("Four more admitted infinitives", "Four more infinitives"),
    ("Four further admitted infinitives", "Four further infinitives"),
    ("Two further admitted infinitives", "Two further infinitives"),
    ("The admitted written mark", "The written stress mark"),
    ("The frozen row records ", "The dictionary gives "),
    ("The frozen page says: ", "The grammar page says: "),
    (" for the row glossed ", " for the form glossed "),
    ("Row glossed ", "Form glossed "),
    ("Recognize admitted noun genders", "Recognize noun genders"),
    ("Recognize admitted everyday phrases", "Recognize everyday phrases"),
    ("Recognize admitted naming items", "Recognize naming phrases"),
    ("Recognize admitted personal pronouns", "Recognize personal pronouns"),
    ("Recognize admitted present-tense forms", "Recognize present-tense forms"),
    ("Notice the admitted written stress mark", "Notice the written stress mark"),
    ("Recognize the admitted nominative role", "Recognize the nominative role"),
    ("It contains admitted records for ", "It contains dictionary entries for "),
    ("It contains admitted pronoun, present-tense, written-stress, and nominative records",
     "It contains dictionary entries for pronouns, present-tense forms, written stress, and the nominative"),
    ("''imperfective aspect''", "imperfective aspect"),
    ("data is frozen at commit", "data is fixed at commit"),
    ("excerpts are frozen by oldid", "excerpts are fixed by oldid"),
]
GOVERNANCE = re.compile(r"\b(?:admitted|frozen|retained|review[- _]pending)\b", re.I)
GRAMMAR_TERM = re.compile(r"(?i)\b(genders?|cases?|tenses?|verbs?|nouns?|pronouns?|masculine|feminine|neuter|nominative|"
                          r"prepositional|indeclinable|declinable)\b")
LINGUISTIC_CLAIM = re.compile(r"(?i)(\brussian\b.*\b(has|have|is|are)\b|\b(has|have)\b.*\b(gender|genders|cases|tense)\b|"
                              r"\bten genders\b|\b\d+\s+genders\b)")
CYRILLIC = re.compile(r"[Ѐ-ӿ́]")


def reword(text: str) -> str:
    for old, new in REWORD:
        text = text.replace(old, new)
    return text


# --- Frozen rows -------------------------------------------------------------------------------
def frozen() -> dict[str, dict]:
    rows = {}
    for r in json.loads(FREEZE.read_text(encoding="utf-8"))["openrussian"]["rows"]:
        if sha(r["line"]) != r["sha256"]:
            raise SystemExit(f"Frozen row {r['recordId']} does not match its hash")
        rows[r["bare"]] = r
    return rows


NOUN_COLS = ["bare", "accented", "translations_en", "translations_de", "gender"]
VERB_COLS = ["bare", "accented", "translations_en", "translations_de", "aspect", "partner", "imperative_sg",
             "imperative_pl", "past_m", "past_f", "past_n", "past_pl", "presfut_sg1", "presfut_sg2", "presfut_sg3",
             "presfut_pl1", "presfut_pl2", "presfut_pl3"]
PERSON = {"я": "presfut_sg1", "ты": "presfut_sg2", "он": "presfut_sg3", "мы": "presfut_pl1", "вы": "presfut_pl2", "они": "presfut_pl3"}
# Person words as Unit 4 shows them (its shipped dictionary forms carry the stress mark on они́).
PERSON_SHOWN = {"я": "я", "ты": "ты", "он": "он", "мы": "мы", "вы": "вы", "они": "они́"}


def field(row: dict, cols: list[str], name: str) -> str:
    return row["line"].split("\t")[cols.index(name)]


def shown(accented: str) -> str:
    return accented.replace("'", STRESS)


def rec(unit: int, objective: str, rec_id: str, row: dict, name: str, original: str, shipped: str, how: str) -> dict:
    out = provenance(unit, objective, rec_id, f"{row['recordId']}#{name}", original, shipped, how)
    out["sourceUrl"] = out["sourceUrl"].replace("nouns.csv", row["file"])
    return out


# --- Span coverage (mirrors V2Validator.unboundLetters) ------------------------------------------
def coverage(host: str, spans: list[str]) -> list[bool]:
    covered = [False] * len(host)
    for span in sorted({s for s in spans if s}, key=len, reverse=True):
        start = 0
        while start <= len(host) - len(span):
            i = host.find(span, start)
            if i < 0:
                break
            end = i + len(span)
            if not any(covered[i:end]) and boundary(host, i, end):
                for k in range(i, end):
                    covered[k] = True
            start = i + 1
    return covered


def boundary(host: str, i: int, end: int) -> bool:
    span = host[i:end]
    if any(ord(c) > 127 for c in span):
        return True
    before = " " if i == 0 else host[i - 1]
    after = " " if end == len(host) else host[end]
    return (not before.isalpha() or not span[0].isalpha()) and (not after.isalpha() or not span[-1].isalpha())


def gaps(host: str, covered: list[bool]) -> list[str]:
    out, cur = [], ""
    for ch, c in zip(host, covered):
        if c:
            if cur:
                out.append(cur)
            cur = ""
        else:
            cur += ch
    if cur:
        out.append(cur)
    return [g for g in out if any(ch.isalpha() for ch in g)]


def hosts(pack: dict, unit: dict) -> list[str]:
    examples = {e["id"]: e for e in pack["examples"]}
    items = {i["id"]: i for i in pack["items"]}
    out: list[str] = []

    def item(i: str) -> None:
        x = items.get(i)
        if x:
            out.extend([x["prompt"], x.get("promptLetter") or "", x["answer"], x["feedback"]] + x["options"] +
                       (x.get("typedAnswerKey") or {}).get("acceptedAnswers", []) + x.get("distractorRationales", []))
    for se in unit["sessions"]:
        for st in se["steps"]:
            out.extend([st["title"], st["body"], st.get("bookDirection") or "", st.get("companionDirection") or ""])
            for e in st.get("exampleIds", []):
                out.extend([examples[e]["display"], examples[e]["note"]])
            for i in st.get("itemIds", []):
                item(i)
    for o in unit["objectives"]:
        for e in o["exampleIds"]:
            out.extend([examples[e]["display"], examples[e]["note"]])
        for i in o["checkItemIds"]:
            item(i)
    return [h for h in out if h]


def gender_item(item_id: str, word: str, label: str, options: list[str]) -> dict:
    return {"id": item_id, "kind": "SOURCE_GENDER", "language": "en",
            "prompt": "Which group does the dictionary give for the shown word?", "promptLetter": word,
            "options": options, "answer": label, "feedback": f"The dictionary lists this word as {label}.",
            "objectiveId": "obj-u3-gender", "sourceId": OR_SOURCE, "reviewStatus": PENDING, "containsGeneratedRussian": False}


def apply(pack: dict) -> dict:
    units = {u["number"]: u for u in pack["units"]}
    rows = frozen()
    examples = {e["id"]: e for e in pack["examples"]}
    items = {i["id"]: i for i in pack["items"]}
    records: list[dict] = []

    # --- A2-26 rewording over every Units 3-4 learner string -------------------------------------
    unit_items: set[str] = set()
    unit_examples: set[str] = set()
    for n in (3, 4):
        u = units[n]
        u["topicSummary"] = reword(u["topicSummary"])
        for o in u["objectives"]:
            o["title"] = reword(o["title"])
            unit_items.update(o["checkItemIds"])
            unit_examples.update(o["exampleIds"])
        for se in u["sessions"]:
            se["title"] = reword(se["title"])
            for st in se["steps"]:
                for f in ("title", "body", "bookDirection", "companionDirection"):
                    if st.get(f):
                        st[f] = reword(st[f])
                unit_items.update(st.get("itemIds", []))
                unit_examples.update(st.get("exampleIds", []))
    for i in unit_items:
        x = items[i]
        x["prompt"], x["feedback"] = reword(x["prompt"]), reword(x["feedback"])
        if x.get("distractorRationales"):
            x["distractorRationales"] = [reword(r) for r in x["distractorRationales"]]
    for e in unit_examples:
        examples[e]["note"] = reword(examples[e]["note"])
    for r in pack["references"]:
        if r["introducedInUnit"] in (3, 4):
            for f in ("title", "summary", "detail"):
                r[f] = reword(r[f])

    # A2-26: raw wiki emphasis markup leaves the quoted Unit 4 grammar sentence.
    rule = next(r for r in pack["provenanceRecords"] if r["itemId"] == "prov-u4-present-rule")
    if "''imperfective aspect''" not in rule["shippedText"]:
        raise SystemExit("prov-u4-present-rule baseline changed")
    rule["shippedText"] = rule["shippedText"].replace("''imperfective aspect''", "imperfective aspect")
    rule["contentHash"] = sha(rule["shippedText"])
    rule["transformation"] = "formatting"

    # --- Unit 3: two more taught nouns, gender recall checks, typed recall -----------------------
    u3 = units[3]
    steps3 = {s["id"]: s for se in u3["sessions"] for s in se["steps"]}
    obj3 = {o["id"]: o for o in u3["objectives"]}
    added_examples = []
    # Words with a bundled clip (стол, брат, дом, хлеб, друг, чай) are not given new uses, so the Block 1
    # listen-check candidates and the owner's sheet stay unchanged.
    for bare, sense, label, teach_step in (("учитель", "teacher", "masculine", "u3-st-teach-gender-2"),
                                          ("мама", "mom", "feminine", "u3-st-teach-gender-3")):
        row = rows[bare]
        accented = field(row, NOUN_COLS, "accented")
        word = shown(accented)
        if field(row, NOUN_COLS, "gender") != label[0] or sense not in [s.strip() for s in field(row, NOUN_COLS, "translations_en").split(",")]:
            raise SystemExit(f"{bare}: frozen row does not give {label}/{sense}")
        records += [
            rec(3, "obj-u3-gender", f"prov-u3-noun-{bare}", row, "accented", accented, word,
                "formatting" if accented != word else "selection"),
            rec(3, "obj-u3-gender", f"prov-u3-gloss-{bare}", row, "translations_en", field(row, NOUN_COLS, "translations_en"),
                sense, "sense selection"),
            rec(3, "obj-u3-gender", f"prov-u3-gender-field-{bare}", row, "gender", label[0], label, "gender field label expansion"),
        ]
        ex_id = f"ex-u3-{bare}"
        added_examples.append({"id": ex_id, "kind": "LEXICAL", "letterId": "", "display": word, "note": sense,
                               "sourceId": OR_SOURCE, "reviewStatus": PENDING, "objectiveId": "obj-u3-gender"})
        st = steps3[teach_step]
        marker = " The dictionary entry for "
        if marker not in st["body"]:
            raise SystemExit(f"{teach_step} baseline changed")
        st["body"] = st["body"].replace(marker, f" {word} — {sense}." + marker, 1)
        st["exampleIds"].append(ex_id)
        obj3["obj-u3-gender"]["exampleIds"].append(ex_id)
    # The remediation step repeats the teaching bodies and shows their examples in teaching order.
    teaching3 = [steps3[s] for s in ("u3-st-teach-gender-1", "u3-st-teach-gender-2", "u3-st-teach-gender-3",
                                     "u3-st-teach-gender-4", "u3-st-teach-gender-5")]
    steps3["u3-st-rem-gender"]["exampleIds"] = list(dict.fromkeys(e for st in teaching3 for e in st.get("exampleIds", [])))
    steps3["u3-st-rem-gender"]["body"] = "\n\n".join(steps3[s]["body"] for s in (
        "u3-st-teach-gender-1", "u3-st-teach-gender-2", "u3-st-teach-gender-3", "u3-st-teach-gender-4", "u3-st-teach-gender-5"))
    new_gender = [gender_item("u3-i-gender-учитель", "учи́тель", "masculine", ["feminine", "masculine", "neuter"]),
                  gender_item("u3-i-gender-мама", "ма́ма", "feminine", ["masculine", "feminine", "neuter"])]
    obj3["obj-u3-gender"]["checkItemIds"] += [i["id"] for i in new_gender]

    typed3 = []
    for bare, sense, word in (("вода", "water", "вода́"), ("работа", "work", "рабо́та"), ("книга", "book", "кни́га"),
                              ("школа", "school", "шко́ла"), ("окно", "window", "окно́"), ("молоко", "milk", "молоко́")):
        typed3.append(typed_item(f"u3-i-type-{bare}", "obj-u3-words", "Type the word from this unit that has this meaning.",
                                 sense, word, f"{word} means {sense}. Stress marks are optional when you type.", OR_SOURCE))
        records.append(rec(3, "obj-u3-words", f"prov-u3-key-{bare}", rows[bare], "accented",
                           field(rows[bare], NOUN_COLS, "accented"), word,
                           "formatting" if field(rows[bare], NOUN_COLS, "accented") != word else "selection"))
    guided3 = typed_item("u3-g-type-письмо", "obj-u3-words",
                         "Practice: type the word from this unit that has this meaning.", "letter", "письмо́",
                         "письмо́ means letter. Stress marks are optional when you type.", OR_SOURCE)
    records.append(rec(3, "obj-u3-words", "prov-u3-key-письмо", rows["письмо"], "accented",
                       field(rows["письмо"], NOUN_COLS, "accented"), "письмо́", "formatting"))
    teach_typing = step("u3-st-teach-typing", "TEACH", "Type taught words",
                        "Type each word from its meaning on a Russian keyboard. The words come from the three groups "
                        "earlier in this unit. Stress marks are optional; the check ignores them.")
    guided_typing = step("u3-st-guided-typing", "GUIDED", "Guided typing",
                         "Type one word from its meaning. This is practice, not the independent check.",
                         dominantAction="Check", itemIds=[guided3["id"]], remediationStepId="u3-st-rem-words")
    type_check3 = step("u3-st-type-words", "CHECK", "Type taught words",
                       "Type each word from its meaning. A miss returns to the explanation.",
                       itemIds=[i["id"] for i in typed3], remediationStepId="u3-st-rem-words")
    rem_words = step("u3-st-rem-words", "REMEDIATE", "Review the words and their meanings",
                     "Return to the words and their meanings from the three groups. Read each word, then type it again.",
                     exampleIds=["ex-u3-вода", "ex-u3-работа", "ex-u3-книга", "ex-u3-школа", "ex-u3-окно",
                                 "ex-u3-молоко", "ex-u3-письмо"])
    u3["objectives"].insert(next(i for i, o in enumerate(u3["objectives"]) if o["id"] == "obj-u3-book"), {
        "id": "obj-u3-words", "unitNumber": 3, "title": "Type taught words from their meanings", "kind": "RECOGNITION",
        "instructionStepIds": ["u3-st-teach-typing"], "exampleIds": ["ex-u3-книга", "ex-u3-вода"],
        "practiceStepIds": ["u3-st-guided-typing"], "checkItemIds": [i["id"] for i in typed3],
        "remediationStepIds": ["u3-st-rem-words"],
    })

    def end_session(u: dict, sid: str, title: str, check_ids: list[str], before: str, extra_first: list[dict],
                    extra_last: list[dict], objectives: list[str], minutes: int) -> None:
        moved = []
        for se in u["sessions"]:
            keep = []
            for st in se["steps"]:
                (moved if st["id"] in check_ids else keep).append(st)
            se["steps"] = keep
        if [s["id"] for s in moved] != check_ids:
            moved.sort(key=lambda s: check_ids.index(s["id"]))
        u["sessions"].insert(next(i for i, se in enumerate(u["sessions"]) if se["id"] == before), {
            "id": sid, "title": title, "estimatedMinutes": minutes, "objectiveIds": objectives,
            "steps": extra_first + moved + extra_last})

    steps3["u3-st-check-gender"]["itemIds"] += [i["id"] for i in new_gender]
    end_session(u3, "u3-s-check", "Unit 3 checks", ["u3-st-check-gender", "u3-st-check-phrases", "u3-st-check-naming"],
                "u3-s-book", [teach_typing, guided_typing, rem_words], [type_check3],
                ["obj-u3-gender", "obj-u3-phrases", "obj-u3-naming", "obj-u3-words"], 14)
    u3["estimatedMinutes"] += 14

    # --- Unit 4: typed present forms of infinitives the unit lists but does not conjugate ---------
    u4 = units[4]
    obj4 = {o["id"]: o for o in u4["objectives"]}
    model = {p: shown(field(rows["читать"], VERB_COLS, PERSON[p])) for p in PERSON}
    typed4 = []
    for bare, person in (("знать", "я"), ("знать", "они"), ("работать", "мы"), ("понимать", "ты"),
                         ("слушать", "он"), ("слушать", "вы")):
        row = rows[bare]
        form_src = field(row, VERB_COLS, PERSON[person])
        form = shown(form_src)
        cue = shown(field(row, VERB_COLS, "accented"))
        typed4.append(typed_item(f"u4-i-type-{bare}-{person}", "obj-u4-present",
                                 f"Type the present form of the shown word that goes with {PERSON_SHOWN[person]}.", cue, form,
                                 f"{form} goes with {PERSON_SHOWN[person]}, like {model[person]} from чита́ть. Stress marks are optional when you type.",
                                 OR_SOURCE))
        records.append(rec(4, "obj-u4-present-typing", f"prov-u4-key-{bare}-{person}", row, PERSON[person], form_src, form, "formatting"))
    drow = rows["делать"]
    dform_src = field(drow, VERB_COLS, "presfut_pl1")
    dform = shown(dform_src)
    guided4 = typed_item("u4-g-type-делать-мы", "obj-u4-present",
                         "Practice: type the present form of the shown word that goes with мы.",
                         shown(field(drow, VERB_COLS, "accented")), dform,
                         f"{dform} goes with мы, like {model['мы']} from чита́ть. Stress marks are optional when you type.", OR_SOURCE)
    records.append(rec(4, "obj-u4-present-typing", "prov-u4-key-делать-мы", drow, "presfut_pl1", dform_src, dform, "formatting"))
    guided_type4 = step("u4-st-guided-type-present", "GUIDED", "Guided typing: a form for мы",
                        "Type the form that goes with мы, following the чита́ть forms. This is practice, not the independent check.",
                        dominantAction="Check", itemIds=[guided4["id"]], remediationStepId="u4-st-rem-type-present")
    present = next(se for se in u4["sessions"] if se["id"] == "u4-s-present")
    rem_index = next(i for i, st in enumerate(present["steps"]) if st["id"] == "u4-st-rem-present")
    present["steps"].insert(rem_index, guided_type4)
    # The typed forms are transfer from the чита́ть pattern, not taught targets, so they form their own objective;
    # obj-u4-present (CC015: every target taught with its exact form, then guided) keeps its four checks.
    steps4 = {s["id"]: s for se in u4["sessions"] for s in se["steps"]}
    rem_type4 = step("u4-st-rem-type-present", "REMEDIATE", "Review the чита́ть forms",
                     "\n\n".join(steps4[s]["body"] for s in ("u4-st-teach-present-2", "u4-st-teach-present-3")),
                     exampleIds=["ex-u4-verb-читать"])
    present["steps"].insert(next(i for i, st in enumerate(present["steps"]) if st["id"] == "u4-st-rem-present"), rem_type4)
    present["objectiveIds"].append("obj-u4-present-typing")
    u4["objectives"].insert(next(i for i, o in enumerate(u4["objectives"]) if o["id"] == "obj-u4-present") + 1, {
        "id": "obj-u4-present-typing", "unitNumber": 4, "title": "Type present forms by following the чита́ть forms",
        "kind": "RECOGNITION", "instructionStepIds": ["u4-st-teach-present-2", "u4-st-teach-present-3"],
        "exampleIds": ["ex-u4-verb-читать", "ex-u4-verb-делать"], "practiceStepIds": ["u4-st-guided-type-present"],
        "checkItemIds": [i["id"] for i in typed4], "remediationStepIds": ["u4-st-rem-type-present"],
    })
    for item in typed4 + [guided4]:
        item["objectiveId"] = "obj-u4-present-typing"
    type_check4 = step("u4-st-type-present", "CHECK", "Type present forms",
                       "Type the form of each shown word that goes with the person word. A miss returns to the explanation.",
                       itemIds=[i["id"] for i in typed4], remediationStepId="u4-st-rem-type-present")
    end_session(u4, "u4-s-check", "Unit 4 checks",
                ["u4-st-check-pronouns", "u4-st-check-present", "u4-st-check-stress", "u4-st-check-nominative"],
                "u4-s-book", [], [type_check4],
                ["obj-u4-pronouns", "obj-u4-present", "obj-u4-present-typing", "obj-u4-stress", "obj-u4-nominative"], 12)
    u4["estimatedMinutes"] += 12

    # --- Linked teaching and remediation show what each check asks for (design report transfer/gap review) ----
    u2 = units[2]
    obj2 = {o["id"]: o for o in u2["objectives"]}
    steps2 = {s["id"]: s for se in u2["sessions"] for s in se["steps"]}
    obj2["obj-u2-final-letters"]["instructionStepIds"] = ["u2-st-final-letters", "u2-st-devoicing"]
    obj2["obj-u2-final-letters"]["remediationStepIds"] = ["u2-st-devoicing", "u2-st-final-letters"]
    steps2["u2-st-type-final"]["remediationStepId"] = "u2-st-devoicing"
    all_steps = {s["id"]: s for u in (u3, u4) for se in u["sessions"] for s in se["steps"]}
    for rem, objective in (("u3-st-rem-phrases", obj3["obj-u3-phrases"]), ("u3-st-rem-naming", obj3["obj-u3-naming"]),
                           ("u4-st-rem-pronouns", obj4["obj-u4-pronouns"]), ("u4-st-rem-stress", obj4["obj-u4-stress"])):
        all_steps[rem]["exampleIds"] = list(objective["exampleIds"])

    # --- A2-32 binder and A2-33 tutor tasks -------------------------------------------------------
    u3["binderTask"] = {
        "id": "binder-u3-groups",
        "prompt": "In your binder, make three columns headed masculine, feminine and neuter. Write each word from this "
                  "unit's three groups in its column with its meaning. Add дя́дя and мужчи́на to the masculine column.",
        "objectiveIds": ["obj-u3-gender", "obj-u3-words"],
    }
    u3["tutorTask"] = {"id": "tutor-u3-phrases", "bring": "The Unit 3 greetings and naming questions, to use them in a short exchange."}
    u4["binderTask"] = {
        "id": "binder-u4-present",
        "prompt": "In your binder, copy the six present forms of чита́ть from this unit with the person word for each "
                  "(я, ты, он, мы, вы, они). Then write the forms of знать next to them and check them against the "
                  "forms in the unit's typing check.",
        "objectiveIds": ["obj-u4-present", "obj-u4-present-typing", "obj-u4-pronouns"],
    }
    u4["tutorTask"] = {"id": "tutor-u4-present", "bring": "Your binder table of present forms, to say each form aloud with its person word."}

    pack["items"].extend(new_gender + typed3 + [guided3] + typed4 + [guided4])
    pack["examples"].extend(added_examples)
    existing = {r["itemId"]: r for r in pack["provenanceRecords"]}
    for r in records:
        old = existing.get(r["itemId"])
        if old is None:
            pack["provenanceRecords"].append(r)
        elif (old["shippedText"], old["sourceRecordId"], old["unitNumber"]) != (r["shippedText"], r["sourceRecordId"], r["unitNumber"]):
            raise SystemExit(f"{r['itemId']} exists with different content")

    # --- Exact objective-bound coverage for the reworded Unit 3 gender explanations -----------------
    # The 2026-10-16 research-synthesis records bound the old bodies; dated T14-b records bind the new ones.
    research = {r["itemId"]: r for r in pack["provenanceRecords"]}
    for step_id in ("u3-st-teach-gender-2", "u3-st-teach-gender-3", "u3-st-rem-gender"):
        old = research[f"prov-ca-u3-gender-{step_id}-complete-v1"]
        body = steps3[step_id]["body"]
        synthesis = dict(old["synthesis"])
        synthesis["bibliography"] = old["synthesis"]["bibliography"] + (
            " T14-b rewording dated 2026-11-11 (A2-26): governance words replaced in the learner text; the teacher "
            "and mom rows (bare/accented/translations_en/gender) added from the same frozen OpenRussian snapshot.")
        synthesis["authorshipDeclaration"] = ("Independently authored English explanation and arrangement on 2026-10-16, "
                                              "reworded on 2026-11-11 for A2-26 (T14-b); existing native excerpts and "
                                              "dictionary fields retain separate attribution. No owned-book prose.")
        synthesis["objectiveOrClaimBinding"] = f"obj-u3-gender:{step_id}:t14b-a226-v1"
        original = synthesis["bibliography"] + "\n" + body
        new = dict(old)
        new.update({"itemId": f"prov-ca-u3-gender-{step_id}-t14b-a226-v1",
                    "sourceRecordId": f"ca-coverage:obj-u3-gender:{step_id}:t14b-a226-v1",
                    "originalSourceText": original, "shippedText": body, "synthesis": synthesis,
                    "sourceHash": sha(original), "contentHash": sha(body)})
        pack["provenanceRecords"].append(new)

    # --- Coverage: new English fragments become authored scaffold records -------------------------
    for n in (3, 4):
        u = units[n]
        shipped = [r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == n]
        scaffolds = [r["text"] for r in pack["authoredEnglishRecords"] if r["unitNumber"] == n]
        count = sum(1 for r in pack["authoredEnglishRecords"] if r["unitNumber"] == n and r["id"].startswith(f"en-u{n}-t14b-"))
        for host in hosts(pack, u):
            leftover = gaps(host, coverage(host, shipped + scaffolds))
            for g in leftover:
                if g in scaffolds:
                    continue
                if CYRILLIC.search(g) or LINGUISTIC_CLAIM.search(g) or (GRAMMAR_TERM.search(g) and "?" not in g):
                    raise SystemExit(f"Unit {n}: fragment needs a source span, not authored English: {g!r} in {host[:80]!r}")
                count += 1
                scaffolds.append(g)
                pack["authoredEnglishRecords"].append({"id": f"en-u{n}-t14b-{count:03d}", "unitNumber": n, "text": g,
                                                       "classification": "INDEPENDENTLY_AUTHORED_SCAFFOLD"})
        for host in hosts(pack, u):
            if GOVERNANCE.search(host):
                raise SystemExit(f"Unit {n}: governance term left in learner text: {host[:100]!r}")
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T14-b Units 3-4 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
