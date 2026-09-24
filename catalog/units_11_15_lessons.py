"""Typed Units 11–15 checks. Every Russian span is an exact frozen substring or cell.

Relationships are recomputed from complete frozen-source identity. This module
does not conjugate, decline, or compose new Russian.
"""

from units_6_10_lessons import _check, _other

O = "src-openrussian-50e210c4"
W = "src-wikibooks-russian-cc-by-sa"


def _wiki(page, needle, ru, en):
    return ("wiki", page, needle, ru, en)


def _field(record, name):
    return ("field", record, name)


def populate(lesson, number: int) -> None:
    {
        11: _unit_11,
        12: _unit_12,
        13: _unit_13,
        14: _unit_14,
        15: _unit_15,
    }[number](lesson)


def _unit_11(lesson) -> None:
    past = "past"
    read = "verbs.csv:чита'ть"
    be = "verbs.csv:быть"
    smile = "verbs.csv:улыба'ться"
    study = "verbs.csv:учи'ться"
    table = "Он чита́л<br>He was reading || Она чита́ла<br>She was reading || Оно чита́ло<br>It was reading || Они чита́ли<br>They were reading"
    pair = "So the English ''past progressive'' sentence  \"I was reading\" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence \"I read\" corresponds to the Russian perfective ('''я прочитал''')"
    reflex = "He was smiling - '''Он улыба́лся'''\n:She was smiling - '''Она улыба́лась'''\n:It was smiling - '''Оно улыба́лось'''\n:They were smiling - '''Они улыба́лись'''"
    lesson.objective(
        "past-read",
        "Choose the admitted past row",
        "FORM_SELECTION",
        (past, "To form the past tense of either aspect of a verb, remove the ending '''-ть''', and add '''-л'''. If the subject of the verb is feminine, add '''-ла'''.", "To form the past tense of either aspect of a verb, remove the ending '''-ть''', and add '''-л'''."),
        [
            _field(read, "past_m"),
            _field(read, "past_f"),
            _field(read, "past_n"),
            _field(read, "past_pl"),
            _wiki(past, table, "Он чита́л", "He was reading"),
            _wiki(past, table, "Она чита́ла", "She was reading"),
        ],
        [
            _check("Which admitted row is the other row of the shown form?", "чита́л", "чита́ла", [
                _other("чита́ло", "This form is another row of the same word."),
                _other("чита́ли", "This form is another row of the same word."),
            ], O, feature="PAST_ROW"),
            _check("Which admitted row is the other row of the shown form?", "чита́ла", "чита́л", [
                _other("чита́ли", "This form is another row of the same word."),
            ], O, feature="PAST_ROW"),
            _check("Which admitted row is the other row of the shown form?", "Он чита́л", "Она чита́ла", [
                _other("Они чита́ли", "This form is another cell of the same frozen line."),
            ], feature="PAST_ROW"),
        ],
        extra=[
            (past, table, "Они чита́ли", "example"),
        ],
    )
    lesson.objective(
        "past-be",
        "Choose the admitted был row",
        "FORM_SELECTION",
        (past, "It conjugates as all regular verbs do: '''был''', '''была''', '''было''', and '''были'''.", "It conjugates as all regular verbs do: '''был''', '''была''', '''было''', and '''были'''."),
        [
            _field(be, "past_m"),
            _field(be, "past_f"),
            _field(be, "past_n"),
            _field(be, "past_pl"),
            _wiki(past, "He was in Moscow - '''Он был в Москве́'''", "Он был в Москве́", "He was in Moscow"),
            _wiki(past, "She was beautiful - '''Она была́ краси́вая'''", "Она была́ краси́вая", "She was beautiful"),
        ],
        [
            _check("Which admitted row is the other row of the shown form?", "бы́л", "была́", [
                _other("бы́ло", "This form is another row of the same word."),
                _other("бы́ли", "This form is another row of the same word."),
            ], O, feature="PAST_ROW"),
            _check("Which admitted row is the other row of the shown form?", "была́", "бы́ли", [
                _other("бы́л", "This form is another row of the same word."),
            ], O, feature="PAST_ROW"),
            _check("Which admitted row is the other row of the shown form?", "бы́ло", "бы́ли", [
                _other("бы́л", "This form is another row of the same word."),
            ], O, feature="PAST_ROW"),
        ],
    )
    lesson.objective(
        "past-aspect",
        "Choose the admitted completed past line",
        "CONTRAST_SELECTION",
        (past, pair, pair),
        [
            _wiki(past, pair, "я читал", "I was reading"),
            _wiki(past, pair, "я прочитал", "I read"),
            _field("verbs.csv:прочита'ть", "past_m"),
            _field("verbs.csv:прочита'ть", "past_f"),
            _field("verbs.csv:прочита'ть", "past_n"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "я читал", "я прочитал", [
                _other("I was reading", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="PAST_ASPECT_LINE"),
            _check("Which admitted line is the other line in the same frozen record?", "я прочитал", "я читал", [
                _other("I read", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="PAST_ASPECT_LINE"),
            _check("Which admitted row is the other row of the shown form?", "прочита́л", "прочита́ла", [
                _other("прочита́ло", "This form is another row of the same word."),
            ], O, feature="PAST_ASPECT_LINE"),
        ],
        extra=[
            (past, pair, "I was reading", "meaning"),
            (past, pair, "I read", "meaning"),
        ],
    )
    lesson.objective(
        "reflexive",
        "Choose the admitted reflexive past row",
        "FORM_SELECTION",
        (past, "Some verbs, such as the imperfective '''улыба́ться''' ('to smile'), have the reflexive ending '''-сь/ся''' to indicate that the object is also the subject.", "Some verbs, such as the imperfective '''улыба́ться''' ('to smile'), have the reflexive ending '''-сь/ся'''"),
        [
            _field(smile, "accented"),
            _field(smile, "past_m"),
            _field(smile, "past_f"),
            _field(smile, "past_pl"),
            _wiki(past, reflex, "Он улыба́лся", "He was smiling"),
            _wiki(past, reflex, "Она улыба́лась", "She was smiling"),
            _field(study, "past_m"),
            _field(study, "past_f"),
            _field(study, "past_pl"),
        ],
        [
            _check("Which admitted row is the other row of the shown form?", "улыба́лся", "улыба́лась", [
                _other("улыба́лись", "This form is another row of the same word."),
                _other("улыба́ться", "This form is another row of the same word."),
            ], O, feature="REFLEXIVE_PAST"),
            _check("Which admitted line is the other line in the same frozen record?", "Он улыба́лся", "Она улыба́лась", [
                _other("Они улыба́лись", "This form is another cell of the same frozen line."),
            ], feature="REFLEXIVE_PAST"),
            _check("Which admitted row is the other row of the shown form?", "учи́лся", "учи́лась", [
                _other("учи́лись", "This form is another row of the same word."),
            ], O, feature="REFLEXIVE_PAST"),
        ],
        extra=[
            (past, reflex, "Они улыба́лись", "example"),
        ],
    )


def _unit_12(lesson) -> None:
    future = "future"
    aspect = "aspect"
    adjectives = "adjectives"
    book = "nouns.csv:кни'га"
    table = "nouns.csv:стол"
    house = "nouns.csv:дом"
    good = "adjectives.csv:хоро'ший"
    imp = "Я бу́ду говори́ть<br>I will be speaking || Мы бу́дем говори́ть<br>We will be speaking"
    perf = "Я скажу́<br>I will speak || Мы ска́жем<br>We will speak"
    pair = "Let us take the verbs \"to speak\" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and \"to do\" (imper.: '''де́лать''', per.: '''сде́лать''')."
    spell = "This states that, if the stem ends in '''ж, ц, ч, ш''', or '''щ''', any unstressed '''o''' in an ending is instead written as '''e'''. For example, 'the good letter' is written '''хорошее письмо''' (and ''not'' '''хорошое письмо''')."
    prep = "The two plural endings, '''ах''' and '''ях''', follow the same rules as the [[Russian/Grammar/Instrumental case|instrumental]] and [[Russian/Grammar/Dative case|dative]] plurals: after consonants, '''o''', or '''а''', use the ending '''ах'''."
    lesson.objective(
        "future-imp",
        "Choose the admitted future line",
        "FORM_SELECTION",
        (future, "Imperfective verbs form the future by preceding the imperfective infinitive with the appropriate conjugation of the perfective verb '''быть'''.", "Imperfective verbs form the future by preceding the imperfective infinitive with the appropriate conjugation of the perfective verb '''быть'''."),
        [
            _wiki(future, imp, "Я бу́ду говори́ть", "I will be speaking"),
            _wiki(future, imp, "Мы бу́дем говори́ть", "We will be speaking"),
            _wiki(future, "Ты бу́дешь говори́ть<br>You will be speaking || Вы бу́дете говори́ть<br>You will be speaking", "Ты бу́дешь говори́ть", "You will be speaking"),
            _wiki(future, "Он бу́дет говори́ть<br>He will be speaking || Они бу́дут говори́ть<br>They will be speaking", "Он бу́дет говори́ть", "He will be speaking"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "Я бу́ду говори́ть", "Мы бу́дем говори́ть", [
                _other("I will be speaking", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_LINE"),
            _check("Which admitted line is the other line in the same frozen record?", "Мы бу́дем говори́ть", "Я бу́ду говори́ть", [
                _other("We will be speaking", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_LINE"),
            _check("Which admitted line is the other line in the same frozen record?", "Ты бу́дешь говори́ть", "Вы бу́дете говори́ть", [
                _other("You will be speaking", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_LINE"),
        ],
        extra=[
            (future, "Ты бу́дешь говори́ть<br>You will be speaking || Вы бу́дете говори́ть<br>You will be speaking", "Вы бу́дете говори́ть", "example"),
        ],
    )
    lesson.objective(
        "future-perf",
        "Choose the admitted completed future line",
        "FORM_SELECTION",
        (future, "Forming the future tense with perfective verbs is relative straight-forward: just conjugate the verb according to the standard rules of verbal conjugation.", "Forming the future tense with perfective verbs is relative straight-forward: just conjugate the verb according to the standard rules of verbal conjugation."),
        [
            _wiki(future, perf, "Я скажу́", "I will speak"),
            _wiki(future, perf, "Мы ска́жем", "We will speak"),
            _field("verbs.csv:сказа'ть", "presfut_sg1"),
            _field("verbs.csv:сказа'ть", "presfut_pl1"),
            _field("verbs.csv:сказа'ть", "presfut_sg2"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "Я скажу́", "Мы ска́жем", [
                _other("I will speak", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_LINE"),
            _check("Which admitted row is the other row of the shown form?", "скажу́", "ска́жем", [
                _other("ска́жешь", "This form is another row of the same word."),
            ], O, feature="FUTURE_LINE"),
            _check("Which admitted line is the other line in the same frozen record?", "Мы ска́жем", "Я скажу́", [
                _other("We will speak", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_LINE"),
        ],
        extra=[
            (future, perf, "I will speak", "meaning"),
            (future, perf, "We will speak", "meaning"),
        ],
    )
    lesson.objective(
        "aspect",
        "Choose the admitted aspect partner",
        "CONTRAST_SELECTION",
        (aspect, "So, verbs in Russian have two words: an ''imperfective'' and a ''perfective'' aspect. For example, 'to speak' is both '''говорить''' (imperfective) and '''сказать''' (perfective).", "So, verbs in Russian have two words: an ''imperfective'' and a ''perfective'' aspect."),
        [
            _wiki(aspect, pair, "говори́ть", "to speak"),
            _wiki(aspect, pair, "сказа́ть", "to speak"),
            _wiki(aspect, pair, "де́лать", "to do"),
            _wiki(aspect, pair, "сде́лать", "to do"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "говори́ть", "сказа́ть", [
                _other("де́лать", "This form is another admitted word."),
            ], feature="ASPECT_PARTNER"),
            _check("Which admitted line is the other line in the same frozen record?", "сказа́ть", "говори́ть", [
                _other("сде́лать", "This form is another admitted word."),
            ], feature="ASPECT_PARTNER"),
            _check("Which admitted line is the other line in the same frozen record?", "де́лать", "сде́лать", [
                _other("говори́ть", "This form is another admitted word."),
            ], feature="ASPECT_PARTNER"),
        ],
    )
    lesson.objective(
        "dative",
        "Choose the admitted indirect-object row",
        "CASE_RECOGNITION",
        ("dative", "The '''dative case''' is traditionally the third grammatical case, and denotes the indirect object of a verb.", "The '''dative case''' is traditionally the third grammatical case, and denotes the indirect object of a verb."),
        [
            _field(book, "sg_nom"),
            _field(book, "sg_dat"),
            _field(book, "pl_nom"),
            _field(table, "sg_nom"),
            _field(table, "sg_dat"),
            _field(table, "pl_nom"),
            _field(house, "sg_nom"),
            _field(house, "sg_dat"),
            _field(house, "pl_nom"),
        ],
        [
            _check("Which admitted row is the other row of the shown form?", "кни́га", "кни́ге", [
                _other("кни́ги", "This form is another row of the same word."),
            ], O, feature="DATIVE_ROW"),
            _check("Which admitted row is the other row of the shown form?", "сто́л", "столу́", [
                _other("столы́", "This form is another row of the same word."),
            ], O, feature="DATIVE_ROW"),
            _check("Which admitted row is the other row of the shown form?", "до́м", "до́му", [
                _other("дома́", "This form is another row of the same word."),
            ], O, feature="DATIVE_ROW"),
        ],
        extra=[
            ("dative", "To give (to)... - '''Давать/дать''' + ''dat''", "Давать/дать", "example"),
        ],
    )
    lesson.objective(
        "spelling",
        "Choose the admitted spelling",
        "PATTERN_CLASSIFICATION",
        (adjectives, spell, spell),
        [
            _field(good, "decl_m_nom"),
            _field(good, "decl_n_nom"),
            _field(good, "decl_f_nom"),
            _wiki(adjectives, spell, "хорошее письмо", "the good letter"),
        ],
        [
            _check("Which admitted spelling is written in the frozen line?", "хорошее письмо", "the good letter", [
                _other("хорошое письмо", "This spelling is the rejected spelling in the same frozen line."),
            ], feature="FIVE_LETTER_SPELLING"),
            _check("Which admitted row is the other row of the shown form?", "хоро́ший", "хоро́шее", [
                _other("хоро́шая", "This form is another row of the same word."),
            ], O, feature="FIVE_LETTER_SPELLING"),
            _check("Which admitted spelling is written in the frozen line?", "хорошое письмо", "хорошее письмо", [
                _other("the good letter", "This admitted wording is the gloss in the same frozen line."),
            ], feature="FIVE_LETTER_SPELLING"),
        ],
        extra=[
            (adjectives, spell, "хорошое письмо", "example"),
            (adjectives, spell, "the good letter", "meaning"),
        ],
    )
    lesson.objective(
        "prep-plural",
        "Choose the admitted plural row",
        "CASE_RECOGNITION",
        ("prepositional", prep, prep),
        [
            _field(book, "pl_nom"),
            _field(book, "pl_prep"),
            _field(book, "sg_dat"),
            _field(table, "pl_nom"),
            _field(table, "pl_prep"),
            _field(table, "sg_dat"),
            _field(house, "pl_nom"),
            _field(house, "pl_prep"),
            _field(house, "sg_dat"),
        ],
        [
            _check("Which admitted row is the other row of the shown form?", "кни́ги", "кни́гах", [
                _other("кни́ге", "This form is another row of the same word."),
            ], O, feature="PREPOSITIONAL_PLURAL"),
            _check("Which admitted row is the other row of the shown form?", "столы́", "стола́х", [
                _other("столу́", "This form is another row of the same word."),
            ], O, feature="PREPOSITIONAL_PLURAL"),
            _check("Which admitted row is the other row of the shown form?", "дома́", "дома́х", [
                _other("до́му", "This form is another row of the same word."),
            ], O, feature="PREPOSITIONAL_PLURAL"),
        ],
    )


def _unit_13(lesson) -> None:
    past = "past"
    future = "future"
    aspect = "aspect"
    pair = "So the English ''past progressive'' sentence  \"I was reading\" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence \"I read\" corresponds to the Russian perfective ('''я прочитал''')"
    report = "In English, when reporting a story, we usually change the tense of what was said: compare \"I ''am'' English\" with \"He said he ''was'' English\" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, \"He said he '''is''' English\" - \"'''Он сказа́л, что он англича́нин'''\", not \"'''Он сказа́л, что он ''был'' англича́нином'''\"."
    told = ":I will tell him in the evening - '''Я скажу ему ве́чером'''\n:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''\n:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''"
    inf = "Let us take the verbs \"to speak\" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and \"to do\" (imper.: '''де́лать''', per.: '''сде́лать''')."
    lesson.objective(
        "past-aspect",
        "Choose the admitted past aspect line",
        "CONTRAST_SELECTION",
        (past, pair, pair),
        [
            _wiki(past, pair, "я читал", "I was reading"),
            _wiki(past, pair, "я прочитал", "I read"),
            _field("verbs.csv:чита'ть", "accented"),
            _field("verbs.csv:чита'ть", "past_m"),
            _field("verbs.csv:чита'ть", "past_f"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "я читал", "я прочитал", [
                _other("I was reading", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="PAST_ASPECT_LINE"),
            _check("Which admitted line is the other line in the same frozen record?", "я прочитал", "я читал", [
                _other("I read", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="PAST_ASPECT_LINE"),
            _check("Which admitted row is the other row of the shown form?", "чита́ть", "чита́л", [
                _other("чита́ла", "This form is another row of the same word."),
            ], O, feature="PAST_ASPECT_LINE"),
        ],
        extra=[
            (past, pair, "I was reading", "meaning"),
            (past, pair, "I read", "meaning"),
        ],
    )
    lesson.objective(
        "tense-keep",
        "Choose the admitted report label",
        "PATTERN_CLASSIFICATION",
        (future, report, report),
        [
            _wiki(future, report, "Он сказа́л, что он англича́нин", "we keep the tense the same"),
            _wiki(future, told, "Я скажу ему ве́чером", "I will tell him in the evening"),
            _wiki(future, told, "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером", "She said that she would speak to him in the evening"),
        ],
        [
            _check("Which admitted label classifies the shown line?", "Он сказа́л, что он англича́нин", "we keep the tense the same", [
                _other("we usually change the tense", "This admitted wording is the other report label in the same frozen line."),
            ], feature="REPORTED_TENSE"),
            _check("Which admitted line is the other line in the same frozen record?", "Я скажу ему ве́чером", "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером", [
                _other("Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски", "This form is another line in the same frozen record."),
            ], feature="REPORTED_TENSE"),
            _check("Which admitted label classifies the shown line?", "Он сказа́л, что он англича́нин", "we keep the tense the same", [
                _other("the reported verb changes tense", "This admitted wording is the other report label in the same frozen line."),
            ], feature="REPORTED_TENSE"),
        ],
        extra=[
            (future, report, "we keep the tense the same", "instruction"),
            (future, report, "we usually change the tense", "instruction"),
            (future, report, "the reported verb changes tense", "instruction"),
            (future, told, "Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски", "example"),
        ],
    )
    lesson.objective(
        "infinitive",
        "Choose the admitted infinitive partner",
        "CONTRAST_SELECTION",
        (aspect, inf, inf),
        [
            _wiki(aspect, inf, "говори́ть", "to speak"),
            _wiki(aspect, inf, "сказа́ть", "to speak"),
            _wiki(aspect, inf, "де́лать", "to do"),
            _wiki(aspect, inf, "сде́лать", "to do"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "говори́ть", "сказа́ть", [
                _other("де́лать", "This form is the other pair in the same frozen line."),
            ], feature="INFINITIVE_ASPECT"),
            _check("Which admitted line is the other line in the same frozen record?", "де́лать", "сде́лать", [
                _other("говори́ть", "This form is the other pair in the same frozen line."),
            ], feature="INFINITIVE_ASPECT"),
            _check("Which admitted line is the other line in the same frozen record?", "сказа́ть", "говори́ть", [
                _other("сде́лать", "This form is the other pair in the same frozen line."),
            ], feature="INFINITIVE_ASPECT"),
        ],
    )
    lesson.objective(
        "reported",
        "Choose the admitted reported line",
        "CONTRAST_SELECTION",
        (future, told, told),
        [
            _wiki(future, told, "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером", "She said that she would speak to him in the evening"),
            _wiki(future, told, "Я скажу ему ве́чером", "I will tell him in the evening"),
            _wiki(future, report, "Он сказа́л, что он англича́нин", "we keep the tense the same"),
            _wiki(future, "'''Он сказа́л, что он ''был'' англича́нином'''", "Он сказа́л, что он ''был'' англича́нином", "был"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "Я скажу ему ве́чером", "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером", [
                _other("Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски", "This form is another line in the same frozen record."),
            ], feature="REPORTED_LINE"),
            _check("Which admitted line is the other line in the same frozen record?", "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером", "Я скажу ему ве́чером", [
                _other("Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски", "This form is another line in the same frozen record."),
            ], feature="REPORTED_LINE"),
            _check("Which admitted line is the other line in the same frozen record?", "Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски", "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером", [
                _other("Я скажу ему ве́чером", "This form is another line in the same frozen record."),
            ], feature="REPORTED_LINE"),
        ],
        extra=[
            (future, report, "we keep the tense the same", "instruction"),
            (future, told, "Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски", "example"),
        ],
    )


def _unit_14(lesson) -> None:
    future = "future"
    past = "past"
    speak = "I will speak - '''Я бу́ду говори́ть'''\n:I will speak for 30 minutes - '''Я поговорю три́дцать мину́т'''"
    work = "I will work - '''Я бу́ду рабо́тать'''\n:I will work for three hours - '''Я порабо́таю три ча́са'''"
    cold = "He was cold - '''Ему было холодно'''\n:They were bored - '''Им было скучно'''"
    lesson.objective(
        "future-aspect",
        "Choose the admitted future aspect line",
        "CONTRAST_SELECTION",
        (future, "Quite simply, the future tense in Russian corresponds to things happening in the future.", "Quite simply, the future tense in Russian corresponds to things happening in the future."),
        [
            _wiki(future, speak, "Я бу́ду говори́ть", "I will speak"),
            _wiki(future, speak, "Я поговорю три́дцать мину́т", "I will speak for 30 minutes"),
            _wiki(future, work, "Я бу́ду рабо́тать", "I will work"),
            _wiki(future, work, "Я порабо́таю три ча́са", "I will work for three hours"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "Я бу́ду говори́ть", "Я поговорю три́дцать мину́т", [
                _other("I will speak", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_ASPECT"),
            _check("Which admitted line is the other line in the same frozen record?", "Я бу́ду рабо́тать", "Я порабо́таю три ча́са", [
                _other("I will work", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_ASPECT"),
            _check("Which admitted line is the other line in the same frozen record?", "Я поговорю три́дцать мину́т", "Я бу́ду говори́ть", [
                _other("I will speak for 30 minutes", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_ASPECT"),
        ],
    )
    lesson.objective(
        "future-person",
        "Choose the admitted future person line",
        "FORM_SELECTION",
        (future, "So, to conjugate the imperfective future with pronouns, we simply conjugate '''быть''' accordingly:", "So, to conjugate the imperfective future with pronouns, we simply conjugate '''быть''' accordingly:"),
        [
            _wiki(future, "Я бу́ду говори́ть<br>I will be speaking || Мы бу́дем говори́ть<br>We will be speaking", "Я бу́ду говори́ть", "I will be speaking"),
            _wiki(future, "Я бу́ду говори́ть<br>I will be speaking || Мы бу́дем говори́ть<br>We will be speaking", "Мы бу́дем говори́ть", "We will be speaking"),
            _field("verbs.csv:сказа'ть", "presfut_sg1"),
            _field("verbs.csv:сказа'ть", "presfut_sg3"),
            _field("verbs.csv:сказа'ть", "presfut_pl3"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "Я бу́ду говори́ть", "Мы бу́дем говори́ть", [
                _other("I will be speaking", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_PERSON"),
            _check("Which admitted row is the other row of the shown form?", "ска́жет", "ска́жут", [
                _other("скажу́", "This form is another row of the same word."),
            ], O, feature="FUTURE_PERSON"),
            _check("Which admitted line is the other line in the same frozen record?", "Мы бу́дем говори́ть", "Я бу́ду говори́ть", [
                _other("We will be speaking", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="FUTURE_PERSON"),
        ],
    )
    lesson.objective(
        "impersonal",
        "Choose the admitted impersonal line",
        "CONTRAST_SELECTION",
        (past, "To refer to states, use the [[Russian/Grammar/Dative case|dative]] for the person or thing; literally 'to me it is interesting', 'to her it is boring'. To refer to these states in the past tense, always use '''было'''; 'she was cold'/'to her it was cold' is '''ей было холодно'''.", "To refer to these states in the past tense, always use '''было'''"),
        [
            _wiki(past, cold, "Ему было холодно", "He was cold"),
            _wiki(past, cold, "Им было скучно", "They were bored"),
            _wiki(past, "To refer to these states in the past tense, always use '''было'''; 'she was cold'/'to her it was cold' is '''ей было холодно'''.", "ей было холодно", "she was cold"),
            _field("verbs.csv:быть", "past_n"),
            _field("verbs.csv:быть", "past_f"),
            _field("verbs.csv:быть", "past_pl"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "Ему было холодно", "Им было скучно", [
                _other("He was cold", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="IMPERSONAL_STATE"),
            _check("Which admitted line is the other line in the same frozen record?", "Им было скучно", "Ему было холодно", [
                _other("They were bored", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="IMPERSONAL_STATE"),
            _check("Which admitted row is the other row of the shown form?", "бы́ло", "была́", [
                _other("бы́ли", "This form is another row of the same word."),
            ], O, feature="IMPERSONAL_STATE"),
        ],
    )


def _unit_15(lesson) -> None:
    aspect = "aspect"
    acc = "accusative"
    read = "verbs.csv:чита'ть"
    say = "verbs.csv:сказа'ть"
    give = "verbs.csv:дать"
    do_pair = "'''делать/сделать''' both mean 'to do' or 'to make'"
    speak_pair = "'''говорить''' (impf.) and '''сказать''' (pf.) - 'to say, speak, tell'"
    request = "*  Купи, пожалуйста, свежие фрукты.- ''' Please, buy fresh fruits.'''"
    lesson.objective(
        "imperative",
        "Choose the admitted request row",
        "FORM_SELECTION",
        (acc, request, "Купи, пожалуйста, свежие фрукты."),
        [
            _field(read, "imperative_sg"),
            _field(read, "imperative_pl"),
            _field(read, "accented"),
            _wiki(acc, request, "Купи, пожалуйста, свежие фрукты.", "Please, buy fresh fruits."),
        ],
        [
            _check("Which admitted row is the other row of the shown form?", "чита́й", "чита́йте", [
                _other("чита́ть", "This form is another row of the same word."),
            ], O, feature="IMPERATIVE_ROW"),
            _check("Which admitted row is the other row of the shown form?", "чита́йте", "чита́й", [
                _other("чита́ть", "This form is another row of the same word."),
            ], O, feature="IMPERATIVE_ROW"),
            _check("Which admitted row is the other row of the shown form?", "чита́ть", "чита́й", [
                _other("чита́йте", "This form is another row of the same word."),
            ], O, feature="IMPERATIVE_ROW"),
        ],
        extra=[
            (acc, request, "Please, buy fresh fruits.", "meaning"),
        ],
    )
    lesson.objective(
        "say-request",
        "Choose the admitted say-request row",
        "FORM_SELECTION",
        (aspect, speak_pair, speak_pair),
        [
            _field(say, "imperative_sg"),
            _field(say, "imperative_pl"),
            _field(say, "accented"),
            _field(give, "imperative_sg"),
            _field(give, "imperative_pl"),
            _field(give, "accented"),
        ],
        [
            _check("Which admitted row is the other row of the shown form?", "скажи́", "скажи́те", [
                _other("сказа́ть", "This form is another row of the same word."),
            ], O, feature="IMPERATIVE_ROW"),
            _check("Which admitted row is the other row of the shown form?", "да́й", "да́йте", [
                _other("дать", "This form is another row of the same word."),
            ], O, feature="IMPERATIVE_ROW"),
            _check("Which admitted row is the other row of the shown form?", "скажи́те", "скажи́", [
                _other("сказа́ть", "This form is another row of the same word."),
            ], O, feature="IMPERATIVE_ROW"),
        ],
    )
    lesson.objective(
        "aspect-consolidate",
        "Choose the admitted aspect pair",
        "CONTRAST_SELECTION",
        (aspect, "In short, all of this is to say that there are two types of verbs in Russian, perfective verbs, and imperfective verbs.", "In short, all of this is to say that there are two types of verbs in Russian, perfective verbs, and imperfective verbs."),
        [
            _wiki(aspect, do_pair, "делать/сделать", "to do"),
            _wiki(aspect, speak_pair, "говорить", "to say, speak, tell"),
            _wiki(aspect, speak_pair, "сказать", "to say, speak, tell"),
            _field("verbs.csv:де'лать", "accented"),
            _field("verbs.csv:сде'лать", "accented"),
        ],
        [
            _check("Which admitted line is the other line in the same frozen record?", "говорить", "сказать", [
                _other("to say, speak, tell", "This admitted wording is the gloss in the same frozen line."),
            ], feature="ASPECT_PAIR"),
            _check("Which admitted label classifies the shown line?", "делать/сделать", "to do", [
                _other("to make", "This admitted wording is the other gloss in the same frozen line."),
            ], feature="ASPECT_PAIR"),
            _check("Which admitted line is the other line in the same frozen record?", "сказать", "говорить", [
                _other("impf.", "This admitted label is in the same frozen line."),
            ], feature="ASPECT_PAIR"),
        ],
        extra=[
            (aspect, speak_pair, "impf.", "instruction"),
            (aspect, do_pair, "to make", "meaning"),
        ],
    )
