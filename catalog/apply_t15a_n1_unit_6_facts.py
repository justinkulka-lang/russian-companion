#!/usr/bin/env python3
"""T15a-n1 (Claude Code, owner direction 11/14/2026): Unit 6 russian-fact markers cite frozen sources.

Exact-baseline transformer. Every marked Unit 6 sentence names, in russianFactSources, the provenance records of a
frozen file under catalog/sources/ that state it: OpenRussian row cells (catalog/sources/units-3-5/openrussian/nouns.csv,
catalog/sources/units-6-10/openrussian/adjectives.csv, commit 50e210c4) or spans of the frozen Wikibooks page
catalog/sources/units-6-10/wikibooks/accusative-4330404.txt. New support records are cut from those frozen files only;
no source is added. Claims no frozen file states are reworded or removed. The 19 coverage records that cited an
unfrozen external PDF now cite the frozen records instead. ref-ca-cases quotes the frozen agreement sentence.
"""
from __future__ import annotations

import copy
import csv
import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "b9355fed5c32773c5101e0274bab17c5edb25e29f0c0c8a9389481890523c24f"
FROZEN = {"nouns.csv": ROOT / "catalog/sources/units-3-5/openrussian/nouns.csv",
          "adjectives.csv": ROOT / "catalog/sources/units-6-10/openrussian/adjectives.csv"}
WIKIBOOKS = ROOT / "catalog/sources/units-6-10/wikibooks/accusative-4330404.txt"
TASK_NOTE = "T15a-n1, Claude Code, 2026-11-14"

WIKI_SPANS = {
    "W4": "As mentioned above, the primary use of the accusative case is to denote the object of a verb.",
    "W6": "Since Russians don't have to rely as much on word order to convey grammatical information, they can move words around more freely than in English.",
    "W35": "Feminine words are the only words to have their own accusative ending: '''-а''' becomes '''-у''', and '''-я''' becomes '''-ю'''.",
    "W57": "Adjectives change to fit the noun they modify, taking on their noun's gender, number, and case.",
    "W103": "| It || Оно || Его",
    "W108": "Notice that the third-person singular masculine and neuter pronouns are the same word",
    "W111": "Possessive pronouns act like adjectives, changing to suit the noun they modify, and thus, in the accusative case, make a distinction between animation.",
    "W132": "I want his cat - '''Я хочу его кота'''",
}
# (key, objective, file, bare, field)
ROW_CELLS = [
    ("stol-gender", "obj-u6-possessive", "nouns.csv", "стол", "gender"),
    ("stol-animate", "obj-u6-animacy", "nouns.csv", "стол", "animate"),
    ("sobaka-gender", "obj-u6-possessive", "nouns.csv", "собака", "gender"),
    ("sobaka-sg-nom", "obj-u6-possessive", "nouns.csv", "собака", "sg_nom"),
    ("sestra-gender", "obj-u6-possessive", "nouns.csv", "сестра", "gender"),
    ("sestra-sg-nom", "obj-u6-possessive", "nouns.csv", "сестра", "sg_nom"),
    ("sestra-sg-acc", "obj-u6-possessive", "nouns.csv", "сестра", "sg_acc"),
    ("brat-gender", "obj-u6-animacy", "nouns.csv", "брат", "gender"),
    ("brat-animate", "obj-u6-animacy", "nouns.csv", "брат", "animate"),
    ("brat-sg-gen", "obj-u6-animacy", "nouns.csv", "брат", "sg_gen"),
    ("uchitel-gender", "obj-u6-animacy", "nouns.csv", "учитель", "gender"),
    ("uchitel-animate", "obj-u6-animacy", "nouns.csv", "учитель", "animate"),
    ("uchitel-sg-gen", "obj-u6-animacy", "nouns.csv", "учитель", "sg_gen"),
    ("drug-gender", "obj-u6-animacy", "nouns.csv", "друг", "gender"),
    ("drug-animate", "obj-u6-animacy", "nouns.csv", "друг", "animate"),
    ("drug-sg-gen", "obj-u6-animacy", "nouns.csv", "друг", "sg_gen"),
    ("dom-gender", "obj-u6-animacy", "nouns.csv", "дом", "gender"),
    ("dom-animate", "obj-u6-animacy", "nouns.csv", "дом", "animate"),
    ("stul-gender", "obj-u6-animacy", "nouns.csv", "стул", "gender"),
    ("stul-animate", "obj-u6-animacy", "nouns.csv", "стул", "animate"),
    ("stul-sg-nom", "obj-u6-animacy", "nouns.csv", "стул", "sg_nom"),
    ("stul-sg-acc", "obj-u6-animacy", "nouns.csv", "стул", "sg_acc"),
    ("papa-gender", "obj-u6-feminine", "nouns.csv", "папа", "gender"),
    ("papa-sg-acc", "obj-u6-feminine", "nouns.csv", "папа", "sg_acc"),
    ("dyadya-sg-acc", "obj-u6-feminine", "nouns.csv", "дядя", "sg_acc"),
]
WIKI_OBJECTIVE = {"W4": "obj-u6-animacy", "W6": "obj-u6-object", "W35": "obj-u6-feminine", "W57": "obj-u6-possessive",
                  "W103": "obj-u6-pronoun", "W108": "obj-u6-pronoun", "W111": "obj-u6-possessive", "W132": "obj-u6-third"}

