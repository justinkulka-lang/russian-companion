"""T15g: Block 2 (Units 6-10) close-out content, plus the Unit 1 orientation sentence.

Exact-baseline transformer; runs once on the D7b pack. It applies, in order:
  1. Sentence rewrites (D18d manual misses from the T15f check, CC029): commentary about checks, sources and exercises
     is removed or rewritten as direct teaching. Fact markers (russianFacts) and their citations (russianFactSources)
     follow the sentence: a removed sentence loses its marker; a rewritten one keeps the same citations.
  2. Gloss fixes (CC025, WC004): example notes and every "display - note" pair built from them in teaching and
     remediation. Frozen support for each new meaning is named in GLOSSES and in the coverage records.
  3. Duplicate checks (CC010, IA016 and the u6/u10, u9 tuple pairs): four items move to distinct attested cues and two
     cross-objective duplicates are retired. No Russian is composed: every new cue or option is an existing frozen
     record's text, or a verbatim OpenRussian cell (nouns.csv row "муж").
  4. A2-36 references (prep section 1): ten new entries and ref-ca-cases extended, each unit-gated by
     introducedInUnit, with meaningful search terms, deep-linked from its objectives' teaching and remediation steps.
     Russian is quoted verbatim from the frozen lines named in REFERENCE_LINES.
  5. Provenance: a frozen selection record (D18a/D19a rule: one frozen line, two-part rule) for every Russian region the
     D20 learner-field scan leaves uncovered, and a content-audit English coverage record (T15f pattern) for every
     changed English field. Learner Russian is never generated.
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
import apply_d7b_selection_records as d7b  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "6d4a846a3cee06045e0fcfdf1b97654b859f2cd88ad66404a6ddb2764aab2c07"  # the D7b pack
TASK = "T15g"
CYR = d7b.CYR
AC = d7b.AC

# 1. (old sentence, new sentence or "" to remove, minimum occurrences across learner fields)
SENTENCES = [
    # Unit 1 orientation (owner direction): review status stays in the status UI and metadata.
    ("Qualified review is still pending.", "Start with the printed letters.", 1),
    # Unit 6
    ("Compare the friend with the source chair clause", "Compare the friend with the chair clause", 1),
    ("These steps ask you to recognize the supplied forms only, not to produce endings for other nouns.", "", 2),
    ("Guided practice chooses the supplied object form, without inventing a sentence or new stress.", "", 2),
    ("The independent teacher check retains this exact pair.", "", 2),
    ("The new independent check classifies that inanimate context rather than repeating the teacher answer.", "", 2),
    ("This check does not establish completed action; no aspect or full-translation claim is tested.", "", 2),
    ("Recognize supplied forms and contexts, not unseen or unrestricted productive mastery.", "", 2),
    ("Recognize these supplied contexts; no universal all-case or completed-action claim.", "", 1),
    ("A singular noun does not take the plural form in these choices.", "A singular noun does not take the plural form.", 2),
    ("на́ши is plural and does not mark the feminine singular object row.",
     "на́ши is plural, so it does not agree with a feminine singular direct object.", 1),
    # Unit 7
    ("When the task shows но́вая and asks for that dictionary form, choose но́вый rather than neuter но́вое.",
     "The dictionary form for но́вая is но́вый, not neuter но́вое.", 2),
    ("All the options are already correctly spelled.", "", 2),
    ("Use this explanation to understand the rule; do not infer a general adverb-formation rule from these choices.",
     "Learn each adjective and adverb pair as it is taught.", 2),
    ("an unrestricted rule for every adjective goes beyond this check; it asks only about the pair shown.",
     "an unrestricted rule for every adjective is not what this pair shows; it links one adjective with one adverb.", 1),
    # Unit 8
    ("These two supplied examples do not establish an unrestricted sentence-generation rule.", "", 2),
    # Unit 9
    ("Guided practice uses мальчик.", "", 2),
    # Unit 10 (WC004: the genitive pronouns carry their object meanings, not the nominative labels)
    ("Меня́ is the genitive form of Я, meaning I.", "Меня́ is the genitive form of Я (I); in English it corresponds to me.", 1),
    ("Нас is the genitive form of Мы, meaning we.", "Нас is the genitive form of Мы (we); in English it corresponds to us.", 1),
]

# 2. Gloss fixes: example id -> (new note, frozen support)
GLOSSES = {
    "ex-or-u7-demonstrative-3": ("these", "units-6-10/wikibooks/adjectives-4292379.txt:14 (\"these good new students\" is эти хорошие новые студенты)"),
    "ex-or-u8-agreement-7": ("these", "units-6-10/wikibooks/adjectives-4292379.txt:14"),
    "ex-or-u10-pronoun-13": ("of him, of it", "units-6-10/wikibooks/genitive-4567338.txt:141 (the genitive for 'of him' and 'of it' is его́)"),
    "ex-or-u10-pronoun-14": ("her", "units-6-10/wikibooks/accusative-4330404.txt:101 (She/her, Её)"),
    "ex-u6-third-0": ("his", "units-6-10/wikibooks/accusative-4330404.txt:129,132 (I want his cat - Я хочу его кота)"),
    "ex-u6-third-1": ("her", "units-6-10/wikibooks/accusative-4330404.txt:129 (the feminine possessive is её)"),
    "ex-u6-third-2": ("their", "units-3-5/openrussian/others.csv:1971 (их: their, them)"),
    "ex-u9-quantity-0": ("one hour", "units-3-5/openrussian/nouns.csv:26 (час: hour); units-6-10/wikibooks/numbers-4371358.txt:88 (один: one)"),
    "ex-u9-quantity-1": ("two hours", "units-3-5/openrussian/nouns.csv:26; numbers-4371358.txt table (2 два)"),
    "ex-u9-quantity-2": ("five hours", "units-3-5/openrussian/nouns.csv:26; numbers-4371358.txt:92 (пять: five)"),
    "ex-u9-quantity-3": ("twenty-one hours", "units-3-5/openrussian/nouns.csv:26; numbers-4371358.txt:73 (21 два́дцать оди́н)"),
    "ex-u9-quantity-4": ("twenty-two hours", "units-3-5/openrussian/nouns.csv:26; numbers-4371358.txt table (20 два́дцать, 2 два)"),
    "ex-u9-quantity-10": ("five years", "units-3-5/openrussian/nouns.csv:3 (год: year; pl_gen ле'т); numbers-4371358.txt:121"),
    "ex-or-u8-irregular-2": ("brothers", "units-3-5/wikibooks/nominative-4213267.txt:37 (Брат >> Братья: brother, brothers)"),
    "ex-or-u8-irregular-4": ("friends", "units-3-5/wikibooks/nominative-4213267.txt:38 (Друг >> Друзья: friend, friends)"),
    "ex-or-u8-irregular-6": ("people", "units-3-5/openrussian/nouns.csv:2 (человек: person, people)"),
    "ex-or-u8-irregular-8": ("sons", "units-3-5/wikibooks/nominative-4213267.txt:36 (Сын >> Сыновья: son, sons)"),
    "ex-u10-absence-0": ("I don't have a son", "units-6-10/wikibooks/genitive-4567338.txt:55 without the reply word No (WC004)"),
    "ex-u10-have-5": ("I don't have a son", "units-6-10/wikibooks/genitive-4567338.txt:55 without the reply word No (WC004)"),
    "ex-u10-pronoun-2": ("Me", "units-6-10/wikibooks/accusative-4330404.txt:91 (I/me, Меня)"),
    "ex-u10-pronoun-3": ("Him/her/it", "units-6-10/wikibooks/accusative-4330404.txt:99-103 (He/him, She/her, It)"),
    "ex-u10-pronoun-4": ("Them", "units-6-10/wikibooks/accusative-4330404.txt:105 (They/them, Их)"),
    "ex-u10-pronoun-7": ("Us", "units-6-10/wikibooks/accusative-4330404.txt:93 (We/us, Нас)"),
}

POSSESSION = "In this line, the possessor is genitive and the possessed thing is nominative."
ABSENCE = "In this line, the absent thing is in the genitive case."

# 4. A2-36 references. id -> (unit, title, summary, detail, search terms, objectives it serves, frozen lines quoted)
REFERENCES = {
    "ref-ca-cases": (6, "Cases and the accusative",
        "How case marks a noun's role, the accusative of direct objects, animacy and object pronouns.",
        "Case shows a noun's role in the sentence, so a Russian sentence relies less on word order than an English one. "
        "Compare Собаки ненавидят котов. — Dogs hate cats. with Коты ненавидят собак. — Cats hate dogs.\n\n"
        "The accusative marks the direct object. Feminine nouns change the ending -а to -у and the ending -я to -ю; "
        "feminine nouns ending in the soft sign -ь do not change.\n\n"
        "Masculine nouns follow animacy. An inanimate noun keeps its nominative form: Я де́лал стул. — I made a chair. "
        "An animate noun takes the same form as its genitive: Я люблю этого кота. — I like this cat. "
        "The form matches the genitive, but the noun is still the direct object.\n\n"
        "Object pronouns: Я — I, Меня — me. Мы — we, Нас — us. Ты — you, Тебя — you. Вы — you, Вас — you. "
        "Он — he, Его — him. Она — she, Её — her. Они — they, Их — them.\n\n"
        "Adjectives and possessives change to fit the noun they describe, taking on its gender, number and case.",
        ["accusative", "direct object", "case", "animate", "inanimate", "object pronouns", "me", "him", "her", "agreement"],
        ["obj-u6-object", "obj-u6-feminine", "obj-u6-animacy", "obj-u6-pronoun"],
        ["A:4-8", "A:52-53", "A:35", "A:23-27", "A:31", "A:89-105", "A:57"]),
    "ref-u6-possessives": (6, "Possessives",
        "My, your, our and his, her, their with the nouns they describe.",
        "Possessives such as Мой, Твой, Наш and Ваш act like adjectives: they change to match the noun they describe, "
        "not the owner. Твой is the informal or singular your; Ваш is the formal or plural your.\n\n"
        "In the accusative, my has the forms Мой and Моего (masculine, inanimate and animate), мою (feminine), "
        "Моё (neuter), and Мои and Моих (plural, inanimate and animate). Our follows the same pattern: Наш, Нашего, "
        "нашу, Наше, Наши, Наших.\n\n"
        "The third-person possessives его (his), её (her) and их (their) do not change with the noun they describe: "
        "Я хочу его кота — I want his cat. Я хочу его котов — I want his cats.",
        ["possessive", "my", "your", "our", "his", "her", "their", "agreement"],
        ["obj-u6-possessive", "obj-u6-third"],
        ["A:111", "A:116-126", "A:129", "A:132-133"]),
    "ref-u6-direction": (6, "Direction and location",
        "в and на with the accusative for direction and the prepositional for location.",
        "With the accusative, в means to or into, and на means onto or toward: Я шёл в дом — I walked into the house. "
        "Я шёл на улицу — I walked onto the street.\n\n"
        "With the prepositional case, в means in or at, and на means upon or at: Я жил в Лондоне — I lived in London. "
        "Оно на столе — It's on the table.\n\n"
        "The accusative shows direction; the prepositional shows location.",
        ["direction", "location", "into", "onto", "accusative", "prepositional", "в", "на"],
        ["obj-u6-direction"],
        ["A:15"]),
    "ref-u7-adjectives": (7, "Adjectives",
        "Dictionary form, agreement, spelling and case forms of adjectives.",
        "The dictionary form of an adjective is masculine singular nominative, ending in -ый, -ий or -ой.\n\n"
        "An adjective agrees with its noun in gender, number and case: новый карандаш — new pencil; "
        "новая машина — new car; новое платье — new dress; эти хорошие новые студенты — these good new students.\n\n"
        "Soft adjectives keep the soft ending: синий стол. — the dark blue table; синяя книга — the dark blue book; "
        "синее письмо — the dark blue letter; синие книги — the dark blue books.\n\n"
        "Spelling: after г, ж, к, х, ч, ш or щ, write и rather than ы. After ж, ц, ч, ш or щ, an unstressed о in an "
        "ending is written е: хорошее письмо — the good letter.\n\n"
        "како́й means which, what or what kind of. э́тот means this; its plural э́ти means these, as in "
        "эти хорошие новые студенты.\n\n"
        "In the prepositional, masculine and neuter adjectives end in -ом (-ем after a soft ending or ш, щ, ч, ж, ц), "
        "feminine adjectives in -ой (-ей), and plural adjectives in -ых (-их): "
        "Она́ в си́ней кварти́ре, не в бе́лой кварти́ре — She's in the blue apartment, not the white apartment.\n\n"
        "In the accusative, feminine adjectives end in -ую (-юю after a soft ending): "
        "Анна встретила новую учительницу. — Anna met the new teacher. Masculine and plural adjectives follow the "
        "animacy of their noun.",
        ["adjective", "agreement", "dictionary form", "soft adjective", "spelling rule", "this", "these", "which",
         "prepositional", "accusative"],
        ["obj-u7-default", "obj-u7-endings", "obj-u7-spelling", "obj-u7-question", "obj-u7-demonstrative",
         "obj-u7-place", "obj-u7-objectadj"],
        ["J:6", "J:11-14", "J:18-21", "J:24", "J:26", "OA:7", "OA:4", "J:14", "J:109", "J:111", "J:113", "J:124",
         "A:61", "A:63", "A:79"]),
    "ref-u7-adverbs": (7, "Adverbs",
        "What adverbs describe, why they do not change, and the two taught adjective and adverb pairs.",
        "Adverbs describe a verb much like adjectives describe nouns.\n\n"
        "Наречия — неизменяемые слова: adverbs do not decline or conjugate, and they do not change for gender or "
        "number. This does not mean that comparative forms cannot exist.\n\n"
        "Two adjective and adverb pairs are taught here: быстрый — быстро and творческий — творчески. "
        "Learn each pair as it is taught.",
        ["adverb", "manner", "invariable", "how", "быстро"],
        ["obj-u7-adverb", "obj-u7-adverb-invariance", "obj-u7-adverb-pairs", "obj-u7-adverb-paradigm"],
        ["D:1", "V:8", "V:44"]),
    "ref-u8-plurals": (8, "Noun plurals",
        "Nominative plural endings, the seven-letter spelling rule, and plurals to learn as words.",
        "Masculine nouns add -ы: стол — table, столы — tables. Feminine nouns ending in -а replace it with -ы: "
        "машина — car, машины — cars. Feminine nouns ending in -я or -ь take -и. Neuter nouns ending in -о take -а: "
        "письмо — letter, письма — letters.\n\n"
        "After к, г, х, ш, щ, ж or ч, use -и instead of -ы. книга (book) has the stem книг-, so it follows this rule.\n\n"
        "Some plurals are learned as words: Сын and Сыновья (son, sons); Брат and Братья (brother, brothers); "
        "Друг and Друзья (friend, friends).\n\n"
        "Plural adjectives end in -ые or -ие; see Adjectives.",
        ["plural", "nominative plural", "seven-letter rule", "spelling", "irregular plural"],
        ["obj-u8-plural", "obj-u8-spelling", "obj-u8-irregular"],
        ["N:31-34", "N:42", "N:36-38", "J:14"]),
    "ref-u8-existence": (8, "There is, there are",
        "есть with the nominative to say that something exists somewhere.",
        "Есть can say there is or there are. It is followed by the nominative case.\n\n"
        "На э́той у́лице есть францу́зы — There are Frenchmen on this street. "
        "В Москве́ есть ста́рые зда́ния — There are old buildings in Moscow.\n\n"
        "The noun naming what exists is nominative; the place phrase is separate.",
        ["there is", "there are", "exists", "есть", "nominative"],
        ["obj-u8-availability"],
        ["G:47-49"]),
    "ref-u9-numbers": (9, "Numbers and counting",
        "Cardinal numbers, the forms of one and two, and the noun case after a number.",
        "Numbers: 1 оди́н, 2 два, 3 три, 4 четы́ре, 5 пять, 6 шесть, 7 семь, 8 во́семь, 9 де́вять, 10 де́сять, "
        "11 оди́ннадцать, 20 два́дцать, 100 сто, 1,000 ты́сяча. Compound numbers: два́дцать оди́н — 21; "
        "сто оди́н — 101; две́сти пятна́дцать — 215.\n\n"
        "One agrees with its noun: один мальчик — one boy; одна девушка — one girl. Two is два with masculine and "
        "neuter nouns and две with feminine nouns.\n\n"
        "In the nominative and the inanimate accusative, the last digit decides the noun's case. A number ending in "
        "один takes the nominative singular: Есть оди́н кот — There is one cat. A number ending in 2, 3 or 4 takes "
        "the genitive singular: Два рубля́ — Two roubles; Два́дцать две копе́йки — 22 kopecks. A number ending in 5 "
        "to 9 or 0, and every teen, takes the genitive plural: Пять рубле́й — Five roubles; "
        "Два́дцать пять копе́ек — 25 kopecks; шестнадцать рублей — sixteen rubles.\n\n"
        "Above 99, the last two digits decide: двести три собаки — two hundred and three dogs; "
        "двести тринадцать собак — two hundred and thirteen dogs. In other sentence cases the number itself "
        "also changes.",
        ["numbers", "counting", "one", "two", "cardinal", "how many", "genitive after numbers"],
        ["obj-u9-cardinal", "obj-u9-teens", "obj-u9-tens", "obj-u9-hundreds", "obj-u9-thousands",
         "obj-u9-odindva", "obj-u9-quantity", "obj-u9-pattern"],
        ["R:12-81", "R:88", "R:90", "G:8-17", "R:92", "R:96", "R:98"]),
    "ref-u9-genitive": (9, "Genitive nouns",
        "Genitive singular and plural noun endings used after numbers.",
        "Genitive singular: masculine nouns ending in a consonant add -а, and -й or -ь becomes -я. Neuter -о "
        "becomes -а and -е becomes -я. Feminine -а becomes -ы, -я and -ь become -и, and after the seven consonants "
        "-а becomes -и.\n\n"
        "Genitive plural, with the forms used after a number: Стол, пять столов — Table, five tables. "
        "Музе́й, шесть музе́ев — Museum, six museums. Вещь, десять веще́й — Thing, ten things. "
        "Неде́ля, семь неде́ль — Week, seven weeks. Кни́га, во́семь книг — Book, eight books.\n\n"
        "The genitive plural has many exceptions; learn each new form as it is taught.",
        ["genitive", "genitive plural", "genitive singular", "counting", "numbers"],
        ["obj-u9-quantity", "obj-u9-pattern"],
        ["G:82-94", "G:97-107"]),
    "ref-u10-possession": (10, "Having and not having",
        "у with the genitive and есть or нет to say what someone has or lacks.",
        "To say someone has something, put the owner in the genitive after у and follow it with есть and the thing "
        "owned in the nominative: У вас есть сын? — Do you have a son? Да, у меня есть сын. — Yes, I have a son. "
        "У твоего́ му́жа есть оди́н друг — Your husband has one friend.\n\n"
        "To say that something is absent, use нет followed by the genitive: Нет рестора́нов — There are no "
        "restaurants. В Москве́ нет ста́рых зда́ний — There are no old buildings in Moscow. "
        "У меня́ нет сы́на — I don't have a son. У моего́ бра́та нет де́нег — My brother doesn't have any money.",
        ["have", "possession", "absence", "there is no", "у меня есть", "нет", "genitive"],
        ["obj-u10-have", "obj-u10-absence"],
        ["G:41-45", "G:50-52", "G:53-56", "G:70"]),
    "ref-u10-genitive": (10, "The genitive: pronouns, prepositions and adjectives",
        "Genitive pronouns, prepositions that take the genitive, genitive adjectives and animate plural objects.",
        "Genitive pronouns: Я → Меня́, Мы → Нас, Ты → Тебя́, Вы → Вас, Он/она/оно → Его́/её, Они → Их. The genitive "
        "for of him and of it is the same, его́. After a preposition, его, её and их gain н: Для него́ — For him/it. "
        "Без неё — Without her.\n\n"
        "Prepositions that take the genitive: Для — for; У — by, near; Без — without; Из — from, out of; "
        "От — away from. Я хочу́ пода́рок для моего́ дру́га — I want a present for my friend. "
        "Ко́фе без молока́ — Black coffee. Я из А́нглии — I'm from England.\n\n"
        "Genitive adjectives: masculine and neuter -ого (-его after a hushing consonant), feminine -ой (-ей), plural "
        "-ых (-их): Кило́ хоро́шего ру́сского сы́ра — A kilogram of good Russian cheese. Кило́ хоро́шей ру́сской "
        "колбасы́ — A kilogram of good Russian sausage. Кило́ чи́стых све́жих я́блок — A kilogram of clean fresh apples. "
        "Я пришёл из моего старого дома — I arrived from my old house.\n\n"
        "An animate plural direct object takes the genitive-shaped accusative: Эти женщины любят меня. — These women "
        "like me. Я люблю этих женщин. — I like these women.",
        ["genitive", "pronouns", "prepositions", "without", "from", "for", "adjectives", "animate plural"],
        ["obj-u10-pronoun", "obj-u10-preposition", "obj-u10-adjective", "obj-u10-animate"],
        ["G:132-143", "G:63-78", "G:120-126", "J:62", "A:28-29"]),
}
# Units 8 agreement reviews the Unit 7 adjective entry (prep: plural adjective agreement is review, not new).
EXTRA_LINKS = {"ref-u7-adjectives": ["obj-u8-agreement"]}

# Preferred frozen line for each quoted Russian region (file, line); others are chosen by the D7b ranking.
FILE = {
    "A": "units-6-10/wikibooks/accusative-4330404.txt", "J": "units-6-10/wikibooks/adjectives-4292379.txt",
    "G": "units-6-10/wikibooks/genitive-4567338.txt", "R": "units-6-10/wikibooks/numbers-4371358.txt",
    "N": "units-3-5/wikibooks/nominative-4213267.txt", "V": "units-6-10/ruwiki/narechie-152437423.txt",
    "D": "units-6-10/wikibooks/adverbs-3197995.txt", "OA": "units-6-10/openrussian/adjectives.csv",
}


# The frozen files each unit's references quote, in order of preference for a region's selection record.
UNIT_FILES = {
    6: [FILE["A"]],
    7: [FILE["J"], FILE["OA"], FILE["A"], FILE["V"], FILE["D"]],
    8: [FILE["N"], FILE["G"], FILE["J"], FILE["OA"], FILE["A"]],
    9: [FILE["R"], FILE["G"]],
    10: [FILE["G"], FILE["A"], FILE["J"]],
}


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def strings(node, path=()):
    """Every (container, key, value) string slot under a learner owner."""
    if isinstance(node, dict):
        for k, v in node.items():
            if isinstance(v, str):
                yield node, k, v
            else:
                yield from strings(v, path + (k,))
    elif isinstance(node, list):
        for i, v in enumerate(node):
            if isinstance(v, str):
                yield node, i, v
            else:
                yield from strings(v, path + (i,))


def owners(pack: dict, units: set[int]):
    """Learner owners (steps, items, examples) of the given units, with the unit number."""
    objective_unit = {o["id"]: u["number"] for u in pack["units"] for o in u["objectives"]}
    for u in pack["units"]:
        if u["number"] in units:
            for s in u["sessions"]:
                for st in s["steps"]:
                    yield u["number"], st
    for i in pack["items"]:
        if objective_unit.get(i["objectiveId"]) in units:
            yield objective_unit[i["objectiveId"]], i
    for e in pack["examples"]:
        if objective_unit.get(e.get("objectiveId")) in units:
            yield objective_unit[e["objectiveId"]], e


def apply_sentences(pack: dict, report: list) -> None:
    units = {1, 6, 7, 8, 9, 10}
    for old, new, minimum in SENTENCES:
        count = 0
        for _unit, owner in owners(pack, units):
            for key in list(owner.keys()):
                if key in ("russianFacts", "russianFactSources", "id"):
                    continue
                value = owner[key]
                if isinstance(value, str) and old in value:
                    count += value.count(old)
                    owner[key] = tidy(value, old, new)
                elif isinstance(value, (dict, list)):
                    for container, k, v in list(strings(value)):
                        if old in v:
                            count += v.count(old)
                            container[k] = tidy(v, old, new)
            rename_fact(owner, old, new)
        if count < minimum:
            raise SystemExit(f"Sentence found {count} times, expected at least {minimum}: {old}")
        report.append(["sentence", old, new, count])


def tidy(value: str, old: str, new: str) -> str:
    text = value.replace(old, new)
    text = re.sub(r"[ ]{2,}", " ", text)
    text = re.sub(r" +\n", "\n", text)
    text = re.sub(r"\n +", "\n", text)
    return text.strip(" ") if value.strip(" ") == value else text


def rename_fact(owner: dict, old: str, new: str) -> None:
    facts = owner.get("russianFacts")
    if not facts:
        return
    sources = owner.get("russianFactSources") or {}
    out = []
    for fact in facts:
        if old in fact:
            replaced = re.sub(r"[ ]{2,}", " ", fact.replace(old, new)).strip()
            if replaced:
                out.append(replaced)
                if fact in sources:
                    sources[replaced] = sources.pop(fact)
            else:
                sources.pop(fact, None)
        else:
            out.append(fact)
    owner["russianFacts"] = out
    if "russianFactSources" in owner:
        owner["russianFactSources"] = sources


def apply_glosses(pack: dict, report: list) -> None:
    examples = {e["id"]: e for e in pack["examples"]}
    for ex_id, (new, support) in GLOSSES.items():
        e = examples[ex_id]
        old = e["note"]
        if old == new:
            raise SystemExit(f"{ex_id} already glossed {new}")
        e["note"] = new
        pair_old, pair_new = f"{e['display']} — {old}", f"{e['display']} — {new}"
        count = 0
        for _unit, owner in owners(pack, {6, 7, 8, 9, 10}):
            for key in list(owner.keys()):
                if key in ("russianFacts", "russianFactSources", "id"):
                    continue
                value = owner[key]
                if isinstance(value, str) and pair_old in value:
                    # whole-pair match only (the note must end there)
                    owner[key] = re.sub(re.escape(pair_old) + r"(?=[.;\n]|$)", pair_new, value)
                    count += 1
            facts = owner.get("russianFacts") or []
            for fact in list(facts):
                if re.search(re.escape(pair_old) + r"(?=[.;\n]|$)", fact):
                    replaced = re.sub(re.escape(pair_old) + r"(?=[.;\n]|$)", pair_new, fact)
                    idx = owner["russianFacts"].index(fact)
                    owner["russianFacts"][idx] = replaced
                    src = owner.get("russianFactSources") or {}
                    if fact in src:
                        src[replaced] = src.pop(fact)
        report.append(["gloss", ex_id, f"{old} -> {new}", count, support])


def records_by_text(pack: dict, unit: int) -> dict[str, list[str]]:
    out: dict[str, list[str]] = {}
    for r in pack["provenanceRecords"]:
        if r["unitNumber"] == unit:
            out.setdefault(r["shippedText"], []).append(r["itemId"])
    return out


RULE_CONTEXT_PINS: dict[str, str] = {}


def tuple_text(cue: str, answer: str, distractors: list[str]) -> str:
    def field(v: str) -> str:
        return f"{len(v.encode('utf-8'))}:{v}"
    return "tuple-v1:1;" + field(cue) + field(answer) + f"{len(distractors)};" + "".join(field(d) for d in distractors)


def retarget_rule_context(pack: dict, item: dict) -> None:
    """Rebuild the item's pinned rule-context tuple (CompleteRuleContextPins) for its new cue, answer and feedback."""
    key = f"ca-rule-context:{item['id']}"
    recs = [r for r in pack["provenanceRecords"] if r["sourceRecordId"] == key]
    distractors = [o for o in item["options"] if o != item["answer"]]
    cue_rec = next(r for r in recs if r["itemId"].endswith("-0"))
    by_text = {r["shippedText"]: r for r in recs if r is not cue_rec}
    ordered = [cue_rec, by_text[item["answer"]]] + [by_text[d] for d in distractors]
    if len(ordered) != len(recs):
        raise SystemExit(f"{item['id']}: rule-context records do not match the options")
    head = cue_rec["originalSourceText"].split("\nContext explanation:\n", 1)[0]
    original = f"{head}\nContext explanation:\n{item['feedback']}\nTuple-v1:" + tuple_text(item["promptLetter"], item["answer"], distractors)
    for r in recs:
        r["originalSourceText"] = original
        r["sourceHash"] = sha(original)
        r["synthesis"]["applications"] = [{"cue": item["promptLetter"], "answer": item["answer"], "distractors": distractors}]
    cue_rec["shippedText"] = item["promptLetter"]
    cue_rec["contentHash"] = sha(item["promptLetter"])
    a = item["assessment"]
    a["relationParticipantIds"] = [r["itemId"] for r in ordered]
    a["answerSourceIds"] = [ordered[1]["itemId"]]
    a["distractorSourceIds"] = [ordered[1 + 1 + distractors.index(o)]["itemId"] if o != item["answer"] else "" for o in item["options"]]
    RULE_CONTEXT_PINS[key] = sha(original)


