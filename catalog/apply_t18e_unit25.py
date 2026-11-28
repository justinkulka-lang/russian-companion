#!/usr/bin/env python3
"""Apply the D64–D65 Unit 25 packet to the T18cd-fix curriculum."""
from __future__ import annotations

import copy
import csv
import hashlib
import json
from pathlib import Path

from apply_d7b_selection_records import frozen_record, selectable
from apply_t14b_units_1_2_owner import typed_item
from apply_t17a_unit_16 import forms, sentences, stressed
from curriculum_storage import encode, loads

ROOT = Path(__file__).resolve().parents[1]
ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
LOG = ROOT / "audit/T18e-unit25-changes.csv"
BASELINE = "f2667ddcdc629fc5c4ff49e236fee4cbe98118ea767adf6dad3db9037388f9b4"
SOURCE_FILES = {
    "N": "units-21-25/wikibooks/names-4660786.txt",
    "U": "units-21-25/wikibooks/useful-words-3353859.txt",
    "L": "units-21-25/supplemental/lichnoe-imya-154915954.txt",
    "A": "units-21-25/supplemental/obraschenie-153012874.txt",
    "G": "units-26-30/wikipedia/russian-grammar-1369617627.txt",
    "V": "units-3-5/openrussian/verbs.csv",
}
TOPICS = ("name-structure", "diminutives", "courtesy", "name-question", "nickname-rule", "address", "documented-name-form")
USE_FORMS = "Use only the documented forms in this exercise."
TEACHING = {
    "name-structure": (
        "имя means first name. отчество means patronymic name. фамилия means family name. "
        "A patronymic is the father-derived part of the personal name.",
        "The pattern with the father's first name uses ович for son of and овна for daughter of. "
        "These are suffixes. Use only these examples.",
        "Василий Иванович shows a first name followed by a patronymic. The surname is not shown. "
        "A complete three-component name also has a family name.",
    ),
    "diminutives": (
        "Александр is Alexander, Алексей is Alexei and Дмитрий is Dmitri. "
        "Sasha goes with Alexander, Alyosha with Alexei and Dima with Dmitri.",
        "Other examples are Misha for Mikhail, Lena for Elena, Olya for Olga, Natasha for Natalya and Katya for Ekaterina. "
        "A name can have several short or affectionate forms.",
        "Short and affectionate names can carry social meaning. Check how the person wishes to be addressed.",
    ),
    "courtesy": (
        "Пожа́луйста means please in a request. Спаси́бо means thanks or thank you.",
        "Извини́те means excuse me with polite or plural address. извини́ is the familiar singular form. "
        "Both are apologies.",
        "The ending -те allows polite address to one person or address to more than one person. "
        "The verb извини́ть means to excuse or pardon.",
    ),
    "name-question": (
        "Как Вас зову́т? means What is your name? The capital in Вас can mark written courtesy to one person "
        "in personal letters, official papers or questionnaires. It does not change the spoken pronoun.",
        "Меня́ зову́т means My name is. It begins the reply and must be followed by a name.",
        "Прошу́ проще́ния means I'm sorry or excuse me. Как дела́? means How are you?",
    ),
    "nickname-rule": (
        "Марюня illustrates от полной формы имени, meaning from the full form of the name. "
        "Машенька illustrates от краткой формы, meaning from the short form.",
        "Сашка can have оттенок пренебрежения, a shade of dismissiveness. "
        "The other two expressions describe the base of a name form.",
        "The suffix -к- can convey dismissiveness. Among people of equal standing it has also conveyed closeness. "
        "Intonation and context help distinguish these readings. Use a form the person prefers.",
    ),
    "address": (
        "Ирина illustrates полные имена, full given-name forms. Ира illustrates краткие имена, short given-name forms.",
        "Ирочка illustrates уменьшительно-ласкательные, diminutive or affectionate forms. Use the documented forms shown here.",
        "Address by полному имени, the full given-name form, can have отстранённо-официального регистра, a detached "
        "formal tone. It also occurs in полуофициальной обстановке, a semi-official setting. Short forms occur in "
        "дружески-неофициальном общении, friendly informal conversation. Intonation, relationship and setting matter.",
    ),
    "documented-name-form": (
        "Марюня illustrates от полной формы имени, from the full given-name form. Мария is one full given-name form "
        "in this group. Машенька illustrates от краткой формы, from the short form Маша. Маня is another short form.",
        "Сашка can have оттенок пренебрежения, a shade of dismissiveness. "
        "The coloring can vary with intonation and context.",
        f"Маруся belongs to the examples built on the full given-name form. {USE_FORMS} "
        "Use only the forms taught in this exercise.",
    ),
}
TITLES = {
    "name-structure": ("Parts of a personal name", "Patronymic suffixes", "First name and patronymic"),
    "diminutives": ("Full and short forms", "More short forms", "Choosing a form of address"),
    "courtesy": ("Please and thank you", "Polite and familiar apologies", "Polite and plural address"),
    "name-question": ("Asking someone’s name", "Giving your name", "An apology and another question"),
    "nickname-rule": ("Full and short bases", "Dismissive coloring", "Intonation and context"),
    "address": ("Full and short given names", "Affectionate forms", "Full names and register"),
    "documented-name-form": ("Full and short bases", "Dismissive coloring", "The base of Маруся"),
}
MEANINGS = {
    "имя": "the given name", "отчество": "the father-derived name", "фамилия": "the family name",
    "ович": "a suffix meaning son of", "овна": "a suffix meaning daughter of",
    "Sasha": "the short form of Alexander", "Alyosha": "the short form of Alexei", "Dima": "the short form of Dmitri",
    "Пожа́луйста": "please in a request", "Спаси́бо": "thanks", "Извини́те": "an apology in polite or plural address",
    "извини́те": "the apology used in polite or plural address", "извини́": "the apology used in familiar singular address",
    "Как Вас зову́т?": "a question asking someone’s name", "Меня́ зову́т": "the start of a reply giving one’s name",
    "Прошу́ проще́ния": "an apology", "Как дела́?": "a question asking how someone is",
    "от полной формы имени": "a derivation from the full given-name form, as with Марюня",
    "от краткой формы": "a derivation from a short form, as with Машенька",
    "оттенок пренебрежения": "a shade of dismissiveness that Сашка can convey",
    "полные имена": "full given-name forms such as Ирина", "краткие имена": "short forms such as Ира",
    "уменьшительно-ласкательные": "affectionate forms such as Ирочка",
    "отстранённо-официального регистра": "a detached formal register",
    "дружески-неофициальном общении": "friendly informal conversation", "полуофициальной обстановке": "a semi-official setting",
    "Мария": "a full given-name form at the base of Маруся",
    "Маша": "a short base used for other derivatives, including Машенька",
    "Маня": "a short base used for derivatives such as Манечка",
}
REWORD = {
    3162: "Use only these examples.", 3200: "Check how the person wishes to be addressed.",
    3315: "Use a form the person prefers.", 3345: "Use the documented forms shown here.",
    3386: USE_FORMS, 3387: "Use only the forms taught in this exercise.",
    3403: "Check an unfamiliar form before using it in this exercise.", 3407: USE_FORMS,
}
REMOVE = {3149,3165,3170,3179,3186,3188,3202,3204,3209,3211,3218,3232,3236,3243,3247,3248,3249,3250,
          3252,3254,3255,3256,3272,3273,3274,3275,3279,3280,3281,3284,3285,3286,3290,3294,3295,3300,
          3311,3313,3320,3328,3335,3353,3360,3367,3374,3380,3381,3388,3392,3399,3404,3405,3406}