# Existing frozen records (OpenRussian rows and Wikibooks spans already shipped in Unit 6).
P = lambda n: f"prov-ex-or-u6-possessive-{n}"  # noqa: E731
A = lambda n: f"prov-ex-or-u6-animacy-{n}"  # noqa: E731
F = lambda n: f"prov-ex-or-u6-feminine-{n}"  # noqa: E731

# Marked sentence -> frozen support (keys of ROW_CELLS / WIKI_SPANS are prefixed "new:"), or per-owner overrides.
SUPPORT: dict[str, object] = {
    # possessive remediation (teaching repeated)
    "Possessives such as мо́й and тво́й agree with the noun they describe in gender, number and case.": ["new:W57", "new:W111", P(4), P(8)],
    "Choose the form from the noun, not from the gender of its owner.": ["new:W111"],
    "In мой стол, стол is masculine singular.": ["prov-ex-u6-possessive-0-ru", "new:stol-gender", A(0)],
    "In моя собака, собака is feminine singular.": ["prov-ex-u6-possessive-1-ru", "new:sobaka-gender", "new:sobaka-sg-nom"],
    "Both phrases mean my plus a noun, but the Russian form of my changes to match the noun.": ["prov-u6-possessive-rem", "prov-ex-u6-possessive-0-en", "prov-ex-u6-possessive-1-en"],
    "In the nominative singular, мо́й is masculine and моя́ is feminine.": [P(4), P(5)],
    "For стол choose мо́й; for собака choose моя́.": ["new:stol-gender", "new:sobaka-gender", P(4), P(5)],
    "The form follows the noun being described.": ["new:W111"],
    "моё is the neuter singular nominative form of my.": [P(6), P("6-gloss")],
    "мои́ is the plural nominative form.": [P(7)],
    "A singular noun does not take the plural form in these choices.": ["new:W57"],
    "Наш means our; Ваш means your (formal or plural).": ["prov-ex-u6-possessive-2-en", "prov-ex-u6-possessive-3-en"],
    "To put this phrase in the direct-object accusative, change both words: на́ша сестра́ becomes на́шу сестру́.": ["new:sestra-sg-nom", "new:sestra-sg-acc", "prov-u6-possessive-x-2"],
    "Keep the same noun and feminine singular agreement.": ["new:W57"],
    "In the worked phrase на́ша сестра́, сестра́ is a feminine singular noun.": ["new:sestra-gender", "new:sestra-sg-nom"],
    "на́шего бра́та changes the referent, and на́ши is plural.": ["prov-ex-u6-possessive-2-ru", "prov-u6-possessive-x-3", A("4-gloss")],
    "тво́й is masculine singular nominative; твоя́ is feminine singular nominative.": [P(8), P(9)],
    "Both mean your.": [P("8-gloss"), P("9-gloss")],
    "To change the masculine form to the feminine form while keeping nominative singular, choose твоя́.": [P(9)],
    "собака is feminine singular, so the nominative form is моя́.": ["new:sobaka-gender", P(5), "new:W57"],
    "мо́й is masculine singular nominative; it does not agree with the feminine singular noun.": [P(4), "new:W57"],
    "мои́ is plural nominative; the noun here is singular.": [P(7), "new:sobaka-sg-nom"],
    "стол is masculine singular, so the nominative form is мо́й.": ["new:stol-gender", P(4), "new:W57"],
    "моя́ is feminine singular nominative; it does not agree with the masculine singular noun.": [P(5), "new:W57"],
    "на́ша сестра́ takes the feminine object-row phrase на́шу сестру́.": ["new:sestra-sg-acc", "prov-u6-possessive-x-2"],
    "на́шего бра́та is a masculine animate object phrase, not the feminine phrase.": ["prov-ex-u6-possessive-2-ru", "new:W111", "new:brat-animate", A(5)],
    "на́ши is plural and does not mark the feminine singular object row.": ["prov-u6-possessive-x-3"],
    "тво́й is the masculine singular nominative form; твоя́ is the feminine singular nominative form of the same adjective.": [P(8), P(9)],
    "тво́й is the masculine singular nominative form, so it does not supply the requested feminine singular nominative form.": [P(8)],
    "This is the plural nominative form instead, so it does not agree with a feminine singular noun.": ["prov-t15a-u6-tvoy-pl-nom", "new:W57"],
    # third person
    "его means his or its: the category refers to a masculine or neuter singular possessor.": ["prov-ex-u6-third-0-ru", "new:W132"],
    "её refers to a feminine singular possessor, and их to plural possessors.": ["prov-ex-u6-third-1-ru", "prov-ex-u6-third-2-ru"],
    "These third-person possessive forms do not change with the gender or number of the possessed noun.": ["prov-u6-third-rem"],
    "её means her: the category refers to a feminine singular possessor.": ["prov-ex-u6-third-1-ru", "prov-ex-u6-pronoun-5-en"],
    "его means his or its, and их means their.": ["prov-ex-u6-third-0-ru", "new:W132", "prov-ex-u6-third-2-ru"],
    "их means their: the category refers to plural possessors.": ["prov-ex-u6-third-2-ru"],
    "его and её refer to singular possessors.": ["prov-ex-u6-third-0-ru", "prov-ex-u6-third-1-ru"],
    "её identifies her, a feminine singular possessor instead of the shown possessor category.": ["prov-ex-u6-third-1-ru", "prov-ex-u6-pronoun-5-en"],
    "их identifies their, plural possessors instead of the shown possessor category.": ["prov-ex-u6-third-2-ru"],
    "его identifies his or its, a masculine or neuter singular possessor instead of the shown possessor category.": ["prov-ex-u6-third-0-ru", "new:W132"],
    "These do not decline according to the gender or number of the modified noun.": ["prov-u6-third-rem"],
    # object
    "Я тебя люблю places the object before the verb.": ["prov-ex-u6-object-1-ru", "prov-ex-u6-pronoun-2-ru"],
    "Я люблю тебя places it after the verb.": ["prov-ex-u6-object-2-ru", "prov-ex-u6-pronoun-2-ru"],
    "Both have the same meaning, I love you: the subject and object keep their roles and forms.": ["prov-ex-u6-object-1-en", "prov-ex-u6-object-2-en", "new:W6"],
    "In this sentence the dogs are the subject and the cats are the accusative object, so it means dogs hate cats.": ["prov-ex-u6-object-3-ru", "prov-ex-u6-object-3-en"],
    "The other option reverses the subject and object roles.": ["prov-ex-u6-object-3-en", "prov-ex-u6-object-4-en"],
    "In this sentence the cats are the subject and the dogs are the accusative object, so it means cats hate dogs.": ["prov-ex-u6-object-4-ru", "prov-ex-u6-object-4-en"],
    "This option repeats the shown order instead of moving the object before or after the verb.": ["prov-ex-u6-object-1-ru", "prov-ex-u6-object-2-ru"],
    "This option reverses the subject and object roles instead of keeping who hates whom.": ["prov-ex-u6-object-3-en", "prov-ex-u6-object-4-en"],
    "I like dogs - Я люблю собак": ["prov-u6-object-rem"],
    "Я люблю тебя places the object after the verb.": ["prov-ex-u6-object-2-ru", "prov-ex-u6-pronoun-2-ru"],
    "Both orders have the same meaning, I love you: the subject and object keep their roles.": ["prov-ex-u6-object-1-en", "prov-ex-u6-object-2-en", "new:W6"],
    # feminine
    "Nouns in the а or я declension, including the masculine noun папа, have their own accusative singular ending.": ["new:W35", "new:papa-gender", "new:papa-sg-acc", "new:dyadya-sg-acc"],
    "Feminine gender is not the only group with that ending.": ["new:papa-gender", "new:papa-sg-acc"],
    "кни́га is the singular nominative form; кни́гу is the singular accusative form of the same noun.": [F(0), F(1)],
    "кни́га is the singular nominative form, so it does not supply the requested singular accusative form.": [F(0)],
    "ма́ма is the singular nominative form; ма́му is the singular accusative form of the same noun.": [F(2), F(3)],
    "ма́ма is the singular nominative form, so it does not supply the requested singular accusative form.": [F(2)],
    "This is the singular genitive form instead, so it does not supply the requested singular accusative form.": ["prov-t15a-u6-mama-sg-gen"],
    # animacy
    "брат is the singular nominative form; бра́та is the singular accusative form of the same noun.": [A(4), A(5)],
    "For this masculine animate noun, the singular accusative has the same form as the genitive.": {
        "u6-g-animacy-0": ["new:brat-gender", "new:brat-animate", "new:brat-sg-gen", A(5), "prov-u6-animacy-rem"],
        "u6-i-animacy-1": ["new:uchitel-gender", "new:uchitel-animate", "new:uchitel-sg-gen", A(7), "prov-u6-animacy-rem"],
        "u6-i-animacy-2": ["new:drug-gender", "new:drug-animate", "new:drug-sg-gen", A(9), "prov-u6-animacy-rem"]},
    "сто́л is the supplied masculine inanimate table form.": [A(0), "new:stol-gender", "new:stol-animate", A("0-gloss")],
    "Its singular nominative and accusative have the same spelling and stress.": [A(0), A(1)],
    "A direct object can be accusative even when its form matches nominative.": ["prov-u6-animacy-x-0", "new:W4"],
    "In these supplied singular masculine examples, an inanimate accusative matches nominative; an animate accusative matches genitive.": ["prov-u6-animacy-x-0", "prov-u6-animacy-rem"],
    "Matching a genitive form does not change the accusative syntactic role.": ["prov-u6-animacy-rem", "new:W4"],
    "до́м is the supplied masculine inanimate house form.": [A(2), "new:dom-gender", "new:dom-animate", A("2-gloss")],
    "Its singular accusative matches its singular nominative.": [A(2), A(3)],
    "брат is singular nominative and бра́та is the supplied singular accusative.": [A(4), A(5)],
    "Brother is masculine animate; this accusative is genitive-shaped.": ["new:brat-gender", "new:brat-animate", "new:brat-sg-gen", A(5), "prov-u6-animacy-rem", A("4-gloss")],
    "учи́тель is singular nominative and учи́теля is the supplied singular accusative.": [A(6), A(7)],
    "Teacher is masculine animate; its singular accusative has the same form as its singular genitive.": ["new:uchitel-gender", "new:uchitel-animate", "new:uchitel-sg-gen", A(7), A("6-gloss")],
    "друг is singular nominative and дру́га is the supplied masculine animate singular accusative.": [A(8), A(9), "new:drug-gender", "new:drug-animate"],
    "The clause Я де́лал стул.": ["prov-u6-animacy-transfer-native-chair"],
    "instead contains a masculine inanimate chair as direct object: its accusative matches nominative.": ["prov-u6-animacy-transfer-native-chair", "new:stul-gender", "new:stul-animate", "new:stul-sg-nom", "new:stul-sg-acc"],
    "учи́тель is the singular nominative form; учи́теля is the singular accusative form of the same noun.": [A(6), A(7)],
    "друг is the singular nominative form; дру́га is the singular accusative form of the same noun.": [A(8), A(9)],
    "In the supplied chair clause, the direct object is masculine singular and inanimate.": ["prov-u6-animacy-transfer-native-chair", "new:stul-gender", "new:stul-animate"],
    "The accusative role does not require a different spelling here.": ["prov-u6-animacy-x-0", "new:stul-sg-nom", "new:stul-sg-acc"],
    "The teacher and friend examples are animate and instead have genitive-shaped singular accusatives.": ["new:uchitel-animate", "new:uchitel-sg-gen", A(7), "new:drug-animate", "new:drug-sg-gen", A(9), "prov-u6-animacy-rem"],
    "Form identity does not turn the chair into a subject or change this into a genitive case.": ["prov-u6-animacy-transfer-native-chair", "new:W4"],
    "брат is the singular nominative form, so it does not supply the requested singular accusative form.": [A(4)],
    "учи́тель is the singular nominative form, so it does not supply the requested singular accusative form.": [A(6)],
    "друг is the singular nominative form, so it does not supply the requested singular accusative form.": [A(8)],
    "This is the singular dative form instead, so it does not supply the requested singular accusative form.": {
        "u6-i-animacy-1": ["prov-t15a-u6-uchitel-sg-dat"], "u6-i-animacy-2": ["prov-t15a-u6-drug-sg-dat"]},
    "The masculine inanimate direct object keeps the nominative form.": ["prov-u6-animacy-x-0"],
    "The genitive-shaped singular accusative applies to the supplied masculine animate teacher and friend, not this inanimate chair.": ["new:uchitel-animate", "new:drug-animate", "new:stul-animate", "prov-u6-animacy-rem", "prov-u6-animacy-x-0"],
    # pronoun
    "Меня is the accusative object form for Я, I/me: first person singular.": ["prov-ex-u6-pronoun-0-ru", "prov-ex-u6-pronoun-0-en"],
    "Нас is the form for we/us instead, first person plural.": ["prov-ex-u6-pronoun-1-ru", "prov-ex-u6-pronoun-1-en"],
    "Match the same person and number across the nominative and accusative table columns.": ["prov-ex-u6-pronoun-0-ru", "prov-ex-u6-pronoun-1-ru"],
    "Её is the accusative object form for Она, she/her: third person singular feminine.": ["prov-ex-u6-pronoun-5-ru", "prov-ex-u6-pronoun-5-en"],
    "Его is the form for he/him or it instead.": ["prov-ex-u6-pronoun-4-ru", "prov-ex-u6-pronoun-4-en", "new:W103"],
    "Match the same person, number and gender across the nominative and accusative table columns.": ["prov-ex-u6-pronoun-5-ru", "prov-ex-u6-pronoun-4-ru"],
    "Нас identifies we/us, first person plural instead of the person, number or gender in the shown nominative form.": ["prov-ex-u6-pronoun-1-ru", "prov-ex-u6-pronoun-1-en"],
    "Его identifies he/him or it, third person singular masculine or neuter instead of the person, number or gender in the shown nominative form.": ["prov-ex-u6-pronoun-4-ru", "prov-ex-u6-pronoun-4-en", "new:W103", "new:W108"],
    "| I/me || Я || Меня": ["prov-u6-pronoun-rem"],
    "Нас is the accusative object form for Мы, we/us: first person plural.": ["prov-ex-u6-pronoun-1-ru", "prov-ex-u6-pronoun-1-en", "prov-u6-pronoun-x-1"],
    "Их is the accusative object form for Они, they/them: third person plural.": ["prov-ex-u6-pronoun-6-ru", "prov-ex-u6-pronoun-6-en", "prov-u6-pronoun-x-3"],
    # direction
    "Я шёл в дом describes walking into the house, a destination.": ["prov-ex-u6-direction-0-ru", "prov-ex-u6-direction-0-en"],
    "Here the preposition takes the accusative, so the function is direction.": {
        "u6-g-direction-0": ["prov-ex-u6-direction-0-ru", "prov-u6-direction-rem"],
        "u6-i-direction-2": ["prov-ex-u6-direction-2-ru", "prov-u6-direction-rem"]},
    "Location instead describes being at a place; The same preposition with the prepositional case can express that different function.": ["prov-u6-direction-x-1", "prov-ex-u6-direction-1-ru"],
    "Я жил в Лондоне describes living in London, a location.": ["prov-ex-u6-direction-1-ru", "prov-ex-u6-direction-1-en"],
    "Here the preposition takes the prepositional case.": {
        "u6-i-direction-1": ["prov-ex-u6-direction-1-ru", "prov-u6-direction-x-1"],
        "u6-i-direction-3": ["prov-ex-u6-direction-3-ru", "prov-u6-direction-x-1"]},
    "Direction instead describes a destination; The same preposition with the accusative can express that different function.": ["prov-ex-u6-direction-0-ru", "prov-u6-direction-rem"],
    "Я шёл на улицу describes walking onto the street, a destination.": ["prov-ex-u6-direction-2-ru", "prov-ex-u6-direction-2-en"],
    "Оно на столе describes being on the table, a location.": ["prov-ex-u6-direction-3-ru", "prov-ex-u6-direction-3-en"],
    "Location describes being at a place instead of moving toward a destination in this example.": ["prov-u6-direction-x-1", "prov-u6-direction-x-0"],
    "Direction describes moving toward a destination instead of being at a place in this example.": ["prov-u6-direction-x-0", "prov-u6-direction-x-1"],
    "generally the accusative case denotes direction": ["prov-u6-direction-rem"],
}