def recue(pack: dict, report: list) -> None:
    items = {i["id"]: i for i in pack["items"]}
    recs = {r["itemId"]: r for r in pack["provenanceRecords"]}

    # u8-g-spelling-0: guided spelling moves from кни́га (the plural check's cue) to the adjective хоро́ший.
    i = items["u8-g-spelling-0"]
    fb = "хоро́ший is the masculine singular nominative form; хоро́шие is the plural nominative form of the same adjective."
    r1 = "хоро́ший is the masculine singular nominative form, so it does not supply the requested plural nominative form."
    r2 = "хоро́шая is the feminine singular nominative form, so it does not supply the requested plural nominative form."
    i.update({"prompt": "Which plural nominative form of the same adjective corresponds to the shown form?",
              "promptLetter": "хоро́ший", "options": ["хоро́шие", "хоро́ший", "хоро́шая"], "answer": "хоро́шие",
              "feedback": fb, "distractorRationales": ["", r1, r2],
              "russianFacts": [fb, r1, r2],
              "russianFactSources": {fb: ["prov-ex-or-u8-spelling-1", "prov-ex-or-u8-spelling-2"],
                                     r1: ["prov-ex-or-u8-spelling-1"], r2: ["prov-t15c-u8-spelling-khoroshiy-decl-f-nom"]}})
    a = i["assessment"]
    a.update({"testedFeature": "ADJECTIVE_NOMINATIVE_FORM", "answerSourceIds": ["prov-ex-or-u8-spelling-2"],
              "distractorSourceIds": ["", "prov-ex-or-u8-spelling-1", "prov-t15c-u8-spelling-khoroshiy-decl-f-nom"],
              "distractorDifference": ["", r1, r2], "whyAnswerDemonstratesObjective": fb,
              "contrastMembers": ["хоро́шие", "хоро́ший", "хоро́шая"],
              "relationParticipantIds": ["prov-ex-or-u8-spelling-1", "prov-ex-or-u8-spelling-2", "prov-ex-or-u8-spelling-1",
                                         "prov-t15c-u8-spelling-khoroshiy-decl-f-nom"]})
    report.append(["recue", "u8-g-spelling-0", "кни́га -> хоро́ший", "", "existing Unit 8 records"])

    # u9-g-pattern-0: the pattern guided check moves from один час (the quantity guided cue) to Есть оди́н кот.
    i = items["u9-g-pattern-0"]
    if "час" in i["feedback"] or any("час" in f for f in i.get("russianFacts", [])):
        raise SystemExit("u9-g-pattern-0 feedback names the old cue")
    i["promptLetter"] = "Есть оди́н кот"
    retarget_rule_context(pack, i)
    report.append(["recue", "u9-g-pattern-0", "один час -> Есть оди́н кот", "", "prov-ex-u9-pattern-5-ru (genitive-4567338.txt:9)"])

    # u10-i-have-3: the possession check uses the attested husband line instead of the absence guided cue.
    i = items["u10-i-have-3"]
    cue = "У твоего́ му́жа есть оди́н друг"
    fb1 = f"{cue} uses the simple possession construction."
    fb2 = "The possessor after the possession preposition is genitive; the possessed thing is nominative."
    fb3 = "The absence explanation would deny possession, which this line does not do."
    rat = "The other explanation describes absence rather than the presence or simple possession shown here."
    have1 = items["u10-i-have-1"]
    src1 = have1["russianFactSources"]
    i.update({"promptLetter": cue, "answer": POSSESSION, "feedback": f"{fb1} {fb2} {fb3}",
              "distractorRationales": ["", rat], "russianFacts": [fb1, fb2, fb3, rat],
              "russianFactSources": {
                  fb1: ["prov-ex-u10-have-3-ru"] + src1["uses the simple possession construction."],
                  fb2: src1["The possessor after the possession preposition is genitive; the possessed thing or slot is nominative."],
                  fb3: src1["The absence explanation would deny possession, which this line does not do."],
                  rat: src1[rat]}})
    i["assessment"].update({"distractorDifference": ["", rat], "whyAnswerDemonstratesObjective": f"{fb1} {fb2} {fb3}"})
    retarget_rule_context(pack, i)
    report.append(["recue", "u10-i-have-3", "У меня́ нет сы́на -> " + cue, "", "prov-ex-u10-have-3-ru (genitive-4567338.txt:70)"])

    # u10-i-preposition-2: the genitive form check moves from друг (the Unit 6 animacy tuple) to муж.
    templates = {"sg_nom": recs["prov-ex-or-u10-preposition-15"], "sg_gen": recs["prov-ex-or-u10-preposition-16"],
                 "sg_dat": recs["prov-t15e-u10-drug-sg-dat"]}
    cells = {"sg_nom": ("муж", "муж"), "sg_gen": ("му'жа", "му́жа"), "sg_dat": ("му'жу", "му́жу")}
    row = next(l for l in (ROOT / "catalog/sources/units-3-5/openrussian/nouns.csv").read_text(encoding="utf-8").split("\n")
               if l.startswith("муж\t"))
    new_ids = {}
    for field, (original, shown) in cells.items():
        if original not in row.split("\t"):
            raise SystemExit(f"муж {field} {original} is not a cell of nouns.csv")
        r = copy.deepcopy(templates[field])
        rid = f"prov-t15g-u10-muzh-{field.replace('_', '-')}"
        r.update({"itemId": rid, "sourceRecordId": f"nouns.csv:муж#{field}", "originalSourceText": original,
                  "shippedText": shown, "sourceHash": sha(original), "contentHash": sha(shown)})
        if templates[field]["sourceHash"] != sha(templates[field]["originalSourceText"]):
            raise SystemExit("template hash rule differs")
        pack["provenanceRecords"].append(r)
        new_ids[field] = rid
    i = items["u10-i-preposition-2"]
    fb = "муж is the singular nominative form; му́жа is the singular genitive form of the same noun."
    r1 = "муж is the singular nominative form, so it does not supply the requested singular genitive form."
    r2 = "му́жу is the singular dative form, so it does not supply the requested singular genitive form."
    i.update({"promptLetter": "муж", "options": ["му́жа", "муж", "му́жу"], "answer": "му́жа", "feedback": fb,
              "distractorRationales": ["", r1, r2], "russianFacts": [fb, r1, r2],
              "russianFactSources": {fb: [new_ids["sg_gen"], new_ids["sg_nom"]], r1: [new_ids["sg_nom"]], r2: [new_ids["sg_dat"]]}})
    i["assessment"].update({"answerSourceIds": [new_ids["sg_gen"]], "distractorSourceIds": ["", new_ids["sg_nom"], new_ids["sg_dat"]],
                            "distractorDifference": ["", r1, r2], "whyAnswerDemonstratesObjective": fb,
                            "contrastMembers": ["му́жа", "муж", "му́жу"],
                            "relationParticipantIds": [new_ids["sg_nom"], new_ids["sg_gen"], new_ids["sg_nom"], new_ids["sg_dat"]]})
    report.append(["recue", "u10-i-preposition-2", "друг -> муж", "", "units-3-5/openrussian/nouns.csv:127 (3 new records)"])

    # Retire the two remaining cross-objective duplicates (u10-i-absence-3 = u10-i-have-1; u10-i-absence-4 = u10-i-have-4).
    retire = {"u10-i-absence-3", "u10-i-absence-4"}
    pack["items"] = [x for x in pack["items"] if x["id"] not in retire]
    for u in pack["units"]:
        for o in u["objectives"]:
            o["checkItemIds"] = [x for x in o.get("checkItemIds", []) if x not in retire]
        for s in u["sessions"]:
            for st in s["steps"]:
                if st.get("itemIds"):
                    st["itemIds"] = [x for x in st["itemIds"] if x not in retire]
    report.append(["retire", "u10-i-absence-3, u10-i-absence-4", "duplicates of u10-i-have-1 and u10-i-have-4", "", ""])


