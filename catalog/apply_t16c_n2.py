"""T16c-n2 content changes (owner decisions D37c, D38c, D40c). English only; no Russian is added or changed.

- D37c: every cumulative-review introduction becomes a plain instruction with no counts (the real assignment size depends on
  the learner's history) and no assessment commentary ("guided practice does not complete ...", "does not establish ...").
  catalog/review-copy.json and the packaged reviewCopy stay identical.
- D38c: ref-*-attribution details keep attribution only; the assessment-scope sentences move to the internal dispositions
  file audit/T16c-n2-attribution-dispositions.csv.
- D40c: the Unit 7 binder prompt says "of your table" for the learner's own table.

Exact-baseline: every replaced text must match exactly, so a second run fails instead of drifting.
"""
import csv
import json
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from curriculum_storage import dumps, loads  # noqa: E402

ROOT = pathlib.Path(__file__).resolve().parent.parent
PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
REVIEW_COPY = ROOT / "catalog/review-copy.json"
DISPOSITIONS = ROOT / "audit/T16c-n2-attribution-dispositions.csv"

AGAIN = "After a miss, read the explanations and examples again, then answer the assigned check."
EVERY = "Answer every review check before continuing."

INTRODUCTIONS = {
    3: f"Review reading and recognising printed letters from Unit 1. {EVERY}",
    4: f"Review print-pair decoding from Unit 1 and noun-gender labels from Unit 3. {EVERY} Practise whole-word reading, "
       "speaking and writing separately.",
    5: f"Review noun genders from Unit 3 and nominative subjects in supplied Unit 4 clauses. {EVERY} Practise recognising the "
       "examples here; practise unfamiliar reading, creating sentences, speaking and writing separately.",
    6: "Review supplied present forms for person cues from Unit 4, noun behavior from Unit 5 and subjects in supplied Unit 4 "
       f"clauses. {EVERY} Practise recognising the taught forms; practise conjugating verbs, unfamiliar reading, choosing "
       "motion verbs, speaking and writing separately.",
    7: f"Review noun genders from Unit 3 and the supplied inanimate-object classification from Unit 6. {EVERY} Practise the "
       "taught forms; practise unfamiliar reading, adjective agreement, interpreting animacy in unfamiliar sentences, speaking "
       "and writing separately.",
    8: "Review supplied present forms for person cues from Unit 4 and feminine singular nominative adjective agreement from "
       "Unit 7. After a miss, read the taught forms again, or the shown car phrase and the related feminine object phrases, "
       f"then answer the assigned check. {EVERY} Practise the taught forms; practise unfamiliar reading, conjugation, "
       "inflection, speaking and writing separately.",
    9: "Review the behavior of supplied nouns from Unit 5, the supplied window nominative plural from Unit 8 and noun genders "
       "from Unit 3. After a miss, read the noun-behavior explanations and examples again, then answer the assigned check. "
       f"{EVERY} Practise the taught forms; practise unfamiliar reading, inflection, speaking and writing separately.",
    10: "Review supplied present forms for person cues from Unit 4 and the masculine numeral agreement with the supplied hour "
        "noun from Unit 9. After a miss, read the taught forms or the agreement explanations and examples again, then answer "
        f"the assigned check. {EVERY} Practise the taught forms; practise unfamiliar reading, conjugation, inflection, "
        "speaking and writing separately.",
    11: "Review supplied noun behavior from Unit 5 and possession or absence explanations from Unit 10. After a miss, read the "
        "noun or possession explanations and examples again, then answer the assigned check. "
        f"{EVERY} Practise the taught forms; practise unfamiliar reading, declension, forming new sentences, speaking and "
        "writing separately.",
    12: "Review noun genders from Unit 3 and supplied singular past-gender forms from Unit 11. After a miss, read the "
        "noun-gender or past-gender explanations and examples again, then answer the assigned check. "
        f"{EVERY} Practise the taught forms; practise unfamiliar reading, agreement, choosing aspect, speaking and writing "
        "separately.",
    13: "Review supplied present forms for person cues from Unit 4, saying/telling or doing/making aspect partners from Unit 12 "
        "and singular past-gender forms from Unit 11. After a miss, read the taught form sets again, then answer the assigned "
        f"check. {EVERY} Practise the taught forms; practise unfamiliar reading, conjugation, choosing aspect in sentences, "
        "speaking and writing separately.",
    14: "Review supplied nominative plurals from Unit 8, possession or absence interpretations from Unit 10 and the supplied "
        "evening report from Unit 13. After a miss, read the plural, possession or report explanations and examples again, "
        f"then answer the assigned check. {EVERY} Practise the taught forms; practise unfamiliar reading, inflection, creating "
        "reported speech, speaking and writing separately.",
    15: f"Review earlier printed letter pairs from Unit 1 and the supplied female-cold state from Unit 14. {AGAIN} {EVERY} "
        "Practise recognising the examples here; practise unfamiliar reading, creating sentences, speaking and writing "
        "separately.",
    16: f"Review earlier printed letter pairs from Unit 1 and the supplied singular imperative form from Unit 15. {AGAIN} "
        f"{EVERY} Practise these forms; practise unfamiliar reading, commands, interpreting addressee count, speaking and "
        "writing separately.",
    17: f"Review earlier printed letter pairs from Unit 1 and supplied instrumental plural noun forms from Unit 16. {AGAIN} "
        f"{EVERY} Practise these forms; practise unfamiliar reading, noun inflection, sentence production, speaking and "
        "writing separately.",
    18: f"Review earlier printed letter pairs from Unit 1 and the supplied book-selection question from Unit 17. {AGAIN} "
        f"{EVERY} Practise these question meanings; practise unfamiliar reading, forming new questions, giving clock times, "
        "speaking and writing separately.",
    19: "Review earlier printed letter pairs from Unit 1 and the supplied woman-and-friendship relative clause from Unit 18. "
        f"{AGAIN} {EVERY} Practise these meanings; practise unfamiliar reading, relative-case choice, sentence production, "
        "speaking and writing separately.",
    20: "Review earlier printed letter pairs from Unit 1 and the supplied conditional example with feminine agreement from "
        f"Unit 19. {AGAIN} {EVERY} Practise unfamiliar reading, forming new conditionals, aspect choices, speaking and "
        "writing separately.",
    21: f"Review earlier printed letter pairs from Unit 1 and the supplied city-movement context from Unit 20. {AGAIN} "
        f"{EVERY} Practise unfamiliar reading, other motion verbs, speaking and writing separately.",
    22: f"Review earlier printed letter pairs from Unit 1 and the supplied reflexive direct-object sentence from Unit 21. "
        f"{AGAIN} {EVERY} Practise unfamiliar reading, other pronoun cases, speaking and writing separately.",
    23: f"Review earlier printed letter pairs from Unit 1 and the supplied quantity phrases from Unit 22. {AGAIN} {EVERY} "
        "Practise unfamiliar reading, numeral declension, speaking and writing separately.",
    24: f"Review earlier printed letter pairs from Unit 1 and the supplied time expressions from Unit 23. {AGAIN} {EVERY} "
        "Practise unfamiliar reading, forming time expressions, speaking and writing separately.",
    25: f"Review earlier printed letter pairs from Unit 1 and the supplied negative sentences from Unit 24. {AGAIN} {EVERY} "
        "Practise unfamiliar reading, forming negative sentences, speaking and writing separately.",
    26: f"Review earlier printed letter pairs from Unit 1 and the supplied conversation formulas from Unit 25. {AGAIN} "
        f"{EVERY} Practise unfamiliar reading, introducing yourself, speaking and writing separately.",
    27: f"Review earlier printed letter pairs from Unit 1 and the supplied address components from Unit 26. {AGAIN} {EVERY} "
        "Practise unfamiliar reading, addressing mail, speaking and writing separately.",
    28: f"Review earlier printed letter pairs from Unit 1 and the supplied past passive forms from Unit 27. {AGAIN} {EVERY} "
        "Practise unfamiliar reading, forming participles, speaking and writing separately.",
    29: f"Review earlier printed letter pairs from Unit 1 and supplied verbal-adverb contexts from Unit 28. {AGAIN} {EVERY} "
        "Practise unfamiliar reading, forming verbal adverbs, speaking and writing separately.",
    30: f"Review earlier printed letter pairs from Unit 1 and supplied active participle forms from Unit 29. {AGAIN} Answer "
        "every review check before recording Unit 30 completion. Practise unfamiliar reading, forming participles, speaking "
        "and writing separately.",
}