REWORD = {  # owner -> [(old sentence, new sentence)]; teaching bodies that repeat the sentence change with it
    "u6-st-rem-possessive": [("Наш means our; Ваш means your.", "Наш means our; Ваш means your (formal or plural)."),
                             ("In the worked phrase на́ша сестра́, both words are feminine singular nominative.", "In the worked phrase на́ша сестра́, сестра́ is a feminine singular noun.")],
    "u6-st-teach-possessive-3": [("Наш means our; Ваш means your.", "Наш means our; Ваш means your (formal or plural)."),
                                 ("In the worked phrase на́ша сестра́, both words are feminine singular nominative.", "In the worked phrase на́ша сестра́, сестра́ is a feminine singular noun.")],
    "u6-st-rem-feminine": [("Nouns in the а or я declension, including masculine nouns such as папа and дядя, have their own accusative singular ending.",
                            "Nouns in the а or я declension, including the masculine noun папа, have their own accusative singular ending.")],
    "u6-st-teach-feminine-2": [("Nouns in the а or я declension, including masculine nouns such as папа and дядя, have their own accusative singular ending.",
                                "Nouns in the а or я declension, including the masculine noun папа, have their own accusative singular ending.")],
}
REMOVE = {  # owner -> sentences no frozen file states (removed from feedback/remediation; teaching unchanged)
    "u6-g-animacy-0": ["The nominative names the subject."],
    "u6-i-animacy-1": ["The nominative names the subject."],
    "u6-i-animacy-2": ["The nominative names the subject."],
    "u6-st-rem-animacy": [
        "Dictionary English notes name a sense; they are not complete sentence translations.",
        "This is bounded recognition of supplied forms, not a rule that every animate noun in every gender or number takes an ending you can generate.",
        "This spelling identity does not by itself tell you a word’s sentence role; use the requested case or supplied context.",
        "Do not treat form syncretism as two different semantic case roles being interchangeable.",
        "The imperfective verb does not establish completed action; no aspect or full-translation claim is tested.",
    ],
}
REMOVED_RECORDS = {"prov-u6-current-fact-030", "prov-u6-current-fact-034", "prov-u6-current-fact-037",
                   "prov-u6-current-fact-040", "prov-u6-current-fact-045", "prov-u6-current-fact-049"}