def apply_references(pack: dict, report: list) -> None:
    refs = {r["id"]: r for r in pack["references"]}
    steps = {st["id"]: st for u in pack["units"] for s in u["sessions"] for st in s["steps"]}
    objectives = {o["id"]: o for u in pack["units"] for o in u["objectives"]}
    for rid, (unit, title, summary, detail, terms, served, lines) in REFERENCES.items():
        entry = refs.get(rid)
        if entry is None:
            entry = {"id": rid, "sourceId": "src-wikibooks-russian-cc-by-sa", "reviewStatus": "IMPLEMENTED_PENDING_FINAL_AUDIT",
                     "classification": "LINGUISTIC_INSTRUCTION"}
            pack["references"].append(entry)
        entry.update({"title": title, "summary": summary, "detail": detail, "introducedInUnit": unit, "searchTerms": terms})
        linked = []
        for oid in served + EXTRA_LINKS.get(rid, []):
            o = objectives[oid]
            teach = next(s for s in o["instructionStepIds"] if steps[s]["kind"] == "TEACH")
            for sid in [teach] + list(o.get("remediationStepIds", [])):
                ids = steps[sid].setdefault("referenceIds", [])
                if rid not in ids:
                    ids.append(rid)
                    linked.append(sid)
        report.append(["reference", rid, title, unit, "; ".join(lines), " ".join(linked)])


