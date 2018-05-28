package com.github.zalesskyi.base.summarization;

import com.github.zalesskyi.base.database.DbSchema;
import com.github.zalesskyi.base.database.MorphologyDbHelper;
import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * Класс, предоставляющий набор полезных функций.
 **/
public class MorphologyUtil {

    private static final String[] STOP_WORDS_TABLES = {DbSchema.ParticlesTable.NAME,
            DbSchema.PronounsTable.NAME, DbSchema.AdverbsTable.NAME,
            DbSchema.NumeralsTable.NAME, DbSchema.IntroductorsTable.NAME,
            DbSchema.ConjunctionsTable.NAME, DbSchema.PrepositionsTable.NAME};

    private static final String NUMBER_REGEX = " \\d+ ";

    /**
     * Метод, удаляющий все стоп-слова из исходного текста.
     *
     * @param text исходный текст
     * @return текст без стоповых слов
     */
    public static String removeStopWordsFrom(String text) {
        try {
            MorphologyDbHelper dbHelper = MorphologyDbHelper.getInstance();
            List<String> stopWords;
            text = text.replaceAll("[\\p{Punct}&&[^.!?\\$_]]", "");
            for (String table : STOP_WORDS_TABLES) {
                stopWords = dbHelper.queryWords(table);
                for (String stopWord : stopWords) {
                    text = text.replaceAll(" " + stopWord + " ", " ");
                }
            }
            text = text.replaceAll(NUMBER_REGEX, " ");
            System.out.println(text); // todo
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return text;
    }


    /**
     * Метод, возвращающий количество слов в заданном тексте.
     *
     * @param text заданный текст
     *
     * @return количество слов
     */
    public static int getWordsCountOf(String text) {
        return text.split(" ").length;
    }


    /**
     * Метод, возвращающий имя таблицы, которая содержит слово.
     *
     * @param word слово
     * @return если слово найдено - имя таблицы.
     *         если нет - null
     */
    @Nullable
    public static String getPartOfSpeech(String word) {
        String tableName = null;
        try {
            MorphologyDbHelper db = MorphologyDbHelper.getInstance();
            if (PartOfSpeechHelper.isNoun(word)) {
                tableName = getTableNameOfNounWord(word, db);
            } else if (PartOfSpeechHelper.isParticiple(word)) {
                tableName = getTableNameOfParticipleWord(word, db);
            } else if (PartOfSpeechHelper.isAdjective(word)) {
                tableName = getTableNameOfAdjectiveWord(word, db);
            } else if (PartOfSpeechHelper.isVerb(word)) {
                tableName = getTableNameOfVerbWord(word, db);
            } else {
                tableName = getTableNameOfNounWord(word, db);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
        return tableName;
    }

    /**
     * Поиск слова (предположительно сущ.) в таблицах.
     * Для конкретного слова Pn = 0.5
     *                       Pp = 0.01
     *                       Pa = 0.007
     *                       Pv = 0.001
     *
     * @param word слово
     * @param db используется для запросов к БД
     * @return имя таблицы, если слово есть в БД
     *         null, если слова в БД нет
     */
    @Nullable
    private static String getTableNameOfNounWord(String word, MorphologyDbHelper db) {
       if (db.isTableContainsWord(DbSchema.NounsTable.NAME, word)) {
           return DbSchema.NounsTable.NAME;
       } else if (db.isTableContainsWord(DbSchema.ParticiplesTable.NAME, word)) {
           return DbSchema.ParticiplesTable.NAME;
       } else if (db.isTableContainsWord(DbSchema.AdjectivesTable.NAME, word)) {
           return DbSchema.AdjectivesTable.NAME;
       } else if (db.isTableContainsWord(DbSchema.VerbsTable.NAME, word)) {
           return DbSchema.VerbsTable.NAME;
       }
        return null;
    }


    /**
     * Поиск слова (предположительно прич.) в таблицах.
     * Для конкретного слова Pn = 0.03
     *                       Pp = 0.87
     *                       Pa = 0.118
     *                       Pv = 0.04
     *
     * @param word слово
     * @param db используется для запросов к БД
     * @return имя таблицы, если слово есть в БД
     *         null, если слова в БД нет
     */
    @Nullable
    private static String getTableNameOfParticipleWord(String word, MorphologyDbHelper db) {
        if (db.isTableContainsWord(DbSchema.ParticiplesTable.NAME, word)) {
            return DbSchema.ParticiplesTable.NAME;
        } else if (db.isTableContainsWord(DbSchema.AdjectivesTable.NAME, word)) {
            return DbSchema.AdjectivesTable.NAME;
        } else if (db.isTableContainsWord(DbSchema.VerbsTable.NAME, word)) {
            return DbSchema.VerbsTable.NAME;
        }  else if (db.isTableContainsWord(DbSchema.NounsTable.NAME, word)) {
            return DbSchema.NounsTable.NAME;
        }
        return null;
    }


    /**
     * Поиск слова (предположительно прил.) в таблицах.
     * Для конкретного слова Pn = 0.5
     *                       Pp = 0.01
     *                       Pa = 0.007
     *                       Pv = 0.001
     *
     * @param word слово
     * @param db используется для запросов к БД
     * @return имя таблицы, если слово есть в БД
     *         null, если слова в БД нет
     */
    @Nullable
    private static String getTableNameOfAdjectiveWord(String word, MorphologyDbHelper db) {
        if (db.isTableContainsWord(DbSchema.AdjectivesTable.NAME, word)) {
            return DbSchema.AdjectivesTable.NAME;
        } else if (db.isTableContainsWord(DbSchema.ParticiplesTable.NAME, word)) {
            return DbSchema.ParticiplesTable.NAME;
        } else if (db.isTableContainsWord(DbSchema.NounsTable.NAME, word)) {
            return DbSchema.NounsTable.NAME;
        }  else if (db.isTableContainsWord(DbSchema.VerbsTable.NAME, word)) {
            return DbSchema.VerbsTable.NAME;
        }
        return null;
    }


    /**
     * Поиск слова (предположительно глаг.) в таблицах.
     * Для конкретного слова Pn = 0.08
     *                       Pp = 0.49
     *                       Pa = 0.2
     *                       Pv = 0.86
     *
     * @param word слово
     * @param db используется для запросов к БД
     * @return имя таблицы, если слово есть в БД
     *         null, если слова в БД нет
     */
    @Nullable
    private static String getTableNameOfVerbWord(String word, MorphologyDbHelper db) {
        if (db.isTableContainsWord(DbSchema.VerbsTable.NAME, word)) {
            return DbSchema.VerbsTable.NAME;
        } else if (db.isTableContainsWord(DbSchema.ParticiplesTable.NAME, word)) {
            return DbSchema.ParticiplesTable.NAME;
        } else if (db.isTableContainsWord(DbSchema.AdjectivesTable.NAME, word)) {
            return DbSchema.AdjectivesTable.NAME;
        }  else if (db.isTableContainsWord(DbSchema.NounsTable.NAME, word)) {
            return DbSchema.NounsTable.NAME;
        }
        return null;
    }
}