TYPED_FEEDBACK = {
    "u6-i-type-object-order": ["Я люблю тебя places the object after the verb.",
                               "Both orders have the same meaning, I love you: the subject and object keep their roles."],
    "u6-i-type-pronoun-my": ["Нас is the accusative object form for Мы, we/us: first person plural."],
    "u6-i-type-pronoun-oni": ["Их is the accusative object form for Они, they/them: third person plural."],
}
STRESS_NOTE = "Stress marks are optional when you type."


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def frozen_cell(file: str, bare: str, field: str) -> str:
    with FROZEN[file].open(encoding="utf-8") as handle:
        for row in csv.DictReader(handle, delimiter="\t", quoting=csv.QUOTE_NONE):
            if row["bare"] == bare:
                value = row[field].strip()
                if not value or "," in value:
                    raise SystemExit(f"Frozen {bare}#{field} is not a single value: {value!r}")
                return value
    raise SystemExit(f"No frozen row {bare}")


def drop_sentence(text: str, sentence: str) -> str:
    if text.count(sentence) != 1:
        raise SystemExit(f"Sentence not found exactly once: {sentence}")
    out = text.replace(" " + sentence, "", 1) if (" " + sentence) in text else text.replace(sentence + " ", "", 1)
    if sentence in out:
        out = out.replace(sentence, "", 1)
    return out


