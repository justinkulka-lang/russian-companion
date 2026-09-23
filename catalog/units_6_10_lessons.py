"""Typed Units 6–10 checks. Every Russian span and rule label is an exact frozen substring."""

from units_3_5_intake import OPENRUSSIAN_ID, WIKIBOOKS_ID

W = WIKIBOOKS_ID
O = OPENRUSSIAN_ID


def _wiki(page, needle, ru, en):
    return ("wiki", page, needle, ru, en)


def _field(record, name):
    return ("field", record, name)


def _check(prompt, shown, answer, distractors, source=W):
    return (prompt, shown, answer, distractors, source)


def _other(text, rationale):
    return (text, rationale)


def populate(lesson, number: int) -> None:
    {
        6: _unit_6,
        7: _unit_7,
        8: _unit_8,
        9: _unit_9,
        10: _unit_10,
    }[number](lesson)


def _unit_6(lesson) -> None:
    gender = "gender"
    acc = "accusative"
    my_line = "the 'my' in 'my dog' ('''моя собака''') and 'my table' ('''мой стол''') are different"
    lesson.objective(
        "possessive",
        "Choose the admitted possessive form",
        "FORM_SELECTION",
        (gender, my_line, my_line),
        [
            _wiki(gender, "my table' ('''мой стол''')", "мой стол", "my table"),
            _wiki(gender, "my dog' ('''моя собака''')", "моя собака", "my dog"),
            _wiki(acc, "| Our || Наш || Нашего || нашу || Наше || Наши || Наших", "Наш", "Our"),
            _wiki(acc, "| Your <small>''(formal/plural)''</small> || Ваш || Вашего ||вашу || Ваше || Ваши || Ваших", "Ваш", "Your"),
            _field("adjectives.csv:мой", "decl_m_nom"),
            _field("adjectives.csv:мой", "decl_f_nom"),
            _field("adjectives.csv:мой", "decl_n_nom"),
            _field("adjectives.csv:мой", "decl_pl_nom"),
            _field("adjectives.csv:твой", "decl_m_nom"),
            _field("adjectives.csv:твой", "decl_f_nom"),
        ],
        [
            _check("Which admitted line is recorded for my table?", "my table", "мой стол", [
                _other("моя собака", "This admitted line is the other possessive example."),
            ]),
            _check("Which admitted form is recorded for Our?", "Our", "Наш", [
                _other("Ваш", "This admitted form is the other row in the frozen table."),
            ]),
            _check("Which admitted form is recorded for my?", "my", "моя́", [
                _other("мо́й", "This admitted form is a different row of the same word."),
                _other("мои́", "This admitted form is the plural row of the same word."),
            ], O),
            _check("Which admitted form is recorded for your?", "your", "тво́й", [
                _other("твоя́", "This admitted form is a different row of the same word."),
                _other("мо́й", "This admitted form belongs to a different word."),
            ], O),
        ],
    )
    decline = "These do not decline according to the gender or number of the modified noun."
    lesson.objective(
        "third",
        "Choose the admitted third-person form",
        "CONTRAST_SELECTION",
        (acc, decline, decline),
        [
            _wiki(acc, "the masculine and neuter possessive pronouns are the same as the personal pronouns: '''его'''.", "его", "personal pronouns"),
            _wiki(acc, "The feminine is '''её''', and the third-person plural is '''их'''.", "её", "её"),
            _wiki(acc, "The feminine is '''её''', and the third-person plural is '''их'''.", "их", "их"),
            _wiki(acc, "I want his cat - '''Я хочу его кота'''", "Я хочу его кота", "I want his cat"),
            _wiki(acc, "I want his cats - '''Я хочу его котов'''", "Я хочу его котов", "I want his cats"),
            _field("others.csv:его", "accented"),
            _field("others.csv:её", "accented"),
            _field("others.csv:их", "accented"),
        ],
        [
            _check("Which admitted line is recorded for I want his cat?", "I want his cat", "Я хочу его кота", [
                _other("Я хочу его котов", "This admitted line is the other object example."),
            ]),
            _check("Which admitted form is recorded for his in the shown line?", "I want his cat", "его", [
                _other("её", "This admitted form is the other third-person form."),
                _other("их", "This admitted form is the other third-person form."),
            ]),
            _check("Which admitted line is recorded for I want his cats?", "I want his cats", "Я хочу его котов", [
                _other("Я хочу его кота", "This admitted line is the other object example."),
            ]),
        ],
    )
    lesson.objective(
        "object",
        "Choose the admitted object line",
        "FORM_SELECTION",
        (acc, "I like dogs - '''Я люблю собак'''", "I like dogs - '''Я люблю собак'''"),
        [
            _wiki(acc, "I like dogs - '''Я люблю собак'''", "Я люблю собак", "I like dogs"),
            _wiki(acc, "I love you\" as '''Я люблю тебя''' and be grammatically correct, it is more commonly said as '''Я тебя люблю'''", "Я тебя люблю", "I love you"),
            _wiki(acc, "Dogs hate cats. - '''Собаки ненавидят котов.'''", "Собаки ненавидят котов.", "Dogs hate cats."),
            _wiki(acc, "Cats hate dogs. - '''Коты ненавидят собак.'''", "Коты ненавидят собак.", "Cats hate dogs."),
        ],
        [
            _check("Which admitted line is recorded for I like dogs?", "I like dogs", "Я люблю собак", [
                _other("Я тебя люблю", "This admitted line is a different object example."),
            ]),
            _check("Which admitted line is recorded for I love you?", "I love you", "Я тебя люблю", [
                _other("Я люблю собак", "This admitted line is a different object example."),
            ]),
            _check("Which admitted line is recorded for Dogs hate cats?", "Dogs hate cats.", "Собаки ненавидят котов.", [
                _other("Коты ненавидят собак.", "This admitted line reverses the shown pair."),
            ]),
            _check("Which admitted line is recorded for Cats hate dogs?", "Cats hate dogs.", "Коты ненавидят собак.", [
                _other("Собаки ненавидят котов.", "This admitted line reverses the shown pair."),
            ]),
        ],
    )
    feminine = "Feminine words are the only words to have their own accusative ending:"
    lesson.objective(
        "feminine",
        "Choose the admitted object form",
        "FORM_SELECTION",
        (acc, feminine, feminine),
        [
            _field("nouns.csv:книга", "sg_nom"),
            _field("nouns.csv:книга", "sg_acc"),
            _field("nouns.csv:мама", "sg_acc"),
            _field("nouns.csv:женщина", "sg_acc"),
        ],
        [
            _check("Which admitted form is the object row recorded for book?", "book", "кни́гу", [
                _other("кни́га", "This admitted form is the dictionary row, not the object row."),
            ], O),
            _check("Which admitted form is the object row recorded for mom?", "mom", "ма́му", [
                _other("же́нщину", "This admitted object form belongs to a different word."),
            ], O),
            _check("Which admitted form is the object row recorded for woman?", "woman", "же́нщину", [
                _other("кни́гу", "This admitted object form belongs to a different word."),
            ], O),
        ],
    )
    animate = "If they're animated nouns, they follow the genitive rules"
    lesson.objective(
        "animacy",
        "Choose the admitted animate or inanimate object form",
        "CONTRAST_SELECTION",
        (acc, animate, animate),
        [
            _field("nouns.csv:стол", "sg_acc"),
            _field("nouns.csv:дом", "sg_acc"),
            _field("nouns.csv:брат", "sg_nom"),
            _field("nouns.csv:брат", "sg_acc"),
            _field("nouns.csv:учитель", "sg_acc"),
            _field("nouns.csv:друг", "sg_acc"),
        ],
        [
            _check("Which admitted form is the object row recorded for brother?", "brother", "бра́та", [
                _other("брат", "This admitted form is the dictionary row, not the object row."),
            ], O),
            _check("Which admitted form is the object row recorded for table?", "table", "сто́л", [
                _other("бра́та", "This admitted form is the object row of a different word."),
            ], O),
            _check("Which admitted form is the object row recorded for teacher?", "teacher", "учи́теля", [
                _other("дру́га", "This admitted object form belongs to a different word."),
            ], O),
            _check("Which admitted form is the object row recorded for friend?", "friend", "дру́га", [
                _other("бра́та", "This admitted object form belongs to a different word."),
            ], O),
        ],
        extra=[(acc, "So, masculine nouns denoting inanimate objects in the accusative case are the same as in the nominative case.", "So, masculine nouns denoting inanimate objects in the accusative case are the same as in the nominative case.", "instruction")],
    )
    lesson.objective(
        "pronoun",
        "Choose the admitted personal object form",
        "FORM_SELECTION",
        (acc, "| I/me || Я || Меня", "| I/me || Я || Меня"),
        [
            _wiki(acc, "| I/me || Я || Меня", "Меня", "I/me"),
            _wiki(acc, "| We/us || Мы || Нас", "Нас", "We/us"),
            _wiki(acc, "| You || Ты || Тебя", "Тебя", "You"),
            _wiki(acc, "| You || Вы || Вас", "Вас", "You"),
            _wiki(acc, "| He/him || Он || Его", "Его", "He/him"),
            _wiki(acc, "| She/her || Она || Её", "Её", "She/her"),
            _wiki(acc, "| They/them || Они || Их", "Их", "They/them"),
        ],
        [
            _check("Which admitted form is recorded for I/me?", "I/me", "Меня", [
                _other("Нас", "This admitted form is the other person row."),
            ]),
            _check("Which admitted form is recorded for We/us?", "We/us", "Нас", [
                _other("Меня", "This admitted form is the other person row."),
            ]),
            _check("Which admitted form is recorded for She/her?", "She/her", "Её", [
                _other("Его", "This admitted form is the other person row."),
            ]),
            _check("Which admitted form is recorded for They/them?", "They/them", "Их", [
                _other("Вас", "This admitted form is a different person row."),
            ]),
        ],
    )
    direction = "generally the accusative case denotes ''direction''"
    lesson.objective(
        "direction",
        "Identify direction or location",
        "CASE_RECOGNITION",
        (acc, direction, direction),
        [
            _wiki(acc, "I walked ''into'' the house' ('''Я шёл в дом''')", "Я шёл в дом", "the house"),
            _wiki(acc, "I lived in London' ('''Я жил в Лондоне''')", "Я жил в Лондоне", "I lived in London"),
            _wiki(acc, "I walked ''onto'' the street' ('''Я шёл на улицу''')", "Я шёл на улицу", "the street"),
            _wiki(acc, "It's on the table' ('''Оно на столе''')", "Оно на столе", "It's on the table"),
            _wiki(acc, "I drove to the museum. - '''Я поехал в музей.'''", "Я поехал в музей.", "I drove to the museum."),
        ],
        [
            _check("Which admitted function word is recorded for the shown line?", "Я шёл в дом", "direction", [
                _other("location", "This admitted word is the function recorded for the location line."),
            ]),
            _check("Which admitted function word is recorded for the shown line?", "Я жил в Лондоне", "location", [
                _other("direction", "This admitted word is the function recorded for the direction line."),
            ]),
            _check("Which admitted function word is recorded for the shown line?", "Я шёл на улицу", "direction", [
                _other("location", "This admitted word is the function recorded for the location line."),
            ]),
            _check("Which admitted function word is recorded for the shown line?", "Оно на столе", "location", [
                _other("direction", "This admitted word is the function recorded for the direction line."),
            ]),
        ],
        extra=[
            (acc, direction, "direction", "pattern"),
            (acc, "while the prepositional case denotes ''location''", "location", "pattern"),
        ],
    )


