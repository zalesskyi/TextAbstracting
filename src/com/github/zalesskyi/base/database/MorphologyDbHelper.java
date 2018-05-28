package com.github.zalesskyi.base.database;

import com.sun.istack.internal.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс, предоставляющий интерфейс для работы с БД.
 */
public class MorphologyDbHelper {
    private static MorphologyDbHelper instance;

    private ConnectionPool connectionPool;
    private DataSource dataSource;

    public static MorphologyDbHelper getInstance() throws Exception {
        if (instance == null) {
            instance = new MorphologyDbHelper();
        }
        return instance;
    }

    private MorphologyDbHelper() throws Exception {
            connectionPool = new ConnectionPool();
            dataSource = connectionPool.setupPool();
    }

    /**
     * Запрос word из одной из таблиц БД.
     *
     * @param from таблица
     * @param where условие
     * @return строку, если слово есть в БД
     *         null, если слова в БД нет.
     * @throws UnsupportedOperationException если не удалось установить подключение с БД.
     */
    @Nullable
    public String queryWord(String from, String where) throws UnsupportedOperationException {
        try (Connection con = dataSource.getConnection()) {
            Statement stat = con.createStatement();

            ResultSet set = stat.executeQuery("SELECT " + DbSchema.AdjectivesTable.Columns.WORD + " FROM " + from + " WHERE " + where);

            if (set.next()) {
                return set.getString(DbSchema.AdjectivesTable.Columns.WORD);           // имя столбца 'WORD' во всех таблицах одинаковое
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
            throw new UnsupportedOperationException("Can't perform this action");
        }
        return null;
    }

    /**
     * Запрос падежа у слова.
     * Поддерживаются части речи:
     *  1) Сущ
     *  2) Прил
     *
     * @param from таблица
     * @param where условие
     * @return падежи, если условие удовлетворяется
     *         null, если нет.
     *
     * @throws IllegalArgumentException если был запрошен падеж
     * частей речи, не являющихся ни сущ., ни прил.
     *
     * @throws UnsupportedOperationException если не удалось установить подключение с БД.
     */
    public List<String> queryCase(String from, String where)
            throws IllegalArgumentException, UnsupportedOperationException {

        try (Connection con = dataSource.getConnection()) {
            if (!from.equals(DbSchema.AdjectivesTable.NAME)
                    && !from.equals(DbSchema.NounsTable.NAME)
                    && !from.equals(DbSchema.ParticiplesTable.NAME)) {
                throw new IllegalArgumentException("Supported only adjectives, nouns and participles");
            }

            Statement stat = con.createStatement();
            ResultSet set = stat.executeQuery("SELECT " +
                    DbSchema.AdjectivesTable.Columns.WORD_CASE + " FROM " + from + " WHERE " + where);

            List<String> cases = new ArrayList<>();
            while (set.next()) {                                                  // заполняем массив результатами запроса
                cases.add(set.getString(DbSchema.NounsTable.Columns.WORD_CASE));
            }
            return cases.isEmpty() ? null : cases;
        } catch (SQLException exc) {
            exc.printStackTrace();
            throw new UnsupportedOperationException("Can't perform this action");
        }
    }


    /**
     * Запрос кода родительского слова.
     * Родительское слово - это слово в том же числе, в том же роде, но в именительном падеже.
     *
     * @param from таблица.
     * @param where условие
     * @return код родительского слова,
     *         если слова в таблице нет возвращается -1.
     * @throws UnsupportedOperationException если не удалось установить соединение с БД.
     */
    public int queryParentCode(String from, String where) throws UnsupportedOperationException {
        try (Connection con = dataSource.getConnection()) {

            Statement stat = con.createStatement();
            ResultSet set = stat.executeQuery("SELECT " +
                    DbSchema.AdjectivesTable.Columns.CODE_PARENT + " FROM " + from + " WHERE " + where);

            if (set.next()) {
                return set.getInt(DbSchema.AdjectivesTable.Columns.CODE_PARENT);
            }
            return -1;
        } catch (SQLException exc) {
            exc.printStackTrace();
            throw new UnsupportedOperationException("Can't perform this action");
        }
    }

    /**
     * Запрос кода текущего слова.
     *
     * @param from таблица.
     * @param where условие
     * @return код слова,
     *         если слова в таблице нет возвращается -1.
     * @throws UnsupportedOperationException если не удалось установить соединение с БД.
     */
    public int queryCode(String from, String where) throws UnsupportedOperationException {
        try (Connection con = dataSource.getConnection()) {

            Statement stat = con.createStatement();
            ResultSet set = stat.executeQuery("SELECT " +
                    DbSchema.AdjectivesTable.Columns.CODE + " FROM " + from + " WHERE " + where);

            if (set.next()) {
                return set.getInt(DbSchema.AdjectivesTable.Columns.CODE);
            }
            return -1;
        } catch (SQLException exc) {
            exc.printStackTrace();
            throw new UnsupportedOperationException("Can't perform this action");
        }
    }

    public List<String> queryWords(String from)
            throws UnsupportedOperationException, IllegalArgumentException {

       /* if (from.equals(DbSchema.AdjectivesTable.NAME) || from.equals(DbSchema.NounsTable.NAME)) {
            throw new IllegalArgumentException();
        }todo*/
        try (Connection con = dataSource.getConnection()) {
            Statement stat = con.createStatement();
            ResultSet set = stat.executeQuery("SELECT " +
                    DbSchema.AdjectivesTable.Columns.WORD + " FROM " + from);
            List<String> words = new ArrayList<>(5000);
            while (set.next()) {
                words.add(set.getString(DbSchema.AdjectivesTable.Columns.WORD));
            }
            Collections.reverse(words);
            return words;
        } catch (SQLException exc) {
            exc.printStackTrace();
            throw new UnsupportedOperationException("Can't perform this action");
        }
    }


    /**
     * Метод, определяющий, содержит ли заданная таблица заданное слово.
     *
     * @param word заданное слово
     * @param table заданная таблица
     * @return true -> содержит
     *         false -> не содержит
     */
    public boolean isTableContainsWord(String table, String word) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement stat = con.prepareStatement("SELECT * FROM " + table +
                    " WHERE " + DbSchema.NounsTable.Columns.WORD + "=?");
            stat.setString(1, word);
            return stat.executeQuery().next();
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        }
    }


    public boolean isTableContainsWordByCond(String table, String where) {
        try (Connection con = dataSource.getConnection()) {
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM " + table + " WHERE " + where);
            return rs.next();
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        }
    }

    public int getPlurality(String table, String word) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement stat = con.prepareStatement("SELECT " + DbSchema.NounsTable.Columns.PLURAL + " FROM " + table +
                    " WHERE " + DbSchema.NounsTable.Columns.WORD + "=?");
            stat.setString(1, word);
            ResultSet res = stat.executeQuery();
            if (res.next()) {
                return res.getInt(DbSchema.NounsTable.Columns.PLURAL);
            }
            return -1;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return -1;
        }
    }
}