def apply(pack: dict) -> dict:
    unit = next(u for u in pack["units"] if u["number"] == 6)
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    items = {i["id"]: i for i in pack["items"]}
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    new: dict[str, dict] = {}
    templates = {"nouns.csv": records["prov-ex-or-u6-animacy-0"], "adjectives.csv": records["prov-ex-or-u6-possessive-4"]}
    for key, objective, file, bare, field in ROW_CELLS:
        record = copy.deepcopy(templates[file])
        original = frozen_cell(file, bare, field)
        shipped = original.replace("'", "́")
        record.update({"itemId": f"prov-t15an1-u6-{key}", "objectiveId": objective, "sourceRecordId": f"{file}:{bare}#{field}",
                       "recordType": "lexical record", "originalSourceText": original, "shippedText": shipped,
                       "sourceHash": sha(original), "contentHash": sha(shipped)})
        new[key] = record
    frozen_page = WIKIBOOKS.read_text(encoding="utf-8")
    wiki_template = records["prov-u6-third-rem"]
    for key, span in WIKI_SPANS.items():
        if frozen_page.count(span) != 1:
            raise SystemExit(f"Wikibooks span {key} is not exactly once in the frozen page")
        record = copy.deepcopy(wiki_template)
        record.update({"itemId": f"prov-t15an1-u6-{key.lower()}", "objectiveId": WIKI_OBJECTIVE[key],
                       "sourceRecordId": "accusative:" + span, "originalSourceText": span, "shippedText": span,
                       "sourceHash": sha(span), "contentHash": sha(span)})
        new[key] = record

    def resolve(ids: list[str]) -> list[str]:
        out = []
        for i in ids:
            out.append(new[i[4:]]["itemId"] if i.startswith("new:") else i)
            if not i.startswith("new:") and i not in records:
                raise SystemExit(f"Unknown support record {i}")
        return out

    coverage_template = records["prov-u6-current-fact-012"]

    def coverage(objective: str, key: str, text: str) -> dict:
        record = copy.deepcopy(coverage_template)
        synthesis = record["synthesis"]
        note = (f"New English wording authored on 2026-11-14 ({TASK_NOTE}); it did not exist before that date. It covers the"
                " wording only; the Russian facts it states are cited from frozen source records in the marker's"
                " russianFactSources. 2026-10-08 is the date of the reused research context only.")
        synthesis.update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                          "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        record.update({"itemId": f"prov-t15an1-{key}", "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                       "shippedText": text, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08; this record's English text is new, authored 2026-11-14 for T15a-n1."})
        pack["provenanceRecords"].append(record)
        return record

    # Rewordings (with coverage of the new wording) and removals.
    for owner, pairs in REWORD.items():
        host = steps[owner]
        for old, newer in pairs:
            if host["body"].count(old) != 1:
                raise SystemExit(f"{owner}: reword baseline changed")
            host["body"] = host["body"].replace(old, newer)
            host["russianFacts"] = [newer if f == old else f for f in host.get("russianFacts", [])]
    coverage("obj-u6-possessive", "u6-possessive-our-your", "Наш means our; Ваш means your (formal or plural).")
    coverage("obj-u6-possessive", "u6-possessive-sestra-feminine", "In the worked phrase на́ша сестра́, сестра́ is a feminine singular noun.")
    coverage("obj-u6-feminine", "u6-feminine-a-ya-declension",
             "Nouns in the а or я declension, including the masculine noun папа, have their own accusative singular ending.")
    # The feminine remediation repeats its teaching step exactly; the whole reworded body keeps exact objective coverage.
    if steps["u6-st-rem-feminine"]["body"] != steps["u6-st-teach-feminine-2"]["body"]:
        raise SystemExit("Feminine remediation no longer repeats its teaching step")
    coverage("obj-u6-feminine", "u6-feminine-remediation-body", steps["u6-st-rem-feminine"]["body"])
    for owner, sentences in REMOVE.items():
        host = steps.get(owner) or items[owner]
        for sentence in sentences:
            if "body" in host:
                host["body"] = drop_sentence(host["body"], sentence)
            else:
                host["feedback"] = drop_sentence(host["feedback"], sentence)
            host["russianFacts"] = [f for f in host.get("russianFacts", []) if f != sentence]
    pack["provenanceRecords"] = [r for r in pack["provenanceRecords"] if r["itemId"] not in REMOVED_RECORDS]
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    for record_id, old, newer in (("prov-u6-current-fact-012", "Наш means our; Ваш means your.", "Наш means our; Ваш means your (formal or plural)."),
                                  ("prov-u6-current-fact-026", "Nouns in the а or я declension, including masculine nouns such as папа and дядя, have their own accusative singular ending.",
                                   "Nouns in the а or я declension, including the masculine noun папа, have their own accusative singular ending.")):
        # The superseded wording leaves the pack; the coverage record of the new wording (above) replaces it.
        del records[record_id]
    # 012, 013 and 026 are superseded by the coverage records of their reworded sentences.
    pack["provenanceRecords"] = [r for r in pack["provenanceRecords"] if r["itemId"] not in {
        "prov-u6-current-fact-012", "prov-u6-current-fact-013", "prov-u6-current-fact-026"}]

    # Typed items: objective-specific feedback with marked, frozen-sourced facts.
    for item_id, facts in TYPED_FEEDBACK.items():
        item = items[item_id]
        item["feedback"] = " ".join(facts + [STRESS_NOTE])
        item["russianFacts"] = list(facts)
        for n, fact in enumerate(facts):
            coverage(item["objectiveId"], f"{item_id}-feedback-{n + 1}", fact)

    # Frozen support for every marker in Unit 6.
    owners = list(steps.values()) + [i for i in pack["items"] if i["objectiveId"].startswith("obj-u6-")]
    missing = []
    for host in owners:
        sources = {}
        for fact in host.get("russianFacts", []):
            spec = SUPPORT.get(fact)
            if isinstance(spec, dict):
                spec = spec.get(host["id"])
            if not spec:
                missing.append((host["id"], fact))
                continue
            sources[fact] = resolve(spec)
        if sources:
            host["russianFactSources"] = sources
        elif "russianFactSources" in host:
            del host["russianFactSources"]
    if missing:
        raise SystemExit("Markers without frozen support: " + json.dumps(missing, ensure_ascii=False))

    # The 19 coverage records that cited an unfrozen external PDF now cite the frozen support of their claim.
    marker_sources = {}
    for host in owners:
        for fact, ids in host.get("russianFactSources", {}).items():
            marker_sources.setdefault(fact, set()).update(ids)
    pack["provenanceRecords"].extend(new.values())
    by_id = {r["itemId"]: r for r in pack["provenanceRecords"]}
    for record in pack["provenanceRecords"]:
        if record["itemId"].startswith("prov-u6-current-fact-") and "Cornell" in record["originalSourceText"]:
            cited = sorted(marker_sources[record["shippedText"]])
            frozen = "; ".join(f"{by_id[i]['sourceRecordId'].split(chr(10))[0][:120]} ({i})" for i in cited)
            note = (f"Frozen sources stating this claim: {frozen}. Revised on 2026-11-14 ({TASK_NOTE}): an external PDF that is"
                    " not frozen in catalog/sources/ is no longer cited. The English wording itself was authored on 2026-10-09"
                    " within the content-audit-2026-10-08 batch; no primary-source prose or new Russian form is copied.")
            record["synthesis"]["bibliography"] = note
            original = note + "\n" + record["shippedText"]
            record.update({"originalSourceText": original, "sourceHash": sha(original)})

    # ref-ca-cases quotes the frozen agreement sentence instead of stating an unsourced rule.
    for reference in pack["references"]:
        if reference["id"] == "ref-ca-cases":
            reference.update({"summary": WIKI_SPANS["W57"], "detail": WIKI_SPANS["W57"], "sourceId": "src-wikibooks-russian-cc-by-sa"})
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15a-n1 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