# (reference id, exact old sentence or span, replacement ("" removes it), disposition note)
ATTRIBUTION = [
    ("ref-u7-ruwiki-attribution", " They do not state a universal adverb paradigm.", "",
     "Assessment scope: the two pairs are bounded examples, not a universal adverb paradigm."),
    ("ref-u17-attribution", " The three clock phrases are bounded recognition.", "",
     "Assessment scope: the three Wikipedia clock phrases are bounded recognition."),
    ("ref-u17-attribution",
     " Productive clock-time is the ReadyRussian Grammar 20 system (Grammar 20 — Telling Time, ReadyRussian.org, CC BY-SA 4.0)"
     " applied to exact expressions for minutes into the hour, minutes remaining after the half hour, and dial reading.",
     " Clock-time practice uses ReadyRussian Grammar 20 — Telling Time (ReadyRussian.org, CC BY-SA 4.0).",
     "Assessment scope: productive clock-time is the ReadyRussian Grammar 20 system applied to exact expressions for minutes"
     " into the hour, minutes remaining after the half hour, and dial reading."),
    ("ref-u2125-attribution",
     " Russian Wikipedia personal-name page oldid 154915954 is retained for bounded classification of the documented full-form"
     " and short-form diminutive bases, the dismissive-suffix limitation, and the lowered-status note, under the Russian"
     " Wikipedia copyrights page oldid 154562152.",
     " The documented full-form and short-form diminutive bases, the dismissive-suffix limitation, and the lowered-status note"
     " come from Russian Wikipedia personal-name page oldid 154915954, under the Russian Wikipedia copyrights page oldid"
     " 154562152.",
     "Assessment scope: the personal-name page is used for bounded classification only."),
    ("ref-u2125-attribution",
     " A separate application chain on that same span uses only names already in the span. It is not a name generator.", "",
     "Internal: a separate application chain on that span uses only names already in the span; it is not a name generator."),
    ("ref-u2125-attribution",
     " The Unit 24 word-order pair is bounded sentence recognition on English Wikipedia Russian grammar oldid 1369617627. It does"
     " not clear scope-sensitive placement. Scope-sensitive placement is the ReadyRussian Language Generator 3 statement that the"
     " modifier negates the following word, and that page's two-sentence contrast, under the ReadyRussian CC BY-SA 4.0 route.",
     " The Unit 24 word-order pair comes from English Wikipedia Russian grammar oldid 1369617627. The statement that the"
     " modifier negates the following word, and that page's two-sentence contrast, come from ReadyRussian Language Generator 3"
     " under the ReadyRussian CC BY-SA 4.0 route.",
     "Assessment scope: the Unit 24 word-order pair is bounded sentence recognition and does not clear scope-sensitive"
     " placement; scope-sensitive placement is the ReadyRussian Language Generator 3 statement and contrast."),
    ("ref-u2125-attribution",
     " Practice for completion over a period covers the bounded examples here, not every temporal clause.", "",
     "Assessment scope: practice for completion over a period covers the bounded examples, not every temporal clause."),
    ("ref-u2630-attribution",
     " Complete postal mastery, general participle-stress rules, universal register judgments, and broad dictionary proficiency"
     " are not supplied by these bounded tasks.", "",
     "Assessment scope: complete postal mastery, general participle-stress rules, universal register judgments and broad"
     " dictionary proficiency are not supplied by these bounded tasks."),
    ("ref-u2630-attribution", " The abbreviation contrast and the particle contrast do not activate an authority variance.", "",
     "Internal: the abbreviation contrast and the particle contrast do not activate an authority variance."),
    ("ref-u2630-attribution",
     " Bounded postal practice, past-passive stress, and a two-entry dictionary task are implemented and review-pending. They are"
     " not complete postal mastery, a general stress rule, or broad dictionary proficiency. Present-passive exploration does not"
     " gate Unit 27.", "",
     "Assessment scope and workflow: bounded postal practice, past-passive stress and a two-entry dictionary task are"
     " implemented and review-pending; they are not complete postal mastery, a general stress rule or broad dictionary"
     " proficiency; present-passive exploration does not gate Unit 27."),
]