def learner_fields(pack: dict):
    """Port of V2LearnerFields.fields + assessmentMetadata: (unit, text)."""
    out = []
    objective_unit = {o["id"]: u["number"] for u in pack["units"] for o in u["objectives"]}
    ex_units, ref_units = {}, {}
    for u in pack["units"]:
        n = u["number"]
        out += [(n, u.get(k) or "") for k in ("title", "shortLabel", "topicSummary", "mappingNote")]
        for o in u["objectives"]:
            out.append((n, o["title"]))
            for e in o.get("exampleIds", []):
                ex_units.setdefault(e, set()).add(n)
        for s in u["sessions"]:
            out.append((n, s.get("title") or ""))
            for st in s["steps"]:
                out += [(n, st.get(k) or "") for k in ("title", "body", "bookDirection", "companionDirection", "unavailableReason", "dominantAction")]
                for e in st.get("exampleIds") or []:
                    ex_units.setdefault(e, set()).add(n)
                for r in st.get("referenceIds") or []:
                    ref_units.setdefault(r, set()).add(n)
    for i in pack["items"]:
        n = objective_unit.get(i["objectiveId"])
        if n is None:
            continue
        out += [(n, i.get(k) or "") for k in ("prompt", "promptLetter", "answer", "feedback")]
        out += [(n, v) for v in i.get("options") or []] + [(n, v) for v in i.get("distractorRationales") or []]
        out += [(n, v) for v in (i.get("typedAnswerKey") or {}).get("acceptedAnswers") or []]
        a = i.get("assessment")
        if a:
            out += [(n, a.get("whyAnswerDemonstratesObjective") or "")] + [(n, v) for v in a.get("distractorDifference") or []]
    for e in pack["examples"]:
        for n in ex_units.get(e["id"]) or ({objective_unit[e["objectiveId"]]} if e.get("objectiveId") in objective_unit else set()):
            out += [(n, e.get("display") or ""), (n, e.get("note") or "")]
    for r in pack["references"]:
        for n in ref_units.get(r["id"], set()):
            out += [(n, r["title"]), (n, r["summary"]), (n, r["detail"])]
    return out