def _unit_7(lesson) -> None:
    adj = "adjectives"
    default = "The default form of an adjective is its nominative, masculine, singular form, and this is the form given in dictionaries."
    lesson.objective(
        "default",
        "Choose the admitted dictionary form",
        "FORM_SELECTION",
        (adj, default, default),
        [
            _field("adjectives.csv:новый", "decl_m_nom"),
            _field("adjectives.csv:хороший", "decl_m_nom"),
            _field("adjectives.csv:синий", "decl_m_nom"),
            _field("adjectives.csv:какой", "decl_m_nom"),
        ],
        [
            _check("Which admitted dictionary form is recorded for new?", "new", "но́вый", [
                _other("хоро́ший", "This admitted dictionary form belongs to a different word."),
            ], O),
            _check("Which admitted dictionary form is recorded for good?", "good", "хоро́ший", [
                _other("си́ний", "This admitted dictionary form belongs to a different word."),
            ], O),
            _check("Which admitted dictionary form is recorded for dark blue?", "dark blue", "си́ний", [
                _other("како́й", "This admitted dictionary form belongs to a different word."),
            ], O),
            _check("Which admitted dictionary form is recorded for what kind of?", "what kind of", "како́й", [
                _other("но́вый", "This admitted dictionary form belongs to a different word."),
            ], O),
        ],
    )
    lesson.objective(
        "endings",
        "Choose the admitted agreement line",
        "FORM_SELECTION",
        (adj, '"new pencil" is \'\'\'новый карандаш\'\'\'', '"new pencil" is \'\'\'новый карандаш\'\'\''),
        [
            _wiki(adj, '"new pencil" is \'\'\'новый карандаш\'\'\'', "новый карандаш", "new pencil"),
            _wiki(adj, '"new car" is \'\'\'новая машина\'\'\'', "новая машина", "new car"),
            _wiki(adj, '"new dress" is \'\'\'новое платье\'\'\'', "новое платье", "new dress"),
            _wiki(adj, "the dark blue table - '''синий стол.'''", "синий стол", "the dark blue table"),
            _wiki(adj, "the dark blue book - '''синяя книга'''", "синяя книга", "the dark blue book"),
            _wiki(adj, "the dark blue letter - '''синее письмо'''", "синее письмо", "the dark blue letter"),
            _wiki(adj, "the dark blue books - '''синие книги'''", "синие книги", "the dark blue books"),
            _field("adjectives.csv:новый", "decl_f_nom"),
            _field("adjectives.csv:новый", "decl_n_nom"),
            _field("adjectives.csv:новый", "decl_pl_nom"),
        ],
        [
            _check("Which admitted line is recorded for new pencil?", "new pencil", "новый карандаш", [
                _other("новая машина", "This admitted line is a different agreement example."),
            ]),
            _check("Which admitted line is recorded for new car?", "new car", "новая машина", [
                _other("новое платье", "This admitted line is a different agreement example."),
            ]),
            _check("Which admitted line is recorded for new dress?", "new dress", "новое платье", [
                _other("новый карандаш", "This admitted line is a different agreement example."),
            ]),
            _check("Which admitted line is recorded for the dark blue books?", "the dark blue books", "синие книги", [
                _other("синяя книга", "This admitted line is the singular book example."),
            ]),
            _check("Which admitted form is recorded for new beside the car line?", "new", "но́вая", [
                _other("но́вый", "This admitted form is a different row of the same word."),
                _other("но́вые", "This admitted form is the plural row of the same word."),
            ], O),
        ],
    )
    spelling = "any unstressed '''o''' in an ending is instead written as '''e'''"
    lesson.objective(
        "spelling",
        "Identify the admitted spelling",
        "PATTERN_CLASSIFICATION",
        (adj, spelling, spelling),
        [
            _wiki(adj, "good letter' is written '''хорошее письмо'''", "хорошее письмо", "good letter"),
            _field("adjectives.csv:хороший", "decl_n_nom"),
            _field("adjectives.csv:хороший", "decl_pl_nom"),
        ],
        [
            _check("Which admitted spelling is written in the shown line?", "хорошее письмо", "e", [
                _other("и", "This admitted spelling belongs to the other frozen spelling line."),
            ]),
            _check("Which admitted spelling does the frozen line say to add?", "хоро́шие", "и", [
                _other("ы", "This admitted spelling is the one the frozen line says not to add."),
            ]),
            _check("Which admitted line is recorded for good letter?", "good letter", "хорошее письмо", [
                _other("хоро́шее", "This admitted form is the dictionary cell, not the full example line."),
            ]),
            _check("Which admitted plural spelling row is recorded for good?", "good", "хоро́шие", [
                _other("хоро́шее", "This admitted form is a different row of the same word."),
            ], O),
        ],
        extra=[
            (adj, spelling, "e", "pattern"),
            (adj, "you add '''и''', not '''ы'''", "и", "pattern"),
            (adj, "you add '''и''', not '''ы'''", "ы", "pattern"),
        ],
    )
    lesson.objective(
        "question",
        "Choose the admitted question form",
        "FORM_SELECTION",
        (adj, default, default),
        [
            _field("adjectives.csv:какой", "decl_m_nom"),
            _field("adjectives.csv:какой", "decl_f_nom"),
            _field("adjectives.csv:какой", "decl_n_nom"),
            _field("adjectives.csv:какой", "decl_pl_nom"),
        ],
        [
            _check("Which admitted form is the plural row recorded for what kind of?", "what kind of", "каки́е", [
                _other("кака́я", "This admitted form is a different row of the same word."),
            ], O),
            _check("Which admitted form is the other singular row recorded for what kind of?", "what kind of", "како́е", [
                _other("како́й", "This admitted form is the dictionary row, not the other singular row."),
            ], O),
            _check("Which admitted form is recorded beside the dictionary row for what kind of?", "what kind of", "кака́я", [
                _other("каки́е", "This admitted form is the plural row of the same word."),
            ], O),
            _check("Which admitted dictionary form is recorded for what kind of?", "what kind of", "како́й", [
                _other("како́е", "This admitted form is a different row of the same word."),
            ], O),
        ],
    )
    students = "these good new students\" is '''эти хорошие новые студенты'''"
    lesson.objective(
        "demonstrative",
        "Choose the admitted demonstrative form",
        "FORM_SELECTION",
        (adj, students, students),
        [
            _wiki(adj, "these good new students\" is '''эти хорошие новые студенты'''", "эти хорошие новые студенты", "these good new students"),
            _field("adjectives.csv:этот", "decl_f_nom"),
            _field("adjectives.csv:этот", "decl_n_nom"),
            _field("adjectives.csv:этот", "decl_pl_nom"),
            _field("adjectives.csv:этот", "decl_f_acc"),
        ],
        [
            _check("Which admitted line is recorded for these good new students?", "these good new students", "эти хорошие новые студенты", [
                _other("э́ти", "This admitted form is the dictionary cell, not the full example line."),
            ]),
            _check("Which admitted form is the object row recorded for this?", "this", "э́ту", [
                _other("э́та", "This admitted form is a different row of the same word."),
            ], O),
            _check("Which admitted form is the plural row recorded for this?", "this", "э́ти", [
                _other("э́то", "This admitted form is a different row of the same word."),
            ], O),
            _check("Which admitted form is the other singular row recorded for this?", "this", "э́та", [
                _other("э́ту", "This admitted form is the object row of the same word."),
            ], O),
        ],
    )
    cinema = "They were talking about a good new cinema' - '''Они говорили о хорошем новом кино'''"
    lesson.objective(
        "place",
        "Choose the admitted place-agreement line",
        "FORM_SELECTION",
        (adj, cinema, cinema),
        [
            _wiki(adj, cinema, "Они говорили о хорошем новом кино", "They were talking about a good new cinema"),
            _wiki(adj, "They were in the old Russian school' - '''Они были в старой русской школе'''", "Они были в старой русской школе", "They were in the old Russian school"),
            _wiki(adj, "I want to live in a clean new city - '''Я хочу́ жить в чи́стом но́вом го́роде'''", "Я хочу́ жить в чи́стом но́вом го́роде", "I want to live in a clean new city"),
            _field("adjectives.csv:новый", "decl_m_prep"),
            _field("adjectives.csv:хороший", "decl_m_prep"),
        ],
        [
            _check("Which admitted line is recorded for the cinema example?", "They were talking about a good new cinema", "Они говорили о хорошем новом кино", [
                _other("Они были в старой русской школе", "This admitted line is the school example."),
            ]),
            _check("Which admitted line is recorded for the school example?", "They were in the old Russian school", "Они были в старой русской школе", [
                _other("Я хочу́ жить в чи́стом но́вом го́роде", "This admitted line is the city example."),
            ]),
            _check("Which admitted form is the place row recorded for new?", "new", "но́вом", [
                _other("хоро́шем", "This admitted place row belongs to a different word."),
            ], O),
            _check("Which admitted line is recorded for the city example?", "I want to live in a clean new city", "Я хочу́ жить в чи́стом но́вом го́роде", [
                _other("Они говорили о хорошем новом кино", "This admitted line is the cinema example."),
            ]),
        ],
    )
    rabbit = "I want a new rabbit - '''Я хочу нового кролика.'''"
    lesson.objective(
        "objectadj",
        "Choose the admitted object-agreement line",
        "CONTRAST_SELECTION",
        (adj, rabbit, rabbit),
        [
            _wiki(adj, "I want a new chair and a new letter - '''Я хочу новый стул и новое письмо'''", "Я хочу новый стул и новое письмо", "I want a new chair and a new letter"),
            _wiki(adj, rabbit, "Я хочу нового кролика.", "I want a new rabbit"),
            _wiki(adj, "My Russian cat likes my new dog - '''Моя русская кошка любит мою новую собаку'''", "Моя русская кошка любит мою новую собаку", "My Russian cat likes my new dog"),
            _wiki("accusative", "I like these women. - '''Я люблю ''этих женщин.'''", "этих женщин", "I like these women."),
            _field("adjectives.csv:новый", "decl_f_acc"),
        ],
        [
            _check("Which admitted line is recorded for a new rabbit?", "I want a new rabbit", "Я хочу нового кролика.", [
                _other("Я хочу новый стул и новое письмо", "This admitted line is the chair-and-letter example."),
            ]),
            _check("Which admitted line is recorded for a new chair and a new letter?", "I want a new chair and a new letter", "Я хочу новый стул и новое письмо", [
                _other("Я хочу нового кролика.", "This admitted line is the rabbit example."),
            ]),
            _check("Which admitted line is recorded for I like these women?", "I like these women.", "этих женщин", [
                _other("Моя русская кошка любит мою новую собаку", "This admitted line is a different object example."),
            ]),
            _check("Which admitted form is the object row recorded for new?", "new", "но́вую", [
                _other("но́вый", "This admitted form is a different row of the same word."),
            ], O),
        ],
    )


