#!/usr/bin/env python3
"""Repair the saved PR64 checkpoint, never rerun the first-pass transformer.

This is a pending implementation repair, not a review approval. The caller
must supply the exact saved checkpoint; later invocations refuse to duplicate
the repair's provenance rows.
"""
import json
import re
import unicodedata
from copy import deepcopy
from apply_content_audit_corrections import Pack, PACK_PATH, SOURCE_ID


def complete_coverage_evidence(data):
    """Structure the authorship/bibliography already retained in coverage notes.

    Coverage records make no answer-key application. A contrast still needs
    its separately stated, same-span application; this adds no approval.
    """
    for record in data["provenanceRecords"]:
        if record["sourceId"] != SOURCE_ID or record.get("synthesis") is not None:
            continue
        if not record["sourceRecordId"].startswith("ca-coverage:"):
            raise ValueError("Missing synthesis on non-coverage record " + record["itemId"])
        binding = record["sourceRecordId"].removeprefix("ca-coverage:")
        if not binding.startswith(record["objectiveId"] + ":"):
            binding = record["objectiveId"] + ":" + binding
        record["sourceRecordId"] = "ca-coverage:" + binding
        header = record["originalSourceText"].split("\n", 1)[0]
        if "Bibliography" not in header or "Authorship: independently authored for Russian Companion." not in header:
            raise ValueError("Coverage note has no retained research declaration " + record["itemId"])
        record["synthesis"] = {
            "bibliography": header,
            "sourceDateOrVersion": "2026-10-08",
            "authorshipDeclaration": "Authorship: independently authored for Russian Companion.",
            "objectiveOrClaimBinding": binding,
            "finalAudit": {key: "" for key in ("artifactIdentity", "artifactDigest", "auditedContentDigest", "disposition", "modelVersion", "date")},
            "applications": [],
        }