def cover_russian(pack: dict, report: list) -> None:
    """D18a/D19a: a frozen selection record for every uncovered Russian region in the learner fields."""
    rows = d7b.allowlist()
    frozen, with_records = {}, set()
    for r in pack["provenanceRecords"]:
        with_records.add(r["unitNumber"])
        if d7b.frozen_record(pack, r):
            frozen.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    needed, seen = [], set()
    for n, text in learner_fields(pack):
        if n not in with_records or n <= 2:
            continue
        for m in d7b.REGION.finditer(text):
            region = m.group(0).strip()
            trimmed = region.rstrip("?!.,")
            if region in frozen.get(n, set()) or trimmed in frozen.get(n, set()) or d7b.exempt(rows, n, trimmed):
                continue
            if (n, trimmed) not in seen:
                seen.add((n, trimmed))
                needed.append((n, trimmed))
    lines = d7b.frozen_lines()
    tmpl = d7b.templates(pack)
    first_objective = {u["number"]: u["objectives"][0]["id"] for u in pack["units"]}
    preferred = {(FILE[k], int(x)) for spec in sum((v[6] for v in REFERENCES.values()), [])
                 for k, rng in [spec.split(":")] for x in [rng.split("-")[0]]}
    missing = []
    for k, (n, region) in enumerate(needed, 1):
        candidates = [(rel, i, raw, d) for rel, i, raw, d in lines if rel in tmpl and d7b.two_part(d, region)]
        if not candidates:
            missing.append((n, region))
            continue

        def rank(c):
            rel, i, raw, d = c
            exact = re.search(f"(?<![{CYR}{AC}]){re.escape(region)}(?![{CYR}{AC}])", d) is not None
            near = any(rel == f and abs(i - line) <= 30 for f, line in preferred)
            order = UNIT_FILES.get(n, [])
            cited = order.index(rel) if rel in order else len(order)
            return (cited, not near, not exact, rel, i)

        rel, i, raw, d = sorted(candidates, key=rank)[0]
        t = tmpl[rel]
        prefix = t["sourceRecordId"].split(":", 1)[0]
        record = copy.deepcopy(t)
        rid = f"prov-t15g-u{n}-{k:03d}"
        record.update({"itemId": rid, "unitNumber": n, "objectiveId": first_objective[n], "recordType": "lexical record",
                       "sourceRecordId": f"{prefix}:{raw[:80]}" if not t["sourceId"].startswith("src-openrussian-") else f"{prefix}:{raw.split(chr(9), 1)[0]}",
                       "originalSourceText": raw, "shippedText": region, "transformation": "selection",
                       "sourceHash": sha(raw), "contentHash": sha(region)})
        pack["provenanceRecords"].append(record)
        frozen.setdefault(n, set()).add(region)
        report.append(["russian-record", rid, n, region, f"catalog/sources/{rel}:{i}"])
    if missing:
        raise SystemExit(f"No single frozen line passes the two-part rule for: {missing}")