def _unit_8(lesson) -> None:
    gender = "gender"
    plural = "There's no 'masculine plural' and 'feminine plural' - there's just, 'plural'."
    lesson.objective(
        "plural",
        "Choose the admitted plural form",
        "FORM_SELECTION",
        (gender, plural, plural),
        [
            _field("nouns.csv:стол", "pl_nom"),
            _field("nouns.csv:город", "pl_nom"),
            _field("nouns.csv:книга", "pl_nom"),
            _field("nouns.csv:окно", "pl_nom"),
            _field("nouns.csv:мама", "pl_nom"),
            _field("nouns.csv:дом", "pl_nom"),
            _field("nouns.csv:слово", "pl_nom"),
        ],
        [
            _check("Which admitted plural form is recorded for table?", "table", "столы́", [
                _other("кни́ги", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted plural form is recorded for town?", "town", "города́", [
                _other("дома́", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted plural form is recorded for window?", "window", "о́кна", [
                _other("слова́", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted plural form is recorded for mom?", "mom", "ма́мы", [
                _other("кни́ги", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted plural form is recorded for house?", "house", "дома́", [
                _other("столы́", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted plural form is recorded for word?", "word", "слова́", [
                _other("о́кна", "This admitted plural form belongs to a different word."),
            ], O),
        ],
    )
    add_i = "you add '''и''', not '''ы'''"
    lesson.objective(
        "spelling",
        "Identify the admitted plural spelling",
        "PATTERN_CLASSIFICATION",
        ("adjectives", add_i, add_i),
        [
            _wiki("adjectives", "good letter' is written '''хорошее письмо'''", "хорошее письмо", "good letter"),
            _field("adjectives.csv:хороший", "decl_pl_nom"),
            _field("nouns.csv:книга", "pl_nom"),
            _field("nouns.csv:день", "pl_nom"),
        ],
        [
            _check("Which admitted spelling does the frozen line say to add?", "хоро́шие", "и", [
                _other("ы", "This admitted spelling is the one the frozen line says not to add."),
            ]),
            _check("Which admitted plural form is recorded for book?", "book", "кни́ги", [
                _other("дни", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted plural form is recorded for day?", "day", "дни", [
                _other("кни́ги", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted line is recorded for good letter?", "good letter", "хорошее письмо", [
                _other("дни", "This admitted plural form belongs to a different word."),
            ]),
            _check("Which admitted plural form is recorded for good?", "good", "хоро́шие", [
                _other("кни́ги", "This admitted plural form belongs to a different word."),
            ], O),
        ],
        extra=[
            ("adjectives", add_i, "и", "pattern"),
            ("adjectives", add_i, "ы", "pattern"),
        ],
    )
    lesson.objective(
        "irregular",
        "Choose the admitted irregular plural",
        "FORM_SELECTION",
        ("genitive", "Table, five tables - '''Стол, пять столов'''", "Table, five tables - '''Стол, пять столов'''"),
        [
            _wiki("genitive", "Table, five tables - '''Стол, пять столов'''", "пять столов", "five tables"),
            _field("nouns.csv:брат", "pl_nom"),
            _field("nouns.csv:друг", "pl_nom"),
            _field("nouns.csv:человек", "pl_nom"),
            _field("nouns.csv:сын", "pl_nom"),
        ],
        [
            _check("Which admitted plural form is recorded for brother?", "brother", "бра́тья", [
                _other("друзья́", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted plural form is recorded for person?", "person", "лю́ди", [
                _other("сыновья́", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted line is recorded for five tables?", "five tables", "пять столов", [
                _other("друзья́", "This admitted plural form belongs to a different word."),
            ]),
            _check("Which admitted plural form is recorded for friend?", "friend", "друзья́", [
                _other("бра́тья", "This admitted plural form belongs to a different word."),
            ], O),
        ],
    )
    lesson.objective(
        "agreement",
        "Choose the admitted plural agreement form",
        "FORM_SELECTION",
        ("adjectives", "the dark blue books - '''синие книги'''", "the dark blue books - '''синие книги'''"),
        [
            _wiki("adjectives", "the dark blue books - '''синие книги'''", "синие книги", "the dark blue books"),
            _wiki("accusative", "| Our || Наш || Нашего || нашу || Наше || Наши || Наших", "Наши", "Our"),
            _field("adjectives.csv:мой", "decl_pl_nom"),
            _field("adjectives.csv:новый", "decl_pl_nom"),
            _field("adjectives.csv:этот", "decl_pl_nom"),
        ],
        [
            _check("Which admitted line is recorded for the dark blue books?", "the dark blue books", "синие книги", [
                _other("Наши", "This admitted form is a different plural row."),
            ]),
            _check("Which admitted form is the plural row recorded for my?", "my", "мои́", [
                _other("но́вые", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted form is the plural row recorded for this?", "this", "э́ти", [
                _other("мои́", "This admitted plural form belongs to a different word."),
            ], O),
            _check("Which admitted form is the plural row recorded for new?", "new", "но́вые", [
                _other("э́ти", "This admitted plural form belongs to a different word."),
            ], O),
        ],
    )
    absent = "There are no restaurants - '''Нет рестора́нов'''"
    lesson.objective(
        "availability",
        "Choose between the admitted existence and absence lines",
        "CONTRAST_SELECTION",
        ("genitive", absent, absent),
        [
            _wiki("genitive", "There are Frenchmen on this street - '''На э́той у́лице есть францу́зы'''", "На э́той у́лице есть францу́зы", "There are Frenchmen on this street"),
            _wiki("genitive", "There are old buildings in Moscow - '''В Москве́ есть ста́рые зда́ния'''", "В Москве́ есть ста́рые зда́ния", "There are old buildings in Moscow"),
            _wiki("genitive", "This is David's house - '''Э́то - дом Дэвида'''", "Э́то - дом Дэвида", "This is David's house"),
            _wiki("genitive", absent, "Нет рестора́нов", "There are no restaurants"),
            _field("others.csv:есть", "accented"),
        ],
        [
            _check("Which admitted line is recorded for There are no restaurants?", "There are no restaurants", "Нет рестора́нов", [
                _other("На э́той у́лице есть францу́зы", "This admitted line is an existence example."),
            ]),
            _check("Which admitted line is recorded for There are Frenchmen on this street?", "There are Frenchmen on this street", "На э́той у́лице есть францу́зы", [
                _other("Нет рестора́нов", "This admitted line is the absence example."),
            ]),
            _check("Which admitted line is recorded for This is David's house?", "This is David's house", "Э́то - дом Дэвида", [
                _other("В Москве́ есть ста́рые зда́ния", "This admitted line is a different existence example."),
            ]),
            _check("Which admitted line is recorded for There are old buildings in Moscow?", "There are old buildings in Moscow", "В Москве́ есть ста́рые зда́ния", [
                _other("Нет рестора́нов", "This admitted line is the absence example."),
            ]),
        ],
    )


def _num(cell, form, gloss):
    return _wiki("numbers", cell, form, gloss)


def _unit_9(lesson) -> None:
    cardinals = [
        ("0 ноль", "ноль", "0"),
        ("1 оди́н (одна́, одно́, одни́)", "оди́н", "1"),
        ("2 два (две)", "два", "2"),
        ("3 три", "три", "3"),
        ("4 четы́ре", "четы́ре", "4"),
        ("5 пять", "пять", "5"),
        ("6 шесть", "шесть", "6"),
        ("7 семь", "семь", "7"),
        ("8 во́семь", "во́семь", "8"),
        ("9 де́вять", "де́вять", "9"),
        ("10 де́сять", "де́сять", "10"),
    ]
    teens = [
        ("11 оди́ннадцать", "оди́ннадцать", "11"),
        ("12 двена́дцать", "двена́дцать", "12"),
        ("13 трина́дцать", "трина́дцать", "13"),
        ("14 четы́рнадцать", "четы́рнадцать", "14"),
        ("15 пятна́дцать", "пятна́дцать", "15"),
        ("16 шестна́дцать", "шестна́дцать", "16"),
        ("17 семна́дцать", "семна́дцать", "17"),
        ("18 восемна́дцать", "восемна́дцать", "18"),
        ("19 девятна́дцать", "девятна́дцать", "19"),
        ("20 два́дцать", "два́дцать", "20"),
    ]
    tens = [
        ("30 три́дцать", "три́дцать", "30"),
        ("40 со́рок", "со́рок", "40"),
        ("50 пятьдеся́т", "пятьдеся́т", "50"),
        ("60 шестьдеся́т", "шестьдеся́т", "60"),
        ("70 се́мьдесят", "се́мьдесят", "70"),
        ("80 во́семьдесят", "во́семьдесят", "80"),
        ("90 девяно́сто", "девяно́сто", "90"),
    ]
    hundreds = [
        ("100 сто", "сто", "100"),
        ("200 две́сти", "две́сти", "200"),
        ("300 три́ста", "три́ста", "300"),
        ("400 четы́реста", "четы́реста", "400"),
        ("500 пятьсо́т", "пятьсо́т", "500"),
        ("600 шестьсо́т", "шестьсо́т", "600"),
        ("700 семьсо́т", "семьсо́т", "700"),
        ("800 восемьсо́т", "восемьсо́т", "800"),
        ("900 девятьсо́т", "девятьсо́т", "900"),
    ]
    lesson.objective(
        "cardinal",
        "Choose the admitted form for 0 through 10",
        "NUMBER_FORM_SELECTION",
        ("numbers", "1 оди́н (одна́, одно́, одни́)", "1 оди́н (одна́, одно́, одни́)"),
        [_num(*row) for row in cardinals],
        [
            _check("Which admitted form is recorded for 0?", "0", "ноль", [_other("оди́н", "This admitted form is a different number in the same table.")]),
            _check("Which admitted form is recorded for 1?", "1", "оди́н", [_other("два", "This admitted form is a different number in the same table.")]),
            _check("Which admitted form is recorded for 5?", "5", "пять", [_other("шесть", "This admitted form is a different number in the same table.")]),
            _check("Which admitted form is recorded for 10?", "10", "де́сять", [_other("де́вять", "This admitted form is a different number in the same table.")]),
            _check("Which admitted form is recorded for 4?", "4", "четы́ре", [_other("три", "This admitted form is a different number in the same table.")]),
        ],
    )
    lesson.objective(
        "teens",
        "Choose the admitted form for 11 through 20",
        "NUMBER_FORM_SELECTION",
        ("numbers", "11 оди́ннадцать", "11 оди́ннадцать"),
        [_num(*row) for row in teens],
        [
            _check("Which admitted form is recorded for 11?", "11", "оди́ннадцать", [_other("двена́дцать", "This admitted form is a different teen.")]),
            _check("Which admitted form is recorded for 14?", "14", "четы́рнадцать", [_other("пятна́дцать", "This admitted form is a different teen.")]),
            _check("Which admitted form is recorded for 19?", "19", "девятна́дцать", [_other("два́дцать", "This admitted form is the following ten, not the teen.")]),
            _check("Which admitted form is recorded for 12?", "12", "двена́дцать", [_other("два́дцать", "This admitted form is the ten, not the teen.")]),
        ],
    )
    lesson.objective(
        "tens",
        "Choose the admitted tens form",
        "NUMBER_FORM_SELECTION",
        ("numbers", "30 три́дцать", "30 три́дцать"),
        [_num(*row) for row in tens] + [_num("20 два́дцать", "два́дцать", "20")],
        [
            _check("Which admitted form is recorded for 30?", "30", "три́дцать", [_other("со́рок", "This admitted form is a different ten.")]),
            _check("Which admitted form is recorded for 50?", "50", "пятьдеся́т", [_other("шестьдеся́т", "This admitted form is a different ten.")]),
            _check("Which admitted form is recorded for 90?", "90", "девяно́сто", [_other("во́семьдесят", "This admitted form is a different ten.")]),
            _check("Which admitted form is recorded for 20?", "20", "два́дцать", [_other("двена́дцать", "This admitted form is the teen, not the ten.")]),
        ],
        extra=[("numbers", "12 двена́дцать", "двена́дцать", "example")],
    )
    lesson.objective(
        "hundreds",
        "Choose the admitted hundreds form",
        "NUMBER_FORM_SELECTION",
        ("numbers", "200 две́сти", "200 две́сти"),
        [_num(*row) for row in hundreds],
        [
            _check("Which admitted form is recorded for 100?", "100", "сто", [_other("две́сти", "This admitted form is a different hundred.")]),
            _check("Which admitted form is recorded for 300?", "300", "три́ста", [_other("три́дцать", "This admitted form is the ten, not the hundred.")]),
            _check("Which admitted form is recorded for 500?", "500", "пятьсо́т", [_other("пять", "This admitted form is the one-digit number, not the hundred.")]),
            _check("Which admitted form is recorded for 900?", "900", "девятьсо́т", [_other("девяно́сто", "This admitted form is the ten, not the hundred.")]),
        ],
        extra=[
            ("numbers", "30 три́дцать", "три́дцать", "example"),
            ("numbers", "5 пять", "пять", "example"),
            ("numbers", "90 девяно́сто", "девяно́сто", "example"),
        ],
    )
    thousand = "1,000 ты́сяча"
    band_2 = "2,000/3,000/4,000 две/три/четы́ре ты́сячи (use genitive singular, see below)"
    band_5 = "5,000 ~ 9,000 пять ~ де́вять ты́сяч (use genitive plural, see below)"
    lesson.objective(
        "thousands",
        "Choose the admitted thousands or composed form",
        "NUMBER_FORM_SELECTION",
        ("numbers", thousand, thousand),
        [
            _num(thousand, "ты́сяча", "1,000"),
            _num("21 два́дцать оди́н", "два́дцать оди́н", "21"),
            _num("101 сто оди́н", "сто оди́н", "101"),
            _num("215 две́сти пятна́дцать", "две́сти пятна́дцать", "215"),
            _num("333 три́ста три́дцать три", "три́ста три́дцать три", "333"),
            _num("1,984 ты́сяча девятьсо́т во́семьдесят четы́ре", "ты́сяча девятьсо́т во́семьдесят четы́ре", "1,984"),
            _wiki("numbers", band_2, "две/три/четы́ре ты́сячи", "2,000/3,000/4,000"),
            _wiki("numbers", band_5, "пять ~ де́вять ты́сяч", "5,000 ~ 9,000"),
        ],
        [
            _check("Which admitted form is recorded for 21?", "21", "два́дцать оди́н", [
                _other("два́дцать", "This admitted form is only the ten, not the composed number."),
            ]),
            _check("Which admitted form is recorded for 101?", "101", "сто оди́н", [
                _other("сто", "This admitted form is only the hundred, not the composed number."),
            ]),
            _check("Which admitted form is recorded for 215?", "215", "две́сти пятна́дцать", [
                _other("две́сти", "This admitted form is only the hundred, not the composed number."),
            ]),
            _check("Which admitted form is recorded for 1,984?", "1,984", "ты́сяча девятьсо́т во́семьдесят четы́ре", [
                _other("ты́сяча", "This admitted form is only the thousand, not the composed number."),
            ]),
            _check("Which admitted form is recorded for 2,000/3,000/4,000?", "2,000/3,000/4,000", "две/три/четы́ре ты́сячи", [
                _other("пять ~ де́вять ты́сяч", "This admitted form is the other thousands band."),
            ]),
        ],
        extra=[
            ("numbers", "20 два́дцать", "два́дцать", "example"),
            ("numbers", "100 сто", "сто", "example"),
            ("numbers", "200 две́сти", "две́сти", "example"),
        ],
    )
    dva = "The numeral два has two forms: два for masculine and neuter items and две for feminine items."
    lesson.objective(
        "odindva",
        "Choose the admitted form of one or two",
        "CONTRAST_SELECTION",
        ("numbers", dva, dva),
        [
            _wiki("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "один мальчик", "one boy"),
            _wiki("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одна девушка", "one girl"),
            _wiki("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одно животное", "one animal"),
            _wiki("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одни джинсы", "one [pair of] jeans"),
            _num("1 оди́н (одна́, одно́, одни́)", "одна́", "1"),
            _num("1 оди́н (одна́, одно́, одни́)", "одно́", "1"),
            _num("1 оди́н (одна́, одно́, одни́)", "одни́", "1"),
            _num("2 два (две)", "две", "2"),
            _field("others.csv:один", "accented"),
            _field("others.csv:два", "accented"),
        ],
        [
            _check("Which admitted line is recorded for one boy?", "one boy", "один мальчик", [
                _other("одна девушка", "This admitted line is the other one-item example."),
            ]),
            _check("Which admitted line is recorded for one girl?", "one girl", "одна девушка", [
                _other("одно животное", "This admitted line is the other one-item example."),
            ]),
            _check("Which admitted line is recorded for one animal?", "one animal", "одно животное", [
                _other("одни джинсы", "This admitted line is the other one-item example."),
            ]),
            _check("Which admitted form is recorded for the other form of two?", "2", "две", [
                _other("два", "This admitted form is the other form on the same frozen line."),
            ]),
        ],
    )
    rule_scope = "If the item needs to be in the nominative or inanimate accusative case, use the following rule."
    rule_1 = "1 -> nominative"
    rule_2 = "2, 3, 4 -> genitive singular"
    rule_5 = "5 and more -> genitive plural"
    teens_rule = "11, 12, 13, 14"
    last_two = "Numerals larger than 99 follow the above rules using the last two digits."
    last_digit = "Numerals larger than twenty follow the above rules using the last digit."
    outside = "When the items at hand belong not in the nominative or inanimate accusative case, the numerals themselves must be declined"
    lesson.objective(
        "quantity",
        "Choose the admitted counted form",
        "NUMBER_FORM_SELECTION",
        ("numbers", last_digit, last_digit),
        [
            _num("один час", "один час", "один час"),
            _num("два часа", "два часа", "два часа"),
            _num("пять часов", "пять часов", "пять часов"),
            _num("двадцать один час", "двадцать один час", "двадцать один час"),
            _num("двадцать два часа", "двадцать два часа", "двадцать два часа"),
            _wiki("numbers", "две девушки (two girls)", "две девушки", "two girls"),
            _wiki("numbers", "пять девушек (five girls)", "пять девушек", "five girls"),
            _wiki("numbers", "шестнадцать рублей (sixteen rubles)", "шестнадцать рублей", "sixteen rubles"),
            _wiki("numbers", "сорок один год (forty-one years)", "сорок один год", "forty-one years"),
            _wiki("numbers", "пять лет", "пять лет", "пять лет"),
            _wiki("numbers", "двести три собаки (two hundred and three dogs)", "двести три собаки", "two hundred and three dogs"),
            _wiki("numbers", "двести тринадцать собак (two hundred and thirteen dogs)", "двести тринадцать собак", "two hundred and thirteen dogs"),
            _wiki("genitive", "22 kopecks - '''Два́дцать две копе́йки'''", "Два́дцать две копе́йки", "22 kopecks"),
            _wiki("genitive", "25 kopecks - '''Два́дцать пять копе́ек'''", "Два́дцать пять копе́ек", "25 kopecks"),
            _field("nouns.csv:стол", "sg_gen"),
            _field("nouns.csv:стол", "pl_gen"),
        ],
        [
            _check("Which admitted counted line follows the one-item pattern?", rule_1, "один час", [
                _other("два часа", "This admitted line is the two-item counted example."),
                _other("пять часов", "This admitted line is the five-and-more counted example."),
            ]),
            _check("Which admitted counted line follows the two-to-four pattern?", rule_2, "два часа", [
                _other("один час", "This admitted line is the one-item counted example."),
                _other("пять часов", "This admitted line is the five-and-more counted example."),
            ]),
            _check("Which admitted counted line follows the five-and-more pattern?", rule_5, "пять часов", [
                _other("два часа", "This admitted line is the two-item counted example."),
            ]),
            _check("Which admitted counted line follows the shown final-one cardinal?", "два́дцать оди́н", "двадцать один час", [
                _other("двадцать два часа", "This admitted line is the final-two counted example."),
                _other("пять часов", "This admitted line is the five-and-more counted example."),
            ]),
            _check("Which admitted counted line follows the teen span?", teens_rule, "шестнадцать рублей", [
                _other("две девушки", "This admitted line is the two-item example, and the teen span uses the last rule."),
            ]),
            _check("Which admitted counted line is recorded for forty-one years?", "forty-one years", "сорок один год", [
                _other("пять лет", "This admitted line is the five-and-more year example."),
            ]),
            _check("Which admitted counted line is recorded for two hundred and thirteen dogs?", "two hundred and thirteen dogs", "двести тринадцать собак", [
                _other("двести три собаки", "This admitted line uses different final digits."),
            ]),
        ],
        extra=[
            ("numbers", rule_1, rule_1, "pattern"),
            ("numbers", rule_2, rule_2, "pattern"),
            ("numbers", rule_5, rule_5, "pattern"),
            ("genitive", "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on.", teens_rule, "pattern"),
            ("numbers", "21 два́дцать оди́н", "два́дцать оди́н", "example"),
        ],
    )
    lesson.objective(
        "pattern",
        "Identify the admitted quantity pattern",
        "PATTERN_CLASSIFICATION",
        ("genitive", "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on.", "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on."),
        [
            _num("один час", "один час", "один час"),
            _num("три кошки (three cats)", "три кошки", "three cats"),
            _num("пять девушек (five girls)", "пять девушек", "five girls"),
            _wiki("numbers", "Я видел двух друзей (I saw two friends)", "Я видел двух друзей", "I saw two friends"),
            _wiki("genitive", "There is one cat - '''Есть оди́н кот'''", "Есть оди́н кот", "There is one cat"),
            _wiki("genitive", "Five roubles - '''Пять рубле́й'''", "Пять рубле́й", "Five roubles"),
        ],
        [
            _check("Which admitted rule applies to the shown line?", "один час", rule_1, [
                _other(rule_2, "This admitted rule is the two-to-four line."),
                _other(rule_5, "This admitted rule is the five-and-more line."),
            ]),
            _check("Which admitted rule applies to the shown line?", "три кошки", rule_2, [
                _other(rule_1, "This admitted rule is the one-item line."),
                _other(rule_5, "This admitted rule is the five-and-more line."),
            ]),
            _check("Which admitted rule applies to the shown line?", "пять девушек", rule_5, [
                _other(rule_2, "This admitted rule is the two-to-four line."),
            ]),
            _check("Which admitted rule applies to 11, 12, 13, and 14?", teens_rule, rule_5, [
                _other(rule_2, "This admitted rule is the two-to-four line, and the frozen teen span applies the last rule."),
                _other(rule_1, "This admitted rule is the one-item line, and the frozen teen span applies the last rule."),
            ]),
            _check("Which admitted scope sentence covers the shown line?", "Я видел двух друзей", outside, [
                _other(rule_scope, "This admitted sentence is the other scope in the frozen paragraph."),
            ]),
        ],
        extra=[
            ("numbers", rule_1, rule_1, "pattern"),
            ("numbers", rule_2, rule_2, "pattern"),
            ("numbers", rule_5, rule_5, "pattern"),
            ("numbers", outside, outside, "instruction"),
            ("numbers", rule_scope, rule_scope, "instruction"),
            ("genitive", "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on.", teens_rule, "pattern"),
            ("numbers", last_two, last_two, "instruction"),
        ],
    )


def _unit_10(lesson) -> None:
    gen = "genitive"
    have = "to say \"I have X\": '''У меня есть Х'''"
    lesson.objective(
        "have",
        "Choose the admitted possession line",
        "FORM_SELECTION",
        (gen, have, have),
        [
            _wiki(gen, have, "У меня есть Х", "I have X"),
            _wiki(gen, "Do you have a son? - '''У вас есть сын?'''", "У вас есть сын?", "Do you have a son?"),
            _wiki(gen, "Yes, I have a son - '''Да, у меня есть сын.'''", "Да, у меня есть сын.", "Yes, I have a son"),
            _wiki(gen, "Your husband has one friend - '''У твоего́ му́жа есть оди́н друг'''", "У твоего́ му́жа есть оди́н друг", "Your husband has one friend"),
            _wiki("adjectives", "My new husband has many friends - '''У моего нового мужа есть много друзе́й'''", "У моего нового мужа есть много друзе́й", "My new husband has many friends"),
            _field("others.csv:есть", "accented"),
        ],
        [
            _check("Which admitted line is recorded for I have X?", "I have X", "У меня есть Х", [
                _other("У вас есть сын?", "This admitted line is a different possession example."),
            ]),
            _check("Which admitted line is recorded for Do you have a son?", "Do you have a son?", "У вас есть сын?", [
                _other("Да, у меня есть сын.", "This admitted line is a different possession example."),
            ]),
            _check("Which admitted line is recorded for Yes, I have a son?", "Yes, I have a son", "Да, у меня есть сын.", [
                _other("У твоего́ му́жа есть оди́н друг", "This admitted line is a different possession example."),
            ]),
            _check("Which admitted line is recorded for Your husband has one friend?", "Your husband has one friend", "У твоего́ му́жа есть оди́н друг", [
                _other("У моего нового мужа есть много друзе́й", "This admitted line is a different possession example."),
            ]),
            _check("Which admitted line is recorded for My new husband has many friends?", "My new husband has many friends", "У моего нового мужа есть много друзе́й", [
                _other("У меня есть Х", "This admitted line is a different possession example."),
            ]),
        ],
    )
    son = "No, I don't have a son - '''У меня́ нет сы́на'''"
    lesson.objective(
        "absence",
        "Choose the admitted absence line",
        "CONTRAST_SELECTION",
        (gen, son, son),
        [
            _wiki(gen, son, "У меня́ нет сы́на", "No, I don't have a son"),
            _wiki(gen, "There are no restaurants - '''Нет рестора́нов'''", "Нет рестора́нов", "There are no restaurants"),
            _wiki(gen, "There are no old buildings in Moscow - '''В Москве́ нет ста́рых зда́ний'''", "В Москве́ нет ста́рых зда́ний", "There are no old buildings in Moscow"),
            _wiki(gen, "My brother doesn't have any money - '''У моего́ бра́та нет де́нег'''", "У моего́ бра́та нет де́нег", "My brother doesn't have any money"),
            _wiki(gen, "Yes, I have a son - '''Да, у меня есть сын.'''", "Да, у меня есть сын.", "Yes, I have a son"),
            _field("others.csv:нет", "accented"),
        ],
        [
            _check("Which admitted line is recorded for No, I don't have a son?", "No, I don't have a son", "У меня́ нет сы́на", [
                _other("Да, у меня есть сын.", "This admitted line is the possession example."),
            ]),
            _check("Which admitted line is recorded for There are no restaurants?", "There are no restaurants", "Нет рестора́нов", [
                _other("У меня́ нет сы́на", "This admitted line is a different absence example."),
            ]),
            _check("Which admitted line is recorded for There are no old buildings in Moscow?", "There are no old buildings in Moscow", "В Москве́ нет ста́рых зда́ний", [
                _other("У моего́ бра́та нет де́нег", "This admitted line is a different absence example."),
            ]),
            _check("Which admitted line is recorded for My brother doesn't have any money?", "My brother doesn't have any money", "У моего́ бра́та нет де́нег", [
                _other("Нет рестора́нов", "This admitted line is a different absence example."),
            ]),
            _check("Which admitted form is recorded for no?", "no", "нет", [
                _other("есть", "This admitted form is the possession word, not the absence word."),
            ], O),
        ],
    )
    table = "'''English''' || I || We || You || You || He/she/it|| They\n|-\n! '''Nominative'''\n| Я || Мы || Ты || Вы || Он/она/оно || Они\n|-\n! '''Genitive'''\n| Меня́ || Нас || Тебя́ || Вас || Его́/её || Их"
    lesson.objective(
        "pronoun",
        "Choose the admitted genitive pronoun form",
        "FORM_SELECTION",
        (gen, "For him/it - '''Для него́'''", "For him/it - '''Для него́'''"),
        [
            _wiki(gen, "For him/it - '''Для него́'''", "Для него́", "For him/it"),
            _wiki(gen, "Without her - '''Без неё'''", "Без неё", "Without her"),
            _wiki(gen, table, "Меня́", "I"),
            _wiki(gen, table, "Его́/её", "He/she/it"),
            _wiki(gen, table, "Их", "They"),
            _wiki(gen, table, "Я", "I"),
            _field("others.csv:его", "accented"),
            _field("others.csv:её", "accented"),
            _field("others.csv:их", "accented"),
        ],
        [
            _check("Which admitted cell is the genitive row recorded for I?", "I", "Меня́", [
                _other("Я", "This admitted cell is the other row in the same frozen table."),
            ]),
            _check("Which admitted line is recorded for For him/it?", "For him/it", "Для него́", [
                _other("Без неё", "This admitted line is the other example in this group."),
            ]),
            _check("Which admitted line is recorded for Without her?", "Without her", "Без неё", [
                _other("Для него́", "This admitted line is the other example in this group."),
            ]),
            _check("Which admitted cell is recorded for They?", "They", "Их", [
                _other("Его́/её", "This admitted cell is a different row in the same frozen table."),
            ]),
            _check("Which admitted stressed form is recorded for their?", "their", "их", [
                _other("его́", "This admitted form belongs to a different word."),
            ], O),
        ],
    )
    lesson.objective(
        "preposition",
        "Choose the admitted preposition line",
        "FORM_SELECTION",
        (gen, "I want a present for my friend - '''Я хочу́ пода́рок для моего́ дру́га'''", "I want a present for my friend - '''Я хочу́ пода́рок для моего́ дру́га'''"),
        [
            _wiki(gen, "I want a present for my friend - '''Я хочу́ пода́рок для моего́ дру́га'''", "Я хочу́ пода́рок для моего́ дру́га", "I want a present for my friend"),
            _wiki(gen, "Black coffee - '''Ко́фе без молока́'''", "Ко́фе без молока́", "Black coffee"),
            _wiki(gen, "I'm from England - '''Я из А́нглии'''", "Я из А́нглии", "I'm from England"),
            _wiki(gen, "Get away from the grass - '''Отойти от травы'''", "Отойти от травы", "Get away from the grass"),
            _wiki(gen, "I am standing by the fence - '''Я стою́ у забо́ра'''", "Я стою́ у забо́ра", "I am standing by the fence"),
            _wiki(gen, "She is sitting by the window - '''Она сидит у окна́'''", "Она сидит у окна́", "She is sitting by the window"),
            _field("others.csv:для", "accented"),
            _field("others.csv:без", "accented"),
            _field("others.csv:из", "accented"),
            _field("others.csv:от", "accented"),
            _field("others.csv:у", "accented"),
        ],
        [
            _check("Which admitted line is recorded for I want a present for my friend?", "I want a present for my friend", "Я хочу́ пода́рок для моего́ дру́га", [
                _other("Ко́фе без молока́", "This admitted line uses a different preposition example."),
            ]),
            _check("Which admitted line is recorded for Black coffee?", "Black coffee", "Ко́фе без молока́", [
                _other("Я из А́нглии", "This admitted line uses a different preposition example."),
            ]),
            _check("Which admitted line is recorded for I'm from England?", "I'm from England", "Я из А́нглии", [
                _other("Отойти от травы", "This admitted line uses a different preposition example."),
            ]),
            _check("Which admitted line is recorded for I am standing by the fence?", "I am standing by the fence", "Я стою́ у забо́ра", [
                _other("Она сидит у окна́", "This admitted line is a different place example."),
            ]),
            _check("Which admitted line is recorded for She is sitting by the window?", "She is sitting by the window", "Она сидит у окна́", [
                _other("Я стою́ у забо́ра", "This admitted line is a different place example."),
            ]),
        ],
    )
    cheese = "A kilogram of good Russian cheese - '''Кило́ хоро́шего ру́сского сы́ра'''"
    lesson.objective(
        "adjective",
        "Choose the admitted genitive agreement form",
        "FORM_SELECTION",
        (gen, cheese, cheese),
        [
            _wiki(gen, cheese, "Кило́ хоро́шего ру́сского сы́ра", "A kilogram of good Russian cheese"),
            _wiki(gen, "A kilogram of good Russian sausage - '''Кило́ хоро́шей ру́сской колбасы́'''", "Кило́ хоро́шей ру́сской колбасы́", "A kilogram of good Russian sausage"),
            _wiki("adjectives", "I arrived from my old house - '''Я пришёл из моего старого дома'''", "Я пришёл из моего старого дома", "I arrived from my old house"),
            _field("adjectives.csv:новый", "decl_m_gen"),
            _field("adjectives.csv:новый", "decl_f_gen"),
            _field("adjectives.csv:новый", "decl_pl_gen"),
            _field("adjectives.csv:хороший", "decl_m_gen"),
        ],
        [
            _check("Which admitted line is recorded for A kilogram of good Russian cheese?", "A kilogram of good Russian cheese", "Кило́ хоро́шего ру́сского сы́ра", [
                _other("Кило́ хоро́шей ру́сской колбасы́", "This admitted line is the other kilogram example."),
            ]),
            _check("Which admitted line is recorded for A kilogram of good Russian sausage?", "A kilogram of good Russian sausage", "Кило́ хоро́шей ру́сской колбасы́", [
                _other("Я пришёл из моего старого дома", "This admitted line is a different genitive example."),
            ]),
            _check("Which admitted genitive form is recorded for new?", "new", "но́вого", [
                _other("но́вой", "This admitted form is a different genitive row of the same word."),
                _other("но́вых", "This admitted form is the plural genitive row of the same word."),
            ], O),
            _check("Which admitted genitive form is recorded for good?", "good", "хоро́шего", [
                _other("но́вого", "This admitted genitive form belongs to a different word."),
            ], O),
        ],
    )
    women = "I like these women. - '''Я люблю ''этих женщин.'''"
    lesson.objective(
        "animate",
        "Choose the admitted animate plural object form",
        "CONTRAST_SELECTION",
        ("accusative", women, women),
        [
            _wiki("accusative", women, "этих женщин", "I like these women."),
            _wiki("accusative", "These women like me. - '''''Эти женщины'' любят меня.'''", "Эти женщины", "These women like me."),
            _field("nouns.csv:женщина", "pl_acc"),
            _field("nouns.csv:женщина", "pl_nom"),
            _field("nouns.csv:брат", "pl_acc"),
            _field("nouns.csv:друг", "pl_acc"),
        ],
        [
            _check("Which admitted line is the object form for these women?", "I like these women.", "этих женщин", [
                _other("Эти женщины", "This admitted line is the other example, where these women are not the object."),
            ]),
            _check("Which admitted line is recorded for These women like me?", "These women like me.", "Эти женщины", [
                _other("этих женщин", "This admitted line is the object example."),
            ]),
            _check("Which admitted plural row is the object form recorded for woman?", "woman", "же́нщин", [
                _other("же́нщины", "This admitted row is the other plural cell, not the object cell."),
            ], O),
            _check("Which admitted plural object form is recorded for brother?", "brother", "бра́тьев", [
                _other("друзе́й", "This admitted plural object form belongs to a different word."),
            ], O),
        ],
        extra=[("accusative", "any plural word that's inanimate is identical to its", "any plural word that's inanimate is identical to its", "instruction")],
    )