FACT_RANGES = (
    (3146,3184,"N:5,7"), (3185,3214,"N:18; L:264–268,284; A:82,85"), (3215,3246,"U:11–15; G:419,1169; V:308"),
    (3247,3295,"U:14,20,25–26; G:419"), (3300,3328,"L:284,286; A:89"),
    (3335,3367,"A:25,78–85,89,94"), (3373,3407,"L:264,284,286; A:82–85,89"),
)
TYPED = (
    ("name-structure", "given-name", "N", 5, "имя", "the Russian term for the given name"),
    ("name-structure", "patronymic", "N", 5, "отчество", "the Russian term for the father-derived name"),
    ("name-structure", "family-name", "N", 5, "фамилия", "the Russian term for the family name"),
    ("courtesy", "please", "U", 11, "Пожа́луйста", "please in a request"),
    ("courtesy", "thanks", "U", 12, "Спаси́бо", "thanks for a favor"),
    ("courtesy", "polite-apology", "U", 15, "Извини́те", "excuse me in polite or plural address, with an initial capital"),
    ("courtesy", "familiar-apology", "V", 308, "извини́", "excuse me in familiar singular address, starting with lowercase"),
)


def sha(text):
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def apply(pack):
    unit = pack["units"][24]
    objectives = {o["id"].removeprefix("obj-u25-"): o for o in unit["objectives"]}
    steps = {st["id"]: st for session in unit["sessions"] for st in session["steps"]}
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    items = {i["id"]: i for i in pack["items"] if i["objectiveId"].startswith("obj-u25-")}
    examples = {e["id"]: e for e in pack["examples"] if e["id"].startswith("ex-u25-")}
    source_lines = {page: (ROOT / "catalog/sources" / path).read_text().splitlines() for page, path in SOURCE_FILES.items()}
    rows = []
    authored = {}

    def log(owner, old, new, rule):
        if old != new:
            rows.append(dict(task="T18e", finding=rule, **{"owner.field": owner}, old=old, new=new))

    def write(host, field, text, topic, rule="D64 / A2-26"):
        owner = f"{host['id']}.{field}"
        log(owner, host.get(field, ""), text, rule)
        host[field] = text
        authored[owner] = (topic, text)

    source_templates = {
        "N": "prov-ex-u25-name-structure-0-ru", "U": "prov-ex-u25-courtesy-0-ru",
        "L": "prov-ex-u25-nickname-rule-0-ru", "A": "prov-ex-u25-address-0-ru",
        "G": "prov-d7b-u25-038",
        "V": next(r["itemId"] for r in records.values()
                  if r["sourceId"] == "src-openrussian-50e210c4" and r["sourceRecordId"].startswith("verbs.csv:")),
    }
    created = {}

    def citation(topic, page, first, shown, last=None, record_id=None, transformation="selection", raw=None):
        raw = raw if raw is not None else "\n".join(source_lines[page][first-1:last or first])
        assert selectable(raw, shown) or transformation == "formatting" and stressed(raw) == shown, (page, first, shown)
        key = (topic, page, first, raw, shown, record_id)
        if key in created:
            return created[key]
        prefix = records[source_templates[page]]["sourceRecordId"].split(":", 1)[0]
        ident = f"prov-t18e-u25-{page.lower()}{first}-{topic}-{len(created) + 1}"
        row = copy.deepcopy(records[source_templates[page]])
        row.pop("audioAssetId", None)
        row.update(itemId=ident, unitNumber=25, objectiveId=objectives[topic]["id"], recordType="lexical record",
                   sourceRecordId=record_id or f"{prefix}:{raw[:80]}", originalSourceText=raw, shippedText=shown,
                   sourceHash=sha(raw), contentHash=sha(shown), transformation=transformation)
        assert frozen_record(pack, row), ident
        pack["provenanceRecords"].append(row)
        records[ident] = row
        created[key] = ident
        log(ident, "", shown, f"D64 / {page}:{first}" + (f"–{last}" if last else ""))
        return ident

    verb = dict(zip(source_lines["V"][0].split("\t"), source_lines["V"][307].split("\t")))

    def verb_form(field, whole_row=False):
        raw = source_lines["V"][307] if whole_row else verb[field]
        return citation("courtesy", "V", 308, stressed(verb[field]), record_id=f"verbs.csv:{verb['accented']}#{field}",
                        transformation="formatting", raw=raw)

    retired_id = "u25-i-name-form-3"
    old_policy = items.pop(retired_id)
    log(retired_id, old_policy["prompt"], "RETIRED", "D64c")
    pack["items"].remove(old_policy)
    for ident in tuple(records):
        record = records[ident]
        if record["unitNumber"] != 25:
            continue
        if record["sourceRecordId"] == "phase2:documented-name-form" or ident in {
            "prov-ca-25-233", "prov-ca-25-235", "prov-ca-25-581", "prov-ca-25-582", "prov-ca-25-585",
            "prov-ca-25-586", "prov-ca-25-587", "prov-ca-25-591", "prov-ca-25-592", "prov-ca-25-594",
            "prov-ca-u25-instruction-name-question-teach-1-body", "prov-ca-u25-instruction-name-question-rem-body",
        } or (record["sourceId"] == "src-content-audit-research" and record["objectiveId"] == objectives["documented-name-form"]["id"]):
            pack["provenanceRecords"].remove(record)
            records.pop(ident)
            log(ident, record["shippedText"], "RETIRED", "D64a,c / D65")

    name_applications = (
        ("u25-g-name-form-1", "Марюня", "от полной формы имени", ("от краткой формы", "оттенок пренебрежения")),
        ("u25-i-name-form-1", "Машенька", "от краткой формы", ("от полной формы имени", "оттенок пренебрежения")),
        ("u25-i-name-form-2", "Сашка", "оттенок пренебрежения", ("от полной формы имени", "от краткой формы")),
        ("u25-i-name-form-base", "Маруся", "Мария", ("Маша", "Маня")),
    )

    def bind(item, ids, relation, shape):
        old = item["assessment"]["relationParticipantIds"]
        log(f"{item['id']}.assessment.relationParticipantIds", old, ids, "D64a / D65")
        item["sourceId"] = records[ids[0]]["sourceId"]
        item["assessment"].update(cueLanguage="ru", relationKind=relation, evidenceMode=relation, responseKind=shape,
                                  relationParticipantIds=ids, answerSourceIds=[ids[1]],
                                  distractorSourceIds=["", *ids[2:]], contrastMembers=item["options"])

    form_topic = "documented-name-form"
    new_name = copy.deepcopy(old_policy)
    new_name["id"] = "u25-i-name-form-base"
    if new_name.get("remediationStepId"):
        assert new_name["remediationStepId"] == "u25-st-rem-name-form"
    new_name["assessment"].update(testedFeature="DOCUMENTED_BASE_NAME", contrastSetId=new_name["id"])
    pack["items"].append(new_name)
    items[new_name["id"]] = new_name
    obj = objectives[form_topic]
    obj["checkItemIds"] = [new_name["id"] if i == retired_id else i for i in obj["checkItemIds"]]
    for step in steps.values():
        if retired_id in step.get("itemIds", []):
            step["itemIds"] = [new_name["id"] if i == retired_id else i for i in step["itemIds"]]
    name_prompts = (
        "How would you classify this name form?",
        "Which description fits this name form?",
        "Which classification applies to this form?",
        "Which full given-name form is a base for this derivative?",
    )
    for (ident, cue, answer, distractors), prompt in zip(name_applications, name_prompts):
        item = items[ident]
        log(f"{ident}.options", item["options"], [answer, *distractors], "D65")
        item.update(promptLetter=cue, answer=answer, options=[answer, *distractors])
        write(item, "prompt", prompt, form_topic, "D65")
        ids = [citation(form_topic, "L", 284, value, 286) for value in (cue, answer, *distractors)]
        bind(item, ids, "STATED_APPLICATION", "RUSSIAN_TOKEN" if item is new_name else "RUSSIAN_PHRASE")

    cap = next(c for c in pack["sourceCapabilities"] if c["objectiveId"] == obj["id"])
    source = records[new_name["assessment"]["relationParticipantIds"][0]]
    log(cap["id"], cap["sourceHash"], source["sourceHash"], "D65 / L:284–286")
    cap.update({key: source[key] for key in ("sourceId", "sourceRevision", "sourceRecordId", "sourceHash")}, exactSpan=source["originalSourceText"])
    for n in range(2):
        example = examples[f"ex-u25-name-form-{n}"]
        ident, cue, key, _ = name_applications[n+1]
        write(example, "note", key, form_topic, "D65")
        example["sourceId"] = source["sourceId"]

    courtesy = items["u25-i-courtesy-2"]
    cue_id, polite_id, familiar_id = [verb_form(field, whole_row=True) for field in ("accented", "imperative_pl", "imperative_sg")]
    familiar_key_id = verb_form("imperative_sg")
    log(f"{courtesy['id']}.options", courtesy["options"], [records[polite_id]["shippedText"], records[familiar_id]["shippedText"]], "D64a / V:308")
    courtesy.update(promptLetter=records[cue_id]["shippedText"], answer=records[polite_id]["shippedText"],
                    options=[records[polite_id]["shippedText"], records[familiar_id]["shippedText"]])
    bind(courtesy, [cue_id, polite_id, familiar_id], "SAME_LEMMA_DIFFERENT_FIELD", "RUSSIAN_TOKEN")
    write(courtesy, "prompt", "Which apology fits polite address to one person or address to several people?", "courtesy", "D64a")
    write(examples["ex-u25-courtesy-2"], "note", "The ending -те marks polite or plural address. Use извини́ in familiar singular address.", "courtesy", "D64a,d")
    write(examples["ex-u25-name-structure-3"], "note", "A first name followed by a patronymic. The surname is not shown.", "name-structure", "D64d / N:5,7")

    for ident, prompt in {
        "u25-g-name-structure-0": "Which component is a person’s given name?",
        "u25-i-name-structure-1": "Which component is derived from the father’s given name?",
        "u25-i-name-structure-2": "Which component identifies the family?",
        "u25-i-diminutives-1": "Which English short form goes with Алексей?",
        "u25-i-diminutives-2": "Which English short form goes with Дмитрий?",
        "u25-i-address-3": "Which register can address by the full given-name form convey?",
    }.items():
        write(items[ident], "prompt", prompt, items[ident]["objectiveId"].removeprefix("obj-u25-"), "D64 / A2-24")

    rationale_evidence = {
        "u25-g-name-structure-0": "first name, or имя",
        "u25-i-name-structure-1": "patronymic name, or отчество",
        "u25-g-diminutives-0": "Sasha for Alexander",
        "u25-i-diminutives-1": "Alyosha for Alexei",
        "u25-i-diminutives-2": "Dima for Dmitri",
        "u25-i-courtesy-1": "Спаси́бо (Spasiba) :thanks",
        "u25-i-courtesy-2": verb["translations_en"],
        "u25-i-address-3": "полному имени",
        "u25-i-name-form-base": "от полной формы имени",
    }
    for item in items.values():
        topic = item["objectiveId"].removeprefix("obj-u25-")
        answer = item["answer"]
        feedback = f"{answer} — {MEANINGS[answer]}."
        if item["id"] == "u25-i-courtesy-2":
            feedback = f"{answer} takes -те for polite or plural address."
        if item["id"] == "u25-i-name-form-base":
            feedback = f"{answer} is a full given-name form at the base of Маруся. {REWORD[3403]}"
        if item["id"] == "u25-i-name-question-1":
            feedback = "Меня́ зову́т begins a reply giving your name. Follow it with your name."
        if item["id"] == "u25-i-name-question-2":
            feedback = "Как Вас зову́т? asks your name. Begin your reply with Меня́ зову́т and then say your name."
        write(item, "feedback", feedback, topic)
        material = ["" if option == answer else f"{option} — {MEANINGS[option]}." for option in item["options"]]
        log(f"{item['id']}.distractorRationales", item["distractorRationales"], material, "D64 / A2-26")
        item["distractorRationales"] = material
        witness = rationale_evidence.get(item["id"])
        rationale = feedback if witness is None else f'{feedback} Frozen evidence: "{witness}".'
        item["assessment"].update(distractorDifference=material, whyAnswerDemonstratesObjective=rationale)
        if witness is not None:
            authored[f"{item['id']}.assessment-evidence"] = topic, rationale
        for n, rationale in enumerate(material):
            if rationale:
                authored[f"{item['id']}.rationale{n}"] = (topic, rationale)

    evidence = {
        "name-structure": [
            citation("name-structure", "N", 5, "Russians use three names"),
            citation("name-structure", "N", 7, "Василий Иванович"),
        ],
        "diminutives": [citation("diminutives", "N", 18, "Alyosha for Alexei"),
                        citation("diminutives", "L", 284, "кратких форм")],
        "courtesy": [
            citation("courtesy", "U", 11, "please"), citation("courtesy", "U", 12, "thanks"),
            citation("courtesy", "U", 15, "I'm sorry / excuse me"), polite_id, familiar_id,
            citation("courtesy", "G", 1169, "The plural (including the polite"),
            citation("courtesy", "G", 1169, "-те"),
        ],
        "name-question": [citation("name-question", "U", n, witness) for n, witness in (
            (14, "I'm sorry / excuse me"), (20, "How are you?"), (25, "Как Вас зову́т?"), (26, "Меня́ зову́т"),
        )] + [citation("name-question", "G", 419, "It begins with a capital letter")],
        "nickname-rule": ["prov-ex-u25-nickname-rule-0-en", "prov-ex-u25-nickname-rule-2-en",
                          citation("nickname-rule", "A", 89, "интонация")],
        "address": [citation("address", "A", first, quote) for first, quote in (
            (78, "полные имена"), (80, "отстранённо-официального регистра"), (82, "дружески-неофициальном общении"),
            (85, "Уменьшительно-ласкательные"), (89, "интонация"),
        )],
        form_topic: [citation(form_topic, "L", 284, value, 286) for value in ("от полной формы имени", "оттенок пренебрежения")]
                    + [citation(form_topic, "A", 89, "интонация")],
    }

    evidence["name-question"].append(citation("name-question", "G", 419, "Вас"))
    evidence[form_topic].append(citation(form_topic, "L", 284, "Манечка"))

    typed = {topic: [] for topic in TOPICS}
    for topic, suffix, page, line, key, cue in TYPED:
        ident = familiar_key_id if page == "V" else citation(topic, page, line, key)
        assert records[ident]["shippedText"] == key
        evidence[topic].append(ident)
        i = typed_item(f"u25-i-type-{suffix}", objectives[topic]["id"], "Type the single Russian form described.",
                       cue, key, "Compare your spelling with the answer.", records[ident]["sourceId"])
        i["typedAnswerKey"]["review"]["reviewer"] = "prepared by Codex (implementer); final audit pending"
        i["keyCitation"] = ident
        typed[topic].append(i["id"])
        pack["items"].append(i)
        log(i["id"], "", key, f"D64e / {page}:{line}")
        for field in ("prompt", "promptLetter", "feedback"):
            authored[f"{i['id']}.{field}"] = (topic, i[field])

    closed_steps, typed_steps = [], []
    for session in unit["sessions"]:
        topic = session["objectiveIds"][0].removeprefix("obj-u25-") if session["objectiveIds"] else ""
        if topic not in TOPICS or not any(s["kind"] == "CHECK" for s in session["steps"]):
            continue
        for number, (title, text) in enumerate(zip(TITLES[topic], TEACHING[topic]), 1):
            step_topic = "name-form" if topic == form_topic else topic
            step = steps[f"u25-st-teach-{step_topic}-{number}"]
            write(step, "title", title, topic)
            write(step, "body", text, topic)
        obj = objectives[topic]
        practice = steps[obj["practiceStepIds"][0]]
        direction = "Read the examples. Cover the forms and write them from their meanings before choosing an answer." if typed[topic] else "Read each example, then choose an answer."
        write(practice, "body", direction, topic)
        rem = steps[obj["remediationStepIds"][0]]
        write(rem, "title", "Review " + TITLES[topic][0].lower(), topic)
        write(rem, "body", "\n\n".join(TEACHING[topic]), topic)
        rem["exampleIds"] = list(dict.fromkeys(e for step in session["steps"] if step["kind"] == "TEACH" for e in step.get("exampleIds", [])))
        check, = [step for step in session["steps"] if step["kind"] == "CHECK"]
        session["steps"].remove(check)
        write(check, "body", "Choose an answer. If you miss one, read the explanation and retry.", topic, "A2-24")
        log(check["id"], session["id"], "u25-s-checks", "A2-24")
        closed_steps.append(check)
        if typed[topic]:
            obj["checkItemIds"] = typed[topic] + obj["checkItemIds"]
            typed_steps.append(dict(check, id=f"u25-st-check-type-{topic}", title="Write the forms",
                                    body="Type each form from its description.", itemIds=typed[topic]))
            assert all(key in rem["body"] for t, _, _, _, key, _ in TYPED if t == topic)
    unit["sessions"].append(dict(id="u25-s-checks", title="Unit 25 checks", estimatedMinutes=20,
                                objectiveIds=[objectives[t]["id"] for t in TOPICS], steps=typed_steps + closed_steps))

    write(unit, "topicSummary", "Practise personal names, forms of address and everyday courtesy.", "name-structure")
    write(unit, "mappingNote", "This unit goes with Lesson 25 in your book, printed pages 305-319: diminutives, Russian personal-name forms, social address, requests, apologies, and polite expressions.", "book")
    write(unit["sessions"][0], "title", "Names and courtesy", "name-structure")
    write(steps["u25-st-orient"], "title", "Names and courtesy", "name-structure")
    write(steps["u25-st-orient"], "body", "Read the examples, practise the forms, and do Lesson 25 in your book.", "name-structure")
    write(steps["u25-st-book-work"], "body", "Do Lesson 25 in your book.", "book")
    write(steps["u25-st-book"], "body", "When you finish the work in your book, mark it done here.", "book")
    for suffix in ("book-work", "book"):
        write(steps[f"u25-st-{suffix}"], "bookDirection", "Do Lesson 25, printed pages 305-319, in your book.", "book")
    for suffix, body in (
        ("anki", "Make cards to review name parts and courtesy expressions."),
        ("binder", "Write the forms from their meanings in your binder, then compare with the model."),
        ("tutor", "Bring your binder work to your tutor if you have one."),
        ("cumulative", "Complete your assigned review before moving on."),
    ):
        write(steps[f"u25-st-{suffix}-direction"], "body", body, "book")
    intro = "Review earlier letter pairs and negative sentences. After a miss, read the explanation and examples again, then retry. Finish the assigned review before moving on."
    log("review-introduction-25.introduction", pack["reviewCopy"]["introductions"]["25"], intro, "D64 / A2-26")
    pack["reviewCopy"]["introductions"]["25"] = intro
    binder = dict(id="binder-u25-names-courtesy", objectiveIds=[objectives[t]["id"] for t in TOPICS if typed[t]])
    write(binder, "prompt", "Write the terms for the three name components and the four courtesy forms from their meanings, then compare with the model.", "book", "WC011")
    write(binder, "modelAnswer", "\n".join(f"{key} — {cue}" for _, _, _, _, key, cue in TYPED), "book", "WC011")
    unit["binderTask"] = binder
    tutor = dict(id="tutor-u25-names-courtesy")
    write(tutor, "bring", "Bring your binder work. Practise asking and giving names and apologizing in familiar and polite situations with your tutor.", "book", "WC011")
    unit["tutorTask"] = tutor

    for suffix, topics, title in (
        ("names", ("name-structure", "diminutives", "nickname-rule", form_topic), "Personal names and their short forms"),
        ("courtesy", ("courtesy", "name-question", "address"), "Courtesy and forms of address"),
    ):
        ref = dict(id=f"ref-u25-{suffix}", introducedInUnit=25, classification="LINGUISTIC_INSTRUCTION",
                   reviewStatus="IMPLEMENTED_PENDING_FINAL_AUDIT", sourceId=records[evidence[topics[0]][0]]["sourceId"],
                   searchTerms=title.lower().split())
        write(ref, "title", title, topics[0], "A2-36")
        write(ref, "summary", title + " in Russian.", topics[0], "A2-36")
        write(ref, "detail", "\n\n".join(body for topic in topics for body in TEACHING[topic]), topics[0], "A2-36")
        pack["references"].append(ref)
        for topic in topics:
            for sid in objectives[topic]["instructionStepIds"]:
                if steps[sid]["kind"] == "TEACH":
                    steps[sid].setdefault("referenceIds", []).append(ref["id"])

    instructions = set(REWORD.values())
    for topic in TOPICS:
        obj = objectives[topic]
        local = [r for r in records.values() if r["objectiveId"] == obj["id"] and frozen_record(pack, r)]
        by_form = {}
        for rec in local:
            for form in forms(rec["shippedText"]):
                by_form.setdefault(form, rec["itemId"])
        hosts = [steps[sid] for sid in obj["remediationStepIds"]] + [i for i in items.values() if i["objectiveId"] == obj["id"]]
        for host in hosts:
            texts = [host["body"]] if "body" in host else [host["feedback"], *filter(None, host["distractorRationales"])]
            facts = {}
            for text in texts:
                for sentence in sentences(text):
                    if sentence in instructions or not any(c.isalpha() for c in sentence):
                        continue
                    form_refs = [by_form[form] for form in sorted(forms(sentence)) if form in by_form]
                    facts[sentence] = list(dict.fromkeys(evidence[topic] + form_refs))
            host["russianFacts"], host["russianFactSources"] = list(facts), facts
            for number, (sentence, sources) in enumerate(facts.items()):
                authored[f"{host['id']}.fact{number}"] = topic, sentence
                log(f"{host['id']}.russianFacts[{number}]", sentence, ", ".join(sources), "D64 / MARK")

    inventory = (ROOT / "audit/T15-n0-unmarked-russian-facts.csv").read_text().splitlines()
    for first, last, refs in FACT_RANGES:
        for number in range(first, last + 1):
            _, owner, field, old, protection = next(csv.reader([inventory[number - 1]]))
            if not protection.startswith("English-only"):
                continue
            if number in REMOVE:
                disposition, new = "REMOVE", "REMOVED"
            elif number in REWORD:
                disposition, new = "REWORD", REWORD[number]
            else:
                disposition, new = "MARK", refs
            log(f"prep:{number}:{owner}.{field}", old, new, f"D64 / {disposition}")

    coverage_template = records["prov-t15an1-u6-possessive-our-your"]
    shipped = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == 25}
    for owner, (topic, text) in authored.items():
        if not text or text in shipped:
            continue
        objective = objectives[topic]["id"]
        note = "English wording authored 2026-11-28 by Codex for T18e. Fact markers cite separate frozen evidence."
        span = note + "\n" + text
        record = copy.deepcopy(coverage_template)
        record["synthesis"].update(bibliography=note, authorshipDeclaration=f'{note} New text: "{text}"',
                                   objectiveOrClaimBinding=f"{objective}:{owner}")
        record.update(itemId="prov-ca-t18e-" + owner.replace(".", "-"), unitNumber=25, objectiveId=objective,
                      sourceRecordId=f"ca-coverage:{objective}:{owner}", shippedText=text, originalSourceText=span,
                      contentHash=sha(text), sourceHash=sha(span),
                      attribution="Russian Companion content-audit research synthesis. English wording authored 2026-11-28 for T18e.")
        pack["provenanceRecords"].append(record)
        shipped.add(text)

    assert sum(len(o["checkItemIds"]) for o in objectives.values()) == 23
    assert sum(map(len, typed.values())) == 7
    return rows


def main():
    text = ASSET.read_text()
    if sha(text) != BASELINE:
        raise SystemExit("Expected the exact T18cd-fix curriculum baseline")
    pack = loads(text)
    rows = apply(pack)
    encoded = encode(pack)
    ordered = {key: encoded[key] for key in json.loads(text) if key in encoded}
    ordered.update({key: value for key, value in encoded.items() if key not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n")
    with LOG.open("w", newline="") as stream:
        writer = csv.DictWriter(stream, ["task", "finding", "owner.field", "old", "new"], lineterminator="\n")
        writer.writeheader()
        writer.writerows(rows)


if __name__ == "__main__":
    main()