BINDER_U7 = ("Write the nominative form in each row from this unit,",
             "Write the nominative form from this unit in each row of your table,")


def main() -> None:
    pack = loads(PACK.read_text(encoding="utf-8"))
    copy = json.loads(REVIEW_COPY.read_text(encoding="utf-8"))
    assert copy == pack["reviewCopy"], "catalog/review-copy.json and the packaged copy differ before the change"
    changes = []
    for unit, text in INTRODUCTIONS.items():
        key = str(unit)
        old = copy["introductions"][key]
        assert old != text, f"introduction {unit} already rewritten"
        copy["introductions"][key] = text
        changes.append((f"reviewCopy.introductions.{unit}", old, text))
    pack["reviewCopy"] = copy

    refs = {r["id"]: r for r in pack["references"]}
    rows = []
    for ref_id, old, new, note in ATTRIBUTION:
        detail = refs[ref_id]["detail"]
        assert detail.count(old) == 1, (ref_id, old)
        refs[ref_id]["detail"] = detail.replace(old, new)
        rows.append({"owner": ref_id, "field": "detail", "removed_text": old.strip(), "replacement": new.strip(),
                     "disposition": "MOVED_TO_INTERNAL_NOTES", "note": note, "decision": "D38c", "task": "T16c-n2"})
    for ref_id in sorted({r[0] for r in ATTRIBUTION}):
        changes.append((f"{ref_id}.detail", "(see dispositions)", refs[ref_id]["detail"]))

    unit7 = next(u for u in pack["units"] if u["number"] == 7)
    prompt = unit7["binderTask"]["prompt"]
    assert prompt.count(BINDER_U7[0]) == 1
    unit7["binderTask"]["prompt"] = prompt.replace(*BINDER_U7)
    changes.append(("binder-u7-adjectives.prompt", prompt, unit7["binderTask"]["prompt"]))

    REVIEW_COPY.write_text(json.dumps(copy, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    PACK.write_text(dumps(pack), encoding="utf-8")
    with DISPOSITIONS.open("w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=list(rows[0].keys()))
        writer.writeheader()
        writer.writerows(rows)
    for owner, old, new in changes:
        print(owner)
        print("  OLD:", old)
        print("  NEW:", new)


if __name__ == "__main__":
    main()
