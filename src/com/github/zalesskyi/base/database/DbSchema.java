package com.github.zalesskyi.base.database;


/**
 * Класс, описывающий базу данных russian_morphology.
 */
public class DbSchema {
    static final String NAME = "russian_morphology";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/russian_morphology";


    static class Credentials {
        static final String LOGIN = "postgres";
        static final String PASSWORD = "postgres";
    }

    /**
     * Таблица прилагательных.
     */
    public static class AdjectivesTable {
        public static final String NAME = "adjectives_morf";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
            public static final String CODE_PARENT = "code_parent";
            public static final String TYPE_SUB = "type_sub";
            public static final String PLURAL = "plural";
            public static final String GENDER = "gender";
            public static final String WORD_CASE = "wcase";
            public static final String COMP = "comp";
            public static final String SHORT = "short";
        }
    }

    /**
     * Таблица наречий.
     */
    public static class AdverbsTable {
        public static final String NAME = "adverbs_morf";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
            public static final String CODE_PARENT = "code_parent";
            public static final String TYPE_SUB = "type_sub";
            public static final String TYPE_SSUB = "type_ssub";
            public static final String COMP = "comp";
        }
    }

    /**
     * Таблица союзов.
     */
    public static class ConjunctionsTable {
        public static final String NAME = "conjunctions";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
        }
    }

    /**
     * Таблица вводных слов.
     */
    public static class IntroductorsTable {
        public static final String NAME = "introductors";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
        }
    }

    /**
     * Таблица существительных.
     */
    public static class NounsTable {
        public static final String NAME = "nouns_morf";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
            public static final String CODE_PARENT = "code_parent";
            public static final String PLURAL = "plural";
            public static final String GENDER = "gender";
            public static final String WORD_CASE = "wcase";
            public static final String SOUL = "soul";
        }
    }

    /**
     * Таблица числительных.
     */
    public static class NumeralsTable {
        public static final String NAME = "numerals_morf";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
            public static final String CODE_PARENT = "code_parent";
            public static final String TYPE_SUB = "type_sub";
            public static final String PLURAL = "plural";
            public static final String GENDER = "gender";
            public static final String WORD_CASE = "wcase";
        }
    }

    /**
     * Таблица частиц.
     */
    public static class ParticlesTable {
        public static final String NAME = "particles";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
        }
    }

    /**
     *Таблица предикативов.
     */
    public static class PredicativeTable {
        public static final String NAME = "predicative";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
        }
    }

    /**
     * Таблица предлогов.
     */
    public static class PrepositionsTable {
        public static final String NAME = "prepositions_morf";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
            public static final String CODE_PARENT = "code_parent";
            public static final String WORD_CASE = "wcase";
        }
    }

    /**
     * Таблица местоимений.
     */
    public static class PronounsTable {
        public static final String NAME = "pronouns_morf";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
            public static final String CODE_PARENT = "code_parent";
            public static final String PART = "part";
            public static final String TYPE_SUB = "type_sub";
            public static final String PLURAL = "plural";
            public static final String GENDER = "gender";
            public static final String WORD_CASE = "wcase";
        }
    }

    public static class VerbsTable {
        public static final String NAME = "verbs_morf";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
            public static final String CODE_PARENT = "code_parent";
            public static final String PLURAL = "plural";
            public static final String GENDER = "gender";
            public static final String TRANSIT = "transit";
            public static final String PERFECT = "perfect";
            public static final String FACE = "face";
            public static final String KIND = "kind";
            public static final String TIME = "wtime";
            public static final String INF = "inf";
            public static final String VOZV = "vozv";   // возвратный ли глагол
            public static final String MOOD = "nakl";   // наклонение
        }
    }

    public static class ParticiplesTable {
        public static final String NAME = "participles_morf";

        public static class Columns {
            public static final String ID = "iid";
            public static final String WORD = "word";
            public static final String CODE = "code";
            public static final String CODE_PARENT = "code_parent";
            public static final String TYPE = "wtype";
            public static final String PLURAL = "plural";
            public static final String GENDER = "gender";
            public static final String WORD_CASE = "wcase";
            public static final String TRANSIT = "transit";
            public static final String PERFECT = "perfect";
            public static final String KIND = "kind";
            public static final String TIME = "wtime";
            public static final String VOZV = "vozv";
            public static final String NAKL = "nakl";
            public static final String SHORT = "short";
        }
    }
}









