def english_hosts(pack: dict) -> dict[tuple[str, str], tuple[int, str, str, list[str]]]:
    """(owner, field) -> (unit, objective, text, facts) for the English-coverage hosts of Units 6-10."""
    out = {}
    unit_of = {o["id"]: u["number"] for u in pack["units"] for o in u["objectives"]}
    for u in pack["units"]:
        if not 6 <= u["number"] <= 10:
            continue
        objective_of = {}
        for o in u["objectives"]:
            for sid in o.get("instructionStepIds", []) + o.get("remediationStepIds", []) + o.get("practiceStepIds", []):
                objective_of.setdefault(sid, o["id"])
        for s in u["sessions"]:
            for st in s["steps"]:
                if st["kind"] in ("TEACH", "REMEDIATE"):
                    out[(st["id"], "body")] = (u["number"], objective_of.get(st["id"], u["objectives"][0]["id"]), st["body"], list(st.get("russianFacts", [])))
    for i in pack["items"]:
        n = unit_of.get(i["objectiveId"])
        if n is None or not 6 <= n <= 10:
            continue
        facts = list(i.get("russianFacts", []))
        out[(i["id"], "feedback")] = (n, i["objectiveId"], i["feedback"], facts)
        out[(i["id"], "cue")] = (n, i["objectiveId"], i.get("promptLetter") or "", [])
        out[(i["id"], "answer")] = (n, i["objectiveId"], i["answer"], [])
        for k, v in enumerate(i.get("options", [])):
            out[(i["id"], f"option{k}")] = (n, i["objectiveId"], v, [])
        for k, v in enumerate(i.get("distractorRationales", [])):
            out[(i["id"], f"rationale{k}")] = (n, i["objectiveId"], v, facts)
        a = i.get("assessment") or {}
        out[(i["id"], "why")] = (n, i["objectiveId"], a.get("whyAnswerDemonstratesObjective") or "", [])
        for k, v in enumerate(a.get("distractorDifference") or []):
            out[(i["id"], f"difference{k}")] = (n, i["objectiveId"], v, [])
    for e in pack["examples"]:
        n = unit_of.get(e.get("objectiveId"))
        if n is not None and 6 <= n <= 10:
            out[(e["id"], "note")] = (n, e["objectiveId"], e.get("note") or "", [])
    return out