def main():
    data = json.loads(PACK_PATH.read_text(encoding="utf-8"))
    if any(r["itemId"].startswith("prov-ca-repair-") for r in data["provenanceRecords"]):
        raise SystemExit("Checkpoint repair already applied; edit the repaired pack directly")
    pack = Pack(data)
    pack._seq = max(
        (int(m.group(1)) for r in data["provenanceRecords"]
         if (m := re.search(r"-(\d+)$", r["itemId"]))), default=0
    ) + 10000
    before = len(data["provenanceRecords"])

    pack.rebind("u7-i-manner-1", cue="бы́стрый → бы́стро",
        answer="a documented adjective-to-manner-adverb pair",
        distractors=["two forms of the same noun", "an unrestricted rule for every adjective"],
        prompt="Which English description matches the documented pair?",
        feedback="бы́стрый → бы́стро is a documented adjective-to-manner-adverb pair: the adjective describes a noun, while the adverb describes how an action is performed. Check each lexical pair rather than applying an unrestricted generator.",
        why="бы́стрый → бы́стро is a documented adjective-to-manner-adverb pair with different grammatical functions.",
        rationales={"two forms of the same noun": "two forms of the same noun would remain noun forms; this pair links an adjective with an adverb.",
            "an unrestricted rule for every adjective": "an unrestricted rule for every adjective is not established by one documented lexical pair."},
        tested_feature="DOCUMENTED_MANNER_PAIR")
    pack.rebind("u7-i-manner-2", cue="Она говорит быстро.",
        answer="manner adverb modifying the action",
        distractors=["an adjective describing the speaker"],
        prompt="Which English description identifies the function of быстро?",
        feedback="In Она говорит быстро., manner adverb modifying the action describes быстро: it tells us how she speaks and does not agree with the speaker.",
        why="In Она говорит быстро., manner adverb modifying the action describes how she speaks.",
        rationales={"an adjective describing the speaker": "an adjective describing the speaker would describe her qualities; быстро describes how the action is performed."},
        tested_feature="ADVERBIAL_FUNCTION")
    pack.rebind("u7-i-manner-4", cue="си́ний",
        answer="this unit supplies no documented manner-adverb counterpart",
        distractors=["the unit establishes a general conversion rule"],
        prompt="Which English description respects the scope of this unit's lexical evidence?",
        feedback="For си́ний, this unit supplies no documented manner-adverb counterpart. This is a statement about the supplied evidence, not a claim that no related word can exist in Russian.",
        why="For си́ний, this unit supplies no documented manner-adverb counterpart, so a learner must consult a reliable lexical source rather than invent a form.",
        rationales={"the unit establishes a general conversion rule": "the unit establishes a general conversion rule is false; its evidence is limited to specific documented pairs."},
        tested_feature="REJECTED_ARBITRARY_ADVERB")

    stress_title = "Past-passive stress in the supplied examples; broader patterns remain pending review."
    next(s for s in pack.units[27]["sessions"] if s["id"] == "u27-s-stress")["title"] = stress_title
    pack.add_authored(27, stress_title)

    # A manner-adverb objective must not introduce an unapproved later
    # predicative construction as its supposedly independent application.
    pack.rebind("u7-i-manner-3", cue="бы́стрый", answer="an adjective describing a noun",
        distractors=["a manner adverb modifying an action", "a verb naming the action"],
        prompt="Which English description identifies бы́стрый?",
        feedback="бы́стрый is an adjective describing a noun; the documented manner adverb is бы́стро. They have different grammatical jobs.",
        why="бы́стрый is an adjective describing a noun, rather than a description of how an action is performed.",
        rationales={"a manner adverb modifying an action": "a manner adverb modifying an action is a different grammatical job, exemplified by бы́стро.",
            "a verb naming the action": "a verb naming the action names what happens; бы́стрый instead describes a noun."},
        tested_feature="ADJECTIVE_ADVERB_CONTRAST")

    # Correct the transformer substring bug: 'imperfective' contains 'perfective'.
    # The imperfective objective must assess its own simultaneous construction.
    for item in list(data["items"]):
        if item["id"].startswith(("u28-g-imperfective-adverb-", "u28-i-imperfective-adverb-")):
            pack.rebind(
                item["id"], cue="читая письмо, он улыбался",
                answer="simultaneous with the main predicate",
                distractors=["an absolute present label", "a different subject from the main verb"],
                prompt="Which English description identifies the timing of читая in this sentence?",
                feedback="In читая письмо, он улыбался, simultaneous with the main predicate describes читая: he reads while smiling. The past main verb does not turn читая into an absolute present tense.",
                why="In читая письмо, он улыбался, читая is simultaneous with the main predicate: the same person reads and smiles during the same interval.",
                rationales={
                    "an absolute present label": "an absolute present label ignores the past main verb; the verbal adverb has relative time.",
                    "a different subject from the main verb": "a different subject from the main verb violates the shared-subject condition in this construction.",
                }, tested_feature="IMPERFECTIVE_ADVERB_SIMULTANEOUS",
            )

    # The old heading cannot be joined to a corrected example as a new claim.
    pack.set_body("u24-st-teach-stand-ins-2",
        "Negative predicative constructions express the absence of a person or thing for an action. The construction selects a case form; it has no nominative form. Compare the indefinite pronoun separately.")
    pack.set_body("u24-st-rem-stand-ins",
        "не́кто is an indefinite pronoun, an unspecified person. In не́кого спросить, there is nobody to ask: не́кого is the negative construction, not не́кто. Choose the construction and case from its sentence rather than changing a nominative mechanically.")
    pack.set_body("u27-comp-remediate",
        "Read the main verb and the participle separately. In Письмо, написанное Анной, лежит на столе., Анной identifies the writer. In Анна убрала прочитанное письмо., Анна put the letter away; прочитанное tells us it had been read but does not identify the reader.")
    for example in data["examples"]:
        if example["objectiveId"] == "obj-u28-imperfective-adverb":
            pack.set_example(example["id"], note="imperfective verbal adverb; relative time is interpreted with its main predicate, normally simultaneous in the taught construction")
        elif example["objectiveId"] == "obj-u28-perfective-adverb":
            pack.set_example(example["id"], note="perfective verbal adverb; normally anterior to its main action, which can itself be past or future")

    # Explanations name the actual displayed construction and its answer,
    # without introducing another option as an equally correct gloss.
    explanations = {
        "u16-g-complements-0": "стать means to become. The infinitive names becoming; it does not mark a past event.",
        "u17-g-calendar-date-0": "г. is year, the abbreviation used after the year number in a date.",
        "u17-i-calendar-date-2": "15 мая 1996 г. is 15 May 1996: the complete date gives the day, month and year.",
        "u19-i-conditional-2": "In я бы поспала́, the speaker is marked feminine by поспала́; я identifies that person as the speaker.",
        "u20-i-motion-3": "Он пошёл к другу. means he set off toward a friend: пошёл presents the start of the journey, and к другу gives its destination.",
        "u20-i-motion-4": "Мальчик отошёл от незнакомца means The boy stepped back from the stranger. The displayed sentence supplies no additional relative clause.",
        "u21-i-purpose-1": "если means if and introduces a condition for the main proposition.",
        "u24-g-alternation-0": "Я не ви́жу э́ту кни́гу. means I don't see this book, singular. э́ту кни́гу specifies one book and не supplies the negation.",
        "u24-i-alternation-2": "Я не ви́жу книг. means I don't see any books. книг is genitive plural, and не supplies the negation.",
        "u24-i-stand-ins-2": "не́кого спросить means there is nobody to ask. не́кого expresses absence of a person available for the infinitive action.",
        "u27-i-comp-2": "Анна убрала прочитанное письмо. means Anna put away the letter that had been read. Анна is the person who put it away; the passive participle does not identify who read it.",
    }
    for item in list(data["items"]):
        why = explanations.get(item["id"])
        if item["id"].startswith(("u28-g-perfective-adverb-", "u28-i-perfective-adverb-")):
            why = "In написав письмо, она отправит его завтра, anterior to a future main action describes написав: writing precedes the future sending, and the same person performs both actions."
        if why:
            pack.rebind(item["id"], cue=item["promptLetter"], answer=item["answer"],
                distractors=[x for x in item["options"] if x != item["answer"]],
                prompt=item["prompt"], feedback=why, why=why,
                rationales={x: item["distractorRationales"][j] for j, x in enumerate(item["options"]) if x != item["answer"]},
                tested_feature=item["assessment"]["testedFeature"])

    # Rebind the two optional stress items to their actual objective rather
    # than borrowing contrast provenance whose objective is required stress.
    for item_id in ("u27-g-stress-1", "u27-i-stress-1"):
        item = pack.items[item_id]
        pack.rebind(item_id, cue=item["promptLetter"], answer=item["answer"],
            distractors=[x for x in item["options"] if x != item["answer"]],
            prompt=item["prompt"], feedback=item["feedback"],
            why=item["assessment"]["whyAnswerDemonstratesObjective"],
            rationales={x: item["distractorRationales"][j] for j, x in enumerate(item["options"]) if x != item["answer"]},
            tested_feature=item["assessment"]["testedFeature"])

    # Use a different observable check for the second independent item;
    # reproducing the same cue/options/key is not independent sampling.
    for aspect in ("imperfective", "perfective"):
        item = pack.items[f"u28-i-{aspect}-adverb-2"]
        answer = ("the reader and the person smiling are the same person" if aspect == "imperfective"
                  else "the writer and sender are the same person")
        options = ["two unrelated people perform the actions", "the sentence omits its main subject"]
        why = f"In {item['promptLetter']}, {answer}: the verbal adverb shares the subject of the main predicate."
        pack.rebind(item["id"], cue=item["promptLetter"], answer=answer,
            distractors=options, prompt="Which English description identifies the shared subject?",
            feedback=why, why=why,
            rationales={options[0]: options[0] + " is not the shared-subject construction shown here.",
                options[1]: options[1] + " is incorrect because the displayed main clause has an explicit subject."},
            tested_feature="VERBAL_ADVERB_SHARED_SUBJECT")

    # Freeze corrected research applications as explicit, pinned alternatives
    # to the faulty legacy spans. Legacy provenance stays available for audit.
    # This intake establishes a checkable chain, not linguistic acceptance.
    capability_checks = {
        "cap-u7-bounded-manner-adverb": ["u7-i-manner-1", "u7-i-manner-2", "u7-i-manner-3", "u7-i-manner-4"],
        "cap-u20-motion-po-setting-out": ["u20-i-motion-3"],
        "cap-u20-motion-prefixed-path": ["u20-i-motion-4"],
        "cap-obj-u27-comprehension": ["u27-i-comp-1", "u27-i-comp-2", "u27-i-comp-3"],
        "cap-obj-u28-imperfective-adverb": ["u28-i-imperfective-adverb-1", "u28-i-imperfective-adverb-2"],
        "cap-obj-u28-perfective-adverb": ["u28-i-perfective-adverb-1", "u28-i-perfective-adverb-2"],
    }
    specifications = []
    for capability in data["sourceCapabilities"]:
        check_ids = capability_checks.get(capability["id"])
        if check_ids is None:
            continue
        unit, objective = pack.unit_of_objective(capability["objectiveId"])
        rows = [pack.items[x] for x in check_ids]
        applications = [{"cue": x["promptLetter"], "answer": x["answer"],
                         "distractors": [o for o in x["options"] if o != x["answer"]]} for x in rows]
        strings = []
        for sid in objective["instructionStepIds"] + objective["remediationStepIds"]:
            strings.append(pack.steps[sid][2]["body"])
        for eid in objective["exampleIds"]:
            ex = pack.examples[eid]
            strings.extend([ex["display"], ex["note"]])
        for item in rows:
            strings.extend([item["promptLetter"], item["answer"], item["prompt"], item["feedback"],
                item["assessment"]["whyAnswerDemonstratesObjective"]] + item["options"] + item["distractorRationales"])
        strings = list(dict.fromkeys(x for x in strings if x))
        note = ("Pending corrected research applications. No linguistic or owner approval is recorded.\n"
            "References: retained original source bindings in this unit; the independent content-audit register; "
            "Gramota dictionary and orthography; Litnevskaya, verbal adverbs; Rusgram, verbal adverbs.\n"
            "https://gramota.ru/poisk?query=быстрый&mode=slovari\n"
            "https://gramota.ru/biblioteka/spravochniki/russkij-yazyk-kratkij-teoreticheskij-kurs-dlya-shkolnikov/deeprichastie\n"
            + "\n".join(strings))
        record_id = "ca-corrected-capability:" + capability["id"]
        binding = capability["objectiveId"] + ":" + capability["supportedClaim"]
        ids = {}
        for text in strings:
            ids[text] = pack.add_provenance(unit=unit["number"], objective=capability["objectiveId"],
                shipped=text, original=note, record_id=record_id, binding=binding,
                synthesis_applications=applications, role="original-synthesis")
        for item in rows:
            a = item["assessment"]
            a["answerSourceIds"] = [ids[item["answer"]]]
            a["distractorSourceIds"] = ["" if x == item["answer"] else ids[x] for x in item["options"]]
            a["relationParticipantIds"] = [ids[item["promptLetter"]], ids[item["answer"]]] + [ids[x] for x in item["options"] if x != item["answer"]]
            a["relationKind"] = a["evidenceMode"] = "STATED_APPLICATION"
            item["sourceId"] = SOURCE_ID
        capability.update(sourceId=SOURCE_ID, sourceRevision="content-audit-2026-10-08",
            sourceRecordId=record_id, sourceHash=__import__("hashlib").sha256(note.encode()).hexdigest(), exactSpan=note)
        specifications.append((capability, {x["assessment"]["testedFeature"] for x in rows}))

    # The runtime whitelist pins exact research-note bytes and feature scope.
    spec_path = PACK_PATH.parents[2] / "java/dev/russiancompanion/v2/curriculum/ContentAuditCapabilityPins.kt"
    specs = []
    for capability, features in specifications:
        quoted = [json.dumps(capability[k]) for k in ("id", "sourceRecordId", "sourceHash", "objectiveId", "supportedClaim")]
        specs.append("        Pin(" + ", ".join(quoted[:3]) + ", " + str(capability["unitNumber"]) + ", "
            + ", ".join(quoted[3:]) + ", setOf(" + ", ".join(json.dumps(x) for x in sorted(features)) + "))")
    spec_path.write_text("""package dev.russiancompanion.v2.curriculum

/** Exact pending corpus intake. This is not a final review or installation approval. */
internal object ContentAuditCapabilityPins {
    data class Pin(val id: String, val recordId: String, val hash: String, val unit: Int,
        val objective: String, val claim: String, val features: Set<String>)
    val all = listOf(
""" + ",\n".join(specs) + "\n    )\n}\n", encoding="utf-8")

    # Declare the real option shape after a rebind, rather than copying stale
    # responseKind from an unrelated former question.
    clause_starts = {"он", "она", "оно", "они", "я", "ты", "вы", "мы", "ему", "ей", "им", "мне", "тебе", "вам", "нам", "когда"}
    for item in data["items"]:
        if item["sourceId"] != SOURCE_ID:
            continue
        answer = item["answer"]
        if not re.search(r"[\u0400-\u04ff]", answer):
            kind = "ENGLISH_CLASSIFICATION"
            if not re.search(r"\b(?:label|english)\b", item["prompt"], re.I):
                item["prompt"] = "Choose the English description. " + item["prompt"]
                pack.cover(pack.unit_of_objective(item["objectiveId"])[0]["number"],
                    item["objectiveId"], item["prompt"], item["id"] + ":prompt")
        else:
            normalized = "".join(c for c in unicodedata.normalize("NFD", answer.lower()) if not unicodedata.combining(c))
            tokens = re.findall(r"[0-9]+|[a-zа-я]+", normalized)
            kind = "RUSSIAN_TOKEN" if len(tokens) <= 1 else (
                "RUSSIAN_CLAUSE" if "," in answer or ";" in answer or tokens[0] in clause_starts else "RUSSIAN_PHRASE")
        item["assessment"]["responseKind"] = kind

    # Register new logistical scaffold exactly. Linguistic text remains in
    # provenance, not in an English-scaffold exemption.
    for unit in data["units"]:
        if unit["number"] < 3:
            continue
        for session in unit["sessions"]:
            for step in session["steps"]:
                if step["kind"] in {"ANKI", "BINDER", "TUTOR", "REVIEW"}:
                    for field in ("title", "body", "companionDirection"):
                        text = step.get(field)
                        if text and not re.search(r"[\u0400-\u04ff]", text):
                            pack.add_authored(unit["number"], text)
                if step["id"] == "u4-st-guided-nominative":
                    for field in ("title", "body", "companionDirection"):
                        text = step.get(field)
                        if text:
                            pack.cover(4, "obj-u4-nominative", text, step["id"] + ":" + field)
                if "cumulative review" in step["title"].lower():
                    pack.add_authored(unit["number"], step["title"])
                for field in ("body", "companionDirection"):
                    if "Return to that subject contrast." in (step.get(field) or ""):
                        pack.cover(unit["number"], "obj-u4-nominative", "Return to that subject contrast.", step["id"] + ":return-direction")

    # Give the repair its own unique IDs, and update every new item binding.
    renamed = {}
    for record in data["provenanceRecords"][before:]:
        old = record["itemId"]
        record["itemId"] = old.replace("prov-ca-", "prov-ca-repair-", 1)
        renamed[old] = record["itemId"]
    for item in data["items"]:
        assessment = item.get("assessment") or {}
        for field in ("answerSourceIds", "distractorSourceIds", "relationParticipantIds"):
            if field in assessment:
                assessment[field] = [renamed.get(x, x) for x in assessment[field]]
    complete_coverage_evidence(data)
    output = PACK_PATH.with_suffix(".json.tmp")
    output.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    output.replace(PACK_PATH)


if __name__ == "__main__":
    main()