def cover_english(pack: dict, before: dict, report: list) -> None:
    """T15f pattern: a content-audit coverage record for each changed English field and each new marked sentence."""
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shipped = {}
    for r in pack["provenanceRecords"]:
        shipped.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    template = records["prov-t15an1-u6-possessive-our-your"]
    supports = {ex: s for ex, (_n, s) in GLOSSES.items()}

    def coverage(unit: int, objective: str, key: str, text: str, support: str = "") -> None:
        if not text or text in shipped.get(unit, set()):
            return
        record = copy.deepcopy(template)
        note = (f"New English wording authored on 2026-11-19 ({TASK}); it did not exist before that date. It covers the"
                " wording only; the Russian facts it states are cited from frozen source records in the marker's"
                " russianFactSources." + (f" The meaning is supported by the frozen line {support}." if support else "") +
                " 2026-10-08 is the date of the reused research context only.")
        record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                    "objectiveOrClaimBinding": f"{objective}:{key}"})
        original = note + "\n" + text
        item_id = f"prov-ca-t15g-{key}"
        if item_id in records:
            raise SystemExit(f"Duplicate record {item_id}")
        record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective,
                       "sourceRecordId": f"ca-coverage:{objective}:{key}", "shippedText": text,
                       "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                       "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                      f" this record's English text is new, authored 2026-11-19 for {TASK}."})
        pack["provenanceRecords"].append(record)
        records[item_id] = record
        shipped.setdefault(unit, set()).add(text)
        report.append(["english-coverage", item_id, unit, text[:120]])

    old_hosts = english_hosts(before)
    for (owner, field), (unit, objective, text, facts) in sorted(english_hosts(pack).items()):
        old = old_hosts.get((owner, field))
        if old is not None and old[2] == text:
            continue
        coverage(unit, objective, f"{owner}-{field}", text, supports.get(owner, ""))
        old_facts = set(old[3]) if old else set()
        for n, fact in enumerate(f for f in facts if f not in old_facts):
            coverage(unit, objective, f"{owner}-{field}-fact{n}", fact)


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15g baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    before = copy.deepcopy(pack)
    report: list = []
    apply_sentences(pack, report)
    apply_glosses(pack, report)
    recue(pack, report)
    apply_references(pack, report)
    cover_russian(pack, report)
    cover_english(pack, before, report)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        with open(sys.argv[1], "w", newline="", encoding="utf-8") as fh:
            csv.writer(fh).writerows(report)
    for key, value in sorted(RULE_CONTEXT_PINS.items()):
        print(f"PIN {key} {value}")
    print(f"{len(report)} report rows; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
